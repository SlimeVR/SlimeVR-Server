package dev.slimevr.skeleton

import dev.slimevr.skeleton.processors.ToeDirectLinkProcessor
import io.github.axisangles.ktmath.Quaternion
import org.junit.jupiter.api.Test
import solarxr_protocol.datatypes.BodyPart
import kotlin.test.assertTrue

class ToeDirectLinkProcessorTest {
	@Test
	fun `test impute missing all toe trackers`() {
		val processor = ToeDirectLinkProcessor()
		val inputs = DEFAULT_SKELETON_STATE.boneInputs.mutate { map ->
			map[BodyPart.LEFT_FOOT] = map.getValue(BodyPart.LEFT_FOOT).copy(
				rawRotation = Quaternion.Companion.fromRotationVector(10f, 40f, 15f),
				isActive = true)
			map[BodyPart.RIGHT_FOOT] = map.getValue(BodyPart.RIGHT_FOOT).copy(
				rawRotation = Quaternion.Companion.fromRotationVector(10f, 40f, 15f),
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

		val leftPinkyToesIsSameRotationAsLeftIndexToe =
			newInputs.boneInputs[BodyPart.LEFT_PINKY_TOE]?.rawRotation == newInputs.boneInputs[BodyPart.LEFT_INDEX_TOE]?.rawRotation

		val rightAbductorDigitorumBrevisIsSameRotationAsRightFoot =
			newInputs.boneInputs[BodyPart.RIGHT_BIG_TOE]?.rawRotation == newInputs.boneInputs[BodyPart.RIGHT_FOOT]?.rawRotation

		val rightIndexToeIsDigitiMinimiSameAsRightBigToe =
			newInputs.boneInputs[BodyPart.RIGHT_INDEX_TOE]?.rawRotation == newInputs.boneInputs[BodyPart.RIGHT_BIG_TOE]?.rawRotation

		val rightPinkyToesIsSameRotationAsRightIndexToe =
			newInputs.boneInputs[BodyPart.RIGHT_PINKY_TOE]?.rawRotation == newInputs.boneInputs[BodyPart.RIGHT_INDEX_TOE]?.rawRotation

		val testSucceeded =
			leftBigToeIsSameRotationAsLeftFoot
			&& leftIndexToeIsSameRotationAsLeftBigToe
			&& leftPinkyToesIsSameRotationAsLeftIndexToe
			&& rightAbductorDigitorumBrevisIsSameRotationAsRightFoot
			&& rightIndexToeIsDigitiMinimiSameAsRightBigToe
			&& rightPinkyToesIsSameRotationAsRightIndexToe

        assertTrue(testSucceeded)
	}
	@Test
	fun `test impute missing toe trackers from index toe and pinky toe`() {
		val processor = ToeDirectLinkProcessor()
		val inputs = DEFAULT_SKELETON_STATE.boneInputs.mutate { map ->
			map[BodyPart.LEFT_FOOT] = map.getValue(BodyPart.LEFT_FOOT).copy(
				rawRotation = Quaternion.Companion.fromRotationVector(10f, 40f, 15f),
				isActive = true)
			map[BodyPart.LEFT_BIG_TOE] = map.getValue(BodyPart.LEFT_BIG_TOE).copy(
				rawRotation = Quaternion.Companion.fromRotationVector(10f, 40f, 15f),
				isActive = true)
			map[BodyPart.RIGHT_FOOT] = map.getValue(BodyPart.RIGHT_FOOT).copy(
				rawRotation = Quaternion.Companion.fromRotationVector(10f, 40f, 15f),
				isActive = true)
			map[BodyPart.RIGHT_BIG_TOE] = map.getValue(BodyPart.RIGHT_BIG_TOE).copy(
				rawRotation = Quaternion.Companion.fromRotationVector(10f, 40f, 15f),
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

		val leftPinkyToesIsSameRotationAsLeftIndexToe =
			 newInputs.boneInputs[BodyPart.LEFT_PINKY_TOE]?.rawRotation == newInputs.boneInputs[BodyPart.LEFT_INDEX_TOE]?.rawRotation

		val rightIndexToeIsDigitiMinimiSameAsRightBigToe =
			newInputs.boneInputs[BodyPart.RIGHT_INDEX_TOE]?.rawRotation == newInputs.boneInputs[BodyPart.RIGHT_BIG_TOE]?.rawRotation

		val rightPinkyToesIsSameRotationAsRightIndexToe =
			newInputs.boneInputs[BodyPart.RIGHT_PINKY_TOE]?.rawRotation == newInputs.boneInputs[BodyPart.RIGHT_INDEX_TOE]?.rawRotation

		val testSucceeded = leftIndexToeIsSameRotationAsLeftBigToe
			&& leftPinkyToesIsSameRotationAsLeftIndexToe
			&& rightIndexToeIsDigitiMinimiSameAsRightBigToe
			&& rightPinkyToesIsSameRotationAsRightIndexToe

        assertTrue(testSucceeded)
	}

	@Test
	fun `test impute missing toe tracker from pinky toe`() {
		val processor = ToeDirectLinkProcessor()
		val inputs = DEFAULT_SKELETON_STATE.boneInputs.mutate { map ->
			map[BodyPart.LEFT_FOOT] = map.getValue(BodyPart.LEFT_FOOT).copy(
				rawRotation = Quaternion.Companion.fromRotationVector(10f, 40f, 15f),
				isActive = true)
			map[BodyPart.LEFT_BIG_TOE] = map.getValue(BodyPart.LEFT_BIG_TOE).copy(
				rawRotation = Quaternion.Companion.fromRotationVector(10f, 40f, 15f),
				isActive = true)
			map[BodyPart.LEFT_INDEX_TOE] = map.getValue(BodyPart.LEFT_INDEX_TOE).copy(
				rawRotation = Quaternion.Companion.fromRotationVector(10f, 40f, 15f),
				isActive = true)
			map[BodyPart.RIGHT_FOOT] = map.getValue(BodyPart.RIGHT_FOOT).copy(
				rawRotation = Quaternion.Companion.fromRotationVector(10f, 40f, 15f),
				isActive = true)
			map[BodyPart.RIGHT_BIG_TOE] = map.getValue(BodyPart.RIGHT_BIG_TOE).copy(
				rawRotation = Quaternion.Companion.fromRotationVector(10f, 40f, 15f),
				isActive = true)
			map[BodyPart.RIGHT_INDEX_TOE] = map.getValue(BodyPart.RIGHT_INDEX_TOE).copy(
				rawRotation = Quaternion.Companion.fromRotationVector(10f, 40f, 15f),
				isActive = true)
		}

		val state = SkeletonState(
			boneInputs = inputs,
			skeletonHeight = 1.7f,
			paused = false
		)

		val newInputs = processor.process(state)

		val leftPinkyToesIsSameRotationAsLeftIndexToe =
			newInputs.boneInputs[BodyPart.LEFT_PINKY_TOE]?.rawRotation == newInputs.boneInputs[BodyPart.LEFT_INDEX_TOE]?.rawRotation

		val rightPinkyToesIsSameRotationAsRightIndexToe =
			newInputs.boneInputs[BodyPart.RIGHT_PINKY_TOE]?.rawRotation == newInputs.boneInputs[BodyPart.RIGHT_INDEX_TOE]?.rawRotation

		val testSucceeded = leftPinkyToesIsSameRotationAsLeftIndexToe
			&& rightPinkyToesIsSameRotationAsRightIndexToe

        assertTrue(testSucceeded)
	}
}
