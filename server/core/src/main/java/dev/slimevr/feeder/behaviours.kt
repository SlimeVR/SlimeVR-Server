package dev.slimevr.feeder

import dev.slimevr.VRServerActions
import dev.slimevr.device.Device
import dev.slimevr.device.DeviceActions
import dev.slimevr.device.DeviceOrigin
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import solarxr_protocol.datatypes.hardware_info.ImuType

object FeederBaseBehaviour : FeederBridgeBehaviour {
	override fun reduce(state: FeederBridgeState, action: FeederBridgeActions): FeederBridgeState = when (action) {
		is FeederBridgeActions.UpdateProtocolVersion -> state.copy(protocolVersion = action.version, firmware = action.firmware)
	}

	override fun observe(receiver: FeederBridge) {
		receiver.inbound.on<FeederBridgeInbound.Version> { event ->
			receiver.context.dispatch(FeederBridgeActions.UpdateProtocolVersion(event.protocolVersion, event.firmware))
		}

		receiver.inbound.on<FeederBridgeInbound.TrackerAdded> { event ->
			handleTrackerAdded(receiver, event.serial)
		}

		receiver.inbound.on<FeederBridgeInbound.TrackerPosition> { event ->
			receiver.appContext.server.getTracker(event.trackerId)?.context?.dispatch(
				TrackerActions.SetRotation(rotation = event.rotation),
			)
		}
	}

	private fun handleTrackerAdded(receiver: FeederBridge, serial: String) {
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
				address = serial,
				macAddress = serial,
				origin = DeviceOrigin.FEEDER,
				protocolVersion = 0,
			)
			server.context.dispatch(VRServerActions.NewDevice(deviceId, newDevice))

			val trackerId = server.nextHandle()
			val tracker = Tracker.create(
				scope = scope,
				id = trackerId,
				deviceId = deviceId,
				sensorType = ImuType.MPU9250,
				hardwareId = serial,
				origin = DeviceOrigin.FEEDER,
				server = server,
				settings = settings,
			)
			server.context.dispatch(VRServerActions.NewTracker(trackerId, tracker))

			newDevice
		}

		device.context.dispatch(DeviceActions.Update { copy(protocolVersion = 0) })
	}
}
