package dev.slimevr.skeleton

import dev.slimevr.config.Settings
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart

interface SkeletonProcessor {
	fun process(state: SkeletonState): SkeletonState
}

/**
 * Handles replacing rotations of boneInputs that are not actively receiving data by either
 * falling back to their parent's yaw or the identity rotation.
 */
class FallbackProcessor : SkeletonProcessor {
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
 * Handles copying bones rotations to their linked bones.
 *
 * Useful for bones whose sole purpose is offsets or bones that rarely have trackers on them.
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

		BodyPart.UPPER_CHEST to BodyPart.CHEST,
		BodyPart.CHEST to BodyPart.UPPER_CHEST,

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

/**
 * Handles imputing the rotation of spine bones that are not actively receiving data from the rotations
 * of nearby bones.
 *
 * Similar to FallbackProcessor specifically for the waist and hip.
 */
class ImputeSpineProcessor(val settings: Settings) : SkeletonProcessor {


	override fun process(state: SkeletonState): SkeletonState {
		val boneInputs = state.boneInputs
		val ratios = settings.context.state.value.data.skeletonConfig.ratios
		val imputeSpineFromUpperToLower = ratios.imputeSpineFromUpperToLower
		val imputeSpineCurvature = ratios.imputeSpineCurvature

		val chestBone = boneInputs[BodyPart.CHEST]
		val waistBone = boneInputs[BodyPart.WAIST]?.takeIf { it.isActive }
		val hipBone = boneInputs[BodyPart.HIP]?.takeIf { it.isActive }
		val leftUpperLegBone = boneInputs[BodyPart.LEFT_UPPER_LEG]
		val rightUpperLegBone = boneInputs[BodyPart.RIGHT_UPPER_LEG]
		val missingSpineParts = buildList {
			if (waistBone == null) add(BodyPart.WAIST)
			if (hipBone == null) add(BodyPart.HIP)
		}

		val chestRotation = chestBone?.rawRotation ?: Quaternion.IDENTITY
		val waistRotation = waistBone?.rawRotation
		val hipRotation = hipBone?.rawRotation
		val upperLegsRotation = leftUpperLegBone?.rawRotation?.lerpQ(
			rightUpperLegBone?.rawRotation ?: Quaternion.IDENTITY,
			0.5f
		) ?: Quaternion.IDENTITY

		return state.copy(
			boneInputs = boneInputs.mapValues { (bodyPart, bone) ->
				val chainIndex = missingSpineParts.indexOf(bodyPart)
				if (chainIndex == -1) return@mapValues bone

				// Nearest active bone above and below this one in the chain
				val fromRotation = if (bodyPart == BodyPart.WAIST) chestRotation else waistRotation ?: chestRotation
				val toRotation = if (bodyPart == BodyPart.WAIST) hipRotation ?: upperLegsRotation else upperLegsRotation

				// TODO remap properly both ratio and curvature
				val remapCenter = if (bodyPart == BodyPart.WAIST) 0.3f else if (waistRotation != null) 0.4f else 0.5f
				val ratio = remap(imputeSpineFromUpperToLower, remapCenter)
				val curvature = imputeSpineCurvature

				val interpolateRatio = if(missingSpineParts.count() <= 1) {
					// Single missing bone; just use the ratio directly
					ratio
				} else {
					ratio + ((chainIndex - ratio) * curvature)
				}

				bone.copy(rawRotation = fromRotation.interpQ(toRotation, interpolateRatio))
			},
		)
	}

	private val DEFAULT_UPPER_LOWER = 0.5f
	private fun remap(value: Float, newCenter: Float): Float =
		if (value <= DEFAULT_UPPER_LOWER) {
			value * (newCenter / DEFAULT_UPPER_LOWER)
		} else {
			newCenter + (value - DEFAULT_UPPER_LOWER) * ((1f - newCenter) / (1f - DEFAULT_UPPER_LOWER))
		}
}
