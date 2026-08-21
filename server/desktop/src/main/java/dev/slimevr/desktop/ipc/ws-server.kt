package dev.slimevr.desktop.ipc

import dev.slimevr.AppContextProvider
import dev.slimevr.logging.AppLogger
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.flow
import okio.Buffer

const val SOLARXR_PORT = 21110

suspend fun createSolarXRWebsocketServer(appContext: AppContextProvider) {
	val engine = embeddedServer(Netty, port = SOLARXR_PORT) {
		install(WebSockets) {
			// Frames land in a fixed buffer downstream, and ktor would otherwise take any size
			maxFrameSize = MAX_FRAME_SIZE.toLong()
		}

		routing {
			webSocket {
				AppLogger.solarxr.info("[WS] New connection")
				handleSolarXRBridge(
					appContext = appContext,
					messages = flow {
						val buffer = Buffer()
						for (frame in incoming) {
							if (frame is Frame.Close) AppLogger.solarxr.info("[WS] Connection closed")
							if (frame !is Frame.Binary) continue

							buffer.clear()
							buffer.write(frame.data)
							emit(buffer)
						}
					},
					send = { frame -> send(Frame.Binary(fin = true, data = frame.readByteArray())) },
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
