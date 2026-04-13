package dev.slimevr.solarxr

import dev.slimevr.EventDispatcher
import dev.slimevr.VRServer
import dev.slimevr.VRServerActions
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import solarxr_protocol.MessageBundle
import solarxr_protocol.data_feed.DataFeedConfig
import solarxr_protocol.data_feed.DataFeedMessage
import solarxr_protocol.data_feed.DataFeedMessageHeader
import solarxr_protocol.rpc.RpcMessage
import solarxr_protocol.rpc.RpcMessageHeader
import java.nio.ByteBuffer

data class SolarXRBridgeState(
	val dataFeedConfigs: List<DataFeedConfig>,
	val datafeedTimers: List<Job>,
)

sealed interface SolarXRBridgeActions {
	data class SetConfig(val configs: List<DataFeedConfig>, val timers: List<Job>) : SolarXRBridgeActions
}

typealias SolarXRBridgeContext = Context<SolarXRBridgeState, SolarXRBridgeActions>
typealias SolarXRBridgeBehaviour = Behaviour<SolarXRBridgeState, SolarXRBridgeActions, SolarXRBridge>

suspend fun onSolarXRMessage(message: ByteBuffer, context: SolarXRBridge) {
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

class SolarXRBridge(
	val id: Int,
	val context: SolarXRBridgeContext,
	val serverContext: VRServer,
	val dataFeedDispatcher: EventDispatcher<DataFeedMessage>,
	val rpcDispatcher: EventDispatcher<RpcMessage>,
	val outbound: EventDispatcher<MessageBundle> = EventDispatcher(),
) {
	suspend fun sendRpc(message: RpcMessage) = outbound.emit(MessageBundle(rpcMsgs = listOf(RpcMessageHeader(message = message))))

	suspend fun sendDataFeed(frame: DataFeedMessageHeader) = outbound.emit(MessageBundle(dataFeedMsgs = listOf(frame)))

	fun disconnect() {
		serverContext.context.dispatch(VRServerActions.SolarXRDisconnected(id))
	}

	companion object {
		fun create(
			id: Int,
			serverContext: VRServer,
			scope: CoroutineScope,
			behaviours: List<SolarXRBridgeBehaviour>,
		): SolarXRBridge {
			val context = Context.create(
				initialState = SolarXRBridgeState(dataFeedConfigs = listOf(), datafeedTimers = listOf()),
				scope = scope,
				behaviours = behaviours,
			)

			val bridge = SolarXRBridge(
				id = id,
				context = context,
				serverContext = serverContext,
				dataFeedDispatcher = EventDispatcher(),
				rpcDispatcher = EventDispatcher(),
			)

			behaviours.forEach { it.observe(bridge) }


			return bridge
		}
	}
}
