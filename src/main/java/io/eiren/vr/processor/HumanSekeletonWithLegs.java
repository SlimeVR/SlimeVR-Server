package io.eiren.vr.processor;

import java.util.List;
import java.util.Map;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;

import io.eiren.vr.VRServer;
import io.eiren.vr.trackers.Tracker;
import io.eiren.vr.trackers.TrackerStatus;

public class HumanSekeletonWithLegs extends HumanSkeleonWithWaist {
	
	protected final float[] kneeAngles = new float[3];
	protected final float[] hipAngles = new float[3];
	protected final Quaternion hipBuf = new Quaternion();
	protected final Quaternion kneeBuf = new Quaternion();
	
	protected final Tracker leftLegTracker;
	protected final Tracker leftAnkleTracker;
	protected final ComputedHumanPoseTracker computedLeftAnkleTracker;
	protected final Tracker rightLegTracker;
	protected final Tracker rightAnkleTracker;
	protected final ComputedHumanPoseTracker computedRightAnkleTracker;
	
	protected final TransformNode leftHipNode = new TransformNode("Left-Hip", false);
	protected final TransformNode leftKneeNode = new TransformNode("Left-Knee", false);
	protected final TransformNode leftAnkleNode = new TransformNode("Left-Ankle", false);
	protected final TransformNode rightHipNode = new TransformNode("Right-Hip", false);
	protected final TransformNode rightKneeNode = new TransformNode("Right-Knee", false);
	protected final TransformNode rightAnkleNode = new TransformNode("Right-Ankle", false);
	
	/**
	 * Distance between centers of both hips
	 */
	protected float hipsWidth = 0.33f;
	/**
	 * Length from waist to knees
	 */
	protected float hipsLength = 0.46f;
	/**
	 * Distance from waist to ankle
	 */
	protected float ankleLength = 0.5f;
	
	protected float minKneePitch = 0f * FastMath.DEG_TO_RAD;
	protected float maxKneePitch = 90f * FastMath.DEG_TO_RAD;
	
	protected float kneeLerpFactor = 0.5f;

	public HumanSekeletonWithLegs(VRServer server, Map<TrackerBodyPosition, ? extends Tracker> trackers, List<ComputedHumanPoseTracker> computedTrackers) {
		super(server, trackers.get(TrackerBodyPosition.WAIST), computedTrackers);
		this.leftLegTracker = trackers.get(TrackerBodyPosition.LEFT_LEG);
		this.leftAnkleTracker = trackers.get(TrackerBodyPosition.LEFT_ANKLE);
		this.rightLegTracker = trackers.get(TrackerBodyPosition.RIGHT_LEG);
		this.rightAnkleTracker = trackers.get(TrackerBodyPosition.RIGHT_ANKLE);
		ComputedHumanPoseTracker lat = null;
		ComputedHumanPoseTracker rat = null;
		for(int i = 0; i < computedTrackers.size(); ++i) {
			ComputedHumanPoseTracker t = computedTrackers.get(i);
			if(t.skeletonPosition == ComputedHumanPoseTrackerPosition.LEFT_ANKLE)
				lat = t;
			if(t.skeletonPosition == ComputedHumanPoseTrackerPosition.RIGHT_ANKLE)
				rat = t;
		}
		computedLeftAnkleTracker = lat;
		computedRightAnkleTracker = rat;
		lat.setStatus(TrackerStatus.OK);
		rat.setStatus(TrackerStatus.OK);
		hipsWidth = server.config.getFloat("body.hipsWidth", hipsWidth);
		hipsLength = server.config.getFloat("body.hipsLength", hipsLength);
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
		
		configMap.put("Hips width", hipsWidth);
		configMap.put("Hips length", hipsLength);
		configMap.put("Legs length", ankleLength);
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
		case "Hips length":
			hipsLength = newLength;
			server.config.setProperty("body.hipsLength", hipsLength);
			leftKneeNode.localTransform.setTranslation(0, -hipsLength, 0);
			rightKneeNode.localTransform.setTranslation(0, -hipsLength, 0);
			break;
		case "Legs length":
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

		// Right Leg
		rightLegTracker.getRotation(hipBuf);
		rightAnkleTracker.getRotation(kneeBuf);
		
		calculateKneeLimits(hipBuf, kneeBuf, rightLegTracker.getConfidenceLevel(), rightAnkleTracker.getConfidenceLevel());
		
		rightHipNode.localTransform.setRotation(hipBuf);
		rightKneeNode.localTransform.setRotation(kneeBuf);
		rightAnkleNode.localTransform.setRotation(kneeBuf);
		
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
		
		computedLeftAnkleTracker.position.set(leftAnkleNode.worldTransform.getTranslation());
		computedLeftAnkleTracker.rotation.set(leftAnkleNode.worldTransform.getRotation());
		computedLeftAnkleTracker.dataTick();
		
		computedRightAnkleTracker.position.set(rightAnkleNode.worldTransform.getTranslation());
		computedRightAnkleTracker.rotation.set(rightAnkleNode.worldTransform.getRotation());
		computedRightAnkleTracker.dataTick();
	}
}
