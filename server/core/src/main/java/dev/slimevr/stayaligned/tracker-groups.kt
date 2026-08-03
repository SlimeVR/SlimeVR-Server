package dev.slimevr.stayaligned

import dev.slimevr.tracker.TrackerState
import dev.slimevr.tracker.getFineFor
import dev.slimevr.util.Side
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.TrackerStatus

/**
 * Represents groups of trackers.
 *
 * The groups are made of the following:
 * - The upper body
 * - The left arm
 * - The right arm
 * - The head
 * - The left hand
 * - The right hand
 * - The left upper leg
 * - The right upper leg
 * - The left lower leg
 * - The right lower leg
 * - The left foot
 * - The right foot
 */
class TrackerGroups(trackerStates: List<TrackerState>) {

	val upperBody = listOfNotNull(
		trackerStates.getFineFor(BodyPart.NECK),
		trackerStates.getFineFor(BodyPart.UPPER_CHEST),
		trackerStates.getFineFor(BodyPart.CHEST),
		trackerStates.getFineFor(BodyPart.WAIST),
		trackerStates.getFineFor(BodyPart.HIP),
	)
	val leftArm = listOfNotNull(
		trackerStates.getFineFor(BodyPart.LEFT_SHOULDER),
		trackerStates.getFineFor(BodyPart.LEFT_UPPER_ARM),
		trackerStates.getFineFor(BodyPart.LEFT_LOWER_ARM),
	)
	val rightArm = listOfNotNull(
		trackerStates.getFineFor(BodyPart.RIGHT_SHOULDER),
		trackerStates.getFineFor(BodyPart.RIGHT_UPPER_ARM),
		trackerStates.getFineFor(BodyPart.RIGHT_LOWER_ARM),
	)
	val head = trackerStates.getFineFor(BodyPart.HEAD)
	val leftHand = trackerStates.getFineFor(BodyPart.LEFT_HAND)
	val rightHand = trackerStates.getFineFor(BodyPart.RIGHT_HAND)
	val leftUpperLeg = trackerStates.getFineFor(BodyPart.LEFT_UPPER_LEG)
	val leftLowerLeg = trackerStates.getFineFor(BodyPart.LEFT_LOWER_LEG)
	val leftFoot = trackerStates.getFineFor(BodyPart.LEFT_FOOT)
	val rightUpperLeg = trackerStates.getFineFor(BodyPart.RIGHT_UPPER_LEG)
	val rightLowerLeg = trackerStates.getFineFor(BodyPart.RIGHT_LOWER_LEG)
	val rightFoot = trackerStates.getFineFor(BodyPart.RIGHT_FOOT)

	/**
	 * Visits a tracker within the groups.
	 */
	fun visit(
		trackerState: TrackerState,
		visitor: TrackerVisitor,
	) {
		when (trackerState.bodyPart) {
			BodyPart.HEAD ->
				visitor.visitHeadTracker(trackerState, upperBody.firstOrNull())

			// Upper body
			BodyPart.NECK,
			BodyPart.UPPER_CHEST,
			BodyPart.CHEST,
			BodyPart.WAIST,
			BodyPart.HIP,
			->
				visitUpperBodyTrackers(
					trackerState,
					visitor,
					head,
					upperBody,
					leftUpperLeg,
					rightUpperLeg,
				)

			// Left arm
			BodyPart.LEFT_SHOULDER,
			BodyPart.LEFT_UPPER_ARM,
			BodyPart.LEFT_LOWER_ARM,
			->
				visitArmTrackers(
					trackerState,
					visitor,
					Side.LEFT,
					upperBody.firstOrNull(),
					leftArm,
					leftHand,
				)

			// Right arm
			BodyPart.RIGHT_SHOULDER,
			BodyPart.RIGHT_UPPER_ARM,
			BodyPart.RIGHT_LOWER_ARM,
			->
				visitArmTrackers(
					trackerState,
					visitor,
					Side.RIGHT,
					upperBody.firstOrNull(),
					rightArm,
					rightHand,
				)

			BodyPart.LEFT_HAND ->
				if (trackerState == leftHand) {
					visitor.visitHandTracker(
						Side.LEFT,
						trackerState,
						leftArm.lastOrNull(),
						rightHand,
					)
				}

			BodyPart.RIGHT_HAND ->
				if (trackerState == rightHand) {
					visitor.visitHandTracker(
						Side.RIGHT,
						trackerState,
						rightArm.lastOrNull(),
						leftHand,
					)
				}

			BodyPart.LEFT_UPPER_LEG ->
				if (trackerState == leftUpperLeg) {
					visitor.visitUpperLegTracker(
						Side.LEFT,
						trackerState,
						upperBody.lastOrNull(),
						leftLowerLeg,
						rightUpperLeg,
					)
				}

			BodyPart.RIGHT_UPPER_LEG ->
				if (trackerState == rightUpperLeg) {
					visitor.visitUpperLegTracker(
						Side.RIGHT,
						trackerState,
						upperBody.lastOrNull(),
						rightLowerLeg,
						leftUpperLeg,
					)
				}

			BodyPart.LEFT_LOWER_LEG ->
				if (trackerState == leftLowerLeg) {
					visitor.visitLowerLegTracker(
						Side.LEFT,
						trackerState,
						leftUpperLeg,
						leftFoot,
						rightLowerLeg,
					)
				}

			BodyPart.RIGHT_LOWER_LEG ->
				if (trackerState == rightLowerLeg) {
					visitor.visitLowerLegTracker(
						Side.RIGHT,
						trackerState,
						rightUpperLeg,
						rightFoot,
						leftLowerLeg,
					)
				}

			BodyPart.LEFT_FOOT ->
				if (trackerState == leftFoot) {
					visitor.visitFootTracker(
						Side.LEFT,
						trackerState,
						leftLowerLeg,
						rightFoot,
					)
				}

			BodyPart.RIGHT_FOOT ->
				if (trackerState == rightFoot) {
					visitor.visitFootTracker(
						Side.RIGHT,
						trackerState,
						rightLowerLeg,
						leftFoot,
					)
				}

			else -> {
				// No tracker to visit
			}
		}
	}

	private fun visitUpperBodyTrackers(
		tracker: TrackerState,
		visitor: TrackerVisitor,
		head: TrackerState?,
		upperBody: List<TrackerState?>,
		leftUpperLeg: TrackerState?,
		rightUpperLeg: TrackerState?,
	) {
		val index = upperBody.indexOf(tracker)
		if (index < 0) {
			return
		}

		if (index == 0) {
			if (upperBody.size == 1) {
				// Only upper body tracker
				visitor.visitUpperBodyTracker(
					tracker,
					head,
					leftUpperLeg,
					rightUpperLeg,
				)
			} else {
				// First upper body tracker
				visitor.visitUpperBodyTracker(
					tracker,
					head,
					upperBody[1],
				)
			}
		} else if (index < upperBody.size - 1) {
			// Middle upper body tracker
			visitor.visitUpperBodyTracker(
				tracker,
				upperBody[index - 1],
				upperBody[index + 1],
			)
		} else {
			// Last upper body tracker
			visitor.visitUpperBodyTracker(
				tracker,
				upperBody[index - 1],
				leftUpperLeg,
				rightUpperLeg,
			)
		}
	}

	private fun visitArmTrackers(
		tracker: TrackerState,
		visitor: TrackerVisitor,
		side: Side,
		upperBody: TrackerState?,
		arm: List<TrackerState?>,
		hand: TrackerState?,
	) {
		val index = arm.indexOf(tracker)
		if (index < 0) {
			return
		}

		if (index == 0) {
			if (arm.size == 1) {
				// Only arm tracker
				visitor.visitArmTracker(
					side,
					tracker,
					upperBody,
					hand,
				)
			} else {
				// First arm tracker
				visitor.visitArmTracker(
					side,
					tracker,
					upperBody,
					arm[1],
				)
			}
		} else if (index < arm.size - 1) {
			// Middle arm tracker
			visitor.visitArmTracker(
				side,
				tracker,
				arm[index - 1],
				arm[index + 1],
			)
		} else {
			// Last arm tracker
			visitor.visitArmTracker(
				side,
				tracker,
				arm[index - 1],
				hand,
			)
		}
	}

	interface TrackerVisitor {

		/**
		 * Visits the head tracker.
		 */
		fun visitHeadTracker(
			trackerState: TrackerState,
			belowUpperBody: TrackerState?,
		)

		/**
		 * Visits an upper body tracker (except for the bottom-most tracker).
		 */
		fun visitUpperBodyTracker(
			trackerState: TrackerState,
			aboveHeadOrUpperBody: TrackerState?,
			belowUpperBody: TrackerState?,
		)

		/**
		 * Visits the bottom-most upper body tracker.
		 */
		fun visitUpperBodyTracker(
			trackerState: TrackerState,
			aboveHeadOrUpperBody: TrackerState?,
			belowLeftUpperLeg: TrackerState?,
			belowRightUpperLeg: TrackerState?,
		)

		/**
		 * Visits an arm tracker.
		 */
		fun visitArmTracker(
			side: Side,
			tracker: TrackerState,
			aboveUpperBodyOrArm: TrackerState?,
			belowHandOrArm: TrackerState?,
		)

		/**
		 * Visits a hand tracker.
		 */
		fun visitHandTracker(
			side: Side,
			tracker: TrackerState,
			aboveArm: TrackerState?,
			oppositeHand: TrackerState?,
		)

		/**
		 * Visits an upper leg tracker.
		 */
		fun visitUpperLegTracker(
			side: Side,
			tracker: TrackerState,
			aboveUpperBody: TrackerState?,
			belowLowerLeg: TrackerState?,
			oppositeUpperLeg: TrackerState?,
		)

		/**
		 * Visits a lower leg tracker.
		 */
		fun visitLowerLegTracker(
			side: Side,
			tracker: TrackerState,
			aboveUpperLeg: TrackerState?,
			belowFoot: TrackerState?,
			oppositeLowerLeg: TrackerState?,
		)

		/**
		 * Visits a foot tracker.
		 */
		fun visitFootTracker(
			side: Side,
			tracker: TrackerState,
			aboveLowerLeg: TrackerState?,
			oppositeFoot: TrackerState?,
		)
	}
}
