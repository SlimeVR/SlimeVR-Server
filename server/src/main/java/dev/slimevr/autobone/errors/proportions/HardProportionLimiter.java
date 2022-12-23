package dev.slimevr.autobone.errors.proportions;

import dev.slimevr.vr.processor.HumanPoseManager;

import java.util.function.Function;


public class HardProportionLimiter implements ProportionLimiter {

	protected final float targetRatio;
	protected final Function<HumanPoseManager, Float> boneLengthFunction;

	/**
	 * @param targetRatio The bone to height ratio to target
	 * @param boneLengthFunction A function that takes a SkeletonConfig object
	 * and returns the bone length
	 */
	public HardProportionLimiter(
		float targetRatio,
		Function<HumanPoseManager, Float> boneLengthFunction
	) {
		this.targetRatio = targetRatio;
		this.boneLengthFunction = boneLengthFunction;
	}

	@Override
	public float getProportionError(HumanPoseManager humanPoseManager, float height) {
		float boneLength = boneLengthFunction.apply(humanPoseManager);
		return targetRatio - (boneLength / height);
	}
}
