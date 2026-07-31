package dev.slimevr.stayaligned.todo

import dev.slimevr.math.angle.AngleErrors
import dev.slimevr.stayaligned.todo.TrackerYaw.extraYaw
import dev.slimevr.stayaligned.todo.TrackerYaw.trackerYaw
import dev.slimevr.tracker.Tracker
import dev.slimevr.util.Side
import solarxr_protocol.datatypes.BodyPart

/**
 * Error between a tracker's yaw and its neighbors' yaws.
 */
class NeighborErrorVisitor(
	val relaxedPose: RelaxedPose,
	val errors: AngleErrors,
) : TrackerSkeleton.TrackerVisitor {

	override fun visitHeadTracker(
		tracker: Tracker,
		belowUpperBody: Tracker?,
	) {
		if (belowUpperBody != null) {
			errors.add(trackerYaw(belowUpperBody) - trackerYaw(tracker))
		}
	}

	override fun visitUpperBodyTracker(
		tracker: Tracker,
		aboveHeadOrUpperBody: Tracker?,
		belowUpperBody: Tracker?,
	) {
		if (
			aboveHeadOrUpperBody != null &&
			// Head often drags the upper body trackers off to the side, so ignore it
			aboveHeadOrUpperBody.context.state.value.bodyPart != BodyPart.HEAD
		) {
			errors.add(trackerYaw(aboveHeadOrUpperBody) - trackerYaw(tracker))
		}

		if (belowUpperBody != null) {
			errors.add(trackerYaw(belowUpperBody) - trackerYaw(tracker))
		}
	}

	override fun visitUpperBodyTracker(
		tracker: Tracker,
		aboveHeadOrUpperBody: Tracker?,
		belowLeftUpperLeg: Tracker?,
		belowRightUpperLeg: Tracker?,
	) {
		if (
			aboveHeadOrUpperBody != null &&
			// Head often drags the upper body trackers off to the side, so ignore it
			aboveHeadOrUpperBody.context.state.value.bodyPart != BodyPart.HEAD
		) {
			errors.add(trackerYaw(aboveHeadOrUpperBody) - trackerYaw(tracker))
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
					trackerYaw(tracker),
			)
			errors.add(
				trackerYaw(belowRightUpperLeg) -
					extraYaw(Side.RIGHT, relaxedPose.upperLeg) -
					trackerYaw(tracker),
			)
		}
	}

	override fun visitArmTracker(
		side: Side,
		tracker: Tracker,
		aboveUpperBodyOrArm: Tracker?,
		belowHandOrArm: Tracker?,
	) {
		// No error because arms can go anywhere
	}

	override fun visitHandTracker(
		side: Side,
		tracker: Tracker,
		aboveArm: Tracker?,
		oppositeHand: Tracker?,
	) {
		// No error because hands can go anywhere
	}

	override fun visitUpperLegTracker(
		side: Side,
		tracker: Tracker,
		aboveUpperBody: Tracker?,
		belowLowerLeg: Tracker?,
		oppositeUpperLeg: Tracker?,
	) {
		if (aboveUpperBody != null) {
			errors.add(
				trackerYaw(aboveUpperBody) +
					extraYaw(side, relaxedPose.upperLeg) -
					trackerYaw(tracker),
			)
		}

		if (belowLowerLeg != null) {
			errors.add(
				trackerYaw(belowLowerLeg) -
					extraYaw(side, relaxedPose.lowerLeg) +
					extraYaw(side, relaxedPose.upperLeg) -
					trackerYaw(tracker),
			)
		}
	}

	override fun visitLowerLegTracker(
		side: Side,
		tracker: Tracker,
		aboveUpperLeg: Tracker?,
		belowFoot: Tracker?,
		oppositeLowerLeg: Tracker?,
	) {
		if (aboveUpperLeg != null) {
			errors.add(
				trackerYaw(aboveUpperLeg) -
					extraYaw(side, relaxedPose.upperLeg) +
					extraYaw(side, relaxedPose.lowerLeg) -
					trackerYaw(tracker),
			)
		}

		if (belowFoot != null) {
			errors.add(
				trackerYaw(belowFoot) -
					extraYaw(side, relaxedPose.foot) +
					extraYaw(side, relaxedPose.lowerLeg) -
					trackerYaw(tracker),
			)
		}
	}

	override fun visitFootTracker(
		side: Side,
		tracker: Tracker,
		aboveLowerLeg: Tracker?,
		oppositeFoot: Tracker?,
	) {
		if (aboveLowerLeg != null) {
			errors.add(
				trackerYaw(aboveLowerLeg) -
					extraYaw(side, relaxedPose.lowerLeg) +
					extraYaw(side, relaxedPose.foot) -
					trackerYaw(tracker),
			)
		}
	}
}
