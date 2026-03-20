package dev.slimevr.solarxr

import dev.slimevr.AppLogger
import dev.slimevr.VRServer
import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.routing.routing
import io.ktor.server.websocket.*
import io.ktor.websocket.Frame
import io.ktor.websocket.readBytes
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import solarxr_protocol.MessageBundle
import java.nio.ByteBuffer
import kotlin.reflect.KClass

const val SOLARXR_PORT = 21110


suspend fun onSolarXRMessage(message: ByteBuffer, context: SolarXRConnection) {
	val messageBundle = MessageBundle.fromByteBuffer(message)

	messageBundle.dataFeedMsgs?.forEach {
		val msg = it.message ?: return;
		context.dataFeedDispatcher.emit(msg)
	}

	messageBundle.rpcMsgs?.forEach {
		val msg = it.message ?: return;
		context.rpcDispatcher.emit(msg)
	}
}


fun createSolarXRWebsocketServer(serverContext: VRServer) {
	embeddedServer(Netty, port = SOLARXR_PORT) {
		install(WebSockets)

		routing {
			webSocket {

				val solarxrConnection =
					createSolarXRConnection(serverContext, scope = this, onSend = {
						send(Frame.Binary(fin = true, data = it))
					})

				for (frame in incoming) {
					when (frame) {
						is Frame.Binary -> onSolarXRMessage(
							frame.buffer,
							solarxrConnection
						)

						is Frame.Close -> {
							AppLogger.solarxr.info("Connection closed")
						}

						else -> {}
					}

				}
			}
		}
	}.start(wait = true)
}

