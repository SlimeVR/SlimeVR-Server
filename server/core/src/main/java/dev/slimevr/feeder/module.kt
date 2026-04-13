package dev.slimevr.feeder

import dev.slimevr.EventDispatcher
import dev.slimevr.VRServer
import dev.slimevr.VRServerActions
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
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
	val serverContext: VRServer,
	val inbound: EventDispatcher<FeederBridgeInbound> = EventDispatcher(),
) {
	fun disconnect() {
		serverContext.context.dispatch(VRServerActions.FeederDisconnected(id))
	}

	companion object {
		fun create(id: Int, serverContext: VRServer, scope: CoroutineScope): FeederBridge {
			val behaviours = listOf(FeederBaseBehaviour(serverContext))

			val context = Context.create(
				initialState = FeederBridgeState(protocolVersion = 0, firmware = null),
				scope = scope,
				behaviours = behaviours,
			)

			val bridge = FeederBridge(id = id, context = context, serverContext = serverContext)

			behaviours.forEach { it.observe(bridge) }

			serverContext.context.dispatch(VRServerActions.FeederConnected(bridge))

			return bridge
		}
	}
}