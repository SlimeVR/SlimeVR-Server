package dev.slimevr.solarxr

import dev.slimevr.AppLogger
import dev.slimevr.VRServer
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import kotlinx.coroutines.awaitCancellation
import solarxr_protocol.MessageBundle
import solarxr_protocol.rpc.ResetRequest
import java.nio.ByteBuffer

const val SOLARXR_PORT = 21110

suspend fun onSolarXRMessage(message: ByteBuffer, context: SolarXRConnection) {
	val messageBundle = MessageBundle.fromByteBuffer(message)

	messageBundle.dataFeedMsgs?.forEach {
		val msg = it.message ?: return
		context.dataFeedDispatcher.emit(msg)
	}

	messageBundle.rpcMsgs?.forEach {
		val msg = it.message ?: return
		context.rpcDispatcher.emit(msg)
	}
}

suspend fun createSolarXRWebsocketServer(serverContext: VRServer) {
	val engine = embeddedServer(Netty, port = SOLARXR_PORT) {
		install(WebSockets)

		routing {
			webSocket {
				AppLogger.solarxr.info("[WS] New connection")
				val solarxrConnection = createSolarXRConnection(
					serverContext,
					scope = this,
					onSend = {
						send(Frame.Binary(fin = true, data = it))
					},
				)

				for (frame in incoming) {
					when (frame) {
						is Frame.Binary -> onSolarXRMessage(
							frame.buffer,
							solarxrConnection,
						)

						is Frame.Close -> {
							AppLogger.solarxr.info("[WS] Connection closed")
						}

						else -> {}
					}
				}
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
