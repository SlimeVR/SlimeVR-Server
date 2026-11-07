package dev.slimevr.hid.behaviours.v3

import dev.slimevr.hid.HIDReceiver
import dev.slimevr.hid.HIDReceiverBehaviour
import dev.slimevr.hid.HIDSeqPacket
import dev.slimevr.hid.HIDTrackerListEntry
import dev.slimevr.hid.HIDTrackerPacket

class HIDPacketLossBehaviour : HIDReceiverBehaviour {
	private val lastSeq = mutableMapOf<Int, Int>()

	override fun observe(receiver: HIDReceiver) {
		receiver.packetEvents.on<HIDSeqPacket> { packet ->
			if (packet !is HIDTrackerPacket || packet is HIDTrackerListEntry) return@on
			val seq = packet.seq
			if (seq == 0) return@on

			val hidId = packet.hidId
			val previous = lastSeq.put(hidId, seq)
			if (previous == seq) return@on

			val device = receiver.getDevice(hidId) ?: return@on
			val lost = if (previous == null) 0 else (seq - previous - 1 + 256) % 256
			device.recordPacketStats(received = 1, lost = lost)
		}.launchIn(receiver.context.scope)
	}

	override fun onDisconnect() {
		lastSeq.clear()
	}
}
