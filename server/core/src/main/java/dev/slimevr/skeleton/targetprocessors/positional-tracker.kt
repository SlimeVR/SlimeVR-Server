package dev.slimevr.skeleton.targetprocessors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.ComputedSkeleton
import dev.slimevr.skeleton.IKTargets
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonTargetProcessor
import solarxr_protocol.datatypes.BodyPart
import kotlin.collections.set

// Target IK to positional trackers
class PositionalTargetProcessor(
	val settings: Settings,
	val bodyParts: Array<Pair<BodyPart, BodyPart>> = arrayOf(
		BodyPart.LEFT_HAND to BodyPart.LEFT_LOWER_ARM,
		BodyPart.RIGHT_HAND to BodyPart.RIGHT_LOWER_ARM,
	),
) : SkeletonTargetProcessor {
	override fun process(mutableIkTargets: IKTargets, inputSkeleton: InputSkeleton, fk: ComputedSkeleton, floorLevel: Float) {
		if (!settings.context.state.value.data.skeletonConfig.toggles.useTrackerPositions) return
		for ((bodyPart, target) in bodyParts) {
			mutableIkTargets[target] = inputSkeleton[bodyPart]?.position ?: continue
		}
	}
}
