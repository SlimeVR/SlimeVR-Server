package dev.slimevr.osc

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


class OSCQueryHandler {

	var started = false
	private lateinit var jmdns: JmDNS


	private class OSCQueryListener : ServiceListener {
		override fun serviceAdded(event: ServiceEvent) {
			//LogManager.info("[OSCQueryHandler] Service added: " + event.name + ", " + event.info.inetAddresses[0].toString())
		}

		override fun serviceRemoved(event: ServiceEvent) {
			LogManager.info("[OSCQueryHandler] Service removed: " + event.name + ", " + event.info.inetAddresses[0].toString())

			// fallback to user-set port & address
		}

		override fun serviceResolved(event: ServiceEvent) {
			LogManager.info("[OSCQueryHandler] Service resolved: " + event.name + ", " + event.info.inetAddresses[0].toString())

			// override user-set port & address
			val address = event.info.inetAddresses[0]
			val port = event.info.port

			// TODO https://zetcode.com/java/httpclient/
			val target = "https:/$address:$port"
			val client = HttpClient.newHttpClient()
			val request = HttpRequest.newBuilder().uri(URI.create(target)).GET().build()
			val response = client.send(request, HttpResponse.BodyHandlers.discarding())

			println("OSCQuery HTTP response: " + response.statusCode())
		}
	}

	fun start() {
		started = true

		try {
			// Create a JmDNS instance
			jmdns = JmDNS.create(InetAddress.getLocalHost(), "SlimeVR-Server-" + InetAddress.getLocalHost())

			// Add an OSCQuery service listener
			jmdns.addServiceListener("_oscjson._tcp.local.", OSCQueryListener())
		} catch (e: IOException) {
			LogManager.warning("[OSCQueryHandler] " + e.message)
		}
	}
}
