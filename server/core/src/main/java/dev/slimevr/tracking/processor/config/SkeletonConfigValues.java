package dev.slimevr.tracking.processor.config;

import java.util.HashMap;
import java.util.Map;


public enum SkeletonConfigValues {
	// @formatter:off
	WAIST_FROM_CHEST_HIP_AVERAGING(1, "waistFromChestHipAveraging", 0.30f),
	WAIST_FROM_CHEST_LEGS_AVERAGING(2, "waistFromChestLegsAveraging", 0.30f),
	HIP_FROM_CHEST_LEGS_AVERAGING(3, "hipFromChestLegsAveraging", 0.50f),
	HIP_FROM_WAIST_LEGS_AVERAGING(4, "hipFromWaistLegsAveraging", 0.40f),
	HIP_LEGS_AVERAGING(5, "hipLegsAveraging", 0.25f),
	KNEE_TRACKER_ANKLE_AVERAGING(6, "kneeTrackerAnkleAveraging", 0.85f),
	KNEE_ANKLE_AVERAGING(7, "kneeAnkleAveraging", 0.00f),;
	// @formatter:on

	public static final SkeletonConfigValues[] values = values();
	private static final Map<Number, SkeletonConfigValues> byIdVal = new HashMap<>();

	static {
		for (SkeletonConfigValues configVal : values()) {
			byIdVal.put(configVal.id, configVal);
		}
	}

	public final int id;
	public final String configKey;
	public final float defaultValue;

	SkeletonConfigValues(
		int id,
		String configKey,
		float defaultValue
	) {
		this.id = id;
		this.configKey = configKey;

		this.defaultValue = defaultValue;
	}

	public static SkeletonConfigValues getById(int id) {
		return byIdVal.get(id);
	}
}
