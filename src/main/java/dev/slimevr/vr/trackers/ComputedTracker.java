package dev.slimevr.vr.trackers;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;


public class ComputedTracker implements Tracker {

	public final Vector3f position = new Vector3f();
	public final Quaternion rotation = new Quaternion();
	protected final String name;
	protected final boolean hasRotation;
	protected final boolean hasPosition;
	protected final int trackerId;
	public TrackerPosition bodyPosition = null;
	protected TrackerStatus status = TrackerStatus.DISCONNECTED;

	public ComputedTracker(
		int trackerId,
		String name,
		boolean hasRotation,
		boolean hasPosition
	) {
		this.name = name;
		this.hasRotation = hasRotation;
		this.hasPosition = hasPosition;
		this.trackerId = trackerId;
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
	public TrackerStatus getStatus() {
		return status;
	}

	public void setStatus(TrackerStatus status) {
		this.status = status;
	}

	@Override
	public TrackerPosition getBodyPosition() {
		return bodyPosition;
	}

	@Override
	public void setBodyPosition(TrackerPosition position) {
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

	@Override
	public int getTrackerId() {
		return this.trackerId;
	}

	@Override
	public int getTrackerNum() {
		return this.getTrackerId();
	}
}
