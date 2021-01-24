package io.eiren.vr.trackers;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public class AdjustedTracker implements Tracker {
	
	public final Tracker tracker;
	public final Quaternion adjustment = new Quaternion();
	private final Quaternion smoothedQuaternion = new Quaternion();
	private float[] angles = new float[3];
	private float[] lastAngles = new float[3];
	public float smooth = 2 * FastMath.DEG_TO_RAD;
	
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
		store.toAngles(angles);
		if(Math.abs(angles[0] - lastAngles[0]) > smooth || Math.abs(angles[1] - lastAngles[1]) > smooth || Math.abs(angles[2] - lastAngles[2]) > smooth) {
			smoothedQuaternion.set(store);
			store.toAngles(lastAngles);
		} else {
			store.set(smoothedQuaternion);
		}
		
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