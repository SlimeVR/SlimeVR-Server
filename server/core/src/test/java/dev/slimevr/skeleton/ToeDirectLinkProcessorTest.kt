package dev.slimevr.skeleton

import dev.slimevr.skeleton.processors.ToeDirectLinkProcessor
import io.github.axisangles.ktmath.Quaternion
import org.junit.jupiter.api.Test
import solarxr_protocol.datatypes.BodyPart
import kotlin.test.assertTrue

class ToeDirectLinkProcessorTest {
	@Test
	fun `test missing all toe trackers`() {
		val processor = ToeDirectLinkProcessor()
		val inputs = DEFAULT_SKELETON_STATE.boneInputs.mutate { map ->
			map[BodyPart.LEFT_FOOT] = map.getValue(BodyPart.LEFT_FOOT).copy(
				rawRotation = Quaternion.Companion.fromRotationVector(10f, 40f, 15f),
				isActive = true)
			map[BodyPart.RIGHT_FOOT] = map.getValue(BodyPart.RIGHT_FOOT).copy(
				rawRotation = Quaternion.Companion.fromRotationVector(20f, 50f, 25f),
				isActive = true)
		}

		val state = SkeletonState(
			boneInputs = inputs,
			skeletonHeight = 1.7f,
			paused = false
		)

		val newInputs = processor.process(state)

		val leftBigToeIsSameRotationAsLeftFoot =
			newInputs.boneInputs[BodyPart.LEFT_BIG_TOE]?.rawRotation == newInputs.boneInputs[BodyPart.LEFT_FOOT]?.rawRotation
		val leftIndexToeIsSameRotationAsLeftBigToe =
			newInputs.boneInputs[BodyPart.LEFT_INDEX_TOE]?.rawRotation == newInputs.boneInputs[BodyPart.LEFT_BIG_TOE]?.rawRotation
		val leftMiddleToeIsSameRotationAsLeftIndexToe =
			newInputs.boneInputs[BodyPart.LEFT_MIDDLE_TOE]?.rawRotation == newInputs.boneInputs[BodyPart.LEFT_INDEX_TOE]?.rawRotation
		val leftRingToeIsSameRotationAsLeftMiddleToe =
			newInputs.boneInputs[BodyPart.LEFT_RING_TOE]?.rawRotation == newInputs.boneInputs[BodyPart.LEFT_MIDDLE_TOE]?.rawRotation
		val leftPinkyToeIsSameRotationAsLeftRingToe =
			newInputs.boneInputs[BodyPart.LEFT_PINKY_TOE]?.rawRotation == newInputs.boneInputs[BodyPart.LEFT_RING_TOE]?.rawRotation

		val rightBigToeIsSameRotationAsRightFoot =
			newInputs.boneInputs[BodyPart.RIGHT_BIG_TOE]?.rawRotation == newInputs.boneInputs[BodyPart.RIGHT_FOOT]?.rawRotation
		val rightIndexToeIsSameRotationAsRightBigToe =
			newInputs.boneInputs[BodyPart.RIGHT_INDEX_TOE]?.rawRotation == newInputs.boneInputs[BodyPart.RIGHT_BIG_TOE]?.rawRotation
		val rightMiddleToeIsSameRotationAsRightIndexToe =
			newInputs.boneInputs[BodyPart.RIGHT_MIDDLE_TOE]?.rawRotation == newInputs.boneInputs[BodyPart.RIGHT_INDEX_TOE]?.rawRotation
		val rightRingToeIsSameRotationAsRightMiddleToe =
			newInputs.boneInputs[BodyPart.RIGHT_RING_TOE]?.rawRotation == newInputs.boneInputs[BodyPart.RIGHT_MIDDLE_TOE]?.rawRotation
		val rightPinkyToeIsSameRotationAsRightRingToe =
			newInputs.boneInputs[BodyPart.RIGHT_PINKY_TOE]?.rawRotation == newInputs.boneInputs[BodyPart.RIGHT_RING_TOE]?.rawRotation

		assertTrue(leftBigToeIsSameRotationAsLeftFoot
			&& leftIndexToeIsSameRotationAsLeftBigToe
			&& leftMiddleToeIsSameRotationAsLeftIndexToe
			&& leftRingToeIsSameRotationAsLeftMiddleToe
			&& leftPinkyToeIsSameRotationAsLeftRingToe)
		assertTrue(rightBigToeIsSameRotationAsRightFoot
			&& rightIndexToeIsSameRotationAsRightBigToe
			&& rightMiddleToeIsSameRotationAsRightIndexToe
			&& rightRingToeIsSameRotationAsRightMiddleToe
			&& rightPinkyToeIsSameRotationAsRightRingToe)
	}

	@Test
	fun `test missing toe trackers from index toe downwards`() {
		val processor = ToeDirectLinkProcessor()
		val inputs = DEFAULT_SKELETON_STATE.boneInputs.mutate { map ->
			map[BodyPart.LEFT_FOOT] = map.getValue(BodyPart.LEFT_FOOT).copy(
				rawRotation = Quaternion.Companion.fromRotationVector(10f, 40f, 15f),
				isActive = true)
			map[BodyPart.LEFT_BIG_TOE] = map.getValue(BodyPart.LEFT_BIG_TOE).copy(
				rawRotation = Quaternion.Companion.fromRotationVector(11f, 41f, 16f),
				isActive = true)
			map[BodyPart.RIGHT_FOOT] = map.getValue(BodyPart.RIGHT_FOOT).copy(
				rawRotation = Quaternion.Companion.fromRotationVector(20f, 50f, 25f),
				isActive = true)
			map[BodyPart.RIGHT_BIG_TOE] = map.getValue(BodyPart.RIGHT_BIG_TOE).copy(
				rawRotation = Quaternion.Companion.fromRotationVector(21f, 51f, 26f),
				isActive = true)
		}

		val state = SkeletonState(
			boneInputs = inputs,
			skeletonHeight = 1.7f,
			paused = false
		)

		val newInputs = processor.process(state)

		val leftIndexToeIsSameRotationAsLeftBigToe =
			newInputs.boneInputs[BodyPart.LEFT_INDEX_TOE]?.rawRotation == newInputs.boneInputs[BodyPart.LEFT_BIG_TOE]?.rawRotation
		val leftMiddleToeIsSameRotationAsLeftIndexToe =
			newInputs.boneInputs[BodyPart.LEFT_MIDDLE_TOE]?.rawRotation == newInputs.boneInputs[BodyPart.LEFT_INDEX_TOE]?.rawRotation
		val leftRingToeIsSameRotationAsLeftMiddleToe =
			newInputs.boneInputs[BodyPart.LEFT_RING_TOE]?.rawRotation == newInputs.boneInputs[BodyPart.LEFT_MIDDLE_TOE]?.rawRotation
		val leftPinkyToeIsSameRotationAsLeftRingToe =
			newInputs.boneInputs[BodyPart.LEFT_PINKY_TOE]?.rawRotation == newInputs.boneInputs[BodyPart.LEFT_RING_TOE]?.rawRotation

		val rightIndexToeIsSameRotationAsRightBigToe =
			newInputs.boneInputs[BodyPart.RIGHT_INDEX_TOE]?.rawRotation == newInputs.boneInputs[BodyPart.RIGHT_BIG_TOE]?.rawRotation
		val rightMiddleToeIsSameRotationAsRightIndexToe =
			newInputs.boneInputs[BodyPart.RIGHT_MIDDLE_TOE]?.rawRotation == newInputs.boneInputs[BodyPart.RIGHT_INDEX_TOE]?.rawRotation
		val rightRingToeIsSameRotationAsRightMiddleToe =
			newInputs.boneInputs[BodyPart.RIGHT_RING_TOE]?.rawRotation == newInputs.boneInputs[BodyPart.RIGHT_MIDDLE_TOE]?.rawRotation
		val rightPinkyToeIsSameRotationAsRightRingToe =
			newInputs.boneInputs[BodyPart.RIGHT_PINKY_TOE]?.rawRotation == newInputs.boneInputs[BodyPart.RIGHT_RING_TOE]?.rawRotation

		assertTrue(leftIndexToeIsSameRotationAsLeftBigToe
			&& leftMiddleToeIsSameRotationAsLeftIndexToe
			&& leftRingToeIsSameRotationAsLeftMiddleToe
			&& leftPinkyToeIsSameRotationAsLeftRingToe)
		assertTrue(rightIndexToeIsSameRotationAsRightBigToe
			&& rightMiddleToeIsSameRotationAsRightIndexToe
			&& rightRingToeIsSameRotationAsRightMiddleToe
			&& rightPinkyToeIsSameRotationAsRightRingToe)
	}

	@Test
	fun `test missing toe trackers from middle toe downwards`() {
		val processor = ToeDirectLinkProcessor()
		val inputs = DEFAULT_SKELETON_STATE.boneInputs.mutate { map ->
			map[BodyPart.LEFT_FOOT] = map.getValue(BodyPart.LEFT_FOOT).copy(
				rawRotation = Quaternion.Companion.fromRotationVector(10f, 40f, 15f),
				isActive = true)
			map[BodyPart.LEFT_BIG_TOE] = map.getValue(BodyPart.LEFT_BIG_TOE).copy(
				rawRotation = Quaternion.Companion.fromRotationVector(11f, 41f, 16f),
				isActive = true)
			map[BodyPart.LEFT_INDEX_TOE] = map.getValue(BodyPart.LEFT_INDEX_TOE).copy(
				rawRotation = Quaternion.Companion.fromRotationVector(12f, 42f, 17f),
				isActive = true)

			map[BodyPart.RIGHT_FOOT] = map.getValue(BodyPart.RIGHT_FOOT).copy(
				rawRotation = Quaternion.Companion.fromRotationVector(20f, 50f, 25f),
				isActive = true)
			map[BodyPart.RIGHT_BIG_TOE] = map.getValue(BodyPart.RIGHT_BIG_TOE).copy(
				rawRotation = Quaternion.Companion.fromRotationVector(21f, 51f, 26f),
				isActive = true)
			map[BodyPart.RIGHT_INDEX_TOE] = map.getValue(BodyPart.RIGHT_INDEX_TOE).copy(
				rawRotation = Quaternion.Companion.fromRotationVector(22f, 52f, 27f),
				isActive = true)
		}

		val state = SkeletonState(
			boneInputs = inputs,
			skeletonHeight = 1.7f,
			paused = false
		)

		val newInputs = processor.process(state)

		val leftMiddleToeIsSameRotationAsLeftIndexToe =
			newInputs.boneInputs[BodyPart.LEFT_MIDDLE_TOE]?.rawRotation == newInputs.boneInputs[BodyPart.LEFT_INDEX_TOE]?.rawRotation
		val leftRingToeIsSameRotationAsLeftMiddleToe =
			newInputs.boneInputs[BodyPart.LEFT_RING_TOE]?.rawRotation == newInputs.boneInputs[BodyPart.LEFT_MIDDLE_TOE]?.rawRotation
		val leftPinkyToeIsSameRotationAsLeftRingToe =
			newInputs.boneInputs[BodyPart.LEFT_PINKY_TOE]?.rawRotation == newInputs.boneInputs[BodyPart.LEFT_RING_TOE]?.rawRotation

		val rightMiddleToeIsSameRotationAsRightIndexToe =
			newInputs.boneInputs[BodyPart.RIGHT_MIDDLE_TOE]?.rawRotation == newInputs.boneInputs[BodyPart.RIGHT_INDEX_TOE]?.rawRotation
		val rightRingToeIsSameRotationAsRightMiddleToe =
			newInputs.boneInputs[BodyPart.RIGHT_RING_TOE]?.rawRotation == newInputs.boneInputs[BodyPart.RIGHT_MIDDLE_TOE]?.rawRotation
		val rightPinkyToeIsSameRotationAsRightRingToe =
			newInputs.boneInputs[BodyPart.RIGHT_PINKY_TOE]?.rawRotation == newInputs.boneInputs[BodyPart.RIGHT_RING_TOE]?.rawRotation

		assertTrue(leftMiddleToeIsSameRotationAsLeftIndexToe
			&& leftRingToeIsSameRotationAsLeftMiddleToe
			&& leftPinkyToeIsSameRotationAsLeftRingToe)
		assertTrue(rightMiddleToeIsSameRotationAsRightIndexToe
			&& rightRingToeIsSameRotationAsRightMiddleToe
			&& rightPinkyToeIsSameRotationAsRightRingToe)
	}

	@Test
	fun `test missing toe trackers from ring toe downwards`() {
		val processor = ToeDirectLinkProcessor()
		val inputs = DEFAULT_SKELETON_STATE.boneInputs.mutate { map ->
			map[BodyPart.LEFT_FOOT] = map.getValue(BodyPart.LEFT_FOOT).copy(
				rawRotation = Quaternion.Companion.fromRotationVector(10f, 40f, 15f),
				isActive = true)
			map[BodyPart.LEFT_BIG_TOE] = map.getValue(BodyPart.LEFT_BIG_TOE).copy(
				rawRotation = Quaternion.Companion.fromRotationVector(11f, 41f, 16f),
				isActive = true)
			map[BodyPart.LEFT_INDEX_TOE] = map.getValue(BodyPart.LEFT_INDEX_TOE).copy(
				rawRotation = Quaternion.Companion.fromRotationVector(12f, 42f, 17f),
				isActive = true)
			map[BodyPart.LEFT_MIDDLE_TOE] = map.getValue(BodyPart.LEFT_MIDDLE_TOE).copy(
				rawRotation = Quaternion.Companion.fromRotationVector(13f, 43f, 18f),
				isActive = true)

			map[BodyPart.RIGHT_FOOT] = map.getValue(BodyPart.RIGHT_FOOT).copy(
				rawRotation = Quaternion.Companion.fromRotationVector(20f, 50f, 25f),
				isActive = true)
			map[BodyPart.RIGHT_BIG_TOE] = map.getValue(BodyPart.RIGHT_BIG_TOE).copy(
				rawRotation = Quaternion.Companion.fromRotationVector(21f, 51f, 26f),
				isActive = true)
			map[BodyPart.RIGHT_INDEX_TOE] = map.getValue(BodyPart.RIGHT_INDEX_TOE).copy(
				rawRotation = Quaternion.Companion.fromRotationVector(22f, 52f, 27f),
				isActive = true)
			map[BodyPart.RIGHT_MIDDLE_TOE] = map.getValue(BodyPart.RIGHT_MIDDLE_TOE).copy(
				rawRotation = Quaternion.Companion.fromRotationVector(23f, 53f, 28f),
				isActive = true)
		}

		val state = SkeletonState(
			boneInputs = inputs,
			skeletonHeight = 1.7f,
			paused = false
		)

		val newInputs = processor.process(state)

		val leftRingToeIsSameRotationAsLeftMiddleToe =
			newInputs.boneInputs[BodyPart.LEFT_RING_TOE]?.rawRotation == newInputs.boneInputs[BodyPart.LEFT_MIDDLE_TOE]?.rawRotation
		val leftPinkyToeIsSameRotationAsLeftRingToe =
			newInputs.boneInputs[BodyPart.LEFT_PINKY_TOE]?.rawRotation == newInputs.boneInputs[BodyPart.LEFT_RING_TOE]?.rawRotation

		val rightRingToeIsSameRotationAsRightMiddleToe =
			newInputs.boneInputs[BodyPart.RIGHT_RING_TOE]?.rawRotation == newInputs.boneInputs[BodyPart.RIGHT_MIDDLE_TOE]?.rawRotation
		val rightPinkyToeIsSameRotationAsRightRingToe =
			newInputs.boneInputs[BodyPart.RIGHT_PINKY_TOE]?.rawRotation == newInputs.boneInputs[BodyPart.RIGHT_RING_TOE]?.rawRotation

		assertTrue(leftRingToeIsSameRotationAsLeftMiddleToe && leftPinkyToeIsSameRotationAsLeftRingToe)
		assertTrue(rightRingToeIsSameRotationAsRightMiddleToe && rightPinkyToeIsSameRotationAsRightRingToe)
	}

	@Test
	fun `test impute missing pinky toe tracker`() {
		val processor = ToeDirectLinkProcessor()
		val inputs = DEFAULT_SKELETON_STATE.boneInputs.mutate { map ->
			map[BodyPart.LEFT_FOOT] = map.getValue(BodyPart.LEFT_FOOT).copy(
				rawRotation = Quaternion.Companion.fromRotationVector(10f, 40f, 15f),
				isActive = true)
			map[BodyPart.LEFT_BIG_TOE] = map.getValue(BodyPart.LEFT_BIG_TOE).copy(
				rawRotation = Quaternion.Companion.fromRotationVector(11f, 41f, 16f),
				isActive = true)
			map[BodyPart.LEFT_INDEX_TOE] = map.getValue(BodyPart.LEFT_INDEX_TOE).copy(
				rawRotation = Quaternion.Companion.fromRotationVector(12f, 42f, 17f),
				isActive = true)
			map[BodyPart.LEFT_MIDDLE_TOE] = map.getValue(BodyPart.LEFT_MIDDLE_TOE).copy(
				rawRotation = Quaternion.Companion.fromRotationVector(15f, 45f, 19f),
				isActive = true)
			map[BodyPart.LEFT_RING_TOE] = map.getValue(BodyPart.LEFT_RING_TOE).copy(
				rawRotation = Quaternion.Companion.fromRotationVector(13f, 43f, 18f),
				isActive = true)

			map[BodyPart.RIGHT_FOOT] = map.getValue(BodyPart.RIGHT_FOOT).copy(
				rawRotation = Quaternion.Companion.fromRotationVector(20f, 50f, 25f),
				isActive = true)
			map[BodyPart.RIGHT_BIG_TOE] = map.getValue(BodyPart.RIGHT_BIG_TOE).copy(
				rawRotation = Quaternion.Companion.fromRotationVector(21f, 51f, 26f),
				isActive = true)
			map[BodyPart.RIGHT_INDEX_TOE] = map.getValue(BodyPart.RIGHT_INDEX_TOE).copy(
				rawRotation = Quaternion.Companion.fromRotationVector(22f, 52f, 27f),
				isActive = true)
			map[BodyPart.RIGHT_MIDDLE_TOE] = map.getValue(BodyPart.RIGHT_MIDDLE_TOE).copy(
				rawRotation = Quaternion.Companion.fromRotationVector(23f, 53f, 28f),
				isActive = true)
			map[BodyPart.RIGHT_RING_TOE] = map.getValue(BodyPart.RIGHT_RING_TOE).copy(
				rawRotation = Quaternion.Companion.fromRotationVector(24f, 54f, 29f),
				isActive = true)
		}

		val state = SkeletonState(
			boneInputs = inputs,
			skeletonHeight = 1.7f,
			paused = false
		)

		val newInputs = processor.process(state)

		val leftPinkyToeIsSameRotationAsLeftRingToe =
			newInputs.boneInputs[BodyPart.LEFT_PINKY_TOE]?.rawRotation == newInputs.boneInputs[BodyPart.LEFT_RING_TOE]?.rawRotation
		val rightPinkyToeIsSameRotationAsRightRingToe =
			newInputs.boneInputs[BodyPart.RIGHT_PINKY_TOE]?.rawRotation == newInputs.boneInputs[BodyPart.RIGHT_RING_TOE]?.rawRotation

		assertTrue(leftPinkyToeIsSameRotationAsLeftRingToe && rightPinkyToeIsSameRotationAsRightRingToe)
	}
}
