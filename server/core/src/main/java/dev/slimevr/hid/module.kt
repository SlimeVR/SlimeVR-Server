package dev.slimevr.hid

import dev.slimevr.AppContextProvider
import dev.slimevr.EventDispatcher
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.device.Device
import dev.slimevr.device.DeviceActions
import dev.slimevr.hid.behaviours.HIDReceiverConfigBehaviour
import dev.slimevr.hid.behaviours.legacy.HIDBatteryBehaviour
import dev.slimevr.hid.behaviours.legacy.HIDDeviceInfoBehaviour
import dev.slimevr.hid.behaviours.legacy.HIDRegistrationBehaviour
import dev.slimevr.hid.behaviours.legacy.HIDRotationBehaviour
import dev.slimevr.hid.behaviours.legacy.HIDSleepLegacyBehaviour
import dev.slimevr.hid.behaviours.legacy.HIDStatusBehaviour
import dev.slimevr.hid.behaviours.v3.HIDDeviceInfoV3Behaviour
import dev.slimevr.hid.behaviours.v3.HIDDeviceStateBehaviour
import dev.slimevr.hid.behaviours.v3.HIDDongleInfoBehaviour
import dev.slimevr.hid.behaviours.v3.HIDMotionV3Behaviour
import dev.slimevr.hid.behaviours.v3.HIDPacketLossBehaviour
import dev.slimevr.hid.behaviours.v3.HIDRssiBehaviour
import dev.slimevr.hid.behaviours.v3.HIDSensorInfoBehaviour
import dev.slimevr.hid.behaviours.v3.HIDSleepV3Behaviour
import dev.slimevr.hid.behaviours.v3.HIDTrackerListBehaviour
import dev.slimevr.logging.AppLogger
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import solarxr_protocol.data_feed.dongle_data.DongleStatus
import solarxr_protocol.datatypes.TrackerStatus

data class HIDTrackerRecord(
	val hidId: Int,
	val address: String,
	val deviceId: Int,
	val trackerId: Int?,
	val sensorCount: Int = 1,
)

data class HIDReceiverState(
	val id: Int,
	val serialNumber: String,
	val isDirect: Boolean, // True if this HID device is a tracker connected directly over USB
	val status: DongleStatus,
	/** Negotiated HID protocol: 2 = legacy, 3 = v3, null until the first report. */
	val protocolVersion: Int?,
	val displayName: String,
	val customName: String?,
	val hardwareRevision: String?,
	val model: String?,
	val manufacturer: String?,
	val firmwareVersion: String?,
	val firmwareDate: String?,
	val hardwareAddress: String?,
	val boardType: String?,

	val trackers: Map<Int, HIDTrackerRecord>,
)

sealed interface HIDReceiverActions {
	data class DeviceRegistered(val hidId: Int, val address: String, val deviceId: Int, val sensorCount: Int = 1) : HIDReceiverActions
	data class TrackerRegistered(val hidId: Int, val trackerId: Int) : HIDReceiverActions
	data class DeviceUnregistered(val address: String) : HIDReceiverActions
	data class SetStatus(val status: DongleStatus) : HIDReceiverActions
	data class SetProtocolVersion(val version: Int) : HIDReceiverActions
	data class SetCustomName(val customName: String?) : HIDReceiverActions
	data class UpdateDongleInfo(val transform: HIDReceiverState.() -> HIDReceiverState) : HIDReceiverActions
}

typealias HIDReceiverContext = Context<HIDReceiverState, HIDReceiverActions>
interface HIDReceiverBehaviour : Behaviour<HIDReceiver> {
	fun onDisconnect() {}
}
typealias HIDPacketDispatcher = EventDispatcher<HIDPacket>

private fun v3Behaviours(): List<HIDReceiverBehaviour> = listOf(
	HIDDongleInfoBehaviour(),
	HIDTrackerListBehaviour(),
	HIDDeviceInfoV3Behaviour(),
	HIDSensorInfoBehaviour(),
	HIDMotionV3Behaviour(),
	HIDDeviceStateBehaviour(),
	HIDRssiBehaviour(),
	HIDPacketLossBehaviour(),
	HIDSleepV3Behaviour(),
)

private fun legacyBehaviours(): List<HIDReceiverBehaviour> = listOf(
	HIDRegistrationBehaviour(),
	HIDDeviceInfoBehaviour(),
	HIDRotationBehaviour(),
	HIDBatteryBehaviour(),
	HIDStatusBehaviour(),
	HIDSleepLegacyBehaviour(),
)

class HIDReceiver(
	val context: HIDReceiverContext,
	val appContext: AppContextProvider,
	val packetEvents: HIDPacketDispatcher,
) {
	fun startObserving() = context.observeAll(this)

	/**
	 * Attaches the legacy or v3 behaviour set and records the negotiated version. Called from the
	 * handshake in `readDevice` once the protocol is known. `protocolVersion` in state is the
	 * guard: a dongle's protocol is fixed for its life and is not cleared on disconnect, so this
	 * is a no-op on every reconnect rather than re-wiring per connection.
	 */
	suspend fun observeProtocol(version: Int) {
		val current = context.state.value.protocolVersion
		if (current == version) return
		if (current != null) {
			AppLogger.hid.warn("HID protocol changed ($current -> $version) for ${context.state.value.serialNumber}; restart to re-wire")
			return
		}
		val behaviours = if (version == HID_PROTOCOL_V3) v3Behaviours() else legacyBehaviours()
		context.behaviours.addAll(behaviours)
		behaviours.forEach { it.observe(this) }
		context.dispatch(HIDReceiverActions.SetProtocolVersion(version))
	}

	fun onDisconnected() {
		context.behaviours.snapshot().forEach {
			(it as? HIDReceiverBehaviour)?.onDisconnect()
		}

		for (record in context.state.value.trackers.values) {
			record.trackerId?.let { id -> appContext.server.getTracker(id) }?.context?.dispatch(TrackerActions.SetStatus(TrackerStatus.DISCONNECTED))
			val device = appContext.server.getDevice(record.deviceId) ?: continue
			device.context.dispatch(
				DeviceActions.Update { copy(status = TrackerStatus.DISCONNECTED) },
			)
		}
		context.dispatch(HIDReceiverActions.SetStatus(DongleStatus.DISCONNECTED))
		// protocolVersion is deliberately kept — it's the wiring guard in observeProtocol, and a
		// dongle stays the same protocol across reconnects.
	}

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
			val settings = appContext.config.settings
			val savedConfig = settings.context.state.value.data.dongles[serialNumber]

			val behaviours = listOf(
				HIDReceiverConfigBehaviour(settings, serialNumber),
			)

			val context = Context.create(
				initialState = HIDReceiverState(
					id = id,
					serialNumber = serialNumber,
					isDirect = isDirect,
					status = DongleStatus.DISCONNECTED,
					protocolVersion = null,
					trackers = emptyMap(),
					displayName = "Dongle #$serialNumber",
					customName = savedConfig?.customName,
					hardwareRevision = null,
					manufacturer = null,
					model = null,
					firmwareVersion = null,
					firmwareDate = null,
					hardwareAddress = null,
					boardType = null,
				),
				scope = scope,
				reducer = ::reduce,
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
