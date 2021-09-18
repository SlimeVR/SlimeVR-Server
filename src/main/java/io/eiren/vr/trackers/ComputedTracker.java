package io.eiren.vr.trackers;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import io.eiren.vr.processor.TrackerBodyPosition;

public class ComputedTracker implements Tracker {

	public final Vector3f position = new Vector3f();
	public final Quaternion rotation = new Quaternion();
	protected final String name;
	protected TrackerStatus status = TrackerStatus.DISCONNECTED;
	public TrackerBodyPosition bodyPosition = null;
	protected final boolean hasRotation;
	protected final boolean hasPosition;
	
	public ComputedTracker(String name, boolean hasRotation, boolean hasPosition) {
		this.name = name;
		this.hasRotation = hasRotation;
		this.hasPosition = hasPosition;
	}
	
	@Override
	public void saveConfig(TrackerConfig config) {
		config.setDesignation(bodyPosition == null ? null : bodyPosition.designation);
	}
	
	@Override
	public void loadConfig(TrackerConfig config) {
		// Loading a config is an act of user editing, therefore it shouldn't not be allowed if editing is not allowed
		if (userEditable()) {
			bodyPosition = TrackerBodyPosition.getByDesignation(config.designation);
		}
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

	@Override
	public void resetFull(Quaternion reference) {
	}

	@Override
	public void resetYaw(Quaternion reference) {
	}

	@Override
	public TrackerBodyPosition getBodyPosition() {
		return bodyPosition;
	}

	@Override
	public void setBodyPosition(TrackerBodyPosition position) {
		this.bodyPosition = position;
	}

	@Override
	public boolean userEditable() {
		return false;
	}

	@Override
	public void tick() {
	}

	@Override
	public boolean hasRotation() {
		return hasRotation;
	}

	@Override
	public boolean hasPosition() {
		return hasPosition;
	}

	@Override
	public boolean isComputed() {
		return true;
	}
}
