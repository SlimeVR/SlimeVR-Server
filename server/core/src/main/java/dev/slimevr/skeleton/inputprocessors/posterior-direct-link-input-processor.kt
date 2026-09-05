package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import dev.slimevr.skeleton.mutateCopy
import solarxr_protocol.datatypes.BodyPart

/**
 * Handles rotations of inactive posterior bones.
 */
class PosteriorDirectLinkInputProcessor : SkeletonInputProcessor {

	private val posteriorToSource = arrayOf(
		BodyPart.LEFT_POSTERIOR to BodyPart.LEFT_HIP,
		BodyPart.RIGHT_POSTERIOR to BodyPart.RIGHT_HIP,
	)

	override fun process(
		inputSkeleton: InputSkeleton,
		skeletonHeight: Float
	): InputSkeleton = inputSkeleton.mutateCopy { updated ->
		for ((bodyPart, source) in posteriorToSource) {
			val bone = updated[bodyPart] ?: continue
			if (bone.isRotationActive) continue

			val sourceBone = updated[source]

			updated[bodyPart] =
				bone.copy(
					rotation = sourceBone?.rotation ?: bone.rotation
				)
		}

		// Tail gets the average of left + right posterior
		val tail = updated[BodyPart.TAIL]
		if (tail != null && !tail.isRotationActive) {
			val left = updated[BodyPart.LEFT_POSTERIOR]
			val right = updated[BodyPart.RIGHT_POSTERIOR]

			val rotation = when {
				left != null && right != null ->
					left.rotation.interpR(right.rotation, 0.5f)

				left != null ->
					left.rotation

				right != null ->
					right.rotation

				else ->
					tail.rotation
			}

			updated[BodyPart.TAIL] =
				tail.copy(rotation = rotation)
		}
	}
}
