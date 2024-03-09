package dev.slimevr.osc

import OSCQueryNode
import OSCQueryServer
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
	private val server: OSCQueryServer
	private val remoteAddresses = FastList<String>()

	init {
		// Request data
		val localIp = RPCUtil.getLocalIp()
		val httpPort = randomFreePort()
		server = OSCQueryServer(
			"SlimeVR-Server-$httpPort",
			OscTransport.UDP,
			localIp,
			vrcOscHandler.portIn.toUShort(),
			httpPort
		)
		server.rootNode.addNode(OSCQueryNode(queryPath))
		server.init()
		LogManager.debug("[OSCQueryHandler] SlimeVR OSCQueryServer started at http://$localIp:$httpPort")

		try {
			// Add service listener
			LogManager.info("[OSCQueryHandler] Listening for VRChat OSCQuery")
			server.service.addServiceListener(
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
		LogManager.debug("[OSCQueryHandler] $serviceName URL: $url")

		// create a new VRCOSCHandler for this service
		if (port != vrcOscHandler.portOut || ip != vrcOscHandler.address.hostName) {
			vrcOscHandler.addOSCSender(port, ip)
		} else {
			LogManager.debug("[OSCQueryHandler] An OSC Sender already exists with this address and port!")
		}
	}

	private fun serviceRemoved(type: String, name: String) {
		LogManager.debug("Service removed: $name")
	}

	fun close() {
		vrcOscHandler.removeAdditionalOscSenders()
		// TODO close async/in a thread
// 		server.close()
	}
}
