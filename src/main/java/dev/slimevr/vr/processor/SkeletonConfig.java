package dev.slimevr.vr.processor;

import java.util.EnumMap;
import java.util.Map;

import com.jme3.math.Vector3f;

import io.eiren.yaml.YamlFile;

public class SkeletonConfig {

	protected final EnumMap<SkeletonConfigValue, Float> configs = new EnumMap<SkeletonConfigValue, Float>(SkeletonConfigValue.class);
	protected final EnumMap<SkeletonConfigToggle, Boolean> toggles = new EnumMap<SkeletonConfigToggle, Boolean>(SkeletonConfigToggle.class);
	protected final EnumMap<SkeletonNodeOffset, Vector3f> nodeOffsets = new EnumMap<SkeletonNodeOffset, Vector3f>(SkeletonNodeOffset.class);

	public SkeletonConfig() {
		computeAllNodeOffsets();
	}

	public SkeletonConfig(Map<SkeletonConfigValue, Float> configs, Map<SkeletonConfigToggle, Boolean> toggles) {
		setConfigs(configs, toggles);
	}

	public SkeletonConfig(SkeletonConfig skeletonConfig) {
		setConfigs(skeletonConfig);
	}

	public Float setConfig(SkeletonConfigValue config, float newValue) {
		Float origVal = configs.put(config, newValue);

		// Re-compute the affected offsets
		if (config.affectedOffsets != null) {
			for (SkeletonNodeOffset offset : config.affectedOffsets) {
				computeNodeOffset(offset);
			}
		}

		return origVal;
	}

	public Float setConfig(String config, float newValue) {
		return setConfig(SkeletonConfigValue.getByStringValue(config), newValue);
	}

	public float getConfig(SkeletonConfigValue config) {
		return configs.getOrDefault(config, config.defaultValue);
	}

	public float getConfig(String config) {
		return getConfig(SkeletonConfigValue.getByStringValue(config));
	}

	public Boolean setConfigToggle(SkeletonConfigToggle config, boolean newValue) {
		return toggles.put(config, newValue);
	}

	public Boolean setConfigToggle(String config, boolean newValue) {
		return setConfigToggle(SkeletonConfigToggle.getByStringValue(config), newValue);
	}

	public boolean getConfigToggle(SkeletonConfigToggle config) {
		return toggles.getOrDefault(config, config.defaultValue);
	}

	public boolean getConfigToggle(String config) {
		return getConfigToggle(SkeletonConfigToggle.getByStringValue(config));
	}

	protected void setNodeOffset(SkeletonNodeOffset nodeOffset, float x, float y, float z) {
		Vector3f offset = nodeOffsets.get(nodeOffset);

		if (offset == null) {
			nodeOffsets.put(nodeOffset, new Vector3f(x, y, z));
		} else {
			offset.set(x, y, z);
		}
	}

	protected void setNodeOffset(SkeletonNodeOffset nodeOffset, Vector3f offset) {
		setNodeOffset(nodeOffset, offset.x, offset.y, offset.z);
	}

	public Vector3f getNodeOffset(SkeletonNodeOffset nodeOffset) {
		return nodeOffsets.getOrDefault(nodeOffset, Vector3f.ZERO);
	}

	protected void computeNodeOffset(SkeletonNodeOffset nodeOffset) {
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
		case WAIST:
			setNodeOffset(nodeOffset, 0, (getConfig(SkeletonConfigValue.CHEST) - getConfig(SkeletonConfigValue.TORSO) + getConfig(SkeletonConfigValue.WAIST)), 0);
			break;
		case HIP:
			setNodeOffset(nodeOffset, 0, -getConfig(SkeletonConfigValue.WAIST), 0);
			break;
		case HIP_TRACKER:
			setNodeOffset(nodeOffset, 0, getConfig(SkeletonConfigValue.HIP_OFFSET), 0);
			break;

		case LEFT_HIP:
			setNodeOffset(nodeOffset, -getConfig(SkeletonConfigValue.HIPS_WIDTH) / 2f, 0, 0);
			break;
		case RIGHT_HIP:
			setNodeOffset(nodeOffset, getConfig(SkeletonConfigValue.HIPS_WIDTH) / 2f, 0, 0);
			break;

		case KNEE:
			setNodeOffset(nodeOffset, 0, -(getConfig(SkeletonConfigValue.LEGS_LENGTH) - getConfig(SkeletonConfigValue.KNEE_HEIGHT)), 0);
			break;
		case ANKLE:
			setNodeOffset(nodeOffset, 0, -getConfig(SkeletonConfigValue.KNEE_HEIGHT), -getConfig(SkeletonConfigValue.FOOT_OFFSET));
			break;
		case FOOT:
			setNodeOffset(nodeOffset, 0, 0, -getConfig(SkeletonConfigValue.FOOT_LENGTH));
			break;
		}
	}

	protected void computeAllNodeOffsets() {
		for (SkeletonNodeOffset offset : SkeletonNodeOffset.values) {
			computeNodeOffset(offset);
		}
	}

	public void setConfigs(Map<SkeletonConfigValue, Float> configs, Map<SkeletonConfigToggle, Boolean> toggles) {
		if (configs != null) {
			this.configs.putAll(configs);
		}
		
		if (toggles != null) {
			this.toggles.putAll(toggles);
		}

		computeAllNodeOffsets();
	}

	public void setConfigs(SkeletonConfig skeletonConfig) {
		setConfigs(skeletonConfig.configs, skeletonConfig.toggles);
	}

	//#region Cast utilities for config reading
	private static Float castFloat(Object o) {
		if(o == null) {
			return null;
		} else if(o instanceof Float) {
			return (Float) o;
		} else if(o instanceof Double) {
			return ((Double) o).floatValue();
		} else if(o instanceof Byte) {
			return (float) (Byte) o;
		} else if(o instanceof Integer) {
			return (float) (Integer) o;
		} else if(o instanceof Long) {
			return (float) (Long) o;
		} else {
			return null;
		}
	}

	private static Boolean castBoolean(Object o) {
		if(o == null) {
			return null;
		} else if(o instanceof Boolean) {
			return (Boolean) o;
		} else {
			return null;
		}
	}
	//#endregion

	public void loadFromConfig(YamlFile config) {
		for (SkeletonConfigValue configValue : SkeletonConfigValue.values) {
			Float val = castFloat(config.getProperty(configValue.configKey));
			if (val != null) {
				configs.put(configValue, val);
			}
		}

		for (SkeletonConfigToggle configValue : SkeletonConfigToggle.values) {
			Boolean val = castBoolean(config.getProperty(configValue.configKey));
			if (val != null) {
				toggles.put(configValue, val);
			}
		}

		computeAllNodeOffsets();
	}

	public void saveToConfig(YamlFile config) {
		configs.forEach((key, value) -> {
			config.setProperty(key.configKey, value);
		});

		toggles.forEach((key, value) -> {
			config.setProperty(key.configKey, value);
		});
	}

	public void resetConfigs() {
		configs.clear();
		toggles.clear();
		computeAllNodeOffsets();
	}
}
