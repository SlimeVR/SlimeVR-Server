package dev.slimevr.driver

import dev.slimevr.AppContextProvider
import dev.slimevr.EventDispatcher
import dev.slimevr.VRServerActions
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.context.ManagedContext
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.CoroutineScope
import solarxr_protocol.datatypes.TrackerStatus

data class DriverBridgeState(
	val protocolVersion: Int,
	val trackers: Map<Int, Tracker>,
)

sealed interface DriverBridgeActions {
	data class AddTracker(val id: Int, val tracker: Tracker) : DriverBridgeActions
	data class UpdateProtocolVersion(val version: Int) : DriverBridgeActions
}

sealed interface DriverBridgeInbound {
	data class Version(val protocolVersion: Int) : DriverBridgeInbound
	data class TrackerAdded(val id: Int, val serial: String) : DriverBridgeInbound
	data class TrackerPosition(val trackerId: Int, val rotation: Quaternion, val position: Vector3?) : DriverBridgeInbound
	data class TrackerBattery(val id: Int, val batteryLevel: Float, val charging: Boolean) : DriverBridgeInbound
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
	private val managedContext: ManagedContext<DriverBridgeState, DriverBridgeActions>? = null,
) {
	fun dispose() = managedContext?.dispose()

	fun startObserving() = context.observeAll(this)

	fun disconnect() {
		dispose()
		appContext.server.context.dispatch(VRServerActions.DriverDisconnected(id))
		context.state.value.trackers.forEach { (_, tracker) ->
			tracker.context.dispatch(TrackerActions.SetStatus(TrackerStatus.DISCONNECTED))
		}
	}

	companion object {
		fun create(id: Int, appContext: AppContextProvider, scope: CoroutineScope): DriverBridge {
			val behaviours = listOf(DriverBaseBehaviour)

			val managedContext = ManagedContext.create(
				initialState = DriverBridgeState(
					protocolVersion = 0,
					trackers = emptyMap(),
				),
				scope = scope,
				behaviours = behaviours,
				name = "Driver[$id]",
			)

			val bridge = DriverBridge(id = id, context = managedContext.context, appContext = appContext, managedContext = managedContext)
			bridge.startObserving()
			appContext.server.context.dispatch(VRServerActions.DriverConnected(bridge))

			return bridge
		}
	}
}
