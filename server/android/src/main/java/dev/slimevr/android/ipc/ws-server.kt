package dev.slimevr.android.ipc

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.AppContextProvider
import dev.slimevr.VRServerActions
import dev.slimevr.fbscodegen.runtime.JvmFlatBufferReader
import dev.slimevr.fbscodegen.runtime.JvmFlatBufferWriter
import dev.slimevr.logging.AppLogger
import dev.slimevr.solarxr.SOLARXR_PROTOCOL_VERSION
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.checkedSolarXRFrame
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
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import solarxr_protocol.ClientHello
import solarxr_protocol.HelloStatus
import solarxr_protocol.MessageBundle
import solarxr_protocol.ServerHello
import java.nio.ByteBuffer
import kotlin.time.Duration.Companion.seconds

const val SOLARXR_PORT = 21110

suspend fun createAndroidSolarXRWebsocketServer(appContext: AppContextProvider) {
	val engine = embeddedServer(CIO, port = SOLARXR_PORT) {
		install(WebSockets)

		routing {
			webSocket {
				coroutineScope {
					val bridge = SolarXRBridge.create(
						id = appContext.server.nextHandle(),
						appContext = appContext,
						scope = this,
					)

					AppLogger.ipc.info("SolarXR[${bridge.id}] connected (websocket)")
					appContext.server.context.dispatch(VRServerActions.SolarXRConnected(bridge))

					bridge.outbound.on<MessageBundle> { bundle ->
						val fbb = FlatBufferBuilder(256)
						bundle.finish(JvmFlatBufferWriter(fbb))
						send(Frame.Binary(fin = true, data = fbb.dataBuffer().moveToByteArray()))
					}.launchIn(this)

					val initTimeout = launch {
						delay(10.seconds)
						if (!bridge.isReady) {
							AppLogger.ipc.warn("SolarXR[${bridge.id}] initialization timed out (websocket)")
							this@coroutineScope.cancel()
						}
					}

					try {
						var awaitingHello = true
						flow {
							for (frame in incoming) {
								when (frame) {
									is Frame.Binary -> emit(frame.data)
									is Frame.Close -> AppLogger.ipc.info("SolarXR[${bridge.id}] connection closed")
									else -> {}
								}
							}
						}.collect { bytes ->
							val buffer = ByteBuffer.wrap(bytes)
							if (awaitingHello) {
								val reader = checkedSolarXRFrame(buffer, ClientHello.FILE_IDENTIFIER)
								val hello = ClientHello.fromByteBuffer(reader)
								val accepted = hello.protocolVersion == SOLARXR_PROTOCOL_VERSION
								val fbb = FlatBufferBuilder(64)
								ServerHello(if (accepted) HelloStatus.ACCEPTED else HelloStatus.REJECTED_UNSUPPORTED_VERSION, SOLARXR_PROTOCOL_VERSION)
									.finish(JvmFlatBufferWriter(fbb))
								send(Frame.Binary(fin = true, data = fbb.dataBuffer().moveToByteArray()))
								require(accepted) { "Unsupported SolarXR protocol version ${hello.protocolVersion}" }
								awaitingHello = false
								bridge.beginConfiguration()
							} else {
								val reader = checkedSolarXRFrame(buffer, MessageBundle.FILE_IDENTIFIER)
								onSolarXRMessage(MessageBundle.fromByteBuffer(reader), bridge)
								if (bridge.isReady) initTimeout.cancel()
							}
						}
					} finally {
						AppLogger.ipc.info("SolarXR[${bridge.id}] disconnected (websocket)")
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
