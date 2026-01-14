package dev.slimevr.tracking.trackers

import dev.slimevr.VRServer
import dev.slimevr.config.TrackerConfig
import dev.slimevr.tracking.processor.stayaligned.trackers.StayAlignedTrackerState
import dev.slimevr.tracking.trackers.TrackerPosition.Companion.getByDesignation
import dev.slimevr.tracking.trackers.udp.IMUType
import dev.slimevr.tracking.trackers.udp.MagnetometerStatus
import dev.slimevr.tracking.trackers.udp.TrackerDataType
import dev.slimevr.util.InterpolationHandler
import io.eiren.util.BufferedTimer
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlin.properties.Delegates

const val TIMEOUT_MS = 2_000L
const val DISCONNECT_MS = 3_000L + TIMEOUT_MS

/**
 * Generic tracker class for input and output tracker,
 * with flags on instantiation.
 */
class Tracker @JvmOverloads constructor(
	val device: Device?,
	/**
	 * VRServer.nextLocalTrackerId
	 */
	val id: Int,
	/**
	 * unique, for config
	 */
	val name: String,
	/**
	 * default display GUI name
	 */
	val displayName: String = "Tracker #$id",
	trackerPosition: TrackerPosition?,
	/**
	 * It's like the ID, but it should be local to the device if it has one
	 */
	trackerNum: Int? = null,
	val hasPosition: Boolean = false,
	val hasRotation: Boolean = false,
	val hasAcceleration: Boolean = false,
	/**
	 * User can change TrackerPosition, mounting...
	 */
	val userEditable: Boolean = false,
	/**
	 * Is used within SlimeVR (shareable trackers)
	 */
	val isInternal: Boolean = false,
	/**
	 * Has solved position + rotation (Vive trackers)
	 */
	val isComputed: Boolean = false,
	val imuType: IMUType? = null,
	/**
	 * Automatically set the status to DISCONNECTED
	 */
	val usesTimeout: Boolean = false,
	/**
	 * If true, smoothing and prediction may be enabled. If either are enabled, then
	 * rotations will be updated with [tick]. This will not have any effect if
	 * [trackRotDirection] is set to false.
	 */
	val allowFiltering: Boolean = false,

	/**
	 * If true, the tracker can be reset
	 */
	val allowReset: Boolean = false,
	/**
	 * If true, the tracker can do mounting calibration
	 */
	val allowMounting: Boolean = false,

	val isHmd: Boolean = false,

	/**
	 * If true, the tracker need the user to perform a reset
	 */
	var needReset: Boolean = false,

	/**
	 * Whether to track the direction of the tracker's rotation
	 * (positive vs negative rotation). This needs to be disabled for AutoBone and
	 * unit tests, where the rotation is absolute and not temporal.
	 *
	 * If true, the output rotation will only be updated after [dataTick]. If false, the
	 * output rotation will be updated immediately with the raw rotation.
	 */
	val trackRotDirection: Boolean = true,
	magStatus: MagnetometerStatus = MagnetometerStatus.NOT_SUPPORTED,
	/**
	 * Rotation by default.
	 * NOT the same as hasRotation (other data types emulate rotation)
	 */
	val trackerDataType: TrackerDataType = TrackerDataType.ROTATION,
) {
	private val timer = BufferedTimer(1f)
	private var timeAtLastUpdate: Long = System.currentTimeMillis()
	private var _rotation = Quaternion.IDENTITY

	// IMU: +z forward, +x left, +y up
	// SlimeVR: +z backward, +x right, +y up
	private var _acceleration = Vector3.NULL
	private var _magVector = Vector3.NULL
	var position = Vector3.NULL
	val resetsHandler: TrackerResetsHandler = TrackerResetsHandler(this)
	val filteringHandler: TrackerFilteringHandler = TrackerFilteringHandler()
	val trackerFlexHandler: TrackerFlexHandler = TrackerFlexHandler(this)
	var batteryVoltage: Float? = null
	var batteryLevel: Float? = null
	var batteryRemainingRuntime: Long? = null
	var ping: Int? = null
	var signalStrength: Int? = null
	var temperature: Float? = null
	var button: Int? = null
	var packetsReceived: Int? = null
	var packetsLost: Int? = null
	var packetLoss: Float? = null
	var customName: String? = null
	var magStatus: MagnetometerStatus = magStatus
		private set

	/**
	 * Watch the rest calibration status
	 */
	var hasCompletedRestCalibration: Boolean? = null

	/**
	 * If the tracker has gotten disconnected after it was initialized first time
	 */
	var status: TrackerStatus by Delegates.observable(TrackerStatus.DISCONNECTED) { _, old, new ->
		if (old == new) return@observable

		if (allowReset && !old.reset && new.reset && !needReset) {
			needReset = true
		}

		if (!isInternal && VRServer.instanceInitialized) {
			// If the status of a non-internal tracker has changed, inform
			// the VRServer to recreate the skeleton, as it may need to
			// assign or un-assign the tracker to a body part
			VRServer.instance.updateSkeletonModel()
			VRServer.instance.refreshTrackersDriftCompensationEnabled()
			VRServer.instance.trackerStatusChanged(this, old, new)
		}
	}

	var trackerPosition: TrackerPosition? by Delegates.observable(trackerPosition) { _, old, new ->
		if (old == new) return@observable

		if (allowReset && !needReset) {
			needReset = true
		}

		if (!isInternal) {
			// Set default mounting orientation for that body part
			new?.let { resetsHandler.mountingOrientation = it.defaultMounting() }
		}
	}

	// Computed value to simplify availability checks
	val hasAdjustedRotation = hasRotation && (allowFiltering || allowReset)

	/**
	 * It's like the ID, but it should be local to the device if it has one
	 */
	val trackerNum: Int = trackerNum ?: id

	val stayAligned = StayAlignedTrackerState(this)
	val yawResetSmoothing = InterpolationHandler()

	init {
		// IMPORTANT: Look here for the required states of inputs
		require(!allowReset || (hasRotation && allowReset)) {
			"If ${::allowReset.name} is true, then ${::hasRotation.name} must also be true"
		}
		require(!allowMounting || (allowReset && allowMounting)) {
			"If ${::allowMounting.name} is true, then ${::allowReset.name} must also be true"
		}
		require(!isHmd || (hasPosition && isHmd)) {
			"If ${::isHmd.name} is true, then ${::hasPosition.name} must also be true"
		}
// 		require(device != null && _trackerNum == null) {
// 			"If ${::device.name} exists, then ${::trackerNum.name} must not be null"
// 		}
	}

	/**
	 * Reads/loads from the given config
	 */
	fun readConfig(config: TrackerConfig) {
		config.customName?.let {
			customName = it
		}
		config.designation?.let { designation ->
			getByDesignation(designation)?.let { trackerPosition = it }
		} ?: run { trackerPosition = null }
		if (allowMounting) {
			// Load manual mounting
			config.mountingOrientation?.let { resetsHandler.mountingOrientation = it.toValue() }
		}
		if (this.isImu() && config.allowDriftCompensation == null) {
			// If value didn't exist, default to true and save
			resetsHandler.allowDriftCompensation = true
			VRServer.instance.configManager.vrConfig.getTracker(this).allowDriftCompensation = true
			VRServer.instance.configManager.saveConfig()
		} else {
			config.allowDriftCompensation?.let {
				resetsHandler.allowDriftCompensation = it
			}
		}
	}

	/**
	 * Writes/saves to the given config
	 */
	fun writeConfig(config: TrackerConfig) {
		trackerPosition?.let { config.designation = it.designation } ?: run { config.designation = null }
		customName?.let { config.customName = it }
		if (allowMounting) {
			// Save manual mounting
			config.mountingOrientation = resetsHandler.mountingOrientation.toObject()
		}
		if (this.isImu()) {
			config.allowDriftCompensation = resetsHandler.allowDriftCompensation
		}
	}

	/**
	 * Loads the mounting reset quaternion from disk
	 */
	fun saveMountingResetOrientation(config: TrackerConfig) {
		// Load automatic mounting
		config.mountingResetOrientation?.let {
			resetsHandler.trySetMountingReset(it.toValue())
		}
	}

	/**
	 * Saves the mounting reset quaternion to disk
	 */
	fun saveMountingResetOrientation(quat: Quaternion?) {
		val configManager = VRServer.instance.configManager
		configManager.vrConfig.getTracker(this).mountingResetOrientation = quat?.toObject()
		configManager.saveConfig()
	}

	/**
	 * Synchronized with the VRServer's 1000hz while loop
	 */
	fun tick(deltaTime: Float) {
		if (usesTimeout) {
			if (System.currentTimeMillis() - timeAtLastUpdate > DISCONNECT_MS) {
				status = TrackerStatus.DISCONNECTED
			} else if (System.currentTimeMillis() - timeAtLastUpdate > TIMEOUT_MS) {
				status = TrackerStatus.TIMED_OUT
			}
		}

		filteringHandler.update()
		yawResetSmoothing.tick(deltaTime)
		stayAligned.update()
	}

	/**
	 * Tells the tracker that it received new data
	 * NOTE: Use only when rotation is received
	 */
	fun dataTick() {
		timer.update()
		timeAtLastUpdate = System.currentTimeMillis()
		if (trackRotDirection) {
			filteringHandler.dataTick(getAdjustedRotation())
		}
	}

	/**
	 * A way to delay the timeout of the tracker
	 */
	fun heartbeat() {
		timeAtLastUpdate = System.currentTimeMillis()
	}

	/**
	 * Gets the adjusted tracker rotation after the resetsHandler's corrections
	 * (reset, mounting and drift compensation).
	 * This is the rotation that is applied on the SlimeVR skeleton bones.
	 * Warning: This performs several Quaternion multiplications, so calling
	 * it too much should be avoided for performance reasons.
	 */
	private fun getAdjustedRotation(): Quaternion {
		var rot = _rotation

		if (!stayAligned.hideCorrection) {
			// Yaw drift happens in the raw rotation space
			rot = Quaternion.rotationAroundYAxis(stayAligned.yawCorrection.toRad()) * rot
		}

		// Reset if needed and is not computed and internal
		return if (allowReset && !(isComputed && isInternal) && trackerDataType == TrackerDataType.ROTATION) {
			// Adjust to reset, mounting and drift compensation
			resetsHandler.getReferenceAdjustedDriftRotationFrom(rot)
		} else {
			rot
		}
	}

	/**
	 * Same as getAdjustedRotation except that Stay Aligned correction is always
	 * applied. This allows Stay Aligned to do gradient descent with the tracker's
	 * rotation.
	 */
	fun getAdjustedRotationForceStayAligned(): Quaternion {
		var rot = _rotation

		// Yaw drift happens in the raw rotation space
		rot = Quaternion.rotationAroundYAxis(stayAligned.yawCorrection.toRad()) * rot

		// Reset if needed and is not computed and internal
		return if (allowReset && !(isComputed && isInternal) && trackerDataType == TrackerDataType.ROTATION) {
			// Adjust to reset, mounting and drift compensation
			resetsHandler.getReferenceAdjustedDriftRotationFrom(rot)
		} else {
			rot
		}
	}

	/**
	 * Gets the identity-adjusted tracker rotation after the resetsHandler's corrections
	 * (identity reset, drift and identity mounting).
	 * This is used for debugging/visualizing tracker data
	 */
	fun getIdentityAdjustedRotation(): Quaternion {
		var rot = _rotation

		if (!stayAligned.hideCorrection) {
			// Yaw drift happens in the raw rotation space
			rot = Quaternion.rotationAroundYAxis(stayAligned.yawCorrection.toRad()) * rot
		}

		// Reset if needed or is a computed tracker besides head
		return if (allowReset && !(isComputed && trackerPosition != TrackerPosition.HEAD) && trackerDataType == TrackerDataType.ROTATION) {
			// Adjust to reset and mounting
			resetsHandler.getIdentityAdjustedDriftRotationFrom(rot)
		} else {
			rot
		}
	}

	/**
	 * Get the rotation of the tracker after the resetsHandler's corrections, filtering,
	 * and reset smoothing if applicable
	 */
	fun getRotation(): Quaternion {
		var rot = getRotationNoResetSmooth()

		if (yawResetSmoothing.remainingTime > 0f) {
			rot = yawResetSmoothing.curRotation * rot
		}

		return rot
	}

	/**
	 * Get the rotation of the tracker after the resetsHandler's corrections and
	 * filtering if applicable
	 */
	fun getRotationNoResetSmooth(): Quaternion = if (trackRotDirection) {
		filteringHandler.getFilteredRotation()
	} else {
		// Get non-filtered rotation
		getAdjustedRotation()
	}

	/**
	 * Gets the world-adjusted acceleration
	 */
	fun getAcceleration(): Vector3 = if (allowReset) {
		resetsHandler.getReferenceAdjustedAccel(_rotation, _acceleration)
	} else {
		_acceleration
	}

	/**
	 * Gets the raw (unadjusted) rotation of the tracker.
	 * If this is an IMU, this will be the raw sensor rotation.
	 */
	fun getRawRotation() = _rotation

	/**
	 * Sets the raw (unadjusted) rotation of the tracker.
	 */
	fun setRotation(rotation: Quaternion) {
		this._rotation = rotation
	}

	/**
	 * Sets the raw (unadjusted) acceleration of the tracker.
	 */
	fun setAcceleration(vec: Vector3) {
		this._acceleration = vec
	}

	/**
	 * True if the raw rotation is coming directly from an IMU (no cameras or lighthouses)
	 * For example, flex sensor trackers are not considered as IMU trackers (see TrackerDataType)
	 */
	fun isImu(): Boolean = imuType != null && trackerDataType == TrackerDataType.ROTATION

	/**
	 * Please don't use this and instead set it via [Device.setMag]
	 */
	internal fun setMagPrivate(mag: Boolean) {
		magStatus = if (mag) {
			MagnetometerStatus.ENABLED
		} else {
			MagnetometerStatus.DISABLED
		}
	}

	/**
	 * Gets the magnetic field vector, in mGauss.
	 */
	fun getMagVector() = if (allowReset) {
		resetsHandler.getReferenceAdjustedAccel(_rotation, _magVector)
	} else {
		_magVector
	}

	/**
	 * Sets the magnetic field vector.
	 */
	fun setMagVector(vec: Vector3) {
		this._magVector = vec
	}

	/**
	 * Gets the current TPS of the tracker
	 */
	val tps: Float
		get() = timer.averageFPS

	/**
	 * Call when doing a full reset to reset the tracking of rotations >180 degrees
	 */
	fun resetFilteringQuats(reference: Quaternion) {
		filteringHandler.resetMovingAverage(getAdjustedRotation(), reference)
	}
}
