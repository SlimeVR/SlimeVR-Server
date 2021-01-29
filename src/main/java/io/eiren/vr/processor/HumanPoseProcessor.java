package io.eiren.vr.processor;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.jme3.math.Quaternion;

import io.eiren.util.ann.ThreadSafe;
import io.eiren.util.ann.VRServerThread;
import io.eiren.util.collections.FastList;
import io.eiren.vr.VRServer;
import io.eiren.vr.trackers.AdjustedTracker;
import io.eiren.vr.trackers.AdjustedYawTracker;
import io.eiren.vr.trackers.HMDTracker;
import io.eiren.vr.trackers.Tracker;
import io.eiren.vr.trackers.TrackerConfig;
import io.eiren.vr.trackers.TrackerStatus;

public class HumanPoseProcessor {
	
	private final VRServer server;
	private final HMDTracker hmd;
	private final List<ComputedHumanPoseTracker> computedTrackers = new FastList<>();
	private final Map<TrackerBodyPosition, AdjustedTracker> trackers = new EnumMap<>(TrackerBodyPosition.class);
	private final List<Consumer<HumanSkeleton>> onSkeletonUpdated = new FastList<>();
	private HumanSkeleton skeleton;

	public HumanPoseProcessor(VRServer server, HMDTracker hmd) {
		this.server = server;
		this.hmd = hmd;
		computedTrackers.add(new ComputedHumanPoseTracker(ComputedHumanPoseTrackerPosition.WAIST));
		computedTrackers.add(new ComputedHumanPoseTracker(ComputedHumanPoseTrackerPosition.LEFT_ANKLE));
		computedTrackers.add(new ComputedHumanPoseTracker(ComputedHumanPoseTrackerPosition.RIGHT_ANKLE));
	}

	@VRServerThread
	public void addSkeletonUpdatedCallback(Consumer<HumanSkeleton> consumer) {
		onSkeletonUpdated.add(consumer);
		if(skeleton != null)
			consumer.accept(skeleton);
	}
	
	@ThreadSafe
	public List<? extends Tracker> getComputedTrackers() {
		return computedTrackers;
	}

	@VRServerThread
	public void trackerAdded(Tracker tracker) {
		TrackerConfig config = server.getTrackerConfig(tracker);
		if(config.designation != null) {
			TrackerBodyPosition pos = TrackerBodyPosition.getByDesignation(config.designation);
			if(pos != null) {
				addTracker(tracker, pos);
			}
		}
	}

	@VRServerThread
	private void addTracker(Tracker tracker, TrackerBodyPosition position) {
		AdjustedTracker tt = new AdjustedYawTracker(tracker);
		
		trackers.put(position, tt);
		server.registerTracker(tt);
		updateSekeltonModel();
	}

	@VRServerThread
	private void updateSekeltonModel() {
		boolean hasWaist = false;
		boolean hasBothLegs = false;
		//boolean hasChest = false;
		if(trackers.get(TrackerBodyPosition.WAIST) != null)
			hasWaist = true;
		//if(trackers.get(TrackerBodyPosition.CHEST) != null)
		//	hasChest = true;
		if(trackers.get(TrackerBodyPosition.LEFT_ANKLE) != null && trackers.get(TrackerBodyPosition.LEFT_LEG) != null
				&& trackers.get(TrackerBodyPosition.RIGHT_ANKLE) != null && trackers.get(TrackerBodyPosition.RIGHT_LEG) != null)
			hasBothLegs = true;
		if(!hasWaist) {
			skeleton = null; // Can't track anything without waist
		} else if(hasBothLegs) {
			if(skeleton instanceof HumanSekeletonWithLegs) {
				return; // Proper skeleton applied
			}
			disconnectAllTrackers();
			skeleton = new HumanSekeletonWithLegs(server, trackers, computedTrackers);
			for(int i = 0; i < onSkeletonUpdated.size(); ++i)
				onSkeletonUpdated.get(i).accept(skeleton);
		} else {
			if(skeleton instanceof HumanSkeleonWithWaist) {
				return; // Proper skeleton applied
			}
			disconnectAllTrackers();
			skeleton = new HumanSkeleonWithWaist(server, trackers.get(TrackerBodyPosition.WAIST), computedTrackers);
			for(int i = 0; i < onSkeletonUpdated.size(); ++i)
				onSkeletonUpdated.get(i).accept(skeleton);
		}
	}

	@VRServerThread
	private void disconnectAllTrackers() {
		for(int i = 0; i < computedTrackers.size(); ++i) {
			computedTrackers.get(i).setStatus(TrackerStatus.DISCONNECTED);
		}
	}

	@VRServerThread
	public void resetTrackers() {
		Quaternion hmdRotation = new Quaternion();
		hmd.getRotation(hmdRotation);
		
		Iterator<AdjustedTracker> iterator = trackers.values().iterator();
		while(iterator.hasNext()) {
			AdjustedTracker tt = iterator.next();
			tt.adjust(hmdRotation);
			
			TrackerConfig config = server.getTrackerConfig(tt);
			tt.saveConfig(config);
		}
	}

	@VRServerThread
	public void update() {
		if(skeleton != null)
			skeleton.updatePose();
	}
}
