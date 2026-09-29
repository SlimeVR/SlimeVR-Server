package dev.slimevr.sentry

import solarxr_protocol.rpc.ErrorReportingConsent
import kotlin.test.Test
import kotlin.test.assertEquals

class ConsentGateTest {
	private fun gate(capacity: Int = 3): Pair<ConsentGate<Int>, MutableList<Int>> {
		val sent = mutableListOf<Int>()
		return ConsentGate<Int>(capacity) { sent.add(it) } to sent
	}

	@Test
	fun `undecided holds and sends nothing`() {
		val (gate, sent) = gate()
		gate.offer(1)
		gate.offer(2)

		assertEquals(emptyList(), sent)
	}

	@Test
	fun `undecided keeps only the newest items`() {
		val (gate, sent) = gate(capacity = 3)
		(1..5).forEach(gate::offer)
		gate.apply(ErrorReportingConsent.ALLOWED)

		assertEquals(listOf(3, 4, 5), sent)
	}

	@Test
	fun `allowed flushes held items in order then passes through`() {
		val (gate, sent) = gate()
		gate.offer(1)
		gate.offer(2)
		gate.apply(ErrorReportingConsent.ALLOWED)
		gate.offer(3)

		assertEquals(listOf(1, 2, 3), sent)
	}

	@Test
	fun `denied clears held items and drops new ones`() {
		val (gate, sent) = gate()
		gate.offer(1)
		gate.apply(ErrorReportingConsent.DENIED)
		gate.offer(2)

		assertEquals(emptyList(), sent)
	}

	@Test
	fun `allowed then denied stops sending immediately`() {
		val (gate, sent) = gate()
		gate.apply(ErrorReportingConsent.ALLOWED)
		gate.offer(1)
		gate.apply(ErrorReportingConsent.DENIED)
		gate.offer(2)

		assertEquals(listOf(1), sent)
	}

	@Test
	fun `denied then allowed only sends what comes after`() {
		val (gate, sent) = gate()
		gate.apply(ErrorReportingConsent.DENIED)
		gate.offer(1)
		gate.apply(ErrorReportingConsent.ALLOWED)
		gate.offer(2)

		assertEquals(listOf(2), sent)
	}

	@Test
	fun `going back to undecided holds again`() {
		val (gate, sent) = gate()
		gate.apply(ErrorReportingConsent.ALLOWED)
		gate.apply(ErrorReportingConsent.UNDECIDED)
		gate.offer(1)
		assertEquals(emptyList(), sent)

		gate.apply(ErrorReportingConsent.ALLOWED)
		assertEquals(listOf(1), sent)
	}
}
