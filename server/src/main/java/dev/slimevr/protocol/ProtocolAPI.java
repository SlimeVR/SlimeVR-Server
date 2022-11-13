package dev.slimevr.protocol;

import dev.slimevr.VRServer;
import solarxr_protocol.MessageBundle;
import solarxr_protocol.data_feed.DataFeedMessageHeader;
import solarxr_protocol.pub_sub.PubSubHeader;
import solarxr_protocol.rpc.RpcMessageHeader;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


public class ProtocolAPI {

	public final VRServer server;
	public final RPCHandler rpcHandler;
	public final DataFeedHandler dataFeedHandler;
	public final PubSubHandler pubSubHandler;

	private final List<ProtocolAPIServer> servers = new ArrayList<>();

	public ProtocolAPI(VRServer server) {
		this.server = server;
		this.rpcHandler = new RPCHandler(this);
		this.dataFeedHandler = new DataFeedHandler(this);
		this.pubSubHandler = new PubSubHandler(this);
	}

	public void onMessage(GenericConnection conn, ByteBuffer message) {
		MessageBundle messageBundle = MessageBundle.getRootAsMessageBundle(message);

		try {
			for (int index = 0; index < messageBundle.dataFeedMsgsLength(); index++) {
				DataFeedMessageHeader header = messageBundle.dataFeedMsgsVector().get(index);
				this.dataFeedHandler.onMessage(conn, header);
			}

			for (int index = 0; index < messageBundle.rpcMsgsLength(); index++) {
				RpcMessageHeader header = messageBundle.rpcMsgsVector().get(index);
				this.rpcHandler.onMessage(conn, header);
			}

			for (int index = 0; index < messageBundle.pubSubMsgsLength(); index++) {
				PubSubHeader header = messageBundle.pubSubMsgsVector().get(index);
				this.pubSubHandler.onMessage(conn, header);
			}
		} catch (AssertionError e) {
			// Catch flatbuffer errors
			e.printStackTrace();
		}
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
