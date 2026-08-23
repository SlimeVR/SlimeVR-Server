package dev.slimevr.tracker.stayaligned.poses

import dev.slimevr.tracker.TrackerState
import dev.slimevr.tracker.getAllFineFor
import dev.slimevr.tracker.getFirstFineFor
import dev.slimevr.tracker.stayaligned.StayAlignedBodyParts

/**
 * The pose of the player.
 */
enum class PlayerPose {
	UNKNOWN,
	STANDING,
	SITTING_IN_CHAIR,
	SITTING_ON_GROUND,
	LYING_ON_BACK,
	KNEELING,
	;

	companion object {

		fun of(trackerStates: List<TrackerState>): PlayerPose {
			val poses =
				TrackerPoses(
					trackerStates.getAllFineFor(StayAlignedBodyParts.upperBodyGroup).sortedBy { StayAlignedBodyParts.upperBodyOrder[it.bodyPart] }.map(TrackerPose.Companion::ofTracker),
					TrackerPose.ofTracker(trackerStates.getFirstFineFor(StayAlignedBodyParts.leftUpperLeg)),
					TrackerPose.ofTracker(trackerStates.getFirstFineFor(StayAlignedBodyParts.rightUpperLeg)),
					TrackerPose.ofTracker(trackerStates.getFirstFineFor(StayAlignedBodyParts.leftLowerLeg)),
					TrackerPose.ofTracker(trackerStates.getFirstFineFor(StayAlignedBodyParts.rightLowerLeg)),
				)

			return (
				if (isStanding(poses)) {
					STANDING
				} else if (isSittingInChair(poses)) {
					SITTING_IN_CHAIR
				} else if (isSittingOnGround(poses)) {
					SITTING_ON_GROUND
				} else if (isLyingOnBack(poses)) {
					LYING_ON_BACK
				} else if (isKneeling(poses)) {
					KNEELING
				} else {
					UNKNOWN
				}
				)
		}

		private class TrackerPoses(
			val upperBody: List<TrackerPose>,
			val leftUpperLeg: TrackerPose,
			val rightUpperLeg: TrackerPose,
			val leftLowerLeg: TrackerPose,
			val rightLowerLeg: TrackerPose,
		)

		private fun isStanding(pose: TrackerPoses) = pose.upperBody.all { it == TrackerPose.TOP_FACING_UP } &&
			pose.leftUpperLeg == TrackerPose.TOP_FACING_UP &&
			pose.rightUpperLeg == TrackerPose.TOP_FACING_UP &&
			pose.leftLowerLeg == TrackerPose.TOP_FACING_UP &&
			pose.rightLowerLeg == TrackerPose.TOP_FACING_UP

		private fun isSittingInChair(pose: TrackerPoses) = pose.upperBody.isNotEmpty() &&
			pose.upperBody[0] == TrackerPose.TOP_FACING_UP &&
			pose.upperBody.all { it == TrackerPose.TOP_FACING_UP || it == TrackerPose.FRONT_FACING_UP } &&
			pose.leftUpperLeg == TrackerPose.FRONT_FACING_UP &&
			pose.rightUpperLeg == TrackerPose.FRONT_FACING_UP &&
			pose.leftLowerLeg == TrackerPose.TOP_FACING_UP &&
			pose.rightLowerLeg == TrackerPose.TOP_FACING_UP

		private fun isSittingOnGround(pose: TrackerPoses) = pose.upperBody.isNotEmpty() &&
			pose.upperBody[0] == TrackerPose.TOP_FACING_UP &&
			pose.upperBody.all { it == TrackerPose.TOP_FACING_UP || it == TrackerPose.FRONT_FACING_UP } &&
			// Allow legs to be flat on ground, or knees-up
			pose.leftUpperLeg.let { it == TrackerPose.FRONT_FACING_UP || it == TrackerPose.TOP_FACING_DOWN } &&
			pose.rightUpperLeg.let { it == TrackerPose.FRONT_FACING_UP || it == TrackerPose.TOP_FACING_DOWN } &&
			pose.leftLowerLeg.let { it == TrackerPose.FRONT_FACING_UP || it == TrackerPose.TOP_FACING_UP } &&
			pose.rightLowerLeg.let { it == TrackerPose.FRONT_FACING_UP || it == TrackerPose.TOP_FACING_UP }

		private fun isLyingOnBack(pose: TrackerPoses) = pose.upperBody.all { it == TrackerPose.FRONT_FACING_UP } &&
			// Allow legs to be flat on ground, or knees-up
			pose.leftUpperLeg.let { it == TrackerPose.FRONT_FACING_UP || it == TrackerPose.TOP_FACING_DOWN } &&
			pose.rightUpperLeg.let { it == TrackerPose.FRONT_FACING_UP || it == TrackerPose.TOP_FACING_DOWN } &&
			pose.leftLowerLeg.let { it == TrackerPose.FRONT_FACING_UP || it == TrackerPose.TOP_FACING_UP } &&
			pose.rightLowerLeg.let { it == TrackerPose.FRONT_FACING_UP || it == TrackerPose.TOP_FACING_UP }

		private fun isKneeling(pose: TrackerPoses) = pose.leftUpperLeg.let { it == TrackerPose.TOP_FACING_UP || it == TrackerPose.FRONT_FACING_UP } &&
			pose.rightUpperLeg.let { it == TrackerPose.TOP_FACING_UP || it == TrackerPose.FRONT_FACING_UP } &&
			pose.leftLowerLeg == TrackerPose.FRONT_FACING_DOWN &&
			pose.rightLowerLeg == TrackerPose.FRONT_FACING_DOWN
	}
}
