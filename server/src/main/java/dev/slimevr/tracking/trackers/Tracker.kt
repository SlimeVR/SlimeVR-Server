package dev.slimevr.tracking.trackers

import com.jme3.math.Vector3f
import io.eiren.util.BufferedTimer
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3

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
	var status = TrackerStatus.DISCONNECTED
	var position = Vector3.NULL
	var rotation = Quaternion.IDENTITY
	var rawRotation = Quaternion.IDENTITY
	var IdentityAdjustedRotation = Quaternion.IDENTITY
	var acceleration = Vector3f()
	var batteryVoltage = 0f
	var batteryLevel = 0f
	var ping = -1
	var signalStrength = -1
	var temperature = 0f
	var displayName: String? = null
	var customName: String? = null
	var allowDriftCompensation = false
	var mountingOrientation: Quaternion? = null

	val tps: Float
		get() = timer.averageFPS

	fun readConfig() {}

	fun writeConfig() {}

	fun tick() {}

	fun dataTick() = timer.update()

	fun setFiltering() {}

	fun resetFull() {}

	fun resetYaw() {}

	fun resetMounting() {}

	fun clearDriftCompensation() {}
}
