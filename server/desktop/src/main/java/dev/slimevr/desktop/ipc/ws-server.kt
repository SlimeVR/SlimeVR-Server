package dev.slimevr.desktop.ipc

import dev.slimevr.AppContextProvider
import dev.slimevr.AppLogger
import dev.slimevr.solarxr.handleSolarXRBridge
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.flow

const val SOLARXR_PORT = 21110

suspend fun createSolarXRWebsocketServer(appContext: AppContextProvider) {
	val engine = embeddedServer(Netty, port = SOLARXR_PORT) {
		install(WebSockets)

		routing {
			webSocket {
				AppLogger.solarxr.info("[WS] New connection")
				handleSolarXRBridge(
					appContext = appContext,
					messages = flow {
						for (frame in incoming) {
							when (frame) {
								is Frame.Binary -> emit(frame.data)
								is Frame.Close -> AppLogger.solarxr.info("[WS] Connection closed")
								else -> {}
							}
						}
					},
					send = { bytes -> send(Frame.Binary(fin = true, data = bytes)) },
				)
			}
		}
	}
	engine.start(wait = false)
	try {
		awaitCancellation()
	} finally {
		engine.stop()
	}
}
