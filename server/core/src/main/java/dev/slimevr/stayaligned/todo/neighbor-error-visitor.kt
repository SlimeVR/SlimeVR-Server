package dev.slimevr.stayaligned.todo

import dev.slimevr.math.angle.AngleErrors
import dev.slimevr.stayaligned.todo.TrackerYaw.trackerYaw
import dev.slimevr.tracker.TrackerState
import dev.slimevr.util.Side
import solarxr_protocol.datatypes.BodyPart

/**
 * Error between a tracker's yaw and its neighbours' yaws.
 */
class NeighborErrorVisitor(
	val relaxedPose: RelaxedPose,
	val errors: AngleErrors,
) : TrackerGroup.TrackerVisitor {

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
					relaxedPose.upperLeg.towards(Side.LEFT) -
					trackerYaw(trackerState),
			)
			errors.add(
				trackerYaw(belowRightUpperLeg) -
					relaxedPose.upperLeg.towards(Side.RIGHT) -
					trackerYaw(trackerState),
			)
		}
	}

	override fun visitArmTracker(
		side: Side,
		tracker: TrackerState,
		aboveUpperBodyOrArm: TrackerState?,
		belowHandOrArm: TrackerState?,
	) {
		// No error because arms can go anywhere
	}

	override fun visitHandTracker(
		side: Side,
		tracker: TrackerState,
		aboveArm: TrackerState?,
		oppositeHand: TrackerState?,
	) {
		// No error because hands can go anywhere
	}

	override fun visitUpperLegTracker(
		side: Side,
		tracker: TrackerState,
		aboveUpperBody: TrackerState?,
		belowLowerLeg: TrackerState?,
		oppositeUpperLeg: TrackerState?,
	) {
		if (aboveUpperBody != null) {
			errors.add(
				trackerYaw(aboveUpperBody) +
					relaxedPose.upperLeg.towards(side) -
					trackerYaw(tracker),
			)
		}

		if (belowLowerLeg != null) {
			errors.add(
				trackerYaw(belowLowerLeg) -
					relaxedPose.lowerLeg.towards(side) +
					relaxedPose.upperLeg.towards(side) -
					trackerYaw(tracker),
			)
		}
	}

	override fun visitLowerLegTracker(
		side: Side,
		tracker: TrackerState,
		aboveUpperLeg: TrackerState?,
		belowFoot: TrackerState?,
		oppositeLowerLeg: TrackerState?,
	) {
		if (aboveUpperLeg != null) {
			errors.add(
				trackerYaw(aboveUpperLeg) -
					relaxedPose.upperLeg.towards(side) +
					relaxedPose.lowerLeg.towards(side) -
					trackerYaw(tracker),
			)
		}

		if (belowFoot != null) {
			errors.add(
				trackerYaw(belowFoot) -
					relaxedPose.foot.towards(side) +
					relaxedPose.lowerLeg.towards(side) -
					trackerYaw(tracker),
			)
		}
	}

	override fun visitFootTracker(
		side: Side,
		tracker: TrackerState,
		aboveLowerLeg: TrackerState?,
		oppositeFoot: TrackerState?,
	) {
		if (aboveLowerLeg != null) {
			errors.add(
				trackerYaw(aboveLowerLeg) -
					relaxedPose.lowerLeg.towards(side) +
					relaxedPose.foot.towards(side) -
					trackerYaw(tracker),
			)
		}
	}
}
