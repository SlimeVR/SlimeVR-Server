package dev.slimevr.tracking.trackers

import dev.slimevr.config.TrackerConfig
import io.eiren.util.BufferedTimer
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3

/**
 * Generic tracker class for input and output tracker,
 * with flags on instantiation
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
	private var rotation = Quaternion.IDENTITY
	var position = Vector3.NULL
	val resetsHandler: ResetsHandler = ResetsHandler(this)
	val filteringHandler: FilteringHandler = FilteringHandler(this)
	var status = TrackerStatus.DISCONNECTED
	var acceleration = Vector3.NULL
	var batteryVoltage = 0f
	var batteryLevel = 0f
	var ping = -1
	var signalStrength = -1
	var temperature = 0f
	var displayName: String? = null
	var customName: String? = null

	init {

	}

	fun readConfig(config: TrackerConfig) {
		if (userEditable) {
		}
	}

	fun writeConfig(config: TrackerConfig) {}

	fun getRotation(): Quaternion {
		return Quaternion.IDENTITY
	}

	fun getIdentityAdjustedRotation(): Quaternion {
		return Quaternion.IDENTITY
	}

	fun getRawRotation(): Quaternion {
		return rotation
	}

	fun setRotation(rotation: Quaternion) {
		this.rotation = rotation
	}

	val tps: Float
		get() = timer.averageFPS

	fun tick() {}

	fun dataTick() = timer.update()

	fun setFiltering() {}

	fun resetFull() {}

	fun resetYaw() {}

	fun resetMounting() {}

	fun clearDriftCompensation() {}
}
