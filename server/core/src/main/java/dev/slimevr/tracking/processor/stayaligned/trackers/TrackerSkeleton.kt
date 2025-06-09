package dev.slimevr.tracking.processor.stayaligned.trackers

import dev.slimevr.tracking.processor.skeleton.HumanSkeleton
import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.TrackerPosition

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
class TrackerSkeleton(skeleton: HumanSkeleton) {

	val allTrackers = with(skeleton) {
		listOfNotNull(
			headTracker,
			// Upper body
			neckTracker,
			upperChestTracker,
			chestTracker,
			waistTracker,
			hipTracker,
			// Left arm
			leftShoulderTracker,
			leftUpperArmTracker,
			leftLowerArmTracker,
			leftHandTracker,
			// Right arm
			rightShoulderTracker,
			rightUpperArmTracker,
			rightLowerArmTracker,
			rightHandTracker,
			// Left leg
			leftUpperLegTracker,
			leftLowerLegTracker,
			leftFootTracker,
			// Right leg
			rightUpperLegTracker,
			rightLowerLegTracker,
			rightFootTracker,
		)
	}

	// Tracker groups
	val upperBody = with(skeleton) {
		listOfNotNull(
			neckTracker,
			upperChestTracker,
			chestTracker,
			waistTracker,
			hipTracker,
		)
	}

	val leftArm = with(skeleton) {
		listOfNotNull(
			leftShoulderTracker,
			leftUpperArmTracker,
			leftLowerArmTracker,
		)
	}

	val rightArm = with(skeleton) {
		listOfNotNull(
			rightShoulderTracker,
			rightUpperArmTracker,
			rightLowerArmTracker,
		)
	}

	// Individual trackers
	val head = skeleton.headTracker
	val leftHand = skeleton.leftHandTracker
	val rightHand = skeleton.rightHandTracker
	val leftUpperLeg = skeleton.leftUpperLegTracker
	val leftLowerLeg = skeleton.leftLowerLegTracker
	val leftFoot = skeleton.leftFootTracker
	val rightUpperLeg = skeleton.rightUpperLegTracker
	val rightLowerLeg = skeleton.rightLowerLegTracker
	val rightFoot = skeleton.rightFootTracker

	/**
	 * Visits a tracker within the skeleton.
	 */
	fun visit(
		tracker: Tracker,
		visitor: TrackerVisitor,
	) {
		when (tracker.trackerPosition) {
			TrackerPosition.HEAD ->
				if (tracker == head) {
					visitor.visitHeadTracker(tracker, upperBody.firstOrNull())
				}

			// Upper body
			TrackerPosition.NECK,
			TrackerPosition.UPPER_CHEST,
			TrackerPosition.CHEST,
			TrackerPosition.WAIST,
			TrackerPosition.HIP,
			->
				visitUpperBodyTrackers(
					tracker,
					visitor,
					head,
					upperBody,
					leftUpperLeg,
					rightUpperLeg,
				)

			// Left arm
			TrackerPosition.LEFT_SHOULDER,
			TrackerPosition.LEFT_UPPER_ARM,
			TrackerPosition.LEFT_LOWER_ARM,
			->
				visitArmTrackers(
					tracker,
					visitor,
					Side.LEFT,
					upperBody.firstOrNull(),
					leftArm,
					leftHand,
				)

			// Right arm
			TrackerPosition.RIGHT_SHOULDER,
			TrackerPosition.RIGHT_UPPER_ARM,
			TrackerPosition.RIGHT_LOWER_ARM,
			->
				visitArmTrackers(
					tracker,
					visitor,
					Side.RIGHT,
					upperBody.firstOrNull(),
					rightArm,
					rightHand,
				)

			TrackerPosition.LEFT_HAND ->
				if (tracker == leftHand) {
					visitor.visitHandTracker(
						Side.LEFT,
						tracker,
						leftArm.lastOrNull(),
						rightHand,
					)
				}

			TrackerPosition.RIGHT_HAND ->
				if (tracker == rightHand) {
					visitor.visitHandTracker(
						Side.RIGHT,
						tracker,
						rightArm.lastOrNull(),
						leftHand,
					)
				}

			TrackerPosition.LEFT_UPPER_LEG ->
				if (tracker == leftUpperLeg) {
					visitor.visitUpperLegTracker(
						Side.LEFT,
						tracker,
						upperBody.lastOrNull(),
						leftLowerLeg,
						rightUpperLeg,
					)
				}

			TrackerPosition.RIGHT_UPPER_LEG ->
				if (tracker == rightUpperLeg) {
					visitor.visitUpperLegTracker(
						Side.RIGHT,
						tracker,
						upperBody.lastOrNull(),
						rightLowerLeg,
						leftUpperLeg,
					)
				}

			TrackerPosition.LEFT_LOWER_LEG ->
				if (tracker == leftLowerLeg) {
					visitor.visitLowerLegTracker(
						Side.LEFT,
						tracker,
						leftUpperLeg,
						leftFoot,
						rightLowerLeg,
					)
				}

			TrackerPosition.RIGHT_LOWER_LEG ->
				if (tracker == rightLowerLeg) {
					visitor.visitLowerLegTracker(
						Side.RIGHT,
						tracker,
						rightUpperLeg,
						rightFoot,
						leftLowerLeg,
					)
				}

			TrackerPosition.LEFT_FOOT ->
				if (tracker == leftFoot) {
					visitor.visitFootTracker(
						Side.LEFT,
						tracker,
						leftLowerLeg,
						rightFoot,
					)
				}

			TrackerPosition.RIGHT_FOOT ->
				if (tracker == rightFoot) {
					visitor.visitFootTracker(
						Side.RIGHT,
						tracker,
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
		tracker: Tracker,
		visitor: TrackerVisitor,
		head: Tracker?,
		upperBody: List<Tracker>,
		leftUpperLeg: Tracker?,
		rightUpperLeg: Tracker?,
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
		tracker: Tracker,
		visitor: TrackerVisitor,
		side: Side,
		upperBody: Tracker?,
		arm: List<Tracker>,
		hand: Tracker?,
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
			tracker: Tracker,
			belowUpperBody: Tracker?,
		)

		/**
		 * Visits an upper body tracker (except for the bottom-most tracker).
		 */
		fun visitUpperBodyTracker(
			tracker: Tracker,
			aboveHeadOrUpperBody: Tracker?,
			belowUpperBody: Tracker?,
		)

		/**
		 * Visits the bottom-most upper body tracker.
		 */
		fun visitUpperBodyTracker(
			tracker: Tracker,
			aboveHeadOrUpperBody: Tracker?,
			belowLeftUpperLeg: Tracker?,
			belowRightUpperLeg: Tracker?,
		)

		/**
		 * Visits an arm tracker.
		 */
		fun visitArmTracker(
			side: Side,
			tracker: Tracker,
			aboveUpperBodyOrArm: Tracker?,
			belowHandOrArm: Tracker?,
		)

		/**
		 * Visits a hand tracker.
		 */
		fun visitHandTracker(
			side: Side,
			tracker: Tracker,
			aboveArm: Tracker?,
			oppositeHand: Tracker?,
		)

		/**
		 * Visits an upper leg tracker.
		 */
		fun visitUpperLegTracker(
			side: Side,
			tracker: Tracker,
			aboveUpperBody: Tracker?,
			belowLowerLeg: Tracker?,
			oppositeUpperLeg: Tracker?,
		)

		/**
		 * Visits a lower leg tracker.
		 */
		fun visitLowerLegTracker(
			side: Side,
			tracker: Tracker,
			aboveUpperLeg: Tracker?,
			belowFoot: Tracker?,
			oppositeLowerLeg: Tracker?,
		)

		/**
		 * Visits a foot tracker.
		 */
		fun visitFootTracker(
			side: Side,
			tracker: Tracker,
			aboveLowerLeg: Tracker?,
			oppositeFoot: Tracker?,
		)
	}
}
