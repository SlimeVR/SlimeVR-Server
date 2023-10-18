package dev.slimevr.autobone.errors

import com.jme3.math.FastMath
import dev.slimevr.autobone.AutoBoneStep
import dev.slimevr.autobone.errors.proportions.ProportionLimiter
import dev.slimevr.autobone.errors.proportions.RangeProportionLimiter
import dev.slimevr.tracking.processor.HumanPoseManager
import dev.slimevr.tracking.processor.config.SkeletonConfigOffsets

// The distance from average human proportions
class BodyProportionError : IAutoBoneError {
	@Throws(AutoBoneException::class)
	override fun getStepError(trainingStep: AutoBoneStep): Float {
		return getBodyProportionError(
			trainingStep.skeleton1,
			// Skeletons are now normalized to reduce bias, so height is always 1
			1f
		)
	}

	fun getBodyProportionError(humanPoseManager: HumanPoseManager, fullHeight: Float): Float {
		var sum = 0f
		for (limiter in proportionLimits) {
			sum += FastMath.abs(limiter.getProportionError(humanPoseManager, fullHeight))
		}
		return sum
	}

	companion object {
		// TODO hip tracker stuff... Hip tracker should be around 3 to 5
		// centimeters.
		// The headset height is not the full height! This value compensates for the
		// offset from the headset height to the user height
		@JvmField
		var eyeHeightToHeightRatio = 0.936f

		// Default config
		// Height: 1.58
		// Full Height: 1.58 / 0.936 = 1.688034
		// Neck: 0.1 / 1.688034 = 0.059241
		// Torso: 0.56 / 1.688034 = 0.331747
		// Upper Chest: 0.16 / 1.688034 = 0.094784
		// Chest: 0.16 / 1.688034 = 0.094784
		// Waist: (0.56 - 0.32 - 0.04) / 1.688034 = 0.118481
		// Hip: 0.04 / 1.688034 = 0.023696
		// Hip Width: 0.26 / 1.688034 = 0.154025
		// Upper Leg: (0.92 - 0.50) / 1.688034 = 0.24881
		// Lower Leg: 0.50 / 1.688034 = 0.296203
		// "Expected" are values from Drillis and Contini (1966)
		// "Experimental" are values from experimentation by the SlimeVR community
		val proportionLimits = arrayOf<ProportionLimiter>(
			// Head
			// Experimental: 0.059
			RangeProportionLimiter(
				0.059f,
				{ config: HumanPoseManager -> config.getOffset(SkeletonConfigOffsets.HEAD) },
				0.01f
			),
			// Neck
			// Expected: 0.052
			// Experimental: 0.059
			RangeProportionLimiter(
				0.054f,
				{ config: HumanPoseManager -> config.getOffset(SkeletonConfigOffsets.NECK) },
				0.0015f
			),
			// Upper Chest
			// Experimental: 0.0945
			RangeProportionLimiter(
				0.0945f,
				{ config: HumanPoseManager -> config.getOffset(SkeletonConfigOffsets.UPPER_CHEST) },
				0.01f
			),
			// Chest
			// Experimental: 0.0945
			RangeProportionLimiter(
				0.0945f,
				{ config: HumanPoseManager -> config.getOffset(SkeletonConfigOffsets.CHEST) },
				0.01f
			),
			// Waist
			// Experimental: 0.118
			RangeProportionLimiter(
				0.118f,
				{ config: HumanPoseManager -> config.getOffset(SkeletonConfigOffsets.WAIST) },
				0.05f
			),
			// Hip
			// Experimental: 0.0237
			RangeProportionLimiter(
				0.0237f,
				{ config: HumanPoseManager -> config.getOffset(SkeletonConfigOffsets.HIP) },
				0.01f
			),
			// Hip Width
			// Expected: 0.191
			// Experimental: 0.154
			RangeProportionLimiter(
				0.184f,
				{ config: HumanPoseManager -> config.getOffset(SkeletonConfigOffsets.HIPS_WIDTH) },
				0.04f
			),
			// Upper Leg
			// Expected: 0.245
			RangeProportionLimiter(
				0.245f,
				{ config: HumanPoseManager -> config.getOffset(SkeletonConfigOffsets.UPPER_LEG) },
				0.015f
			),
			// Lower Leg
			// Expected: 0.246 (0.285 including below ankle, could use a separate
			// offset?)
			RangeProportionLimiter(
				0.285f,
				{ config: HumanPoseManager -> config.getOffset(SkeletonConfigOffsets.LOWER_LEG) },
				0.02f
			)
		)

		@JvmStatic
		fun getProportionLimitForOffset(offset: SkeletonConfigOffsets): ProportionLimiter? {
			return when (offset) {
				SkeletonConfigOffsets.HEAD -> proportionLimits[0]
				SkeletonConfigOffsets.NECK -> proportionLimits[1]
				SkeletonConfigOffsets.UPPER_CHEST -> proportionLimits[2]
				SkeletonConfigOffsets.CHEST -> proportionLimits[3]
				SkeletonConfigOffsets.WAIST -> proportionLimits[4]
				SkeletonConfigOffsets.HIP -> proportionLimits[5]
				SkeletonConfigOffsets.HIPS_WIDTH -> proportionLimits[6]
				SkeletonConfigOffsets.UPPER_LEG -> proportionLimits[7]
				SkeletonConfigOffsets.LOWER_LEG -> proportionLimits[8]
				else -> null
			}
		}
	}
}
