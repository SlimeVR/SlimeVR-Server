package dev.slimevr.protocol.rpc.reset;

import java.util.function.Consumer;

import com.google.flatbuffers.FlatBufferBuilder;

import dev.slimevr.protocol.GenericConnection;

import dev.slimevr.protocol.ProtocolAPI;
import dev.slimevr.protocol.rpc.RPCHandler;
import dev.slimevr.reset.ResetListener;
import solarxr_protocol.rpc.ResetResponse;
import solarxr_protocol.rpc.ResetStatus;
import solarxr_protocol.rpc.RpcMessage;


public class RPCResetHandler implements ResetListener {
	public RPCHandler rpcHandler;
	public ProtocolAPI api;

	public RPCResetHandler(RPCHandler rpcHandler, ProtocolAPI api) {
		this.rpcHandler = rpcHandler;
		this.api = api;

		this.api.server.resetHandler.addListener(this);
	}

	public void sendResetStatusResponse(int resetType, int status) {
		FlatBufferBuilder fbb = new FlatBufferBuilder(32);
		ResetResponse.startResetResponse(fbb);
		ResetResponse.addResetType(fbb, resetType);
		ResetResponse.addStatus(fbb, status);
		int update = ResetResponse.endResetResponse(fbb);
		int outbound = rpcHandler.createRPCMessage(fbb, RpcMessage.ResetResponse, update);
		fbb.finish(outbound);

		this.forAllListeners((conn) -> {
			conn.send(fbb.dataBuffer());
		});
	}

	@Override
	public void onStarted(int resetType) {
		sendResetStatusResponse(resetType, ResetStatus.STARTED);
	}

	@Override
	public void onFinished(int resetType) {
		sendResetStatusResponse(resetType, ResetStatus.FINISHED);
	}

	public void forAllListeners(Consumer<? super GenericConnection> action) {
		this.api
			.getAPIServers()
			.forEach(
				(server) -> server
					.getAPIConnections()
					.forEach(action)
			);
	}
}

