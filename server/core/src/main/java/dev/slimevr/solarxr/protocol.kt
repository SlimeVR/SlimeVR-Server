package dev.slimevr.solarxr

import dev.slimevr.fbscodegen.runtime.JvmFlatBufferReader
import java.nio.ByteBuffer

const val SOLARXR_PROTOCOL_VERSION: UInt = 2u
const val SOLARXR_MAX_FRAME_SIZE = 256 * 1024

/** Checks framing before generated code reads a FlatBuffer root. */
fun checkedSolarXRFrame(bytes: ByteBuffer, identifier: String): JvmFlatBufferReader {
	require(bytes.remaining() in 8..SOLARXR_MAX_FRAME_SIZE) { "Invalid SolarXR frame length" }
	val position = bytes.position()
	require((0 until 4).all { bytes.get(position + 4 + it).toInt().toChar() == identifier[it] }) {
		"Unexpected SolarXR file identifier"
	}
	return JvmFlatBufferReader(bytes)
}
