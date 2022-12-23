package dev.slimevr.autobone.errors.proportions;

import dev.slimevr.tracking.processor.HumanPoseManager;


public interface ProportionLimiter {
	float getProportionError(HumanPoseManager humanPoseManager, float height);
}
