package dev.slimevr.tracking.processor.skeleton.refactor

import dev.slimevr.tracking.processor.Bone
import dev.slimevr.tracking.processor.BoneType
import dev.slimevr.tracking.processor.Constraint
import dev.slimevr.tracking.processor.Constraint.Companion.ConstraintType

class Skeleton(
	val isTrackingLeftArmFromController: Boolean,
	val isTrackingRightArmFromController: Boolean,
) {
	// Upper body bones
	val headBone = Bone(BoneType.HEAD, Constraint(ConstraintType.COMPLETE))
	val neckBone = Bone(BoneType.NECK, Constraint(ConstraintType.COMPLETE))
	val upperChestBone = Bone(BoneType.UPPER_CHEST, Constraint(ConstraintType.TWIST_SWING, 90f, 120f))
	val chestBone = Bone(BoneType.CHEST, Constraint(ConstraintType.TWIST_SWING, 60f, 120f))
	val waistBone = Bone(BoneType.WAIST, Constraint(ConstraintType.TWIST_SWING, 60f, 120f))
	val hipBone = Bone(BoneType.HIP, Constraint(ConstraintType.TWIST_SWING, 60f, 120f))

	// Lower body bones
	val leftHipBone = Bone(BoneType.LEFT_HIP, Constraint(ConstraintType.TWIST_SWING, 0f, 15f))
	val rightHipBone = Bone(BoneType.RIGHT_HIP, Constraint(ConstraintType.TWIST_SWING, 0f, 15f))
	val leftUpperLegBone = Bone(BoneType.LEFT_UPPER_LEG, Constraint(ConstraintType.TWIST_SWING, 120f, 180f))
	val rightUpperLegBone = Bone(BoneType.RIGHT_UPPER_LEG, Constraint(ConstraintType.TWIST_SWING, 120f, 180f))
	val leftLowerLegBone = Bone(BoneType.LEFT_LOWER_LEG, Constraint(ConstraintType.LOOSE_HINGE, 180f, 0f, 50f))
	val rightLowerLegBone = Bone(BoneType.RIGHT_LOWER_LEG, Constraint(ConstraintType.LOOSE_HINGE, 180f, 0f, 50f))
	val leftFootBone = Bone(BoneType.LEFT_FOOT, Constraint(ConstraintType.TWIST_SWING, 60f, 60f))
	val rightFootBone = Bone(BoneType.RIGHT_FOOT, Constraint(ConstraintType.TWIST_SWING, 60f, 60f))

	// Arm bones
	val leftUpperShoulderBone = Bone(BoneType.LEFT_UPPER_SHOULDER, Constraint(ConstraintType.COMPLETE))
	val rightUpperShoulderBone = Bone(BoneType.RIGHT_UPPER_SHOULDER, Constraint(ConstraintType.COMPLETE))
	val leftShoulderBone = Bone(BoneType.LEFT_SHOULDER, Constraint(ConstraintType.TWIST_SWING, 0f, 30f))
	val rightShoulderBone = Bone(BoneType.RIGHT_SHOULDER, Constraint(ConstraintType.TWIST_SWING, 0f, 30f))
	val leftUpperArmBone = Bone(BoneType.LEFT_UPPER_ARM, Constraint(ConstraintType.TWIST_SWING, 120f, 180f))
	val rightUpperArmBone = Bone(BoneType.RIGHT_UPPER_ARM, Constraint(ConstraintType.TWIST_SWING, 120f, 180f))
	val leftLowerArmBone = Bone(BoneType.LEFT_LOWER_ARM, Constraint(ConstraintType.LOOSE_HINGE, 0f, -180f, 40f))
	val rightLowerArmBone = Bone(BoneType.RIGHT_LOWER_ARM, Constraint(ConstraintType.LOOSE_HINGE, 0f, -180f, 40f))
	val leftHandBone = Bone(BoneType.LEFT_HAND, Constraint(ConstraintType.TWIST_SWING, 120f, 120f))
	val rightHandBone = Bone(BoneType.RIGHT_HAND, Constraint(ConstraintType.TWIST_SWING, 120f, 120f))

	// Tracker bones
	val headTrackerBone = Bone(BoneType.HEAD_TRACKER, Constraint(ConstraintType.COMPLETE))
	val chestTrackerBone = Bone(BoneType.CHEST_TRACKER, Constraint(ConstraintType.COMPLETE))
	val hipTrackerBone = Bone(BoneType.HIP_TRACKER, Constraint(ConstraintType.COMPLETE))
	val leftKneeTrackerBone = Bone(BoneType.LEFT_KNEE_TRACKER, Constraint(ConstraintType.COMPLETE))
	val rightKneeTrackerBone = Bone(BoneType.RIGHT_KNEE_TRACKER, Constraint(ConstraintType.COMPLETE))
	val leftFootTrackerBone = Bone(BoneType.LEFT_FOOT_TRACKER, Constraint(ConstraintType.COMPLETE))
	val rightFootTrackerBone = Bone(BoneType.RIGHT_FOOT_TRACKER, Constraint(ConstraintType.COMPLETE))
	val leftElbowTrackerBone = Bone(BoneType.LEFT_ELBOW_TRACKER, Constraint(ConstraintType.COMPLETE))
	val rightElbowTrackerBone = Bone(BoneType.RIGHT_ELBOW_TRACKER, Constraint(ConstraintType.COMPLETE))
	val leftHandTrackerBone = Bone(BoneType.LEFT_HAND_TRACKER, Constraint(ConstraintType.COMPLETE))
	val rightHandTrackerBone = Bone(BoneType.RIGHT_HAND_TRACKER, Constraint(ConstraintType.COMPLETE))

	init {
		assembleSkeleton()
	}

	private fun assembleSkeleton() {
		// Assemble upper skeleton (head to hip)
		headBone.attachChild(neckBone)
		neckBone.attachChild(upperChestBone)
		upperChestBone.attachChild(chestBone)
		chestBone.attachChild(waistBone)
		waistBone.attachChild(hipBone)

		// Assemble lower skeleton (hip to feet)
		hipBone.attachChild(leftHipBone)
		hipBone.attachChild(rightHipBone)
		leftHipBone.attachChild(leftUpperLegBone)
		rightHipBone.attachChild(rightUpperLegBone)
		leftUpperLegBone.attachChild(leftLowerLegBone)
		rightUpperLegBone.attachChild(rightLowerLegBone)
		leftLowerLegBone.attachChild(leftFootBone)
		rightLowerLegBone.attachChild(rightFootBone)

		// Attach tracker bones for tracker offsets
		neckBone.attachChild(headTrackerBone)
		upperChestBone.attachChild(chestTrackerBone)
		hipBone.attachChild(hipTrackerBone)
		leftUpperLegBone.attachChild(leftKneeTrackerBone)
		rightUpperLegBone.attachChild(rightKneeTrackerBone)
		leftFootBone.attachChild(leftFootTrackerBone)
		rightFootBone.attachChild(rightFootTrackerBone)

		assembleSkeletonArms()
	}

	private fun assembleSkeletonArms() {
		// Shoulders
		neckBone.attachChild(leftUpperShoulderBone)
		neckBone.attachChild(rightUpperShoulderBone)
		leftUpperShoulderBone.attachChild(leftShoulderBone)
		rightUpperShoulderBone.attachChild(rightShoulderBone)

		// Upper arm
		leftShoulderBone.attachChild(leftUpperArmBone)
		rightShoulderBone.attachChild(rightUpperArmBone)

		// Lower arm and hand
		if (isTrackingLeftArmFromController) {
			leftHandTrackerBone.attachChild(leftHandBone)
			leftHandBone.attachChild(leftLowerArmBone)
			leftLowerArmBone.attachChild(leftElbowTrackerBone)
		} else {
			leftUpperArmBone.attachChild(leftLowerArmBone)
			leftUpperArmBone.attachChild(leftElbowTrackerBone)
			leftLowerArmBone.attachChild(leftHandBone)
			leftHandBone.attachChild(leftHandTrackerBone)
		}
		if (isTrackingRightArmFromController) {
			rightHandTrackerBone.attachChild(rightHandBone)
			rightHandBone.attachChild(rightLowerArmBone)
			rightLowerArmBone.attachChild(rightElbowTrackerBone)
		} else {
			rightUpperArmBone.attachChild(rightLowerArmBone)
			rightUpperArmBone.attachChild(rightElbowTrackerBone)
			rightLowerArmBone.attachChild(rightHandBone)
			rightHandBone.attachChild(rightHandTrackerBone)
		}
	}

	fun getBone(bone: BoneType): Bone? = when (bone) {
		BoneType.HEAD -> headBone
		BoneType.HEAD_TRACKER -> headTrackerBone
		BoneType.NECK -> neckBone
		BoneType.UPPER_CHEST -> upperChestBone
		BoneType.CHEST_TRACKER -> chestTrackerBone
		BoneType.CHEST -> chestBone
		BoneType.WAIST -> waistBone
		BoneType.HIP -> hipBone
		BoneType.HIP_TRACKER -> hipTrackerBone
		BoneType.LEFT_HIP -> leftHipBone
		BoneType.RIGHT_HIP -> rightHipBone
		BoneType.LEFT_UPPER_LEG -> leftUpperLegBone
		BoneType.RIGHT_UPPER_LEG -> rightUpperLegBone
		BoneType.LEFT_KNEE_TRACKER -> leftKneeTrackerBone
		BoneType.RIGHT_KNEE_TRACKER -> rightKneeTrackerBone
		BoneType.LEFT_LOWER_LEG -> leftLowerLegBone
		BoneType.RIGHT_LOWER_LEG -> rightLowerLegBone
		BoneType.LEFT_FOOT -> leftFootBone
		BoneType.RIGHT_FOOT -> rightFootBone
		BoneType.LEFT_FOOT_TRACKER -> leftFootTrackerBone
		BoneType.RIGHT_FOOT_TRACKER -> rightFootTrackerBone
		BoneType.LEFT_UPPER_SHOULDER -> leftUpperShoulderBone
		BoneType.RIGHT_UPPER_SHOULDER -> rightUpperShoulderBone
		BoneType.LEFT_SHOULDER -> leftShoulderBone
		BoneType.RIGHT_SHOULDER -> rightShoulderBone
		BoneType.LEFT_UPPER_ARM -> leftUpperArmBone
		BoneType.RIGHT_UPPER_ARM -> rightUpperArmBone
		BoneType.LEFT_ELBOW_TRACKER -> leftElbowTrackerBone
		BoneType.RIGHT_ELBOW_TRACKER -> rightElbowTrackerBone
		BoneType.LEFT_LOWER_ARM -> leftLowerArmBone
		BoneType.RIGHT_LOWER_ARM -> rightLowerArmBone
		BoneType.LEFT_HAND -> leftHandBone
		BoneType.RIGHT_HAND -> rightHandBone
		BoneType.LEFT_HAND_TRACKER -> leftHandTrackerBone
		BoneType.RIGHT_HAND_TRACKER -> rightHandTrackerBone
		else -> null
	}
}
