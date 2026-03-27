package dev.slimevr.hid

import dev.slimevr.EventDispatcher
import dev.slimevr.VRServer
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.device.Device
import dev.slimevr.device.DeviceActions
import dev.slimevr.tracker.Tracker
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
typealias HIDReceiverBehaviour = Behaviour<HIDReceiverState, HIDReceiverActions, HIDReceiver>
typealias HIDPacketDispatcher = EventDispatcher<HIDPacket>

@Suppress("UNCHECKED_CAST")
inline fun <reified T : HIDPacket> HIDPacketDispatcher.onPacket(crossinline callback: suspend (T) -> Unit) {
	register(T::class) { callback(it as T) }
}

class HIDReceiver(
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

	companion object {
		fun create(
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

			val context = Context.create(
				initialState = HIDReceiverState(serialNumber = serialNumber, trackers = mapOf()),
				scope = scope,
				behaviours = behaviours,
			)

			val dispatcher = HIDPacketDispatcher()

			val receiver = HIDReceiver(
				context = context,
				serverContext = serverContext,
				packetEvents = dispatcher,
			)

			behaviours.forEach { it.observe(receiver) }

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
	}
}