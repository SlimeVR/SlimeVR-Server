package dev.slimevr.vr.processor;

import java.util.HashMap;
import java.util.Map;

public enum SkeletonConfigValue {
	
	HEAD("Head", "headShift", 0.1f, new SkeletonNodeOffset[] { SkeletonNodeOffset.HEAD }),
	NECK("Neck", "neckLength", 0.1f, new SkeletonNodeOffset[] { SkeletonNodeOffset.NECK }),
	TORSO("Torso", "torsoLength", 0.7f, new SkeletonNodeOffset[] { SkeletonNodeOffset.WAIST }),
	CHEST("Chest", "chestDistance", 0.35f, new SkeletonNodeOffset[] { SkeletonNodeOffset.CHEST, SkeletonNodeOffset.WAIST }),
	WAIST("Waist", "waistDistance", 0.1f, new SkeletonNodeOffset[] { SkeletonNodeOffset.WAIST, SkeletonNodeOffset.HIP }),
	HIP_OFFSET("Hip offset", "hipOffset", 0.0f, new SkeletonNodeOffset[] { SkeletonNodeOffset.HIP_TRACKER }),
	HIPS_WIDTH("Hips width", "hipsWidth", 0.3f, new SkeletonNodeOffset[] { SkeletonNodeOffset.LEFT_HIP, SkeletonNodeOffset.RIGHT_HIP }),
	LEGS_LENGTH("Legs length", "legsLength", 0.84f, new SkeletonNodeOffset[] { SkeletonNodeOffset.KNEE }),
	KNEE_HEIGHT("Knee height", "kneeHeight", 0.42f, new SkeletonNodeOffset[] { SkeletonNodeOffset.KNEE, SkeletonNodeOffset.ANKLE }),
	FOOT_LENGTH("Foot length", "footLength", 0.05f, new SkeletonNodeOffset[] { SkeletonNodeOffset.FOOT }),
	FOOT_OFFSET("Foot offset", "footOffset", 0.0f, new SkeletonNodeOffset[] { SkeletonNodeOffset.ANKLE }),
	;

	private static final String CONFIG_PREFIX = "body.";
	
	public final String stringVal;
	public final String configKey;

	public final float defaultValue;

	public final SkeletonNodeOffset[] affectedOffsets;

	public static final SkeletonConfigValue[] values = values();
	private static final Map<String, SkeletonConfigValue> byStringVal = new HashMap<>();
	
	private SkeletonConfigValue(String stringVal, String configKey, float defaultValue, SkeletonNodeOffset[] affectedOffsets) {
		this.stringVal = stringVal;
		this.configKey = CONFIG_PREFIX + configKey;

		this.defaultValue = defaultValue;

		this.affectedOffsets = affectedOffsets == null ? new SkeletonNodeOffset[0] : affectedOffsets;
	}
	
	public static SkeletonConfigValue getByStringValue(String stringVal) {
		return stringVal == null ? null : byStringVal.get(stringVal.toLowerCase());
	}
	
	static {
		for (SkeletonConfigValue configVal : values()) {
			byStringVal.put(configVal.stringVal.toLowerCase(), configVal);
		}
	}
}
