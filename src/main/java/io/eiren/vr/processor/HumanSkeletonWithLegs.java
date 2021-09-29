package io.eiren.vr.processor;

import java.util.List;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import io.eiren.util.ann.VRServerThread;
import io.eiren.vr.VRServer;
import io.eiren.vr.trackers.Tracker;
import io.eiren.vr.trackers.TrackerStatus;
import io.eiren.vr.trackers.TrackerUtils;

public class HumanSkeletonWithLegs extends HumanSkeletonWithWaist {
	
	public static final float HIPS_WIDTH_DEFAULT = 0.3f;
	public static final float FOOT_LENGTH_DEFAULT = 0.05f;
	public static final float DEFAULT_FLOOR_OFFSET = 0.05f;
	
	protected final Quaternion hipBuf = new Quaternion();
	protected final Quaternion kneeBuf = new Quaternion();
	protected final Vector3f hipVector = new Vector3f();
	protected final Vector3f ankleVector = new Vector3f();
	protected final Quaternion kneeRotation = new Quaternion();
	
	protected final Tracker leftLegTracker;
	protected final Tracker leftAnkleTracker;
	protected final Tracker leftFootTracker;
	protected final ComputedHumanPoseTracker computedLeftFootTracker;
	protected final ComputedHumanPoseTracker computedLeftKneeTracker;
	protected final Tracker rightLegTracker;
	protected final Tracker rightAnkleTracker;
	protected final Tracker rightFootTracker;
	protected final ComputedHumanPoseTracker computedRightFootTracker;
	protected final ComputedHumanPoseTracker computedRightKneeTracker;
	
	protected final TransformNode leftHipNode = new TransformNode("Left-Hip", false);
	protected final TransformNode leftKneeNode = new TransformNode("Left-Knee", false);
	protected final TransformNode leftAnkleNode = new TransformNode("Left-Ankle", false);
	protected final TransformNode leftFootNode = new TransformNode("Left-Foot", false);
	protected final TransformNode rightHipNode = new TransformNode("Right-Hip", false);
	protected final TransformNode rightKneeNode = new TransformNode("Right-Knee", false);
	protected final TransformNode rightAnkleNode = new TransformNode("Right-Ankle", false);
	protected final TransformNode rightFootNode = new TransformNode("Right-Foot", false);
	
	/**
	 * Distance between centers of both hips
	 */
	protected float hipsWidth = HIPS_WIDTH_DEFAULT;
	/**
	 * Length from waist to knees
	 */
	protected float kneeHeight = 0.42f;
	/**
	 * Distance from waist to ankle
	 */
	protected float legsLength = 0.84f;
	protected float footLength = FOOT_LENGTH_DEFAULT;
	
	protected float minKneePitch = 0f * FastMath.DEG_TO_RAD;
	protected float maxKneePitch = 90f * FastMath.DEG_TO_RAD;
	
	protected float kneeLerpFactor = 0.5f;
	
	protected boolean extendedPelvisModel = true;
	protected boolean extendedKneeModel = false;

	public HumanSkeletonWithLegs(VRServer server, List<ComputedHumanPoseTracker> computedTrackers) {
		super(server, computedTrackers);
		List<Tracker> allTracekrs = server.getAllTrackers();
		this.leftLegTracker = TrackerUtils.findTrackerForBodyPositionOrEmpty(allTracekrs, TrackerBodyPosition.LEFT_LEG, TrackerBodyPosition.LEFT_ANKLE);
		this.leftAnkleTracker = TrackerUtils.findTrackerForBodyPositionOrEmpty(allTracekrs, TrackerBodyPosition.LEFT_ANKLE, TrackerBodyPosition.LEFT_LEG);
		this.leftFootTracker = TrackerUtils.findTrackerForBodyPosition(allTracekrs, TrackerBodyPosition.LEFT_FOOT);
		this.rightLegTracker = TrackerUtils.findTrackerForBodyPositionOrEmpty(allTracekrs, TrackerBodyPosition.RIGHT_LEG, TrackerBodyPosition.RIGHT_ANKLE);
		this.rightAnkleTracker = TrackerUtils.findTrackerForBodyPositionOrEmpty(allTracekrs, TrackerBodyPosition.RIGHT_ANKLE, TrackerBodyPosition.RIGHT_LEG);
		this.rightFootTracker = TrackerUtils.findTrackerForBodyPosition(allTracekrs, TrackerBodyPosition.RIGHT_FOOT);
		ComputedHumanPoseTracker lat = null;
		ComputedHumanPoseTracker rat = null;
		ComputedHumanPoseTracker rkt = null;
		ComputedHumanPoseTracker lkt = null;
		for(int i = 0; i < computedTrackers.size(); ++i) {
			ComputedHumanPoseTracker t = computedTrackers.get(i);
			if(t.skeletonPosition == ComputedHumanPoseTrackerPosition.LEFT_FOOT)
				lat = t;
			if(t.skeletonPosition == ComputedHumanPoseTrackerPosition.RIGHT_FOOT)
				rat = t;
			if(t.skeletonPosition == ComputedHumanPoseTrackerPosition.LEFT_KNEE)
				lkt = t;
			if(t.skeletonPosition == ComputedHumanPoseTrackerPosition.RIGHT_KNEE)
				rkt = t;
		}
		if(lat == null)
			lat = new ComputedHumanPoseTracker(ComputedHumanPoseTrackerPosition.LEFT_FOOT, TrackerBodyPosition.LEFT_FOOT);
		if(rat == null)
			rat = new ComputedHumanPoseTracker(ComputedHumanPoseTrackerPosition.RIGHT_FOOT, TrackerBodyPosition.RIGHT_FOOT);
		computedLeftFootTracker = lat;
		computedRightFootTracker = rat;
		computedLeftKneeTracker = lkt;
		computedRightKneeTracker = rkt;
		lat.setStatus(TrackerStatus.OK);
		rat.setStatus(TrackerStatus.OK);
		hipsWidth = server.config.getFloat("body.hipsWidth", hipsWidth);
		kneeHeight = server.config.getFloat("body.kneeHeight", kneeHeight);
		legsLength = server.config.getFloat("body.legsLength", legsLength);
		footLength = server.config.getFloat("body.footLength", footLength);
		//extendedPelvisModel = server.config.getBoolean("body.model.extendedPelvis", extendedPelvisModel);
		extendedKneeModel = server.config.getBoolean("body.model.extendedKnee", extendedKneeModel);
		
		waistNode.attachChild(leftHipNode);
		leftHipNode.localTransform.setTranslation(-hipsWidth / 2, 0, 0);
		
		waistNode.attachChild(rightHipNode);
		rightHipNode.localTransform.setTranslation(hipsWidth / 2, 0, 0);
		
		leftHipNode.attachChild(leftKneeNode);
		leftKneeNode.localTransform.setTranslation(0, -(legsLength - kneeHeight), 0);
		
		rightHipNode.attachChild(rightKneeNode);
		rightKneeNode.localTransform.setTranslation(0, -(legsLength - kneeHeight), 0);
		
		leftKneeNode.attachChild(leftAnkleNode);
		leftAnkleNode.localTransform.setTranslation(0, -kneeHeight, 0);
		
		rightKneeNode.attachChild(rightAnkleNode);
		rightAnkleNode.localTransform.setTranslation(0, -kneeHeight, 0);

		leftAnkleNode.attachChild(leftFootNode);
		leftFootNode.localTransform.setTranslation(0, 0, -footLength);
		
		rightAnkleNode.attachChild(rightFootNode);
		rightFootNode.localTransform.setTranslation(0, 0, -footLength);
		
		configMap.put("Hips width", hipsWidth);
		configMap.put("Legs length", legsLength);
		configMap.put("Knee height", kneeHeight);
		configMap.put("Foot length", footLength);
	}
	
	@Override
	public void resetSkeletonConfig(String joint) {
		super.resetSkeletonConfig(joint);
		switch(joint) {
		case "All":
			// Resets from the parent already performed
			resetSkeletonConfig("Hips width");
			resetSkeletonConfig("Foot length");
			resetSkeletonConfig("Legs length");
			break;
		case "Hips width":
			setSkeletonConfig(joint, HIPS_WIDTH_DEFAULT);
			break;
		case "Foot length":
			setSkeletonConfig(joint, FOOT_LENGTH_DEFAULT);
			break;
		case "Legs length": // Set legs length to be 5cm above floor level
			Vector3f vec = new Vector3f();
			hmdTracker.getPosition(vec);
			float height = vec.y;
			if(height > 0.5f) { // Reset only if floor level is right, todo: read floor level from SteamVR if it's not 0
				setSkeletonConfig(joint, height - neckLength - waistDistance - DEFAULT_FLOOR_OFFSET);
			}
			resetSkeletonConfig("Knee height");
			break;
		case "Knee height": // Knees are at 50% of the legs by default
			setSkeletonConfig(joint, legsLength / 2.0f);
			break;
		}
	}
	
	@Override
	public void setSkeletonConfig(String joint, float newLength) {
		super.setSkeletonConfig(joint, newLength);
		switch(joint) {
		case "Hips width":
			hipsWidth = newLength;
			server.config.setProperty("body.hipsWidth", hipsWidth);
			leftHipNode.localTransform.setTranslation(-hipsWidth / 2, 0, 0);
			rightHipNode.localTransform.setTranslation(hipsWidth / 2, 0, 0);
			break;
		case "Knee height":
			kneeHeight = newLength;
			server.config.setProperty("body.kneeHeight", kneeHeight);
			leftAnkleNode.localTransform.setTranslation(0, -kneeHeight, 0);
			rightAnkleNode.localTransform.setTranslation(0, -kneeHeight, 0);
			leftKneeNode.localTransform.setTranslation(0, -(legsLength - kneeHeight), 0);
			rightKneeNode.localTransform.setTranslation(0, -(legsLength - kneeHeight), 0);
			break;
		case "Legs length":
			legsLength = newLength;
			server.config.setProperty("body.legsLength", legsLength);
			leftKneeNode.localTransform.setTranslation(0, -(legsLength - kneeHeight), 0);
			rightKneeNode.localTransform.setTranslation(0, -(legsLength - kneeHeight), 0);
			break;
		case "Foot length":
			footLength = newLength;
			server.config.setProperty("body.footLength", footLength);
			leftFootNode.localTransform.setTranslation(0, 0, -footLength);
			rightFootNode.localTransform.setTranslation(0, 0, -footLength);
			break;
		}
	}
	
	@Override
	public boolean getSkeletonConfigBoolean(String config) {
		switch(config) {
		case "Extended pelvis model":
			return extendedPelvisModel;
		case "Extended knee model":
			return extendedKneeModel;
		}
		return super.getSkeletonConfigBoolean(config);
	}
	
	@Override
	public void setSkeletonConfigBoolean(String config, boolean newState) {
		switch(config) {
		case "Extended pelvis model":
			extendedPelvisModel = newState;
			server.config.setProperty("body.model.extendedPelvis", newState);
			break;
		case "Extended knee model":
			extendedKneeModel = newState;
			server.config.setProperty("body.model.extendedKnee", newState);
			break;
		default:
			super.setSkeletonConfigBoolean(config, newState);
			break;
		}
	}
	
	@Override
	public void updateLocalTransforms() {
		super.updateLocalTransforms();
		// Left Leg
		leftLegTracker.getRotation(hipBuf);
		leftAnkleTracker.getRotation(kneeBuf);

		if(extendedKneeModel)
			calculateKneeLimits(hipBuf, kneeBuf, leftLegTracker.getConfidenceLevel(), leftAnkleTracker.getConfidenceLevel());
		
		leftHipNode.localTransform.setRotation(hipBuf);
		leftKneeNode.localTransform.setRotation(kneeBuf);
		leftAnkleNode.localTransform.setRotation(kneeBuf);
		leftFootNode.localTransform.setRotation(kneeBuf);

		if(leftFootTracker != null) {
			leftFootTracker.getRotation(kneeBuf);
			leftAnkleNode.localTransform.setRotation(kneeBuf);
			leftFootNode.localTransform.setRotation(kneeBuf);
		}
		
		// Right Leg
		rightLegTracker.getRotation(hipBuf);
		rightAnkleTracker.getRotation(kneeBuf);
		
		if(extendedKneeModel)
			calculateKneeLimits(hipBuf, kneeBuf, rightLegTracker.getConfidenceLevel(), rightAnkleTracker.getConfidenceLevel());
		
		rightHipNode.localTransform.setRotation(hipBuf);
		rightKneeNode.localTransform.setRotation(kneeBuf);
		rightAnkleNode.localTransform.setRotation(kneeBuf);
		rightFootNode.localTransform.setRotation(kneeBuf);
		
		if(rightFootTracker != null) {
			rightFootTracker.getRotation(kneeBuf);
			rightAnkleNode.localTransform.setRotation(kneeBuf);
			rightFootNode.localTransform.setRotation(kneeBuf);
		}

		if(extendedPelvisModel) {
			// Average pelvis between two legs
			leftHipNode.localTransform.getRotation(hipBuf);
			rightHipNode.localTransform.getRotation(kneeBuf);
			kneeBuf.nlerp(hipBuf, 0.5f);
			chestNode.localTransform.getRotation(hipBuf);
			kneeBuf.nlerp(hipBuf, 0.3333333f);
			waistNode.localTransform.setRotation(kneeBuf);
			// TODO : Use vectors to add like 50% of wasit tracker yaw to waist node to reduce drift and let user take weird poses
			// TODO Set virtual waist node yaw to that of waist node
		}
	}
	
	// Knee basically has only 1 DoF (pitch), average yaw and roll between knee and hip
	protected void calculateKneeLimits(Quaternion hipBuf, Quaternion kneeBuf, float hipConfidense, float kneeConfidense) {
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
		float angle = Math.abs(end - start);
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
	
	@Override
	protected void updateComputedTrackers() {
		super.updateComputedTrackers();
		
		if(computedLeftFootTracker != null) {
			computedLeftFootTracker.position.set(leftFootNode.worldTransform.getTranslation());
			computedLeftFootTracker.rotation.set(leftFootNode.worldTransform.getRotation());
			computedLeftFootTracker.dataTick();
		}
		
		if(computedLeftKneeTracker != null) {
			computedLeftKneeTracker.position.set(leftKneeNode.worldTransform.getTranslation());
			computedLeftKneeTracker.rotation.set(leftHipNode.worldTransform.getRotation());
			computedLeftKneeTracker.dataTick();
		}
		
		if(computedRightFootTracker != null) {
			computedRightFootTracker.position.set(rightFootNode.worldTransform.getTranslation());
			computedRightFootTracker.rotation.set(rightFootNode.worldTransform.getRotation());
			computedRightFootTracker.dataTick();
		}
		
		if(computedRightKneeTracker != null) {
			computedRightKneeTracker.position.set(rightKneeNode.worldTransform.getTranslation());
			computedRightKneeTracker.rotation.set(rightHipNode.worldTransform.getRotation());
			computedRightKneeTracker.dataTick();
		}
	}
	
	@Override
	@VRServerThread
	public void resetTrackersFull() {
		// Each tracker uses the tracker before it to adjust iteself,
		// so trackers that don't need adjustments could be used too
		super.resetTrackersFull();
		// Start with waist, it was reset in the parent
		Quaternion referenceRotation = new Quaternion();
		this.waistTracker.getRotation(referenceRotation);
		
		this.leftLegTracker.resetFull(referenceRotation);
		this.rightLegTracker.resetFull(referenceRotation);
		this.leftLegTracker.getRotation(referenceRotation);
		
		this.leftAnkleTracker.resetFull(referenceRotation);
		this.leftAnkleTracker.getRotation(referenceRotation);
		
		if(this.leftFootTracker != null) {
			this.leftFootTracker.resetFull(referenceRotation);
		}

		this.rightLegTracker.getRotation(referenceRotation);
		
		this.rightAnkleTracker.resetFull(referenceRotation);
		this.rightAnkleTracker.getRotation(referenceRotation);
		
		if(this.rightFootTracker != null) {
			this.rightFootTracker.resetFull(referenceRotation);
		}
	}
	
	@Override
	@VRServerThread
	public void resetTrackersYaw() {
		// Each tracker uses the tracker before it to adjust iteself,
		// so trackers that don't need adjustments could be used too
		super.resetTrackersYaw();
		// Start with waist, it was reset in the parent
		Quaternion referenceRotation = new Quaternion();
		this.waistTracker.getRotation(referenceRotation);
		
		this.leftLegTracker.resetYaw(referenceRotation);
		this.rightLegTracker.resetYaw(referenceRotation);
		this.leftLegTracker.getRotation(referenceRotation);
		
		this.leftAnkleTracker.resetYaw(referenceRotation);
		this.leftAnkleTracker.getRotation(referenceRotation);
		
		if(this.leftFootTracker != null) {
			this.leftFootTracker.resetYaw(referenceRotation);
		}

		this.rightLegTracker.getRotation(referenceRotation);
		
		this.rightAnkleTracker.resetYaw(referenceRotation);
		this.rightAnkleTracker.getRotation(referenceRotation);
		
		if(this.rightFootTracker != null) {
			this.rightFootTracker.resetYaw(referenceRotation);
		}
	}
}
