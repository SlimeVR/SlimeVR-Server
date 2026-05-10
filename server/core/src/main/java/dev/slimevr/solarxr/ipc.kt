package dev.slimevr.solarxr

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.AppContextProvider
import dev.slimevr.VRServerActions
import dev.slimevr.fbscodegen.runtime.JvmFlatBufferWriter
import io.ktor.util.moveToByteArray
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import solarxr_protocol.MessageBundle
import java.nio.ByteBuffer

suspend fun handleSolarXRBridge(
	appContext: AppContextProvider,
	messages: Flow<ByteArray>,
	send: suspend (ByteArray) -> Unit,
) = coroutineScope {
	val bridge = SolarXRBridge.create(
		id = appContext.server.nextHandle(),
		appContext = appContext,
		scope = this,
	)

	appContext.server.context.dispatch(VRServerActions.SolarXRConnected(bridge))

	bridge.outbound.on<MessageBundle> { bundle ->
		val fbb = FlatBufferBuilder(256)
		fbb.finish(bundle.encode(JvmFlatBufferWriter(fbb)))
		send(fbb.dataBuffer().moveToByteArray())
	}

	try {
		messages.collect { bytes ->
			onSolarXRMessage(ByteBuffer.wrap(bytes), bridge)
		}
	} finally {
		bridge.disconnect()
	}
}
