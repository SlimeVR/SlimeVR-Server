package dev.slimevr.protocol

import java.util.function.BiConsumer

abstract class ProtocolHandler<H> {
	@JvmField
	val handlers: Array<BiConsumer<GenericConnection, H>?>

	init {
		this.handlers = arrayOfNulls(this.messagesCount())
	}

	abstract fun onMessage(conn: GenericConnection, message: H)

	abstract fun messagesCount(): Int

	fun registerPacketListener(packetType: Byte, consumer: BiConsumer<GenericConnection, H>) {
		val packetInt = packetType.toInt()
		if (handlers[packetInt] != null) {
			handlers[packetInt] = handlers[packetInt]!!.andThen(consumer)
		} else {
			handlers[packetInt] = consumer
		}
	}
}
