package io.eiren.vr.sensors;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public class RotationSensor {
	public final Vector3f gyroVector = new Vector3f();
	public final Vector3f accelVector = new Vector3f();
	public final Vector3f magVector = new Vector3f();
	public final Quaternion rotQuaternion = new Quaternion();
}
