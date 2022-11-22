package dev.slimevr.autobone.errors.proportions;

import dev.slimevr.vr.processor.skeleton.SkeletonConfig;


public interface ProportionLimiter {
	public float getProportionError(SkeletonConfig config, float height);
}
