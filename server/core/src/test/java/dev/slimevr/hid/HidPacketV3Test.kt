package dev.slimevr.hid

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/** Writes [id]'s 2-byte header plus [payload] into a fresh buffer slot and returns the bytes. */
private fun packet(seq: Int, id: Int, vararg payload: Int): ByteArray = ByteArray(2 + payload.size).also {
	it[0] = seq.toByte()
	it[1] = id.toByte()
	payload.forEachIndexed { index, value -> it[2 + index] = value.toByte() }
}

private fun bundle(vararg packets: ByteArray): ByteArray {
	val out = ByteArray(HID_REPORT_SIZE)
	var offset = 0
	for (p in packets) {
		p.copyInto(out, offset)
		offset += p.size
	}
	return out
}

/** int16 little-endian pair. */
private fun le16(value: Int) = intArrayOf(value and 0xFF, value shr 8 and 0xFF)

class HidPacketV3Test {
	@Test
	fun `decodes a single rotation packet with its sequence number`() {
		val quat = IntArray(8) { 0 }
		val data = bundle(packet(7, 9, 3, *quat))

		val packets = parseHIDBundleV3(data)

		val rot = assertIs<HIDRotationV3>(packets.single())
		assertEquals(7, rot.seq)
		assertEquals(3, rot.hidId)
		assertEquals(null, rot.sensorId)
	}

	@Test
	fun `decodes acceleration at full precision`() {
		// FIXED_7: raw 128 == 1.0, raw -128 == -1.0, raw 64 == 0.5
		val payload = le16(128) + le16(-128) + le16(64)
		val data = bundle(packet(1, 10, 5, *payload))

		val accel = assertIs<HIDAccelerationV3>(parseHIDBundleV3(data).single())
		assertEquals(1f, accel.acceleration.x)
		assertEquals(-1f, accel.acceleration.y)
		assertEquals(0.5f, accel.acceleration.z)
	}

	@Test
	fun `walks a bundle of mixed packets and tracker ids`() {
		val data = bundle(
			packet(1, 9, 3, *IntArray(8)),
			packet(2, 8, 4, 40), // rssi for a different tracker
			packet(1, 19, 3, 1, 0xFF, 0xFF, 0xFF, 0xFF), // timeout, unknown
		)

		val packets = parseHIDBundleV3(data)

		assertEquals(3, packets.size)
		assertIs<HIDRotationV3>(packets[0])
		val rssi = assertIs<HIDRssi>(packets[1])
		assertEquals(4, rssi.hidId)
		assertEquals(-40, rssi.rssi)
		val timeout = assertIs<HIDTimeout>(packets[2])
		assertEquals(HID_TIME_UNKNOWN, timeout.secondsUntilTimeout)
	}

	@Test
	fun `stops at a terminator id`() {
		val data = bundle(packet(1, 8, 3, 20), packet(0, 0))
		assertEquals(1, parseHIDBundleV3(data).size)
	}

	@Test
	fun `stops at an unknown id`() {
		val data = bundle(packet(1, 8, 3, 20), packet(1, 99, 1, 2, 3))
		assertEquals(1, parseHIDBundleV3(data).size)
	}

	@Test
	fun `stops when a declared length overruns the report`() {
		// One good RSSI packet, then a device-info header (21 bytes) with only 4 bytes left
		val good = packet(1, 8, 3, 20)
		val truncatedHeader = packet(2, HIDPacketIdV3.DEVICE_INFO.id, 9, 9)
		val data = ByteArray(good.size + truncatedHeader.size)
		good.copyInto(data, 0)
		truncatedHeader.copyInto(data, good.size)

		assertEquals(1, parseHIDBundleV3(data, data.size).size)
	}

	@Test
	fun `decodes back to back variable length dongle info packets`() {
		val model = "Butterfly".encodeToByteArray().map { it.toInt() }.toIntArray()
		val maker = "SlimeVR".encodeToByteArray().map { it.toInt() }.toIntArray()
		val data = bundle(
			packet(0, 205, 2, model.size, *model),
			packet(0, 205, 3, maker.size, *maker),
		)

		val packets = parseHIDBundleV3(data)

		assertEquals(2, packets.size)
		assertEquals("Butterfly", assertIs<HIDDongleInfo.Model>(packets[0]).value)
		assertEquals("SlimeVR", assertIs<HIDDongleInfo.Manufacturer>(packets[1]).value)
	}

	@Test
	fun `device state reads battery, temperature and runtime`() {
		val runtime = intArrayOf(0x10, 0x0E, 0x00, 0x00) // 3600 seconds
		val data = bundle(packet(4, 18, 3, 90, 170, 25, 30, 0, *runtime))

		val state = assertIs<HIDDeviceStateV3>(parseHIDBundleV3(data).single())
		assertEquals(0.9f, state.batteryLevel)
		assertEquals(30f, state.sensorTemp)
		assertEquals(3600L, state.runtimeSeconds)
	}

	@Test
	@Suppress("DEPRECATION")
	fun `legacy decoder still parses a register frame`() {
		val frame = ByteArray(16)
		frame[0] = 0xFF.toByte()
		frame[1] = 2
		for (b in 0 until 6) frame[2 + b] = ((0x0102030405L shr (b * 8)) and 0xFF).toByte()

		val register = assertIs<HIDDeviceRegisterLegacy>(parseLegacyHIDPackets(frame).single())
		assertEquals(2, register.hidId)
		assertEquals("000102030405", register.address)
	}
}
