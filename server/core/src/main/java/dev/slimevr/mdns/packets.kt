package dev.slimevr.mdns

import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

private const val CLASS_IN = 1

private val SERVICE_TTL = 75.minutes
private val HOST_TTL = 2.minutes

internal enum class MdnsRecordType(val code: Int) {
	A(1),
	PTR(12),
	TXT(16),
	SRV(33),
	ANY(255),
	;

	companion object {
		fun fromCode(code: Int) = entries.firstOrNull { it.code == code }
	}
}

/** One of our published services, with fully qualified names ("…local"). */
internal data class MdnsService(
	val type: String,
	val instance: String,
	val host: String,
	val address: String,
	val port: Int,
	val txt: Map<String, String>,
)

/** [type] is null for record types we never answer. */
internal data class MdnsQuestion(val name: String, val type: MdnsRecordType?)

/** The records we read from other responders; any other type is skipped. */
internal sealed interface MdnsRecord {
	val name: String

	data class Pointer(override val name: String, val target: String) : MdnsRecord
	data class Service(override val name: String, val port: Int, val host: String) : MdnsRecord
	data class Text(override val name: String, val entries: Map<String, String>) : MdnsRecord
	data class Address(override val name: String, val address: String) : MdnsRecord
}

/** A TTL of zero is a goodbye: the record must be forgotten. */
internal data class ReceivedRecord(val record: MdnsRecord, val ttl: Duration)

internal fun buildServiceResponse(service: MdnsService, ttl: Duration = SERVICE_TTL): ByteArray = buildPacket(flags = 0x8400, answers = 1, additional = 3) {
	writeRecord(service.type, MdnsRecordType.PTR, ttl, encodeName(service.instance))
	writeRecord(service.instance, MdnsRecordType.TXT, ttl, encodeTxt(service.txt))
	writeRecord(service.instance, MdnsRecordType.SRV, ttl, encodeSrv(service.port, service.host))
	writeRecord(service.host, MdnsRecordType.A, if (ttl == Duration.ZERO) Duration.ZERO else HOST_TTL, encodeIpv4(service.address))
}

internal fun buildHostResponse(service: MdnsService): ByteArray = buildPacket(flags = 0x8400, answers = 1) {
	writeRecord(service.host, MdnsRecordType.A, HOST_TTL, encodeIpv4(service.address))
}

internal fun buildQuery(name: String, type: MdnsRecordType): ByteArray = buildPacket(flags = 0, questions = 1) {
	write(encodeName(name))
	writeShort(type.code.toShort())
	writeShort(CLASS_IN.toShort())
}

private fun buildPacket(flags: Int, questions: Int = 0, answers: Int = 0, additional: Int = 0, body: Buffer.() -> Unit): ByteArray = Buffer().apply {
	writeShort(0) // id
	writeShort(flags.toShort())
	writeShort(questions.toShort())
	writeShort(answers.toShort())
	writeShort(0) // authority
	writeShort(additional.toShort())
	body()
}.readByteArray()

private fun Buffer.writeRecord(name: String, type: MdnsRecordType, ttl: Duration, data: ByteArray) {
	write(encodeName(name))
	writeShort(type.code.toShort())
	writeShort(CLASS_IN.toShort())
	writeInt(ttl.inWholeSeconds.toInt())
	writeShort(data.size.toShort())
	write(data)
}

private fun encodeName(name: String): ByteArray = Buffer().apply {
	name.split('.').filter { it.isNotEmpty() }.forEach { label ->
		val bytes = label.encodeToByteArray()
		writeByte(bytes.size.toByte())
		write(bytes)
	}
	writeByte(0)
}.readByteArray()

private fun encodeTxt(txt: Map<String, String>): ByteArray = Buffer().apply {
	txt.forEach { (key, value) ->
		val bytes = "$key=$value".encodeToByteArray()
		writeByte(bytes.size.toByte())
		write(bytes)
	}
}.readByteArray()

private fun encodeSrv(port: Int, host: String): ByteArray = Buffer().apply {
	writeShort(0) // priority
	writeShort(0) // weight
	writeShort(port.toShort())
	write(encodeName(host))
}.readByteArray()

private fun encodeIpv4(address: String): ByteArray {
	val octets = address.split('.').map { it.toInt() }
	require(octets.size == 4 && octets.all { it in 0..255 }) { "Not an IPv4 address: $address" }
	return ByteArray(4) { octets[it].toByte() }
}

private fun ByteArray.isResponse() = u16(2) and 0x8000 != 0

internal fun parseQueryQuestions(packet: ByteArray): List<MdnsQuestion> {
	if (packet.size < 12 || packet.isResponse()) return emptyList()
	var offset = 12
	return List(packet.u16(4)) {
		val (name, end) = readName(packet, offset)
		offset = end + 4 // type + class
		MdnsQuestion(name, MdnsRecordType.fromCode(packet.u16(end)))
	}
}

internal fun parseResponseRecords(packet: ByteArray): List<ReceivedRecord> {
	if (packet.size < 12 || !packet.isResponse()) return emptyList()
	var offset = 12
	repeat(packet.u16(4)) { offset = readName(packet, offset).second + 4 }
	val count = packet.u16(6) + packet.u16(8) + packet.u16(10)
	return List(count) {
		val (name, end) = readName(packet, offset)
		val type = MdnsRecordType.fromCode(packet.u16(end))
		val ttl = ((packet.u16(end + 4).toLong() shl 16) or packet.u16(end + 6).toLong()).seconds
		val length = packet.u16(end + 8)
		val data = end + 10
		offset = data + length
		if (offset > packet.size) throw IndexOutOfBoundsException("mDNS record past end of packet")
		val record = when (type) {
			MdnsRecordType.PTR -> MdnsRecord.Pointer(name, readName(packet, data).first)
			MdnsRecordType.SRV -> MdnsRecord.Service(name, port = packet.u16(data + 4), host = readName(packet, data + 6).first)
			MdnsRecordType.TXT -> MdnsRecord.Text(name, decodeTxt(packet.copyOfRange(data, offset)))
			MdnsRecordType.A -> if (length == 4) MdnsRecord.Address(name, (0..3).joinToString(".") { (packet[data + it].toInt() and 0xFF).toString() }) else null
			else -> null
		}
		record?.let { ReceivedRecord(it, ttl) }
	}.filterNotNull()
}

private fun decodeTxt(data: ByteArray): Map<String, String> {
	val entries = mutableMapOf<String, String>()
	var offset = 0
	while (offset < data.size) {
		val length = data[offset].toInt() and 0xFF
		val entry = data.copyOfRange(offset + 1, offset + 1 + length).decodeToString()
		if (entry.isNotEmpty()) entries[entry.substringBefore('=')] = entry.substringAfter('=', "")
		offset += 1 + length
	}
	return entries
}

private fun readName(packet: ByteArray, start: Int): Pair<String, Int> {
	val labels = mutableListOf<String>()
	var offset = start
	var end = -1
	var jumps = 0
	while (true) {
		val length = packet[offset].toInt() and 0xFF
		when {
			length == 0 -> {
				if (end < 0) end = offset + 1
				return labels.joinToString(".") to end
			}

			length and 0xC0 == 0xC0 -> {
				if (end < 0) end = offset + 2
				if (++jumps > 16) throw IndexOutOfBoundsException("mDNS name pointer loop")
				offset = ((length and 0x3F) shl 8) or (packet[offset + 1].toInt() and 0xFF)
			}

			else -> {
				labels += packet.copyOfRange(offset + 1, offset + 1 + length).decodeToString()
				offset += 1 + length
			}
		}
	}
}

private fun ByteArray.u16(offset: Int) = ((this[offset].toInt() and 0xFF) shl 8) or (this[offset + 1].toInt() and 0xFF)
