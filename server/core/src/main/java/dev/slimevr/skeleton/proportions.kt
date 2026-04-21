package dev.slimevr.skeleton

import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.SkeletonBone

// Sum of default bone lengths for height-contributing bones (from Drillis and Contini 1966).
// Used to normalize HEIGHT_SCALED_BONE_RATIOS.
private const val DEFAULT_HEIGHT = 0.1f + 0.16f + 0.16f + 0.20f + 0.04f + 0.42f + 0.50f // = 1.58f

// Maps each solarxr SkeletonBone to the BodyPart(s) it controls in the skeleton.
// Symmetric bones (legs, arms) map to both left and right sides.
val SKELETON_BONE_TO_BODY_PARTS: Map<SkeletonBone, List<BodyPart>> = mapOf(
	SkeletonBone.HEAD to listOf(BodyPart.HEAD),
	SkeletonBone.NECK to listOf(BodyPart.NECK),
	SkeletonBone.UPPER_CHEST to listOf(BodyPart.UPPER_CHEST),
	SkeletonBone.CHEST to listOf(BodyPart.CHEST),
	SkeletonBone.WAIST to listOf(BodyPart.WAIST),
	SkeletonBone.HIP to listOf(BodyPart.HIP),
	SkeletonBone.UPPER_LEG to listOf(BodyPart.LEFT_UPPER_LEG, BodyPart.RIGHT_UPPER_LEG),
	SkeletonBone.LOWER_LEG to listOf(BodyPart.LEFT_LOWER_LEG, BodyPart.RIGHT_LOWER_LEG),
	SkeletonBone.FOOT_LENGTH to listOf(BodyPart.LEFT_FOOT, BodyPart.RIGHT_FOOT),
	SkeletonBone.UPPER_ARM to listOf(BodyPart.LEFT_UPPER_ARM, BodyPart.RIGHT_UPPER_ARM),
	SkeletonBone.LOWER_ARM to listOf(BodyPart.LEFT_LOWER_ARM, BodyPart.RIGHT_LOWER_ARM),
)

// Per-bone fraction of total standing height, derived from Drillis and Contini (1966).
// Includes spine, legs, and arms, all bones whose length scales with user height.
// Non-height bones (HEAD, FOOT_LENGTH) are absent; they keep fixed defaults from DEFAULT_SKELETON_STATE.
private val HEIGHT_SCALED_BONE_RATIOS: Map<SkeletonBone, Float> = mapOf(
	SkeletonBone.NECK to 0.1f / DEFAULT_HEIGHT,
	SkeletonBone.UPPER_CHEST to 0.16f / DEFAULT_HEIGHT,
	SkeletonBone.CHEST to 0.16f / DEFAULT_HEIGHT,
	SkeletonBone.WAIST to 0.20f / DEFAULT_HEIGHT,
	SkeletonBone.HIP to 0.04f / DEFAULT_HEIGHT,
	SkeletonBone.UPPER_LEG to 0.42f / DEFAULT_HEIGHT,
	SkeletonBone.LOWER_LEG to 0.50f / DEFAULT_HEIGHT,
	SkeletonBone.UPPER_ARM to 0.26f / DEFAULT_HEIGHT,
	SkeletonBone.LOWER_ARM to 0.26f / DEFAULT_HEIGHT,
)

// Subset of HEIGHT_SCALED_BONE_RATIOS whose lengths actually sum to standing height (spine + legs).
// Arms are excluded: they scale with height but are not part of the height measurement.
// For symmetric bones (UPPER_LEG, LOWER_LEG) computeUserHeight takes the larger side.
private val HEIGHT_CONTRIBUTING_BONES: Set<SkeletonBone> =
	HEIGHT_SCALED_BONE_RATIOS.keys - setOf(SkeletonBone.UPPER_ARM, SkeletonBone.LOWER_ARM)

// Sums the HEIGHT_CONTRIBUTING_BONES lengths to derive standing height.
// For symmetric bones (legs) the larger side is used.
fun computeUserHeight(lengths: Map<BodyPart, Float>): Double = HEIGHT_CONTRIBUTING_BONES.sumOf { bone ->
	val bodyParts = SKELETON_BONE_TO_BODY_PARTS[bone] ?: return@sumOf 0.0
	bodyParts.maxOfOrNull { lengths[it] ?: 0f }?.toDouble() ?: 0.0
}

// Returns proportions keyed by SkeletonBone.name for config storage.
// Only height-scaled bones are included.
fun computeDefaultProportionsByBone(height: Float): Map<String, Float> = HEIGHT_SCALED_BONE_RATIOS.mapKeys { (bone, _) -> bone.name }
	.mapValues { (_, ratio) -> height * ratio }

// Returns proportions for all tracked bones: height-scaled + default lengths for the rest.
fun computeAllDefaultProportionsByBone(height: Float): Map<String, Float> {
	val heightScaled = computeDefaultProportionsByBone(height)
	val nonScaled = SKELETON_BONE_TO_BODY_PARTS
		.filter { (bone, _) -> bone !in HEIGHT_SCALED_BONE_RATIOS }
		.mapNotNull { (bone, bodyParts) ->
			val length = bodyParts
				.mapNotNull { DEFAULT_SKELETON_STATE.bones[it]?.length }
				.average().takeIf { !it.isNaN() }?.toFloat() ?: return@mapNotNull null
			bone.name to length
		}.toMap()
	return heightScaled + nonScaled
}

fun expandProportions(proportions: Map<String, Float>): Map<BodyPart, Float> = proportions.entries.flatMap { (boneName, length) ->
	val bone = SkeletonBone.entries.firstOrNull { it.name == boneName } ?: return@flatMap emptyList()
	(SKELETON_BONE_TO_BODY_PARTS[bone] ?: return@flatMap emptyList()).map { bodyPart -> bodyPart to length }
}.toMap()
