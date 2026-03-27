package dev.slimevr.solarxr

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.EventDispatcher
import dev.slimevr.VRServer
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import io.ktor.util.moveToByteArray
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import solarxr_protocol.MessageBundle
import solarxr_protocol.data_feed.DataFeedConfig
import solarxr_protocol.data_feed.DataFeedMessage
import solarxr_protocol.rpc.RpcMessage
import solarxr_protocol.rpc.RpcMessageHeader
import java.nio.ByteBuffer

data class SolarXRConnectionState(
	val dataFeedConfigs: List<DataFeedConfig>,
	val datafeedTimers: List<Job>,
)

sealed interface SolarXRConnectionActions {
	data class SetConfig(val configs: List<DataFeedConfig>, val timers: List<Job>) : SolarXRConnectionActions
}

typealias SolarXRConnectionContext = Context<SolarXRConnectionState, SolarXRConnectionActions>
typealias SolarXRConnectionBehaviour = Behaviour<SolarXRConnectionState, SolarXRConnectionActions, SolarXRConnection>

suspend fun onSolarXRMessage(message: ByteBuffer, context: SolarXRConnection) {
	val messageBundle = MessageBundle.fromByteBuffer(message)

	messageBundle.dataFeedMsgs?.forEach {
		val msg = it.message ?: return
		context.dataFeedDispatcher.emit(msg)
	}

	messageBundle.rpcMsgs?.forEach {
		val msg = it.message ?: return
		context.rpcDispatcher.emit(msg)
	}
}

class SolarXRConnection(
	val context: SolarXRConnectionContext,
	val serverContext: VRServer,
	val dataFeedDispatcher: EventDispatcher<DataFeedMessage>,
	val rpcDispatcher: EventDispatcher<RpcMessage>,
	private val onSend: suspend (ByteArray) -> Unit,
) {
	suspend fun send(bytes: ByteArray) = onSend(bytes)

	suspend fun sendRpc(message: RpcMessage) {
		val fbb = FlatBufferBuilder(256)
		fbb.finish(
			MessageBundle(rpcMsgs = listOf(RpcMessageHeader(message = message))).encode(fbb),
		)
		onSend(fbb.dataBuffer().moveToByteArray())
	}

	companion object {
		fun create(
			serverContext: VRServer,
			onSend: suspend (ByteArray) -> Unit,
			scope: CoroutineScope,
			behaviours: List<SolarXRConnectionBehaviour>,
		): SolarXRConnection {
			val context = Context.create(
				initialState = SolarXRConnectionState(dataFeedConfigs = listOf(), datafeedTimers = listOf()),
				scope = scope,
				behaviours = behaviours,
			)

			val conn = SolarXRConnection(
				context = context,
				serverContext = serverContext,
				dataFeedDispatcher = EventDispatcher(),
				rpcDispatcher = EventDispatcher(),
				onSend = onSend,
			)

			behaviours.forEach { it.observe(conn) }
			return conn
		}
	}
}
