package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import solarxr_protocol.datatypes.BodyPart

/**
 * Handles rotations of inactive posterior bones.
 */
class PosteriorDirectLinkInputProcessor : SkeletonInputProcessor {

	private val posteriorToSource = arrayOf(
		BodyPart.LEFT_POSTERIOR to BodyPart.HIP,
		BodyPart.RIGHT_POSTERIOR to BodyPart.HIP,
	)

	override fun process(
		mutableInputSkeleton: InputSkeleton,
		skeletonHeight: Float
	) {
		for ((bodyPart, source) in posteriorToSource) {
			val bone = mutableInputSkeleton[bodyPart] ?: continue
			if (bone.isRotationActive) continue

			val sourceBone = mutableInputSkeleton[source]

			mutableInputSkeleton[bodyPart] =
				bone.copy(
					rotation = sourceBone?.rotation ?: bone.rotation
				)
		}

		// Tail gets the average of left + right posterior
		val tail = mutableInputSkeleton[BodyPart.TAIL]
		if (tail != null && !tail.isRotationActive) {
			val left = mutableInputSkeleton[BodyPart.LEFT_POSTERIOR]
			val right = mutableInputSkeleton[BodyPart.RIGHT_POSTERIOR]

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

			mutableInputSkeleton[BodyPart.TAIL] =
				tail.copy(rotation = rotation)
		}
	}
}
