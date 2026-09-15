package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.config.Settings
import dev.slimevr.resourcepacks.bones.CompiledSkeleton
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import dev.slimevr.skeleton.constrainSkeleton

class ConstraintInputProcessor(
	val settings: Settings,
	definition: CompiledSkeleton,
) : SkeletonInputProcessor {
	private val constraints = definition.constraints

	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		val skeletonConfig = settings.context.state.value.data.skeletonConfig
		if (!skeletonConfig.toggles.enforceConstraints) return

		constrainSkeleton(mutableInputSkeleton, constraints)
	}
}
