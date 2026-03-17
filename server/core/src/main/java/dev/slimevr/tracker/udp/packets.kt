package dev.slimevr.tracker.udp

import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import io.ktor.utils.io.core.remaining
import kotlinx.io.*
import kotlin.reflect.KClass

// ── Enums ───────────────────────────────────────────────────────────────────

enum class PacketType(val id: Int) {
	HEARTBEAT(0), ROTATION(1), HANDSHAKE(3), ACCEL(4), PING_PONG(10),
	SERIAL(11), BATTERY_LEVEL(12), TAP(13), ERROR(14), SENSOR_INFO(15),
	ROTATION_2(16), ROTATION_DATA(17), MAGNETOMETER_ACCURACY(18),
	SIGNAL_STRENGTH(19), TEMPERATURE(20), USER_ACTION(21), PROTOCOL_CHANGE(200);

	companion object {
		private val map = entries.associateBy { it.id }
		fun fromId(id: Int) = map[id]
	}
}

enum class UserAction(val id: Int) {
	RESET_FULL(2), RESET_YAW(3), RESET_MOUNTING(4), PAUSE_TRACKING(5);
	companion object {
		private val map = entries.associateBy { it.id }
		fun fromId(id: Int) = map[id]
	}
}

sealed interface Packet
sealed interface SensorSpecificPacket : Packet { val sensorId: Int }
sealed interface RotationPacket : SensorSpecificPacket { val rotation: Quaternion }

data object Heartbeat : Packet
data class Handshake(val board: Int = 0, val imu: Int = 0, val mcu: Int = 0, val pVer: Int = 0, val firmware: String? = null, val mac: String? = null) : Packet
data class Rotation(override val sensorId: Int = 0, override val rotation: Quaternion = Quaternion.IDENTITY) : RotationPacket
data class Accel(val acceleration: Vector3 = Vector3.NULL, override val sensorId: Int = 0) : SensorSpecificPacket
data class PingPong(val pingId: Int = 0) : Packet
data class Serial(val serial: String = "") : Packet
data class BatteryLevel(val voltage: Float = 0f, val level: Float = 0f) : Packet
data class Tap(override val sensorId: Int = 0, val tap: Int = 0) : SensorSpecificPacket
data class Error(override val sensorId: Int = 0, val errorNumber: Int = 0) : SensorSpecificPacket
data class SensorInfo(override val sensorId: Int = 0, val status: Int = 0, val type: Int = 0) : SensorSpecificPacket
data class Rotation2(override val sensorId: Int = 1, override val rotation: Quaternion = Quaternion.IDENTITY) : RotationPacket
data class RotationData(override val sensorId: Int = 0, val dataType: Int = 0, val rotation: Quaternion = Quaternion.IDENTITY, val calibration: Int = 0) : SensorSpecificPacket
data class MagnetometerAccuracy(override val sensorId: Int = 0, val accuracy: Float = 0f) : SensorSpecificPacket
data class SignalStrength(override val sensorId: Int = 0, val signal: Int = 0) : SensorSpecificPacket
data class Temperature(override val sensorId: Int = 0, val temp: Float = 0f) : SensorSpecificPacket
data class UserActionPacket(val action: UserAction? = null) : Packet
data class ProtocolChange(val targetProtocol: Int = 0, val targetVersion: Int = 0) : Packet

private fun Source.readU8() = readByte().toInt() and 0xFF
private fun Sink.writeU8(v: Int) = writeByte(v.toByte())

private fun Source.readSafeFloat() = readFloat().let { if (it.isNaN()) 0f else it }
private fun Source.readSafeQuat() = Quaternion(readFloat(), readFloat(), readFloat(), readFloat()).let {
	if (it.x.isNaN() || (it.x == 0f && it.y == 0f && it.z == 0f && it.w == 0f)) Quaternion.IDENTITY else it
}

private fun Sink.writeQuat(q: Quaternion) {
	writeFloat(q.x); writeFloat(q.y); writeFloat(q.z); writeFloat(q.w)
}

private fun Source.readStr(len: Int) = buildString {
	repeat(len) { readByte().toInt().takeIf { it != 0 }?.let { append(it.toChar()) } }
}

object PacketCodec {
	fun read(type: PacketType, src: Source): Packet = when (type) {
		PacketType.HEARTBEAT -> Heartbeat
		PacketType.HANDSHAKE -> with(src) {
			if (exhausted()) return Handshake()
			val b = if (remaining >= 4) readInt() else 0
			val i = if (remaining >= 4) readInt() else 0
			val m = if (remaining >= 4) readInt() else 0
			if (remaining >= 12) skip(12)
			val p = if (remaining >= 4) readInt() else 0
			val f = if (remaining >= 1) readStr(readByte().toInt()) else null
			val mac = if (remaining >= 6) readByteArray(6).joinToString(":") { "%02X".format(it) }.takeIf { it != "00:00:00:00:00:00" } else null
			Handshake(b, i, m, p, f, mac)
		}
		PacketType.ROTATION -> Rotation(rotation = src.readSafeQuat())
		PacketType.ACCEL -> Accel(Vector3(src.readSafeFloat(), src.readSafeFloat(), src.readSafeFloat()), if (!src.exhausted()) src.readU8() else 0)
		PacketType.PING_PONG -> PingPong(src.readInt())
		PacketType.SERIAL -> Serial(src.readStr(src.readInt()))
		PacketType.BATTERY_LEVEL -> src.readSafeFloat().let { f ->
			if (src.remaining >= 4) BatteryLevel(f, src.readSafeFloat()) else BatteryLevel(0f, f)
		}
		PacketType.TAP -> Tap(src.readU8(), src.readU8())
		PacketType.ERROR -> Error(src.readU8(), src.readU8())
		PacketType.SENSOR_INFO -> SensorInfo(src.readU8(), src.readU8(), if (!src.exhausted()) src.readU8() else 0)
		PacketType.ROTATION_2 -> Rotation2(rotation = src.readSafeQuat())
		PacketType.ROTATION_DATA -> RotationData(src.readU8(), src.readU8(), src.readSafeQuat(), src.readU8())
		PacketType.MAGNETOMETER_ACCURACY -> MagnetometerAccuracy(src.readU8(), src.readSafeFloat())
		PacketType.SIGNAL_STRENGTH -> SignalStrength(src.readU8(), src.readByte().toInt())
		PacketType.TEMPERATURE -> Temperature(src.readU8(), src.readSafeFloat())
		PacketType.USER_ACTION -> UserActionPacket(UserAction.fromId(src.readU8()))
		PacketType.PROTOCOL_CHANGE -> ProtocolChange(src.readU8(), src.readU8())
	}

	fun write(dst: Sink, packet: Packet) = when (packet) {
		is Heartbeat -> {}
		is Handshake -> {
			dst.writeU8(PacketType.HANDSHAKE.id)
			dst.write("Hey OVR =D 5".toByteArray(Charsets.US_ASCII))
		}
		is Rotation -> dst.writeQuat(packet.rotation)
		is Accel -> { dst.writeFloat(packet.acceleration.x); dst.writeFloat(packet.acceleration.y); dst.writeFloat(packet.acceleration.z) }
		is PingPong -> dst.writeInt(packet.pingId)
		is Serial -> { dst.writeInt(packet.serial.length); packet.serial.forEach { dst.writeU8(it.code) } }
		is BatteryLevel -> { dst.writeFloat(packet.voltage); dst.writeFloat(packet.level) }
		is Tap -> { dst.writeU8(packet.sensorId); dst.writeU8(packet.tap) }
		is Error -> { dst.writeU8(packet.sensorId); dst.writeU8(packet.errorNumber) }
		is SensorInfo -> { dst.writeU8(packet.sensorId); dst.writeU8(packet.status); dst.writeU8(packet.type) }
		is Rotation2 -> dst.writeQuat(packet.rotation)
		is RotationData -> { dst.writeU8(packet.sensorId); dst.writeU8(packet.dataType); dst.writeQuat(packet.rotation); dst.writeU8(packet.calibration) }
		is MagnetometerAccuracy -> { dst.writeU8(packet.sensorId); dst.writeFloat(packet.accuracy) }
		is SignalStrength -> { dst.writeU8(packet.sensorId); dst.writeByte(packet.signal.toByte()) }
		is Temperature -> { dst.writeU8(packet.sensorId); dst.writeFloat(packet.temp) }
		is UserActionPacket -> dst.writeU8(packet.action?.id ?: 0)
		is ProtocolChange -> { dst.writeU8(packet.targetProtocol); dst.writeU8(packet.targetVersion) }
	}
}

// ── Entry Points ────────────────────────────────────────────────────────────

fun readPacket(src: Source): Packet? {
	if (src.exhausted()) return null
	val type = PacketType.fromId(src.readInt()) ?: return null
	if (type != PacketType.HANDSHAKE) src.skip(8) // Skip sequence number
	return PacketCodec.read(type, src)
}

fun writePacket(dst: Sink, packet: Packet) {
	val type = when(packet) {
		is Heartbeat -> PacketType.HEARTBEAT
		is Handshake -> PacketType.HANDSHAKE
		is Rotation -> PacketType.ROTATION
		is Accel -> PacketType.ACCEL
		is PingPong -> PacketType.PING_PONG
		is Serial -> PacketType.SERIAL
		is BatteryLevel -> PacketType.BATTERY_LEVEL
		is Tap -> PacketType.TAP
		is Error -> PacketType.ERROR
		is SensorInfo -> PacketType.SENSOR_INFO
		is Rotation2 -> PacketType.ROTATION_2
		is RotationData -> PacketType.ROTATION_DATA
		is MagnetometerAccuracy -> PacketType.MAGNETOMETER_ACCURACY
		is SignalStrength -> PacketType.SIGNAL_STRENGTH
		is Temperature -> PacketType.TEMPERATURE
		is UserActionPacket -> PacketType.USER_ACTION
		is ProtocolChange -> PacketType.PROTOCOL_CHANGE
	}

	if (type != PacketType.HANDSHAKE) {
		dst.writeInt(type.id)
		dst.writeLong(type.id.toLong()) // Sequence number placeholder
	}
	PacketCodec.write(dst, packet)
}


class PacketDispatcher {
	val listeners = mutableMapOf<KClass<out Packet>, MutableList<(Packet) -> Unit>>()

	/**
	 * Listen for a specific packet type.
	 * Usage: dispatcher.on<Rotation> { packet -> println(packet.rotation) }
	 */
	inline fun <reified T : Packet> on(crossinline callback: (T) -> Unit) {
		val list = listeners.getOrPut(T::class) { mutableListOf() }
		synchronized(list) {
			list.add { callback(it as T) }
		}
	}

	/**
	 * Broadcasts a packet to all registered listeners for its type.
	 */
	fun emit(packet: Packet) {
		val list = listeners[packet::class] ?: return
		synchronized(list) {
			list.forEach { it(packet) }
		}
	}
}
