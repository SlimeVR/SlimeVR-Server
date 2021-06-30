package io.eiren.vr.trackers;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import io.eiren.math.FloatMath;
import io.eiren.util.BufferedTimer;
import io.eiren.vr.processor.TrackerBodyPosition;

public class IMUTracker implements Tracker, TrackerWithTPS, TrackerWithBattery {
	
	public final Vector3f gyroVector = new Vector3f();
	public final Vector3f accelVector = new Vector3f();
	public final Vector3f magVector = new Vector3f();
	public final Quaternion rotQuaternion = new Quaternion();
	protected final Quaternion rotAdjust = new Quaternion();
	protected TrackerStatus status = TrackerStatus.OK;
	
	protected final String name;
	protected final TrackersUDPServer server;
	protected float confidence = 0;
	protected float batteryVoltage = 0;
	
	protected BufferedTimer timer = new BufferedTimer(1f);
	public int ping = -1;
	
	public StringBuilder serialBuffer = new StringBuilder();
	long lastSerialUpdate = 0;
	public TrackerBodyPosition bodyPosition = null;
	
	public IMUTracker(String name, TrackersUDPServer server) {
		this.name = name;
		this.server = server;
	}
	
	@Override
	public void saveConfig(TrackerConfig config) {
		config.setDesignation(bodyPosition == null ? null : bodyPosition.designation);
	}
	
	@Override
	public void loadConfig(TrackerConfig config) {
		if(!FloatMath.equalsToZero(config.trackerRotation)) {
			rotAdjust.fromAngles(0, config.trackerRotation * FastMath.DEG_TO_RAD, 0);
		} else {
			rotAdjust.loadIdentity();
		}
		bodyPosition = TrackerBodyPosition.getByDesignation(config.designation);
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
		store.multLocal(rotAdjust);
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
	public float getTPS() {
		return timer.getAverageFPS();
	}
	
	@Override
	public void dataTick() {
		timer.update();
	}
	
	@Override
	public float getConfidenceLevel() {
		return confidence;
	}
	
	public void setConfidence(float newConf) {
		this.confidence = newConf;
	}
	
	@Override
	public float getBatteryLevel() {
		return FloatMath.mapValue(getBatteryVoltage(), 3.6f, 4.2f, 0f, 1f);
	}
	
	@Override
	public float getBatteryVoltage() {
		return batteryVoltage;
	}
	
	public void setBatteryVoltage(float voltage) {
		this.batteryVoltage = voltage;
	}

	@Override
	public void resetFull(Quaternion reference) {
	}

	@Override
	public void resetYaw(Quaternion reference) {
	}

	@Override
	public TrackerBodyPosition getBodyPosition() {
		return bodyPosition;
	}

	@Override
	public void setBodyPosition(TrackerBodyPosition position) {
		this.bodyPosition = position;
	}
}
