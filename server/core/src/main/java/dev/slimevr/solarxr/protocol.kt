package dev.slimevr.solarxr

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.fbscodegen.runtime.JvmFlatBufferReader
import dev.slimevr.fbscodegen.runtime.JvmFlatBufferWriter
import java.nio.ByteBuffer
import solarxr_protocol.MessageBundle

const val SOLARXR_PROTOCOL_VERSION: UInt = 2u
const val SOLARXR_MAX_FRAME_SIZE = 256 * 1024

fun checkedSolarXRFrame(bytes: ByteBuffer): JvmFlatBufferReader {
	require(bytes.remaining() in 8..SOLARXR_MAX_FRAME_SIZE) { "Invalid SolarXR frame length" }
	return JvmFlatBufferReader(bytes)
}

fun writeSolarXRBundle(fbb: FlatBufferBuilder, bundle: MessageBundle) {
	val writer = JvmFlatBufferWriter(fbb)
	writer.finish(bundle.encode(writer))
}
