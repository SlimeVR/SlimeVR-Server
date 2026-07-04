package dev.slimevr.tapdetection

import dev.slimevr.config.TapDetectionConfig
import dev.slimevr.tracker.Tracker
import dev.slimevr.util.safeLaunch
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.rpc.ResetType
import solarxr_protocol.rpc.TapDetectionSetupNotification

private const val NS_CONVERTER = 1.0e9f
private const val CLUMP_TIME_NS = 0.06f * NS_CONVERTER
private const val NEEDED_ACCEL_DELTA = 6.0f
private const val ALLOWED_BODY_ACCEL = 2.5f
private const val ALLOWED_BODY_ACCEL_SQUARED = ALLOWED_BODY_ACCEL * ALLOWED_BODY_ACCEL
private const val TAP_WINDOW_PER_TAP_NS = 0.3f * NS_CONVERTER

class TapDetectionBasicBehaviour : TapDetectionBehaviour {
	data class TrackerTapDetectionState(
		var trackerId: Int,
		var numberTrackersOverThreshold: Int = 0,
		var resetType: ResetType? = null,
		var tapsNeeded: Int = 2,
		var actionDelay: Float = 0f,
		val accelList: ArrayDeque<Pair<Float, Long>> = ArrayDeque(),
		val tapTimestamps: ArrayDeque<Long> = ArrayDeque(),
		var waitForLowAccel: Boolean = false,
	)

	@OptIn(ExperimentalCoroutinesApi::class)
	override fun observe(receiver: TapDetectionManager) {
		val tapConfigFlow = receiver.settings.context.state
			.map { it.data.tapDetectionConfig }
			.distinctUntilChanged()
		val setupModeFlow = receiver.context.state
			.map { it.setupMode }
			.distinctUntilChanged()
		val okTrackersFlow = receiver.server.context.state
			.map { it.trackers.values }
			.flatMapLatest { trackers ->
				combine(trackers.map { it.context.state }) { states ->
					states.map { it.bodyPart to it.status }
				}
					.distinctUntilChanged()
					.map { trackers.filter { tracker -> tracker.context.state.value.status == TrackerStatus.OK } }
			}

		// Outer flow is refreshed whenever TapDetection config, setupMode, or a tracker's bodyPart or status changes
		combine(
			tapConfigFlow,
			setupModeFlow,
			okTrackersFlow,
			::Triple,
		)
			.flatMapLatest { (tapDetectionConfig, setupMode, trackers) ->
				// Computed once per outer-flow refresh for all trackers
				val trackersBodyParts = trackers.map { it.context.state.value.bodyPart }.toSet()
				val yawResetBodyPart = listOf(tapDetectionConfig.yawResetBodyPart, BodyPart.UPPER_CHEST, BodyPart.CHEST, BodyPart.HIP, BodyPart.WAIST)
					.firstOrNull { it in trackersBodyParts } ?: BodyPart.UPPER_CHEST
				val fullResetBodyPart = listOf(tapDetectionConfig.fullResetBodyPart, BodyPart.LEFT_UPPER_LEG, BodyPart.LEFT_LOWER_LEG)
					.firstOrNull { it in trackersBodyParts } ?: BodyPart.LEFT_UPPER_LEG
				val mountingResetBodyPart = listOf(tapDetectionConfig.mountingResetBodyPart, BodyPart.RIGHT_UPPER_LEG, BodyPart.RIGHT_LOWER_LEG)
					.firstOrNull { it in trackersBodyParts } ?: BodyPart.RIGHT_UPPER_LEG

				trackers.map { tracker ->
					val trackerTapDetectionState = createTrackerTapDetectionState(tapDetectionConfig, setupMode, tracker, yawResetBodyPart, fullResetBodyPart, mountingResetBodyPart)

					// Inner flow emits whenever a tracker's rawAcceleration is updated
					tracker.context.state
						.filter { trackerTapDetectionState.resetType != null || setupMode }
						.map { it.rawAcceleration }
						.distinctUntilChanged()
						.onEach { processTapDetection(receiver, setupMode, trackerTapDetectionState, it) }
				}.merge()
			}
			.launchIn(receiver.context.scope)
	}

	// Loads TapDetection config
	private fun createTrackerTapDetectionState(
		tapDetectionConfig: TapDetectionConfig,
		setupMode: Boolean,
		tracker: Tracker,
		yawResetBodyPart: BodyPart,
		fullResetBodyPart: BodyPart,
		mountingResetBodyPart: BodyPart,
	): TrackerTapDetectionState {
		// This holds a tracker's config and state for tap detection
		val trackerTapDetectionState = TrackerTapDetectionState(tracker.context.state.value.id)
		trackerTapDetectionState.numberTrackersOverThreshold = tapDetectionConfig.numberTrackersOverThreshold

		// setupMode uses defaults
		if (!setupMode) {
			when (tracker.context.state.value.bodyPart) {
				yawResetBodyPart if tapDetectionConfig.yawResetEnabled -> {
					trackerTapDetectionState.resetType = ResetType.YAW
					trackerTapDetectionState.tapsNeeded = tapDetectionConfig.yawResetTaps
					trackerTapDetectionState.actionDelay = tapDetectionConfig.yawResetDelay
				}

				fullResetBodyPart if tapDetectionConfig.fullResetEnabled -> {
					trackerTapDetectionState.resetType = ResetType.FULL
					trackerTapDetectionState.tapsNeeded = tapDetectionConfig.fullResetTaps
					trackerTapDetectionState.actionDelay = tapDetectionConfig.fullResetDelay
				}

				mountingResetBodyPart if tapDetectionConfig.mountingResetEnabled -> {
					trackerTapDetectionState.resetType = ResetType.MOUNTING
					trackerTapDetectionState.tapsNeeded = tapDetectionConfig.mountingResetTaps
					trackerTapDetectionState.actionDelay = tapDetectionConfig.mountingResetDelay
				}

				else -> {}
			}
		}

		return trackerTapDetectionState
	}

	// Logic loop for tap detection
	private fun processTapDetection(
		receiver: TapDetectionManager,
		setupMode: Boolean,
		state: TrackerTapDetectionState,
		trackerAcceleration: Vector3,
	) {
		val now = System.nanoTime()

		// Get the acceleration of the tracker and store it
		state.accelList.add(trackerAcceleration.len() to now)

		// Remove old stored accelerations (if they are too old)
		while (state.accelList.isNotEmpty() && now - state.accelList.first().second > CLUMP_TIME_NS) {
			state.accelList.removeFirst()
		}

		val max = state.accelList.maxOfOrNull { it.first } ?: 0f
		val min = state.accelList.minOfOrNull { it.first } ?: 0f
		val accelDelta = max - min

		// Check for a single tap
		if (accelDelta > NEEDED_ACCEL_DELTA && !state.waitForLowAccel) {
			val othersOverThreshold = receiver.server.context.state.value.trackers.values
				.count { it.context.state.value.id != state.trackerId && it.context.state.value.rawAcceleration.lenSq() > ALLOWED_BODY_ACCEL_SQUARED }
			if (othersOverThreshold < state.numberTrackersOverThreshold) {
				state.tapTimestamps.add(now)
				// After a tap, a lower acceleration is needed before another one
				state.waitForLowAccel = true
			}
		}

		// Achieved low accel?
		if (max < ALLOWED_BODY_ACCEL) state.waitForLowAccel = false

		if (state.tapTimestamps.isNotEmpty()) {
			// Remove old stored taps (if they are too old)
			val totalWindowNs = (TAP_WINDOW_PER_TAP_NS * state.tapTimestamps.size).toLong()
			while (state.tapTimestamps.isNotEmpty() && now - state.tapTimestamps.first() > totalWindowNs) {
				state.tapTimestamps.removeFirst()
			}

			if (state.tapTimestamps.size >= state.tapsNeeded) {
				// Taps completed!
				receiver.context.scope.safeLaunch {
					// If it's in setup mode, tap to assign
					if (setupMode) {
						receiver.server.sendSolarxrRpc(
							TapDetectionSetupNotification(state.trackerId.toUShort()),
						)
					}

					// If it has a reset to execute
					state.resetType?.let { reset ->
						receiver.resetsManager.scheduleReset("TapDetection", reset, state.actionDelay)
					}
				}

				state.accelList.clear()
				state.tapTimestamps.clear()
				state.waitForLowAccel = false
			}
		}
	}

	override fun reduce(state: TapDetectionState, action: TapDetectionActions) = when (action) {
		is TapDetectionActions.SetSetupMode -> {
			state.copy(setupMode = action.setupMode)
		}
	}
}
