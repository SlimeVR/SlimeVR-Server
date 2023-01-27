package dev.slimevr.tracking.processor;

import solarxr_protocol.datatypes.BodyPart;


/**
 * Keys for all bones in the skeleton.
 */
public enum BoneType {
	HMD(),
	HEAD(BodyPart.HEAD),
	HEAD_TRACKER(),
	NECK(BodyPart.NECK),
	CHEST(BodyPart.CHEST),
	CHEST_TRACKER,
	WAIST(BodyPart.WAIST),
	HIP(BodyPart.HIP),
	HIP_TRACKER,
	LEFT_HIP,
	RIGHT_HIP,
	LEFT_UPPER_LEG(BodyPart.LEFT_UPPER_LEG),
	RIGHT_UPPER_LEG(BodyPart.RIGHT_UPPER_LEG),
	LEFT_KNEE_TRACKER,
	RIGHT_KNEE_TRACKER,
	LEFT_LOWER_LEG(BodyPart.LEFT_LOWER_LEG),
	RIGHT_LOWER_LEG(BodyPart.RIGHT_LOWER_LEG),
	LEFT_FOOT(BodyPart.LEFT_FOOT),
	RIGHT_FOOT(BodyPart.RIGHT_FOOT),
	LEFT_FOOT_TRACKER(BodyPart.LEFT_FOOT),
	RIGHT_FOOT_TRACKER(BodyPart.RIGHT_FOOT),
	LEFT_CONTROLLER(BodyPart.LEFT_CONTROLLER),
	RIGHT_CONTROLLER(BodyPart.RIGHT_CONTROLLER),
	LEFT_LOWER_ARM(BodyPart.LEFT_LOWER_ARM),
	RIGHT_LOWER_ARM(BodyPart.RIGHT_LOWER_ARM),
	LEFT_ELBOW_TRACKER,
	RIGHT_ELBOW_TRACKER,
	UPPER_ARM,
	LEFT_UPPER_ARM(BodyPart.LEFT_UPPER_ARM),
	RIGHT_UPPER_ARM(BodyPart.RIGHT_UPPER_ARM),
	LEFT_SHOULDER(BodyPart.LEFT_SHOULDER),
	RIGHT_SHOULDER(BodyPart.RIGHT_SHOULDER),
	LEFT_HAND(BodyPart.LEFT_HAND),
	RIGHT_HAND(BodyPart.RIGHT_HAND),
	LEFT_HAND_TRACKER,
	RIGHT_HAND_TRACKER;

	public static final BoneType[] values = values();

	public final int bodyPart;

	BoneType() {
		this.bodyPart = BodyPart.NONE;
	}

	BoneType(int associatedBodyPart) {
		this.bodyPart = associatedBodyPart;
	}
}
