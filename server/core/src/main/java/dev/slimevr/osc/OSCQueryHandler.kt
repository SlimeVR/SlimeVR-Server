package dev.slimevr.osc

import OSCQueryService
import ServiceInfo
import com.fasterxml.jackson.databind.ObjectMapper
import io.eiren.util.logging.LogManager
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class OSCQueryHandler(
	private val oscHandler: OSCHandler,
	private val queryText: String,
	private val serviceStartsWith: String,
) {
	private val service: OSCQueryService = OSCQueryService()

	init {
		try {
			// Add OSCQuery service listeners for local and non-local
			service.addServiceListener("_oscjson._tcp.local.") { serviceResolved(it) }
			service.addServiceListener("_oscjson._tcp.") { serviceResolved(it) }
		} catch (e: IOException) {
			LogManager.warning("[OSCQueryHandler] " + e.message)
		}
	}

	private fun serviceResolved(info: ServiceInfo) {
		LogManager.debug("[OSCQueryHandler] Resolved: " + info.name)
		if (!info.name.startsWith(serviceStartsWith)) return

		val ip = info.inetAddresses[0].hostAddress
		val port = info.port

		LogManager.debug("[OSCQueryHandler] URL: http://$ip:$port")
		updateOSCSendingInfo("http://$ip:$port")

		// val service = service.createService(
		// 	"_oscjson._tcp.",
		// 	"SlimeVR-Server-" + RPCUtil.getLocalIp(),
		// 	7357.toUShort(),
		// 	"test"
		// )
		// TODO: request node queryText {"COMMAND":"LISTEN","DATA":"/tracking/vrsystem"}
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
