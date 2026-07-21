package dev.slimevr.hid

import kotlinx.io.Sink
import java.io.ByteArrayOutputStream

sealed interface HIDOutboundPacket {
	fun write(sink: Sink)
}

data class HIDCommand(
	val trackerId : Int,
	val command: Command,
) : HIDOutboundPacket {
	 override fun write(sink: Sink) {
		sink.writeByte(0)
		sink.writeInt(trackerId)
		sink.writeInt(command.id)
	}
}


enum class Command(
	var id: Int
) {
	SHUTDOWN (0),
	UNPAIR (1)
}
