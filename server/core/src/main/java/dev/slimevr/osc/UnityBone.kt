package dev.slimevr.osc

import dev.slimevr.tracking.processor.BoneType
import dev.slimevr.tracking.trackers.TrackerPosition

/**
 * Unity HumanBodyBones from:
 * https://docs.unity3d.com/ScriptReference/HumanBodyBones.html
 */
enum class UnityBone(
	val stringVal: String,
	val boneType: BoneType?,
	val trackerPosition: TrackerPosition?,
) {

	HIPS("Hips", BoneType.HIP, TrackerPosition.HIP),
	LEFT_UPPER_LEG("LeftUpperLeg", BoneType.LEFT_UPPER_LEG, TrackerPosition.LEFT_UPPER_LEG),
	RIGHT_UPPER_LEG("RightUpperLeg", BoneType.RIGHT_UPPER_LEG, TrackerPosition.RIGHT_UPPER_LEG),
	LEFT_LOWER_LEG("LeftLowerLeg", BoneType.LEFT_LOWER_LEG, TrackerPosition.LEFT_LOWER_LEG),
	RIGHT_LOWER_LEG("RightLowerLeg", BoneType.RIGHT_LOWER_LEG, TrackerPosition.RIGHT_LOWER_LEG),
	LEFT_FOOT("LeftFoot", BoneType.LEFT_FOOT, TrackerPosition.LEFT_FOOT),
	RIGHT_FOOT("RightFoot", BoneType.RIGHT_FOOT, TrackerPosition.RIGHT_FOOT),
	SPINE("Spine", BoneType.WAIST, TrackerPosition.WAIST),
	CHEST("Chest", BoneType.CHEST, TrackerPosition.CHEST),
	UPPER_CHEST("UpperChest", BoneType.CHEST, TrackerPosition.CHEST),
	NECK("Neck", BoneType.NECK, TrackerPosition.NECK),
	HEAD("Head", BoneType.HEAD, TrackerPosition.HEAD),
	LEFT_SHOULDER("LeftShoulder", BoneType.LEFT_SHOULDER, TrackerPosition.LEFT_SHOULDER),
	RIGHT_SHOULDER("RightShoulder", BoneType.RIGHT_SHOULDER, TrackerPosition.RIGHT_SHOULDER),
	LEFT_UPPER_ARM("LeftUpperArm", BoneType.LEFT_UPPER_ARM, TrackerPosition.LEFT_UPPER_ARM),
	RIGHT_UPPER_ARM("RightUpperArm", BoneType.RIGHT_UPPER_ARM, TrackerPosition.RIGHT_UPPER_ARM),
	LEFT_LOWER_ARM("LeftLowerArm", BoneType.LEFT_LOWER_ARM, TrackerPosition.LEFT_LOWER_ARM),
	RIGHT_LOWER_ARM("RightLowerArm", BoneType.RIGHT_LOWER_ARM, TrackerPosition.RIGHT_LOWER_ARM),
	LEFT_HAND("LeftHand", BoneType.LEFT_HAND, TrackerPosition.LEFT_HAND),
	RIGHT_HAND("RightHand", BoneType.RIGHT_HAND, TrackerPosition.RIGHT_HAND),
	LEFT_TOES("LeftToes", null, null),
	RIGHT_TOES("RightToes", null, null),
	LEFT_EYE("LeftEye", null, null),
	RIGHT_EYE("RightEye", null, null),
	JAW("Jaw", null, null),
	LEFT_THUMB_PROXIMAL("LeftThumbProximal", null, TrackerPosition.LEFT_THUMB_PROXIMAL),
	LEFT_THUMB_INTERMEDIATE("LeftThumbIntermediate", null, TrackerPosition.LEFT_THUMB_INTERMEDIATE),
	LEFT_THUMB_DISTAL("LeftThumbDistal", null, TrackerPosition.LEFT_THUMB_DISTAL),
	LEFT_INDEX_PROXIMAL("LeftIndexProximal", null, TrackerPosition.LEFT_INDEX_PROXIMAL),
	LEFT_INDEX_INTERMEDIATE("LeftIndexIntermediate", null, TrackerPosition.LEFT_INDEX_INTERMEDIATE),
	LEFT_INDEX_DISTAL("LeftIndexDistal", null, TrackerPosition.LEFT_INDEX_DISTAL),
	LEFT_MIDDLE_PROXIMAL("LeftMiddleProximal", null, TrackerPosition.LEFT_MIDDLE_PROXIMAL),
	LEFT_MIDDLE_INTERMEDIATE("LeftMiddleIntermediate", null, TrackerPosition.LEFT_MIDDLE_INTERMEDIATE),
	LEFT_MIDDLE_DISTAL("LeftMiddleDistal", null, TrackerPosition.LEFT_MIDDLE_DISTAL),
	LEFT_RING_PROXIMAL("LeftRingProximal", null, TrackerPosition.LEFT_RING_PROXIMAL),
	LEFT_RING_INTERMEDIATE("LeftRingIntermediate", null, TrackerPosition.LEFT_RING_INTERMEDIATE),
	LEFT_RING_DISTAL("LeftRingDistal", null, TrackerPosition.LEFT_RING_DISTAL),
	LEFT_LITTLE_PROXIMAL("LeftLittleProximal", null, TrackerPosition.LEFT_LITTLE_PROXIMAL),
	LEFT_LITTLE_INTERMEDIATE("LeftLittleIntermediate", null, TrackerPosition.LEFT_LITTLE_INTERMEDIATE),
	LEFT_LITTLE_DISTAL("LeftLittleDistal", null, TrackerPosition.LEFT_LITTLE_DISTAL),
	RIGHT_THUMB_PROXIMAL("RightThumbProximal", null, TrackerPosition.RIGHT_THUMB_PROXIMAL),
	RIGHT_THUMB_INTERMEDIATE("RightThumbIntermediate", null, TrackerPosition.RIGHT_THUMB_INTERMEDIATE),
	RIGHT_THUMB_DISTAL("RightThumbDistal", null, TrackerPosition.RIGHT_THUMB_DISTAL),
	RIGHT_INDEX_PROXIMAL("RightIndexProximal", null, TrackerPosition.RIGHT_INDEX_PROXIMAL),
	RIGHT_INDEX_INTERMEDIATE("RightIndexIntermediate", null, TrackerPosition.RIGHT_INDEX_INTERMEDIATE),
	RIGHT_INDEX_DISTAL("RightIndexDistal", null, TrackerPosition.RIGHT_INDEX_DISTAL),
	RIGHT_MIDDLE_PROXIMAL("RightMiddleProximal", null, TrackerPosition.RIGHT_MIDDLE_PROXIMAL),
	RIGHT_MIDDLE_INTERMEDIATE("RightMiddleIntermediate", null, TrackerPosition.RIGHT_MIDDLE_INTERMEDIATE),
	RIGHT_MIDDLE_DISTAL("RightMiddleDistal", null, TrackerPosition.RIGHT_MIDDLE_DISTAL),
	RIGHT_RING_PROXIMAL("RightRingProximal", null, TrackerPosition.RIGHT_RING_PROXIMAL),
	RIGHT_RING_INTERMEDIATE("RightRingIntermediate", null, TrackerPosition.RIGHT_RING_INTERMEDIATE),
	RIGHT_RING_DISTAL("RightRingDistal", null, TrackerPosition.RIGHT_RING_DISTAL),
	RIGHT_LITTLE_PROXIMAL("RightLittleProximal", null, TrackerPosition.RIGHT_LITTLE_PROXIMAL),
	RIGHT_LITTLE_INTERMEDIATE("RightLittleIntermediate", null, TrackerPosition.RIGHT_LITTLE_INTERMEDIATE),
	RIGHT_LITTLE_DISTAL("RightLittleDistal", null, TrackerPosition.RIGHT_LITTLE_DISTAL),
	LAST_BONE("LastBone", null, null),
	;

	companion object {
		private val byStringVal: Map<String, UnityBone> = values().associateBy { it.stringVal.lowercase() }

		@JvmStatic
		fun getByStringVal(stringVal: String): UnityBone? = byStringVal[stringVal.lowercase()]
	}
}
