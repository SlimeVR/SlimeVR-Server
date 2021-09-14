package dev.slimevr.vr.poserecorder;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import io.eiren.vr.processor.TrackerBodyPosition;
import io.eiren.vr.trackers.Tracker;
import io.eiren.vr.trackers.TrackerConfig;
import io.eiren.vr.trackers.TrackerStatus;

public final class TrackerFrame implements Tracker {

	private int dataFlags = 0;

	public final String name;
	public final TrackerBodyPosition designation;
	public final Quaternion rotation;
	public final Vector3f position;

	public TrackerFrame(String designationString, TrackerBodyPosition designation, Quaternion rotation, Vector3f position) {
		if (designationString != null) {
			this.name = "TrackerFrame:/" + designationString;
		} else {
			this.name = "TrackerFrame";
		}

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

	public TrackerFrame(TrackerBodyPosition designation, Quaternion rotation, Vector3f position) {
		this(designation == null ? null : designation.designation, designation, rotation, position);
	}

	public TrackerFrame(TrackerBodyPosition designation, Quaternion rotation) {
		this(designation, rotation, null);
	}

	public TrackerFrame(TrackerBodyPosition designation, Vector3f position) {
		this(designation, null, position);
	}

	public static TrackerFrame fromTracker(Tracker tracker) {
		if (tracker == null) {
			return null;
		}

		// If the tracker is not ready
		if (tracker.getStatus() != TrackerStatus.OK && tracker.getStatus() != TrackerStatus.BUSY && tracker.getStatus() != TrackerStatus.OCCLUDED) {
			return null;
		}

		boolean hasPosition = tracker.hasPosition() && tracker.getStatus() != TrackerStatus.OCCLUDED;
		// If tracker has no data
		if (tracker.getBodyPosition() == null && !tracker.hasRotation() && !hasPosition) {
			return null;
		}

		Quaternion rotation = null;
		if (tracker.hasRotation()) {
			rotation = new Quaternion();
			tracker.getRotation(rotation);
		}

		// If the tracker is occluded, don't get the position
		Vector3f position = null;
		if (hasPosition) {
			position = new Vector3f();
			tracker.getPosition(position);
		}

		return new TrackerFrame(tracker.getBodyPosition(), rotation, position);
	}

	public int getDataFlags() {
		return dataFlags;
	}

	public boolean hasData(TrackerFrameData flag) {
		return flag.check(dataFlags);
	}

	//#region Tracker Interface Implementation
	@Override
	public boolean getRotation(Quaternion store) {
		if (hasData(TrackerFrameData.ROTATION)) {
			store.set(rotation);
			return true;
		}

		store.set(0, 0, 0, 1);
		return false;
	}

	@Override
	public boolean getPosition(Vector3f store) {
		if (hasData(TrackerFrameData.POSITION)) {
			store.set(position);
			return true;
		}

		store.set(0, 0, 0);
		return false;
	}

	@Override
	public String getName() {
		return name;
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
		return 0;
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
	public TrackerBodyPosition getBodyPosition() {
		return designation;
	}

	@Override
	public void setBodyPosition(TrackerBodyPosition position) {
		throw new UnsupportedOperationException("TrackerFrame does not allow setting the body position");
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
	//#endregion
}
