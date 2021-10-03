package io.eiren.vr.trackers;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import io.eiren.math.FloatMath;
import io.eiren.util.BufferedTimer;
import io.eiren.vr.processor.TrackerBodyPosition;

public class IMUTracker implements Tracker, TrackerWithTPS, TrackerWithBattery {
	
	public static final float MAX_MAG_CORRECTION_ACCURACY = 5 * FastMath.RAD_TO_DEG;
	
	public final Vector3f gyroVector = new Vector3f();
	public final Vector3f accelVector = new Vector3f();
	public final Vector3f magVector = new Vector3f();
	public final Quaternion rotQuaternion = new Quaternion();
	public final Quaternion rotMagQuaternion = new Quaternion();
	protected final Quaternion rotAdjust = new Quaternion();
	protected final Quaternion correction = new Quaternion();
	protected TrackerMountingRotation mounting = null;
	protected TrackerStatus status = TrackerStatus.OK;
	
	protected final String name;
	protected final TrackersUDPServer server;
	protected float confidence = 0;
	protected float batteryVoltage = 0;
	public int calibrationStatus = 0;
	public int magCalibrationStatus = 0;
	public float magnetometerAccuracy = 0;
	protected boolean magentometerCalibrated = false;
	public boolean hasNewCorrectionData = false;
	
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
		// Loading a config is an act of user editing, therefore it shouldn't not be allowed if editing is not allowed
		if (userEditable()) {
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
	public void tick() {
		if(magentometerCalibrated && hasNewCorrectionData) {
			hasNewCorrectionData = false;
			if(magnetometerAccuracy <= MAX_MAG_CORRECTION_ACCURACY) {
				// Adjust gyro rotation to match magnetometer rotation only if magnetometer
				// accuracy is within the parameters
				calculateLiveMagnetometerCorrection();
			}
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
		//correction.mult(store, store); // Correction is not used now to preven accidental errors while debugging other things
		store.multLocal(rotAdjust);
		return true;
	}
	
	public void getCorrection(Quaternion store) {
		store.set(correction);
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
		resetYaw(reference);
	}

	/**
	 * Does not perform actual gyro reset to reference, that's the task of
	 * reference adjusted tracker. Only aligns gyro with magnetometer if
	 * it's reliable
	 */
	@Override
	public void resetYaw(Quaternion reference) {
		if(magCalibrationStatus >= CalibrationAccuracy.HIGH.status) {
			magentometerCalibrated = true;
			// During calibration set correction to match magnetometer readings exactly
			// TODO : Correct only yaw
			correction.set(rotQuaternion).inverseLocal().multLocal(rotMagQuaternion);
		}
	}
	
	/**
	 * Calculate correction between normal and magnetometer
	 * readings up to accuracy threshold
	 */
	protected void calculateLiveMagnetometerCorrection() {
		// TODO Magic, correct only yaw
		// TODO Print "jump" length when correcing if it's more than 1 degree
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

	@Override
	public boolean hasRotation() {
		return true;
	}

	@Override
	public boolean hasPosition() {
		return false;
	}

	@Override
	public boolean isComputed() {
		return false;
	}
}
