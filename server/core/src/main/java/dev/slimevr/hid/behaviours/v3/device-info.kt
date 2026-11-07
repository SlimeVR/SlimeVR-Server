package dev.slimevr.hid.behaviours.v3

import dev.slimevr.device.DeviceActions
import dev.slimevr.hid.HIDDeviceInfoV3
import dev.slimevr.hid.HIDReceiver
import dev.slimevr.hid.HIDReceiverBehaviour
import dev.slimevr.hid.HID_PROTOCOL_V3
import dev.slimevr.hid.behaviours.ensureHidTracker
import dev.slimevr.hid.behaviours.registerHidDevice

class HIDDeviceInfoV3Behaviour : HIDReceiverBehaviour {
	override fun observe(receiver: HIDReceiver) {
		receiver.packetEvents.on<HIDDeviceInfoV3> { packet ->
			val deviceId = registerHidDevice(receiver, packet.hidId, packet.hwid, protocolVersion = HID_PROTOCOL_V3)
			receiver.appContext.server.getDevice(deviceId)?.context?.dispatch(
				DeviceActions.Update {
					copy(
						boardType = packet.boardType,
						mcuType = packet.mcuType,
						firmwareVersion = packet.firmwareVersion,
						firmwareDate = packet.firmwareDate,
					)
				},
			)
			ensureHidTracker(receiver, packet.hidId, deviceId)
		}.launchIn(receiver.context.scope)
	}
}
