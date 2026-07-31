package dev.slimevr.hid

import dev.slimevr.VRServerActions
import dev.slimevr.device.Device
import dev.slimevr.device.DeviceActions
import dev.slimevr.device.DeviceOrigin
import dev.slimevr.logging.AppLogger
import dev.slimevr.util.timeSource
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import solarxr_protocol.datatypes.TrackerStatus
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class HIDRegistrationBehaviour : HIDReceiverBehaviour {
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
		receiver.packetEvents.on<HIDDeviceRegister> { packet ->
			val state = receiver.context.state.value
			val existing = state.trackers[packet.hidId]
			if (existing != null) return@on

			val existingDevice = receiver.appContext.server.context.state.value.devices.values
				.find { it.context.state.value.macAddress == packet.address && it.context.state.value.origin == DeviceOrigin.HID }

			if (existingDevice != null) {
				receiver.context.dispatch(HIDReceiverActions.DeviceRegistered(packet.hidId, packet.address, existingDevice.context.state.value.id))
				AppLogger.hid.info("Reconnected HID device ${packet.address} (hidId=${packet.hidId})")
				return@on
			}

			val deviceId = receiver.appContext.server.nextHandle()
			val device = Device.create(
				scope = receiver.appContext.server.context.scope,
				appContext = receiver.appContext,
				id = deviceId,
				address = packet.address,
				macAddress = packet.address,
				origin = DeviceOrigin.HID,
				protocolVersion = 0,
			)
			receiver.appContext.server.context.dispatch(VRServerActions.NewDevice(deviceId, device))
			receiver.context.dispatch(HIDReceiverActions.DeviceRegistered(packet.hidId, packet.address, deviceId))
			AppLogger.hid.info("Registered HID device ${packet.address} (hidId=${packet.hidId})")
		}.launchIn(receiver.context.scope)
	}
}

class HIDDeviceInfoBehaviour : HIDReceiverBehaviour {
	override fun reduce(state: HIDReceiverState, action: HIDReceiverActions): HIDReceiverState = when (action) {
		is HIDReceiverActions.TrackerRegistered -> {
			val existing = state.trackers[action.hidId] ?: return state
			state.copy(trackers = state.trackers + (action.hidId to existing.copy(trackerId = action.trackerId)))
		}

		else -> state
	}

	override fun observe(receiver: HIDReceiver) {
		receiver.packetEvents.on<HIDDeviceInfo> { packet ->
			val device = receiver.getDevice(packet.hidId) ?: return@on
			val deviceState = device.context.state.value

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
				?: receiver.appContext.server.context.state.value.trackers.values
					.find { it.context.state.value.hardwareId == deviceState.address && it.context.state.value.origin == DeviceOrigin.HID }
				?: run {
					val trackerId = receiver.appContext.server.nextHandle()
					val newTracker = Tracker.create(
						scope = receiver.appContext.server.context.scope,
						id = trackerId,
						deviceId = deviceState.id,
						sensorType = packet.imuType,
						hardwareId = deviceState.address,
						origin = DeviceOrigin.HID,
						appContext = receiver.appContext,
					)
					receiver.appContext.server.context.dispatch(VRServerActions.NewTracker(trackerId, newTracker))
					newTracker
				}
			receiver.context.dispatch(HIDReceiverActions.TrackerRegistered(packet.hidId, tracker.context.state.value.id))
			// HID does not have a rest calibration signal
			tracker.context.dispatch(TrackerActions.Update { copy(imuType = packet.imuType, completedRestCalibration = true, magStatus = packet.magStatus) })
			tracker.context.dispatch(TrackerActions.SetStatus(TrackerStatus.OK))
		}.launchIn(receiver.context.scope)
	}
}

class HIDRotationBehaviour : HIDReceiverBehaviour {
	override fun observe(receiver: HIDReceiver) {
		receiver.packetEvents.on<HIDRotation> { packet ->
			val tracker = receiver.getTracker(packet.hidId) ?: return@on
			tracker.context.dispatch(TrackerActions.SetRotation(rotation = packet.rotation, acceleration = packet.acceleration))
		}.launchIn(receiver.context.scope)

		receiver.packetEvents.on<HIDRotationBattery> { packet ->
			val tracker = receiver.getTracker(packet.hidId) ?: return@on
			tracker.context.dispatch(TrackerActions.SetRotation(rotation = packet.rotation, acceleration = packet.acceleration))
		}.launchIn(receiver.context.scope)

		receiver.packetEvents.on<HIDRotationMag> { packet ->
			val tracker = receiver.getTracker(packet.hidId) ?: return@on
			tracker.context.dispatch(TrackerActions.SetRotation(rotation = packet.rotation, magnetometer = packet.magnetometer))
		}.launchIn(receiver.context.scope)

		receiver.packetEvents.on<HIDRotationButton> { packet ->
			val tracker = receiver.getTracker(packet.hidId) ?: return@on
			tracker.context.dispatch(TrackerActions.SetRotation(rotation = packet.rotation, acceleration = packet.acceleration))
		}.launchIn(receiver.context.scope)
	}
}

class HIDBatteryBehaviour : HIDReceiverBehaviour {
	override fun observe(receiver: HIDReceiver) {
		receiver.packetEvents.on<HIDRotationBattery> { packet ->
			receiver.getDevice(packet.hidId)?.context?.dispatch(
				DeviceActions.Update {
					copy(batteryLevel = packet.batteryLevel, batteryVoltage = packet.batteryVoltage, signalStrength = packet.rssi)
				},
			)
		}.launchIn(receiver.context.scope)

		receiver.packetEvents.on<HIDRotationButton> { packet ->
			receiver.getDevice(packet.hidId)?.context?.dispatch(
				DeviceActions.Update { copy(signalStrength = packet.rssi) },
			)
		}.launchIn(receiver.context.scope)

		receiver.packetEvents.on<HIDData> { packet ->
			receiver.getDevice(packet.hidId)?.context?.dispatch(
				DeviceActions.Update { copy(signalStrength = packet.rssi) },
			)
		}.launchIn(receiver.context.scope)

		receiver.packetEvents.on<HIDRuntime> { packet ->
			// -1: not yet known (keep existing value); 0: N/A (e.g. charging)
			if (packet.runtime >= 0) {
				receiver.getDevice(packet.hidId)?.context?.dispatch(
					DeviceActions.Update { copy(batteryRemainingRuntime = packet.runtime) },
				)
			}
		}.launchIn(receiver.context.scope)
	}
}

private val hidTimeout = 2.seconds

class HIDSleepBehaviour : HIDReceiverBehaviour {
	override fun observe(receiver: HIDReceiver) {
		val startedAt = timeSource.markNow()
		val sleepJobs = mutableMapOf<Int, Job>()
		val idleJobs = mutableMapOf<Int, Job>()
		val lastSeen = mutableMapOf<Int, Duration>()

		fun scheduleSleep(hidId: Int, timeoutMs: Int) {
			if (timeoutMs == 0) return
			sleepJobs[hidId]?.cancel()
			if (timeoutMs == 65535) {
				sleepJobs.remove(hidId)
				return
			}
			sleepJobs[hidId] = receiver.context.scope.launch {
				delay(timeoutMs.toLong())
				receiver.getTracker(hidId)?.context?.dispatch(TrackerActions.SetStatus(TrackerStatus.SLEEPING))
			}
		}

		fun armIdleTimeout(hidId: Int) {
			lastSeen[hidId] = startedAt.elapsedNow()
			if (idleJobs[hidId]?.isActive == true) return
			idleJobs[hidId] = receiver.context.scope.launch {
				var remaining = hidTimeout
				while (remaining > Duration.ZERO) {
					delay(remaining)
					remaining = (lastSeen[hidId] ?: Duration.ZERO) + hidTimeout - startedAt.elapsedNow()
				}
				receiver.getTracker(hidId)?.context?.dispatch(TrackerActions.SetStatus(TrackerStatus.SLEEPING))
			}
		}

		fun onPacket(hidId: Int) {
			val tracker = receiver.getTracker(hidId) ?: return
			if (tracker.context.state.value.status == TrackerStatus.SLEEPING) {
				sleepJobs[hidId]?.cancel()
				sleepJobs.remove(hidId)
				tracker.context.dispatch(TrackerActions.SetStatus(TrackerStatus.OK))
			}
			armIdleTimeout(hidId)
		}

		receiver.packetEvents.on<HIDRotationButton> { packet ->
			onPacket(packet.hidId)
			scheduleSleep(packet.hidId, packet.timeout)
		}.launchIn(receiver.context.scope)

		receiver.packetEvents.on<HIDData> { packet ->
			onPacket(packet.hidId)
			scheduleSleep(packet.hidId, packet.timeout)
		}.launchIn(receiver.context.scope)

		receiver.packetEvents.on<HIDRotation> { packet -> onPacket(packet.hidId) }.launchIn(receiver.context.scope)
		receiver.packetEvents.on<HIDRotationBattery> { packet -> onPacket(packet.hidId) }.launchIn(receiver.context.scope)
		receiver.packetEvents.on<HIDRotationMag> { packet -> onPacket(packet.hidId) }.launchIn(receiver.context.scope)
		receiver.packetEvents.on<HIDStatus> { packet -> onPacket(packet.hidId) }.launchIn(receiver.context.scope)
		receiver.packetEvents.on<HIDRuntime> { packet -> onPacket(packet.hidId) }.launchIn(receiver.context.scope)
	}
}

class HIDStatusBehaviour : HIDReceiverBehaviour {
	override fun observe(receiver: HIDReceiver) {
		receiver.packetEvents.on<HIDStatus> { packet ->
			if (receiver.getTracker(packet.hidId) == null) return@on
			receiver.getDevice(packet.hidId)?.context?.dispatch(
				DeviceActions.Update { copy(status = packet.status, signalStrength = packet.rssi) },
			)
		}.launchIn(receiver.context.scope)
	}
}

class HIDPacketLossBehaviour : HIDReceiverBehaviour {
	override fun observe(receiver: HIDReceiver) {
		receiver.packetEvents.on<HIDStatus> { packet ->
			receiver.getDevice(packet.hidId)?.context?.dispatch(
				DeviceActions.PacketStats(packetsReceived = packet.packetsReceived.toLong(), packetsLost = packet.packetsLost.toLong()),
			)
		}.launchIn(receiver.context.scope)
	}
}
