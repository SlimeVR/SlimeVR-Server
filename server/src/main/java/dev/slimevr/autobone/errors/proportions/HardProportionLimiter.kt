package dev.slimevr.autobone.errors.proportions

import dev.slimevr.tracking.processor.HumanPoseManager
import java.util.function.Function

/**
 * @param targetRatio The bone to height ratio to target
 * @param boneLengthFunction A function that takes a SkeletonConfig object
 * and returns the bone length
 */
open class HardProportionLimiter(
	override val targetRatio: Float = 0f,
	protected val boneLengthFunction: Function<HumanPoseManager, Float>,
) : ProportionLimiter {
	override fun getProportionError(humanPoseManager: HumanPoseManager, height: Float): Float {
		val boneLength = boneLengthFunction.apply(humanPoseManager)
		return targetRatio - boneLength / height
	}
}
