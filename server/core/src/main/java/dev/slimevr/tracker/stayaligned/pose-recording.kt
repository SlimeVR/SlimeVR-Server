package dev.slimevr.tracker.stayaligned

import dev.slimevr.config.StayAlignedPoseConfig
import dev.slimevr.tracker.Motion
import dev.slimevr.tracker.TrackerState
import dev.slimevr.tracker.stayaligned.poses.PlayerPose
import dev.slimevr.tracker.stayaligned.poses.RelaxedPose
import solarxr_protocol.rpc.StayAlignedPart
import solarxr_protocol.rpc.StayAlignedPoseRecordingFailure
import solarxr_protocol.rpc.StayAlignedRecordingTrackerState
import solarxr_protocol.rpc.StayAlignedRelaxedPose

data class PoseRecordingCheck(
	val parts: Map<StayAlignedPart, StayAlignedRecordingTrackerState>,
	val detectedPose: StayAlignedRelaxedPose,
	val failure: StayAlignedPoseRecordingFailure,
)

/** Whether each part that has a tracker is still or moving. */
private fun partStates(trackerStates: List<TrackerState>): Map<StayAlignedPart, StayAlignedRecordingTrackerState> = StayAlignedPart.entries.mapNotNull { part ->
	val trackers = trackersOf(trackerStates, part)
	when {
		trackers.isEmpty() -> null
		trackers.all { it.motion == Motion.RESTING } -> part to StayAlignedRecordingTrackerState.STILL
		else -> part to StayAlignedRecordingTrackerState.MOVING
	}
}.toMap()

/** Missing trackers come first, then trackers that move, then the pose. */
fun checkPoseRecording(trackerStates: List<TrackerState>, requested: StayAlignedRelaxedPose): PoseRecordingCheck {
	val parts = partStates(trackerStates)
	val detectedPose = PlayerPose.of(trackerStates).relaxedPose
	val failure = when {
		missingRequiredParts(trackerStates).isNotEmpty() -> StayAlignedPoseRecordingFailure.MISSING_TRACKERS
		StayAlignedRecordingTrackerState.MOVING in parts.values -> StayAlignedPoseRecordingFailure.NOT_STILL
		detectedPose == StayAlignedRelaxedPose.UNKNOWN -> StayAlignedPoseRecordingFailure.POSITION_UNKNOWN
		detectedPose != requested -> StayAlignedPoseRecordingFailure.WRONG_POSE
		else -> StayAlignedPoseRecordingFailure.NONE
	}
	return PoseRecordingCheck(parts, detectedPose, failure)
}

/** The pose the user is in right now, measured from the rotations the trackers had when they started resting. */
fun recordedPoseConfig(trackerStates: List<TrackerState>): StayAlignedPoseConfig {
	val relaxedPose = RelaxedPose.fromTrackers(trackerStates)
	return StayAlignedPoseConfig(
		recorded = true,
		enabled = true,
		upperLegAngleInDeg = relaxedPose.upperLeg.toDeg(),
		lowerLegAngleInDeg = relaxedPose.lowerLeg.toDeg(),
		footAngleInDeg = relaxedPose.foot.toDeg(),
	)
}
