package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.BODY_PART_CONSTRAINT_MAP
import dev.slimevr.skeleton.BodyPartMap
import dev.slimevr.skeleton.Constraint
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import dev.slimevr.skeleton.constrainSkeleton

class ConstraintInputProcessor(
	val settings: Settings,
	val constraints: BodyPartMap<Constraint> = BODY_PART_CONSTRAINT_MAP,
) : SkeletonInputProcessor {
	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		val skeletonConfig = settings.context.state.value.data.skeletonConfig
		if (!skeletonConfig.toggles.enforceConstraints) return

		constrainSkeleton(mutableInputSkeleton, constraints)
	}
}
