package dev.slimevr.osc

import com.fasterxml.jackson.databind.ObjectMapper
import dev.slimevr.protocol.rpc.setup.RPCUtil
import io.eiren.util.logging.LogManager
import java.io.IOException
import java.net.InetAddress
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import javax.jmdns.JmDNS
import javax.jmdns.ServiceEvent
import javax.jmdns.ServiceListener

class OSCQueryHandler(oscHandler: OSCHandler) {

	private class OSCQueryListener(val oscHandler: OSCHandler) : ServiceListener {
		override fun serviceAdded(event: ServiceEvent) {}

		override fun serviceRemoved(event: ServiceEvent) {}

		override fun serviceResolved(event: ServiceEvent) {
			LogManager.info("[OSCQueryHandler] Service resolved: ${event.name}, ${event.info.inetAddresses[0]}")

			// Request http
			val httpAddress = "http:/${event.info.inetAddresses[0]}:${event.info.port}"
			val client = HttpClient.newHttpClient()
			val hostInfoRequest = HttpRequest.newBuilder().uri(URI.create("$httpAddress?HOST_INFO")).build()
			val methodsRequest = HttpRequest.newBuilder().uri(URI.create(httpAddress)).build()

			// Get http response
			val hostInfoResponse = client.send(hostInfoRequest, HttpResponse.BodyHandlers.ofString())
			val methodsResponse = client.send(methodsRequest, HttpResponse.BodyHandlers.ofString())

			// map to json
			val objectMapper = ObjectMapper()
			val hostInfoJson = objectMapper.readTree(hostInfoResponse.body())
			val methodsJson = objectMapper.readTree(methodsResponse.body())

			// Get data from HOST_INFO
			val oscIP = hostInfoJson.get("OSC_IP").asText()
			val oscPort = hostInfoJson.get("OSC_PORT").asInt()
			LogManager.info("[OSCQueryHandler] Found OSC address = $oscIP and OSC port = $oscPort for ${event.name}")

			// Update the oscHandler
			oscHandler.updateOscSender(oscPort, oscIP)
		}
	}

	init {
		try {
			// Create a JmDNS instance
			val jmdns = JmDNS.create(InetAddress.getLocalHost(), "SlimeVR-Server-" + RPCUtil.getLocalIp())

			// Add an OSCQuery service listener
			jmdns.addServiceListener("_oscjson._tcp.local.", OSCQueryListener(oscHandler))
		} catch (e: IOException) {
			LogManager.warning("[OSCQueryHandler] " + e.message)
		}
	}
}
