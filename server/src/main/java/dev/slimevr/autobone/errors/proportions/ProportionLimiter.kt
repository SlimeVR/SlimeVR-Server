package dev.slimevr.autobone.errors.proportions

import dev.slimevr.tracking.processor.HumanPoseManager

interface ProportionLimiter {
	fun getProportionError(humanPoseManager: HumanPoseManager, height: Float): Float
	val targetRatio: Float
}
