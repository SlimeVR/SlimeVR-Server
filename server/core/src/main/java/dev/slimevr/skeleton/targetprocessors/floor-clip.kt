package dev.slimevr.skeleton.targetprocessors

import dev.slimevr.skeleton.ComputedSkeleton
import dev.slimevr.skeleton.IKTargets
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.Skeleton
import dev.slimevr.skeleton.SkeletonTargetProcessor
import dev.slimevr.skeleton.iterateBodyPartHierarchy
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart

class FloorClipTargetProcessor(
	val skeleton: Skeleton,
	val targetParts: Array<BodyPart> = arrayOf(
		BodyPart.LEFT_LOWER_LEG,
		BodyPart.RIGHT_LOWER_LEG,
	),
) : SkeletonTargetProcessor {
	// TODO don't just correct ankles, also correct some of the hip (20% in old code). Probably to be done on IK directly.
	override fun process(mutableIkTargets: IKTargets, inputSkeleton: InputSkeleton, fk: ComputedSkeleton, floorLevel: Float) {
		if (!skeleton.effectiveFloorClip) return

		for (parentPart in targetParts) {
			// Get existing target or make a new one at the current bone position.
			val parentTarget = mutableIkTargets[parentPart] ?: fk[parentPart]?.tailPosition ?: continue

			// Find the lowest y position, either the parent's or its children's.
			var lowestTargetY = parentTarget.y
			for ((_, childPart) in iterateBodyPartHierarchy(parentPart, true)) {
				if (inputSkeleton[childPart]?.isRotationActive == false) continue
				val target = mutableIkTargets[childPart] ?: fk[childPart]?.tailPosition ?: continue
				if (target.y < lowestTargetY) lowestTargetY = target.y
			}

			// Snap the parent up
			val offsetY = (floorLevel - lowestTargetY).coerceAtLeast(0f)
			mutableIkTargets[parentPart] = Vector3(parentTarget.x, parentTarget.y + offsetY, parentTarget.z)
		}
	}
}
