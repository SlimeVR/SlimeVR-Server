package dev.slimevr.hid

import dev.slimevr.VRServerActions
import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import dev.slimevr.device.Device
import dev.slimevr.device.DeviceActions
import dev.slimevr.logging.AppLogger
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.util.timeSource
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import solarxr_protocol.datatypes.DeviceOrigin
import solarxr_protocol.datatypes.TrackerStatus
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class HIDRegistrationBehaviour : HIDReceiverBehaviour {
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

class HIDReceiverConfigBehaviour(
	private val settings: Settings,
	private val serialNumber: String,
) : HIDReceiverBehaviour {
	override fun observe(receiver: HIDReceiver) {
		receiver.context.state
			.distinctUntilChangedBy { it.customName }
			.drop(1)
			.onEach { state ->
				settings.context.dispatch(SettingsActions.UpdateDongle(serialNumber) { copy(customName = state.customName) })
			}
			.launchIn(receiver.context.scope)
	}
}

class HIDDeviceInfoBehaviour : HIDReceiverBehaviour {
	override fun observe(receiver: HIDReceiver) {
		receiver.packetEvents.on<HIDDeviceInfo> { packet ->
			val device = receiver.getDevice(packet.hidId) ?: return@on
			val deviceState = device.context.state.value

			device.context.dispatch(
				DeviceActions.Update {
					copy(
						boardType = packet.boardType,
						mcuType = packet.mcuType,
						firmwareVersion = packet.firmwareVersion,
						firmwareDate = packet.firmwareDate,
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
						imuType = packet.imuType,
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
			tracker.setRotation(rotation = packet.rotation, acceleration = packet.acceleration)
		}.launchIn(receiver.context.scope)

		receiver.packetEvents.on<HIDRotationBattery> { packet ->
			val tracker = receiver.getTracker(packet.hidId) ?: return@on
			tracker.setRotation(rotation = packet.rotation, acceleration = packet.acceleration)
		}.launchIn(receiver.context.scope)

		receiver.packetEvents.on<HIDRotationMag> { packet ->
			val tracker = receiver.getTracker(packet.hidId) ?: return@on
			tracker.setRotation(rotation = packet.rotation, magnetometer = packet.magnetometer)
		}.launchIn(receiver.context.scope)

		receiver.packetEvents.on<HIDRotationButton> { packet ->
			val tracker = receiver.getTracker(packet.hidId) ?: return@on
			tracker.setRotation(rotation = packet.rotation, acceleration = packet.acceleration)
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
	val startedAt = timeSource.markNow()
	val sleepJobs = mutableMapOf<Int, Job>()
	val idleJobs = mutableMapOf<Int, Job>()
	val lastSeen = mutableMapOf<Int, Duration>()

	override fun observe(receiver: HIDReceiver) {
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

	override fun onDisconnect() {
		for (job in sleepJobs.values + idleJobs.values) {
			job.cancel()
		}
		sleepJobs.clear()
		idleJobs.clear()
	}
}

class HIDStatusBehaviour : HIDReceiverBehaviour {
	override fun observe(receiver: HIDReceiver) {
		receiver.packetEvents.on<HIDStatus> { packet ->
			val device = receiver.getDevice(packet.hidId) ?: return@on
			val packetsReceived = packet.packetsReceived.toLong()
			val packetsLost = packet.packetsLost.toLong()

			// Dispatched together so DeviceTelemetryBehaviour sees one atomic
			// state change per packet, not two separate samples microseconds apart.
			if (receiver.getTracker(packet.hidId) != null) {
				device.context.dispatch(
					DeviceActions.Update {
						copy(
							status = packet.status,
							signalStrength = packet.rssi,
							packetsReceived = packetsReceived,
							packetsLost = packetsLost,
						)
					},
				)
			} else {
				device.context.dispatch(DeviceActions.PacketStats(packetsReceived, packetsLost))
			}
		}.launchIn(receiver.context.scope)
	}
}
