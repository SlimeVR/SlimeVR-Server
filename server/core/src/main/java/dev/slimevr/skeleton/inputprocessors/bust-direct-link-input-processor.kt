package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import dev.slimevr.skeleton.mutateCopy
import solarxr_protocol.datatypes.BodyPart

/**
 * Handles rotations of inactive toe bones.
 */
class BustDirectLinkInputProcessor : SkeletonInputProcessor {
	/**
	 * First element is the linked BodyPart.
	 *
	 * Second element is the BodyPart the first element is linked to.
	 */
	private val bustToSource = arrayOf(
		BodyPart.LEFT_BUST to BodyPart.CHEST,
		BodyPart.RIGHT_BUST to BodyPart.CHEST,
	)

	override fun process(inputSkeleton: InputSkeleton, skeletonHeight: Float): InputSkeleton = inputSkeleton.mutateCopy { updated ->
		for ((bodyPart, source) in bustToSource) {
			val bone = updated[bodyPart] ?: continue
			if (bone.isRotationActive) continue
			val sourceBone = updated[source]
			updated[bodyPart] =
				bone.copy(rotation = sourceBone?.rotation ?: bone.rotation)
		}
	}
}
