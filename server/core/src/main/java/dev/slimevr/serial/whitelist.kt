package dev.slimevr.serial

import dev.slimevr.hid.isCompatibleHidReceiver
import dev.slimevr.hid.isCompatibleHidTracker
import solarxr_protocol.rpc.SerialDeviceType

private val ESP_BRIDGES: Set<Pair<Int, Int>> = setOf(
	Pair(0x1A86, 0x7522), // CH340
	Pair(0x1A86, 0x7523), // CH340
	Pair(0x1A86, 0x5523), // CH341
	Pair(0x1A86, 0x55D3), // CH343
	Pair(0x1A86, 0x55D4), // CH9102x
	Pair(0x10C4, 0xEA60), // CP210x
	Pair(0x303A, 0x1001), // ESP32-S3
	Pair(0x303A, 0x0002), // ESP32
	Pair(0x0403, 0x6001), // FTDI FT232
)

fun classifyPort(vid: Int, pid: Int): SerialDeviceType = when {
	isCompatibleHidReceiver(vid, pid) -> SerialDeviceType.HID_RECEIVER
	isCompatibleHidTracker(vid, pid) -> SerialDeviceType.HID_TRACKER
	(vid to pid) in ESP_BRIDGES -> SerialDeviceType.ESP_TRACKER
	else -> SerialDeviceType.UNKNOWN
}

/** Recognized devices first, then by location, so the first entry is a stable default pick */
fun sortPorts(ports: Collection<SerialPortInfo>): List<SerialPortInfo> = ports.sortedWith(
	compareBy({ it.type == SerialDeviceType.UNKNOWN }, { it.type.value }, { it.portLocation }),
)
