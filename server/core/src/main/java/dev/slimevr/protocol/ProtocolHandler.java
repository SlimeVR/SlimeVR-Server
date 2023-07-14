package dev.slimevr.protocol;

import java.util.function.BiConsumer;


public abstract class ProtocolHandler<H> {

	public final BiConsumer<GenericConnection, H>[] handlers;

	public ProtocolHandler() {
		this.handlers = new BiConsumer[this.messagesCount()];
	}

	public abstract void onMessage(GenericConnection conn, H message);

	public abstract int messagesCount();

	public void registerPacketListener(byte packetType, BiConsumer<GenericConnection, H> consumer) {
		if (this.handlers[packetType] != null)
			this.handlers[packetType] = this.handlers[packetType].andThen(consumer);
		else
			this.handlers[packetType] = consumer;
	}


}
