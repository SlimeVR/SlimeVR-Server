package dev.slimevr.hid

import dev.slimevr.logging.AppLogger

private const val SERVER_TYPE_OFFICIAL = 1
const val HID_ALL_TRACKERS = 255

private fun frame(vararg bytes: Int): ByteArray {
	val out = ByteArray(HID_REPORT_SIZE)
	bytes.forEachIndexed { index, value -> out[index] = value.toByte() }
	return out
}

fun encodeServerHello(): ByteArray = frame(
	0,
	HIDPacketIdV3.SERVER_HELLO.id,
	3,
	SERVER_TYPE_OFFICIAL,
	0,
)

fun encodeClearPairing(trackerId: Int = HID_ALL_TRACKERS): ByteArray = frame(0, HIDPacketIdV3.CLEAR_PAIRING.id, trackerId)

fun encodeSendTrackersList(): ByteArray = frame(0, HIDPacketIdV3.SEND_TRACKERS_LIST.id)

suspend fun sendHidCommand(connection: HidConnection, packet: ByteArray) {
	val written = connection.write(packet)
	if (written < 0) AppLogger.hid.warn("HID command write failed (id ${packet.getOrNull(1)?.toInt()})")
}
