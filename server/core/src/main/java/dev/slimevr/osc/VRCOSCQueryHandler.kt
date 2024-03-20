package dev.slimevr.osc

import OSCQueryNode
import OSCQueryServer
import ServiceInfo
import io.eiren.util.logging.LogManager
import randomFreePort
import java.io.IOException
import java.net.InetAddress
import kotlin.concurrent.thread

private const val serviceStartsWith = "VRChat-Client"
private const val queryPath = "/tracking/vrsystem"

class VRCOSCQueryHandler(
	private val vrcOscHandler: VRCOSCHandler,
) {
	private val oscQueryServer: OSCQueryServer
	private val localIp = InetAddress.getLocalHost().hostAddress
	private val loopbackIp = InetAddress.getLoopbackAddress().hostAddress

	init {
		// Request data
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
		LogManager.debug("[VRCOSCQueryHandler] SlimeVR OSCQueryServer started at http://$localIp:$httpPort")

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
	 * Updates the OSCQuery OSC service
	 */
	fun updateOSCQuery() {
		// TODO add support in the lib
		// oscQueryServer.updateOSCService(vrcOscHandler.portIn.toUShort())
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
