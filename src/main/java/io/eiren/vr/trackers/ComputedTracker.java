package io.eiren.vr.trackers;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public class ComputedTracker implements Tracker {

	public final Vector3f position = new Vector3f();
	public final Quaternion rotation = new Quaternion();
	protected final String name;
	protected TrackerStatus status = TrackerStatus.DISCONNECTED;
	
	public ComputedTracker(String name) {
		this.name = name;
	}
	
	@Override
	public void saveConfig(TrackerConfig config) {
	}
	
	@Override
	public void loadConfig(TrackerConfig config) {
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public boolean getPosition(Vector3f store) {
		store.set(position);
		return true;
	}

	@Override
	public boolean getRotation(Quaternion store) {
		store.set(rotation);
		return true;
	}

	@Override
	public TrackerStatus getStatus() {
		return status;
	}
	
	public void setStatus(TrackerStatus status) {
		this.status = status;
	}
	
	@Override
	public float getConfidenceLevel() {
		return 1.0f;
	}
}
