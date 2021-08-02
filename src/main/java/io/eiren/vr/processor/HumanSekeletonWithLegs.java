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

public class HumanSekeletonWithLegs extends HumanSkeleonWithWaist {
	
	public static final float HIPS_WIDTH_DEFAULT = 0.3f;
	public static final float FOOT_LENGTH_DEFAULT = 0.05f;
	public static final float DEFAULT_FLOOR_OFFSET = 0.05f;
	
	protected final float[] kneeAngles = new float[3];
	protected final float[] hipAngles = new float[3];
	protected final Quaternion hipBuf = new Quaternion();
	protected final Quaternion kneeBuf = new Quaternion();
	
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

	public HumanSekeletonWithLegs(VRServer server, List<ComputedHumanPoseTracker> computedTrackers) {
		super(server, computedTrackers);
		List<Tracker> allTracekrs = server.getAllTrackers();
		this.leftLegTracker = TrackerUtils.findTrackerForBodyPosition(allTracekrs, TrackerBodyPosition.LEFT_LEG);
		this.leftAnkleTracker = TrackerUtils.findTrackerForBodyPosition(allTracekrs, TrackerBodyPosition.LEFT_ANKLE);
		this.leftFootTracker = TrackerUtils.findTrackerForBodyPosition(allTracekrs, TrackerBodyPosition.LEFT_FOOT);
		this.rightLegTracker = TrackerUtils.findTrackerForBodyPosition(allTracekrs, TrackerBodyPosition.RIGHT_LEG);
		this.rightAnkleTracker = TrackerUtils.findTrackerForBodyPosition(allTracekrs, TrackerBodyPosition.RIGHT_ANKLE);
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
	public void updateLocalTransforms() {
		super.updateLocalTransforms();
		// Left Leg
		leftLegTracker.getRotation(hipBuf);
		leftAnkleTracker.getRotation(kneeBuf);

		//calculateKneeLimits(hipBuf, kneeBuf, leftLegTracker.getConfidenceLevel(), leftAnkleTracker.getConfidenceLevel());
		
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
		
		//calculateKneeLimits(hipBuf, kneeBuf, rightLegTracker.getConfidenceLevel(), rightAnkleTracker.getConfidenceLevel());
		
		rightHipNode.localTransform.setRotation(hipBuf);
		rightKneeNode.localTransform.setRotation(kneeBuf);
		rightAnkleNode.localTransform.setRotation(kneeBuf);
		rightFootNode.localTransform.setRotation(kneeBuf);
		
		if(rightFootTracker != null) {
			rightFootTracker.getRotation(kneeBuf);
			rightAnkleNode.localTransform.setRotation(kneeBuf);
			rightFootNode.localTransform.setRotation(kneeBuf);
		}
		
		// TODO Calculate waist node as some function between waist and hip rotations
	}
	
	// Knee basically has only 1 DoF (pitch), average yaw between knee and hip
	protected void calculateKneeLimits(Quaternion hipBuf, Quaternion kneeBuf, float hipConfidense, float kneeConfidense) {
		hipBuf.toAngles(hipAngles);
		kneeBuf.toAngles(kneeAngles);
		
		hipAngles[1] = kneeAngles[1] = interpolateRadians(kneeLerpFactor, kneeAngles[1], hipAngles[1]);
		//hipAngles[2] = kneeAngles[2] = interpolateRadians(kneeLerpFactor, kneeAngles[2], hipAngles[2]);
		
		hipBuf.fromAngles(hipAngles);
		kneeBuf.fromAngles(kneeAngles);
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
}
