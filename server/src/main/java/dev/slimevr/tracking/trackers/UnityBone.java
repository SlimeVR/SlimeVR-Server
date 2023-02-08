package dev.slimevr.tracking.trackers;

import dev.slimevr.tracking.processor.BoneType;

import java.util.HashMap;
import java.util.Map;


/**
 * Unity HumanBodyBones from:
 * https://docs.unity3d.com/ScriptReference/HumanBodyBones.html
 */
public enum UnityBone {
	HIPS("Hips", BoneType.HIP, TrackerPosition.HIP),
	LEFT_UPPER_LEG("LeftUpperLeg", BoneType.LEFT_UPPER_LEG, TrackerPosition.LEFT_UPPER_LEG),
	RIGHT_UPPER_LEG("RightUpperLeg", BoneType.RIGHT_UPPER_LEG, TrackerPosition.RIGHT_UPPER_LEG),
	LEFT_LOWER_LEG("LeftLowerLeg", BoneType.LEFT_LOWER_LEG, TrackerPosition.LEFT_LOWER_LEG),
	RIGHT_LOWER_LEG("RightLowerLeg", BoneType.RIGHT_LOWER_LEG, TrackerPosition.RIGHT_LOWER_LEG),
	LEFT_FOOT("LeftFoot", BoneType.LEFT_FOOT, TrackerPosition.LEFT_FOOT),
	RIGHT_FOOT("RightFoot", BoneType.RIGHT_FOOT, TrackerPosition.RIGHT_FOOT),
	SPINE("Spine", BoneType.WAIST, TrackerPosition.WAIST),
	CHEST("Chest", BoneType.CHEST, TrackerPosition.CHEST),
	UPPER_CHEST("UpperChest", null, null),
	NECK("Neck", BoneType.NECK, TrackerPosition.NECK),
	HEAD("Head", BoneType.HEAD, TrackerPosition.HMD),
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
	LEFT_THUMB_PROXIMAL("LeftThumbProximal", null, null),
	LEFT_THUMB_INTERMEDIATE("LeftThumbIntermediate", null, null),
	LEFT_THUMB_DISTAL("LeftThumbDistal", null, null),
	LEFT_INDEX_PROXIMAL("LeftIndexProximal", null, null),
	LEFT_INDEX_INTERMEDIATE("LeftIndexIntermediate", null, null),
	LEFT_INDEX_DISTAL("LeftIndexDistal", null, null),
	LEFT_MIDDLE_PROXIMAL("LeftMiddleProximal", null, null),
	LEFT_MIDDLE_INTERMEDIATE("LeftMiddleIntermediate", null, null),
	LEFT_MIDDLE_DISTAL("LeftMiddleDistal", null, null),
	LEFT_RING_PROXIMAL("LeftRingProximal", null, null),
	LEFT_RING_INTERMEDIATE("LeftRingIntermediate", null, null),
	LEFT_RING_DISTAL("LeftRingDistal", null, null),
	LEFT_LITTLE_PROXIMAL("LeftLittleProximal", null, null),
	LEFT_LITTLE_INTERMEDIATE("LeftLittleIntermediate", null, null),
	LEFT_LITTLE_DISTAL("LeftLittleDistal", null, null),
	RIGHT_THUMB_PROXIMAL("RightThumbProximal", null, null),
	RIGHT_THUMB_INTERMEDIATE("RightThumbIntermediate", null, null),
	RIGHT_THUMB_DISTAL("RightThumbDistal", null, null),
	RIGHT_INDEX_PROXIMAL("RightIndexProximal", null, null),
	RIGHT_INDEX_INTERMEDIATE("RightIndexIntermediate", null, null),
	RIGHT_INDEX_DISTAL("RightIndexDistal", null, null),
	RIGHT_MIDDLE_PROXIMAL("RightMiddleProximal", null, null),
	RIGHT_MIDDLE_INTERMEDIATE("RightMiddleIntermediate", null, null),
	RIGHT_MIDDLE_DISTAL("RightMiddleDistal", null, null),
	RIGHT_RING_PROXIMAL("RightRingProximal", null, null),
	RIGHT_RING_INTERMEDIATE("RightRingIntermediate", null, null),
	RIGHT_RING_DISTAL("RightRingDistal", null, null),
	RIGHT_LITTLE_PROXIMAL("RightLittleProximal", null, null),
	RIGHT_LITTLE_INTERMEDIATE("RightLittleIntermediate", null, null),
	RIGHT_LITTLE_DISTAL("RightLittleDistal", null, null),
	LAST_BONE("LastBone", null, null);


	private static final Map<String, UnityBone> byStringVal = new HashMap<>();
	private static final Map<BoneType, UnityBone> byBoneType = new HashMap<>();

	static {
		for (UnityBone configVal : values()) {
			byStringVal.put(configVal.stringVal.toLowerCase(), configVal);
		}
	}
	static {
		for (UnityBone configVal : values()) {
			byBoneType.put(configVal.boneType, configVal);
		}
	}

	public static final UnityBone[] values = values();
	public final String stringVal;
	public final BoneType boneType;
	public final TrackerPosition trackerPosition;

	UnityBone(String stringVal, BoneType boneType, TrackerPosition trackerPosition) {
		this.stringVal = stringVal;
		this.boneType = boneType;
		this.trackerPosition = trackerPosition;
	}

	public static UnityBone getByStringVal(String stringVal) {
		return stringVal == null ? null : byStringVal.get(stringVal.toLowerCase());
	}

	public static UnityBone getByBoneType(BoneType boneType) {
		return boneType == null ? null : byBoneType.get(boneType);
	}
}
