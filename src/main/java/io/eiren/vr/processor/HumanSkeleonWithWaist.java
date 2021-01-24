package io.eiren.vr.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import io.eiren.util.ann.VRServerThread;
import io.eiren.vr.VRServer;
import io.eiren.vr.trackers.HMDTracker;
import io.eiren.vr.trackers.Tracker;
import io.eiren.vr.trackers.TrackerStatus;

public class HumanSkeleonWithWaist extends HumanSkeleton {
	
	protected final Map<HumanJoint, Float> jointsMap = new HashMap<>();
	protected final VRServer server;
	
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
	protected float waistDistance = 0.55f;
	/**
	 * Distance from eyes to waist, defines reported
	 * tracker position, if you want to move resulting
	 * tracker up or down from actual waist
	 */
	protected float trackerWaistDistance = 0.55f;
	/**
	 * Distacne from eyes to the base of the neck
	 */
	protected float neckLength = 0.2f;
	/**
	 * Distance from eyes to ear
	 */
	protected float headShift = 0.00f;

	public HumanSkeleonWithWaist(VRServer server, Tracker waistTracker, List<ComputedHumanPoseTracker> computedTrackers) {
		this.wasitTracker = waistTracker;
		this.hmdTracker = server.hmdTracker;
		this.server = server;
		ComputedHumanPoseTracker cwt = null;
		for(int i = 0; i < computedTrackers.size(); ++i) {
			ComputedHumanPoseTracker t = computedTrackers.get(i);
			if(t.skeletonPosition == ComputedHumanPoseTrackerPosition.WAIST)
				cwt = t;
		}
		computedWaistTracker = cwt;
		cwt.setStatus(TrackerStatus.OK);
		headShift = server.config.getFloat("body.headShift", headShift);
		neckLength = server.config.getFloat("body.neckLength", neckLength);
		waistDistance = server.config.getFloat("body.waistDistance", waistDistance);
		trackerWaistDistance = server.config.getFloat("body.trackerWaistDistance", trackerWaistDistance);
		// Build skeleton
		hmdNode.attachChild(headNode);
		headNode.localTransform.setTranslation(0, 0, headShift);
		
		headNode.attachChild(neckNode);
		neckNode.localTransform.setTranslation(0, -neckLength, 0);
		
		neckNode.attachChild(waistNode);
		waistNode.localTransform.setTranslation(0, -waistDistance, 0);
		
		neckNode.attachChild(trackerWaistNode);
		trackerWaistNode.localTransform.setTranslation(0, -trackerWaistDistance, 0);
		
		jointsMap.put(HumanJoint.HEAD, headShift);
		jointsMap.put(HumanJoint.NECK, neckLength);
		jointsMap.put(HumanJoint.WAIST, waistDistance);
		jointsMap.put(HumanJoint.WASIT_VIRTUAL, trackerWaistDistance);
	}
	
	@Override
	public Map<HumanJoint, Float> getJointsMap() {
		return jointsMap;
	}

	@Override
	public void sentJointLength(HumanJoint joint, float newLength) {
		jointsMap.put(joint, newLength);
		switch(joint) {
		case HEAD:
			headShift = newLength;
			server.config.setProperty("body.headShift", headShift);
			headNode.localTransform.setTranslation(0, 0, headShift);
			break;
		case NECK:
			neckLength = newLength;
			server.config.setProperty("body.neckLength", neckLength);
			neckNode.localTransform.setTranslation(0, -neckLength, 0);
			break;
		case WAIST:
			waistDistance = newLength;
			server.config.setProperty("body.waistDistance", waistDistance);
			waistNode.localTransform.setTranslation(0, -waistDistance, 0);
			break;
		case WASIT_VIRTUAL:
			trackerWaistDistance = newLength;
			server.config.setProperty("body.trackerWaistDistance", trackerWaistDistance);
			trackerWaistNode.localTransform.setTranslation(0, -trackerWaistDistance, 0);
			break;
		}
	}
	
	@Override
	public TransformNode getRootNode() {
		return hmdNode;
	}
	
	@Override
	@VRServerThread
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
		trackerWaistNode.localTransform.setRotation(qBuf);

		// Pelvic bone doesn't tilt when humans tilt, unless they really try.
		// Can't calculate tilt without additional sensors, so just remove it
		// completely.
		qBuf.multLocal(vBuf);
		vBuf.multLocal(1, 0, 1); // Keep only yaw / Don't normalize, it's done by lookAt()
		qBuf.lookAt(vBuf, Vector3f.UNIT_Y);
		waistNode.localTransform.setRotation(qBuf);
	}
	
	protected void updateComputedTrackers() {
		computedWaistTracker.position.set(trackerWaistNode.worldTransform.getTranslation());
		computedWaistTracker.rotation.set(trackerWaistNode.worldTransform.getRotation());
	}
}
