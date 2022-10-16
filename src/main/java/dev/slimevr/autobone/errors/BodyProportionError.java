package dev.slimevr.autobone.errors;

import com.jme3.math.FastMath;

import dev.slimevr.autobone.AutoBoneTrainingStep;
import dev.slimevr.vr.processor.skeleton.SkeletonConfig;
import dev.slimevr.vr.processor.skeleton.SkeletonConfigOffsets;


// The distance from average human proportions
public class BodyProportionError implements IAutoBoneError {

	// TODO hip tracker stuff... Hip tracker should be around 3 to 5
	// centimeters.

	// 1.3939?
	// Human average is probably 1.1235 (SD 0.07)
	public float legBodyRatio = 1.1235f;

	// SD of 0.07, capture 68% within range
	public float legBodyRatioRange = 0.07f;

	// kneeLegRatio seems to be around 0.54 to 0.6 after asking a few people in
	// the SlimeVR discord.
	public float kneeLegRatio = 0.55f;

	// kneeLegRatio seems to be around 0.55 to 0.64 after asking a few people in
	// the SlimeVR discord. TODO : Chest should be a bit shorter (0.54?) if user
	// has an additional hip tracker.
	public float chestTorsoRatio = 0.57f;

	@Override
	public float getStepError(AutoBoneTrainingStep trainingStep) throws AutoBoneException {
		return getBodyProportionError(trainingStep.getSkeleton1().skeletonConfig, trainingStep.getCurrentHeight());
	}

	public float getBodyProportionError(SkeletonConfig config, float height) {
		float neckLength = config.getOffset(SkeletonConfigOffsets.NECK);
		float chestLength = config.getOffset(SkeletonConfigOffsets.CHEST);
		float torsoLength = config.getOffset(SkeletonConfigOffsets.TORSO);
		float legsLength = config.getOffset(SkeletonConfigOffsets.LEGS_LENGTH);
		float kneeHeight = config.getOffset(SkeletonConfigOffsets.KNEE_HEIGHT);

		float chestTorso = FastMath.abs((chestLength / torsoLength) - chestTorsoRatio);
		float legBody = FastMath.abs((legsLength / (torsoLength + neckLength)) - legBodyRatio);
		float kneeLeg = FastMath.abs((kneeHeight / legsLength) - kneeLegRatio);

		if (legBody <= legBodyRatioRange) {
			legBody = 0f;
		} else {
			legBody -= legBodyRatioRange;
		}

		return (chestTorso + legBody + kneeLeg) / 3f;
	}
}
