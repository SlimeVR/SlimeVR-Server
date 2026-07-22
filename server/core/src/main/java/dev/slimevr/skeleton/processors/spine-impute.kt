package dev.slimevr.skeleton.processors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.SkeletonProcessor
import dev.slimevr.skeleton.SkeletonState
import dev.slimevr.skeleton.resolveRotationFor
import solarxr_protocol.datatypes.BodyPart

/**
 * Handles imputing the rotation of spine bones that are not actively receiving data from the rotations
 * of nearby bones.
 *
 * Similar to FallbackProcessor specifically for the waist and hip.
 */
class SpineImputeProcessor(val settings: Settings) : SkeletonProcessor {
	private val chestSet = setOf(BodyPart.CHEST)
	private val hipSet = setOf(BodyPart.HIP)
	private val waistSet = setOf(BodyPart.WAIST)
	private val upperLegsSet = setOf(BodyPart.LEFT_UPPER_LEG, BodyPart.RIGHT_UPPER_LEG)

	/**
	 * Used to skew the impute ratio for certain bone combinations.
	 *
	 * First element is a Pair containing the missing bone to the source bones.
	 *
	 * Second element is how reliable that pair is. Higher = missing bone relies more on source bones.
	 */
	private val combinationToReliability = mapOf(
		(BodyPart.WAIST to chestSet) to 0.6f,
		(BodyPart.WAIST to hipSet) to 1.0f,
		(BodyPart.WAIST to upperLegsSet) to 1.0f,
		(BodyPart.HIP to chestSet) to 1.0f,
		(BodyPart.HIP to waistSet) to 0.8f,
		(BodyPart.HIP to upperLegsSet) to 1.0f,
	)

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

		val updatedSpineBones = missingSpineParts.withIndex().associate { (chainIndex, bodyPart) ->
			val bone = boneInputs.getValue(bodyPart)

			// Get the first active bones above and below this one in the chain
			val (fromBodyParts, toBodyParts) = when (bodyPart) {
				BodyPart.WAIST -> {
					val from = chestSet.takeIf { hasChest } ?: return@associate bodyPart to bone
					val to = when {
						hasHip -> hipSet
						hasUpperLegs -> upperLegsSet
						else -> return@associate bodyPart to bone
					}
					from to to
				}

				BodyPart.HIP -> {
					val from = when {
						hasWaist -> waistSet
						hasChest -> chestSet
						else -> return@associate bodyPart to bone
					}
					val to = upperLegsSet.takeIf { hasUpperLegs } ?: return@associate bodyPart to bone
					from to to
				}

				else -> error("Invalid missing spine body part $bodyPart")
			}

			val fromReliability = combinationToReliability[(bodyPart to fromBodyParts)] ?: error("Invalid from body part combination $bodyPart, $fromBodyParts")
			val toReliability = combinationToReliability[(bodyPart to toBodyParts)] ?: error("Invalid to body part combination $bodyPart, $toBodyParts")

			val interpolateRatio = interpolateRatio(
				chainIndex,
				missingSpineParts.size,
				ratios.imputeSpineFromUpperToLower,
				ratios.imputeSpineCurvature,
				fromReliability,
				toReliability,
			)

			val fromRotation = boneInputs.resolveRotationFor(fromBodyParts)
			val toRotation = boneInputs.resolveRotationFor(toBodyParts)

			bodyPart to bone.copy(rawRotation = fromRotation.interpQ(toRotation, interpolateRatio))
		}

		return state.copy(boneInputs = boneInputs + updatedSpineBones)
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
