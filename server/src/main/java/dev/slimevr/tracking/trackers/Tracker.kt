package dev.slimevr.tracking.trackers

import com.jme3.math.FastMath
import dev.slimevr.config.TrackerConfig
import dev.slimevr.tracking.trackers.TrackerPosition.Companion.getByDesignation
import dev.slimevr.vrServer
import io.eiren.util.BufferedTimer
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3

const val TIMEOUT_MS = 2000L

/**
 * Generic tracker class for input and output tracker,
 * with flags on instantiation.
 */
class Tracker @JvmOverloads constructor(
	val device: Device?,
	val id: Int,
	val name: String,
	var trackerPosition: TrackerPosition?,
	val hasPosition: Boolean = false,
	val hasRotation: Boolean = false,
	val hasAcceleration: Boolean = false,
	val userEditable: Boolean = false,
	val isInternal: Boolean = false,
	val isComputed: Boolean = false,
	val isImu: Boolean = false,
	val isPoseFrame: Boolean = false,
	val usesTimeout: Boolean = false,
	val needsFiltering: Boolean = false,
	val needsReset: Boolean = false,
	val needsMounting: Boolean = false,
	val isWireless: Boolean = false,
	val hasBattery: Boolean = false,
) {
	private val timer = BufferedTimer(1f)
	private var timeAtLastUpdate: Long = 0
	private var rotation = Quaternion.IDENTITY
	var position = Vector3.NULL
	var acceleration = Vector3.NULL
	val resetsHandler: TrackerResetsHandler = TrackerResetsHandler()
	val filteringHandler: TrackerFilteringHandler = TrackerFilteringHandler()
	var status = TrackerStatus.DISCONNECTED
	var batteryVoltage = -1f
	var batteryLevel = -1f
	var ping = -1
	var signalStrength = -1
	var temperature = -1f
	var displayName: String? = null
	var customName: String? = null

	/**
	 * Reads/loads from the given config
	 */
	fun readConfig(config: TrackerConfig) {
		if (userEditable) {
			val cN = config.customName
			if (cN != null) {
				customName = cN
			}
			val des = getByDesignation(config.designation)
			if (des != null) {
				trackerPosition = des
			}
			val mO = config.mountingOrientation
			if (needsMounting && mO != null) {
				resetsHandler.mountingOrientation = mO
			} else {
				resetsHandler.mountingOrientation = Quaternion.IDENTITY
			}
			val aDC = config.allowDriftCompensation
			if (isImu && aDC == null) {
				// If value didn't exist, default to true and save
				resetsHandler.allowDriftCompensation = true
				vrServer!!
					.getConfigManager()
					.getVrConfig()
					.getTracker(this)
					.setAllowDriftCompensation(true)
				vrServer!!.getConfigManager().saveConfig()
			} else {
				resetsHandler.allowDriftCompensation = aDC
			}
		}
	}

	/**
	 * Writes/saves to the given config
	 */
	fun writeConfig(config: TrackerConfig) {
		val tP = trackerPosition
		if (tP != null) {
			config.designation = tP.designation
		}
		val cN = config.customName
		if (cN != null) {
			config.customName = cN
		}
		if (needsMounting) {
			config
				.setMountingOrientation(
					if (resetsHandler.mountingOrientation != null) {
						resetsHandler.mountingOrientation
					} else {
						EulerAngles(
							EulerOrder.YZX,
							0f,
							FastMath.PI,
							0f
						).toQuaternion()
					}
				)
		}
		if (isImu) {
			config.allowDriftCompensation = resetsHandler.allowDriftCompensation
		}
	}

	/**
	 * Synchronized with the VRServer's 1000hz while loop
	 */
	fun tick() {
		if (usesTimeout) {
			if (System.currentTimeMillis() - timeAtLastUpdate > TIMEOUT_MS) {
				status = TrackerStatus.DISCONNECTED
			}
		}
		filteringHandler.tick()
	}

	/**
	 * Tells the tracker that it received new data
	 */
	fun dataTick() {
		timer.update()
		timeAtLastUpdate = System.currentTimeMillis()
		filteringHandler.dataTick(rotation)
	}

	/**
	 * Gets the adjusted tracker rotation after all corrections
	 * (filtering, reset, mounting and drift compensation).
	 * This is the rotation that is applied on the SlimeVR skeleton bones.
	 */
	fun getRotation(): Quaternion {
		var rot = if (filteringHandler.enabled) {
			// Get filtered rotation
			filteringHandler.getFilteredRotation()
		} else {
			// Get unfiltered rotation
			rotation
		}

		if (needsReset && trackerPosition != TrackerPosition.HEAD) {
			// Adjust to reset, mounting and drift compensation
			rot = resetsHandler.getReferenceAdjustedRotationFrom(rot)
		}

		return rot
	}

	/**
	 * Gets the identity-adjusted tracker rotation after some corrections
	 * (filtering, identity reset and identity mounting).
	 * This is used for debugging/visualizing tracker data
	 */
	fun getIdentityAdjustedRotation(): Quaternion {
		var rot = if (filteringHandler.enabled) {
			// Get filtered rotation
			filteringHandler.getFilteredRotation()
		} else {
			// Get unfiltered rotation
			rotation
		}

		if (needsReset && trackerPosition != TrackerPosition.HEAD) {
			// Adjust to reset and mounting
			rot = resetsHandler.getIdentityAdjustedRotationFrom(rot)
		}

		return rot
	}

	/**
	 * Gets the raw (unadjusted) rotation of the tracker.
	 * If this is an IMU, this will be the raw sensor rotation.
	 */
	fun getRawRotation(): Quaternion {
		return rotation
	}

	/**
	 * Sets the raw (unadjusted) rotation of the tracker.
	 */
	fun setRotation(rotation: Quaternion) {
		this.rotation = rotation
	}

	/**
	 * Gets the current TPS of the tracker
	 */
	val tps: Float
		get() = timer.averageFPS
}
