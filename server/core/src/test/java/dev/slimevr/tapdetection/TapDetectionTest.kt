package dev.slimevr.tapdetection

import dev.slimevr.tracker.Motion
import io.github.axisangles.ktmath.Vector3
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.TestTimeSource

class TapDetectionTest {
	data class AccelEvent(
		val delaySincePrevious: Duration,
		val accel: Vector3,
		val expectedTap: Boolean,
	)

	// Accel high enough to trigger a tap
	val highAccel = Vector3(0f, 0f, TapDetectionBasicBehaviour.NEEDED_ACCEL_DELTA * 3f)

	// Low accel needed as rest between taps
	val lowAccel = Vector3.ZERO

	// Accelerations needed for a tap happen with this delay
	val accelDelay = TapDetectionBasicBehaviour.ACCEL_WINDOW * 0.9

	// Delay between several taps
	val tapDelay = TapDetectionBasicBehaviour.TAP_WINDOW_PER_TAP * 0.9

	// Too long of a delay between several taps
	val tapTimeoutDelay = TapDetectionBasicBehaviour.TAP_WINDOW_PER_TAP * 1.1

	/**
	 * Runs a sequence of tap events through TapDetection, asserting the results.
	 */
	private fun runTapSequence(
		tapsNeeded: Int,
		events: List<AccelEvent>,
		bodyAccelerating: Boolean = false,
		motion: Motion = Motion.RESTING,
	) {
		val state = TapDetectionBasicBehaviour.TrackerTapDetectionState(
			trackerId = 1,
			tapsNeeded = tapsNeeded,
		)
		val behaviour = TapDetectionBasicBehaviour()

		val timeSource = TestTimeSource()
		events.forEach { event ->
			timeSource += event.delaySincePrevious
			assertEquals(
				event.expectedTap,
				behaviour.runTapDetection(
					now = timeSource.markNow(),
					bodyAccelerating = bodyAccelerating,
					trackerTapDetectionState = state,
					trackerAcceleration = event.accel,
					trackerMotion = motion,
				),
			)
		}
	}

	@Test
	fun `Detect double tap`() = runTapSequence(
		tapsNeeded = 2,
		events = listOf(
			AccelEvent(Duration.ZERO, lowAccel, expectedTap = false),
			AccelEvent(accelDelay, highAccel, expectedTap = false),
			AccelEvent(tapDelay, lowAccel, expectedTap = false),
			AccelEvent(accelDelay, highAccel, expectedTap = true),
		),
	)

	@Test
	fun `Double tap timed out`() = runTapSequence(
		tapsNeeded = 2,
		events = listOf(
			AccelEvent(Duration.ZERO, lowAccel, expectedTap = false),
			AccelEvent(accelDelay, highAccel, expectedTap = false),
			AccelEvent(tapTimeoutDelay, lowAccel, expectedTap = false),
			AccelEvent(accelDelay, highAccel, expectedTap = false),
		),
	)

	@Test
	fun `Detect triple tap`() = runTapSequence(
		tapsNeeded = 3,
		events = listOf(
			AccelEvent(Duration.ZERO, lowAccel, expectedTap = false),
			AccelEvent(accelDelay, highAccel, expectedTap = false),
			AccelEvent(tapDelay, lowAccel, expectedTap = false),
			AccelEvent(accelDelay, highAccel, expectedTap = false),
			AccelEvent(tapDelay, lowAccel, expectedTap = false),
			AccelEvent(accelDelay, highAccel, expectedTap = true),
		),
	)

	@Test
	fun `Triple tap timed out`() = runTapSequence(
		tapsNeeded = 3,
		events = listOf(
			AccelEvent(Duration.ZERO, lowAccel, expectedTap = false),
			AccelEvent(accelDelay, highAccel, expectedTap = false),
			AccelEvent(tapDelay, lowAccel, expectedTap = false),
			AccelEvent(accelDelay, highAccel, expectedTap = false),
			AccelEvent(tapTimeoutDelay, lowAccel, expectedTap = false),
			AccelEvent(accelDelay, highAccel, expectedTap = false),
		),
	)

	@Test
	fun `Tap not repeated while accel stays high`() = runTapSequence(
		tapsNeeded = 2,
		events = listOf(
			AccelEvent(Duration.ZERO, lowAccel, expectedTap = false),
			AccelEvent(accelDelay, highAccel, expectedTap = false),
			AccelEvent(tapDelay, highAccel, expectedTap = false),
			AccelEvent(accelDelay, highAccel, expectedTap = false),
			AccelEvent(tapDelay, highAccel, expectedTap = false),
		),
	)

	@Test
	fun `Tap not detected while rest of body is accelerating`() = runTapSequence(
		tapsNeeded = 2,
		events = listOf(
			AccelEvent(Duration.ZERO, lowAccel, expectedTap = false),
			AccelEvent(accelDelay, highAccel, expectedTap = false),
			AccelEvent(tapDelay, lowAccel, expectedTap = false),
			AccelEvent(accelDelay, highAccel, expectedTap = false),
		),
		bodyAccelerating = true,
	)

	@Test
	fun `Tap not detected while tracker is rotating`() = runTapSequence(
		tapsNeeded = 2,
		events = listOf(
			AccelEvent(Duration.ZERO, lowAccel, expectedTap = false),
			AccelEvent(accelDelay, highAccel, expectedTap = false),
			AccelEvent(tapDelay, lowAccel, expectedTap = false),
			AccelEvent(accelDelay, highAccel, expectedTap = false),
		),
		motion = Motion.ROTATING,
	)
}
