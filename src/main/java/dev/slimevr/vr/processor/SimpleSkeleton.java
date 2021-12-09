package dev.slimevr.vr.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import io.eiren.util.collections.FastList;
import io.eiren.vr.VRServer;
import io.eiren.vr.processor.ComputedHumanPoseTracker;
import io.eiren.vr.processor.ComputedHumanPoseTrackerPosition;
import io.eiren.vr.processor.TransformNode;
import io.eiren.vr.trackers.Tracker;
import io.eiren.vr.trackers.TrackerPosition;
import io.eiren.vr.trackers.TrackerRole;
import io.eiren.vr.trackers.TrackerStatus;
import io.eiren.vr.trackers.TrackerUtils;

public class SimpleSkeleton implements SkeletonConfigCallback {
	
	//#region Upper body nodes (torso)
	protected final TransformNode hmdNode = new TransformNode("HMD", false);
	protected final TransformNode headNode = new TransformNode("Head", false);
	protected final TransformNode neckNode = new TransformNode("Neck", false);
	protected final TransformNode chestNode = new TransformNode("Chest", false);
	protected final TransformNode waistNode = new TransformNode("Waist", false);
	protected final TransformNode hipNode = new TransformNode("Hip", false);
	protected final TransformNode trackerWaistNode = new TransformNode("Waist-Tracker", false);
	//#endregion
	
	//#region Lower body nodes (legs)
	protected final TransformNode leftHipNode = new TransformNode("Left-Hip", false);
	protected final TransformNode leftKneeNode = new TransformNode("Left-Knee", false);
	protected final TransformNode leftAnkleNode = new TransformNode("Left-Ankle", false);
	protected final TransformNode leftFootNode = new TransformNode("Left-Foot", false);

	protected final TransformNode rightHipNode = new TransformNode("Right-Hip", false);
	protected final TransformNode rightKneeNode = new TransformNode("Right-Knee", false);
	protected final TransformNode rightAnkleNode = new TransformNode("Right-Ankle", false);
	protected final TransformNode rightFootNode = new TransformNode("Right-Foot", false);
	
	protected float minKneePitch = 0f * FastMath.DEG_TO_RAD;
	protected float maxKneePitch = 90f * FastMath.DEG_TO_RAD;
	
	protected float kneeLerpFactor = 0.5f;
	//#endregion
	
	//#region Tracker Input
	protected Tracker hmdTracker;
	protected Tracker chestTracker;
	protected Tracker waistTracker;
	protected Tracker hipTracker;

	protected Tracker leftLegTracker;
	protected Tracker leftAnkleTracker;
	protected Tracker leftFootTracker;

	protected Tracker rightLegTracker;
	protected Tracker rightAnkleTracker;
	protected Tracker rightFootTracker;
	//#endregion

	//#region Tracker Output
	protected ComputedHumanPoseTracker computedChestTracker;
	protected ComputedHumanPoseTracker computedWaistTracker;

	protected ComputedHumanPoseTracker computedLeftKneeTracker;
	protected ComputedHumanPoseTracker computedLeftFootTracker;
	
	protected ComputedHumanPoseTracker computedRightKneeTracker;
	protected ComputedHumanPoseTracker computedRightFootTracker;
	//#endregion
	
	protected boolean extendedPelvisModel = true;
	protected boolean extendedKneeModel = false;

	public final SkeletonConfig skeletonConfig;

	//#region Buffers
	private Vector3f posBuf = new Vector3f();

	private Quaternion rotBuf1 = new Quaternion();
	private Quaternion rotBuf2 = new Quaternion();

	protected final Vector3f hipVector = new Vector3f();
	protected final Vector3f ankleVector = new Vector3f();

	protected final Quaternion kneeRotation = new Quaternion();
	//#endregion
	
	//#region Constructors
	protected SimpleSkeleton(List<? extends ComputedHumanPoseTracker> computedTrackers) {
		//#region Assemble skeleton to hip
		hmdNode.attachChild(headNode);
		headNode.attachChild(neckNode);
		neckNode.attachChild(chestNode);
		chestNode.attachChild(waistNode);
		waistNode.attachChild(hipNode);
		hipNode.attachChild(trackerWaistNode);
		//#endregion
		
		//#region Assemble skeleton to feet
		hipNode.attachChild(leftHipNode);
		hipNode.attachChild(rightHipNode);

		leftHipNode.attachChild(leftKneeNode);
		rightHipNode.attachChild(rightKneeNode);

		leftKneeNode.attachChild(leftAnkleNode);
		rightKneeNode.attachChild(rightAnkleNode);

		leftAnkleNode.attachChild(leftFootNode);
		rightAnkleNode.attachChild(rightFootNode);
		//#endregion

		// Set default skeleton configuration (callback automatically sets initial offsets)
		skeletonConfig = new SkeletonConfig(true, this);

		if (computedTrackers != null) {
			setComputedTrackers(computedTrackers);
		}
		fillNullComputedTrackers(true);
	}

	public SimpleSkeleton(VRServer server, List<? extends ComputedHumanPoseTracker> computedTrackers) {
		this(computedTrackers);
		setTrackersFromServer(server);
		skeletonConfig.loadFromConfig(server.config);
	}
	
	public SimpleSkeleton(List<? extends Tracker> trackers, List<? extends ComputedHumanPoseTracker> computedTrackers) {
		this(computedTrackers);

		if (trackers != null) {
			setTrackersFromList(trackers);
		} else {
			setTrackersFromList(new FastList<Tracker>(0));
		}
	}

	public SimpleSkeleton(List<? extends Tracker> trackers, List<? extends ComputedHumanPoseTracker> computedTrackers, Map<String, Float> configs, Map<String, Float> altConfigs) {
		// Initialize
		this(trackers, computedTrackers);
		
		// Set configs
		if(altConfigs != null) {
			// Set alts first, so if there's any overlap it doesn't affect the values
			altConfigs.forEach(skeletonConfig::setConfig);
		}
		configs.forEach(skeletonConfig::setConfig);
	}
	
	public SimpleSkeleton(List<? extends Tracker> trackers, List<? extends ComputedHumanPoseTracker> computedTrackers, Map<String, Float> configs) {
		this(trackers, computedTrackers, configs, null);
	}
	//#endregion

	//#region Set Trackers
	public void setTrackersFromList(List<? extends Tracker> trackers, boolean setHmd) {
		if (setHmd) {
			this.hmdTracker = TrackerUtils.findTrackerForBodyPosition(trackers, TrackerPosition.HMD);
		}

		this.chestTracker = TrackerUtils.findTrackerForBodyPositionOrEmpty(trackers, TrackerPosition.CHEST, TrackerPosition.WAIST, TrackerPosition.HIP);
		this.waistTracker = TrackerUtils.findTrackerForBodyPositionOrEmpty(trackers, TrackerPosition.WAIST, TrackerPosition.CHEST, TrackerPosition.HIP);
		this.hipTracker = TrackerUtils.findTrackerForBodyPositionOrEmpty(trackers, TrackerPosition.HIP, TrackerPosition.WAIST, TrackerPosition.CHEST);

		this.leftLegTracker = TrackerUtils.findTrackerForBodyPositionOrEmpty(trackers, TrackerPosition.LEFT_LEG, TrackerPosition.LEFT_ANKLE, null);
		this.leftAnkleTracker = TrackerUtils.findTrackerForBodyPositionOrEmpty(trackers, TrackerPosition.LEFT_ANKLE, TrackerPosition.LEFT_LEG, null);
		this.leftFootTracker = TrackerUtils.findTrackerForBodyPosition(trackers, TrackerPosition.LEFT_FOOT);

		this.rightLegTracker = TrackerUtils.findTrackerForBodyPositionOrEmpty(trackers, TrackerPosition.RIGHT_LEG, TrackerPosition.RIGHT_ANKLE, null);
		this.rightAnkleTracker = TrackerUtils.findTrackerForBodyPositionOrEmpty(trackers, TrackerPosition.RIGHT_ANKLE, TrackerPosition.RIGHT_LEG, null);
		this.rightFootTracker = TrackerUtils.findTrackerForBodyPosition(trackers, TrackerPosition.RIGHT_FOOT);
	}

	public void setTrackersFromList(List<? extends Tracker> trackers) {
		setTrackersFromList(trackers, true);
	}

	public void setTrackersFromServer(VRServer server) {
		this.hmdTracker = server.hmdTracker;
		setTrackersFromList(server.getAllTrackers(), false);
	}

	public void setComputedTracker(ComputedHumanPoseTracker tracker) {
		switch (tracker.getTrackerRole()) {
		case CHEST:
			computedChestTracker = tracker;
			break;
		case WAIST:
			computedWaistTracker = tracker;
			break;

		case LEFT_KNEE:
			computedLeftKneeTracker = tracker;
			break;
		case LEFT_FOOT:
			computedLeftFootTracker = tracker;
			break;

		case RIGHT_KNEE:
			computedRightKneeTracker = tracker;
			break;
		case RIGHT_FOOT:
			computedRightFootTracker = tracker;
			break;
		}
	}

	public void setComputedTrackers(List<? extends ComputedHumanPoseTracker> trackers) {
		for(int i = 0; i < trackers.size(); ++i) {
			setComputedTracker(trackers.get(i));
		}
	}

	public void setComputedTrackersAndFillNull(List<? extends ComputedHumanPoseTracker> trackers, boolean onlyFillWaistAndFeet) {
		setComputedTrackers(trackers);
		fillNullComputedTrackers(onlyFillWaistAndFeet);
	}

	public void fillNullComputedTrackers(boolean onlyFillWaistAndFeet) {
		if (computedWaistTracker == null) {
			computedWaistTracker = new ComputedHumanPoseTracker(Tracker.getNextLocalTrackerId(), ComputedHumanPoseTrackerPosition.WAIST, TrackerRole.WAIST);
			computedWaistTracker.setStatus(TrackerStatus.OK);
		}

		if (computedLeftFootTracker == null) {
			computedLeftFootTracker = new ComputedHumanPoseTracker(Tracker.getNextLocalTrackerId(), ComputedHumanPoseTrackerPosition.LEFT_FOOT, TrackerRole.LEFT_FOOT);
			computedLeftFootTracker.setStatus(TrackerStatus.OK);
		}

		if (computedRightFootTracker == null) {
			computedRightFootTracker = new ComputedHumanPoseTracker(Tracker.getNextLocalTrackerId(), ComputedHumanPoseTrackerPosition.RIGHT_FOOT, TrackerRole.RIGHT_FOOT);
			computedRightFootTracker.setStatus(TrackerStatus.OK);
		}

		if (!onlyFillWaistAndFeet) {
			if (computedChestTracker == null) {
				computedChestTracker = new ComputedHumanPoseTracker(Tracker.getNextLocalTrackerId(), ComputedHumanPoseTrackerPosition.CHEST, TrackerRole.CHEST);
				computedChestTracker.setStatus(TrackerStatus.OK);
			}
	
			if (computedLeftKneeTracker == null) {
				computedLeftKneeTracker = new ComputedHumanPoseTracker(Tracker.getNextLocalTrackerId(), ComputedHumanPoseTrackerPosition.LEFT_KNEE, TrackerRole.LEFT_KNEE);
				computedLeftKneeTracker.setStatus(TrackerStatus.OK);
			}
	
			if (computedRightKneeTracker == null) {
				computedRightKneeTracker = new ComputedHumanPoseTracker(Tracker.getNextLocalTrackerId(), ComputedHumanPoseTrackerPosition.RIGHT_KNEE, TrackerRole.RIGHT_KNEE);
				computedRightKneeTracker.setStatus(TrackerStatus.OK);
			}
		}
	}
	//#endregion

	//#region Get Trackers
	public ComputedHumanPoseTracker getComputedTracker(TrackerRole trackerRole) {
		switch (trackerRole) {
		case CHEST:
			return computedChestTracker;
		case WAIST:
			return computedWaistTracker;

		case LEFT_KNEE:
			return computedLeftKneeTracker;
		case LEFT_FOOT:
			return computedLeftFootTracker;

		case RIGHT_KNEE:
			return computedRightKneeTracker;
		case RIGHT_FOOT:
			return computedRightFootTracker;
		}

		return null;
	}
	//#endregion

	//#region Processing
	// Useful for sub-classes that need to return a sub-tracker (like PoseFrameTracker -> TrackerFrame)
	protected Tracker trackerPreUpdate(Tracker tracker) {
		return tracker;
	}

	// Updates the pose from tracker positions
	public void updatePose() {
		updateLocalTransforms();
		hmdNode.update();
		updateComputedTrackers();
	}

	//#region Update the node transforms from the trackers
	protected void updateLocalTransforms() {
		//#region Pass all trackers through trackerPreUpdate
		Tracker hmdTracker = trackerPreUpdate(this.hmdTracker);

		Tracker chestTracker = trackerPreUpdate(this.chestTracker);
		Tracker waistTracker = trackerPreUpdate(this.waistTracker);
		Tracker hipTracker = trackerPreUpdate(this.hipTracker);

		Tracker leftLegTracker = trackerPreUpdate(this.leftLegTracker);
		Tracker leftAnkleTracker = trackerPreUpdate(this.leftAnkleTracker);
		Tracker leftFootTracker = trackerPreUpdate(this.leftFootTracker);

		Tracker rightLegTracker = trackerPreUpdate(this.rightLegTracker);
		Tracker rightAnkleTracker = trackerPreUpdate(this.rightAnkleTracker);
		Tracker rightFootTracker = trackerPreUpdate(this.rightFootTracker);
		//#endregion

		if (hmdTracker != null) {
			if (hmdTracker.getPosition(posBuf)) {
				hmdNode.localTransform.setTranslation(posBuf);
			}
			if (hmdTracker.getRotation(rotBuf1)) {
				hmdNode.localTransform.setRotation(rotBuf1);
				headNode.localTransform.setRotation(rotBuf1);
			}
		} else {
			// Set to zero
			hmdNode.localTransform.setTranslation(Vector3f.ZERO);
			hmdNode.localTransform.setRotation(Quaternion.IDENTITY);
			headNode.localTransform.setRotation(Quaternion.IDENTITY);
		}
		
		if (chestTracker.getRotation(rotBuf1)) {
			neckNode.localTransform.setRotation(rotBuf1);
		}
		if (waistTracker.getRotation(rotBuf1)) {
			chestNode.localTransform.setRotation(rotBuf1);
		}
		if (hipTracker.getRotation(rotBuf1)) {
			waistNode.localTransform.setRotation(rotBuf1);
			trackerWaistNode.localTransform.setRotation(rotBuf1);
			hipNode.localTransform.setRotation(rotBuf1);
		}

		// Left Leg
		leftLegTracker.getRotation(rotBuf1);
		leftAnkleTracker.getRotation(rotBuf2);

		if(extendedKneeModel)
			calculateKneeLimits(rotBuf1, rotBuf2, leftLegTracker.getConfidenceLevel(), leftAnkleTracker.getConfidenceLevel());
		
		leftHipNode.localTransform.setRotation(rotBuf1);
		leftKneeNode.localTransform.setRotation(rotBuf2);
		leftAnkleNode.localTransform.setRotation(rotBuf2);
		leftFootNode.localTransform.setRotation(rotBuf2);

		if(leftFootTracker != null) {
			leftFootTracker.getRotation(rotBuf2);
			leftAnkleNode.localTransform.setRotation(rotBuf2);
			leftFootNode.localTransform.setRotation(rotBuf2);
		}
		
		// Right Leg
		rightLegTracker.getRotation(rotBuf1);
		rightAnkleTracker.getRotation(rotBuf2);
		
		if(extendedKneeModel)
			calculateKneeLimits(rotBuf1, rotBuf2, rightLegTracker.getConfidenceLevel(), rightAnkleTracker.getConfidenceLevel());
		
		rightHipNode.localTransform.setRotation(rotBuf1);
		rightKneeNode.localTransform.setRotation(rotBuf2);
		rightAnkleNode.localTransform.setRotation(rotBuf2);
		rightFootNode.localTransform.setRotation(rotBuf2);
		
		if(rightFootTracker != null) {
			rightFootTracker.getRotation(rotBuf2);
			rightAnkleNode.localTransform.setRotation(rotBuf2);
			rightFootNode.localTransform.setRotation(rotBuf2);
		}

		if(extendedPelvisModel) {
			// Average pelvis between two legs
			leftHipNode.localTransform.getRotation(rotBuf1);
			rightHipNode.localTransform.getRotation(rotBuf2);
			rotBuf2.nlerp(rotBuf1, 0.5f);
			chestNode.localTransform.getRotation(rotBuf1);
			rotBuf2.nlerp(rotBuf1, 0.3333333f);
			hipNode.localTransform.setRotation(rotBuf2);
			//trackerWaistNode.localTransform.setRotation(rotBuf2); // <== Provides cursed results from my test in VRChat when sitting or laying down -Erimel
			// TODO : Correct the trackerWaistNode without getting cursed results (only correct yaw?)
			// TODO : Use vectors to add like 50% of waist tracker yaw to waist node to reduce drift and let user take weird poses
		}
	}
	//#endregion

	//#region Knee Model
	// Knee basically has only 1 DoF (pitch), average yaw and roll between knee and hip
	protected void calculateKneeLimits(Quaternion hipBuf, Quaternion kneeBuf, float hipConfidence, float kneeConfidence) {
		ankleVector.set(0, -1, 0);
		hipVector.set(0, -1, 0);
		hipBuf.multLocal(hipVector);
		kneeBuf.multLocal(ankleVector);
		kneeRotation.angleBetweenVectors(hipVector, ankleVector); // Find knee angle
		
		// Substract knee angle from knee rotation. With perfect leg and perfect
		// sensors result should match hip rotation perfectly
		kneeBuf.multLocal(kneeRotation.inverse());
		
		// Average knee and hip with a slerp
		hipBuf.slerp(kneeBuf, 0.5f); // TODO : Use confidence to calculate changeAmt
		kneeBuf.set(hipBuf);

		// Return knee angle into knee rotation
		kneeBuf.multLocal(kneeRotation);
	}
	
	public static float normalizeRad(float angle) {
		return FastMath.normalize(angle, -FastMath.PI, FastMath.PI);
	}
	
	public static float interpolateRadians(float factor, float start, float end) {
		float angle = FastMath.abs(end - start);
		if(angle > FastMath.PI) {
			if(end > start) {
				start += FastMath.TWO_PI;
			} else {
				end += FastMath.TWO_PI;
			}
		}
		float val = start + (end - start) * factor;
		return normalizeRad(val);
	}
	//#endregion

	//#region Update the output trackers
	protected void updateComputedTrackers() {
		if (computedChestTracker != null) {
			computedChestTracker.position.set(chestNode.worldTransform.getTranslation());
			computedChestTracker.rotation.set(neckNode.worldTransform.getRotation());
			computedChestTracker.dataTick();
		}

		if (computedWaistTracker != null) {
			computedWaistTracker.position.set(trackerWaistNode.worldTransform.getTranslation());
			computedWaistTracker.rotation.set(trackerWaistNode.worldTransform.getRotation());
			computedWaistTracker.dataTick();
		}
		
		if (computedLeftKneeTracker != null) {
			computedLeftKneeTracker.position.set(leftKneeNode.worldTransform.getTranslation());
			computedLeftKneeTracker.rotation.set(leftHipNode.worldTransform.getRotation());
			computedLeftKneeTracker.dataTick();
		}

		if (computedLeftFootTracker != null) {
			computedLeftFootTracker.position.set(leftFootNode.worldTransform.getTranslation());
			computedLeftFootTracker.rotation.set(leftFootNode.worldTransform.getRotation());
			computedLeftFootTracker.dataTick();
		}

		if (computedRightKneeTracker != null) {
			computedRightKneeTracker.position.set(rightKneeNode.worldTransform.getTranslation());
			computedRightKneeTracker.rotation.set(rightHipNode.worldTransform.getRotation());
			computedRightKneeTracker.dataTick();
		}
		
		if (computedRightFootTracker != null) {
			computedRightFootTracker.position.set(rightFootNode.worldTransform.getTranslation());
			computedRightFootTracker.rotation.set(rightFootNode.worldTransform.getRotation());
			computedRightFootTracker.dataTick();
		}
	}
	//#endregion
	//#endregion
	
	//#region Skeleton Config
	@Override
	public void updateConfigState(SkeletonConfigValue config, float newValue) {
		// Do nothing, the node offset callback handles all that's needed
	}

	@Override
	public void updateToggleState(SkeletonConfigToggle configToggle, boolean newValue) {
		// Cache the values of these configs
		switch (configToggle) {
		case EXTENDED_PELVIS_MODEL:
			extendedPelvisModel = newValue;
			break;
		case EXTENDED_KNEE_MODEL:
			extendedKneeModel = newValue;
			break;
		}
	}

	@Override
	public void updateNodeOffset(SkeletonNodeOffset nodeOffset, Vector3f offset) {
		switch (nodeOffset) {
		case HEAD:
			headNode.localTransform.setTranslation(offset);
			break;
		case NECK:
			neckNode.localTransform.setTranslation(offset);
			break;
		case CHEST:
			chestNode.localTransform.setTranslation(offset);
			break;
		case WAIST:
			waistNode.localTransform.setTranslation(offset);
			break;
		case HIP:
			hipNode.localTransform.setTranslation(offset);
			break;
		case HIP_TRACKER:
			trackerWaistNode.localTransform.setTranslation(offset);
			break;

		case LEFT_HIP:
			leftHipNode.localTransform.setTranslation(offset);
			break;
		case RIGHT_HIP:
			rightHipNode.localTransform.setTranslation(offset);
			break;
			
		case KNEE:
			leftKneeNode.localTransform.setTranslation(offset);
			rightKneeNode.localTransform.setTranslation(offset);
			break;
		case ANKLE:
			leftAnkleNode.localTransform.setTranslation(offset);
			rightAnkleNode.localTransform.setTranslation(offset);
			break;
		case FOOT:
			leftFootNode.localTransform.setTranslation(offset);
			rightFootNode.localTransform.setTranslation(offset);
			break;
		}
	}

	public void updatePoseAffectedByConfig(SkeletonConfigValue config) {
		switch(config) {
		case HEAD:
			headNode.update();
			updateComputedTrackers();
			break;
		case NECK:
			neckNode.update();
			updateComputedTrackers();
			break;
		case TORSO:
			hipNode.update();
			updateComputedTrackers();
			break;
		case CHEST:
			chestNode.update();
			updateComputedTrackers();
			break;
		case WAIST:
			waistNode.update();
			updateComputedTrackers();
			break;
		case HIP_OFFSET:
			trackerWaistNode.update();
			updateComputedTrackers();
			break;
		case HIPS_WIDTH:
			leftHipNode.update();
			rightHipNode.update();
			updateComputedTrackers();
			break;
		case KNEE_HEIGHT:
			leftKneeNode.update();
			rightKneeNode.update();
			break;
		case LEGS_LENGTH:
			leftKneeNode.update();
			rightKneeNode.update();
			updateComputedTrackers();
			break;
		case FOOT_LENGTH:
			leftFootNode.update();
			rightFootNode.update();
			updateComputedTrackers();
			break;
		case FOOT_OFFSET:
			leftAnkleNode.update();
			rightAnkleNode.update();
			updateComputedTrackers();
			break;
		}
	}
	//#endregion
}
