package dev.slimevr.serial

import kotlin.test.Test
import kotlin.test.assertEquals

private fun info(loc: String) = SerialPortInfo(loc, "Fake $loc", 0x1A86, 0x7523)

private val empty = SerialServerState(ports = mapOf(), flashing = setOf())

class SerialServerReducerTest {
	@Test
	fun `PortsChanged replaces the port map`() {
		val before = reduce(empty, SerialServerActions.PortsChanged(mapOf("A" to info("A"))))

		val after = reduce(before, SerialServerActions.PortsChanged(mapOf("B" to info("B"))))

		assertEquals(setOf("B"), after.ports.keys)
	}

	@Test
	fun `PortsChanged keeps a flashing claim for a port that left`() {
		val state = reduce(empty, SerialServerActions.FlashingStarted("A"))

		val after = reduce(state, SerialServerActions.PortsChanged(mapOf("B" to info("B"))))

		assertEquals(setOf("A"), after.flashing)
	}

	@Test
	fun `FlashingStarted and FlashingEnded add and remove the claim`() {
		val started = reduce(empty, SerialServerActions.FlashingStarted("A"))
		assertEquals(setOf("A"), started.flashing)

		assertEquals(emptySet(), reduce(started, SerialServerActions.FlashingEnded("A")).flashing)
		assertEquals(started, reduce(started, SerialServerActions.FlashingEnded("B")))
	}
}
