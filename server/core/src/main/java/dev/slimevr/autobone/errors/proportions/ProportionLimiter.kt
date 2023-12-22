package dev.slimevr.autobone.errors.proportions

import dev.slimevr.tracking.processor.HumanPoseManager
import dev.slimevr.tracking.processor.config.SkeletonConfigOffsets

interface ProportionLimiter {
	fun getProportionError(humanPoseManager: HumanPoseManager, height: Float): Float
	val targetRatio: Float
	val skeletonConfigOffset: SkeletonConfigOffsets
}
