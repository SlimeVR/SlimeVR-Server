package dev.slimevr.poserecorder;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.slimevr.vr.trackers.Tracker;
import dev.slimevr.vr.trackers.TrackerConfig;
import dev.slimevr.vr.trackers.TrackerPosition;
import dev.slimevr.vr.trackers.TrackerStatus;
import dev.slimevr.vr.trackers.udp.UDPDevice;


public final class TrackerFrame implements Tracker {

	public final TrackerPosition designation;
	public final Quaternion rotation;
	public final Vector3f position;
	private final int trackerId = Tracker.getNextLocalTrackerId();
	private int dataFlags = 0;

	public TrackerFrame(TrackerPosition designation, Quaternion rotation, Vector3f position) {
		this.designation = designation;
		if (designation != null) {
			dataFlags |= TrackerFrameData.DESIGNATION.flag;
		}

		this.rotation = rotation;
		if (rotation != null) {
			dataFlags |= TrackerFrameData.ROTATION.flag;
		}

		this.position = position;
		if (position != null) {
			dataFlags |= TrackerFrameData.POSITION.flag;
		}
	}

	public static TrackerFrame fromTracker(Tracker tracker) {
		if (tracker == null) {
			return null;
		}

		// If the tracker is not ready
		if (
			tracker.getStatus() != TrackerStatus.OK
				&& tracker.getStatus() != TrackerStatus.BUSY
				&& tracker.getStatus() != TrackerStatus.OCCLUDED
		) {
			return null;
		}

		// If tracker has no data
		if (tracker.getBodyPosition() == null && !tracker.hasRotation() && !tracker.hasPosition()) {
			return null;
		}

		Quaternion rotation = null;
		if (tracker.hasRotation()) {
			rotation = new Quaternion();
			if (!tracker.getRotation(rotation)) {
				// If getting the rotation failed, set it back to null
				rotation = null;
			}
		}

		Vector3f position = null;
		if (tracker.hasPosition()) {
			position = new Vector3f();
			if (!tracker.getPosition(position)) {
				// If getting the position failed, set it back to null
				position = null;
			}
		}

		return new TrackerFrame(tracker.getBodyPosition(), rotation, position);
	}

	public int getDataFlags() {
		return dataFlags;
	}

	public boolean hasData(TrackerFrameData flag) {
		return flag.check(dataFlags);
	}

	// #region Tracker Interface Implementation
	@Override
	public boolean getRotation(Quaternion store) {
		if (hasData(TrackerFrameData.ROTATION)) {
			store.set(rotation);
			return true;
		}

		store.set(Quaternion.IDENTITY);
		return false;
	}

	@Override
	public boolean getPosition(Vector3f store) {
		if (hasData(TrackerFrameData.POSITION)) {
			store.set(position);
			return true;
		}

		store.set(Vector3f.ZERO);
		return false;
	}

	@Override
	public String getName() {
		return "TrackerFrame:/" + (designation != null ? designation.designation : "null");
	}

	@Override
	public TrackerStatus getStatus() {
		return TrackerStatus.OK;
	}

	@Override
	public void loadConfig(TrackerConfig config) {
		throw new UnsupportedOperationException("TrackerFrame does not implement configuration");
	}

	@Override
	public void saveConfig(TrackerConfig config) {
		throw new UnsupportedOperationException("TrackerFrame does not implement configuration");
	}

	@Override
	public float getConfidenceLevel() {
		return 1f;
	}

	@Override
	public void resetFull(Quaternion reference) {
		throw new UnsupportedOperationException("TrackerFrame does not implement calibration");
	}

	@Override
	public void resetYaw(Quaternion reference) {
		throw new UnsupportedOperationException("TrackerFrame does not implement calibration");
	}

	@Override
	public void tick() {
		throw new UnsupportedOperationException("TrackerFrame does not implement this method");
	}

	@Override
	public TrackerPosition getBodyPosition() {
		return designation;
	}

	@Override
	public void setBodyPosition(TrackerPosition position) {
		throw new UnsupportedOperationException(
			"TrackerFrame does not allow setting the body position"
		);
	}

	@Override
	public boolean userEditable() {
		return false;
	}

	@Override
	public boolean hasRotation() {
		return hasData(TrackerFrameData.ROTATION);
	}

	@Override
	public boolean hasPosition() {
		return hasData(TrackerFrameData.POSITION);
	}

	@Override
	public boolean isComputed() {
		return true;
	}
	// #endregion

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
