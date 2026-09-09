@file:Suppress("DEPRECATION") // legacy v2 packets

package dev.slimevr.hid.behaviours.legacy

import dev.slimevr.hid.HIDReceiver
import dev.slimevr.hid.HIDReceiverBehaviour
import dev.slimevr.hid.HIDRotationBatteryLegacy
import dev.slimevr.hid.HIDRotationButtonLegacy
import dev.slimevr.hid.HIDRotationLegacy
import dev.slimevr.hid.HIDRotationMagLegacy

class HIDRotationBehaviour : HIDReceiverBehaviour {
	override fun observe(receiver: HIDReceiver) {
		receiver.packetEvents.on<HIDRotationLegacy> { packet ->
			val tracker = receiver.getTracker(packet.hidId) ?: return@on
			tracker.setRotation(rotation = packet.rotation, acceleration = packet.acceleration)
		}.launchIn(receiver.context.scope)

		receiver.packetEvents.on<HIDRotationBatteryLegacy> { packet ->
			val tracker = receiver.getTracker(packet.hidId) ?: return@on
			tracker.setRotation(rotation = packet.rotation, acceleration = packet.acceleration)
		}.launchIn(receiver.context.scope)

		receiver.packetEvents.on<HIDRotationMagLegacy> { packet ->
			val tracker = receiver.getTracker(packet.hidId) ?: return@on
			tracker.setRotation(rotation = packet.rotation, magnetometer = packet.magnetometer)
		}.launchIn(receiver.context.scope)

		receiver.packetEvents.on<HIDRotationButtonLegacy> { packet ->
			val tracker = receiver.getTracker(packet.hidId) ?: return@on
			tracker.setRotation(rotation = packet.rotation, acceleration = packet.acceleration)
		}.launchIn(receiver.context.scope)
	}
}
