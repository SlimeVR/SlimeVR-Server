package dev.slimevr.tracker
import dev.slimevr.skeleton.BoneInput
import dev.slimevr.skeleton.BoneState
import dev.slimevr.skeleton.DEFAULT_SKELETON_STATE
import dev.slimevr.skeleton.SkeletonState
import dev.slimevr.skeleton.bodyPartMap
import dev.slimevr.skeleton.mutate
import dev.slimevr.skeleton.processors.ToesImputeProcessor
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import org.junit.jupiter.api.Test
import solarxr_protocol.datatypes.BodyPart
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ToesImputeProcessorTest {
	@Test
	fun `test impute missing all toe trackers`() {
		val processor = ToesImputeProcessor()
		val inputs = DEFAULT_SKELETON_STATE.boneInputs.mutate { map ->
			map[BodyPart.LEFT_FOOT] = map.getValue(BodyPart.LEFT_FOOT).copy(
				rawRotation = Quaternion.fromRotationVector(10f, 40f, 15f),
				isActive = true)
			map[BodyPart.RIGHT_FOOT] = map.getValue(BodyPart.RIGHT_FOOT).copy(
				rawRotation = Quaternion.fromRotationVector(10f, 40f, 15f),
				isActive = true)
		}

		val state = SkeletonState(
			boneInputs = inputs,
			skeletonHeight = 1.7f,
			paused = false
		)

		val newInputs = processor.process(state)

		val leftAbductorDigitorumBrevisIsSameAsLeftFoot =
			newInputs.boneInputs[BodyPart.LEFT_ABDUCTOR_HALLUCIS]?.rawRotation == newInputs.boneInputs[BodyPart.LEFT_FOOT]?.rawRotation

		val leftFlexorDigitorumBrevisDigitiMinimiIsSameAsLeftAbductorHallucis =
			newInputs.boneInputs[BodyPart.LEFT_FLEXOR_DIGITORUM_BREVIS]?.rawRotation == newInputs.boneInputs[BodyPart.LEFT_ABDUCTOR_HALLUCIS]?.rawRotation

		val leftAbductorDigitiMinimiIsSameAsLeftFlexorDigitorumBrevis =
			newInputs.boneInputs[BodyPart.LEFT_ABDUCTOR_DIGITI_MINIMI]?.rawRotation == newInputs.boneInputs[BodyPart.LEFT_FLEXOR_DIGITORUM_BREVIS]?.rawRotation

		val rightAbductorDigitorumBrevisIsSameAsRightFoot =
			newInputs.boneInputs[BodyPart.RIGHT_ABDUCTOR_HALLUCIS]?.rawRotation == newInputs.boneInputs[BodyPart.RIGHT_FOOT]?.rawRotation

		val rightFlexorDigitorumBrevisIsDigitiMinimiSameAsRightAbductorHallucis =
			newInputs.boneInputs[BodyPart.RIGHT_FLEXOR_DIGITORUM_BREVIS]?.rawRotation == newInputs.boneInputs[BodyPart.RIGHT_ABDUCTOR_HALLUCIS]?.rawRotation

		val rightAbductorDigitiMinimiIsSameAsRightFlexorDigitorumBrevis =
			newInputs.boneInputs[BodyPart.RIGHT_ABDUCTOR_DIGITI_MINIMI]?.rawRotation == newInputs.boneInputs[BodyPart.RIGHT_FLEXOR_DIGITORUM_BREVIS]?.rawRotation

		val testSucceeded =
			leftAbductorDigitorumBrevisIsSameAsLeftFoot
			&& leftFlexorDigitorumBrevisDigitiMinimiIsSameAsLeftAbductorHallucis
			&& leftAbductorDigitiMinimiIsSameAsLeftFlexorDigitorumBrevis
			&& rightAbductorDigitorumBrevisIsSameAsRightFoot
			&& rightFlexorDigitorumBrevisIsDigitiMinimiSameAsRightAbductorHallucis
			&& rightAbductorDigitiMinimiIsSameAsRightFlexorDigitorumBrevis

		assertTrue (testSucceeded )
	}
	@Test
	fun `test impute missing toe trackers from flexor digitorum brevis and abductor digiti minimi`() {
		val processor = ToesImputeProcessor()
		val inputs = DEFAULT_SKELETON_STATE.boneInputs.mutate { map ->
			map[BodyPart.LEFT_FOOT] = map.getValue(BodyPart.LEFT_FOOT).copy(
				rawRotation = Quaternion.fromRotationVector(10f, 40f, 15f),
				isActive = true)
			map[BodyPart.LEFT_ABDUCTOR_HALLUCIS] = map.getValue(BodyPart.LEFT_ABDUCTOR_HALLUCIS).copy(
				rawRotation = Quaternion.fromRotationVector(10f, 40f, 15f),
				isActive = true)
			map[BodyPart.RIGHT_FOOT] = map.getValue(BodyPart.RIGHT_FOOT).copy(
				rawRotation = Quaternion.fromRotationVector(10f, 40f, 15f),
				isActive = true)
			map[BodyPart.RIGHT_ABDUCTOR_HALLUCIS] = map.getValue(BodyPart.RIGHT_ABDUCTOR_HALLUCIS).copy(
				rawRotation = Quaternion.fromRotationVector(10f, 40f, 15f),
				isActive = true)
		}

		val state = SkeletonState(
			boneInputs = inputs,
			skeletonHeight = 1.7f,
			paused = false
		)

		val newInputs = processor.process(state)

		val leftFlexorDigitorumBrevisDigitiMinimiIsSameAsLeftAbductorHallucis =
			newInputs.boneInputs[BodyPart.LEFT_FLEXOR_DIGITORUM_BREVIS]?.rawRotation == newInputs.boneInputs[BodyPart.LEFT_ABDUCTOR_HALLUCIS]?.rawRotation

		val leftAbductorDigitiMinimiIsSameAsLeftFlexorDigitorumBrevis =
			 newInputs.boneInputs[BodyPart.LEFT_ABDUCTOR_DIGITI_MINIMI]?.rawRotation == newInputs.boneInputs[BodyPart.LEFT_FLEXOR_DIGITORUM_BREVIS]?.rawRotation

		val rightFlexorDigitorumBrevisIsDigitiMinimiSameAsRightAbductorHallucis =
			newInputs.boneInputs[BodyPart.RIGHT_FLEXOR_DIGITORUM_BREVIS]?.rawRotation == newInputs.boneInputs[BodyPart.RIGHT_ABDUCTOR_HALLUCIS]?.rawRotation

		val rightAbductorDigitiMinimiIsSameAsRightFlexorDigitorumBrevis =
			newInputs.boneInputs[BodyPart.RIGHT_ABDUCTOR_DIGITI_MINIMI]?.rawRotation == newInputs.boneInputs[BodyPart.RIGHT_FLEXOR_DIGITORUM_BREVIS]?.rawRotation

		val testSucceeded = leftFlexorDigitorumBrevisDigitiMinimiIsSameAsLeftAbductorHallucis
			&& leftAbductorDigitiMinimiIsSameAsLeftFlexorDigitorumBrevis
			&& rightFlexorDigitorumBrevisIsDigitiMinimiSameAsRightAbductorHallucis
			&& rightAbductorDigitiMinimiIsSameAsRightFlexorDigitorumBrevis

		assertTrue (testSucceeded )
	}

	@Test
	fun `test impute missing toe tracker from abductor digiti minimi`() {
		val processor = ToesImputeProcessor()
		val inputs = DEFAULT_SKELETON_STATE.boneInputs.mutate { map ->
			map[BodyPart.LEFT_FOOT] = map.getValue(BodyPart.LEFT_FOOT).copy(
				rawRotation = Quaternion.fromRotationVector(10f, 40f, 15f),
				isActive = true)
			map[BodyPart.LEFT_ABDUCTOR_HALLUCIS] = map.getValue(BodyPart.LEFT_ABDUCTOR_HALLUCIS).copy(
				rawRotation = Quaternion.fromRotationVector(10f, 40f, 15f),
				isActive = true)
			map[BodyPart.LEFT_FLEXOR_DIGITORUM_BREVIS] = map.getValue(BodyPart.LEFT_FLEXOR_DIGITORUM_BREVIS).copy(
				rawRotation = Quaternion.fromRotationVector(10f, 40f, 15f),
				isActive = true)
			map[BodyPart.RIGHT_FOOT] = map.getValue(BodyPart.RIGHT_FOOT).copy(
				rawRotation = Quaternion.fromRotationVector(10f, 40f, 15f),
				isActive = true)
			map[BodyPart.RIGHT_ABDUCTOR_HALLUCIS] = map.getValue(BodyPart.RIGHT_ABDUCTOR_HALLUCIS).copy(
				rawRotation = Quaternion.fromRotationVector(10f, 40f, 15f),
				isActive = true)
			map[BodyPart.RIGHT_FLEXOR_DIGITORUM_BREVIS] = map.getValue(BodyPart.RIGHT_FLEXOR_DIGITORUM_BREVIS).copy(
				rawRotation = Quaternion.fromRotationVector(10f, 40f, 15f),
				isActive = true)
		}

		val state = SkeletonState(
			boneInputs = inputs,
			skeletonHeight = 1.7f,
			paused = false
		)

		val newInputs = processor.process(state)

		val leftAbductorDigitiMinimiIsSameAsLeftFlexorDigitorumBrevis =
			newInputs.boneInputs[BodyPart.LEFT_ABDUCTOR_DIGITI_MINIMI]?.rawRotation == newInputs.boneInputs[BodyPart.LEFT_FLEXOR_DIGITORUM_BREVIS]?.rawRotation

		val rightAbductorDigitiMinimiIsSameAsRightFlexorDigitorumBrevis =
			newInputs.boneInputs[BodyPart.RIGHT_ABDUCTOR_DIGITI_MINIMI]?.rawRotation == newInputs.boneInputs[BodyPart.RIGHT_FLEXOR_DIGITORUM_BREVIS]?.rawRotation

		val testSucceeded = leftAbductorDigitiMinimiIsSameAsLeftFlexorDigitorumBrevis
			&& rightAbductorDigitiMinimiIsSameAsRightFlexorDigitorumBrevis

		assertTrue (testSucceeded )
	}
}
