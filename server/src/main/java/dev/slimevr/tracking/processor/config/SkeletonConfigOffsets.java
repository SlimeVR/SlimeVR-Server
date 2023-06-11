package dev.slimevr.tracking.processor.config;

import dev.slimevr.tracking.processor.BoneType;

import java.util.HashMap;
import java.util.Map;


public enum SkeletonConfigOffsets {
	HEAD(
		1,
		"headShift",
		0.1f,
		new BoneType[] { BoneType.HEAD }
	),
	NECK(
		2,
		"neckLength",
		0.1f,
		new BoneType[] { BoneType.NECK }
	),
	UPPER_CHEST(
		3,
		"upperChestLength",
		0.16f,
		new BoneType[] { BoneType.UPPER_CHEST, BoneType.CHEST_TRACKER }
	),
	CHEST_OFFSET(
		4,
		"chestOffset",
		0.0f,
		new BoneType[] { BoneType.CHEST_TRACKER }
	),
	CHEST(
		5,
		"chestLength",
		0.16f,
		new BoneType[] { BoneType.CHEST, BoneType.CHEST_TRACKER }
	),
	WAIST(
		6,
		"waistLength",
		0.20f,
		new BoneType[] { BoneType.WAIST }
	),
	HIP(
		7,
		"hipLength",
		0.04f,
		new BoneType[] { BoneType.HIP }
	),
	HIP_OFFSET(
		8,
		"hipOffset",
		0.0f,
		new BoneType[] { BoneType.HIP_TRACKER }
	),
	HIPS_WIDTH(
		9,
		"hipsWidth",
		0.26f,
		new BoneType[] { BoneType.LEFT_HIP, BoneType.RIGHT_HIP }
	),
	UPPER_LEG(
		10,
		"upperLegLength",
		0.42f,
		new BoneType[] { BoneType.LEFT_UPPER_LEG, BoneType.RIGHT_UPPER_LEG }
	),
	LOWER_LEG(
		11,
		"lowerLegLength",
		0.50f,
		new BoneType[] { BoneType.LEFT_LOWER_LEG, BoneType.RIGHT_LOWER_LEG }
	),
	FOOT_LENGTH(
		12,
		"footLength",
		0.05f,
		new BoneType[] { BoneType.LEFT_FOOT, BoneType.RIGHT_FOOT }
	),
	FOOT_SHIFT(
		13,
		"footShift",
		-0.05f,
		new BoneType[] { BoneType.LEFT_LOWER_LEG, BoneType.RIGHT_LOWER_LEG }
	),
	SKELETON_OFFSET(
		14,
		"skeletonOffset",
		0.0f,
		new BoneType[] { BoneType.CHEST_TRACKER, BoneType.HIP_TRACKER,
			BoneType.LEFT_KNEE_TRACKER, BoneType.RIGHT_KNEE_TRACKER,
			BoneType.LEFT_FOOT_TRACKER, BoneType.RIGHT_KNEE_TRACKER }
	),
	SHOULDERS_DISTANCE(
		15,
		"shouldersDistance",
		0.08f,
		new BoneType[] { BoneType.LEFT_SHOULDER, BoneType.RIGHT_SHOULDER }
	),
	SHOULDERS_WIDTH(
		16,
		"shouldersWidth",
		0.36f,
		new BoneType[] { BoneType.LEFT_SHOULDER, BoneType.RIGHT_SHOULDER }
	),
	UPPER_ARM(
		17,
		"upperArmLength",
		0.26f,
		new BoneType[] { BoneType.LEFT_UPPER_ARM, BoneType.RIGHT_UPPER_ARM }
	),
	LOWER_ARM(
		18,
		"lowerArmLength",
		0.26f,
		new BoneType[] { BoneType.LEFT_LOWER_ARM, BoneType.RIGHT_LOWER_ARM }
	),
	HAND_Y(
		19,
		"handDistanceY",
		0.035f,
		new BoneType[] { BoneType.LEFT_HAND, BoneType.RIGHT_HAND }
	),
	HAND_Z(
		20,
		"handDistanceZ",
		0.13f,
		new BoneType[] { BoneType.LEFT_HAND, BoneType.RIGHT_HAND }
	),
	ELBOW_OFFSET(
		21,
		"elbowOffset",
		0.0f,
		new BoneType[] { BoneType.LEFT_ELBOW_TRACKER, BoneType.RIGHT_ELBOW_TRACKER }
	),;

	public static final SkeletonConfigOffsets[] values = values();
	private static final Map<Number, SkeletonConfigOffsets> byIdVal = new HashMap<>();

	static {
		for (SkeletonConfigOffsets configVal : values()) {
			byIdVal.put(configVal.id, configVal);
		}
	}

	public final int id;
	public final String configKey;
	public final float defaultValue;
	public final BoneType[] affectedOffsets;

	SkeletonConfigOffsets(
		int id,
		String configKey,
		float defaultValue,
		BoneType[] affectedOffsets
	) {
		this.id = id; // id of SkeletonBone in solarxr
		this.configKey = configKey;

		this.defaultValue = defaultValue;

		this.affectedOffsets = affectedOffsets
			== null ? new BoneType[0] : affectedOffsets;
	}

	public static SkeletonConfigOffsets getById(int id) {
		return byIdVal.get(id);
	}
}
