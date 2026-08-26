package dev.slimevr.tracker

import com.jme3.math.FastMath
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class PolarityTrackingTest {
    val ninetyYaw = EulerAngles(EulerOrder.YZX, 0f, FastMath.HALF_PI, 0f).toQuaternion()
    val almostOneEightyYaw = EulerAngles(EulerOrder.YZX, 0f, FastMath.PI - 0.01f, 0f).toQuaternion()
    val overOneEightyYaw = EulerAngles(EulerOrder.YZX, 0f, FastMath.PI + 0.01f, 0f).toQuaternion()

    @Test
    fun `Opposite polarities not equal`() {
        assertNotEquals(Quaternion.IDENTITY, -Quaternion.IDENTITY)
    }

    @Test
    fun `360 degrees rotation gives opposite polarity`() {
        assertEquals(-Quaternion.IDENTITY, Quaternion.IDENTITY * -Quaternion.IDENTITY)
    }

    @Test
    fun `twinNearest IDENTITY rests polarity`() {
        assertEquals(ninetyYaw, ninetyYaw.unaryMinus().twinNearest(Quaternion.IDENTITY))
    }

    @Test
    fun `Session Calibration aligns raw rotation polarity with IDENTITY up to 180 degrees of calibrated rotation`() {
        // almostOneEightyYaw is aligned with IDENTITY
        assertEquals(ninetyYaw, ninetyYaw.twinNearest(Quaternion.IDENTITY))
        val headingCorrection = estimateHeadingCorrect(ninetyYaw, Quaternion.IDENTITY)
        val attitudeAlignment = estimateAttitudeAlign(ninetyYaw, headingCorrection, Quaternion.IDENTITY)

        // This should not be aligned with IDENTITY
        val rotatedWithinLessThanOneEighty = ninetyYaw * almostOneEightyYaw
        assertNotEquals(rotatedWithinLessThanOneEighty, rotatedWithinLessThanOneEighty.twinNearest(Quaternion.IDENTITY))

        // This should be aligned with IDENTITY once calibrated (removing almostOneEightyYaw from rotatedWithinLessThanOneEighty)
        val calibratedSamePolarity = applyCalibration(rotatedWithinLessThanOneEighty, headingCorrection, attitudeAlignment)
        assertEquals(calibratedSamePolarity, calibratedSamePolarity.twinNearest(Quaternion.IDENTITY))

        // This should be aligned with IDENTITY once calibrated (removing almostOneEightyYaw from rotatedWithinLessThanOneEighty)
        val rotatedMoreThanOneEighty = ninetyYaw * overOneEightyYaw
        val calibratedDifferentPolarity = applyCalibration(rotatedMoreThanOneEighty, headingCorrection, attitudeAlignment)
        assertNotEquals(calibratedDifferentPolarity, calibratedDifferentPolarity.twinNearest(Quaternion.IDENTITY))
    }
}