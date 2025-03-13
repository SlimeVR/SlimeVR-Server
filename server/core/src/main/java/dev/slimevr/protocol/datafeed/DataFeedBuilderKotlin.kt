package dev.slimevr.protocol.datafeed

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.tracking.processor.skeleton.HumanSkeleton
import dev.slimevr.tracking.processor.stayaligned.skeleton.RelaxedPose.Companion.fromTrackers
import dev.slimevr.tracking.processor.stayaligned.state.StayAlignedTrackerState
import solarxr_protocol.data_feed.stay_aligned.StayAlignedData
import solarxr_protocol.data_feed.stay_aligned.StayAlignedTracker

object DataFeedBuilderKotlin {

	fun createStayAlignedData(
		fbb: FlatBufferBuilder,
		humanSkeleton: HumanSkeleton,
	): Int {
		val relaxedPose = fromTrackers(humanSkeleton)

		StayAlignedData.startStayAlignedData(fbb)

		StayAlignedData.addUpperLegAngleInDeg(fbb, relaxedPose.upperLeg.toDeg())
		StayAlignedData.addLowerLegAngleInDeg(fbb, relaxedPose.lowerLeg.toDeg())
		StayAlignedData.addFootAngleInDeg(fbb, relaxedPose.foot.toDeg())

		return StayAlignedData.endStayAlignedData(fbb)
	}

	fun createTrackerStayAlignedTracker(
		fbb: FlatBufferBuilder,
		state: StayAlignedTrackerState,
	): Int {
		StayAlignedTracker.startStayAlignedTracker(fbb)

		StayAlignedTracker.addYawCorrectionInDeg(fbb, state.yawCorrection.yaw.toDeg())
		StayAlignedTracker.addLockedErrorInDeg(fbb, state.yawErrors.lockedError.toDeg())
		StayAlignedTracker.addCenterErrorInDeg(fbb, state.yawErrors.centerError.toDeg())
		StayAlignedTracker.addNeighborErrorInDeg(fbb, state.yawErrors.neighborError.toDeg())
		StayAlignedTracker.addLocked(fbb, state.lockedRotation != null)

		return StayAlignedTracker.endStayAlignedTracker(fbb)
	}
}
