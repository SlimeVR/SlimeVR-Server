package dev.slimevr.skeleton.targetprocessors

import dev.slimevr.skeleton.ComputedSkeleton
import dev.slimevr.skeleton.IKTargets
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.Skeleton
import dev.slimevr.skeleton.SkeletonTargetProcessor
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart

class FloorClipTargetProcessor(
	val skeleton: Skeleton,
) : SkeletonTargetProcessor {
	val ankleFootParts: Array<Pair<BodyPart, BodyPart>> = arrayOf(
		BodyPart.LEFT_LOWER_LEG to BodyPart.LEFT_FOOT,
		BodyPart.RIGHT_LOWER_LEG to BodyPart.RIGHT_FOOT,
	)

	// TODO don't just correct feet, also correct some of the hip (20% in old code). Probably to be done on IK directly.
	override fun process(mutableIkTargets: IKTargets, inputSkeleton: InputSkeleton, fk: ComputedSkeleton, floorLevel: Float) {
		if (!skeleton.effectiveFloorClip) return

		for ((anklePart, footPart) in ankleFootParts) {
			// Get existing target or make a new one at the current bone position
			val ankleTarget = mutableIkTargets[anklePart] ?: fk[anklePart]?.tailPosition ?: continue
			val footTarget = mutableIkTargets[footPart] ?: fk[footPart]?.tailPosition ?: continue

			// Snap the ankle up but foot touch floor
			val ankleToFoot = (ankleTarget.y - footTarget.y).coerceAtLeast(0f)
			val targetY = ankleTarget.y.coerceAtLeast(floorLevel) + ankleToFoot
			mutableIkTargets[anklePart] = Vector3(ankleTarget.x, targetY, ankleTarget.z)
		}
	}
}
