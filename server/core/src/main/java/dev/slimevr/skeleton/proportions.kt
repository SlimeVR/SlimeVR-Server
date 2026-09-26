package dev.slimevr.skeleton

import dev.slimevr.vrchat.EYE_HEIGHT_TO_HEIGHT_RATIO
import io.github.axisangles.ktmath.Vector3
import io.github.axisangles.ktmath.times
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.SkeletonBone
import kotlin.collections.map
import kotlin.collections.plus

data class BoneSpec(val default: Float, val min: Float, val max: Float, val curve: List<Pair<Float, Float>>)

private val LINEAR_WITH_HEIGHT = listOf(1f to 1f)

// The curves below are generated from two public surveys:
//  - Snyder, Schneider, Owings, Reynolds, Golomb and Schork, "Anthropometry of Infants, Children, and
//    Youths to Age 18 for Product Safety Design" (University of Michigan Highway Safety Research
//    Institute, 1977; US Consumer Product Safety Commission). 3,900 people from 0.7 to 2.1 m.
//  - NHANES 2015-2016 and 2017-2018 body measurements (US National Center for Health Statistics, public
//    domain). 18,000 people aged 2 and up. It only measures the upper arm and upper leg here.
//
// Each is a measurement divided by stature, averaged over people within 0.1 m of the stature given, then
// divided by the same average for people 1.6 to 2.1 m tall. Where both surveys measure a bone the two
// curves are averaged. Flat at 1 from where it reaches adult values. Below 0.9 m the sample is small.

// Upper leg length (Snyder: trochanteric minus tibiale height; NHANES: upper leg length). NHANES has
// no one under 1.2 m.
private val UPPER_LEG_LENGTH_BY_STATURE = listOf(
	0.9f to 0.864f,
	1.0f to 0.889f,
	1.1f to 0.927f,
	1.2f to 0.964f,
	1.3f to 0.980f,
	1.4f to 0.990f,
	1.5f to 0.990f,
	1.6f to 1.000f,
)

// Lower leg length (Snyder only: tibiale minus sphyrion height).
private val LOWER_LEG_LENGTH_BY_STATURE = listOf(
	0.9f to 0.872f,
	1.0f to 0.893f,
	1.1f to 0.922f,
	1.2f to 0.953f,
	1.3f to 0.983f,
	1.4f to 1.000f,
)

// Upper arm length (Snyder: acromion to radiale; NHANES: upper arm length).
private val UPPER_ARM_LENGTH_BY_STATURE = listOf(
	0.8f to 0.917f,
	0.9f to 0.929f,
	1.0f to 0.933f,
	1.1f to 0.938f,
	1.2f to 0.947f,
	1.3f to 0.960f,
	1.4f to 0.979f,
	1.5f to 1.000f,
)

// Forearm length (Snyder only: radiale to stylion).
private val LOWER_ARM_LENGTH_BY_STATURE = listOf(
	0.9f to 0.950f,
	1.0f to 0.961f,
	1.1f to 0.971f,
	1.2f to 0.970f,
	1.3f to 0.975f,
	1.4f to 0.982f,
	1.5f to 0.986f,
	1.6f to 1.000f,
)

// Hand length (Snyder only).
private val HAND_LENGTH_BY_STATURE = listOf(
	0.8f to 1.045f,
	0.9f to 1.046f,
	1.0f to 1.041f,
	1.1f to 1.031f,
	1.2f to 1.019f,
	1.3f to 1.011f,
	1.4f to 1.012f,
	1.5f to 1.010f,
	1.6f to 1.000f,
)

// Foot length (Snyder only). Children under 1.5 m sit a flat 4 to 5% above adults, then it comes down.
private val FOOT_LENGTH_BY_STATURE = listOf(
	0.8f to 1.039f,
	0.9f to 1.049f,
	1.0f to 1.048f,
	1.1f to 1.041f,
	1.2f to 1.039f,
	1.3f to 1.040f,
	1.4f to 1.039f,
	1.5f to 1.026f,
	1.6f to 1.000f,
)

// Shoulder width (Snyder only: biacromial breadth).
private val SHOULDERS_WIDTH_BY_STATURE = listOf(
	0.9f to 1.053f,
	1.0f to 1.043f,
	1.1f to 1.031f,
	1.2f to 1.019f,
	1.3f to 1.000f,
)

// TODO : Placeholder, move the defaults to config defaults somehow
val BONE_SPECS: Map<SkeletonBone, BoneSpec> = mapOf(
	SkeletonBone.NECK to BoneSpec(default = 0.1f, min = 0.01f, max = 0.3f, curve = LINEAR_WITH_HEIGHT),
	SkeletonBone.UPPER_CHEST to BoneSpec(default = 0.16f, min = 0.01f, max = 0.5f, curve = LINEAR_WITH_HEIGHT),
	SkeletonBone.LOWER_CHEST to BoneSpec(default = 0.16f, min = 0.01f, max = 0.5f, curve = LINEAR_WITH_HEIGHT),
	SkeletonBone.UPPER_WAIST to BoneSpec(default = 0.1f, min = 0.01f, max = 0.5f, curve = LINEAR_WITH_HEIGHT),
	SkeletonBone.LOWER_WAIST to BoneSpec(default = 0.1f, min = 0.01f, max = 0.5f, curve = LINEAR_WITH_HEIGHT),
	SkeletonBone.HIP to BoneSpec(default = 0.04f, min = 0.01f, max = 0.3f, curve = LINEAR_WITH_HEIGHT),
	SkeletonBone.HIPS_WIDTH to BoneSpec(default = 0.26f, min = 0.01f, max = 0.6f, curve = LINEAR_WITH_HEIGHT),
	SkeletonBone.UPPER_LEG to BoneSpec(default = 0.42f, min = 0.01f, max = 0.8f, curve = UPPER_LEG_LENGTH_BY_STATURE),
	SkeletonBone.LOWER_LEG to BoneSpec(default = 0.5f, min = 0.01f, max = 0.8f, curve = LOWER_LEG_LENGTH_BY_STATURE),
	SkeletonBone.FOOT_LENGTH to BoneSpec(default = 0.13f, min = 0.01f, max = 0.4f, curve = FOOT_LENGTH_BY_STATURE),
	SkeletonBone.FOOT_SHIFT to BoneSpec(default = -0.05f, min = -0.5f, max = 0.5f, curve = LINEAR_WITH_HEIGHT),
	SkeletonBone.SHOULDERS_DISTANCE to BoneSpec(default = 0.06f, min = 0.01f, max = 0.3f, curve = LINEAR_WITH_HEIGHT),
	SkeletonBone.SHOULDERS_WIDTH to BoneSpec(default = 0.35f, min = 0.01f, max = 0.8f, curve = SHOULDERS_WIDTH_BY_STATURE),
	SkeletonBone.UPPER_ARM to BoneSpec(default = 0.26f, min = 0.01f, max = 0.6f, curve = UPPER_ARM_LENGTH_BY_STATURE),
	SkeletonBone.LOWER_ARM to BoneSpec(default = 0.26f, min = 0.01f, max = 0.6f, curve = LOWER_ARM_LENGTH_BY_STATURE),
	SkeletonBone.HAND to BoneSpec(default = 0.08f, min = 0.01f, max = 0.3f, curve = HAND_LENGTH_BY_STATURE),
)

val DEFAULT_PROPORTIONS: Map<SkeletonBone, Float> = BONE_SPECS.mapValues { (_, spec) -> spec.default }

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
	SkeletonBone.HAND to BodyPartMap(mapOf(BodyPart.LEFT_HAND to Vector3.NEG_Y, BodyPart.RIGHT_HAND to Vector3.NEG_Y)),
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

// Per-bone fraction of total standing height: the spine, legs and arms, the hand and foot, and the
// widths and offsets between them (hips, shoulders, foot shift), so a short user is narrow as well as short.
private val HEIGHT_SCALED_BONE_RATIOS: Map<SkeletonBone, Float> = (
	HEIGHT_CONTRIBUTING_BONES +
		setOf(
			SkeletonBone.UPPER_ARM,
			SkeletonBone.LOWER_ARM,
			SkeletonBone.HAND,
			SkeletonBone.FOOT_LENGTH,
			SkeletonBone.FOOT_SHIFT,
			SkeletonBone.HIPS_WIDTH,
			SkeletonBone.SHOULDERS_DISTANCE,
			SkeletonBone.SHOULDERS_WIDTH,
		)
	).associateWith { (DEFAULT_PROPORTIONS[it] ?: 0f) / DEFAULT_HEIGHT }

// Sums the HEIGHT_CONTRIBUTING_BONES lengths to derive standing height.
fun Map<SkeletonBone, Float>.height(): Float = HEIGHT_CONTRIBUTING_BONES.sumOf { bone ->
	this[bone]?.toDouble() ?: 0.0
}.toFloat()

private fun interpolate(points: List<Pair<Float, Float>>, x: Float): Float {
	val (firstX, firstY) = points.first()
	if (x <= firstX) return firstY
	val (lastX, lastY) = points.last()
	if (x >= lastX) return lastY
	val (x0, y0) = points.last { it.first <= x }
	val (x1, y1) = points.first { it.first > x }
	return y0 + (y1 - y0) * (x - x0) / (x1 - x0)
}

// Returns proportions keyed by SkeletonBone.name for config storage.
// Only height-scaled bones are included.
fun computeDefaultProportionsByBone(height: Float): Map<String, Float> {
	val stature = height / EYE_HEIGHT_TO_HEIGHT_RATIO
	val weights = HEIGHT_SCALED_BONE_RATIOS.mapValues { (bone, ratio) -> ratio * interpolate(BONE_SPECS.getValue(bone).curve, stature) }
	// The height bones still sum to the height, so those without a curve take the length the others give up.
	val heightBonesWeight = HEIGHT_CONTRIBUTING_BONES.sumOf { weights.getValue(it).toDouble() }.toFloat()
	return weights.map { (bone, weight) ->
		bone.name to height * if (bone in HEIGHT_CONTRIBUTING_BONES) weight / heightBonesWeight else weight
	}.toMap()
}

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

	// Set offsets (lengths) and head offsets
	tail[BodyPart.HEAD] = Vector3.ZERO // Head has no length
	for ((cfg, length) in lengths) {
		BONE_VALUE_TO_OFFSETS[cfg]?.let { for ((bone, vec) in it) tail[bone] = (tail[bone] ?: Vector3.ZERO) + length * vec }
		BONE_VALUE_TO_HEAD_OFFSETS[cfg]?.let { for ((bone, vec) in it) head[bone] = (head[bone] ?: Vector3.ZERO) + length * vec }
	}
	lengths[SkeletonBone.HAND]?.let {
		tail.putAll(getFingerOffsets(it))
		head.putAll(getFingerHeadOffsets(it))
	}
	lengths[SkeletonBone.FOOT_LENGTH]?.let {
		tail.putAll(getToeOffsets(it))
		head.putAll(getToeHeadOffsets(it))
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

fun clampBoneValue(bone: SkeletonBone, value: Float): Float = BONE_SPECS.getValue(bone).let { value.coerceIn(it.min, it.max) }

fun configToBoneValues(proportions: Map<String, Float>): Map<SkeletonBone, Float> = proportions
	.mapNotNull { (name, value) ->
		val bone = BONE_SPECS.keys.firstOrNull { it.name == name } ?: return@mapNotNull null
		bone to clampBoneValue(bone, value)
	}
	.toMap()

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
		knuckle = Vector3(0.16f, 0.6f, -0.4f),
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
		headOffset = Vector3(0.23f, 0f, 0f),
	),
	Toe(
		BodyPart.LEFT_INDEX_TOE to BodyPart.RIGHT_INDEX_TOE,
		lengthFraction = 0.24f,
		headOffset = Vector3(0.106f, 0f, 0f),
	),
	Toe(
		BodyPart.LEFT_MIDDLE_TOE to BodyPart.RIGHT_MIDDLE_TOE,
		lengthFraction = 0.23f,
		headOffset = Vector3(-0.01f, 0f, 0f),
	),
	Toe(
		BodyPart.LEFT_RING_TOE to BodyPart.RIGHT_RING_TOE,
		lengthFraction = 0.21f,
		headOffset = Vector3(-0.122f, 0f, 0f),
	),
	Toe(
		BodyPart.LEFT_LITTLE_TOE to BodyPart.RIGHT_LITTLE_TOE,
		lengthFraction = 0.18f,
		headOffset = Vector3(-0.23f, 0f, 0f),
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
