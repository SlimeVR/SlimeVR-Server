package dev.slimevr.skeleton.inputprocessors

import com.jme3.math.FastMath
import dev.slimevr.config.Settings
import dev.slimevr.skeleton.BodyPartMap
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart
import kotlin.enums.enumEntries

// At this default value, the user's spine should behave as we meant it to.
const val DEFAULT_SPINE_UPPER_LOWER = 0.5f

private data class Reliability(
	val forward: Float,
	val side: Float? = null, // Uses forward if null
	val proportion: Float = 1f,
)

private enum class SpineSource(val parts: Array<BodyPart>) {
	UPPER_CHEST(arrayOf(BodyPart.UPPER_CHEST)),
	LOWER_CHEST(arrayOf(BodyPart.LOWER_CHEST)),
	UPPER_WAIST(arrayOf(BodyPart.UPPER_WAIST)),
	LOWER_WAIST(arrayOf(BodyPart.LOWER_WAIST)),
	HIP(arrayOf(BodyPart.HIP)),
	UPPER_LEGS(arrayOf(BodyPart.LEFT_UPPER_LEG, BodyPart.RIGHT_UPPER_LEG)),
}

private val SPINE_SOURCES = enumEntries<SpineSource>().toTypedArray()

// The higher a value is, the that source is considered as reliable.
// If a "To" is 2x the "From", it'll use 100% "To".
// If a "To" is the same as "From", it'll do 50-50.
// A negative value will go towards the opposite rotation.
// A From and To may be taken from different roots.
// Order matters! Missing elements of the inner arrays will be skipped gracefully.
private val SPINE_SOURCE_RELIABILITY = arrayOf(
	SpineSource.UPPER_CHEST to arrayOf(
		SpineSource.UPPER_CHEST to Reliability(-11.5f, -20f), // Itself
		// To
		SpineSource.LOWER_CHEST to Reliability(-6f, -15f, 0f),
		SpineSource.UPPER_WAIST to Reliability(1.5f, 4f, 0.4f),
		SpineSource.LOWER_WAIST to Reliability(2f, 5f, 0.4f),
		SpineSource.HIP to Reliability(4f, 6.25f, 0.3f),
		SpineSource.UPPER_LEGS to Reliability(4f, 5f),
	),
	SpineSource.LOWER_CHEST to arrayOf(
		// From
		SpineSource.UPPER_CHEST to Reliability(-18f, 40f),
		SpineSource.LOWER_CHEST to Reliability(30f), // Itself
		// To
		SpineSource.UPPER_WAIST to Reliability(1.5f, 10f, 0.4f),
		SpineSource.LOWER_WAIST to Reliability(2f, 8f, 0.4f),
		SpineSource.HIP to Reliability(4f, 7.25f, 0.3f),
		SpineSource.UPPER_LEGS to Reliability(4f, 6f),
	),
	SpineSource.UPPER_WAIST to arrayOf(
		// From
		SpineSource.UPPER_CHEST to Reliability(10.5f),
		SpineSource.LOWER_CHEST to Reliability(11.5f),
		SpineSource.UPPER_WAIST to Reliability(40f), // Itself
		// To
		SpineSource.LOWER_WAIST to Reliability(16f, 18f, 0.85f),
		SpineSource.HIP to Reliability(4.5f, 13.5f, 0.85f),
		SpineSource.UPPER_LEGS to Reliability(0.25f, 12f),
	),
	SpineSource.LOWER_WAIST to arrayOf(
		// From
		SpineSource.UPPER_CHEST to Reliability(6f),
		SpineSource.LOWER_CHEST to Reliability(6.75f),
		SpineSource.UPPER_WAIST to Reliability(25f),
		SpineSource.LOWER_WAIST to Reliability(38f), // Itself
		// To
		SpineSource.HIP to Reliability(5.25f, 13f, 0.85f),
		SpineSource.UPPER_LEGS to Reliability(1.75f, 11f),
	),
	SpineSource.HIP to arrayOf(
		// From
		SpineSource.UPPER_CHEST to Reliability(9.5f, 9f),
		SpineSource.LOWER_CHEST to Reliability(9.75f, 9.25f),
		SpineSource.UPPER_WAIST to Reliability(13f, 20f),
		SpineSource.LOWER_WAIST to Reliability(14f, 30f),
		SpineSource.HIP to Reliability(38f, 60f, 0.9f), // Itself
		// To
		SpineSource.UPPER_LEGS to Reliability(7f, 17f),
	),
)
private val SPINE_SOURCE_RELIABILITY_INDEXED = SPINE_SOURCE_RELIABILITY.withIndex()
private val SPINE_SOURCE_RELIABILITY_SOURCES = SPINE_SOURCE_RELIABILITY.associate { (spineSource, reliabilities) ->
	spineSource to reliabilities
}
private val SPINE_SOURCE_RELIABILITY_OWN_INDEX = SPINE_SOURCE_RELIABILITY.associate { (spineSource, reliabilities) ->
	spineSource to SPINE_SOURCE_RELIABILITY_SOURCES[spineSource]!!.map { it.first }.indexOf(spineSource)
}
private val SPINE_SOURCE_RELIABILITY_MAP = SPINE_SOURCE_RELIABILITY.map { (spineSource, reliabilities) ->
	spineSource to reliabilities.toMap()
}.toTypedArray()

/** The spine bones this processor imputes, top down. Other sources are only read as references. */
val SPINE_IMPUTED_PARTS: List<BodyPart> = SPINE_SOURCE_RELIABILITY.flatMap { (source, _) -> source.parts.toList() }

private fun nearestActive(sources: Array<Pair<SpineSource, Reliability>>, startIndex: Int, step: Int, sourceActive: Map<SpineSource, Boolean>): List<SpineSource> {
	var i = startIndex
	val result = mutableListOf<SpineSource>()
	var proportionSum = 0f
	while (i >= 0 && i < sources.count() && proportionSum < 1f) {
		val source = sources[i]
		if (sourceActive[source.first] == true) {
			result.add(source.first)
			proportionSum += source.second.proportion
		}
		i += step
	}
	return result
}

/**
 * Builds a map containing the list of From SpineSource and a list of To SpineSource for each of key in SPINE_SOURCE_RELIABILITY.
 */
private fun buildFromTo(sourceActive: Map<SpineSource, Boolean>) = SPINE_SOURCE_RELIABILITY.associate { source ->
	val spineSource = source.first
	val sources = SPINE_SOURCE_RELIABILITY_SOURCES[spineSource] ?: error("No SPINE_SOURCE_RELIABILITY_SOURCES found for $spineSource")
	val selfIndex = SPINE_SOURCE_RELIABILITY_OWN_INDEX[spineSource] ?: error("No SPINE_SOURCE_RELIABILITY_OWN_INDEX found for $spineSource")

	// Find From, the first active bone in itself + its parents.
	val from = nearestActive(sources, selfIndex, -1, sourceActive)
	// Find To, the next active bone in its children
	val to = nearestActive(sources, selfIndex + 1, 1, sourceActive)

	spineSource to (from to to)
}

private fun averageRotation(inputSkeleton: InputSkeleton, takeBodyParts: Array<BodyPart> = arrayOf()): Quaternion? {
	val bonesToAverage = inputSkeleton.values.filter { it.bodyPart in takeBodyParts }
	return bonesToAverage.map { it.rotation }
		.reduceIndexedOrNull { index, acc, rotation ->
			// Very important that this is interpQ as lerpQ will break on axes/polarity
			acc.interpQ(rotation, 1f / (index + 1))
		}
}

private fun weightedAverageRotation(sources: List<SpineSource>, reliabilities: Map<SpineSource, Reliability>, boneInputs: InputSkeleton): Quaternion {
	var accProportion = 0f
	return sources.foldIndexed(Quaternion.ZERO) { i, acc, fromSource ->
		val rotation = averageRotation(boneInputs, fromSource.parts)
		if (rotation != null) {
			val proportion = if (i + 1 == sources.count()) {
				// Use all the remaining
				1 - accProportion
			} else {
				reliabilities[fromSource]?.proportion ?: error("No proportion found for $fromSource")
			}
			accProportion += proportion

			acc + (rotation * proportion)
		} else {
			acc
		}
	}.unit()
}

private fun weightedAverageReliability(sources: List<SpineSource>, reliabilities: Map<SpineSource, Reliability>): Pair<Float, Float?> {
	var accProportion = 0f
	var accForward = 0f
	var accSide: Float? = null
	for ((i, source) in sources.withIndex()) {
		val reliability = reliabilities[source] ?: continue

		val proportion = if (i + 1 == sources.count()) {
			// Use all the remaining
			1 - accProportion
		} else {
			reliabilities[source]?.proportion ?: error("No proportion found for $source")
		}
		accProportion += proportion

		accForward += (reliability.forward * proportion)
		if (reliability.side != null) accSide = (accSide ?: 0f) + (reliability.side * proportion)
	}
	return accForward to accSide
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

/**
 * Interpolates between 2 rotations with a different ratio for forward and the rest/side.
 */
private fun interpolateForwardSide(fromRotation: Quaternion, toRotation: Quaternion, forwardRatio: Float, sideRatio: Float?): Quaternion {
	if (sideRatio == null) return fromRotation.interpQ(toRotation, forwardRatio)

	// Work with the rotation delta to avoid axes being ambiguous
	val delta = fromRotation.inv() * toRotation

	// Decompose the delta into its twist (forward) and swing (remaining/sides) along the x-axis
	val forward = delta.project(Vector3.POS_X).unit()
	val side = delta / forward

	// Scale each axis of the delta with our ratios
	val scaledForward = forward.pow(forwardRatio)
	val scaledSide = side.pow(sideRatio)

	// Recompose the delta. We removed formward from delta to get side, so we must add forward to side back.
	val scaledDelta = scaledSide * scaledForward

	// Return scaled delta applied to From rotation
	return fromRotation * scaledDelta
}

/**
 * Handles imputing the rotation of spine bones that are not actively receiving data from the rotations
 * of nearby bones.
 */
class SpineInputProcessor(val settings: Settings) : SkeletonInputProcessor {
	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		val ratios = settings.context.state.value.data.skeletonConfig.ratios
		val sourceActive = SPINE_SOURCES.associateWith { it.parts.all { part -> mutableInputSkeleton[part]?.isRotationActive == true } }
		val fromTo = buildFromTo(sourceActive)

		val boneInputs = BodyPartMap(mutableInputSkeleton)
		for ((spineIndex, spineSourceReliability) in SPINE_SOURCE_RELIABILITY_INDEXED) {
			val spineSource = spineSourceReliability.first

			// For optimization's sake, assume only one BodyPart per SpineSource we traverse.
			val bodyPart = spineSource.parts.first()
			val bone = boneInputs[bodyPart] ?: continue

			// Get reliabilities mapped to this spine bone
			val reliabilities = SPINE_SOURCE_RELIABILITY_MAP[spineIndex].second

			// Get the spine sources for the current spineSource
			val (from, to) = fromTo[spineSource] ?: error("No fromTo for $spineSource found.")
			if (to.isEmpty()) continue // Nothing to interpolate with

			// Determine the sources used for getting rotation
			val fromRotationSources = from.ifEmpty { to }
			val fromReliabilitySources = from.ifEmpty { listOf(spineSource) }
			val toSources = if (from.isNotEmpty()) to else to.flatMap { fromTo[it]?.second ?: emptyList() }

			// Get From and To's weighted average reliabilities
			val (fromForwardReliability, fromSideReliability) = weightedAverageReliability(fromReliabilitySources, reliabilities)
			val (toForwardReliability, toSideReliability) = weightedAverageReliability(toSources, reliabilities)

			// Get From and To's weighted average rotations
			val fromRotation = weightedAverageRotation(fromRotationSources, reliabilities, boneInputs)
			val toRotation = weightedAverageRotation(toSources, reliabilities, boneInputs)

			// We are interpolating as-if we were the To if From is empty
			val sourceActive = bone.isRotationActive || from.isEmpty()

			// Calculate ratios from reliabilities
			val forwardRatio = interpolateRatio(
				ratios.imputeSpineFromUpperToLower,
				ratios.spineCurvature,
				fromForwardReliability,
				toForwardReliability,
				sourceActive,
			)
			val sideRatio = if (fromSideReliability != null || toSideReliability != null) {
				interpolateRatio(
					ratios.imputeSpineFromUpperToLower,
					ratios.spineCurvature,
					fromSideReliability ?: fromForwardReliability,
					toSideReliability ?: toForwardReliability,
					sourceActive,
				)
			} else {
				null
			}

			// Interpolate between from and to using our interpolation ratio.
			mutableInputSkeleton[bodyPart] = bone.copy(
				rotation = interpolateForwardSide(
					fromRotation,
					toRotation,
					forwardRatio,
					sideRatio,
				),
			)
		}
	}
}
