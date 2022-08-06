package dev.slimevr.vr.processor.skeleton;

import java.util.HashMap;
import java.util.Map;


public enum SkeletonConfigToggles {

	EXTENDED_SPINE_MODEL(1, "Extended spine model", "extendedSpine", true),
	EXTENDED_PELVIS_MODEL(2, "Extended pelvis model", "extendedPelvis", true),
	EXTENDED_KNEE_MODEL(3, "Extended knee model", "extendedKnee", true),
	FORCE_ARMS_FROM_HMD(4, "Force arms from HMD", "forceArmsFromHMD", true),;

	public static final SkeletonConfigToggles[] values = values();
	public static final String CONFIG_PREFIX = "skeleton.toggles.";
	private static final Map<String, SkeletonConfigToggles> byStringVal = new HashMap<>();

	private static final Map<Number, SkeletonConfigToggles> byIdVal = new HashMap<>();

	static {
		for (SkeletonConfigToggles configVal : values()) {
			byIdVal.put(configVal.id, configVal);
			byStringVal.put(configVal.stringVal.toLowerCase(), configVal);
		}
	}

	public final int id;
	public final String stringVal;
	public final String configKey;
	public final boolean defaultValue;

	SkeletonConfigToggles(int id, String stringVal, String configKey, boolean defaultValue) {
		this.id = id;
		this.stringVal = stringVal;
		this.configKey = CONFIG_PREFIX + configKey;

		this.defaultValue = defaultValue;
	}

	public static SkeletonConfigToggles getByStringValue(String stringVal) {
		return stringVal == null ? null : byStringVal.get(stringVal.toLowerCase());
	}

	public static SkeletonConfigToggles getById(int id) {
		return byIdVal.get(id);
	}
}
