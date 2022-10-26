package dev.slimevr.autobone.errors;


import com.jme3.math.FastMath;

import dev.slimevr.autobone.AutoBoneTrainingStep;
import dev.slimevr.autobone.errors.proportions.ProportionLimiter;
import dev.slimevr.autobone.errors.proportions.RangeProportionLimiter;
import dev.slimevr.vr.processor.skeleton.SkeletonConfig;
import dev.slimevr.vr.processor.skeleton.SkeletonConfigOffsets;


// The distance from average human proportions
public class BodyProportionError implements IAutoBoneError {

	// TODO hip tracker stuff... Hip tracker should be around 3 to 5
	// centimeters.

	// The headset height is not the full height! This value compensates for the
	// offset from the headset height to the user height
	public float eyeHeightToHeightRatio = 0.936f;

	// Default config
	// Height: 1.58
	// Full Height: 1.58 / 0.936 = 1.688034
	// Neck: 0.1 / 1.688034 = 0.059241
	// Torso: 0.56 / 1.688034 = 0.331747
	// Chest: 0.32 / 1.688034 = 0.18957
	// Waist: (0.56 - 0.32 - 0.04) / 1.688034 = 0.118481
	// Hip: 0.04 / 1.688034 = 0.023696
	// Hip Width: 0.26 / 1.688034 = 0.154025
	// Upper Leg: (0.92 - 0.50) / 1.688034 = 0.24881
	// Lower Leg: 0.50 / 1.688034 = 0.296203

	// "Expected" are values from Drillis and Contini (1966)
	// "Experimental" are values from experimentation by the SlimeVR community
	public static final ProportionLimiter[] proportionLimits = new ProportionLimiter[] {
		// Head
		// Experimental: 0.059
		new RangeProportionLimiter(0.059f, config -> {
			return config.getOffset(SkeletonConfigOffsets.HEAD);
		}, 0.01f),

		// Neck
		// Expected: 0.052
		// Experimental: 0.059
		new RangeProportionLimiter(0.054f, config -> {
			return config.getOffset(SkeletonConfigOffsets.NECK);
		}, 0.0015f),

		// Torso
		// Expected: 0.288 (0.333 including hip, this shouldn't be right...)
		new RangeProportionLimiter(0.333f, config -> {
			return config.getOffset(SkeletonConfigOffsets.TORSO);
		}, 0.015f),

		// Chest
		// Experimental: 0.189
		new RangeProportionLimiter(0.189f, config -> {
			return config.getOffset(SkeletonConfigOffsets.CHEST);
		}, 0.02f),

		// Waist
		// Experimental: 0.118
		new RangeProportionLimiter(0.118f, config -> {
			return config.getOffset(SkeletonConfigOffsets.TORSO)
				- config.getOffset(SkeletonConfigOffsets.CHEST)
				- config.getOffset(SkeletonConfigOffsets.WAIST);
		}, 0.05f),

		// Hip
		// Experimental: 0.0237
		new RangeProportionLimiter(0.0237f, config -> {
			return config.getOffset(SkeletonConfigOffsets.WAIST);
		}, 0.01f),

		// Hip Width
		// Expected: 0.191
		// Experimental: 0.154
		new RangeProportionLimiter(0.184f, config -> {
			return config.getOffset(SkeletonConfigOffsets.HIPS_WIDTH);
		}, 0.04f),

		// Upper Leg
		// Expected: 0.245
		new RangeProportionLimiter(0.245f, config -> {
			return config.getOffset(SkeletonConfigOffsets.LEGS_LENGTH)
				- config.getOffset(SkeletonConfigOffsets.KNEE_HEIGHT);
		}, 0.015f),

		// Lower Leg
		// Expected: 0.246 (0.285 including below ankle, could use a separate
		// offset?)
		new RangeProportionLimiter(0.285f, config -> {
			return config.getOffset(SkeletonConfigOffsets.KNEE_HEIGHT);
		}, 0.02f),
	};

	@Override
	public float getStepError(AutoBoneTrainingStep trainingStep) throws AutoBoneException {
		return getBodyProportionError(
			trainingStep.getSkeleton1().skeletonConfig,
			trainingStep.getCurrentHeight()
		);
	}

	public float getBodyProportionError(SkeletonConfig config, float height) {
		float fullHeight = height / eyeHeightToHeightRatio;

		float sum = 0f;
		for (ProportionLimiter limiter : proportionLimits) {
			sum += FastMath.abs(limiter.getProportionError(config, fullHeight));
		}

		return sum;
	}
}
