package dev.slimevr.hid.behaviours.v3

import dev.slimevr.hid.HIDAccelerationV3
import dev.slimevr.hid.HIDButton
import dev.slimevr.hid.HIDDeviceStateV3
import dev.slimevr.hid.HIDMagnetometerV3
import dev.slimevr.hid.HIDReceiver
import dev.slimevr.hid.HIDReceiverBehaviour
import dev.slimevr.hid.HIDRotationV3
import dev.slimevr.hid.HIDTimeout
import dev.slimevr.hid.HID_TIME_UNKNOWN
import dev.slimevr.hid.behaviours.HidSleepTimers

class HIDSleepV3Behaviour : HIDReceiverBehaviour {
	private var timers: HidSleepTimers? = null

	override fun observe(receiver: HIDReceiver) {
		val timers = HidSleepTimers(receiver).also { this.timers = it }

		receiver.packetEvents.on<HIDTimeout> { packet ->
			timers.onPacket(packet.hidId)
			if (packet.secondsUntilTimeout == HID_TIME_UNKNOWN) {
				timers.cancelSleep(packet.hidId)
			} else {
				timers.scheduleSleep(packet.hidId, packet.secondsUntilTimeout * 1000)
			}
		}.launchIn(receiver.context.scope)

		receiver.packetEvents.on<HIDRotationV3> { timers.onPacket(it.hidId) }.launchIn(receiver.context.scope)
		receiver.packetEvents.on<HIDAccelerationV3> { timers.onPacket(it.hidId) }.launchIn(receiver.context.scope)
		receiver.packetEvents.on<HIDMagnetometerV3> { timers.onPacket(it.hidId) }.launchIn(receiver.context.scope)
		receiver.packetEvents.on<HIDDeviceStateV3> { timers.onPacket(it.hidId) }.launchIn(receiver.context.scope)
		receiver.packetEvents.on<HIDButton> { timers.onPacket(it.hidId) }.launchIn(receiver.context.scope)
	}

	override fun onDisconnect() {
		timers?.stop()
	}
}
