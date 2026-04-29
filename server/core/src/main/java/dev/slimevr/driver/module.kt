package dev.slimevr.driver

import dev.slimevr.AppContextProvider
import dev.slimevr.EventDispatcher
import dev.slimevr.VRServerActions
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.CoroutineScope

data class DriverBridgeState(
	val protocolVersion: Int,
)

sealed interface DriverBridgeActions {
	data class UpdateProtocolVersion(val version: Int) : DriverBridgeActions
}

sealed interface DriverBridgeInbound {
	data class Version(val protocolVersion: Int) : DriverBridgeInbound
	data class TrackerPosition(val trackerId: Int, val rotation: Quaternion, val position: Vector3?) : DriverBridgeInbound
}

sealed interface DriverBridgeOutbound {
	data class TrackerAdded(val trackerId: Int, val serial: String, val name: String) : DriverBridgeOutbound
	data class TrackerPosition(val trackerId: Int, val rotation: Quaternion, val position: Vector3?) : DriverBridgeOutbound
}

typealias DriverBridgeContext = Context<DriverBridgeState, DriverBridgeActions>
typealias DriverBridgeBehaviour = Behaviour<DriverBridgeState, DriverBridgeActions, DriverBridge>

class DriverBridge(
	val id: Int,
	val context: DriverBridgeContext,
	val appContext: AppContextProvider,
	val inbound: EventDispatcher<DriverBridgeInbound> = EventDispatcher(),
	val outbound: EventDispatcher<DriverBridgeOutbound> = EventDispatcher(),
) {
	fun dispose() = context.dispose()

	fun startObserving() = context.observeAll(this)

	fun disconnect() {
		dispose()
		appContext.server.context.dispatch(VRServerActions.DriverDisconnected(id))
	}

	companion object {
		fun create(id: Int, appContext: AppContextProvider, scope: CoroutineScope): DriverBridge {
			val behaviours = listOf(DriverBaseBehaviour)

			val context = Context.create(
				initialState = DriverBridgeState(protocolVersion = 0),
				scope = scope,
				behaviours = behaviours,
				name = "Driver[$id]",
			)

			val bridge = DriverBridge(id = id, context = context, appContext = appContext)
			bridge.startObserving()
			appContext.server.context.dispatch(VRServerActions.DriverConnected(bridge))

			return bridge
		}
	}
}
