package dev.slimevr.skeleton.targetprocessors

import dev.slimevr.bones.BodyPart
import dev.slimevr.bones.BoneId
import dev.slimevr.bones.boneId
import dev.slimevr.config.Settings
import dev.slimevr.skeleton.ComputedSkeleton
import dev.slimevr.skeleton.IKTargets
import dev.slimevr.skeleton.SkeletonTargetProcessor
import io.github.axisangles.ktmath.Vector3

class FloorClipTargetProcessor(
	val settings: Settings,
	bodyParts: Array<BodyPart> = arrayOf(BodyPart.LEFT_LOWER_LEG, BodyPart.RIGHT_LOWER_LEG),
) : SkeletonTargetProcessor {
	private val boneIds: Array<BoneId> = bodyParts.map { it.boneId }.toTypedArray()

	override fun process(mutableIkTargets: IKTargets, fk: ComputedSkeleton, floorLevel: Float) {
		if (!settings.context.state.value.data.skeletonConfig.toggles.floorClip) return

		for (boneId in boneIds) {
			// Get existing target or make a new one at the current bone position
			val target = mutableIkTargets[boneId] ?: fk[boneId]?.tailPosition ?: continue
			// Snap the target up to the floor if it's under
			mutableIkTargets[boneId] = Vector3(target.x, target.y.coerceAtLeast(floorLevel), target.z)
		}
	}
}
