package dev.slimevr.autobone.errors

import com.jme3.math.FastMath
import dev.slimevr.autobone.AutoBoneTrainingStep
import dev.slimevr.poserecorder.PoseFrameTracker
import dev.slimevr.tracking.processor.skeleton.HumanSkeleton
import dev.slimevr.tracking.trackers.ComputedTracker

// The distance of any points to the corresponding absolute position
class PositionError : IAutoBoneError {
	@Throws(AutoBoneException::class)
	override fun getStepError(trainingStep: AutoBoneTrainingStep): Float {
		val trackers = trainingStep.trainingFrames.trackers
		return (
			(
				getPositionError(
					trackers,
					trainingStep.cursor1,
					trainingStep.humanPoseManager1.skeleton
				) +
					getPositionError(
						trackers,
						trainingStep.cursor2,
						trainingStep.humanPoseManager2.skeleton
					)
				) /
				2f
			)
	}

	companion object {
		fun getPositionError(
			trackers: List<PoseFrameTracker>,
			cursor: Int,
			skeleton: HumanSkeleton,
		): Float {
			var offset = 0f
			var offsetCount = 0
			for (tracker in trackers) {
				val trackerFrame = tracker.safeGetFrame(cursor)
				if (trackerFrame == null ||
					!trackerFrame.hasPosition() ||
					trackerFrame.bodyPosition?.trackerRole == null
				) {
					continue
				}
				val computedTracker: ComputedTracker? = skeleton
					.getComputedTracker(trackerFrame.bodyPosition.trackerRole)
				if (computedTracker != null) {
					offset += FastMath.abs(computedTracker.position.distance(trackerFrame.position))
					offsetCount++
				}
			}
			return if (offsetCount > 0) offset / offsetCount else 0f
		}
	}
}
