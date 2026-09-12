package dev.slimevr.skeleton

import dev.slimevr.skeleton.inputprocessors.PosteriorDirectLinkInputProcessor
import io.github.axisangles.ktmath.Quaternion
import org.junit.jupiter.api.Test
import solarxr_protocol.datatypes.BodyPart
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PosteriorDirectLinkInputProcessorTest {
	@Test
	fun `test missing all posterior trackers`() {
		val processor = PosteriorDirectLinkInputProcessor()
		val inputs = DEFAULT_SKELETON_STATE.boneInputs.clone()
		val targetRotation = Quaternion.fromRotationVector(10f, 40f, 15f)
		inputs[BodyPart.HIP] = inputs.getValue(BodyPart.HIP).copy(
			rotation = targetRotation,
			isRotationActive = true,
		)
		inputs[BodyPart.LEFT_POSTERIOR] = DEFAULT_BONE_INPUT.copy(bodyPart = BodyPart.LEFT_POSTERIOR)
		inputs[BodyPart.RIGHT_POSTERIOR] = DEFAULT_BONE_INPUT.copy(bodyPart = BodyPart.RIGHT_POSTERIOR)
		inputs[BodyPart.TAIL] = DEFAULT_BONE_INPUT.copy(bodyPart = BodyPart.TAIL)

		processor.process(inputs, 1.7f)

		val leftPosteriorIsSameRotationAsHip =
			inputs[BodyPart.LEFT_POSTERIOR]?.rotation == targetRotation
		val rightPosteriorIsSameRotationAsHip =
			inputs[BodyPart.RIGHT_POSTERIOR]?.rotation == targetRotation
		val tailIsSameRotationAsHip =
			inputs[BodyPart.TAIL]?.rotation == targetRotation

		assertTrue(leftPosteriorIsSameRotationAsHip)
		assertTrue(rightPosteriorIsSameRotationAsHip)
		assertTrue(tailIsSameRotationAsHip)
	}
}
