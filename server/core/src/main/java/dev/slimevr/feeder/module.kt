package dev.slimevr.feeder

import dev.slimevr.AppContextProvider
import dev.slimevr.EventDispatcher
import dev.slimevr.VRServerActions
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.context.ManagedContext
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.CoroutineScope

data class FeederBridgeState(
	val protocolVersion: Int,
	val firmware: String?,
)

sealed interface FeederBridgeActions {
	data class UpdateProtocolVersion(val version: Int, val firmware: String?) : FeederBridgeActions
}

sealed interface FeederBridgeInbound {
	data class Version(val protocolVersion: Int, val firmware: String?) : FeederBridgeInbound
	data class TrackerAdded(val serial: String) : FeederBridgeInbound
	data class TrackerPosition(val trackerId: Int, val rotation: Quaternion, val position: Vector3?) : FeederBridgeInbound
}

typealias FeederBridgeContext = Context<FeederBridgeState, FeederBridgeActions>
typealias FeederBridgeBehaviour = Behaviour<FeederBridgeState, FeederBridgeActions, FeederBridge>

class FeederBridge(
	val id: Int,
	val context: FeederBridgeContext,
	val appContext: AppContextProvider,
	val inbound: EventDispatcher<FeederBridgeInbound> = EventDispatcher(),
	private val managedContext: ManagedContext<FeederBridgeState, FeederBridgeActions>? = null,
) {
	fun dispose() = managedContext?.dispose()

	fun startObserving() = context.observeAll(this)

	fun disconnect() {
		dispose()
		appContext.server.context.dispatch(VRServerActions.FeederDisconnected(id))
	}

	companion object {
		fun create(id: Int, appContext: AppContextProvider, scope: CoroutineScope): FeederBridge {
			val behaviours = listOf(FeederBaseBehaviour)

			val managedContext = ManagedContext.create(
				initialState = FeederBridgeState(protocolVersion = 0, firmware = null),
				scope = scope,
				behaviours = behaviours,
				name = "Feeder[$id]",
			)

			val bridge = FeederBridge(id = id, context = managedContext.context, appContext = appContext, managedContext = managedContext)
			bridge.startObserving()
			appContext.server.context.dispatch(VRServerActions.FeederConnected(bridge))

			return bridge
		}
	}
}
