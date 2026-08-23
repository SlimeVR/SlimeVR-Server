package dev.slimevr.desktop.ipc

import com.google.flatbuffers.FlatBufferBuilder
import dev.hannah.portals.PortalManager
import dev.slimevr.AppContextProvider
import dev.slimevr.CURRENT_PLATFORM
import dev.slimevr.Platform
import dev.slimevr.SLIMEVR_IDENTIFIER
import dev.slimevr.VRServerActions
import dev.slimevr.desktop.unblockSteamVRDriver
import dev.slimevr.fbscodegen.runtime.JvmFlatBufferReader
import dev.slimevr.fbscodegen.runtime.JvmFlatBufferWriter
import dev.slimevr.logging.AppLogger
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import dev.slimevr.solarxr.onSolarXRMessage
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.Buffer
import solarxr_protocol.MessageBundle
import solarxr_protocol.rpc.EnableSteamVRDriverRequest
import solarxr_protocol.rpc.OpenKeybindSettingsRequest
import java.nio.ByteBuffer

class EnableSteamVRDriverBehaviour : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		receiver.rpcDispatcher.on<EnableSteamVRDriverRequest> {
			receiver.context.scope.launch {
				val client = HttpClient(CIO)
				unblockSteamVRDriver(client, "slimevr")
				client.close()
			}
		}.launchIn(receiver.context.scope)
	}
}

class OpenKeybindSettingsBehaviour : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		receiver.rpcDispatcher.on<OpenKeybindSettingsRequest> {
			if (CURRENT_PLATFORM != Platform.LINUX) return@on
			receiver.context.scope.launch {
				withContext(Dispatchers.IO) {
					runCatching {
						PortalManager(SLIMEVR_IDENTIFIER).openGlobalShortcutsSettings()
					}.onFailure { AppLogger.keybind.error(it, "Failed to open global shortcuts settings") }
				}
			}
		}.launchIn(receiver.context.scope)
	}
}

suspend fun handleSolarXRBridge(
	appContext: AppContextProvider,
	messages: Flow<Buffer>,
	send: suspend (Buffer) -> Unit,
) = coroutineScope {
	val bridge = SolarXRBridge.create(
		id = appContext.server.nextHandle(),
		appContext = appContext,
		scope = this,
		extraBehaviours = { _ ->
			buildList {
				add(EnableSteamVRDriverBehaviour())
				add(OpenKeybindSettingsBehaviour())
			}
		},
	)

	appContext.server.context.dispatch(VRServerActions.SolarXRConnected(bridge))

	// One builder and one send buffer for the life of the connection. clear() keeps the buffer it has
	// already grown into, so a datafeed frame stops re-growing from 256 bytes every time. Safe to
	// share: this collector handles one bundle at a time, and send drains the buffer
	val fbb = FlatBufferBuilder(256)
	val sendBuffer = Buffer()

	bridge.outbound.on<MessageBundle> { bundle ->
		fbb.clear()
		fbb.finish(bundle.encode(JvmFlatBufferWriter(fbb)))
		sendBuffer.write(fbb.dataBuffer())
		send(sendBuffer)
	}.launchIn(this)

	bridge.startObserving()

	val receiveArray = ByteArray(MAX_FRAME_SIZE)

	try {
		messages.collect { frame ->
			val size = frame.size.toInt()
			// read(array, offset, count) only copies from one internal segment (up to 8k) per call
			// and returns however much that was, so filling a fixed array takes a loop
			var done = 0
			while (done < size) done += frame.read(receiveArray, done, size - done)
			val reader = JvmFlatBufferReader(ByteBuffer.wrap(receiveArray, 0, size))
			onSolarXRMessage(MessageBundle.decode(reader, reader.getInt(0)), bridge)
		}
	} finally {
		bridge.disconnect()
		coroutineContext.cancelChildren()
	}
}
