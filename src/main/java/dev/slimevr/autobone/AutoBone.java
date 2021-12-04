package dev.slimevr.autobone;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

import dev.slimevr.poserecorder.PoseFrameSkeleton;
import dev.slimevr.poserecorder.PoseFrameTracker;
import dev.slimevr.poserecorder.PoseFrames;
import dev.slimevr.poserecorder.TrackerFrame;
import dev.slimevr.poserecorder.TrackerFrameData;
import io.eiren.util.ann.ThreadSafe;
import io.eiren.util.logging.LogManager;
import io.eiren.util.collections.FastList;
import io.eiren.vr.VRServer;
import io.eiren.vr.processor.HumanSkeleton;
import io.eiren.vr.processor.HumanSkeletonWithLegs;
import io.eiren.vr.processor.HumanSkeletonWithWaist;
import io.eiren.vr.trackers.TrackerPosition;
import io.eiren.vr.trackers.TrackerRole;
import io.eiren.vr.trackers.TrackerUtils;

public class AutoBone {
	
	public class Epoch {
		
		public final int epoch;
		public final float epochError;
		
		public Epoch(int epoch, float epochError) {
			this.epoch = epoch;
			this.epochError = epochError;
		}
		
		@Override
		public String toString() {
			return "Epoch: " + epoch + ", Epoch Error: " + epochError;
		}
	}
	
	public int cursorIncrement = 1;
	
	public int minDataDistance = 2;
	public int maxDataDistance = 32;
	
	public int numEpochs = 5;
	
	public float initialAdjustRate = 2.5f;
	public float adjustRateDecay = 1.01f;
	
	public float slideErrorFactor = 1.0f;
	public float offsetErrorFactor = 0.0f;
	public float proportionErrorFactor = 0.2f;
	public float heightErrorFactor = 0.1f;
	public float positionErrorFactor = 0.0f;
	public float positionOffsetErrorFactor = 0.0f;
	
	// For scaling distances, since smaller sizes will cause smaller distances
	private float totalLengthBase = 2f;

	// Human average is probably 1.1235 (SD 0.07)
	public float legBodyRatio = 1.1235f;
	// SD of 0.07, capture 68% within range
	public float legBodyRatioRange = 0.07f;
	
	// Assume these to be approximately half
	public float kneeLegRatio = 0.5f;
	public float chestTorsoRatio = 0.5f;
	
	protected final VRServer server;
	
	protected HumanSkeletonWithLegs skeleton = null;
	
	// This is filled by reloadConfigValues()
	public final HashMap<String, Float> configs = new HashMap<String, Float>();
	public final HashMap<String, Float> staticConfigs = new HashMap<String, Float>();
	
	public final FastList<String> heightConfigs = new FastList<String>(new String[]{"Neck", "Torso", "Legs length"});
	public final FastList<String> lengthConfigs = new FastList<String>(new String[]{"Head", "Neck", "Torso", "Hips width", "Legs length"});
	
	public AutoBone(VRServer server) {
		this.server = server;
		
		reloadConfigValues();
		
		server.addSkeletonUpdatedCallback(this::skeletonUpdated);
	}
	
	public void reloadConfigValues() {
		reloadConfigValues(null);
	}
	
	public void reloadConfigValues(List<PoseFrameTracker> trackers) {
		// Load torso configs
		staticConfigs.put("Head", server.config.getFloat("body.headShift", HumanSkeletonWithWaist.HEAD_SHIFT_DEFAULT));
		staticConfigs.put("Neck", server.config.getFloat("body.neckLength", HumanSkeletonWithWaist.NECK_LENGTH_DEFAULT));
		configs.put("Torso", server.config.getFloat("body.torsoLength", 0.7f));
		if(server.config.getBoolean("autobone.forceChestTracker", false) || (trackers != null && TrackerUtils.findTrackerForBodyPosition(trackers, TrackerPosition.CHEST) != null)) {
			// If force enabled or has a chest tracker
			staticConfigs.remove("Chest");
			configs.put("Chest", server.config.getFloat("body.chestDistance", 0.35f));
		} else {
			// Otherwise, make sure it's not used
			configs.remove("Chest");
			staticConfigs.put("Chest", server.config.getFloat("body.chestDistance", 0.35f));
		}
		if(server.config.getBoolean("autobone.forceHipTracker", false) || (trackers != null && TrackerUtils.findTrackerForBodyPosition(trackers, TrackerPosition.HIP) != null && TrackerUtils.findTrackerForBodyPosition(trackers, TrackerPosition.WAIST) != null)) {
			// If force enabled or has a hip tracker and waist tracker
			staticConfigs.remove("Waist");
			configs.put("Waist", server.config.getFloat("body.waistDistance", 0.1f));
		} else {
			// Otherwise, make sure it's not used
			configs.remove("Waist");
			staticConfigs.put("Waist", server.config.getFloat("body.waistDistance", 0.1f));
		}
		
		// Load leg configs
		staticConfigs.put("Hips width", server.config.getFloat("body.hipsWidth", HumanSkeletonWithLegs.HIPS_WIDTH_DEFAULT));
		configs.put("Legs length", server.config.getFloat("body.legsLength", 0.84f));
		configs.put("Knee height", server.config.getFloat("body.kneeHeight", 0.42f));

		// Keep "feet" at ankles
		staticConfigs.put("Foot length", 0f);
		staticConfigs.put("Foot offset", 0f);
	}
	
	@ThreadSafe
	public void skeletonUpdated(HumanSkeleton newSkeleton) {
		if(newSkeleton instanceof HumanSkeletonWithLegs) {
			skeleton = (HumanSkeletonWithLegs) newSkeleton;
			applyConfigToSkeleton(newSkeleton);
			LogManager.log.info("[AutoBone] Received updated skeleton");
		}
	}
	
	public void applyConfig() {
		if(!applyConfigToSkeleton(skeleton)) {
			// Unable to apply to skeleton, save directly
			saveConfigs();
		}
	}
	
	public boolean applyConfigToSkeleton(HumanSkeleton skeleton) {
		if(skeleton == null) {
			return false;
		}
		
		configs.forEach(skeleton::setSkeletonConfig);
		
		server.saveConfig();
		
		LogManager.log.info("[AutoBone] Configured skeleton bone lengths");
		return true;
	}
	
	private void setConfig(String name, String path) {
		Float value = configs.get(name);
		if(value != null) {
			server.config.setProperty(path, value);
		}
	}
	
	// This doesn't require a skeleton, therefore can be used if skeleton is null
	public void saveConfigs() {
		setConfig("Head", "body.headShift");
		setConfig("Neck", "body.neckLength");
		setConfig("Waist", "body.waistDistance");
		setConfig("Chest", "body.chestDistance");
		setConfig("Torso", "body.torsoLength");
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
		if(configs == null) {
			throw new NullPointerException("Argument \"configs\" must not be null");
		}
		
		Float configVal = configs.get(config);
		return configVal != null || configsAlt == null ? configVal : configsAlt.get(config);
	}

	public float sumSelectConfigs(List<String> selection, Map<String, Float> configs, Map<String, Float> configsAlt) {
		float sum = 0f;
		
		for (String config : selection) {
			Float length = getConfig(config, configs, configsAlt);
			if(length != null) {
				sum += length;
			}
		}
		
		return sum;
	}
	
	public float getLengthSum(Map<String, Float> configs) {
		return getLengthSum(configs, null);
	}

	public float getLengthSum(Map<String, Float> configs, Map<String, Float> configsAlt) {
		float length = 0f;
		
		if (configsAlt != null) {
			for(Entry<String, Float> config : configsAlt.entrySet()) {
				// If there isn't a duplicate config
				if (!configs.containsKey(config.getKey())) {
					length += config.getValue();
				}
			}
		}

		for(Float boneLength : configs.values()) {
			length += boneLength;
		}
		
		return length;
	}
	
	public float getMaxHmdHeight(PoseFrames frames) {
		float maxHeight = 0f;
		for(TrackerFrame[] frame : frames) {
			TrackerFrame hmd = TrackerUtils.findTrackerForBodyPosition(frame, TrackerPosition.HMD);
			if(hmd != null && hmd.hasData(TrackerFrameData.POSITION) && hmd.position.y > maxHeight) {
				maxHeight = hmd.position.y;
			}
		}
		return maxHeight;
	}
	
	public void processFrames(PoseFrames frames) {
		processFrames(frames, -1f);
	}
	
	public void processFrames(PoseFrames frames, Consumer<Epoch> epochCallback) {
		processFrames(frames, -1f, epochCallback);
	}
	
	public void processFrames(PoseFrames frames, float targetHeight) {
		processFrames(frames, true, targetHeight);
	}
	
	public void processFrames(PoseFrames frames, float targetHeight, Consumer<Epoch> epochCallback) {
		processFrames(frames, true, targetHeight, epochCallback);
	}
	
	public float processFrames(PoseFrames frames, boolean calcInitError, float targetHeight) {
		return processFrames(frames, calcInitError, targetHeight, null);
	}
	
	public float processFrames(PoseFrames frames, boolean calcInitError, float targetHeight, Consumer<Epoch> epochCallback) {
		final int frameCount = frames.getMaxFrameCount();

		List<PoseFrameTracker> trackers = frames.getTrackers();
		reloadConfigValues(trackers); // Reload configs and detect chest tracker from the first frame
		
		final PoseFrameSkeleton skeleton1 = new PoseFrameSkeleton(trackers, null, configs, staticConfigs);
		final PoseFrameSkeleton skeleton2 = new PoseFrameSkeleton(trackers, null, configs, staticConfigs);
		
		// If target height isn't specified, auto-detect
		if(targetHeight < 0f) {
			if(skeleton != null) {
				targetHeight = sumSelectConfigs(heightConfigs, skeleton.getSkeletonConfig(), staticConfigs);
				LogManager.log.warning("[AutoBone] Target height loaded from skeleton (Make sure you reset before running!): " + targetHeight);
			} else {
				float hmdHeight = getMaxHmdHeight(frames);
				if(hmdHeight <= 0.50f) {
					LogManager.log.warning("[AutoBone] Max headset height detected (Value seems too low, did you not stand up straight while measuring?): " + hmdHeight);
				} else {
					LogManager.log.info("[AutoBone] Max headset height detected: " + hmdHeight);
				}
				
				// Estimate target height from HMD height
				targetHeight = hmdHeight;
			}
		}
		
		for(int epoch = calcInitError ? -1 : 0; epoch < numEpochs; epoch++) {
			float sumError = 0f;
			int errorCount = 0;
			
			float adjustRate = epoch >= 0 ? (initialAdjustRate / FastMath.pow(adjustRateDecay, epoch)) : 0f;
			
			for(int cursorOffset = minDataDistance; cursorOffset <= maxDataDistance && cursorOffset < frameCount; cursorOffset++) {
				for(int frameCursor = 0; frameCursor < frameCount - cursorOffset; frameCursor += cursorIncrement) {
					int frameCursor2 = frameCursor + cursorOffset;

					skeleton1.setSkeletonConfigs(configs);
					skeleton2.setSkeletonConfigs(configs);
					
					skeleton1.setCursor(frameCursor);
					skeleton1.updatePose();

					skeleton2.setCursor(frameCursor2);
					skeleton2.updatePose();
					
					float totalLength = getLengthSum(configs);
					float curHeight = sumSelectConfigs(heightConfigs, configs, staticConfigs);
					float scaleLength = sumSelectConfigs(lengthConfigs, configs, staticConfigs);
					float errorDeriv = getErrorDeriv(frames, frameCursor, frameCursor2, skeleton1, skeleton2, targetHeight - curHeight, totalLengthBase / scaleLength);
					float error = errorFunc(errorDeriv);
					
					// In case of fire
					if(Float.isNaN(error) || Float.isInfinite(error)) {
						// Extinguish
						LogManager.log.warning("[AutoBone] Error value is invalid, resetting variables to recover");
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
					
					for(Entry<String, Float> entry : configs.entrySet()) {
						// Skip adjustment if the epoch is before starting (for logging only)
						if(epoch < 0) {
							break;
						}
						
						float originalLength = entry.getValue();
						
						// Try positive and negative adjustments
						boolean isHeightVar = heightConfigs.contains(entry.getKey());
						boolean isLengthVar = lengthConfigs.contains(entry.getKey());
						float minError = errorDeriv;
						float finalNewLength = -1f;
						for(int i = 0; i < 2; i++) {
							// Scale by the ratio for smooth adjustment and more stable results
							float curAdjustVal = ((i == 0 ? adjustVal : -adjustVal) * originalLength) / totalLength;
							float newLength = originalLength + curAdjustVal;
							
							// No small or negative numbers!!! Bad algorithm!
							if(newLength < 0.01f) {
								continue;
							}
							
							updateSkeletonBoneLength(skeleton1, skeleton2, entry.getKey(), newLength);
							
							float newHeight = isHeightVar ? curHeight + curAdjustVal : curHeight;
							float newScaleLength = isLengthVar ? scaleLength + curAdjustVal : scaleLength;
							float newErrorDeriv = getErrorDeriv(frames, frameCursor, frameCursor2, skeleton1, skeleton2, targetHeight - newHeight, totalLengthBase / newScaleLength);
							
							if(newErrorDeriv < minError) {
								minError = newErrorDeriv;
								finalNewLength = newLength;
							}
						}
						
						if(finalNewLength > 0f) {
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
			
			if(epochCallback != null) {
				epochCallback.accept(new Epoch(epoch + 1, avgError));
			}
		}
		
		float finalHeight = sumSelectConfigs(heightConfigs, configs, staticConfigs);
		LogManager.log.info("[AutoBone] Target height: " + targetHeight + " New height: " + finalHeight);
		
		return FastMath.abs(finalHeight - targetHeight);
	}
	
	// The change in position of the ankle over time
	protected float getSlideErrorDeriv(PoseFrameSkeleton skeleton1, PoseFrameSkeleton skeleton2) {
		float slideLeft = skeleton1.getComputedTracker(TrackerRole.LEFT_FOOT).position.distance(skeleton2.getComputedTracker(TrackerRole.LEFT_FOOT).position);
		float slideRight = skeleton1.getComputedTracker(TrackerRole.RIGHT_FOOT).position.distance(skeleton2.getComputedTracker(TrackerRole.RIGHT_FOOT).position);
		
		// Divide by 4 to halve and average, it's halved because you want to approach a midpoint, not the other point
		return (slideLeft + slideRight) / 4f;
	}
	
	// The offset between both feet at one instant and over time
	protected float getOffsetErrorDeriv(PoseFrameSkeleton skeleton1, PoseFrameSkeleton skeleton2) {
		float skeleton1Left = skeleton1.getComputedTracker(TrackerRole.LEFT_FOOT).position.getY();
		float skeleton1Right = skeleton1.getComputedTracker(TrackerRole.RIGHT_FOOT).position.getY();
		
		float skeleton2Left = skeleton2.getComputedTracker(TrackerRole.LEFT_FOOT).position.getY();
		float skeleton2Right = skeleton2.getComputedTracker(TrackerRole.RIGHT_FOOT).position.getY();
		
		float dist1 = FastMath.abs(skeleton1Left - skeleton1Right);
		float dist2 = FastMath.abs(skeleton2Left - skeleton2Right);
		
		float dist3 = FastMath.abs(skeleton1Left - skeleton2Right);
		float dist4 = FastMath.abs(skeleton2Left - skeleton1Right);
		
		float dist5 = FastMath.abs(skeleton1Left - skeleton2Left);
		float dist6 = FastMath.abs(skeleton1Right - skeleton2Right);
		
		// Divide by 12 to halve and average, it's halved because you want to approach a midpoint, not the other point
		return (dist1 + dist2 + dist3 + dist4 + dist5 + dist6) / 12f;
	}
	
	// The distance from average human proportions
	protected float getProportionErrorDeriv(PoseFrameSkeleton skeleton) {
		Float neckLength = skeleton.getSkeletonConfig("Neck");
		Float chestLength = skeleton.getSkeletonConfig("Chest");
		Float torsoLength = skeleton.getSkeletonConfig("Torso");
		Float legsLength = skeleton.getSkeletonConfig("Legs length");
		Float kneeHeight = skeleton.getSkeletonConfig("Knee height");

		float chestTorso = chestLength != null && torsoLength != null ? FastMath.abs((chestLength / torsoLength) - chestTorsoRatio) : 0f;
		float legBody = legsLength != null && torsoLength != null && neckLength != null ? FastMath.abs((legsLength / (torsoLength + neckLength)) - legBodyRatio) : 0f;
		float kneeLeg = kneeHeight != null && legsLength != null ? FastMath.abs((kneeHeight / legsLength) - kneeLegRatio) : 0f;
		
		if(legBody <= legBodyRatioRange) {
			legBody = 0f;
		} else {
			legBody -= legBodyRatioRange;
		}
		
		return (chestTorso + legBody + kneeLeg) / 3f;
	}
	
	// The distance of any points to the corresponding absolute position
	protected float getPositionErrorDeriv(PoseFrames frames, int cursor, PoseFrameSkeleton skeleton) {
		float offset = 0f;
		int offsetCount = 0;
		
		List<PoseFrameTracker> trackers = frames.getTrackers();
		for (int i = 0; i < trackers.size(); i++) {
			PoseFrameTracker tracker = trackers.get(i);

			TrackerFrame trackerFrame = tracker.safeGetFrame(cursor);
			if(trackerFrame == null || !trackerFrame.hasData(TrackerFrameData.POSITION)) {
				continue;
			}
			
			Vector3f nodePos = skeleton.getComputedTracker(trackerFrame.designation.trackerRole).position;
			if(nodePos != null) {
				offset += FastMath.abs(nodePos.distance(trackerFrame.position));
				offsetCount++;
			}
		}
		
		return offsetCount > 0 ? offset / offsetCount : 0f;
	}
	
	// The difference between offset of absolute position and the corresponding point over time
	protected float getPositionOffsetErrorDeriv(PoseFrames frames, int cursor1, int cursor2, PoseFrameSkeleton skeleton1, PoseFrameSkeleton skeleton2) {
		float offset = 0f;
		int offsetCount = 0;
		
		List<PoseFrameTracker> trackers = frames.getTrackers();
		for (int i = 0; i < trackers.size(); i++) {
			PoseFrameTracker tracker = trackers.get(i);

			TrackerFrame trackerFrame1 = tracker.safeGetFrame(cursor1);
			if(trackerFrame1 == null || !trackerFrame1.hasData(TrackerFrameData.POSITION)) {
				continue;
			}
			
			TrackerFrame trackerFrame2 = tracker.safeGetFrame(cursor2);
			if(trackerFrame2 == null || !trackerFrame2.hasData(TrackerFrameData.POSITION)) {
				continue;
			}
			
			Vector3f nodePos1 = skeleton1.getComputedTracker(trackerFrame1.designation.trackerRole).position;
			if(nodePos1 == null) {
				continue;
			}
			
			Vector3f nodePos2 = skeleton2.getComputedTracker(trackerFrame2.designation.trackerRole).position;
			if(nodePos2 == null) {
				continue;
			}
			
			float dist1 = FastMath.abs(nodePos1.distance(trackerFrame1.position));
			float dist2 = FastMath.abs(nodePos2.distance(trackerFrame2.position));
			
			offset += FastMath.abs(dist2 - dist1);
			offsetCount++;
		}
		
		return offsetCount > 0 ? offset / offsetCount : 0f;
	}
	
	protected float getErrorDeriv(PoseFrames frames, int cursor1, int cursor2, PoseFrameSkeleton skeleton1, PoseFrameSkeleton skeleton2, float heightChange, float distScale) {
		float totalError = 0f;
		float sumWeight = 0f;
		
		if(slideErrorFactor > 0f) {
			totalError += getSlideErrorDeriv(skeleton1, skeleton2) * distScale * slideErrorFactor;
			sumWeight += slideErrorFactor;
		}
		
		if(offsetErrorFactor > 0f) {
			totalError += getOffsetErrorDeriv(skeleton1, skeleton2) * distScale * offsetErrorFactor;
			sumWeight += offsetErrorFactor;
		}
		
		if(proportionErrorFactor > 0f) {
			// Either skeleton will work fine, skeleton1 is used as a default
			totalError += getProportionErrorDeriv(skeleton1) * proportionErrorFactor;
			sumWeight += proportionErrorFactor;
		}
		
		if(heightErrorFactor > 0f) {
			totalError += FastMath.abs(heightChange) * heightErrorFactor;
			sumWeight += heightErrorFactor;
		}
		
		if(positionErrorFactor > 0f) {
			totalError += (getPositionErrorDeriv(frames, cursor1, skeleton1) + getPositionErrorDeriv(frames, cursor2, skeleton2) / 2f) * distScale * positionErrorFactor;
			sumWeight += positionErrorFactor;
		}
		
		if(positionOffsetErrorFactor > 0f) {
			totalError += getPositionOffsetErrorDeriv(frames, cursor1, cursor2, skeleton1, skeleton2) * distScale * positionOffsetErrorFactor;
			sumWeight += positionOffsetErrorFactor;
		}
		
		// Minimize sliding, minimize foot height offset, minimize change in total height
		return sumWeight > 0f ? totalError / sumWeight : 0f;
	}
	
	// Mean square error function
	protected static float errorFunc(float errorDeriv) {
		return 0.5f * (errorDeriv * errorDeriv);
	}
	
	protected void updateSkeletonBoneLength(PoseFrameSkeleton skeleton1, PoseFrameSkeleton skeleton2, String joint, float newLength) {
		skeleton1.setSkeletonConfig(joint, newLength, true);
		skeleton2.setSkeletonConfig(joint, newLength, true);
	}
}
