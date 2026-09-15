package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.BODY_PART_CONSTRAINT_MAP
import dev.slimevr.skeleton.BoneId
import dev.slimevr.skeleton.Constraint
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import dev.slimevr.skeleton.constrainSkeleton
import dev.slimevr.skeleton.resolveToBoneIds

class ConstraintInputProcessor(
	val settings: Settings,
) : SkeletonInputProcessor {
	private val constraints: Map<BoneId, Constraint> = BODY_PART_CONSTRAINT_MAP.resolveToBoneIds()

	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		val skeletonConfig = settings.context.state.value.data.skeletonConfig
		if (!skeletonConfig.toggles.enforceConstraints) return

		constrainSkeleton(mutableInputSkeleton, constraints)
	}
}
