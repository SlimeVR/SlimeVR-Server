package dev.slimevr.skeleton

import io.github.axisangles.ktmath.Vector3
import io.github.axisangles.ktmath.times
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.SkeletonBone
import kotlin.collections.map
import kotlin.collections.plus

// Placeholder, move this to config defaults somehow
val DEFAULT_PROPORTIONS = mapOf(
	SkeletonBone.HEAD to 0.1f,
	SkeletonBone.NECK to 0.1f,
	SkeletonBone.UPPER_CHEST to 0.16f,
	SkeletonBone.CHEST to 0.16f,
	SkeletonBone.CHEST_OFFSET to 0f,
	SkeletonBone.WAIST to 0.2f,
	SkeletonBone.HIP to 0.04f,
	SkeletonBone.HIP_OFFSET to 0f,
	SkeletonBone.HIPS_WIDTH to 0.26f,
	SkeletonBone.UPPER_LEG to 0.42f,
	SkeletonBone.LOWER_LEG to 0.5f,
	SkeletonBone.FOOT_LENGTH to 0.05f,
	SkeletonBone.FOOT_SHIFT to -0.05f,
	SkeletonBone.SKELETON_OFFSET to 0f,
	SkeletonBone.SHOULDERS_DISTANCE to 0.08f,
	SkeletonBone.SHOULDERS_WIDTH to 0.35f,
	SkeletonBone.UPPER_ARM to 0.26f,
	SkeletonBone.LOWER_ARM to 0.26f,
	SkeletonBone.HAND_Y to 0.035f,
	SkeletonBone.HAND_Z to 0.13f,
	SkeletonBone.ELBOW_OFFSET to 0f,
)

// Set of SkeletonBones whose lengths sum to standing height (spine + legs).
// Arms are excluded: they scale with height but are not part of the height measurement.
private val HEIGHT_CONTRIBUTING_BONES: Set<SkeletonBone> = setOf(
	SkeletonBone.NECK,
	SkeletonBone.UPPER_CHEST,
	SkeletonBone.CHEST,
	SkeletonBone.WAIST,
	SkeletonBone.HIP,
	SkeletonBone.UPPER_LEG,
	SkeletonBone.LOWER_LEG,
)

// Maps each SolarXR SkeletonBone to the BodyPart(s) it controls in the skeleton with vectors for offset directions.
private val SKELETON_BONE_FLOAT_TO_BODY_PARTS: Map<SkeletonBone, Map<BodyPart, Vector3>> = mapOf(
	SkeletonBone.HEAD to mapOf(BodyPart.HEAD to Vector3.POS_Z),
	SkeletonBone.NECK to mapOf(BodyPart.NECK to Vector3.NEG_Y),
	SkeletonBone.UPPER_CHEST to mapOf(BodyPart.UPPER_CHEST to Vector3.NEG_Y),
	SkeletonBone.CHEST to mapOf(BodyPart.CHEST to Vector3.NEG_Y),
	SkeletonBone.CHEST_OFFSET to mapOf(),
	SkeletonBone.WAIST to mapOf(BodyPart.WAIST to Vector3.NEG_Y),
	SkeletonBone.HIP to mapOf(BodyPart.HIP to Vector3.NEG_Y),
	SkeletonBone.HIP_OFFSET to mapOf(),
	SkeletonBone.HIPS_WIDTH to mapOf(BodyPart.LEFT_HIP to Vector3.NEG_X / 2f, BodyPart.RIGHT_HIP to Vector3.POS_X / 2f),
	SkeletonBone.UPPER_LEG to mapOf(BodyPart.LEFT_UPPER_LEG to Vector3.NEG_Y, BodyPart.RIGHT_UPPER_LEG to Vector3.NEG_Y),
	SkeletonBone.LOWER_LEG to mapOf(BodyPart.LEFT_LOWER_LEG to Vector3.NEG_Y, BodyPart.RIGHT_LOWER_LEG to Vector3.NEG_Y),
	SkeletonBone.FOOT_LENGTH to mapOf(BodyPart.LEFT_FOOT to Vector3.NEG_Z, BodyPart.RIGHT_FOOT to Vector3.NEG_Z),
	SkeletonBone.FOOT_SHIFT to mapOf(BodyPart.LEFT_LOWER_LEG to Vector3.NEG_Z, BodyPart.RIGHT_LOWER_LEG to Vector3.NEG_Z),
	SkeletonBone.SKELETON_OFFSET to mapOf(),
	SkeletonBone.SHOULDERS_DISTANCE to mapOf(BodyPart.LEFT_SHOULDER to Vector3.NEG_Y, BodyPart.RIGHT_SHOULDER to Vector3.NEG_Y),
	SkeletonBone.SHOULDERS_WIDTH to mapOf(BodyPart.LEFT_SHOULDER to Vector3.NEG_X / 2f, BodyPart.RIGHT_SHOULDER to Vector3.POS_X / 2f),
	SkeletonBone.UPPER_ARM to mapOf(BodyPart.LEFT_UPPER_ARM to Vector3.NEG_Y, BodyPart.RIGHT_UPPER_ARM to Vector3.NEG_Y),
	SkeletonBone.LOWER_ARM to mapOf(BodyPart.LEFT_LOWER_ARM to Vector3.NEG_Y, BodyPart.RIGHT_LOWER_ARM to Vector3.NEG_Y),
	SkeletonBone.HAND_Y to mapOf(BodyPart.LEFT_HAND to Vector3.NEG_Y, BodyPart.RIGHT_HAND to Vector3.NEG_Y),
	SkeletonBone.HAND_Z to mapOf(BodyPart.LEFT_HAND to Vector3.NEG_Z, BodyPart.RIGHT_HAND to Vector3.NEG_Z),
	SkeletonBone.ELBOW_OFFSET to mapOf(),
)

private val BODY_PART_VECTOR_TO_SKELETON_BONES: Map<BodyPart, Map<SkeletonBone, Vector3>> = SKELETON_BONE_FLOAT_TO_BODY_PARTS
	.flatMap { (cfg, bones) ->
		// Invert map, splitting entries [ List<Pair<BodyPart, Pair<SkeletonBone, Vector3>>> ]
		// Vector also needs to be inverted ((vec/len)/len)==(1/vec)
		bones.map { (bone, vec) -> bone to (cfg to vec / vec.lenSq()) }
	}
	// Merge entries, creating a map again [ Map<BodyPart, List<Pair<SkeletonBone, Vector3>>> ]
	.groupBy({ it.first }, { it.second })
	// Transform the values into maps [ Map<BodyPart, Map<SkeletonBone, Vector3>> ]
	.mapValues { it.value.toMap() }

// Maps each SolarXR SkeletonBone to the BodyPart(s) it controls in the skeleton.
// Symmetric bones (legs, arms) map to both left and right sides.
val SKELETON_BONE_TO_BODY_PARTS: Map<SkeletonBone, Set<BodyPart>> = SKELETON_BONE_FLOAT_TO_BODY_PARTS.mapValues { it.value.keys }

// Sum of default bone lengths for height-contributing bones
// Used to normalize HEIGHT_SCALED_BONE_RATIOS.
val DEFAULT_HEIGHT = DEFAULT_PROPORTIONS.height() // = 1.58f

// Per-bone fraction of total standing height, includes spine, legs, and arms, all bones
// whose length scales with user height.
// Non-height bones (HEAD, FOOT_LENGTH) are absent; they keep fixed defaults from DEFAULT_SKELETON_STATE.
private val HEIGHT_SCALED_BONE_RATIOS: Map<SkeletonBone, Float> = (
	HEIGHT_CONTRIBUTING_BONES + setOf(SkeletonBone.UPPER_ARM, SkeletonBone.LOWER_ARM)
	).associateWith { (DEFAULT_PROPORTIONS[it] ?: 0f) / DEFAULT_HEIGHT }

// Sums the HEIGHT_CONTRIBUTING_BONES lengths to derive standing height.
fun Map<SkeletonBone, Float>.height(): Float = HEIGHT_CONTRIBUTING_BONES.sumOf { bone ->
	this[bone]?.toDouble() ?: 0.0
}.toFloat()

// Returns proportions keyed by SkeletonBone.name for config storage.
// Only height-scaled bones are included.
fun computeDefaultProportionsByBone(height: Float): Map<String, Float> = HEIGHT_SCALED_BONE_RATIOS
	.mapKeys { (bone, _) -> bone.name }
	.mapValues { (_, ratio) -> height * ratio }

// Returns proportions for all tracked bones: height-scaled + default lengths for the rest.
fun computeAllDefaultProportionsByBone(height: Float): Map<String, Float> {
	val nonScaled = DEFAULT_PROPORTIONS.mapKeys { (bone, _) -> bone.name }
	val heightScaled = computeDefaultProportionsByBone(height)
	return nonScaled + heightScaled
}

fun Map<SkeletonBone, Float>.toBodyPartOffsets(): Map<BodyPart, Vector3> = this
	.flatMap { (cfg, length) ->
		SKELETON_BONE_FLOAT_TO_BODY_PARTS[cfg]?.map { (bone, vec) -> bone to length * vec } ?: emptyList()
	}
	.groupBy({ it.first }, { it.second })
	.mapValues { it.value.fold(Vector3.NULL) { acc, value -> acc + value } }

fun Map<BodyPart, Vector3>.toSkeletonBoneValues(): Map<SkeletonBone, Float> = this
	.flatMap { (bone, vec) ->
		BODY_PART_VECTOR_TO_SKELETON_BONES[bone]?.map { (cfg, cfgVec) -> cfg to vec.hadamard(cfgVec).len() } ?: emptyList()
	}
	.groupBy({ it.first }, { it.second })
	.mapValues { it.value.sum() }

fun configToSkeletonBoneValues(proportions: Map<String, Float>): Map<SkeletonBone, Float> = proportions.mapKeys {
	SkeletonBone.entries.firstOrNull { cfg -> cfg.name == it.key } ?: SkeletonBone.NONE
}

fun expandProportions(proportions: Map<String, Float>): Map<BodyPart, Vector3> = configToSkeletonBoneValues(proportions).toBodyPartOffsets()
