package dev.slimevr.vr.processor.skeleton;

import com.jme3.math.Vector3f;
import dev.slimevr.Main;
import dev.slimevr.config.ConfigManager;
import io.eiren.util.logging.LogManager;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;


public class SkeletonConfig {

	protected final EnumMap<SkeletonConfigOffsets, Float> configOffsets = new EnumMap<>(
		SkeletonConfigOffsets.class
	);
	protected final EnumMap<SkeletonConfigToggles, Boolean> configToggles = new EnumMap<>(
		SkeletonConfigToggles.class
	);
	protected final EnumMap<SkeletonConfigValues, Float> configValues = new EnumMap<>(
		SkeletonConfigValues.class
	);

	protected boolean[] changedToggles = new boolean[SkeletonConfigToggles.values.length];
	protected boolean[] changedValues = new boolean[SkeletonConfigValues.values.length];

	protected final EnumMap<BoneType, Vector3f> nodeOffsets = new EnumMap<>(
		BoneType.class
	);

	protected final boolean autoUpdateOffsets;
	protected final SkeletonConfigCallback callback;
	protected Skeleton skeleton;
	protected float userHeight;

	public SkeletonConfig(boolean autoUpdateOffsets) {
		this.autoUpdateOffsets = autoUpdateOffsets;
		this.callback = null;

		callCallbackOnAll(true);

		if (autoUpdateOffsets) {
			computeAllNodeOffsets();
		}
	}

	public SkeletonConfig(
		boolean autoUpdateOffsets,
		SkeletonConfigCallback callback,
		Skeleton skeleton
	) {
		this.autoUpdateOffsets = autoUpdateOffsets;
		this.callback = callback;
		this.skeleton = skeleton;

		callCallbackOnAll(true);

		if (autoUpdateOffsets) {
			computeAllNodeOffsets();
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

	public void setOffset(
		SkeletonConfigOffsets config,
		Float newValue,
		boolean computeOffsets
	) {
		if (newValue != null) {
			configOffsets.put(config, newValue);
		} else {
			configOffsets.remove(config);
		}

		// Re-compute the affected offsets
		if (computeOffsets && autoUpdateOffsets && config.affectedOffsets != null) {
			for (BoneType offset : config.affectedOffsets) {
				computeNodeOffset(offset);
			}
		}

		// Calls callback
		if (callback != null) {
			try {
				callback
					.updateOffsetsState(config, newValue != null ? newValue : config.defaultValue);
			} catch (Exception e) {
				LogManager.severe("[SkeletonConfig] Exception while calling callback", e);
			}
		}

		// Re-calculate user height
		userHeight = getOffset(SkeletonConfigOffsets.NECK)
			+ getOffset(SkeletonConfigOffsets.CHEST)
			+ getOffset(SkeletonConfigOffsets.WAIST)
			+ getOffset(SkeletonConfigOffsets.HIP)
			+ getOffset(SkeletonConfigOffsets.UPPER_LEG)
			+ getOffset(SkeletonConfigOffsets.LOWER_LEG);
	}

	public void setOffset(SkeletonConfigOffsets config, Float newValue) {
		setOffset(config, newValue, true);
	}

	public float getOffset(SkeletonConfigOffsets config) {
		if (config == null) {
			return 0f;
		}

		Float val = configOffsets.get(config);
		return val != null ? val : config.defaultValue;
	}

	public float getUserHeightFromOffsets() {
		return userHeight;
	}

	public void setToggle(SkeletonConfigToggles config, Boolean newValue) {
		if (newValue != null) {
			if (configToggles.get(config) != null && (newValue != configToggles.get(config))) {
				changedToggles[config.id - 1] = true;
			}
			configToggles.put(config, newValue);
		} else {
			configToggles.remove(config);
		}

		if (callback != null) {
			try {
				callback
					.updateTogglesState(config, newValue != null ? newValue : config.defaultValue);
			} catch (Exception e) {
				LogManager.severe("[SkeletonConfig] Exception while calling callback", e);
			}
		}

		if (config == SkeletonConfigToggles.I_POSE) {
			computeNodeOffset(BoneType.LEFT_UPPER_ARM);
			computeNodeOffset(BoneType.RIGHT_UPPER_ARM);
			computeNodeOffset(BoneType.LEFT_LOWER_ARM);
			computeNodeOffset(BoneType.RIGHT_LOWER_ARM);
			computeNodeOffset(BoneType.LEFT_ELBOW_TRACKER);
			computeNodeOffset(BoneType.RIGHT_ELBOW_TRACKER);
		}
	}

	public boolean getToggle(SkeletonConfigToggles config) {
		if (config == null) {
			return false;
		}

		Boolean val = configToggles.get(config);
		return val != null ? val : config.defaultValue;
	}

	public void setValue(SkeletonConfigValues config, Float newValue) {
		if (newValue != null) {
			if (configValues.get(config) != null && (!newValue.equals(configValues.get(config)))) {
				changedValues[config.id - 1] = true;
			}
			configValues.put(config, newValue);
		} else {
			configValues.remove(config);
		}

		if (callback != null) {
			try {
				callback
					.updateValuesState(config, newValue != null ? newValue : config.defaultValue);
			} catch (Exception e) {
				LogManager.severe("[SkeletonConfig] Exception while calling callback", e);
			}
		}
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
			case HEAD -> setNodeOffset(nodeOffset, 0, 0, getOffset(SkeletonConfigOffsets.HEAD));
			case NECK -> setNodeOffset(nodeOffset, 0, -getOffset(SkeletonConfigOffsets.NECK), 0);
			case CHEST -> setNodeOffset(nodeOffset, 0, -getOffset(SkeletonConfigOffsets.CHEST), 0);
			case CHEST_TRACKER -> setNodeOffset(
				nodeOffset,
				0,
				getOffset(SkeletonConfigOffsets.CHEST_OFFSET)
					- getOffset(SkeletonConfigOffsets.CHEST),
				-getOffset(SkeletonConfigOffsets.SKELETON_OFFSET)
			);
			case WAIST -> setNodeOffset(nodeOffset, 0, -getOffset(SkeletonConfigOffsets.WAIST), 0);
			case HIP -> setNodeOffset(nodeOffset, 0, -getOffset(SkeletonConfigOffsets.HIP), 0);
			case HIP_TRACKER -> setNodeOffset(
				nodeOffset,
				0,
				getOffset(SkeletonConfigOffsets.HIP_OFFSET),
				-getOffset(SkeletonConfigOffsets.SKELETON_OFFSET)
			);
			case LEFT_HIP -> setNodeOffset(
				nodeOffset,
				-getOffset(SkeletonConfigOffsets.HIPS_WIDTH) / 2f,
				0,
				0
			);
			case RIGHT_HIP -> setNodeOffset(
				nodeOffset,
				getOffset(SkeletonConfigOffsets.HIPS_WIDTH) / 2f,
				0,
				0
			);
			case LEFT_UPPER_LEG, RIGHT_UPPER_LEG -> setNodeOffset(
				nodeOffset,
				0,
				-getOffset(SkeletonConfigOffsets.UPPER_LEG),
				0
			);
			case LEFT_KNEE_TRACKER, RIGHT_KNEE_TRACKER -> setNodeOffset(
				nodeOffset,
				0,
				0,
				-getOffset(SkeletonConfigOffsets.SKELETON_OFFSET)
			);
			case LEFT_LOWER_LEG, RIGHT_LOWER_LEG -> setNodeOffset(
				nodeOffset,
				0,
				-getOffset(SkeletonConfigOffsets.LOWER_LEG),
				-getOffset(SkeletonConfigOffsets.FOOT_SHIFT)
			);
			case LEFT_FOOT, RIGHT_FOOT -> setNodeOffset(
				nodeOffset,
				0,
				0,
				-getOffset(SkeletonConfigOffsets.FOOT_LENGTH)
			);
			case LEFT_FOOT_TRACKER, RIGHT_FOOT_TRACKER -> setNodeOffset(
				nodeOffset,
				0,
				0,
				-getOffset(SkeletonConfigOffsets.SKELETON_OFFSET)
			);
			case LEFT_SHOULDER -> setNodeOffset(
				nodeOffset,
				-getOffset(SkeletonConfigOffsets.SHOULDERS_WIDTH) / 2f,
				-getOffset(SkeletonConfigOffsets.SHOULDERS_DISTANCE),
				0
			);
			case RIGHT_SHOULDER -> setNodeOffset(
				nodeOffset,
				getOffset(SkeletonConfigOffsets.SHOULDERS_WIDTH) / 2f,
				-getOffset(SkeletonConfigOffsets.SHOULDERS_DISTANCE),
				0
			);
			case LEFT_UPPER_ARM -> {
				if (getToggle(SkeletonConfigToggles.I_POSE)) {
					setNodeOffset(
						nodeOffset,
						0,
						-getOffset(SkeletonConfigOffsets.UPPER_ARM),
						0
					);
				} else {
					setNodeOffset(
						nodeOffset,
						-getOffset(SkeletonConfigOffsets.UPPER_ARM),
						0,
						0
					);
				}
			}
			case RIGHT_UPPER_ARM -> {
				if (getToggle(SkeletonConfigToggles.I_POSE)) {
					setNodeOffset(
						nodeOffset,
						0,
						-getOffset(SkeletonConfigOffsets.UPPER_ARM),
						0
					);
				} else {
					setNodeOffset(
						nodeOffset,
						getOffset(SkeletonConfigOffsets.UPPER_ARM),
						0,
						0
					);
				}
			}
			case LEFT_LOWER_ARM -> {
				if (getToggle(SkeletonConfigToggles.I_POSE)) {
					setNodeOffset(
						nodeOffset,
						0,
						-getOffset(SkeletonConfigOffsets.LOWER_ARM),
						0
					);
				} else {
					setNodeOffset(
						nodeOffset,
						-getOffset(SkeletonConfigOffsets.LOWER_ARM),
						0,
						0
					);
				}
			}
			case RIGHT_LOWER_ARM -> {
				if (getToggle(SkeletonConfigToggles.I_POSE)) {
					setNodeOffset(
						nodeOffset,
						0,
						-getOffset(SkeletonConfigOffsets.LOWER_ARM),
						0
					);
				} else {
					setNodeOffset(
						nodeOffset,
						getOffset(SkeletonConfigOffsets.LOWER_ARM),
						0,
						0
					);
				}
			}
			case LEFT_HAND, RIGHT_HAND -> setNodeOffset(
				nodeOffset,
				0,
				-getOffset(SkeletonConfigOffsets.CONTROLLER_Y),
				-getOffset(SkeletonConfigOffsets.CONTROLLER_Z)
			);
			case LEFT_CONTROLLER, RIGHT_CONTROLLER -> setNodeOffset(
				nodeOffset,
				0,
				getOffset(SkeletonConfigOffsets.CONTROLLER_Y),
				getOffset(SkeletonConfigOffsets.CONTROLLER_Z)
			);
			case LEFT_ELBOW_TRACKER -> {
				if (getToggle(SkeletonConfigToggles.I_POSE)) {
					setNodeOffset(
						nodeOffset,
						0,
						-getOffset(SkeletonConfigOffsets.ELBOW_OFFSET),
						0
					);
				} else {
					setNodeOffset(
						nodeOffset,
						-getOffset(SkeletonConfigOffsets.ELBOW_OFFSET),
						0,
						0
					);
				}
			}
			case RIGHT_ELBOW_TRACKER -> {
				if (getToggle(SkeletonConfigToggles.I_POSE)) {
					setNodeOffset(
						nodeOffset,
						0,
						-getOffset(SkeletonConfigOffsets.ELBOW_OFFSET),
						0
					);
				} else {
					setNodeOffset(
						nodeOffset,
						getOffset(SkeletonConfigOffsets.ELBOW_OFFSET),
						0,
						0
					);
				}
			}
		}
	}

	public void computeAllNodeOffsets() {
		for (BoneType offset : BoneType.values) {
			computeNodeOffset(offset);
		}
	}

	public void setOffsets(
		Map<SkeletonConfigOffsets, Float> configOffsets,
		boolean computeOffsets
	) {
		if (configOffsets != null) {
			configOffsets.forEach((key, value) -> {
				// Do not recalculate the offsets, these are done in bulk at the
				// end
				setOffset(key, value, false);
			});
		}

		if (computeOffsets && autoUpdateOffsets) {
			computeAllNodeOffsets();
		}
	}

	public void setOffsets(
		Map<SkeletonConfigOffsets, Float> configOffsets
	) {
		setOffsets(configOffsets, true);
	}

	public void setOffsets(SkeletonConfig skeletonConfig) {
		// Don't recalculate node offsets, just re-use them from skeletonConfig
		setOffsets(
			skeletonConfig.configOffsets,
			false
		);

		// Copy skeletonConfig's nodeOffsets as the configs are all the same
		skeletonConfig.nodeOffsets.forEach((key, value) -> {
			setNodeOffset(key, value.x, value.y, value.z);
		});
	}

	public void resetOffsets() {
		if (skeleton != null) {
			for (SkeletonConfigOffsets config : SkeletonConfigOffsets.values) {
				skeleton.resetSkeletonConfig(config);
			}
		} else {
			configOffsets.clear();
			if (autoUpdateOffsets) {
				computeAllNodeOffsets();
			}
		}

		// Calls offset callback
		if (callback != null) {
			for (SkeletonConfigOffsets config : SkeletonConfigOffsets.values) {
				try {
					callback
						.updateOffsetsState(config, config.defaultValue);
				} catch (Exception e) {
					LogManager.severe("[SkeletonConfig] Exception while calling callback", e);
				}
			}
		}
	}

	public void resetValues() {
		configValues.clear();

		// Calls values callback
		if (callback != null) {
			for (SkeletonConfigValues config : SkeletonConfigValues.values) {
				try {
					callback
						.updateValuesState(config, config.defaultValue);
				} catch (Exception e) {
					LogManager.severe("[SkeletonConfig] Exception while calling callback", e);
				}
			}
		}

		// Remove from config to use default if they change in the future.
		Arrays.fill(changedValues, false);
		for (SkeletonConfigValues value : SkeletonConfigValues.values) {
			Main
				.getVrServer()
				.getConfigManager()
				.getVrConfig()
				.getSkeleton()
				.getValues()
				.remove(value.configKey);
		}
	}

	public void resetToggles() {
		configToggles.clear();

		// Calls toggles callback
		if (callback != null) {
			for (SkeletonConfigOffsets config : SkeletonConfigOffsets.values) {
				try {
					callback
						.updateOffsetsState(config, config.defaultValue);
				} catch (Exception e) {
					LogManager.severe("[SkeletonConfig] Exception while calling callback", e);
				}
			}
		}

		// Remove from config to use default if they change in the future.
		Arrays.fill(changedToggles, false);
		for (SkeletonConfigToggles value : SkeletonConfigToggles.values) {
			Main
				.getVrServer()
				.getConfigManager()
				.getVrConfig()
				.getSkeleton()
				.getToggles()
				.remove(value.configKey);
		}
	}

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
		dev.slimevr.config.SkeletonConfig skeletonConfig = Main
			.getVrServer()
			.getConfigManager()
			.getVrConfig()
			.getSkeleton();

		// Write all possible values to keep consistent even if defaults changed
		for (SkeletonConfigOffsets value : SkeletonConfigOffsets.values) {
			skeletonConfig.getOffsets().put(value.configKey, getOffset(value));
		}

		// Only write changed values to keep using defaults if not changed
		for (SkeletonConfigToggles value : SkeletonConfigToggles.values) {
			if (changedToggles[value.id - 1])
				skeletonConfig.getToggles().put(value.configKey, getToggle(value));
		}

		// Only write changed values to keep using defaults if not changed
		for (SkeletonConfigValues value : SkeletonConfigValues.values) {
			if (changedValues[value.id - 1])
				skeletonConfig.getValues().put(value.configKey, getValue(value));
		}
	}
}
