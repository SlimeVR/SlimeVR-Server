package dev.slimevr.hid

import dev.slimevr.AppLogger
import dev.slimevr.VRServerActions
import dev.slimevr.device.Device
import dev.slimevr.device.DeviceActions
import dev.slimevr.device.DeviceOrigin
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import solarxr_protocol.datatypes.TrackerStatus

object HIDRegistrationBehaviour : HIDReceiverBehaviour {
	override fun reduce(state: HIDReceiverState, action: HIDReceiverActions) = when (action) {
		is HIDReceiverActions.DeviceRegistered -> state.copy(
			trackers = state.trackers +
				(
					action.hidId to HIDTrackerRecord(
						hidId = action.hidId,
						address = action.address,
						deviceId = action.deviceId,
						trackerId = null,
					)
					),
		)

		else -> state
	}

	override fun observe(receiver: HIDReceiver) {
		receiver.packetEvents.onPacket<HIDDeviceRegister> { packet ->
			val state = receiver.context.state.value
			val existing = state.trackers[packet.hidId]
			if (existing != null) return@onPacket

			val existingDevice = receiver.serverContext.context.state.value.devices.values
				.find { it.context.state.value.macAddress == packet.address && it.context.state.value.origin == DeviceOrigin.HID }

			if (existingDevice != null) {
				receiver.context.dispatch(HIDReceiverActions.DeviceRegistered(packet.hidId, packet.address, existingDevice.context.state.value.id))
				AppLogger.hid.info("Reconnected HID device ${packet.address} (hidId=${packet.hidId})")
				return@onPacket
			}

			val deviceId = receiver.serverContext.nextHandle()
			val device = Device.create(
				scope = receiver.serverContext.context.scope,
				id = deviceId,
				address = packet.address,
				macAddress = packet.address,
				origin = DeviceOrigin.HID,
				protocolVersion = 0,
			)
			receiver.serverContext.context.dispatch(VRServerActions.NewDevice(deviceId, device))
			receiver.context.dispatch(HIDReceiverActions.DeviceRegistered(packet.hidId, packet.address, deviceId))
			AppLogger.hid.info("Registered HID device ${packet.address} (hidId=${packet.hidId})")
		}
	}
}

object HIDDeviceInfoBehaviour : HIDReceiverBehaviour {
	override fun reduce(state: HIDReceiverState, action: HIDReceiverActions): HIDReceiverState = when (action) {
		is HIDReceiverActions.TrackerRegistered -> {
			val existing = state.trackers[action.hidId] ?: return state
			state.copy(trackers = state.trackers + (action.hidId to existing.copy(trackerId = action.trackerId)))
		}

		else -> state
	}

	override fun observe(receiver: HIDReceiver) {
		receiver.packetEvents.onPacket<HIDDeviceInfo> { packet ->
			val device = receiver.getDevice(packet.hidId) ?: return@onPacket

			device.context.dispatch(
				DeviceActions.Update {
					copy(
						boardType = packet.boardType,
						mcuType = packet.mcuType,
						firmware = packet.firmware,
						batteryLevel = packet.batteryLevel,
						batteryVoltage = packet.batteryVoltage,
						signalStrength = packet.rssi,
					)
				},
			)

			val tracker = receiver.getTracker(packet.hidId)
			if (tracker == null) {
				val deviceState = device.context.state.value

				val existingTracker = receiver.serverContext.context.state.value.trackers.values
					.find { it.context.state.value.hardwareId == deviceState.address && it.context.state.value.origin == DeviceOrigin.HID }

				if (existingTracker != null) {
					receiver.context.dispatch(HIDReceiverActions.TrackerRegistered(packet.hidId, existingTracker.context.state.value.id))
					existingTracker.context.dispatch(TrackerActions.Update { copy(sensorType = packet.imuType) })
				} else {
					val trackerId = receiver.serverContext.nextHandle()
					val newTracker = Tracker.create(
						scope = receiver.serverContext.context.scope,
						id = trackerId,
						deviceId = deviceState.id,
						sensorType = packet.imuType,
						hardwareId = deviceState.address,
						origin = DeviceOrigin.HID,
						server = receiver.serverContext
					)
					receiver.serverContext.context.dispatch(VRServerActions.NewTracker(trackerId, newTracker))
					receiver.context.dispatch(HIDReceiverActions.TrackerRegistered(packet.hidId, trackerId))
					AppLogger.hid.info("Registered HID tracker for device ${deviceState.address} (hidId=${packet.hidId})")
				}

				device.context.dispatch(DeviceActions.Update { copy(status = TrackerStatus.OK) })
			} else {
				tracker.context.dispatch(TrackerActions.Update { copy(sensorType = packet.imuType) })
			}
		}
	}
}

object HIDRotationBehaviour : HIDReceiverBehaviour {
	override fun observe(receiver: HIDReceiver) {
		receiver.packetEvents.onPacket<HIDRotation> { packet ->
			val tracker = receiver.getTracker(packet.hidId) ?: return@onPacket
			tracker.context.dispatch(TrackerActions.Update { copy(rawRotation = packet.rotation) })
		}

		receiver.packetEvents.onPacket<HIDRotationBattery> { packet ->
			val tracker = receiver.getTracker(packet.hidId) ?: return@onPacket
			tracker.context.dispatch(TrackerActions.Update { copy(rawRotation = packet.rotation) })
		}

		receiver.packetEvents.onPacket<HIDRotationMag> { packet ->
			val tracker = receiver.getTracker(packet.hidId) ?: return@onPacket
			tracker.context.dispatch(TrackerActions.Update { copy(rawRotation = packet.rotation) })
		}

		receiver.packetEvents.onPacket<HIDRotationButton> { packet ->
			val tracker = receiver.getTracker(packet.hidId) ?: return@onPacket
			tracker.context.dispatch(TrackerActions.Update { copy(rawRotation = packet.rotation) })
		}
	}
}

object HIDBatteryBehaviour : HIDReceiverBehaviour {
	override fun observe(receiver: HIDReceiver) {
		receiver.packetEvents.onPacket<HIDRotationBattery> { packet ->
			receiver.getDevice(packet.hidId)?.context?.dispatch(
				DeviceActions.Update {
					copy(batteryLevel = packet.batteryLevel, batteryVoltage = packet.batteryVoltage, signalStrength = packet.rssi)
				},
			)
		}

		receiver.packetEvents.onPacket<HIDRotationButton> { packet ->
			receiver.getDevice(packet.hidId)?.context?.dispatch(
				DeviceActions.Update { copy(signalStrength = packet.rssi) },
			)
		}
	}
}

object HIDStatusBehaviour : HIDReceiverBehaviour {
	override fun observe(receiver: HIDReceiver) {
		receiver.packetEvents.onPacket<HIDStatus> { packet ->
			if (receiver.getTracker(packet.hidId) == null) return@onPacket
			receiver.getDevice(packet.hidId)?.context?.dispatch(
				DeviceActions.Update { copy(status = packet.status, signalStrength = packet.rssi) },
			)
		}
	}
}
