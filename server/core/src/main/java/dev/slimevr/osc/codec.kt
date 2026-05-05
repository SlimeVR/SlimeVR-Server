package dev.slimevr.osc

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

private const val BUNDLE_PREFIX = "#bundle "

fun encodeMessage(msg: OscMessage): ByteArray {
	val buf = ByteBuffer.allocate(65536) // max UDP packet
	buf.order(java.nio.ByteOrder.BIG_ENDIAN)

	// Address + null terminator
	val addressBytes = msg.address.toByteArray(StandardCharsets.US_ASCII)
	buf.put(addressBytes)
	buf.put(0.toByte()) // null terminator
	val addressPadded = padToMultiple(addressBytes.size + 1, 4)
	buf.put(ByteArray(addressPadded - addressBytes.size - 1))

	// Type tag string
	val typeTag = "," + msg.args.joinToString("") { it.typeTag.toString() }
	val typeTagBytes = typeTag.toByteArray(StandardCharsets.US_ASCII)
	buf.put(typeTagBytes)
	buf.put(0.toByte())
	val typeTagPadded = padToMultiple(typeTagBytes.size + 1, 4)
	buf.put(ByteArray(typeTagPadded - typeTagBytes.size - 1))

	// Arguments
	for (arg in msg.args) {
		when (arg) {
			is OscArg.Int -> buf.putInt(arg.value)

			is OscArg.Long -> buf.putLong(arg.value)

			is OscArg.Float -> buf.putFloat(arg.value)

			is OscArg.Double -> buf.putDouble(arg.value)

			is OscArg.String -> {
				val strBytes = arg.value.toByteArray(StandardCharsets.US_ASCII)
				buf.put(strBytes)
				buf.put(0.toByte())
				val strPadded = padToMultiple(strBytes.size + 1, 4)
				buf.put(ByteArray(strPadded - strBytes.size - 1))
			}

			is OscArg.Blob -> {
				buf.putInt(arg.value.size)
				buf.put(arg.value)
				val blobPadded = padToMultiple(arg.value.size, 4)
				buf.put(ByteArray(blobPadded - arg.value.size))
			}

			else -> {} // Impulse, Null, True, False have no arguments
		}
	}

	return buf.array().sliceArray(0 until buf.position())
}

fun decodeMessage(bytes: ByteArray, offset: Int = 0): Pair<OscMessage, Int> {
	var pos = offset
	val buf = ByteBuffer.wrap(bytes, offset, bytes.size - offset)
	buf.order(java.nio.ByteOrder.BIG_ENDIAN)

	// Parse address
	val addressStart = pos
	while (pos < bytes.size && bytes[pos] != 0.toByte()) pos++
	val address = bytes.sliceArray(addressStart until pos).toString(StandardCharsets.US_ASCII)
	pos++ // skip null terminator
	pos = alignToMultiple(pos, 4)

	// Parse type tag
	val typeTagStart = pos
	while (pos < bytes.size && bytes[pos] != 0.toByte()) pos++
	val typeTag = bytes.sliceArray(typeTagStart until pos).toString(StandardCharsets.US_ASCII)
	pos++ // skip null terminator
	pos = alignToMultiple(pos, 4)

	// Parse arguments
	val args = mutableListOf<OscArg>()
	if (typeTag.startsWith(",")) {
		buf.position(pos)
		for (i in 1 until typeTag.length) {
			when (typeTag[i]) {
				'i' -> args.add(OscArg.Int(buf.int))

				'h' -> args.add(OscArg.Long(buf.long))

				'f' -> args.add(OscArg.Float(buf.float))

				'd' -> args.add(OscArg.Double(buf.double))

				's' -> {
					val strStart = buf.position()
					while (buf.position() < bytes.size && bytes[buf.position()] != 0.toByte()) {
						buf.position(buf.position() + 1)
					}
					val str = bytes.sliceArray(strStart until buf.position()).toString(StandardCharsets.US_ASCII)
					args.add(OscArg.String(str))
					buf.position(buf.position() + 1) // skip null
					val strPadding = padToMultiple(str.length + 1, 4)
					buf.position(buf.position() + strPadding - str.length - 1)
				}

				'b' -> {
					val size = buf.int
					val blob = ByteArray(size)
					buf.get(blob)
					args.add(OscArg.Blob(blob))
					val blobPadding = padToMultiple(size, 4)
					buf.position(buf.position() + blobPadding - size)
				}

				'I' -> args.add(OscArg.Impulse)

				'N' -> args.add(OscArg.Null)

				'T' -> args.add(OscArg.True)

				'F' -> args.add(OscArg.False)
			}
		}
		pos = offset + buf.position()
	}

	return OscMessage(address, args) to pos
}

fun encodeBundle(bundle: OscBundle): ByteArray {
	val buf = ByteBuffer.allocate(65536)
	buf.order(java.nio.ByteOrder.BIG_ENDIAN)

	// Bundle prefix
	val prefixBytes = BUNDLE_PREFIX.toByteArray(StandardCharsets.US_ASCII)
	buf.put(prefixBytes)

	// Timetag (8 bytes, NTP format)
	buf.putLong(bundle.timetag)

	// Contents
	for (content in bundle.contents) {
		val contentBytes = when (content) {
			is OscContent.Message -> encodeMessage(content.msg)
			is OscContent.Bundle -> encodeBundle(content.bundle)
		}
		buf.putInt(contentBytes.size)
		buf.put(contentBytes)
	}

	return buf.array().sliceArray(0 until buf.position())
}

fun decodeBundle(bytes: ByteArray, offset: Int = 0): Pair<OscBundle, Int> {
	val prefixBytes = BUNDLE_PREFIX.toByteArray(StandardCharsets.US_ASCII)
	if (!bytes.sliceArray(offset until minOf(offset + 8, bytes.size)).contentEquals(prefixBytes)) {
		throw IllegalArgumentException("Invalid bundle prefix")
	}

	val buf = ByteBuffer.wrap(bytes)
	buf.order(java.nio.ByteOrder.BIG_ENDIAN)

	var pos = offset + 8
	buf.position(pos)
	val timetag = buf.long
	pos += 8

	val contents = mutableListOf<OscContent>()
	while (pos + 4 <= bytes.size) {
		buf.position(pos)
		val elementSize = buf.int
		pos += 4

		if (elementSize <= 0 || pos + elementSize > bytes.size) break

		try {
			if (elementSize >= 8 && bytes.sliceArray(pos until pos + 8).contentEquals(prefixBytes)) {
				val (bundle, _) = decodeBundle(bytes, pos)
				contents.add(OscContent.Bundle(bundle))
			} else {
				val (msg, _) = decodeMessage(bytes, pos)
				contents.add(OscContent.Message(msg))
			}
		} catch (e: Exception) {
			break
		}
		pos += elementSize
	}

	return OscBundle(timetag, contents) to pos
}

private fun padToMultiple(value: Int, alignment: Int): Int {
	val remainder = value % alignment
	return if (remainder == 0) value else value + (alignment - remainder)
}

private fun alignToMultiple(value: Int, alignment: Int): Int = padToMultiple(value, alignment)
