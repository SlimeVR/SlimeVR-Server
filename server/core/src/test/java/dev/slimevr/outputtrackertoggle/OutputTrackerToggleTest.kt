package dev.slimevr.outputtrackertoggle

import dev.slimevr.config.OutputTrackersConfig
import solarxr_protocol.datatypes.BodyPart
import kotlin.test.Test
import kotlin.test.assertEquals

class OutputTrackerToggleTest {
	@Test
	fun `Output trackers are automatically determined based off body parts`() {
		data class TestCase(
			val name: String,
			val okBodyParts: Set<BodyPart?>,
			val configuredTrackers: List<BodyPart>,
			val expected: Set<BodyPart>,
		)

		val testCases = listOf(
			TestCase(
				name = "no body part",
				okBodyParts = emptySet(),
				configuredTrackers = emptyList(),
				expected = emptySet(),
			),
			TestCase(
				name = "upper chest enables upper chest and hip",
				okBodyParts = setOf(BodyPart.UPPER_CHEST),
				configuredTrackers = emptyList(),
				expected = setOf(
					BodyPart.UPPER_CHEST,
					BodyPart.HIP,
				),
			),
			TestCase(
				name = "chest enables upper chest and hip",
				okBodyParts = setOf(BodyPart.CHEST),
				configuredTrackers = emptyList(),
				expected = setOf(
					BodyPart.UPPER_CHEST,
					BodyPart.HIP,
				),
			),
			TestCase(
				name = "waist enables hip",
				okBodyParts = setOf(BodyPart.WAIST),
				configuredTrackers = emptyList(),
				expected = setOf(
					BodyPart.HIP,
				),
			),
			TestCase(
				name = "hip enables hip",
				okBodyParts = setOf(BodyPart.HIP),
				configuredTrackers = emptyList(),
				expected = setOf(
					BodyPart.HIP,
				),
			),
			TestCase(
				name = "left lower arm enables left upper arm",
				okBodyParts = setOf(BodyPart.LEFT_LOWER_ARM),
				configuredTrackers = emptyList(),
				expected = setOf(
					BodyPart.LEFT_UPPER_ARM,
				),
			),
			TestCase(
				name = "right lower arm enables right upper arm",
				okBodyParts = setOf(BodyPart.RIGHT_LOWER_ARM),
				configuredTrackers = emptyList(),
				expected = setOf(
					BodyPart.RIGHT_UPPER_ARM,
				),
			),
			TestCase(
				name = "left lower leg enables left foot",
				okBodyParts = setOf(BodyPart.LEFT_LOWER_LEG),
				configuredTrackers = emptyList(),
				expected = setOf(
					BodyPart.LEFT_FOOT,
				),
			),
			TestCase(
				name = "right lower leg enables right foot",
				okBodyParts = setOf(BodyPart.RIGHT_LOWER_LEG),
				configuredTrackers = emptyList(),
				expected = setOf(
					BodyPart.RIGHT_FOOT,
				),
			),
			TestCase(
				name = "multiple automatic trackers",
				okBodyParts = setOf(
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
				okBodyParts = emptySet(),
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
				okBodyParts = emptySet(),
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
				okBodyParts = setOf(
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

		val behaviour = OutputTrackerToggleBasicBehaviour()
		testCases.forEach { case ->
			val result = behaviour.determineAutomaticOutputTrackers(
				config = OutputTrackersConfig(trackers = case.configuredTrackers),
				okBodyParts = case.okBodyParts,
			)

			assertEquals(
				expected = case.expected,
				actual = result.toSet(),
				message = case.name,
			)
		}
	}
}
