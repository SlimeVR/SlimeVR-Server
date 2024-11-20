package dev.slimevr.osc

import OSCQueryNode
import OSCQueryServer
import ServiceInfo
import dev.slimevr.protocol.rpc.setup.RPCUtil
import io.eiren.util.logging.LogManager
import randomFreePort
import java.io.IOException
import kotlin.concurrent.thread

private const val serviceStartsWith = "VRChat-Client"
private const val queryPath = "/tracking/vrsystem"

/**
 * Handler for OSCQuery for VRChat using our library
 * https://github.com/SlimeVR/oscquery-kt
 */
class VRCOSCQueryHandler(
	private val vrcOscHandler: VRCOSCHandler,
) {
	private val oscQueryServer: OSCQueryServer

	init {
		// Request data
		val localIp = RPCUtil.getLocalIp() ?: throw IllegalStateException("No local IP address found for OSCQuery to bind to")
		val httpPort = randomFreePort()
		oscQueryServer = OSCQueryServer(
			"SlimeVR-Server-$httpPort",
			OscTransport.UDP,
			localIp,
			vrcOscHandler.portIn.toUShort(),
			httpPort,
		)
		oscQueryServer.rootNode.addNode(OSCQueryNode(queryPath))
		oscQueryServer.init()
		LogManager.info("[VRCOSCQueryHandler] SlimeVR OSCQueryServer started at http://$localIp:$httpPort")

		try {
			// Add service listener
			LogManager.info("[VRCOSCQueryHandler] Listening for VRChat OSCQuery")
			oscQueryServer.service.addServiceListener(
				"_osc._udp.local.",
				onServiceAdded = ::serviceAdded,
			)
		} catch (e: IOException) {
			LogManager.warning("[VRCOSCQueryHandler] " + e.message)
		}
	}

	/**
	 * Updates the OSC service's port
	 */
	fun updateOSCQuery(port: UShort) {
		if (oscQueryServer.oscPort != port) {
			thread(start = true) {
				oscQueryServer.updateOscService(port)
			}
		}
	}

	/**
	 * Called when a service is added
	 */
	private fun serviceAdded(info: ServiceInfo) {
		// Check the service name
		if (!info.name.startsWith(serviceStartsWith)) return

		// Get url from ServiceInfo
		val ip = info.inetAddresses[0].hostAddress
		val port = info.port

		// create a new OSCHandler for this service
		vrcOscHandler.addOSCQuerySender(port, ip)
	}

	/**
	 * Closes the OSCQueryServer and the associated OSC sender.
	 */
	fun close() {
		vrcOscHandler.closeOscQuerySender(false)
		thread(start = true) {
			oscQueryServer.close()
		}
	}
}
