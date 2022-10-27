package dev.slimevr.autobone.errors.proportions;

import java.util.function.Function;

import dev.slimevr.vr.processor.skeleton.SkeletonConfig;


public class HardProportionLimiter implements ProportionLimiter {

	protected final float targetRatio;
	protected final Function<SkeletonConfig, Float> boneLengthFunction;

	/**
	 * @param targetRatio The bone to height ratio to target
	 * @param boneLengthFunction A function that takes a SkeletonConfig object
	 * and returns the bone length
	 */
	public HardProportionLimiter(
		float targetRatio,
		Function<SkeletonConfig, Float> boneLengthFunction
	) {
		this.targetRatio = targetRatio;
		this.boneLengthFunction = boneLengthFunction;
	}

	@Override
	public float getProportionError(SkeletonConfig config, float height) {
		float boneLength = boneLengthFunction.apply(config);
		return targetRatio - (boneLength / height);
	}
}
