package dev.slimevr.protocol.datafeed

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.guards.ServerGuards
import dev.slimevr.tracking.processor.skeleton.HumanSkeleton
import dev.slimevr.tracking.processor.stayaligned.poses.RelaxedPose
import dev.slimevr.tracking.processor.stayaligned.trackers.RestDetector
import dev.slimevr.tracking.processor.stayaligned.trackers.StayAlignedTrackerState
import solarxr_protocol.data_feed.stay_aligned.StayAlignedPose
import solarxr_protocol.data_feed.stay_aligned.StayAlignedTracker

object DataFeedBuilderKotlin {

	fun createStayAlignedPose(
		fbb: FlatBufferBuilder,
		humanSkeleton: HumanSkeleton,
	): Int {
		val relaxedPose = RelaxedPose.fromTrackers(humanSkeleton)

		StayAlignedPose.startStayAlignedPose(fbb)

		StayAlignedPose.addUpperLegAngleInDeg(fbb, relaxedPose.upperLeg.toDeg())
		StayAlignedPose.addLowerLegAngleInDeg(fbb, relaxedPose.lowerLeg.toDeg())
		StayAlignedPose.addFootAngleInDeg(fbb, relaxedPose.foot.toDeg())

		return StayAlignedPose.endStayAlignedPose(fbb)
	}

	fun createTrackerStayAlignedTracker(
		fbb: FlatBufferBuilder,
		state: StayAlignedTrackerState,
	): Int {
		StayAlignedTracker.startStayAlignedTracker(fbb)

		StayAlignedTracker.addYawCorrectionInDeg(fbb, state.yawCorrection.toDeg())
		StayAlignedTracker.addLockedErrorInDeg(fbb, state.yawErrors.lockedError.toL2Norm().toDeg())
		StayAlignedTracker.addCenterErrorInDeg(fbb, state.yawErrors.centerError.toL2Norm().toDeg())
		StayAlignedTracker.addNeighborErrorInDeg(fbb, state.yawErrors.neighborError.toL2Norm().toDeg())
		StayAlignedTracker.addLocked(fbb, state.restDetector.state == RestDetector.State.AT_REST)

		return StayAlignedTracker.endStayAlignedTracker(fbb)
	}

	fun createServerGuard(fbb: FlatBufferBuilder, serverGuards: ServerGuards): Int {
		return solarxr_protocol.data_feed.server.ServerGuards.createServerGuards(fbb, serverGuards.canDoMounting, serverGuards.canDoYawReset)
	}
}
