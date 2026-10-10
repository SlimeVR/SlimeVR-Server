package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import dev.slimevr.skeleton.findFirstParent
import dev.slimevr.skeleton.forEachBone
import solarxr_protocol.datatypes.BodyPart
import kotlin.collections.set

// Only allow child fallbacks in certain cases
val CHILD_FALLBACKS = mapOf(
	// Head and neck will fall back on spine
	BodyPart.HEAD to arrayOf(
		BodyPart.NECK,
		BodyPart.UPPER_CHEST,
		BodyPart.LOWER_CHEST,
		BodyPart.UPPER_WAIST,
		BodyPart.LOWER_WAIST,
		BodyPart.HIP,
	),
	BodyPart.NECK to arrayOf(
		BodyPart.UPPER_CHEST,
		BodyPart.LOWER_CHEST,
		BodyPart.UPPER_WAIST,
		BodyPart.LOWER_WAIST,
		BodyPart.HIP,
	),

	// Upper spine will fall back on lower spine
	BodyPart.UPPER_CHEST to arrayOf(
		BodyPart.LOWER_CHEST,
		BodyPart.UPPER_WAIST,
		BodyPart.LOWER_WAIST,
		BodyPart.HIP,
	),
	BodyPart.LOWER_CHEST to arrayOf(
		BodyPart.UPPER_WAIST,
		BodyPart.LOWER_WAIST,
		BodyPart.HIP,
	),
	BodyPart.UPPER_WAIST to arrayOf(
		BodyPart.LOWER_WAIST,
		BodyPart.HIP,
	),
	BodyPart.LOWER_WAIST to arrayOf(
		BodyPart.HIP,
	),

	// Upper leg will fall back on lower leg
	BodyPart.LEFT_UPPER_LEG to arrayOf(BodyPart.LEFT_LOWER_LEG, BodyPart.LEFT_FOOT),
	BodyPart.RIGHT_UPPER_LEG to arrayOf(BodyPart.RIGHT_LOWER_LEG, BodyPart.RIGHT_FOOT),
	BodyPart.LEFT_LOWER_LEG to arrayOf(BodyPart.LEFT_FOOT),
	BodyPart.RIGHT_LOWER_LEG to arrayOf(BodyPart.RIGHT_FOOT),
)

/**
 * Handles replacing rotations and accelerations of boneInputs that are not actively receiving data by
 * falling back to their parents or children.
 *
 * This does not produce reliable data. For that, see the direct link processor.
 */
class FallbackInputProcessor : SkeletonInputProcessor {
	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		mutableInputSkeleton.forEachBone { bodyPart, bone ->
			if (bone.isRotationActive && bone.isAccelerationActive) return@forEachBone
			val newRotation = if (!bone.isRotationActive) {
				val fallbackPart = bodyPart.findFirstParent { mutableInputSkeleton[it]?.isRotationActive == true }
					?: CHILD_FALLBACKS[bodyPart]?.firstOrNull { mutableInputSkeleton[it]?.isRotationActive == true }
				mutableInputSkeleton[fallbackPart]?.rotation?.eulerHeading() ?: bone.rotation
			} else {
				bone.rotation
			}
			val newAcceleration = if (!bone.isAccelerationActive) {
				val fallbackPart = bodyPart.findFirstParent { mutableInputSkeleton[it]?.isAccelerationActive == true }
					?: CHILD_FALLBACKS[bodyPart]?.firstOrNull { mutableInputSkeleton[it]?.isAccelerationActive == true }
				mutableInputSkeleton[fallbackPart]?.acceleration ?: bone.acceleration
			} else {
				bone.acceleration
			}
			mutableInputSkeleton[bodyPart] = bone.copy(rotation = newRotation, acceleration = newAcceleration)
		}
	}
}
