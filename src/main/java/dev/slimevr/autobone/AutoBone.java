package dev.slimevr.autobone;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.jme3.math.Vector3f;

import dev.slimevr.poserecorder.PoseFrame;
import dev.slimevr.poserecorder.TrackerFrame;
import dev.slimevr.poserecorder.TrackerFrameData;
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
	
	// Human average is probably 1.1235 (SD 0.07)
	public float legBodyRatio = 1.1235f;
	// SD of 0.07, capture 68% within range
	public float legBodyRatioRange = 0.07f;
	
	// Assume these to be approximately half
	public float kneeLegRatio = 0.5f;
	public float chestWaistRatio = 0.5f;
	
	protected final VRServer server;
	
	protected HumanSkeletonWithLegs skeleton = null;
	
	// This is filled by reloadConfigValues()
	public final HashMap<String, Float> configs = new HashMap<String, Float>();
	public final HashMap<String, Float> staticConfigs = new HashMap<String, Float>();
	
	public final FastList<String> heightConfigs = new FastList<String>(new String[]{"Neck", "Waist", "Legs length"
	});
	
	public AutoBone(VRServer server) {
		this.server = server;
		
		reloadConfigValues();
		
		server.addSkeletonUpdatedCallback(this::skeletonUpdated);
	}
	
	public void reloadConfigValues() {
		reloadConfigValues(null);
	}
	
	public void reloadConfigValues(TrackerFrame[] frame) {
		// Load waist configs
		staticConfigs.put("Head", server.config.getFloat("body.headShift", HumanSkeletonWithWaist.HEAD_SHIFT_DEFAULT));
		staticConfigs.put("Neck", server.config.getFloat("body.neckLength", HumanSkeletonWithWaist.NECK_LENGTH_DEFAULT));
		configs.put("Waist", server.config.getFloat("body.waistDistance", 0.85f));
		
		if(server.config.getBoolean("autobone.forceChestTracker", false) || (frame != null && TrackerUtils.findTrackerForBodyPosition(frame, TrackerBodyPosition.CHEST) != null) || TrackerUtils.findTrackerForBodyPosition(server.getAllTrackers(), TrackerBodyPosition.CHEST) != null) {
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
	
	public float getHeight(Map<String, Float> configs) {
		return getHeight(configs, null);
	}
	
	public float getHeight(Map<String, Float> configs, Map<String, Float> configsAlt) {
		float height = 0f;
		
		for(String heightConfig : heightConfigs) {
			Float length = getConfig(heightConfig, configs, configsAlt);
			if(length != null) {
				height += length;
			}
		}
		
		return height;
	}
	
	public float getLengthSum(Map<String, Float> configs) {
		float length = 0f;
		
		for(float boneLength : configs.values()) {
			length += boneLength;
		}
		
		return length;
	}
	
	public float getMaxHmdHeight(PoseFrame frames) {
		float maxHeight = 0f;
		for(TrackerFrame[] frame : frames) {
			TrackerFrame hmd = TrackerUtils.findTrackerForBodyPosition(frame, TrackerBodyPosition.HMD);
			if(hmd != null && hmd.hasData(TrackerFrameData.POSITION) && hmd.position.y > maxHeight) {
				maxHeight = hmd.position.y;
			}
		}
		return maxHeight;
	}
	
	public void processFrames(PoseFrame frames) {
		processFrames(frames, -1f);
	}
	
	public void processFrames(PoseFrame frames, Consumer<Epoch> epochCallback) {
		processFrames(frames, -1f, epochCallback);
	}
	
	public void processFrames(PoseFrame frames, float targetHeight) {
		processFrames(frames, true, targetHeight);
	}
	
	public void processFrames(PoseFrame frames, float targetHeight, Consumer<Epoch> epochCallback) {
		processFrames(frames, true, targetHeight, epochCallback);
	}
	
	public float processFrames(PoseFrame frames, boolean calcInitError, float targetHeight) {
		return processFrames(frames, calcInitError, targetHeight, null);
	}
	
	public float processFrames(PoseFrame frames, boolean calcInitError, float targetHeight, Consumer<Epoch> epochCallback) {
		final int frameCount = frames.getMaxFrameCount();
		
		final SimpleSkeleton skeleton1 = new SimpleSkeleton(configs, staticConfigs);
		final TrackerFrame[] trackerBuffer1 = new TrackerFrame[frames.getTrackerCount()];
		
		frames.getFrames(0, trackerBuffer1);
		reloadConfigValues(trackerBuffer1); // Reload configs and detect chest tracker from the first frame
		
		final SimpleSkeleton skeleton2 = new SimpleSkeleton(configs, staticConfigs);
		final TrackerFrame[] trackerBuffer2 = new TrackerFrame[frames.getTrackerCount()];
		
		// If target height isn't specified, auto-detect
		if(targetHeight < 0f) {
			if(skeleton != null) {
				targetHeight = getHeight(skeleton.getSkeletonConfig());
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
			
			float adjustRate = epoch >= 0 ? (float) (initialAdjustRate / Math.pow(adjustRateDecay, epoch)) : 0f;
			
			for(int cursorOffset = minDataDistance; cursorOffset <= maxDataDistance && cursorOffset < frameCount; cursorOffset++) {
				for(int frameCursor = 0; frameCursor < frameCount - cursorOffset; frameCursor += cursorIncrement) {
					frames.getFrames(frameCursor, trackerBuffer1);
					frames.getFrames(frameCursor + cursorOffset, trackerBuffer2);
					
					skeleton1.setSkeletonConfigs(configs);
					skeleton2.setSkeletonConfigs(configs);
					
					skeleton1.setPoseFromFrame(trackerBuffer1);
					skeleton2.setPoseFromFrame(trackerBuffer2);
					
					float totalLength = getLengthSum(configs);
					float curHeight = getHeight(configs, staticConfigs);
					float errorDeriv = getErrorDeriv(trackerBuffer1, trackerBuffer2, skeleton1, skeleton2, targetHeight - curHeight);
					float error = errorFunc(errorDeriv);
					
					// In case of fire
					if(Float.isNaN(error) || Float.isInfinite(error)) {
						// Extinguish
						LogManager.log.warning("[AutoBone] Error value is invalid, resetting variables to recover");
						reloadConfigValues(trackerBuffer1);
						
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
					
					for(Entry<String, Float> entry : configs.entrySet()) {
						// Skip adjustment if the epoch is before starting (for logging only)
						if(epoch < 0) {
							break;
						}
						
						float originalLength = entry.getValue();
						
						// Try positive and negative adjustments
						boolean isHeightVar = heightConfigs.contains(entry.getKey());
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
							float newErrorDeriv = getErrorDeriv(trackerBuffer1, trackerBuffer2, skeleton1, skeleton2, targetHeight - newHeight);
							
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
		
		float finalHeight = getHeight(configs, staticConfigs);
		LogManager.log.info("[AutoBone] Target height: " + targetHeight + " New height: " + finalHeight);
		
		return Math.abs(finalHeight - targetHeight);
	}
	
	// The change in position of the ankle over time
	protected float getSlideErrorDeriv(SimpleSkeleton skeleton1, SimpleSkeleton skeleton2) {
		float slideLeft = skeleton1.getNodePosition(TrackerBodyPosition.LEFT_ANKLE).distance(skeleton2.getNodePosition(TrackerBodyPosition.LEFT_ANKLE));
		float slideRight = skeleton1.getNodePosition(TrackerBodyPosition.RIGHT_ANKLE).distance(skeleton2.getNodePosition(TrackerBodyPosition.RIGHT_ANKLE));
		
		// Divide by 4 to halve and average, it's halved because you want to approach a midpoint, not the other point
		return (slideLeft + slideRight) / 4f;
	}
	
	// The offset between both feet at one instant and over time
	protected float getOffsetErrorDeriv(SimpleSkeleton skeleton1, SimpleSkeleton skeleton2) {
		float skeleton1Left = skeleton1.getNodePosition(TrackerBodyPosition.LEFT_ANKLE).getY();
		float skeleton1Right = skeleton1.getNodePosition(TrackerBodyPosition.RIGHT_ANKLE).getY();
		
		float skeleton2Left = skeleton2.getNodePosition(TrackerBodyPosition.LEFT_ANKLE).getY();
		float skeleton2Right = skeleton2.getNodePosition(TrackerBodyPosition.RIGHT_ANKLE).getY();
		
		float dist1 = Math.abs(skeleton1Left - skeleton1Right);
		float dist2 = Math.abs(skeleton2Left - skeleton2Right);
		
		float dist3 = Math.abs(skeleton1Left - skeleton2Right);
		float dist4 = Math.abs(skeleton2Left - skeleton1Right);
		
		float dist5 = Math.abs(skeleton1Left - skeleton2Left);
		float dist6 = Math.abs(skeleton1Right - skeleton2Right);
		
		// Divide by 12 to halve and average, it's halved because you want to approach a midpoint, not the other point
		return (dist1 + dist2 + dist3 + dist4 + dist5 + dist6) / 12f;
	}
	
	// The distance from average human proportions
	protected float getProportionErrorDeriv(SimpleSkeleton skeleton) {
		Float neckLength = skeleton.getSkeletonConfig("Neck");
		Float chestLength = skeleton.getSkeletonConfig("Chest");
		Float waistLength = skeleton.getSkeletonConfig("Waist");
		Float legsLength = skeleton.getSkeletonConfig("Legs length");
		Float kneeHeight = skeleton.getSkeletonConfig("Knee height");
		
		float chestWaist = chestLength != null && waistLength != null ? Math.abs((chestLength / waistLength) - chestWaistRatio) : 0f;
		float legBody = legsLength != null && waistLength != null && neckLength != null ? Math.abs((legsLength / (waistLength + neckLength)) - legBodyRatio) : 0f;
		float kneeLeg = kneeHeight != null && legsLength != null ? Math.abs((kneeHeight / legsLength) - kneeLegRatio) : 0f;
		
		if(legBody <= legBodyRatioRange) {
			legBody = 0f;
		} else {
			legBody -= legBodyRatioRange;
		}
		
		return (chestWaist + legBody + kneeLeg) / 3f;
	}
	
	// The distance of any points to the corresponding absolute position
	protected float getPositionErrorDeriv(TrackerFrame[] frame, SimpleSkeleton skeleton) {
		float offset = 0f;
		int offsetCount = 0;
		
		for(TrackerFrame trackerFrame : frame) {
			if(trackerFrame == null || !trackerFrame.hasData(TrackerFrameData.POSITION)) {
				continue;
			}
			
			Vector3f nodePos = skeleton.getNodePosition(trackerFrame.designation.designation);
			if(nodePos != null) {
				offset += Math.abs(nodePos.distance(trackerFrame.position));
				offsetCount++;
			}
		}
		
		return offsetCount > 0 ? offset / offsetCount : 0f;
	}
	
	// The difference between offset of absolute position and the corresponding point over time
	protected float getPositionOffsetErrorDeriv(TrackerFrame[] frame1, TrackerFrame[] frame2, SimpleSkeleton skeleton1, SimpleSkeleton skeleton2) {
		float offset = 0f;
		int offsetCount = 0;
		
		for(TrackerFrame trackerFrame1 : frame1) {
			if(trackerFrame1 == null || !trackerFrame1.hasData(TrackerFrameData.POSITION)) {
				continue;
			}
			
			TrackerFrame trackerFrame2 = TrackerUtils.findTrackerForBodyPosition(frame2, trackerFrame1.designation);
			if(trackerFrame2 == null || !trackerFrame2.hasData(TrackerFrameData.POSITION)) {
				continue;
			}
			
			Vector3f nodePos1 = skeleton1.getNodePosition(trackerFrame1.designation);
			if(nodePos1 == null) {
				continue;
			}
			
			Vector3f nodePos2 = skeleton2.getNodePosition(trackerFrame2.designation);
			if(nodePos2 == null) {
				continue;
			}
			
			float dist1 = Math.abs(nodePos1.distance(trackerFrame1.position));
			float dist2 = Math.abs(nodePos2.distance(trackerFrame2.position));
			
			offset += Math.abs(dist2 - dist1);
			offsetCount++;
		}
		
		return offsetCount > 0 ? offset / offsetCount : 0f;
	}
	
	protected float getErrorDeriv(TrackerFrame[] frame1, TrackerFrame[] frame2, SimpleSkeleton skeleton1, SimpleSkeleton skeleton2, float heightChange) {
		float totalError = 0f;
		float sumWeight = 0f;
		
		if(slideErrorFactor > 0f) {
			totalError += getSlideErrorDeriv(skeleton1, skeleton2) * slideErrorFactor;
			sumWeight += slideErrorFactor;
		}
		
		if(offsetErrorFactor > 0f) {
			totalError += getOffsetErrorDeriv(skeleton1, skeleton2) * offsetErrorFactor;
			sumWeight += offsetErrorFactor;
		}
		
		if(proportionErrorFactor > 0f) {
			// Either skeleton will work fine, skeleton1 is used as a default
			totalError += getProportionErrorDeriv(skeleton1) * proportionErrorFactor;
			sumWeight += proportionErrorFactor;
		}
		
		if(heightErrorFactor > 0f) {
			totalError += Math.abs(heightChange) * heightErrorFactor;
			sumWeight += heightErrorFactor;
		}
		
		if(positionErrorFactor > 0f) {
			totalError += (getPositionErrorDeriv(frame1, skeleton1) + getPositionErrorDeriv(frame2, skeleton2) / 2f) * positionErrorFactor;
			sumWeight += positionErrorFactor;
		}
		
		if(positionOffsetErrorFactor > 0f) {
			totalError += getPositionOffsetErrorDeriv(frame1, frame2, skeleton1, skeleton2) * positionOffsetErrorFactor;
			sumWeight += positionOffsetErrorFactor;
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
