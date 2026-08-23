package dev.slimevr.solarxr

import dev.slimevr.AppContextProvider
import dev.slimevr.EventDispatcher
import dev.slimevr.Subscription
import dev.slimevr.VRServerActions
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.context.ManagedContext
import dev.slimevr.solarxr.driver.DriverHandshakeBehaviour
import dev.slimevr.solarxr.driver.DriverIncomingTrackersBehaviour
import dev.slimevr.solarxr.driver.DriverOutgoingTrackersBehaviour
import dev.slimevr.tracker.TrackerActions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import solarxr_protocol.MessageBundle
import solarxr_protocol.data_feed.DataFeedConfig
import solarxr_protocol.data_feed.DataFeedMessage
import solarxr_protocol.data_feed.DataFeedMessageHeader
import solarxr_protocol.datatypes.BoneMask
import solarxr_protocol.datatypes.DeviceOrigin
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.driver_protocol.DriverMessage
import solarxr_protocol.driver_protocol.DriverMessageHeader
import solarxr_protocol.rpc.RpcMessage
import solarxr_protocol.rpc.RpcMessageHeader
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class SolarXRBridgeState(
	val dataFeedConfigs: List<DataFeedConfig>,
	val driverName: String? = null,
	val boneMask: BoneMask? = null,
)

sealed interface SolarXRBridgeActions {
	data class SetConfig(val configs: List<DataFeedConfig>) : SolarXRBridgeActions
	data class SetDriverInfo(val name: String?, val boneMask: BoneMask?) : SolarXRBridgeActions
}

typealias SolarXRBridgeContext = Context<SolarXRBridgeState, SolarXRBridgeActions>
typealias SolarXRBridgeBehaviour = Behaviour<SolarXRBridgeState, SolarXRBridgeActions, SolarXRBridge>

suspend fun onSolarXRMessage(message: MessageBundle, context: SolarXRBridge) {
	message.dataFeedMsgs?.forEach {
		val msg = it.message ?: return
		context.dataFeedDispatcher.emit(msg)
	}

	message.rpcMsgs?.forEach {
		val msg = it.message ?: return
		val replyTo = it.replyTo
		if (replyTo != null) {
			context.rpcRequests.tryResolve(replyTo, msg)
			return@forEach
		}
		context.rpcReplies.record(msg, it.txId)
		context.rpcDispatcher.emit(msg)
	}

	message.driverMsgs?.forEach {
		val msg = it.message ?: return
		val replyTo = it.replyTo
		if (replyTo != null) {
			context.driverRequests.tryResolve(replyTo, msg)
			return@forEach
		}
		context.driverReplies.record(msg, it.txId)
		context.driverDispatcher.emit(msg)
	}
}

class SolarXRBridge(
	val id: Int,
	val context: SolarXRBridgeContext,
	val appContext: AppContextProvider,
	val dataFeedDispatcher: EventDispatcher<DataFeedMessage> = EventDispatcher("SolarXR[$id].dataFeed", context.scope, capacity = 32),
	val rpcDispatcher: EventDispatcher<RpcMessage> = EventDispatcher("SolarXR[$id].rpc", context.scope, capacity = 64),
	val driverDispatcher: EventDispatcher<DriverMessage> = EventDispatcher("SolarXR[$id].driver", context.scope, capacity = 64),
	val outbound: EventDispatcher<MessageBundle> = EventDispatcher("SolarXR[$id].outbound", context.scope, capacity = 64),
	private val managedContext: ManagedContext<SolarXRBridgeState, SolarXRBridgeActions>? = null,
) {
	// Jobs are mutable handles with no meaningful equality; storing them in state
	// would break distinctUntilChanged and data class copy semantics.
	internal var datafeedTimers: List<Job> = emptyList()
	fun dispose() = managedContext?.dispose()

	internal val rpcReplies = PendingReplies<RpcMessage>()
	internal val driverReplies = PendingReplies<DriverMessage>()

	@PublishedApi internal val rpcRequests = PendingRequests<RpcMessage>()

	@PublishedApi internal val driverRequests = PendingRequests<DriverMessage>()

	fun txIdFor(message: RpcMessage): UInt? = rpcReplies.consume(message)
	fun driverTxIdFor(message: DriverMessage): UInt? = driverReplies.consume(message)

	inline fun <reified P : RpcMessage> onRpc(noinline action: suspend (P, UInt?) -> Unit): Subscription<RpcMessage, P> = rpcDispatcher.on<P> { msg -> action(msg, txIdFor(msg)) }
	inline fun <reified P : DriverMessage> onDriverMessage(noinline action: suspend (P, UInt?) -> Unit): Subscription<DriverMessage, P> = driverDispatcher.on<P> { msg -> action(msg, driverTxIdFor(msg)) }

	suspend fun sendRpc(message: RpcMessage, replyTo: UInt? = null, txId: UInt? = null) = outbound.emit(MessageBundle(rpcMsgs = listOf(RpcMessageHeader(txId = txId, replyTo = replyTo, message = message))))
	suspend fun sendDriverMessage(message: DriverMessage, replyTo: UInt? = null, txId: UInt? = null) = outbound.emit(MessageBundle(driverMsgs = listOf(DriverMessageHeader(txId = txId, replyTo = replyTo, message = message))))

	suspend inline fun <reified R : RpcMessage> requestRpc(message: RpcMessage, timeout: Duration = 10.seconds): R = rpcRequests.request(timeout, message) { msg, newTxId -> sendRpc(msg, txId = newTxId) }
	suspend inline fun <reified R : DriverMessage> requestDriverMessage(message: DriverMessage, timeout: Duration = 10.seconds): R = driverRequests.request(timeout, message) { msg, newTxId -> sendDriverMessage(msg, txId = newTxId) }

	suspend fun sendDataFeed(frame: DataFeedMessageHeader) = outbound.emit(MessageBundle(dataFeedMsgs = listOf(frame)))

	fun disconnectDriverTrackers() {
		val driverName = context.state.value.driverName ?: return
		// If the connection was a driver connection, we set the status of its trackers to disconnected
		appContext.server.context.state.value.trackers.values
			.filter {
				val state = it.context.state.value
				state.origin == DeviceOrigin.DRIVER && state.driverName == driverName
			}
			.forEach {
				it.context.dispatchAll(
					listOf(
						TrackerActions.SetDriverName(null),
						TrackerActions.SetStatus(TrackerStatus.DISCONNECTED),
					),
				)
			}
	}

	fun disconnect() {
		dispose()
		appContext.server.context.dispatch(VRServerActions.SolarXRDisconnected(id))

		disconnectDriverTrackers()
	}

	fun startObserving() = context.observeAll(this)

	companion object {
		fun buildBehaviours(appContext: AppContextProvider): List<SolarXRBridgeBehaviour> = buildList {
			add(DataFeedInitBehaviour(appContext.server, appContext.skeleton))
			add(SerialBehaviour(appContext.serialServer))
			add(FirmwareBehaviour(appContext.server, appContext.firmwareManager))
			appContext.vrcConfigManager?.let { vrc ->
				add(VrcBehaviour(vrc, appContext.server, userHeight = { appContext.skeleton.context.state.value.skeletonHeight }))
			}
			add(ResetsBehaviour(appContext.config.settings))
			add(TapDetectionBehaviour(appContext.config.settings, appContext.tapDetectionManager))
			add(VrcOscBehaviour(appContext.config.settings, appContext.vrcOscManager))
			add(VmcBehaviour(appContext.config.settings, appContext.vmcManager))
			add(HeightCalibrationBehaviour(appContext.heightCalibrationManager))
			add(ProvisioningBehaviour(appContext.server, appContext.provisioningManager))
			add(BoneRoutingBehaviour(appContext))
			add(DriverSettingsBehaviour(appContext))
			add(HIDSettingsBehaviour(appContext.config.settings))
			add(SkeletonSettingsBehaviour(appContext.config.settings))
			add(SkeletonProportionsBehaviour(appContext.config.userConfig, appContext.skeleton))
			add(TrackingChecklistBehaviour(appContext.trackingChecklist, appContext.config.settings))
			add(AssignTrackerBehaviour(appContext.server))
			add(DongleSettingsBehaviour(appContext.server))
			add(DriverHandshakeBehaviour(appContext))
			add(DriverOutgoingTrackersBehaviour(appContext))
			add(DriverIncomingTrackersBehaviour(appContext))
			add(MagBehaviour(appContext))
			add(TimeoutSettingsBehaviour(appContext.config.settings))
			add(KnownTrackersBehaviour(appContext.config.settings))
			add(BvhBehaviour(appContext.bvhManager))
			add(InstalledInfoBehaviour())
			add(KeybindsBehaviour(appContext.config.settings, appContext.keybindManager))
			add(SessionCalibrationBehaviour(appContext.resetsManager))
			add(StayAlignedBehaviour(appContext.config.settings, appContext.server))
		}

		fun create(
			id: Int,
			appContext: AppContextProvider,
			scope: CoroutineScope,
			extraBehaviours: (AppContextProvider) -> List<SolarXRBridgeBehaviour> = { emptyList() },
		): SolarXRBridge {
			val managedContext = ManagedContext.create(
				initialState = SolarXRBridgeState(dataFeedConfigs = listOf()),
				scope = scope,
				behaviours = buildBehaviours(appContext) + extraBehaviours(appContext),
				name = "SolarXR[$id]",
			)

			val bridge = SolarXRBridge(
				id = id,
				context = managedContext.context,
				appContext = appContext,
				managedContext = managedContext,
			)
			return bridge
		}
	}
}
