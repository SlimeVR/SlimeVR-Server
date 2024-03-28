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

	fun registerPacketListener(packetType: Byte, consumer: BiConsumer<GenericConnection, H>?) {
		if (handlers[packetType.toInt()] != null) {
			handlers[packetType.toInt()] =
				consumer?.let { handlers[packetType.toInt()]!!.andThen(it) }
		} else {
			handlers[packetType.toInt()] = consumer
		}
	}
}
