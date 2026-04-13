package dev.slimevr.hid

import dev.slimevr.EventDispatcher
import dev.slimevr.VRServer
import dev.slimevr.config.Settings
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.device.Device
import dev.slimevr.tracker.Tracker
import kotlinx.coroutines.CoroutineScope

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
			serverContext: VRServer,
			settings: Settings,
			scope: CoroutineScope,
		): HIDReceiver {
			val behaviours = listOf(
				HIDRegistrationBehaviour(settings),
				HIDDeviceInfoBehaviour(settings),
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

			return receiver
		}
	}
}
