package dev.slimevr.hid.behaviours.v3

import dev.slimevr.device.DeviceActions
import dev.slimevr.hid.HIDReceiver
import dev.slimevr.hid.HIDReceiverActions
import dev.slimevr.hid.HIDReceiverBehaviour
import dev.slimevr.hid.HIDTrackerListEntry
import dev.slimevr.hid.HID_ALL_TRACKERS
import dev.slimevr.hid.HID_PROTOCOL_V3
import dev.slimevr.hid.behaviours.ensureHidTracker
import dev.slimevr.hid.behaviours.registerHidDevice
import dev.slimevr.logging.AppLogger
import solarxr_protocol.datatypes.DeviceOrigin
import solarxr_protocol.datatypes.TrackerStatus

class HIDTrackerListBehaviour : HIDReceiverBehaviour {
	override fun observe(receiver: HIDReceiver) {
		receiver.packetEvents.on<HIDTrackerListEntry> { packet ->
			if (packet.hidId == HID_ALL_TRACKERS) {
				receiver.appContext.server.context.state.value.devices.values
					.find { it.context.state.value.macAddress == packet.hwid && it.context.state.value.origin == DeviceOrigin.HID }
					?.context?.dispatch(DeviceActions.Update { copy(status = TrackerStatus.DISCONNECTED) })
				receiver.context.dispatch(HIDReceiverActions.DeviceUnregistered(packet.hwid))
				AppLogger.hid.info("Unpaired HID device ${packet.hwid}")
				return@on
			}
			val deviceId = registerHidDevice(
				receiver,
				packet.hidId,
				packet.hwid,
				protocolVersion = HID_PROTOCOL_V3,
				sensorCount = packet.sensorCount,
			)
			ensureHidTracker(receiver, packet.hidId, deviceId)
		}.launchIn(receiver.context.scope)
	}
}
