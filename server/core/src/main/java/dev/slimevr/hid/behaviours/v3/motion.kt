package dev.slimevr.hid.behaviours.v3

import dev.slimevr.hid.HIDAccelerationV3
import dev.slimevr.hid.HIDMagnetometerV3
import dev.slimevr.hid.HIDReceiver
import dev.slimevr.hid.HIDReceiverBehaviour
import dev.slimevr.hid.HIDRotationV3

class HIDMotionV3Behaviour : HIDReceiverBehaviour {
	override fun observe(receiver: HIDReceiver) {
		receiver.packetEvents.on<HIDRotationV3> { packet ->
			if (packet.sensorId != null) return@on
			receiver.getTracker(packet.hidId)?.setRotation(rotation = packet.rotation)
		}.launchIn(receiver.context.scope)

		receiver.packetEvents.on<HIDAccelerationV3> { packet ->
			if (packet.sensorId != null) return@on
			receiver.getTracker(packet.hidId)?.setRotation(acceleration = packet.acceleration)
		}.launchIn(receiver.context.scope)

		receiver.packetEvents.on<HIDMagnetometerV3> { packet ->
			if (packet.sensorId != null) return@on
			receiver.getTracker(packet.hidId)?.setRotation(magnetometer = packet.magnetometer)
		}.launchIn(receiver.context.scope)
	}
}
