package dev.slimevr.autobone.errors;

import com.jme3.math.FastMath;

import dev.slimevr.autobone.AutoBoneTrainingStep;


public class HeightError implements IAutoBoneError {
	@Override
	public float getStepError(AutoBoneTrainingStep trainingStep) throws AutoBoneException {
		return getHeightError(
			trainingStep.getCurrentHeight(),
			trainingStep.getTargetHeight()
		);
	}

	public float getHeightError(float currentHeight, float targetHeight) {
		return FastMath.abs(targetHeight - currentHeight);
	}
}
