package dev.slimevr.protocol;

import com.google.flatbuffers.FlatBufferBuilder;
import solarxr_protocol.MessageBundle;

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

	public int createMessage(FlatBufferBuilder fbb, int datafeedMessagesOffset, int rpcMsgsOffset) {
		MessageBundle.startMessageBundle(fbb);
		if (datafeedMessagesOffset > -1)
			MessageBundle.addDataFeedMsgs(fbb, datafeedMessagesOffset);
		if (rpcMsgsOffset > -1)
			MessageBundle.addRpcMsgs(fbb, rpcMsgsOffset);
		return MessageBundle.endMessageBundle(fbb);
	}
}
