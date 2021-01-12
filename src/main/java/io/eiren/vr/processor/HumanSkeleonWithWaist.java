package io.eiren.vr.processor;

import java.util.List;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import io.eiren.vr.VRServer;
import io.eiren.vr.trackers.HMDTracker;
import io.eiren.vr.trackers.Tracker;

public class HumanSkeleonWithWaist extends HumanSkeleton {
	
	protected final Quaternion qBuf = new Quaternion();
	protected final Vector3f vBuf = new Vector3f();
	
	protected final Tracker wasitTracker;
	protected final HMDTracker hmdTracker;
	protected final ComputedHumanPoseTracker computedWaistTracker;
	protected float waistDistance = 0.63f;
	protected float waistSwingMultiplier = 1f;
	protected final TransformNode hmdNode = new TransformNode();
	protected final TransformNode waistNode = new TransformNode();

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
		waistDistance = server.config.getFloat("body.waistDistance", waistDistance);
		waistSwingMultiplier = server.config.getFloat("body.waistSwingMultiplier", waistSwingMultiplier);
		// Build skeleton
		hmdNode.attachChild(waistNode);
		waistNode.localTransform.setTranslation(0, -waistDistance, 0);
	}
	
	@Override
	public void updatePose() {
		wasitTracker.getRotation(qBuf);
		if(waistSwingMultiplier != 1.0) {
			// TODO : Adjust waist swing if swing multiplier != 0
		}
		
		hmdTracker.getPosition(vBuf);
		hmdNode.localTransform.setTranslation(vBuf);
		hmdNode.localTransform.setRotation(qBuf);
		
		hmdNode.update();
		
		updateTrackers();
	}
	
	protected void updateTrackers() {
		computedWaistTracker.position.set(waistNode.worldTransform.getTranslation());
		computedWaistTracker.rotation.set(waistNode.worldTransform.getRotation());
	}
}
