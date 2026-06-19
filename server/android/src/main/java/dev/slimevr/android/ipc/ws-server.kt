package dev.slimevr.android.ipc

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.AppContextProvider
import dev.slimevr.AppLogger
import dev.slimevr.VRServerActions
import dev.slimevr.fbscodegen.runtime.JvmFlatBufferReader
import dev.slimevr.fbscodegen.runtime.JvmFlatBufferWriter
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.onSolarXRMessage
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.util.moveToByteArray
import io.ktor.websocket.Frame
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow
import solarxr_protocol.MessageBundle
import java.nio.ByteBuffer

const val SOLARXR_PORT = 21110

suspend fun createAndroidSolarXRWebsocketServer(appContext: AppContextProvider) {
	val engine = embeddedServer(CIO, port = SOLARXR_PORT) {
		install(WebSockets)

		routing {
			webSocket {
				AppLogger.ipc.info("New connection")
				coroutineScope {
					val bridge = SolarXRBridge.create(
						id = appContext.server.nextHandle(),
						appContext = appContext,
						scope = this,
					)

					appContext.server.context.dispatch(VRServerActions.SolarXRConnected(bridge))

					bridge.outbound.on<MessageBundle> { bundle ->
						val fbb = FlatBufferBuilder(256)
						fbb.finish(bundle.encode(JvmFlatBufferWriter(fbb)))
						send(Frame.Binary(fin = true, data = fbb.dataBuffer().moveToByteArray()))
					}

					try {
						flow {
							for (frame in incoming) {
								when (frame) {
									is Frame.Binary -> emit(frame.data)
									is Frame.Close -> AppLogger.ipc.info("Connection closed")
									else -> {}
								}
							}
						}.collect { bytes ->
							val reader = JvmFlatBufferReader(ByteBuffer.wrap(bytes))
							onSolarXRMessage(MessageBundle.decode(reader, reader.getInt(0)), bridge)
						}
					} finally {
						bridge.disconnect()
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
