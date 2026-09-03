package dev.slimevr.skeleton

import dev.slimevr.skeleton.inputprocessors.BoneDirectLinkInputProcessor
import dev.slimevr.skeleton.inputprocessors.ToeDirectLinkInputProcessor
import io.github.axisangles.ktmath.Quaternion
import org.junit.jupiter.api.Test
import solarxr_protocol.datatypes.BodyPart
import kotlin.test.assertTrue

class BustDirectLinkInputProcessorTest {
	@Test
	fun `test all missing bust trackers`() {
		val processor = BoneDirectLinkInputProcessor()
		val inputs = DEFAULT_SKELETON_STATE.boneInputs.mutateCopy { map ->
			map[BodyPart.UPPER_CHEST] = map.getValue(BodyPart.UPPER_CHEST).copy(
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

		val leftBustIsSameRotationAsChest =
			newInputs[BodyPart.LEFT_BUST]?.rotation == newInputs[BodyPart.UPPER_CHEST]?.rotation

		val rightBustIsSameRotationAsChest =
			newInputs[BodyPart.RIGHT_BUST]?.rotation == newInputs[BodyPart.UPPER_CHEST]?.rotation

		assertTrue(leftBustIsSameRotationAsChest)
		assertTrue(rightBustIsSameRotationAsChest)
	}
}
