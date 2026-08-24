@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package dev.slimevr.trackingchecklist

import dev.slimevr.VRServer
import dev.slimevr.VRServerState
import dev.slimevr.config.Settings
import dev.slimevr.device.DeviceState
import dev.slimevr.driver.DriverBridgeSource
import dev.slimevr.networkprofile.NetworkProfileManager
import dev.slimevr.resets.ResetBodyParts
import dev.slimevr.resets.ResetsManager
import dev.slimevr.routing.BoneRoutingManager
import dev.slimevr.routing.Routes
import dev.slimevr.skeleton.Skeleton
import dev.slimevr.tracker.TrackerState
import dev.slimevr.vrchat.VRCConfigManager
import dev.slimevr.vrchat.VRCConfigState
import dev.slimevr.vrchat.computeRecommendedValues
import dev.slimevr.vrchat.computeValidity
import dev.slimevr.vrchat.isVRCConfigValid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
import solarxr_protocol.datatypes.DeviceOrigin
import solarxr_protocol.datatypes.MountingMethod
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.datatypes.hardware_info.ImuType
import solarxr_protocol.rpc.RoutingOutput
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

/**
 * Everything about a tracker the checklist reasons about, and nothing else.
 *
 * A tracker emits on every rotation update, hundreds of times a second, while the answers the
 * checklist computes change a couple of times a minute. Projecting to this before the combine below
 * is what separates the two: rotation does not appear here, so a rotation update projects to an equal
 * value and [distinctUntilChanged] drops it before any check runs.
 *
 * The dependency is declared rather than remembered. A check that needs another field adds it here,
 * and the compiler points at every place that has to change. nothing can silently stop updating
 * because someone read a field the projection forgot to carry.
 */
data class ChecklistTracker(
	val id: Int,
	val origin: DeviceOrigin,
	val status: TrackerStatus,
	val bodyPart: BodyPart?,
	val imuType: ImuType?,
	val completedRestCalibration: Boolean?,
	// Deliberately not the position itself: the checks only ask whether there is one, and carrying the
	// value would put this back on the rotation update rate.
	val hasPosition: Boolean,
)

internal fun checklistTracker(tracker: TrackerState) = ChecklistTracker(
	id = tracker.id,
	origin = tracker.origin,
	status = tracker.status,
	bodyPart = tracker.bodyPart,
	imuType = tracker.imuType,
	completedRestCalibration = tracker.completedRestCalibration,
	hasPosition = tracker.position != null,
)

internal fun trackerStatesFlow(server: VRServer): Flow<List<ChecklistTracker>> = allContextStates(server, { state -> state.trackers.values }) { tracker ->
	tracker.context.state.map { state -> checklistTracker(state) }.distinctUntilChanged()
}

class HMDCheckBehaviour(private val trackerStates: StateFlow<List<ChecklistTracker>>) : TrackingChecklistBehaviourType {
	private fun computeStep(trackers: List<ChecklistTracker>): TrackingChecklistStep {
		val hmdTracker = trackers.firstOrNull { tracker -> tracker.origin == DeviceOrigin.DRIVER }
		val isAssigned = hmdTracker?.bodyPart == BodyPart.HEAD
		return TrackingChecklistStep(
			valid = isAssigned,
			enabled = hmdTracker != null,
			ignorable = true,
			visibility = TrackingChecklistStepVisibility.WHEN_INVALID,
			extraData = if (!isAssigned) {
				TrackingChecklistUnassignedHMD(
					trackerId = hmdTracker?.id?.toUShort() ?: error("trackerId should not be set if hmdTracker"),
				)
			} else {
				null
			},
		)
	}

	override fun observe(receiver: TrackingChecklist) {
		trackerStates
			.map { trackers -> computeStep(trackers) }
			.distinctUntilChanged()
			.onEach { step -> receiver.context.dispatch(TrackingChecklistActions.UpdateStep(TrackingChecklistStepId.UNASSIGNED_HMD, step)) }
			.launchIn(receiver.context.scope)
	}
}

class TrackerRestCheckBehaviour(private val trackerStates: StateFlow<List<ChecklistTracker>>) : TrackingChecklistBehaviourType {
	private fun computeStep(trackers: List<ChecklistTracker>): TrackingChecklistStep {
		val uncalibratedTrackers = trackers.filter { tracker ->
			(tracker.origin == DeviceOrigin.UDP || tracker.origin == DeviceOrigin.HID) &&
				(tracker.status == TrackerStatus.OK || tracker.status == TrackerStatus.SLEEPING) &&
				(tracker.completedRestCalibration != null && !tracker.completedRestCalibration)
		}
		return TrackingChecklistStep(
			valid = uncalibratedTrackers.isEmpty(),
			enabled = trackers.isNotEmpty(),
			extraData = if (uncalibratedTrackers.isNotEmpty()) {
				TrackingChecklistNeedCalibration(
					trackersId = uncalibratedTrackers.map { tracker -> tracker.id.toUShort() },
				)
			} else {
				null
			},
		)
	}

	override fun observe(receiver: TrackingChecklist) {
		trackerStates
			.map { trackers -> computeStep(trackers) }
			.distinctUntilChanged()
			.onEach { step -> receiver.context.dispatch(TrackingChecklistActions.UpdateStep(TrackingChecklistStepId.TRACKERS_REST_CALIBRATION, step)) }
			.launchIn(receiver.context.scope)
	}
}

class TrackerErrorCheckBehaviour(private val trackerStates: StateFlow<List<ChecklistTracker>>) : TrackingChecklistBehaviourType {
	private fun computeStep(trackers: List<ChecklistTracker>): TrackingChecklistStep {
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
		trackerStates
			.map { trackers -> computeStep(trackers) }
			.distinctUntilChanged()
			.onEach { step -> receiver.context.dispatch(TrackingChecklistActions.UpdateStep(TrackingChecklistStepId.TRACKER_ERROR, step)) }
			.launchIn(receiver.context.scope)
	}
}

class SteamVRHandsCheckBehaviour(
	private val trackerStates: StateFlow<List<ChecklistTracker>>,
	private val server: VRServer,
	private val boneRouting: BoneRoutingManager,
) : TrackingChecklistBehaviourType {
	private val HAND_BONES = setOf(BodyPart.LEFT_HAND, BodyPart.RIGHT_HAND)

	private fun computeStep(
		trackers: List<ChecklistTracker>,
		routes: Routes,
		driverConnected: Boolean,
	): TrackingChecklistStep {
		// The skeleton computes a hand bone from the arm chain, so routing one sends a hand
		// tracker to SteamVR whether or not the user wears anything on that hand.
		val handsSentToDriver = HAND_BONES.any { hand -> routes[hand]?.contains(RoutingOutput.DRIVER) == true }
		val handTrackers = trackers.filter { tracker -> tracker.bodyPart in HAND_BONES }
		// Controllers reach us back through the driver, anything else on a hand is a
		// tracker the user actually wears.
		val hasControllers = handTrackers.any { tracker -> tracker.origin == DeviceOrigin.DRIVER }
		val hasHandTrackers = handTrackers.any { tracker ->
			tracker.origin != DeviceOrigin.DRIVER && tracker.origin != DeviceOrigin.VRC
		}

		return TrackingChecklistStep(
			valid = !handsSentToDriver || (!hasControllers && hasHandTrackers),
			enabled = driverConnected,
			ignorable = true,
			visibility = TrackingChecklistStepVisibility.WHEN_INVALID,
		)
	}

	override fun observe(receiver: TrackingChecklist) {
		combine(
			trackerStates,
			boneRouting.context.state.map { state -> state.routes },
			server.context.state
				.map { state -> state.drivers.values.any { it.source == DriverBridgeSource.DRIVER } }
				.distinctUntilChanged(),
			::computeStep,
		)
			.distinctUntilChanged()
			.onEach { step -> receiver.context.dispatch(TrackingChecklistActions.UpdateStep(TrackingChecklistStepId.STEAMVR_HANDS_ENABLED, step)) }
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

private fun isImuAssigned(tracker: ChecklistTracker): Boolean = (tracker.origin == DeviceOrigin.UDP || tracker.origin == DeviceOrigin.HID) &&
	!tracker.hasPosition &&
	tracker.imuType !== null &&
	tracker.status != TrackerStatus.ERROR &&
	tracker.bodyPart != null

private fun isConnectedAssignedImu(tracker: ChecklistTracker): Boolean = (tracker.origin == DeviceOrigin.UDP || tracker.origin == DeviceOrigin.HID) &&
	!tracker.hasPosition &&
	tracker.imuType !== null &&
	(tracker.status == TrackerStatus.OK || tracker.status == TrackerStatus.SLEEPING) &&
	tracker.bodyPart != null

class FullResetCheckBehaviour(
	private val trackerStates: StateFlow<List<ChecklistTracker>>,
	private val resetsManager: ResetsManager,
) : TrackingChecklistBehaviourType {
	private val needsReset = MutableStateFlow<Set<Int>>(emptySet())

	override fun observe(receiver: TrackingChecklist) {
		val scope = receiver.context.scope

		val connected = mutableSetOf<Int>()
		trackerStates
			.map { trackers -> trackers.filter { isConnectedAssignedImu(it) }.map { it.id }.toSet() }
			.distinctUntilChanged()
			.onEach { current ->
				needsReset.update { ids -> ids + (current - connected) }
				connected.clear()
				connected.addAll(current)
			}
			.launchIn(scope)

		val bodyParts = mutableMapOf<Int, BodyPart>()
		trackerStates
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

		combine(needsReset, trackerStates) { ids, trackers ->
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
	private val trackerStates: StateFlow<List<ChecklistTracker>>,
	private val resetsManager: ResetsManager,
	private val settings: Settings,
) : TrackingChecklistBehaviourType {
	override fun observe(receiver: TrackingChecklist) {
		combine(
			trackerStates,
			resetsManager.context.state,
			settings.context.state,
		) { trackers, resetsState, settingsState ->
			val imuTrackers = trackers.filter { isImuAssigned(it) }
			TrackingChecklistStep(
				valid = resetsState.mountingResetCompleted,
				enabled = settingsState.data.resetsConfig.lastMountingMethod == MountingMethod.POSE && imuTrackers.isNotEmpty(),
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
	private val trackerStates: StateFlow<List<ChecklistTracker>>,
	private val resetsManager: ResetsManager,
	private val settings: Settings,
) : TrackingChecklistBehaviourType {
	override fun observe(receiver: TrackingChecklist) {
		combine(
			trackerStates,
			resetsManager.context.state,
			settings.context.state,
		) { trackers, resetsState, settingsState ->
			val resetsConfig = settingsState.data.resetsConfig
			val imuTrackers = trackers.filter { isImuAssigned(it) }
			TrackingChecklistStep(
				valid = resetsState.feetMountingResetCompleted,
				enabled = resetsConfig.lastMountingMethod == MountingMethod.POSE &&
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

class StayAlignedCheckBehaviour(
	private val settings: Settings,
) : TrackingChecklistBehaviourType {
	override fun observe(receiver: TrackingChecklist) {
		settings.context.state.map { settingsState ->
			TrackingChecklistStep(
				valid = settingsState.data.stayAlignedConfig.enabled,
				enabled = true,
				optional = true,
				ignorable = true,
				visibility = TrackingChecklistStepVisibility.WHEN_INVALID,
			)
		}
			.distinctUntilChanged()
			.onEach { step -> receiver.context.dispatch(TrackingChecklistActions.UpdateStep(TrackingChecklistStepId.STAY_ALIGNED_CONFIGURED, step)) }
			.launchIn(receiver.context.scope)
	}
}
