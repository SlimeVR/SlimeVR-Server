package dev.slimevr.autobone.errors.proportions;

import dev.slimevr.vr.processor.HumanPoseManager;


public interface ProportionLimiter {
	float getProportionError(HumanPoseManager humanPoseManager, float height);
}
