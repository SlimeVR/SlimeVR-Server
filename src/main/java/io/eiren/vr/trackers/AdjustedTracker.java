package io.eiren.vr.trackers;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public class AdjustedTracker implements Tracker {
	
	public final Tracker tracker;
	private final Quaternion smoothedQuaternion = new Quaternion();
	public final Quaternion adjustmentYaw = new Quaternion();
	public final Quaternion adjustmentAttachment = new Quaternion();
	protected float[] lastAngles = new float[3];
	public float smooth = 0 * FastMath.DEG_TO_RAD;
	private final float[] angles = new float[3];
	private float pitchCorrection = 0;
	private float rollCorrection = 0;
	
	protected float confidenceMultiplier = 1.0f;
	
	public AdjustedTracker(Tracker tracker) {
		this.tracker = tracker;
	}

	@Override
	public void loadConfig(TrackerConfig config) {
	}

	@Override
	public void saveConfig(TrackerConfig config) {
	}

	public void adjustFull(Quaternion reference) {
		adjustYaw(reference);
		
		Quaternion sensorRotation = new Quaternion();
		tracker.getRotation(sensorRotation);
		adjustmentYaw.mult(sensorRotation, sensorRotation);
		
		adjustmentAttachment.set(sensorRotation).inverseLocal();
	}
	
	public void adjustYaw(Quaternion reference) {
		Quaternion targetTrackerRotation = new Quaternion(reference);

		// Use only yaw HMD rotation
		float[] angles = new float[3];
		targetTrackerRotation.toAngles(angles);
		targetTrackerRotation.fromAngles(0, angles[1], 0);
		
		Quaternion sensorRotation = new Quaternion();
		tracker.getRotation(sensorRotation);
		
		sensorRotation.toAngles(angles);
		sensorRotation.fromAngles(0, angles[1], 0);
		
		adjustmentYaw.set(sensorRotation).inverseLocal().multLocal(targetTrackerRotation);
		
		confidenceMultiplier = 1.0f / tracker.getConfidenceLevel();
		lastAngles[0] = 1000;
	}
	
	protected void adjustInternal(Quaternion store) {
		store.multLocal(adjustmentAttachment);
		adjustmentYaw.mult(store, store);
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
		adjustInternal(store);
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