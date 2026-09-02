package dev.slimevr.skeleton

import dev.slimevr.skeleton.inputprocessors.ToeDirectLinkInputProcessor
import io.github.axisangles.ktmath.Quaternion
import org.junit.jupiter.api.Test
import solarxr_protocol.datatypes.BodyPart
import kotlin.test.assertTrue

class PosteriorDirectLinkInputProcessorTest {
	@Test
	fun `test missing all posterior trackers`() {
		val processor = ToeDirectLinkInputProcessor()
		val inputs = DEFAULT_SKELETON_STATE.boneInputs.mutateCopy { map ->
			map[BodyPart.HIP] = map.getValue(BodyPart.HIP).copy(
				rotation = Quaternion.fromRotationVector(10f, 40f, 15f),
				isRotationActive = true,
			)
			map[BodyPart.LEFT_HIP] = map.getValue(BodyPart.LEFT_HIP).copy(
				rotation = Quaternion.fromRotationVector(10f, 40f, 15f),
				isRotationActive = true,
			)
			map[BodyPart.RIGHT_HIP] = map.getValue(BodyPart.RIGHT_HIP).copy(
				rotation = Quaternion.fromRotationVector(10f, 40f, 15f),
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

		val newInputs = processor.process(state.boneInputs, state.skeletonHeight)

		val leftPosteriorIsSameRotationAsLeftHip =
			newInputs[BodyPart.LEFT_POSTERIOR]?.rotation == newInputs[BodyPart.LEFT_HIP]?.rotation
		val rightPosteriorIsSameRotationAsRightHip =
			newInputs[BodyPart.RIGHT_POSTERIOR]?.rotation == newInputs[BodyPart.RIGHT_HIP]?.rotation
		val tailIsSameRotationIsHip =
			newInputs[BodyPart.TAIL]?.rotation == newInputs[BodyPart.HIP]?.rotation

		assertTrue(leftPosteriorIsSameRotationAsLeftHip)
		assertTrue(rightPosteriorIsSameRotationAsRightHip)
		assertTrue(tailIsSameRotationIsHip)
	}
}
