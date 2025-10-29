package dev.slimevr.osc

import dev.slimevr.tracking.processor.BoneType
import dev.slimevr.tracking.trackers.TrackerPosition
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Unity HumanBodyBones from:
 * https://docs.unity3d.com/ScriptReference/HumanBodyBones.html
 */
@Serializable
enum class UnityBone(
	val stringVal: String,
	val boneType: BoneType?,
	val trackerPosition: TrackerPosition?,
) {
	@SerialName("hips")
	HIPS("Hips", BoneType.HIP, TrackerPosition.HIP),

	@SerialName("leftUpperLeg")
	LEFT_UPPER_LEG("LeftUpperLeg", BoneType.LEFT_UPPER_LEG, TrackerPosition.LEFT_UPPER_LEG),

	@SerialName("rightUpperLeg")
	RIGHT_UPPER_LEG("RightUpperLeg", BoneType.RIGHT_UPPER_LEG, TrackerPosition.RIGHT_UPPER_LEG),

	@SerialName("leftLowerLeg")
	LEFT_LOWER_LEG("LeftLowerLeg", BoneType.LEFT_LOWER_LEG, TrackerPosition.LEFT_LOWER_LEG),

	@SerialName("rightLowerLeg")
	RIGHT_LOWER_LEG("RightLowerLeg", BoneType.RIGHT_LOWER_LEG, TrackerPosition.RIGHT_LOWER_LEG),

	@SerialName("leftFoot")
	LEFT_FOOT("LeftFoot", BoneType.LEFT_FOOT, TrackerPosition.LEFT_FOOT),

	@SerialName("rightFoot")
	RIGHT_FOOT("RightFoot", BoneType.RIGHT_FOOT, TrackerPosition.RIGHT_FOOT),

	@SerialName("spine")
	SPINE("Spine", BoneType.WAIST, TrackerPosition.WAIST),

	@SerialName("chest")
	CHEST("Chest", BoneType.CHEST, TrackerPosition.CHEST),

	@SerialName("upperChest")
	UPPER_CHEST("UpperChest", BoneType.CHEST, TrackerPosition.CHEST),

	@SerialName("neck")
	NECK("Neck", BoneType.NECK, TrackerPosition.NECK),

	@SerialName("head")
	HEAD("Head", BoneType.HEAD, TrackerPosition.HEAD),

	@SerialName("leftShoulder")
	LEFT_SHOULDER("LeftShoulder", BoneType.LEFT_SHOULDER, TrackerPosition.LEFT_SHOULDER),

	@SerialName("rightShoulder")
	RIGHT_SHOULDER("RightShoulder", BoneType.RIGHT_SHOULDER, TrackerPosition.RIGHT_SHOULDER),

	@SerialName("leftUpperArm")
	LEFT_UPPER_ARM("LeftUpperArm", BoneType.LEFT_UPPER_ARM, TrackerPosition.LEFT_UPPER_ARM),

	@SerialName("rightUpperArm")
	RIGHT_UPPER_ARM("RightUpperArm", BoneType.RIGHT_UPPER_ARM, TrackerPosition.RIGHT_UPPER_ARM),

	@SerialName("leftLowerArm")
	LEFT_LOWER_ARM("LeftLowerArm", BoneType.LEFT_LOWER_ARM, TrackerPosition.LEFT_LOWER_ARM),

	@SerialName("rightLowerArm")
	RIGHT_LOWER_ARM("RightLowerArm", BoneType.RIGHT_LOWER_ARM, TrackerPosition.RIGHT_LOWER_ARM),

	@SerialName("leftHand")
	LEFT_HAND("LeftHand", BoneType.LEFT_HAND, TrackerPosition.LEFT_HAND),

	@SerialName("rightHand")
	RIGHT_HAND("RightHand", BoneType.RIGHT_HAND, TrackerPosition.RIGHT_HAND),

	@SerialName("leftToes")
	LEFT_TOES("LeftToes", null, null),

	@SerialName("rightToes")
	RIGHT_TOES("RightToes", null, null),

	@SerialName("leftEye")
	LEFT_EYE("LeftEye", null, null),

	@SerialName("rightEye")
	RIGHT_EYE("RightEye", null, null),

	@SerialName("jaw")
	JAW("Jaw", null, null),

	@SerialName("leftThumbMetacarpal")
	LEFT_THUMB_PROXIMAL("LeftThumbProximal", BoneType.LEFT_THUMB_METACARPAL, TrackerPosition.LEFT_THUMB_METACARPAL),

	@SerialName("leftThumbProximal")
	LEFT_THUMB_INTERMEDIATE("LeftThumbIntermediate", BoneType.LEFT_THUMB_PROXIMAL, TrackerPosition.LEFT_THUMB_PROXIMAL),

	@SerialName("leftThumbDistal")
	LEFT_THUMB_DISTAL("LeftThumbDistal", BoneType.LEFT_THUMB_DISTAL, TrackerPosition.LEFT_THUMB_DISTAL),

	@SerialName("leftIndexProximal")
	LEFT_INDEX_PROXIMAL("LeftIndexProximal", BoneType.LEFT_INDEX_PROXIMAL, TrackerPosition.LEFT_INDEX_PROXIMAL),

	@SerialName("leftIndexIntermediate")
	LEFT_INDEX_INTERMEDIATE("LeftIndexIntermediate", BoneType.LEFT_INDEX_INTERMEDIATE, TrackerPosition.LEFT_INDEX_INTERMEDIATE),

	@SerialName("leftIndexDistal")
	LEFT_INDEX_DISTAL("LeftIndexDistal", BoneType.LEFT_INDEX_DISTAL, TrackerPosition.LEFT_INDEX_DISTAL),

	@SerialName("leftMiddleProximal")
	LEFT_MIDDLE_PROXIMAL("LeftMiddleProximal", BoneType.LEFT_MIDDLE_PROXIMAL, TrackerPosition.LEFT_MIDDLE_PROXIMAL),

	@SerialName("leftMiddleIntermediate")
	LEFT_MIDDLE_INTERMEDIATE("LeftMiddleIntermediate", BoneType.LEFT_MIDDLE_INTERMEDIATE, TrackerPosition.LEFT_MIDDLE_INTERMEDIATE),

	@SerialName("leftMiddleDistal")
	LEFT_MIDDLE_DISTAL("LeftMiddleDistal", BoneType.LEFT_MIDDLE_DISTAL, TrackerPosition.LEFT_MIDDLE_DISTAL),

	@SerialName("leftRingProximal")
	LEFT_RING_PROXIMAL("LeftRingProximal", BoneType.LEFT_RING_PROXIMAL, TrackerPosition.LEFT_RING_PROXIMAL),

	@SerialName("leftRingIntermediate")
	LEFT_RING_INTERMEDIATE("LeftRingIntermediate", BoneType.LEFT_RING_INTERMEDIATE, TrackerPosition.LEFT_RING_INTERMEDIATE),

	@SerialName("leftRingDistal")
	LEFT_RING_DISTAL("LeftRingDistal", BoneType.LEFT_RING_DISTAL, TrackerPosition.LEFT_RING_DISTAL),

	@SerialName("leftLittleProximal")
	LEFT_LITTLE_PROXIMAL("LeftLittleProximal", BoneType.LEFT_LITTLE_PROXIMAL, TrackerPosition.LEFT_LITTLE_PROXIMAL),

	@SerialName("leftLittleIntermediate")
	LEFT_LITTLE_INTERMEDIATE("LeftLittleIntermediate", BoneType.LEFT_LITTLE_INTERMEDIATE, TrackerPosition.LEFT_LITTLE_INTERMEDIATE),

	@SerialName("leftLittleDistal")
	LEFT_LITTLE_DISTAL("LeftLittleDistal", BoneType.LEFT_LITTLE_DISTAL, TrackerPosition.LEFT_LITTLE_DISTAL),

	@SerialName("rightThumbMetacarpal")
	RIGHT_THUMB_PROXIMAL("RightThumbProximal", BoneType.RIGHT_THUMB_METACARPAL, TrackerPosition.RIGHT_THUMB_METACARPAL),

	@SerialName("rightThumbProximal")
	RIGHT_THUMB_INTERMEDIATE("RightThumbIntermediate", BoneType.RIGHT_THUMB_PROXIMAL, TrackerPosition.RIGHT_THUMB_PROXIMAL),

	@SerialName("rightThumbDistal")
	RIGHT_THUMB_DISTAL("RightThumbDistal", BoneType.RIGHT_THUMB_DISTAL, TrackerPosition.RIGHT_THUMB_DISTAL),

	@SerialName("rightIndexProximal")
	RIGHT_INDEX_PROXIMAL("RightIndexProximal", BoneType.RIGHT_INDEX_PROXIMAL, TrackerPosition.RIGHT_INDEX_PROXIMAL),

	@SerialName("rightIndexIntermediate")
	RIGHT_INDEX_INTERMEDIATE("RightIndexIntermediate", BoneType.RIGHT_INDEX_INTERMEDIATE, TrackerPosition.RIGHT_INDEX_INTERMEDIATE),

	@SerialName("rightIndexDistal")
	RIGHT_INDEX_DISTAL("RightIndexDistal", BoneType.RIGHT_INDEX_DISTAL, TrackerPosition.RIGHT_INDEX_DISTAL),

	@SerialName("rightMiddleProximal")
	RIGHT_MIDDLE_PROXIMAL("RightMiddleProximal", BoneType.RIGHT_MIDDLE_PROXIMAL, TrackerPosition.RIGHT_MIDDLE_PROXIMAL),

	@SerialName("rightMiddleIntermediate")
	RIGHT_MIDDLE_INTERMEDIATE("RightMiddleIntermediate", BoneType.RIGHT_MIDDLE_INTERMEDIATE, TrackerPosition.RIGHT_MIDDLE_INTERMEDIATE),

	@SerialName("rightMiddleDistal")
	RIGHT_MIDDLE_DISTAL("RightMiddleDistal", BoneType.RIGHT_MIDDLE_DISTAL, TrackerPosition.RIGHT_MIDDLE_DISTAL),

	@SerialName("rightRingProximal")
	RIGHT_RING_PROXIMAL("RightRingProximal", BoneType.RIGHT_RING_PROXIMAL, TrackerPosition.RIGHT_RING_PROXIMAL),

	@SerialName("rightRingIntermediate")
	RIGHT_RING_INTERMEDIATE("RightRingIntermediate", BoneType.RIGHT_RING_INTERMEDIATE, TrackerPosition.RIGHT_RING_INTERMEDIATE),

	@SerialName("rightRingDistal")
	RIGHT_RING_DISTAL("RightRingDistal", BoneType.RIGHT_RING_DISTAL, TrackerPosition.RIGHT_RING_DISTAL),

	@SerialName("rightLittleProximal")
	RIGHT_LITTLE_PROXIMAL("RightLittleProximal", BoneType.RIGHT_LITTLE_PROXIMAL, TrackerPosition.RIGHT_LITTLE_PROXIMAL),

	@SerialName("rightLittleIntermediate")
	RIGHT_LITTLE_INTERMEDIATE("RightLittleIntermediate", BoneType.RIGHT_LITTLE_INTERMEDIATE, TrackerPosition.RIGHT_LITTLE_INTERMEDIATE),

	@SerialName("rightLittleDistal")
	RIGHT_LITTLE_DISTAL("RightLittleDistal", BoneType.RIGHT_LITTLE_DISTAL, TrackerPosition.RIGHT_LITTLE_DISTAL),

	LAST_BONE("LastBone", null, null),
	;

	companion object {
		private val byStringVal: Map<String, UnityBone> = values().associateBy { it.stringVal.lowercase() }

		@JvmStatic
		fun getByStringVal(stringVal: String): UnityBone? = byStringVal[stringVal.lowercase()]

		/**
		 * Returns the bone on the opposite limb, or the original bone if
		 * it not a limb bone.
		 */
		fun tryGetOppositeArmBone(bone: UnityBone): UnityBone = when (bone) {
			LEFT_SHOULDER -> RIGHT_SHOULDER
			LEFT_UPPER_ARM -> RIGHT_UPPER_ARM
			LEFT_LOWER_ARM -> RIGHT_LOWER_ARM
			LEFT_HAND -> RIGHT_HAND
			RIGHT_SHOULDER -> LEFT_SHOULDER
			RIGHT_UPPER_ARM -> LEFT_UPPER_ARM
			RIGHT_LOWER_ARM -> LEFT_LOWER_ARM
			RIGHT_HAND -> LEFT_HAND
			LEFT_UPPER_LEG -> RIGHT_UPPER_LEG
			LEFT_LOWER_LEG -> RIGHT_LOWER_LEG
			LEFT_FOOT -> RIGHT_FOOT
			RIGHT_UPPER_LEG -> LEFT_UPPER_LEG
			RIGHT_LOWER_LEG -> LEFT_LOWER_LEG
			RIGHT_FOOT -> LEFT_FOOT
			LEFT_THUMB_PROXIMAL -> RIGHT_THUMB_PROXIMAL
			LEFT_THUMB_INTERMEDIATE -> RIGHT_THUMB_INTERMEDIATE
			LEFT_THUMB_DISTAL -> RIGHT_THUMB_DISTAL
			LEFT_INDEX_PROXIMAL -> RIGHT_INDEX_PROXIMAL
			LEFT_INDEX_INTERMEDIATE -> RIGHT_INDEX_INTERMEDIATE
			LEFT_INDEX_DISTAL -> RIGHT_INDEX_DISTAL
			LEFT_MIDDLE_PROXIMAL -> RIGHT_MIDDLE_PROXIMAL
			LEFT_MIDDLE_INTERMEDIATE -> RIGHT_MIDDLE_INTERMEDIATE
			LEFT_MIDDLE_DISTAL -> RIGHT_MIDDLE_DISTAL
			LEFT_RING_PROXIMAL -> RIGHT_RING_PROXIMAL
			LEFT_RING_INTERMEDIATE -> RIGHT_RING_INTERMEDIATE
			LEFT_RING_DISTAL -> RIGHT_RING_DISTAL
			LEFT_LITTLE_PROXIMAL -> RIGHT_LITTLE_PROXIMAL
			LEFT_LITTLE_INTERMEDIATE -> RIGHT_LITTLE_INTERMEDIATE
			LEFT_LITTLE_DISTAL -> RIGHT_LITTLE_DISTAL
			RIGHT_THUMB_PROXIMAL -> LEFT_THUMB_PROXIMAL
			RIGHT_THUMB_INTERMEDIATE -> LEFT_THUMB_INTERMEDIATE
			RIGHT_THUMB_DISTAL -> LEFT_THUMB_DISTAL
			RIGHT_INDEX_PROXIMAL -> LEFT_INDEX_PROXIMAL
			RIGHT_INDEX_INTERMEDIATE -> LEFT_INDEX_INTERMEDIATE
			RIGHT_INDEX_DISTAL -> LEFT_INDEX_DISTAL
			RIGHT_MIDDLE_PROXIMAL -> LEFT_MIDDLE_PROXIMAL
			RIGHT_MIDDLE_INTERMEDIATE -> LEFT_MIDDLE_INTERMEDIATE
			RIGHT_MIDDLE_DISTAL -> LEFT_MIDDLE_DISTAL
			RIGHT_RING_PROXIMAL -> LEFT_RING_PROXIMAL
			RIGHT_RING_INTERMEDIATE -> LEFT_RING_INTERMEDIATE
			RIGHT_RING_DISTAL -> LEFT_RING_DISTAL
			RIGHT_LITTLE_PROXIMAL -> LEFT_LITTLE_PROXIMAL
			RIGHT_LITTLE_INTERMEDIATE -> LEFT_LITTLE_INTERMEDIATE
			RIGHT_LITTLE_DISTAL -> LEFT_LITTLE_DISTAL
			else -> bone
		}

		/**
		 * Returns true if the bone is part of the left arm (incl. fingers, excl. shoulder)
		 */
		fun isLeftArmBone(bone: UnityBone): Boolean = bone == LEFT_UPPER_ARM ||
			bone == LEFT_LOWER_ARM ||
			bone == LEFT_HAND ||
			bone == LEFT_THUMB_PROXIMAL ||
			bone == LEFT_THUMB_INTERMEDIATE ||
			bone == LEFT_THUMB_DISTAL ||
			bone == LEFT_INDEX_PROXIMAL ||
			bone == LEFT_INDEX_INTERMEDIATE ||
			bone == LEFT_INDEX_DISTAL ||
			bone == LEFT_MIDDLE_PROXIMAL ||
			bone == LEFT_MIDDLE_INTERMEDIATE ||
			bone == LEFT_MIDDLE_DISTAL ||
			bone == LEFT_RING_PROXIMAL ||
			bone == LEFT_RING_INTERMEDIATE ||
			bone == LEFT_RING_DISTAL ||
			bone == LEFT_LITTLE_PROXIMAL ||
			bone == LEFT_LITTLE_INTERMEDIATE ||
			bone == LEFT_LITTLE_DISTAL

		/**
		 * Returns true if the bone is part of the right arm (incl. fingers, excl. shoulder)
		 */
		fun isRightArmBone(bone: UnityBone): Boolean = bone == RIGHT_UPPER_ARM ||
			bone == RIGHT_LOWER_ARM ||
			bone == RIGHT_HAND ||
			bone == RIGHT_THUMB_PROXIMAL ||
			bone == RIGHT_THUMB_INTERMEDIATE ||
			bone == RIGHT_THUMB_DISTAL ||
			bone == RIGHT_INDEX_PROXIMAL ||
			bone == RIGHT_INDEX_INTERMEDIATE ||
			bone == RIGHT_INDEX_DISTAL ||
			bone == RIGHT_MIDDLE_PROXIMAL ||
			bone == RIGHT_MIDDLE_INTERMEDIATE ||
			bone == RIGHT_MIDDLE_DISTAL ||
			bone == RIGHT_RING_PROXIMAL ||
			bone == RIGHT_RING_INTERMEDIATE ||
			bone == RIGHT_RING_DISTAL ||
			bone == RIGHT_LITTLE_PROXIMAL ||
			bone == RIGHT_LITTLE_INTERMEDIATE ||
			bone == RIGHT_LITTLE_DISTAL

		/**
		 * Returns true if the bone is the left upper arm or proximal left finger bone
		 */
		fun isLeftStartOfArmOrFingerBone(bone: UnityBone): Boolean = bone == LEFT_UPPER_ARM ||
			bone == LEFT_THUMB_PROXIMAL ||
			bone == LEFT_INDEX_PROXIMAL ||
			bone == LEFT_MIDDLE_PROXIMAL ||
			bone == LEFT_RING_PROXIMAL ||
			bone == LEFT_LITTLE_PROXIMAL

		/**
		 * Returns true if the bone is the right upper arm or proximal right finger bone
		 */
		fun isRightStartOfArmOrFingerBone(bone: UnityBone): Boolean = bone == RIGHT_UPPER_ARM ||
			bone == RIGHT_THUMB_PROXIMAL ||
			bone == RIGHT_INDEX_PROXIMAL ||
			bone == RIGHT_MIDDLE_PROXIMAL ||
			bone == RIGHT_RING_PROXIMAL ||
			bone == RIGHT_LITTLE_PROXIMAL

		/**
		 * Returns true if the bone is part of the left fingers
		 */
		fun isLeftFingerBone(bone: UnityBone): Boolean = bone == LEFT_THUMB_PROXIMAL ||
			bone == LEFT_THUMB_INTERMEDIATE ||
			bone == LEFT_THUMB_DISTAL ||
			bone == LEFT_INDEX_PROXIMAL ||
			bone == LEFT_INDEX_INTERMEDIATE ||
			bone == LEFT_INDEX_DISTAL ||
			bone == LEFT_MIDDLE_PROXIMAL ||
			bone == LEFT_MIDDLE_INTERMEDIATE ||
			bone == LEFT_MIDDLE_DISTAL ||
			bone == LEFT_RING_PROXIMAL ||
			bone == LEFT_RING_INTERMEDIATE ||
			bone == LEFT_RING_DISTAL ||
			bone == LEFT_LITTLE_PROXIMAL ||
			bone == LEFT_LITTLE_INTERMEDIATE ||
			bone == LEFT_LITTLE_DISTAL

		/**
		 * Returns true if the bone part of the right fingers
		 */
		fun isRightFingerBone(bone: UnityBone): Boolean = bone == RIGHT_THUMB_PROXIMAL ||
			bone == RIGHT_THUMB_INTERMEDIATE ||
			bone == RIGHT_THUMB_DISTAL ||
			bone == RIGHT_INDEX_PROXIMAL ||
			bone == RIGHT_INDEX_INTERMEDIATE ||
			bone == RIGHT_INDEX_DISTAL ||
			bone == RIGHT_MIDDLE_PROXIMAL ||
			bone == RIGHT_MIDDLE_INTERMEDIATE ||
			bone == RIGHT_MIDDLE_DISTAL ||
			bone == RIGHT_RING_PROXIMAL ||
			bone == RIGHT_RING_INTERMEDIATE ||
			bone == RIGHT_RING_DISTAL ||
			bone == RIGHT_LITTLE_PROXIMAL ||
			bone == RIGHT_LITTLE_INTERMEDIATE ||
			bone == RIGHT_LITTLE_DISTAL
	}
}
