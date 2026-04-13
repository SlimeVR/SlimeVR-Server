package dev.slimevr.driver

import dev.slimevr.EventDispatcher
import dev.slimevr.VRServer
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
	val serverContext: VRServer,
	val inbound: EventDispatcher<DriverBridgeInbound> = EventDispatcher(),
	val outbound: EventDispatcher<DriverBridgeOutbound> = EventDispatcher(),
) {
	fun disconnect() {
		serverContext.context.dispatch(VRServerActions.DriverDisconnected(id))
	}

	companion object {
		fun create(id: Int, serverContext: VRServer, scope: CoroutineScope): DriverBridge {
			val behaviours = listOf(DriverBaseBehaviour(serverContext))

			val context = Context.create(
				initialState = DriverBridgeState(protocolVersion = 0),
				scope = scope,
				behaviours = behaviours,
			)

			val bridge = DriverBridge(id = id, context = context, serverContext = serverContext)

			behaviours.forEach { it.observe(bridge) }

			serverContext.context.dispatch(VRServerActions.DriverConnected(bridge))

			return bridge
		}
	}
}