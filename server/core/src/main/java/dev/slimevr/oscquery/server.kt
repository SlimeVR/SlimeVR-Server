package dev.slimevr.oscquery

import com.appstractive.dnssd.NetService
import com.appstractive.dnssd.createNetService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

private const val OSC_JSON_SERVICE_TYPE = "_oscjson._tcp"
private const val OSC_UDP_SERVICE_TYPE = "_osc._udp"

typealias OscQueryServiceFactory = (
	type: String,
	name: String,
	port: Int,
	priority: Int,
	weight: Int,
	addresses: List<String>?,
	txt: Map<String, String>,
) -> NetService

class OscQueryServer(
	private val name: String,
	private val address: String,
	private var oscPort: UShort,
	private val tree: OscQueryTree = OscQueryTree(),
	private val transport: OscQueryTransport = OscQueryTransport.UDP,
	private val serviceFactory: OscQueryServiceFactory = ::createNetService,
) {
	private val httpServer = OscQueryHttpServer(hostInfo = ::buildHostInfo, tree = tree)
	private var oscQueryService: NetService? = null
	private var oscService: NetService? = null
	private var started = false

	val oscQueryPort: UShort
		get() = httpServer.port

	fun addNode(node: OscQueryNode) = tree.add(node)

	fun removeNode(path: String): Boolean = tree.remove(path)

	suspend fun start(scope: CoroutineScope) {
		if (started) return
		val httpPort = httpServer.start(scope)
		oscQueryService = createOscQueryService(httpPort).also { it.register() }
		oscService = createOscService(oscPort).also { it.register() }
		started = true
	}

	suspend fun updateOscPort(port: UShort) {
		if (oscPort == port) return
		oscPort = port
		if (!started) return
		withContext(NonCancellable) {
			oscService?.unregister()
			oscService = createOscService(port).also { it.register() }
		}
	}

	suspend fun close() {
		withContext(NonCancellable) {
			oscService?.unregister()
			oscService = null
			oscQueryService?.unregister()
			oscQueryService = null
		}
		httpServer.close()
		started = false
	}

	private fun createOscQueryService(httpPort: UShort) = createService(
		type = OSC_JSON_SERVICE_TYPE,
		name = "$name-$httpPort",
		port = httpPort.toInt(),
		txt = mapOf(
			"txtvers" to "1",
			"osc_transport" to transport.name.lowercase(),
			"osc_port" to oscPort.toString(),
		),
	)

	private fun createOscService(port: UShort) = createService(
		type = OSC_UDP_SERVICE_TYPE,
		name = "$name-$oscQueryPort",
		port = port.toInt(),
		txt = mapOf(
			"txtvers" to "1",
			"oscquery_port" to oscQueryPort.toString(),
		),
	)

	// Pin the advertised address to our resolved local IP. Otherwise dns-sd-kt
	// falls back to `getLocalAddresses().firstOrNull()` which iterates every NIC
	// and may pick a virtual / wrong-subnet adapter — making the service
	// unreachable from headsets on the actual LAN.
	private fun createService(type: String, name: String, port: Int, txt: Map<String, String>): NetService = serviceFactory(type, name, port, 0, 1, listOf(address), txt)

	private fun buildHostInfo() = OscQueryHostInfo(
		name = name,
		oscIp = address,
		oscPort = oscPort,
		oscTransport = transport,
	)
}
