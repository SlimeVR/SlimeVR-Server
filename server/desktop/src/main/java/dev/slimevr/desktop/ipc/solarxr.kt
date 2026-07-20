package dev.slimevr.desktop.ipc

import com.google.flatbuffers.FlatBufferBuilder
import dev.hannah.portals.PortalManager
import dev.slimevr.AppContextProvider
import dev.slimevr.AppLogger
import dev.slimevr.CURRENT_PLATFORM
import dev.slimevr.Platform
import dev.slimevr.SLIMEVR_IDENTIFIER
import dev.slimevr.VRServerActions
import dev.slimevr.desktop.unblockSteamVRDriver
import dev.slimevr.fbscodegen.runtime.JvmFlatBufferReader
import dev.slimevr.fbscodegen.runtime.JvmFlatBufferWriter
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import dev.slimevr.solarxr.onSolarXRMessage
import dev.slimevr.util.safeLaunch
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.util.moveToByteArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import solarxr_protocol.MessageBundle
import solarxr_protocol.rpc.EnableSteamVRDriverRequest
import solarxr_protocol.rpc.OpenKeybindSettingsRequest
import java.nio.ByteBuffer

class EnableSteamVRDriverBehaviour : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		receiver.rpcDispatcher.on<EnableSteamVRDriverRequest> {
			receiver.context.scope.safeLaunch {
				val client = HttpClient(CIO)
				unblockSteamVRDriver(client, "slimevr")
				client.close()
			}
		}
	}
}

class OpenKeybindSettingsBehaviour : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		receiver.rpcDispatcher.on<OpenKeybindSettingsRequest> {
			if (CURRENT_PLATFORM != Platform.LINUX) return@on
			receiver.context.scope.safeLaunch {
				withContext(Dispatchers.IO) {
					runCatching {
						PortalManager(SLIMEVR_IDENTIFIER).openGlobalShortcutsSettings()
					}.onFailure { AppLogger.keybind.error(it, "Failed to open global shortcuts settings") }
				}
			}
		}
	}
}

suspend fun handleSolarXRBridge(
	appContext: AppContextProvider,
	messages: Flow<ByteArray>,
	send: suspend (ByteArray) -> Unit,
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

	bridge.outbound.on<MessageBundle> { bundle ->
		val fbb = FlatBufferBuilder(256)
		fbb.finish(bundle.encode(JvmFlatBufferWriter(fbb)))
		send(fbb.dataBuffer().moveToByteArray())
	}

	try {
		messages.collect { bytes ->
			val reader = JvmFlatBufferReader(ByteBuffer.wrap(bytes))
			onSolarXRMessage(MessageBundle.decode(reader, reader.getInt(0)), bridge)
		}
	} finally {
		bridge.disconnect()
	}
}
