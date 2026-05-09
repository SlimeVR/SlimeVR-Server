package dev.slimevr.vrcosc

import dev.slimevr.AppLogger
import dev.slimevr.config.DEFAULT_VRC_OSC_PORT_OUT
import dev.slimevr.oscquery.OscQueryAccess
import dev.slimevr.oscquery.OscQueryDiscovery
import dev.slimevr.oscquery.OscQueryNode
import dev.slimevr.oscquery.OscQueryServer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import solarxr_protocol.rpc.VRCOSCOscQueryState

private const val VRCHAT_SERVICE_PREFIX = "VRChat-Client"

class VRCOSCOscQueryBehaviour : VRCOSCBehaviour {
	private class OscQueryRuntime(
		val localIp: String,
	) {
		var server: OscQueryServer? = null
		var discovery: OscQueryDiscovery? = null
		var discoveryJob: Job? = null
	}

	override fun reduce(state: VRCOSCState, action: VRCOSCActions) = when (action) {
		is VRCOSCActions.SetOscQuery -> state.copy(
			status = state.status.copy(
				oscQueryState = action.state,
				oscQueryAdvertisedPort = action.advertisedPort,
				oscQueryError = action.error,
			),
		)

		is VRCOSCActions.SetDiscoveredTargets -> state.copy(
			status = state.status.copy(discoveredTargets = action.targets),
		)

		else -> state
	}

	override fun observe(receiver: VRCOSCManager) {
		val runtime = OscQueryRuntime(resolveLocalIp())

		receiver.context.state
			.map { state -> Triple(state.config.enabled, state.config.manualNetwork == null, vrcOscPortIn(state.config)) }
			.distinctUntilChanged()
			.onEach { (enabled, automatic, portIn) ->
				if (!enabled || !automatic) {
					stopOscQuery(receiver, runtime)
					return@onEach
				}

				syncOscQueryServer(receiver, runtime, portIn)
				startDiscovery(receiver, runtime)
			}.launchIn(receiver.context.scope)
	}

	private fun resolveLocalIp(): String = try {
		val candidates = java.util.Collections.list(java.net.NetworkInterface.getNetworkInterfaces())
			.asSequence()
			.filter { iface -> iface.isUp && !iface.isLoopback && !iface.isVirtual }
			.flatMap { iface -> java.util.Collections.list(iface.inetAddresses).asSequence() }
			.filter { address -> !address.isLoopbackAddress && !address.hostAddress.contains(':') }
			.toList()

		val siteLocal = candidates.firstOrNull { address -> address.isSiteLocalAddress }
		(siteLocal ?: candidates.firstOrNull())?.hostAddress
			?: java.net.InetAddress.getLoopbackAddress().hostAddress
	} catch (_: Exception) {
		java.net.InetAddress.getLoopbackAddress().hostAddress
	}

	private suspend fun stopOscQuery(receiver: VRCOSCManager, runtime: OscQueryRuntime) {
		runtime.discoveryJob?.cancel()
		runtime.discoveryJob = null
		runtime.discovery?.close()
		runtime.discovery = null
		try {
			runtime.server?.close()
		} catch (e: Exception) {
			AppLogger.vrc.error("Failed to stop VRChat OSCQuery", e)
		}
		runtime.server = null

		receiver.context.dispatchAll(
			listOf(
				VRCOSCActions.SetDiscoveredTargets(emptyList()),
				VRCOSCActions.SetOscQuery(state = VRCOSCOscQueryState.DISABLED),
			),
		)
	}

	private suspend fun syncOscQueryServer(receiver: VRCOSCManager, runtime: OscQueryRuntime, portIn: Int) {
		val existing = runtime.server
		if (existing == null) {
			try {
				val newServer = OscQueryServer(
					name = "SlimeVR-Server",
					address = runtime.localIp,
					oscPort = portIn.toUShort(),
				)
				newServer.addNode(OscQueryNode(TRACKING_VRSYSTEM_PATH, access = OscQueryAccess.WRITE))
				withContext(Dispatchers.IO) { newServer.start(receiver.context.scope) }
				runtime.server = newServer
				AppLogger.vrc.info("VRChat OSCQuery started on http://${runtime.localIp}:${newServer.oscQueryPort}")
				receiver.context.dispatch(
					VRCOSCActions.SetOscQuery(
						state = VRCOSCOscQueryState.SEARCHING,
						advertisedPort = newServer.oscQueryPort.toInt(),
					),
				)
			} catch (e: Exception) {
				dispatchOscQueryError(
					receiver = receiver,
					message = "Failed to start VRChat OSCQuery server",
					throwable = e,
				)
				runtime.server = null
			}
			return
		}

		try {
			existing.updateOscPort(portIn.toUShort())
		} catch (e: Exception) {
			dispatchOscQueryError(
				receiver = receiver,
				message = "Failed to update VRChat OSCQuery port",
				throwable = e,
				advertisedPort = existing.oscQueryPort.toInt(),
			)
		}
	}

	private suspend fun startDiscovery(receiver: VRCOSCManager, runtime: OscQueryRuntime) {
		if (runtime.discovery != null) return

		val discovery = OscQueryDiscovery(serviceFilter = { serviceName -> serviceName.startsWith(VRCHAT_SERVICE_PREFIX) })
		runtime.discovery = discovery

		try {
			discovery.start(receiver.context.scope)
		} catch (e: Exception) {
			discovery.close()
			runtime.discovery = null
			dispatchOscQueryError(
				receiver = receiver,
				message = "Failed to start VRChat OSCQuery discovery",
				throwable = e,
				advertisedPort = runtime.server?.oscQueryPort?.toInt(),
			)
			return
		}

		runtime.discoveryJob = discovery.services
			.onEach { services ->
				val targets = services.mapNotNull { service ->
					val address = service.addresses.firstOrNull().orEmpty().ifEmpty { service.host }
					if (address.isBlank()) {
						null
					} else {
						VRCOSCDiscoveredTargetInfo(
							name = service.name,
							address = address,
							portOut = DEFAULT_VRC_OSC_PORT_OUT,
						)
					}
				}
				val advertisedPort = runtime.server?.oscQueryPort?.toInt()
				receiver.context.dispatchAll(
					listOf(
						VRCOSCActions.SetDiscoveredTargets(targets),
						VRCOSCActions.SetOscQuery(
							state = if (targets.isEmpty()) VRCOSCOscQueryState.SEARCHING else VRCOSCOscQueryState.FOUND,
							advertisedPort = advertisedPort,
						),
					),
				)
			}.launchIn(receiver.context.scope)
	}

	private suspend fun dispatchOscQueryError(
		receiver: VRCOSCManager,
		message: String,
		throwable: Throwable,
		advertisedPort: Int? = null,
	) {
		AppLogger.vrc.error(message, throwable)
		receiver.context.dispatch(
			VRCOSCActions.SetOscQuery(
				state = VRCOSCOscQueryState.ERROR,
				advertisedPort = advertisedPort,
				error = formatExceptionMessage(message, throwable),
			),
		)
	}
}
