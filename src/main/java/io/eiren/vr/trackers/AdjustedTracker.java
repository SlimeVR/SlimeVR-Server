package io.eiren.vr.trackers;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public abstract class AdjustedTracker implements Tracker {
	
	public final Tracker tracker;
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
	}

	@Override
	public void saveConfig(TrackerConfig config) {
	}
	
	public abstract void adjust(Quaternion reference);
	
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
	
	protected abstract void adjustInternal(Quaternion store);

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