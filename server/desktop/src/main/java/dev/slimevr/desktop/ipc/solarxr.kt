package dev.slimevr.desktop.ipc

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.AppContextProvider
import dev.slimevr.VRServerActions
import dev.slimevr.fbscodegen.runtime.JvmFlatBufferReader
import dev.slimevr.fbscodegen.runtime.JvmFlatBufferWriter
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.onSolarXRMessage
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
			val reader = JvmFlatBufferReader(ByteBuffer.wrap(bytes))
			onSolarXRMessage(MessageBundle.decode(reader, reader.getInt(0)), bridge)
		}
	} finally {
		bridge.disconnect()
	}
}