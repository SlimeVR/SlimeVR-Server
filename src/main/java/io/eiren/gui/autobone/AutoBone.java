package io.eiren.gui.autobone;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import io.eiren.util.ann.ThreadSafe;
import io.eiren.util.ann.VRServerThread;
import io.eiren.util.logging.LogManager;
import io.eiren.util.StringUtils;
import io.eiren.util.collections.FastList;
import io.eiren.vr.VRServer;
import io.eiren.vr.processor.HumanSkeleton;
import io.eiren.vr.processor.HumanSkeletonWithLegs;
import io.eiren.vr.processor.HumanSkeletonWithWaist;

public class AutoBone {

	public final static int MIN_DATA_DISTANCE = 1;
	public final static int MAX_DATA_DISTANCE = 1;

	public final static int NUM_EPOCHS = 50;

	public final static float INITIAL_ADJUSTMENT_RATE = 1f;
	public final static float ADJUSTMENT_RATE_DECAY = 1.1f;

	protected final static float SLIDE_ERROR_FACTOR = 1.0f;
	protected final static float OFFSET_ERROR_FACTOR = 0.0f;
	protected final static float HEIGHT_ERROR_FACTOR = 0.0f;

	protected final static float HEADSET_HEIGHT_RATIO = 1.0f;

	protected final static float NECK_WAIST_RATIO_MIN = 0.2f;
	protected final static float NECK_WAIST_RATIO_MAX = 0.3f;

	protected final static float CHEST_WAIST_RATIO_MIN = 0.35f;
	protected final static float CHEST_WAIST_RATIO_MAX = 0.6f;

	protected final static float HIP_MIN = 0.08f;
	protected final static float HIP_WAIST_RATIO_MAX = 0.4f;

	// Human average is 1.1235 (SD 0.07)
	protected final static float LEG_WAIST_RATIO_MIN = 1.1235f - ((0.07f * 3f) + 0.05f);
	protected final static float LEG_WAIST_RATIO_MAX = 1.1235f + ((0.07f * 3f) + 0.05f);

	protected final static float KNEE_LEG_RATIO_MIN = 0.42f;
	protected final static float KNEE_LEG_RATIO_MAX = 0.58f;

	protected final VRServer server;

	HumanSkeletonWithLegs skeleton = null;

	protected PoseFrame[] frames = new PoseFrame[0];
	protected int frameRecordingCursor = -1;

	protected long frameRecordingInterval = 60L;
	protected long nextFrameTimeMs = -1L;

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
		server.addOnTick(this::onTick);
	}

	public void reloadConfigValues() {
		// Load waist configs
		configs.put("Head", server.config.getFloat("body.headShift", HumanSkeletonWithWaist.HEAD_SHIFT_DEFAULT));
		configs.put("Neck", server.config.getFloat("body.neckLength", HumanSkeletonWithWaist.NECK_LENGTH_DEFAULT));
		configs.put("Waist", server.config.getFloat("body.waistDistance", 0.85f));
		configs.put("Chest", server.config.getFloat("body.chestDistance", 0.42f));

		// Load leg configs
		configs.put("Hips width", server.config.getFloat("body.hipsWidth", HumanSkeletonWithLegs.HIPS_WIDTH_DEFAULT));
		configs.put("Legs length", server.config.getFloat("body.legsLength", 0.84f));
		configs.put("Knee height", server.config.getFloat("body.kneeHeight", 0.42f));
	}

	public void setSkeletonLengths(SimpleSkeleton skeleton) {
		for (Entry<String, Float> entry : configs.entrySet()) {
			skeleton.setSkeletonConfig(entry.getKey(), entry.getValue());
		}
	}

	@ThreadSafe
	public void skeletonUpdated(HumanSkeleton newSkeleton) {
		java.awt.EventQueue.invokeLater(() -> {
			if (newSkeleton instanceof HumanSkeletonWithLegs) {
				skeleton = (HumanSkeletonWithLegs)newSkeleton;
				applyConfigToSkeleton(newSkeleton);
				LogManager.log.info("[AutoBone] Received updated skeleton");
			}
		});
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

	@VRServerThread
	public void onTick() {
		if (frameRecordingCursor >= 0 && frameRecordingCursor < frames.length && skeleton != null && System.currentTimeMillis() >= nextFrameTimeMs) {
			nextFrameTimeMs = System.currentTimeMillis() + frameRecordingInterval;

			PoseFrame frame = new PoseFrame(skeleton);
			frames[frameRecordingCursor++] = frame;

			LogManager.log.info("Recorded frame " + frameRecordingCursor);
		}
	}

	public void startFrameRecording(int numFrames, long interval) {
		frames = new PoseFrame[numFrames];

		frameRecordingInterval = interval;
		nextFrameTimeMs = -1L;

		frameRecordingCursor = 0;

		LogManager.log.info("[AutoBone] Recording " + numFrames + " samples at a " + interval + " ms frame interval");
	}

	public void stopFrameRecording() {
		// Set to end of the frame array to prevent race condition with `frameRecordingCursor++`
		frameRecordingCursor = frames.length;
	}

	public boolean isRecording() {
		return frameRecordingCursor >= 0 && frameRecordingCursor < frames.length;
	}

	public PoseFrame[] getFrames() {
		return frames;
	}

	public void setFrames(PoseFrame[] frames) {
		this.frames = frames;
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

	public float getMaxHmdHeight(PoseFrame[] frames) {
		float maxHeight = 0f;
		for (PoseFrame frame : frames) {
			if (frame.rootPos.y > maxHeight) {
				maxHeight = frame.rootPos.y;
			}
		}
		return maxHeight;
	}

	public void processFrames() {
		processFrames(NUM_EPOCHS, true);
	}

	public void processFrames(float targetHeight) {
		processFrames(NUM_EPOCHS, true, targetHeight);
	}

	public void processFrames(int epochs, boolean calcInitError) {
		processFrames(epochs, calcInitError, INITIAL_ADJUSTMENT_RATE, ADJUSTMENT_RATE_DECAY);
	}

	public void processFrames(int epochs, boolean calcInitError, float targetHeight) {
		processFrames(epochs, calcInitError, INITIAL_ADJUSTMENT_RATE, ADJUSTMENT_RATE_DECAY, targetHeight);
	}

	public void processFrames(int epochs, boolean calcInitError, float adjustRate, float adjustRateDecay) {
		processFrames(epochs, calcInitError, adjustRate, adjustRateDecay, MIN_DATA_DISTANCE, MAX_DATA_DISTANCE);
	}

	public void processFrames(int epochs, boolean calcInitError, float adjustRate, float adjustRateDecay, float targetHeight) {
		processFrames(epochs, calcInitError, adjustRate, adjustRateDecay, MIN_DATA_DISTANCE, MAX_DATA_DISTANCE, targetHeight);
	}

	public void processFrames(int epochs, boolean calcInitError, float adjustRate, float adjustRateDecay, int minDataDist, int maxDataDist) {
		processFrames(epochs, calcInitError, adjustRate, adjustRateDecay, minDataDist, maxDataDist, -1f);
	}

	public float processFrames(int epochs, boolean calcInitError, float adjustRate, float adjustRateDecay, int minDataDist, int maxDataDist, float targetHeight) {
		Set<Entry<String, Float>> configSet = configs.entrySet();

		SimpleSkeleton skeleton1 = new SimpleSkeleton(configSet);
		SimpleSkeleton skeleton2 = new SimpleSkeleton(configSet);

		int epochCounter = calcInitError ? -1 : 0;

		int cursorOffset = minDataDist;

		float sumError = 0f;
		int errorCount = 0;

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
				targetHeight = hmdHeight * HEADSET_HEIGHT_RATIO;
			}
		}

		for (;;) {
			// Detect end of iteration
			if (cursorOffset >= frames.length || cursorOffset > maxDataDist) {
				epochCounter++;

				// Calculate average error over the epoch
				float avgError = errorCount > 0 ? sumError / errorCount : -1f;

				// Reset error sum values
				sumError = 0f;
				errorCount = 0;

				LogManager.log.info("[AutoBone] Epoch " + epochCounter + " average error: " + avgError);

				if (epochCounter >= epochs) {
					break;
				} else {
					// Reset cursor offset and decay the adjustment rate
					cursorOffset = minDataDist;
					adjustRate /= adjustRateDecay;
				}
			}

			int frameCursor1 = 0;
			int frameCursor2 = cursorOffset++;

			do {
				PoseFrame frame1 = frames[frameCursor1];
				PoseFrame frame2 = frames[frameCursor2];

				// If there's missing data, skip it
				if (frame1 == null || frame2 == null) {
					continue;
				}

				setSkeletonLengths(skeleton1);
				setSkeletonLengths(skeleton2);

				skeleton1.setPoseFromFrame(frame1);
				skeleton2.setPoseFromFrame(frame2);

				skeleton1.updatePose();
				skeleton2.updatePose();

				float curHeight = getHeight(configs);
				float errorDeriv = getErrorDeriv(skeleton1, skeleton2, curHeight - targetHeight);
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
					if (epochCounter < 0) {
						break;
					}

					float originalLength = entry.getValue();

					// Try positive and negative adjustments
					float minError = error;
					float finalNewLength = -1f;
					for (int i = 0; i < 2; i++) {
						float curAdjustVal = i == 0 ? adjustVal : -adjustVal;
						float newLength = originalLength + curAdjustVal;

						// No small or negative numbers!!! Bad algorithm!
						if (newLength < 0.01f) {
							continue;
						}

						updateSekeletonBoneLength(skeleton1, skeleton2, entry.getKey(), newLength);

						float newHeight = heightConfigs.contains(entry.getKey()) ? curHeight + curAdjustVal : curHeight;
						float newError = errorFunc(getErrorDeriv(skeleton1, skeleton2, newHeight - targetHeight));

						if (newError < minError) {
							minError = newError;
							finalNewLength = newLength;
						}
					}

					if (finalNewLength > 0f) {
						// Keep values within a reasonable range
						Float val;
						switch (entry.getKey()) {
						/*
						case "Neck":
							val = configs.get("Waist");
							if (val == null) {
								break;
							} else if (finalNewLength <= NECK_WAIST_RATIO_MIN * val || finalNewLength >= NECK_WAIST_RATIO_MAX * val) {
								entry.setValue(Math.min(Math.max(finalNewLength, NECK_WAIST_RATIO_MIN * val), NECK_WAIST_RATIO_MAX * val));
							} else {
								entry.setValue(finalNewLength);
							}
							break;

						case "Chest":
							val = configs.get("Waist");
							if (val == null) {
								break;
							} else if (finalNewLength <= CHEST_WAIST_RATIO_MIN * val || finalNewLength >= CHEST_WAIST_RATIO_MAX * val) {
								entry.setValue(Math.min(Math.max(finalNewLength, CHEST_WAIST_RATIO_MIN * val), CHEST_WAIST_RATIO_MAX * val));
							} else {
								entry.setValue(finalNewLength);
							}
							break;

						case "Hips width":
							val = configs.get("Waist");
							if (val == null) {
								break;
							} else if (finalNewLength < HIP_MIN || finalNewLength >= HIP_WAIST_RATIO_MAX * val) {
								entry.setValue(Math.min(Math.max(finalNewLength, HIP_MIN), HIP_WAIST_RATIO_MAX * val));
							} else {
								entry.setValue(finalNewLength);
							}
							break;

						case "Legs length":
							val = configs.get("Waist");
							if (val == null) {
								break;
							} else if (finalNewLength <= LEG_WAIST_RATIO_MIN * val || finalNewLength >= LEG_WAIST_RATIO_MAX * val) {
								entry.setValue(Math.min(Math.max(finalNewLength, LEG_WAIST_RATIO_MIN * val), LEG_WAIST_RATIO_MAX * val));
							} else {
								entry.setValue(finalNewLength);
							}
							break;

						case "Knee height":
							val = configs.get("Legs length");
							if (val == null) {
								break;
							} else if (finalNewLength <= KNEE_LEG_RATIO_MIN * val || finalNewLength >= KNEE_LEG_RATIO_MAX * val) {
								entry.setValue(Math.min(Math.max(finalNewLength, KNEE_LEG_RATIO_MIN * val), KNEE_LEG_RATIO_MAX * val));
							} else {
								entry.setValue(finalNewLength);
							}
							break;

						default:
							entry.setValue(finalNewLength);
							break;
						}
						*/

						// Temp while above is commented
						entry.setValue(finalNewLength);
					}

					// Reset the length to minimize bias in other variables, it's applied later
					updateSekeletonBoneLength(skeleton1, skeleton2, entry.getKey(), originalLength);
				}
			} while (++frameCursor1 < frames.length && ++frameCursor2 < frames.length);
		}

		float finalHeight = getHeight(configs);
		LogManager.log.info("[AutoBone] Target height: " + targetHeight + " New height: " + finalHeight);

		try {
			LogManager.log.info("[AutoBone] Ratios: [{Neck-Waist: " + StringUtils.prettyNumber(configs.get("Neck") / configs.get("Waist")) +
			"}, {Chest-Waist: " + StringUtils.prettyNumber(configs.get("Chest") / configs.get("Waist")) +
			"}, {Hip-Waist: " + StringUtils.prettyNumber(configs.get("Hips width") / configs.get("Waist")) +
			"}, {Leg-Waist: " + StringUtils.prettyNumber(configs.get("Legs length") / configs.get("Waist")) +
			"}, {Knee-Leg: " + StringUtils.prettyNumber(configs.get("Knee height") / configs.get("Legs length")) + "}]");
		} catch (Exception e) {
			// I literally couldn't care less, this is only for debugging
		}

		LogManager.log.info("[AutoBone] Done! Applying to skeleton...");
		if (!applyConfigToSkeleton(skeleton)) {
			LogManager.log.info("[AutoBone] Applying to skeleton failed, only saving configs...");
			saveConfigs();
		}

		return Math.abs(finalHeight - targetHeight);
	}

	protected static float getErrorDeriv(SimpleSkeleton skeleton1, SimpleSkeleton skeleton2, float heightChange) {
		float slideLeft = skeleton1.getLeftFootPos().distance(skeleton2.getLeftFootPos());
		float slideRight = skeleton1.getRightFootPos().distance(skeleton2.getRightFootPos());

		// Averaged error
		float slideError = (slideLeft + slideRight) / 2f;

		float dist1 = skeleton1.getLeftFootPos().y - skeleton1.getRightFootPos().y;
		float dist2 = skeleton2.getLeftFootPos().y - skeleton2.getRightFootPos().y;

		// Averaged error
		float distError = (dist1 + dist2) / 2f;

		// Minimize sliding, minimize foot height offset, minimize change in total height
		return ((SLIDE_ERROR_FACTOR * Math.abs(slideError)) +
		(OFFSET_ERROR_FACTOR * Math.abs(distError)) +
		(HEIGHT_ERROR_FACTOR * Math.abs(heightChange))) /
		(SLIDE_ERROR_FACTOR + OFFSET_ERROR_FACTOR + HEIGHT_ERROR_FACTOR);
	}

	// Mean square error function
	protected static float errorFunc(float errorDeriv) {
		return 0.5f * (errorDeriv * errorDeriv);
	}

	protected void updateSekeletonBoneLength(SimpleSkeleton skeleton1, SimpleSkeleton skeleton2, String joint, float newLength) {
		skeleton1.setSkeletonConfig(joint, newLength);
		skeleton2.setSkeletonConfig(joint, newLength);

		skeleton1.updatePose();
		skeleton2.updatePose();
	}
}
