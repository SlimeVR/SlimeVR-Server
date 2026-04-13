package dev.slimevr.solarxr

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.VRServer
import io.ktor.util.moveToByteArray
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import solarxr_protocol.MessageBundle
import java.nio.ByteBuffer

suspend fun handleSolarXRBridge(
	server: VRServer,
	messages: Flow<ByteArray>,
	send: suspend (ByteArray) -> Unit,
	behaviours: List<SolarXRBridgeBehaviour>,
) = coroutineScope {
	val bridge = SolarXRBridge.create(
		id = server.nextHandle(),
		serverContext = server,
		scope = this,
		behaviours = behaviours,
	)

	bridge.outbound.on<MessageBundle> { bundle ->
		val fbb = FlatBufferBuilder(256)
		fbb.finish(bundle.encode(fbb))
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
