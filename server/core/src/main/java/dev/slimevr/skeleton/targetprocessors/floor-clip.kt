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
			val parentTargetY = mutableIkTargets[parentPart] ?: fk[parentPart]?.tailPosition ?: continue

			// Offset the parent target by the lowest child as well so that none of its active children are under the floor either.
			var lowestChildTargetY = parentTargetY.y
			for ((_, childPart) in iterateBodyPartHierarchy(parentPart, true)) {
				if (inputSkeleton[childPart]?.isRotationActive == false) continue
				val target = mutableIkTargets[childPart] ?: fk[childPart]?.tailPosition ?: continue
				if (target.y < lowestChildTargetY) lowestChildTargetY = target.y
			}
			val childOffset = (parentTargetY.y - lowestChildTargetY).coerceAtLeast(0f)

			// Snap the parent up.
			val targetY = parentTargetY.y.coerceAtLeast(floorLevel) + childOffset
			mutableIkTargets[parentPart] = Vector3(parentTargetY.x, targetY, parentTargetY.z)
		}
	}
}
