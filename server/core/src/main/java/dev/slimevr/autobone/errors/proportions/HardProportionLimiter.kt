package dev.slimevr.autobone.errors.proportions

import dev.slimevr.tracking.processor.HumanPoseManager
import dev.slimevr.tracking.processor.config.SkeletonConfigOffsets

/**
 * @param targetRatio The bone to height ratio to target
 * @param skeletonConfigOffset The SkeletonConfigOffset to use for the length
 */
open class HardProportionLimiter(
	override val targetRatio: Float = 0f,
	override val skeletonConfigOffset: SkeletonConfigOffsets,
) : ProportionLimiter {
	override fun getProportionError(humanPoseManager: HumanPoseManager, height: Float): Float {
		val boneLength = humanPoseManager.getOffset(skeletonConfigOffset)
		return targetRatio - boneLength / height
	}
}
