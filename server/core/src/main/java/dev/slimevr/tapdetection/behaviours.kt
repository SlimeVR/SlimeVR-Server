package dev.slimevr.tapdetection

import dev.slimevr.config.TapDetectionConfig
import dev.slimevr.timeSource
import dev.slimevr.tracker.Tracker
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
import kotlinx.coroutines.launch
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.rpc.ResetType
import solarxr_protocol.rpc.TapDetectionSetupNotification
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeMark

class TapDetectionBasicBehaviour : TapDetectionBehaviour {
	data class TrackerTapDetectionState(
		var trackerId: Int,
		var numberTrackersOverThreshold: Int = 1,
		var resetType: ResetType? = null,
		var tapsNeeded: Int = 2,
		var actionDelay: Float = 0f,
		val accelList: ArrayDeque<Pair<Float, TimeMark>> = ArrayDeque(),
		val tapTimestamps: ArrayDeque<TimeMark> = ArrayDeque(),
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
				// Tracker state emits on every rotation packet, but only bodyPart/status matter here.
				// Dedup per tracker first, or combine gets resumed once per packet per tracker.
				combine(
					trackers.map { tracker ->
						tracker.context.state.distinctUntilChanged { a, b -> a.bodyPart == b.bodyPart && a.status == b.status }
					},
				) { states ->
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
				// To keep track of which trackers are over threshold
				val trackersOverThreshold = mutableSetOf<Int>()

				// Computed once per outer-flow refresh for all trackers
				val trackersBodyParts = trackers.map { it.context.state.value.bodyPart }.toSet()
				val yawResetBodyPart = listOf(tapDetectionConfig.yawResetBodyPart, BodyPart.UPPER_CHEST, BodyPart.CHEST, BodyPart.HIP, BodyPart.WAIST)
					.firstOrNull { it in trackersBodyParts } ?: BodyPart.UPPER_CHEST
				val fullResetBodyPart = listOf(tapDetectionConfig.fullResetBodyPart, BodyPart.LEFT_UPPER_LEG, BodyPart.LEFT_LOWER_LEG)
					.firstOrNull { it in trackersBodyParts } ?: BodyPart.LEFT_UPPER_LEG
				val mountingResetBodyPart = listOf(tapDetectionConfig.mountingResetBodyPart, BodyPart.RIGHT_UPPER_LEG, BodyPart.RIGHT_LOWER_LEG)
					.firstOrNull { it in trackersBodyParts } ?: BodyPart.RIGHT_UPPER_LEG

				trackers.map { tracker ->
					val trackerTapDetectionState = createTrackerTapDetectionState(
						tapDetectionConfig,
						setupMode,
						tracker,
						yawResetBodyPart,
						fullResetBodyPart,
						mountingResetBodyPart,
					)

					// Inner flow emits whenever a tracker's rawAcceleration is updated
					tracker.context.state
						.filter { trackerTapDetectionState.resetType != null || setupMode }
						.map { it.rawAcceleration }
						.distinctUntilChanged()
						.onEach {
							val tapTriggered = runTapDetection(
								timeSource.markNow(),
								trackersOverThreshold,
								trackerTapDetectionState,
								it,
							)

							if (tapTriggered) {
								receiver.context.scope.launch {
									// If it's in setup mode, tap to assign
									if (setupMode) {
										receiver.server.sendSolarxrRpc(
											TapDetectionSetupNotification(
												trackerTapDetectionState.trackerId.toUShort(),
											),
										)
									}

									// If it has a reset to execute
									trackerTapDetectionState.resetType?.let { reset ->
										receiver.resetsManager.scheduleReset(
											"TapDetection",
											reset,
											trackerTapDetectionState.actionDelay,
										)
									}
								}
							}
						}
				}.merge()
			}
			.launchIn(receiver.context.scope)
	}

	// Loads TapDetection config and initializes a tracker's state
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
	fun runTapDetection(
		now: TimeMark,
		trackersOverThreshold: MutableSet<Int>,
		trackerTapDetectionState: TrackerTapDetectionState,
		trackerAcceleration: Vector3,
	): Boolean {
		// Remove old stored accelerations (if they are too old)
		while (trackerTapDetectionState.accelList.isNotEmpty() && (trackerTapDetectionState.accelList.first().second + ACCEL_WINDOW).hasPassedNow()) {
			trackerTapDetectionState.accelList.removeFirst()
		}

		// Get the acceleration of the tracker and store it
		trackerTapDetectionState.accelList.add(trackerAcceleration.len() to now)

		val max = trackerTapDetectionState.accelList.maxOf { it.first }
		val min = trackerTapDetectionState.accelList.minOf { it.first }
		val accelDelta = max - min

		// Is this tracker over threshold for false positive prevention?
		val isOverThreshold = trackerAcceleration.lenSq() > ALLOWED_BODY_ACCEL_SQUARED
		if (isOverThreshold) {
			trackersOverThreshold.add(trackerTapDetectionState.trackerId)
		} else {
			trackersOverThreshold.remove(trackerTapDetectionState.trackerId)
		}

		// Check for a single tap
		if (accelDelta > NEEDED_ACCEL_DELTA && !trackerTapDetectionState.waitForLowAccel) {
			val othersOverThreshold = trackersOverThreshold.size - if (isOverThreshold) 1 else 0
			if (othersOverThreshold < trackerTapDetectionState.numberTrackersOverThreshold) {
				trackerTapDetectionState.tapTimestamps.add(now)
				trackerTapDetectionState.waitForLowAccel = true
			}
		}

		// Achieved low accel?
		if (max < ALLOWED_BODY_ACCEL) trackerTapDetectionState.waitForLowAccel = false

		if (trackerTapDetectionState.tapTimestamps.isNotEmpty()) {
			// Remove old stored taps (if they are too old)
			val totalTapWindow = TAP_WINDOW_PER_TAP * trackerTapDetectionState.tapTimestamps.size
			while (trackerTapDetectionState.tapTimestamps.isNotEmpty() && (trackerTapDetectionState.tapTimestamps.first() + totalTapWindow).hasPassedNow()) {
				trackerTapDetectionState.tapTimestamps.removeFirst()
			}

			if (trackerTapDetectionState.tapTimestamps.size >= trackerTapDetectionState.tapsNeeded) {
				trackerTapDetectionState.accelList.clear()
				trackerTapDetectionState.tapTimestamps.clear()
				trackerTapDetectionState.waitForLowAccel = false

				return true
			}
		}

		return false
	}

	override fun reduce(state: TapDetectionState, action: TapDetectionActions) = when (action) {
		is TapDetectionActions.SetSetupMode -> {
			state.copy(setupMode = action.setupMode)
		}
	}

	companion object {
		const val NEEDED_ACCEL_DELTA = 6.0f
		const val ALLOWED_BODY_ACCEL = 2.5f
		const val ALLOWED_BODY_ACCEL_SQUARED = ALLOWED_BODY_ACCEL * ALLOWED_BODY_ACCEL
		val ACCEL_WINDOW = 0.06.seconds
		val TAP_WINDOW_PER_TAP = 0.3.seconds
	}
}
