package dev.slimevr.device

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

private const val NOW = 100_000L

private fun sample(time: Long, received: Int, lost: Int) = DevicePacketSample(time = time, received = received, lost = lost)
private fun rssi(time: Long, rssi: Int) = DeviceRssiSample(time = time, rssi = rssi)
private fun run(count: Int, received: Int, lost: Int) = (0 until count).map { index -> sample(NOW - (count - 1 - index), received, lost) }

private fun statsOf(
	packets: List<DevicePacketSample> = emptyList(),
	rssis: List<DeviceRssiSample> = emptyList(),
	windowMs: Long = 1000L,
) = computeWindowedStats(packets, rssis, windowMs, NOW)

class PacketStatsTest {
	@Test
	fun `too few packets means nothing measured`() {
		// 19 arrivals, one under the gate. A tracker that is off must not read as a clean link
		val stats = statsOf(packets = run(19, received = 1, lost = 0))
		assertNull(stats.packetLoss)
		assertNull(stats.packetsReceived)
		assertNull(stats.packetsLost)
	}

	@Test
	fun `loss is the fraction of packets that went missing`() {
		// 100 arrivals, and one of them followed a gap of 25
		val stats = statsOf(packets = run(100, received = 1, lost = 0) + sample(NOW, received = 1, lost = 25))
		assertEquals(25f / 126f, stats.packetLoss)
		assertEquals(101, stats.packetsReceived)
		assertEquals(25, stats.packetsLost)
	}

	@Test
	fun `loss always lands inside zero to one`() {
		val cases = listOf(
			listOf(sample(NOW, received = 200, lost = 1)),
			listOf(sample(NOW, received = 1, lost = 200)),
			listOf(sample(NOW - 5, received = 0, lost = 30), sample(NOW, received = 30, lost = 0)),
			run(50, received = 3, lost = 1),
		)
		for (packets in cases) {
			val loss = statsOf(packets = packets).packetLoss
			assertTrue(loss != null && loss in 0f..1f, "loss out of range: $loss")
		}
	}

	@Test
	fun `a window holding enough packets ignores everything older`() {
		// Heavy loss two seconds back, a clean second since
		val old = (0 until 40).map { sample(NOW - 2039 + it, received = 1, lost = 5) }
		val stats = statsOf(packets = old + run(40, received = 1, lost = 0))
		assertEquals(0f, stats.packetLoss)
		assertEquals(40, stats.packetsReceived)
	}

	@Test
	fun `a thin window reaches back for a slow reporter`() {
		// One batched report a second and a half back, which the window alone would miss
		val stats = statsOf(packets = listOf(sample(NOW - 1500, received = 190, lost = 10)))
		assertEquals(0.05f, stats.packetLoss)
		assertEquals(190, stats.packetsReceived)
	}

	@Test
	fun `the reach back stops at the lookback`() {
		val packets = (0 until 40).map { sample(NOW - 20_000 + it, received = 1, lost = 1) }
		assertNull(statsOf(packets = packets).packetLoss)
	}

	@Test
	fun `the range spans every reading in the window`() {
		val rssis = listOf(rssi(NOW - 2500, -60), rssi(NOW - 2000, -40), rssi(NOW - 500, -50))
		val stats = statsOf(rssis = rssis, windowMs = 3000L)
		assertEquals(-60, stats.rssiMin)
		assertEquals(-40, stats.rssiMax)
	}

	@Test
	fun `the average weighs a reading by how long it stood`() {
		// -50 held for 9 of the 10 seconds, -70 for the last one
		val rssis = listOf(rssi(NOW - 10_000, -50), rssi(NOW - 1000, -70))
		val stats = statsOf(rssis = rssis, windowMs = 10_000L)
		assertEquals(-52, stats.rssiAvg)
		assertEquals(-70, stats.rssiMin)
		assertEquals(-50, stats.rssiMax)
	}

	@Test
	fun `a run of lost datagrams is an outage`() {
		// One datagram per sample, so a run is nearly all of what the sample saw
		assertTrue(isOutage(sample(NOW, received = 1, lost = 3)))
		assertTrue(isOutage(sample(NOW, received = 1, lost = 30)))
		// Under the floor, a share alone would have marked these
		assertFalse(isOutage(sample(NOW, received = 1, lost = 1)))
		assertFalse(isOutage(sample(NOW, received = 1, lost = 2)))
	}

	@Test
	fun `ordinary jitter in a batched report is not an outage`() {
		// A second's worth of packets losing a handful is a healthy link
		assertFalse(isOutage(sample(NOW, received = 197, lost = 3)))
		assertFalse(isOutage(sample(NOW, received = 190, lost = 10)))
		assertFalse(isOutage(sample(NOW, received = 160, lost = 40)))
	}

	@Test
	fun `a batched report losing a quarter of itself is an outage`() {
		assertTrue(isOutage(sample(NOW, received = 150, lost = 50)))
		assertTrue(isOutage(sample(NOW, received = 0, lost = 200)))
	}

	@Test
	fun `a repeated reading is not stored twice`() {
		val history = DevicePacketHistory()
		history.recordRssi(rssi(NOW - 2000, -70))
		history.recordRssi(rssi(NOW - 1500, -70))
		history.recordRssi(rssi(NOW - 1000, -40))
		repeat(30) { index -> history.record(sample(NOW - 29 + index, received = 1, lost = 0)) }

		// Dropping the repeat leaves -70 covering the stretch up to the change, not a slice of it
		val stats = history.statsForWindow(windowMs = 3000L, now = NOW)
		assertEquals(-70, stats.rssiMin)
		assertEquals(-40, stats.rssiMax)
		assertEquals(30, stats.packetsReceived)
	}
}
