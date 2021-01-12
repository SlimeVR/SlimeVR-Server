package io.eiren.vr.processor;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;

import essentia.util.collections.FastList;
import io.eiren.vr.trackers.ComputedTracker;
import io.eiren.vr.trackers.HMDTracker;
import io.eiren.vr.trackers.Tracker;

public class HumanPoseProcessor {
	
	private final HMDTracker hmd;
	private final List<ComputedTracker> computedTrackers = new FastList<>();
	private final EnumMap<TrackerPosition, TransformedTracker> trackers = new EnumMap<>(TrackerPosition.class);
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
		TransformedTracker tt = new TransformedTracker(tracker);
		synchronized(trackers) {
			trackers.put(position, tt);
		}
	}
	
	public void resetTrackers() {
		Quaternion buff = new Quaternion();
		Quaternion targetRotation = new Quaternion();
		hmd.getRotation(targetRotation);
		
		// TODO
		
		synchronized(trackers) {
			Iterator<TransformedTracker> iterator = trackers.values().iterator();
			while(iterator.hasNext()) {
				TransformedTracker tt = iterator.next();
				tt.getRotation(buff);
				// TODO : Set offset
			}
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
	
	private static class TransformedTracker {
		public final Tracker tracker;
		public final Quaternion transformation = new Quaternion();
		
		public TransformedTracker(Tracker tracker) {
			this.tracker = tracker;
		}
		
		public void getRotation(Quaternion store) {
			tracker.getRotation(store);
			store.multLocal(transformation);
		}
	}
}
