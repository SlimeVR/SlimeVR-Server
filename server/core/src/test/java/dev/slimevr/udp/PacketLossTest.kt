package dev.slimevr.udp

import kotlin.test.Test
import kotlin.test.assertEquals

/** Feeds a sequence through the gap math the way PacketLossBehaviour does, returning total loss */
private fun lossOver(numbers: List<Long>): Int {
	var last: Long? = null
	var lost = 0
	for (num in numbers) {
		lost += packetsLostBetween(last, num)
		last = if (last == null) num else maxOf(last, num)
	}
	return lost
}

class PacketLossTest {
	@Test
	fun `a gap counts the packets that never arrived`() {
		assertEquals(0, packetsLostBetween(last = 10L, num = 11L))
		assertEquals(1, packetsLostBetween(last = 10L, num = 12L))
		assertEquals(9, packetsLostBetween(last = 10L, num = 20L))
	}

	@Test
	fun `gaps across a sequence add up`() {
		// First packet is not a gap, then 5 after 1 misses 3, then 10 after 6 misses 3
		assertEquals(6, lossOver(listOf(1L, 5L, 6L, 10L)))
		assertEquals(0, lossOver((1L..100L).toList()))
	}

	@Test
	fun `a duplicate or a late packet is not loss`() {
		assertEquals(0, packetsLostBetween(last = 10L, num = 10L))
		assertEquals(0, packetsLostBetween(last = 20L, num = 15L))
		// The gap was already charged when 20 arrived, so replaying 15 adds nothing
		assertEquals(9, lossOver(listOf(10L, 20L, 15L, 21L)))
	}

	@Test
	fun `a long dropout reports every packet it missed`() {
		// Six seconds of silence at 100 TPS, counted in full rather than written off
		assertEquals(599, packetsLostBetween(last = 5_000L, num = 5_600L))
	}

	@Test
	fun `a nonsense packet number cannot produce a negative count`() {
		assertEquals(Int.MAX_VALUE, packetsLostBetween(last = 0L, num = Long.MAX_VALUE))
	}
}
