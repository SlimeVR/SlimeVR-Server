package dev.slimevr.skeleton.targetprocessors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.ComputedSkeleton
import dev.slimevr.skeleton.IKTargets
import dev.slimevr.skeleton.SkeletonTargetProcessor
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart
import kotlin.collections.set

class FloorClipTargetProcessor(
	val settings: Settings,
	val bodyParts: Array<BodyPart> = arrayOf(BodyPart.LEFT_LOWER_LEG, BodyPart.RIGHT_LOWER_LEG),
) : SkeletonTargetProcessor {
	override fun process(fk: ComputedSkeleton, ikTargets: IKTargets, floorLevel: Float): IKTargets {
		if (!settings.context.state.value.data.skeletonConfig.toggles.floorClip) return ikTargets

		for (bodyPart in bodyParts) {
			// Get existing target or make a new one at the current bone position
			val target = ikTargets[bodyPart] ?: fk[bodyPart]?.tailPosition ?: continue
			// Snap the target up to the floor if it's under
			ikTargets[bodyPart] = Vector3(target.x, target.y.coerceAtLeast(floorLevel), target.z)
		}
		return ikTargets
	}
}
