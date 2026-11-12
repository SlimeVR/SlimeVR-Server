package dev.slimevr.skeleton.targetprocessors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.BodyPartMap
import dev.slimevr.skeleton.ComputedSkeleton
import dev.slimevr.skeleton.IKTargets
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonTargetProcessor
import solarxr_protocol.datatypes.BodyPart
import kotlin.collections.set

/** Bone carrying the position to aim at, to the bone whose IK chain gets solved. */
val POSITIONAL_IK_TARGETS = arrayOf(
	BodyPart.LEFT_HAND to BodyPart.LEFT_LOWER_ARM,
	BodyPart.RIGHT_HAND to BodyPart.RIGHT_LOWER_ARM,

// 	BodyPart.HIP to BodyPart.LOWER_WAIST,

	BodyPart.LEFT_UPPER_LEG to BodyPart.LEFT_UPPER_LEG,
	BodyPart.RIGHT_UPPER_LEG to BodyPart.RIGHT_UPPER_LEG,

	BodyPart.LEFT_LOWER_LEG to BodyPart.LEFT_LOWER_LEG,
	BodyPart.RIGHT_LOWER_LEG to BodyPart.RIGHT_LOWER_LEG,

	BodyPart.LEFT_FOOT to BodyPart.LEFT_LOWER_LEG,
	BodyPart.RIGHT_FOOT to BodyPart.RIGHT_LOWER_LEG,
)

val requiredInactive = BodyPartMap(
	mapOf(
		BodyPart.LEFT_HAND to arrayOf(BodyPart.LEFT_SHOULDER, BodyPart.LEFT_UPPER_ARM, BodyPart.LEFT_LOWER_ARM),
		BodyPart.RIGHT_HAND to arrayOf(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_UPPER_ARM, BodyPart.RIGHT_LOWER_ARM),
	),
)

// Target IK to positional trackers
class PositionalTargetProcessor(
	val settings: Settings,
	val bodyParts: Array<Pair<BodyPart, BodyPart>> = POSITIONAL_IK_TARGETS,
) : SkeletonTargetProcessor {
	override fun process(mutableIkTargets: IKTargets, inputSkeleton: InputSkeleton, fk: ComputedSkeleton, floorLevel: Float) {
		if (!settings.context.state.value.data.skeletonConfig.toggles.useTrackerPositions) return
		for ((bodyPart, target) in bodyParts) {
			// TODO Use active rotations as IK constraints instead and fill in the blanks
			// Disable if you have arm trackers
			if (requiredInactive[bodyPart]?.any { inputSkeleton[it]?.isRotationActive == true } == true) continue

			mutableIkTargets[target] = inputSkeleton[bodyPart]?.position ?: continue
		}
	}
}
