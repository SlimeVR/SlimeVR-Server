package dev.slimevr.hid

import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.datatypes.hardware_info.BoardType
import solarxr_protocol.datatypes.hardware_info.ImuType
import solarxr_protocol.datatypes.hardware_info.McuType

sealed interface HIDPacket {
	val hidId: Int
}

/** Receiver associates a wireless tracker ID with its 6-byte address (type 255). */
data class HIDDeviceRegister(override val hidId: Int, val address: String) : HIDPacket

/** Board/MCU/firmware/battery + IMU type for tracker registration (type 0). */
data class HIDDeviceInfo(
	override val hidId: Int,
	val imuType: ImuType,
	val boardType: BoardType,
	val mcuType: McuType,
	val firmware: String,
	val batteryLevel: Float,
	val batteryVoltage: Float,
	val rssi: Int,
) : HIDPacket

/** Full-precision Q15 quaternion + Q7 acceleration (type 1). */
data class HIDRotation(
	override val hidId: Int,
	val rotation: Quaternion,
	val acceleration: Vector3,
) : HIDPacket

/** Compact exp-map quaternion + Q7 acceleration + battery level + rssi (type 2). */
data class HIDRotationBattery(
	override val hidId: Int,
	val rotation: Quaternion,
	val acceleration: Vector3,
	val batteryLevel: Float,
	val batteryVoltage: Float,
	val rssi: Int,
) : HIDPacket

/** Tracker status report + rssi (type 3). */
data class HIDStatus(
	override val hidId: Int,
	val status: TrackerStatus,
	val rssi: Int,
	val packetsReceived: Int,
	val packetsLost: Int,
) : HIDPacket

/** Full-precision Q15 quaternion + Q10 magnetometer (type 4). */
data class HIDRotationMag(
	override val hidId: Int,
	val rotation: Quaternion,
	val magnetometer: Vector3,
) : HIDPacket

/** Button state + compact exp-map quaternion + Q7 acceleration + rssi (type 7). */
data class HIDRotationButton(
	override val hidId: Int,
	val button: Int,
	val rotation: Quaternion,
	val acceleration: Vector3,
	val rssi: Int,
) : HIDPacket
