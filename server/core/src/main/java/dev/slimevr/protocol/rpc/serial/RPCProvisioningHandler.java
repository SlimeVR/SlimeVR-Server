package dev.slimevr.protocol.rpc.serial;

import com.google.flatbuffers.FlatBufferBuilder;
import dev.slimevr.protocol.GenericConnection;
import dev.slimevr.protocol.ProtocolAPI;
import dev.slimevr.protocol.rpc.RPCHandler;
import dev.slimevr.serial.ProvisioningListener;
import dev.slimevr.serial.ProvisioningStatus;
import dev.slimevr.serial.SerialPort;
import solarxr_protocol.rpc.*;

import java.util.function.Consumer;


public class RPCProvisioningHandler implements ProvisioningListener {

	public RPCHandler rpcHandler;
	public ProtocolAPI api;

	public RPCProvisioningHandler(RPCHandler rpcHandler, ProtocolAPI api) {
		this.rpcHandler = rpcHandler;
		this.api = api;

		rpcHandler
			.registerPacketListener(
				RpcMessage.StartWifiProvisioningRequest,
				this::onStartWifiProvisioningRequest
			);
		rpcHandler
			.registerPacketListener(
				RpcMessage.StopWifiProvisioningRequest,
				this::onStopWifiProvisioningRequest
			);

		this.api.server.provisioningHandler.addListener(this);
	}

	public void onStartWifiProvisioningRequest(
		GenericConnection conn,
		RpcMessageHeader messageHeader
	) {
		StartWifiProvisioningRequest req = (StartWifiProvisioningRequest) messageHeader
			.message(new StartWifiProvisioningRequest());
		if (req == null)
			return;
		this.api.server.provisioningHandler.start(req.ssid(), req.password(), req.port());
		conn.getContext().setUseProvisioning(true);
	}

	public void onStopWifiProvisioningRequest(
		GenericConnection conn,
		RpcMessageHeader messageHeader
	) {
		StopWifiProvisioningRequest req = (StopWifiProvisioningRequest) messageHeader
			.message(new StopWifiProvisioningRequest());
		if (req == null)
			return;
		conn.getContext().setUseProvisioning(false);
		this.api.server.provisioningHandler.stop();
	}

	@Override
	public void onProvisioningStatusChange(ProvisioningStatus status, SerialPort port) {

		FlatBufferBuilder fbb = new FlatBufferBuilder(32);

		WifiProvisioningStatusResponse.startWifiProvisioningStatusResponse(fbb);
		WifiProvisioningStatusResponse.addStatus(fbb, status.id);
		int update = WifiProvisioningStatusResponse.endWifiProvisioningStatusResponse(fbb);
		int outbound = rpcHandler
			.createRPCMessage(fbb, RpcMessage.WifiProvisioningStatusResponse, update);
		fbb.finish(outbound);

		this.forAllListeners((conn) -> conn.send(fbb.dataBuffer()));
	}

	private void forAllListeners(Consumer<? super GenericConnection> action) {
		this.api
			.getAPIServers()
			.forEach(
				(server) -> server
					.getAPIConnections()
					.filter(conn -> conn.getContext().useProvisioning())
					.forEach(action)
			);
	}
}
