package dev.slimevr.vr.trackers;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.slimevr.VRServer;
import dev.slimevr.vr.trackers.udp.TrackersUDPServer;
import dev.slimevr.vr.trackers.udp.UDPDevice;
import io.eiren.util.BufferedTimer;


public class IMUTracker implements Tracker, TrackerWithTPS, TrackerWithBattery {

	public static final float MAX_MAG_CORRECTION_ACCURACY = 5 * FastMath.RAD_TO_DEG;

	// public final Vector3f gyroVector = new Vector3f();
	// public final Vector3f accelVector = new Vector3f();
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
	private final Quaternion buffQuat = new Quaternion();
	public int movementFilterTickCount = 0;
	public float movementFilterAmount = 1f;
	public int calibrationStatus = 0;
	public int magCalibrationStatus = 0;
	public float magnetometerAccuracy = 0;
	public boolean hasNewCorrectionData = false;
	public int ping = -1;
	public int signalStrength = -1;
	public float temperature = 0;
	public TrackerPosition bodyPosition = null;
	protected CircularArrayList<Quaternion> previousRots;
	protected Quaternion mounting = null;
	protected TrackerStatus status = TrackerStatus.OK;
	protected float confidence = 0;
	protected float batteryVoltage = 0;
	protected float batteryLevel = 0;
	protected boolean magentometerCalibrated = false;
	protected BufferedTimer timer = new BufferedTimer(1f);

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
	}

	@Override
	public void saveConfig(TrackerConfig config) {
		config.setDesignation(bodyPosition == null ? null : bodyPosition.designation);
		config.mountingRotation = mounting != null ? mounting : null;
	}

	@Override
	public void loadConfig(TrackerConfig config) {
		// Loading a config is an act of user editing, therefore it shouldn't
		// not be
		// allowed if editing is not allowed
		if (userEditable()) {
			if (config.mountingRotation != null) {
				mounting = config.mountingRotation;
				rotAdjust.set(config.mountingRotation);
			} else {
				rotAdjust.loadIdentity();
			}
			TrackerPosition
				.getByDesignation(config.designation)
				.ifPresent(trackerPosition -> bodyPosition = trackerPosition);
			setFilter(
				vrserver.config.getString("filters.type"),
				vrserver.config.getFloat("filters.amount", 0.3f),
				vrserver.config.getInt("filters.tickCount", 1)
			);
		}
	}

	public void setFilter(String type, float amount, int ticks) {
		amount = FastMath.clamp(amount, 0, 1f);
		ticks = (int) FastMath.clamp(ticks, 0, 50);
		if (type != null) {
			switch (type) {
				case "INTERPOLATION":
					movementFilterAmount = 1f - (amount / 1.6f);
					movementFilterTickCount = ticks;
					break;
				case "EXTRAPOLATION":
					movementFilterAmount = 1f + (amount * 1.15f);
					movementFilterTickCount = ticks;
					break;
				case "NONE":
				default:
					movementFilterAmount = 1f;
					movementFilterTickCount = 0;
					break;
			}
		} else {
			movementFilterAmount = 1f;
			movementFilterTickCount = 0;
		}
		previousRots = new CircularArrayList<Quaternion>(movementFilterTickCount + 1);
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
	public void tick() {
		if (magentometerCalibrated && hasNewCorrectionData) {
			hasNewCorrectionData = false;
			if (magnetometerAccuracy <= MAX_MAG_CORRECTION_ACCURACY) {
				// Adjust gyro rotation to match magnetometer rotation only if
				// magnetometer
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
		if (movementFilterTickCount > 0 && movementFilterAmount != 1 && previousRots.size() > 0) {
			buffQuat.set(previousRots.get(0));
			buffQuat.slerpLocal(rotQuaternion, movementFilterAmount);
			store.set(buffQuat);
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

		if (movementFilterTickCount != 0) {
			if (previousRots.size() > movementFilterTickCount) {
				previousRots.remove(0);
			}
			previousRots.add(rotQuaternion.clone());
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
			magentometerCalibrated = true;
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
	public UDPDevice getDevice() {
		return this.device;
	}

	@Override
	public String getDescriptiveName() {
		return this.descriptiveName;
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
