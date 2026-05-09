package dev.slimevr.oscquery

import dev.slimevr.AppLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.Collections
import javax.jmdns.JmDNS
import javax.jmdns.ServiceEvent
import javax.jmdns.ServiceListener

private const val OSC_JSON_SERVICE_TYPE = "_oscjson._tcp.local."

/**
 * Listens for OSCQuery (`_oscjson._tcp` by default) services on the local
 * network and exposes the resolved set as a [StateFlow].
 *
 * Binds one JmDNS instance per IPv4 site-local interface — `JmDNS.create()`
 * with no arguments only listens on a single auto-picked NIC, which on
 * multi-NIC hosts (Wi-Fi + Ethernet, virtual adapters) frequently misses the
 * LAN the headset is actually on.
 */
class OscQueryDiscovery(
	private val serviceType: String = OSC_JSON_SERVICE_TYPE,
	private val serviceFilter: (String) -> Boolean = { true },
) {
	private val mutableServices = MutableStateFlow<List<OscQueryService>>(emptyList())
	val services: StateFlow<List<OscQueryService>> = mutableServices.asStateFlow()

	private var job: Job? = null

	fun start(scope: CoroutineScope) {
		if (job != null) return
		job = scope.launch(Dispatchers.IO) {
			val instances = mutableListOf<JmDNS>()
			try {
				val addresses = enumerateBindAddresses()
				if (addresses.isEmpty()) {
					AppLogger.oscQuery.warn("OSCQuery discovery: no usable interface addresses found, falling back to default JmDNS bind")
				}

				for (address in addresses.ifEmpty { listOf<InetAddress?>(null) }) {
					val instance = try {
						JmDNS.create(address, null)
					} catch (e: Exception) {
						AppLogger.oscQuery.error("OSCQuery discovery: failed to create JmDNS on $address", e)
						continue
					}
					instance.addServiceListener(serviceType, listener(this, instance))
					instances.add(instance)
				}

				awaitCancellation()
			} finally {
				withContext(NonCancellable) {
					for (instance in instances) {
						try {
							instance.close()
						} catch (e: Exception) {
							AppLogger.oscQuery.error("OSCQuery discovery: failed to close JmDNS instance", e)
						}
					}
					mutableServices.value = emptyList()
				}
			}
		}
	}

	fun close() {
		job?.cancel()
		job = null
	}

	// JmDNS callbacks fire on its own threads and can't be suspend. The shims
	// hand work back to the coroutine scope so log ordering and state mutations
	// stay sequential.
	private fun listener(scope: CoroutineScope, instance: JmDNS) = object : ServiceListener {
		override fun serviceAdded(event: ServiceEvent) {
			instance.requestServiceInfo(event.type, event.name)
		}

		override fun serviceRemoved(event: ServiceEvent) {
			scope.launch { onRemoved(event.name) }
		}

		override fun serviceResolved(event: ServiceEvent) {
			scope.launch { onResolved(event) }
		}
	}

	private fun onRemoved(name: String) {
		if (!serviceFilter(name)) return
		mutableServices.value = mutableServices.value.filterNot { entry -> entry.name == name }
	}

	private suspend fun onResolved(event: ServiceEvent) {
		val name = event.name
		if (!serviceFilter(name)) return
		val info = event.info ?: return

		val addresses = info.inet4Addresses?.map { addr -> addr.hostAddress }?.toList().orEmpty()
		if (addresses.isEmpty()) {
			// This JmDNS instance only resolved IPv6 entries — another instance on
			// a different interface will have the IPv4 view.
			return
		}

		val resolved = OscQueryService(
			name = name,
			addresses = addresses,
			host = info.inetAddresses?.firstOrNull()?.canonicalHostName.orEmpty(),
			type = event.type,
			port = info.port,
			txt = buildMap {
				info.propertyNames?.iterator()?.forEach { key ->
					val value = info.getPropertyString(key)
					put(key.toString(), value.orEmpty())
				}
			},
		)
		AppLogger.oscQuery.info("OSCQuery discovered service: name=$name address=${addresses.first()} port=${info.port}")
		mutableServices.value = mutableServices.value
			.filterNot { existing -> existing.name == name }
			.plus(resolved)
			.sortedBy { entry -> entry.name }
	}
}

private fun enumerateBindAddresses(): List<InetAddress> = try {
	Collections.list(NetworkInterface.getNetworkInterfaces())
		.asSequence()
		.filter { iface -> iface.isUp && !iface.isLoopback && !iface.isVirtual }
		.flatMap { iface -> Collections.list(iface.inetAddresses).asSequence() }
		.filterIsInstance<Inet4Address>()
		.filter { address -> !address.isLoopbackAddress && address.isSiteLocalAddress }
		.distinct()
		.toList()
} catch (_: Exception) {
	emptyList()
}
