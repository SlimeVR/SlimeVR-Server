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
import kotlin.math.max
import kotlin.math.sqrt

/**
 * Calculates the position standard deviation (root mean square distance from the mean)
 * for a given list of Vector3 samples.
 *
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

interface UserHeightCalibrationListener {
	fun onStatusChange(status: UserHeightRecordingStatusResponseT)
}

class UserHeightCalibration(val server: VRServer, val humanPoseManager: HumanPoseManager) {
	var status = UserHeightCalibrationStatus.NONE
	var canDoFloorHeight = false // Flag updated in checkTrackers()

	var currentHeight = 0f
	var currentFloorLevel = 0f
	var lastHeightChange = 0f

	private val hmdPositionSamples: CircularFifoQueue<Vector3> = CircularFifoQueue(MAX_SAMPLES)
	private var heightStableStartTime: Float? = null

	private val floorPositionSamples: CircularFifoQueue<Vector3> = CircularFifoQueue(MAX_SAMPLES)
	private var floorStableStartTime: Float? = null

	private val listeners: MutableList<UserHeightCalibrationListener> = CopyOnWriteArrayList()
	private var hmd: Tracker? = null

	private val handTrackers: MutableList<Tracker> = mutableListOf()

	fun start() {
		if (canDoFloorHeight) {
			status = UserHeightCalibrationStatus.RECORDING_FLOOR
			currentFloorLevel = Float.MAX_VALUE
		} else {
			status = UserHeightCalibrationStatus.RECORDING_HEIGHT
			currentFloorLevel = 0f
		}

		hmdPositionSamples.clear()
		floorPositionSamples.clear()
		heightStableStartTime = null
		floorStableStartTime = null
		currentHeight = 0f
		sendStatusUpdate()
	}

	init {
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
					!it.isInternal
				it.hasPosition &&
					it.status == TrackerStatus.OK
			},
		)

		canDoFloorHeight = handTrackers.isNotEmpty()

		hmd = server.allTrackers.find {
			it.trackerPosition == TrackerPosition.HEAD &&
				it.hasPosition &&
				!it.isInternal &&
				it.status == TrackerStatus.OK
		}
		server.serverGuards.canDoUserHeightCalibration = hmd != null

		currentHeight = 0f
		currentFloorLevel = 0f
	}

	fun tick() {
		val currentTime = System.nanoTime().toFloat()

		if (status == UserHeightCalibrationStatus.RECORDING_FLOOR) {
			recordFloor(currentTime)
		} else if (status == UserHeightCalibrationStatus.RECORDING_HEIGHT) {
			recordHeight(currentTime)
		}

		if (lastHeightChange != 0f && currentTime - lastHeightChange >= UPDATE_DEBOUNCE) {
			sendStatusUpdate()
			lastHeightChange = 0f
		}
	}

	private fun recordFloor(currentTime: Float) {
		val lowestTracker = handTrackers.minByOrNull { it.position.y }
		val currentLowestPos = lowestTracker?.position ?: return

		currentFloorLevel = minOf(currentFloorLevel, currentLowestPos.y)

		floorPositionSamples.add(currentLowestPos)

		if (floorPositionSamples.isAtFullCapacity) {
			val isStable = checkFloorStability()

			if (isStable) {
				if (floorStableStartTime == null) {
					floorStableStartTime = currentTime
				}

				val stableDuration = currentTime - floorStableStartTime!!
				if (stableDuration >= CONTROLLER_STABILITY_DURATION) {
					status = UserHeightCalibrationStatus.RECORDING_HEIGHT
					sendStatusUpdate()
				}
			} else {
				floorStableStartTime = null
			}
		}
	}

	private fun recordHeight(currentTime: Float) {
		val localHmd = hmd ?: return

		val currentPos = localHmd.position
		val relativeY = currentPos.y - currentFloorLevel

		val newHeight = max(currentHeight, relativeY)
		if (newHeight != currentHeight) {
			currentHeight = newHeight
			lastHeightChange = currentTime
		}

		hmdPositionSamples.add(currentPos)

		if (hmdPositionSamples.isAtFullCapacity) {
			val isStable = checkHeightStability()

			if (isStable) {
				if (heightStableStartTime == null) {
					heightStableStartTime = currentTime
				}

				val stableDuration = currentTime - heightStableStartTime!!
				if (stableDuration >= HEAD_STABILITY_DURATION) {
					status = UserHeightCalibrationStatus.DONE
					sendStatusUpdate()
				}
			} else {
				heightStableStartTime = null
			}
		}
	}

	private fun checkFloorStability(): Boolean {
		val posStdDev = calculatePositionStdDev(floorPositionSamples)
		return posStdDev <= CONTROLLER_POSITION_STD_DEV_THRESHOLD
	}

	private fun checkHeightStability(): Boolean {
		val posStdDev = calculatePositionStdDev(hmdPositionSamples)
		return posStdDev <= POSITION_STD_DEV_THRESHOLD
	}

	fun addListener(listener: UserHeightCalibrationListener) {
		listeners.add(listener)
	}

	fun removeListener(listener: UserHeightCalibrationListener) {
		listeners.remove(listener)
	}

	fun sendStatusUpdate() {
		val res = UserHeightRecordingStatusResponseT().apply {
			this.canDoFloorHeight = this@UserHeightCalibration.canDoFloorHeight
			this.status = this@UserHeightCalibration.status
			this.userHeight = this@UserHeightCalibration.currentHeight
		}

		listeners.forEach { it.onStatusChange(res) }
	}

	companion object {
		private const val MAX_SAMPLES = 100

		private const val POSITION_STD_DEV_THRESHOLD = 0.01f
		private const val CONTROLLER_POSITION_STD_DEV_THRESHOLD = 0.005f
		private const val HEAD_STABILITY_DURATION = 2_000_000_000f
		private const val CONTROLLER_STABILITY_DURATION = 1_000_000_000f
		private const val UPDATE_DEBOUNCE = 100_000_000f
	}
}
