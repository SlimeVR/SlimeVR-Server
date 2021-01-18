package io.eiren.vr.trackers;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public class AdjustedTracker implements Tracker {
	
	public final Tracker tracker;
	public final Quaternion adjustment = new Quaternion();
	
	public AdjustedTracker(Tracker tracker) {
		this.tracker = tracker;
	}
	
	public void saveAdjustment(TrackerConfig cfg) {
		cfg.adjustment = new Quaternion(adjustment);
	}
	
	public void adjust(Quaternion reference) {
		Quaternion sensorRotation = new Quaternion();
		tracker.getRotation(sensorRotation);
		
		adjustment.set(sensorRotation).inverseLocal().multLocal(reference);
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