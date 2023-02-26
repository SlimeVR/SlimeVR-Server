package dev.slimevr.autobone.errors

import dev.slimevr.autobone.AutoBoneTrainingStep

interface IAutoBoneError {
	@Throws(AutoBoneException::class)
	fun getStepError(trainingStep: AutoBoneTrainingStep): Float
}
