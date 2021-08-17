package io.eiren.gui.autobone;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import io.eiren.util.ann.ThreadSafe;
import io.eiren.util.logging.LogManager;
import io.eiren.util.collections.FastList;
import io.eiren.vr.VRServer;
import io.eiren.vr.processor.HumanSkeleton;
import io.eiren.vr.processor.HumanSkeletonWithLegs;
import io.eiren.vr.processor.HumanSkeletonWithWaist;
import io.eiren.vr.processor.TrackerBodyPosition;
import io.eiren.vr.trackers.TrackerUtils;

public class AutoBone {

	public int cursorIncrement = 1;

	public int minDataDistance = 2;
	public int maxDataDistance = 32;

	public int numEpochs = 50;

	public float initialAdjustRate = 2.0f;
	public float adjustRateDecay = 1.01f;

	public float slideErrorFactor = 1.0f;
	public float offsetErrorFactor = 0.0f;
	public float heightErrorFactor = 0.05f;

	/*
	public float NECK_WAIST_RATIO_MIN = 0.2f;
	public float NECK_WAIST_RATIO_MAX = 0.3f;

	public float CHEST_WAIST_RATIO_MIN = 0.35f;
	public float CHEST_WAIST_RATIO_MAX = 0.6f;

	public float HIP_MIN = 0.08f;
	public float HIP_WAIST_RATIO_MAX = 0.4f;

	// Human average is 1.1235 (SD 0.07)
	public float LEG_WAIST_RATIO_MIN = 1.1235f - ((0.07f * 3f) + 0.05f);
	public float LEG_WAIST_RATIO_MAX = 1.1235f + ((0.07f * 3f) + 0.05f);

	public float KNEE_LEG_RATIO_MIN = 0.42f;
	public float KNEE_LEG_RATIO_MAX = 0.58f;
	*/

	protected final VRServer server;

	HumanSkeletonWithLegs skeleton = null;

	// This is filled by reloadConfigValues()
	public final HashMap<String, Float> configs = new HashMap<String, Float>();
	public final HashMap<String, Float> staticConfigs = new HashMap<String, Float>();

	public final FastList<String> heightConfigs = new FastList<String>(new String[] {
		"Neck",
		"Waist",
		"Legs length"
	});

	public AutoBone(VRServer server) {
		this.server = server;

		reloadConfigValues();

		server.addSkeletonUpdatedCallback(this::skeletonUpdated);
	}

	public void reloadConfigValues() {
		// Load waist configs
		staticConfigs.put("Head", server.config.getFloat("body.headShift", HumanSkeletonWithWaist.HEAD_SHIFT_DEFAULT));
		staticConfigs.put("Neck", server.config.getFloat("body.neckLength", HumanSkeletonWithWaist.NECK_LENGTH_DEFAULT));
		configs.put("Waist", server.config.getFloat("body.waistDistance", 0.85f));

		if (server.config.getBoolean("autobone.forceChestTracker", false) ||
		TrackerUtils.findTrackerForBodyPosition(server.getAllTrackers(), TrackerBodyPosition.CHEST) != null) {
			// If force enabled or has a chest tracker
			configs.put("Chest", server.config.getFloat("body.chestDistance", 0.42f));
		} else {
			// Otherwise, make sure it's not used
			configs.remove("Chest");
			staticConfigs.put("Chest", server.config.getFloat("body.chestDistance", 0.42f));
		}

		// Load leg configs
		staticConfigs.put("Hips width", server.config.getFloat("body.hipsWidth", HumanSkeletonWithLegs.HIPS_WIDTH_DEFAULT));
		configs.put("Legs length", server.config.getFloat("body.legsLength", 0.84f));
		configs.put("Knee height", server.config.getFloat("body.kneeHeight", 0.42f));
	}

	@ThreadSafe
	public void skeletonUpdated(HumanSkeleton newSkeleton) {
		if (newSkeleton instanceof HumanSkeletonWithLegs) {
			skeleton = (HumanSkeletonWithLegs)newSkeleton;
			applyConfigToSkeleton(newSkeleton);
			LogManager.log.info("[AutoBone] Received updated skeleton");
		}
	}

	public boolean applyConfigToSkeleton(HumanSkeleton skeleton) {
		if (skeleton == null) {
			return false;
		}

		for (Entry<String, Float> entry : configs.entrySet()) {
			skeleton.setSkeletonConfig(entry.getKey(), entry.getValue());
		}

		server.saveConfig();

		LogManager.log.info("[AutoBone] Configured skeleton bone lengths");
		return true;
	}

	private void setConfig(String name, String path) {
		Float value = configs.get(name);
		if (value != null) {
			server.config.setProperty(path, value);
		}
	}

	// This doesn't require a skeleton, therefore can be used if skeleton is null
	public void saveConfigs() {
		setConfig("Head", "body.headShift");
		setConfig("Neck", "body.neckLength");
		setConfig("Waist", "body.waistDistance");
		setConfig("Chest", "body.chestDistance");
		setConfig("Hips width", "body.hipsWidth");
		setConfig("Legs length", "body.legsLength");
		setConfig("Knee height", "body.kneeHeight");

		server.saveConfig();
	}

	public Float getConfig(String config) {
		Float configVal = configs.get(config);
		return configVal != null ? configVal : staticConfigs.get(config);
	}

	public Float getConfig(String config, Map<String, Float> configs, Map<String, Float> configsAlt) {
		if (configs == null) {
			throw new NullPointerException("Argument \"configs\" must not be null");
		}

		Float configVal = configs.get(config);
		return configVal != null || configsAlt == null ? configVal : configsAlt.get(config);
	}

	public float getHeight(Map<String, Float> configs) {
		return getHeight(configs, null);
	}

	public float getHeight(Map<String, Float> configs, Map<String, Float> configsAlt) {
		float height = 0f;

		for (String heightConfig : heightConfigs) {
			Float length = getConfig(heightConfig, configs, configsAlt);
			if (length != null) {
				height += length;
			}
		}

		return height;
	}

	public float getLengthSum(Map<String, Float> configs) {
		float length = 0f;

		for (float boneLength : configs.values()) {
			length += boneLength;
		}

		return length;
	}

	public float getMaxHmdHeight(PoseFrame[] frames) {
		float maxHeight = 0f;
		for (PoseFrame frame : frames) {
			if (frame.rootPos.y > maxHeight) {
				maxHeight = frame.rootPos.y;
			}
		}
		return maxHeight;
	}

	public void processFrames(PoseFrame[] frames) {
		processFrames(frames, -1f);
	}

	public void processFrames(PoseFrame[] frames, float targetHeight) {
		processFrames(frames, true, targetHeight);
	}

	public float processFrames(PoseFrame[] frames, boolean calcInitError, float targetHeight) {
		Set<Entry<String, Float>> configSet = configs.entrySet();

		SimpleSkeleton skeleton1 = new SimpleSkeleton(configSet);
		SimpleSkeleton skeleton2 = new SimpleSkeleton(configSet);

		// If target height isn't specified, auto-detect
		if (targetHeight < 0f) {
			if (skeleton != null) {
				targetHeight = getHeight(skeleton.getSkeletonConfig());
				LogManager.log.warning("[AutoBone] Target height loaded from skeleton (Make sure you reset before running!): " + targetHeight);
			} else {
				float hmdHeight = getMaxHmdHeight(frames);
				if (hmdHeight <= 0.50f) {
					LogManager.log.warning("[AutoBone] Max headset height detected (Value seems too low, did you not stand up straight while measuring?): " + hmdHeight);
				} else {
					LogManager.log.info("[AutoBone] Max headset height detected: " + hmdHeight);
				}

				// Estimate target height from HMD height
				targetHeight = hmdHeight;
			}
		}

		for (Entry<String, Float> entry : configSet) {
			entry.setValue(1f);
		}

		HashMap<String, Float> iterConfigs = new HashMap<String, Float>(configs);
		for (int epoch = calcInitError ? -1 : 0; epoch < numEpochs; epoch++) {
			float sumError = 0f;
			int errorCount = 0;

			float adjustRate = epoch >= 0 ? (float)(initialAdjustRate / Math.pow(adjustRateDecay, epoch)) : 0f;

			for (int cursorOffset = minDataDistance; cursorOffset <= maxDataDistance && cursorOffset < frames.length; cursorOffset++) {
				for (int frameCursor = 0; frameCursor < frames.length - cursorOffset; frameCursor += cursorIncrement) {
					PoseFrame frame1 = frames[frameCursor];
					PoseFrame frame2 = frames[frameCursor + cursorOffset];

					// If there's missing data, skip it
					if (frame1 == null || frame2 == null) {
						continue;
					}

					configSet = configs.entrySet();
					skeleton1.setSkeletonConfigs(configSet);
					skeleton2.setSkeletonConfigs(configSet);

					skeleton1.setPoseFromFrame(frame1);
					skeleton2.setPoseFromFrame(frame2);

					float totalLength = getLengthSum(configs);
					float curHeight = getHeight(configs, staticConfigs);
					float errorDeriv = getErrorDeriv(skeleton1, skeleton2, targetHeight - curHeight);
					float error = errorFunc(errorDeriv);

					// In case of fire
					if (Float.isNaN(error) || Float.isInfinite(error)) {
						// Extinguish
						LogManager.log.warning("[AutoBone] Error value is invalid, resetting variables to recover");
						reloadConfigValues();

						// Reset error sum values
						sumError = 0f;
						errorCount = 0;

						// Continue on new data
						continue;
					}

					// Store the error count for logging purposes
					sumError += errorDeriv;
					errorCount++;

					float adjustVal = error * adjustRate;

					iterConfigs.putAll(configs);
					for (Entry<String, Float> entry : configs.entrySet()) {
						// Skip adjustment if the epoch is before starting (for logging only)
						if (epoch < 0) {
							break;
						}

						float originalLength = entry.getValue();

						// Try positive and negative adjustments
						boolean isHeightVar = heightConfigs.contains(entry.getKey());
						float minError = errorDeriv;
						float finalNewLength = -1f;
						for (int i = 0; i < 2; i++) {
							// Scale by the ratio for smooth adjustment and more stable results
							float curAdjustVal = ((i == 0 ? adjustVal : -adjustVal) * originalLength) / totalLength;
							float newLength = originalLength + curAdjustVal;

							// No small or negative numbers!!! Bad algorithm!
							if (newLength < 0.01f) {
								continue;
							}

							updateSkeletonBoneLength(skeleton1, skeleton2, entry.getKey(), newLength);

							float newHeight = isHeightVar ? curHeight + curAdjustVal : curHeight;
							float newErrorDeriv = getErrorDeriv(skeleton1, skeleton2, targetHeight - newHeight);

							if (newErrorDeriv < minError) {
								minError = newErrorDeriv;
								finalNewLength = newLength;
							}
						}

						if (finalNewLength > 0f) {
							entry.setValue(finalNewLength);
						}

						// Reset the length to minimize bias in other variables, it's applied later
						updateSkeletonBoneLength(skeleton1, skeleton2, entry.getKey(), originalLength);
					}
				}
			}

			// Calculate average error over the epoch
			float avgError = errorCount > 0 ? sumError / errorCount : -1f;
			LogManager.log.info("[AutoBone] Epoch " + (epoch + 1) + " average error: " + avgError);
		}

		float finalHeight = getHeight(configs);
		LogManager.log.info("[AutoBone] Target height: " + targetHeight + " New height: " + finalHeight);

		LogManager.log.info("[AutoBone] Done! Applying to skeleton...");
		if (!applyConfigToSkeleton(skeleton)) {
			LogManager.log.info("[AutoBone] Applying to skeleton failed, only saving configs...");
			saveConfigs();
		}

		return Math.abs(finalHeight - targetHeight);
	}

	protected float getSlideErrorDeriv(SimpleSkeleton skeleton1, SimpleSkeleton skeleton2) {
		float slideLeft = skeleton1.getLeftFootPos().distance(skeleton2.getLeftFootPos());
		float slideRight = skeleton1.getRightFootPos().distance(skeleton2.getRightFootPos());

		// Divide by 4 to halve and average, it's halved because you want to approach a midpoint, not the other point
		return (slideLeft + slideRight) / 4f;
	}

	protected float getOffsetErrorDeriv(SimpleSkeleton skeleton1, SimpleSkeleton skeleton2) {
		float dist1 = Math.abs(skeleton1.getLeftFootPos().y - skeleton1.getRightFootPos().y);
		float dist2 = Math.abs(skeleton2.getLeftFootPos().y - skeleton2.getRightFootPos().y);

		float dist3 = Math.abs(skeleton1.getLeftFootPos().y - skeleton2.getRightFootPos().y);
		float dist4 = Math.abs(skeleton1.getLeftFootPos().y - skeleton2.getRightFootPos().y);

		float dist5 = Math.abs(skeleton1.getLeftFootPos().y - skeleton2.getLeftFootPos().y);
		float dist6 = Math.abs(skeleton1.getRightFootPos().y - skeleton2.getRightFootPos().y);

		// Divide by 12 to halve and average, it's halved because you want to approach a midpoint, not the other point
		return (dist1 + dist2 + dist3 + dist4 + dist5 + dist6) / 12f;
	}

	protected float getErrorDeriv(SimpleSkeleton skeleton1, SimpleSkeleton skeleton2, float heightChange) {
		float totalError = 0f;
		float sumWeight = 0f;

		if (slideErrorFactor > 0f) {
			totalError += getSlideErrorDeriv(skeleton1, skeleton2) * slideErrorFactor;
			sumWeight += slideErrorFactor;
		}

		if (offsetErrorFactor > 0f) {
			totalError += getOffsetErrorDeriv(skeleton1, skeleton2) * offsetErrorFactor;
			sumWeight += offsetErrorFactor;
		}

		if (heightErrorFactor > 0f) {
			totalError += Math.abs(heightChange) * heightErrorFactor;
			sumWeight += heightErrorFactor;
		}

		// Minimize sliding, minimize foot height offset, minimize change in total height
		return sumWeight > 0f ? totalError / sumWeight : 0f;
	}

	// Mean square error function
	protected static float errorFunc(float errorDeriv) {
		return 0.5f * (errorDeriv * errorDeriv);
	}

	protected void updateSkeletonBoneLength(SimpleSkeleton skeleton1, SimpleSkeleton skeleton2, String joint, float newLength) {
		skeleton1.setSkeletonConfig(joint, newLength, true);
		skeleton2.setSkeletonConfig(joint, newLength, true);
	}
}
