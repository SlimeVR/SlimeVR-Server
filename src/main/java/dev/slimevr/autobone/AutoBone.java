package dev.slimevr.autobone;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import dev.slimevr.VRServer;
import dev.slimevr.poserecorder.*;
import dev.slimevr.vr.processor.HumanPoseProcessor;
import dev.slimevr.vr.processor.TransformNode;
import dev.slimevr.vr.processor.skeleton.BoneType;
import dev.slimevr.vr.processor.skeleton.Skeleton;
import dev.slimevr.vr.processor.skeleton.HumanSkeleton;
import dev.slimevr.vr.processor.skeleton.SkeletonConfig;
import dev.slimevr.vr.processor.skeleton.SkeletonConfigValue;
import dev.slimevr.vr.trackers.TrackerRole;
import io.eiren.util.StringUtils;
import io.eiren.util.collections.FastList;
import io.eiren.util.logging.LogManager;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


public class AutoBone {

	private static final File saveDir = new File("Recordings");
	private static final File loadDir = new File("LoadRecordings");
	// This is filled by reloadConfigValues()
	public final EnumMap<BoneType, Float> offsets = new EnumMap<BoneType, Float>(
		BoneType.class
	);

	public final FastList<BoneType> adjustOffsets = new FastList<BoneType>(
		new BoneType[] {
			BoneType.HEAD,
			BoneType.NECK,
			BoneType.CHEST,
			BoneType.WAIST,
			BoneType.HIP,

			// This one doesn't seem to work very well and is generally going to
			// be similar between users
			// BoneType.RIGHT_HIP,

			BoneType.UPPER_LEG,
			BoneType.LOWER_LEG,
		}
	);

	public final FastList<BoneType> heightOffsets = new FastList<BoneType>(
		new BoneType[] {
			BoneType.NECK,
			BoneType.CHEST,
			BoneType.WAIST,
			BoneType.HIP,

			BoneType.UPPER_LEG,
			BoneType.LOWER_LEG,
		}
	);

	public final FastList<SkeletonConfigValue> legacyAdjustedConfigs = new FastList<SkeletonConfigValue>(
		new SkeletonConfigValue[] {
			SkeletonConfigValue.HEAD,
			SkeletonConfigValue.NECK,

			SkeletonConfigValue.TORSO,
			SkeletonConfigValue.CHEST,
			SkeletonConfigValue.WAIST,

			SkeletonConfigValue.LEGS_LENGTH,
			SkeletonConfigValue.KNEE_HEIGHT,
		}
	);

	public final EnumMap<SkeletonConfigValue, Float> legacyConfigs = new EnumMap<SkeletonConfigValue, Float>(
		SkeletonConfigValue.class
	);

	protected final VRServer server;
	public int cursorIncrement = 2;
	public int minDataDistance = 1;
	public int maxDataDistance = 1;
	public int numEpochs = 100;
	public float initialAdjustRate = 10f;
	public float adjustRateMultiplier = 0.995f;
	public float slideErrorFactor = 1.0f;
	public float offsetSlideErrorFactor = 1.0f;
	public float offsetErrorFactor = 0.0f;
	public float proportionErrorFactor = 0.0f;
	public float heightErrorFactor = 0.0f;

	public boolean randomizeFrameOrder = true;
	public boolean scaleEachStep = true;

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
		this.adjustRateMultiplier = server.config
			.getFloat("autobone.adjustRateMultiplier", this.adjustRateMultiplier);

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

	public void reloadConfigValues(List<PoseFrameTracker> trackers) {
		for (BoneType offset : adjustOffsets) {
			offsets.put(offset, 0.4f);
		}
	}

	public void setConfigOffset(
		SkeletonConfig skeletonConfig,
		BoneType config,
		float value
	) {
		switch (config) {
			case HEAD:
				skeletonConfig.setNodeOffset(config, 0, 0, value);
				break;

			case NECK:
			case CHEST:
			case WAIST:
			case HIP:
			case UPPER_LEG:
			case LOWER_LEG:
				skeletonConfig.setNodeOffset(config, 0, -value, 0);
				break;

			case RIGHT_HIP:
				skeletonConfig.setNodeOffset(BoneType.LEFT_HIP, -value, 0, 0);
				skeletonConfig.setNodeOffset(BoneType.RIGHT_HIP, value, 0, 0);
				break;
		}
	}

	public void setConfigOffsets(
		SkeletonConfig skeletonConfig,
		EnumMap<BoneType, Float> config
	) {
		config.forEach((offset, value) -> {
			setConfigOffset(skeletonConfig, offset, value);
		});
	}

	public Vector3f getBoneDirection(
		HumanSkeleton skeleton,
		BoneType node,
		boolean rightSide,
		Vector3f buffer
	) {
		if (buffer == null) {
			buffer = new Vector3f();
		}

		TransformNode relevantTransform = skeleton.getNode(node, rightSide);
		return relevantTransform.worldTransform
			.getTranslation()
			.subtract(relevantTransform.getParent().worldTransform.getTranslation(), buffer)
			.normalizeLocal();
	}

	public float getDotProductDiff(
		HumanSkeleton skeleton1,
		HumanSkeleton skeleton2,
		BoneType node,
		boolean rightSide,
		Vector3f offset
	) {
		Vector3f normalizedOffset = offset.normalize();

		Vector3f boneRotation = new Vector3f();
		getBoneDirection(skeleton1, node, rightSide, boneRotation);
		float dot1 = normalizedOffset.dot(boneRotation);

		getBoneDirection(skeleton2, node, rightSide, boneRotation);
		float dot2 = normalizedOffset.dot(boneRotation);

		return dot2 - dot1;
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
		if (!applyConfig(getSkeleton())) {
			// Unable to apply to skeleton, save directly
			// saveConfigs();
		}
	}

	public boolean applyConfig(BiConsumer<SkeletonConfigValue, Float> configConsumer) {
		if (configConsumer == null) {
			return false;
		}

		try {
			configConsumer
				.accept(SkeletonConfigValue.HEAD, offsets.get(BoneType.HEAD));
			configConsumer
				.accept(SkeletonConfigValue.NECK, offsets.get(BoneType.NECK));

			configConsumer
				.accept(
					SkeletonConfigValue.TORSO,
					offsets.get(BoneType.CHEST)
						+ offsets.get(BoneType.HIP)
						+ offsets.get(BoneType.WAIST)
				);
			configConsumer
				.accept(SkeletonConfigValue.CHEST, offsets.get(BoneType.CHEST));
			configConsumer
				.accept(SkeletonConfigValue.WAIST, offsets.get(BoneType.HIP));

			configConsumer
				.accept(
					SkeletonConfigValue.LEGS_LENGTH,
					offsets.get(BoneType.UPPER_LEG)
						+ offsets.get(BoneType.LOWER_LEG)
				);
			configConsumer
				.accept(
					SkeletonConfigValue.KNEE_HEIGHT,
					offsets.get(BoneType.LOWER_LEG)
				);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean applyConfig(Map<SkeletonConfigValue, Float> skeletonConfig) {
		if (skeletonConfig == null) {
			return false;
		}

		return applyConfig(skeletonConfig::put);
	}

	public boolean applyConfig(SkeletonConfig skeletonConfig) {
		if (skeletonConfig == null) {
			return false;
		}

		return applyConfig(skeletonConfig::setConfig);
	}

	public boolean applyConfig(Skeleton skeleton) {
		if (skeleton == null) {
			return false;
		}

		SkeletonConfig skeletonConfig = skeleton.getSkeletonConfig();
		if (!applyConfig(skeletonConfig))
			return false;
		// setConfigOffsets(skeletonConfig, offsets);
		skeletonConfig.saveToConfig(server.config);
		server.saveConfig();

		LogManager.info("[AutoBone] Configured skeleton bone lengths");
		return true;
	}

	public Float getConfig(BoneType config) {
		return offsets.get(config);
	}

	public Float getConfig(
		BoneType config,
		Map<BoneType, Float> configs,
		Map<BoneType, Float> configsAlt
	) {
		if (configs == null) {
			throw new NullPointerException("Argument \"configs\" must not be null");
		}

		Float configVal = configs.get(config);
		return configVal != null || configsAlt == null ? configVal : configsAlt.get(config);
	}

	public float sumSelectConfigs(
		List<BoneType> selection,
		Map<BoneType, Float> configs,
		Map<BoneType, Float> configsAlt
	) {
		float sum = 0f;

		for (BoneType config : selection) {
			Float length = getConfig(config, configs, configsAlt);
			if (length != null) {
				sum += length;
			}
		}

		return sum;
	}

	public float getLengthSum(Map<BoneType, Float> configs) {
		return getLengthSum(configs, null);
	}

	public float getLengthSum(
		Map<BoneType, Float> configs,
		Map<BoneType, Float> configsAlt
	) {
		float length = 0f;

		if (configsAlt != null) {
			for (Entry<BoneType, Float> config : configsAlt.entrySet()) {
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

	private Random rand = new Random();

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
			null
		);
		final PoseFrameSkeleton skeleton2 = new PoseFrameSkeleton(
			trackers,
			null
		);

		// If target height isn't specified, auto-detect
		if (targetHeight < 0f) {
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
				hmdHeight = 1.83f;
			} else {
				LogManager.info("[AutoBone] Max headset height detected: " + hmdHeight);
			}

			// Estimate target height from HMD height
			targetHeight = hmdHeight;
		}

		// Epoch loop, each epoch is one full iteration over the full dataset
		for (int epoch = calcInitError ? -1 : 0; epoch < numEpochs; epoch++) {
			float sumError = 0f;
			int errorCount = 0;

			float adjustRate = epoch >= 0
				? (initialAdjustRate * FastMath.pow(adjustRateMultiplier, epoch))
				: 0f;

			int[] randomFrameIndices = null;
			if (randomizeFrameOrder) {
				randomFrameIndices = new int[frameCount];

				int zeroPos = -1;
				for (int i = 0; i < frameCount; i++) {
					int index = rand.nextInt(frameCount);

					if (i > 0) {
						while (index == zeroPos || randomFrameIndices[index] > 0) {
							index = rand.nextInt(frameCount);
						}
					} else {
						zeroPos = index;
					}

					randomFrameIndices[index] = i;
				}
			}

			// Iterate over the frames using a cursor and an offset for
			// comparing frames a
			// certain number of frames apart
			for (
				int cursorOffset = minDataDistance; cursorOffset <= maxDataDistance
					&& cursorOffset < frameCount;
				cursorOffset++
			) {
				for (
					int frameCursor = 0; frameCursor < frameCount - cursorOffset;
					frameCursor += cursorIncrement
				) {
					int frameCursor2 = frameCursor + cursorOffset;

					setConfigOffsets(skeleton1.skeletonConfig, offsets);
					setConfigOffsets(skeleton2.skeletonConfig, offsets);

					if (randomizeFrameOrder) {
						skeleton1.setCursor(randomFrameIndices[frameCursor]);
						skeleton2.setCursor(randomFrameIndices[frameCursor2]);
					} else {
						skeleton1.setCursor(frameCursor);
						skeleton2.setCursor(frameCursor2);
					}

					skeleton1.updatePose();
					skeleton2.updatePose();

					float totalLength = getLengthSum(offsets);
					float curHeight = sumSelectConfigs(heightOffsets, offsets, null);
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

					Vector3f slideLeft = skeleton2
						.getComputedTracker(TrackerRole.LEFT_FOOT).position
							.subtract(
								skeleton1.getComputedTracker(TrackerRole.LEFT_FOOT).position
							);

					Vector3f slideRight = skeleton2
						.getComputedTracker(TrackerRole.RIGHT_FOOT).position
							.subtract(
								skeleton1
									.getComputedTracker(TrackerRole.RIGHT_FOOT).position
							);

					for (Entry<BoneType, Float> entry : offsets.entrySet()) {
						// Skip adjustment if the epoch is before starting (for
						// logging only)
						if (epoch < 0) {
							break;
						}

						float originalLength = entry.getValue();
						boolean isHeightVar = heightOffsets.contains(entry.getKey());

						float leftDotProduct = getDotProductDiff(
							skeleton1,
							skeleton2,
							entry.getKey(),
							false,
							slideLeft
						);

						float rightDotProduct = getDotProductDiff(
							skeleton1,
							skeleton2,
							entry.getKey(),
							true,
							slideRight
						);

						float dotLength = originalLength
							* ((leftDotProduct + rightDotProduct) / 2f);

						// Scale by the ratio for smooth adjustment and more
						// stable results
						float curAdjustVal = (adjustVal * -dotLength) / totalLength;
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

						if (newErrorDeriv < errorDeriv) {
							entry.setValue(newLength);
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

					if (scaleEachStep) {
						float stepHeight = sumSelectConfigs(heightOffsets, offsets, null);

						if (stepHeight > 0f) {
							float stepHeightDiff = targetHeight - stepHeight;
							for (Entry<BoneType, Float> entry : offsets.entrySet()) {
								// Only height variables
								if (
									entry.getKey() == BoneType.NECK
										|| !heightOffsets.contains(entry.getKey())
								)
									continue;

								float length = entry.getValue();

								// Multiply the diff by the length to height
								// ratio
								float adjVal = stepHeightDiff * (length / stepHeight);

								// Scale the length to fit the target height
								entry.setValue(Math.max(length + (adjVal / 2f), 0.01f));
							}
						}
					}
				}
			}

			// Calculate average error over the epoch
			float avgError = errorCount > 0 ? sumError / errorCount : -1f;
			LogManager.info("[AutoBone] Epoch " + (epoch + 1) + " average error: " + avgError);

			applyConfig(legacyConfigs);
			if (epochCallback != null) {
				epochCallback.accept(new Epoch(epoch + 1, numEpochs, avgError, legacyConfigs));
			}
		}

		float finalHeight = sumSelectConfigs(heightOffsets, offsets, null);
		LogManager
			.info(
				"[AutoBone] Target height: "
					+ targetHeight
					+ " New height: "
					+
					finalHeight
			);

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
			if (
				trackerFrame1 == null
					|| !trackerFrame1.hasData(TrackerFrameData.POSITION)
					|| trackerFrame1.designation.trackerRole.isEmpty()
			) {
				continue;
			}

			TrackerFrame trackerFrame2 = tracker.safeGetFrame(cursor2);
			if (
				trackerFrame2 == null
					|| !trackerFrame2.hasData(TrackerFrameData.POSITION)
					|| trackerFrame2.designation.trackerRole.isEmpty()
			) {
				continue;
			}

			Vector3f nodePos1 = skeleton1
				.getComputedTracker(trackerFrame1.designation.trackerRole.get()).position;
			if (nodePos1 == null) {
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
		BoneType config,
		float newLength
	) {
		setConfigOffset(skeleton1.skeletonConfig, config, newLength);
		skeleton1.updatePose();

		setConfigOffset(skeleton2.skeletonConfig, config, newLength);
		skeleton2.updatePose();
	}

	public String getLengthsString() {
		final StringBuilder configInfo = new StringBuilder();
		this.offsets.forEach((key, value) -> {
			if (configInfo.length() > 0) {
				configInfo.append(", ");
			}

			configInfo.append(key.toString() + ": " + StringUtils.prettyNumber(value * 100f, 2));
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
