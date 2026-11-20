@file:Suppress("DEPRECATION") // legacy v2 packets

package dev.slimevr.hid.behaviours.legacy

import dev.slimevr.hid.HIDDeviceRegisterLegacy
import dev.slimevr.hid.HIDReceiver
import dev.slimevr.hid.HIDReceiverBehaviour
import dev.slimevr.hid.HID_PROTOCOL_LEGACY
import dev.slimevr.hid.behaviours.registerHidDevice

class HIDRegistrationBehaviour : HIDReceiverBehaviour {
	override fun observe(receiver: HIDReceiver) {
		receiver.packetEvents.on<HIDDeviceRegisterLegacy> { packet ->
			registerHidDevice(receiver, packet.hidId, packet.address, protocolVersion = HID_PROTOCOL_LEGACY)
		}.launchIn(receiver.context.scope)
	}
}
