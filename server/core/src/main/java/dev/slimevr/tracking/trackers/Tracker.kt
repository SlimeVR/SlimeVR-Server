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
import solarxr_protocol.datatypes.DeviceIdT
import solarxr_protocol.datatypes.TrackerIdT
import solarxr_protocol.rpc.StatusData
import solarxr_protocol.rpc.StatusDataUnion
import solarxr_protocol.rpc.StatusTrackerErrorT
import solarxr_protocol.rpc.StatusTrackerResetT
import java.io.File
import java.io.OutputStreamWriter
import kotlin.math.abs
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
	val needsReset: Boolean = false,
	val needsMounting: Boolean = false,
	val isHmd: Boolean = false,
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
	var ping: Int? = null
	var signalStrength: Int? = null
	var temperature: Float? = null
	var customName: String? = null
	var magStatus: MagnetometerStatus = magStatus
		private set

	/**
	 * If the tracker has gotten disconnected after it was initialized first time
	 */
	var statusResetRecently = false
	private var alreadyInitialized = false
	var status: TrackerStatus by Delegates.observable(TrackerStatus.DISCONNECTED) { _, old, new ->
		if (old == new) return@observable

		if (!new.reset) {
			if (alreadyInitialized) {
				statusResetRecently = true
			}
			alreadyInitialized = true
		}
		if (!isInternal && VRServer.instanceInitialized) {
			// If the status of a non-internal tracker has changed, inform
			// the VRServer to recreate the skeleton, as it may need to
			// assign or un-assign the tracker to a body part
			VRServer.instance.updateSkeletonModel()
			VRServer.instance.refreshTrackersDriftCompensationEnabled()

			if (isHmd) {
				VRServer.instance.humanPoseManager.checkReportMissingHmd()
			}
			checkReportErrorStatus()
			checkReportRequireReset()

			VRServer.instance.trackerStatusChanged(this, old, new)
		}
	}

	var trackerPosition: TrackerPosition? by Delegates.observable(trackerPosition) { _, old, new ->
		if (old == new) return@observable

		if (!isInternal) {
			// Set default mounting orientation for that body part
			new?.let { resetsHandler.mountingOrientation = it.defaultMounting() }

			checkReportRequireReset()
		}
	}

	// Computed value to simplify availability checks
	val hasAdjustedRotation = hasRotation && (allowFiltering || needsReset)

	/**
	 * It's like the ID, but it should be local to the device if it has one
	 */
	val trackerNum: Int = trackerNum ?: id

	val stayAligned = StayAlignedTrackerState(this)
	val yawResetSmoothing = InterpolationHandler()

	val csv: File?
	val csvOut: OutputStreamWriter?
	val startTime = System.currentTimeMillis()

	init {
		// IMPORTANT: Look here for the required states of inputs
		require(!needsReset || (hasRotation && needsReset)) {
			"If ${::needsReset.name} is true, then ${::hasRotation.name} must also be true"
		}
		require(!needsMounting || (needsReset && needsMounting)) {
			"If ${::needsMounting.name} is true, then ${::needsReset.name} must also be true"
		}
		require(!isHmd || (hasPosition && isHmd)) {
			"If ${::isHmd.name} is true, then ${::hasPosition.name} must also be true"
		}
// 		require(device != null && _trackerNum == null) {
// 			"If ${::device.name} exists, then ${::trackerNum.name} must not be null"
// 		}
		if (!isInternal && isImu()) {
			csv = File("C:/Users/Butterscotch/Desktop/Tracker Accel", "tracker_$id.csv")
			csvOut = csv.writer()

			LogManager.info("Starting recording (probably)")
			csvOut.write("Time (ms),Acceleration X,Acceleration Y,Acceleration Z,Acceleration Magnitude,Velocity X,Velocity Y,Velocity Z,Velocity Magnitude,Position X,Position Y,Position Z,HMD Position X,HMD Position Y,HMD Position Z\n")
		} else {
			csv = null
			csvOut = null
		}
	}

	fun checkReportRequireReset() {
		if (needsReset && trackerPosition != null && lastResetStatus == 0u &&
			!status.reset && (isImu() || !statusResetRecently && trackerDataType != TrackerDataType.FLEX_ANGLE)
		) {
			reportRequireReset()
		} else if (lastResetStatus != 0u && (trackerPosition == null || status.reset)) {
			VRServer.instance.statusSystem.removeStatus(lastResetStatus)
			lastResetStatus = 0u
		}
	}

	/**
	 * If 0 then it's null
	 */
	var lastResetStatus = 0u
	private fun reportRequireReset() {
		require(lastResetStatus == 0u) {
			"lastResetStatus must be 0u, but was $lastResetStatus"
		}

		val tempTrackerNum = this.trackerNum
		val statusMsg = StatusTrackerResetT().apply {
			trackerId = TrackerIdT().apply {
				if (device != null) {
					deviceId = DeviceIdT().apply { id = device.id }
				}
				trackerNum = tempTrackerNum
			}
		}
		val status = StatusDataUnion().apply {
			type = StatusData.StatusTrackerReset
			value = statusMsg
		}
		lastResetStatus = VRServer.instance.statusSystem.addStatus(status, true)
	}

	private fun checkReportErrorStatus() {
		if (status == TrackerStatus.ERROR && lastErrorStatus == 0u) {
			reportErrorStatus()
		} else if (lastErrorStatus != 0u && status != TrackerStatus.ERROR) {
			VRServer.instance.statusSystem.removeStatus(lastErrorStatus)
			lastErrorStatus = 0u
		}
	}

	var lastErrorStatus = 0u
	private fun reportErrorStatus() {
		require(lastErrorStatus == 0u) {
			"lastResetStatus must be 0u, but was $lastErrorStatus"
		}

		val tempTrackerNum = this.trackerNum
		val statusMsg = StatusTrackerErrorT().apply {
			trackerId = TrackerIdT().apply {
				if (device != null) {
					deviceId = DeviceIdT().apply { id = device.id }
				}
				trackerNum = tempTrackerNum
			}
		}
		val status = StatusDataUnion().apply {
			type = StatusData.StatusTrackerError
			value = statusMsg
		}
		lastErrorStatus = VRServer.instance.statusSystem.addStatus(status, true)
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
		if (needsMounting) {
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
		if (!isInternal &&
			!(!isImu() && (trackerPosition == TrackerPosition.LEFT_HAND || trackerPosition == TrackerPosition.RIGHT_HAND))
		) {
			checkReportRequireReset()
		}
	}

	/**
	 * Writes/saves to the given config
	 */
	fun writeConfig(config: TrackerConfig) {
		trackerPosition?.let { config.designation = it.designation } ?: run { config.designation = null }
		customName?.let { config.customName = it }
		if (needsMounting) {
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
		val time = processTimeline(accum, timeline, lastSampleTime, accelBias) { accum, sample, _ ->
			val time = sample.time
			val accel = accum.acceleration
			val vel = accum.velocity
			val pos = accum.offset
			val hmd = sample.hmdPos

			csvOut?.write("$time,${accel.x},${accel.y},${accel.z},${accel.len()},${vel.x},${vel.y},${vel.z},${vel.len()},${pos.x},${pos.y},${pos.z},${hmd.x},${hmd.y},${hmd.z}\n")
		}

		return time
	}

	/**
	 * Tells the tracker that it received new data
	 */
	fun dataTick() {
		timer.update()
		timeAtLastUpdate = System.currentTimeMillis()
		if (trackRotDirection) {
			filteringHandler.dataTick(getAdjustedRotation())
		}

		if (csvOut != null) {
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
					stats.mean < 0.1f && stats.standardDeviation < 0.2f
				}
			}

			// On rest state change
			if (curFrameRest != lastFrameRest) {
				if (curFrameRest) {
					LogManager.info("[Accel] Tracker $id (${trackerPosition?.designation}) is now eepy.")

					curTimeline?.let { move ->
						val calibAccum = AccelAccumulator()
						// We don't need the pre-rest time
						val moveTime = processTimeline(calibAccum, move)
						val postAvg = calibAccum.velocity

						// Assume the velocity at the end is the resting velocity
						val slope = postAvg / (moveTime / 1000f)
						LogManager.info("moveTime: $moveTime\npostAvg: $postAvg\nslope: $slope")

						val outAccum = AccelAccumulator()
						writeTimeline(outAccum, move, accelBias = slope)

						// We need to compare offsets of HMD and tracker
						val hmdStart = move.samples.first().hmdPos
						val hmdEnd = move.samples.last().hmdPos
						val hmdOff = hmdEnd - hmdStart

						// Swap X and Z, we might just be aligning accel wrong?
						// TODO: Check if accel is actually correct
						val pos = Vector3(outAccum.offset.z, outAccum.offset.y, outAccum.offset.x)
						LogManager.info("[Accel] Tracker $id (${trackerPosition?.designation}) final offset: $pos\nHmd offset: $hmdOff\nDiff: ${pos - hmdOff}")

						val dir = if (abs(pos.x) > abs(pos.z)) {
							if (pos.x > 0f) {
								"front"
							} else {
								"back"
							}
						} else {
							if (pos.z > 0f) {
								"right"
							} else {
								"left"
							}
						}
						LogManager.info("[Accel] Tracker $id (${trackerPosition?.designation}) has $dir mounting.")

						if (resetNext) {
							resetNext = false

							LogManager.info("[Accel] Tracker $id (${trackerPosition?.designation}) setting mounting!")
							val mount = when (dir) {
								"front" -> Quaternion.SLIMEVR.FRONT

								"back" -> Quaternion.SLIMEVR.BACK

								"left" -> Quaternion.SLIMEVR.LEFT

								"right" -> Quaternion.SLIMEVR.RIGHT

								else -> {
									Quaternion.SLIMEVR.FRONT
								}
							}

							resetsHandler.mountingOrientation = mount
						}
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
		return if (needsReset && !(isComputed && isInternal) && trackerDataType == TrackerDataType.ROTATION) {
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
		return if (needsReset && !(isComputed && isInternal) && trackerDataType == TrackerDataType.ROTATION) {
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
		return if (needsReset && !(isComputed && trackerPosition != TrackerPosition.HEAD) && trackerDataType == TrackerDataType.ROTATION) {
			// Adjust to reset and mounting
			resetsHandler.getIdentityAdjustedDriftRotationFrom(rot)
		} else {
			rot
		}
	}

	/**
	 * Get the rotation of the tracker after the resetsHandler's corrections and filtering if applicable
	 */
	fun getRotation(): Quaternion {
		var rot = if (trackRotDirection) {
			filteringHandler.getFilteredRotation()
		} else {
			// Get non-filtered rotation
			getAdjustedRotation()
		}

		if (yawResetSmoothing.remainingTime > 0f) {
			rot = yawResetSmoothing.curRotation * rot
		}

		return rot
	}

	/**
	 * Gets the world-adjusted acceleration
	 */
	fun getAcceleration(): Vector3 = if (needsReset) {
		resetsHandler.getReferenceAdjustedAccel(_rotation, _acceleration)
	} else {
		_acceleration
	}

	/**
	 * Gets the unadjusted (local-space) acceleration
	 */
	fun getRawAcceleration(): Vector3 = _acceleration

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
	fun getMagVector() = if (needsReset) {
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
