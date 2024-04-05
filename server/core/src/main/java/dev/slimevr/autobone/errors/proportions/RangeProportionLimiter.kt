package dev.slimevr.autobone.errors.proportions

import dev.slimevr.tracking.processor.HumanPoseManager
import dev.slimevr.tracking.processor.config.SkeletonConfigOffsets
import kotlin.math.*

class RangeProportionLimiter : HardProportionLimiter {
	private val targetPositiveRange: Float
	private val targetNegativeRange: Float

	/**
	 * @param targetRatio The bone to height ratio to target
	 * @param skeletonConfigOffset The SkeletonConfigOffset to use for the length
	 * @param range The range from the target ratio to accept (ex. 0.1)
	 */
	constructor(
		targetRatio: Float,
		skeletonConfigOffset: SkeletonConfigOffsets,
		range: Float,
	) : super(targetRatio, skeletonConfigOffset) {
		val absRange = abs(range)

		// Handle if someone puts in a negative value
		targetPositiveRange = absRange
		targetNegativeRange = -absRange
	}

	/**
	 * @param targetRatio The bone to height ratio to target
	 * @param skeletonConfigOffset The SkeletonConfigOffset to use for the length
	 * @param positiveRange The positive range from the target ratio to accept
	 * (ex. 0.1)
	 * @param negativeRange The negative range from the target ratio to accept
	 * (ex. -0.1)
	 */
	constructor(
		targetRatio: Float,
		skeletonConfigOffset: SkeletonConfigOffsets,
		positiveRange: Float,
		negativeRange: Float,
	) : super(targetRatio, skeletonConfigOffset) {

		// If the positive range is less than the negative range, something is
		// wrong
		require(positiveRange >= negativeRange) { "positiveRange must not be less than negativeRange" }
		targetPositiveRange = positiveRange
		targetNegativeRange = negativeRange
	}

	override fun getProportionError(humanPoseManager: HumanPoseManager, height: Float): Float {
		val boneLength = humanPoseManager.getOffset(skeletonConfigOffset)
		val ratioOffset = targetRatio - boneLength / height

		// If the range is exceeded, return the offset from the range limit
		if (ratioOffset > targetPositiveRange) {
			return ratioOffset - targetPositiveRange
		} else if (ratioOffset < targetNegativeRange) {
			return ratioOffset - targetNegativeRange
		}
		return 0f
	}
}
