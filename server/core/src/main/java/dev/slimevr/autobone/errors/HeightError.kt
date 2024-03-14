package dev.slimevr.autobone.errors

import com.jme3.math.FastMath
import dev.slimevr.autobone.AutoBoneStep

// The difference from the current height to the target height
class HeightError : IAutoBoneError {
	@Throws(AutoBoneException::class)
	override fun getStepError(trainingStep: AutoBoneStep): Float = getHeightError(
		trainingStep.currentHmdHeight,
		trainingStep.targetHmdHeight,
	)

	fun getHeightError(currentHeight: Float, targetHeight: Float): Float = FastMath.abs(targetHeight - currentHeight)
}
