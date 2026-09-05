package dev.slimevr.routing

import solarxr_protocol.datatypes.BodyPart
import kotlin.test.Test
import kotlin.test.assertEquals

class CandidateBonesTest {
	@Test
	fun `Candidate bones are automatically determined based off body parts`() {
		data class TestCase(
			val name: String,
			val fineBodyParts: Set<BodyPart?>,
			val expected: Set<BodyPart>,
		)

		val testCases = listOf(
			TestCase(
				name = "no body part",
				fineBodyParts = emptySet(),
				expected = emptySet(),
			),
			TestCase(
				name = "upper chest enables upper chest and hip",
				fineBodyParts = setOf(BodyPart.UPPER_CHEST),
				expected = setOf(
					BodyPart.UPPER_CHEST,
					BodyPart.HIP,
				),
			),
			TestCase(
				name = "chest enables upper chest and hip",
				fineBodyParts = setOf(BodyPart.CHEST),
				expected = setOf(
					BodyPart.UPPER_CHEST,
					BodyPart.HIP,
				),
			),
			TestCase(
				name = "waist enables hip",
				fineBodyParts = setOf(BodyPart.WAIST),
				expected = setOf(
					BodyPart.HIP,
				),
			),
			TestCase(
				name = "hip enables hip",
				fineBodyParts = setOf(BodyPart.HIP),
				expected = setOf(
					BodyPart.HIP,
				),
			),
			TestCase(
				name = "left lower arm enables left upper arm",
				fineBodyParts = setOf(BodyPart.LEFT_LOWER_ARM),
				expected = setOf(
					BodyPart.LEFT_UPPER_ARM,
				),
			),
			TestCase(
				name = "right lower arm enables right upper arm",
				fineBodyParts = setOf(BodyPart.RIGHT_LOWER_ARM),
				expected = setOf(
					BodyPart.RIGHT_UPPER_ARM,
				),
			),
			TestCase(
				name = "left lower leg enables left foot",
				fineBodyParts = setOf(BodyPart.LEFT_LOWER_LEG),
				expected = setOf(
					BodyPart.LEFT_FOOT,
				),
			),
			TestCase(
				name = "right lower leg enables right foot",
				fineBodyParts = setOf(BodyPart.RIGHT_LOWER_LEG),
				expected = setOf(
					BodyPart.RIGHT_FOOT,
				),
			),
			TestCase(
				name = "multiple automatic trackers",
				fineBodyParts = setOf(
					BodyPart.LEFT_UPPER_ARM,
					BodyPart.RIGHT_FOOT,
					BodyPart.HIP,
				),
				expected = setOf(
					BodyPart.LEFT_UPPER_ARM,
					BodyPart.RIGHT_FOOT,
					BodyPart.HIP,
				),
			),
			TestCase(
				name = "worn hand trackers still enable nothing, hands are overridden by hand",
				fineBodyParts = setOf(
					BodyPart.LEFT_HAND,
					BodyPart.RIGHT_HAND,
				),
				expected = emptySet(),
			),
			TestCase(
				name = "hands alongside automatic trackers leave the automatic ones untouched",
				fineBodyParts = setOf(
					BodyPart.LEFT_FOOT,
					BodyPart.RIGHT_HAND,
				),
				expected = setOf(
					BodyPart.LEFT_FOOT,
				),
			),
		)

		testCases.forEach { case ->
			val result = determineCandidateBones(case.fineBodyParts)

			assertEquals(
				expected = case.expected,
				actual = result.toSet(),
				message = case.name,
			)
		}
	}
}
