package dev.slimevr.skeleton

import dev.slimevr.config.Settings
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart

/**
 * Handles replacing rotations of boneInputs that are not actively receiving data by either
 * falling back to their parent's yaw or the identity rotation.
 */
class BoneFallbackProcessor : SkeletonProcessor {
	override fun process(state: SkeletonState): SkeletonState {
		val boneInputs = state.boneInputs

		return state.copy(
			boneInputs = boneInputs.mapValues { (bodyPart, bone) ->
				if (bone.isActive) return@mapValues bone

				val firstActiveParentBone = boneInputs[bodyPart.findFirstParent { boneInputs[it]?.isActive == true }]
				val fallbackRotation = firstActiveParentBone?.rawRotation?.project(Vector3.POS_Y)?.unit() ?: Quaternion.IDENTITY
				bone.copy(rawRotation = fallbackRotation)
			},
		)
	}
}

/**
 * Handles copying source bones' rotations to their linked bones.
 */
class BoneLinkProcessor : SkeletonProcessor {
	/**
	 * First element is the linked BodyPart.
	 *
	 * Second element is the BodyPart the first element is linked to.
	 */
	private val linkedToSource = mapOf(
		BodyPart.HEAD to BodyPart.NECK,
		BodyPart.NECK to BodyPart.HEAD,

		BodyPart.LEFT_HIP to BodyPart.HIP,
		BodyPart.RIGHT_HIP to BodyPart.HIP,

		BodyPart.LEFT_FOOT to BodyPart.LEFT_LOWER_LEG,
		BodyPart.RIGHT_FOOT to BodyPart.RIGHT_LOWER_LEG,

		BodyPart.LEFT_SHOULDER to BodyPart.UPPER_CHEST,
		BodyPart.RIGHT_SHOULDER to BodyPart.UPPER_CHEST,

		BodyPart.LEFT_UPPER_ARM to BodyPart.LEFT_SHOULDER,
		BodyPart.RIGHT_UPPER_ARM to BodyPart.RIGHT_SHOULDER,

		BodyPart.LEFT_LOWER_ARM to BodyPart.LEFT_UPPER_ARM,
		BodyPart.RIGHT_LOWER_ARM to BodyPart.RIGHT_UPPER_ARM,

		BodyPart.LEFT_HAND to BodyPart.LEFT_LOWER_ARM,
		BodyPart.RIGHT_HAND to BodyPart.RIGHT_LOWER_ARM,
	)

	override fun process(state: SkeletonState): SkeletonState {
		val updatedBoneInputs = state.boneInputs.toMutableMap()

		for ((bodyPart, bone) in state.boneInputs) {
			if (bone.isActive || bodyPart !in linkedToSource) continue

			val sourceBone = updatedBoneInputs[linkedToSource[bodyPart]]
			updatedBoneInputs[bodyPart] = bone.copy(rawRotation = sourceBone?.rawRotation ?: bone.rawRotation)
		}

		return state.copy(boneInputs = updatedBoneInputs)
	}
}

class SpineLinkProcessor : SkeletonProcessor {
	/**
	 * First element is the BodyPart whose rawBone is not actively receiving data.
	 * Second element contains a set of BodyParts whose rotation should be used as a fallback prioritized from first to last.
	 */
	private val missingToFallbacks = mapOf(
		BodyPart.UPPER_CHEST to setOf(BodyPart.CHEST, BodyPart.WAIST, BodyPart.HIP),
		BodyPart.CHEST to setOf(BodyPart.UPPER_CHEST, BodyPart.WAIST, BodyPart.HIP),
		BodyPart.WAIST to setOf(BodyPart.CHEST, BodyPart.HIP, BodyPart.UPPER_CHEST),
		BodyPart.HIP to setOf(BodyPart.WAIST, BodyPart.CHEST, BodyPart.UPPER_CHEST),
	)

	override fun process(state: SkeletonState): SkeletonState {
		val boneInputs = state.boneInputs

		return state.copy(
			boneInputs = boneInputs.mapValues { (bodyPart, bone) ->
				if (bone.isActive) return@mapValues bone

				val closestActiveBone = missingToFallbacks[bodyPart]
					?.firstNotNullOfOrNull { part ->
						boneInputs[part]?.takeIf { it.isActive }
					}
				bone.copy(
					rawRotation = closestActiveBone?.rawRotation ?: bone.rawRotation,
				)
			},
		)
	}
}

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
	 * First element is a Double containing the missing bone to the source bones.
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

		return state.copy(
			boneInputs = boneInputs.mapValues { (bodyPart, bone) ->
				val chainIndex = missingSpineParts.indexOf(bodyPart)
				if (chainIndex == -1) return@mapValues bone

				// Get the first active bones above and below this one in the chain
				val (fromBodyPart, toBodyPart) = when (bodyPart) {
					BodyPart.WAIST -> {
						val from = chestSet.takeIf { hasChest } ?: return@mapValues bone
						val to = when {
							hasHip -> hipSet
							hasUpperLegs -> upperLegsSet
							else -> return@mapValues bone
						}
						from to to
					}

					BodyPart.HIP -> {
						val from = when {
							hasWaist -> waistSet
							hasChest -> chestSet
							else -> return@mapValues bone
						}
						val to = upperLegsSet.takeIf { hasUpperLegs } ?: return@mapValues bone
						from to to
					}

					else -> error("Invalid missing spine body part $bodyPart")
				}

				val fromReliability = combinationToReliability[(bodyPart to fromBodyPart)] ?: error("Invalid from body part combination $bodyPart, $fromBodyPart")
				val toReliability = combinationToReliability[(bodyPart to toBodyPart)] ?: error("Invalid to body part combination $bodyPart, $toBodyPart")

				val interpolateRatio = interpolateRatio(
					chainIndex,
					missingSpineParts.size,
					ratios.imputeSpineFromUpperToLower,
					ratios.imputeSpineCurvature,
					fromReliability,
					toReliability,
				)

				val fromRotation = resolveRotation(boneInputs, fromBodyPart)
				val toRotation = resolveRotation(boneInputs, toBodyPart)

				bone.copy(rawRotation = fromRotation.interpQ(toRotation, interpolateRatio))
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

	/**
	 * Returns the average rotation of the BodyParts.
	 */
	private fun resolveRotation(boneInputs: Map<BodyPart, BoneInput>, bodyParts: Set<BodyPart>): Quaternion {
		val rotations = bodyParts.mapNotNull { boneInputs[it]?.rawRotation }
		return rotations.reduceIndexedOrNull { index, acc, rotation ->
			acc.lerpQ(rotation, 1f / (index + 1))
		} ?: Quaternion.IDENTITY
	}
}
