package dev.slimevr.udp

import dev.slimevr.EventDispatcher
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import io.ktor.utils.io.core.ByteReadPacket
import io.ktor.utils.io.core.remaining
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readByteArray
import kotlinx.io.readFloat
import kotlinx.io.readString
import kotlinx.io.readUByte
import kotlinx.io.readUShort
import kotlinx.io.write
import kotlinx.io.writeUByte
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.datatypes.hardware_info.BoardType
import solarxr_protocol.datatypes.hardware_info.ImuType
import solarxr_protocol.datatypes.hardware_info.McuType
import java.nio.ByteBuffer

enum class ServerFeatureFlags {
	/** Server can parse bundle packets: `PACKET_BUNDLE` = 100 (0x64). */
	PROTOCOL_BUNDLE_SUPPORT,

	/** Server can parse bundle packets with compact headers and packed IMU rotation/acceleration frames:
	- `PACKET_BUNDLE_COMPACT` = 101 (0x65),
	- `PACKET_ROTATION_AND_ACCELERATION` = 23 (0x17). */
	PROTOCOL_BUNDLE_COMPACT_SUPPORT

	;

	companion object {
		val flagsEnabled: Set<ServerFeatureFlags> = setOf(
			PROTOCOL_BUNDLE_SUPPORT,
			PROTOCOL_BUNDLE_COMPACT_SUPPORT
		)

		val packed: ByteArray by lazy {
			val count = entries.size
			val byteLength = if (count == 0) 0 else (count - 1) / 8 + 1
			val tempPacked = ByteArray(byteLength)

			for (flag in flagsEnabled) {
				val bit = flag.ordinal
				val byteIndex = bit / 8
				val bitIndex = bit % 8
				tempPacked[byteIndex] = (tempPacked[byteIndex].toInt() or (1 shl bitIndex)).toByte()
			}
			tempPacked
		}
	}
}

class FirmwareFeatures {
	enum class FirmwareFeatureFlags {
		REMOTE_COMMAND,
		B64_WIFI_SCANNING,
		SENSOR_CONFIG;

		companion object {
			// "Size + 7" ensures that 1-8 flags = 1 byte, 9-16 flags = 2 bytes, etc.
			val byteCount = (entries.size + 7) / 8
		}
	}

	private val flags = ByteArray(FirmwareFeatureFlags.byteCount)

	fun has(flag: FirmwareFeatureFlags): Boolean {
		val bit = flag.ordinal
		val byteIndex = bit / 8
		if (byteIndex >= flags.size) return false

		return (flags[byteIndex].toInt() and (1 shl (bit % 8))) != 0
	}

	companion object {

		fun from(received: ByteBuffer, length: Int): FirmwareFeatures {
			val res = FirmwareFeatures()
			val bytesToRead = res.flags.size.coerceAtMost(length)
			received.get(res.flags, 0, bytesToRead)
			return res
		}
	}
}

private fun Source.readU8(): Int = readByte().toInt() and 0xFF

private fun Source.readSafeFloat(): Float = readFloat().let { if (it.isNaN()) 0f else it }

private fun Source.readSafeQuat(): Quaternion {
	val x = readFloat()
	val y = readFloat()
	val z = readFloat()
	val w = readFloat()
	return if (x.isNaN() || y.isNaN() || z.isNaN() || w.isNaN() || (x == 0f && y == 0f && z == 0f && w == 0f)) {
		Quaternion.IDENTITY
	} else {
		Quaternion(w, x, y, z)
	}
}

enum class PacketType(val id: Int) {
	HEARTBEAT(0),
	ROTATION(1),
	HANDSHAKE(3),
	ACCEL(4),
	PING_PONG(10),
	SERIAL(11),
	BATTERY_LEVEL(12),
	TAP(13),
	ERROR(14),
	SENSOR_INFO(15),
	ROTATION_2(16),
	ROTATION_DATA(17),
	MAGNETOMETER_ACCURACY(18),
	SIGNAL_STRENGTH(19),
	TEMPERATURE(20),
	USER_ACTION(21),
	FEATURE_FLAGS(22),
	ROTATION_AND_ACCEL(23),
	ACK_CONFIG_CHANGE(24),
	SET_CONFIG_FLAG(25),
	FLEX_DATA(26),
	POSITION(27),
	PACKET_BUNDLE(100),
	PACKET_BUNDLE_COMPACT(101),
	PROTOCOL_CHANGE(200),
	;

	companion object {
		private val map = entries.associateBy { it.id }
		fun fromId(id: Int) = map[id]
	}
}

sealed interface UDPPacket {
	fun write(dst: Sink) {}
}

/** Packets that are processed before the handshake is complete */
sealed interface PreHandshakePacket : UDPPacket

sealed interface SensorSpecificPacket : UDPPacket {
	val sensorId: Int
}

data object Heartbeat : UDPPacket

data class Handshake(
	val boardType: BoardType = BoardType.UNKNOWN,
	val imuType: Int = 0,
	val mcuType: McuType = McuType.Other,
	val protocolVersion: Int = 0,
	val firmware: String? = null,
	val macString: String? = null,
) : PreHandshakePacket {
	override fun write(dst: Sink) {
		dst.writeByte(PacketType.HANDSHAKE.id.toByte())
		dst.write("Hey OVR =D 5".toByteArray(Charsets.US_ASCII))
	}

	companion object {
		fun read(src: Source): Handshake = with(src) {
			if (remaining == 0L) return Handshake()
			val b = if (remaining >= 4) BoardType.fromValue(readInt().toUShort()) ?: BoardType.UNKNOWN else BoardType.UNKNOWN
			val i = if (remaining >= 4) readInt() else 0
			val m = if (remaining >= 4) McuType.fromValue(readInt().toUShort()) ?: McuType.Other else McuType.Other
			if (remaining >= 12) {
				readInt()
				readInt()
				readInt()
			}
			val p = if (remaining >= 4) readInt() else 0
			val f = if (remaining >= 1) readString(readByte().toLong()) else null
			val mac = if (remaining >= 6) {
				val bytes = readByteArray(6)
				bytes.joinToString(":") { "%02X".format(it) }.takeIf { it != "00:00:00:00:00:00" }
			} else {
				null
			}
			Handshake(b, i, m, p, f, mac)
		}
	}
}

data class PacketBundle(
	val packets: List<UDPPacket>,
): PreHandshakePacket {
	companion object {
		fun read(src: Source): PacketBundle = with(src) {
			val readPackets = mutableListOf<UDPPacket>()
			while (remaining >= 2) {
				val bundlePacketLen = readUShort().toInt()
				if (bundlePacketLen <= 0) continue

				val rawBytes = readByteArray(bundlePacketLen)
				val subSrc = ByteReadPacket(rawBytes)

				subSrc.use { subSrc ->
					if (subSrc.remaining >= 4) {
						val packetId = subSrc.readInt()
						val type = PacketType.fromId(packetId)

						if (type != null) {
							// 4. Pass the isolated sub-source to your existing parser
							val packetData = readPacket(type, subSrc)
							readPackets.add(packetData)
						}
					}
				}
			}
			return PacketBundle(packets = readPackets)
		}
	}
}

data class PacketBundleCompact(
	val packets: List<UDPPacket>,
): PreHandshakePacket {
	companion object {
		fun read(src: Source): PacketBundle = with(src) {
			val readPackets = mutableListOf<UDPPacket>()
			while (remaining >= 1) {
				val bundlePacketLen = readUByte().toInt()
				if (bundlePacketLen <= 0) continue

				val rawBytes = readByteArray(bundlePacketLen)
				val subSrc = ByteReadPacket(rawBytes)

				subSrc.use { subSrc ->
					if (subSrc.remaining >= 4) {
						val packetId = subSrc.readUByte().toInt()
						val type = PacketType.fromId(packetId)

						if (type != null) {
							// 4. Pass the isolated sub-source to your existing parser
							val packetData = readPacket(type, subSrc)
							readPackets.add(packetData)
						}
					}
				}
			}
			return PacketBundle(packets = readPackets)
		}
	}
}

data class Rotation(override val sensorId: Int = 0, val rotation: Quaternion = Quaternion.IDENTITY) : SensorSpecificPacket {
	companion object {
		fun read(src: Source) = Rotation(0, src.readSafeQuat())
	}
}

data class Accel(val acceleration: Vector3 = Vector3.NULL, override val sensorId: Int = 0) : SensorSpecificPacket {
	companion object {
		fun read(src: Source) = Accel(
			Vector3(src.readSafeFloat(), src.readSafeFloat(), src.readSafeFloat()),
			if (src.remaining > 0) src.readU8() else 0,
		)
	}
}

data class PingPong(val pingId: Int = 0) : PreHandshakePacket {
	override fun write(dst: Sink) {
		dst.writeInt(pingId)
	}
	companion object {
		fun read(src: Source) = PingPong(src.readInt())
	}
}

data class Serial(val serial: String = "") : UDPPacket {
	companion object {
		fun read(src: Source) = Serial(src.readString(src.readInt().toLong()))
	}
}

data class BatteryLevel(val voltage: Float = 0f, val level: Float = 0f) : UDPPacket {
	companion object {
		fun read(src: Source): BatteryLevel {
			val f = src.readSafeFloat()
			return if (src.remaining >= 4) BatteryLevel(f, src.readSafeFloat()) else BatteryLevel(0f, f)
		}
	}
}

data class Tap(override val sensorId: Int = 0, val tap: Int = 0) : SensorSpecificPacket {
	companion object {
		fun read(src: Source) = Tap(src.readU8(), src.readU8())
	}
}

data class ErrorPacket(override val sensorId: Int = 0, val errorNumber: Int = 0) : SensorSpecificPacket {
	companion object {
		fun read(src: Source) = ErrorPacket(src.readU8(), src.readU8())
	}
}

data class SensorInfo(
	override val sensorId: Int = 0,
	val status: TrackerStatus = TrackerStatus.DISCONNECTED,
	val imuType: ImuType = ImuType.Other,
	val sensorConfig: SensorConfig? = null,
	val hasCompletedRestCalibration: Boolean? = null,
	val trackerPosition: Int? = null,
	val trackerDataType: Int? = null,
) : SensorSpecificPacket {
	data class SensorConfig(
		val magEnabled: Boolean,
		val magSupported: Boolean,
		val calibrationEnabled: Boolean,
		val calibrationSupported: Boolean,
		val tempGradientCalibrationEnabled: Boolean,
		val tempGradientCalibrationSupported: Boolean
	) {
		companion object {
			fun fromUDP(raw: UShort) = with(raw) {
				val raw = toInt()

				SensorConfig(
					magEnabled = raw and 1 != 0,
					magSupported = (raw shr 1) and 1 != 0,
					calibrationEnabled = (raw shr 2) and 1 != 0,
					calibrationSupported = (raw shr 3) and 1 != 0,
					tempGradientCalibrationEnabled = (raw shr 4) and 1 != 0,
					tempGradientCalibrationSupported = (raw shr 5) and 1 != 0,
				)
			}
		}
	}

	companion object {
		private fun statusFromUDP(raw: UByte): TrackerStatus = when (raw.toInt()) {
			0 -> TrackerStatus.DISCONNECTED
			1 -> TrackerStatus.OK
			2 -> TrackerStatus.ERROR
			else -> TrackerStatus.DISCONNECTED
		}

		fun read(src: Source) = with(src) {
			val id = readU8()
			val stat = statusFromUDP(readUByte())
			val imu = if (remaining > 0) ImuType.fromValue(readUByte().toUShort()) ?: ImuType.Other else ImuType.Other
			val conf = if (remaining >= 2) SensorConfig.fromUDP(readShort().toUShort()) else null
			val calib = if (remaining > 0) readU8() != 0 else null
			val pos = if (remaining > 0) readU8() else null
			val dt = if (remaining > 0) readU8() else null
			SensorInfo(id, stat, imu, conf, calib, pos, dt)
		}
	}
}

data class Rotation2(override val sensorId: Int = 1, val rotation: Quaternion = Quaternion.IDENTITY) : SensorSpecificPacket {
	companion object {
		fun read(src: Source) = Rotation2(1, src.readSafeQuat())
	}
}

data class RotationData(
	override val sensorId: Int = 0,
	val dataType: Int = 0,
	val rotation: Quaternion = Quaternion.IDENTITY,
	val calibrationInfo: Int = 0,
) : SensorSpecificPacket {
	companion object {
		fun read(src: Source): RotationData = with(src) {
			val id = readU8()
			val type = readU8()
			val rot = readSafeQuat()
			val calib = if (remaining > 0) readU8() else 0
			return RotationData(id, type, rot, calib)
		}
	}
}

data class MagnetometerAccuracy(override val sensorId: Int = 0, val accuracy: Float = 0f) : SensorSpecificPacket {
	companion object {
		fun read(src: Source) = MagnetometerAccuracy(src.readU8(), src.readSafeFloat())
	}
}

data class SignalStrength(override val sensorId: Int = 0, val signal: Int = 0) : SensorSpecificPacket {
	companion object {
		fun read(src: Source): SignalStrength {
			val id = src.readU8()
			val sig = src.readByte().toInt()
			return SignalStrength(id, sig)
		}
	}
}

data class Temperature(override val sensorId: Int = 0, val temp: Float = 0f) : SensorSpecificPacket {
	companion object {
		fun read(src: Source): Temperature {
			val id = src.readU8()
			val t = if (src.remaining >= 4) src.readSafeFloat() else 0f
			return Temperature(id, t)
		}
	}
}

data class UserActionPacket(val type: Int = 0) : UDPPacket {
	companion object {
		fun read(src: Source) = UserActionPacket(src.readU8())
	}
}

data class FeatureFlags(val firmwareFeatures: FirmwareFeatures = FirmwareFeatures()) : UDPPacket {
	override fun write(dst: Sink) {
		dst.write(ServerFeatureFlags.packed)
	}

	companion object {
		fun read(src: Source) = FeatureFlags(
			FirmwareFeatures.from(
				length = src.remaining.toInt(),
				received = ByteBuffer.wrap(src.readByteArray()),
			)
		)
	}
}

data class RotationAndAccel(
	override val sensorId: Int = 0,
	val rotation: Quaternion = Quaternion.IDENTITY,
	val acceleration: Vector3 = Vector3.NULL,
) : SensorSpecificPacket {
	companion object {
		fun read(src: Source): RotationAndAccel {
			val id = src.readU8()
			val scaleR = 1f / 32768f
			val x = src.readShort() * scaleR
			val y = src.readShort() * scaleR
			val z = src.readShort() * scaleR
			val w = src.readShort() * scaleR
			val scaleA = 1f / 128f
			val accel = Vector3(src.readShort() * scaleA, src.readShort() * scaleA, src.readShort() * scaleA)
			return RotationAndAccel(id, Quaternion(w, x, y, z).unit(), accel)
		}
	}
}

data class AckConfigChange(override val sensorId: Int = 0, val configType: UShort = 0u) : SensorSpecificPacket {
	companion object {
		fun read(src: Source) = AckConfigChange(src.readU8(), src.readShort().toUShort())
	}
}

data class SetConfigFlag(override val sensorId: Int = 255, val configType: UShort = 0u, val state: Boolean = false) : SensorSpecificPacket {
	override fun write(dst: Sink) {
		dst.writeUByte(sensorId.toUByte())
		dst.writeShort(configType.toShort())
		dst.writeUByte(if (state) 1u else 0u)
	}
}

data class FlexData(override val sensorId: Int = 0, val flexData: Float = 0f) : SensorSpecificPacket {
	companion object {
		fun read(src: Source) = FlexData(src.readU8(), src.readSafeFloat())
	}
}

data class PositionPacket(override val sensorId: Int = 0, val position: Vector3 = Vector3.NULL) : SensorSpecificPacket {
	companion object {
		fun read(src: Source) = PositionPacket(src.readU8(), Vector3(src.readSafeFloat(), src.readSafeFloat(), src.readSafeFloat()))
	}
}

data class ProtocolChange(val targetProtocol: Int = 0, val targetVersion: Int = 0) : UDPPacket {
	override fun write(dst: Sink) {
		dst.writeUByte(targetProtocol.toUByte())
		dst.writeUByte(targetVersion.toUByte())
	}
	companion object {
		fun read(src: Source) = ProtocolChange(src.readU8(), src.readU8())
	}
}

fun readPacket(type: PacketType, src: Source): UDPPacket = when (type) {
	PacketType.HEARTBEAT -> Heartbeat
	PacketType.HANDSHAKE -> Handshake.read(src)
	PacketType.ROTATION -> Rotation.read(src)
	PacketType.ACCEL -> Accel.read(src)
	PacketType.PING_PONG -> PingPong.read(src)
	PacketType.SERIAL -> Serial.read(src)
	PacketType.BATTERY_LEVEL -> BatteryLevel.read(src)
	PacketType.TAP -> Tap.read(src)
	PacketType.ERROR -> ErrorPacket.read(src)
	PacketType.SENSOR_INFO -> SensorInfo.read(src)
	PacketType.ROTATION_2 -> Rotation2.read(src)
	PacketType.ROTATION_DATA -> RotationData.read(src)
	PacketType.MAGNETOMETER_ACCURACY -> MagnetometerAccuracy.read(src)
	PacketType.SIGNAL_STRENGTH -> SignalStrength.read(src)
	PacketType.TEMPERATURE -> Temperature.read(src)
	PacketType.USER_ACTION -> UserActionPacket.read(src)
	PacketType.FEATURE_FLAGS -> FeatureFlags.read(src)
	PacketType.ROTATION_AND_ACCEL -> RotationAndAccel.read(src)
	PacketType.ACK_CONFIG_CHANGE -> AckConfigChange.read(src)
	PacketType.SET_CONFIG_FLAG -> SetConfigFlag()
	PacketType.FLEX_DATA -> FlexData.read(src)
	PacketType.POSITION -> PositionPacket.read(src)
	PacketType.PACKET_BUNDLE -> PacketBundle.read(src)
	PacketType.PACKET_BUNDLE_COMPACT -> PacketBundleCompact.read(src)
	PacketType.PROTOCOL_CHANGE -> ProtocolChange.read(src)
}

fun writePacket(dst: Sink, packet: UDPPacket) {
	val type = when (packet) {
		is Heartbeat -> PacketType.HEARTBEAT
		is Handshake -> PacketType.HANDSHAKE
		is PingPong -> PacketType.PING_PONG
		is SetConfigFlag -> PacketType.SET_CONFIG_FLAG
		is ProtocolChange -> PacketType.PROTOCOL_CHANGE
		is FeatureFlags -> PacketType.FEATURE_FLAGS
		else -> error("Outbound support not implemented for ${packet::class.simpleName}")
	}

	if (type != PacketType.HANDSHAKE) {
		dst.writeInt(type.id)
		dst.writeLong(0)
	}
	packet.write(dst)
}

data class PacketEvent<out T : UDPPacket>(
	val data: T,
	// Packet number is optional for the inner packets of a bundle packet
	val packetNumber: Long?,
)

typealias UDPPacketDispatcher = EventDispatcher<PacketEvent<UDPPacket>>

@Suppress("UNCHECKED_CAST")
inline fun <reified T : UDPPacket> UDPPacketDispatcher.onPacket(crossinline callback: suspend (PacketEvent<T>) -> Unit) {
	register(T::class) { callback(it as PacketEvent<T>) }
}
