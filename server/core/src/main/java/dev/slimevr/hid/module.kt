package dev.slimevr.hid

import dev.slimevr.AppContextProvider
import dev.slimevr.EventDispatcher
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.device.Device
import dev.slimevr.tracker.Tracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow

data class HIDTrackerRecord(
	val hidId: Int,
	val address: String,
	val deviceId: Int,
	val trackerId: Int?,
)

data class HIDReceiverState(
	val id: Int,
	val serialNumber: String,
	val isDirect: Boolean, // True if this HID device is a tracker connected directly over USB

	val displayName: String,
	val hardwareRevision: String,
	val model: String,
	val manufacturer: String,
	val firmwareVersion: String,
	val firmwareDate: String,
	val hardwareAddress: String,
	val boardType: String,

	val trackers: Map<Int, HIDTrackerRecord>,
)

sealed interface HIDReceiverActions {
	data class DeviceRegistered(val hidId: Int, val address: String, val deviceId: Int) : HIDReceiverActions
	data class TrackerRegistered(val hidId: Int, val trackerId: Int) : HIDReceiverActions
}

typealias HIDReceiverContext = Context<HIDReceiverState, HIDReceiverActions>
typealias HIDReceiverBehaviour = Behaviour<HIDReceiverState, HIDReceiverActions, HIDReceiver>
typealias HIDPacketDispatcher = EventDispatcher<HIDPacket>

class HIDReceiver(
	val context: HIDReceiverContext,
	val appContext: AppContextProvider,
	val packetEvents: HIDPacketDispatcher,
) {
	fun startObserving() = context.observeAll(this)

	fun getDevice(hidId: Int): Device? {
		val record = context.state.value.trackers[hidId] ?: return null
		return appContext.server.getDevice(record.deviceId)
	}

	fun getTracker(hidId: Int): Tracker? {
		val record = context.state.value.trackers[hidId] ?: return null
		val trackerId = record.trackerId ?: return null
		return appContext.server.getTracker(trackerId)
	}

	companion object {
		fun create(
			id: Int,
			serialNumber: String,
			isDirect: Boolean,
			appContext: AppContextProvider,
			scope: CoroutineScope,
		): HIDReceiver {
			val behaviours = listOf(
				HIDRegistrationBehaviour(),
				HIDDeviceInfoBehaviour(),
				HIDRotationBehaviour(),
				HIDBatteryBehaviour(),
				HIDStatusBehaviour(),
				HIDPacketLossBehaviour(),
				HIDSleepBehaviour(),
			)

			val context = Context.create(
				initialState = HIDReceiverState(
					id = id,
					serialNumber = serialNumber,
					isDirect = isDirect,
					trackers = emptyMap(),
					displayName = "Dongle #${serialNumber}",
					hardwareRevision = "1",
					manufacturer = "SlimeVR",
					model = "SuperCool",
					firmwareVersion = "67",
					firmwareDate = "2000 BC",
					hardwareAddress = "10:2A:23:34:1F",
					boardType = "Nya_a"
				),
				scope = scope,
				behaviours = behaviours,
				name = "HIDReceiver[$serialNumber]",
			)

			// Every packet here is a state sample, so a backlog is worth less than the newest reading.
			// Measured peak depth is ~55 with six trackers, so the depth warning sits clear of that.
			val dispatcher = HIDPacketDispatcher(
				name = "HID[$serialNumber]",
				scope = context.scope,
				capacity = 256,
				onBufferOverflow = BufferOverflow.DROP_OLDEST,
			)

			val receiver = HIDReceiver(
				context = context,
				appContext = appContext,
				packetEvents = dispatcher,
			)
			receiver.startObserving()
			return receiver
		}
	}
}
