package dev.slimevr.skeleton

import dev.slimevr.skeleton.inputprocessors.BoneDirectLinkInputProcessor
import io.github.axisangles.ktmath.Quaternion
import org.junit.jupiter.api.Test
import solarxr_protocol.datatypes.BodyPart
import kotlin.test.assertTrue

class ToeDirectLinkProcessorTest {
	@Test
	fun `test missing all toe trackers`() {
		val processor = BoneDirectLinkInputProcessor()
		val inputs = DEFAULT_SKELETON_STATE.boneInputs.mutateCopy { map ->
			map[BodyPart.LEFT_FOOT] = map.getValue(BodyPart.LEFT_FOOT).copy(
				rotation = Quaternion.fromRotationVector(10f, 40f, 15f),
				isRotationActive = true,
			)
			map[BodyPart.RIGHT_FOOT] = map.getValue(BodyPart.RIGHT_FOOT).copy(
				rotation = Quaternion.fromRotationVector(20f, 50f, 25f),
				isRotationActive = true,
			)
		}

		val state = SkeletonState(
			boneInputs = inputs,
			skeletonHeight = 1.7f,
			floorLevel = 0f,
			paused = false,
			pausedProcessedBoneInputs = inputs,
		)

		processor.process(inputs, state.skeletonHeight)

		val leftBigToeIsSameRotationAsLeftFoot =
			inputs[BodyPart.LEFT_BIG_TOE]?.rotation == inputs[BodyPart.LEFT_FOOT]?.rotation
		val leftIndexToeIsSameRotationAsLeftBigToe =
			inputs[BodyPart.LEFT_INDEX_TOE]?.rotation == inputs[BodyPart.LEFT_BIG_TOE]?.rotation
		val leftMiddleToeIsSameRotationAsLeftIndexToe =
			inputs[BodyPart.LEFT_MIDDLE_TOE]?.rotation == inputs[BodyPart.LEFT_INDEX_TOE]?.rotation
		val leftRingToeIsSameRotationAsLeftMiddleToe =
			inputs[BodyPart.LEFT_RING_TOE]?.rotation == inputs[BodyPart.LEFT_MIDDLE_TOE]?.rotation
		val leftLittleToeIsSameRotationAsLeftRingToe =
			inputs[BodyPart.LEFT_LITTLE_TOE]?.rotation == inputs[BodyPart.LEFT_RING_TOE]?.rotation

		val rightBigToeIsSameRotationAsRightFoot =
			inputs[BodyPart.RIGHT_BIG_TOE]?.rotation == inputs[BodyPart.RIGHT_FOOT]?.rotation
		val rightIndexToeIsSameRotationAsRightBigToe =
			inputs[BodyPart.RIGHT_INDEX_TOE]?.rotation == inputs[BodyPart.RIGHT_BIG_TOE]?.rotation
		val rightMiddleToeIsSameRotationAsRightIndexToe =
			inputs[BodyPart.RIGHT_MIDDLE_TOE]?.rotation == inputs[BodyPart.RIGHT_INDEX_TOE]?.rotation
		val rightRingToeIsSameRotationAsRightMiddleToe =
			inputs[BodyPart.RIGHT_RING_TOE]?.rotation == inputs[BodyPart.RIGHT_MIDDLE_TOE]?.rotation
		val rightLittleToeIsSameRotationAsRightRingToe =
			inputs[BodyPart.RIGHT_LITTLE_TOE]?.rotation == inputs[BodyPart.RIGHT_RING_TOE]?.rotation

		assertTrue(
			leftBigToeIsSameRotationAsLeftFoot &&
				leftIndexToeIsSameRotationAsLeftBigToe &&
				leftMiddleToeIsSameRotationAsLeftIndexToe &&
				leftRingToeIsSameRotationAsLeftMiddleToe &&
				leftLittleToeIsSameRotationAsLeftRingToe,
		)
		assertTrue(
			rightBigToeIsSameRotationAsRightFoot &&
				rightIndexToeIsSameRotationAsRightBigToe &&
				rightMiddleToeIsSameRotationAsRightIndexToe &&
				rightRingToeIsSameRotationAsRightMiddleToe &&
				rightLittleToeIsSameRotationAsRightRingToe,
		)
	}

	@Test
	fun `test missing toe trackers from index toe downwards`() {
		val processor = BoneDirectLinkInputProcessor()
		val inputs = DEFAULT_SKELETON_STATE.boneInputs.mutateCopy { map ->
			map[BodyPart.LEFT_FOOT] = map.getValue(BodyPart.LEFT_FOOT).copy(
				rotation = Quaternion.fromRotationVector(10f, 40f, 15f),
				isRotationActive = true,
			)
			map[BodyPart.LEFT_BIG_TOE] = map.getValue(BodyPart.LEFT_BIG_TOE).copy(
				rotation = Quaternion.fromRotationVector(11f, 41f, 16f),
				isRotationActive = true,
			)
			map[BodyPart.RIGHT_FOOT] = map.getValue(BodyPart.RIGHT_FOOT).copy(
				rotation = Quaternion.fromRotationVector(20f, 50f, 25f),
				isRotationActive = true,
			)
			map[BodyPart.RIGHT_BIG_TOE] = map.getValue(BodyPart.RIGHT_BIG_TOE).copy(
				rotation = Quaternion.fromRotationVector(21f, 51f, 26f),
				isRotationActive = true,
			)
		}

		val state = SkeletonState(
			boneInputs = inputs,
			skeletonHeight = 1.7f,
			floorLevel = 0f,
			paused = false,
			pausedProcessedBoneInputs = inputs,
		)

		processor.process(inputs, state.skeletonHeight)

		val leftIndexToeIsSameRotationAsLeftBigToe =
			inputs[BodyPart.LEFT_INDEX_TOE]?.rotation == inputs[BodyPart.LEFT_BIG_TOE]?.rotation
		val leftMiddleToeIsSameRotationAsLeftIndexToe =
			inputs[BodyPart.LEFT_MIDDLE_TOE]?.rotation == inputs[BodyPart.LEFT_INDEX_TOE]?.rotation
		val leftRingToeIsSameRotationAsLeftMiddleToe =
			inputs[BodyPart.LEFT_RING_TOE]?.rotation == inputs[BodyPart.LEFT_MIDDLE_TOE]?.rotation
		val leftLittleToeIsSameRotationAsLeftRingToe =
			inputs[BodyPart.LEFT_LITTLE_TOE]?.rotation == inputs[BodyPart.LEFT_RING_TOE]?.rotation

		val rightIndexToeIsSameRotationAsRightBigToe =
			inputs[BodyPart.RIGHT_INDEX_TOE]?.rotation == inputs[BodyPart.RIGHT_BIG_TOE]?.rotation
		val rightMiddleToeIsSameRotationAsRightIndexToe =
			inputs[BodyPart.RIGHT_MIDDLE_TOE]?.rotation == inputs[BodyPart.RIGHT_INDEX_TOE]?.rotation
		val rightRingToeIsSameRotationAsRightMiddleToe =
			inputs[BodyPart.RIGHT_RING_TOE]?.rotation == inputs[BodyPart.RIGHT_MIDDLE_TOE]?.rotation
		val rightLittleToeIsSameRotationAsRightRingToe =
			inputs[BodyPart.RIGHT_LITTLE_TOE]?.rotation == inputs[BodyPart.RIGHT_RING_TOE]?.rotation

		assertTrue(
			leftIndexToeIsSameRotationAsLeftBigToe &&
				leftMiddleToeIsSameRotationAsLeftIndexToe &&
				leftRingToeIsSameRotationAsLeftMiddleToe &&
				leftLittleToeIsSameRotationAsLeftRingToe,
		)
		assertTrue(
			rightIndexToeIsSameRotationAsRightBigToe &&
				rightMiddleToeIsSameRotationAsRightIndexToe &&
				rightRingToeIsSameRotationAsRightMiddleToe &&
				rightLittleToeIsSameRotationAsRightRingToe,
		)
	}

	@Test
	fun `test missing toe trackers from middle toe downwards`() {
		val processor = BoneDirectLinkInputProcessor()
		val inputs = DEFAULT_SKELETON_STATE.boneInputs.mutateCopy { map ->
			map[BodyPart.LEFT_FOOT] = map.getValue(BodyPart.LEFT_FOOT).copy(
				rotation = Quaternion.fromRotationVector(10f, 40f, 15f),
				isRotationActive = true,
			)
			map[BodyPart.LEFT_BIG_TOE] = map.getValue(BodyPart.LEFT_BIG_TOE).copy(
				rotation = Quaternion.fromRotationVector(11f, 41f, 16f),
				isRotationActive = true,
			)
			map[BodyPart.LEFT_INDEX_TOE] = map.getValue(BodyPart.LEFT_INDEX_TOE).copy(
				rotation = Quaternion.fromRotationVector(12f, 42f, 17f),
				isRotationActive = true,
			)

			map[BodyPart.RIGHT_FOOT] = map.getValue(BodyPart.RIGHT_FOOT).copy(
				rotation = Quaternion.fromRotationVector(20f, 50f, 25f),
				isRotationActive = true,
			)
			map[BodyPart.RIGHT_BIG_TOE] = map.getValue(BodyPart.RIGHT_BIG_TOE).copy(
				rotation = Quaternion.fromRotationVector(21f, 51f, 26f),
				isRotationActive = true,
			)
			map[BodyPart.RIGHT_INDEX_TOE] = map.getValue(BodyPart.RIGHT_INDEX_TOE).copy(
				rotation = Quaternion.fromRotationVector(22f, 52f, 27f),
				isRotationActive = true,
			)
		}

		val state = SkeletonState(
			boneInputs = inputs,
			skeletonHeight = 1.7f,
			floorLevel = 0f,
			paused = false,
			pausedProcessedBoneInputs = inputs,
		)

		processor.process(inputs, state.skeletonHeight)

		val leftMiddleToeIsSameRotationAsLeftIndexToe =
			inputs[BodyPart.LEFT_MIDDLE_TOE]?.rotation == inputs[BodyPart.LEFT_INDEX_TOE]?.rotation
		val leftRingToeIsSameRotationAsLeftMiddleToe =
			inputs[BodyPart.LEFT_RING_TOE]?.rotation == inputs[BodyPart.LEFT_MIDDLE_TOE]?.rotation
		val leftLittleToeIsSameRotationAsLeftRingToe =
			inputs[BodyPart.LEFT_LITTLE_TOE]?.rotation == inputs[BodyPart.LEFT_RING_TOE]?.rotation

		val rightMiddleToeIsSameRotationAsRightIndexToe =
			inputs[BodyPart.RIGHT_MIDDLE_TOE]?.rotation == inputs[BodyPart.RIGHT_INDEX_TOE]?.rotation
		val rightRingToeIsSameRotationAsRightMiddleToe =
			inputs[BodyPart.RIGHT_RING_TOE]?.rotation == inputs[BodyPart.RIGHT_MIDDLE_TOE]?.rotation
		val rightLittleToeIsSameRotationAsRightRingToe =
			inputs[BodyPart.RIGHT_LITTLE_TOE]?.rotation == inputs[BodyPart.RIGHT_RING_TOE]?.rotation

		assertTrue(
			leftMiddleToeIsSameRotationAsLeftIndexToe &&
				leftRingToeIsSameRotationAsLeftMiddleToe &&
				leftLittleToeIsSameRotationAsLeftRingToe,
		)
		assertTrue(
			rightMiddleToeIsSameRotationAsRightIndexToe &&
				rightRingToeIsSameRotationAsRightMiddleToe &&
				rightLittleToeIsSameRotationAsRightRingToe,
		)
	}

	@Test
	fun `test missing toe trackers from ring toe downwards`() {
		val processor = BoneDirectLinkInputProcessor()
		val inputs = DEFAULT_SKELETON_STATE.boneInputs.mutateCopy { map ->
			map[BodyPart.LEFT_FOOT] = map.getValue(BodyPart.LEFT_FOOT).copy(
				rotation = Quaternion.fromRotationVector(10f, 40f, 15f),
				isRotationActive = true,
			)
			map[BodyPart.LEFT_BIG_TOE] = map.getValue(BodyPart.LEFT_BIG_TOE).copy(
				rotation = Quaternion.fromRotationVector(11f, 41f, 16f),
				isRotationActive = true,
			)
			map[BodyPart.LEFT_INDEX_TOE] = map.getValue(BodyPart.LEFT_INDEX_TOE).copy(
				rotation = Quaternion.fromRotationVector(12f, 42f, 17f),
				isRotationActive = true,
			)
			map[BodyPart.LEFT_MIDDLE_TOE] = map.getValue(BodyPart.LEFT_MIDDLE_TOE).copy(
				rotation = Quaternion.fromRotationVector(13f, 43f, 18f),
				isRotationActive = true,
			)

			map[BodyPart.RIGHT_FOOT] = map.getValue(BodyPart.RIGHT_FOOT).copy(
				rotation = Quaternion.fromRotationVector(20f, 50f, 25f),
				isRotationActive = true,
			)
			map[BodyPart.RIGHT_BIG_TOE] = map.getValue(BodyPart.RIGHT_BIG_TOE).copy(
				rotation = Quaternion.fromRotationVector(21f, 51f, 26f),
				isRotationActive = true,
			)
			map[BodyPart.RIGHT_INDEX_TOE] = map.getValue(BodyPart.RIGHT_INDEX_TOE).copy(
				rotation = Quaternion.fromRotationVector(22f, 52f, 27f),
				isRotationActive = true,
			)
			map[BodyPart.RIGHT_MIDDLE_TOE] = map.getValue(BodyPart.RIGHT_MIDDLE_TOE).copy(
				rotation = Quaternion.fromRotationVector(23f, 53f, 28f),
				isRotationActive = true,
			)
		}

		val state = SkeletonState(
			boneInputs = inputs,
			skeletonHeight = 1.7f,
			floorLevel = 0f,
			paused = false,
			pausedProcessedBoneInputs = inputs,
		)

		processor.process(inputs, state.skeletonHeight)

		val leftRingToeIsSameRotationAsLeftMiddleToe =
			inputs[BodyPart.LEFT_RING_TOE]?.rotation == inputs[BodyPart.LEFT_MIDDLE_TOE]?.rotation
		val leftLittleToeIsSameRotationAsLeftRingToe =
			inputs[BodyPart.LEFT_LITTLE_TOE]?.rotation == inputs[BodyPart.LEFT_RING_TOE]?.rotation

		val rightRingToeIsSameRotationAsRightMiddleToe =
			inputs[BodyPart.RIGHT_RING_TOE]?.rotation == inputs[BodyPart.RIGHT_MIDDLE_TOE]?.rotation
		val rightLittleToeIsSameRotationAsRightRingToe =
			inputs[BodyPart.RIGHT_LITTLE_TOE]?.rotation == inputs[BodyPart.RIGHT_RING_TOE]?.rotation

		assertTrue(leftRingToeIsSameRotationAsLeftMiddleToe && leftLittleToeIsSameRotationAsLeftRingToe)
		assertTrue(rightRingToeIsSameRotationAsRightMiddleToe && rightLittleToeIsSameRotationAsRightRingToe)
	}

	@Test
	fun `test missing little toe tracker`() {
		val processor = BoneDirectLinkInputProcessor()
		val inputs = DEFAULT_SKELETON_STATE.boneInputs.mutateCopy { map ->
			map[BodyPart.LEFT_FOOT] = map.getValue(BodyPart.LEFT_FOOT).copy(
				rotation = Quaternion.fromRotationVector(10f, 40f, 15f),
				isRotationActive = true,
			)
			map[BodyPart.LEFT_BIG_TOE] = map.getValue(BodyPart.LEFT_BIG_TOE).copy(
				rotation = Quaternion.fromRotationVector(11f, 41f, 16f),
				isRotationActive = true,
			)
			map[BodyPart.LEFT_INDEX_TOE] = map.getValue(BodyPart.LEFT_INDEX_TOE).copy(
				rotation = Quaternion.fromRotationVector(12f, 42f, 17f),
				isRotationActive = true,
			)
			map[BodyPart.LEFT_MIDDLE_TOE] = map.getValue(BodyPart.LEFT_MIDDLE_TOE).copy(
				rotation = Quaternion.fromRotationVector(15f, 45f, 19f),
				isRotationActive = true,
			)
			map[BodyPart.LEFT_RING_TOE] = map.getValue(BodyPart.LEFT_RING_TOE).copy(
				rotation = Quaternion.fromRotationVector(13f, 43f, 18f),
				isRotationActive = true,
			)

			map[BodyPart.RIGHT_FOOT] = map.getValue(BodyPart.RIGHT_FOOT).copy(
				rotation = Quaternion.fromRotationVector(20f, 50f, 25f),
				isRotationActive = true,
			)
			map[BodyPart.RIGHT_BIG_TOE] = map.getValue(BodyPart.RIGHT_BIG_TOE).copy(
				rotation = Quaternion.fromRotationVector(21f, 51f, 26f),
				isRotationActive = true,
			)
			map[BodyPart.RIGHT_INDEX_TOE] = map.getValue(BodyPart.RIGHT_INDEX_TOE).copy(
				rotation = Quaternion.fromRotationVector(22f, 52f, 27f),
				isRotationActive = true,
			)
			map[BodyPart.RIGHT_MIDDLE_TOE] = map.getValue(BodyPart.RIGHT_MIDDLE_TOE).copy(
				rotation = Quaternion.fromRotationVector(23f, 53f, 28f),
				isRotationActive = true,
			)
			map[BodyPart.RIGHT_RING_TOE] = map.getValue(BodyPart.RIGHT_RING_TOE).copy(
				rotation = Quaternion.fromRotationVector(24f, 54f, 29f),
				isRotationActive = true,
			)
		}

		val state = SkeletonState(
			boneInputs = inputs,
			skeletonHeight = 1.7f,
			floorLevel = 0f,
			paused = false,
			pausedProcessedBoneInputs = inputs,
		)

		processor.process(inputs, state.skeletonHeight)

		val leftLittleToeIsSameRotationAsLeftRingToe =
			inputs[BodyPart.LEFT_LITTLE_TOE]?.rotation == inputs[BodyPart.LEFT_RING_TOE]?.rotation
		val rightLittleToeIsSameRotationAsRightRingToe =
			inputs[BodyPart.RIGHT_LITTLE_TOE]?.rotation == inputs[BodyPart.RIGHT_RING_TOE]?.rotation

		assertTrue(leftLittleToeIsSameRotationAsLeftRingToe && rightLittleToeIsSameRotationAsRightRingToe)
	}
}
