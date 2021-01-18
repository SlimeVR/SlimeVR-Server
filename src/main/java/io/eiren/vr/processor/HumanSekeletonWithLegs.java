package io.eiren.vr.processor;

import java.util.List;
import java.util.Map;

import io.eiren.vr.VRServer;
import io.eiren.vr.trackers.Tracker;
import io.eiren.vr.trackers.TrackerStatus;

public class HumanSekeletonWithLegs extends HumanSkeleonWithWaist {
	
	protected final Tracker leftLegTracker;
	protected final Tracker leftAnkleTracker;
	protected final ComputedHumanPoseTracker computedLeftAnkleTracker;
	protected final Tracker rightLegTracker;
	protected final Tracker rightAnkleTracker;
	protected final ComputedHumanPoseTracker computedRightAnkleTracker;
	
	protected final TransformNode leftHipNode = new TransformNode();
	protected final TransformNode leftKneeNode = new TransformNode();
	protected final TransformNode leftAnkleNode = new TransformNode();
	protected final TransformNode rightHipNode = new TransformNode();
	protected final TransformNode rightKneeNode = new TransformNode();
	protected final TransformNode rightAnkleNode = new TransformNode();
	
	protected float hipsWidth = 0.3f;
	protected float hipLength = 0.86f;
	protected float ankleLength = 0.42f;

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
		
		waistNode.attachChild(leftHipNode);
		leftHipNode.localTransform.setTranslation(hipsWidth / 2, 0, 0);
		
		waistNode.attachChild(rightHipNode);
		rightHipNode.localTransform.setTranslation(-hipsWidth / 2, 0, 0);
		
		leftHipNode.attachChild(leftKneeNode);
		leftKneeNode.localTransform.setTranslation(0, -hipLength, 0);
		
		rightHipNode.attachChild(rightKneeNode);
		rightKneeNode.localTransform.setTranslation(0, -hipLength, 0);
		
		leftKneeNode.attachChild(leftAnkleNode);
		leftAnkleNode.localTransform.setTranslation(0, -ankleLength, 0);
		
		rightKneeNode.attachChild(rightAnkleNode);
		rightAnkleNode.localTransform.setTranslation(0, -ankleLength, 0);
	}
	
	@Override
	public void updateLocalTransforms() {
		super.updateLocalTransforms();
		leftLegTracker.getRotation(qBuf);
		leftHipNode.localTransform.setRotation(qBuf);
		
		rightLegTracker.getRotation(qBuf);
		rightHipNode.localTransform.setRotation(qBuf);
		
		leftAnkleTracker.getRotation(qBuf);
		leftKneeNode.localTransform.setRotation(qBuf);
		leftAnkleNode.localTransform.setRotation(qBuf);
		
		rightAnkleTracker.getRotation(qBuf);
		rightKneeNode.localTransform.setRotation(qBuf);
		rightAnkleNode.localTransform.setRotation(qBuf);
	}

	@Override
	protected void updateComputedTrackers() {
		super.updateComputedTrackers();
		
		computedLeftAnkleTracker.position.set(leftAnkleNode.worldTransform.getTranslation());
		computedLeftAnkleTracker.rotation.set(leftAnkleNode.worldTransform.getRotation());
		
		computedRightAnkleTracker.position.set(rightAnkleNode.worldTransform.getTranslation());
		computedRightAnkleTracker.rotation.set(rightAnkleNode.worldTransform.getRotation());
	}
}
