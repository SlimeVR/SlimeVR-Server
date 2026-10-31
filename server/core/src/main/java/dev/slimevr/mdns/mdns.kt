package dev.slimevr.mdns

import dev.slimevr.logging.AppLogger
import dev.slimevr.multicast.MulticastBind
import dev.slimevr.multicast.MulticastSocket
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

private const val MDNS_PORT = 5353
private val MDNS_GROUP = byteArrayOf(224.toByte(), 0, 0, 251.toByte())
private val MAX_QUERY_INTERVAL = 60.seconds
private val REBIND_DELAY = 5.seconds

data class MdnsDiscoveredService(
	val name: String,
	val type: String,
	val host: String,
	val port: Int,
	val addresses: List<String>,
	val txt: Map<String, String>,
)

/** Advertises a [type] (e.g. "_oscjson._tcp") service named [name] until cancelled, then says goodbye. */
typealias MdnsAdvertise = suspend (type: String, name: String, port: Int, txt: Map<String, String>) -> Nothing

/** Services of [type] currently on the network, kept up to date (and queried for) while collected. */
typealias MdnsBrowse = (type: String) -> Flow<List<MdnsDiscoveredService>>

interface Mdns {
	val advertise: MdnsAdvertise
	val browse: MdnsBrowse
}

private typealias RecordCache = Map<Any, Pair<MdnsRecord, TimeSource.Monotonic.ValueTimeMark>>

private fun RecordCache.unexpired() = filterValues { (_, expiry) -> !expiry.hasPassedNow() }

private val ticks = flow {
	while (true) {
		emit(Unit)
		delay(1.seconds)
	}
}

private enum class MdnsReply { SERVICE, HOST }

private class MdnsSocket(private val socket: MulticastSocket, val address: String) {
	suspend fun send(packet: ByteArray) {
		try {
			socket.send(packet)
		} catch (e: CancellationException) {
			throw e
		} catch (e: Exception) {
			AppLogger.mdns.warn(e, "mDNS send failed on $address")
		}
	}
}

/** What to send back for [question] about [service], null when the question is not about it. */
private fun replyFor(question: MdnsQuestion, service: MdnsService): MdnsReply? {
	fun asks(vararg types: MdnsRecordType) = question.type == MdnsRecordType.ANY || question.type in types
	return when {
		question.name.equals(service.type, ignoreCase = true) && asks(MdnsRecordType.PTR) -> MdnsReply.SERVICE
		question.name.equals(service.instance, ignoreCase = true) && asks(MdnsRecordType.SRV, MdnsRecordType.TXT) -> MdnsReply.SERVICE
		question.name.equals(service.host, ignoreCase = true) && asks(MdnsRecordType.A) -> MdnsReply.HOST
		else -> null
	}
}

private fun repliesTo(questions: List<MdnsQuestion>, services: List<MdnsService>): List<ByteArray> = questions
	.flatMap { question -> services.mapNotNull { service -> replyFor(question, service)?.let { service to it } } }
	.toSet()
	.map { (service, reply) ->
		when (reply) {
			MdnsReply.SERVICE -> buildServiceResponse(service)
			MdnsReply.HOST -> buildHostResponse(service)
		}
	}

private val MdnsRecord.cacheKey: Any
	get() = when (this) {
		is MdnsRecord.Service -> "SRV" to name.lowercase()
		is MdnsRecord.Text -> "TXT" to name.lowercase()
		is MdnsRecord.Pointer, is MdnsRecord.Address -> this
	}

private fun servicesOf(records: Collection<MdnsRecord>, type: String): List<MdnsDiscoveredService> {
	val typeName = "$type.local"
	return records.filterIsInstance<MdnsRecord.Pointer>()
		.filter { it.name.equals(typeName, ignoreCase = true) }
		.mapNotNull { pointer ->
			val srv = records.filterIsInstance<MdnsRecord.Service>().firstOrNull { it.name.equals(pointer.target, ignoreCase = true) }
				?: return@mapNotNull null
			MdnsDiscoveredService(
				name = pointer.target.dropLast(typeName.length + 1),
				type = type,
				host = srv.host,
				port = srv.port,
				addresses = records.filterIsInstance<MdnsRecord.Address>().filter { it.name.equals(srv.host, ignoreCase = true) }.map { it.address },
				txt = records.filterIsInstance<MdnsRecord.Text>().firstOrNull { it.name.equals(pointer.target, ignoreCase = true) }?.entries.orEmpty(),
			)
		}
		.distinctBy { it.name }
		.sortedBy { it.name }
}

/** Questions still needed to resolve [type] instances we only know by PTR, or hosts we have no address for. */
private fun missingQuestions(records: Collection<MdnsRecord>, type: String): List<MdnsQuestion> {
	val typeName = "$type.local"
	val services = records.filterIsInstance<MdnsRecord.Service>()
	val pointers = records.filterIsInstance<MdnsRecord.Pointer>().filter { it.name.equals(typeName, ignoreCase = true) }
	val unresolved = pointers
		.filter { pointer -> services.none { it.name.equals(pointer.target, ignoreCase = true) } }
		.map { MdnsQuestion(it.target, MdnsRecordType.SRV) }
	val addressless = services
		.filter { srv -> pointers.any { it.target.equals(srv.name, ignoreCase = true) } }
		.filter { srv -> records.none { it is MdnsRecord.Address && it.name.equals(srv.host, ignoreCase = true) } }
		.map { MdnsQuestion(it.host, MdnsRecordType.A) }
	return unresolved + addressless
}

/**
 * mDNS on the interface owning the latest [localAddress], through one multicast socket opened with
 * [bind] while anything is advertised or browsed. The socket is bound again whenever the address
 * changes, and after [REBIND_DELAY] when binding or listening fails.
 *
 * Responses are DNS-SD style: PTR as the answer, SRV/TXT/A as additional records. VRChat
 * only reads SRV/TXT/A from the additional records and drops responses carrying
 * them as answers
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun createMdns(scope: CoroutineScope, localAddress: Flow<String>, bind: MulticastBind): Mdns {
	val published = MutableStateFlow<List<MdnsService>>(emptyList())
	val cache = MutableStateFlow<RecordCache>(emptyMap())

	fun onPacket(opened: MdnsSocket, packet: ByteArray) {
		val (questions, records) = try {
			parseQueryQuestions(packet) to parseResponseRecords(packet)
		} catch (_: IndexOutOfBoundsException) {
			return // malformed packet from someone else on the network
		}
		if (records.isNotEmpty()) {
			cache.update { current ->
				records.fold(current.unexpired()) { acc, (record, ttl) ->
					if (ttl == Duration.ZERO) acc - record.cacheKey else acc + (record.cacheKey to (record to TimeSource.Monotonic.markNow() + ttl))
				}
			}
		}
		val replies = repliesTo(questions, published.value)
		if (replies.isNotEmpty()) scope.launch { replies.forEach { opened.send(it) } }
	}

	val socket = localAddress.distinctUntilChanged().transformLatest { address ->
		val bound = bind(MDNS_GROUP, MDNS_PORT, address)
		val opened = MdnsSocket(bound, address)
		try {
			coroutineScope {
				launch { bound.listen { packet -> onPacket(opened, packet) } }
				try {
					emit(opened)
					awaitCancellation()
				} finally {
					withContext(NonCancellable) {
						published.value.forEach { opened.send(buildServiceResponse(it, ttl = Duration.ZERO)) }
						bound.close()
					}
				}
			}
		} finally {
			cache.value = emptyMap()
		}
	}.retryWhen { cause, _ ->
		AppLogger.mdns.warn(cause, "mDNS socket failed, binding again in $REBIND_DELAY")
		delay(REBIND_DELAY)
		true
	}.shareIn(scope, SharingStarted.WhileSubscribed(replayExpirationMillis = 0), replay = 1)

	return object : Mdns {
		override val advertise: MdnsAdvertise = { type, name, port, txt ->
			socket.collectLatest { opened ->
				val service = MdnsService(
					type = "$type.local",
					instance = "$name.$type.local",
					host = "$name.local",
					address = opened.address,
					port = port,
					txt = txt,
				)
				published.update { it + service }
				try {
					// Announce twice, one second apart (RFC 6762 8.3)
					opened.send(buildServiceResponse(service))
					delay(1.seconds)
					opened.send(buildServiceResponse(service))
					awaitCancellation()
				} finally {
					withContext(NonCancellable) { opened.send(buildServiceResponse(service, ttl = Duration.ZERO)) }
					published.update { it - service }
				}
			}
			awaitCancellation()
		}

		override val browse: MdnsBrowse = { type ->
			channelFlow {
				socket.collectLatest { opened ->
					coroutineScope {
						launch {
							var interval = 1.seconds
							while (true) {
								opened.send(buildQuery("$type.local", MdnsRecordType.PTR))
								delay(interval)
								interval = (interval * 2).coerceAtMost(MAX_QUERY_INTERVAL)
							}
						}
						val asked = mutableSetOf<MdnsQuestion>()
						combine(cache, ticks) { entries, _ -> entries.unexpired().values.map { (record, _) -> record } }
							.distinctUntilChanged()
							.collect { records ->
								missingQuestions(records, type)
									.filter { asked.add(it) }
									.forEach { opened.send(buildQuery(it.name, requireNotNull(it.type))) }
								send(servicesOf(records, type))
							}
					}
				}
			}.distinctUntilChanged()
		}
	}
}
