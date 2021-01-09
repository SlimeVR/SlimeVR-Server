package io.eiren.vr.processor;

import java.util.EnumMap;
import java.util.List;

import essentia.util.collections.FastList;
import io.eiren.vr.trackers.ComputedTracker;
import io.eiren.vr.trackers.HMDTracker;
import io.eiren.vr.trackers.Tracker;

public class HumanPoseProcessor {
	
	private final HMDTracker hmd;
	private final List<ComputedTracker> computedTrackers = new FastList<>();
	private final EnumMap<TrackerPosition, Tracker> trackers = new EnumMap<>(TrackerPosition.class);
	private final ComputedTracker waist;
	private final ComputedTracker leftFoot;
	private final ComputedTracker rightFoot;
	
	public HumanPoseProcessor(HMDTracker hmd) {
		this.hmd = hmd;
		computedTrackers.add(waist = new ComputedTracker("Waist"));
		computedTrackers.add(leftFoot = new ComputedTracker("Left Foot"));
		computedTrackers.add(rightFoot = new ComputedTracker("Right Foot"));
	}
	
	public List<? extends Tracker> getComputedTrackers() {
		return computedTrackers;
	}
	
	public void addTracker(Tracker tracker, TrackerPosition position) {
		synchronized(trackers) {
			trackers.put(position, tracker);
		}
	}
	
	public void update() {
		
	}
	
	public enum TrackerPosition {
		NECK,
		CHEST,
		WAIST,
		LEFT_LEG,
		RIGHT_LEG,
		LEFT_FOOT,
		RIGHT_FOOT
	}
}
