package io.eiren.vr.processor;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import essentia.util.collections.FastList;
import io.eiren.vr.VRServer;
import io.eiren.vr.trackers.HMDTracker;
import io.eiren.vr.trackers.Tracker;
import io.eiren.vr.trackers.TrackerConfig;
import io.eiren.vr.trackers.TrackerStatus;

public class HumanPoseProcessor {
	
	private final VRServer server;
	private final HMDTracker hmd;
	private final List<ComputedHumanPoseTracker> computedTrackers = new FastList<>();
	private final Map<TrackerBodyPosition, AdjustedTracker> trackers = new EnumMap<>(TrackerBodyPosition.class);
	private HumanSkeleton skeleton;

	public HumanPoseProcessor(VRServer server, HMDTracker hmd) {
		this.server = server;
		this.hmd = hmd;
		computedTrackers.add(new ComputedHumanPoseTracker(ComputedHumanPoseTrackerPosition.WAIST));
		computedTrackers.add(new ComputedHumanPoseTracker(ComputedHumanPoseTrackerPosition.LEFT_ANKLE));
		computedTrackers.add(new ComputedHumanPoseTracker(ComputedHumanPoseTrackerPosition.RIGHT_ANKLE));
	}
	
	public List<? extends Tracker> getComputedTrackers() {
		return computedTrackers;
	}
	
	public void trackerAdded(Tracker tracker) {
		TrackerConfig config = server.getTrackerConfig(tracker);
		if(config.designation != null) {
			TrackerBodyPosition pos = TrackerBodyPosition.getByDesignation(config.designation);
			if(pos != null) {
				addTracker(tracker, pos);
			}
		}
	}
	
	private void addTracker(Tracker tracker, TrackerBodyPosition position) {
		AdjustedTracker tt = new AdjustedTracker(tracker, position);
		
		TrackerConfig config = server.getTrackerConfig(tt);
		if(config.adjustment != null)
			tt.adjustment.set(config.adjustment);
		
		trackers.put(position, tt);
		server.registerTracker(tt);
		updateSekeltonModel();
	}
	
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
			skeleton = new HumanSekeletonWithLegs(server, trackers, computedTrackers);
		} else {
			if(skeleton instanceof HumanSkeleonWithWaist) {
				return; // Proper skeleton applied
			}
			skeleton = new HumanSkeleonWithWaist(server, trackers.get(TrackerBodyPosition.WAIST), computedTrackers);
		}
	}
	
	public void resetTrackers() {
		Quaternion sensorRotation = new Quaternion();
		Quaternion hmdRotation = new Quaternion();
		Quaternion targetTrackerRotation = new Quaternion();
		hmd.getRotation(hmdRotation);

		// Adjust only yaw rotation
		Vector3f hmdFront = new Vector3f(0, 0, 1);
		hmdRotation.multLocal(hmdFront);
		hmdFront.multLocal(1, 0, 1).normalizeLocal();
		hmdRotation.lookAt(hmdFront, Vector3f.UNIT_Y);
		
		Iterator<AdjustedTracker> iterator = trackers.values().iterator();
		while(iterator.hasNext()) {
			AdjustedTracker tt = iterator.next();
			tt.getRotation(sensorRotation);

			// Adjust only yaw rotation
			Vector3f sensorFront = new Vector3f(0, 0, 1);
			sensorRotation.multLocal(sensorFront);
			sensorFront.multLocal(1, 0, 1).normalizeLocal();
			sensorRotation.lookAt(sensorFront, Vector3f.UNIT_Y);
			
			
			tt.position.baseRotation.mult(hmdRotation, targetTrackerRotation);
			tt.adjustment.set(sensorRotation).inverseLocal().multLocal(targetTrackerRotation);
			
			TrackerConfig config = server.getTrackerConfig(tt);
			config.adjustment = new Quaternion(tt.adjustment);
		}
	}
	
	public void update() {
		if(skeleton != null)
			skeleton.updatePose();
	}
	
	private static class AdjustedTracker implements Tracker {
		public final Tracker tracker;
		public final Quaternion adjustment = new Quaternion();
		public final TrackerBodyPosition position;
		
		public AdjustedTracker(Tracker tracker, TrackerBodyPosition position) {
			this.tracker = tracker;
			this.position = position;
		}
		
		@Override
		public boolean getRotation(Quaternion store) {
			tracker.getRotation(store);
			adjustment.mult(store, store);
			return true;
		}

		@Override
		public boolean getPosition(Vector3f store) {
			return tracker.getPosition(store);
		}

		@Override
		public String getName() {
			return tracker.getName() + "/adj";
		}

		@Override
		public TrackerStatus getStatus() {
			return tracker.getStatus();
		}
	}
}
