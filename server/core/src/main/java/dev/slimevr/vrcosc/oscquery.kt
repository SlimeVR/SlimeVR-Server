package dev.slimevr.vrcosc

import dev.slimevr.config.DEFAULT_VRC_OSC_PORT_OUT
import dev.slimevr.config.Settings
import dev.slimevr.logging.AppLogger
import dev.slimevr.mdns.MdnsAdvertise
import dev.slimevr.mdns.MdnsBrowse
import dev.slimevr.oscquery.OscQueryAccess
import dev.slimevr.oscquery.OscQueryNode
import dev.slimevr.oscquery.OscQueryServer
import dev.slimevr.oscquery.fetchOscQueryOscPort
import dev.slimevr.util.formatExceptionMessage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import solarxr_protocol.rpc.VRCOSCOscQueryState

private const val VRCHAT_SERVICE_PREFIX = "VRChat-Client"
private const val OSC_JSON_SERVICE_TYPE = "_oscjson._tcp"

class VRCOSCOscQueryBehaviour(
	private val settings: Settings,
	private val localIp: StateFlow<String>,
	private val browse: MdnsBrowse,
	private val advertise: MdnsAdvertise,
) : VRCOSCBehaviour {
	private class OscQueryRuntime {
		var server: OscQueryServer? = null
		var advertiseJob: Job? = null
		var discoveryJob: Job? = null
	}

	override fun observe(receiver: VRCOSCManager) {
		val runtime = OscQueryRuntime()

		val settingsFlow = settings.context.state
			.map { Pair(it.data.vrcOscConfig.enabled, it.data.vrcOscConfig.useManualNetwork) }
			.distinctUntilChanged()
		val portInFlow = receiver.context.state
			.map { it.status.inputPort }
			.distinctUntilChanged()
		combine(
			settingsFlow,
			portInFlow,
			::Pair,
		)
			.onEach { (settings, portIn) ->
				val (enabled, useManualNetwork) = settings
				if (!enabled || portIn == null) {
					stopOscQuery(receiver, runtime)
					return@onEach
				}

				syncOscQueryServer(receiver, runtime, portIn)
				if (useManualNetwork) {
					stopDiscovery(receiver, runtime)
				} else {
					startDiscovery(receiver, runtime)
				}
			}.launchIn(receiver.context.scope)
	}

	private suspend fun stopDiscovery(receiver: VRCOSCManager, runtime: OscQueryRuntime) {
		runtime.discoveryJob?.cancelAndJoin()
		runtime.discoveryJob = null
		val server = runtime.server ?: return
		receiver.context.dispatchAll(
			listOf(
				VRCOSCActions.SetDiscoveredTargets(emptyList()),
				VRCOSCActions.SetOscQuery(
					state = VRCOSCOscQueryState.SEARCHING,
					advertisedPort = server.oscQueryPort.toInt(),
				),
			),
		)
	}

	private suspend fun stopOscQuery(receiver: VRCOSCManager, runtime: OscQueryRuntime) {
		runtime.discoveryJob?.cancelAndJoin()
		runtime.discoveryJob = null
		runtime.advertiseJob?.cancelAndJoin()
		runtime.advertiseJob = null
		try {
			runtime.server?.close()
		} catch (e: Exception) {
			AppLogger.vrc.error(e, "Failed to stop VRChat OSCQuery")
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
					localAddress = localIp,
					oscPort = portIn.toUShort(),
					advertise = advertise,
				)
				arrayOf(HEAD_POSE_SUBPATH, LEFT_WRIST_POSE_SUBPATH, RIGHT_WRIST_POSE_SUBPATH).forEach {
					newServer.addNode(OscQueryNode("$TRACKING_VRSYSTEM_PATH$it", type = "ffffff", access = OscQueryAccess.WRITE))
				}
				withContext(Dispatchers.IO) { newServer.start(receiver.context.scope) }
				runtime.server = newServer
				runtime.advertiseJob = receiver.context.scope.launch {
					try {
						newServer.advertise()
					} catch (e: CancellationException) {
						throw e
					} catch (e: Exception) {
						dispatchOscQueryError(
							receiver = receiver,
							message = "Failed to advertise VRChat OSCQuery",
							throwable = e,
							advertisedPort = newServer.oscQueryPort.toInt(),
						)
					}
				}
				AppLogger.vrc.info("VRChat OSCQuery started on http://${localIp.value}:${newServer.oscQueryPort}")
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

		existing.updateOscPort(portIn.toUShort())
	}

	private fun startDiscovery(receiver: VRCOSCManager, runtime: OscQueryRuntime) {
		if (runtime.discoveryJob != null) return

		runtime.discoveryJob = browse(OSC_JSON_SERVICE_TYPE)
			.map { services -> services.filter { it.name.startsWith(VRCHAT_SERVICE_PREFIX) && it.addresses.isNotEmpty() } }
			.distinctUntilChanged()
			.onEach { services ->
				AppLogger.oscQuery.info("Discovered VRChat clients: ${services.map { "${it.name} ${it.addresses}:${it.port}" }}")
				val targets = services.map { service ->
					val address = service.addresses.first()
					val portOut = try {
						fetchOscQueryOscPort(address, service.port).also { port ->
							AppLogger.oscQuery.info("HOST_INFO of ${service.name} at $address:${service.port}: OSC port $port")
						}
					} catch (e: CancellationException) {
						throw e
					} catch (e: Exception) {
						AppLogger.oscQuery.warn(e, "Could not read HOST_INFO of ${service.name} at $address:${service.port}, assuming OSC port $DEFAULT_VRC_OSC_PORT_OUT")
						DEFAULT_VRC_OSC_PORT_OUT
					}
					VRCOSCDiscoveredTargetInfo(
						name = service.name,
						address = address,
						portOut = portOut,
					)
				}
				receiver.context.dispatchAll(
					listOf(
						VRCOSCActions.SetDiscoveredTargets(targets),
						VRCOSCActions.SetOscQuery(
							state = if (targets.isEmpty()) VRCOSCOscQueryState.SEARCHING else VRCOSCOscQueryState.FOUND,
							advertisedPort = runtime.server?.oscQueryPort?.toInt(),
						),
					),
				)
			}
			.catch { e ->
				dispatchOscQueryError(
					receiver = receiver,
					message = "VRChat OSCQuery discovery failed",
					throwable = e,
					advertisedPort = runtime.server?.oscQueryPort?.toInt(),
				)
			}
			.launchIn(receiver.context.scope)
	}

	private suspend fun dispatchOscQueryError(
		receiver: VRCOSCManager,
		message: String,
		throwable: Throwable,
		advertisedPort: Int? = null,
	) {
		AppLogger.vrc.error(throwable, message)
		receiver.context.dispatch(
			VRCOSCActions.SetOscQuery(
				state = VRCOSCOscQueryState.ERROR,
				advertisedPort = advertisedPort,
				error = formatExceptionMessage(message, throwable),
			),
		)
	}
}
