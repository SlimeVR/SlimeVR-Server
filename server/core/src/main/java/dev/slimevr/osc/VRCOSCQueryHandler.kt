package dev.slimevr.osc

import OSCQueryNode
import OSCQueryServer
import OSCQueryService
import ServiceInfo
import dev.slimevr.protocol.rpc.setup.RPCUtil
import io.eiren.util.collections.FastList
import io.eiren.util.logging.LogManager
import randomFreePort
import java.io.IOException

private const val serviceStartsWith = "VRChat-Client"
private const val queryPath = "/tracking/vrsystem"

class VRCOSCQueryHandler(
	private val vrcOscHandler: VRCOSCHandler,
) {
	private val service: OSCQueryService = OSCQueryService()
	private val remoteAddresses = FastList<String>()
	private val oscQueryServers = FastList<OSCQueryServer>()

	init {
		try {
			// Add service listener
			LogManager.info("[OSCQueryHandler] Listening for VRChat OSCQuery")
			service.addServiceListener(
				"_osc._udp.local.",
				onServiceResolved = {}, // TODO lib doesn't support it being optional
				onServiceAdded = ::serviceAdded,
				onServiceRemoved = ::serviceRemoved
			)
		} catch (e: IOException) {
			LogManager.warning("[OSCQueryHandler] " + e.message)
		}
	}

	private fun serviceAdded(info: ServiceInfo) {
		// Check the service name
		val serviceName = info.name
		if (!serviceName.startsWith(serviceStartsWith)) {
			LogManager.info("[OSCQueryHandler] Rejected (name must start with \"$serviceStartsWith\"): $serviceName")
			return
		}

		// Get url from ServiceInfo
		val ip = info.inetAddresses[0].hostAddress
		val port = info.port
		val url = "http://$ip:$port"
		if (remoteAddresses.contains(url)) {
			LogManager.info("[OSCQueryHandler] Rejected (already has a matching url): $serviceName")
			return
		}
		remoteAddresses.add(url)

		LogManager.debug("[OSCQueryHandler] Resolved and accepted: $serviceName")
		LogManager.debug("[OSCQueryHandler] $serviceName URL: $url")k

			// create a new VRCOSCHandler for this service
			if (port != vrcOscHandler.portOut || ip != vrcOscHandler.address.hostName) {
				vrcOscHandler.addOSCSender(port, ip)
			} else {
				LogManager.debug("[OSCQueryHandler] An OSC Sender already exists with this address and port!")
			}

		// Request data
		val localIp = RPCUtil.getLocalIp()
		val httpPort = randomFreePort()
		val server = OSCQueryServer(
			"SlimeVR-Server-$localIp",
			OscTransport.UDP,
			vrcOscHandler.portIn.toUShort(),
			localIp,
			httpPort,
			localIp
		)
		val node = OSCQueryNode(queryPath, null, null)
		server.rootNode.addNode(node)
		server.init()
		oscQueryServers.add(server)
		LogManager.debug("[OSCQueryHandler] SlimeVR OSCQueryServer started at http://$localIp:$httpPort")
	}

	private fun serviceRemoved(type: String, name: String) {
		LogManager.debug("Service removed: $name")
	}

	fun close() {
		vrcOscHandler.removeAdditionalOscSenders()
		for (server in oscQueryServers) {
			// TODO close async/in a thread
			// server.close()
		}
	}
}
