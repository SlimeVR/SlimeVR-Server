package dev.slimevr.vr.processor.skeleton;

import com.jme3.math.Vector3f;
import io.eiren.util.logging.LogManager;
import io.eiren.yaml.YamlFile;

import java.util.EnumMap;
import java.util.Map;


public class SkeletonConfig {

	protected final EnumMap<SkeletonConfigValue, Float> configs = new EnumMap<SkeletonConfigValue, Float>(
		SkeletonConfigValue.class
	);
	protected final EnumMap<SkeletonConfigToggle, Boolean> toggles = new EnumMap<SkeletonConfigToggle, Boolean>(
		SkeletonConfigToggle.class
	);
	protected final EnumMap<BoneType, Vector3f> nodeOffsets = new EnumMap<BoneType, Vector3f>(
		BoneType.class
	);

	protected final boolean autoUpdateOffsets;
	protected final SkeletonConfigCallback callback;

	public SkeletonConfig(boolean autoUpdateOffsets) {
		this.autoUpdateOffsets = autoUpdateOffsets;
		this.callback = null;

		callCallbackOnAll(true);

		if (autoUpdateOffsets) {
			computeAllNodeOffsets();
		}
	}

	public SkeletonConfig(boolean autoUpdateOffsets, SkeletonConfigCallback callback) {
		this.autoUpdateOffsets = autoUpdateOffsets;
		this.callback = callback;

		callCallbackOnAll(true);

		if (autoUpdateOffsets) {
			computeAllNodeOffsets();
		}
	}

	public SkeletonConfig(
		Map<SkeletonConfigValue, Float> configs,
		Map<SkeletonConfigToggle, Boolean> toggles,
		boolean autoUpdateOffsets,
		SkeletonConfigCallback callback
	) {
		this.autoUpdateOffsets = autoUpdateOffsets;
		this.callback = callback;
		setConfigs(configs, toggles);

		callCallbackOnAll(true);
	}

	public SkeletonConfig(
		Map<SkeletonConfigValue, Float> configs,
		Map<SkeletonConfigToggle, Boolean> toggles,
		boolean autoUpdateOffsets
	) {
		this(configs, toggles, autoUpdateOffsets, null);
	}

	public SkeletonConfig(
		SkeletonConfig skeletonConfig,
		boolean autoUpdateOffsets,
		SkeletonConfigCallback callback
	) {
		this.autoUpdateOffsets = autoUpdateOffsets;
		this.callback = callback;
		setConfigs(skeletonConfig);

		callCallbackOnAll(true);
	}

	public SkeletonConfig(SkeletonConfig skeletonConfig, boolean autoUpdateOffsets) {
		this(skeletonConfig, autoUpdateOffsets, null);
	}

	// #region Cast utilities for config reading
	private static Float castFloat(Object o) {
		if (o == null) {
			return null;
		} else if (o instanceof Float) {
			return (Float) o;
		} else if (o instanceof Double) {
			return ((Double) o).floatValue();
		} else if (o instanceof Byte) {
			return (float) (Byte) o;
		} else if (o instanceof Integer) {
			return (float) (Integer) o;
		} else if (o instanceof Long) {
			return (float) (Long) o;
		} else {
			return null;
		}
	}

	private static Boolean castBoolean(Object o) {
		if (o == null) {
			return null;
		} else if (o instanceof Boolean) {
			return (Boolean) o;
		} else {
			return null;
		}
	}

	private void callCallbackOnAll(boolean defaultOnly) {
		if (callback == null) {
			return;
		}

		for (SkeletonConfigValue config : SkeletonConfigValue.values) {
			try {
				Float val = configs.get(config);
				if (!defaultOnly || val == null) {
					callback.updateConfigState(config, val == null ? config.defaultValue : val);
				}
			} catch (Exception e) {
				LogManager.severe("[SkeletonConfig] Exception while calling callback", e);
			}
		}

		for (SkeletonConfigToggle config : SkeletonConfigToggle.values) {
			try {
				Boolean val = toggles.get(config);
				if (!defaultOnly || val == null) {
					callback.updateToggleState(config, val == null ? config.defaultValue : val);
				}
			} catch (Exception e) {
				LogManager.severe("[SkeletonConfig] Exception while calling callback", e);
			}
		}
	}

	public Float setConfig(SkeletonConfigValue config, Float newValue, boolean computeOffsets) {
		Float origVal = newValue != null ? configs.put(config, newValue) : configs.remove(config);

		// Re-compute the affected offsets
		if (computeOffsets && autoUpdateOffsets && config.affectedOffsets != null) {
			for (BoneType offset : config.affectedOffsets) {
				computeNodeOffset(offset);
			}
		}

		if (callback != null) {
			try {
				callback
					.updateConfigState(config, newValue != null ? newValue : config.defaultValue);
			} catch (Exception e) {
				LogManager.severe("[SkeletonConfig] Exception while calling callback", e);
			}
		}

		return origVal;
	}

	public Float setConfig(SkeletonConfigValue config, Float newValue) {
		return setConfig(config, newValue, true);
	}

	public Float setConfig(String config, Float newValue) {
		return setConfig(SkeletonConfigValue.getByStringValue(config), newValue);
	}

	public float getConfig(SkeletonConfigValue config) {
		if (config == null) {
			return 0f;
		}

		// IMPORTANT!! This null check is necessary, getOrDefault seems to
		// randomly
		// decide to return null at times, so this is a secondary check
		Float val = configs.getOrDefault(config, config.defaultValue);
		return val != null ? val : config.defaultValue;
	}

	public float getConfig(String config) {
		if (config == null) {
			return 0f;
		}
		return getConfig(SkeletonConfigValue.getByStringValue(config));
	}

	public Boolean setToggle(SkeletonConfigToggle config, Boolean newValue) {
		Boolean origVal = newValue != null ? toggles.put(config, newValue) : toggles.remove(config);

		if (callback != null) {
			try {
				callback
					.updateToggleState(config, newValue != null ? newValue : config.defaultValue);
			} catch (Exception e) {
				LogManager.severe("[SkeletonConfig] Exception while calling callback", e);
			}
		}

		return origVal;
	}

	public Boolean setToggle(String config, Boolean newValue) {
		return setToggle(SkeletonConfigToggle.getByStringValue(config), newValue);
	}

	public boolean getToggle(SkeletonConfigToggle config) {
		if (config == null) {
			return false;
		}

		// IMPORTANT!! This null check is necessary, getOrDefault seems to
		// randomly
		// decide to return null at times, so this is a secondary check
		Boolean val = toggles.getOrDefault(config, config.defaultValue);
		return val != null ? val : config.defaultValue;
	}

	public boolean getToggle(String config) {
		if (config == null) {
			return false;
		}

		return getToggle(SkeletonConfigToggle.getByStringValue(config));
	}

	protected void setNodeOffset(BoneType nodeOffset, float x, float y, float z) {
		Vector3f offset = nodeOffsets.get(nodeOffset);

		if (offset == null) {
			offset = new Vector3f(x, y, z);
			nodeOffsets.put(nodeOffset, offset);
		} else {
			offset.set(x, y, z);
		}

		if (callback != null) {
			try {
				callback.updateNodeOffset(nodeOffset, offset);
			} catch (Exception e) {
				LogManager.severe("[SkeletonConfig] Exception while calling callback", e);
			}
		}
	}

	protected void setNodeOffset(BoneType nodeOffset, Vector3f offset) {
		if (offset == null) {
			setNodeOffset(nodeOffset, 0f, 0f, 0f);
			return;
		}

		setNodeOffset(nodeOffset, offset.x, offset.y, offset.z);
	}

	public Vector3f getNodeOffset(BoneType nodeOffset) {
		return nodeOffsets.getOrDefault(nodeOffset, Vector3f.ZERO);
	}

	public void computeNodeOffset(BoneType nodeOffset) {
		switch (nodeOffset) {
			case HEAD:
				setNodeOffset(nodeOffset, 0, 0, getConfig(SkeletonConfigValue.HEAD));
				break;
			case NECK:
				setNodeOffset(nodeOffset, 0, -getConfig(SkeletonConfigValue.NECK), 0);
				break;
			case CHEST:
				setNodeOffset(nodeOffset, 0, -getConfig(SkeletonConfigValue.CHEST), 0);
				break;
			case CHEST_TRACKER:
				setNodeOffset(nodeOffset, 0, 0, -getConfig(SkeletonConfigValue.SKELETON_OFFSET));
				break;
			case WAIST:
				setNodeOffset(
					nodeOffset,
					0,
					(getConfig(SkeletonConfigValue.CHEST)
						- getConfig(SkeletonConfigValue.TORSO)
						+ getConfig(SkeletonConfigValue.WAIST)),
					0
				);
				break;
			case HIP:
				setNodeOffset(nodeOffset, 0, -getConfig(SkeletonConfigValue.WAIST), 0);
				break;
			case HIP_TRACKER:
				setNodeOffset(
					nodeOffset,
					0,
					getConfig(SkeletonConfigValue.HIP_OFFSET),
					-getConfig(SkeletonConfigValue.SKELETON_OFFSET)
				);
				break;

			case LEFT_HIP:
				setNodeOffset(nodeOffset, -getConfig(SkeletonConfigValue.HIPS_WIDTH) / 2f, 0, 0);
				break;
			case RIGHT_HIP:
				setNodeOffset(nodeOffset, getConfig(SkeletonConfigValue.HIPS_WIDTH) / 2f, 0, 0);
				break;

			case UPPER_LEG:
				setNodeOffset(
					nodeOffset,
					0,
					-(getConfig(SkeletonConfigValue.LEGS_LENGTH)
						- getConfig(SkeletonConfigValue.KNEE_HEIGHT)),
					0
				);
				break;
			case KNEE_TRACKER:
				setNodeOffset(nodeOffset, 0, 0, -getConfig(SkeletonConfigValue.SKELETON_OFFSET));
				break;
			case LOWER_LEG:
				setNodeOffset(
					nodeOffset,
					0,
					-getConfig(SkeletonConfigValue.KNEE_HEIGHT),
					-getConfig(SkeletonConfigValue.FOOT_OFFSET)
				);
				break;
			case FOOT:
				setNodeOffset(nodeOffset, 0, 0, -getConfig(SkeletonConfigValue.FOOT_LENGTH));
				break;
			case FOOT_TRACKER:
				setNodeOffset(nodeOffset, 0, 0, -getConfig(SkeletonConfigValue.SKELETON_OFFSET));
				break;

			case CONTROLLER:
				setNodeOffset(
					nodeOffset,
					0,
					getConfig(SkeletonConfigValue.CONTROLLER_DISTANCE_Y),
					getConfig(SkeletonConfigValue.CONTROLLER_DISTANCE_Z)
				);
				break;
			case HAND:
				setNodeOffset(
					nodeOffset,
					0,
					-getConfig(SkeletonConfigValue.CONTROLLER_DISTANCE_Y),
					-getConfig(SkeletonConfigValue.CONTROLLER_DISTANCE_Z)
				);
				break;
			case LOWER_ARM:
				setNodeOffset(nodeOffset, 0, getConfig(SkeletonConfigValue.LOWER_ARM_LENGTH), 0);
				break;
			case LOWER_ARM_HMD:
				setNodeOffset(nodeOffset, 0, -getConfig(SkeletonConfigValue.LOWER_ARM_LENGTH), 0);
				break;
			case ELBOW_TRACKER:
				setNodeOffset(nodeOffset, 0, getConfig(SkeletonConfigValue.ELBOW_OFFSET), 0);
				break;
			case UPPER_ARM:
				setNodeOffset(nodeOffset, 0, -getConfig(SkeletonConfigValue.UPPER_ARM_LENGTH), 0);
				break;
			case LEFT_SHOULDER:
				setNodeOffset(
					nodeOffset,
					-getConfig(SkeletonConfigValue.SHOULDERS_WIDTH) / 2f,
					-getConfig(SkeletonConfigValue.SHOULDERS_DISTANCE),
					0
				);
				break;
			case RIGHT_SHOULDER:
				setNodeOffset(
					nodeOffset,
					getConfig(SkeletonConfigValue.SHOULDERS_WIDTH) / 2f,
					-getConfig(SkeletonConfigValue.SHOULDERS_DISTANCE),
					0
				);
				break;
		}
	}

	public void computeAllNodeOffsets() {
		for (BoneType offset : BoneType.values) {
			computeNodeOffset(offset);
		}
	}

	public void setConfigs(
		Map<SkeletonConfigValue, Float> configs,
		Map<SkeletonConfigToggle, Boolean> toggles
	) {
		if (configs != null) {
			configs.forEach((key, value) -> {
				// Do not recalculate the offsets, these are done in bulk at the
				// end
				setConfig(key, value, false);
			});
		}

		if (toggles != null) {
			toggles.forEach(this::setToggle);
		}

		if (autoUpdateOffsets) {
			computeAllNodeOffsets();
		}
	}

	public void setStringConfigs(Map<String, Float> configs, Map<String, Boolean> toggles) {
		if (configs != null) {
			configs.forEach((key, value) -> {
				// Do not recalculate the offsets, these are done in bulk at the
				// end
				setConfig(SkeletonConfigValue.getByStringValue(key), value, false);
			});
		}

		if (toggles != null) {
			toggles.forEach((key, value) -> {
				setToggle(SkeletonConfigToggle.getByStringValue(key), value);
			});
		}

		if (autoUpdateOffsets) {
			computeAllNodeOffsets();
		}
	}

	public void setConfigs(SkeletonConfig skeletonConfig) {
		setConfigs(skeletonConfig.configs, skeletonConfig.toggles);
	}
	// #endregion

	public void loadFromConfig(YamlFile config) {
		for (SkeletonConfigValue configValue : SkeletonConfigValue.values) {
			Float val = castFloat(config.getProperty(configValue.configKey));
			if (val != null) {
				// Do not recalculate the offsets, these are done in bulk at the
				// end
				setConfig(configValue, val, false);
			}
		}

		for (SkeletonConfigToggle configValue : SkeletonConfigToggle.values) {
			Boolean val = castBoolean(config.getProperty(configValue.configKey));
			if (val != null) {
				setToggle(configValue, val);
			}
		}

		if (autoUpdateOffsets) {
			computeAllNodeOffsets();
		}
	}

	public void saveToConfig(YamlFile config) {
		// Write all possible values, this keeps configs consistent even if
		// defaults
		// were changed
		for (SkeletonConfigValue value : SkeletonConfigValue.values) {
			config.setProperty(value.configKey, getConfig(value));
		}

		for (SkeletonConfigToggle value : SkeletonConfigToggle.values) {
			config.setProperty(value.configKey, getToggle(value));
		}
	}

	public void resetConfigs() {
		configs.clear();
		toggles.clear();

		callCallbackOnAll(false);

		if (autoUpdateOffsets) {
			computeAllNodeOffsets();
		}
	}
}
