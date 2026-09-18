package dev.slimevr.osc

import kotlinx.io.Buffer
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.indexOf
import kotlinx.io.readByteArray
import kotlinx.io.readDouble
import kotlinx.io.readFloat
import kotlinx.io.readString
import kotlinx.io.write
import kotlinx.io.writeDouble
import kotlinx.io.writeFloat

private val BUNDLE_PREFIX = "#bundle".encodeToByteArray() + byteArrayOf(0)

private fun pad4(n: Int) = (n + 3) and 3.inv()

/** OSC aligns strings and blobs to a four-byte boundary with trailing nulls. */
private fun Sink.writePadding(unpadded: Int) = repeat(pad4(unpadded) - unpadded) { writeByte(0) }

private fun Sink.writeOscString(value: String) {
	val bytes = value.encodeToByteArray()
	write(bytes)
	writeByte(0)
	writePadding(bytes.size + 1)
}

private fun Source.readOscString(): String {
	val length = indexOf(0)
	require(length >= 0) { "Unterminated OSC string" }
	val value = readString(length)
	// The null terminator counts towards the alignment it pads out to
	skip(pad4(length.toInt() + 1) - length)
	return value
}

private fun Source.startsWithBundlePrefix(): Boolean {
	val peeked = peek()
	return peeked.request(BUNDLE_PREFIX.size.toLong()) &&
		peeked.readByteArray(BUNDLE_PREFIX.size).contentEquals(BUNDLE_PREFIX)
}

fun writeMessage(dst: Sink, msg: OscMessage) {
	dst.writeOscString(msg.address)
	dst.writeOscString("," + msg.args.joinToString("") { it.typeTag.toString() })
	for (arg in msg.args) {
		when (arg) {
			is OscArg.Int -> dst.writeInt(arg.value)

			is OscArg.Long -> dst.writeLong(arg.value)

			is OscArg.Float -> dst.writeFloat(arg.value)

			is OscArg.Double -> dst.writeDouble(arg.value)

			is OscArg.String -> dst.writeOscString(arg.value)

			is OscArg.Blob -> {
				dst.writeInt(arg.value.size)
				dst.write(arg.value)
				dst.writePadding(arg.value.size)
			}

			else -> {}
		}
	}
}

fun writeBundle(dst: Sink, bundle: OscBundle) {
	dst.write(BUNDLE_PREFIX)
	dst.writeLong(bundle.timetag)
	// Each element is length-prefixed, so it has to be built before its size is known. Handing it over
	// between two Buffers moves segments rather than copying the bytes, and drains it for the next one.
	val element = Buffer()
	for (content in bundle.contents) {
		when (content) {
			is OscContent.Message -> writeMessage(element, content.msg)
			is OscContent.Bundle -> writeBundle(element, content.bundle)
		}
		dst.writeInt(element.size.toInt())
		dst.transferFrom(element)
	}
}

fun readMessage(src: Source): OscMessage {
	val address = src.readOscString()
	val typeTag = src.readOscString()
	val args = mutableListOf<OscArg>()
	if (typeTag.startsWith(",")) {
		for (c in typeTag.drop(1)) {
			when (c) {
				'i' -> args += OscArg.Int(src.readInt())

				'h' -> args += OscArg.Long(src.readLong())

				'f' -> args += OscArg.Float(src.readFloat())

				'd' -> args += OscArg.Double(src.readDouble())

				's' -> args += OscArg.String(src.readOscString())

				'b' -> {
					val size = src.readInt()
					args += OscArg.Blob(src.readByteArray(size))
					src.skip((pad4(size) - size).toLong())
				}

				'I' -> args += OscArg.Impulse

				'N' -> args += OscArg.Null

				'T' -> args += OscArg.True

				'F' -> args += OscArg.False
			}
		}
	}
	return OscMessage(address, args)
}

fun readBundle(src: Source): OscBundle {
	require(src.startsWithBundlePrefix()) { "Invalid bundle prefix" }
	src.skip(BUNDLE_PREFIX.size.toLong())
	val timetag = src.readLong()
	val contents = mutableListOf<OscContent>()
	while (src.request(4)) {
		val size = src.readInt()
		if (size <= 0 || !src.request(size.toLong())) break
		val element = Buffer()
		src.readTo(element, size.toLong())
		contents += try {
			if (element.startsWithBundlePrefix()) {
				OscContent.Bundle(readBundle(element))
			} else {
				OscContent.Message(readMessage(element))
			}
		} catch (_: Exception) {
			break
		}
	}
	return OscBundle(timetag, contents)
}
