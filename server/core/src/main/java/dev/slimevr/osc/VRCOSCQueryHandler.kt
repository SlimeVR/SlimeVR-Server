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
			httpPort
		)
		oscQueryServer.rootNode.addNode(OSCQueryNode(queryPath))
		oscQueryServer.init()
		LogManager.debug("[VRCOSCQueryHandler] SlimeVR OSCQueryServer started at http://$localIp:$httpPort")

		try {
			// Add service listener
			LogManager.info("[VRCOSCQueryHandler] Listening for VRChat OSCQuery")
			oscQueryServer.service.addServiceListener(
				"_osc._udp.local.",
				onServiceAdded = ::serviceAdded
			)
		} catch (e: IOException) {
			LogManager.warning("[VRCOSCQueryHandler] " + e.message)
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
		val handlerIp = vrcOscHandler.address.hostName
		val handlerPort = vrcOscHandler.portOut
		if (port != handlerPort || (ip != handlerIp && !(ip == localIp && handlerIp == loopbackIp))) {
			vrcOscHandler.addOSCQuerySender(port, ip)
		} else {
			LogManager.debug("[VRCOSCQueryHandler] An OSC Sender already exists for the port $port and address $ip")
		}
	}

	/**
	 * Updates the advertised OSC port of the OSCQueryServer
	 */
	fun updatePortIn(portIn: Int) {
		// TODO
		// oscQueryServer.oscPort = portIn
	}

	/**
	 * Closes the OSCQueryServer and the associated OSC sender.
	 */
	fun close() {
		vrcOscHandler.closeOscQuerySender()
		thread(start = true) {
			oscQueryServer.close()
		}
	}
}
