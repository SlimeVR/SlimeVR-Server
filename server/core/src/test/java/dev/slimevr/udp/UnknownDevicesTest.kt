package dev.slimevr.udp

import dev.slimevr.bindFakeUdp
import dev.slimevr.solarxr.rpc.createUnknownDevice
import dev.slimevr.util.macAddressToULong
import dev.slimevr.util.udpHardwareId
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import solarxr_protocol.datatypes.hardware_info.BoardType
import solarxr_protocol.datatypes.hardware_info.McuType
import solarxr_protocol.rpc.UnknownDeviceStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private const val MAC = "AA:BB:CC:DD:EE:FF"
private const val OTHER_MAC = "11:22:33:44:55:66"

private fun device(mac: String = MAC, lastSeen: Long = 1000L, firmware: String? = "0.6.3") = UnknownDeviceState(
	hardwareId = mac,
	address = "192.168.1.70/54321",
	boardType = BoardType.SLIMEVR,
	mcuType = McuType.ESP32_C3,
	firmwareVersion = firmware,
	lastSeen = lastSeen,
)

private val EMPTY = UdpServer.INITIAL_STATE

class UnknownDevicesTest {
	@Test
	fun `status follows the allowed list, and identity is the mac or the ip without port`() {
		val allowed = createUnknownDevice(device(), setOf(MAC))
		val waiting = createUnknownDevice(device(), setOf(OTHER_MAC))

		assertEquals(UnknownDeviceStatus.ADDING, allowed.status)
		assertEquals(UnknownDeviceStatus.WAITING, waiting.status)
		assertEquals(0xC0A80146u, allowed.ipAddress)
		assertEquals(MAC, udpHardwareId(MAC, "192.168.1.70/54321"))
		assertEquals("192.168.1.70", udpHardwareId(null, "192.168.1.70/54321"))
		assertEquals(0xAABBCCDDEEFFuL, macAddressToULong(MAC))
		assertEquals(null, macAddressToULong("not a mac"))
	}

	@Test
	fun `reducer refreshes, expires stale entries, removes and ignores`() {
		val seen = listOf(device(MAC, lastSeen = 1000L), device(OTHER_MAC, lastSeen = 1000L), device(MAC, lastSeen = 3000L, firmware = "0.6.4"))
			.fold(EMPTY) { s, d -> reduce(s, UdpServerActions.UnknownDeviceSeen(d)) }
		assertEquals(3000L, seen.unknownDevices[MAC]?.lastSeen)
		assertEquals("0.6.4", seen.unknownDevices[MAC]?.firmwareVersion)

		val expired = reduce(seen, UdpServerActions.UnknownDevicesExpired(cutoff = 1000L))
		assertEquals(setOf(MAC), expired.unknownDevices.keys)

		assertTrue(reduce(expired, UdpServerActions.UnknownDeviceRemoved(MAC)).unknownDevices.isEmpty())

		val ignored = reduce(expired, UdpServerActions.IgnoreUnknownDevice(MAC))
		val again = reduce(ignored, UdpServerActions.UnknownDeviceSeen(device(lastSeen = 4000L)))
		assertTrue(again.unknownDevices.isEmpty())
	}
}

@OptIn(ExperimentalCoroutinesApi::class)
class UnknownDeviceExpiryTest {
	@Test
	fun `an entry expires a timeout after its last handshake`() = runTest {
		val server = UdpServer.create(backgroundScope, clock = { testScheduler.currentTime }, bind = ::bindFakeUdp)
		server.context.dispatch(UdpServerActions.UnknownDeviceSeen(device(lastSeen = testScheduler.currentTime)))
		runCurrent()

		advanceTimeBy(3000)
		server.context.dispatch(UdpServerActions.UnknownDeviceSeen(device(lastSeen = testScheduler.currentTime)))
		runCurrent()

		// Due 5s after the refresh at 3s, so still there at 7s and gone by 9s
		advanceTimeBy(UNKNOWN_DEVICE_TIMEOUT_MS - 1000)
		assertTrue(MAC in server.context.state.value.unknownDevices)

		advanceTimeBy(2000)
		assertFalse(MAC in server.context.state.value.unknownDevices)
	}
}
