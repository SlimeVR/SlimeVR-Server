package dev.slimevr.vr.trackers;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.slimevr.VRServer;
import dev.slimevr.config.TrackerConfig;
import dev.slimevr.filtering.QuaternionMovingAverage;
import dev.slimevr.filtering.TrackerFilters;
import dev.slimevr.vr.Device;
import dev.slimevr.vr.trackers.udp.TrackersUDPServer;
import dev.slimevr.vr.trackers.udp.UDPDevice;
import io.eiren.util.BufferedTimer;

import java.util.Optional;


public class IMUTracker
	implements Tracker, TrackerWithTPS, TrackerWithBattery, TrackerWithWireless,
	TrackerWithFiltering {

	public static final float MAX_MAG_CORRECTION_ACCURACY = 5 * FastMath.RAD_TO_DEG;

	// public final Vector3f gyroVector = new Vector3f();
	public final Vector3f accelVector = new Vector3f();
	public final Vector3f magVector = new Vector3f();
	public final Quaternion rotQuaternion = new Quaternion();
	public final Quaternion rotMagQuaternion = new Quaternion();
	public final Quaternion rotAdjust = new Quaternion();
	public final UDPDevice device;
	public final int trackerNum;
	protected final Quaternion correction = new Quaternion();
	protected final int trackerId;
	protected final String name;
	protected final String descriptiveName;
	protected final TrackersUDPServer server;
	protected final VRServer vrserver;
	public int calibrationStatus = 0;
	public int magCalibrationStatus = 0;
	public float magnetometerAccuracy = 0;
	private String customName;
	public boolean hasNewCorrectionData = false;
	private int ping = -1;
	private int signalStrength = -1;
	public float temperature = 0;
	public TrackerPosition bodyPosition = null;
	protected Quaternion mounting = null;
	protected TrackerStatus status = TrackerStatus.OK;
	protected float confidence = 0;
	protected float batteryVoltage = 0;
	protected float batteryLevel = 0;
	protected boolean magnetometerCalibrated = false;
	protected BufferedTimer timer = new BufferedTimer(1f);
	protected QuaternionMovingAverage movingAverage;

	public IMUTracker(
		UDPDevice device,
		int trackerId,
		int trackerNum,
		String name,
		String descriptiveName,
		TrackersUDPServer server,
		VRServer vrserver
	) {
		this.device = device;
		this.trackerNum = trackerNum;
		this.name = name;
		this.server = server;
		this.trackerId = trackerId;
		this.descriptiveName = descriptiveName;
		this.vrserver = vrserver;

		setFiltering(
			vrserver.getConfigManager().getVrConfig().getFilters().enumGetType(),
			vrserver.getConfigManager().getVrConfig().getFilters().getAmount()
		);
	}

	@Override
	public void writeConfig(TrackerConfig config) {
		config.setDesignation(bodyPosition == null ? null : bodyPosition.designation);
		config.setMountingRotation(mounting != null ? mounting : null);
		config.setCustomName(customName);
	}

	@Override
	public void readConfig(TrackerConfig config) {
		// Loading a config is an act of user editing, therefore it shouldn't
		// not be
		// allowed if editing is not allowed
		if (userEditable()) {
			setCustomName(config.getCustomName());

			if (config.getMountingRotation() != null) {
				mounting = config.getMountingRotation();
				rotAdjust.set(config.getMountingRotation());
			} else {
				rotAdjust.loadIdentity();
			}
			Optional<TrackerPosition> trackerPosition = TrackerPosition
				.getByDesignation(config.getDesignation());
			if (trackerPosition.isEmpty()) {
				bodyPosition = null;
			} else {
				bodyPosition = trackerPosition.get();
			}
		}
	}

	public Quaternion getMountingRotation() {
		return mounting;
	}

	public void setMountingRotation(Quaternion mr) {
		mounting = mr;
		if (mounting != null) {
			rotAdjust.set(mounting);
		} else {
			rotAdjust.loadIdentity();
		}
	}

	@Override
	public void setFiltering(TrackerFilters type, float amount) {
		if (type != null) {
			switch (type) {
				case SMOOTHING:
				case PREDICTION:
					movingAverage = new QuaternionMovingAverage(
						type,
						amount,
						rotQuaternion
					);
					break;
				case NONE:
				default:
					movingAverage = null;
					break;
			}
		} else {
			movingAverage = null;
		}
	}

	@Override
	public void tick() {
		if (magnetometerCalibrated && hasNewCorrectionData) {
			hasNewCorrectionData = false;
			if (magnetometerAccuracy <= MAX_MAG_CORRECTION_ACCURACY) {
				// Adjust gyro rotation to match magnetometer rotation only if
				// magnetometer
				// accuracy is within the parameters
				calculateLiveMagnetometerCorrection();
			}
		}

		// Update moving average (that way movement is smooth even if TPS is
		// stuttering)
		if (movingAverage != null) {
			movingAverage.update();
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
	public boolean getAcceleration(Vector3f store) {
		store.set(accelVector);
		return true;
	}

	@Override
	public boolean getRotation(Quaternion store) {
		if (movingAverage != null) {
			store.set(movingAverage.getFilteredQuaternion());
		} else {
			store.set(rotQuaternion);
		}
		// correction.mult(store, store); // Correction is not used now to
		// prevent
		// accidental errors while debugging other things
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

		// Add new rotation to moving average
		if (movingAverage != null) {
			movingAverage.addQuaternion(rotQuaternion.clone());
		}
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
		return batteryLevel;
	}

	public void setBatteryLevel(float level) {
		this.batteryLevel = level;
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
	 * reference adjusted tracker. Only aligns gyro with magnetometer if it's
	 * reliable
	 */
	@Override
	public void resetYaw(Quaternion reference) {
		if (magCalibrationStatus >= CalibrationAccuracy.HIGH.status) {
			magnetometerCalibrated = true;
			// During calibration set correction to match magnetometer readings
			// exactly
			// TODO : Correct only yaw
			correction.set(rotQuaternion).inverseLocal().multLocal(rotMagQuaternion);
		}
	}

	/**
	 * Calculate correction between normal and magnetometer readings up to
	 * accuracy threshold
	 */
	protected void calculateLiveMagnetometerCorrection() {
		// TODO Magic, correct only yaw
		// TODO Print "jump" length when correcting if it's more than 1 degree
	}

	@Override
	public TrackerPosition getBodyPosition() {
		return bodyPosition;
	}

	@Override
	public void setBodyPosition(TrackerPosition position) {
		this.bodyPosition = position;
	}

	@Override
	public boolean userEditable() {
		return true;
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

	@Override
	public int getTrackerId() {
		return this.trackerId;
	}

	@Override
	public int getTrackerNum() {
		return this.trackerNum;
	}

	@Override
	public Device getDevice() {
		return this.device;
	}

	@Override
	public String getDisplayName() {
		return "IMU Tracker #" + getTrackerId();
	}

	@Override
	public String getCustomName() {
		return customName;
	}

	public void setCustomName(String customName) {
		this.customName = customName;
	}

	@Override
	public Tracker get() {
		return this;
	}

	@Override
	public int getPing() {
		return this.ping;
	}

	@Override
	public int getSignalStrength() {
		return this.signalStrength;
	}

	public void setPing(int ping) {
		this.ping = ping;
	}

	public void setSignalStrength(int signalStrength) {
		this.signalStrength = signalStrength;
	}

	public enum CalibrationAccuracy {

		UNRELIABLE(0), LOW(1), MEDIUM(2), HIGH(3),;

		private static final CalibrationAccuracy[] byStatus = new CalibrationAccuracy[4];

		static {
			for (CalibrationAccuracy ca : values())
				byStatus[ca.status] = ca;
		}

		public final int status;

		CalibrationAccuracy(int status) {
			this.status = status;
		}

		public static CalibrationAccuracy getByStatus(int status) {
			if (status < 0 || status > 3)
				return null;
			return byStatus[status];
		}
	}
}
