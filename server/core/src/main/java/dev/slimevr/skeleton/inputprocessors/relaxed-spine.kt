package dev.slimevr.skeleton.inputprocessors

import com.jme3.math.FastMath
import dev.slimevr.config.Settings
import dev.slimevr.skeleton.BodyPartMap
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import io.github.axisangles.ktmath.Quaternion
import solarxr_protocol.datatypes.BodyPart
import java.util.EnumMap

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

private val SPINE_BONES_TO_RELAX = arrayOf(
	BodyPart.UPPER_CHEST,
	BodyPart.LOWER_CHEST,
	BodyPart.UPPER_WAIST,
	BodyPart.LOWER_WAIST,
	BodyPart.HIP,
)

// TODO : fine tuning! :3
//  (also, refactoring this whole file as it has become a mess!)
// The higher a value is, the more reliable it is.
// If a "To" is 2x the "From", it'll use 100% "To".
// If a "To" is the same as "From", it'll do 50-50.
private fun reliabilityOf(bodyPart: BodyPart, source: SpineSource): Float = when (bodyPart) {
	BodyPart.UPPER_CHEST -> when (source) {
		// From
		SpineSource.UPPER_CHEST -> 7f

		// To (negative to relax the spine
		SpineSource.LOWER_CHEST -> -1f

		SpineSource.UPPER_WAIST -> -1.5f

		SpineSource.LOWER_WAIST -> -2f

		SpineSource.HIP -> -4f

		SpineSource.UPPER_LEGS -> -3f
	}

	BodyPart.LOWER_CHEST -> when (source) {
		// From
		SpineSource.UPPER_CHEST -> 10f

		SpineSource.LOWER_CHEST -> 20f

		// To (negative to relax the spine
		SpineSource.UPPER_WAIST -> -1.5f

		SpineSource.LOWER_WAIST -> -2f

		SpineSource.HIP -> -4f

		SpineSource.UPPER_LEGS -> -3f
	}

	BodyPart.UPPER_WAIST -> when (source) {
		// From
		SpineSource.UPPER_CHEST, SpineSource.LOWER_CHEST -> 10f

		SpineSource.UPPER_WAIST -> 68f

		// To
		SpineSource.LOWER_WAIST -> 17f

		SpineSource.HIP -> 5.5f

		SpineSource.UPPER_LEGS -> 4.5f
	}

	BodyPart.LOWER_WAIST -> when (source) {
		// From
		SpineSource.UPPER_CHEST, SpineSource.LOWER_CHEST -> 6f

		SpineSource.UPPER_WAIST -> 6.25f

		SpineSource.LOWER_WAIST -> 44f

		// To
		SpineSource.HIP -> 10f

		SpineSource.UPPER_LEGS -> 4.25f
	}

	BodyPart.HIP -> when (source) {
		// From
		SpineSource.UPPER_CHEST, SpineSource.LOWER_CHEST -> 9.5f

		SpineSource.UPPER_WAIST -> 12.5f

		SpineSource.LOWER_WAIST -> 13f

		SpineSource.HIP -> 48f

		// To
		SpineSource.UPPER_LEGS -> 13f
	}

	else -> error("Invalid missing spine body part $bodyPart")
}

private fun interpolateRatio(fromUpperToLower: Float, curvature: Float, fromReliability: Float, toReliability: Float, isActive: Boolean): Float {
	// Compute reliability ratio
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
 * Remaps the From-To ratio with From-To reliability.
 */
private fun remapRatioWithReliability(ratio: Float, reliability: Float): Float = if (ratio <= DEFAULT_SPINE_UPPER_LOWER) {
	ratio * (1f / DEFAULT_SPINE_UPPER_LOWER) * reliability
} else {
	reliability + (ratio - DEFAULT_SPINE_UPPER_LOWER) * (1f / DEFAULT_SPINE_UPPER_LOWER) * (1f - reliability)
}

private fun averageRotation(inputSkeleton: InputSkeleton, takeBodyParts: Array<BodyPart> = arrayOf()): Quaternion {
	val bonesToAverage = inputSkeleton.values.filter { it.bodyPart in takeBodyParts }
	return bonesToAverage.map { it.rotation }
		.reduceIndexedOrNull { index, acc, rotation ->
			acc.lerpQ(rotation, 1f / (index + 1))
		} ?: Quaternion.IDENTITY
}

/**
 * Handles imputing the rotation of spine bones that are not actively receiving data from the rotations
 * of nearby bones.
 *
 * Similar to FallbackProcessor specifically for the waist and hip.
 */
class RelaxedSpineInputProcessor(val settings: Settings) : SkeletonInputProcessor {
	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		val fixedInputs = BodyPartMap(mutableInputSkeleton)

		val ratios = settings.context.state.value.data.skeletonConfig.ratios

		val hasUpperChest = fixedInputs[BodyPart.UPPER_CHEST]?.isRotationActive == true
		val hasLowerChest = fixedInputs[BodyPart.LOWER_CHEST]?.isRotationActive == true
		val hasUpperWaist = fixedInputs[BodyPart.UPPER_WAIST]?.isRotationActive == true
		val hasLowerWaist = fixedInputs[BodyPart.LOWER_WAIST]?.isRotationActive == true
		val hasHip = fixedInputs[BodyPart.HIP]?.isRotationActive == true
		val hasUpperLegs = fixedInputs[BodyPart.LEFT_UPPER_LEG]?.isRotationActive == true && fixedInputs[BodyPart.RIGHT_UPPER_LEG]?.isRotationActive == true

		for (bodyPart in SPINE_BONES_TO_RELAX) {
			val bone = fixedInputs[bodyPart] ?: continue

			// Get the first active bones above and below this one in the chain
			val (fromSource, toSource) = when (bodyPart) {
				BodyPart.UPPER_CHEST -> {
					val from = SpineSource.UPPER_CHEST.takeIf { hasUpperChest } ?: continue
					val to = when {
						hasUpperWaist -> SpineSource.UPPER_WAIST
						hasLowerWaist -> SpineSource.LOWER_WAIST
						hasHip -> SpineSource.HIP
						hasUpperLegs -> SpineSource.UPPER_LEGS
						else -> continue
					}
					from to to
				}

				BodyPart.LOWER_CHEST -> {
					val from = when {
						hasLowerChest -> SpineSource.LOWER_CHEST
						hasUpperChest -> SpineSource.UPPER_CHEST
						else -> continue
					}
					val to = when {
						hasUpperWaist -> SpineSource.UPPER_WAIST
						hasLowerWaist -> SpineSource.LOWER_WAIST
						hasHip -> SpineSource.HIP
						hasUpperLegs -> SpineSource.UPPER_LEGS
						else -> continue
					}
					from to to
				}

				BodyPart.UPPER_WAIST -> {
					val from = when {
						hasUpperWaist -> SpineSource.UPPER_WAIST
						hasLowerChest -> SpineSource.LOWER_CHEST
						hasUpperChest -> SpineSource.UPPER_CHEST
						else -> continue
					}
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
						hasLowerWaist -> SpineSource.LOWER_WAIST
						hasUpperWaist -> SpineSource.UPPER_WAIST
						hasLowerChest -> SpineSource.LOWER_CHEST
						hasUpperChest -> SpineSource.UPPER_CHEST
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
						hasHip -> SpineSource.HIP
						hasLowerWaist -> SpineSource.LOWER_WAIST
						hasUpperWaist -> SpineSource.UPPER_WAIST
						hasLowerChest -> SpineSource.LOWER_CHEST
						hasUpperChest -> SpineSource.UPPER_CHEST
						else -> continue
					}
					val to = SpineSource.UPPER_LEGS.takeIf { hasUpperLegs } ?: continue
					from to to
				}

				else -> error("Invalid missing spine body part $bodyPart")
			}

			val interpolateRatio = interpolateRatio(
				ratios.imputeSpineFromUpperToLower,
				ratios.imputeSpineCurvature,
				reliabilityOf(bodyPart, fromSource),
				reliabilityOf(bodyPart, toSource),
				bodyPart == fromSource.parts.first(),
			)

			val fromRotation = averageRotation(fixedInputs, fromSource.parts)
			val toRotation = averageRotation(fixedInputs, toSource.parts)

			mutableInputSkeleton[bodyPart] = bone.copy(rotation = fromRotation.interpQ(toRotation, interpolateRatio))
		}
	}
}
