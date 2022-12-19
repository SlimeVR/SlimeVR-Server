package dev.slimevr.vr.trackers;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.slimevr.VRServer;
import dev.slimevr.config.TrackerConfig;
import dev.slimevr.filtering.CircularArrayList;
import dev.slimevr.filtering.QuaternionMovingAverage;
import dev.slimevr.filtering.TrackerFilters;
import dev.slimevr.vr.Device;
import dev.slimevr.vr.trackers.udp.TrackersUDPServer;
import dev.slimevr.vr.trackers.udp.UDPDevice;
import io.eiren.util.BufferedTimer;
import io.eiren.util.collections.FastList;

import java.util.Optional;


public class IMUTracker
	implements Tracker, TrackerWithTPS, TrackerWithBattery, TrackerWithWireless,
	TrackerWithFiltering {

	public static final float MAX_MAG_CORRECTION_ACCURACY = 5 * FastMath.RAD_TO_DEG;

	// public final Vector3f gyroVector = new Vector3f();
	public final Vector3f accelVector = new Vector3f();
	// public final Vector3f magVector = new Vector3f();
	public final Quaternion rotQuaternion = new Quaternion();
	public final Quaternion rotMagQuaternion = new Quaternion();
	public final Quaternion mountAdjust = new Quaternion();
	public final UDPDevice device;
	public final int trackerNum;
	public final Vector3f rotVector = new Vector3f();
	public final Quaternion gyroFix = new Quaternion();
	public final Quaternion attachmentFix = new Quaternion();
	public final Quaternion mountRotFix = new Quaternion();
	public final Quaternion yawFix = new Quaternion();
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
	protected boolean compensateDrift = false;
	protected float driftAmount;
	protected static long DRIFT_COOLDOWN_MS = 15000;
	protected final Quaternion averagedDriftQuat = new Quaternion();
	protected CircularArrayList<Quaternion> driftQuats;
	protected CircularArrayList<Long> driftTimes;
	protected FastList<Float> driftWeights = new FastList<>();
	protected long totalDriftTime;
	protected long driftSince;
	protected long timeAtLastReset;

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

		if (vrserver != null) {
			setFiltering(
				vrserver.getConfigManager().getVrConfig().getFilters().enumGetType(),
				vrserver.getConfigManager().getVrConfig().getFilters().getAmount()
			);
			setDriftSettings(
				vrserver.getConfigManager().getVrConfig().getDrift().getEnabled(),
				vrserver.getConfigManager().getVrConfig().getDrift().getAmount(),
				vrserver.getConfigManager().getVrConfig().getDrift().getMaxResets()
			);
		}
	}

	@Override
	public void writeConfig(TrackerConfig config) {
		config.setDesignation(bodyPosition == null ? null : bodyPosition.designation);
		config
			.setMountingRotation(
				mounting != null ? mounting : new Quaternion().fromAngles(0, FastMath.PI, 0)
			);
		config.setCustomName(customName);
	}

	@Override
	public void readConfig(TrackerConfig config) {
		// Loading a config is an act of user editing, therefore it shouldn't
		// be allowed if editing is not allowed
		if (userEditable()) {
			setCustomName(config.getCustomName());

			if (config.getMountingRotation() != null) {
				mounting = config.getMountingRotation();
				mountAdjust.set(config.getMountingRotation());
			} else {
				mountAdjust.loadIdentity();
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
			mountAdjust.set(mounting);
		} else {
			mountAdjust.loadIdentity();
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

	public void setDriftSettings(boolean enabled, float amount, int maxResets) {
		compensateDrift = enabled;
		driftAmount = amount;
		driftQuats = new CircularArrayList<>(maxResets);
		driftTimes = new CircularArrayList<>(maxResets);
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
		// prevent accidental errors while debugging other things
		store.multLocal(mountAdjust);
		adjustInternal(store);
		if (compensateDrift && totalDriftTime > 0) {
			store
				.slerpLocal(
					store.mult(averagedDriftQuat),
					driftAmount
						* ((float) (System.currentTimeMillis() - driftSince)
							/ totalDriftTime)
				);
		}
		return true;
	}

	@Override
	public boolean getRawRotation(Quaternion store) {
		store.set(rotQuaternion);
		return true;
	}

	public boolean getUnfilteredRotation(Quaternion store) {
		store.set(rotQuaternion);
		// correction.mult(store, store); // Correction is not used now to
		// prevent accidental errors while debugging other things
		store.multLocal(mountAdjust);
		adjustInternal(store);
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

	/**
	 * Reset the tracker so that its current rotation is counted as (0, <HMD
	 * Yaw>, 0). This allows the tracker to be strapped to body at any pitch and
	 * roll.
	 * <p>
	 * Performs {@link #resetYaw(Quaternion)} for yaw drift correction.
	 */
	@Override
	public void resetFull(Quaternion reference) {
		fixGyroscope(getMountedAdjustedRotation());
		fixAttachment(getMountedAdjustedRotation());

		resetYaw(reference);
	}

	/**
	 * Reset the tracker so that it's current yaw rotation is counted as <HMD
	 * Yaw>. This allows the tracker to have yaw independent of the HMD. Tracker
	 * should still report yaw as if it was mounted facing HMD, mounting
	 * position should be corrected in the source. Also aligns gyro magnetometer
	 * if it's reliable.
	 */
	@Override
	public void resetYaw(Quaternion reference) {
		// Get rotation before fixing yaw
		Quaternion beforeReset = new Quaternion();
		getUnfilteredRotation(beforeReset);

		fixYaw(reference);

		// Get rotation after fixing yaw
		Quaternion afterReset = new Quaternion();
		getUnfilteredRotation(afterReset);
		// Calculate amount of drift
		calculateDrift(beforeReset, afterReset);

		if (magCalibrationStatus >= CalibrationAccuracy.HIGH.status) {
			magnetometerCalibrated = true;
			// During calibration set correction to match magnetometer readings
			// TODO : Correct only yaw
			correction.set(rotQuaternion).inverseLocal().multLocal(rotMagQuaternion);
		}
	}

	protected void adjustInternal(Quaternion store) {
		gyroFix.mult(store, store);
		store.multLocal(attachmentFix);
		store.multLocal(mountRotFix);
		yawFix.mult(store, store);
	}

	private Quaternion getMountedAdjustedRotation() {
		return rotQuaternion.mult(mountAdjust);
	}

	private void fixGyroscope(Quaternion sensorRotation) {
		sensorRotation.fromAngles(0, sensorRotation.getYaw(), 0);
		gyroFix.set(sensorRotation).inverseLocal();
	}

	private void fixAttachment(Quaternion sensorRotation) {
		gyroFix.mult(sensorRotation, sensorRotation);
		attachmentFix.set(sensorRotation).inverseLocal();
	}

	@Override
	public void resetMounting(boolean reverseYaw) {
		// Get the current calibrated rotation
		Quaternion buffer = getMountedAdjustedRotation();
		gyroFix.mult(buffer, buffer);
		buffer.multLocal(attachmentFix);

		// Reset the vector for the rotation to point straight up
		rotVector.set(0f, 1f, 0f);
		// Rotate the vector by the quat, then flatten and normalize the vector
		buffer.multLocal(rotVector).setY(0f).normalizeLocal();

		// Calculate the yaw angle using tan
		// Just use an angle offset of zero for unsolvable circumstances
		float yawAngle = FastMath.isApproxZero(rotVector.x) && FastMath.isApproxZero(rotVector.z)
			? 0f
			: FastMath.atan2(rotVector.x, rotVector.z);

		// Make an adjustment quaternion from the angle
		buffer.fromAngles(0f, reverseYaw ? yawAngle : yawAngle - FastMath.PI, 0f);

		Quaternion lastRotAdjust = mountRotFix.clone();
		mountRotFix.set(buffer);

		// Get the difference from the last adjustment
		buffer.multLocal(lastRotAdjust.inverseLocal());
		// Apply the yaw rotation difference to the yaw fix quaternion
		yawFix.multLocal(buffer.inverseLocal());
	}

	private void fixYaw(Quaternion reference) {
		// Use only yaw HMD rotation
		Quaternion targetRotation = reference.clone();
		targetRotation.fromAngles(0, targetRotation.getYaw(), 0);

		Quaternion sensorRotation = getMountedAdjustedRotation();
		gyroFix.mult(sensorRotation, sensorRotation);
		sensorRotation.multLocal(attachmentFix);
		sensorRotation.multLocal(mountRotFix);

		sensorRotation.fromAngles(0, sensorRotation.getYaw(), 0);

		yawFix.set(sensorRotation).inverseLocal().multLocal(targetRotation);
	}

	/**
	 * Calculates 1 since last reset and store the data related to it in
	 * driftQuat, timeAtLastReset and timeForLastReset
	 */
	synchronized public void calculateDrift(Quaternion beforeResetQuat, Quaternion afterResetQuat) {
		// TODO add way to ignore repeated resets and just use most recent
		// within a time window.

		if (driftSince > 0 && System.currentTimeMillis() - timeAtLastReset > DRIFT_COOLDOWN_MS) {
			// Check and remove from lists to keep them under the reset limit
			if (driftQuats.size() == driftQuats.capacity()) {
				driftQuats.removeLast();
				driftTimes.removeLast();
			}

			// Add new drift quaternion
			driftQuats
				.add(
					new Quaternion()
						.fromAngles(0f, beforeResetQuat.mult(afterResetQuat.inverse()).getYaw(), 0f)
						.inverseLocal()
				);

			// Set how much time it has been since last drift reset
			long driftTime;
			if (timeAtLastReset > 0)
				driftTime = System.currentTimeMillis() - timeAtLastReset;
			else
				driftTime = System.currentTimeMillis() - driftSince;

			// Add to total drift time
			driftTimes.add(driftTime);
			totalDriftTime = 0;
			for (Long time : driftTimes) {
				totalDriftTime += time;
			}

			// Calculate drift Quaternions' weights
			driftWeights.clear();
			for (Long time : driftTimes) {
				driftWeights.add(((float) time) / ((float) totalDriftTime));
			}

			// Set final averaged drift Quaternion
			averagedDriftQuat.fromAveragedQuaternions(driftQuats, driftWeights);

			timeAtLastReset = System.currentTimeMillis();
		}

		driftSince = System.currentTimeMillis();
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
