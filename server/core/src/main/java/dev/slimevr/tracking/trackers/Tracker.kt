package dev.slimevr.tracking.trackers

import dev.slimevr.VRServer
import dev.slimevr.autobone.StatsCalculator
import dev.slimevr.config.TrackerConfig
import dev.slimevr.filtering.CircularArrayList
import dev.slimevr.tracking.processor.stayaligned.trackers.StayAlignedTrackerState
import dev.slimevr.tracking.trackers.TrackerPosition.Companion.getByDesignation
import dev.slimevr.tracking.trackers.udp.IMUType
import dev.slimevr.tracking.trackers.udp.MagnetometerStatus
import dev.slimevr.tracking.trackers.udp.TrackerDataType
import dev.slimevr.util.InterpolationHandler
import io.eiren.util.BufferedTimer
import io.eiren.util.collections.FastList
import io.eiren.util.logging.LogManager
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlin.math.abs
import kotlin.math.atan2
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
		/*
		if (!isInternal && isImu()) {
			csv = File("C:/Users/Butterscotch/Desktop/Tracker Accel", "tracker_$id.csv")
			csvOut = csv.writer()

			LogManager.info("Starting recording (probably)")
			csvOut.write("Time (ms),Acceleration X,Acceleration Y,Acceleration Z,Acceleration Magnitude,Velocity X,Velocity Y,Velocity Z,Velocity Magnitude,Position X,Position Y,Position Z,HMD Position X,HMD Position Y,HMD Position Z\n")
		} else {
			csv = null
			csvOut = null
		}
		 */
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

	val minDur = 2000L
	var startTime = System.currentTimeMillis()

	data class AccelSample(val time: Long, val accel: Vector3, val hmdPos: Vector3)
	data class AccelTimeline(val resting: Boolean, val samples: FastList<AccelSample> = FastList<AccelSample>())

	var lastFrameRest = true
	var curFrameRest = true

	val lastSamples = CircularArrayList<AccelSample>(8)
	var curTimeline: AccelTimeline? = null

	var resetNext = false

	fun accumSample(accum: AccelAccumulator, sample: AccelSample, lastSampleTime: Long = -1, accelBias: Vector3 = Vector3.NULL): Float {
		val delta = if (lastSampleTime >= 0) {
			(sample.time - lastSampleTime) / 1000f
		} else {
			0f
		}
		accum.dataTick(sample.accel - accelBias, delta)

		return delta
	}

	fun processTimeline(accum: AccelAccumulator, timeline: AccelTimeline, lastSampleTime: Long = -1, accelBias: Vector3 = Vector3.NULL, action: (accum: AccelAccumulator, sample: AccelSample, delta: Float) -> Unit = { _, _, _ -> }): Long {
		// If -1, assume we are at the start
		var lastTime = lastSampleTime

		for (sample in timeline.samples) {
			val delta = accumSample(accum, sample, lastTime, accelBias)
			action(accum, sample, delta)
			lastTime = sample.time
		}

		return lastTime
	}

	fun processRest(accum: AccelAccumulator, timeline: AccelTimeline, lastSampleTime: Long = -1): Pair<Long, Vector3> {
		val sampleCount = timeline.samples.size.toFloat()
		var avgY = Vector3.NULL

		val lastTime = processTimeline(accum, timeline, lastSampleTime) { accum, _, _ ->
			avgY += accum.velocity / sampleCount
		}

		return Pair(lastTime, avgY)
	}

	fun writeTimeline(accum: AccelAccumulator, timeline: AccelTimeline, lastSampleTime: Long = -1, accelBias: Vector3 = Vector3.NULL): Long {
		// Accel position is only the offset, so let's make the HMD an offset too
		val initHmd = timeline.samples.first().hmdPos

		val time = processTimeline(accum, timeline, lastSampleTime, accelBias) { accum, sample, _ ->
			val time = sample.time
			val accel = accum.acceleration
			val vel = accum.velocity
			val pos = accum.offset
			val hmd = sample.hmdPos - initHmd

			// csvOut?.write("$time,${accel.x},${accel.y},${accel.z},${accel.len()},${vel.x},${vel.y},${vel.z},${vel.len()},${pos.x},${pos.y},${pos.z},${hmd.x},${hmd.y},${hmd.z}\n")
		}

		return time
	}

	fun angle(vector: Vector3): Quaternion {
		val yaw = atan2(vector.x, vector.z)
		return Quaternion.rotationAroundYAxis(yaw)
	}

	fun startMounting() {
		resetNext = true
		startTime = System.currentTimeMillis()
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

		if (resetNext) {
			lastFrameRest = curFrameRest

			val accel = getAcceleration()
			val accelLen = accel.len()
			val hmdPos = if (VRServer.instanceInitialized) {
				VRServer.instance.humanPoseManager.skeleton.headTracker?.position ?: Vector3.NULL
			} else {
				Vector3.NULL
			}
			val sample = AccelSample(timeAtLastUpdate - startTime, accel, hmdPos)

			// Ensure a minimum sample size, assume resting at start
			if (lastSamples.size >= 4) {
				val stats = StatsCalculator()
				for (sample in lastSamples) {
					stats.addValue(sample.accel.len())
				}

				curFrameRest = if (curFrameRest) {
					stats.mean < 0.3f && accelLen - stats.mean < 0.6f
				} else {
					stats.mean < 0.1f && stats.standardDeviation < 0.2f && sample.time >= minDur
				}
			}

			// On rest state change
			if (curFrameRest != lastFrameRest) {
				if (curFrameRest) {
					LogManager.info("[Accel] Tracker $id (${trackerPosition?.designation}) is now eepy.")

					curTimeline?.let { move ->
						val firstSample = move.samples.first()
						val lastSample = move.samples.last()

						val calibAccum = AccelAccumulator()
						processTimeline(calibAccum, move)

						val moveTime = lastSample.time - firstSample.time
						val postAvg = calibAccum.velocity

						// Assume the velocity at the end is the resting velocity
						val slope = postAvg / (moveTime / 1000f)
						// LogManager.info("moveTime: $moveTime\npostAvg: $postAvg\nslope: $slope")

						val outAccum = AccelAccumulator()
						processTimeline(outAccum, move, accelBias = slope)

						// We need to compare offsets of HMD and tracker
						val hmdOff = lastSample.hmdPos - firstSample.hmdPos
						val trackerOff = outAccum.offset

						val hmd = Vector3(hmdOff.x, 0f, hmdOff.z)
						val tracker = Vector3(trackerOff.x, 0f, trackerOff.z)

						val hmdRot = angle(hmd.unit())
						val trackerRot = angle(tracker.unit())
						val mountRot = trackerRot * hmdRot.inv()

						val mountVec = (resetsHandler.mountingOrientation * resetsHandler.mountRotFix * mountRot).inv().sandwich(Vector3.POS_Z)
						val mountText = if (abs(mountVec.z) > abs(mountVec.x)) {
							if (mountVec.z < 0f) {
								"front"
							} else {
								"back"
							}
						} else {
							if (mountVec.x > 0f) {
								"right"
							} else {
								"left"
							}
						}

						LogManager.info("[Accel] Tracker $id (${trackerPosition?.designation}):\nTracker: $trackerOff\nHmd: $hmdOff\nErr: ${tracker.len() - hmd.len()}\nResult: $mountVec ($mountText)")
						resetsHandler.mountRotFix *= mountRot
						resetNext = false
					}
					curTimeline = null
				} else {
					LogManager.info("[Accel] Tracker $id (${trackerPosition?.designation}) now has zoomies!")

					// Cycle timeline
					curTimeline = AccelTimeline(false)
					for (sample in lastSamples) {
						curTimeline?.samples?.add(sample)
					}
				}

				// Flush rest detection
				lastSamples.clear()
			}

			// Moving avg accel for rest detection
			if (lastSamples.size == lastSamples.capacity()) {
				lastSamples.removeLast()
			}

			// Collect samples for rest detection at a constant-ish rate if possible
			if (curFrameRest) {
				lastSamples.add(sample)
			} else {
				// Collect the latest samples when moving
				curTimeline?.samples?.add(sample)

				if (lastSamples.isNotEmpty()) {
					// Try to have TPS at a lower rate
					if (sample.time - lastSamples.first().time > 100) {
						lastSamples.add(sample)
					}
				} else {
					lastSamples.add(sample)
				}
			}
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
