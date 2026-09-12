@file:Suppress("DEPRECATION") // this file is the legacy v2 implementation

package dev.slimevr.hid

import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.MagnetometerStatus
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.datatypes.hardware_info.BoardType
import solarxr_protocol.datatypes.hardware_info.ImuType
import solarxr_protocol.datatypes.hardware_info.McuType
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private const val HID_PACKET_SIZE = 16

private const val LEGACY_DEPRECATION = "Legacy HID protocol v2; new firmware negotiates v3 and sends the bundle packets instead"

enum class HIDPacketIdLegacy(val id: Int, val decode: (ByteArray, Int) -> HIDPacket) {
	DEVICE_INFO(0, { d, i -> HIDDeviceInfoLegacy.decode(d, i) }),
	ROTATION(1, { d, i -> HIDRotationLegacy.decode(d, i) }),
	ROTATION_BATTERY(2, { d, i -> HIDRotationBatteryLegacy.decode(d, i) }),
	STATUS(3, { d, i -> HIDStatusLegacy.decode(d, i) }),
	ROTATION_MAG(4, { d, i -> HIDRotationMagLegacy.decode(d, i) }),
	RUNTIME(5, { d, i -> HIDRuntimeLegacy.decode(d, i) }),
	DATA(6, { d, i -> HIDDataLegacy.decode(d, i) }),
	ROTATION_BUTTON(7, { d, i -> HIDRotationButtonLegacy.decode(d, i) }),
	REGISTER(255, { d, i -> HIDDeviceRegisterLegacy.decode(d, i) }),
	;

	companion object {
		private val byId = entries.associateBy { it.id }
		fun fromId(id: Int): HIDPacketIdLegacy? = byId[id]
	}
}

@Deprecated(LEGACY_DEPRECATION)
data class HIDDeviceRegisterLegacy(override val hidId: Int, val address: String) : HIDTrackerPacket {
	companion object {
		fun decode(data: ByteArray, i: Int) = HIDDeviceRegisterLegacy(
			hidId = readU8(data, i + 1),
			address = "%012X".format(readLE48Unsigned(data, i + 2).toLong()),
		)
	}
}

@Deprecated(LEGACY_DEPRECATION)
data class HIDDeviceInfoLegacy(
	override val hidId: Int,
	val imuType: ImuType,
	val boardType: BoardType,
	val mcuType: McuType,
	val firmwareVersion: String,
	val firmwareDate: String,
	val batteryLevel: Float?,
	val batteryVoltage: Float,
	val rssi: Int,
	val magStatus: MagnetometerStatus,
) : HIDTrackerPacket {
	companion object {
		fun decode(data: ByteArray, i: Int) = HIDDeviceInfoLegacy(
			hidId = readU8(data, i + 1),
			imuType = ImuType.fromValue(readU8(data, i + 8).toUShort()) ?: ImuType.UNKNOWN,
			boardType = BoardType.fromValue(readU8(data, i + 5).toUShort()) ?: BoardType.UNKNOWN,
			mcuType = McuType.fromValue(readU8(data, i + 6).toUShort()) ?: McuType.UNKNOWN,
			firmwareVersion = "%d.%d.%d".format(readU8(data, i + 12), readU8(data, i + 13), readU8(data, i + 14)),
			firmwareDate = formatFwDate(readU8(data, i + 11) shl 8 or readU8(data, i + 10)),
			batteryLevel = decodeBattery(readU8(data, i + 2)),
			batteryVoltage = decodeBatteryVoltage(readU8(data, i + 3)),
			rssi = -readU8(data, i + 15),
			magStatus = MagnetometerStatus.fromValue(readU8(data, i + 9).toUByte()) ?: MagnetometerStatus.NOT_SUPPORTED,
		)
	}
}

@Deprecated(LEGACY_DEPRECATION)
data class HIDRotationLegacy(
	override val hidId: Int,
	val rotation: Quaternion,
	val acceleration: Vector3,
) : HIDTrackerPacket {
	companion object {
		fun decode(data: ByteArray, i: Int) = HIDRotationLegacy(
			hidId = readU8(data, i + 1),
			rotation = decodeQ15Quat(data, i + 2),
			acceleration = decodeAccel(data, i + 10),
		)
	}
}

@Deprecated(LEGACY_DEPRECATION)
data class HIDRotationBatteryLegacy(
	override val hidId: Int,
	val rotation: Quaternion,
	val acceleration: Vector3,
	val batteryLevel: Float?,
	val batteryVoltage: Float,
	val rssi: Int,
) : HIDTrackerPacket {
	companion object {
		fun decode(data: ByteArray, i: Int) = HIDRotationBatteryLegacy(
			hidId = readU8(data, i + 1),
			rotation = decodeExpMapQuat(data, i + 5),
			acceleration = decodeAccel(data, i + 9),
			batteryLevel = decodeBattery(readU8(data, i + 2)),
			batteryVoltage = decodeBatteryVoltage(readU8(data, i + 3)),
			rssi = -readU8(data, i + 15),
		)
	}
}

@Deprecated(LEGACY_DEPRECATION)
data class HIDStatusLegacy(
	override val hidId: Int,
	val status: TrackerStatus,
	val rssi: Int,
	val packetsReceived: Int,
	val packetsLost: Int,
) : HIDTrackerPacket {
	companion object {
		fun decode(data: ByteArray, i: Int) = HIDStatusLegacy(
			hidId = readU8(data, i + 1),
			status = TrackerStatus.fromValue((readU8(data, i + 2) + 1).toUByte()) ?: TrackerStatus.OK,
			rssi = -readU8(data, i + 15),
			packetsReceived = readU8(data, i + 4),
			packetsLost = readU8(data, i + 5),
		)
	}
}

@Deprecated(LEGACY_DEPRECATION)
data class HIDRotationMagLegacy(
	override val hidId: Int,
	val rotation: Quaternion,
	val magnetometer: Vector3,
) : HIDTrackerPacket {
	companion object {
		fun decode(data: ByteArray, i: Int) = HIDRotationMagLegacy(
			hidId = readU8(data, i + 1),
			rotation = decodeQ15Quat(data, i + 2),
			magnetometer = decodeMag(data, i + 10),
		)
	}
}

@Deprecated(LEGACY_DEPRECATION)
data class HIDRuntimeLegacy(override val hidId: Int, val runtime: Long) : HIDTrackerPacket {
	companion object {
		fun decode(data: ByteArray, i: Int) = HIDRuntimeLegacy(hidId = readU8(data, i + 1), runtime = readLE64Unsigned(data, i + 2))
	}
}

@Deprecated(LEGACY_DEPRECATION)
data class HIDDataLegacy(
	override val hidId: Int,
	val button: Int,
	val timeout: Int,
	val rssi: Int,
) : HIDTrackerPacket {
	companion object {
		fun decode(data: ByteArray, i: Int) = HIDDataLegacy(
			hidId = readU8(data, i + 1),
			button = readU8(data, i + 2),
			timeout = readU8(data, i + 3) shl 8 or readU8(data, i + 4),
			rssi = -readU8(data, i + 15),
		)
	}
}

@Deprecated(LEGACY_DEPRECATION)
data class HIDRotationButtonLegacy(
	override val hidId: Int,
	val button: Int,
	val timeout: Int,
	val rotation: Quaternion,
	val acceleration: Vector3,
	val rssi: Int,
) : HIDTrackerPacket {
	companion object {
		fun decode(data: ByteArray, i: Int) = HIDRotationButtonLegacy(
			hidId = readU8(data, i + 1),
			button = readU8(data, i + 2),
			timeout = readU8(data, i + 3) shl 8 or readU8(data, i + 4),
			rotation = decodeExpMapQuat(data, i + 5),
			acceleration = decodeAccel(data, i + 9),
			rssi = -readU8(data, i + 15),
		)
	}
}

private fun decodeExpMapQuat(data: ByteArray, offset: Int): Quaternion {
	val buf = readLE32Unsigned(data, offset)
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

private fun decodeBattery(raw: Int): Float? = if (raw == 128) null else (raw and 127).toFloat() / 100f

fun parseLegacyHIDPackets(data: ByteArray, length: Int = data.size): List<HIDPacket> {
	if (length % HID_PACKET_SIZE != 0) return emptyList()
	return (0 until length / HID_PACKET_SIZE).mapNotNull { frame ->
		val i = frame * HID_PACKET_SIZE
		HIDPacketIdLegacy.fromId(readU8(data, i))?.decode?.invoke(data, i)
	}
}
