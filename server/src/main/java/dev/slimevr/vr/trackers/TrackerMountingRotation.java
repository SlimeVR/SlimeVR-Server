package dev.slimevr.vr.trackers;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;


public enum TrackerMountingRotation {

	FRONT(180), LEFT(90), BACK(0), RIGHT(-90);

	public static final TrackerMountingRotation[] values = values();
	public final Quaternion quaternion;

	TrackerMountingRotation(float angle) {
		this.quaternion = new Quaternion().fromAngles(0, angle * FastMath.DEG_TO_RAD, 0);
	}

	public static TrackerMountingRotation fromName(String name) {
		for (TrackerMountingRotation r : values) {
			if (r.name().equals(name))
				return r;
		}
		return null;
	}

	public static TrackerMountingRotation fromQuaternion(Quaternion q) {
		for (TrackerMountingRotation r : values) {
			if (r.quaternion.equals(q))
				return r;
		}
		return null;
	}
}
