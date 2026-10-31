package dev.slimevr.oscquery

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

private val oscQueryHttpClient = HttpClient(CIO) {
	install(HttpTimeout) {
		connectTimeoutMillis = 2_000
		requestTimeoutMillis = 5_000
	}
}

/**
 * OSC port of the OSCQuery host at [address]:[port], read from its HOST_INFO. Only OSC_PORT is read:
 * the rest varies between implementations (e.g. extension names we don't model).
 */
suspend fun fetchOscQueryOscPort(address: String, port: Int): Int {
	val host = if (':' in address) "[$address]" else address
	val hostInfo = oscQueryJson.parseToJsonElement(oscQueryHttpClient.get("http://$host:$port/?HOST_INFO").bodyAsText())
	return hostInfo.jsonObject.getValue("OSC_PORT").jsonPrimitive.int
}
