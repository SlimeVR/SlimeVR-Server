package dev.slimevr.routing

import dev.slimevr.bones.BodyPart
import dev.slimevr.bones.BoneId
import dev.slimevr.bones.boneId
import dev.slimevr.testCompiledSkeleton
import kotlin.test.Test
import kotlin.test.assertEquals

class CandidateBonesTest {
	@Test
	fun `Candidate bones are automatically determined based off tracked bones`() {
		data class TestCase(
			val name: String,
			val trackedBoneIds: Set<BodyPart?>,
			val expected: Set<BodyPart>,
		)

		val testCases = listOf(
			TestCase(
				name = "no body part",
				trackedBoneIds = emptySet(),
				expected = emptySet(),
			),
			TestCase(
				name = "upper chest enables upper chest and hip",
				trackedBoneIds = setOf(BodyPart.UPPER_CHEST),
				expected = setOf(
					BodyPart.UPPER_CHEST,
					BodyPart.HIP,
				),
			),
			TestCase(
				name = "lower chest enables upper chest and hip",
				trackedBoneIds = setOf(BodyPart.LOWER_CHEST),
				expected = setOf(
					BodyPart.UPPER_CHEST,
					BodyPart.HIP,
				),
			),
			TestCase(
				name = "lower waist enables hip",
				trackedBoneIds = setOf(BodyPart.LOWER_WAIST),
				expected = setOf(
					BodyPart.HIP,
				),
			),
			TestCase(
				name = "hip enables hip",
				trackedBoneIds = setOf(BodyPart.HIP),
				expected = setOf(
					BodyPart.HIP,
				),
			),
			TestCase(
				name = "left lower arm enables left upper arm",
				trackedBoneIds = setOf(BodyPart.LEFT_LOWER_ARM),
				expected = setOf(
					BodyPart.LEFT_UPPER_ARM,
				),
			),
			TestCase(
				name = "right lower arm enables right upper arm",
				trackedBoneIds = setOf(BodyPart.RIGHT_LOWER_ARM),
				expected = setOf(
					BodyPart.RIGHT_UPPER_ARM,
				),
			),
			TestCase(
				name = "left lower leg enables left foot",
				trackedBoneIds = setOf(BodyPart.LEFT_LOWER_LEG),
				expected = setOf(
					BodyPart.LEFT_FOOT,
				),
			),
			TestCase(
				name = "right lower leg enables right foot",
				trackedBoneIds = setOf(BodyPart.RIGHT_LOWER_LEG),
				expected = setOf(
					BodyPart.RIGHT_FOOT,
				),
			),
			TestCase(
				name = "multiple automatic trackers",
				trackedBoneIds = setOf(
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
				trackedBoneIds = setOf(
					BodyPart.LEFT_HAND,
					BodyPart.RIGHT_HAND,
				),
				expected = emptySet(),
			),
			TestCase(
				name = "hands alongside automatic trackers leave the automatic ones untouched",
				trackedBoneIds = setOf(
					BodyPart.LEFT_FOOT,
					BodyPart.RIGHT_HAND,
				),
				expected = setOf(
					BodyPart.LEFT_FOOT,
				),
			),
		)

		testCases.forEach { case ->
			val trackedBoneIds: Set<BoneId?> = case.trackedBoneIds.mapTo(mutableSetOf()) { it?.boneId }
			val result = determineCandidateBones(trackedBoneIds, testCompiledSkeleton)

			assertEquals(
				expected = case.expected.mapTo(mutableSetOf()) { it.boneId },
				actual = result,
				message = case.name,
			)
		}
	}
}
