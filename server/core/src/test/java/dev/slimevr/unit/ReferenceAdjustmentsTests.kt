package dev.slimevr.unit

import com.jme3.math.FastMath
import dev.slimevr.VRServer.Companion.getNextLocalTrackerId
import dev.slimevr.tracking.processor.TransformNode
import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.udp.IMUType
import io.eiren.math.FloatMath
import io.eiren.util.StringUtils.prettyNumber
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3.Companion.POS_Y
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.util.stream.Stream
import kotlin.streams.asStream

/**
 * Tests [TrackerResetsHandler.resetFull]
 */
class ReferenceAdjustmentsTests {
	@get:TestFactory
	val testsYaw: Stream<DynamicTest>
		get() = anglesSet
			.map { p: AnglesSet ->
				DynamicTest.dynamicTest(
					"Adjustment Yaw Test of Tracker(${p.pitch},${p.yaw},${p.roll})",
				) {
					yaws.forEach {
						checkReferenceAdjustmentYaw(
							q(p.pitch.toFloat(), p.yaw.toFloat(), p.roll.toFloat()),
							0,
							it,
							0,
						)
					}
				}
			}

	@get:TestFactory
	val testsFull: Stream<DynamicTest>
		get() = anglesSet
			.map { p: AnglesSet ->
				DynamicTest.dynamicTest(
					"Adjustment Full Test of Tracker(${p.pitch},${p.yaw},${p.roll})",
				) {
					anglesSet
						.forEach {
							checkReferenceAdjustmentFull(
								q(p.pitch.toFloat(), p.yaw.toFloat(), p.roll.toFloat()),
								it.pitch,
								it.yaw,
								it.roll,
							)
						}
				}
			}

	// TODO : Test is not passing because the test is wrong
	// See issue https://github.com/SlimeVR/SlimeVR-Server/issues/55
	// @TestFactory
	val testsForRotation: Stream<DynamicTest>
		get() = anglesSet
			.flatMap { p: AnglesSet ->
				yaws.asSequence().map {
					DynamicTest.dynamicTest(
						"Adjustment Rotation Test of Tracker(${p.pitch},${p.yaw},${p.roll}), Ref $it",
					) {
						testAdjustedTrackerRotation(
							q(p.pitch.toFloat(), p.yaw.toFloat(), p.roll.toFloat()),
							0,
							it,
							0,
						)
					}
				}.asStream()
			}

	fun checkReferenceAdjustmentFull(
		trackerQuat: Quaternion,
		refPitch: Int,
		refYaw: Int,
		refRoll: Int,
	) {
		var referenceQuat = q(refPitch.toFloat(), refYaw.toFloat(), refRoll.toFloat())
		val tracker = Tracker(
			null,
			getNextLocalTrackerId(),
			"test",
			"test",
			null,
			hasRotation = true,
			imuType = IMUType.UNKNOWN,
			needsReset = true,
		)
		tracker.setRotation(trackerQuat)
		tracker.resetsHandler.resetFull(referenceQuat)
		val read = tracker.getRotation()
		Assertions.assertNotNull(read, "Adjusted tracker didn't return rotation")

		// Use only yaw HMD rotation
		referenceQuat = referenceQuat.project(POS_Y).unit()
		Assertions.assertEquals(
			QuatEqualFullWithEpsilon(referenceQuat),
			QuatEqualFullWithEpsilon(read),
			"Adjusted quat is not equal to reference quat (${toDegs(referenceQuat)} vs ${toDegs(read)})",
		)
	}

	fun checkReferenceAdjustmentYaw(
		trackerQuat: Quaternion,
		refPitch: Int,
		refYaw: Int,
		refRoll: Int,
	) {
		// FIXME
		val referenceQuat = q(refPitch.toFloat(), refYaw.toFloat(), refRoll.toFloat())
		val tracker = Tracker(
			null,
			getNextLocalTrackerId(),
			"test",
			"test",
			null,
			hasRotation = true,
			imuType = IMUType.UNKNOWN,
			needsReset = true,
		)
		tracker.setRotation(trackerQuat)
		tracker.resetsHandler.resetYaw(referenceQuat)
		val read = tracker.getRotation()
		Assertions.assertNotNull(read, "Adjusted tracker didn't return rotation")
		Assertions.assertEquals(
			QuatEqualYawWithEpsilon(referenceQuat),
			QuatEqualYawWithEpsilon(read),
			"Adjusted quat is not equal to reference quat (${toDegs(referenceQuat)} vs ${toDegs(read)})",
		)
	}

	private fun testAdjustedTrackerRotation(
		trackerQuat: Quaternion,
		refPitch: Int,
		refYaw: Int,
		refRoll: Int,
	) {
		val referenceQuat = q(refPitch.toFloat(), refYaw.toFloat(), refRoll.toFloat())
		val tracker = Tracker(
			null,
			getNextLocalTrackerId(),
			"test",
			"test",
			null,
			hasRotation = true,
			imuType = IMUType.UNKNOWN,
			needsReset = true,
		)
		tracker.setRotation(trackerQuat)
		tracker.resetsHandler.resetFull(referenceQuat)

		// Use only yaw HMD rotation
		referenceQuat.project(POS_Y).unit()
		val trackerNode = TransformNode(true)
		val rotationNode = TransformNode(true)
		rotationNode.attachChild(trackerNode)
		trackerNode.localTransform.rotation = tracker.getRawRotation()
		var yaw = 0
		while (yaw <= 360) {
			var pitch = -90
			while (pitch <= 90) {
				var roll = -90
				while (roll <= 90) {
					val rotation = EulerAngles(
						EulerOrder.YZX,
						pitch * FastMath.DEG_TO_RAD,
						yaw * FastMath.DEG_TO_RAD,
						roll * FastMath.DEG_TO_RAD,
					).toQuaternion()
					val rotationCompare = EulerAngles(
						EulerOrder.YZX,
						pitch * FastMath.DEG_TO_RAD,
						(yaw + refYaw) * FastMath.DEG_TO_RAD,
						roll * FastMath.DEG_TO_RAD,
					).toQuaternion()
					rotationNode.localTransform.rotation = rotation
					rotationNode.update()
					tracker.setRotation(trackerNode.worldTransform.rotation)
					val angles = tracker.getRawRotation().toEulerAngles(EulerOrder.YZX)
					val anglesAdj = tracker.getRotation().toEulerAngles(EulerOrder.YZX)
					val anglesDiff = tracker
						.getRotation()
						.inv()
						.times(rotationCompare)
						.toEulerAngles(EulerOrder.YZX)
					if (!PRINT_TEST_RESULTS) {
						Assertions.assertTrue(
							FloatMath.equalsToZero(anglesDiff.x) &&
								FloatMath.equalsToZero(anglesDiff.y) &&
								FloatMath.equalsToZero(anglesDiff.z),
							name(yaw, pitch, roll, angles, anglesAdj, anglesDiff),
						)
					} else {
						if (FloatMath.equalsToZero(anglesDiff.x) &&
							FloatMath.equalsToZero(anglesDiff.y) &&
							FloatMath.equalsToZero(anglesDiff.z)
						) {
							successes++
						} else {
							errors++
						}
						println(name(yaw, pitch, roll, angles, anglesAdj, anglesDiff))
					}
					roll += 15
				}
				pitch += 15
			}
			yaw += 30
		}
		if (PRINT_TEST_RESULTS) println("Errors: $errors, successes: $successes")
	}

	private data class QuatEqualYawWithEpsilon(val q: Quaternion) {
		override fun equals(other: Any?): Boolean {
			if (other == null) return false
			val q2: Quaternion = when (other) {
				is Quaternion -> other
				is QuatEqualYawWithEpsilon -> other.q
				else -> return false
			}
			var degs1 = q.toEulerAngles(EulerOrder.YZX)
			var degs2 = q2.toEulerAngles(EulerOrder.YZX)
			if (degs1.y < -FloatMath.ANGLE_EPSILON_RAD) {
				degs1 = EulerAngles(
					EulerOrder.YZX,
					degs1.x,
					degs1.y + FastMath.TWO_PI,
					degs1.z,
				)
			}
			if (degs2.y < -FloatMath.ANGLE_EPSILON_RAD) {
				degs2 = EulerAngles(
					EulerOrder.YZX,
					degs2.x,
					degs2.y + FastMath.TWO_PI,
					degs2.z,
				)
			}
			return FloatMath.equalsWithEpsilon(degs1.y, degs2.y)
		}

		override fun hashCode(): Int = q.hashCode()
	}

	data class QuatEqualFullWithEpsilon(val q: Quaternion) {
		override fun hashCode(): Int = q.hashCode()

		override fun equals(other: Any?): Boolean {
			if (other == null) return false
			val q2: Quaternion = when (other) {
				is Quaternion -> other
				is QuatEqualFullWithEpsilon -> other.q
				else -> return false
			}
			var degs1 = q.toEulerAngles(EulerOrder.YZX)
			var degs2 = q2.toEulerAngles(EulerOrder.YZX)
			if (degs1.y < -FloatMath.ANGLE_EPSILON_RAD) {
				degs1 = EulerAngles(
					EulerOrder.YZX,
					degs1.x,
					degs1.y + FastMath.TWO_PI,
					degs1.z,
				)
			}
			if (degs2.y < -FloatMath.ANGLE_EPSILON_RAD) {
				degs2 = EulerAngles(
					EulerOrder.YZX,
					degs2.x,
					degs2.y + FastMath.TWO_PI,
					degs2.z,
				)
			}
			return (
				FloatMath.equalsWithEpsilon(degs1.x, degs2.x) &&
					FloatMath.equalsWithEpsilon(degs1.y, degs2.y) &&
					FloatMath.equalsWithEpsilon(degs1.z, degs2.z)
				)
		}
	}

	companion object {
		private val yaws = intArrayOf(0, 45, 90, 180, 270)
		private val pitches = intArrayOf(0, 15, 35, -15, -35)
		private val rolls = intArrayOf(0, 15, 35, -15, -35)
		private const val PRINT_TEST_RESULTS = false
		private var errors = 0
		private var successes = 0

		val anglesSet: Stream<AnglesSet>
			get() = yaws.asSequence()
				.zip(pitches.asSequence())
				.zip(rolls.asSequence()) { (yaw, pitch), roll ->
					AnglesSet(pitch, yaw, roll)
				}.asStream()

		private fun name(
			yaw: Int,
			pitch: Int,
			roll: Int,
			angles: EulerAngles,
			anglesAdj: EulerAngles,
			anglesDiff: EulerAngles,
		): String = """Rot: $yaw/$pitch/$roll.
				Angles: ${prettyNumber(angles.x * FastMath.RAD_TO_DEG, 1)}/${prettyNumber(anglesAdj.x * FastMath.RAD_TO_DEG, 1)},
				${prettyNumber(angles.y * FastMath.RAD_TO_DEG, 1)}/${prettyNumber(anglesAdj.y * FastMath.RAD_TO_DEG, 1)},
				${prettyNumber(angles.z * FastMath.RAD_TO_DEG, 1)}/${prettyNumber(anglesAdj.z * FastMath.RAD_TO_DEG, 1)}.
				Diff: ${prettyNumber(anglesDiff.x * FastMath.RAD_TO_DEG, 1)},
				${prettyNumber(anglesDiff.y * FastMath.RAD_TO_DEG, 1)},
				${prettyNumber(anglesDiff.z * FastMath.RAD_TO_DEG, 1)}
				""".replace('\n', ' ')

		fun q(pitch: Float, yaw: Float, roll: Float): Quaternion = EulerAngles(
			EulerOrder.YZX,
			pitch * FastMath.DEG_TO_RAD,
			yaw * FastMath.DEG_TO_RAD,
			roll * FastMath.DEG_TO_RAD,
		).toQuaternion()

		fun toDegs(q: Quaternion): String {
			val (_, x, y, z) = q.toEulerAngles(EulerOrder.YZX)
			return "${prettyNumber(x * FastMath.RAD_TO_DEG, 0)}, " +
				"${prettyNumber(y * FastMath.RAD_TO_DEG, 0)}, " +
				prettyNumber(z * FastMath.RAD_TO_DEG, 0)
		}
	}
}

data class AnglesSet(val pitch: Int, val yaw: Int, val roll: Int)
