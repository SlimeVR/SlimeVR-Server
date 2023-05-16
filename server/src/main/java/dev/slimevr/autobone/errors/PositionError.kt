package dev.slimevr.autobone.errors

import dev.slimevr.autobone.AutoBoneTrainingStep
import dev.slimevr.poseframeformat.trackerdata.TrackerFrames
import dev.slimevr.tracking.processor.skeleton.HumanSkeleton

// The distance of any points to the corresponding absolute position
class PositionError : IAutoBoneError {
	@Throws(AutoBoneException::class)
	override fun getStepError(trainingStep: AutoBoneTrainingStep): Float {
		val trackers = trainingStep.trainingFrames.frameHolders
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
			trackers: List<TrackerFrames>,
			cursor: Int,
			skeleton: HumanSkeleton,
		): Float {
			var offset = 0f
			var offsetCount = 0
			for (tracker in trackers) {
				val trackerFrame = tracker.tryGetFrame(cursor) ?: continue
				val position = trackerFrame.tryGetPosition() ?: continue
				val trackerRole = trackerFrame.tryGetTrackerPosition()?.trackerRole ?: continue

				val computedTracker = skeleton.getComputedTracker(trackerRole) ?: continue

				offset += (position - computedTracker.position).len()
				offsetCount++
			}
			return if (offsetCount > 0) offset / offsetCount else 0f
		}
	}
}
