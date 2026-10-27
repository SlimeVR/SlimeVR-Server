package dev.slimevr.mdns

import dev.slimevr.multicast.MulticastSocket
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

class MdnsTest {
	private val vrchat = MdnsService(
		type = "_oscjson._tcp.local",
		instance = "VRChat-Client-103C3C._oscjson._tcp.local",
		host = "VRChat-Client-103C3C.oscjson.local",
		address = "192.168.1.242",
		port = 46463,
		txt = mapOf("txtvers" to "1"),
	)

	// VRChat drops responses that carry SRV/TXT/A as answers instead of additional records
	@Test
	fun serviceResponseHasPtrAnswerAndSrvTxtAInAdditionalRecords() {
		val packet = buildServiceResponse(vrchat)

		assertEquals(1, packet.u16(6)) // answers
		assertEquals(3, packet.u16(10)) // additional
		assertEquals(
			listOf(
				ReceivedRecord(MdnsRecord.Pointer(vrchat.type, vrchat.instance), 75.minutes),
				ReceivedRecord(MdnsRecord.Text(vrchat.instance, vrchat.txt), 75.minutes),
				ReceivedRecord(MdnsRecord.Service(vrchat.instance, vrchat.port, vrchat.host), 75.minutes),
				ReceivedRecord(MdnsRecord.Address(vrchat.host, vrchat.address), 2.minutes),
			),
			parseResponseRecords(packet),
		)
	}

	@Test
	fun parsesCompressedNamesAndSkipsUnknownRecordTypes() {
		// Question "_osc._udp" + pointer to "local" in the first question's name
		val query = header(flags = 0, questions = 2) +
			encode("_oscjson._tcp.local") + u16(12) + u16(1) +
			encode("_osc._udp", terminated = false) + u16(0xC000 or (12 + 1 + 8 + 1 + 4)) + u16(255) + u16(0x8001)
		assertEquals(
			listOf(MdnsQuestion("_oscjson._tcp.local", MdnsRecordType.PTR), MdnsQuestion("_osc._udp.local", MdnsRecordType.ANY)),
			parseQueryQuestions(query),
		)

		// AAAA (type 28) is not read, the A record after it is
		val response = header(flags = 0x8400, answers = 2) +
			encode("host.local") + u16(28) + u16(1) + u16(0) + u16(120) + u16(16) + ByteArray(16) +
			encode("host.local") + u16(1) + u16(1) + u16(0) + u16(120) + u16(4) + byteArrayOf(10, 0, 0, 1)
		assertEquals(listOf(ReceivedRecord(MdnsRecord.Address("host.local", "10.0.0.1"), 2.minutes)), parseResponseRecords(response))
	}

	@Test
	fun advertisingAnnouncesTwiceThenSaysGoodbyeAndClosesTheSocket() = runTest {
		val sockets = mutableListOf<FakeSocket>()
		val mdns = createMdns(backgroundScope, "192.168.1.10") { _, _, _ -> FakeSocket().also(sockets::add) }

		val advertising = launch { mdns.advertise("_oscjson._tcp", "SlimeVR-Server-ABC123", 1234, mapOf("txtvers" to "1")) }
		advanceTimeBy(1_500)
		assertEquals(2, sockets.single().sent.size)

		advertising.cancelAndJoin()
		runCurrent()
		assertTrue(parseResponseRecords(sockets.single().sent.last()).all { it.ttl == Duration.ZERO })
		assertTrue(sockets.single().closed)
	}

	@Test
	fun browsingSharesTheSocketAndFindsAnsweringServices() = runTest {
		val sockets = mutableListOf<FakeSocket>()
		val mdns = createMdns(backgroundScope, "192.168.1.10") { _, _, _ -> FakeSocket().also(sockets::add) }

		val advertising = launch { mdns.advertise("_osc._udp", "SlimeVR-Server-ABC123", 9001, emptyMap()) }
		val found = async { mdns.browse("_oscjson._tcp").first { it.isNotEmpty() } }
		runCurrent()
		val socket = sockets.single()
		assertTrue(socket.sent.any { parseQueryQuestions(it) == listOf(MdnsQuestion("_oscjson._tcp.local", MdnsRecordType.PTR)) })

		socket.incoming.send(buildServiceResponse(vrchat))
		assertEquals(
			listOf(MdnsDiscoveredService("VRChat-Client-103C3C", "_oscjson._tcp", vrchat.host, vrchat.port, listOf(vrchat.address), vrchat.txt)),
			found.await(),
		)

		runCurrent()
		assertFalse(socket.closed)
		advertising.cancelAndJoin()
		runCurrent()
		assertTrue(socket.closed)
	}

	@Test
	fun bindFailureReachesTheCaller() = runTest {
		val mdns = createMdns(backgroundScope, "192.168.1.10") { _, _, _ -> error("no interface") }

		assertEquals("no interface", runCatching { mdns.browse("_oscjson._tcp").first() }.exceptionOrNull()?.message)
	}

	private fun header(flags: Int, questions: Int = 0, answers: Int = 0) = u16(0) + u16(flags) + u16(questions) + u16(answers) + u16(0) + u16(0)

	private fun encode(name: String, terminated: Boolean = true): ByteArray = name.split('.').fold(byteArrayOf()) { acc, label ->
		acc + byteArrayOf(label.length.toByte()) + label.encodeToByteArray()
	} + if (terminated) byteArrayOf(0) else byteArrayOf()

	private fun u16(value: Int) = byteArrayOf((value shr 8).toByte(), value.toByte())

	private fun ByteArray.u16(offset: Int) = ((this[offset].toInt() and 0xFF) shl 8) or (this[offset + 1].toInt() and 0xFF)
}

private class FakeSocket : MulticastSocket {
	val sent = mutableListOf<ByteArray>()
	val incoming = Channel<ByteArray>(Channel.UNLIMITED)
	var closed = false

	override suspend fun listen(onPacket: (ByteArray) -> Unit) {
		for (packet in incoming) onPacket(packet)
	}

	override suspend fun send(payload: ByteArray) {
		check(!closed) { "send on closed socket" }
		sent += payload
	}

	override suspend fun close() {
		closed = true
		incoming.close()
	}
}
