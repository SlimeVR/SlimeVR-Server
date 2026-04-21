@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package dev.slimevr.trackingchecklist

import dev.slimevr.VRServer
import dev.slimevr.VRServerState
import dev.slimevr.device.DeviceOrigin
import dev.slimevr.device.DeviceState
import dev.slimevr.skeleton.Skeleton
import dev.slimevr.tracker.TrackerState
import dev.slimevr.vrchat.VRCConfigManager
import dev.slimevr.vrchat.VRCConfigState
import dev.slimevr.vrchat.computeRecommendedValues
import dev.slimevr.vrchat.computeValidity
import dev.slimevr.vrchat.isVRCConfigValid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.DeviceId
import solarxr_protocol.datatypes.TrackerId
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.rpc.TrackingChecklistNeedCalibration
import solarxr_protocol.rpc.TrackingChecklistSteamVRDisconnected
import solarxr_protocol.rpc.TrackingChecklistStep
import solarxr_protocol.rpc.TrackingChecklistStepId
import solarxr_protocol.rpc.TrackingChecklistTrackerError
import solarxr_protocol.rpc.TrackingChecklistUnassignedHMD

// Flat-maps a server state flow into a combined flow of all context states for a given collection.
// Re-emits whenever any item's state changes or the collection itself changes.
private inline fun <C, reified S> allContextStates(
	server: VRServer,
	crossinline select: (VRServerState) -> Collection<C>,
	crossinline stateOf: (C) -> Flow<S>,
): Flow<List<S>> = server.context.state.flatMapLatest { serverState ->
	val items = select(serverState)
	if (items.isEmpty()) return@flatMapLatest flowOf(emptyList())
	combine(items.map { item -> stateOf(item) }) { states -> states.toList() }
}

private fun trackerStatesFlow(server: VRServer): Flow<List<TrackerState>> = allContextStates(server, { state -> state.trackers.values }) { tracker -> tracker.context.state }

private fun deviceStatesFlow(server: VRServer): Flow<List<DeviceState>> = allContextStates(server, { state -> state.devices.values }) { device -> device.context.state }

private fun trackerIdOf(tracker: TrackerState): TrackerId = TrackerId(trackerNum = tracker.id.toUByte(), deviceId = DeviceId(tracker.deviceId.toUByte()))

class SteamVRCheckBehaviour(private val server: VRServer) : TrackingChecklistBehaviourType {
	override fun observe(receiver: TrackingChecklist) {
		server.context.state
			.map { state ->
				val connected = state.drivers.isNotEmpty()
				TrackingChecklistStep(
					valid = connected,
					enabled = true,
					ignorable = true,
					extraData = if (!connected) TrackingChecklistSteamVRDisconnected() else null,
				)
			}
			.distinctUntilChanged()
			.onEach { step -> receiver.context.dispatch(TrackingChecklistActions.UpdateStep(TrackingChecklistStepId.STEAMVR_DISCONNECTED, step)) }
			.launchIn(receiver.context.scope)
	}
}

class HMDCheckBehaviour(private val server: VRServer) : TrackingChecklistBehaviourType {
	private fun computeStep(trackers: List<TrackerState>): TrackingChecklistStep {
		// FIXME: Most likely incomplete
		val hasSteamVR = trackers.any { tracker -> tracker.origin == DeviceOrigin.DRIVER }
		val hmdTracker = trackers.firstOrNull { tracker -> tracker.origin == DeviceOrigin.DRIVER && tracker.position != null }
		val isAssigned = hmdTracker?.bodyPart == BodyPart.HEAD
		return TrackingChecklistStep(
			valid = isAssigned,
			enabled = hasSteamVR,
			ignorable = true,
			extraData = if (!isAssigned) {
				TrackingChecklistUnassignedHMD(
					trackerId = hmdTracker?.let { tracker -> trackerIdOf(tracker) },
				)
			} else {
				null
			},
		)
	}

	override fun observe(receiver: TrackingChecklist) {
		trackerStatesFlow(server)
			.map { trackers -> computeStep(trackers) }
			.distinctUntilChanged()
			.onEach { step -> receiver.context.dispatch(TrackingChecklistActions.UpdateStep(TrackingChecklistStepId.UNASSIGNED_HMD, step)) }
			.launchIn(receiver.context.scope)
	}
}

class TrackerRestCheckBehaviour(private val server: VRServer) : TrackingChecklistBehaviourType {
	private fun computeStep(trackers: List<TrackerState>): TrackingChecklistStep {
		val uncalibratedTrackers = trackers.filter { tracker ->
			(tracker.origin == DeviceOrigin.UDP || tracker.origin == DeviceOrigin.HID) &&
				tracker.status == TrackerStatus.OK &&
				(tracker.completedRestCalibration != null && !tracker.completedRestCalibration)
		}
		return TrackingChecklistStep(
			valid = uncalibratedTrackers.isEmpty(),
			enabled = trackers.isNotEmpty(),
			extraData = if (!uncalibratedTrackers.isEmpty()) {
				TrackingChecklistNeedCalibration(
					trackersId = uncalibratedTrackers.map { tracker -> trackerIdOf(tracker) },
				)
			} else {
				null
			},
		)
	}

	override fun observe(receiver: TrackingChecklist) {
		trackerStatesFlow(server)
			.map { trackers -> computeStep(trackers) }
			.distinctUntilChanged()
			.onEach { step -> receiver.context.dispatch(TrackingChecklistActions.UpdateStep(TrackingChecklistStepId.TRACKERS_REST_CALIBRATION, step)) }
			.launchIn(receiver.context.scope)
	}
}

class TrackerErrorCheckBehaviour(private val server: VRServer) : TrackingChecklistBehaviourType {
	private fun computeStep(trackers: List<TrackerState>): TrackingChecklistStep {
		val errorTrackers = trackers
			.filter { tracker -> tracker.status == TrackerStatus.ERROR && tracker.bodyPart != null }
			.toSet()
		return TrackingChecklistStep(
			valid = errorTrackers.isEmpty(),
			enabled = trackers.isNotEmpty(),
			extraData = if (errorTrackers.isNotEmpty()) {
				TrackingChecklistTrackerError(
					trackersId = errorTrackers.map { tracker -> trackerIdOf(tracker) },
				)
			} else {
				null
			},
		)
	}

	override fun observe(receiver: TrackingChecklist) {
		trackerStatesFlow(server)
			.map { trackers -> computeStep(trackers) }
			.distinctUntilChanged()
			.onEach { step -> receiver.context.dispatch(TrackingChecklistActions.UpdateStep(TrackingChecklistStepId.TRACKER_ERROR, step)) }
			.launchIn(receiver.context.scope)
	}
}

class VRChatSettingsCheckBehaviour(
	private val server: VRServer,
	private val skeleton: Skeleton,
	private val vrcConfigManager: VRCConfigManager,
) : TrackingChecklistBehaviourType {
	private fun computeStep(vrc: VRCConfigState, userHeight: Double): TrackingChecklistStep {
		val values = vrc.currentValues
		if (!vrc.isSupported || values == null) return TrackingChecklistStep(valid = true, enabled = false)
		val recommended = computeRecommendedValues(server, userHeight)
		val validity = computeValidity(values, recommended)
		return TrackingChecklistStep(valid = isVRCConfigValid(validity, vrc.mutedWarnings), enabled = true)
	}

	override fun observe(receiver: TrackingChecklist) {
		combine(
			skeleton.context.state.map { state -> state.userHeight },
			vrcConfigManager.context.state,
		) { userHeight, vrc -> computeStep(vrc, userHeight) }
			.distinctUntilChanged()
			.onEach { step -> receiver.context.dispatch(TrackingChecklistActions.UpdateStep(TrackingChecklistStepId.VRCHAT_SETTINGS, step)) }
			.launchIn(receiver.context.scope)
	}
}
