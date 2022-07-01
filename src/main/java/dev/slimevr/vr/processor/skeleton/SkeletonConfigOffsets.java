package dev.slimevr.vr.processor.skeleton;

import java.util.HashMap;
import java.util.Map;


public enum SkeletonConfigOffsets {
	HEAD(
		1,
		"Head",
		"headShift",
		"Head shift",
		0.1f,
		new BoneType[] { BoneType.HEAD }
	),
	NECK(
		2,
		"Neck",
		"neckLength",
		"Neck length",
		0.1f,
		new BoneType[] { BoneType.NECK }
	),
	TORSO(
		3,
		"Torso",
		"torsoLength",
		"Torso length",
		0.56f,
		new BoneType[] { BoneType.WAIST }
	),
	CHEST(
		4,
		"Chest",
		"chestDistance",
		"Chest distance",
		0.32f,
		new BoneType[] { BoneType.CHEST, BoneType.WAIST,
			BoneType.LEFT_SHOULDER, BoneType.RIGHT_SHOULDER }
	),
	WAIST(
		5,
		"Waist",
		"waistDistance",
		"Waist distance",
		0.04f,
		new BoneType[] { BoneType.WAIST, BoneType.HIP }
	),
	HIP_OFFSET(
		6,
		"Hip offset",
		"hipOffset",
		"Hip offset",
		0.0f,
		new BoneType[] { BoneType.HIP_TRACKER }
	),
	HIPS_WIDTH(
		7,
		"Hips width",
		"hipsWidth",
		"Hips width",
		0.26f,
		new BoneType[] { BoneType.LEFT_HIP, BoneType.RIGHT_HIP }
	),
	LEGS_LENGTH(
		8,
		"Legs length",
		"legsLength",
		"Legs length",
		0.92f,
		new BoneType[] { BoneType.LEFT_UPPER_LEG, BoneType.RIGHT_UPPER_LEG }
	),
	KNEE_HEIGHT(
		9,
		"Knee height",
		"kneeHeight",
		"Knee height",
		0.50f,
		new BoneType[] { BoneType.LEFT_UPPER_LEG, BoneType.RIGHT_UPPER_LEG,
			BoneType.LEFT_LOWER_LEG, BoneType.RIGHT_LOWER_LEG }
	),
	FOOT_LENGTH(
		10,
		"Foot length",
		"footLength",
		"Foot length",
		0.05f,
		new BoneType[] { BoneType.LEFT_FOOT, BoneType.RIGHT_FOOT }
	),
	FOOT_SHIFT(
		11,
		"Foot shift",
		"footShift",
		"Foot shift",
		-0.05f,
		new BoneType[] { BoneType.LEFT_LOWER_LEG, BoneType.RIGHT_LOWER_LEG }
	),
	SKELETON_OFFSET(
		12,
		"Skeleton offset",
		"skeletonOffset",
		"Skeleton offset",
		0.0f,
		new BoneType[] { BoneType.CHEST_TRACKER, BoneType.HIP_TRACKER,
			BoneType.LEFT_KNEE_TRACKER, BoneType.RIGHT_KNEE_TRACKER,
			BoneType.LEFT_FOOT_TRACKER, BoneType.RIGHT_KNEE_TRACKER }
	),
	CONTROLLER_DISTANCE_Z(
		13,
		"Controller distance z",
		"controllerDistanceZ",
		"Controller distance z",
		0.13f,
		new BoneType[] { BoneType.LEFT_CONTROLLER, BoneType.RIGHT_CONTROLLER,
			BoneType.LEFT_HAND, BoneType.RIGHT_HAND }
	),
	CONTROLLER_DISTANCE_Y(
		14,
		"Controller distance y",
		"controllerDistanceY",
		"Controller distance y",
		0.035f,
		new BoneType[] { BoneType.LEFT_CONTROLLER, BoneType.RIGHT_CONTROLLER,
			BoneType.LEFT_HAND, BoneType.RIGHT_HAND }
	),
	LOWER_ARM_LENGTH(
		15,
		"Lower arm length",
		"lowerArmLength",
		"Lower arm length",
		0.25f,
		new BoneType[] { BoneType.LEFT_LOWER_ARM, BoneType.RIGHT_LOWER_ARM }
	),
	SHOULDERS_DISTANCE(
		16,
		"Shoulders distance",
		"shoulersDistance",
		"Shoulders distance",
		0.08f,
		new BoneType[] { BoneType.LEFT_SHOULDER, BoneType.RIGHT_SHOULDER }
	),
	SHOULDERS_WIDTH(
		17,
		"Shoulders width",
		"shoulersWidth",
		"Shoulders width",
		0.36f,
		new BoneType[] { BoneType.LEFT_SHOULDER, BoneType.RIGHT_SHOULDER }
	),
	UPPER_ARM_LENGTH(
		18,
		"Upper arm length",
		"upperArmLength",
		"Upper arm length",
		0.25f,
		new BoneType[] { BoneType.LEFT_UPPER_ARM, BoneType.RIGHT_UPPER_ARM }
	),
	ELBOW_OFFSET(
		19,
		"Elbow offset",
		"elbowOffset",
		"Elbow offset",
		0f,
		new BoneType[] { BoneType.LEFT_ELBOW_TRACKER, BoneType.RIGHT_ELBOW_TRACKER }
	),;

	public static final SkeletonConfigOffsets[] values = values();
	private static final String CONFIG_PREFIX = "body.";
	private static final Map<String, SkeletonConfigOffsets> byStringVal = new HashMap<>();
	private static final Map<Number, SkeletonConfigOffsets> byIdVal = new HashMap<>();

	static {
		for (SkeletonConfigOffsets configVal : values()) {
			byIdVal.put(configVal.id, configVal);
			byStringVal.put(configVal.stringVal.toLowerCase(), configVal);
		}
	}

	public final int id;
	public final String stringVal;
	public final String configKey;
	public final String label;
	public final float defaultValue;
	public final BoneType[] affectedOffsets;

	SkeletonConfigOffsets(
		int id,
		String stringVal,
		String configKey,
		String label,
		float defaultValue,
		BoneType[] affectedOffsets
	) {
		this.id = id;
		this.stringVal = stringVal;
		this.configKey = CONFIG_PREFIX + configKey;
		this.label = label;

		this.defaultValue = defaultValue;

		this.affectedOffsets = affectedOffsets
			== null ? new BoneType[0] : affectedOffsets;
	}

	public static SkeletonConfigOffsets getByStringValue(String stringVal) {
		return stringVal == null ? null : byStringVal.get(stringVal.toLowerCase());
	}

	public static SkeletonConfigOffsets getById(int id) {
		return byIdVal.get(id);
	}
}
