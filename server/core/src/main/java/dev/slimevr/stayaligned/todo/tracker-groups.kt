package dev.slimevr.stayaligned.todo

import dev.slimevr.tracker.TrackerState
import dev.slimevr.util.Side
import solarxr_protocol.datatypes.BodyPart

/**
 * Represents a skeleton of trackers.
 *
 * The skeleton consists of:
 * - An upper body group
 * - A head tracker, connected to the top of the upper body group
 * - Two arm groups, connected to the top of the upper body group
 * - Two hands connected to the bottom of the corresponding arm group
 * - Two upper legs, connected to the bottom of the upper body group
 * - Two lower legs, connected to the bottom of each corresponding upper leg
 * - Two feet, connected to the bottom of each corresponding lower leg
 */
class TrackerGroup(trackerStates: List<TrackerState>) {

    private fun List<TrackerState>.get(bodyPart: BodyPart): TrackerState? {
        return this.firstOrNull { it.bodyPart == bodyPart }
    }

    val upperBody = listOfNotNull(
        trackerStates.get(BodyPart.UPPER_CHEST),
        trackerStates.get(BodyPart.CHEST),
        trackerStates.get(BodyPart.WAIST),
        trackerStates.get(BodyPart.HIP),
    )

    val leftArm = listOfNotNull(
        trackerStates.get(BodyPart.LEFT_SHOULDER),
        trackerStates.get(BodyPart.LEFT_UPPER_ARM),
        trackerStates.get(BodyPart.LEFT_LOWER_ARM),
    )

    val rightArm = listOfNotNull(
        trackerStates.get(BodyPart.RIGHT_SHOULDER),
        trackerStates.get(BodyPart.RIGHT_UPPER_ARM),
        trackerStates.get(BodyPart.RIGHT_LOWER_ARM),
    )

    // Individual trackers
    val head = trackerStates.get(BodyPart.HEAD)
    val leftHand = trackerStates.get(BodyPart.LEFT_HAND)
    val rightHand = trackerStates.get(BodyPart.RIGHT_HAND)
    val leftUpperLeg = trackerStates.get(BodyPart.LEFT_UPPER_LEG)
    val leftLowerLeg = trackerStates.get(BodyPart.LEFT_LOWER_LEG)
    val leftFoot = trackerStates.get(BodyPart.LEFT_FOOT)
    val rightUpperLeg = trackerStates.get(BodyPart.RIGHT_UPPER_LEG)
    val rightLowerLeg = trackerStates.get(BodyPart.RIGHT_LOWER_LEG)
    val rightFoot = trackerStates.get(BodyPart.RIGHT_FOOT)

    /**
     * Visits a tracker within the skeleton.
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
		val index = upperBody.map { it?.bodyPart }.indexOf(tracker.bodyPart)
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
		val index = arm.map { it?.bodyPart }.indexOf(tracker.bodyPart)
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
