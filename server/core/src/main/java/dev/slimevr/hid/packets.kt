package dev.slimevr.hid

import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Quaternion.Companion.fromRotationVector
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.datatypes.hardware_info.BoardType
import solarxr_protocol.datatypes.hardware_info.ImuType
import solarxr_protocol.datatypes.hardware_info.McuType
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private const val HID_PACKET_SIZE = 16

private val AXES_OFFSET = fromRotationVector(-PI.toFloat() / 2f, 0f, 0f)

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

private fun readLE16Signed(data: ByteArray, offset: Int): Int = data[offset + 1].toInt() shl 8 or data[offset].toUByte().toInt()

private fun decodeQ15Quat(data: ByteArray, offset: Int): Quaternion {
	val scale = 1f / 32768f
	val x = readLE16Signed(data, offset).toShort().toFloat() * scale
	val y = readLE16Signed(data, offset + 2).toShort().toFloat() * scale
	val z = readLE16Signed(data, offset + 4).toShort().toFloat() * scale
	val w = readLE16Signed(data, offset + 6).toShort().toFloat() * scale
	return AXES_OFFSET * Quaternion(w, x, y, z)
}

private fun decodeExpMapQuat(data: ByteArray, offset: Int): Quaternion {
	val buf = ByteBuffer.wrap(data, offset, 4).order(ByteOrder.LITTLE_ENDIAN).int.toUInt()
	val vx = ((buf and 1023u).toFloat() / 1024f) * 2f - 1f
	val vy = ((buf shr 10 and 2047u).toFloat() / 2048f) * 2f - 1f
	val vz = ((buf shr 21 and 2047u).toFloat() / 2048f) * 2f - 1f
	val d = vx * vx + vy * vy + vz * vz
	val invSqrtD = 1f / sqrt(d + 1e-6f)
	val a = (PI.toFloat() / 2f) * d * invSqrtD
	val s = sin(a)
	val k = s * invSqrtD
	return AXES_OFFSET * Quaternion(cos(a), k * vx, k * vy, k * vz)
}

private fun decodeAccel(data: ByteArray, offset: Int): Vector3 {
	val scale = 1f / 128f
	return Vector3(
		readLE16Signed(data, offset).toShort().toFloat() * scale,
		readLE16Signed(data, offset + 2).toShort().toFloat() * scale,
		readLE16Signed(data, offset + 4).toShort().toFloat() * scale,
	)
}

private fun decodeBattery(raw: Int): Float = if (raw == 128) 1f else (raw and 127).toFloat()

private fun decodeBatteryVoltage(raw: Int): Float = (raw.toFloat() + 245f) / 100f

private fun parseSingleHIDPacket(data: ByteArray, i: Int): HIDPacket? {
	val packetType = data[i].toUByte().toInt()
	val hidId = data[i + 1].toUByte().toInt()

	return when (packetType) {
		255 -> {
			val addr = ByteBuffer.wrap(data, i + 2, 8).order(ByteOrder.LITTLE_ENDIAN).long and 0x0000_FFFF_FFFF_FFFFL
			HIDDeviceRegister(hidId, "%012X".format(addr))
		}

		0 -> {
			val batt = data[i + 2].toUByte().toInt()
			val battV = data[i + 3].toUByte().toInt()
			val brdId = data[i + 5].toUByte().toInt()
			val mcuId = data[i + 6].toUByte().toInt()
			val imuId = data[i + 8].toUByte().toInt()
			val fwDate = data[i + 11].toUByte().toInt() shl 8 or data[i + 10].toUByte().toInt()
			val fwMajor = data[i + 12].toUByte().toInt()
			val fwMinor = data[i + 13].toUByte().toInt()
			val fwPatch = data[i + 14].toUByte().toInt()
			val rssi = data[i + 15].toUByte().toInt()
			val fwYear = 2020 + (fwDate shr 9 and 127)
			val fwMonth = fwDate shr 5 and 15
			val fwDay = fwDate and 31
			HIDDeviceInfo(
				hidId = hidId,
				imuType = ImuType.fromValue(imuId.toUShort()) ?: ImuType.Other,
				boardType = BoardType.fromValue(brdId.toUShort()) ?: BoardType.UNKNOWN,
				mcuType = McuType.fromValue(mcuId.toUShort()) ?: McuType.Other,
				firmware = "%04d-%02d-%02d %d.%d.%d".format(fwYear, fwMonth, fwDay, fwMajor, fwMinor, fwPatch),
				batteryLevel = decodeBattery(batt),
				batteryVoltage = decodeBatteryVoltage(battV),
				rssi = -rssi,
			)
		}

		1 -> HIDRotation(
			hidId = hidId,
			rotation = decodeQ15Quat(data, i + 2),
			acceleration = decodeAccel(data, i + 10),
		)

		2 -> HIDRotationBattery(
			hidId = hidId,
			rotation = decodeExpMapQuat(data, i + 5),
			acceleration = decodeAccel(data, i + 9),
			batteryLevel = decodeBattery(data[i + 2].toUByte().toInt()),
			batteryVoltage = decodeBatteryVoltage(data[i + 3].toUByte().toInt()),
			rssi = -data[i + 15].toUByte().toInt(),
		)

		3 -> HIDStatus(
			hidId = hidId,
			status = TrackerStatus.fromValue((data[i + 2].toUByte().toInt() + 1).toUByte()) ?: TrackerStatus.OK,
			rssi = -data[i + 15].toUByte().toInt(),
		)

		4 -> {
			val scaleMag = 1000f / 1024f
			HIDRotationMag(
				hidId = hidId,
				rotation = decodeQ15Quat(data, i + 2),
				magnetometer = Vector3(
					readLE16Signed(data, i + 10).toShort().toFloat() * scaleMag,
					readLE16Signed(data, i + 12).toShort().toFloat() * scaleMag,
					readLE16Signed(data, i + 14).toShort().toFloat() * scaleMag,
				),
			)
		}

		7 -> HIDRotationButton(
			hidId = hidId,
			button = data[i + 2].toUByte().toInt(),
			rotation = decodeExpMapQuat(data, i + 5),
			acceleration = decodeAccel(data, i + 9),
			rssi = -data[i + 15].toUByte().toInt(),
		)

		else -> null
	}
}

fun parseHIDPackets(data: ByteArray): List<HIDPacket> {
	if (data.size % HID_PACKET_SIZE != 0) return emptyList()
	return (0 until data.size / HID_PACKET_SIZE).mapNotNull { parseSingleHIDPacket(data, it * HID_PACKET_SIZE) }
}
