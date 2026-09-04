package dev.slimevr.hid

import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.MagnetometerStatus
import solarxr_protocol.datatypes.hardware_info.BoardType
import solarxr_protocol.datatypes.hardware_info.ImuType
import solarxr_protocol.datatypes.hardware_info.McuType
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

/** Sentinel meaning "unknown / not predicted" for the u32 time fields in packets 18 and 19. */
const val HID_TIME_UNKNOWN: Long = 0xFFFFFFFFL

/**
 * v3 packet ids with their on-wire length in bytes (including the 2-byte `seq`/`id` header) and
 * the decoder that turns the bytes into a packet. A length of -1 marks a variable-length packet
 * sized by the walker. `decode` is null for packets the server only sends, and for TBD packets
 * that are listed here just so the walker can step over them.
 */
enum class HIDPacketIdV3(val id: Int, val length: Int, val decode: ((ByteArray, Int) -> HIDPacket?)?) {
	RSSI(8, 4, { d, i -> HIDRssi.decode(d, i) }),
	ROTATION(9, 11, { d, i -> HIDRotationV3.decode(d, i, sensor = false) }),
	ACCELERATION(10, 9, { d, i -> HIDAccelerationV3.decode(d, i, sensor = false) }),
	MAGNETOMETER(11, 9, { d, i -> HIDMagnetometerV3.decode(d, i, sensor = false) }),
	ROTATION_SENSOR(12, 12, { d, i -> HIDRotationV3.decode(d, i, sensor = true) }),
	ACCELERATION_SENSOR(13, 10, { d, i -> HIDAccelerationV3.decode(d, i, sensor = true) }),
	MAGNETOMETER_SENSOR(14, 10, { d, i -> HIDMagnetometerV3.decode(d, i, sensor = true) }),
	DEVICE_INFO(15, 21, { d, i -> HIDDeviceInfoV3.decode(d, i) }),
	CUSTOM_BOARD_ID(16, 16, { d, i -> HIDCustomBoardId.decode(d, i) }),
	SENSOR_INFO(17, 9, { d, i -> HIDSensorInfo.decode(d, i) }),
	DEVICE_STATE(18, 12, { d, i -> HIDDeviceStateV3.decode(d, i) }),
	TIMEOUT(19, 8, { d, i -> HIDTimeout.decode(d, i) }),
	BUTTON(20, 5, { d, i -> HIDButton.decode(d, i) }),
	CONFIG_TRACKER(21, 13, null), // TBD
	SERVER_HELLO(204, 5, null),
	DONGLE_INFO(205, -1, { d, i -> HIDDongleInfo.decode(d, i) }), // length is data[i + 3] + 4
	CLEAR_PAIRING(206, 3, null),
	SEND_TRACKERS_LIST(207, 2, null),
	TRACKERS_LIST(208, 13, { d, i -> HIDTrackerListEntry.decode(d, i) }),
	CONFIG_DONGLE(209, 13, null), // TBD
	;

	companion object {
		private val byId = entries.associateBy { it.id }
		fun fromId(id: Int): HIDPacketIdV3? = byId[id]
	}
}

/**
 * Packet 205: one metadata field the dongle shares, fully decoded by its data type. A `null` from
 * [decode] means an unknown data type or a too-short payload the walker should skip.
 */
sealed interface HIDDongleInfo : HIDSeqPacket {
	/** Data type 1: 8-byte HWID, dongle class/type (unused), hardware type, hardware revision. */
	data class BasicInfo(
		override val seq: Int,
		val hwid: String,
		val hardwareType: Int,
		val hardwareRevision: Int,
	) : HIDDongleInfo

	data class Model(override val seq: Int, val value: String) : HIDDongleInfo
	data class Manufacturer(override val seq: Int, val value: String) : HIDDongleInfo
	data class FirmwareVersion(override val seq: Int, val value: String) : HIDDongleInfo
	data class FirmwareDate(override val seq: Int, val value: String) : HIDDongleInfo
	data class CustomHardwareType(override val seq: Int, val value: String) : HIDDongleInfo

	companion object {
		fun decode(data: ByteArray, i: Int): HIDDongleInfo? {
			val seq = readU8(data, i)
			val payload = data.copyOfRange(i + 4, i + 4 + readU8(data, i + 3))
			return when (readU8(data, i + 2)) {
				1 -> if (payload.size >= 12) BasicInfo(seq, decodeHwid(payload, 0), readU8(payload, 10), readU8(payload, 11)) else null
				2 -> Model(seq, payload.decodeToString())
				3 -> Manufacturer(seq, payload.decodeToString())
				4 -> FirmwareVersion(seq, payload.decodeToString())
				5 -> if (payload.size >= 8) FirmwareDate(seq, formatUnixDate(readLE64Unsigned(payload, 0))) else null
				6 -> CustomHardwareType(seq, payload.decodeToString())
				else -> null
			}
		}
	}
}

/** One entry of the dongle's paired-tracker list (packet 208). `hidId` 255 means unpaired. */
data class HIDTrackerListEntry(
	override val seq: Int,
	override val hidId: Int,
	val hwid: String,
	val sensorCount: Int,
	/** Firmware status byte; the enum it maps to is not defined by the proposal yet. */
	val stateRaw: Int,
) : HIDTrackerPacket, HIDSeqPacket {
	companion object {
		fun decode(data: ByteArray, i: Int) = HIDTrackerListEntry(
			seq = readU8(data, i),
			hidId = readU8(data, i + 2),
			hwid = decodeHwid(data, i + 3),
			sensorCount = readU8(data, i + 11),
			stateRaw = readU8(data, i + 12),
		)
	}
}

data class HIDRssi(override val seq: Int, override val hidId: Int, val rssi: Int) : HIDTrackerPacket, HIDSeqPacket {
	companion object {
		fun decode(data: ByteArray, i: Int) = HIDRssi(seq = readU8(data, i), hidId = readU8(data, i + 2), rssi = -readU8(data, i + 3))
	}
}

data class HIDRotationV3(
	override val seq: Int,
	override val hidId: Int,
	val sensorId: Int?,
	val rotation: Quaternion,
) : HIDTrackerPacket, HIDSeqPacket {
	companion object {
		fun decode(data: ByteArray, i: Int, sensor: Boolean) = HIDRotationV3(
			seq = readU8(data, i),
			hidId = readU8(data, i + 2),
			sensorId = if (sensor) readU8(data, i + 3) else null,
			rotation = decodeQ15Quat(data, if (sensor) i + 4 else i + 3),
		)
	}
}

data class HIDAccelerationV3(
	override val seq: Int,
	override val hidId: Int,
	val sensorId: Int?,
	val acceleration: Vector3,
) : HIDTrackerPacket, HIDSeqPacket {
	companion object {
		fun decode(data: ByteArray, i: Int, sensor: Boolean) = HIDAccelerationV3(
			seq = readU8(data, i),
			hidId = readU8(data, i + 2),
			sensorId = if (sensor) readU8(data, i + 3) else null,
			acceleration = decodeAccel(data, if (sensor) i + 4 else i + 3),
		)
	}
}

data class HIDMagnetometerV3(
	override val seq: Int,
	override val hidId: Int,
	val sensorId: Int?,
	val magnetometer: Vector3,
) : HIDTrackerPacket, HIDSeqPacket {
	companion object {
		fun decode(data: ByteArray, i: Int, sensor: Boolean) = HIDMagnetometerV3(
			seq = readU8(data, i),
			hidId = readU8(data, i + 2),
			sensorId = if (sensor) readU8(data, i + 3) else null,
			magnetometer = decodeMag(data, if (sensor) i + 4 else i + 3),
		)
	}
}

data class HIDDeviceInfoV3(
	override val seq: Int,
	override val hidId: Int,
	val hwid: String,
	val protocolVersion: Int,
	val boardType: BoardType,
	val mcuType: McuType,
	val boardRevision: Int,
	val deviceType: Int,
	val firmwareVersion: String,
	val firmwareDate: String,
) : HIDTrackerPacket, HIDSeqPacket {
	companion object {
		fun decode(data: ByteArray, i: Int) = HIDDeviceInfoV3(
			seq = readU8(data, i),
			hidId = readU8(data, i + 2),
			hwid = decodeHwid(data, i + 3),
			protocolVersion = readU8(data, i + 11),
			boardType = BoardType.fromValue(readU8(data, i + 12).toUShort()) ?: BoardType.UNKNOWN,
			mcuType = McuType.fromValue(readU8(data, i + 13).toUShort()) ?: McuType.UNKNOWN,
			boardRevision = readU8(data, i + 14),
			deviceType = readU8(data, i + 15),
			firmwareDate = formatFwDate(readU8(data, i + 17) shl 8 or readU8(data, i + 16)),
			firmwareVersion = "%d.%d.%d".format(readU8(data, i + 18), readU8(data, i + 19), readU8(data, i + 20)),
		)
	}
}

data class HIDCustomBoardId(override val seq: Int, override val hidId: Int, val boardName: String) : HIDTrackerPacket, HIDSeqPacket {
	companion object {
		fun decode(data: ByteArray, i: Int) = HIDCustomBoardId(readU8(data, i), readU8(data, i + 2), asciiString(data, i + 3, 13))
	}
}

data class HIDSensorInfo(
	override val seq: Int,
	override val hidId: Int,
	val sensorId: Int,
	val imuType: ImuType,
	val magStatus: MagnetometerStatus,
	val sensorState: Int,
	val defaultBodyPosition: Int,
) : HIDTrackerPacket, HIDSeqPacket {
	companion object {
		fun decode(data: ByteArray, i: Int) = HIDSensorInfo(
			seq = readU8(data, i),
			hidId = readU8(data, i + 2),
			sensorId = readU8(data, i + 3),
			imuType = ImuType.fromValue(readU8(data, i + 4).toUShort()) ?: ImuType.UNKNOWN,
			magStatus = MagnetometerStatus.fromValue(readU8(data, i + 5).toUByte()) ?: MagnetometerStatus.NOT_SUPPORTED,
			sensorState = readU8(data, i + 6),
			defaultBodyPosition = readU8(data, i + 7),
		)
	}
}

data class HIDDeviceStateV3(
	override val seq: Int,
	override val hidId: Int,
	val batteryLevel: Float?,
	val batteryVoltage: Float,
	val deviceTemp: Float?,
	val sensorTemp: Float?,
	/** Firmware state byte; the enum it maps to is not defined by the proposal yet. */
	val stateRaw: Int,
	/** Predicted runtime in seconds, or [HID_TIME_UNKNOWN]. */
	val runtimeSeconds: Long,
) : HIDTrackerPacket, HIDSeqPacket {
	companion object {
		fun decode(data: ByteArray, i: Int) = HIDDeviceStateV3(
			seq = readU8(data, i),
			hidId = readU8(data, i + 2),
			batteryLevel = decodeBatteryPercent(readU8(data, i + 3)),
			batteryVoltage = decodeBatteryVoltage(readU8(data, i + 4)),
			deviceTemp = decodeTemp(data, i + 5),
			sensorTemp = decodeTemp(data, i + 6),
			stateRaw = readU8(data, i + 7),
			runtimeSeconds = readLE32Unsigned(data, i + 8).toLong(),
		)
	}
}

data class HIDTimeout(
	override val seq: Int,
	override val hidId: Int,
	val reason: Int,
	/** Seconds until timeout, or [HID_TIME_UNKNOWN]. 0 = timeout activated now. */
	val secondsUntilTimeout: Long,
) : HIDTrackerPacket, HIDSeqPacket {
	companion object {
		fun decode(data: ByteArray, i: Int) = HIDTimeout(
			seq = readU8(data, i),
			hidId = readU8(data, i + 2),
			reason = readU8(data, i + 3),
			secondsUntilTimeout = readLE32Unsigned(data, i + 4).toLong(),
		)
	}
}

data class HIDButton(
	override val seq: Int,
	override val hidId: Int,
	val buttonId: Int,
	val state: Int,
) : HIDTrackerPacket, HIDSeqPacket {
	companion object {
		fun decode(data: ByteArray, i: Int) = HIDButton(
			seq = readU8(data, i),
			hidId = readU8(data, i + 2),
			buttonId = readU8(data, i + 3),
			state = readU8(data, i + 4),
		)
	}
}

/** 8-byte little-endian hardware id, formatted as a 16-hex-digit string. */
fun decodeHwid(data: ByteArray, offset: Int): String = "%016X".format(readLE64Unsigned(data, offset))

/** UNIX seconds → ISO `yyyy-MM-dd`, matching the string shape [formatFwDate] produces. */
private fun formatUnixDate(epochSeconds: Long): String =
	LocalDate.ofInstant(Instant.ofEpochSecond(epochSeconds), ZoneOffset.UTC).toString()

/** int8 celsius (no scale or offset defined by the proposal); raw 0 is treated as "no reading". */
private fun decodeTemp(data: ByteArray, offset: Int): Float? = readI8(data, offset).takeIf { it != 0 }?.toFloat()

/** Battery percentage as a 0..1 fraction; a value over 100 is treated as unknown. */
private fun decodeBatteryPercent(raw: Int): Float? = if (raw > 100) null else raw.toFloat() / 100f

private fun asciiString(data: ByteArray, offset: Int, maxLength: Int): String {
	val end = (offset until offset + maxLength).firstOrNull { data[it].toInt() == 0 } ?: (offset + maxLength)
	return String(data, offset, end - offset, Charsets.US_ASCII)
}

/** Length of the v3 packet starting at [offset], or -1 if it cannot be determined / does not fit. */
private fun v3PacketLength(type: HIDPacketIdV3, data: ByteArray, offset: Int, end: Int): Int {
	if (type != HIDPacketIdV3.DONGLE_INFO) return type.length
	if (offset + 4 > end) return -1
	return readU8(data, offset + 3) + 4
}

/**
 * Protocol v3: a single HID report carrying a bundle of variable-length packets. Reads the
 * 2-byte `seq`/`id` header, sizes the packet by id, decodes it, and advances. Stops at the end
 * of the report, at an unknown or reserved id, or when a packet's declared length overruns.
 */
fun parseHIDBundleV3(data: ByteArray, length: Int = data.size): List<HIDPacket> {
	val end = length.coerceAtMost(data.size)
	val packets = mutableListOf<HIDPacket>()
	var i = 0
	while (i + 2 <= end) {
		val id = readU8(data, i + 1)
		if (id == 0 || id == 255) break
		val type = HIDPacketIdV3.fromId(id) ?: break
		val packetLength = v3PacketLength(type, data, i, end)
		if (packetLength < 2 || i + packetLength > end) break
		type.decode?.invoke(data, i)?.let { packets.add(it) }
		i += packetLength
	}
	return packets
}
