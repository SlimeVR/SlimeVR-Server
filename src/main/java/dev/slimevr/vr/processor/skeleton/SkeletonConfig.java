package dev.slimevr.vr.processor.skeleton;

import com.jme3.math.Vector3f;
import dev.slimevr.Main;
import dev.slimevr.config.ConfigManager;
import io.eiren.util.logging.LogManager;

import java.util.EnumMap;
import java.util.Map;


public class SkeletonConfig {

	protected final EnumMap<SkeletonConfigOffsets, Float> configOffsets = new EnumMap<SkeletonConfigOffsets, Float>(
		SkeletonConfigOffsets.class
	);
	protected final EnumMap<SkeletonConfigToggles, Boolean> configToggles = new EnumMap<SkeletonConfigToggles, Boolean>(
		SkeletonConfigToggles.class
	);
	protected final EnumMap<SkeletonConfigValues, Float> configValues = new EnumMap<SkeletonConfigValues, Float>(
		SkeletonConfigValues.class
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

		for (SkeletonConfigOffsets config : SkeletonConfigOffsets.values) {
			try {
				Float val = configOffsets.get(config);
				if (!defaultOnly || val == null) {
					callback.updateOffsetsState(config, val == null ? config.defaultValue : val);
				}
			} catch (Exception e) {
				LogManager.severe("[SkeletonConfig] Exception while calling callback", e);
			}
		}

		for (SkeletonConfigToggles config : SkeletonConfigToggles.values) {
			try {
				Boolean val = configToggles.get(config);
				if (!defaultOnly || val == null) {
					callback.updateTogglesState(config, val == null ? config.defaultValue : val);
				}
			} catch (Exception e) {
				LogManager.severe("[SkeletonConfig] Exception while calling callback", e);
			}
		}

		for (SkeletonConfigValues config : SkeletonConfigValues.values) {
			try {
				Float val = configValues.get(config);
				if (!defaultOnly || val == null) {
					callback.updateValuesState(config, val == null ? config.defaultValue : val);
				}
			} catch (Exception e) {
				LogManager.severe("[SkeletonConfig] Exception while calling callback", e);
			}
		}
	}

	public Float setOffset(
		SkeletonConfigOffsets config,
		Float newValue,
		boolean computeOffsets
	) {
		Float origVal = newValue != null
			? configOffsets.put(config, newValue)
			: configOffsets.remove(config);

		// Re-compute the affected offsets
		if (computeOffsets && autoUpdateOffsets && config.affectedOffsets != null) {
			for (BoneType offset : config.affectedOffsets) {
				computeNodeOffset(offset);
			}
		}

		if (callback != null) {
			try {
				callback
					.updateOffsetsState(config, newValue != null ? newValue : config.defaultValue);
			} catch (Exception e) {
				LogManager.severe("[SkeletonConfig] Exception while calling callback", e);
			}
		}

		return origVal;
	}

	public Float setOffset(SkeletonConfigOffsets config, Float newValue) {
		return setOffset(config, newValue, true);
	}

	public float getOffset(SkeletonConfigOffsets config) {
		if (config == null) {
			return 0f;
		}

		Float val = configOffsets.get(config);
		return val != null ? val : config.defaultValue;
	}

	public Boolean setToggle(SkeletonConfigToggles config, Boolean newValue) {
		Boolean origVal = newValue != null
			? configToggles.put(config, newValue)
			: configToggles.remove(config);

		if (callback != null) {
			try {
				callback
					.updateTogglesState(config, newValue != null ? newValue : config.defaultValue);
			} catch (Exception e) {
				LogManager.severe("[SkeletonConfig] Exception while calling callback", e);
			}
		}

		return origVal;
	}

	public boolean getToggle(SkeletonConfigToggles config) {
		if (config == null) {
			return false;
		}

		Boolean val = configToggles.get(config);
		return val != null ? val : config.defaultValue;
	}

	public Float setValue(SkeletonConfigValues config, Float newValue) {
		Float origVal = newValue != null
			? configValues.put(config, newValue)
			: configValues.remove(config);

		if (callback != null) {
			try {
				callback
					.updateValuesState(config, newValue != null ? newValue : config.defaultValue);
			} catch (Exception e) {
				LogManager.severe("[SkeletonConfig] Exception while calling callback", e);
			}
		}

		return origVal;
	}

	public float getValue(SkeletonConfigValues config) {
		if (config == null) {
			return 0f;
		}

		Float val = configValues.get(config);
		return val != null ? val : config.defaultValue;
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

	public void computeNodeOffset(BoneType nodeOffset) {
		switch (nodeOffset) {
			case HEAD:
				setNodeOffset(nodeOffset, 0, 0, getOffset(SkeletonConfigOffsets.HEAD));
				break;
			case NECK:
				setNodeOffset(nodeOffset, 0, -getOffset(SkeletonConfigOffsets.NECK), 0);
				break;
			case CHEST:
				setNodeOffset(nodeOffset, 0, -getOffset(SkeletonConfigOffsets.CHEST), 0);
				break;
			case CHEST_TRACKER:
				setNodeOffset(
					nodeOffset,
					0,
					0,
					-getOffset(SkeletonConfigOffsets.SKELETON_OFFSET)
				);
				break;
			case WAIST:
				setNodeOffset(
					nodeOffset,
					0,
					(getOffset(SkeletonConfigOffsets.CHEST)
						- getOffset(SkeletonConfigOffsets.TORSO)
						+ getOffset(SkeletonConfigOffsets.WAIST)),
					0
				);
				break;
			case HIP:
				setNodeOffset(nodeOffset, 0, -getOffset(SkeletonConfigOffsets.WAIST), 0);
				break;
			case HIP_TRACKER:
				setNodeOffset(
					nodeOffset,
					0,
					getOffset(SkeletonConfigOffsets.HIP_OFFSET),
					-getOffset(SkeletonConfigOffsets.SKELETON_OFFSET)
				);
				break;
			case LEFT_HIP:
				setNodeOffset(
					nodeOffset,
					-getOffset(SkeletonConfigOffsets.HIPS_WIDTH) / 2f,
					0,
					0
				);
				break;
			case RIGHT_HIP:
				setNodeOffset(
					nodeOffset,
					getOffset(SkeletonConfigOffsets.HIPS_WIDTH) / 2f,
					0,
					0
				);
				break;
			case LEFT_UPPER_LEG:
			case RIGHT_UPPER_LEG:
				setNodeOffset(
					nodeOffset,
					0,
					-(getOffset(SkeletonConfigOffsets.LEGS_LENGTH)
						- getOffset(SkeletonConfigOffsets.KNEE_HEIGHT)),
					0
				);
				break;
			case LEFT_KNEE_TRACKER:
			case RIGHT_KNEE_TRACKER:
				setNodeOffset(
					nodeOffset,
					0,
					0,
					-getOffset(SkeletonConfigOffsets.SKELETON_OFFSET)
				);
				break;
			case LEFT_LOWER_LEG:
			case RIGHT_LOWER_LEG:
				setNodeOffset(
					nodeOffset,
					0,
					-getOffset(SkeletonConfigOffsets.KNEE_HEIGHT),
					-getOffset(SkeletonConfigOffsets.FOOT_SHIFT)
				);
				break;
			case LEFT_FOOT:
			case RIGHT_FOOT:
				setNodeOffset(nodeOffset, 0, 0, -getOffset(SkeletonConfigOffsets.FOOT_LENGTH));
				break;
			case LEFT_FOOT_TRACKER:
			case RIGHT_FOOT_TRACKER:
				setNodeOffset(
					nodeOffset,
					0,
					0,
					-getOffset(SkeletonConfigOffsets.SKELETON_OFFSET)
				);
				break;
			case LEFT_CONTROLLER:
			case RIGHT_CONTROLLER:
				setNodeOffset(
					nodeOffset,
					0,
					getOffset(SkeletonConfigOffsets.CONTROLLER_DISTANCE_Y),
					getOffset(SkeletonConfigOffsets.CONTROLLER_DISTANCE_Z)
				);
				break;
			case LEFT_HAND:
			case RIGHT_HAND:
				setNodeOffset(
					nodeOffset,
					0,
					-getOffset(SkeletonConfigOffsets.CONTROLLER_DISTANCE_Y),
					-getOffset(SkeletonConfigOffsets.CONTROLLER_DISTANCE_Z)
				);
				break;
			case LEFT_LOWER_ARM:
			case RIGHT_LOWER_ARM:
				setNodeOffset(
					nodeOffset,
					0,
					getOffset(SkeletonConfigOffsets.LOWER_ARM_LENGTH),
					0
				);
				break;
			case LEFT_ELBOW_TRACKER:
			case RIGHT_ELBOW_TRACKER:
				setNodeOffset(nodeOffset, 0, getOffset(SkeletonConfigOffsets.ELBOW_OFFSET), 0);
				break;
			case LEFT_UPPER_ARM:
			case RIGHT_UPPER_ARM:
				setNodeOffset(
					nodeOffset,
					0,
					-getOffset(SkeletonConfigOffsets.UPPER_ARM_LENGTH),
					0
				);
				break;
			case LEFT_SHOULDER:
				setNodeOffset(
					nodeOffset,
					-getOffset(SkeletonConfigOffsets.SHOULDERS_WIDTH) / 2f,
					-getOffset(SkeletonConfigOffsets.SHOULDERS_DISTANCE),
					0
				);
				break;
			case RIGHT_SHOULDER:
				setNodeOffset(
					nodeOffset,
					getOffset(SkeletonConfigOffsets.SHOULDERS_WIDTH) / 2f,
					-getOffset(SkeletonConfigOffsets.SHOULDERS_DISTANCE),
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
		Map<SkeletonConfigOffsets, Float> configOffsets,
		Map<SkeletonConfigToggles, Boolean> configToggles,
		Map<SkeletonConfigValues, Float> configValues
	) {
		if (configOffsets != null) {
			configOffsets.forEach((key, value) -> {
				// Do not recalculate the offsets, these are done in bulk at the
				// end
				setOffset(key, value, false);
			});
		}

		if (configToggles != null) {
			configToggles.forEach(this::setToggle);
		}

		if (configValues != null) {
			configValues.forEach(this::setValue);
		}

		if (autoUpdateOffsets) {
			computeAllNodeOffsets();
		}
	}

	public void setConfigs(SkeletonConfig skeletonConfig) {
		setConfigs(
			skeletonConfig.configOffsets,
			skeletonConfig.configToggles,
			skeletonConfig.configValues
		);
	}
	// #endregion

	public void loadFromConfig(ConfigManager configManager) {

		for (SkeletonConfigOffsets configValue : SkeletonConfigOffsets.values) {
			Float val = configManager
				.getVrConfig()
				.getSkeleton()
				.getOffsets()
				.get(configValue.configKey);
			if (val != null) {
				// Do not recalculate the offsets, these are done in bulk at the
				// end
				setOffset(configValue, val, false);
			}
		}

		for (SkeletonConfigToggles configValue : SkeletonConfigToggles.values) {
			Boolean val = configManager
				.getVrConfig()
				.getSkeleton()
				.getToggles()
				.get(configValue.configKey);
			if (val != null) {
				setToggle(configValue, val);
			}
		}

		for (SkeletonConfigValues configValue : SkeletonConfigValues.values) {
			Float val = configManager
				.getVrConfig()
				.getSkeleton()
				.getValues()
				.get(configValue.configKey);
			if (val != null) {
				setValue(configValue, val);
			}
		}


		if (autoUpdateOffsets) {
			computeAllNodeOffsets();
		}
	}

	public void save() {
		ConfigManager cm = Main.vrServer.getConfigManager();
		// Write all possible values, this keeps configs consistent even if
		// defaults were changed
		for (SkeletonConfigOffsets value : SkeletonConfigOffsets.values) {
			cm.getVrConfig().getSkeleton().getOffsets().put(value.configKey, getOffset(value));
		}

		for (SkeletonConfigToggles value : SkeletonConfigToggles.values) {
			cm.getVrConfig().getSkeleton().getToggles().put(value.configKey, getToggle(value));
		}

		for (SkeletonConfigValues value : SkeletonConfigValues.values) {
			cm.getVrConfig().getSkeleton().getValues().put(value.configKey, getValue(value));
		}
	}

	public void resetConfigs() {
		configOffsets.clear();
		configToggles.clear();
		configValues.clear();

		callCallbackOnAll(false);

		if (autoUpdateOffsets) {
			computeAllNodeOffsets();
		}
	}
}
