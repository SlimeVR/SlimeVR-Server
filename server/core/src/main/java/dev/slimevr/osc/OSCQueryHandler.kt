package dev.slimevr.osc

import OSCQueryNode
import OSCQueryServer
import OSCQueryService
import ServiceInfo
import com.fasterxml.jackson.databind.ObjectMapper
import dev.slimevr.protocol.rpc.setup.RPCUtil
import io.eiren.util.logging.LogManager
import randomFreePort
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class OSCQueryHandler(
	private val oscHandler: OSCHandler,
	private val serviceStartsWith: String,
	private val queryPath: String,
) {
	private val service: OSCQueryService = OSCQueryService()

	init {
		try {
			// Add OSCQuery service listeners for local and non-local
			service.addServiceListener("_oscjson._tcp.local.") { serviceResolved(it) }
			service.addServiceListener("_oscjson._tcp.") { serviceResolved(it) }
			// Request data
			val localIp = RPCUtil.getLocalIp()
			val freePort = randomFreePort()
			val server = OSCQueryServer(
				"SlimeVR-Server-$localIp",
				OscTransport.UDP,
				oscHandler.portIn.toUShort(),
				localIp,
				freePort,
				localIp
			)
			val node = OSCQueryNode(queryPath, null, null)
			server.rootNode.addNode(node)
			server.init()
			LogManager.debug("[OSCQueryHandler] SlimeVR OSCQueryServer started at http://$localIp:$freePort")
		} catch (e: IOException) {
			LogManager.warning("[OSCQueryHandler] " + e.message)
		}
	}

	private fun serviceResolved(info: ServiceInfo) {
		// Check the service name
		if (!info.name.startsWith(serviceStartsWith)) {
			LogManager.debug("[OSCQueryHandler] Resolved but rejected (name must start with \"$serviceStartsWith\"): " + info.name)
			return
		}
		LogManager.debug("[OSCQueryHandler] Resolved and accepted: " + info.name)

		// Get data from ServiceInfo
		val ip = info.inetAddresses[0].hostAddress
		val port = info.port

		// Update our OSC Sender
		LogManager.debug("[OSCQueryHandler] URL: http://$ip:$port")
		updateOSCSendingInfo("http://$ip:$port")

		// Request data
		val localIp = RPCUtil.getLocalIp()
		val freePort = randomFreePort()
		val server = OSCQueryServer(
			"SlimeVR-Server-$localIp",
			OscTransport.UDP,
			oscHandler.portIn.toUShort(),
			localIp,
			freePort,
			localIp
		)
		val node = OSCQueryNode(queryPath, null, null)
		server.rootNode.addNode(node)
		server.init()
		LogManager.debug("[OSCQueryHandler] SlimeVR OSCQueryServer started at http://$localIp:$freePort")
	}

	/**
	 * Retrieves the OSC Port and IP from the remote OSCQuery service.
	 * These tell us where to send our OSC packets to.
	 */
	private fun updateOSCSendingInfo(address: String) {
		// Request HOST_INFO via http
		val hostInfoRequest = HttpRequest.newBuilder().uri(URI.create("$address?HOST_INFO")).build()

		// Get http response
		val hostInfoResponse = HttpClient.newHttpClient().send(hostInfoRequest, HttpResponse.BodyHandlers.ofString())

		// Check HTTP status
		if (hostInfoResponse.statusCode() != HttpURLConnection.HTTP_OK) {
			LogManager.warning("[OSCQueryHandler] Received HTTP status code ${hostInfoResponse.statusCode()}")
			return
		}

		// map to json
		val hostInfoJson = ObjectMapper().readTree(hostInfoResponse.body())

		// Get data from Json
		val oscIP = hostInfoJson.get("OSC_IP").asText()
		val oscPort = hostInfoJson.get("OSC_PORT").asInt()
		LogManager.info("[OSCQueryHandler] Found OSC address = $oscIP and port = $oscPort")

		// Update the oscHandler
		oscHandler.updateOscSender(oscPort, oscIP)
	}
}
