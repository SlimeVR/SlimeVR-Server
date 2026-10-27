package dev.slimevr.oscquery

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
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
	fun httpRoutesAndAdvertisementsAreWired() = runBlocking {
		val advertised = MutableStateFlow<List<Advertised>>(emptyList())
		val server = OscQueryServer(
			name = "SlimeVR",
			address = "127.0.0.1",
			oscPort = 9000u,
			advertise = { type, name, port, txt ->
				val ad = Advertised(type, name, port, txt)
				advertised.update { it + ad }
				try {
					awaitCancellation()
				} finally {
					advertised.update { it - ad }
				}
			},
		)
		server.addNode(OscQueryNode(fullPath = "/tracking/vrsystem"))

		server.start(this)
		val advertising = launch { server.advertise() }
		try {
			assertTrue(server.oscQueryPort > 0u)
			val initial = withTimeout(5_000) { advertised.first { it.size == 2 } }
			val oscJson = initial.single { it.type == "_oscjson._tcp" }
			assertEquals(server.oscQueryPort.toInt(), oscJson.port)
			assertEquals("9000", oscJson.txt["osc_port"])
			assertEquals(9000, initial.single { it.type == "_osc._udp" }.port)
			assertEquals(1, initial.map { it.name }.distinct().size) // both services share the instance name

			val hostInfo = json.parseToJsonElement(readUrl("http://127.0.0.1:${server.oscQueryPort}/?HOST_INFO")).jsonObject
			assertEquals("SlimeVR", hostInfo["NAME"]?.jsonPrimitive?.content)
			assertEquals(9000, hostInfo["OSC_PORT"]?.jsonPrimitive?.content?.toInt())

			val root = json.parseToJsonElement(readUrl("http://127.0.0.1:${server.oscQueryPort}/")).jsonObject
			assertEquals("/", root["FULL_PATH"]?.jsonPrimitive?.content)
			assertEquals(root["CONTENTS"]?.jsonObject?.containsKey("tracking"), true)

			val node = json.parseToJsonElement(readUrl("http://127.0.0.1:${server.oscQueryPort}/tracking/vrsystem")).jsonObject
			assertEquals("/tracking/vrsystem", node["FULL_PATH"]?.jsonPrimitive?.content)

			assertEquals(404, readStatus("http://127.0.0.1:${server.oscQueryPort}/missing"))

			// A new OSC port is re-advertised in both records
			server.updateOscPort(9100u)
			val updated = withTimeout(5_000) {
				advertised.first { ads -> ads.size == 2 && ads.any { it.type == "_osc._udp" && it.port == 9100 } }
			}
			assertEquals("9100", updated.single { it.type == "_oscjson._tcp" }.txt["osc_port"])

			advertising.cancelAndJoin()
			assertEquals(emptyList(), advertised.value)
		} finally {
			advertising.cancel()
			server.close()
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

private data class Advertised(
	val type: String,
	val name: String,
	val port: Int,
	val txt: Map<String, String>,
)
