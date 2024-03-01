package dev.slimevr.osc

import com.fasterxml.jackson.databind.ObjectMapper
import dev.slimevr.protocol.rpc.setup.RPCUtil
import io.eiren.util.logging.LogManager
import java.io.IOException
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import javax.jmdns.JmDNS
import javax.jmdns.ServiceEvent
import javax.jmdns.ServiceListener

class OSCQueryHandler(
	private val oscHandler: OSCHandler,
	private val queryText: String,
	serviceStartsWith: String,
) {

	private class OSCQueryListener(private val oscQueryHandler: OSCQueryHandler, private val serviceStartsWith: String) : ServiceListener {

		override fun serviceAdded(event: ServiceEvent) {}

		override fun serviceRemoved(event: ServiceEvent) {}

		override fun serviceResolved(event: ServiceEvent) {
			if (event.name.startsWith(serviceStartsWith)) {
				LogManager.debug("[OSCQueryHandler] Service resolved: ${event.name}, ${event.info.inetAddresses[0]}")
				oscQueryHandler.updateWebsocket(event)
				oscQueryHandler.updateOSCSendingInfo(event)
			}
		}
	}

	init {
		try {
			val jmdns = JmDNS.create(InetAddress.getByName(RPCUtil.getLocalIp()), "SlimeVR-Server-" + RPCUtil.getLocalIp())

			// Add OSCQuery service listeners for local and non-local
			jmdns.addServiceListener("_oscjson._tcp.local.", OSCQueryListener(this, serviceStartsWith))
			jmdns.addServiceListener("_oscjson._tcp.", OSCQueryListener(this, serviceStartsWith))
		} catch (e: IOException) {
			LogManager.warning("[OSCQueryHandler] " + e.message)
		}
	}

	fun updateWebsocket(service: ServiceEvent) {
		val ip = service.info.inetAddresses[0]
		val port = service.info.port
	}

	/**
	 * Retrieves the OSC Port and IP from the remote OSCQuery service.
	 * These tell us where to send our OSC packets to.
	 */
	fun updateOSCSendingInfo(service: ServiceEvent) {
		// Request HOST_INFO via http
		val remoteAddress = service.info.urLs[0]
		val hostInfoRequest = HttpRequest.newBuilder().uri(URI.create("$remoteAddress?HOST_INFO")).build()
		LogManager.debug("[OSCQueryHandler] OSCQuery's service's address: $remoteAddress")

		// Get http response
		val hostInfoResponse = HttpClient.newHttpClient().send(hostInfoRequest, HttpResponse.BodyHandlers.ofString())

		if (hostInfoResponse.statusCode() != HttpURLConnection.HTTP_OK) {
			LogManager.warning("[OSCQueryHandler] Received HTTP status code ${hostInfoResponse.statusCode()}")
			return
		}

		// map to json
		val objectMapper = ObjectMapper()
		val hostInfoJson = objectMapper.readTree(hostInfoResponse.body())

		// Get data from HOST_INFO
		val oscIP = hostInfoJson.get("OSC_IP").asText()
		val oscPort = hostInfoJson.get("OSC_PORT").asInt()
		LogManager.info("[OSCQueryHandler] Found OSC address = $oscIP and port = $oscPort for ${service.name}")

		// Update the oscHandler
		oscHandler.updateOscSender(oscPort, oscIP)
	}
}
