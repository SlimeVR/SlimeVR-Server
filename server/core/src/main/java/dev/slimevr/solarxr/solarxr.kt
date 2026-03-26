package dev.slimevr.solarxr

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.VRServer
import dev.slimevr.context.Context
import dev.slimevr.context.CustomBehaviour
import dev.slimevr.context.createContext
import dev.slimevr.EventDispatcher
import io.ktor.util.moveToByteArray
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import solarxr_protocol.MessageBundle
import solarxr_protocol.data_feed.DataFeedConfig
import solarxr_protocol.data_feed.DataFeedMessage
import solarxr_protocol.rpc.RpcMessage
import solarxr_protocol.rpc.RpcMessageHeader

data class SolarXRConnectionState(
	val dataFeedConfigs: List<DataFeedConfig>,
	val datafeedTimers: List<Job>,
)

sealed interface SolarXRConnectionActions {
	data class SetConfig(val configs: List<DataFeedConfig>, val timers: List<Job>) : SolarXRConnectionActions
}

typealias SolarXRConnectionContext = Context<SolarXRConnectionState, SolarXRConnectionActions>
typealias SolarXRConnectionBehaviour = CustomBehaviour<SolarXRConnectionState, SolarXRConnectionActions, SolarXRConnection>


data class SolarXRConnection(
	val context: SolarXRConnectionContext,
	val serverContext: VRServer,
	val dataFeedDispatcher: EventDispatcher<DataFeedMessage>,
	val rpcDispatcher: EventDispatcher<RpcMessage>,
	val send: suspend (ByteArray) -> Unit,
	val sendRpc: suspend (RpcMessage) -> Unit,
)

fun createSolarXRConnection(
	serverContext: VRServer,
	onSend: suspend (ByteArray) -> Unit,
	scope: CoroutineScope,
): SolarXRConnection {
	val state = SolarXRConnectionState(
		dataFeedConfigs = listOf(),
		datafeedTimers = listOf(),
	)

	val behaviours = listOf(DataFeedInitBehaviour, SerialConsoleBehaviour, FirmwareBehaviour)

	val context = createContext(
		initialState = state,
		reducers = behaviours.map { it.reducer },
		scope = scope,
	)

	val sendRpc: suspend (RpcMessage) -> Unit = { message ->
		val fbb = FlatBufferBuilder(256)
		fbb.finish(
			MessageBundle(rpcMsgs = listOf(RpcMessageHeader(message = message))).encode(fbb),
		)
		onSend(fbb.dataBuffer().moveToByteArray())
	}

	val conn = SolarXRConnection(
		context = context,
		serverContext = serverContext,
		dataFeedDispatcher = EventDispatcher(),
		rpcDispatcher = EventDispatcher(),
		send = onSend,
		sendRpc = sendRpc,
	)

	behaviours.map { it.observer }.forEach { it?.invoke(conn) }

	return conn
}
