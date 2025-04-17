package dev.slimevr.tracking.processor.stayaligned.adjust

import dev.slimevr.math.Angle
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
	private val centerYaw: Angle,
	private val relaxedPose: RelaxedPose,
) : TrackerSkeleton.TrackerVisitor<Angle> {

	override fun visitHeadTracker(
		tracker: Tracker,
		belowUpperBody: Tracker?,
	): Angle =
		centerYaw - trackerYaw(tracker)

	override fun visitUpperBodyTracker(
		tracker: Tracker,
		aboveHeadOrUpperBody: Tracker?,
		belowUpperBody: Tracker?,
	): Angle =
		centerYaw - trackerYaw(tracker)

	override fun visitUpperBodyTracker(
		tracker: Tracker,
		aboveHeadOrUpperBody: Tracker?,
		belowLeftUpperLeg: Tracker?,
		belowRightUpperLeg: Tracker?,
	): Angle =
		centerYaw - trackerYaw(tracker)

	override fun visitArmTracker(
		side: Side,
		tracker: Tracker,
		aboveUpperBodyOrArm: Tracker?,
		belowHandOrArm: Tracker?,
	): Angle =
		// Arms can go anywhere
		Angle.ZERO

	override fun visitHandTracker(
		side: Side,
		tracker: Tracker,
		aboveArm: Tracker?,
		oppositeHand: Tracker?,
	): Angle =
		// Hands can go anywhere
		Angle.ZERO

	override fun visitUpperLegTracker(
		side: Side,
		tracker: Tracker,
		aboveUpperBody: Tracker?,
		belowLowerLeg: Tracker?,
		oppositeUpperLeg: Tracker?,
	): Angle =
		(centerYaw + extraYaw(side, relaxedPose.upperLeg)) - trackerYaw(tracker)

	override fun visitLowerLegTracker(
		side: Side,
		tracker: Tracker,
		aboveUpperLeg: Tracker?,
		belowFoot: Tracker?,
		oppositeLowerLeg: Tracker?,
	): Angle =
		(centerYaw + extraYaw(side, relaxedPose.lowerLeg)) - trackerYaw(tracker)

	override fun visitFootTracker(
		side: Side,
		tracker: Tracker,
		aboveLowerLeg: Tracker?,
		oppositeFoot: Tracker?,
	): Angle =
		(centerYaw + extraYaw(side, relaxedPose.foot)) - trackerYaw(tracker)
}
