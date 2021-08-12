package io.eiren.gui.autobone;

import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import io.eiren.util.ann.ThreadSafe;
import io.eiren.util.ann.VRServerThread;
import io.eiren.util.logging.LogManager;
import io.eiren.vr.VRServer;
import io.eiren.vr.processor.HumanSkeleton;
import io.eiren.vr.processor.HumanSkeletonWithLegs;
import io.eiren.vr.processor.HumanSkeletonWithWaist;

public class AutoBone {

	protected final static int MIN_DATA_DISTANCE = 1;
	protected final static int MAX_DATA_DISTANCE = 200;

	protected final static int NUM_EPOCHS = 20;

	protected final static float INITIAL_ADJUSTMENT_RATE = 1f;
	protected final static float ADJUSTMENT_RATE_DECAY = 1.1f;

	protected final static float SLIDE_ERROR_FACTOR = 1f;
	protected final static float OFFSET_ERROR_FACTOR = 0.75f;
	protected final static float HEIGHT_ERROR_FACTOR = 0.75f;

	protected final static float HEADSET_HEIGHT_RATIO = 0.91f;

	protected final static float CHEST_WAIST_RATIO_MIN = 0.2f;
	protected final static float CHEST_WAIST_RATIO_MAX = 0.6f;

	protected final static float HIP_MIN = 0.08f;
	protected final static float HIP_WAIST_RATIO_MAX = 0.4f;

	protected final static float LEG_WAIST_RATIO_MIN = 0.5235f;
	protected final static float LEG_WAIST_RATIO_MAX = 1.7235f;

	protected final static float KNEE_LEG_RATIO_MIN = 0.42f;
	protected final static float KNEE_LEG_RATIO_MAX = 0.58f;

	protected final VRServer server;

	HumanSkeletonWithLegs skeleton = null;

	protected PoseFrame[] frames = new PoseFrame[0];
	protected int frameRecordingCursor = -1;

	protected long frameRecordingInterval = 60L;
	protected long lastFrameTimeMs = -1L;

	// This is filled by reloadConfigValues()
	public final HashMap<String, Float> configs = new HashMap<String, Float>();

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
		configs.put("Knee height", server.config.getFloat("body.kneeHeight", 0.42f));
		configs.put("Legs length", server.config.getFloat("body.legsLength", 0.84f));
		//configs.put("Foot length", server.config.getFloat("body.footLength", HumanSkeletonWithLegs.FOOT_LENGTH_DEFAULT)); // Feet aren't actually used
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

		LogManager.log.info("[AutoBone] Configured skeleton bone lengths");
		return true;
	}

	@VRServerThread
	public void onTick() {
		if (frameRecordingCursor >= 0 && frameRecordingCursor < frames.length && skeleton != null && System.currentTimeMillis() - lastFrameTimeMs >= frameRecordingInterval) {
			lastFrameTimeMs = System.currentTimeMillis();

			PoseFrame frame = new PoseFrame(skeleton);
			frames[frameRecordingCursor++] = frame;

			LogManager.log.info("Recorded frame " + frameRecordingCursor);
		}
	}

	public void startFrameRecording(int numFrames, long interval) {
		frames = new PoseFrame[numFrames];

		frameRecordingInterval = interval;
		lastFrameTimeMs = 0L;

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

	public float getHeight() {
		float height = 0f;

		Float waistLength = configs.get("Waist");
		if (waistLength != null) {
			height += waistLength;
		}

		Float legsLength = configs.get("Legs length");
		if (legsLength != null) {
			height += legsLength;
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
		processFrames(NUM_EPOCHS, true, INITIAL_ADJUSTMENT_RATE, ADJUSTMENT_RATE_DECAY, MIN_DATA_DISTANCE, MAX_DATA_DISTANCE);
	}

	public void processFrames(int epochs, boolean calcInitError) {
		processFrames(epochs, calcInitError, INITIAL_ADJUSTMENT_RATE, ADJUSTMENT_RATE_DECAY, MIN_DATA_DISTANCE, MAX_DATA_DISTANCE);
	}

	public void processFrames(int epochs, boolean calcInitError, float adjustRate, float adjustRateDecay) {
		processFrames(epochs, calcInitError, adjustRate, adjustRateDecay, MIN_DATA_DISTANCE, MAX_DATA_DISTANCE);
	}

	public void processFrames(int epochs, boolean calcInitError, float adjustRate, float adjustRateDecay, int minDataDist, int maxDataDist) {
		Set<Entry<String, Float>> configSet = configs.entrySet();

		SimpleSkeleton skeleton1 = new SimpleSkeleton(configSet);
		SimpleSkeleton skeleton2 = new SimpleSkeleton(configSet);

		int epochCounter = calcInitError ? -1 : 0;

		int cursorOffset = minDataDist;

		float sumError = 0f;
		int errorCount = 0;

		float hmdHeight = getMaxHmdHeight(frames);
		if (hmdHeight <= 0.50f) {
			LogManager.log.warning("[AutoBone] Max headset height detected (Value seems too low, did you not stand up straight while measuring?): " + hmdHeight);
		} else {
			LogManager.log.info("[AutoBone] Max headset height detected: " + hmdHeight);
		}

		// Estimate target height from HMD height
		float targetHeight = hmdHeight * HEADSET_HEIGHT_RATIO;

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

				float error = getError(skeleton1, skeleton2, getHeight() - targetHeight);

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
				sumError += error;
				errorCount++;

				float adjustVal = error * adjustRate;

				// if (frameCursor1 == 0 && frameCursor2 == 1) {
				// 	LogManager.log.info("[AutoBone] Current position error: " + error);
				// }

				entryLoop: for (Entry<String, Float> entry : configs.entrySet()) {
					// Skip adjustment if the epoch is before starting (for logging only)
					if (epochCounter < 0) {
						break;
					}

					float originalLength = entry.getValue();

					// Try positive and negative adjustments
					posNegAdj: for (int i = 0; i < 2; i++) {
						float curAdjustVal = i == 0 ? adjustVal : -adjustVal;
						float newLength = originalLength + curAdjustVal;

						// No small or negative numbers!!! Bad algorithm!
						if (newLength < 0.01f) {
							continue;
						}

						// Detect and fix invalid values, skipping adjustment
						Float val;
						switch (entry.getKey()) {
						case "Chest":
							val = configs.get("Waist");
							if (val == null || newLength <= CHEST_WAIST_RATIO_MIN * val || newLength >= CHEST_WAIST_RATIO_MAX * val) {
								entry.setValue(Math.min(Math.max(newLength, CHEST_WAIST_RATIO_MIN * val), CHEST_WAIST_RATIO_MAX * val));

								// If bone length hasn't been changed on skeleton, skip right to next entry
								if (i > 0) {
									break posNegAdj;
								} else {
									continue entryLoop;
								}
							}
							break;

						case "Hips width":
							val = configs.get("Waist");
							if (val == null || newLength < HIP_MIN || newLength >= HIP_WAIST_RATIO_MAX * val) {
								entry.setValue(Math.min(Math.max(newLength, HIP_MIN), HIP_WAIST_RATIO_MAX * val));

								// If bone length hasn't been changed on skeleton, skip right to next entry
								if (i > 0) {
									break posNegAdj;
								} else {
									continue entryLoop;
								}
							}
							break;

						case "Legs length":
							val = configs.get("Waist");
							if (val == null || newLength <= LEG_WAIST_RATIO_MIN * val || newLength >= LEG_WAIST_RATIO_MAX * val) {
								entry.setValue(Math.min(Math.max(newLength, LEG_WAIST_RATIO_MIN * val), LEG_WAIST_RATIO_MAX * val));

								// If bone length hasn't been changed on skeleton, skip right to next entry
								if (i > 0) {
									break posNegAdj;
								} else {
									continue entryLoop;
								}
							}
							break;

						case "Knee height":
							val = configs.get("Legs length");
							if (val == null || newLength <= KNEE_LEG_RATIO_MIN * val || newLength >= KNEE_LEG_RATIO_MAX * val) {
								entry.setValue(Math.min(Math.max(newLength, KNEE_LEG_RATIO_MIN * val), KNEE_LEG_RATIO_MAX * val));

								// If bone length hasn't been changed on skeleton, skip right to next entry
								if (i > 0) {
									break posNegAdj;
								} else {
									continue entryLoop;
								}
							}
							break;
						}

						updateSekeletonBoneLength(skeleton1, skeleton2, entry.getKey(), newLength);
						float newError = getError(skeleton1, skeleton2, (getHeight() + curAdjustVal) - targetHeight);

						if (newError < error) {
							entry.setValue(newLength);
							break;
						}
					}

					// Reset the length to minimize bias in other variables, it's applied later
					updateSekeletonBoneLength(skeleton1, skeleton2, entry.getKey(), originalLength);
				}
			} while (++frameCursor1 < frames.length && ++frameCursor2 < frames.length);
		}

		LogManager.log.info("[AutoBone] Target height: " + targetHeight + " New height: " + getHeight());

		LogManager.log.info("[AutoBone] Done! Applying to skeleton...");
		applyConfigToSkeleton(skeleton);
	}

	protected static float getError(SimpleSkeleton skeleton1, SimpleSkeleton skeleton2, float heightChange) {
		float slideLeft = skeleton1.getLeftFootPos().distance(skeleton2.getLeftFootPos());
		float slideRight = skeleton1.getRightFootPos().distance(skeleton2.getRightFootPos());

		// Averaged error
		float slideError = (slideLeft + slideRight) / 2f;

		float dist1 = skeleton1.getLeftFootPos().y - skeleton1.getRightFootPos().y;
		float dist2 = skeleton2.getLeftFootPos().y - skeleton2.getRightFootPos().y;

		// Averaged error
		float distError = (dist1 + dist2) / 2f;

		// Minimize sliding, minimize foot height offset, minimize change in total height
		return (SLIDE_ERROR_FACTOR * (slideError * slideError)) + (OFFSET_ERROR_FACTOR * (distError * distError)) + (HEIGHT_ERROR_FACTOR * (heightChange * heightChange));
	}

	protected void updateSekeletonBoneLength(SimpleSkeleton skeleton1, SimpleSkeleton skeleton2, String joint, float newLength) {
		skeleton1.setSkeletonConfig(joint, newLength);
		skeleton2.setSkeletonConfig(joint, newLength);

		skeleton1.updatePose();
		skeleton2.updatePose();
	}
}
