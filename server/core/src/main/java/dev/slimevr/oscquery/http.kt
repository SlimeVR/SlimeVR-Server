package dev.slimevr.oscquery

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.cio.CIO
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.param
import io.ktor.server.routing.routing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class OscQueryHttpServer(
	private val hostInfo: () -> OscQueryHostInfo,
	private val tree: OscQueryTree,
) {
	private val mutex = Mutex()
	private var engine: EmbeddedServer<*, *>? = null
	private var boundPort: UShort = 0u

	val port: UShort
		get() = boundPort

	suspend fun start(scope: CoroutineScope, address: String = "0.0.0.0", port: UShort = 0u): UShort = mutex.withLock {
		if (engine != null) return boundPort

		val started = embeddedServer(CIO, host = address, port = port.toInt()) {
			routing {
				param("HOST_INFO") { get("/") { respondHostInfo(call) } }
				get("/") { respondNode(call, "/") }
				get("/{path...}") {
					val path = call.parameters.getAll("path").orEmpty().joinToString("/", prefix = "/")
					respondNode(call, path)
				}
			}
		}.start(wait = false)

		engine = started
		boundPort = started.engine.resolvedConnectors().firstOrNull()?.port?.toUShort() ?: port
		boundPort
	}

	fun close() {
		val started = engine
		engine = null
		boundPort = 0u
		started?.stop(gracePeriodMillis = 1_000, timeoutMillis = 2_000)
	}

	private suspend fun respondHostInfo(call: ApplicationCall) {
		call.respondText(oscQueryJson.encodeToString(hostInfo()), ContentType.Application.Json)
	}

	private suspend fun respondNode(call: ApplicationCall, path: String) {
		val node = tree.find(path)
		if (node == null) {
			call.respondText("Not Found", ContentType.Text.Plain, status = HttpStatusCode.NotFound)
			return
		}
		call.respondText(oscQueryJson.encodeToString(node), ContentType.Application.Json)
	}
}
