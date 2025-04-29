package dev.slimevr.tracking.processor.stayaligned.adjust

import dev.slimevr.math.Angle
import dev.slimevr.math.AngleErrors
import dev.slimevr.tracking.processor.stayaligned.adjust.TrackerYaw.extraYaw
import dev.slimevr.tracking.processor.stayaligned.adjust.TrackerYaw.trackerYaw
import dev.slimevr.tracking.processor.stayaligned.poses.RelaxedPose
import dev.slimevr.tracking.processor.stayaligned.trackers.Side
import dev.slimevr.tracking.processor.stayaligned.trackers.TrackerSkeleton
import dev.slimevr.tracking.trackers.Tracker

/**
 * Assumes that the body is centered around an average yaw, and returns the error of the
 * tracker with respect to that average yaw.
 */
class CenterErrorVisitor(
	val centerYaw: Angle,
	val relaxedPose: RelaxedPose,
	val errors: AngleErrors,
) : TrackerSkeleton.TrackerVisitor {

	override fun visitHeadTracker(
		tracker: Tracker,
		belowUpperBody: Tracker?,
	) {
		errors.add(centerYaw - trackerYaw(tracker))
	}

	override fun visitUpperBodyTracker(
		tracker: Tracker,
		aboveHeadOrUpperBody: Tracker?,
		belowUpperBody: Tracker?,
	) {
		errors.add(centerYaw - trackerYaw(tracker))
	}

	override fun visitUpperBodyTracker(
		tracker: Tracker,
		aboveHeadOrUpperBody: Tracker?,
		belowLeftUpperLeg: Tracker?,
		belowRightUpperLeg: Tracker?,
	) {
		errors.add(centerYaw - trackerYaw(tracker))
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
		errors.add(centerYaw + extraYaw(side, relaxedPose.upperLeg) - trackerYaw(tracker))
	}

	override fun visitLowerLegTracker(
		side: Side,
		tracker: Tracker,
		aboveUpperLeg: Tracker?,
		belowFoot: Tracker?,
		oppositeLowerLeg: Tracker?,
	) {
		errors.add(centerYaw + extraYaw(side, relaxedPose.lowerLeg) - trackerYaw(tracker))
	}

	override fun visitFootTracker(
		side: Side,
		tracker: Tracker,
		aboveLowerLeg: Tracker?,
		oppositeFoot: Tracker?,
	) {
		errors.add(centerYaw + extraYaw(side, relaxedPose.foot) - trackerYaw(tracker))
	}
}
