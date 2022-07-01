package dev.slimevr.vr.processor.skeleton;

import java.util.HashMap;
import java.util.Map;


public enum SkeletonConfigValues {
	// @formatter:off
	WAIST_FROM_CHEST_HIP_AVERAGING(1, "Waist from chest hip averaging", "waistFromChestHipAveraging", 0.45f),
	WAIST_FROM_CHEST_LEGS_AVERAGING(2, "Waist from chest legs averaging", "waistFromChestLegsAveraging", 0.2f),
	HIP_FROM_CHEST_LEGS_AVERAGING(3, "Hip from chest legs averaging", "hipFromChestLegsAveraging", 0.45f),
	HIP_FROM_WAIST_LEGS_AVERAGING(4, "Hip from waist legs averaging", "hipFromWaistLegsAveraging", 0.4f),
	HIP_LEGS_AVERAGING(5, "Hip legs averaging", "hipLegsAveraging", 0.25f),
	KNEE_TRACKER_ANKLE_AVERAGING(6, "Knee tracker ankle averaging", "kneeTrackerAnkleAveraging", 0.75f),;
	// @formatter:on

	public static final SkeletonConfigValues[] values = values();
	public static final String CONFIG_PREFIX = "skeleton.values.";
	private static final Map<String, SkeletonConfigValues> byStringVal = new HashMap<>();

	private static final Map<Number, SkeletonConfigValues> byIdVal = new HashMap<>();

	static {
		for (SkeletonConfigValues configVal : values()) {
			byIdVal.put(configVal.id, configVal);
			byStringVal.put(configVal.stringVal.toLowerCase(), configVal);
		}
	}

	public final int id;
	public final String stringVal;
	public final String configKey;
	public final float defaultValue;

	SkeletonConfigValues(
		int id,
		String stringVal,
		String configKey,
		float defaultValue
	) {
		this.id = id;
		this.stringVal = stringVal;
		this.configKey = CONFIG_PREFIX + configKey;

		this.defaultValue = defaultValue;
	}

	public static SkeletonConfigValues getByStringValue(String stringVal) {
		return stringVal == null ? null : byStringVal.get(stringVal.toLowerCase());
	}

	public static SkeletonConfigValues getById(int id) {
		return byIdVal.get(id);
	}
}
