package dev.slimevr.vr.trackers;

import solarxr_protocol.datatypes.BodyPart;

import java.util.HashMap;
import java.util.Map;


/**
 * Unity HumanBodyBones from:
 * https://docs.unity3d.com/ScriptReference/HumanBodyBones.html
 */
public enum UnityBone {
	HIPS("Hips", BodyPart.HIP),
	LEFT_UPPER_LEG("LeftUpperLeg", BodyPart.LEFT_UPPER_LEG),
	RIGHT_UPPER_LEG("RightUpperLeg", BodyPart.RIGHT_UPPER_LEG),
	LEFT_LOWER_LEG("LeftLowerLeg", BodyPart.LEFT_LOWER_LEG),
	RIGHT_LOWER_LEG("RightLowerLeg", BodyPart.RIGHT_LOWER_LEG),
	LEFT_FOOT("LeftFoot", BodyPart.LEFT_FOOT),
	RIGHT_FOOT("RightFoot", BodyPart.RIGHT_FOOT),
	SPINE("Spine", BodyPart.WAIST),
	CHEST("Chest", BodyPart.CHEST),
	UPPER_CHEST("UpperChest", 0),
	NECK("Neck", BodyPart.NECK),
	HEAD("Head", BodyPart.HEAD),
	LEFT_SHOULDER("LeftShoulder", BodyPart.LEFT_SHOULDER),
	RIGHT_SHOULDER("RightShoulder", BodyPart.RIGHT_SHOULDER),
	LEFT_UPPER_ARM("LeftUpperArm", BodyPart.LEFT_UPPER_ARM),
	RIGHT_UPPER_ARM("RightUpperArm", BodyPart.RIGHT_UPPER_ARM),
	LEFT_LOWER_ARM("LeftLowerArm", BodyPart.LEFT_LOWER_ARM),
	RIGHT_LOWER_ARM("RightLowerArm", BodyPart.RIGHT_LOWER_ARM),
	LEFT_HAND("LeftHand", BodyPart.LEFT_HAND),
	RIGHT_HAND("RightHand", BodyPart.RIGHT_HAND),
	LEFT_TOES("LeftToes", 0),
	RIGHT_TOES("RightToes", 0),
	LEFT_EYE("LeftEye", 0),
	RIGHT_EYE("RightEye", 0),
	JAW("Jaw", 0),
	LEFT_THUMB_PROXIMAL("LeftThumbProximal", 0),
	LEFT_THUMB_INTERMEDIATE("LeftThumbIntermediate", 0),
	LEFT_THUMB_DISTAL("LeftThumbDistal", 0),
	LEFT_INDEX_PROXIMAL("LeftIndexProximal", 0),
	LEFT_INDEX_INTERMEDIATE("LeftIndexIntermediate", 0),
	LEFT_INDEX_DISTAL("LeftIndexDistal", 0),
	LEFT_MIDDLE_PROXIMAL("LeftMiddleProximal", 0),
	LEFT_MIDDLE_INTERMEDIATE("LeftMiddleIntermediate", 0),
	LEFT_MIDDLE_DISTAL("LeftMiddleDistal", 0),
	LEFT_RING_PROXIMAL("LeftRingProximal", 0),
	LEFT_RING_INTERMEDIATE("LeftRingIntermediate", 0),
	LEFT_RING_DISTAL("LeftRingDistal", 0),
	LEFT_LITTLE_PROXIMAL("LeftLittleProximal", 0),
	LEFT_LITTLE_INTERMEDIATE("LeftLittleIntermediate", 0),
	LEFT_LITTLE_DISTAL("LeftLittleDistal", 0),
	RIGHT_THUMB_PROXIMAL("RightThumbProximal", 0),
	RIGHT_THUMB_INTERMEDIATE("RightThumbIntermediate", 0),
	RIGHT_THUMB_DISTAL("RightThumbDistal", 0),
	RIGHT_INDEX_PROXIMAL("RightIndexProximal", 0),
	RIGHT_INDEX_INTERMEDIATE("RightIndexIntermediate", 0),
	RIGHT_INDEX_DISTAL("RightIndexDistal", 0),
	RIGHT_MIDDLE_PROXIMAL("RightMiddleProximal", 0),
	RIGHT_MIDDLE_INTERMEDIATE("RightMiddleIntermediate", 0),
	RIGHT_MIDDLE_DISTAL("RightMiddleDistal", 0),
	RIGHT_RING_PROXIMAL("RightRingProximal", 0),
	RIGHT_RING_INTERMEDIATE("RightRingIntermediate", 0),
	RIGHT_RING_DISTAL("RightRingDistal", 0),
	RIGHT_LITTLE_PROXIMAL("RightLittleProximal", 0),
	RIGHT_LITTLE_INTERMEDIATE("RightLittleIntermediate", 0),
	RIGHT_LITTLE_DISTAL("RightLittleDistal", 0),
	LAST_BONE("LastBone", 0);


	private static final Map<String, UnityBone> byStringVal = new HashMap<>();
	private static final Map<Integer, UnityBone> byBodyPart = new HashMap<>();

	static {
		for (UnityBone configVal : values()) {
			byStringVal.put(configVal.stringVal.toLowerCase(), configVal);
		}
	}
	static {
		for (UnityBone configVal : values()) {
			byBodyPart.put(configVal.bodyPart, configVal);
		}
	}

	public static final UnityBone[] values = values();
	public final String stringVal;
	public final int bodyPart;

	UnityBone(String stringVal, int bodyPart) {
		this.stringVal = stringVal;
		this.bodyPart = bodyPart;
	}

	public static UnityBone getByStringVal(String stringVal) {
		return stringVal == null ? null : byStringVal.get(stringVal.toLowerCase());
	}

	public static UnityBone getByBodyPart(int bone) {
		return bone == 0 ? null : byBodyPart.get(bone);
	}
}
