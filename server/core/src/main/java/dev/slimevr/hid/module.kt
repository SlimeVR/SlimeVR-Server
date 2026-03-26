package dev.slimevr.hid

import dev.slimevr.AppLogger
import dev.slimevr.EventDispatcher
import dev.slimevr.VRServer
import dev.slimevr.VRServerActions
import dev.slimevr.context.Context
import dev.slimevr.context.CustomBehaviour
import dev.slimevr.context.createContext
import dev.slimevr.device.Device
import dev.slimevr.device.DeviceActions
import dev.slimevr.device.DeviceOrigin
import dev.slimevr.device.createDevice
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.tracker.createTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import solarxr_protocol.datatypes.TrackerStatus

const val HID_TRACKER_RECEIVER_VID = 0x1209
const val HID_TRACKER_RECEIVER_PID = 0x7690
const val HID_TRACKER_PID = 0x7692

// --- State and actions ---

data class HIDTrackerRecord(
	val hidId: Int,
	val address: String,
	val deviceId: Int,
	val trackerId: Int?,
)

data class HIDReceiverState(
	val serialNumber: String,
	val trackers: Map<Int, HIDTrackerRecord>,
)

sealed interface HIDReceiverActions {
	data class DeviceRegistered(val hidId: Int, val address: String, val deviceId: Int) : HIDReceiverActions
	data class TrackerRegistered(val hidId: Int, val trackerId: Int) : HIDReceiverActions
}

typealias HIDReceiverContext = Context<HIDReceiverState, HIDReceiverActions>
typealias HIDReceiverBehaviour = CustomBehaviour<HIDReceiverState, HIDReceiverActions, HIDReceiver>
typealias HIDPacketDispatcher = EventDispatcher<HIDPacket>

@Suppress("UNCHECKED_CAST")
inline fun <reified T : HIDPacket> HIDPacketDispatcher.onPacket(crossinline callback: suspend (T) -> Unit) {
	register(T::class) { callback(it as T) }
}

val HIDRegistrationBehaviour = HIDReceiverBehaviour(
	reducer = { s, a ->
		when (a) {
			is HIDReceiverActions.DeviceRegistered -> s.copy(
				trackers = s.trackers + (a.hidId to HIDTrackerRecord(
					hidId = a.hidId,
					address = a.address,
					deviceId = a.deviceId,
					trackerId = null,
				)),
			)

			else -> s
		}
	},
	observer = { receiver ->
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
			val device = createDevice(
				scope = receiver.serverContext.context.scope,
				id = deviceId,
				address = packet.address,
				macAddress = packet.address,
				origin = DeviceOrigin.HID,
				protocolVersion = 0,
				serverContext = receiver.serverContext,
			)
			receiver.serverContext.context.dispatch(VRServerActions.NewDevice(deviceId, device))
			receiver.context.dispatch(HIDReceiverActions.DeviceRegistered(packet.hidId, packet.address, deviceId))
			AppLogger.hid.info("Registered HID device ${packet.address} (hidId=${packet.hidId})")
		}
	},
)

val HIDDeviceInfoBehaviour = HIDReceiverBehaviour(
	reducer = { s, a ->
		when (a) {
			is HIDReceiverActions.TrackerRegistered -> {
				val existing = s.trackers[a.hidId] ?: return@HIDReceiverBehaviour s
				s.copy(trackers = s.trackers + (a.hidId to existing.copy(trackerId = a.trackerId)))
			}

			else -> s
		}
	},
	observer = { receiver ->
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
					val newTracker = createTracker(
						scope = receiver.serverContext.context.scope,
						id = trackerId,
						deviceId = deviceState.id,
						sensorType = packet.imuType,
						hardwareId = deviceState.address,
						origin = DeviceOrigin.HID,
						serverContext = receiver.serverContext,
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
	},
)

val HIDRotationBehaviour = HIDReceiverBehaviour(
	observer = { receiver ->
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
	},
)

val HIDBatteryBehaviour = HIDReceiverBehaviour(
	observer = { receiver ->
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
	},
)

val HIDStatusBehaviour = HIDReceiverBehaviour(
	observer = { receiver ->
		receiver.packetEvents.onPacket<HIDStatus> { packet ->
			if (receiver.getTracker(packet.hidId) == null) return@onPacket
			receiver.getDevice(packet.hidId)?.context?.dispatch(
				DeviceActions.Update { copy(status = packet.status, signalStrength = packet.rssi) },
			)
		}
	},
)

data class HIDReceiver(
	val context: HIDReceiverContext,
	val serverContext: VRServer,
	val packetEvents: HIDPacketDispatcher,
) {
	fun getDevice(hidId: Int): Device? {
		val record = context.state.value.trackers[hidId] ?: return null
		return serverContext.getDevice(record.deviceId)
	}

	fun getTracker(hidId: Int): Tracker? {
		val record = context.state.value.trackers[hidId] ?: return null
		val trackerId = record.trackerId ?: return null
		return serverContext.getTracker(trackerId)
	}
}

fun createHIDReceiver(
	serialNumber: String,
	data: Flow<ByteArray>,
	serverContext: VRServer,
	scope: CoroutineScope,
): HIDReceiver {
	val behaviours = listOf(
		HIDRegistrationBehaviour,
		HIDDeviceInfoBehaviour,
		HIDRotationBehaviour,
		HIDBatteryBehaviour,
		HIDStatusBehaviour,
	)

	val context = createContext(
		initialState = HIDReceiverState(
			serialNumber = serialNumber,
			trackers = mapOf(),
		),
		reducers = behaviours.map { it.reducer },
		scope = scope,
	)

	val dispatcher = HIDPacketDispatcher()

	val receiver = HIDReceiver(
		context = context,
		serverContext = serverContext,
		packetEvents = dispatcher,
	)

	behaviours.map { it.observer }.forEach { it?.invoke(receiver) }

	data
		.onEach { report -> parseHIDPackets(report).forEach { dispatcher.emit(it) } }
		.launchIn(scope)

	scope.launch {
		try {
			awaitCancellation()
		} finally {
			withContext(NonCancellable) {
				for (record in context.state.value.trackers.values) {
					serverContext.getDevice(record.deviceId)?.context?.dispatch(
						DeviceActions.Update { copy(status = TrackerStatus.DISCONNECTED) },
					)
				}
			}
		}
	}

	return receiver
}
