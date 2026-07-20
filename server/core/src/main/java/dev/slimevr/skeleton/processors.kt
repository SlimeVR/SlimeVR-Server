package dev.slimevr.skeleton

import dev.slimevr.config.Settings
import dev.slimevr.config.SkeletonRatiosConfig
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

				val firstActiveParentBone = boneInputs[findBodyPartParent(bodyPart) { boneInputs[it]?.isActive == true }]
				val fallbackRotation = firstActiveParentBone?.rawRotation?.project(Vector3.POS_Y)?.unit() ?: Quaternion.IDENTITY
				bone.copy(rawRotation = fallbackRotation)
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
class ImputeSpineProcessor(val settings: Settings) : SkeletonProcessor {
	private val bonesToImpute = listOf(BodyPart.WAIST, BodyPart.HIP)

	override fun process(state: SkeletonState): SkeletonState {
		val ratios = settings.context.state.value.data.skeletonConfig.ratios
		val imputeSpineFromUpperToLower = ratios.imputeSpineFromUpperToLower
		val imputeSpineCurvature = ratios.imputeSpineCurvature
		val boneInputs = state.boneInputs

		return state.copy(
			boneInputs = boneInputs.mapValues { (bodyPart, bone) ->
				if (bone.isActive || bodyPart !in bonesToImpute) return@mapValues bone

				bone.copy(rawRotation = bone.rawRotation)
			},
		)
	}

	private fun imputeBoneRotation(fromRotation: Quaternion, toRotation: Quaternion): Quaternion {
		return Quaternion.IDENTITY
	}

	private fun getAverageLegRotation(boneInputs: Map<BodyPart, BoneInput>): Quaternion? {
		val leftLeg = boneInputs[BodyPart.LEFT_UPPER_LEG]?.takeIf { it.isActive } ?: return null
		val rightLeg = boneInputs[BodyPart.RIGHT_UPPER_LEG]?.takeIf { it.isActive } ?: return null
		return leftLeg.rawRotation.lerpQ(rightLeg.rawRotation, 0.5f)
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