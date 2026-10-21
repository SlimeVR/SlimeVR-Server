package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart

// Neck bends less when looking up
private const val UP_RATIO = 0.74f

// Neck bends slightly more when looking down.
private const val DOWN_RATIO = 1.14f

/**
 * Guesses where the neck is supposed to be when the user has a head tracker but no neck tracker.
 *
 * Tries to mitigate the problem where looking down make your whole tracking go down (or
 * knees bend if floor-clip is enabled).
 */
class NeckInputProcessor : SkeletonInputProcessor {
	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		val neckBone = mutableInputSkeleton[BodyPart.NECK] ?: return
		if (neckBone.isRotationActive) return
		val upperChestRotation = mutableInputSkeleton[BodyPart.UPPER_CHEST]?.rotation ?: return
		val headRotation = mutableInputSkeleton[BodyPart.HEAD]?.rotation ?: return

		// Work with the rotation delta to avoid axes being ambiguous
		val delta = upperChestRotation.inv() * headRotation

		// Decompose the delta into its twist (forward) and swing (remaining/sides) along the x-axis
		val forward = delta.project(Vector3.POS_X).unit()
		val side = delta / forward

		// Get the ratio depending on if the user is looking down or up
		val ratio = if (forward.x < 0f) DOWN_RATIO else UP_RATIO

		val scaledDelta = side * forward.pow(ratio)
		val newRotation = upperChestRotation * scaledDelta
		mutableInputSkeleton[BodyPart.NECK] = neckBone.copy(rotation = newRotation)
	}
}
