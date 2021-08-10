package io.eiren.vr.trackers;

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
	public final Quaternion rotMagQuaternion = new Quaternion();
	protected final Quaternion rotAdjust = new Quaternion();
	protected TrackerMountingRotation mounting = null;
	protected TrackerStatus status = TrackerStatus.OK;
	
	protected final String name;
	protected final TrackersUDPServer server;
	protected float confidence = 0;
	protected float batteryVoltage = 0;
	public int calibrationStatus = 0;
	public int magCalibrationStatus = 0;
	public float magnetometerAccuracy = 0;
	
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
		config.mountingRotation = mounting != null ? mounting.name() : null;
	}
	
	@Override
	public void loadConfig(TrackerConfig config) {
		if(config.mountingRotation != null) {
			mounting = TrackerMountingRotation.valueOf(config.mountingRotation);
			if(mounting != null) {
				rotAdjust.set(mounting.quaternion);
			} else {
				rotAdjust.loadIdentity();
			}
		} else {
			rotAdjust.loadIdentity();
		}
		bodyPosition = TrackerBodyPosition.getByDesignation(config.designation);
	}
	
	public TrackerMountingRotation getMountingRotation() {
		return mounting;
	}
	
	public void setMountingRotation(TrackerMountingRotation mr) {
		mounting = mr;
		if(mounting != null) {
			rotAdjust.set(mounting.quaternion);
		} else {
			rotAdjust.loadIdentity();
		}
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

	@Override
	public boolean userEditable() {
		return true;
	}
	
	public enum CalibrationAccuracy {
		
		UNRELIABLE(0),
		LOW(1),
		MEDIUM(2),
		HIGH(3),
		;
		
		private static final CalibrationAccuracy[] byStatus = new CalibrationAccuracy[4];
		public final int status;
		
		private CalibrationAccuracy(int status) {
			this.status = status;
		}
		
		public static CalibrationAccuracy getByStatus(int status) {
			if(status < 0 || status > 3)
				return null;
			return byStatus[status];
		}
		
		static {
			for(CalibrationAccuracy ca : values())
				byStatus[ca.status] = ca;
		}
	}
}
