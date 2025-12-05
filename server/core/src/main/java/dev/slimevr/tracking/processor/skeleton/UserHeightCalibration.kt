package dev.slimevr.tracking.processor.skeleton

import dev.slimevr.VRServer
import dev.slimevr.tracking.processor.HumanPoseManager
import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.TrackerPosition
import dev.slimevr.tracking.trackers.TrackerStatus
import io.github.axisangles.ktmath.Vector3
import org.apache.commons.collections4.queue.CircularFifoQueue
import solarxr_protocol.rpc.UserHeightCalibrationStatus
import solarxr_protocol.rpc.UserHeightRecordingStatusResponseT
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sqrt

/**
 * @param positionSamples The list of Vector3 samples.
 * @return The standard deviation as a Float. Returns Float.MAX_VALUE if the list is empty.
 */
fun calculatePositionStdDev(positionSamples: Collection<Vector3>): Float {
	val sampleCount = positionSamples.size
	if (sampleCount == 0) return Float.MAX_VALUE

	var sumX = 0f
	var sumY = 0f
	var sumZ = 0f

	for (pos in positionSamples) {
		sumX += pos.x
		sumY += pos.y
		sumZ += pos.z
	}

	val meanX = sumX / sampleCount
	val meanY = sumY / sampleCount
	val meanZ = sumZ / sampleCount

	var varianceSum = 0f
	for (pos in positionSamples) {
		val dx = pos.x - meanX
		val dy = pos.y - meanY
		val dz = pos.z - meanZ
		// Square of the Euclidean distance from the mean
		varianceSum += dx * dx + dy * dy + dz * dz
	}

	val variance = varianceSum / sampleCount
	return sqrt(variance)
}

fun isHmdLeveled(hmd: Tracker, threshold: Double): Boolean {
	val q = hmd.getRotation()

	val worldHmdUp = q.sandwich(Vector3.POS_Y)
	val dotProduct = worldHmdUp.dot(Vector3.POS_Y)
	return dotProduct >= threshold
}

fun isControllerPointingDown(controller: Tracker, threshold: Double): Boolean {
	val q = controller.getRawRotation()
	val controllerForwardWorld = q.sandwich(Vector3.NEG_Z)
	val worldDown = Vector3.NEG_Y
	val dotProduct = controllerForwardWorld.dot(worldDown)

	return dotProduct >= threshold
}

interface UserHeightCalibrationListener {
	fun onStatusChange(status: UserHeightRecordingStatusResponseT)
}

class UserHeightCalibration(val server: VRServer, val humanPoseManager: HumanPoseManager) {
	var status = UserHeightCalibrationStatus.NONE

	var currentHeight = 0f
	var currentFloorLevel = 0f

	var startTime = 0L

	private val hmdPositionSamples: CircularFifoQueue<Vector3> = CircularFifoQueue(MAX_SAMPLES)
	private var heightStableStartTime: Long? = null

	private val floorPositionSamples: CircularFifoQueue<Vector3> = CircularFifoQueue(MAX_SAMPLES)
	private var floorStableStartTime: Long? = null

	private val listeners: MutableList<UserHeightCalibrationListener> = CopyOnWriteArrayList()
	private var hmd: Tracker? = null

	private val handTrackers: MutableList<Tracker> = mutableListOf()

	fun start() {
		clear()
		checkTrackers()

		if (!server.serverGuards.canDoUserHeightCalibration) {
			return
		}

		startTime = System.nanoTime()

		status = UserHeightCalibrationStatus.RECORDING_FLOOR
		currentFloorLevel = Float.MAX_VALUE

		sendStatusUpdate()
	}

	fun clear() {
		status = UserHeightCalibrationStatus.NONE
		hmdPositionSamples.clear()
		floorPositionSamples.clear()
		heightStableStartTime = null
		floorStableStartTime = null
		currentHeight = 0f
		startTime = 0L
	}

	init {
		clear()
		checkTrackers()
	}

	fun checkTrackers() {
		handTrackers.clear()
		handTrackers.addAll(
			server.allTrackers.filter {
				(
					it.trackerPosition == TrackerPosition.LEFT_HAND ||
						it.trackerPosition == TrackerPosition.RIGHT_HAND
					) &&
					!it.isInternal &&
					it.hasPosition &&
					it.status == TrackerStatus.OK
			},
		)

		hmd = server.allTrackers.find {
			it.trackerPosition == TrackerPosition.HEAD &&
				it.hasPosition &&
				!it.isInternal &&
				it.status == TrackerStatus.OK
		}
		server.serverGuards.canDoUserHeightCalibration = hmd != null && handTrackers.isNotEmpty()

		currentHeight = 0f
		currentFloorLevel = 0f
	}

	fun applyCalibration() {
		server.configManager.vrConfig.skeleton.hmdHeight = currentHeight
		server.configManager.vrConfig.skeleton.floorHeight = 0f

		server.humanPoseManager.resetOffsets()
		server.humanPoseManager.saveConfig()
		server.configManager.saveConfig()

		server.trackingChecklistManager.resetMountingCompleted = false
		server.trackingChecklistManager.feetResetMountingCompleted = false
	}

	fun tick() {
		if (startTime == 0L) return

		val currentTime = System.nanoTime()
		if (active && currentTime - startTime > TIMEOUT_TIME) {
			status = UserHeightCalibrationStatus.ERROR_TIMEOUT
			sendStatusUpdate()
			return
		}

		when (status) {
			UserHeightCalibrationStatus.RECORDING_FLOOR, UserHeightCalibrationStatus.WAITING_FOR_CONTROLLER_PITCH -> recordFloor(currentTime)
			UserHeightCalibrationStatus.WAITING_FOR_RISE, UserHeightCalibrationStatus.RECORDING_HEIGHT, UserHeightCalibrationStatus.WAITING_FOR_FW_LOOK -> recordHeight(currentTime)
		}
	}

	private fun recordFloor(currentTime: Long) {
		val lowestTracker = handTrackers.minByOrNull { it.position.y }
		val currentLowestPos = lowestTracker?.position ?: return

		if (currentLowestPos.y > MAX_FLOOR_Y) {
			floorStableStartTime = null
			floorPositionSamples.clear()
			return
		}

		if (!isControllerPointingDown(lowestTracker, CONTROLLER_ANGLE_THRESHOLD)) {
			status = UserHeightCalibrationStatus.WAITING_FOR_CONTROLLER_PITCH
			floorStableStartTime = null
			floorPositionSamples.clear()
			sendStatusUpdate()
			return
		}

		floorPositionSamples.add(currentLowestPos)
		currentFloorLevel = minOf(currentFloorLevel, currentLowestPos.y)

		if (floorPositionSamples.isAtFullCapacity) {
			val isStable = calculatePositionStdDev(floorPositionSamples) <= CONTROLLER_POSITION_STD_DEV_THRESHOLD

			if (isStable) {
				if (floorStableStartTime == null) {
					floorStableStartTime = currentTime
				}

				val stableDuration = currentTime - floorStableStartTime!!
				if (stableDuration >= CONTROLLER_STABILITY_DURATION) {
					status = UserHeightCalibrationStatus.WAITING_FOR_RISE
					sendStatusUpdate()
				}
			} else {
				floorStableStartTime = null
			}
		}
	}

	private fun recordHeight(currentTime: Long) {
		val localHmd = hmd ?: return

		val currentPos = localHmd.position
		val relativeY = currentPos.y - currentFloorLevel

		if (relativeY <= HMD_RISE_THRESHOLD) {
			status = UserHeightCalibrationStatus.WAITING_FOR_RISE
			sendStatusUpdate()
			hmdPositionSamples.clear()
			heightStableStartTime = null
			return
		}

		if (currentHeight != relativeY) {
			currentHeight = relativeY
			sendStatusUpdate()
		}

		if (!isHmdLeveled(localHmd, HEAD_ANGLE_THRESHOLD)) {
			status = UserHeightCalibrationStatus.WAITING_FOR_FW_LOOK
			heightStableStartTime = null
			hmdPositionSamples.clear()
			sendStatusUpdate()
			return
		}

		status = UserHeightCalibrationStatus.RECORDING_HEIGHT
		hmdPositionSamples.add(currentPos)

		if (hmdPositionSamples.isAtFullCapacity) {
			val std = calculatePositionStdDev(hmdPositionSamples)
			val isStable = std <= POSITION_STD_DEV_THRESHOLD

			if (isStable) {
				if (heightStableStartTime == null) {
					heightStableStartTime = currentTime
				}

				val stableDuration = currentTime - heightStableStartTime!!
				if (stableDuration >= HEAD_STABILITY_DURATION) {
					status = if (currentHeight < 1.2f) {
						UserHeightCalibrationStatus.ERROR_TOO_SMALL
					} else if (currentHeight > 1.936f) {
						UserHeightCalibrationStatus.ERROR_TOO_HIGH
					} else {
						UserHeightCalibrationStatus.DONE
					}

					if (status == UserHeightCalibrationStatus.DONE) {
						applyCalibration()
					}

					sendStatusUpdate()
				}
			} else {
				heightStableStartTime = null
			}
		}
	}

	fun addListener(listener: UserHeightCalibrationListener) {
		listeners.add(listener)
	}

	fun removeListener(listener: UserHeightCalibrationListener) {
		listeners.remove(listener)
	}

	fun sendStatusUpdate() {
		val res = UserHeightRecordingStatusResponseT().apply {
			this.status = this@UserHeightCalibration.status
			this.hmdHeight = this@UserHeightCalibration.currentHeight
		}
		listeners.forEach { it.onStatusChange(res) }
	}

	val active: Boolean
		get() {
			return status == UserHeightCalibrationStatus.RECORDING_HEIGHT || status == UserHeightCalibrationStatus.RECORDING_FLOOR || status == UserHeightCalibrationStatus.WAITING_FOR_RISE || status == UserHeightCalibrationStatus.WAITING_FOR_FW_LOOK
		}

	companion object {
		private const val MAX_SAMPLES = 100

		private const val POSITION_STD_DEV_THRESHOLD = 0.003f
		private const val HEAD_STABILITY_DURATION = 600_000_000f

		private const val CONTROLLER_POSITION_STD_DEV_THRESHOLD = 0.005f
		private const val CONTROLLER_STABILITY_DURATION = 300_000_000f

		private const val MAX_FLOOR_Y = 0.10f
		private const val HMD_RISE_THRESHOLD = 1.2f

		val HEAD_ANGLE_THRESHOLD = cos((PI / 180f) * 15f)
		val CONTROLLER_ANGLE_THRESHOLD = cos((PI / 180f) * 45f)

		private const val TIMEOUT_TIME = 30_000_000_000f
	}
}
