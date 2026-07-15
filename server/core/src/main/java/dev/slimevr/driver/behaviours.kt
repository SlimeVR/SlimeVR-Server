package dev.slimevr.driver

import dev.slimevr.VRServerActions
import dev.slimevr.device.Device
import dev.slimevr.device.DeviceActions
import dev.slimevr.device.DeviceOrigin
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.datatypes.TrackerStatus

class DriverOutgoingTrackersBehaviour : DriverBridgeBehaviour {

	override fun observe(receiver: DriverBridge) {
		// Should be safe: StateFlow never delivers two emissions concurrently to the same collector.
		val subscribedTrackers = mutableSetOf<UByte>()

		receiver.appContext.skeleton.computed.onEach { computedBones ->
			val enabledBodyParts =
				receiver.appContext.outputTrackerToggle.context.state.value.trackers
			val serverState = receiver.appContext.server.context.state.value

			computedBones.forEach { (part, state) ->
				val closestTracker =
					serverState.trackers.values.find { it.context.state.value.bodyPart == part }?.context?.state?.value
						?: return@forEach
				val enabled =
					enabledBodyParts.contains(part) // TODO: add paused tracking here
				if (closestTracker.origin == DeviceOrigin.DRIVER) return@forEach
				val closestDevice =
					serverState.devices[closestTracker.deviceId]?.context?.state?.value
						?: error("device should exists")

				val newTracker = subscribedTrackers.add(part.value)
				if (newTracker) {
					receiver.outbound.emit(
						DriverBridgeOutbound.TrackerAdded(
							trackerId = part.value.toInt(),
							part = part,
							serial = closestTracker.hardwareId,
							name = closestTracker.customName ?: closestTracker.name,
							manufacturer = closestDevice.manufacturer,
						),
					)
				}

				if (enabled) {
					receiver.outbound.emit(
						DriverBridgeOutbound.TrackerPosition(
							trackerId = part.value.toInt(),
							rotation = state.rotation,
							position = state.tailPosition,
						),
					)

					receiver.outbound.emit(
						DriverBridgeOutbound.TrackerStatus(
							trackerId = part.value.toInt(),
							battery = closestDevice.batteryLevel,
							charging = closestDevice.batteryVoltage != null && closestDevice.batteryVoltage >= 4.3f,
							status = closestTracker.status,
						),
					)
				} else {
					receiver.outbound.emit(
						DriverBridgeOutbound.TrackerPosition(
							trackerId = part.value.toInt(),
							rotation = Quaternion.NULL,
							position = Vector3.NULL,
						),
					)

					receiver.outbound.emit(
						DriverBridgeOutbound.TrackerStatus(
							trackerId = part.value.toInt(),
							status = TrackerStatus.DISCONNECTED,
							battery = 0f,
							charging = false,
						),
					)
				}
			}
		}.launchIn(receiver.context.scope)
	}
}

class DriverIncomingTrackersBehaviour : DriverBridgeBehaviour {
	override fun reduce(
		state: DriverBridgeState,
		action: DriverBridgeActions,
	): DriverBridgeState = when (action) {
		is DriverBridgeActions.AddTracker -> state.copy(trackers = state.trackers + (action.id to action.trackerId))
		is DriverBridgeActions.UpdateProtocolVersion -> state.copy(protocolVersion = action.version)
	}

	override fun observe(receiver: DriverBridge) {
		receiver.inbound.on<DriverBridgeInbound.Version> { event ->
			receiver.context.dispatch(DriverBridgeActions.UpdateProtocolVersion(event.protocolVersion))
		}

		receiver.inbound.on<DriverBridgeInbound.TrackerAdded> { event ->
			handleTrackerAdded(
				receiver,
				event.id,
				event.name,
				event.manufacturer,
				event.serial,
			)
		}

		receiver.inbound.on<DriverBridgeInbound.TrackerPosition> { event ->
			val trackerId = receiver.context.state.value.trackers[event.id] ?: return@on
			receiver.appContext.server.getTracker(trackerId)?.let { tracker ->
				tracker.context.dispatch(
					TrackerActions.Update {
						copy(rawRotation = event.rotation, position = event.position)
					},
				)
			}
		}

		receiver.inbound.on<DriverBridgeInbound.TrackerBattery> { event ->
			val trackerId = receiver.context.state.value.trackers[event.id] ?: return@on
			receiver.appContext.server.getTracker(trackerId)?.let { tracker ->
				val device =
					tracker.appContext.server.getDevice(tracker.context.state.value.deviceId)
						?: error("could not find device")
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
	}

	private fun handleTrackerAdded(
		receiver: DriverBridge,
		id: Int,
		name: String,
		manufacturer: String,
		serial: String,
	) {
		val server = receiver.appContext.server
		val scope = server.context.scope
		val existingTracker = server.context.state.value.trackers.values
			.find { tracker -> tracker.context.state.value.hardwareId == serial }

		val (device, tracker) = if (existingTracker != null) {
			val device = server.getDevice(existingTracker.context.state.value.deviceId)
				?: error("could not find existing device for serial $serial")
			Pair(device, existingTracker)
		} else {
			val deviceId = server.nextHandle()
			val newDevice = Device.create(
				scope = scope,
				appContext = receiver.appContext,
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
				appContext = receiver.appContext,
			)
			server.context.dispatch(VRServerActions.NewTracker(trackerId, tracker))

			Pair(newDevice, tracker)
		}

		receiver.context.dispatch(
			DriverBridgeActions.AddTracker(
				id,
				tracker.context.state.value.id,
			),
		)
		tracker.context.dispatch(TrackerActions.SetStatus(TrackerStatus.OK))
		device.context.dispatch(DeviceActions.Update { copy(protocolVersion = 0) })
	}
}
