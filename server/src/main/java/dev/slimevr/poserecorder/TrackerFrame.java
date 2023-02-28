package dev.slimevr.poserecorder;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.slimevr.VRServer;
import dev.slimevr.config.TrackerConfig;
import dev.slimevr.tracking.trackers.Device;
import dev.slimevr.tracking.trackers.TrackerJava;
import dev.slimevr.tracking.trackers.TrackerPosition;
import dev.slimevr.tracking.trackers.TrackerStatus;


public final class TrackerFrame implements TrackerJava {

	public final TrackerPosition designation;
	public final Quaternion rotation;
	public final Vector3f position;
	public final Vector3f acceleration;
	public final Quaternion rawRotation;
	private final int trackerId = VRServer.getNextLocalTrackerId();
	private int dataFlags = 0;

	public TrackerFrame(
		TrackerPosition designation,
		Quaternion rotation,
		Vector3f position,
		Vector3f acceleration,
		Quaternion rawRotation
	) {
		this.designation = designation;
		if (designation != null) {
			dataFlags = TrackerFrameData.DESIGNATION_ENUM.add(dataFlags);
		}

		this.rotation = rotation;
		if (rotation != null) {
			dataFlags = TrackerFrameData.ROTATION.add(dataFlags);
		}

		this.position = position;
		if (position != null) {
			dataFlags = TrackerFrameData.POSITION.add(dataFlags);
		}

		this.acceleration = acceleration;
		if (acceleration != null) {
			dataFlags = TrackerFrameData.ACCELERATION.add(dataFlags);
		}

		this.rawRotation = rawRotation;
		if (rawRotation != null) {
			dataFlags = TrackerFrameData.RAW_ROTATION.add(dataFlags);
		}
	}

	public static TrackerFrame fromTracker(TrackerJava tracker) {
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

		TrackerPosition designation = tracker.getBodyPosition();

		Quaternion rotation = null;
		if (tracker.hasRotation()) {
			rotation = new Quaternion();
			if (!tracker.getRotation(rotation)) {
				// If the get failed, set it back to null
				rotation = null;
			}
		}

		Vector3f position = null;
		if (tracker.hasPosition()) {
			position = new Vector3f();
			if (!tracker.getPosition(position)) {
				// If the get failed, set it back to null
				position = null;
			}
		}

		Vector3f acceleration = null;
		if (tracker.hasAcceleration()) {
			acceleration = new Vector3f();
			if (!tracker.getAcceleration(acceleration)) {
				// If the get failed, set it back to null
				acceleration = null;
			}
		}

		// TODO: Why is there no `hasRawRotation`? Update this to check that
		// first when it exists
		Quaternion rawRotation = new Quaternion();
		if (!(tracker.getRawRotation(rawRotation) && !rawRotation.equals(rotation))) {
			// If the get failed or the rawRotation is the same as rotation, set
			// it back to null
			rawRotation = null;
		}

		// If tracker has no data at all, there's no point in writing a frame
		if (
			designation == null
				&& rotation == null
				&& position == null
				&& acceleration == null
				&& rawRotation == null
		) {
			return null;
		}

		return new TrackerFrame(
			designation,
			rotation,
			position,
			acceleration,
			rawRotation
		);
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
	public boolean getRawRotation(Quaternion store) {
		if (hasData(TrackerFrameData.RAW_ROTATION)) {
			store.set(rawRotation);
			return true;
		}

		return getRotation(store);
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
	public boolean getAcceleration(Vector3f store) {
		if (hasData(TrackerFrameData.ACCELERATION)) {
			store.set(acceleration);
			return true;
		}

		store.set(Vector3f.ZERO);
		return false;
	}

	@Override
	public String getName() {
		return "TrackerFrame:/" + (designation != null ? designation.getDesignation() : "null");
	}

	@Override
	public TrackerStatus getStatus() {
		return TrackerStatus.OK;
	}

	@Override
	public void readConfig(TrackerConfig config) {
		throw new UnsupportedOperationException("TrackerFrame does not implement configuration");
	}

	@Override
	public void writeConfig(TrackerConfig config) {
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
	public void resetMounting(boolean reverseYaw) {
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
	public boolean hasAcceleration() {
		return false;
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
	public Device getDevice() {
		return null;
	}

	@Override
	public TrackerJava get() {
		return this;
	}

	@Override
	public String getDisplayName() {
		return getName();
	}

	@Override
	public String getCustomName() {
		return null;
	}

	@Override
	public void setCustomName(String customName) {
	}
}
