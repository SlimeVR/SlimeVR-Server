package dev.slimevr.solarxr

import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.routing.routing
import io.ktor.server.websocket.*
import io.ktor.websocket.Frame
import io.ktor.websocket.readBytes
import solarxr_protocol.MessageBundle
import java.nio.ByteBuffer

const val SOLARXR_PORT = 21110;

fun onSolarXRMessage(message: ByteBuffer) {
	val messageBundle = MessageBundle.getRootAsMessageBundle(message)


	for (index in 0..<messageBundle.dataFeedMsgsLength) {
		val header = messageBundle.dataFeedMsgs(index) ?: error("WIERD?")
		println("HEADER: ${header.message()}")
//		this.dataFeedHandler.onMessage(conn, header)
	}

	for (index in 0..<messageBundle.rpcMsgsLength) {
		val header = messageBundle.rpcMsgs(index)
//		this.rpcHandler.onMessage(conn, header)
	}

	for (index in 0..<messageBundle.pubSubMsgsLength) {
		val header = messageBundle.pubSubMsgs(index)
//		this.pubSubHandler.onMessage(conn, header)
	}
}

fun createSolarXRWebsocketServer() {
	embeddedServer(Netty, port = SOLARXR_PORT) {
		install(WebSockets)

		routing {
			webSocket {
				println("Client Connected!")

				for (frame in incoming) {
					when (frame) {
						is Frame.Binary -> {
							val data = frame.readBytes()
							onSolarXRMessage(frame.buffer)
							println("Received Binary Packet: ${data.size} bytes")
						}
						else -> {}
					}

				}
			}
		}
	}.start(wait = true)
}

