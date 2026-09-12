@file:Suppress("DEPRECATION") // legacy v2 packets

package dev.slimevr.hid.behaviours.legacy

import dev.slimevr.device.DeviceActions
import dev.slimevr.hid.HIDReceiver
import dev.slimevr.hid.HIDReceiverBehaviour
import dev.slimevr.hid.HIDStatusLegacy

class HIDStatusBehaviour : HIDReceiverBehaviour {
	override fun observe(receiver: HIDReceiver) {
		receiver.packetEvents.on<HIDStatusLegacy> { packet ->
			val device = receiver.getDevice(packet.hidId) ?: return@on
			if (receiver.getTracker(packet.hidId) != null) {
				device.context.dispatch(
					DeviceActions.Update { copy(status = packet.status, signalStrength = packet.rssi) },
				)
			}
			device.recordRssi(packet.rssi)
			device.recordPacketStats(received = packet.packetsReceived, lost = packet.packetsLost)
		}.launchIn(receiver.context.scope)
	}
}
