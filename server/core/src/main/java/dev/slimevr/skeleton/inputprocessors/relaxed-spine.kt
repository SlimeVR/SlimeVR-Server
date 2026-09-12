package dev.slimevr.skeleton.inputprocessors

import com.jme3.math.FastMath
import dev.slimevr.config.Settings
import dev.slimevr.skeleton.BodyPartMap
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import io.github.axisangles.ktmath.Quaternion
import solarxr_protocol.datatypes.BodyPart
import kotlin.enums.enumEntries

// At this default value, the user's spine should behave as we meant it to.
const val DEFAULT_SPINE_UPPER_LOWER = 0.5f

private enum class SpineSource(val parts: Array<BodyPart>) {
	UPPER_CHEST(arrayOf(BodyPart.UPPER_CHEST)),
	LOWER_CHEST(arrayOf(BodyPart.LOWER_CHEST)),
	UPPER_WAIST(arrayOf(BodyPart.UPPER_WAIST)),
	LOWER_WAIST(arrayOf(BodyPart.LOWER_WAIST)),
	HIP(arrayOf(BodyPart.HIP)),
	UPPER_LEGS(arrayOf(BodyPart.LEFT_UPPER_LEG, BodyPart.RIGHT_UPPER_LEG)),
}

private val SPINE_SOURCES = enumEntries<SpineSource>().toTypedArray()
private val NB_SPINE_SOURCES = SPINE_SOURCES.count()

/** Ordered from parent to child */
private val SPINE_BONES = arrayOf(
	SpineSource.UPPER_CHEST,
	SpineSource.LOWER_CHEST,
	SpineSource.UPPER_WAIST,
	SpineSource.LOWER_WAIST,
	SpineSource.HIP,
)

// The higher a value is, the more reliable that source is.
// If a "To" is 2x the "From", it'll use 100% "To".
// If a "To" is the same as "From", it'll do 50-50.
// A negative value will go towards the opposite rotation.
// A From and To may be taken from different roots.
// TODO: Fine tune
private val SPINE_SOURCE_RELIABILITY = mapOf(
	SpineSource.UPPER_CHEST to mapOf(
		SpineSource.UPPER_CHEST to -6f, // Itself
		// To
		SpineSource.LOWER_CHEST to 1f,
		SpineSource.UPPER_WAIST to 1.5f,
		SpineSource.LOWER_WAIST to 2f,
		SpineSource.HIP to 4f,
		SpineSource.UPPER_LEGS to 3f,
	),
	SpineSource.LOWER_CHEST to mapOf(
		// From
		SpineSource.UPPER_CHEST to -10f,
		SpineSource.LOWER_CHEST to -11f, // Itself
		// To
		SpineSource.UPPER_WAIST to 1.5f,
		SpineSource.LOWER_WAIST to 2f,
		SpineSource.HIP to 4f,
		SpineSource.UPPER_LEGS to 3f,
	),
	SpineSource.UPPER_WAIST to mapOf(
		// From
		SpineSource.UPPER_CHEST to 10f,
		SpineSource.LOWER_CHEST to 10f,
		SpineSource.UPPER_WAIST to 68f, // Itself
		// To
		SpineSource.LOWER_WAIST to 17f,
		SpineSource.HIP to 5.5f,
		SpineSource.UPPER_LEGS to 4.5f,
	),
	SpineSource.LOWER_WAIST to mapOf(
		// From
		SpineSource.UPPER_CHEST to 6f,
		SpineSource.LOWER_CHEST to 6f,
		SpineSource.UPPER_WAIST to 6.25f,
		SpineSource.LOWER_WAIST to 44f, // Itself
		// To
		SpineSource.HIP to 10f,
		SpineSource.UPPER_LEGS to 4.25f,
	),
	SpineSource.HIP to mapOf(
		// From
		SpineSource.UPPER_CHEST to 9.5f,
		SpineSource.LOWER_CHEST to 9.5f,
		SpineSource.UPPER_WAIST to 12.5f,
		SpineSource.LOWER_WAIST to 13f,
		SpineSource.HIP to 40f, // Itself
		// To
		SpineSource.UPPER_LEGS to 13f,
	),
)

private fun nearestActive(startIndex: Int, step: Int, sourceActive: Map<SpineSource, Boolean>): Int? {
	var i = startIndex
	while (i in SPINE_SOURCES.indices) {
		if (sourceActive[SPINE_SOURCES[i]] == true) return i
		i += step
	}
	return null
}

private fun getFromTo(selfIndex: Int, sourceActive: Map<SpineSource, Boolean>): Pair<Int?, Int?> {
	// Find From, the first active bone in itself + its parents.
	val from = nearestActive(selfIndex, -1, sourceActive)

	// Find To, the next active bone in its children, skipping the immediate next if itself is active except if it's the only one (for hip).
	// If To is null, we return null for the whole thing
	val isActive = sourceActive[SPINE_SOURCES[selfIndex]] == true
	val immediateNext = selfIndex + 1
	val takeImmediateNext = (!isActive || selfIndex + 2 == NB_SPINE_SOURCES) && sourceActive[SPINE_SOURCES[immediateNext]] == true
	val to = if (takeImmediateNext) immediateNext else nearestActive(selfIndex + 2, 1, sourceActive)

	return from to to
}

/**
 * Remaps the From-To ratio with From-To reliability.
 */
private fun remapRatioWithReliability(fromUpperToLower: Float, reliability: Float): Float = if (fromUpperToLower <= DEFAULT_SPINE_UPPER_LOWER) {
	fromUpperToLower * (1f / DEFAULT_SPINE_UPPER_LOWER) * reliability
} else {
	reliability + (fromUpperToLower - DEFAULT_SPINE_UPPER_LOWER) * (1f / DEFAULT_SPINE_UPPER_LOWER) * (1f - reliability)
}

private fun interpolateRatio(fromUpperToLower: Float, curvature: Float, fromReliability: Float, toReliability: Float, isActive: Boolean): Float {
	val reliability = (toReliability / fromReliability) * DEFAULT_SPINE_UPPER_LOWER

	return if (isActive) {
		// Use a reliability-adjusted curvature directly if relaxing an active bone
		reliability * curvature
	} else {
		// Remap the upper-lower ratio with reliability
		val reliabilityAdjusted = remapRatioWithReliability(fromUpperToLower, reliability)
		// Lerp from upper-lower to reliability-adjusted according to curvature
		FastMath.lerp(fromUpperToLower, reliabilityAdjusted, curvature)
	}
}

private fun averageRotation(inputSkeleton: InputSkeleton, takeBodyParts: Array<BodyPart> = arrayOf()): Quaternion {
	val bonesToAverage = inputSkeleton.values.filter { it.bodyPart in takeBodyParts }
	return bonesToAverage.map { it.rotation }
		.reduceIndexedOrNull { index, acc, rotation ->
			acc.lerpQ(rotation, 1f / (index + 1))
		} ?: Quaternion.IDENTITY
}

/**
 * Interpolates between 2 quaternions but with the absolute of the ratio for the twist part.
 */
private fun interpolateAbsTwist(fromRotation: Quaternion, toRotation: Quaternion, ratio: Float): Quaternion {
	if (ratio >= 0f) return fromRotation.interpQ(toRotation, ratio)

	// Deconstruct the twist and swing of the rotations
	val fromTwist = fromRotation.twist()
	val fromSwing = fromRotation / fromTwist
	val toTwist = toRotation.twist()
	val toSwing = toRotation / toTwist

	// Interpolate each part separately
	val interpolatedTwist = fromTwist.interpQ(toTwist, -ratio)
	val interpolatedSwing = fromSwing.interpQ(toSwing, ratio)

	return interpolatedSwing * interpolatedTwist
}

/**
 * Handles imputing the rotation of spine bones that are not actively receiving data from the rotations
 * of nearby bones.
 *
 * Similar to FallbackProcessor specifically for the waist and hip.
 */
class RelaxedSpineInputProcessor(val settings: Settings) : SkeletonInputProcessor {
	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		val ratios = settings.context.state.value.data.skeletonConfig.ratios
		val boneInputs = BodyPartMap(mutableInputSkeleton)
		val sourceActive = SPINE_SOURCES.associateWith { it.parts.all { part -> boneInputs[part]?.isRotationActive == true } }
		val fromTo = SPINE_BONES.withIndex().associate { (selfIndex, source) ->
			source to getFromTo(selfIndex, sourceActive)
		}

		for (spineSource in SPINE_BONES) {
			// For optimization's sake, assume only one BodyPart per SpineSource we traverse.
			val bodyPart = spineSource.parts.first()
			val bone = boneInputs[bodyPart] ?: continue
			val isActive = bone.isRotationActive

			// Get reliabilities mapped to this spine bone
			val reliabilities = SPINE_SOURCE_RELIABILITY[spineSource] ?: continue

			// Get the spine sources for the current spineSource
			val (fromIndex, toIndex) = fromTo[spineSource] ?: error("No fromTo for $spineSource found.")
			// toIndex will be null if the bone has no active children.
			if (toIndex == null) continue
			val (fromSpineSource, toSpineSource) = if (fromIndex != null) {
				SPINE_SOURCES[fromIndex] to SPINE_SOURCES[toIndex]
			} else {
				// Fall back to itself to its to's to
				val toTo = fromTo[SPINE_SOURCES[toIndex]] ?: continue
				spineSource to SPINE_SOURCES[toTo.second ?: continue]
			}

			// We are interpolating as-if we were the To, but using self instead of to
			val sourceActive = isActive || fromIndex == null
			// Get the interpolation ratio
			val interpolateRatio = interpolateRatio(
				ratios.imputeSpineFromUpperToLower,
				ratios.imputeSpineCurvature,
				reliabilities[fromSpineSource] ?: continue,
				reliabilities[toSpineSource] ?: continue,
				sourceActive,
			)

			// If from is null, use to as from and use to's to as to
			val fromParts = if (fromIndex != null) fromSpineSource.parts else SPINE_SOURCES[toIndex].parts
			val toParts = toSpineSource.parts
			// Interpolate between from and to using our interpolation ratio.
			mutableInputSkeleton[bodyPart] = bone.copy(
				rotation = interpolateAbsTwist(
					averageRotation(boneInputs, fromParts),
					averageRotation(boneInputs, toParts),
					interpolateRatio,
				),
			)
		}
	}
}
