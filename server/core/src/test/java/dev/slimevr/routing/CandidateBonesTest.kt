package dev.slimevr.routing

import dev.slimevr.config.BoneRoutingConfig
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.RoutingOutput
import kotlin.test.Test
import kotlin.test.assertEquals

class CandidateBonesTest {
	@Test
	fun `Candidate bones are automatically determined based off body parts`() {
		data class TestCase(
			val name: String,
			val fineBodyParts: Set<BodyPart?>,
			val configuredTrackers: List<BodyPart>,
			val expected: Set<BodyPart>,
		)

		val testCases = listOf(
			TestCase(
				name = "no body part",
				fineBodyParts = emptySet(),
				configuredTrackers = emptyList(),
				expected = emptySet(),
			),
			TestCase(
				name = "upper chest enables upper chest and hip",
				fineBodyParts = setOf(BodyPart.UPPER_CHEST),
				configuredTrackers = emptyList(),
				expected = setOf(
					BodyPart.UPPER_CHEST,
					BodyPart.HIP,
				),
			),
			TestCase(
				name = "chest enables upper chest and hip",
				fineBodyParts = setOf(BodyPart.CHEST),
				configuredTrackers = emptyList(),
				expected = setOf(
					BodyPart.UPPER_CHEST,
					BodyPart.HIP,
				),
			),
			TestCase(
				name = "waist enables hip",
				fineBodyParts = setOf(BodyPart.WAIST),
				configuredTrackers = emptyList(),
				expected = setOf(
					BodyPart.HIP,
				),
			),
			TestCase(
				name = "hip enables hip",
				fineBodyParts = setOf(BodyPart.HIP),
				configuredTrackers = emptyList(),
				expected = setOf(
					BodyPart.HIP,
				),
			),
			TestCase(
				name = "left lower arm enables left upper arm",
				fineBodyParts = setOf(BodyPart.LEFT_LOWER_ARM),
				configuredTrackers = emptyList(),
				expected = setOf(
					BodyPart.LEFT_UPPER_ARM,
				),
			),
			TestCase(
				name = "right lower arm enables right upper arm",
				fineBodyParts = setOf(BodyPart.RIGHT_LOWER_ARM),
				configuredTrackers = emptyList(),
				expected = setOf(
					BodyPart.RIGHT_UPPER_ARM,
				),
			),
			TestCase(
				name = "left lower leg enables left foot",
				fineBodyParts = setOf(BodyPart.LEFT_LOWER_LEG),
				configuredTrackers = emptyList(),
				expected = setOf(
					BodyPart.LEFT_FOOT,
				),
			),
			TestCase(
				name = "right lower leg enables right foot",
				fineBodyParts = setOf(BodyPart.RIGHT_LOWER_LEG),
				configuredTrackers = emptyList(),
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
				configuredTrackers = emptyList(),
				expected = setOf(
					BodyPart.LEFT_UPPER_ARM,
					BodyPart.RIGHT_FOOT,
					BodyPart.HIP,
				),
			),
			TestCase(
				name = "hands are always included from config",
				fineBodyParts = emptySet(),
				configuredTrackers = listOf(
					BodyPart.LEFT_HAND,
					BodyPart.RIGHT_HAND,
				),
				expected = setOf(
					BodyPart.LEFT_HAND,
					BodyPart.RIGHT_HAND,
				),
			),
			TestCase(
				name = "non-hand manual trackers are ignored",
				fineBodyParts = emptySet(),
				configuredTrackers = listOf(
					BodyPart.CHEST,
					BodyPart.WAIST,
					BodyPart.LEFT_HAND,
				),
				expected = setOf(
					BodyPart.LEFT_HAND,
				),
			),
			TestCase(
				name = "automatic trackers combined with hands",
				fineBodyParts = setOf(
					BodyPart.LEFT_FOOT,
				),
				configuredTrackers = listOf(
					BodyPart.RIGHT_HAND,
				),
				expected = setOf(
					BodyPart.LEFT_FOOT,
					BodyPart.RIGHT_HAND,
				),
			),
		)


		testCases.forEach { case ->
			val result = determineCandidateBones(
				config = BoneRoutingConfig(
					manualRoutes = case.configuredTrackers.associateWith { setOf(RoutingOutput.DRIVER) },
				),
				fineBodyParts = case.fineBodyParts,
			)

			assertEquals(
				expected = case.expected,
				actual = result.toSet(),
				message = case.name,
			)
		}
	}

	@Test
	fun `hands stay routable manually while never auto enabling`() {
		val candidates = determineCandidateBones(
			config = BoneRoutingConfig(
				manualRoutes = mapOf(BodyPart.LEFT_HAND to setOf(RoutingOutput.DRIVER)),
			),
			fineBodyParts = emptySet(),
		)

		assertEquals(setOf(BodyPart.LEFT_HAND), candidates)
	}
}
