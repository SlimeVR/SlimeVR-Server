package dev.slimevr.desktop.ipc

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.AppContextProvider
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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import solarxr_protocol.MessageBundle
import solarxr_protocol.rpc.EnableSteamVRDriverRequest
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
