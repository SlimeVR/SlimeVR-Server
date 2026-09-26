package dev.slimevr.skeleton

import solarxr_protocol.rpc.SkeletonBone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ProportionsTest {
	@Test
	fun `length bones are raised to their minimum and lowered to their maximum`() {
		val foot = BONE_SPECS.getValue(SkeletonBone.FOOT_LENGTH)
		assertEquals(foot.min, clampBoneValue(SkeletonBone.FOOT_LENGTH, 0f))
		assertEquals(BONE_SPECS.getValue(SkeletonBone.HAND).min, clampBoneValue(SkeletonBone.HAND, -0.2f))
		assertEquals(foot.max, clampBoneValue(SkeletonBone.FOOT_LENGTH, 5f))
		assertEquals(0.3f, clampBoneValue(SkeletonBone.UPPER_LEG, 0.3f))
	}

	@Test
	fun `every bone except the foot shift has a positive minimum`() {
		for (bone in SkeletonBone.entries) {
			if (bone == SkeletonBone.NONE || bone == SkeletonBone.FOOT_SHIFT) continue
			assertTrue(BONE_SPECS.getValue(bone).min > 0f, bone.name)
			assertTrue(BONE_SPECS.getValue(bone).max > BONE_SPECS.getValue(bone).min, bone.name)
		}
	}

	@Test
	fun `foot shift ranges from minus to plus half a metre`() {
		assertEquals(-0.5f, BONE_SPECS.getValue(SkeletonBone.FOOT_SHIFT).min)
		assertEquals(0.5f, BONE_SPECS.getValue(SkeletonBone.FOOT_SHIFT).max)
		assertEquals(0f, clampBoneValue(SkeletonBone.FOOT_SHIFT, 0f))
		assertEquals(-0.05f, clampBoneValue(SkeletonBone.FOOT_SHIFT, -0.05f))
		assertEquals(-0.5f, clampBoneValue(SkeletonBone.FOOT_SHIFT, -2f))
		assertEquals(0.5f, clampBoneValue(SkeletonBone.FOOT_SHIFT, 2f))
	}

	@Test
	fun `the defaults sit inside their ranges`() {
		for ((bone, value) in DEFAULT_PROPORTIONS) {
			assertEquals(value, clampBoneValue(bone, value), bone.name)
		}
	}

	@Test
	fun `every bone but none has a default`() {
		for (bone in SkeletonBone.entries) {
			if (bone == SkeletonBone.NONE) continue
			assertNotNull(DEFAULT_PROPORTIONS[bone], bone.name)
		}
	}

	@Test
	fun `config keys that are not a bone are dropped`() {
		assertEquals(mapOf(SkeletonBone.HAND to 0.08f), configToBoneValues(mapOf("HAND" to 0.08f, "NONE" to 1f, "NOT_A_BONE" to 1f)))
	}

	@Test
	fun `a config with a zero length loads clamped`() {
		val bones = configToBoneValues(mapOf("FOOT_LENGTH" to 0f, "FOOT_SHIFT" to -0.05f, "UPPER_LEG" to 0.4f))
		assertEquals(BONE_SPECS.getValue(SkeletonBone.FOOT_LENGTH).min, bones[SkeletonBone.FOOT_LENGTH])
		assertEquals(-0.05f, bones[SkeletonBone.FOOT_SHIFT])
		assertEquals(0.4f, bones[SkeletonBone.UPPER_LEG])
	}

	@Test
	fun `adult heights scale every bone linearly`() {
		val a = computeDefaultProportionsByBone(1.5f)
		val b = computeDefaultProportionsByBone(1.8f)
		for ((name, value) in a) assertEquals(b.getValue(name) / 1.8f, value / 1.5f, 1e-4f, name)
	}

	@Test
	fun `the height bones always sum to the height`() {
		for (height in listOf(0.9f, 1.0f, 1.2f, 1.5f, 1.8f, 2.2f)) {
			val bones = computeDefaultProportionsByBone(height).mapKeys { SkeletonBone.valueOf(it.key) }
			assertEquals(height, bones.height(), 1e-4f, "height $height")
		}
	}

	@Test
	fun `a child has shorter legs and a longer trunk than a scaled adult`() {
		val child = computeDefaultProportionsByBone(0.94f)
		val adult = computeDefaultProportionsByBone(1.58f)
		val scale = 0.94f / 1.58f
		assertTrue(child.getValue("UPPER_LEG") < adult.getValue("UPPER_LEG") * scale)
		assertTrue(child.getValue("UPPER_CHEST") > adult.getValue("UPPER_CHEST") * scale)
	}

	@Test
	fun `a child has shorter arms and wider shoulders and bigger hands than a scaled adult`() {
		val child = computeDefaultProportionsByBone(0.94f)
		val adult = computeDefaultProportionsByBone(1.58f)
		val scale = 0.94f / 1.58f
		assertTrue(child.getValue("UPPER_ARM") < adult.getValue("UPPER_ARM") * scale)
		assertTrue(child.getValue("LOWER_ARM") < adult.getValue("LOWER_ARM") * scale)
		assertTrue(child.getValue("SHOULDERS_WIDTH") > adult.getValue("SHOULDERS_WIDTH") * scale)
		assertTrue(child.getValue("HAND") > adult.getValue("HAND") * scale)
		assertTrue(child.getValue("FOOT_LENGTH") > adult.getValue("FOOT_LENGTH") * scale)
	}
}
