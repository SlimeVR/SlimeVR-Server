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

	// 1.3939?
	// Human average is probably 1.1235 (SD 0.07)
	public float legBodyRatio = 1.1235f;

	// SD of 0.07, capture 68% within range
	public float legBodyRatioRange = 0.07f;

	// kneeLegRatio seems to be around 0.54 to 0.6 after asking a few people in
	// the SlimeVR discord.
	public float kneeLegRatio = 0.55f;

	// TODO : Chest should be a bit shorter (0.54?) if user has an additional
	// hip tracker.
	public float chestTorsoRatio = 0.57f;

	// Default config
	// Height: 1.58
	// Full Height: 1.58 / 0.936 = 1.688034
	// Neck: 0.1 / 1.688034 = 0.059241
	// Torso: 0.56 / 1.688034 = 0.331747
	// Upper Leg: (0.92 - 0.50) / 1.688034 = 0.24881
	// Lower Leg: 0.50 / 1.688034 = 0.296203

	// "Expected" are values from Drillis and Contini (1966)
	// "Experimental" are values from experimentation by the SlimeVR community
	public static final ProportionLimiter[] proportionLimits = new ProportionLimiter[] {
		// Neck
		// Expected: 0.052
		new RangeProportionLimiter(0.052f, config -> {
			return config.getOffset(SkeletonConfigOffsets.NECK);
		}, 0.01f),

		// Torso
		// Expected: 0.288 (0.333 including hip, this shouldn't be right...)
		new RangeProportionLimiter(0.333f, config -> {
			return config.getOffset(SkeletonConfigOffsets.TORSO);
		}, 0.015f),

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
		int count = 0;
		for (ProportionLimiter limiter : proportionLimits) {
			sum += FastMath.abs(limiter.getProportionError(config, fullHeight));
			count++;
		}

		return count > 0 ? sum / count : 0f;
	}
}
