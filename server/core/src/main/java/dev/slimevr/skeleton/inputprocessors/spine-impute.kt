package dev.slimevr.skeleton.inputprocessors

import com.jme3.math.FastMath
import dev.slimevr.config.Settings
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import io.github.axisangles.ktmath.Quaternion
import solarxr_protocol.datatypes.BodyPart

// At this default value, the user's spine should behave as we meant it to.
const val DEFAULT_SPINE_RATIO = 0.5f

private enum class SpineSource(val parts: Array<BodyPart>) {
	LOWER_CHEST(arrayOf(BodyPart.LOWER_CHEST)),
	UPPER_WAIST(arrayOf(BodyPart.UPPER_WAIST)),
	LOWER_WAIST(arrayOf(BodyPart.LOWER_WAIST)),
	HIP(arrayOf(BodyPart.HIP)),
	UPPER_LEGS(arrayOf(BodyPart.LEFT_UPPER_LEG, BodyPart.RIGHT_UPPER_LEG)),
}

/**
 * Returns the interpolation ratio modified with the reliability and curve.
 * If a single bone is missing, ratio is reliability adjusted.
 * At 0.0 curve, ratio is raw.
 * At 0.5 curve, ratio is reliability adjusted.
 * At 1.0 curve, ratio is 0 for the first in chain and 1 for the last in chain.
 */
private fun interpolateRatio(chainIndex: Int, chainSize: Int, fromUpperToLower: Float, curvature: Float, fromReliability: Float, toReliability: Float): Float {
	val reliabilityAdjusted = remapRatioWithReliability(fromUpperToLower, fromReliability, toReliability)
	if (chainSize <= 1) return reliabilityAdjusted // Single missing bone; reliability adjusted

	return if (curvature <= DEFAULT_SPINE_RATIO) {
		// Raw to reliability adjusted
		FastMath.lerp(fromUpperToLower, reliabilityAdjusted, curvature * 2f)
	} else {
		// Reliability adjusted to max curve
		val maxCurve = chainIndex / (chainSize - 1).toFloat()
		FastMath.lerp(reliabilityAdjusted, maxCurve, (curvature - DEFAULT_SPINE_RATIO) * 2f)
	}
}

/**
 * Remaps a ratio This assumes a default ratio of 50%.
 */
private fun remapRatioWithReliability(ratio: Float, fromReliability: Float, toReliability: Float): Float {
	val reliability = (toReliability / fromReliability) * DEFAULT_SPINE_RATIO

	return if (ratio <= DEFAULT_SPINE_RATIO) {
		ratio * 2f * reliability
	} else {
		reliability + (ratio - DEFAULT_SPINE_RATIO) * 2f * (1f - reliability)
	}
}

private fun averageRotation(inputSkeleton: InputSkeleton, takeBodyParts: Array<BodyPart> = arrayOf()): Quaternion {
	val bonesToAverage = inputSkeleton.values.filter { it.bodyPart in takeBodyParts }
	return bonesToAverage.map { it.rotation }
		.reduceIndexedOrNull { index, acc, rotation ->
			acc.lerpQ(rotation, 1f / (index + 1))
		} ?: Quaternion.IDENTITY
}

private fun reliabilityOf(bodyPart: BodyPart, source: SpineSource): Float = when (bodyPart) {
	BodyPart.UPPER_WAIST -> when (source) {
		// From
		SpineSource.LOWER_CHEST -> 1f

		// To
		SpineSource.LOWER_WAIST -> 1.2f

		SpineSource.HIP -> 0.45f

		SpineSource.UPPER_LEGS -> 0.4f

		else -> error("Invalid spine combination $bodyPart, $source")
	}

	BodyPart.LOWER_WAIST -> when (source) {
		// From
		SpineSource.LOWER_CHEST -> 1f

		SpineSource.UPPER_WAIST -> 1.2f

		// To
		SpineSource.HIP -> 0.75f

		SpineSource.UPPER_LEGS -> 0.7f

		else -> error("Invalid spine combination $bodyPart, $source")
	}

	BodyPart.HIP -> when (source) {
		// From
		SpineSource.LOWER_CHEST -> 1.25f

		SpineSource.LOWER_WAIST -> 1.3f

		SpineSource.UPPER_WAIST -> 1.35f

		// To
		SpineSource.UPPER_LEGS -> 1.6f

		else -> error("Invalid spine combination $bodyPart, $source")
	}

	else -> error("Invalid missing spine body part $bodyPart")
}

/**
 * Handles imputing the rotation of spine bones that are not actively receiving data from the rotations
 * of nearby bones.
 *
 * Similar to FallbackProcessor specifically for the waist and hip.
 */
class SpineImputeInputProcessor(val settings: Settings) : SkeletonInputProcessor {
	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		val ratios = settings.context.state.value.data.skeletonConfig.ratios

		val hasChest = mutableInputSkeleton[BodyPart.UPPER_CHEST]?.isRotationActive == true || mutableInputSkeleton[BodyPart.LOWER_CHEST]?.isRotationActive == true
		val hasUpperWaist = mutableInputSkeleton[BodyPart.UPPER_WAIST]?.isRotationActive == true
		val hasLowerWaist = mutableInputSkeleton[BodyPart.LOWER_WAIST]?.isRotationActive == true
		val hasHip = mutableInputSkeleton[BodyPart.HIP]?.isRotationActive == true
		val hasUpperLegs = mutableInputSkeleton[BodyPart.LEFT_UPPER_LEG]?.isRotationActive == true && mutableInputSkeleton[BodyPart.RIGHT_UPPER_LEG]?.isRotationActive == true
		val missingSpineParts = buildList {
			if (!hasUpperWaist) add(BodyPart.UPPER_WAIST)
			if (!hasLowerWaist) add(BodyPart.LOWER_WAIST)
			if (!hasHip) add(BodyPart.HIP)
		}

		for ((chainIndex, bodyPart) in missingSpineParts.withIndex()) {
			val bone = mutableInputSkeleton[bodyPart] ?: continue

			// Get the first active bones above and below this one in the chain
			val (fromSource, toSource) = when (bodyPart) {
				BodyPart.UPPER_WAIST -> {
					val from = SpineSource.LOWER_CHEST.takeIf { hasChest } ?: continue
					val to = when {
						hasLowerWaist -> SpineSource.LOWER_WAIST
						hasHip -> SpineSource.HIP
						hasUpperLegs -> SpineSource.UPPER_LEGS
						else -> continue
					}
					from to to
				}

				BodyPart.LOWER_WAIST -> {
					val from = when {
						hasUpperWaist -> SpineSource.UPPER_WAIST
						hasChest -> SpineSource.LOWER_CHEST
						else -> continue
					}
					val to = when {
						hasHip -> SpineSource.HIP
						hasUpperLegs -> SpineSource.UPPER_LEGS
						else -> continue
					}
					from to to
				}

				BodyPart.HIP -> {
					val from = when {
						hasLowerWaist -> SpineSource.LOWER_WAIST
						hasUpperWaist -> SpineSource.UPPER_WAIST
						hasChest -> SpineSource.LOWER_CHEST
						else -> continue
					}
					val to = SpineSource.UPPER_LEGS.takeIf { hasUpperLegs } ?: continue
					from to to
				}

				else -> error("Invalid missing spine body part $bodyPart")
			}

			val interpolateRatio = interpolateRatio(
				chainIndex,
				missingSpineParts.size,
				ratios.imputeSpineFromUpperToLower,
				ratios.imputeSpineCurvature,
				reliabilityOf(bodyPart, fromSource),
				reliabilityOf(bodyPart, toSource),
			)

			val fromRotation = averageRotation(mutableInputSkeleton, fromSource.parts)
			val toRotation = averageRotation(mutableInputSkeleton, toSource.parts)

			mutableInputSkeleton[bodyPart] = bone.copy(rotation = fromRotation.interpQ(toRotation, interpolateRatio))
		}
	}
}
