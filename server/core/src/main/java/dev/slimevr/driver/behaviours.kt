package dev.slimevr.driver

import dev.slimevr.device.DeviceOrigin
import dev.slimevr.tracker.TrackerActions
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

object DriverBaseBehaviour : DriverBridgeBehaviour {
	override fun reduce(state: DriverBridgeState, action: DriverBridgeActions): DriverBridgeState = when (action) {
		is DriverBridgeActions.UpdateProtocolVersion -> state.copy(protocolVersion = action.version)
	}

	override fun observe(receiver: DriverBridge) {
		receiver.inbound.on<DriverBridgeInbound.Version> { event ->
			receiver.context.dispatch(DriverBridgeActions.UpdateProtocolVersion(event.protocolVersion))
		}

		receiver.inbound.on<DriverBridgeInbound.TrackerPosition> { event ->
			receiver.appContext.server.getTracker(event.trackerId)?.context?.dispatch(
				TrackerActions.Update { copy(rawRotation = event.rotation) },
			)
		}

		// Should be safe: StateFlow never delivers two emissions concurrently to the same collector.
		val subscribedTrackers = mutableSetOf<Int>()

		receiver.appContext.server.context.state
			.onEach { state ->
				state.trackers.values.forEach { tracker ->
					val ts = tracker.context.state.value
					if (ts.origin == DeviceOrigin.DRIVER) return@forEach
					if (subscribedTrackers.add(ts.id)) {
						receiver.outbound.emit(
							DriverBridgeOutbound.TrackerAdded(
								trackerId = ts.id,
								serial = ts.hardwareId,
								name = ts.customName ?: ts.name,
							),
						)
						tracker.context.state
							.onEach { trackerState ->
								receiver.outbound.emit(
									DriverBridgeOutbound.TrackerPosition(
										trackerId = trackerState.id,
										rotation = trackerState.rawRotation,
										position = trackerState.position,
									),
								)
							}
							.launchIn(receiver.context.scope)
					}
				}
			}
			.launchIn(receiver.context.scope)
	}
}