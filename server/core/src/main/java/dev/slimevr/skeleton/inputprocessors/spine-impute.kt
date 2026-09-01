package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import dev.slimevr.skeleton.mutateCopy
import dev.slimevr.skeleton.resolveAverageRotationFor
import solarxr_protocol.datatypes.BodyPart

/**
 * Handles imputing the rotation of spine bones that are not actively receiving data from the rotations
 * of nearby bones.
 *
 * Similar to FallbackProcessor specifically for the waist and hip.
 */
class SpineImputeInputProcessor(val settings: Settings) : SkeletonInputProcessor {
	private enum class SpineSource(val parts: Array<BodyPart>) {
		CHEST(arrayOf(BodyPart.CHEST)),
		HIP(arrayOf(BodyPart.HIP)),
		WAIST(arrayOf(BodyPart.WAIST)),
		UPPER_LEGS(arrayOf(BodyPart.LEFT_UPPER_LEG, BodyPart.RIGHT_UPPER_LEG)),
	}

	private fun reliabilityOf(bodyPart: BodyPart, source: SpineSource): Float = when (bodyPart) {
		BodyPart.WAIST -> when (source) {
			SpineSource.CHEST -> 0.6f
			SpineSource.HIP, SpineSource.UPPER_LEGS -> 1.0f
			else -> error("Invalid spine combination $bodyPart, $source")
		}

		BodyPart.HIP -> when (source) {
			SpineSource.CHEST, SpineSource.UPPER_LEGS -> 1.0f
			SpineSource.WAIST -> 0.8f
			else -> error("Invalid spine combination $bodyPart, $source")
		}

		else -> error("Invalid missing spine body part $bodyPart")
	}

	override fun process(inputSkeleton: InputSkeleton, skeletonHeight: Float): InputSkeleton {
		val ratios = settings.context.state.value.data.skeletonConfig.ratios

		val hasChest = inputSkeleton[BodyPart.UPPER_CHEST]?.isRotationActive == true || inputSkeleton[BodyPart.CHEST]?.isRotationActive == true
		val hasWaist = inputSkeleton[BodyPart.WAIST]?.isRotationActive == true
		val hasHip = inputSkeleton[BodyPart.HIP]?.isRotationActive == true
		val hasUpperLegs = inputSkeleton[BodyPart.LEFT_UPPER_LEG]?.isRotationActive == true && inputSkeleton[BodyPart.RIGHT_UPPER_LEG]?.isRotationActive == true
		val missingSpineParts = buildList {
			if (!hasWaist) add(BodyPart.WAIST)
			if (!hasHip) add(BodyPart.HIP)
		}

		return inputSkeleton.mutateCopy { updated ->
			for ((chainIndex, bodyPart) in missingSpineParts.withIndex()) {
				val bone = inputSkeleton.getValue(bodyPart)

				// Get the first active bones above and below this one in the chain
				val (fromSource, toSource) = when (bodyPart) {
					BodyPart.WAIST -> {
						val from = SpineSource.CHEST.takeIf { hasChest } ?: continue
						val to = when {
							hasHip -> SpineSource.HIP
							hasUpperLegs -> SpineSource.UPPER_LEGS
							else -> continue
						}
						from to to
					}

					BodyPart.HIP -> {
						val from = when {
							hasWaist -> SpineSource.WAIST
							hasChest -> SpineSource.CHEST
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

				val fromRotation = inputSkeleton.resolveAverageRotationFor(fromSource.parts)
				val toRotation = inputSkeleton.resolveAverageRotationFor(toSource.parts)

				updated[bodyPart] = bone.copy(rawRotation = fromRotation.interpQ(toRotation, interpolateRatio))
			}
		}
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

		return if (curvature <= 0.5f) {
			// Raw to reliability adjusted
			lerp(fromUpperToLower, reliabilityAdjusted, curvature * 2f)
		} else {
			// Reliability adjusted to max curve
			val maxCurve = chainIndex / (chainSize - 1).toFloat()
			lerp(reliabilityAdjusted, maxCurve, (curvature - 0.5f) * 2f)
		}
	}

	/**
	 * Remaps a ratio This assumes a default ratio of 50%.
	 */
	private fun remapRatioWithReliability(ratio: Float, fromReliability: Float, toReliability: Float): Float {
		val reliability = (fromReliability / toReliability) * 0.5f

		return if (ratio <= 0.5f) {
			ratio * 2f * reliability
		} else {
			reliability + (ratio - 0.5f) * 2f * (1f - reliability)
		}
	}

	private fun lerp(from: Float, to: Float, t: Float): Float = from + (to - from) * t
}
