package dev.slimevr.hid.behaviours.v3

import dev.slimevr.device.DeviceActions
import dev.slimevr.hid.HIDReceiver
import dev.slimevr.hid.HIDReceiverBehaviour
import dev.slimevr.hid.HIDRssi

class HIDRssiBehaviour : HIDReceiverBehaviour {
	override fun observe(receiver: HIDReceiver) {
		receiver.packetEvents.on<HIDRssi> { packet ->
			val device = receiver.getDevice(packet.hidId) ?: return@on
			device.context.dispatch(DeviceActions.Update { copy(signalStrength = packet.rssi) })
			device.recordRssi(packet.rssi)
		}.launchIn(receiver.context.scope)
	}
}
