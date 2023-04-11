package dev.slimevr.autobone.errors

import dev.slimevr.autobone.AutoBoneStep

interface IAutoBoneError {
	@Throws(AutoBoneException::class)
	fun getStepError(trainingStep: AutoBoneStep): Float
}
