package dev.slimevr.trackeraction

import dev.slimevr.config.TrackerActionConfig
import dev.slimevr.tracker.TrackerState
import dev.slimevr.util.timeSource
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
import solarxr_protocol.datatypes.DeviceOrigin
import solarxr_protocol.datatypes.TrackerActionMethod
import solarxr_protocol.datatypes.TrackerStatus
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeMark

class TapDetectionBehaviour : TrackerActionBehaviour {
	data class TrackerTapDetectionState(
		var trackerId: Int,
		var performsReset: Boolean = false,
		var tapsNeeded: Int = 2,
		val accelList: ArrayDeque<Pair<Float, TimeMark>> = ArrayDeque(),
		val tapTimestamps: ArrayDeque<TimeMark> = ArrayDeque(),
		var waitForLowAccel: Boolean = false,
	)

	// Loads TapDetection config and initializes a tracker's state
	private fun createTrackerTapDetectionState(
		tapDetectionConfig: TrackerActionConfig,
		tapDetectionState: TrackerActionState,
		trackerState: TrackerState,
	): TrackerTapDetectionState {
		// This holds a tracker's config and state for tap detection
		val trackerTapDetectionState = TrackerTapDetectionState(trackerState.id)

		// setupMode uses defaults
		if (!tapDetectionState.setupMode) {
			when (trackerState.bodyPart) {
				tapDetectionState.yawResetBodyPart if tapDetectionConfig.yawResetAction.enabled -> {
					trackerTapDetectionState.performsReset = true
					trackerTapDetectionState.tapsNeeded = tapDetectionConfig.yawResetAction.taps
				}

				tapDetectionState.fullResetBodyPart if tapDetectionConfig.fullResetAction.enabled -> {
					trackerTapDetectionState.performsReset = true
					trackerTapDetectionState.tapsNeeded = tapDetectionConfig.fullResetAction.taps
				}

				tapDetectionState.mountingResetBodyPart if tapDetectionConfig.mountingResetAction.enabled -> {
					trackerTapDetectionState.performsReset = true
					trackerTapDetectionState.tapsNeeded = tapDetectionConfig.mountingResetAction.taps
				}

				else -> {}
			}
		}

		return trackerTapDetectionState
	}

	// This + the assigned tap body parts are used.
	private val falsePositiveBodyParts = setOf(
		BodyPart.UPPER_CHEST,
		BodyPart.LOWER_CHEST,
		BodyPart.UPPER_WAIST,
		BodyPart.LOWER_WAIST,
		BodyPart.HIP,
		BodyPart.LEFT_UPPER_LEG,
		BodyPart.RIGHT_UPPER_LEG,
		BodyPart.LEFT_LOWER_LEG,
		BodyPart.RIGHT_LOWER_LEG,
		BodyPart.LEFT_FOOT,
		BodyPart.RIGHT_FOOT,
	)

	@OptIn(ExperimentalCoroutinesApi::class)
	override fun observe(receiver: TrackerActionManager) {
		val tapConfigFlow = receiver.settings.context.state
			.map { it.data.trackerActionConfig }
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
					trackers.filter {
						// Only run tap detection for IMU trackers
						it.context.state.value.imuType != null
					}.map { tracker ->
						tracker.context.state.distinctUntilChanged { a, b -> a.bodyPart == b.bodyPart && a.status == b.status }
					},
				) { states ->
					states.map { it.bodyPart to it.status }
				}
					.distinctUntilChanged()
					.map { trackers.filter { it.context.state.value.status == TrackerStatus.OK } }
			}

		combine(tapConfigFlow, setupModeFlow, okTrackersFlow, ::Triple).onEach { (tapDetectionConfig, setupMode, trackers) ->
			if (setupMode) {
				receiver.context.dispatch(TrackerActionActions.UpdateBodyParts(null, null, null))
				return@onEach
			}

			val trackersBodyParts = trackers.map { it.context.state.value.bodyPart }.toSet()
			val yawResetBodyPart = arrayOf(tapDetectionConfig.yawResetAction.bodyPart, BodyPart.UPPER_CHEST, BodyPart.LOWER_CHEST, BodyPart.HIP, BodyPart.UPPER_WAIST, BodyPart.LOWER_WAIST)
				.firstOrNull { it in trackersBodyParts } ?: BodyPart.UPPER_CHEST
			val fullResetBodyPart = arrayOf(tapDetectionConfig.fullResetAction.bodyPart, BodyPart.LEFT_UPPER_LEG, BodyPart.LEFT_LOWER_LEG)
				.firstOrNull { it in trackersBodyParts } ?: BodyPart.LEFT_UPPER_LEG
			val mountingResetBodyPart = arrayOf(tapDetectionConfig.mountingResetAction.bodyPart, BodyPart.RIGHT_UPPER_LEG, BodyPart.RIGHT_LOWER_LEG)
				.firstOrNull { it in trackersBodyParts } ?: BodyPart.RIGHT_UPPER_LEG

			receiver.context.dispatch(TrackerActionActions.UpdateBodyParts(yawResetBodyPart, fullResetBodyPart, mountingResetBodyPart))
		}.launchIn(receiver.context.scope)

		// Outer flow is refreshed whenever TapDetection config, setupMode, or a tracker's bodyPart or status changes
		combine(
			tapConfigFlow,
			receiver.context.state,
			okTrackersFlow,
			::Triple,
		)
			.flatMapLatest { (tapDetectionConfig, tapDetectionState, trackers) ->
				// To keep track of which trackers are moving
				val tapsTrackersOverThreshold = tapDetectionConfig.tapsTrackersOverThreshold
				val bodyPartsToCheck = falsePositiveBodyParts + tapDetectionState.yawResetBodyPart + tapDetectionState.fullResetBodyPart + tapDetectionState.mountingResetBodyPart
				val trackersOverThreshold = mutableSetOf<Int>()

				trackers.map { tracker ->
					val trackerTapDetectionState = createTrackerTapDetectionState(
						tapDetectionConfig,
						tapDetectionState,
						tracker.context.state.value,
					)

					// Inner flow emits whenever a tracker's rawAcceleration is updated
					tracker.context.state
						.filter { tapDetectionState.setupMode || it.bodyPart in bodyPartsToCheck }
						.map { it.rawAcceleration to it.origin }
						.distinctUntilChanged()
						.onEach { (rawAcceleration, origin) ->
							if (rawAcceleration == null) return@onEach

							val thresholdMultiplier = if (origin == DeviceOrigin.HID) HID_MULTIPLIER else 1f

							// Is this tracker over threshold for false positive prevention?
							val isOverThreshold = if (rawAcceleration.lenSq() > ALLOWED_BODY_ACCEL_SQUARED * thresholdMultiplier) {
								trackersOverThreshold.add(trackerTapDetectionState.trackerId)
								true
							} else {
								trackersOverThreshold.remove(trackerTapDetectionState.trackerId)
								false
							}

							// Only trackers that actually have to detect taps
							if (trackerTapDetectionState.performsReset || tapDetectionState.setupMode) {
								val othersOverThreshold = trackersOverThreshold.count() - if (isOverThreshold) 1 else 0
								val tapTriggered = runTapDetection(
									timeSource.markNow(),
									othersOverThreshold >= tapsTrackersOverThreshold,
									trackerTapDetectionState,
									rawAcceleration,
									thresholdMultiplier,
								)

								if (tapTriggered) {
									receiver.actionTriggered(trackerTapDetectionState.trackerId, "TapDetection", TrackerActionMethod.TAP)
								}
							}
						}
				}.merge()
			}
			.launchIn(receiver.context.scope)
	}

	// Logic loop for tap detection
	fun runTapDetection(
		now: TimeMark,
		bodyAccelerating: Boolean,
		trackerTapDetectionState: TrackerTapDetectionState,
		trackerAcceleration: Vector3,
		thresholdMultiplier: Float,
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

		// Check for a single tap
		if (!bodyAccelerating && accelDelta > NEEDED_ACCEL_DELTA * thresholdMultiplier && !trackerTapDetectionState.waitForLowAccel) {
			trackerTapDetectionState.tapTimestamps.add(now)
			trackerTapDetectionState.waitForLowAccel = true
		}

		// Achieved low accel?
		if (max < ALLOWED_BODY_ACCEL * thresholdMultiplier) trackerTapDetectionState.waitForLowAccel = false

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

	companion object {
		const val NEEDED_ACCEL_DELTA = 6.0f
		const val ALLOWED_BODY_ACCEL = 2.5f
		const val ALLOWED_BODY_ACCEL_SQUARED = ALLOWED_BODY_ACCEL * ALLOWED_BODY_ACCEL
		val ACCEL_WINDOW = 0.06.seconds
		val TAP_WINDOW_PER_TAP = 0.3.seconds

		// Smol slimes/butterflies are more sensible to taps
		const val HID_MULTIPLIER = 1.3f
	}
}
