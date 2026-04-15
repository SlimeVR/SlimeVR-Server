package dev.slimevr.solarxr

import dev.slimevr.AppContextProvider
import dev.slimevr.EventDispatcher
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
	val appContext: AppContextProvider,
	val dataFeedDispatcher: EventDispatcher<DataFeedMessage>,
	val rpcDispatcher: EventDispatcher<RpcMessage>,
	val outbound: EventDispatcher<MessageBundle> = EventDispatcher(),
) {
	suspend fun sendRpc(message: RpcMessage) = outbound.emit(MessageBundle(rpcMsgs = listOf(RpcMessageHeader(message = message))))

	suspend fun sendDataFeed(frame: DataFeedMessageHeader) = outbound.emit(MessageBundle(dataFeedMsgs = listOf(frame)))

	fun disconnect() {
		appContext.server.context.dispatch(VRServerActions.SolarXRDisconnected(id))
	}

	fun startObserving() = context.observeAll(this)

	companion object {
		fun buildBehaviours(appContext: AppContextProvider): List<SolarXRBridgeBehaviour> = buildList {
			add(DataFeedInitBehaviour(appContext.server, appContext.skeleton))
			add(SerialBehaviour(appContext.serialServer))
			add(FirmwareBehaviour(appContext.server, appContext.firmwareManager))
			appContext.vrcConfigManager?.let { vrc ->
				add(VrcBehaviour(vrc, appContext.server, userHeight = { appContext.skeleton.context.state.value.userHeight }))
			}
			add(HeightCalibrationBehaviour(appContext.heightCalibrationManager))
			add(ProvisioningBehaviour(appContext.server, appContext.provisioningManager))
			add(SkeletonBehaviour(appContext.config.userConfig, appContext.skeleton))
			add(TrackingChecklistBehaviour(appContext.trackingChecklist, appContext.config.settings))
			add(AssignTrackerBehaviour(appContext.server))
			add(MagBehaviour(appContext))
		}

		fun create(
			id: Int,
			appContext: AppContextProvider,
			scope: CoroutineScope,
		): SolarXRBridge {
			val context = Context.create(
				initialState = SolarXRBridgeState(dataFeedConfigs = listOf(), datafeedTimers = listOf()),
				scope = scope,
				behaviours = buildBehaviours(appContext),
			)

			val bridge = SolarXRBridge(
				id = id,
				context = context,
				appContext = appContext,
				dataFeedDispatcher = EventDispatcher(),
				rpcDispatcher = EventDispatcher(),
			)
			bridge.startObserving()
			return bridge
		}
	}
}
