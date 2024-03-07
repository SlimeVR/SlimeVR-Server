package dev.slimevr.osc

import OSCQueryNode
import OSCQueryServer
import OSCQueryService
import ServiceInfo
import com.fasterxml.jackson.databind.ObjectMapper
import dev.slimevr.protocol.rpc.setup.RPCUtil
import io.eiren.util.collections.FastList
import io.eiren.util.logging.LogManager
import randomFreePort
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

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
			// Add OSCQuery service listeners for local and non-local
			LogManager.debug("[OSCQueryHandler] Listening for OSCQuery services")
			service.addServiceListener("_oscjson._tcp.local.") { serviceResolved(it) }
			service.addServiceListener("_oscjson._tcp.") { serviceResolved(it) }
		} catch (e: IOException) {
			LogManager.warning("[OSCQueryHandler] " + e.message)
		}
	}

	private fun serviceResolved(info: ServiceInfo) {
		// Check the service name
		val serviceName = info.name
		if (!serviceName.startsWith(serviceStartsWith)) {
			// LogManager.debug("[OSCQueryHandler] Resolved but rejected (name must start with \"$serviceStartsWith\"): $serviceName")
			return
		}

		// Get url from ServiceInfo
		val ip = info.inetAddresses[0].hostAddress
		val port = info.port
		val url = "http://$ip:$port"
		if (remoteAddresses.contains(url)) {
			LogManager.debug("[OSCQueryHandler] Resolved but rejected (already has a matching url): $serviceName")
			return
		}
		remoteAddresses.add(url)

		LogManager.debug("[OSCQueryHandler] Resolved and accepted: $serviceName")
		LogManager.debug("[OSCQueryHandler] $serviceName URL: $url")

		// Update sending info if needed
		updateOSCSendingInfo(url)

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

	/**
	 * Retrieves the OSC Port and IP from the remote OSCQuery service.
	 * These tell us where to send our OSC packets to.
	 */
	private fun updateOSCSendingInfo(url: String) {
		// Request HOST_INFO via http
		val hostInfoRequest = HttpRequest.newBuilder().uri(URI.create("$url?HOST_INFO")).build()

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
		val oscPortOut = hostInfoJson.get("OSC_PORT").asInt()
		LogManager.info("[OSCQueryHandler] Found OSC address = $oscIP and port = $oscPortOut")

		// create a new VRCOSCHandler for this service
		if (oscPortOut != vrcOscHandler.portOut || oscIP != vrcOscHandler.address.hostName) {
			vrcOscHandler.addOSCSender(oscPortOut, oscIP)
		} else {
			LogManager.debug("[OSCQueryHandler] An OSC Sender already exists with this address and port!")
		}
	}

	fun close() {
		vrcOscHandler.removeAdditionalOscSenders()
		for (server in oscQueryServers) {
			// server.close() // Hangs the server somehow?
		}
	}
}
