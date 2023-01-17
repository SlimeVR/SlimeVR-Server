package dev.slimevr.vr.processor.skeleton;

import java.util.HashMap;
import java.util.Map;


public enum SkeletonConfigToggles {

	EXTENDED_SPINE_MODEL(1, "extendedSpine", true),
	EXTENDED_PELVIS_MODEL(2, "extendedPelvis", true),
	EXTENDED_KNEE_MODEL(3, "extendedKnee", true),
	FORCE_ARMS_FROM_HMD(4, "forceArmsFromHMD", true),
	FLOOR_CLIP(5, "floorClip", true),
	SKATING_CORRECTION(6, "skatingCorrection", true),
	VIVE_EMULATION(7, "viveEmulation", false),
	I_POSE(8, "iPose", false),;

	public static final SkeletonConfigToggles[] values = values();

	private static final Map<Number, SkeletonConfigToggles> byIdVal = new HashMap<>();

	static {
		for (SkeletonConfigToggles configVal : values()) {
			byIdVal.put(configVal.id, configVal);
		}
	}

	public final int id;
	public final String configKey;
	public final boolean defaultValue;

	SkeletonConfigToggles(int id, String configKey, boolean defaultValue) {
		this.id = id;
		this.configKey = configKey;

		this.defaultValue = defaultValue;
	}

	public static SkeletonConfigToggles getById(int id) {
		return byIdVal.get(id);
	}
}
