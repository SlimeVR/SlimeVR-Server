package dev.slimevr.osc

import OSCQueryWebsocket
import com.fasterxml.jackson.databind.ObjectMapper
import dev.slimevr.protocol.rpc.setup.RPCUtil
import io.eiren.util.logging.LogManager
import org.java_websocket.drafts.Draft_6455
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
	private val address: String,
	private val queryAddresses: Array<String>,
	serviceStartsWith: String,
) {
	private var service: ServiceEvent? = null
	private val httpClient = HttpClient.newHttpClient()

	private class OSCQueryListener(private val oscQueryHandler: OSCQueryHandler, private val serviceStartsWith: String) : ServiceListener {

		override fun serviceAdded(event: ServiceEvent) {}

		override fun serviceRemoved(event: ServiceEvent) {}

		override fun serviceResolved(event: ServiceEvent) {
			if (event.name.startsWith(serviceStartsWith)) {
				LogManager.info("[OSCQueryHandler] Service resolved: ${event.name}, ${event.info.inetAddresses[0]}")
				oscQueryHandler.updateService(event)
			}
		}
	}

	init {
		try {
			// Create a JmDNS instance
			val iNetAddress = if (address == "127.0.0.1") {
				InetAddress.getLocalHost()
			} else {
				InetAddress.getByName(address)
			}
			val jmdns = JmDNS.create(iNetAddress, "SlimeVR-Server-" + RPCUtil.getLocalIp())

			// Add an OSCQuery service listener
			jmdns.addServiceListener("_oscjson._tcp.local.", OSCQueryListener(this, serviceStartsWith))
		} catch (e: IOException) {
			LogManager.warning("[OSCQueryHandler] " + e.message)
		}
	}

	fun updateService(newService: ServiceEvent) {
		service = newService
		updateWebsocket()
		updateUDPInfo()
	}

	private fun updateWebsocket() {
		service?.let {
			// Get data service info
			val port = it.info.port
			val address = it.info.inetAddresses?.get(0)
			LogManager.info("[OSCQueryHandler] Found Websocket address = $address and port = $port for ${it.name}")

			// Add endpoint
			val jsonTest = "{\"COMMAND\": \"LISTEN\",\"DATA\": \"/tracking/vrsystem\" }"

			val websocketServer = OSCQueryWebsocket(port, Draft_6455())
			websocketServer.start()

			// for (param in queryAddresses) { }
		}
	}

	private fun updateUDPInfo() {
		service?.let {
			// Request HOST_INFO via http
			val httpAddress = "http:/${it.info.inetAddresses?.get(0)}:${it.info.port}"
			val hostInfoRequest = HttpRequest.newBuilder().uri(URI.create("$httpAddress?HOST_INFO")).build()

			// Get http response
			val hostInfoResponse = httpClient.send(hostInfoRequest, HttpResponse.BodyHandlers.ofString())

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
			LogManager.info("[OSCQueryHandler] Found OSC address = $oscIP and port = $oscPort for ${it.name}")

			// Update the oscHandler
			oscHandler.updateOscSender(oscPort, oscIP)
		}
	}
}
