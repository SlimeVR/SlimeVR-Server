package dev.slimevr.oscquery

import dev.slimevr.mdns.MdnsAdvertise
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.random.Random

private const val OSC_JSON_SERVICE_TYPE = "_oscjson._tcp"
private const val OSC_UDP_SERVICE_TYPE = "_osc._udp"

class OscQueryServer(
	private val name: String,
	private val address: String,
	oscPort: UShort,
	private val advertise: MdnsAdvertise,
	private val tree: OscQueryTree = OscQueryTree(),
	private val transport: OscQueryTransport = OscQueryTransport.UDP,
) {
	private val httpServer = OscQueryHttpServer(hostInfo = ::buildHostInfo, tree = tree)
	private val oscPort = MutableStateFlow(oscPort)

	// mDNS instance names must be unique on the network and we don't probe for conflicts
	// the odds are very low 1 in 16.7 million, if it happens the user can restart the server
	// TODO: actually check for conflicts someday
	private val instanceName = "$name-${Random.nextInt(0x1000000).toString(16).uppercase().padStart(6, '0')}"

	val oscQueryPort: UShort
		get() = httpServer.port

	fun addNode(node: OscQueryNode) = tree.add(node)

	fun removeNode(path: String): Boolean = tree.remove(path)

	suspend fun start(scope: CoroutineScope) {
		httpServer.start(scope)
	}

	suspend fun advertise(): Nothing {
		oscPort.collectLatest { port ->
			coroutineScope {
				launch {
					advertise(
						OSC_JSON_SERVICE_TYPE,
						instanceName,
						oscQueryPort.toInt(),
						mapOf("txtvers" to "1", "osc_transport" to transport.name.lowercase(), "osc_port" to port.toString()),
					)
				}
				advertise(OSC_UDP_SERVICE_TYPE, instanceName, port.toInt(), mapOf("txtvers" to "1", "oscquery_port" to oscQueryPort.toString()))
			}
		}
		awaitCancellation()
	}

	fun updateOscPort(port: UShort) {
		oscPort.value = port
	}

	fun close() {
		httpServer.close()
	}

	private fun buildHostInfo() = OscQueryHostInfo(
		name = name,
		oscIp = address,
		oscPort = oscPort.value,
		oscTransport = transport,
	)
}
