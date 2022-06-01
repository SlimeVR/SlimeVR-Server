package dev.slimevr.vr.processor.skeleton;

import solarxr_protocol.datatypes.BodyPart;


/**
 * Keys for all bones in the skeleton. TODO: Some bones are deprecated because
 * they are still used as SkeletonNodeOffset, and represent both left and right
 * offsets, but they should be split into two.
 */
public enum BoneType {

	HEAD(BodyPart.HMD),
	NECK(BodyPart.NECK),
	CHEST(BodyPart.CHEST),
	CHEST_TRACKER(BodyPart.CHEST),
	WAIST(BodyPart.WAIST),
	WAIST_TRACKER(BodyPart.WAIST),
	HIP(BodyPart.HIP),
	HIP_TRACKER(BodyPart.HIP),
	LEFT_HIP,
	RIGHT_HIP,
	@Deprecated
	UPPER_LEG,
	LEFT_UPPER_LEG(BodyPart.LEFT_UPPER_LEG),
	RIGHT_UPPER_LEG(BodyPart.RIGHT_UPPER_LEG),
	@Deprecated
	KNEE_TRACKER,
	LEFT_KNEE_TRACKER,
	RIGHT_KNEE_TRACKER,
	@Deprecated
	LOWER_LEG,
	LEFT_LOWER_LEG(BodyPart.LEFT_LOWER_LEG),
	RIGHT_LOWER_LEG(BodyPart.RIGHT_LOWER_LEG),
	@Deprecated
	FOOT,
	LEFT_FOOT(BodyPart.LEFT_FOOT),
	RIGHT_FOOT(BodyPart.RIGHT_FOOT),
	@Deprecated
	FOOT_TRACKER,
	LEFT_FOOT_TRACKER(BodyPart.LEFT_FOOT),
	RIGHT_FOOT_TRACKER(BodyPart.RIGHT_FOOT),
	@Deprecated
	CONTROLLER,
	LEFT_CONTROLLER(BodyPart.LEFT_CONTROLLER),
	RIGHT_CONTROLLER(BodyPart.RIGHT_CONTROLLER),
	@Deprecated
	LOWER_ARM,
	LEFT_LOWER_ARM(BodyPart.LEFT_LOWER_ARM),
	RIGHT_LOWER_ARM(BodyPart.RIGHT_LOWER_ARM),
	@Deprecated
	LOWER_ARM_HMD,
	@Deprecated
	ELBOW_TRACKER,
	LEFT_ELBOW_TRACKER,
	RIGHT_ELBOW_TRACKER,
	@Deprecated
	UPPER_ARM,
	LEFT_UPPER_ARM,
	RIGHT_UPPER_ARM,
	LEFT_SHOULDER,
	RIGHT_SHOULDER,
	@Deprecated
	HAND,
	LEFT_HAND,
	RIGHT_HAND,
	@Deprecated
	HAND_TRACKER,
	LEFT_HAND_TRACKER,
	RIGHT_HAND_TRACKER;

	public static final BoneType[] values = values();

	public final int bodyPart;

	private BoneType() {
		this.bodyPart = BodyPart.NONE;
	}

	private BoneType(int associatedBodyPart) {
		this.bodyPart = associatedBodyPart;
	}
}
