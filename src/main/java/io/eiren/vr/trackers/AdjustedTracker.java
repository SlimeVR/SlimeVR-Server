package io.eiren.vr.trackers;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public class AdjustedTracker implements Tracker {
	
	public final Tracker tracker;
	public final Quaternion adjustment = new Quaternion();
	private final Quaternion smoothedQuaternion = new Quaternion();
	private float[] angles = new float[3];
	protected float[] lastAngles = new float[3];
	public float smooth = 0 * FastMath.DEG_TO_RAD;
	
	protected float confidenceMultiplier = 1.0f;
	
	public AdjustedTracker(Tracker tracker) {
		this.tracker = tracker;
	}
	
	@Override
	public void loadConfig(TrackerConfig config) {
		if(config.adjustment != null)
			adjustment.set(config.adjustment);
	}
	
	@Override
	public void saveConfig(TrackerConfig cfg) {
		cfg.adjustment = new Quaternion(adjustment);
	}
	
	public void adjust(Quaternion reference) {
		Quaternion targetTrackerRotation = new Quaternion(reference);
		
		// Use only yaw rotation
		Vector3f hmdFront = new Vector3f(0, 0, 1);
		targetTrackerRotation.multLocal(hmdFront);
		hmdFront.multLocal(1, 0, 1).normalizeLocal();
		targetTrackerRotation.lookAt(hmdFront, Vector3f.UNIT_Y);
		
		Quaternion sensorRotation = new Quaternion();
		tracker.getRotation(sensorRotation);
		
		adjustment.set(sensorRotation).inverseLocal().multLocal(targetTrackerRotation);
		
		confidenceMultiplier = 1.0f / tracker.getConfidenceLevel();
		lastAngles[0] = 1000;
	}
	
	@Override
	public boolean getRotation(Quaternion store) {
		tracker.getRotation(store);
		if(smooth > 0) {
			store.toAngles(angles);
			if(Math.abs(angles[0] - lastAngles[0]) > smooth || Math.abs(angles[1] - lastAngles[1]) > smooth || Math.abs(angles[2] - lastAngles[2]) > smooth) {
				smoothedQuaternion.set(store);
				store.toAngles(lastAngles);
			} else {
				store.set(smoothedQuaternion);
			}
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
	
	@Override
	public float getConfidenceLevel() {
		return tracker.getConfidenceLevel() * confidenceMultiplier;
	}
}