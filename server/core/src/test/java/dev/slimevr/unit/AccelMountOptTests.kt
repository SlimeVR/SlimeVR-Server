package dev.slimevr.unit

import dev.slimevr.reset.accel.AccelFrame
import dev.slimevr.reset.accel.AccelMountOptimizer
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.math.PI

class AccelMountOptTests {

	@Test
	fun testOptimizeMounting() {
		val trueAttachment = Quaternion.IDENTITY
		val trueYawFix = Quaternion.IDENTITY

		// Tracker mounted with 45 degree yaw offset
		val mountingOffset = Quaternion.rotationAroundYAxis((PI / 4).toFloat())

		val frames = listOf(
			// Moving forward
			createTestFrame(
				hmdAccel = Vector3(0f, 0f, 2f),
				trackerRot = mountingOffset,
				trueAttachment = trueAttachment,
				trueYawFix = trueYawFix,
			),
			// Moving right
			createTestFrame(
				hmdAccel = Vector3(2f, 0f, 0f),
				trackerRot = mountingOffset,
				trueAttachment = trueAttachment,
				trueYawFix = trueYawFix,
			),
			// Diagonal
			createTestFrame(
				hmdAccel = Vector3(1.5f, 0f, 1.5f),
				trackerRot = mountingOffset,
				trueAttachment = trueAttachment,
				trueYawFix = trueYawFix,
			),
		)

		val optimizer = AccelMountOptimizer()
		val result = optimizer.optimize(frames)

		assertTrue(result.error < 0.01) {
			"Optimization error should be near zero, got: ${result.error}"
		}

		for (frame in frames) {
			val computedAccel = optimizer.computeWorldAccel(
				frame.trackerRot,
				frame.trackerAccel,
				result.attachmentFix,
				result.yawFix,
			)
			val diff = (computedAccel - frame.hmdAccel).len()
			assertTrue(diff < 0.1f) {
				"Expected: ${frame.hmdAccel}, Got: $computedAccel, Diff: $diff"
			}
		}
	}

	@Test
	fun testOptimizeWithPitch() {
		val trueAttachment = Quaternion.IDENTITY
		val trueYawFix = Quaternion.IDENTITY

		// Tracker tilted 30 degrees forward
		val pitchOffset = Quaternion.rotationAroundXAxis((PI / 6).toFloat())

		val frames = listOf(
			createTestFrame(
				hmdAccel = Vector3(0f, 0f, 3f),
				trackerRot = pitchOffset,
				trueAttachment = trueAttachment,
				trueYawFix = trueYawFix,
			),
			createTestFrame(
				hmdAccel = Vector3(2f, 0f, 0f),
				trackerRot = pitchOffset,
				trueAttachment = trueAttachment,
				trueYawFix = trueYawFix,
			),
			createTestFrame(
				hmdAccel = Vector3(0f, 1f, 2f),
				trackerRot = pitchOffset,
				trueAttachment = trueAttachment,
				trueYawFix = trueYawFix,
			),
		)

		val optimizer = AccelMountOptimizer()
		val result = optimizer.optimize(frames)

		assertTrue(result.error < 0.01) {
			"Optimization error should be near zero, got: ${result.error}"
		}
	}

	@Test
	fun testOptimizeWithCombinedOffset() {
		val trueAttachment = Quaternion.IDENTITY
		val trueYawFix = Quaternion.IDENTITY

		// 30 degree yaw + 20 degree pitch
		val yawOffset = Quaternion.rotationAroundYAxis((PI / 6).toFloat())
		val pitchOffset = Quaternion.rotationAroundXAxis((PI / 9).toFloat())
		val combinedOffset = yawOffset * pitchOffset

		val frames = listOf(
			createTestFrame(
				hmdAccel = Vector3(0f, 0f, 2.5f),
				trackerRot = combinedOffset,
				trueAttachment = trueAttachment,
				trueYawFix = trueYawFix,
			),
			createTestFrame(
				hmdAccel = Vector3(2f, 0f, 0f),
				trackerRot = combinedOffset,
				trueAttachment = trueAttachment,
				trueYawFix = trueYawFix,
			),
			createTestFrame(
				hmdAccel = Vector3(-1f, 0.5f, 1.5f),
				trackerRot = combinedOffset,
				trueAttachment = trueAttachment,
				trueYawFix = trueYawFix,
			),
		)

		val optimizer = AccelMountOptimizer()
		val result = optimizer.optimize(frames)

		assertTrue(result.error < 0.01) {
			"Optimization error should be near zero, got: ${result.error}"
		}

		for (frame in frames) {
			val computedAccel = optimizer.computeWorldAccel(
				frame.trackerRot,
				frame.trackerAccel,
				result.attachmentFix,
				result.yawFix,
			)
			val diff = (computedAccel - frame.hmdAccel).len()
			assertTrue(diff < 0.1f) {
				"Expected: ${frame.hmdAccel}, Got: $computedAccel"
			}
		}
	}

	// Creates a test frame by computing what local accel the tracker would see
	private fun createTestFrame(
		hmdAccel: Vector3,
		trackerRot: Quaternion,
		trueAttachment: Quaternion,
		trueYawFix: Quaternion,
	): AccelFrame {
		val trackerAccel = trackerRot.inv().sandwich(hmdAccel)

		return AccelFrame(
			trackerAccel = trackerAccel,
			trackerRot = trackerRot,
			hmdAccel = hmdAccel,
		)
	}
}
