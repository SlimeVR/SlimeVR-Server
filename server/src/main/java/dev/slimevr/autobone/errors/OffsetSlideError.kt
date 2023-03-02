package dev.slimevr.autobone.errors

import com.jme3.math.FastMath
import dev.slimevr.autobone.AutoBoneTrainingStep
import dev.slimevr.tracking.processor.skeleton.HumanSkeleton
import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.TrackerRole

// The change in distance between both of the ankles over time
class OffsetSlideError : IAutoBoneError {
	@Throws(AutoBoneException::class)
	override fun getStepError(trainingStep: AutoBoneTrainingStep): Float {
		return getSlideError(
			trainingStep.humanPoseManager1.skeleton,
			trainingStep.humanPoseManager2.skeleton
		)
	}

	companion object {
		fun getSlideError(skeleton1: HumanSkeleton, skeleton2: HumanSkeleton): Float {
			val leftTracker1: Tracker = skeleton1.getComputedTracker(TrackerRole.LEFT_FOOT)
			val rightTracker1: Tracker = skeleton1.getComputedTracker(TrackerRole.RIGHT_FOOT)
			val leftTracker2: Tracker = skeleton2.getComputedTracker(TrackerRole.LEFT_FOOT)
			val rightTracker2: Tracker = skeleton2.getComputedTracker(TrackerRole.RIGHT_FOOT)
			return getSlideError(leftTracker1, rightTracker1, leftTracker2, rightTracker2)
		}

		fun getSlideError(
			leftTracker1: Tracker,
			rightTracker1: Tracker,
			leftTracker2: Tracker,
			rightTracker2: Tracker,
		): Float {
			val leftFoot1 = leftTracker1.position
			val rightFoot1 = rightTracker1.position
			val leftFoot2 = leftTracker2.position
			val rightFoot2 = rightTracker2.position
			val slideDist1 = leftFoot1.distance(rightFoot1)
			val slideDist2 = leftFoot2.distance(rightFoot2)
			val slideDist3 = leftFoot1.distance(rightFoot2)
			val slideDist4 = leftFoot2.distance(rightFoot1)

			// Compute all combinations of distances
			val dist1 = FastMath.abs(slideDist1 - slideDist2)
			val dist2 = FastMath.abs(slideDist1 - slideDist3)
			val dist3 = FastMath.abs(slideDist1 - slideDist4)
			val dist4 = FastMath.abs(slideDist2 - slideDist3)
			val dist5 = FastMath.abs(slideDist2 - slideDist4)
			val dist6 = FastMath.abs(slideDist3 - slideDist4)

			// Divide by 12 (6 values * 2 to halve) to halve and average, it's
			// halved because you want to approach a midpoint, not the other point
			return (dist1 + dist2 + dist3 + dist4 + dist5 + dist6) / 12f
		}
	}
}
