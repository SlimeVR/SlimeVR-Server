package dev.slimevr.android.ipc

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.AppContextProvider
import dev.slimevr.VRServerActions
import dev.slimevr.logging.AppLogger
import dev.slimevr.solarxr.OutboundFrame
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.checkedSolarXRFrame
import dev.slimevr.solarxr.onSolarXRMessage
import dev.slimevr.solarxr.writeSolarXRBundle
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
import solarxr_protocol.MessageBundle
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

					bridge.outbound.on<OutboundFrame> { frame ->
						val fbb = FlatBufferBuilder(256)
						writeSolarXRBundle(fbb, frame.bundle)
						send(Frame.Binary(fin = true, data = fbb.dataBuffer().moveToByteArray()))
						if (frame.closeReason != null) {
							AppLogger.ipc.warn("SolarXR[${bridge.id}] closing (websocket): ${frame.closeReason}")
							this@coroutineScope.cancel()
						}
					}.launchIn(this)

					val initTimeout = launch {
						delay(10.seconds)
						if (!bridge.isReady) {
							AppLogger.ipc.warn("SolarXR[${bridge.id}] initialization timed out (websocket)")
							this@coroutineScope.cancel()
						}
					}

					try {
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
							val reader = checkedSolarXRFrame(buffer)
							onSolarXRMessage(MessageBundle.fromByteBuffer(reader), bridge)
							if (bridge.isReady) initTimeout.cancel()
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
