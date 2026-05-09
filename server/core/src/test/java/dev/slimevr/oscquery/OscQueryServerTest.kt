package dev.slimevr.oscquery

import com.appstractive.dnssd.NetService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.net.URI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OscQueryServerTest {
	private val json = Json { ignoreUnknownKeys = true }

	@Test
	fun httpRoutesAndServiceLifecycleAreWired() = runBlocking {
		val createdServices = mutableListOf<FakeNetService>()
		val server = OscQueryServer(
			name = "SlimeVR",
			address = "127.0.0.1",
			oscPort = 9000u,
			serviceFactory = { type, name, port, _, _, _, txt ->
				FakeNetService(type = type, name = name, port = port, txt = txt).also(createdServices::add)
			},
		)
		server.addNode(OscQueryNode(fullPath = "/tracking/vrsystem"))

		try {
			server.start(this)

			assertTrue(server.oscQueryPort > 0u)
			assertEquals(2, createdServices.size)
			assertEquals("_oscjson._tcp", createdServices[0].type)
			assertEquals("_osc._udp", createdServices[1].type)
			assertTrue(createdServices.all { it.isRegistered.value })

			val hostInfo = json.parseToJsonElement(readUrl("http://127.0.0.1:${server.oscQueryPort}/HOST_INFO")).jsonObject
			assertEquals("SlimeVR", hostInfo["NAME"]?.jsonPrimitive?.content)
			assertEquals(9000, hostInfo["OSC_PORT"]?.jsonPrimitive?.content?.toInt())

			val root = json.parseToJsonElement(readUrl("http://127.0.0.1:${server.oscQueryPort}/")).jsonObject
			assertEquals("/", root["FULL_PATH"]?.jsonPrimitive?.content)
			assertEquals(root["CONTENTS"]?.jsonObject?.containsKey("tracking"), true)

			val node = json.parseToJsonElement(readUrl("http://127.0.0.1:${server.oscQueryPort}/tracking/vrsystem")).jsonObject
			assertEquals("/tracking/vrsystem", node["FULL_PATH"]?.jsonPrimitive?.content)

			val notFound = readStatus("http://127.0.0.1:${server.oscQueryPort}/missing")
			assertEquals(404, notFound)

			server.updateOscPort(9100u)
			assertEquals(3, createdServices.size)
			assertEquals(createdServices[1].unregisterCount, 1)
			assertEquals(9100, createdServices[2].port)
			assertTrue(createdServices[2].isRegistered.value)
		} finally {
			server.close()
			assertTrue(createdServices.all { it.unregistered.value })
		}
	}
}

private suspend fun readUrl(url: String): String = withContext(Dispatchers.IO) {
	URI(url).toURL().openStream()
}.bufferedReader().use { it.readText() }

private suspend fun readStatus(url: String): Int {
	val connection = withContext(Dispatchers.IO) {
		URI(url).toURL().openConnection()
	} as java.net.HttpURLConnection
	return try {
		connection.requestMethod = "GET"
		connection.responseCode
	} finally {
		connection.disconnect()
	}
}

private class FakeNetService(
	override val name: String,
	override val domain: String = "",
	override val type: String,
	override val port: Int,
	private val txt: Map<String, String>,
) : NetService {
	override val isRegistered = MutableStateFlow(false)
	val unregistered = MutableStateFlow(false)
	var registerCount = 0
	var unregisterCount = 0

	override suspend fun register(timeoutInMs: Long) {
		registerCount++
		isRegistered.value = true
	}

	override suspend fun unregister() {
		unregisterCount++
		unregistered.value = true
		isRegistered.value = false
	}
}
