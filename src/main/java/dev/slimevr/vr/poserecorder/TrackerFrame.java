package dev.slimevr.vr.poserecorder;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import io.eiren.vr.processor.TrackerBodyPosition;
import io.eiren.vr.trackers.Tracker;

public final class TrackerFrame {

	private int dataFlags = 0;

	public final TrackerBodyPosition designation;
	public final Quaternion rotation;
	public final Vector3f position;

	public TrackerFrame(TrackerBodyPosition designation, Quaternion rotation, Vector3f position) {
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

	public TrackerFrame(TrackerBodyPosition designation, Quaternion rotation) {
		this(designation, rotation, null);
	}

	public TrackerFrame(TrackerBodyPosition designation, Vector3f position) {
		this(designation, null, position);
	}

	public static TrackerFrame fromTracker(Tracker tracker) {
		// If null or has no data
		if (tracker == null || (!tracker.hasRotation() && !tracker.hasPosition())) {
			return null;
		}

		Quaternion rotation = null;
		if (tracker.hasRotation()) {
			rotation = new Quaternion();
			tracker.getRotation(rotation);
		}

		Vector3f position = null;
		if (tracker.hasPosition()) {
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
}
