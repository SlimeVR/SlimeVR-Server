package dev.slimevr.autobone.errors;

import dev.slimevr.autobone.AutoBoneTrainingStep;


public interface IAutoBoneError {
	public float getStepError(AutoBoneTrainingStep trainingStep) throws AutoBoneException;
}
