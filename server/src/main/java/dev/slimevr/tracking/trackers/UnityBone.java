package dev.slimevr.tracking.trackers;

import dev.slimevr.tracking.processor.BoneType;

import java.util.HashMap;
import java.util.Map;


/**
 * Unity HumanBodyBones from:
 * https://docs.unity3d.com/ScriptReference/HumanBodyBones.html
 */
public enum UnityBone {
	HIPS("Hips", BoneType.HIP),
	LEFT_UPPER_LEG("LeftUpperLeg", BoneType.LEFT_UPPER_LEG),
	RIGHT_UPPER_LEG("RightUpperLeg", BoneType.RIGHT_UPPER_LEG),
	LEFT_LOWER_LEG("LeftLowerLeg", BoneType.LEFT_LOWER_LEG),
	RIGHT_LOWER_LEG("RightLowerLeg", BoneType.RIGHT_LOWER_LEG),
	LEFT_FOOT("LeftFoot", BoneType.LEFT_FOOT),
	RIGHT_FOOT("RightFoot", BoneType.RIGHT_FOOT),
	SPINE("Spine", BoneType.WAIST),
	CHEST("Chest", BoneType.CHEST),
	UPPER_CHEST("UpperChest", null),
	NECK("Neck", BoneType.NECK),
	HEAD("Head", BoneType.HEAD),
	LEFT_SHOULDER("LeftShoulder", BoneType.LEFT_SHOULDER),
	RIGHT_SHOULDER("RightShoulder", BoneType.RIGHT_SHOULDER),
	LEFT_UPPER_ARM("LeftUpperArm", BoneType.LEFT_UPPER_ARM),
	RIGHT_UPPER_ARM("RightUpperArm", BoneType.RIGHT_UPPER_ARM),
	LEFT_LOWER_ARM("LeftLowerArm", BoneType.LEFT_LOWER_ARM),
	RIGHT_LOWER_ARM("RightLowerArm", BoneType.RIGHT_LOWER_ARM),
	LEFT_HAND("LeftHand", BoneType.LEFT_HAND),
	RIGHT_HAND("RightHand", BoneType.RIGHT_HAND),
	LEFT_TOES("LeftToes", null),
	RIGHT_TOES("RightToes", null),
	LEFT_EYE("LeftEye", null),
	RIGHT_EYE("RightEye", null),
	JAW("Jaw", null),
	LEFT_THUMB_PROXIMAL("LeftThumbProximal", null),
	LEFT_THUMB_INTERMEDIATE("LeftThumbIntermediate", null),
	LEFT_THUMB_DISTAL("LeftThumbDistal", null),
	LEFT_INDEX_PROXIMAL("LeftIndexProximal", null),
	LEFT_INDEX_INTERMEDIATE("LeftIndexIntermediate", null),
	LEFT_INDEX_DISTAL("LeftIndexDistal", null),
	LEFT_MIDDLE_PROXIMAL("LeftMiddleProximal", null),
	LEFT_MIDDLE_INTERMEDIATE("LeftMiddleIntermediate", null),
	LEFT_MIDDLE_DISTAL("LeftMiddleDistal", null),
	LEFT_RING_PROXIMAL("LeftRingProximal", null),
	LEFT_RING_INTERMEDIATE("LeftRingIntermediate", null),
	LEFT_RING_DISTAL("LeftRingDistal", null),
	LEFT_LITTLE_PROXIMAL("LeftLittleProximal", null),
	LEFT_LITTLE_INTERMEDIATE("LeftLittleIntermediate", null),
	LEFT_LITTLE_DISTAL("LeftLittleDistal", null),
	RIGHT_THUMB_PROXIMAL("RightThumbProximal", null),
	RIGHT_THUMB_INTERMEDIATE("RightThumbIntermediate", null),
	RIGHT_THUMB_DISTAL("RightThumbDistal", null),
	RIGHT_INDEX_PROXIMAL("RightIndexProximal", null),
	RIGHT_INDEX_INTERMEDIATE("RightIndexIntermediate", null),
	RIGHT_INDEX_DISTAL("RightIndexDistal", null),
	RIGHT_MIDDLE_PROXIMAL("RightMiddleProximal", null),
	RIGHT_MIDDLE_INTERMEDIATE("RightMiddleIntermediate", null),
	RIGHT_MIDDLE_DISTAL("RightMiddleDistal", null),
	RIGHT_RING_PROXIMAL("RightRingProximal", null),
	RIGHT_RING_INTERMEDIATE("RightRingIntermediate", null),
	RIGHT_RING_DISTAL("RightRingDistal", null),
	RIGHT_LITTLE_PROXIMAL("RightLittleProximal", null),
	RIGHT_LITTLE_INTERMEDIATE("RightLittleIntermediate", null),
	RIGHT_LITTLE_DISTAL("RightLittleDistal", null),
	LAST_BONE("LastBone", null);


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

	UnityBone(String stringVal, BoneType boneType) {
		this.stringVal = stringVal;
		this.boneType = boneType;
	}

	public static UnityBone getByStringVal(String stringVal) {
		return stringVal == null ? null : byStringVal.get(stringVal.toLowerCase());
	}

	public static UnityBone getByBoneType(BoneType boneType) {
		return boneType == null ? null : byBoneType.get(boneType);
	}
}
