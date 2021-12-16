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
import io.eiren.vr.trackers.TrackerPosition;
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
	protected final Tracker hipTracker;
	protected final HMDTracker hmdTracker;
	protected final ComputedHumanPoseTracker computedWaistTracker;
	protected final ComputedHumanPoseTracker computedChestTracker;
	protected final TransformNode hmdNode = new TransformNode("HMD", false);
	protected final TransformNode headNode = new TransformNode("Head", false);
	protected final TransformNode neckNode = new TransformNode("Neck", false);
	protected final TransformNode waistNode = new TransformNode("Waist", false);
	protected final TransformNode chestNode = new TransformNode("Chest", false);
	protected final TransformNode trackerWaistNode = new TransformNode("Waist-Tracker", false);
	protected final TransformNode hipNode = new TransformNode("Hip", false);
	
	/**
	 * Distance from shoulders to chest
	 */
	protected float chestDistance = 0.32f;
	/**
	 * Distance from hip to waist
	 */
	protected float waistDistance = 0.05f;
	/**
	 * Distance from shoulder to hip
	 */
	protected float torsoLength = 0.64f;
	/**
	 * Distance from eyes to hip, defines reported
	 * tracker position, if you want to move resulting
	 * tracker up or down from actual hip
	 */
	protected float hipOffset = 0.0f;
	/**
	 * Distance from eyes to the base of the neck
	 */
	protected float neckLength = NECK_LENGTH_DEFAULT;
	/**
	 * Distance from eyes to ear
	 */
	protected float headShift = HEAD_SHIFT_DEFAULT;
	
	public HumanSkeletonWithWaist(VRServer server, List<ComputedHumanPoseTracker> computedTrackers) {
		List<Tracker> allTrackers = server.getAllTrackers();
		this.waistTracker = TrackerUtils.findTrackerForBodyPositionOrEmpty(allTrackers, TrackerPosition.WAIST, TrackerPosition.CHEST, TrackerPosition.HIP);
		this.chestTracker = TrackerUtils.findTrackerForBodyPositionOrEmpty(allTrackers, TrackerPosition.CHEST, TrackerPosition.WAIST, TrackerPosition.HIP);
		this.hipTracker = TrackerUtils.findTrackerForBodyPositionOrEmpty(allTrackers, TrackerPosition.HIP, TrackerPosition.WAIST, TrackerPosition.CHEST);
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
		torsoLength = server.config.getFloat("body.torsoLength", torsoLength);
		hipOffset = server.config.getFloat("body.hipOffset", hipOffset);
		// Build skeleton
		hmdNode.attachChild(headNode);
		headNode.localTransform.setTranslation(0, 0, headShift);
		
		headNode.attachChild(neckNode);
		neckNode.localTransform.setTranslation(0, -neckLength, 0);
		
		neckNode.attachChild(chestNode);
		chestNode.localTransform.setTranslation(0, -chestDistance, 0);
		
		chestNode.attachChild(waistNode);
		waistNode.localTransform.setTranslation(0, (chestDistance - torsoLength + waistDistance), 0);

		waistNode.attachChild(hipNode);
		hipNode.localTransform.setTranslation(0, -waistDistance, 0);

		hipNode.attachChild(trackerWaistNode);
		trackerWaistNode.localTransform.setTranslation(0, hipOffset, 0);
		
		configMap.put("Head", headShift);
		configMap.put("Neck", neckLength);
		configMap.put("Chest", chestDistance);
		configMap.put("Waist", waistDistance);
		configMap.put("Hip offset", hipOffset);
		configMap.put("Torso", torsoLength);
	}
	
	@Override
	public void resetSkeletonConfig(String joint) {
		switch(joint) {
		case "All": // Reset all joints according to height
			resetSkeletonConfig("Head");
			resetSkeletonConfig("Neck");
			resetSkeletonConfig("Hip offset");
			resetSkeletonConfig("Torso");
			resetSkeletonConfig("Waist");
			resetSkeletonConfig("Chest");
			break;
		case "Head":
			setSkeletonConfig(joint, HEAD_SHIFT_DEFAULT);
			break;
		case "Neck":
			setSkeletonConfig(joint, NECK_LENGTH_DEFAULT);
			break;
		case "Torso": // Distance from shoulders to hip (full torso length)
			Vector3f vec = new Vector3f();
			hmdTracker.getPosition(vec);
			float height = vec.y;
			if(height > 0.5f) { // Reset only if floor level is right, TODO: read floor level from SteamVR if it's not 0
				setSkeletonConfig(joint, ((height) / 2.0f) - neckLength);
			}
			else// if floor level is incorrect
			{
				setSkeletonConfig(joint, 0.64f);
			}
			break;
		case "Chest": //Chest is roughly half of the upper body (shoulders to chest)
			setSkeletonConfig(joint, torsoLength / 2.0f);
			break;
		case "Waist": // waist length is from hips to waist
			setSkeletonConfig(joint, 0.05f);
			break;
		case "Hip offset":
			setSkeletonConfig(joint, 0.0f);
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
		case "Torso":
			torsoLength = newLength;
			server.config.setProperty("body.torsoLength", torsoLength);
			waistNode.localTransform.setTranslation(0, (chestDistance - torsoLength + waistDistance), 0);
			break;
		case "Chest":
			chestDistance = newLength;
			server.config.setProperty("body.chestDistance", chestDistance);
			chestNode.localTransform.setTranslation(0, -chestDistance, 0);
			waistNode.localTransform.setTranslation(0, (chestDistance - torsoLength + waistDistance), 0);
			break;
		case "Waist":
			waistDistance = newLength;
			server.config.setProperty("body.waistDistance", waistDistance);
			waistNode.localTransform.setTranslation(0, (chestDistance - torsoLength + waistDistance), 0);
			hipNode.localTransform.setTranslation(0, -waistDistance, 0);
			break;
		case "Hip offset":
			hipOffset = newLength;
			server.config.setProperty("body.hipOffset", hipOffset);
			trackerWaistNode.localTransform.setTranslation(0, hipOffset, 0);
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
			chestNode.localTransform.setRotation(qBuf);
		}
		if(hipTracker.getRotation(qBuf)) {
			waistNode.localTransform.setRotation(qBuf);
			trackerWaistNode.localTransform.setRotation(qBuf);
			hipNode.localTransform.setRotation(qBuf);
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
		this.waistTracker.getRotation(referenceRotation);
		
		this.hipTracker.resetFull(referenceRotation);
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
		this.waistTracker.getRotation(referenceRotation);

		this.hipTracker.resetYaw(referenceRotation);
	}
}
