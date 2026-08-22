package dev.slimevr.skeleton

import io.github.axisangles.ktmath.Vector3
import io.github.axisangles.ktmath.times
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.SkeletonBone
import kotlin.collections.map
import kotlin.collections.plus

// TODO : Placeholder, move this to config defaults somehow
val DEFAULT_PROPORTIONS = mapOf(
	SkeletonBone.HEAD to 0.1f,
	SkeletonBone.NECK to 0.1f,
	SkeletonBone.UPPER_CHEST to 0.16f,
	SkeletonBone.CHEST to 0.16f,
	SkeletonBone.WAIST to 0.18f,
	SkeletonBone.HIP to 0.06f,
	SkeletonBone.HIPS_WIDTH to 0.26f,
	SkeletonBone.UPPER_LEG to 0.42f,
	SkeletonBone.LOWER_LEG to 0.5f,
	SkeletonBone.FOOT_LENGTH to 0.12f,
	SkeletonBone.FOOT_SHIFT to -0.05f,
	SkeletonBone.SHOULDERS_DISTANCE to 0.06f,
	SkeletonBone.SHOULDERS_WIDTH to 0.35f,
	SkeletonBone.UPPER_ARM to 0.26f,
	SkeletonBone.LOWER_ARM to 0.26f,
	SkeletonBone.HAND_Y to 0.08f,
	SkeletonBone.HAND_Z to 0f,
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
private val BONE_VALUE_TO_OFFSETS: Map<SkeletonBone, Map<BodyPart, Vector3>> = mapOf(
	SkeletonBone.HEAD to mapOf(BodyPart.HEAD to Vector3.POS_Z),
	SkeletonBone.NECK to mapOf(BodyPart.NECK to Vector3.NEG_Y),
	SkeletonBone.UPPER_CHEST to mapOf(BodyPart.UPPER_CHEST to Vector3.NEG_Y),
	SkeletonBone.CHEST to mapOf(BodyPart.CHEST to Vector3.NEG_Y),
	SkeletonBone.WAIST to mapOf(BodyPart.WAIST to Vector3.NEG_Y),
	SkeletonBone.HIP to mapOf(BodyPart.HIP to Vector3.NEG_Y),
	SkeletonBone.HIPS_WIDTH to mapOf(BodyPart.LEFT_HIP to Vector3.NEG_X / 2f, BodyPart.RIGHT_HIP to Vector3.POS_X / 2f),
	SkeletonBone.UPPER_LEG to mapOf(BodyPart.LEFT_UPPER_LEG to Vector3.NEG_Y, BodyPart.RIGHT_UPPER_LEG to Vector3.NEG_Y),
	SkeletonBone.LOWER_LEG to mapOf(BodyPart.LEFT_LOWER_LEG to Vector3.NEG_Y, BodyPart.RIGHT_LOWER_LEG to Vector3.NEG_Y),
	SkeletonBone.FOOT_LENGTH to mapOf(BodyPart.LEFT_FOOT to Vector3.NEG_Z, BodyPart.RIGHT_FOOT to Vector3.NEG_Z),
	SkeletonBone.FOOT_SHIFT to mapOf(BodyPart.LEFT_LOWER_LEG to Vector3.NEG_Z, BodyPart.RIGHT_LOWER_LEG to Vector3.NEG_Z),
	SkeletonBone.SHOULDERS_DISTANCE to mapOf(BodyPart.LEFT_SHOULDER to Vector3.NEG_Y, BodyPart.RIGHT_SHOULDER to Vector3.NEG_Y),
	SkeletonBone.SHOULDERS_WIDTH to mapOf(BodyPart.LEFT_SHOULDER to Vector3.NEG_X / 2f, BodyPart.RIGHT_SHOULDER to Vector3.POS_X / 2f),
	SkeletonBone.UPPER_ARM to mapOf(BodyPart.LEFT_UPPER_ARM to Vector3.NEG_Y, BodyPart.RIGHT_UPPER_ARM to Vector3.NEG_Y),
	SkeletonBone.LOWER_ARM to mapOf(BodyPart.LEFT_LOWER_ARM to Vector3.NEG_Y, BodyPart.RIGHT_LOWER_ARM to Vector3.NEG_Y),
	SkeletonBone.HAND_Y to mapOf(BodyPart.LEFT_HAND to Vector3.NEG_Y, BodyPart.RIGHT_HAND to Vector3.NEG_Y),
	SkeletonBone.HAND_Z to mapOf(BodyPart.LEFT_HAND to Vector3.NEG_Z, BodyPart.RIGHT_HAND to Vector3.NEG_Z),
)

private val BONE_OFFSET_TO_VALUES: Map<BodyPart, Map<SkeletonBone, Vector3>> = BONE_VALUE_TO_OFFSETS
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
val SKELETON_BONE_TO_BODY_PARTS: Map<SkeletonBone, Set<BodyPart>> = BONE_VALUE_TO_OFFSETS.mapValues { it.value.keys }

// Sum of default bone lengths for height-contributing bones
// Used to normalize HEIGHT_SCALED_BONE_RATIOS.
val DEFAULT_HEIGHT = DEFAULT_PROPORTIONS.height()

// Per-bone fraction of total standing height, includes spine, legs, and arms, all bones
// whose length scales with user height.
// Non-height bones (HEAD, HIPS_WIDTH) are absent; they keep fixed defaults from DEFAULT_SKELETON_STATE.
private val HEIGHT_SCALED_BONE_RATIOS: Map<SkeletonBone, Float> = (
	HEIGHT_CONTRIBUTING_BONES + setOf(SkeletonBone.UPPER_ARM, SkeletonBone.LOWER_ARM, SkeletonBone.HAND_Y, SkeletonBone.FOOT_LENGTH)
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

fun Map<SkeletonBone, Float>.toBoneOffsets(): BodyPartMap<Vector3> {
	val offsets = bodyPartMap<Vector3>()
	// Normal bones
	for ((cfg, length) in this) {
		val boneOffsets = BONE_VALUE_TO_OFFSETS[cfg] ?: continue
		for ((bone, vec) in boneOffsets) {
			offsets[bone] = (offsets[bone] ?: Vector3.NULL) + length * vec
		}
	}
	// Fingers
	this[SkeletonBone.HAND_Y]?.let { offsets.putAll(getFingerOffsets(it)) }
	// Toes
	this[SkeletonBone.FOOT_LENGTH]?.let { offsets.putAll(getToeOffsets(it)) }
	return offsets
}

fun Map<BodyPart, Vector3>.toBoneValues(): Map<SkeletonBone, Float> = this
	.flatMap { (bone, vec) ->
		BONE_OFFSET_TO_VALUES[bone]?.map { (cfg, cfgVec) -> cfg to vec.hadamard(cfgVec).len() } ?: emptyList()
	}
	.groupBy({ it.first }, { it.second })
	.mapValues { it.value.first() }
// TODO: ?? ^ I don't really know what's going on here, or understand the original intent behind the code,
// but the doubling of proportions was caused by this. It used to be .mapValues { it.value.sum() }, but for `SkeletonBone`s
// that contribute to the offsets of multiple `BodyPart`s (e.g. HIP_WIDTH contributes to LEFT_HIP and RIGHT_HIP,
// UPPER_ARM contributes to LEFT_UPPER_ARM and RIGHT_UPPER_ARM) the offsets are in the values twice.

fun configToBoneValues(proportions: Map<String, Float>): Map<SkeletonBone, Float> = proportions.mapKeys {
	SkeletonBone.entries.firstOrNull { cfg -> cfg.name == it.key } ?: SkeletonBone.NONE
}

/**
 * Returns the offsets for the finger bones scaled from the handLength.
 */
private fun getFingerOffsets(handLength: Float) = (
	iterateBodyPartHierarchy(BodyPart.LEFT_HAND, true) +
		iterateBodyPartHierarchy(BodyPart.RIGHT_HAND, true)
	).map { it.second }.associateWith {
	when (it) {
		BodyPart.LEFT_THUMB_METACARPAL, BodyPart.RIGHT_THUMB_METACARPAL,
		-> {
			val length = handLength * 0.72f * PROXIMAL_RATIO
			Vector3(0f, -length, -length * 0.5f)
		}

		BodyPart.LEFT_THUMB_PROXIMAL, BodyPart.RIGHT_THUMB_PROXIMAL,
		-> {
			val length = handLength * 0.72f * INTERMEDIATE_RATIO
			Vector3(0f, -length, -length * 0.5f)
		}

		BodyPart.LEFT_THUMB_DISTAL, BodyPart.RIGHT_THUMB_DISTAL,
		-> {
			val length = handLength * 0.72f * DISTAL_RATIO
			Vector3(0f, -length, -length * 0.5f)
		}

		BodyPart.LEFT_INDEX_PROXIMAL, BodyPart.RIGHT_INDEX_PROXIMAL,
		-> Vector3(0f, -handLength * 0.805f * PROXIMAL_RATIO, 0f)

		BodyPart.LEFT_INDEX_INTERMEDIATE, BodyPart.RIGHT_INDEX_INTERMEDIATE,
		-> Vector3(0f, -handLength * 0.805f * INTERMEDIATE_RATIO, 0f)

		BodyPart.LEFT_INDEX_DISTAL, BodyPart.RIGHT_INDEX_DISTAL,
		-> Vector3(0f, -handLength * 0.805f * DISTAL_RATIO, 0f)

		BodyPart.LEFT_MIDDLE_PROXIMAL, BodyPart.RIGHT_MIDDLE_PROXIMAL,
		-> Vector3(0f, -handLength * 0.92f * PROXIMAL_RATIO, 0f)

		BodyPart.LEFT_MIDDLE_INTERMEDIATE, BodyPart.RIGHT_MIDDLE_INTERMEDIATE,
		-> Vector3(0f, -handLength * 0.92f * INTERMEDIATE_RATIO, 0f)

		BodyPart.LEFT_MIDDLE_DISTAL, BodyPart.RIGHT_MIDDLE_DISTAL,
		-> Vector3(0f, -handLength * 0.92f * DISTAL_RATIO, 0f)

		BodyPart.LEFT_RING_PROXIMAL, BodyPart.RIGHT_RING_PROXIMAL,
		-> Vector3(0f, -handLength * 0.805f * PROXIMAL_RATIO, 0f)

		BodyPart.LEFT_RING_INTERMEDIATE, BodyPart.RIGHT_RING_INTERMEDIATE,
		-> Vector3(0f, -handLength * 0.805f * INTERMEDIATE_RATIO, 0f)

		BodyPart.LEFT_RING_DISTAL, BodyPart.RIGHT_RING_DISTAL,
		-> Vector3(0f, -handLength * 0.805f * DISTAL_RATIO, 0f)

		BodyPart.LEFT_LITTLE_PROXIMAL, BodyPart.RIGHT_LITTLE_PROXIMAL,
		-> Vector3(0f, -handLength * 0.69f * PROXIMAL_RATIO, 0f)

		BodyPart.LEFT_LITTLE_INTERMEDIATE, BodyPart.RIGHT_LITTLE_INTERMEDIATE,
		-> Vector3(0f, -handLength * 0.69f * INTERMEDIATE_RATIO, 0f)

		BodyPart.LEFT_LITTLE_DISTAL, BodyPart.RIGHT_LITTLE_DISTAL,
		-> Vector3(0f, -handLength * 0.69f * DISTAL_RATIO, 0f)

		else -> error("$it is not expected as child of hands.")
	}
}

// Ratios for finger lengths. They should sum up to 1.
private const val PROXIMAL_RATIO = 0.5f
private const val INTERMEDIATE_RATIO = 0.283f
private const val DISTAL_RATIO = 0.217f

/**
 * Returns the offsets for the toe bones scaled from the handLength.
 */
private fun getToeOffsets(footLength: Float) = (
	iterateBodyPartHierarchy(BodyPart.LEFT_FOOT, true) +
		iterateBodyPartHierarchy(BodyPart.RIGHT_FOOT, true)
	).map { it.second }.associateWith {
		Vector3(0f, 0f, -footLength * 0.2f)
	}
