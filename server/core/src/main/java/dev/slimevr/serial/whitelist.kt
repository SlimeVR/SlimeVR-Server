package dev.slimevr.serial

import dev.slimevr.hid.isCompatibleHidDevice

private val SUPPORTED_BOARDS: Set<Pair<Int, Int>> = setOf(
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

fun isKnownSerialBoard(vid: Int, pid: Int) = SUPPORTED_BOARDS.contains(vid to pid) || isCompatibleHidDevice(vid, pid)
