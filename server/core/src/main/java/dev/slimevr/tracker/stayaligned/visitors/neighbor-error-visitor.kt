package dev.slimevr.tracker.stayaligned.visitors

import dev.slimevr.math.angle.AngleErrors
import dev.slimevr.tracker.TrackerState
import dev.slimevr.tracker.stayaligned.YawUtils.extraYaw
import dev.slimevr.tracker.stayaligned.YawUtils.trackerYaw
import dev.slimevr.tracker.stayaligned.poses.RelaxedPose
import dev.slimevr.util.Side
import solarxr_protocol.datatypes.BodyPart

/**
 * Error between a tracker's yaw and its neighbours' yaws.
 */
class NeighborErrorVisitor(
	val relaxedPose: RelaxedPose,
	val errors: AngleErrors,
) : TrackerGroups.TrackerVisitor {

	override fun visitHeadTracker(
		trackerState: TrackerState,
		belowUpperBody: TrackerState?,
	) {
		if (belowUpperBody != null) {
			errors.add(trackerYaw(belowUpperBody) - trackerYaw(trackerState))
		}
	}

	override fun visitUpperBodyTracker(
		trackerState: TrackerState,
		aboveHeadOrUpperBody: TrackerState?,
		belowUpperBody: TrackerState?,
	) {
		if (
			aboveHeadOrUpperBody != null &&
			// Head often drags the upper body trackers off to the side, so ignore it
			aboveHeadOrUpperBody.bodyPart != BodyPart.HEAD
		) {
			errors.add(trackerYaw(aboveHeadOrUpperBody) - trackerYaw(trackerState))
		}

		if (belowUpperBody != null) {
			errors.add(trackerYaw(belowUpperBody) - trackerYaw(trackerState))
		}
	}

	override fun visitUpperBodyTracker(
		trackerState: TrackerState,
		aboveHeadOrUpperBody: TrackerState?,
		belowLeftUpperLeg: TrackerState?,
		belowRightUpperLeg: TrackerState?,
	) {
		if (
			aboveHeadOrUpperBody != null &&
			// Head often drags the upper body trackers off to the side, so ignore it
			aboveHeadOrUpperBody.bodyPart != BodyPart.HEAD
		) {
			errors.add(trackerYaw(aboveHeadOrUpperBody) - trackerYaw(trackerState))
		}

		// Only consider upper leg trackers if both are available, so that the upper
		// body tracker can be balanced between both
		if (
			belowLeftUpperLeg != null &&
			belowRightUpperLeg != null
		) {
			errors.add(
				trackerYaw(belowLeftUpperLeg) -
					extraYaw(Side.LEFT, relaxedPose.upperLeg) -
					trackerYaw(trackerState),
			)
			errors.add(
				trackerYaw(belowRightUpperLeg) -
					extraYaw(Side.RIGHT, relaxedPose.upperLeg) -
					trackerYaw(trackerState),
			)
		}
	}

	override fun visitArmTracker(
		side: Side,
		trackerState: TrackerState,
		aboveUpperBodyOrArm: TrackerState?,
		belowHandOrArm: TrackerState?,
	) {
		// No error because arms can go anywhere
	}

	override fun visitHandTracker(
		side: Side,
		trackerState: TrackerState,
		aboveArm: TrackerState?,
		oppositeHand: TrackerState?,
	) {
		// No error because hands can go anywhere
	}

	override fun visitUpperLegTracker(
		side: Side,
		trackerState: TrackerState,
		aboveUpperBody: TrackerState?,
		belowLowerLeg: TrackerState?,
		oppositeUpperLeg: TrackerState?,
	) {
		if (aboveUpperBody != null) {
			errors.add(
				trackerYaw(aboveUpperBody) +
					extraYaw(side, relaxedPose.upperLeg) -
					trackerYaw(trackerState),
			)
		}

		if (belowLowerLeg != null) {
			errors.add(
				trackerYaw(belowLowerLeg) -
					extraYaw(side, relaxedPose.lowerLeg) +
					extraYaw(side, relaxedPose.upperLeg) -
					trackerYaw(trackerState),
			)
		}
	}

	override fun visitLowerLegTracker(
		side: Side,
		trackerState: TrackerState,
		aboveUpperLeg: TrackerState?,
		belowFoot: TrackerState?,
		oppositeLowerLeg: TrackerState?,
	) {
		if (aboveUpperLeg != null) {
			errors.add(
				trackerYaw(aboveUpperLeg) -
					extraYaw(side, relaxedPose.upperLeg) +
					extraYaw(side, relaxedPose.lowerLeg) -
					trackerYaw(trackerState),
			)
		}

		if (belowFoot != null) {
			errors.add(
				trackerYaw(belowFoot) -
					extraYaw(side, relaxedPose.foot) +
					extraYaw(side, relaxedPose.lowerLeg) -
					trackerYaw(trackerState),
			)
		}
	}

	override fun visitFootTracker(
		side: Side,
		trackerState: TrackerState,
		aboveLowerLeg: TrackerState?,
		oppositeFoot: TrackerState?,
	) {
		if (aboveLowerLeg != null) {
			errors.add(
				trackerYaw(aboveLowerLeg) -
					extraYaw(side, relaxedPose.lowerLeg) +
					extraYaw(side, relaxedPose.foot) -
					trackerYaw(trackerState),
			)
		}
	}
}
