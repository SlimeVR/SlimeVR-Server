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

	public int minDataDistance = 1;
	public int maxDataDistance = 1;

	public int numEpochs = 5;

	public float initialAdjustRate = 1.0f;
	public float adjustRateDecay = 1.05f;

	public float slideErrorFactor = 1.0f;
	public float offsetErrorFactor = 0.0f;
	public float heightErrorFactor = 0.004f;

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
		//configs.put("Head", server.config.getFloat("body.headShift", HumanSkeletonWithWaist.HEAD_SHIFT_DEFAULT));
		configs.put("Neck", server.config.getFloat("body.neckLength", HumanSkeletonWithWaist.NECK_LENGTH_DEFAULT));
		configs.put("Waist", server.config.getFloat("body.waistDistance", 0.85f));

		if (server.config.getBoolean("autobone.forceChestTracker", false) ||
		TrackerUtils.findTrackerForBodyPosition(server.getAllTrackers(), TrackerBodyPosition.CHEST) != null) {
			// If force enabled or has a chest tracker
			configs.put("Chest", server.config.getFloat("body.chestDistance", 0.42f));
		} else {
			// Otherwise, make sure it's not used
			configs.remove("Chest");
		}

		// Load leg configs
		configs.put("Hips width", server.config.getFloat("body.hipsWidth", HumanSkeletonWithLegs.HIPS_WIDTH_DEFAULT));
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

	// This doesn't require a skeleton, therefore can be used if skeleton is null
	public void saveConfigs() {
		Float headOffset = configs.get("Head");
		if (headOffset != null) {
			server.config.setProperty("body.headShift", headOffset);
		}

		Float neckLength = configs.get("Neck");
		if (neckLength != null) {
			server.config.setProperty("body.neckLength", neckLength);
		}

		Float waistLength = configs.get("Waist");
		if (waistLength != null) {
			server.config.setProperty("body.waistDistance", waistLength);
		}

		Float chestDistance = configs.get("Chest");
		if (chestDistance != null) {
			server.config.setProperty("body.chestDistance", chestDistance);
		}

		Float hipsWidth = configs.get("Hips width");
		if (hipsWidth != null) {
			server.config.setProperty("body.hipsWidth", hipsWidth);
		}

		Float legsLength = configs.get("Legs length");
		if (legsLength != null) {
			server.config.setProperty("body.legsLength", legsLength);
		}

		Float kneeHeight = configs.get("Knee height");
		if (kneeHeight != null) {
			server.config.setProperty("body.kneeHeight", kneeHeight);
		}

		server.saveConfig();
	}

	public float getHeight(Map<String, Float> configs) {
		float height = 0f;

		for (String heightConfig : heightConfigs) {
			Float length = configs.get(heightConfig);
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

					float curHeight = getHeight(configs);
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

					for (Entry<String, Float> entry : configs.entrySet()) {
						// Skip adjustment if the epoch is before starting (for logging only)
						if (epoch < 0) {
							break;
						}

						float originalLength = entry.getValue();

						// Try positive and negative adjustments
						boolean isHeightVar = heightConfigs.contains(entry.getKey());
						float minError = error;
						float finalNewLength = -1f;
						for (int i = 0; i < 2; i++) {
							// Scale by the ratio for smooth adjustment and more stable results
							float curAdjustVal = (i == 0 ? adjustVal : -adjustVal) * originalLength;
							float newLength = originalLength + curAdjustVal;

							// No small or negative numbers!!! Bad algorithm!
							if (newLength < 0.01f) {
								continue;
							}

							updateSkeletonBoneLength(skeleton1, skeleton2, entry.getKey(), newLength);

							float newHeight = isHeightVar ? curHeight + curAdjustVal : curHeight;
							float newError = errorFunc(getErrorDeriv(skeleton1, skeleton2, targetHeight - newHeight));

							if (newError < minError) {
								minError = newError;
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

	protected float getErrorDeriv(SimpleSkeleton skeleton1, SimpleSkeleton skeleton2, float heightChange) {
		float slideLeft = skeleton1.getLeftFootPos().distance(skeleton2.getLeftFootPos());
		float slideRight = skeleton1.getRightFootPos().distance(skeleton2.getRightFootPos());

		// Averaged error
		float slideError = (slideLeft + slideRight) / 2f;

		float dist1 = skeleton1.getLeftFootPos().y - skeleton1.getRightFootPos().y;
		float dist2 = skeleton2.getLeftFootPos().y - skeleton2.getRightFootPos().y;

		// Averaged error
		float distError = (dist1 + dist2) / 2f;

		// Minimize sliding, minimize foot height offset, minimize change in total height
		return ((slideErrorFactor * Math.abs(slideError)) +
		(offsetErrorFactor * Math.abs(distError)) +
		(heightErrorFactor * Math.abs(heightChange))) /
		(slideErrorFactor + offsetErrorFactor + heightErrorFactor);
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
