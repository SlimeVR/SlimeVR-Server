@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package dev.slimevr.trackingchecklist

import dev.slimevr.VRServer
import dev.slimevr.VRServerState
import dev.slimevr.config.MountingMethods
import dev.slimevr.config.Settings
import dev.slimevr.device.DeviceOrigin
import dev.slimevr.device.DeviceState
import dev.slimevr.networkprofile.NetworkProfileManager
import dev.slimevr.resets.ResetBodyParts
import dev.slimevr.resets.ResetsManager
import dev.slimevr.skeleton.Skeleton
import dev.slimevr.tracker.TrackerState
import dev.slimevr.vrchat.VRCConfigManager
import dev.slimevr.vrchat.VRCConfigState
import dev.slimevr.vrchat.computeRecommendedValues
import dev.slimevr.vrchat.computeValidity
import dev.slimevr.vrchat.isVRCConfigValid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.rpc.TrackingChecklistNeedCalibration
import solarxr_protocol.rpc.TrackingChecklistPublicNetworks
import solarxr_protocol.rpc.TrackingChecklistStep
import solarxr_protocol.rpc.TrackingChecklistStepId
import solarxr_protocol.rpc.TrackingChecklistStepVisibility
import solarxr_protocol.rpc.TrackingChecklistTrackerError
import solarxr_protocol.rpc.TrackingChecklistTrackerReset
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
			visibility = TrackingChecklistStepVisibility.WHEN_INVALID,
			extraData = if (!isAssigned) {
				TrackingChecklistUnassignedHMD(
					trackerId = hmdTracker?.id?.toUShort(),
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
				(tracker.status == TrackerStatus.OK || tracker.status == TrackerStatus.SLEEPING) &&
				(tracker.completedRestCalibration != null && !tracker.completedRestCalibration)
		}
		return TrackingChecklistStep(
			valid = uncalibratedTrackers.isEmpty(),
			enabled = trackers.isNotEmpty(),
			extraData = if (!uncalibratedTrackers.isEmpty()) {
				TrackingChecklistNeedCalibration(
					trackersId = uncalibratedTrackers.map { tracker -> tracker.id.toUShort() },
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
					trackersId = errorTrackers.map { tracker -> tracker.id.toUShort() },
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
	private fun computeStep(vrc: VRCConfigState, userHeight: Float): TrackingChecklistStep {
		val values = vrc.currentValues
		if (!vrc.isSupported || values == null) return TrackingChecklistStep(valid = true, enabled = false)
		val recommended = computeRecommendedValues(server, userHeight)
		val validity = computeValidity(values, recommended)
		return TrackingChecklistStep(valid = isVRCConfigValid(validity, vrc.mutedWarnings), ignorable = true, enabled = true)
	}

	override fun observe(receiver: TrackingChecklist) {
		combine(
			skeleton.context.state.map { state -> state.skeletonHeight },
			vrcConfigManager.context.state,
		) { userHeight, vrc -> computeStep(vrc, userHeight) }
			.distinctUntilChanged()
			.onEach { step -> receiver.context.dispatch(TrackingChecklistActions.UpdateStep(TrackingChecklistStepId.VRCHAT_SETTINGS, step)) }
			.launchIn(receiver.context.scope)
	}
}

class NetworkProfileCheckBehaviour(
	private val manager: NetworkProfileManager,
) : TrackingChecklistBehaviourType {
	override fun observe(receiver: TrackingChecklist) {
		manager.context.state
			.map { state ->
				TrackingChecklistStep(
					valid = state.publicNetworks.isEmpty(),
					enabled = state.isSupported,
					extraData = if (state.publicNetworks.isNotEmpty()) {
						TrackingChecklistPublicNetworks(
							adapters = state.publicNetworks.map {
								it.name ?: "[no name]"
							},
						)
					} else {
						null
					},
				)
			}
			.distinctUntilChanged()
			.onEach { step -> receiver.context.dispatch(TrackingChecklistActions.UpdateStep(TrackingChecklistStepId.NETWORK_PROFILE_PUBLIC, step)) }
			.launchIn(receiver.context.scope)
	}
}

private fun isImuAssigned(tracker: TrackerState): Boolean = (tracker.origin == DeviceOrigin.UDP || tracker.origin == DeviceOrigin.HID) &&
	tracker.position == null &&
	tracker.imuType !== null &&
	tracker.status != TrackerStatus.ERROR &&
	tracker.bodyPart != null

private fun isConnectedAssignedImu(tracker: TrackerState): Boolean = (tracker.origin == DeviceOrigin.UDP || tracker.origin == DeviceOrigin.HID) &&
	tracker.position == null &&
	tracker.imuType !== null &&
	(tracker.status == TrackerStatus.OK || tracker.status == TrackerStatus.SLEEPING) &&
	tracker.bodyPart != null

class FullResetCheckBehaviour(
	private val server: VRServer,
	private val resetsManager: ResetsManager,
) : TrackingChecklistBehaviourType {
	private val needsReset = MutableStateFlow<Set<Int>>(emptySet())

	override fun observe(receiver: TrackingChecklist) {
		val scope = receiver.context.scope

		val connected = mutableSetOf<Int>()
		trackerStatesFlow(server)
			.map { trackers -> trackers.filter { isConnectedAssignedImu(it) }.map { it.id }.toSet() }
			.distinctUntilChanged()
			.onEach { current ->
				needsReset.update { ids -> ids + (current - connected) }
				connected.clear()
				connected.addAll(current)
			}
			.launchIn(scope)

		val bodyParts = mutableMapOf<Int, BodyPart>()
		trackerStatesFlow(server)
			.map { trackers -> trackers.mapNotNull { tracker -> tracker.bodyPart?.let { tracker.id to it } }.toMap() }
			.distinctUntilChanged()
			.onEach { current ->
				for ((id, bodyPart) in current) {
					val previous = bodyParts[id]
					if (previous != null && previous != bodyPart) {
						needsReset.update { ids -> ids + id }
					}
				}
				bodyParts.clear()
				bodyParts.putAll(current)
			}
			.launchIn(scope)

		// Clear everything on a full reset.
		resetsManager.context.state
			.distinctUntilChangedBy { it.lastFullResetTime }
			.drop(1)
			.onEach { needsReset.value = emptySet() }
			.launchIn(scope)

		combine(needsReset, trackerStatesFlow(server)) { ids, trackers ->
			val assignedIds = trackers.filter { isImuAssigned(it) }.map { it.id }.toSet()
			val pending = ids intersect assignedIds
			TrackingChecklistStep(
				valid = pending.isEmpty(),
				enabled = assignedIds.isNotEmpty(),
				ignorable = false,
				visibility = TrackingChecklistStepVisibility.ALWAYS,
				extraData = if (pending.isNotEmpty()) {
					TrackingChecklistTrackerReset(trackersId = pending.map { it.toUShort() })
				} else {
					null
				},
			)
		}
			.distinctUntilChanged()
			.onEach { step -> receiver.context.dispatch(TrackingChecklistActions.UpdateStep(TrackingChecklistStepId.FULL_RESET, step)) }
			.launchIn(scope)
	}
}

class MountingCalibrationCheckBehaviour(
	private val server: VRServer,
	private val resetsManager: ResetsManager,
	private val settings: Settings,
) : TrackingChecklistBehaviourType {
	override fun observe(receiver: TrackingChecklist) {
		combine(
			trackerStatesFlow(server),
			resetsManager.context.state,
			settings.context.state,
		) { trackers, resetsState, settingsState ->
			val imuTrackers = trackers.filter { isImuAssigned(it) }
			TrackingChecklistStep(
				valid = resetsState.mountingResetCompleted,
				enabled = settingsState.data.resetsConfig.lastMountingMethod == MountingMethods.AUTOMATIC && imuTrackers.isNotEmpty(),
				ignorable = true,
				visibility = TrackingChecklistStepVisibility.ALWAYS,
			)
		}
			.distinctUntilChanged()
			.onEach { step -> receiver.context.dispatch(TrackingChecklistActions.UpdateStep(TrackingChecklistStepId.MOUNTING_CALIBRATION, step)) }
			.launchIn(receiver.context.scope)
	}
}

class FeetMountingCalibrationCheckBehaviour(
	private val server: VRServer,
	private val resetsManager: ResetsManager,
	private val settings: Settings,
) : TrackingChecklistBehaviourType {
	override fun observe(receiver: TrackingChecklist) {
		combine(
			trackerStatesFlow(server),
			resetsManager.context.state,
			settings.context.state,
		) { trackers, resetsState, settingsState ->
			val resetsConfig = settingsState.data.resetsConfig
			val imuTrackers = trackers.filter { isImuAssigned(it) }
			TrackingChecklistStep(
				valid = resetsState.feetMountingResetCompleted,
				enabled = resetsConfig.lastMountingMethod == MountingMethods.AUTOMATIC &&
					!resetsConfig.resetMountingFeet &&
					imuTrackers.any { it.bodyPart in ResetBodyParts.FEET },
				ignorable = true,
				visibility = TrackingChecklistStepVisibility.ALWAYS,
			)
		}
			.distinctUntilChanged()
			.onEach { step -> receiver.context.dispatch(TrackingChecklistActions.UpdateStep(TrackingChecklistStepId.FEET_MOUNTING_CALIBRATION, step)) }
			.launchIn(receiver.context.scope)
	}
}
