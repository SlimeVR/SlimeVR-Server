package io.eiren.vr.trackers;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public class IMUTracker implements Tracker {
	
	public final Vector3f gyroVector = new Vector3f();
	public final Vector3f accelVector = new Vector3f();
	public final Vector3f magVector = new Vector3f();
	public final Quaternion rotQuaternion = new Quaternion();
	protected TrackerStatus status = TrackerStatus.OK;
	
	protected final String name;
	protected final TrackersUDPServer server;
	
	public IMUTracker(String name, TrackersUDPServer server) {
		this.name = name;
		this.server = server;
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public boolean getPosition(Vector3f store) {
		store.set(0, 0, 0);
		return false;
	}
	
	@Override
	public boolean getRotation(Quaternion store) {
		store.set(rotQuaternion);
		return true;
	}

	@Override
	public TrackerStatus getStatus() {
		return status;
	}
	
	public void setStatus(TrackerStatus status) {
		this.status = status;
	}
	
	public void startCalibration() {
		
	}
}
