package dev.slimevr.tapdetection

import io.github.axisangles.ktmath.Vector3
import kotlin.test.Test
import kotlin.test.assertEquals

class TapDetectionTest {
    data class AccelEvent(
        val delaySincePrevious: Long,
        val accel: Vector3,
        val expectedTap: Boolean,
    )

    val highAccel = Vector3(0f, 0f, TapDetectionBasicBehaviour.NEEDED_ACCEL_DELTA * 3f)
    val lowAccel = Vector3(0f, 0f, 0f)
    // Accelerations needed for a tap happen with this delay
    val accelDelay = (TapDetectionBasicBehaviour.ACCEL_WINDOW_NS * 0.9f).toLong()
    // Delay between several taps
    val tapDelay = (TapDetectionBasicBehaviour.TAP_WINDOW_PER_TAP_NS * 0.9f).toLong()
    // Too long of a delay between several taps
    val tapTimeoutDelay = (TapDetectionBasicBehaviour.TAP_WINDOW_PER_TAP_NS * 1.1f).toLong()

    /**
     * Runs a sequence of tap events through TapDetection, asserting the results.
     */
    private fun runTapSequence(
        tapsNeeded: Int,
        events: List<AccelEvent>,
    ) {
        val state = TapDetectionBasicBehaviour.TrackerTapDetectionState(
            trackerId = 1,
            tapsNeeded = tapsNeeded,
        )
        val trackersOverThreshold = mutableSetOf<Int>()
        val behaviour = TapDetectionBasicBehaviour()

        var now = 0L
        events.forEach { event ->
            now += event.delaySincePrevious
            assertEquals(
                event.expectedTap,
                behaviour.runTapDetection(
                    now = now,
                    trackersOverThreshold = trackersOverThreshold,
                    trackerTapDetectionState = state,
                    trackerAcceleration = event.accel,
                ),
            )
        }
    }

    @Test
    fun `Detect double tap`() = runTapSequence(
        tapsNeeded = 2,
        events = listOf(
            AccelEvent(0L, lowAccel, expectedTap = false),
            AccelEvent(accelDelay, highAccel, expectedTap = false),
            AccelEvent(tapDelay, lowAccel, expectedTap = false),
            AccelEvent(accelDelay, highAccel, expectedTap = true),
        ),
    )

    @Test
    fun `Double tap timed out`() = runTapSequence(
        tapsNeeded = 2,
        events = listOf(
            AccelEvent(0L, lowAccel, expectedTap = false),
            AccelEvent(accelDelay, highAccel, expectedTap = false),
            AccelEvent(tapTimeoutDelay, lowAccel, expectedTap = false),
            AccelEvent(accelDelay, highAccel, expectedTap = false),
        ),
    )

    @Test
    fun `Detect triple tap`() = runTapSequence(
        tapsNeeded = 3,
        events = listOf(
            AccelEvent(0L, lowAccel, expectedTap = false),
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
            AccelEvent(0L, lowAccel, expectedTap = false),
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
            AccelEvent(0L, lowAccel, expectedTap = false),
            AccelEvent(accelDelay, highAccel, expectedTap = false),
            AccelEvent(tapDelay, highAccel, expectedTap = false),
            AccelEvent(tapDelay, highAccel, expectedTap = false),
            AccelEvent(tapTimeoutDelay, highAccel, expectedTap = false),
        ),
    )
}