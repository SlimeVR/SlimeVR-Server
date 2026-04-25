package dev.slimevr.protocol

abstract class ProtocolHandler<H> {
	@JvmField
	val handlers: Array<((GenericConnection, H) -> Unit)?>

	init {
		this.handlers = arrayOfNulls(this.messagesCount())
	}

	abstract fun onMessage(conn: GenericConnection, message: H)

	abstract fun messagesCount(): Int

	fun registerPacketListener(packetType: Byte, consumer: (GenericConnection, H) -> Unit) {
		val packetInt = packetType.toInt()
		if (handlers[packetInt] != null) {
			val previous = handlers[packetInt]!!
			handlers[packetInt] = { conn, msg ->
				previous(conn, msg)
				consumer(conn, msg)
			}
		} else {
			handlers[packetInt] = consumer
		}
	}
}
