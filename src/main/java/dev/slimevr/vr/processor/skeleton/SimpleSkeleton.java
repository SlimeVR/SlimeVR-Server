package dev.slimevr.vr.processor.skeleton;

import java.util.List;
import java.util.Map;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import dev.slimevr.VRServer;
import dev.slimevr.util.ann.VRServerThread;
import dev.slimevr.vr.processor.ComputedHumanPoseTracker;
import dev.slimevr.vr.processor.ComputedHumanPoseTrackerPosition;
import dev.slimevr.vr.processor.TransformNode;
import dev.slimevr.vr.trackers.Tracker;
import dev.slimevr.vr.trackers.TrackerPosition;
import dev.slimevr.vr.trackers.TrackerRole;
import dev.slimevr.vr.trackers.TrackerStatus;
import dev.slimevr.vr.trackers.TrackerUtils;
import io.eiren.util.collections.FastList;

public class SimpleSkeleton extends HumanSkeleton implements SkeletonConfigCallback {
		
	//#region Upper body nodes (torso)
	protected final TransformNode hmdNode = new TransformNode("HMD", false);
	protected final TransformNode headNode = new TransformNode("Head", false);
	protected final TransformNode neckNode = new TransformNode("Neck", false);
	protected final TransformNode chestNode = new TransformNode("Chest", false);
	protected final TransformNode trackerChestNode = new TransformNode("Chest-Tracker", false);
	protected final TransformNode waistNode = new TransformNode("Waist", false);
	protected final TransformNode hipNode = new TransformNode("Hip", false);
	protected final TransformNode trackerWaistNode = new TransformNode("Waist-Tracker", false);
	//#endregion
	
	//#region Lower body nodes (legs)
	protected final TransformNode leftHipNode = new TransformNode("Left-Hip", false);
	protected final TransformNode leftKneeNode = new TransformNode("Left-Knee", false);
	protected final TransformNode trackerLeftKneeNode = new TransformNode("Left-Knee-Tracker", false);
	protected final TransformNode leftAnkleNode = new TransformNode("Left-Ankle", false);
	protected final TransformNode leftFootNode = new TransformNode("Left-Foot", false);
	protected final TransformNode trackerLeftFootNode = new TransformNode("Left-Foot-Tracker", false);
	
	protected final TransformNode rightHipNode = new TransformNode("Right-Hip", false);
	protected final TransformNode rightKneeNode = new TransformNode("Right-Knee", false);
	protected final TransformNode trackerRightKneeNode = new TransformNode("Right-Knee-Tracker", false);
	protected final TransformNode rightAnkleNode = new TransformNode("Right-Ankle", false);
	protected final TransformNode rightFootNode = new TransformNode("Right-Foot", false);
	protected final TransformNode trackerRightFootNode = new TransformNode("Right-Foot-Tracker", false);
	
	protected float minKneePitch = 0f * FastMath.DEG_TO_RAD;
	protected float maxKneePitch = 90f * FastMath.DEG_TO_RAD;
	
	protected float kneeLerpFactor = 0.5f;
	//#endregion

	//#region Arms (elbows)
	protected final TransformNode leftHandNode = new TransformNode("Left-Hand", false);
	protected final TransformNode rightHandNode = new TransformNode("Right-Hand", false);
	protected final TransformNode leftWristNode = new TransformNode("Left-Wrist", false);
	protected final TransformNode rightWristNode = new TransformNode("Right-Wrist", false);
	protected final TransformNode leftElbowNode = new TransformNode("Left-Elbow", false);
	protected final TransformNode rightElbowNode = new TransformNode("Right-Elbow", false);
	protected final TransformNode trackerLeftElbowNode = new TransformNode("Left-Elbow-Tracker", false);
	protected final TransformNode trackerRightElbowNode = new TransformNode("Right-Elbow-Tracker", false);
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

	protected Tracker leftHandTracker;
	protected Tracker rightHandTracker;
	protected Tracker leftElbowTracker;
	protected Tracker rightElbowTracker;
	//#endregion
	
	//#region Tracker Output
	protected ComputedHumanPoseTracker computedChestTracker;
	protected ComputedHumanPoseTracker computedWaistTracker;
	
	protected ComputedHumanPoseTracker computedLeftKneeTracker;
	protected ComputedHumanPoseTracker computedLeftFootTracker;
	
	protected ComputedHumanPoseTracker computedRightKneeTracker;
	protected ComputedHumanPoseTracker computedRightFootTracker;

	protected ComputedHumanPoseTracker computedLeftElbowTracker;
	protected ComputedHumanPoseTracker computedRightElbowTracker;
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

	private boolean hasSpineTracker;
	//#endregion
	
	//#region Constructors
	protected SimpleSkeleton(List<? extends ComputedHumanPoseTracker> computedTrackers) {
		//#region Assemble skeleton to hip
		hmdNode.attachChild(headNode);
		headNode.attachChild(neckNode);
		neckNode.attachChild(chestNode);
		chestNode.attachChild(waistNode);
		waistNode.attachChild(hipNode);
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

		//#region Assemble skeleton arms
		leftHandNode.attachChild(leftWristNode);
		rightHandNode.attachChild(rightWristNode);
		leftWristNode.attachChild(leftElbowNode);
		rightWristNode.attachChild(rightElbowNode);
		//#endregion

		//#region Attach tracker nodes for offsets
		chestNode.attachChild(trackerChestNode);
		hipNode.attachChild(trackerWaistNode);

		leftKneeNode.attachChild(trackerLeftKneeNode);
		rightKneeNode.attachChild(trackerRightKneeNode);

		leftFootNode.attachChild(trackerLeftFootNode);
		rightFootNode.attachChild(trackerRightFootNode);

		leftElbowNode.attachChild(trackerLeftElbowNode);
		rightElbowNode.attachChild(trackerRightElbowNode);
		//#endregion
		
		// Set default skeleton configuration (callback automatically sets initial offsets)
		skeletonConfig = new SkeletonConfig(true, this);
		
		if(computedTrackers != null) {
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
		
		if(trackers != null) {
			setTrackersFromList(trackers);
		} else {
			setTrackersFromList(new FastList<Tracker>(0));
		}
	}
	
	public SimpleSkeleton(List<? extends Tracker> trackers, List<? extends ComputedHumanPoseTracker> computedTrackers, Map<SkeletonConfigValue, Float> configs, Map<SkeletonConfigValue, Float> altConfigs) {
		// Initialize
		this(trackers, computedTrackers);
		
		// Set configs
		if(altConfigs != null) {
			// Set alts first, so if there's any overlap it doesn't affect the values
			skeletonConfig.setConfigs(altConfigs, null);
		}
		skeletonConfig.setConfigs(configs, null);
	}
	
	public SimpleSkeleton(List<? extends Tracker> trackers, List<? extends ComputedHumanPoseTracker> computedTrackers, Map<SkeletonConfigValue, Float> configs) {
		this(trackers, computedTrackers, configs, null);
	}
	//#endregion
	
	//#region Set Trackers
	public void setTrackersFromList(List<? extends Tracker> trackers, boolean setHmd) {
		if(setHmd) {
			this.hmdTracker = TrackerUtils.findTrackerForBodyPosition(trackers, TrackerPosition.HMD);
		}
		
		this.chestTracker = TrackerUtils.findTrackerForBodyPositionOrEmpty(trackers, TrackerPosition.CHEST, TrackerPosition.WAIST, TrackerPosition.HIP);
		this.waistTracker = TrackerUtils.findTrackerForBodyPositionOrEmpty(trackers, TrackerPosition.WAIST, TrackerPosition.HIP, TrackerPosition.CHEST);
		this.hipTracker = TrackerUtils.findTrackerForBodyPositionOrEmpty(trackers, TrackerPosition.HIP, TrackerPosition.WAIST, TrackerPosition.CHEST);
		
		this.leftLegTracker = TrackerUtils.findTrackerForBodyPositionOrEmpty(trackers, TrackerPosition.LEFT_LEG, TrackerPosition.LEFT_ANKLE, null);
		this.leftAnkleTracker = TrackerUtils.findTrackerForBodyPositionOrEmpty(trackers, TrackerPosition.LEFT_ANKLE, TrackerPosition.LEFT_LEG, null);
		this.leftFootTracker = TrackerUtils.findTrackerForBodyPosition(trackers, TrackerPosition.LEFT_FOOT);
		
		this.rightLegTracker = TrackerUtils.findTrackerForBodyPositionOrEmpty(trackers, TrackerPosition.RIGHT_LEG, TrackerPosition.RIGHT_ANKLE, null);
		this.rightAnkleTracker = TrackerUtils.findTrackerForBodyPositionOrEmpty(trackers, TrackerPosition.RIGHT_ANKLE, TrackerPosition.RIGHT_LEG, null);
		this.rightFootTracker = TrackerUtils.findTrackerForBodyPosition(trackers, TrackerPosition.RIGHT_FOOT);

		this.leftHandTracker = TrackerUtils.findTrackerForBodyPosition(trackers, TrackerPosition.LEFT_CONTROLLER);
		this.rightHandTracker = TrackerUtils.findTrackerForBodyPosition(trackers, TrackerPosition.RIGHT_CONTROLLER);
		this.leftElbowTracker = TrackerUtils.findTrackerForBodyPosition(trackers, TrackerPosition.LEFT_ELBOW);
		this.rightElbowTracker = TrackerUtils.findTrackerForBodyPosition(trackers, TrackerPosition.RIGHT_ELBOW);
	}
	
	public void setTrackersFromList(List<? extends Tracker> trackers) {
		setTrackersFromList(trackers, true);
	}
	
	public void setTrackersFromServer(VRServer server) {
		this.hmdTracker = server.hmdTracker;
		setTrackersFromList(server.getAllTrackers(), false);
	}
	
	public void setComputedTracker(ComputedHumanPoseTracker tracker) {
		switch(tracker.getTrackerRole()) {
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

		case LEFT_ELBOW:
			computedLeftElbowTracker = tracker;
			break;
		case RIGHT_ELBOW:
			computedRightElbowTracker = tracker;
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
		if(computedWaistTracker == null) {
			computedWaistTracker = new ComputedHumanPoseTracker(Tracker.getNextLocalTrackerId(), ComputedHumanPoseTrackerPosition.WAIST, TrackerRole.WAIST);
			computedWaistTracker.setStatus(TrackerStatus.OK);
		}
		
		if(computedLeftFootTracker == null) {
			computedLeftFootTracker = new ComputedHumanPoseTracker(Tracker.getNextLocalTrackerId(), ComputedHumanPoseTrackerPosition.LEFT_FOOT, TrackerRole.LEFT_FOOT);
			computedLeftFootTracker.setStatus(TrackerStatus.OK);
		}
		
		if(computedRightFootTracker == null) {
			computedRightFootTracker = new ComputedHumanPoseTracker(Tracker.getNextLocalTrackerId(), ComputedHumanPoseTrackerPosition.RIGHT_FOOT, TrackerRole.RIGHT_FOOT);
			computedRightFootTracker.setStatus(TrackerStatus.OK);
		}
		
		if(!onlyFillWaistAndFeet) {
			if(computedChestTracker == null) {
				computedChestTracker = new ComputedHumanPoseTracker(Tracker.getNextLocalTrackerId(), ComputedHumanPoseTrackerPosition.CHEST, TrackerRole.CHEST);
				computedChestTracker.setStatus(TrackerStatus.OK);
			}
			
			if(computedLeftKneeTracker == null) {
				computedLeftKneeTracker = new ComputedHumanPoseTracker(Tracker.getNextLocalTrackerId(), ComputedHumanPoseTrackerPosition.LEFT_KNEE, TrackerRole.LEFT_KNEE);
				computedLeftKneeTracker.setStatus(TrackerStatus.OK);
			}
			
			if(computedRightKneeTracker == null) {
				computedRightKneeTracker = new ComputedHumanPoseTracker(Tracker.getNextLocalTrackerId(), ComputedHumanPoseTrackerPosition.RIGHT_KNEE, TrackerRole.RIGHT_KNEE);
				computedRightKneeTracker.setStatus(TrackerStatus.OK);
			}
			
			if(computedLeftElbowTracker == null) {
				computedLeftElbowTracker = new ComputedHumanPoseTracker(Tracker.getNextLocalTrackerId(), ComputedHumanPoseTrackerPosition.LEFT_ELBOW, TrackerRole.LEFT_ELBOW);
				computedLeftElbowTracker.setStatus(TrackerStatus.OK);
			}
			if(computedRightElbowTracker == null) {
				computedRightElbowTracker = new ComputedHumanPoseTracker(Tracker.getNextLocalTrackerId(), ComputedHumanPoseTrackerPosition.RIGHT_ELBOW, TrackerRole.RIGHT_ELBOW);
				computedRightElbowTracker.setStatus(TrackerStatus.OK);
			}
		}
	}
	//#endregion
	
	//#region Get Trackers
	public ComputedHumanPoseTracker getComputedTracker(TrackerRole trackerRole) {
		switch(trackerRole) {
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
		case LEFT_ELBOW:
			return computedLeftElbowTracker;
		case RIGHT_ELBOW:
			return computedRightElbowTracker;
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
	@VRServerThread
	@Override
	public void updatePose() {
		updateLocalTransforms();
		updateRootTrackers();
		updateComputedTrackers();
	}
	void updateRootTrackers(){
		hmdNode.update();
		leftHandNode.update();
		rightHandNode.update();
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

		Tracker leftHandTracker = trackerPreUpdate(this.leftHandTracker);
		Tracker rightHandTracker = trackerPreUpdate(this.rightHandTracker);
		Tracker rightElbowTracker = trackerPreUpdate(this.rightElbowTracker);
		Tracker leftElbowTracker = trackerPreUpdate(this.leftElbowTracker);
		//#endregion
		
		hasSpineTracker = chestTracker.hasRotation() || waistTracker.hasRotation() || hipTracker.hasRotation();

		if(hmdTracker != null) {
			if(hmdTracker.getPosition(posBuf)) {
				hmdNode.localTransform.setTranslation(posBuf);
			}
			if(hmdTracker.getRotation(rotBuf1)) {
				hmdNode.localTransform.setRotation(rotBuf1);
				headNode.localTransform.setRotation(rotBuf1);
			}
		} else {
			// Set to zero
			hmdNode.localTransform.setTranslation(Vector3f.ZERO);
			hmdNode.localTransform.setRotation(Quaternion.IDENTITY);
			headNode.localTransform.setRotation(Quaternion.IDENTITY);
		}
		
		// Spine
		if(hasSpineTracker){
			if(chestTracker.getRotation(rotBuf1)) {
				neckNode.localTransform.setRotation(rotBuf1);
			}
			if(waistTracker.getRotation(rotBuf1)) {
				chestNode.localTransform.setRotation(rotBuf1);
				trackerChestNode.localTransform.setRotation(rotBuf1);
			}
			if(hipTracker.getRotation(rotBuf1)) {
				waistNode.localTransform.setRotation(rotBuf1);
				hipNode.localTransform.setRotation(rotBuf1);
				trackerWaistNode.localTransform.setRotation(rotBuf1);
			}
		}
		else if(hmdTracker != null){ // If no spine tracker, allign spine yaw with HMD
			rotBuf1 = rotBuf1.fromAngles(0, rotBuf1.toAngles(null)[1], 0);
			neckNode.localTransform.setRotation(rotBuf1);
			chestNode.localTransform.setRotation(rotBuf1);
			trackerChestNode.localTransform.setRotation(rotBuf1);
			waistNode.localTransform.setRotation(rotBuf1);
			hipNode.localTransform.setRotation(rotBuf1);
			trackerWaistNode.localTransform.setRotation(rotBuf1);
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

		trackerLeftKneeNode.localTransform.setRotation(rotBuf2);
		trackerLeftFootNode.localTransform.setRotation(rotBuf2);
		
		if(leftFootTracker != null) {
			leftFootTracker.getRotation(rotBuf2);
			leftAnkleNode.localTransform.setRotation(rotBuf2);
			leftFootNode.localTransform.setRotation(rotBuf2);
			trackerLeftFootNode.localTransform.setRotation(rotBuf2);
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

		trackerRightKneeNode.localTransform.setRotation(rotBuf2);
		trackerRightFootNode.localTransform.setRotation(rotBuf2);
		
		if(rightFootTracker != null) {
			rightFootTracker.getRotation(rotBuf2);
			rightAnkleNode.localTransform.setRotation(rotBuf2);
			rightFootNode.localTransform.setRotation(rotBuf2);
			trackerRightFootNode.localTransform.setRotation(rotBuf2);
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


		// Left arm
		if(leftHandTracker != null){
			if(leftHandTracker.getPosition(posBuf)) 				
				leftHandNode.localTransform.setTranslation(posBuf);
			if(leftHandTracker.getRotation(rotBuf1)) 
				leftHandNode.localTransform.setRotation(rotBuf1);
		}
		if(leftElbowTracker != null){
			if(leftElbowTracker.getRotation(rotBuf1)){
				leftWristNode.localTransform.setRotation(rotBuf1);
				trackerLeftElbowNode.localTransform.setRotation(rotBuf1);
			}
		}

		// Right arm
		if(rightHandTracker != null){
			if(rightHandTracker.getPosition(posBuf)) 				
				rightHandNode.localTransform.setTranslation(posBuf);
			if(rightHandTracker.getRotation(rotBuf1)) 
			rightHandNode.localTransform.setRotation(rotBuf1);
		}
		if(rightElbowTracker != null){
			if(rightElbowTracker.getRotation(rotBuf1)){
				rightWristNode.localTransform.setRotation(rotBuf1);
				trackerRightElbowNode.localTransform.setRotation(rotBuf1);
			}
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
		if(computedChestTracker != null) {
			computedChestTracker.position.set(trackerChestNode.worldTransform.getTranslation());
			computedChestTracker.rotation.set(neckNode.worldTransform.getRotation());
			computedChestTracker.dataTick();
		}
		
		if(computedWaistTracker != null) {
			computedWaistTracker.position.set(trackerWaistNode.worldTransform.getTranslation());
			computedWaistTracker.rotation.set(trackerWaistNode.worldTransform.getRotation());
			computedWaistTracker.dataTick();
		}
		
		if(computedLeftKneeTracker != null) {
			computedLeftKneeTracker.position.set(trackerLeftKneeNode.worldTransform.getTranslation());
			computedLeftKneeTracker.rotation.set(leftHipNode.worldTransform.getRotation());
			computedLeftKneeTracker.dataTick();
		}
		
		if(computedLeftFootTracker != null) {
			computedLeftFootTracker.position.set(trackerLeftFootNode.worldTransform.getTranslation());
			computedLeftFootTracker.rotation.set(trackerLeftFootNode.worldTransform.getRotation());
			computedLeftFootTracker.dataTick();
		}
		
		if(computedRightKneeTracker != null) {
			computedRightKneeTracker.position.set(trackerRightKneeNode.worldTransform.getTranslation());
			computedRightKneeTracker.rotation.set(rightHipNode.worldTransform.getRotation());
			computedRightKneeTracker.dataTick();
		}
		
		if(computedRightFootTracker != null) {
			computedRightFootTracker.position.set(trackerRightFootNode.worldTransform.getTranslation());
			computedRightFootTracker.rotation.set(trackerRightFootNode.worldTransform.getRotation());
			computedRightFootTracker.dataTick();
		}

		if(computedLeftElbowTracker != null) {
			computedLeftElbowTracker.position.set(trackerLeftElbowNode.worldTransform.getTranslation());
			computedLeftElbowTracker.rotation.set(trackerLeftElbowNode.worldTransform.getRotation());
			computedLeftElbowTracker.dataTick();
		}

		if(computedRightElbowTracker != null) {
			computedRightElbowTracker.position.set(trackerRightElbowNode.worldTransform.getTranslation());
			computedRightElbowTracker.rotation.set(trackerRightElbowNode.worldTransform.getRotation());
			computedRightElbowTracker.dataTick();
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
		if(configToggle == null) {
			return;
		}
		
		// Cache the values of these configs
		switch(configToggle) {
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
		if(nodeOffset == null) {
			return;
		}
		
		switch(nodeOffset) {
		case HEAD:
			headNode.localTransform.setTranslation(offset);
			break;
		case NECK:
			neckNode.localTransform.setTranslation(offset);
			break;
		case CHEST:
			chestNode.localTransform.setTranslation(offset);
			break;
		case CHEST_TRACKER:
			trackerChestNode.localTransform.setTranslation(offset);
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
		case KNEE_TRACKER:
			trackerLeftKneeNode.localTransform.setTranslation(offset);
			trackerRightKneeNode.localTransform.setTranslation(offset);
			break;
		case ANKLE:
			leftAnkleNode.localTransform.setTranslation(offset);
			rightAnkleNode.localTransform.setTranslation(offset);
			break;
		case FOOT:
			leftFootNode.localTransform.setTranslation(offset);
			rightFootNode.localTransform.setTranslation(offset);
			break;
		case FOOT_TRACKER:
			trackerLeftFootNode.localTransform.setTranslation(offset);
			trackerRightFootNode.localTransform.setTranslation(offset);
			break;

		case HAND:
			leftWristNode.localTransform.setTranslation(offset);
			rightWristNode.localTransform.setTranslation(offset);
			break;
		case ELBOW:
			leftElbowNode.localTransform.setTranslation(offset);
			rightElbowNode.localTransform.setTranslation(offset);
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
		case SKELETON_OFFSET:
			trackerChestNode.update();
			trackerWaistNode.update();
			trackerLeftKneeNode.update();
			trackerRightKneeNode.update();
			trackerLeftFootNode.update();
			trackerRightFootNode.update();
			trackerLeftElbowNode.update();
			trackerRightElbowNode.update();
			updateComputedTrackers();
			break;
		case CONTROLLER_DISTANCE_Z:
		case CONTROLLER_DISTANCE_Y:
			leftWristNode.update();
			rightWristNode.update();
			updateComputedTrackers();
			break;
		case ELBOW_DISTANCE:
			leftElbowNode.update();
			rightElbowNode.update();
			updateComputedTrackers();
			break;
		}
	}
	//#endregion
	
	@Override
	public TransformNode getRootNode() {
		return hmdNode;
	}

	@Override
	public TransformNode[] getAllNodes() {
		List<TransformNode> nodesList = new FastList<>();

		hmdNode.depthFirstTraversal((node) -> {nodesList.add(node);});
		leftHandNode.depthFirstTraversal((node) -> {nodesList.add(node);});
		rightHandNode.depthFirstTraversal((node) -> {nodesList.add(node);});

		return nodesList.toArray(new TransformNode[0]);
	}
	
	@Override
	public SkeletonConfig getSkeletonConfig() {
		return skeletonConfig;
	}
	
	@Override
	public void resetSkeletonConfig(SkeletonConfigValue config) {
		if(config == null) {
			return;
		}
		
		Vector3f vec;
		float height;
		switch(config) {
		case HEAD:
			skeletonConfig.setConfig(SkeletonConfigValue.HEAD, null);
			break;
		case NECK:
			skeletonConfig.setConfig(SkeletonConfigValue.NECK, null);
			break;
		case TORSO: // Distance from shoulders to hip (full torso length)
			vec = new Vector3f();
			hmdTracker.getPosition(vec);
			height = vec.y;
			if(height > 0.5f) { // Reset only if floor level is right, TODO: read floor level from SteamVR if it's not 0
				skeletonConfig.setConfig(SkeletonConfigValue.TORSO, ((height) * 0.42f) - skeletonConfig.getConfig(SkeletonConfigValue.NECK));
			} else// if floor level is incorrect
			{
				skeletonConfig.setConfig(SkeletonConfigValue.TORSO, null);
			}
			break;
		case CHEST: //Chest is 57% of the upper body by default (shoulders to chest)
			skeletonConfig.setConfig(SkeletonConfigValue.CHEST, skeletonConfig.getConfig(SkeletonConfigValue.TORSO) * 0.57f);
			break;
		case WAIST: // waist length is from hips to waist
			skeletonConfig.setConfig(SkeletonConfigValue.WAIST, null);
			break;
		case HIP_OFFSET:
			skeletonConfig.setConfig(SkeletonConfigValue.HIP_OFFSET, null);
			break;
		case HIPS_WIDTH:
			skeletonConfig.setConfig(SkeletonConfigValue.HIPS_WIDTH, null);
			break;
		case FOOT_LENGTH:
			skeletonConfig.setConfig(SkeletonConfigValue.FOOT_LENGTH, null);
			break;
		case FOOT_OFFSET:
			skeletonConfig.setConfig(SkeletonConfigValue.FOOT_OFFSET, null);
			break;
		case SKELETON_OFFSET:
			skeletonConfig.setConfig(SkeletonConfigValue.SKELETON_OFFSET, null);
			break;
		case LEGS_LENGTH: // Set legs length to be 5cm above floor level
			vec = new Vector3f();
			hmdTracker.getPosition(vec);
			height = vec.y;
			if(height > 0.5f) { // Reset only if floor level is right, todo: read floor level from SteamVR if it's not 0
				skeletonConfig.setConfig(SkeletonConfigValue.LEGS_LENGTH, height - skeletonConfig.getConfig(SkeletonConfigValue.NECK) - skeletonConfig.getConfig(SkeletonConfigValue.TORSO) - 0.05f);
			} else //if floor level is incorrect
			{
				skeletonConfig.setConfig(SkeletonConfigValue.LEGS_LENGTH, null);
			}
			resetSkeletonConfig(SkeletonConfigValue.KNEE_HEIGHT);
			break;
		case KNEE_HEIGHT: // Knees are at 55% of the legs by default
			skeletonConfig.setConfig(SkeletonConfigValue.KNEE_HEIGHT, skeletonConfig.getConfig(SkeletonConfigValue.LEGS_LENGTH) * 0.55f);
			break;
		case CONTROLLER_DISTANCE_Z:
			skeletonConfig.setConfig(SkeletonConfigValue.CONTROLLER_DISTANCE_Z, null);
			break;
		case CONTROLLER_DISTANCE_Y:
			skeletonConfig.setConfig(SkeletonConfigValue.CONTROLLER_DISTANCE_Y, null);
			break;
		case ELBOW_DISTANCE:
			skeletonConfig.setConfig(SkeletonConfigValue.ELBOW_DISTANCE, null);
			break;
		}
	}
	
	Tracker[] getTrackerToReset(){
		return new Tracker[] {trackerPreUpdate(this.chestTracker), trackerPreUpdate(this.waistTracker), 
			trackerPreUpdate(this.hipTracker), trackerPreUpdate(this.leftLegTracker),
			trackerPreUpdate(this.leftAnkleTracker), trackerPreUpdate(this.leftFootTracker),
			trackerPreUpdate(this.rightLegTracker), trackerPreUpdate(this.rightAnkleTracker),
			trackerPreUpdate(this.rightFootTracker), trackerPreUpdate(this.rightElbowTracker),
			trackerPreUpdate(this.leftElbowTracker)};
	}

	@Override
	public void resetTrackersFull() {
		//#region Pass all trackers through trackerPreUpdate
		Tracker hmdTracker = trackerPreUpdate(this.hmdTracker);
		Tracker[] trackersToReset = getTrackerToReset();
		//#endregion
		
		// Resets all axis of the trackers with the HMD as reference.
		Quaternion referenceRotation = new Quaternion();
		hmdTracker.getRotation(referenceRotation);
		
		for (Tracker tracker : trackersToReset) {
			if(tracker != null){
				tracker.resetFull(referenceRotation);
			}
		}
	}
	
	@Override
	@VRServerThread
	public void resetTrackersYaw() {
		//#region Pass all trackers through trackerPreUpdate
		Tracker hmdTracker = trackerPreUpdate(this.hmdTracker);
		Tracker[] trackersToReset = getTrackerToReset();
		//#endregion
		
		// Resets the yaw of the trackers with the HMD as reference.
		Quaternion referenceRotation = new Quaternion();
		hmdTracker.getRotation(referenceRotation);
		
		for (Tracker tracker : trackersToReset) {
			if(tracker != null){
				tracker.resetYaw(referenceRotation);
			}
		}
	}
}
