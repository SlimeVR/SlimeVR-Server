package dev.slimevr.oscquery

import com.appstractive.dnssd.DiscoveryEvent
import com.appstractive.dnssd.discoverServices
import dev.slimevr.AppLogger
import dev.slimevr.util.safeLaunch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect

private const val OSC_JSON_SERVICE_TYPE = "_oscjson._tcp"

class OscQueryDiscovery(
	private val serviceType: String = OSC_JSON_SERVICE_TYPE,
	private val serviceFilter: (String) -> Boolean = { true },
	private val discoverServicesFlow: (String) -> Flow<DiscoveryEvent> = ::discoverServices,
) {
	private val mutableServices = MutableStateFlow<List<OscQueryService>>(emptyList())
	val services: StateFlow<List<OscQueryService>> = mutableServices.asStateFlow()

	private var job: Job? = null

	fun start(scope: CoroutineScope) {
		if (job != null) return
		job = scope.safeLaunch {
			discoverServicesFlow(serviceType).collect { event ->
				when (event) {
					is DiscoveryEvent.Discovered -> {
						if (!serviceFilter(event.service.name)) return@collect
						event.resolve()
					}

					is DiscoveryEvent.Resolved -> onResolved(event)

					is DiscoveryEvent.Removed -> onRemoved(event.service.name)
				}
			}
		}
	}

	suspend fun close() {
		job?.cancelAndJoin()
		job = null
		mutableServices.value = emptyList()
	}

	private fun onRemoved(name: String) {
		if (!serviceFilter(name)) return
		mutableServices.value = mutableServices.value.filterNot { entry -> entry.name == name }
	}

	private suspend fun onResolved(event: DiscoveryEvent.Resolved) {
		val service = event.service
		val name = service.name
		if (!serviceFilter(name)) return

		val resolved = OscQueryService(
			name = name,
			addresses = service.addresses,
			host = service.host,
			type = service.type,
			port = service.port,
			txt = service.txt.mapValues { (_, value) -> value?.decodeToString().orEmpty() },
		)
		AppLogger.oscQuery.info(
			"OSCQuery discovered service: name=$name address=${service.addresses.firstOrNull().orEmpty()} port=${service.port}",
		)
		mutableServices.value = mutableServices.value
			.filterNot { existing -> existing.name == name }
			.plus(resolved)
			.sortedBy { entry -> entry.name }
	}
}
