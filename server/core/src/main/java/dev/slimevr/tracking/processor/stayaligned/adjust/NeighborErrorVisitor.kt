package dev.slimevr.tracking.processor.stayaligned.adjust

import dev.slimevr.math.Angle
import dev.slimevr.math.AngleAverage
import dev.slimevr.tracking.processor.stayaligned.adjust.TrackerYaw.extraYaw
import dev.slimevr.tracking.processor.stayaligned.adjust.TrackerYaw.trackerYaw
import dev.slimevr.tracking.processor.stayaligned.skeleton.RelaxedPose
import dev.slimevr.tracking.processor.stayaligned.skeleton.Side
import dev.slimevr.tracking.processor.stayaligned.skeleton.TrackerSkeleton
import dev.slimevr.tracking.trackers.Tracker

/**
 * Error between a tracker's yaw and its neighbors' yaws.
 */
class NeighborErrorVisitor(
	private val relaxedPose: RelaxedPose,
) : TrackerSkeleton.TrackerVisitor<Angle> {

	override fun visitHeadTracker(
		tracker: Tracker,
		belowUpperBody: Tracker?,
	): Angle {
		val targetYaw = AngleAverage()

		if (belowUpperBody != null) {
			targetYaw.add(trackerYaw(belowUpperBody))
		}

		return error(tracker, targetYaw)
	}

	override fun visitUpperBodyTracker(
		tracker: Tracker,
		aboveHeadOrUpperBody: Tracker?,
		belowUpperBody: Tracker?,
	): Angle {
		val targetYaw = AngleAverage()

		if (
			aboveHeadOrUpperBody != null // &&
// 			// Head often drags the upper body trackers off to the side, so ignore it
// 			aboveHeadOrUpperBody.trackerPosition != TrackerPosition.HEAD
		) {
			targetYaw.add(trackerYaw(aboveHeadOrUpperBody))
		}

		if (belowUpperBody != null) {
			targetYaw.add(trackerYaw(belowUpperBody))
		}

		return error(tracker, targetYaw)
	}

	override fun visitUpperBodyTracker(
		tracker: Tracker,
		aboveHeadOrUpperBody: Tracker?,
		belowLeftUpperLeg: Tracker?,
		belowRightUpperLeg: Tracker?,
	): Angle {
		val targetYaw = AngleAverage()

		if (
			aboveHeadOrUpperBody != null // &&
// 			// Head often drags the upper body trackers off to the side, so ignore it
// 			aboveHeadOrUpperBody.trackerPosition != TrackerPosition.HEAD
		) {
			targetYaw.add(trackerYaw(aboveHeadOrUpperBody))
		}

		// Only consider upper leg trackers if both are available, so that the upper
		// body tracker can be balanced between both
		if (
			belowLeftUpperLeg != null &&
			belowRightUpperLeg != null
		) {
			targetYaw.add(trackerYaw(belowLeftUpperLeg) - extraYaw(Side.LEFT, relaxedPose.upperLeg))
			targetYaw.add(trackerYaw(belowRightUpperLeg) - extraYaw(Side.RIGHT, relaxedPose.upperLeg))
		}

		return error(tracker, targetYaw)
	}

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
	): Angle {
		val targetYaw = AngleAverage()

		if (aboveUpperBody != null) {
			targetYaw.add(
				trackerYaw(aboveUpperBody) + extraYaw(side, relaxedPose.upperLeg),
			)
		}

		if (belowLowerLeg != null) {
			targetYaw.add(
				trackerYaw(belowLowerLeg) - extraYaw(side, relaxedPose.lowerLeg - relaxedPose.upperLeg),
			)
		}

		return error(tracker, targetYaw)
	}

	override fun visitLowerLegTracker(
		side: Side,
		tracker: Tracker,
		aboveUpperLeg: Tracker?,
		belowFoot: Tracker?,
		oppositeLowerLeg: Tracker?,
	): Angle {
		val targetYaw = AngleAverage()

		if (aboveUpperLeg != null) {
			targetYaw.add(trackerYaw(aboveUpperLeg) + extraYaw(side, relaxedPose.lowerLeg - relaxedPose.upperLeg))
		}

		if (belowFoot != null) {
			targetYaw.add(trackerYaw(belowFoot) - extraYaw(side, relaxedPose.foot - relaxedPose.lowerLeg))
		}

		return error(tracker, targetYaw)
	}

	override fun visitFootTracker(
		side: Side,
		tracker: Tracker,
		aboveLowerLeg: Tracker?,
		oppositeFoot: Tracker?,
	): Angle {
		val targetYaw = AngleAverage()

		if (aboveLowerLeg != null) {
			targetYaw.add(trackerYaw(aboveLowerLeg) + extraYaw(side, relaxedPose.foot - relaxedPose.lowerLeg))
		}

		return error(tracker, targetYaw)
	}

	companion object {

		fun error(tracker: Tracker, targetYaw: AngleAverage) =
			if (targetYaw.isEmpty()) {
				Angle.ZERO
			} else {
				targetYaw.toAngle() - trackerYaw(tracker)
			}
	}
}
