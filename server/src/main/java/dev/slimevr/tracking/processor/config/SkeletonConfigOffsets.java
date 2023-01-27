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
	CHEST(
		3,
		"chestLength",
		0.32f,
		new BoneType[] { BoneType.CHEST, BoneType.CHEST_TRACKER }
	),
	CHEST_OFFSET(
		4,
		"chestOffset",
		0.0f,
		new BoneType[] { BoneType.CHEST_TRACKER }
	),
	WAIST(
		5,
		"waistLength",
		0.20f,
		new BoneType[] { BoneType.WAIST }
	),
	HIP(
		6,
		"hipLength",
		0.04f,
		new BoneType[] { BoneType.HIP }
	),
	HIP_OFFSET(
		7,
		"hipOffset",
		0.0f,
		new BoneType[] { BoneType.HIP_TRACKER }
	),
	HIPS_WIDTH(
		8,
		"hipsWidth",
		0.26f,
		new BoneType[] { BoneType.LEFT_HIP, BoneType.RIGHT_HIP }
	),
	UPPER_LEG(
		9,
		"upperLegLength",
		0.42f,
		new BoneType[] { BoneType.LEFT_UPPER_LEG, BoneType.RIGHT_UPPER_LEG }
	),
	LOWER_LEG(
		10,
		"lowerLegLength",
		0.50f,
		new BoneType[] { BoneType.LEFT_LOWER_LEG, BoneType.RIGHT_LOWER_LEG }
	),
	FOOT_LENGTH(
		11,
		"footLength",
		0.05f,
		new BoneType[] { BoneType.LEFT_FOOT, BoneType.RIGHT_FOOT }
	),
	FOOT_SHIFT(
		12,
		"footShift",
		-0.05f,
		new BoneType[] { BoneType.LEFT_LOWER_LEG, BoneType.RIGHT_LOWER_LEG }
	),
	SKELETON_OFFSET(
		13,
		"skeletonOffset",
		0.0f,
		new BoneType[] { BoneType.CHEST_TRACKER, BoneType.HIP_TRACKER,
			BoneType.LEFT_KNEE_TRACKER, BoneType.RIGHT_KNEE_TRACKER,
			BoneType.LEFT_FOOT_TRACKER, BoneType.RIGHT_KNEE_TRACKER }
	),
	SHOULDERS_DISTANCE(
		14,
		"shouldersDistance",
		0.08f,
		new BoneType[] { BoneType.LEFT_SHOULDER, BoneType.RIGHT_SHOULDER }
	),
	SHOULDERS_WIDTH(
		15,
		"shouldersWidth",
		0.36f,
		new BoneType[] { BoneType.LEFT_SHOULDER, BoneType.RIGHT_SHOULDER }
	),
	UPPER_ARM(
		16,
		"upperArmLength",
		0.25f,
		new BoneType[] { BoneType.LEFT_UPPER_ARM, BoneType.RIGHT_UPPER_ARM }
	),
	LOWER_ARM(
		17,
		"lowerArmLength",
		0.25f,
		new BoneType[] { BoneType.LEFT_LOWER_ARM, BoneType.RIGHT_LOWER_ARM }
	),
	CONTROLLER_Y(
		18,
		"controllerDistanceY",
		0.035f,
		new BoneType[] { BoneType.LEFT_CONTROLLER, BoneType.RIGHT_CONTROLLER,
			BoneType.LEFT_HAND, BoneType.RIGHT_HAND }
	),
	CONTROLLER_Z(
		19,
		"controllerDistanceZ",
		0.13f,
		new BoneType[] { BoneType.LEFT_CONTROLLER, BoneType.RIGHT_CONTROLLER,
			BoneType.LEFT_HAND, BoneType.RIGHT_HAND }
	),
	ELBOW_OFFSET(
		20,
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
		this.id = id;
		this.configKey = configKey;

		this.defaultValue = defaultValue;

		this.affectedOffsets = affectedOffsets
			== null ? new BoneType[0] : affectedOffsets;
	}

	public static SkeletonConfigOffsets getById(int id) {
		return byIdVal.get(id);
	}
}
