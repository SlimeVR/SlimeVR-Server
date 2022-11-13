package dev.slimevr.autobone.errors.proportions;

import java.util.function.Function;

import com.jme3.math.FastMath;

import dev.slimevr.vr.processor.skeleton.SkeletonConfig;


public class RangeProportionLimiter extends HardProportionLimiter {

	protected final float targetPositiveRange;
	protected final float targetNegativeRange;

	/**
	 * @param targetRatio The bone to height ratio to target
	 * @param boneLengthFunction A function that takes a SkeletonConfig object
	 * and returns the bone length
	 * @param range The range from the target ratio to accept (ex. 0.1)
	 */
	public RangeProportionLimiter(
		float targetRatio,
		Function<SkeletonConfig, Float> boneLengthFunction,
		float range
	) {
		super(targetRatio, boneLengthFunction);

		// Handle if someone puts in a negative value
		range = FastMath.abs(range);
		targetPositiveRange = range;
		targetNegativeRange = -range;
	}

	/**
	 * @param targetRatio The bone to height ratio to target
	 * @param boneLengthFunction A function that takes a SkeletonConfig object
	 * and returns the bone length
	 * @param positiveRange The positive range from the target ratio to accept
	 * (ex. 0.1)
	 * @param negativeRange The negative range from the target ratio to accept
	 * (ex. -0.1)
	 */
	public RangeProportionLimiter(
		float targetRatio,
		Function<SkeletonConfig, Float> boneLengthFunction,
		float positiveRange,
		float negativeRange
	) {
		super(targetRatio, boneLengthFunction);

		// If the positive range is less than the negative range, something is
		// wrong
		if (positiveRange < negativeRange) {
			throw new IllegalArgumentException("positiveRange must not be less than negativeRange");
		}

		targetPositiveRange = positiveRange;
		targetNegativeRange = negativeRange;
	}

	@Override
	public float getProportionError(SkeletonConfig config, float height) {
		float boneLength = boneLengthFunction.apply(config);
		float ratioOffset = targetRatio - (boneLength / height);

		// If the range is exceeded, return the offset from the range limit
		if (ratioOffset > targetPositiveRange) {
			return ratioOffset - targetPositiveRange;
		} else if (ratioOffset < targetNegativeRange) {
			return ratioOffset - targetNegativeRange;
		}

		return 0f;
	}
}
