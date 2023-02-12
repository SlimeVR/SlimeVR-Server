package dev.slimevr.autobone.errors

import com.jme3.math.FastMath
import dev.slimevr.autobone.AutoBoneTrainingStep
import dev.slimevr.poserecorder.PoseFrameTracker
import dev.slimevr.tracking.processor.skeleton.HumanSkeleton

// The difference between offset of absolute position and the corresponding point over time
class PositionOffsetError : IAutoBoneError {
	@Throws(AutoBoneException::class)
	override fun getStepError(trainingStep: AutoBoneTrainingStep): Float {
		val trackers = trainingStep.trainingFrames.trackers
		return getPositionOffsetError(
			trackers,
			trainingStep.cursor1,
			trainingStep.cursor2,
			trainingStep.humanPoseManager1.skeleton,
			trainingStep.humanPoseManager2.skeleton
		)
	}

	fun getPositionOffsetError(
		trackers: List<PoseFrameTracker>,
		cursor1: Int,
		cursor2: Int,
		skeleton1: HumanSkeleton,
		skeleton2: HumanSkeleton
	): Float {
		var offset = 0f
		var offsetCount = 0
		for (tracker in trackers) {
			val trackerFrame1 = tracker.safeGetFrame(cursor1)
			if (trackerFrame1 == null ||
				!trackerFrame1.hasPosition() ||
				trackerFrame1.bodyPosition == null ||
				trackerFrame1.bodyPosition.trackerRole.isEmpty
			) {
				continue
			}
			val trackerFrame2 = tracker.safeGetFrame(cursor2)
			if (trackerFrame2 == null ||
				!trackerFrame2.hasPosition() ||
				trackerFrame2.bodyPosition == null ||
				trackerFrame2.bodyPosition.trackerRole.isEmpty
			) {
				continue
			}
			val computedTracker1 = skeleton1
				.getComputedTracker(trackerFrame1.bodyPosition.trackerRole.get()) ?: continue
			val computedTracker2 = skeleton2
				.getComputedTracker(trackerFrame2.bodyPosition.trackerRole.get()) ?: continue
			val dist1 = FastMath.abs(computedTracker1.position.distance(trackerFrame1.position))
			val dist2 = FastMath.abs(computedTracker2.position.distance(trackerFrame2.position))
			offset += FastMath.abs(dist2 - dist1)
			offsetCount++
		}
		return if (offsetCount > 0) offset / offsetCount else 0f
	}
}
