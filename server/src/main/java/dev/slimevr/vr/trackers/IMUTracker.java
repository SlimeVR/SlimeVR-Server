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
	private static final Quaternion LEFT_TPOSE_OFFSET = new Quaternion()
		.fromAngles(0, 0, FastMath.HALF_PI);
	private static final Quaternion RIGHT_TPOSE_OFFSET = new Quaternion()
		.fromAngles(0, 0, -FastMath.HALF_PI);

	// public final Vector3f gyroVector = new Vector3f();
	public final Vector3f accelVector = new Vector3f();
	// public final Vector3f magVector = new Vector3f();
	public final Quaternion rotQuaternion = new Quaternion();
	public final Quaternion rotMagQuaternion = new Quaternion();
	public final Quaternion mountAdjust = new Quaternion();
	public final UDPDevice device;
	public final int trackerNum;
	public final Vector3f rotVector = new Vector3f();

	// Reference adjustment quats
	private final Quaternion gyroFix = new Quaternion();
	private final Quaternion attachmentFix = new Quaternion();
	private final Quaternion yawMountRotFix = new Quaternion();
	private final Quaternion yawFix = new Quaternion();

	// Zero-reference adjustment quats for IMU debugging
	private final Quaternion gyroFixNoMounting = new Quaternion();
	private final Quaternion attachmentFixNoMounting = new Quaternion();
	private final Quaternion yawFixZeroReference = new Quaternion();

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
	protected boolean allowDriftCompensation = true;
	protected boolean compensateDrift = false;
	protected float driftAmount;
	protected static long DRIFT_COOLDOWN_MS = 30000;
	protected final Quaternion averagedDriftQuat = new Quaternion();
	private final FastList<Float> driftWeights = new FastList<>();
	private final static Quaternion rotationSinceReset = new Quaternion();
	private CircularArrayList<Quaternion> driftQuats;
	private CircularArrayList<Long> driftTimes;
	private long totalDriftTime;
	private long driftSince;
	private long timeAtLastReset;

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
			setDriftCompensationSettings(
				vrserver.getConfigManager().getVrConfig().getDriftCompensation().getEnabled(),
				vrserver.getConfigManager().getVrConfig().getDriftCompensation().getAmount(),
				vrserver.getConfigManager().getVrConfig().getDriftCompensation().getMaxResets()
			);
		}
	}

	@Override
	public void writeConfig(TrackerConfig config) {
		config.setDesignation(bodyPosition == null ? null : bodyPosition.designation);
		config
			.setMountingOrientation(
				mounting != null ? mounting : new Quaternion().fromAngles(0, FastMath.PI, 0)
			);
		config.setCustomName(customName);
		config.setAllowDriftCompensation(allowDriftCompensation);
	}

	@Override
	public void readConfig(TrackerConfig config) {
		// Loading a config is an act of user editing, therefore it shouldn't
		// be allowed if editing is not allowed
		if (userEditable()) {
			setCustomName(config.getCustomName());

			if (config.getMountingOrientation() != null) {
				mounting = config.getMountingOrientation();
				mountAdjust.set(config.getMountingOrientation());
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
			if (config.getAllowDriftCompensation() == null) {
				// If value didn't exist, default to true and save
				allowDriftCompensation = true;
				vrserver
					.getConfigManager()
					.getVrConfig()
					.getTracker(this.get())
					.setAllowDriftCompensation(true);
				vrserver.getConfigManager().saveConfig();
			} else {
				allowDriftCompensation = config.getAllowDriftCompensation();
			}
		}
	}

	public Quaternion getMountingOrientation() {
		return mounting;
	}

	public void setMountingOrientation(Quaternion mr) {
		mounting = mr;
		if (mounting != null) {
			mountAdjust.set(mounting);
		} else {
			mountAdjust.loadIdentity();
		}
	}

	public boolean getAllowDriftCompensation() {
		return allowDriftCompensation;
	}

	public void setAllowDriftCompensation(boolean allowDriftCompensation) {
		this.allowDriftCompensation = allowDriftCompensation;
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

	public void setDriftCompensationSettings(boolean enabled, float amount, int maxResets) {
		compensateDrift = enabled;
		driftAmount = amount;
		if (enabled) {
			if (driftQuats == null || maxResets != driftQuats.capacity()) {
				driftQuats = new CircularArrayList<>(maxResets);
				driftTimes = new CircularArrayList<>(maxResets);
			}
		} else {
			driftQuats = null;
			driftTimes = null;
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

	/**
	 * Calculates reference-adjusted rotation (with full/quick reset) including
	 * the mounting orientation (front, back, left, right) and mounting reset
	 * adjustment. Also taking drift compensation into account.
	 *
	 * @param store Where to store the calculation result.
	 */
	@Override
	public boolean getRotation(Quaternion store) {
		getFilteredRotation(store);
		// correction.mult(store, store); // Correction is not used now to
		// prevent accidental errors while debugging other things
		store.multLocal(mountAdjust);
		adjustToReference(store);
		if ((compensateDrift && allowDriftCompensation) && totalDriftTime > 0) {
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

	/**
	 * Calculates zero-reference-adjusted rotation (with full/quick reset). Same
	 * as {@link #getRotation(Quaternion)}, except rotation is aligned to an
	 * identity quaternion instead of HMD and does not include mounting reset
	 * and mounting orientation adjustments. Does not take drift compensation
	 * into account.
	 *
	 * This rotation can be used in visualizations for debugging purposes.
	 *
	 * @param store Where to store the calculation result.
	 */
	public boolean getIdentityAdjustedRotation(Quaternion store) {
		getFilteredRotation(store);
		adjustToIdentity(store);
		return true;
	}

	public boolean getFilteredRotation(Quaternion store) {
		if (movingAverage != null) {
			store.set(movingAverage.getFilteredQuaternion());
		} else {
			store.set(rotQuaternion);
		}
		return true;
	}

	@Override
	public boolean getRawRotation(Quaternion store) {
		store.set(rotQuaternion);
		return true;
	}

	public Quaternion getAdjustedRawRotation() {
		Quaternion rot = new Quaternion(rotQuaternion);
		// correction.mult(store, store); // Correction is not used now to
		// prevent accidental errors while debugging other things
		rot.multLocal(mountAdjust);
		adjustToReference(rot);
		return rot;
	}

	private Quaternion getMountedAdjustedRotation() {
		Quaternion rot = new Quaternion(rotQuaternion);
		// correction.mult(store, store); // Correction is not used now to
		// prevent accidental errors while debugging other things
		rot.multLocal(mountAdjust);
		return rot;
	}

	private Quaternion getMountedAdjustedDriftRotation() {
		Quaternion rot = new Quaternion(rotQuaternion);
		// correction.mult(store, store); // Correction is not used now to
		// prevent accidental errors while debugging other things
		rot.multLocal(mountAdjust);
		if ((compensateDrift && allowDriftCompensation) && totalDriftTime > 0) {
			rot
				.slerpLocal(
					rot.mult(averagedDriftQuat),
					driftAmount
						* ((float) (System.currentTimeMillis() - driftSince)
							/ totalDriftTime)
				);
		}
		return rot;
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
	 * Reset the tracker so that its current rotation is counted as (0, HMD Yaw,
	 * 0). This allows the tracker to be strapped to body at any pitch and roll.
	 */
	@Override
	public void resetFull(Quaternion reference, boolean tPose) {
		Quaternion rot = getAdjustedRawRotation();
		fixGyroscope(getMountedAdjustedRotation(), tPose);
		fixAttachment(getMountedAdjustedRotation(), tPose);
		makeIdentityAdjustmentQuatsFull();
		fixYaw(getMountedAdjustedRotation(), reference);
		makeIdentityAdjustmentQuatsYaw();
		calibrateMag();
		calculateDrift(rot);
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
		Quaternion rot = getAdjustedRawRotation();
		fixYaw(getMountedAdjustedRotation(), reference);
		makeIdentityAdjustmentQuatsYaw();
		calibrateMag();
		calculateDrift(rot);
	}

	/**
	 * Converts raw or filtered rotation into reference- and
	 * mounting-reset-adjusted by applying quaternions produced after
	 * {@link #resetFull(Quaternion, boolean)}, {@link #resetYaw(Quaternion)}
	 * and {@link #resetMounting(boolean, boolean)}.
	 *
	 * @param store Raw or filtered rotation to mutate.
	 */
	protected void adjustToReference(Quaternion store) {
		gyroFix.mult(store, store);
		store.multLocal(attachmentFix);
		store.multLocal(yawMountRotFix);
		yawFix.mult(store, store);
	}

	/**
	 * Converts raw or filtered rotation into zero-reference-adjusted by
	 * applying quaternions produced after
	 * {@link #resetFull(Quaternion, boolean)}, {@link #resetYaw(Quaternion)}.
	 *
	 * @param store Raw or filtered rotation to mutate.
	 */
	protected void adjustToIdentity(Quaternion store) {
		gyroFixNoMounting.mult(store, store);
		store.multLocal(attachmentFixNoMounting);
		yawFixZeroReference.mult(store, store);
	}

	private void fixGyroscope(Quaternion sensorRotation, boolean tPose) {
		sensorRotation = sensorRotation.clone();
		if (tPose)
			fixForTPose(sensorRotation);
		sensorRotation.fromAngles(0, sensorRotation.getYaw(), 0);
		gyroFix.set(sensorRotation.inverseLocal());
	}

	private void fixAttachment(Quaternion sensorRotation, boolean tPose) {
		sensorRotation = sensorRotation.clone();
		gyroFix.mult(sensorRotation, sensorRotation);
		if (tPose)
			fixForTPose(sensorRotation);
		attachmentFix.set(sensorRotation.inverseLocal());
	}

	@Override
	public void resetMounting(boolean reverseYaw, boolean tPose) {
		// Get the current calibrated rotation
		Quaternion buffer = getMountedAdjustedDriftRotation();
		gyroFix.mult(buffer, buffer);
		buffer.multLocal(attachmentFix);
		float yawAngle;

		if (tPose && (isOnLeftArm() || isOnRightArm())) {
			// Find the global axis the tracker thinks it rotated about (should
			// be z), then projected on to the xz plane
			rotVector
				.set(
					((isOnLeftArm() ? LEFT_TPOSE_OFFSET : RIGHT_TPOSE_OFFSET)
						.mult(buffer)).toAxis()
				);
			yawAngle = FastMath
				.atan2(
					rotVector.cross(Vector3f.NEGATIVE_UNIT_Z).dot(Vector3f.UNIT_Y),
					rotVector.dot(Vector3f.NEGATIVE_UNIT_Z)
				);
			// TODO find out what what is causing arms to not work (yawFix?)
		} else {
			// Find the global axis the tracker thinks it rotated about (should
			// be z), then projected on to the xz plane
			rotVector.set(buffer.inverseLocal().toAxis());
			yawAngle = new Quaternion(0f, 0f, 0f, 1f)
				.align(rotVector, Vector3f.UNIT_X)
				.normalizeLocal()
				.getYaw();
		}

		// Make an adjustment quaternion from the angle
		buffer.fromAngles(0f, reverseYaw ? yawAngle : yawAngle - FastMath.PI, 0f);

		Quaternion lastRotAdjust = yawMountRotFix.clone();
		yawMountRotFix.set(buffer);

		// Get the difference from the last adjustment
		buffer.multLocal(lastRotAdjust.inverseLocal());
		// Apply the yaw rotation difference to the yaw fix quaternion
		yawFix.multLocal(buffer.inverseLocal());
	}

	private void fixYaw(Quaternion sensorRotation, Quaternion reference) {
		// Use only yaw HMD rotation
		reference = reference.clone();
		reference.fromAngles(0, reference.getYaw(), 0);

		sensorRotation = sensorRotation.clone();
		gyroFix.mult(sensorRotation, sensorRotation);
		sensorRotation.multLocal(attachmentFix);
		sensorRotation.multLocal(yawMountRotFix);

		sensorRotation.fromAngles(0, sensorRotation.getYaw(), 0);

		yawFix.set(sensorRotation.inverseLocal().multLocal(reference));
	}

	private void calibrateMag() {
		if (magCalibrationStatus >= CalibrationAccuracy.HIGH.status) {
			magnetometerCalibrated = true;
			// During calibration set correction to match magnetometer readings
			// TODO : Correct only yaw
			correction.set(rotQuaternion).inverseLocal().multLocal(rotMagQuaternion);
		}
	}

	/**
	 * Calculates drift since last reset and store the data related to it in
	 * driftQuat, timeAtLastReset and timeForLastReset
	 */
	private void calculateDrift(Quaternion beforeQuat) {
		if (compensateDrift && allowDriftCompensation) {
			Quaternion rotQuat = getAdjustedRawRotation();

			if (
				driftSince > 0
					&& System.currentTimeMillis() - timeAtLastReset > DRIFT_COOLDOWN_MS
			) {
				// Check and remove from lists to keep them under the reset
				// limit
				if (driftQuats.size() == driftQuats.capacity()) {
					driftQuats.removeLast();
					driftTimes.removeLast();
				}

				// Add new drift quaternion
				driftQuats
					.add(
						rotQuat
							.fromAngles(0, rotQuat.getYaw(), 0)
							.mult(
								beforeQuat.fromAngles(0, beforeQuat.getYaw(), 0).inverse()
							)
					);

				// Add drift time to total
				driftTimes.add(System.currentTimeMillis() - driftSince);
				totalDriftTime = 0;
				for (Long time : driftTimes) {
					totalDriftTime += time;
				}

				// Calculate drift Quaternions' weights
				driftWeights.clear();
				for (Long time : driftTimes) {
					driftWeights.add(((float) time) / ((float) totalDriftTime));
				}
				// Make it so recent Quaternions weigh more
				for (int i = driftWeights.size() - 1; i > 0; i--) {
					// Add some of i-1's value to i
					driftWeights
						.set(
							i,
							driftWeights.get(i) + (driftWeights.get(i - 1) / driftWeights.size())
						);
					// Remove the value that was added to i from i-1
					driftWeights
						.set(
							i - 1,
							driftWeights.get(i - 1)
								- (driftWeights.get(i - 1) / driftWeights.size())
						);
				}

				// Set final averaged drift Quaternion
				averagedDriftQuat.fromAveragedQuaternions(driftQuats, driftWeights);

				// Save tracker rotation and current time
				rotationSinceReset.set(driftQuats.getLatest());
				timeAtLastReset = System.currentTimeMillis();
			} else if (
				System.currentTimeMillis() - timeAtLastReset < DRIFT_COOLDOWN_MS
					&& driftQuats.size() > 0
			) {
				// Replace latest drift quaternion
				rotationSinceReset
					.multLocal(
						rotQuat
							.fromAngles(0, rotQuat.getYaw(), 0)
							.mult(
								beforeQuat.fromAngles(0, beforeQuat.getYaw(), 0).inverse()
							)
					);
				driftQuats
					.set(
						driftQuats.size() - 1,
						rotationSinceReset
					);

				// Add drift time to total
				driftTimes
					.set(
						driftTimes.size() - 1,
						driftTimes.getLatest() + System.currentTimeMillis() - driftSince
					);
				totalDriftTime = 0;
				for (Long time : driftTimes) {
					totalDriftTime += time;
				}

				// Calculate drift Quaternions' weights
				driftWeights.clear();
				for (Long time : driftTimes) {
					driftWeights.add(((float) time) / ((float) totalDriftTime));
				}
				// Make it so recent Quaternions weigh more
				for (int i = driftWeights.size() - 1; i > 0; i--) {
					driftWeights
						.set(
							i,
							driftWeights.get(i) + (driftWeights.get(i - 1) / driftWeights.size())
						);
					driftWeights
						.set(
							i - 1,
							driftWeights.get(i - 1)
								- (driftWeights.get(i - 1) / driftWeights.size())
						);
				}

				// Set final averaged drift Quaternion
				averagedDriftQuat.fromAveragedQuaternions(driftQuats, driftWeights);
			} else {
				timeAtLastReset = System.currentTimeMillis();
			}

			driftSince = System.currentTimeMillis();
		}
	}

	private void makeIdentityAdjustmentQuatsFull() {
		Quaternion sensorRotation = new Quaternion();
		getRawRotation(sensorRotation);
		sensorRotation.fromAngles(0, sensorRotation.getYaw(), 0);
		gyroFixNoMounting.set(sensorRotation).inverseLocal();
		getRawRotation(sensorRotation);
		gyroFixNoMounting.mult(sensorRotation, sensorRotation);
		attachmentFixNoMounting.set(sensorRotation).inverseLocal();
	}

	private void makeIdentityAdjustmentQuatsYaw() {
		Quaternion sensorRotation = new Quaternion();
		getRawRotation(sensorRotation);
		gyroFixNoMounting.mult(sensorRotation, sensorRotation);
		sensorRotation.multLocal(attachmentFixNoMounting);
		sensorRotation.fromAngles(0, sensorRotation.getYaw(), 0);
		yawFixZeroReference.set(sensorRotation).inverseLocal();
	}

	/**
	 * Calculate correction between normal and magnetometer readings up to
	 * accuracy threshold
	 */
	protected void calculateLiveMagnetometerCorrection() {
		// TODO Magic, correct only yaw
		// TODO Print "jump" length when correcting if it's more than 1 degree
	}

	private void fixForTPose(Quaternion store) {
		if (isOnLeftArm()) {
			store.set(LEFT_TPOSE_OFFSET.mult(store));
		} else if (isOnRightArm()) {
			store.set(RIGHT_TPOSE_OFFSET.mult(store));
		}
	}

	private boolean isOnLeftArm() {
		return bodyPosition == TrackerPosition.LEFT_UPPER_ARM
			|| bodyPosition == TrackerPosition.LEFT_LOWER_ARM
			|| bodyPosition == TrackerPosition.LEFT_HAND;
	}

	private boolean isOnRightArm() {
		return bodyPosition == TrackerPosition.RIGHT_UPPER_ARM
			|| bodyPosition == TrackerPosition.RIGHT_LOWER_ARM
			|| bodyPosition == TrackerPosition.RIGHT_HAND;
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
	public boolean hasAcceleration() {
		return true;
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
