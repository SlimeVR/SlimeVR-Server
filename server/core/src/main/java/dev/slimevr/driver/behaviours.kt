package dev.slimevr.driver

import dev.slimevr.AppLogger
import dev.slimevr.VRServerActions
import dev.slimevr.device.Device
import dev.slimevr.device.DeviceActions
import dev.slimevr.device.DeviceOrigin
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.datatypes.hardware_info.ImuType

object DriverBaseBehaviour : DriverBridgeBehaviour {
	override fun reduce(state: DriverBridgeState, action: DriverBridgeActions): DriverBridgeState = when (action) {
		is DriverBridgeActions.AddTracker -> state.copy(trackers = state.trackers + (action.id to action.tracker))
		is DriverBridgeActions.UpdateProtocolVersion -> state.copy(protocolVersion = action.version)
	}

	override fun observe(receiver: DriverBridge) {
		receiver.inbound.on<DriverBridgeInbound.Version> { event ->
			receiver.context.dispatch(DriverBridgeActions.UpdateProtocolVersion(event.protocolVersion))
		}

		receiver.inbound.on<DriverBridgeInbound.TrackerAdded> { event ->
			handleTrackerAdded(receiver, event.id, event.name, event.manufacturer, event.serial)
		}

		receiver.inbound.on<DriverBridgeInbound.TrackerPosition> { event ->
			val tracker = receiver.context.state.value.trackers[event.trackerId]
			if (tracker != null) {
				tracker.context.dispatch(TrackerActions.SetRotation(rotation = event.rotation))
			} else {
				AppLogger.steamvr.warn("Failed to find tracker ${event.trackerId}")
			}
		}

		receiver.inbound.on<DriverBridgeInbound.TrackerBattery> { event ->
			receiver.context.state.value.trackers[event.id]?.let { tracker ->
				val device = tracker.server.getDevice(tracker.context.state.value.deviceId) ?: error("could not find device")
				device.context.dispatch(
					DeviceActions.Update {
						copy(
							batteryLevel = event.batteryLevel / 100f,
							batteryVoltage = if (event.charging) 4.3f else 3.7f,
						)
					},
				)
			}
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

	private fun handleTrackerAdded(receiver: DriverBridge, id: Int, name: String, manufacturer: String, serial: String) {
		val server = receiver.appContext.server
		val settings = receiver.appContext.config.settings
		val scope = server.context.scope
		val existingTracker = server.context.state.value.trackers.values
			.find { tracker -> tracker.context.state.value.hardwareId == serial }

		val device = if (existingTracker != null) {
			server.getDevice(existingTracker.context.state.value.deviceId)
				?: error("could not find existing device for serial $serial")
		} else {
			val deviceId = server.nextHandle()
			val newDevice = Device.create(
				scope = scope,
				id = deviceId,
				name = name,
				manufacturer = manufacturer,
				address = serial,
				macAddress = serial,
				origin = DeviceOrigin.DRIVER,
				protocolVersion = 0,
			)
			server.context.dispatch(VRServerActions.NewDevice(deviceId, newDevice))

			val trackerId = server.nextHandle()
			val tracker = Tracker.create(
				scope = scope,
				id = trackerId,
				name = name,
				deviceId = deviceId,
				sensorType = null,
				hardwareId = serial,
				origin = DeviceOrigin.DRIVER,
				server = server,
				settings = settings,
			)
			server.context.dispatch(VRServerActions.NewTracker(trackerId, tracker))

			receiver.context.dispatch(DriverBridgeActions.AddTracker(id, tracker))
			tracker.context.dispatch(TrackerActions.SetStatus(TrackerStatus.OK))

			newDevice
		}

		device.context.dispatch(DeviceActions.Update { copy(protocolVersion = 0) })
	}
}
