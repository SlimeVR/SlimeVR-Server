@file:Suppress("DEPRECATION") // legacy v2 packets

package dev.slimevr.hid.behaviours.legacy

import dev.slimevr.hid.HIDDataLegacy
import dev.slimevr.hid.HIDReceiver
import dev.slimevr.hid.HIDReceiverBehaviour
import dev.slimevr.hid.HIDRotationBatteryLegacy
import dev.slimevr.hid.HIDRotationButtonLegacy
import dev.slimevr.hid.HIDRotationLegacy
import dev.slimevr.hid.HIDRotationMagLegacy
import dev.slimevr.hid.HIDRuntimeLegacy
import dev.slimevr.hid.HIDStatusLegacy
import dev.slimevr.hid.behaviours.HidSleepTimers

/** Legacy v2: the type 6/7 timeout hint plus every motion packet feeding the idle watchdog. */
class HIDSleepLegacyBehaviour : HIDReceiverBehaviour {
	private var timers: HidSleepTimers? = null

	override fun observe(receiver: HIDReceiver) {
		val timers = HidSleepTimers(receiver).also { this.timers = it }

		// Legacy packets 6/7 carry a 16-bit millisecond timeout: 0 = no hint, 65535 = never sleep.
		fun applyLegacyTimeout(hidId: Int, timeoutMs: Int) = when (timeoutMs) {
			0 -> Unit
			65535 -> timers.cancelSleep(hidId)
			else -> timers.scheduleSleep(hidId, timeoutMs.toLong())
		}

		receiver.packetEvents.on<HIDRotationButtonLegacy> { packet ->
			timers.onPacket(packet.hidId)
			applyLegacyTimeout(packet.hidId, packet.timeout)
		}.launchIn(receiver.context.scope)

		receiver.packetEvents.on<HIDDataLegacy> { packet ->
			timers.onPacket(packet.hidId)
			applyLegacyTimeout(packet.hidId, packet.timeout)
		}.launchIn(receiver.context.scope)

		receiver.packetEvents.on<HIDRotationLegacy> { timers.onPacket(it.hidId) }.launchIn(receiver.context.scope)
		receiver.packetEvents.on<HIDRotationBatteryLegacy> { timers.onPacket(it.hidId) }.launchIn(receiver.context.scope)
		receiver.packetEvents.on<HIDRotationMagLegacy> { timers.onPacket(it.hidId) }.launchIn(receiver.context.scope)
		receiver.packetEvents.on<HIDStatusLegacy> { timers.onPacket(it.hidId) }.launchIn(receiver.context.scope)
		receiver.packetEvents.on<HIDRuntimeLegacy> { timers.onPacket(it.hidId) }.launchIn(receiver.context.scope)
	}

	override fun onDisconnect() {
		timers?.stop()
	}
}
