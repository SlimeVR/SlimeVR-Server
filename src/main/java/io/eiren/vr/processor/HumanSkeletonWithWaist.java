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
import io.eiren.vr.trackers.TrackerUtils;

public class HumanSkeletonWithWaist extends HumanSkeleton {
	
	public static final float HEAD_SHIFT_DEFAULT = 0.1f;
	public static final float NECK_LENGTH_DEFAULT = 0.1f;
	
	protected final Map<String, Float> configMap = new HashMap<>();
	protected final VRServer server;
	
	protected final float[] waistAngles = new float[3];
	protected final Quaternion qBuf = new Quaternion();
	protected final Vector3f vBuf = new Vector3f();
	
	protected final Tracker waistTracker;
	protected final Tracker chestTracker;
	protected final HMDTracker hmdTracker;
	protected final ComputedHumanPoseTracker computedWaistTracker;
	protected final ComputedHumanPoseTracker computedChestTracker;
	protected final TransformNode hmdNode = new TransformNode("HMD", false);
	protected final TransformNode headNode = new TransformNode("Head", false);
	protected final TransformNode neckNode = new TransformNode("Neck", false);
	protected final TransformNode waistNode = new TransformNode("Waist", false);
	protected final TransformNode chestNode = new TransformNode("Chest", false);
	protected final TransformNode trackerWaistNode = new TransformNode("Waist-Tracker", false);
	
	protected float chestDistance = 0.42f;
	/**
	 * Distance from eyes to waist
	 */
	protected float waistDistance = 0.85f;
	/**
	 * Distance from eyes to waist, defines reported
	 * tracker position, if you want to move resulting
	 * tracker up or down from actual waist
	 */
	protected float trackerWaistDistance = 0.0f;
	/**
	 * Distacne from eyes to the base of the neck
	 */
	protected float neckLength = NECK_LENGTH_DEFAULT;
	/**
	 * Distance from eyes to ear
	 */
	protected float headShift = HEAD_SHIFT_DEFAULT;
	
	public HumanSkeletonWithWaist(VRServer server, List<ComputedHumanPoseTracker> computedTrackers) {
		List<Tracker> allTracekrs = server.getAllTrackers();
		this.waistTracker = TrackerUtils.findTrackerForBodyPositionOrEmpty(allTracekrs, TrackerBodyPosition.WAIST, TrackerBodyPosition.CHEST);
		this.chestTracker = TrackerUtils.findTrackerForBodyPositionOrEmpty(allTracekrs, TrackerBodyPosition.CHEST, TrackerBodyPosition.WAIST);
		this.hmdTracker = server.hmdTracker;
		this.server = server;
		ComputedHumanPoseTracker cwt = null;
		ComputedHumanPoseTracker cct = null;
		for(int i = 0; i < computedTrackers.size(); ++i) {
			ComputedHumanPoseTracker t = computedTrackers.get(i);
			if(t.skeletonPosition == ComputedHumanPoseTrackerPosition.WAIST)
				cwt = t;
			if(t.skeletonPosition == ComputedHumanPoseTrackerPosition.CHEST)
				cct = t;
		}
		computedWaistTracker = cwt;
		computedChestTracker = cct;
		cwt.setStatus(TrackerStatus.OK);
		headShift = server.config.getFloat("body.headShift", headShift);
		neckLength = server.config.getFloat("body.neckLength", neckLength);
		chestDistance = server.config.getFloat("body.chestDistance", chestDistance);
		waistDistance = server.config.getFloat("body.waistDistance", waistDistance);
		trackerWaistDistance = server.config.getFloat("body.trackerWaistDistance", trackerWaistDistance);
		// Build skeleton
		hmdNode.attachChild(headNode);
		headNode.localTransform.setTranslation(0, 0, headShift);
		
		headNode.attachChild(neckNode);
		neckNode.localTransform.setTranslation(0, -neckLength, 0);
		
		neckNode.attachChild(chestNode);
		chestNode.localTransform.setTranslation(0, -chestDistance, 0);
		
		chestNode.attachChild(waistNode);
		waistNode.localTransform.setTranslation(0, -(waistDistance - chestDistance), 0);
		
		chestNode.attachChild(trackerWaistNode);
		trackerWaistNode.localTransform.setTranslation(0, -(waistDistance + trackerWaistDistance - chestDistance), 0);
		
		configMap.put("Head", headShift);
		configMap.put("Neck", neckLength);
		configMap.put("Chest", chestDistance);
		configMap.put("Waist", waistDistance);
		configMap.put("Virtual waist", trackerWaistDistance);
	}
	
	@Override
	public void resetSkeletonConfig(String joint) {
		switch(joint) {
		case "All": // Reset all joints according to height
			resetSkeletonConfig("Head");
			resetSkeletonConfig("Neck");
			resetSkeletonConfig("Virtual waist");
			resetSkeletonConfig("Waist");
			resetSkeletonConfig("Chest");
			break;
		case "Head":
			setSkeletonConfig(joint, HEAD_SHIFT_DEFAULT);
			break;
		case "Neck":
			setSkeletonConfig(joint, NECK_LENGTH_DEFAULT);
			break;
		case "Virtual waist":
			setSkeletonConfig(joint, 0.0f);
			break;
		case "Chest":
			setSkeletonConfig(joint, waistDistance / 2.0f);
			break;
		case "Waist": // Puts Waist in the middle of the height
			Vector3f vec = new Vector3f();
			hmdTracker.getPosition(vec);
			float height = vec.y;
			if(height > 0.5f) { // Reset only if floor level is right, todo: read floor level from SteamVR if it's not 0
				setSkeletonConfig(joint, (height) / 2.0f);
			}
			break;
		}
	}
	
	@Override
	public Map<String, Float> getSkeletonConfig() {
		return configMap;
	}
	
	@Override
	public void setSkeletonConfig(String joint, float newLength) {
		configMap.put(joint, newLength);
		switch(joint) {
		case "Head":
			headShift = newLength;
			server.config.setProperty("body.headShift", headShift);
			headNode.localTransform.setTranslation(0, 0, headShift);
			break;
		case "Neck":
			neckLength = newLength;
			server.config.setProperty("body.neckLength", neckLength);
			neckNode.localTransform.setTranslation(0, -neckLength, 0);
			break;
		case "Waist":
			waistDistance = newLength;
			server.config.setProperty("body.waistDistance", waistDistance);
			waistNode.localTransform.setTranslation(0, -(waistDistance - chestDistance), 0);
			trackerWaistNode.localTransform.setTranslation(0, -(waistDistance + trackerWaistDistance - chestDistance), 0);
			break;
		case "Chest":
			chestDistance = newLength;
			server.config.setProperty("body.chestDistance", chestDistance);
			chestNode.localTransform.setTranslation(0, -chestDistance, 0);
			waistNode.localTransform.setTranslation(0, -(waistDistance - chestDistance), 0);
			trackerWaistNode.localTransform.setTranslation(0, -(waistDistance + trackerWaistDistance - chestDistance), 0);
			break;
		case "Virtual waist":
			trackerWaistDistance = newLength;
			server.config.setProperty("body.trackerWaistDistance", trackerWaistDistance);
			trackerWaistNode.localTransform.setTranslation(0, -(waistDistance + trackerWaistDistance - chestDistance), 0);
			break;
		}
	}
	
	public boolean getSkeletonConfigBoolean(String config) {
		return false;
	}
	
	public void setSkeletonConfigBoolean(String config, boolean newState) {
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
		if(hmdTracker.getPosition(vBuf)) {
			hmdNode.localTransform.setTranslation(vBuf);
		}
		if(hmdTracker.getRotation(qBuf)) {
			hmdNode.localTransform.setRotation(qBuf);
			headNode.localTransform.setRotation(qBuf);
		}
		
		if(chestTracker.getRotation(qBuf))
			neckNode.localTransform.setRotation(qBuf);
		
		if(waistTracker.getRotation(qBuf)) {
			trackerWaistNode.localTransform.setRotation(qBuf);
			chestNode.localTransform.setRotation(qBuf);
			waistNode.localTransform.setRotation(qBuf);
		}
	}
	
	protected void updateComputedTrackers() {
		if(computedWaistTracker != null) {
			computedWaistTracker.position.set(trackerWaistNode.worldTransform.getTranslation());
			computedWaistTracker.rotation.set(trackerWaistNode.worldTransform.getRotation());
			computedWaistTracker.dataTick();
		}
		
		if(computedChestTracker != null) {
			computedChestTracker.position.set(chestNode.worldTransform.getTranslation());
			computedChestTracker.rotation.set(neckNode.worldTransform.getRotation());
			computedChestTracker.dataTick();
		}
	}
	
	@Override
	@VRServerThread
	public void resetTrackersFull() {
		// Each tracker uses the tracker before it to adjust iteself,
		// so trackers that don't need adjustments could be used too
		Quaternion referenceRotation = new Quaternion();
		server.hmdTracker.getRotation(referenceRotation);
		
		this.chestTracker.resetFull(referenceRotation);
		this.chestTracker.getRotation(referenceRotation);
		
		this.waistTracker.resetFull(referenceRotation);
	}
	
	@Override
	@VRServerThread
	public void resetTrackersYaw() {
		// Each tracker uses the tracker before it to adjust iteself,
		// so trackers that don't need adjustments could be used too
		Quaternion referenceRotation = new Quaternion();
		server.hmdTracker.getRotation(referenceRotation);
		
		this.chestTracker.resetYaw(referenceRotation);
		this.chestTracker.getRotation(referenceRotation);
		
		this.waistTracker.resetYaw(referenceRotation);
	}
}
