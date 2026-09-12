package dev.slimevr.hid

import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlin.math.PI

/** Marker for anything decoded out of an HID report, legacy or v3. */
sealed interface HIDPacket

/** An HID packet that names a wireless tracker by its per-dongle id. */
sealed interface HIDTrackerPacket : HIDPacket {
	val hidId: Int
}

/** A v3 packet carrying a per-tracker sequence number (0 = ignore). */
sealed interface HIDSeqPacket : HIDPacket {
	val seq: Int
}

/** Largest HID report either protocol produces. */
const val HID_REPORT_SIZE = 64

/** Which protocol a dongle speaks, decided once at connect and kept in its state. */
const val HID_PROTOCOL_LEGACY = 2
const val HID_PROTOCOL_V3 = 3

val AXES_OFFSET: Quaternion = Quaternion.fromRotationVector(-PI.toFloat() / 2f, 0f, 0f)

fun readU8(data: ByteArray, offset: Int): Int = data[offset].toUByte().toInt()

fun readI8(data: ByteArray, offset: Int): Int = data[offset].toInt()

fun readLE16Signed(data: ByteArray, offset: Int): Int = data[offset + 1].toInt() shl 8 or data[offset].toUByte().toInt()

fun readLE32Unsigned(data: ByteArray, offset: Int): UInt = (data[offset].toUInt() and 0xFFu) or
	((data[offset + 1].toUInt() and 0xFFu) shl 8) or
	((data[offset + 2].toUInt() and 0xFFu) shl 16) or
	((data[offset + 3].toUInt() and 0xFFu) shl 24)

fun readLE48Unsigned(data: ByteArray, offset: Int): ULong = (data[offset].toULong() and 0xFFu) or
	((data[offset + 1].toULong() and 0xFFu) shl 8) or
	((data[offset + 2].toULong() and 0xFFu) shl 16) or
	((data[offset + 3].toULong() and 0xFFu) shl 24) or
	((data[offset + 4].toULong() and 0xFFu) shl 32) or
	((data[offset + 5].toULong() and 0xFFu) shl 40)

fun readLE64Unsigned(data: ByteArray, offset: Int): Long = (data[offset].toUByte().toLong()) or
	(data[offset + 1].toUByte().toLong() shl 8) or
	(data[offset + 2].toUByte().toLong() shl 16) or
	(data[offset + 3].toUByte().toLong() shl 24) or
	(data[offset + 4].toUByte().toLong() shl 32) or
	(data[offset + 5].toUByte().toLong() shl 40) or
	(data[offset + 6].toUByte().toLong() shl 48) or
	(data[offset + 7].toUByte().toLong() shl 56)

fun decodeQ15Quat(data: ByteArray, offset: Int): Quaternion {
	val scale = 1f / 32768f
	val x = readLE16Signed(data, offset).toShort().toFloat() * scale
	val y = readLE16Signed(data, offset + 2).toShort().toFloat() * scale
	val z = readLE16Signed(data, offset + 4).toShort().toFloat() * scale
	val w = readLE16Signed(data, offset + 6).toShort().toFloat() * scale
	return AXES_OFFSET * Quaternion(w, x, y, z)
}

fun decodeAccel(data: ByteArray, offset: Int): Vector3 {
	val scale = 1f / 128f
	return Vector3(
		readLE16Signed(data, offset).toShort().toFloat() * scale,
		readLE16Signed(data, offset + 2).toShort().toFloat() * scale,
		readLE16Signed(data, offset + 4).toShort().toFloat() * scale,
	)
}

fun decodeMag(data: ByteArray, offset: Int): Vector3 {
	val scale = 1000f / 1024f
	return Vector3(
		readLE16Signed(data, offset).toShort().toFloat() * scale,
		readLE16Signed(data, offset + 2).toShort().toFloat() * scale,
		readLE16Signed(data, offset + 4).toShort().toFloat() * scale,
	)
}

fun decodeBatteryVoltage(raw: Int): Float = (raw.toFloat() + 245f) / 100f

/** Packed firmware date: bits 15..9 year since 2020, bits 8..5 month, bits 4..0 day. */
fun formatFwDate(fwDate: Int): String {
	val year = 2020 + (fwDate shr 9 and 127)
	val month = fwDate shr 5 and 15
	val day = fwDate and 31
	return "%04d-%02d-%02d".format(year, month, day)
}
