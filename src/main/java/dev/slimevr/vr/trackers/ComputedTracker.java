package dev.slimevr.vr.trackers;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.slimevr.vr.trackers.udp.UDPDevice;


public class ComputedTracker implements Tracker, TrackerWithTPS {

	public final Vector3f position = new Vector3f();
	public final Quaternion rotation = new Quaternion();
	protected final String name;
	protected final String serial;
	protected final boolean hasRotation;
	protected final boolean hasPosition;
	protected final int trackerId;
	public TrackerPosition bodyPosition = null;
	protected TrackerStatus status = TrackerStatus.DISCONNECTED;

	public ComputedTracker(
		int trackerId,
		String serial,
		String name,
		boolean hasRotation,
		boolean hasPosition
	) {
		this.name = name;
		this.serial = serial;
		this.hasRotation = hasRotation;
		this.hasPosition = hasPosition;
		this.trackerId = trackerId;
	}

	public ComputedTracker(int trackerId, String name, boolean hasRotation, boolean hasPosition) {
		this(trackerId, name, name, hasRotation, hasPosition);
	}

	@Override
	public void saveConfig(TrackerConfig config) {
		config.setDesignation(bodyPosition == null ? null : bodyPosition.designation);
	}

	@Override
	public void loadConfig(TrackerConfig config) {
		// Loading a config is an act of user editing, therefore it shouldn't
		// not be
		// allowed if editing is not allowed
		if (userEditable()) {
			TrackerPosition
				.getByDesignation(config.designation)
				.ifPresent(trackerPosition -> bodyPosition = trackerPosition);
		}
	}

	@Override
	public String getName() {
		return this.serial;
	}

	@Override
	public String getDescriptiveName() {
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
	public void dataTick() {
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
	public float getTPS() {
		return -1;
	}

	@Override
	public int getTrackerId() {
		return this.trackerId;
	}

	@Override
	public int getTrackerNum() {
		return this.getTrackerId();
	}

	@Override
	public UDPDevice getDevice() {
		return null;
	}
}
