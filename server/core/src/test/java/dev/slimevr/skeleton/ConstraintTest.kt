package dev.slimevr.skeleton

import com.jme3.math.FastMath
import dev.slimevr.quaternionAssertEquals
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import org.junit.jupiter.api.Test
import kotlin.math.sqrt

class ConstraintTest {
	@Test
	fun applyLocalOperationTest() {
		val parent = EulerAngles(
			EulerOrder.YZX,
			0f,
			90f,
			0f,
		).toQuaternion()
		val child = EulerAngles(
			EulerOrder.YZX,
			0f,
			0f,
			90f,
		).toQuaternion()
		val childLocal = EulerAngles(
			EulerOrder.YZX,
			0f,
			-90f,
			90f,
		).toQuaternion()

		val newChild = applyLocalOperation(
			parent,
			child,
		) {
			quaternionAssertEquals(childLocal, it, message = "Rotation is not local")
			it
		}
		quaternionAssertEquals(child, newChild, message = "Final global rotation is not equal")
	}

	@Test
	fun decomposeToSwingTwistTest() {
		// Based on Inverse Kinematics – Cyclic Coordinate Descent (CCD) (Ben Kenwright)
		//  https://alogicalmind.com/res/inverse_kinematics_ccd/paper.pdf
		val original = Quaternion(1f, 2f, 3f, 4f).unit()
		val denominator = sqrt((original.w * original.w) + (original.y * original.y))
		val twist = Quaternion(
			w = original.w / denominator,
			x = 0f,
			y = original.y / denominator,
			z = 0f,
		)
		val swing = Quaternion(
			w = denominator,
			x = ((original.w * original.x) + (original.y * original.z)) / denominator,
			y = 0f,
			z = ((original.w * original.z) - (original.x * original.y)) / denominator,
		)

		quaternionAssertEquals(original, swing * twist, message = "Swing x Twist != Original")

		val (testSwing, testTwist) = decomposeToSwingTwist(original, Vector3.NEG_Y)

		quaternionAssertEquals(twist, testTwist, message = "Twist is not equal")
		quaternionAssertEquals(swing, testSwing, message = "Swing is not equal")
	}
}
