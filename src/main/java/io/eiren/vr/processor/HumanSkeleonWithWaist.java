package io.eiren.vr.processor;

import java.util.List;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import io.eiren.vr.VRServer;
import io.eiren.vr.trackers.HMDTracker;
import io.eiren.vr.trackers.Tracker;
import io.eiren.vr.trackers.TrackerStatus;

public class HumanSkeleonWithWaist extends HumanSkeleton {
	
	protected final Quaternion qBuf = new Quaternion();
	protected final Vector3f vBuf = new Vector3f();
	
	protected final Tracker wasitTracker;
	protected final HMDTracker hmdTracker;
	protected final ComputedHumanPoseTracker computedWaistTracker;
	protected final TransformNode hmdNode = new TransformNode("HMD", false);
	protected final TransformNode headNode = new TransformNode("Head", false);
	protected final TransformNode neckNode = new TransformNode("Neck", false);
	protected final TransformNode waistNode = new TransformNode("Waist", false);
	protected final TransformNode trackerWaistNode = new TransformNode("Waist-Tracker", false);
	
	/**
	 * Distance from eyes to waist
	 */
	protected float waistDistance = 0.72f;
	/**
	 * Distance from eyes to waist, defines reported
	 * tracker position, if you want to move resulting
	 * tracker up or down from actual waist
	 */
	protected float trackerWaistDistance = 0.65f;
	/**
	 * Distacne from eyes to the base of the neck
	 */
	protected float neckLength = 0.2f;
	/**
	 * Distance from eyes to ear
	 */
	protected float headShift = 0.09f;

	public HumanSkeleonWithWaist(VRServer server, Tracker waistTracker, List<ComputedHumanPoseTracker> computedTrackers) {
		this.wasitTracker = waistTracker;
		this.hmdTracker = server.hmdTracker;
		ComputedHumanPoseTracker cwt = null;
		for(int i = 0; i < computedTrackers.size(); ++i) {
			ComputedHumanPoseTracker t = computedTrackers.get(i);
			if(t.skeletonPosition == ComputedHumanPoseTrackerPosition.WAIST)
				cwt = t;
		}
		computedWaistTracker = cwt;
		cwt.setStatus(TrackerStatus.OK);
		waistDistance = server.config.getFloat("body.waistDistance", waistDistance);
		// Build skeleton
		hmdNode.attachChild(headNode);
		headNode.localTransform.setTranslation(0, 0, headShift);
		
		headNode.attachChild(neckNode);
		neckNode.localTransform.setTranslation(0, -neckLength, 0);
		
		neckNode.attachChild(waistNode);
		waistNode.localTransform.setTranslation(0, -waistDistance + neckLength, 0);
		
		neckNode.attachChild(trackerWaistNode);
		trackerWaistNode.localTransform.setTranslation(0, -trackerWaistDistance + neckLength, 0);
	}
	
	@Override
	public TransformNode getRootNode() {
		return hmdNode;
	}
	
	@Override
	public void updatePose() {
		updateLocalTransforms();
		hmdNode.update();
		updateComputedTrackers();
	}
	
	protected void updateLocalTransforms() {
		hmdTracker.getPosition(vBuf);
		hmdTracker.getRotation(qBuf);
		hmdNode.localTransform.setTranslation(vBuf);
		hmdNode.localTransform.setRotation(qBuf);
		headNode.localTransform.setRotation(qBuf);
		
		wasitTracker.getRotation(qBuf);
		
		neckNode.localTransform.setRotation(qBuf);
		waistNode.localTransform.setRotation(qBuf);
		trackerWaistNode.localTransform.setRotation(qBuf);
	}
	
	protected void updateComputedTrackers() {
		computedWaistTracker.position.set(trackerWaistNode.worldTransform.getTranslation());
		computedWaistTracker.rotation.set(trackerWaistNode.worldTransform.getRotation());
	}
}
