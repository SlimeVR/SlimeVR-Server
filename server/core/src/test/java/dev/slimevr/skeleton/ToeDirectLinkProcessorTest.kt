package dev.slimevr.skeleton

import dev.slimevr.skeleton.inputprocessors.ToeDirectLinkInputProcessor
import io.github.axisangles.ktmath.Quaternion
import org.junit.jupiter.api.Test
import solarxr_protocol.datatypes.BodyPart
import kotlin.test.assertTrue

class ToeDirectLinkProcessorTest {
	@Test
	fun `test missing all toe trackers`() {
		val processor = ToeDirectLinkInputProcessor()
		val inputs = DEFAULT_SKELETON_STATE.boneInputs.mutateCopy { map ->
			map[BodyPart.LEFT_FOOT] = map.getValue(BodyPart.LEFT_FOOT).copy(
				rawRotation = Quaternion.fromRotationVector(10f, 40f, 15f),
				isRotationActive = true,
			)
			map[BodyPart.RIGHT_FOOT] = map.getValue(BodyPart.RIGHT_FOOT).copy(
				rawRotation = Quaternion.fromRotationVector(20f, 50f, 25f),
				isRotationActive = true,
			)
		}

		val state = SkeletonState(
			boneInputs = inputs,
			skeletonHeight = 1.7f,
			floorLevel = 0f,
			paused = false,
			pausedBoneInputs = inputs,
		)

		val newInputs = processor.process(state.boneInputs, state.skeletonHeight)

		val leftBigToeIsSameRotationAsLeftFoot =
			newInputs[BodyPart.LEFT_BIG_TOE]?.rawRotation == newInputs[BodyPart.LEFT_FOOT]?.rawRotation
		val leftIndexToeIsSameRotationAsLeftBigToe =
			newInputs[BodyPart.LEFT_INDEX_TOE]?.rawRotation == newInputs[BodyPart.LEFT_BIG_TOE]?.rawRotation
		val leftMiddleToeIsSameRotationAsLeftIndexToe =
			newInputs[BodyPart.LEFT_MIDDLE_TOE]?.rawRotation == newInputs[BodyPart.LEFT_INDEX_TOE]?.rawRotation
		val leftRingToeIsSameRotationAsLeftMiddleToe =
			newInputs[BodyPart.LEFT_RING_TOE]?.rawRotation == newInputs[BodyPart.LEFT_MIDDLE_TOE]?.rawRotation
		val leftLittleToeIsSameRotationAsLeftRingToe =
			newInputs[BodyPart.LEFT_LITTLE_TOE]?.rawRotation == newInputs[BodyPart.LEFT_RING_TOE]?.rawRotation

		val rightBigToeIsSameRotationAsRightFoot =
			newInputs[BodyPart.RIGHT_BIG_TOE]?.rawRotation == newInputs[BodyPart.RIGHT_FOOT]?.rawRotation
		val rightIndexToeIsSameRotationAsRightBigToe =
			newInputs[BodyPart.RIGHT_INDEX_TOE]?.rawRotation == newInputs[BodyPart.RIGHT_BIG_TOE]?.rawRotation
		val rightMiddleToeIsSameRotationAsRightIndexToe =
			newInputs[BodyPart.RIGHT_MIDDLE_TOE]?.rawRotation == newInputs[BodyPart.RIGHT_INDEX_TOE]?.rawRotation
		val rightRingToeIsSameRotationAsRightMiddleToe =
			newInputs[BodyPart.RIGHT_RING_TOE]?.rawRotation == newInputs[BodyPart.RIGHT_MIDDLE_TOE]?.rawRotation
		val rightLittleToeIsSameRotationAsRightRingToe =
			newInputs[BodyPart.RIGHT_LITTLE_TOE]?.rawRotation == newInputs[BodyPart.RIGHT_RING_TOE]?.rawRotation

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
		val processor = ToeDirectLinkInputProcessor()
		val inputs = DEFAULT_SKELETON_STATE.boneInputs.mutateCopy { map ->
			map[BodyPart.LEFT_FOOT] = map.getValue(BodyPart.LEFT_FOOT).copy(
				rawRotation = Quaternion.fromRotationVector(10f, 40f, 15f),
				isRotationActive = true,
			)
			map[BodyPart.LEFT_BIG_TOE] = map.getValue(BodyPart.LEFT_BIG_TOE).copy(
				rawRotation = Quaternion.fromRotationVector(11f, 41f, 16f),
				isRotationActive = true,
			)
			map[BodyPart.RIGHT_FOOT] = map.getValue(BodyPart.RIGHT_FOOT).copy(
				rawRotation = Quaternion.fromRotationVector(20f, 50f, 25f),
				isRotationActive = true,
			)
			map[BodyPart.RIGHT_BIG_TOE] = map.getValue(BodyPart.RIGHT_BIG_TOE).copy(
				rawRotation = Quaternion.fromRotationVector(21f, 51f, 26f),
				isRotationActive = true,
			)
		}

		val state = SkeletonState(
			boneInputs = inputs,
			skeletonHeight = 1.7f,
			floorLevel = 0f,
			paused = false,
			pausedBoneInputs = inputs,
		)

		val newInputs = processor.process(state.boneInputs, state.skeletonHeight)

		val leftIndexToeIsSameRotationAsLeftBigToe =
			newInputs[BodyPart.LEFT_INDEX_TOE]?.rawRotation == newInputs[BodyPart.LEFT_BIG_TOE]?.rawRotation
		val leftMiddleToeIsSameRotationAsLeftIndexToe =
			newInputs[BodyPart.LEFT_MIDDLE_TOE]?.rawRotation == newInputs[BodyPart.LEFT_INDEX_TOE]?.rawRotation
		val leftRingToeIsSameRotationAsLeftMiddleToe =
			newInputs[BodyPart.LEFT_RING_TOE]?.rawRotation == newInputs[BodyPart.LEFT_MIDDLE_TOE]?.rawRotation
		val leftLittleToeIsSameRotationAsLeftRingToe =
			newInputs[BodyPart.LEFT_LITTLE_TOE]?.rawRotation == newInputs[BodyPart.LEFT_RING_TOE]?.rawRotation

		val rightIndexToeIsSameRotationAsRightBigToe =
			newInputs[BodyPart.RIGHT_INDEX_TOE]?.rawRotation == newInputs[BodyPart.RIGHT_BIG_TOE]?.rawRotation
		val rightMiddleToeIsSameRotationAsRightIndexToe =
			newInputs[BodyPart.RIGHT_MIDDLE_TOE]?.rawRotation == newInputs[BodyPart.RIGHT_INDEX_TOE]?.rawRotation
		val rightRingToeIsSameRotationAsRightMiddleToe =
			newInputs[BodyPart.RIGHT_RING_TOE]?.rawRotation == newInputs[BodyPart.RIGHT_MIDDLE_TOE]?.rawRotation
		val rightLittleToeIsSameRotationAsRightRingToe =
			newInputs[BodyPart.RIGHT_LITTLE_TOE]?.rawRotation == newInputs[BodyPart.RIGHT_RING_TOE]?.rawRotation

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
		val processor = ToeDirectLinkInputProcessor()
		val inputs = DEFAULT_SKELETON_STATE.boneInputs.mutateCopy { map ->
			map[BodyPart.LEFT_FOOT] = map.getValue(BodyPart.LEFT_FOOT).copy(
				rawRotation = Quaternion.fromRotationVector(10f, 40f, 15f),
				isRotationActive = true,
			)
			map[BodyPart.LEFT_BIG_TOE] = map.getValue(BodyPart.LEFT_BIG_TOE).copy(
				rawRotation = Quaternion.fromRotationVector(11f, 41f, 16f),
				isRotationActive = true,
			)
			map[BodyPart.LEFT_INDEX_TOE] = map.getValue(BodyPart.LEFT_INDEX_TOE).copy(
				rawRotation = Quaternion.fromRotationVector(12f, 42f, 17f),
				isRotationActive = true,
			)

			map[BodyPart.RIGHT_FOOT] = map.getValue(BodyPart.RIGHT_FOOT).copy(
				rawRotation = Quaternion.fromRotationVector(20f, 50f, 25f),
				isRotationActive = true,
			)
			map[BodyPart.RIGHT_BIG_TOE] = map.getValue(BodyPart.RIGHT_BIG_TOE).copy(
				rawRotation = Quaternion.fromRotationVector(21f, 51f, 26f),
				isRotationActive = true,
			)
			map[BodyPart.RIGHT_INDEX_TOE] = map.getValue(BodyPart.RIGHT_INDEX_TOE).copy(
				rawRotation = Quaternion.fromRotationVector(22f, 52f, 27f),
				isRotationActive = true,
			)
		}

		val state = SkeletonState(
			boneInputs = inputs,
			skeletonHeight = 1.7f,
			floorLevel = 0f,
			paused = false,
			pausedBoneInputs = inputs,
		)

		val newInputs = processor.process(state.boneInputs, state.skeletonHeight)

		val leftMiddleToeIsSameRotationAsLeftIndexToe =
			newInputs[BodyPart.LEFT_MIDDLE_TOE]?.rawRotation == newInputs[BodyPart.LEFT_INDEX_TOE]?.rawRotation
		val leftRingToeIsSameRotationAsLeftMiddleToe =
			newInputs[BodyPart.LEFT_RING_TOE]?.rawRotation == newInputs[BodyPart.LEFT_MIDDLE_TOE]?.rawRotation
		val leftLittleToeIsSameRotationAsLeftRingToe =
			newInputs[BodyPart.LEFT_LITTLE_TOE]?.rawRotation == newInputs[BodyPart.LEFT_RING_TOE]?.rawRotation

		val rightMiddleToeIsSameRotationAsRightIndexToe =
			newInputs[BodyPart.RIGHT_MIDDLE_TOE]?.rawRotation == newInputs[BodyPart.RIGHT_INDEX_TOE]?.rawRotation
		val rightRingToeIsSameRotationAsRightMiddleToe =
			newInputs[BodyPart.RIGHT_RING_TOE]?.rawRotation == newInputs[BodyPart.RIGHT_MIDDLE_TOE]?.rawRotation
		val rightLittleToeIsSameRotationAsRightRingToe =
			newInputs[BodyPart.RIGHT_LITTLE_TOE]?.rawRotation == newInputs[BodyPart.RIGHT_RING_TOE]?.rawRotation

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
		val processor = ToeDirectLinkInputProcessor()
		val inputs = DEFAULT_SKELETON_STATE.boneInputs.mutateCopy { map ->
			map[BodyPart.LEFT_FOOT] = map.getValue(BodyPart.LEFT_FOOT).copy(
				rawRotation = Quaternion.fromRotationVector(10f, 40f, 15f),
				isRotationActive = true,
			)
			map[BodyPart.LEFT_BIG_TOE] = map.getValue(BodyPart.LEFT_BIG_TOE).copy(
				rawRotation = Quaternion.fromRotationVector(11f, 41f, 16f),
				isRotationActive = true,
			)
			map[BodyPart.LEFT_INDEX_TOE] = map.getValue(BodyPart.LEFT_INDEX_TOE).copy(
				rawRotation = Quaternion.fromRotationVector(12f, 42f, 17f),
				isRotationActive = true,
			)
			map[BodyPart.LEFT_MIDDLE_TOE] = map.getValue(BodyPart.LEFT_MIDDLE_TOE).copy(
				rawRotation = Quaternion.fromRotationVector(13f, 43f, 18f),
				isRotationActive = true,
			)

			map[BodyPart.RIGHT_FOOT] = map.getValue(BodyPart.RIGHT_FOOT).copy(
				rawRotation = Quaternion.fromRotationVector(20f, 50f, 25f),
				isRotationActive = true,
			)
			map[BodyPart.RIGHT_BIG_TOE] = map.getValue(BodyPart.RIGHT_BIG_TOE).copy(
				rawRotation = Quaternion.fromRotationVector(21f, 51f, 26f),
				isRotationActive = true,
			)
			map[BodyPart.RIGHT_INDEX_TOE] = map.getValue(BodyPart.RIGHT_INDEX_TOE).copy(
				rawRotation = Quaternion.fromRotationVector(22f, 52f, 27f),
				isRotationActive = true,
			)
			map[BodyPart.RIGHT_MIDDLE_TOE] = map.getValue(BodyPart.RIGHT_MIDDLE_TOE).copy(
				rawRotation = Quaternion.fromRotationVector(23f, 53f, 28f),
				isRotationActive = true,
			)
		}

		val state = SkeletonState(
			boneInputs = inputs,
			skeletonHeight = 1.7f,
			floorLevel = 0f,
			paused = false,
			pausedBoneInputs = inputs,
		)

		val newInputs = processor.process(state.boneInputs, state.skeletonHeight)

		val leftRingToeIsSameRotationAsLeftMiddleToe =
			newInputs[BodyPart.LEFT_RING_TOE]?.rawRotation == newInputs[BodyPart.LEFT_MIDDLE_TOE]?.rawRotation
		val leftLittleToeIsSameRotationAsLeftRingToe =
			newInputs[BodyPart.LEFT_LITTLE_TOE]?.rawRotation == newInputs[BodyPart.LEFT_RING_TOE]?.rawRotation

		val rightRingToeIsSameRotationAsRightMiddleToe =
			newInputs[BodyPart.RIGHT_RING_TOE]?.rawRotation == newInputs[BodyPart.RIGHT_MIDDLE_TOE]?.rawRotation
		val rightLittleToeIsSameRotationAsRightRingToe =
			newInputs[BodyPart.RIGHT_LITTLE_TOE]?.rawRotation == newInputs[BodyPart.RIGHT_RING_TOE]?.rawRotation

		assertTrue(leftRingToeIsSameRotationAsLeftMiddleToe && leftLittleToeIsSameRotationAsLeftRingToe)
		assertTrue(rightRingToeIsSameRotationAsRightMiddleToe && rightLittleToeIsSameRotationAsRightRingToe)
	}

	@Test
	fun `test missing little toe tracker`() {
		val processor = ToeDirectLinkInputProcessor()
		val inputs = DEFAULT_SKELETON_STATE.boneInputs.mutateCopy { map ->
			map[BodyPart.LEFT_FOOT] = map.getValue(BodyPart.LEFT_FOOT).copy(
				rawRotation = Quaternion.fromRotationVector(10f, 40f, 15f),
				isRotationActive = true,
			)
			map[BodyPart.LEFT_BIG_TOE] = map.getValue(BodyPart.LEFT_BIG_TOE).copy(
				rawRotation = Quaternion.fromRotationVector(11f, 41f, 16f),
				isRotationActive = true,
			)
			map[BodyPart.LEFT_INDEX_TOE] = map.getValue(BodyPart.LEFT_INDEX_TOE).copy(
				rawRotation = Quaternion.fromRotationVector(12f, 42f, 17f),
				isRotationActive = true,
			)
			map[BodyPart.LEFT_MIDDLE_TOE] = map.getValue(BodyPart.LEFT_MIDDLE_TOE).copy(
				rawRotation = Quaternion.fromRotationVector(15f, 45f, 19f),
				isRotationActive = true,
			)
			map[BodyPart.LEFT_RING_TOE] = map.getValue(BodyPart.LEFT_RING_TOE).copy(
				rawRotation = Quaternion.fromRotationVector(13f, 43f, 18f),
				isRotationActive = true,
			)

			map[BodyPart.RIGHT_FOOT] = map.getValue(BodyPart.RIGHT_FOOT).copy(
				rawRotation = Quaternion.fromRotationVector(20f, 50f, 25f),
				isRotationActive = true,
			)
			map[BodyPart.RIGHT_BIG_TOE] = map.getValue(BodyPart.RIGHT_BIG_TOE).copy(
				rawRotation = Quaternion.fromRotationVector(21f, 51f, 26f),
				isRotationActive = true,
			)
			map[BodyPart.RIGHT_INDEX_TOE] = map.getValue(BodyPart.RIGHT_INDEX_TOE).copy(
				rawRotation = Quaternion.fromRotationVector(22f, 52f, 27f),
				isRotationActive = true,
			)
			map[BodyPart.RIGHT_MIDDLE_TOE] = map.getValue(BodyPart.RIGHT_MIDDLE_TOE).copy(
				rawRotation = Quaternion.fromRotationVector(23f, 53f, 28f),
				isRotationActive = true,
			)
			map[BodyPart.RIGHT_RING_TOE] = map.getValue(BodyPart.RIGHT_RING_TOE).copy(
				rawRotation = Quaternion.fromRotationVector(24f, 54f, 29f),
				isRotationActive = true,
			)
		}

		val state = SkeletonState(
			boneInputs = inputs,
			skeletonHeight = 1.7f,
			floorLevel = 0f,
			paused = false,
			pausedBoneInputs = inputs,
		)

		val newInputs = processor.process(state.boneInputs, state.skeletonHeight)

		val leftLittleToeIsSameRotationAsLeftRingToe =
			newInputs[BodyPart.LEFT_LITTLE_TOE]?.rawRotation == newInputs[BodyPart.LEFT_RING_TOE]?.rawRotation
		val rightLittleToeIsSameRotationAsRightRingToe =
			newInputs[BodyPart.RIGHT_LITTLE_TOE]?.rawRotation == newInputs[BodyPart.RIGHT_RING_TOE]?.rawRotation

		assertTrue(leftLittleToeIsSameRotationAsLeftRingToe && rightLittleToeIsSameRotationAsRightRingToe)
	}
}
