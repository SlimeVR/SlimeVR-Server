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
	protected final TransformNode hmdNode = new TransformNode();
	protected final TransformNode neckNode = new TransformNode();
	protected final TransformNode waistNode = new TransformNode();
	
	protected float waistDistance = 0.72f;
	protected float waistSwingMultiplier = 1f;
	protected float neckLength = 0.2f;

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
		waistSwingMultiplier = server.config.getFloat("body.waistSwingMultiplier", waistSwingMultiplier);
		// Build skeleton
		hmdNode.attachChild(neckNode);
		waistNode.localTransform.setTranslation(0, -neckLength, 0);
		
		neckNode.attachChild(waistNode);
		waistNode.localTransform.setTranslation(0, -waistDistance + neckLength, 0);
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
		
		wasitTracker.getRotation(qBuf);
		if(waistSwingMultiplier != 1.0) {
			// TODO : Adjust waist swing if swing multiplier != 0
		}
		
		neckNode.localTransform.setRotation(qBuf);
		waistNode.localTransform.setRotation(qBuf);
	}
	
	protected void updateComputedTrackers() {
		computedWaistTracker.position.set(waistNode.worldTransform.getTranslation());
		computedWaistTracker.rotation.set(waistNode.worldTransform.getRotation());
	}
}
