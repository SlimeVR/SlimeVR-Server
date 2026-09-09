package dev.slimevr.skeleton

import dev.slimevr.skeleton.inputprocessors.BustInputProcessor
import io.github.axisangles.ktmath.Quaternion
import org.junit.jupiter.api.Test
import solarxr_protocol.datatypes.BodyPart
import kotlin.test.assertTrue

class BustDirectLinkInputProcessorTest {

	@Test
	fun `test all missing bust trackers`() {
		val processor = BustInputProcessor()

		val inputs = DEFAULT_SKELETON_STATE.boneInputs.mutateCopy { map ->
			map[BodyPart.UPPER_CHEST] =
				map.getValue(BodyPart.UPPER_CHEST).copy(
					rotation = Quaternion.fromRotationVector(10f, 40f, 15f),
					isRotationActive = true,
				)
		}

		processor.process(inputs, 1.7f)

		val leftBustIsSameRotationAsChest =
			inputs[BodyPart.LEFT_BUST]?.rotation ==
				inputs[BodyPart.UPPER_CHEST]?.rotation

		val rightBustIsSameRotationAsChest =
			inputs[BodyPart.RIGHT_BUST]?.rotation ==
				inputs[BodyPart.UPPER_CHEST]?.rotation

		assertTrue(leftBustIsSameRotationAsChest)
		assertTrue(rightBustIsSameRotationAsChest)
	}
}
