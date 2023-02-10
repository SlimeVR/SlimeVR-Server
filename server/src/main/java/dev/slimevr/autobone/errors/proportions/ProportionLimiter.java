package dev.slimevr.autobone.errors.proportions;

import dev.slimevr.tracking.processor.HumanPoseManager;


public interface ProportionLimiter {
	public float getProportionError(HumanPoseManager humanPoseManager, float height);

	public float getTargetRatio();
}
