package dev.slimevr.autobone.errors.proportions

import dev.slimevr.tracking.processor.HumanPoseManager
import dev.slimevr.tracking.processor.config.SkeletonConfigOffsets
import kotlin.math.*

class ProportionLimiter {
	val targetRatio: Float
	val skeletonConfigOffset: SkeletonConfigOffsets
	val scaleByHeight: Boolean

	val positiveRange: Float
	val negativeRange: Float

	/**
	 * @param targetRatio The bone to height ratio to target
	 * @param skeletonConfigOffset The SkeletonConfigOffset to use for the length
	 * @param range The range from the target ratio to accept (ex. 0.1)
	 * @param scaleByHeight True if the bone length will be scaled by the height
	 */
	constructor(
		targetRatio: Float,
		skeletonConfigOffset: SkeletonConfigOffsets,
		range: Float,
		scaleByHeight: Boolean = true,
	) {
		this.targetRatio = targetRatio
		this.skeletonConfigOffset = skeletonConfigOffset
		this.scaleByHeight = scaleByHeight

		// Handle if someone puts in a negative value
		val absRange = abs(range)
		positiveRange = absRange
		negativeRange = -absRange
	}

	/**
	 * @param targetRatio The bone to height ratio to target
	 * @param skeletonConfigOffset The SkeletonConfigOffset to use for the length
	 * @param positiveRange The positive range from the target ratio to accept
	 * (ex. 0.1)
	 * @param negativeRange The negative range from the target ratio to accept
	 * (ex. -0.1)
	 * @param scaleByHeight True if the bone length will be scaled by the height
	 */
	constructor(
		targetRatio: Float,
		skeletonConfigOffset: SkeletonConfigOffsets,
		positiveRange: Float,
		negativeRange: Float,
		scaleByHeight: Boolean = true,
	) {
		// If the positive range is less than the negative range, something is wrong
		require(positiveRange >= negativeRange) { "positiveRange must not be less than negativeRange" }

		this.targetRatio = targetRatio
		this.skeletonConfigOffset = skeletonConfigOffset
		this.scaleByHeight = scaleByHeight

		this.positiveRange = positiveRange
		this.negativeRange = negativeRange
	}

	fun getProportionError(humanPoseManager: HumanPoseManager, height: Float): Float {
		val boneLength = humanPoseManager.getOffset(skeletonConfigOffset)
		val ratioOffset = if (scaleByHeight) {
			targetRatio - boneLength / height
		} else {
			targetRatio - boneLength
		}

		// If the range is exceeded, return the offset from the range limit
		if (ratioOffset > positiveRange) {
			return ratioOffset - positiveRange
		} else if (ratioOffset < negativeRange) {
			return ratioOffset - negativeRange
		}
		return 0f
	}
}
