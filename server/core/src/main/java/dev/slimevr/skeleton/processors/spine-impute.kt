package dev.slimevr.skeleton.processors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.SkeletonProcessor
import dev.slimevr.skeleton.SkeletonState
import dev.slimevr.skeleton.mutate
import dev.slimevr.skeleton.resolveRotationFor
import solarxr_protocol.datatypes.BodyPart

/**
 * Handles imputing the rotation of spine bones that are not actively receiving data from the rotations
 * of nearby bones.
 *
 * Similar to FallbackProcessor specifically for the waist and hip.
 */
class SpineImputeProcessor(val settings: Settings) : SkeletonProcessor {
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

	override fun process(state: SkeletonState): SkeletonState {
		val boneInputs = state.boneInputs
		val ratios = settings.context.state.value.data.skeletonConfig.ratios

		val hasChest = boneInputs[BodyPart.UPPER_CHEST]?.isActive == true || boneInputs[BodyPart.CHEST]?.isActive == true
		val hasWaist = boneInputs[BodyPart.WAIST]?.isActive == true
		val hasHip = boneInputs[BodyPart.HIP]?.isActive == true
		val hasUpperLegs = boneInputs[BodyPart.LEFT_UPPER_LEG]?.isActive == true && boneInputs[BodyPart.RIGHT_UPPER_LEG]?.isActive == true
		val missingSpineParts = buildList {
			if (!hasWaist) add(BodyPart.WAIST)
			if (!hasHip) add(BodyPart.HIP)
		}

		return state.copy(
			boneInputs = boneInputs.mutate { updated ->
				for ((chainIndex, bodyPart) in missingSpineParts.withIndex()) {
					val bone = boneInputs.getValue(bodyPart)

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

					val fromRotation = boneInputs.resolveRotationFor(fromSource.parts)
					val toRotation = boneInputs.resolveRotationFor(toSource.parts)

					updated[bodyPart] = bone.copy(rawRotation = fromRotation.interpQ(toRotation, interpolateRatio))
				}
			},
		)
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
