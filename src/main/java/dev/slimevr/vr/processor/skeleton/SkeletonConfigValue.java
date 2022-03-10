package dev.slimevr.vr.processor.skeleton;

import java.util.HashMap;
import java.util.Map;

public enum SkeletonConfigValue {
	
	HEAD("Head", "headShift", "Head shift", 0.1f, new SkeletonNodeOffset[]{SkeletonNodeOffset.HEAD}),
	NECK("Neck", "neckLength", "Neck length", 0.1f, new SkeletonNodeOffset[]{SkeletonNodeOffset.NECK}),
	TORSO("Torso", "torsoLength", "Torso length", 0.6f, new SkeletonNodeOffset[]{SkeletonNodeOffset.WAIST}),
	CHEST("Chest", "chestDistance", "Chest distance", 0.3f, new SkeletonNodeOffset[]{SkeletonNodeOffset.CHEST, SkeletonNodeOffset.WAIST}),
	WAIST("Waist", "waistDistance", "Waist distance", 0.05f, new SkeletonNodeOffset[]{SkeletonNodeOffset.WAIST, SkeletonNodeOffset.HIP}),
	HIP_OFFSET("Hip offset", "hipOffset", "Hip offset", 0.0f, new SkeletonNodeOffset[]{SkeletonNodeOffset.HIP_TRACKER}),
	HIPS_WIDTH("Hips width", "hipsWidth", "Hips width", 0.28f, new SkeletonNodeOffset[]{SkeletonNodeOffset.LEFT_HIP, SkeletonNodeOffset.RIGHT_HIP}),
	LEGS_LENGTH("Legs length", "legsLength", "Legs length", 0.88f, new SkeletonNodeOffset[]{SkeletonNodeOffset.KNEE}),
	KNEE_HEIGHT("Knee height", "kneeHeight", "Knee height", 0.44f, new SkeletonNodeOffset[]{SkeletonNodeOffset.KNEE, SkeletonNodeOffset.ANKLE}),
	FOOT_LENGTH("Foot length", "footLength", "Foot length", 0.05f, new SkeletonNodeOffset[]{SkeletonNodeOffset.FOOT}),
	FOOT_OFFSET("Foot offset", "footOffset", "Foot offset", 0.0f, new SkeletonNodeOffset[]{SkeletonNodeOffset.ANKLE}),
	SKELETON_OFFSET("Skeleton offset", "skeletonOffset", "Skeleton offset", 0.0f, new SkeletonNodeOffset[]{SkeletonNodeOffset.CHEST_TRACKER, SkeletonNodeOffset.HIP_TRACKER, SkeletonNodeOffset.KNEE_TRACKER, SkeletonNodeOffset.FOOT_TRACKER}),
	CONTROLLER_DISTANCE("Controller distance", "controllerDistance", "Controller distance", 0.15f, new SkeletonNodeOffset[]{SkeletonNodeOffset.HAND}),
	ELBOW_DISTANCE("Elbow distance", "elbowDistance", "Elbow distance", 0.3f, new SkeletonNodeOffset[]{SkeletonNodeOffset.ELBOW}),
	;
	
	private static final String CONFIG_PREFIX = "body.";
	
	public final String stringVal;
	public final String configKey;
	public final String label;
	
	public final float defaultValue;
	
	public final SkeletonNodeOffset[] affectedOffsets;
	
	public static final SkeletonConfigValue[] values = values();
	private static final Map<String, SkeletonConfigValue> byStringVal = new HashMap<>();
	
	private SkeletonConfigValue(String stringVal, String configKey, String label, float defaultValue, SkeletonNodeOffset[] affectedOffsets) {
		this.stringVal = stringVal;
		this.configKey = CONFIG_PREFIX + configKey;
		this.label = label;
		
		this.defaultValue = defaultValue;
		
		this.affectedOffsets = affectedOffsets == null ? new SkeletonNodeOffset[0] : affectedOffsets;
	}
	
	public static SkeletonConfigValue getByStringValue(String stringVal) {
		return stringVal == null ? null : byStringVal.get(stringVal.toLowerCase());
	}
	
	static {
		for(SkeletonConfigValue configVal : values()) {
			byStringVal.put(configVal.stringVal.toLowerCase(), configVal);
		}
	}
}
