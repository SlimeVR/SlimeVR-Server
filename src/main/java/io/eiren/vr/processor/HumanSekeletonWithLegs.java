package io.eiren.vr.processor;

import java.util.List;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;

import io.eiren.util.ann.VRServerThread;
import io.eiren.vr.VRServer;
import io.eiren.vr.trackers.Tracker;
import io.eiren.vr.trackers.TrackerStatus;
import io.eiren.vr.trackers.TrackerUtils;

public class HumanSekeletonWithLegs extends HumanSkeleonWithWaist {
	
	protected final float[] kneeAngles = new float[3];
	protected final float[] hipAngles = new float[3];
	protected final Quaternion hipBuf = new Quaternion();
	protected final Quaternion kneeBuf = new Quaternion();
	
	protected final Tracker leftLegTracker;
	protected final Tracker leftAnkleTracker;
	protected final Tracker leftFootTracker;
	protected final ComputedHumanPoseTracker computedLeftFootTracker;
	protected final Tracker rightLegTracker;
	protected final Tracker rightAnkleTracker;
	protected final Tracker rightFootTracker;
	protected final ComputedHumanPoseTracker computedRightFootTracker;
	
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
	protected float hipsWidth = 0.30f;
	/**
	 * Length from waist to knees
	 */
	protected float hipsLength = 0.51f;
	/**
	 * Distance from waist to ankle
	 */
	protected float ankleLength = 0.55f;
	
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
		for(int i = 0; i < computedTrackers.size(); ++i) {
			ComputedHumanPoseTracker t = computedTrackers.get(i);
			if(t.skeletonPosition == ComputedHumanPoseTrackerPosition.LEFT_FOOT)
				lat = t;
			if(t.skeletonPosition == ComputedHumanPoseTrackerPosition.RIGHT_FOOT)
				rat = t;
		}
		computedLeftFootTracker = lat;
		computedRightFootTracker = rat;
		lat.setStatus(TrackerStatus.OK);
		rat.setStatus(TrackerStatus.OK);
		hipsWidth = server.config.getFloat("body.hipsWidth", hipsWidth);
		hipsLength = server.config.getFloat("body.hipLength", hipsLength);
		ankleLength = server.config.getFloat("body.ankleLength", ankleLength);
		
		waistNode.attachChild(leftHipNode);
		leftHipNode.localTransform.setTranslation(-hipsWidth / 2, 0, 0);
		
		waistNode.attachChild(rightHipNode);
		rightHipNode.localTransform.setTranslation(hipsWidth / 2, 0, 0);
		
		leftHipNode.attachChild(leftKneeNode);
		leftKneeNode.localTransform.setTranslation(0, -hipsLength, 0);
		
		rightHipNode.attachChild(rightKneeNode);
		rightKneeNode.localTransform.setTranslation(0, -hipsLength, 0);
		
		leftKneeNode.attachChild(leftAnkleNode);
		leftAnkleNode.localTransform.setTranslation(0, -ankleLength, 0);
		
		rightKneeNode.attachChild(rightAnkleNode);
		rightAnkleNode.localTransform.setTranslation(0, -ankleLength, 0);

		leftAnkleNode.attachChild(leftFootNode);
		leftFootNode.localTransform.setTranslation(0, 0, -0.05f);
		
		rightAnkleNode.attachChild(rightFootNode);
		rightFootNode.localTransform.setTranslation(0, 0, -0.05f);
		
		configMap.put("Hips width", hipsWidth);
		configMap.put("Hip length", hipsLength);
		configMap.put("Ankle length", ankleLength);
	}
	
	@Override
	public void setSkeletonConfig(String joint, float newLength) {
		super.setSkeletonConfig(joint, newLength);
		switch(joint) {
		case "Hips width":
			hipsWidth = newLength;
			server.config.setProperty("body.hipsWidth", hipsWidth);
			leftHipNode.localTransform.setTranslation(hipsWidth / 2, 0, 0);
			rightHipNode.localTransform.setTranslation(-hipsWidth / 2, 0, 0);
			break;
		case "Hip length":
			hipsLength = newLength;
			server.config.setProperty("body.hipLength", hipsLength);
			leftKneeNode.localTransform.setTranslation(0, -hipsLength, 0);
			rightKneeNode.localTransform.setTranslation(0, -hipsLength, 0);
			break;
		case "Ankle length":
			ankleLength = newLength;
			server.config.setProperty("body.ankleLength", ankleLength);
			leftAnkleNode.localTransform.setTranslation(0, -ankleLength, 0);
			rightAnkleNode.localTransform.setTranslation(0, -ankleLength, 0);
			break;
		}
	}
	
	@Override
	public void updateLocalTransforms() {
		super.updateLocalTransforms();
		// Left Leg
		leftLegTracker.getRotation(hipBuf);
		leftAnkleTracker.getRotation(kneeBuf);

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
		
		computedLeftFootTracker.position.set(leftFootNode.worldTransform.getTranslation());
		computedLeftFootTracker.rotation.set(leftFootNode.worldTransform.getRotation());
		computedLeftFootTracker.dataTick();
		
		computedRightFootTracker.position.set(rightFootNode.worldTransform.getTranslation());
		computedRightFootTracker.rotation.set(rightFootNode.worldTransform.getRotation());
		computedRightFootTracker.dataTick();
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
		
		if(this.rightAnkleTracker != null) {
			this.rightAnkleTracker.resetFull(referenceRotation);
		}
	}
}
