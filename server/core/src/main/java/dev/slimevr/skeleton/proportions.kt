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
	SkeletonBone.LOWER_CHEST to 0.16f,
	SkeletonBone.UPPER_WAIST to 0.1f,
	SkeletonBone.LOWER_WAIST to 0.1f,
	SkeletonBone.HIP to 0.04f,
	SkeletonBone.HIPS_WIDTH to 0.26f,
	SkeletonBone.UPPER_LEG to 0.42f,
	SkeletonBone.LOWER_LEG to 0.5f,
	SkeletonBone.FOOT_LENGTH to 0.13f,
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
	SkeletonBone.LOWER_CHEST,
	SkeletonBone.UPPER_WAIST,
	SkeletonBone.LOWER_WAIST,
	SkeletonBone.HIP,
	SkeletonBone.UPPER_LEG,
	SkeletonBone.LOWER_LEG,
)

// Maps each SolarXR SkeletonBone to the BodyPart(s) it controls in the skeleton with vectors for offset directions.
private val BONE_VALUE_TO_OFFSETS: Map<SkeletonBone, BodyPartMap<Vector3>> = mapOf(
	SkeletonBone.HEAD to BodyPartMap(mapOf(BodyPart.HEAD to Vector3.POS_Z)),
	SkeletonBone.NECK to BodyPartMap(mapOf(BodyPart.NECK to Vector3.NEG_Y)),
	SkeletonBone.UPPER_CHEST to BodyPartMap(mapOf(BodyPart.UPPER_CHEST to Vector3.NEG_Y)),
	SkeletonBone.LOWER_CHEST to BodyPartMap(mapOf(BodyPart.LOWER_CHEST to Vector3.NEG_Y)),
	SkeletonBone.UPPER_WAIST to BodyPartMap(mapOf(BodyPart.UPPER_WAIST to Vector3.NEG_Y)),
	SkeletonBone.LOWER_WAIST to BodyPartMap(mapOf(BodyPart.LOWER_WAIST to Vector3.NEG_Y)),
	SkeletonBone.HIP to BodyPartMap(mapOf(BodyPart.HIP to Vector3.NEG_Y)),
	SkeletonBone.UPPER_LEG to BodyPartMap(mapOf(BodyPart.LEFT_UPPER_LEG to Vector3.NEG_Y, BodyPart.RIGHT_UPPER_LEG to Vector3.NEG_Y)),
	SkeletonBone.LOWER_LEG to BodyPartMap(mapOf(BodyPart.LEFT_LOWER_LEG to Vector3.NEG_Y, BodyPart.RIGHT_LOWER_LEG to Vector3.NEG_Y)),
	SkeletonBone.FOOT_LENGTH to BodyPartMap(mapOf(BodyPart.LEFT_FOOT to Vector3.NEG_Z, BodyPart.RIGHT_FOOT to Vector3.NEG_Z)),
	SkeletonBone.FOOT_SHIFT to BodyPartMap(mapOf(BodyPart.LEFT_LOWER_LEG to Vector3.NEG_Z, BodyPart.RIGHT_LOWER_LEG to Vector3.NEG_Z)),
	SkeletonBone.SHOULDERS_DISTANCE to BodyPartMap(mapOf(BodyPart.LEFT_SHOULDER to Vector3.NEG_Y, BodyPart.RIGHT_SHOULDER to Vector3.NEG_Y)),
	SkeletonBone.SHOULDERS_WIDTH to BodyPartMap(mapOf(BodyPart.LEFT_SHOULDER to Vector3.NEG_X / 2f, BodyPart.RIGHT_SHOULDER to Vector3.POS_X / 2f)),
	SkeletonBone.UPPER_ARM to BodyPartMap(mapOf(BodyPart.LEFT_UPPER_ARM to Vector3.NEG_Y, BodyPart.RIGHT_UPPER_ARM to Vector3.NEG_Y)),
	SkeletonBone.LOWER_ARM to BodyPartMap(mapOf(BodyPart.LEFT_LOWER_ARM to Vector3.NEG_Y, BodyPart.RIGHT_LOWER_ARM to Vector3.NEG_Y)),
	SkeletonBone.HAND_Y to BodyPartMap(mapOf(BodyPart.LEFT_HAND to Vector3.NEG_Y, BodyPart.RIGHT_HAND to Vector3.NEG_Y)),
	SkeletonBone.HAND_Z to BodyPartMap(mapOf(BodyPart.LEFT_HAND to Vector3.NEG_Z, BodyPart.RIGHT_HAND to Vector3.NEG_Z)),
)

private val BONE_VALUE_TO_HEAD_OFFSETS: Map<SkeletonBone, BodyPartMap<Vector3>> = mapOf(
	SkeletonBone.HIPS_WIDTH to BodyPartMap(mapOf(BodyPart.LEFT_UPPER_LEG to Vector3.NEG_X / 2f, BodyPart.RIGHT_UPPER_LEG to Vector3.POS_X / 2f)),
)

// Inverts an offset table so a resolved offset vector can be turned back into a SkeletonBone value.
// Each direction is divided by its squared length so a dot product with the resolved offset
// recovers the signed scalar.
private fun invertOffsetTable(table: Map<SkeletonBone, BodyPartMap<Vector3>>): BodyPartMap<Map<SkeletonBone, Vector3>> = BodyPartMap(
	table
		.flatMap { (cfg, bones) -> bones.map { (bone, vec) -> bone to (cfg to vec / vec.lenSq()) } }
		.groupBy({ it.first }, { it.second })
		.mapValues { it.value.toMap() },
)

private val BONE_OFFSET_TO_VALUES: BodyPartMap<Map<SkeletonBone, Vector3>> = invertOffsetTable(BONE_VALUE_TO_OFFSETS)
private val BONE_HEAD_OFFSET_TO_VALUES: BodyPartMap<Map<SkeletonBone, Vector3>> = invertOffsetTable(BONE_VALUE_TO_HEAD_OFFSETS)

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

// Resolved bone geometry for a set of proportions. tail is the head->tail vector in the bone's own
// frame; head is the parent-tail->head vector in the parent's frame (Vector3.ZERO for most bones).
data class BoneOffsets(
	val tail: BodyPartMap<Vector3>,
	val head: BodyPartMap<Vector3>,
)

fun toBoneOffsets(lengths: Map<SkeletonBone, Float>): BoneOffsets {
	val tail = bodyPartMap<Vector3>()
	val head = bodyPartMap<Vector3>()
	for ((cfg, length) in lengths) {
		BONE_VALUE_TO_OFFSETS[cfg]?.let { for ((bone, vec) in it) tail[bone] = (tail[bone] ?: Vector3.ZERO) + length * vec }
		BONE_VALUE_TO_HEAD_OFFSETS[cfg]?.let { for ((bone, vec) in it) head[bone] = (head[bone] ?: Vector3.ZERO) + length * vec }
	}
	lengths[SkeletonBone.HAND_Y]?.let {
		tail.putAll(getFingerOffsets(it))
		head.putAll(getFingerHeadOffsets(it))
	}
	lengths[SkeletonBone.FOOT_LENGTH]?.let {
		tail.putAll(getToeOffsets(it))
		head.putAll(getToeHeadOffsets(it))
	}
	lengths[SkeletonBone.UPPER_CHEST]?.let {
		tail.putAll(getBustOffsets(it))
		head.putAll(getBustHeadOffsets(it))
	}
	return BoneOffsets(tail, head)
}

fun toBoneValues(tailOffsets: BodyPartMap<Vector3>, headOffsets: BodyPartMap<Vector3>): Map<SkeletonBone, Float> {
	fun invert(offsets: BodyPartMap<Vector3>, table: BodyPartMap<Map<SkeletonBone, Vector3>>) = offsets.flatMap { (bone, vec) ->
		table[bone]?.map { (cfg, cfgVec) -> cfg to vec.dot(cfgVec) } ?: emptyList()
	}

	return (invert(tailOffsets, BONE_OFFSET_TO_VALUES) + invert(headOffsets, BONE_HEAD_OFFSET_TO_VALUES))
		.groupBy({ it.first }, { it.second })
		.mapValues { it.value.first() }
}

fun configToBoneValues(proportions: Map<String, Float>): Map<SkeletonBone, Float> = proportions.mapKeys {
	SkeletonBone.entries.firstOrNull { cfg -> cfg.name == it.key } ?: SkeletonBone.NONE
}

// Fraction of a finger's length taken by each phalanx, hand-ward to tip-ward. Sums to 1.
private val PHALANX_RATIOS = floatArrayOf(0.5f, 0.283f, 0.217f)

/**
 * A finger in the hand's rest frame. The hand hangs with the palm toward the thigh, so for the left
 * hand +X points to the palm (medial), -Y toward the fingertips, -Z forward; the right hand mirrors X.
 * The knuckles fan front-to-back: the index sits forward near the thumb, the little finger back.
 *
 * @param segments the three bones hand-ward to tip-ward
 * @param lengthFraction the whole finger's length as a fraction of handLength
 * @param knuckle the first bone's head vs the hand's tail, fractions of handLength
 * @param lean how the finger drifts as it extends, per unit of segment length (the -Y fall is on top)
 */
private class Finger(
	val segments: List<Pair<BodyPart, BodyPart>>,
	val lengthFraction: Float,
	val knuckle: Vector3,
	val lean: Vector3,
)

private val FINGERS = listOf(
	Finger(
		listOf(
			BodyPart.LEFT_THUMB_METACARPAL to BodyPart.RIGHT_THUMB_METACARPAL,
			BodyPart.LEFT_THUMB_PROXIMAL to BodyPart.RIGHT_THUMB_PROXIMAL,
			BodyPart.LEFT_THUMB_DISTAL to BodyPart.RIGHT_THUMB_DISTAL,
		),
		lengthFraction = 0.72f,
		knuckle = Vector3(0.16f, 0.3f, -0.28f),
		lean = Vector3(0.05f, 0f, -0.7f),
	),
	Finger(
		listOf(
			BodyPart.LEFT_INDEX_PROXIMAL to BodyPart.RIGHT_INDEX_PROXIMAL,
			BodyPart.LEFT_INDEX_INTERMEDIATE to BodyPart.RIGHT_INDEX_INTERMEDIATE,
			BodyPart.LEFT_INDEX_DISTAL to BodyPart.RIGHT_INDEX_DISTAL,
		),
		lengthFraction = 0.805f,
		knuckle = Vector3(0.03f, 0.05f, -0.34f),
		lean = Vector3.ZERO,
	),
	Finger(
		listOf(
			BodyPart.LEFT_MIDDLE_PROXIMAL to BodyPart.RIGHT_MIDDLE_PROXIMAL,
			BodyPart.LEFT_MIDDLE_INTERMEDIATE to BodyPart.RIGHT_MIDDLE_INTERMEDIATE,
			BodyPart.LEFT_MIDDLE_DISTAL to BodyPart.RIGHT_MIDDLE_DISTAL,
		),
		lengthFraction = 0.92f,
		knuckle = Vector3(0.04f, 0f, -0.11f),
		lean = Vector3.ZERO,
	),
	Finger(
		listOf(
			BodyPart.LEFT_RING_PROXIMAL to BodyPart.RIGHT_RING_PROXIMAL,
			BodyPart.LEFT_RING_INTERMEDIATE to BodyPart.RIGHT_RING_INTERMEDIATE,
			BodyPart.LEFT_RING_DISTAL to BodyPart.RIGHT_RING_DISTAL,
		),
		lengthFraction = 0.805f,
		knuckle = Vector3(0.03f, 0.03f, 0.11f),
		lean = Vector3.ZERO,
	),
	Finger(
		listOf(
			BodyPart.LEFT_LITTLE_PROXIMAL to BodyPart.RIGHT_LITTLE_PROXIMAL,
			BodyPart.LEFT_LITTLE_INTERMEDIATE to BodyPart.RIGHT_LITTLE_INTERMEDIATE,
			BodyPart.LEFT_LITTLE_DISTAL to BodyPart.RIGHT_LITTLE_DISTAL,
		),
		lengthFraction = 0.69f,
		knuckle = Vector3(0f, 0.1f, 0.31f),
		lean = Vector3.ZERO,
	),
)

/**
 * head to tail vectors for every finger bone, scaled from handLength. The three phalanges of a finger
 * share a direction and split the finger's length by [PHALANX_RATIOS].
 */
private fun getFingerOffsets(handLength: Float): Map<BodyPart, Vector3> = buildMap {
	for (finger in FINGERS) {
		val fingerLength = handLength * finger.lengthFraction
		val dir = Vector3(finger.lean.x, -1f + finger.lean.y, finger.lean.z).unit()
		finger.segments.forEachIndexed { i, (left, right) ->
			val segment = fingerLength * PHALANX_RATIOS[i]
			put(left, dir * segment)
			put(right, Vector3(-dir.x, dir.y, dir.z) * segment)
		}
	}
}

/**
 * head offset for each finger's first bone, fanning the knuckles across the palm.
 * The other bones chain tail-to-head and are absent here.
 */
private fun getFingerHeadOffsets(handLength: Float): Map<BodyPart, Vector3> = buildMap {
	for (finger in FINGERS) {
		val (left, right) = finger.segments.first()
		val k = finger.knuckle
		put(left, Vector3(k.x * handLength, k.y * handLength, k.z * handLength))
		put(right, Vector3(-k.x * handLength, k.y * handLength, k.z * handLength))
	}
}

private class Toe(
	val segments: Pair<BodyPart, BodyPart>,
	val lengthFraction: Float,
	val headOffset: Vector3,
)

private val TOES = listOf(
	Toe(
		BodyPart.LEFT_BIG_TOE to BodyPart.RIGHT_BIG_TOE,
		lengthFraction = 0.27f,
		headOffset = Vector3(0.28f, 0f, 0f),
	),
	Toe(
		BodyPart.LEFT_INDEX_TOE to BodyPart.RIGHT_INDEX_TOE,
		lengthFraction = 0.24f,
		headOffset = Vector3(0.12f, 0f, 0f),
	),
	Toe(
		BodyPart.LEFT_MIDDLE_TOE to BodyPart.RIGHT_MIDDLE_TOE,
		lengthFraction = 0.23f,
		headOffset = Vector3(0f, 0f, 0f),
	),
	Toe(
		BodyPart.LEFT_RING_TOE to BodyPart.RIGHT_RING_TOE,
		lengthFraction = 0.21f,
		headOffset = Vector3(-0.12f, 0f, 0f),
	),
	Toe(
		BodyPart.LEFT_LITTLE_TOE to BodyPart.RIGHT_LITTLE_TOE,
		lengthFraction = 0.18f,
		headOffset = Vector3(-0.26f, 0f, 0f),
	),
)

/**
 * Returns the offsets for the toe bones scaled from the footLength.
 */
private fun getToeOffsets(footLength: Float) = buildMap {
	for (toe in TOES) {
		val toeLength = footLength * toe.lengthFraction
		put(toe.segments.first, Vector3(0f, 0f, -toeLength))
		put(toe.segments.second, Vector3(0f, 0f, -toeLength))
	}
}

// Head offsets spread the toe roots across the forefoot. X is the foot's medial-lateral axis in
// foot-local space, positive toward the big toe of a left foot. Values are fractions of footLength.
private fun getToeHeadOffsets(footLength: Float): Map<BodyPart, Vector3> = buildMap {
	for (toe in TOES) {
		val k = toe.headOffset
		put(toe.segments.first, Vector3(k.x * footLength, k.y * footLength, k.z * footLength))
		put(toe.segments.second, Vector3(-k.x * footLength, k.y * footLength, k.z * footLength))
	}
}


private class Bust(
	val segments: Pair<BodyPart, BodyPart>,
	val lengthFraction: Float,
	val headOffset: Vector3,
)

private val BUST = listOf(
	Bust(
		BodyPart.LEFT_BUST to BodyPart.RIGHT_BUST,
		lengthFraction = 0.27f,
		headOffset = Vector3(0.28f, 0f, 0f),
	),
)


/**
 * Returns the offsets for the bust bones scaled from the chest.
 */
private fun getBustOffsets(bustLength: Float) = buildMap {
	for (bust in BUST) {
		val bustLength = bustLength * bust.lengthFraction
		put(bust.segments.first, Vector3(0f, 0f, -bustLength * 0.2f))
		put(bust.segments.second, Vector3(0f, 0f, -bustLength * 0.2f))
	}
}

// Head offsets spread the bust roots across the chest. X is the chest's medial-lateral axis in
// bust-local space, positive toward the left bust. Values are fractions of bustLength.
private fun getBustHeadOffsets(bustLength: Float): Map<BodyPart, Vector3> = buildMap {
	for (bust in BUST) {
		val k = bust.headOffset
		put(bust.segments.first, Vector3(k.x * -bustLength * 0.12f, k.y * bustLength, k.z * bustLength))
		put(bust.segments.second, Vector3(-k.x * -bustLength * 0.12f, k.y * bustLength, k.z * bustLength))
	}
}
