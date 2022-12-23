package dev.slimevr.vr.processor;

import com.jme3.math.Vector3f;
import dev.slimevr.Main;
import dev.slimevr.config.ConfigManager;
import dev.slimevr.vr.processor.skeleton.SkeletonConfigOffsets;
import dev.slimevr.vr.processor.skeletonParts.BoneType;
import dev.slimevr.vr.processor.skeletonParts.SkeletonConfigToggles;
import dev.slimevr.vr.processor.skeletonParts.SkeletonConfigValues;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;


public class SkeletonConfigManager {

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
	protected HumanPoseManager humanPoseManager;
	protected float userHeight;
	static final float FLOOR_OFFSET = 0.05f;

	public SkeletonConfigManager(boolean autoUpdateOffsets) {
		this.autoUpdateOffsets = autoUpdateOffsets;

		updateSettingsInSkeleton();

		if (autoUpdateOffsets) {
			computeAllNodeOffsets();
		}
	}

	public SkeletonConfigManager(
		boolean autoUpdateOffsets,
		HumanPoseManager humanPoseManager
	) {
		this.autoUpdateOffsets = autoUpdateOffsets;
		this.humanPoseManager = humanPoseManager;

		updateSettingsInSkeleton();

		if (autoUpdateOffsets) {
			computeAllNodeOffsets();
		}
	}

	public void updateSettingsInSkeleton() {
		if (humanPoseManager == null)
			return;

		for (SkeletonConfigToggles config : SkeletonConfigToggles.values) {
			Boolean val = configToggles.get(config);
			humanPoseManager.updateToggleState(config, val == null ? config.defaultValue : val);
		}

		for (SkeletonConfigValues config : SkeletonConfigValues.values) {
			Float val = configValues.get(config);
			humanPoseManager.updateValueState(config, val == null ? config.defaultValue : val);
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

		// Re-calculate user height
		userHeight = getOffset(SkeletonConfigOffsets.NECK)
			+ getOffset(SkeletonConfigOffsets.TORSO)
			+ getOffset(SkeletonConfigOffsets.LEGS_LENGTH);
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

		// Updates in skeleton
		if (humanPoseManager != null) {
			humanPoseManager
				.updateToggleState(config, newValue != null ? newValue : config.defaultValue);
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

		// Updates in skeleton
		if (humanPoseManager != null) {
			humanPoseManager
				.updateValueState(config, newValue != null ? newValue : config.defaultValue);
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

		// Updates in skeleton
		if (humanPoseManager != null) {
			humanPoseManager.updateNodeOffset(nodeOffset, offset);
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
				0,
				-getOffset(SkeletonConfigOffsets.SKELETON_OFFSET)
			);
			case WAIST -> setNodeOffset(
				nodeOffset,
				0,
				(getOffset(SkeletonConfigOffsets.CHEST)
					- getOffset(SkeletonConfigOffsets.TORSO)
					+ getOffset(SkeletonConfigOffsets.WAIST)),
				0
			);
			case HIP -> setNodeOffset(nodeOffset, 0, -getOffset(SkeletonConfigOffsets.WAIST), 0);
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
				-(getOffset(SkeletonConfigOffsets.LEGS_LENGTH)
					- getOffset(SkeletonConfigOffsets.KNEE_HEIGHT)),
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
				-getOffset(SkeletonConfigOffsets.KNEE_HEIGHT),
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
			case LEFT_CONTROLLER, RIGHT_CONTROLLER -> setNodeOffset(
				nodeOffset,
				0,
				getOffset(SkeletonConfigOffsets.CONTROLLER_DISTANCE_Y),
				getOffset(SkeletonConfigOffsets.CONTROLLER_DISTANCE_Z)
			);
			case LEFT_HAND, RIGHT_HAND -> setNodeOffset(
				nodeOffset,
				0,
				-getOffset(SkeletonConfigOffsets.CONTROLLER_DISTANCE_Y),
				-getOffset(SkeletonConfigOffsets.CONTROLLER_DISTANCE_Z)
			);
			case LEFT_LOWER_ARM, RIGHT_LOWER_ARM -> setNodeOffset(
				nodeOffset,
				0,
				getOffset(SkeletonConfigOffsets.LOWER_ARM_LENGTH),
				0
			);
			case LEFT_ELBOW_TRACKER, RIGHT_ELBOW_TRACKER -> setNodeOffset(
				nodeOffset,
				0,
				getOffset(SkeletonConfigOffsets.ELBOW_OFFSET),
				0
			);
			case LEFT_UPPER_ARM, RIGHT_UPPER_ARM -> setNodeOffset(
				nodeOffset,
				0,
				-getOffset(SkeletonConfigOffsets.UPPER_ARM_LENGTH),
				0
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

	public void setOffsets(SkeletonConfigManager skeletonConfigManager) {
		// Don't recalculate node offsets, just re-use them from skeletonConfig
		setOffsets(
			skeletonConfigManager.configOffsets,
			false
		);

		// Copy skeletonConfig's nodeOffsets as the configs are all the same
		skeletonConfigManager.nodeOffsets.forEach((key, value) -> {
			setNodeOffset(key, value.x, value.y, value.z);
		});
	}

	public void resetOffsets() {
		if (humanPoseManager != null) {
			for (SkeletonConfigOffsets config : SkeletonConfigOffsets.values) {
				resetOffset(config);
			}
		} else {
			configOffsets.clear();
			if (autoUpdateOffsets) {
				computeAllNodeOffsets();
			}
		}
	}

	public void resetToggles() {
		configToggles.clear();

		// Remove from config to use default if they change in the future.
		Arrays.fill(changedToggles, false);
		for (SkeletonConfigToggles value : SkeletonConfigToggles.values) {
			Main.vrServer
				.getConfigManager()
				.getVrConfig()
				.getSkeleton()
				.getToggles()
				.remove(value.configKey);
			// Set default in skeleton
			humanPoseManager.setToggle(value, value.defaultValue);
		}
	}

	public void resetValues() {
		configValues.clear();

		// Updates in skeleton
		if (humanPoseManager != null) {
			for (SkeletonConfigValues config : SkeletonConfigValues.values) {
				humanPoseManager.updateValueState(config, config.defaultValue);
			}
		}

		// Remove from config to use default if they change in the future.
		Arrays.fill(changedValues, false);
		for (SkeletonConfigValues value : SkeletonConfigValues.values) {
			Main.vrServer
				.getConfigManager()
				.getVrConfig()
				.getSkeleton()
				.getValues()
				.remove(value.configKey);
			// Set default in skeleton
			humanPoseManager.setValue(value, value.defaultValue);
		}
	}

	public void resetAllConfigs() {
		resetOffsets();
		resetToggles();
		resetValues();
	}

	public void resetOffset(SkeletonConfigOffsets config) {
		if (config == null)
			return;

		switch (config) {
			case HEAD -> humanPoseManager.setOffset(SkeletonConfigOffsets.HEAD, null);
			case NECK -> humanPoseManager.setOffset(SkeletonConfigOffsets.NECK, null);
			case TORSO -> {
				// Distance from shoulders to hip (full torso length)
				float height = humanPoseManager.getHmdHeight();
				if (height > 0.5f) { // Reset only if floor level seems right,
					humanPoseManager
						.setOffset(
							SkeletonConfigOffsets.TORSO,
							((height) * 0.42f)
								- humanPoseManager.getOffset(SkeletonConfigOffsets.NECK)
						);
				} else // if floor level is incorrect
				{
					humanPoseManager.setOffset(SkeletonConfigOffsets.TORSO, null);
				}
			}
			case CHEST ->
				// Chest is 57% of the upper body by default
				// (shoulders to chest)
				humanPoseManager
					.setOffset(
						SkeletonConfigOffsets.CHEST,
						humanPoseManager.getOffset(SkeletonConfigOffsets.TORSO) * 0.57f
					);
			case WAIST -> // Waist length is from hip to waist
				humanPoseManager.setOffset(SkeletonConfigOffsets.WAIST, null);
			case HIP_OFFSET -> humanPoseManager
				.setOffset(SkeletonConfigOffsets.HIP_OFFSET, null);
			case HIPS_WIDTH -> humanPoseManager
				.setOffset(SkeletonConfigOffsets.HIPS_WIDTH, null);
			case FOOT_LENGTH -> humanPoseManager
				.setOffset(SkeletonConfigOffsets.FOOT_LENGTH, null);
			case FOOT_SHIFT -> humanPoseManager
				.setOffset(SkeletonConfigOffsets.FOOT_SHIFT, null);
			case SKELETON_OFFSET -> humanPoseManager
				.setOffset(SkeletonConfigOffsets.SKELETON_OFFSET, null);
			case LEGS_LENGTH -> {
				// Set legs length to be 5cm above floor level
				float height = humanPoseManager.getHmdHeight();
				if (height > 0.5f) { // Reset only if floor level seems right,
					humanPoseManager
						.setOffset(
							SkeletonConfigOffsets.LEGS_LENGTH,
							height
								- humanPoseManager.getOffset(SkeletonConfigOffsets.NECK)
								- humanPoseManager.getOffset(SkeletonConfigOffsets.TORSO)
								- FLOOR_OFFSET
						);
				} else // if floor level is incorrect
				{
					humanPoseManager.setOffset(SkeletonConfigOffsets.LEGS_LENGTH, null);
				}
				resetOffset(SkeletonConfigOffsets.KNEE_HEIGHT);
			}
			case KNEE_HEIGHT -> // Knees are at 55% of the legs by default
				humanPoseManager
					.setOffset(
						SkeletonConfigOffsets.KNEE_HEIGHT,
						humanPoseManager.getOffset(SkeletonConfigOffsets.LEGS_LENGTH) * 0.55f
					);
			case CONTROLLER_DISTANCE_Z -> humanPoseManager
				.setOffset(SkeletonConfigOffsets.CONTROLLER_DISTANCE_Z, null);
			case CONTROLLER_DISTANCE_Y -> humanPoseManager
				.setOffset(SkeletonConfigOffsets.CONTROLLER_DISTANCE_Y, null);
			case LOWER_ARM_LENGTH -> humanPoseManager
				.setOffset(SkeletonConfigOffsets.LOWER_ARM_LENGTH, null);
			case ELBOW_OFFSET -> humanPoseManager
				.setOffset(SkeletonConfigOffsets.ELBOW_OFFSET, null);
			case SHOULDERS_DISTANCE -> humanPoseManager
				.setOffset(SkeletonConfigOffsets.SHOULDERS_DISTANCE, null);
			case SHOULDERS_WIDTH -> humanPoseManager
				.setOffset(SkeletonConfigOffsets.SHOULDERS_WIDTH, null);
			case UPPER_ARM_LENGTH -> humanPoseManager
				.setOffset(SkeletonConfigOffsets.UPPER_ARM_LENGTH, null);
		}
	}

	public void loadFromConfig(ConfigManager configManager) {
		// Load offsets
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

		// Load toggles
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

		// Load values
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

		// Updates all offsets
		if (autoUpdateOffsets) {
			computeAllNodeOffsets();
		}
	}

	public void save() {
		dev.slimevr.config.SkeletonConfig skeletonConfig = Main.vrServer
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
