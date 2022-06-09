package dev.slimevr.autobone;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import dev.slimevr.VRServer;
import dev.slimevr.poserecorder.*;
import dev.slimevr.vr.processor.HumanPoseProcessor;
import dev.slimevr.vr.processor.skeleton.Skeleton;
import dev.slimevr.vr.processor.skeleton.SkeletonConfig;
import dev.slimevr.vr.processor.skeleton.SkeletonConfigValue;
import dev.slimevr.vr.trackers.TrackerPosition;
import dev.slimevr.vr.trackers.TrackerRole;
import dev.slimevr.vr.trackers.TrackerUtils;
import io.eiren.util.StringUtils;
import io.eiren.util.collections.FastList;
import io.eiren.util.logging.LogManager;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;


public class AutoBone {

	private static final File saveDir = new File("Recordings");
	private static final File loadDir = new File("LoadRecordings");
	// This is filled by reloadConfigValues()
	public final EnumMap<SkeletonConfigValue, Float> configs = new EnumMap<SkeletonConfigValue, Float>(
		SkeletonConfigValue.class
	);
	public final EnumMap<SkeletonConfigValue, Float> staticConfigs = new EnumMap<SkeletonConfigValue, Float>(
		SkeletonConfigValue.class
	);
	public final FastList<SkeletonConfigValue> heightConfigs = new FastList<SkeletonConfigValue>(
		new SkeletonConfigValue[] { SkeletonConfigValue.NECK, SkeletonConfigValue.TORSO,
			SkeletonConfigValue.LEGS_LENGTH }
	);
	public final FastList<SkeletonConfigValue> lengthConfigs = new FastList<SkeletonConfigValue>(
		new SkeletonConfigValue[] { SkeletonConfigValue.HEAD, SkeletonConfigValue.NECK,
			SkeletonConfigValue.TORSO, SkeletonConfigValue.HIPS_WIDTH,
			SkeletonConfigValue.LEGS_LENGTH }
	);
	protected final VRServer server;
	public int cursorIncrement = 1;
	public int minDataDistance = 2;
	public int maxDataDistance = 32;
	public int numEpochs = 5;
	public float initialAdjustRate = 2.5f;
	public float adjustRateDecay = 1.01f;
	public float slideErrorFactor = 1.0f;
	public float offsetSlideErrorFactor = 0.0f;
	public float offsetErrorFactor = 0.0f;
	public float proportionErrorFactor = 0.2f;
	public float heightErrorFactor = 0.1f;

	// TODO Needs much more work, probably going to rethink how the errors work
	// to
	// avoid this barely functional workaround @ButterscotchV
	// For scaling distances, since smaller sizes will cause smaller distances
	// private float totalLengthBase = 2f;
	public float positionErrorFactor = 0.0f;
	public float positionOffsetErrorFactor = 0.0f;
	public boolean calcInitError = false;
	public float targetHeight = -1;

	// TODO hip tracker stuff... Hip tracker should be around 3 to 5
	// centimeters.
	// Human average is probably 1.1235 (SD 0.07)
	public float legBodyRatio = 1.1235f;
	// SD of 0.07, capture 68% within range
	public float legBodyRatioRange = 0.07f;
	// kneeLegRatio seems to be around 0.54 to 0.6 after asking a few people in
	// the
	// SlimeVR discord.
	public float kneeLegRatio = 0.55f;
	// kneeLegRatio seems to be around 0.55 to 0.64 after asking a few people in
	// the
	// SlimeVR discord. TODO : Chest should be a bit shorter (0.54?) if user has
	// an
	// additional hip tracker.
	public float chestTorsoRatio = 0.57f;

	public AutoBone(VRServer server) {
		this.server = server;
		reloadConfigValues();

		this.minDataDistance = server.config
			.getInt("autobone.minimumDataDistance", this.minDataDistance);
		this.maxDataDistance = server.config
			.getInt("autobone.maximumDataDistance", this.maxDataDistance);

		this.numEpochs = server.config.getInt("autobone.epochCount", this.numEpochs);

		this.initialAdjustRate = server.config
			.getFloat("autobone.adjustRate", this.initialAdjustRate);
		this.adjustRateDecay = server.config
			.getFloat("autobone.adjustRateDecay", this.adjustRateDecay);

		this.slideErrorFactor = server.config
			.getFloat("autobone.slideErrorFactor", this.slideErrorFactor);
		this.offsetSlideErrorFactor = server.config
			.getFloat("autobone.offsetSlideErrorFactor", this.offsetSlideErrorFactor);
		this.offsetErrorFactor = server.config
			.getFloat("autobone.offsetErrorFactor", this.offsetErrorFactor);
		this.proportionErrorFactor = server.config
			.getFloat("autobone.proportionErrorFactor", this.proportionErrorFactor);
		this.heightErrorFactor = server.config
			.getFloat("autobone.heightErrorFactor", this.heightErrorFactor);
		this.positionErrorFactor = server.config
			.getFloat("autobone.positionErrorFactor", this.positionErrorFactor);
		this.positionOffsetErrorFactor = server.config
			.getFloat("autobone.positionOffsetErrorFactor", this.positionOffsetErrorFactor);

		this.calcInitError = server.config.getBoolean("autobone.calculateInitialError", true);
		this.targetHeight = server.config.getFloat("autobone.manualTargetHeight", -1f);
	}

	// Mean square error function
	protected static float errorFunc(float errorDeriv) {
		return 0.5f * (errorDeriv * errorDeriv);
	}

	public static File getLoadDir() {
		return loadDir;
	}

	public void reloadConfigValues() {
		reloadConfigValues(null);
	}

	private float readFromConfig(SkeletonConfigValue configValue) {
		return server.config.getFloat(configValue.configKey, configValue.defaultValue);
	}

	public void reloadConfigValues(List<PoseFrameTracker> trackers) {
		// Load torso configs
		staticConfigs.put(SkeletonConfigValue.HEAD, readFromConfig(SkeletonConfigValue.HEAD));
		staticConfigs.put(SkeletonConfigValue.NECK, readFromConfig(SkeletonConfigValue.NECK));
		configs.put(SkeletonConfigValue.TORSO, readFromConfig(SkeletonConfigValue.TORSO));
		if (
			server.config.getBoolean("autobone.forceChestTracker", false)
				|| (trackers != null
					&& TrackerUtils
						.findNonComputedHumanPoseTrackerForBodyPosition(
							trackers,
							TrackerPosition.CHEST
						)
						!= null)
		) {
			// If force enabled or has a chest tracker
			staticConfigs.remove(SkeletonConfigValue.CHEST);
			configs.put(SkeletonConfigValue.CHEST, readFromConfig(SkeletonConfigValue.CHEST));
		} else {
			// Otherwise, make sure it's not used
			configs.remove(SkeletonConfigValue.CHEST);
			staticConfigs.put(SkeletonConfigValue.CHEST, readFromConfig(SkeletonConfigValue.CHEST));
		}
		if (
			server.config.getBoolean("autobone.forceHipTracker", false)
				|| (trackers != null
					&& TrackerUtils
						.findNonComputedHumanPoseTrackerForBodyPosition(
							trackers,
							TrackerPosition.HIP
						)
						!= null
					&& TrackerUtils
						.findNonComputedHumanPoseTrackerForBodyPosition(
							trackers,
							TrackerPosition.WAIST
						)
						!= null)
		) {
			// If force enabled or has a hip tracker and waist tracker
			staticConfigs.remove(SkeletonConfigValue.WAIST);
			configs.put(SkeletonConfigValue.WAIST, readFromConfig(SkeletonConfigValue.WAIST));
		} else {
			// Otherwise, make sure it's not used
			configs.remove(SkeletonConfigValue.WAIST);
			staticConfigs.put(SkeletonConfigValue.WAIST, readFromConfig(SkeletonConfigValue.WAIST));
		}

		// Load leg configs
		staticConfigs
			.put(SkeletonConfigValue.HIPS_WIDTH, readFromConfig(SkeletonConfigValue.HIPS_WIDTH));
		configs
			.put(SkeletonConfigValue.LEGS_LENGTH, readFromConfig(SkeletonConfigValue.LEGS_LENGTH));
		configs
			.put(SkeletonConfigValue.KNEE_HEIGHT, readFromConfig(SkeletonConfigValue.KNEE_HEIGHT));

		// Keep "feet" at ankles
		staticConfigs.put(SkeletonConfigValue.FOOT_LENGTH, 0f);
		staticConfigs.put(SkeletonConfigValue.FOOT_OFFSET, 0f);
		staticConfigs.put(SkeletonConfigValue.SKELETON_OFFSET, 0f);
	}

	/**
	 * A simple utility method to get the {@link Skeleton} from the
	 * {@link VRServer}
	 *
	 * @return The {@link Skeleton} associated with the {@link VRServer}, or
	 * null if there is none available
	 * @see {@link VRServer}, {@link Skeleton}
	 */
	private Skeleton getSkeleton() {
		HumanPoseProcessor humanPoseProcessor = server != null ? server.humanPoseProcessor : null;
		return humanPoseProcessor != null ? humanPoseProcessor.getSkeleton() : null;
	}

	public void applyConfig() {
		if (!applyConfigToSkeleton(getSkeleton())) {
			// Unable to apply to skeleton, save directly
			saveConfigs();
		}
	}

	public boolean applyConfigToSkeleton(Skeleton skeleton) {
		if (skeleton == null) {
			return false;
		}

		SkeletonConfig skeletonConfig = skeleton.getSkeletonConfig();
		skeletonConfig.setConfigs(configs, null);
		skeletonConfig.saveToConfig(server.config);
		server.saveConfig();

		LogManager.info("[AutoBone] Configured skeleton bone lengths");
		return true;
	}

	private void setConfig(SkeletonConfigValue config) {
		Float value = configs.get(config);
		if (value != null) {
			server.config.setProperty(config.configKey, value);
		}
	}

	// This doesn't require a skeleton, therefore can be used if skeleton is
	// null
	public void saveConfigs() {
		for (SkeletonConfigValue config : SkeletonConfigValue.values) {
			setConfig(config);
		}

		server.saveConfig();
	}

	public Float getConfig(SkeletonConfigValue config) {
		Float configVal = configs.get(config);
		return configVal != null ? configVal : staticConfigs.get(config);
	}

	public Float getConfig(
		SkeletonConfigValue config,
		Map<SkeletonConfigValue, Float> configs,
		Map<SkeletonConfigValue, Float> configsAlt
	) {
		if (configs == null) {
			throw new NullPointerException("Argument \"configs\" must not be null");
		}

		Float configVal = configs.get(config);
		return configVal != null || configsAlt == null ? configVal : configsAlt.get(config);
	}

	public float sumSelectConfigs(
		List<SkeletonConfigValue> selection,
		Map<SkeletonConfigValue, Float> configs,
		Map<SkeletonConfigValue, Float> configsAlt
	) {
		float sum = 0f;

		for (SkeletonConfigValue config : selection) {
			Float length = getConfig(config, configs, configsAlt);
			if (length != null) {
				sum += length;
			}
		}

		return sum;
	}

	public float sumSelectConfigs(
		List<SkeletonConfigValue> selection,
		SkeletonConfig skeletonConfig
	) {
		float sum = 0f;

		for (SkeletonConfigValue config : selection) {
			sum += skeletonConfig.getConfig(config);
		}

		return sum;
	}

	public float getLengthSum(Map<SkeletonConfigValue, Float> configs) {
		return getLengthSum(configs, null);
	}

	public float getLengthSum(
		Map<SkeletonConfigValue, Float> configs,
		Map<SkeletonConfigValue, Float> configsAlt
	) {
		float length = 0f;

		if (configsAlt != null) {
			for (Entry<SkeletonConfigValue, Float> config : configsAlt.entrySet()) {
				// If there isn't a duplicate config
				if (!configs.containsKey(config.getKey())) {
					length += config.getValue();
				}
			}
		}

		for (Float boneLength : configs.values()) {
			length += boneLength;
		}

		return length;
	}

	public void processFrames(PoseFrames frames, Consumer<Epoch> epochCallback) {
		processFrames(frames, -1f, epochCallback);
	}

	public void processFrames(
		PoseFrames frames,
		float targetHeight,
		Consumer<Epoch> epochCallback
	) {
		processFrames(frames, true, targetHeight, epochCallback);
	}

	public float processFrames(
		PoseFrames frames,
		boolean calcInitError,
		float targetHeight,
		Consumer<Epoch> epochCallback
	) {
		final int frameCount = frames.getMaxFrameCount();

		List<PoseFrameTracker> trackers = frames.getTrackers();
		reloadConfigValues(trackers); // Reload configs and detect chest tracker
										// from the first frame

		final PoseFrameSkeleton skeleton1 = new PoseFrameSkeleton(
			trackers,
			null,
			configs,
			staticConfigs
		);
		final PoseFrameSkeleton skeleton2 = new PoseFrameSkeleton(
			trackers,
			null,
			configs,
			staticConfigs
		);

		// If target height isn't specified, auto-detect
		if (targetHeight < 0f) {
			// Get the current skeleton from the server
			Skeleton skeleton = getSkeleton();
			if (skeleton != null) {
				// If there is a skeleton available, calculate the target height
				// from its
				// configs
				targetHeight = sumSelectConfigs(heightConfigs, skeleton.getSkeletonConfig());
				LogManager
					.warning(
						"[AutoBone] Target height loaded from skeleton (Make sure you reset before running!): "
							+ targetHeight
					);
			} else {
				// Otherwise if there is no skeleton available, attempt to get
				// the max HMD
				// height from the recording
				float hmdHeight = frames.getMaxHmdHeight();
				if (hmdHeight <= 0.50f) {
					LogManager
						.warning(
							"[AutoBone] Max headset height detected (Value seems too low, did you not stand up straight while measuring?): "
								+ hmdHeight
						);
				} else {
					LogManager.info("[AutoBone] Max headset height detected: " + hmdHeight);
				}

				// Estimate target height from HMD height
				targetHeight = hmdHeight;
			}
		}

		// Epoch loop, each epoch is one full iteration over the full dataset
		for (int epoch = calcInitError ? -1 : 0; epoch < numEpochs; epoch++) {
			float sumError = 0f;
			int errorCount = 0;

			float adjustRate = epoch
				>= 0 ? (initialAdjustRate / FastMath.pow(adjustRateDecay, epoch)) : 0f;

			// Iterate over the frames using a cursor and an offset for
			// comparing frames a
			// certain number of frames apart
			for (
				int cursorOffset = minDataDistance;
				cursorOffset <= maxDataDistance && cursorOffset < frameCount; cursorOffset++
			) {
				for (
					int frameCursor = 0; frameCursor < frameCount - cursorOffset;
					frameCursor += cursorIncrement
				) {
					int frameCursor2 = frameCursor + cursorOffset;

					skeleton1.skeletonConfig.setConfigs(configs, null);
					skeleton2.skeletonConfig.setConfigs(configs, null);

					skeleton1.setCursor(frameCursor);
					skeleton1.updatePose();

					skeleton2.setCursor(frameCursor2);
					skeleton2.updatePose();

					float totalLength = getLengthSum(configs);
					float curHeight = sumSelectConfigs(heightConfigs, configs, staticConfigs);
					// float scaleLength = sumSelectConfigs(lengthConfigs,
					// configs, staticConfigs);
					float errorDeriv = getErrorDeriv(
						frames,
						frameCursor,
						frameCursor2,
						skeleton1,
						skeleton2,
						targetHeight - curHeight,
						1f
					);
					float error = errorFunc(errorDeriv);

					// In case of fire
					if (Float.isNaN(error) || Float.isInfinite(error)) {
						// Extinguish
						LogManager
							.warning(
								"[AutoBone] Error value is invalid, resetting variables to recover"
							);
						reloadConfigValues(trackers);

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

					// If there is no adjustment whatsoever, skip this
					if (adjustVal == 0f) {
						continue;
					}

					for (Entry<SkeletonConfigValue, Float> entry : configs.entrySet()) {
						// Skip adjustment if the epoch is before starting (for
						// logging only)
						if (epoch < 0) {
							break;
						}

						float originalLength = entry.getValue();

						// Try positive and negative adjustments
						boolean isHeightVar = heightConfigs.contains(entry.getKey());
						// boolean isLengthVar =
						// lengthConfigs.contains(entry.getKey());
						float minError = errorDeriv;
						float finalNewLength = -1f;
						for (int i = 0; i < 2; i++) {
							// Scale by the ratio for smooth adjustment and more
							// stable results
							float curAdjustVal = ((i == 0 ? adjustVal : -adjustVal)
								* originalLength) / totalLength;
							float newLength = originalLength + curAdjustVal;

							// No small or negative numbers!!! Bad algorithm!
							if (newLength < 0.01f) {
								continue;
							}

							updateSkeletonBoneLength(
								skeleton1,
								skeleton2,
								entry.getKey(),
								newLength
							);

							float newHeight = isHeightVar ? curHeight + curAdjustVal : curHeight;
							// float newScaleLength = isLengthVar ? scaleLength
							// + curAdjustVal :
							// scaleLength;
							float newErrorDeriv = getErrorDeriv(
								frames,
								frameCursor,
								frameCursor2,
								skeleton1,
								skeleton2,
								targetHeight - newHeight,
								1f
							);

							if (newErrorDeriv < minError) {
								minError = newErrorDeriv;
								finalNewLength = newLength;
							}
						}

						if (finalNewLength > 0f) {
							entry.setValue(finalNewLength);
						}

						// Reset the length to minimize bias in other variables,
						// it's applied later
						updateSkeletonBoneLength(
							skeleton1,
							skeleton2,
							entry.getKey(),
							originalLength
						);
					}
				}
			}

			// Calculate average error over the epoch
			float avgError = errorCount > 0 ? sumError / errorCount : -1f;
			LogManager.info("[AutoBone] Epoch " + (epoch + 1) + " average error: " + avgError);

			if (epochCallback != null) {
				epochCallback.accept(new Epoch(epoch + 1, numEpochs, avgError, configs));
			}
		}

		float finalHeight = sumSelectConfigs(heightConfigs, configs, staticConfigs);
		LogManager
			.info("[AutoBone] Target height: " + targetHeight + " New height: " + finalHeight);

		return FastMath.abs(finalHeight - targetHeight);
	}

	// The change in position of the ankle over time
	protected float getSlideErrorDeriv(PoseFrameSkeleton skeleton1, PoseFrameSkeleton skeleton2) {
		float slideLeft = skeleton1.getComputedTracker(TrackerRole.LEFT_FOOT).position
			.distance(skeleton2.getComputedTracker(TrackerRole.LEFT_FOOT).position);
		float slideRight = skeleton1.getComputedTracker(TrackerRole.RIGHT_FOOT).position
			.distance(skeleton2.getComputedTracker(TrackerRole.RIGHT_FOOT).position);

		// Divide by 4 to halve and average, it's halved because you want to
		// approach a
		// midpoint, not the other point
		return (slideLeft + slideRight) / 4f;
	}

	// The change in distance between both of the ankles over time
	protected float getOffsetSlideErrorDeriv(
		PoseFrameSkeleton skeleton1,
		PoseFrameSkeleton skeleton2
	) {
		Vector3f leftFoot1 = skeleton1.getComputedTracker(TrackerRole.LEFT_FOOT).position;
		Vector3f rightFoot1 = skeleton1.getComputedTracker(TrackerRole.RIGHT_FOOT).position;

		Vector3f leftFoot2 = skeleton2.getComputedTracker(TrackerRole.LEFT_FOOT).position;
		Vector3f rightFoot2 = skeleton2.getComputedTracker(TrackerRole.RIGHT_FOOT).position;

		float slideDist1 = leftFoot1.distance(rightFoot1);
		float slideDist2 = leftFoot2.distance(rightFoot2);

		float slideDist3 = leftFoot1.distance(rightFoot2);
		float slideDist4 = leftFoot2.distance(rightFoot1);

		float dist1 = FastMath.abs(slideDist1 - slideDist2);
		float dist2 = FastMath.abs(slideDist3 - slideDist4);

		float dist3 = FastMath.abs(slideDist1 - slideDist3);
		float dist4 = FastMath.abs(slideDist1 - slideDist4);

		float dist5 = FastMath.abs(slideDist2 - slideDist3);
		float dist6 = FastMath.abs(slideDist2 - slideDist4);

		// Divide by 12 to halve and average, it's halved because you want to
		// approach a
		// midpoint, not the other point
		return (dist1 + dist2 + dist3 + dist4 + dist5 + dist6) / 12f;
	}

	// The offset between both feet at one instant and over time
	protected float getOffsetErrorDeriv(PoseFrameSkeleton skeleton1, PoseFrameSkeleton skeleton2) {
		float leftFoot1 = skeleton1.getComputedTracker(TrackerRole.LEFT_FOOT).position.getY();
		float rightFoot1 = skeleton1.getComputedTracker(TrackerRole.RIGHT_FOOT).position.getY();

		float leftFoot2 = skeleton2.getComputedTracker(TrackerRole.LEFT_FOOT).position.getY();
		float rightFoot2 = skeleton2.getComputedTracker(TrackerRole.RIGHT_FOOT).position.getY();

		float dist1 = FastMath.abs(leftFoot1 - rightFoot1);
		float dist2 = FastMath.abs(leftFoot2 - rightFoot2);

		float dist3 = FastMath.abs(leftFoot1 - rightFoot2);
		float dist4 = FastMath.abs(leftFoot2 - rightFoot1);

		float dist5 = FastMath.abs(leftFoot1 - leftFoot2);
		float dist6 = FastMath.abs(rightFoot1 - rightFoot2);

		// Divide by 12 to halve and average, it's halved because you want to
		// approach a
		// midpoint, not the other point
		return (dist1 + dist2 + dist3 + dist4 + dist5 + dist6) / 12f;
	}

	// The distance from average human proportions
	protected float getProportionErrorDeriv(SkeletonConfig skeleton) {
		float neckLength = skeleton.getConfig(SkeletonConfigValue.NECK);
		float chestLength = skeleton.getConfig(SkeletonConfigValue.CHEST);
		float torsoLength = skeleton.getConfig(SkeletonConfigValue.TORSO);
		float legsLength = skeleton.getConfig(SkeletonConfigValue.LEGS_LENGTH);
		float kneeHeight = skeleton.getConfig(SkeletonConfigValue.KNEE_HEIGHT);

		float chestTorso = FastMath.abs((chestLength / torsoLength) - chestTorsoRatio);
		float legBody = FastMath.abs((legsLength / (torsoLength + neckLength)) - legBodyRatio);
		float kneeLeg = FastMath.abs((kneeHeight / legsLength) - kneeLegRatio);

		if (legBody <= legBodyRatioRange) {
			legBody = 0f;
		} else {
			legBody -= legBodyRatioRange;
		}

		return (chestTorso + legBody + kneeLeg) / 3f;
	}

	// The distance of any points to the corresponding absolute position
	protected float getPositionErrorDeriv(
		PoseFrames frames,
		int cursor,
		PoseFrameSkeleton skeleton
	) {
		float offset = 0f;
		int offsetCount = 0;

		List<PoseFrameTracker> trackers = frames.getTrackers();
		for (PoseFrameTracker tracker : trackers) {
			TrackerFrame trackerFrame = tracker.safeGetFrame(cursor);
			if (
				trackerFrame == null
					|| !trackerFrame.hasData(TrackerFrameData.POSITION)
					|| trackerFrame.designation.trackerRole.isEmpty()
			) {
				continue;
			}

			Vector3f nodePos = skeleton
				.getComputedTracker(trackerFrame.designation.trackerRole.get()).position;
			if (nodePos != null) {
				offset += FastMath.abs(nodePos.distance(trackerFrame.position));
				offsetCount++;
			}
		}

		return offsetCount > 0 ? offset / offsetCount : 0f;
	}

	// The difference between offset of absolute position and the corresponding
	// point over time
	protected float getPositionOffsetErrorDeriv(
		PoseFrames frames,
		int cursor1,
		int cursor2,
		PoseFrameSkeleton skeleton1,
		PoseFrameSkeleton skeleton2
	) {
		float offset = 0f;
		int offsetCount = 0;

		List<PoseFrameTracker> trackers = frames.getTrackers();
		for (PoseFrameTracker tracker : trackers) {
			TrackerFrame trackerFrame1 = tracker.safeGetFrame(cursor1);
			if (trackerFrame1 == null || !trackerFrame1.hasData(TrackerFrameData.POSITION)) {
				continue;
			}

			TrackerFrame trackerFrame2 = tracker.safeGetFrame(cursor2);
			if (
				trackerFrame2 == null
					|| !trackerFrame2.hasData(TrackerFrameData.POSITION)
					|| trackerFrame1.designation.trackerRole.isEmpty()
			) {
				continue;
			}

			Vector3f nodePos1 = skeleton1
				.getComputedTracker(trackerFrame1.designation.trackerRole.get()).position;
			if (nodePos1 == null) {
				continue;
			}


			if (trackerFrame2.designation.trackerRole.isEmpty()) {
				continue;
			}
			Vector3f nodePos2 = skeleton2
				.getComputedTracker(trackerFrame2.designation.trackerRole.get()).position;
			if (nodePos2 == null) {
				continue;
			}

			float dist1 = FastMath.abs(nodePos1.distance(trackerFrame1.position));
			float dist2 = FastMath.abs(nodePos2.distance(trackerFrame2.position));

			offset += FastMath.abs(dist2 - dist1);
			offsetCount++;
		}

		return offsetCount > 0 ? offset / offsetCount : 0f;
	}

	protected float getErrorDeriv(
		PoseFrames frames,
		int cursor1,
		int cursor2,
		PoseFrameSkeleton skeleton1,
		PoseFrameSkeleton skeleton2,
		float heightChange,
		float distScale
	) {
		float totalError = 0f;
		float sumWeight = 0f;

		if (slideErrorFactor > 0f) {
			// This is the main error function, this calculates the distance
			// between the
			// foot positions on both frames
			totalError += getSlideErrorDeriv(skeleton1, skeleton2) * distScale * slideErrorFactor;
			sumWeight += slideErrorFactor;
		}

		if (offsetSlideErrorFactor > 0f) {
			// This error function compares the distance between the feet on
			// each frame and
			// returns the offset between them
			totalError += getOffsetSlideErrorDeriv(skeleton1, skeleton2)
				* distScale
				* offsetSlideErrorFactor;
			sumWeight += offsetSlideErrorFactor;
		}

		if (offsetErrorFactor > 0f) {
			// This error function compares the height of each foot in each
			// frame
			totalError += getOffsetErrorDeriv(skeleton1, skeleton2) * distScale * offsetErrorFactor;
			sumWeight += offsetErrorFactor;
		}

		if (proportionErrorFactor > 0f) {
			// This error function compares the current values to general
			// expected
			// proportions to keep measurements in line
			// Either skeleton will work fine, skeleton1 is used as a default
			totalError += getProportionErrorDeriv(skeleton1.skeletonConfig) * proportionErrorFactor;
			sumWeight += proportionErrorFactor;
		}

		if (heightErrorFactor > 0f) {
			// This error function compares the height change to the actual
			// measured height
			// of the headset
			totalError += FastMath.abs(heightChange) * heightErrorFactor;
			sumWeight += heightErrorFactor;
		}

		if (positionErrorFactor > 0f) {
			// This error function compares the position of an assigned tracker
			// with the
			// position on the skeleton
			totalError += (getPositionErrorDeriv(frames, cursor1, skeleton1)
				+ getPositionErrorDeriv(frames, cursor2, skeleton2) / 2f)
				* distScale
				* positionErrorFactor;
			sumWeight += positionErrorFactor;
		}

		if (positionOffsetErrorFactor > 0f) {
			// This error function compares the offset of the position of an
			// assigned
			// tracker with the position on the skeleton
			totalError += getPositionOffsetErrorDeriv(
				frames,
				cursor1,
				cursor2,
				skeleton1,
				skeleton2
			) * distScale * positionOffsetErrorFactor;
			sumWeight += positionOffsetErrorFactor;
		}

		return sumWeight > 0f ? totalError / sumWeight : 0f;
	}

	protected void updateSkeletonBoneLength(
		PoseFrameSkeleton skeleton1,
		PoseFrameSkeleton skeleton2,
		SkeletonConfigValue config,
		float newLength
	) {
		skeleton1.skeletonConfig.setConfig(config, newLength);
		skeleton1.updatePoseAffectedByConfig(config);

		skeleton2.skeletonConfig.setConfig(config, newLength);
		skeleton2.updatePoseAffectedByConfig(config);
	}

	public String getLengthsString() {
		final StringBuilder configInfo = new StringBuilder();
		this.configs.forEach((key, value) -> {
			if (configInfo.length() > 0) {
				configInfo.append(", ");
			}

			configInfo.append(key.stringVal + ": " + StringUtils.prettyNumber(value * 100f, 2));
		});

		return configInfo.toString();
	}

	public void saveRecording(PoseFrames frames) {
		if (saveDir.isDirectory() || saveDir.mkdirs()) {
			File saveRecording;
			int recordingIndex = 1;
			do {
				saveRecording = new File(saveDir, "ABRecording" + recordingIndex++ + ".pfr");
			} while (saveRecording.exists());

			LogManager
				.info("[AutoBone] Exporting frames to \"" + saveRecording.getPath() + "\"...");
			if (PoseFrameIO.writeToFile(saveRecording, frames)) {
				LogManager
					.info(
						"[AutoBone] Done exporting! Recording can be found at \""
							+ saveRecording.getPath()
							+ "\"."
					);
			} else {
				LogManager
					.severe(
						"[AutoBone] Failed to export the recording to \""
							+ saveRecording.getPath()
							+ "\"."
					);
			}
		} else {
			LogManager
				.severe(
					"[AutoBone] Failed to create the recording directory \""
						+ saveDir.getPath()
						+ "\"."
				);
		}
	}

	public List<Pair<String, PoseFrames>> loadRecordings() {
		List<Pair<String, PoseFrames>> recordings = new FastList<Pair<String, PoseFrames>>();
		if (loadDir.isDirectory()) {
			File[] files = loadDir.listFiles();
			if (files != null) {
				for (File file : files) {
					if (
						file.isFile()
							&& org.apache.commons.lang3.StringUtils
								.endsWithIgnoreCase(file.getName(), ".pfr")
					) {
						LogManager
							.info(
								"[AutoBone] Detected recording at \""
									+ file.getPath()
									+ "\", loading frames..."
							);
						PoseFrames frames = PoseFrameIO.readFromFile(file);

						if (frames == null) {
							LogManager
								.severe("Reading frames from \"" + file.getPath() + "\" failed...");
						} else {
							recordings.add(Pair.of(file.getName(), frames));
						}
					}
				}
			}
		}

		return recordings;
	}

	public class Epoch {

		public final int epoch;
		public final int totalEpochs;
		public final float epochError;
		public final EnumMap<SkeletonConfigValue, Float> configValues;

		public Epoch(
			int epoch,
			int totalEpochs,
			float epochError,
			EnumMap<SkeletonConfigValue, Float> configValues
		) {
			this.epoch = epoch;
			this.totalEpochs = totalEpochs;
			this.epochError = epochError;
			this.configValues = configValues;
		}

		@Override
		public String toString() {
			return "Epoch: " + epoch + ", Epoch Error: " + epochError;
		}
	}
}
