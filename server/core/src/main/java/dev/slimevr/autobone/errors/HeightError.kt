package dev.slimevr.autobone.errors

import dev.slimevr.autobone.AutoBoneStep
import kotlin.math.*

// The difference from the current height to the target height
class HeightError : IAutoBoneError {
	@Throws(AutoBoneException::class)
	override fun getStepError(trainingStep: AutoBoneStep): Float = getHeightError(
		trainingStep.currentHmdHeight,
		trainingStep.targetHmdHeight,
	)

	fun getHeightError(currentHeight: Float, targetHeight: Float): Float = abs(targetHeight - currentHeight)
}
