package io.eiren.vr.trackers;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;

public enum TrackerMountingRotation {
	
	FORWARD(180),
	LEFT(90),
	BACK(0),
	RIGHT(-90);
	
	public final float angle;
	public final Quaternion quaternion = new Quaternion();
	
	public static final TrackerMountingRotation[] values = values();
	
	private TrackerMountingRotation(float angle) {
		this.angle = angle;
		quaternion.fromAngles(0, angle * FastMath.DEG_TO_RAD, 0);
	}
}
