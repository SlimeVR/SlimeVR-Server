package dev.slimevr.autobone.errors

import com.jme3.math.FastMath
import dev.slimevr.autobone.AutoBoneTrainingStep

// The difference from the current height to the target height
class HeightError : IAutoBoneError {
	@Throws(AutoBoneException::class)
	override fun getStepError(trainingStep: AutoBoneTrainingStep): Float {
		return getHeightError(
			trainingStep.currentHeight,
			trainingStep.targetHeight
		)
	}

	fun getHeightError(currentHeight: Float, targetHeight: Float): Float {
		return FastMath.abs(targetHeight - currentHeight)
	}
}
