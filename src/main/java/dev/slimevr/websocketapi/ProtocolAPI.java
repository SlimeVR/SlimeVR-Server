package dev.slimevr.websocketapi;

import com.google.flatbuffers.FlatBufferBuilder;
import dev.slimevr.VRServer;
import io.eiren.util.logging.LogManager;
import slimevr_protocol.InboundPacket;
import slimevr_protocol.InboundUnion;
import slimevr_protocol.OutboundPacket;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class ProtocolAPI {

	private int outbountPacketCount = 0;
	public final VRServer server;
	public final RPCHandler rpcHandler;
	public final DataFeedHandler dataFeedHandler;

	private List<ProtocolAPIServer> servers = new ArrayList<>();

	@SuppressWarnings("unchecked")
	public final BiConsumer<GenericConnection, InboundPacket>[] inboundHandlers = new BiConsumer[InboundUnion.names.length] ;

	public ProtocolAPI(VRServer server) {
		this.server = server;
		this.rpcHandler = new RPCHandler(this);
		this.dataFeedHandler = new DataFeedHandler(this);
	}

	public void onMessage(GenericConnection conn, ByteBuffer message) {
		InboundPacket inboundPacket = InboundPacket.getRootAsInboundPacket(message);

		BiConsumer<GenericConnection, InboundPacket> consumer = this.inboundHandlers[inboundPacket.packetType()];
		if (consumer != null)
			consumer.accept(conn, inboundPacket);
		else
			LogManager.log.info("[ProtocolAPI] Unhandled packet received id: " + inboundPacket.packetType());
	}

	public void registerPacketListener(byte packetType, BiConsumer<GenericConnection, InboundPacket> consumer) {
		if (this.inboundHandlers[packetType] != null)
			this.inboundHandlers[packetType] = this.inboundHandlers[packetType].andThen(consumer);
		else
			this.inboundHandlers[packetType] = consumer;
	}

	public int createOutboundPacket(FlatBufferBuilder fbb, byte packetType, int packetOffset) {
		return OutboundPacket.createOutboundPacket(fbb, this.outbountPacketCount++, false, packetType, packetOffset);
	}

	public List<ProtocolAPIServer> getAPIServers() {
		return servers;
	}

	public void registerAPIServer(ProtocolAPIServer server) {
		this.servers.add(server);
	}

	public void removeAPIServer(ProtocolAPIServer server) {
		this.servers.remove(server);
	}
}
