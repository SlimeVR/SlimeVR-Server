package dev.slimevr.protocol.rpc.serial;

import com.fazecast.jSerialComm.SerialPort;
import com.google.flatbuffers.FlatBufferBuilder;
import dev.slimevr.protocol.GenericConnection;
import dev.slimevr.protocol.ProtocolAPI;
import dev.slimevr.protocol.rpc.RPCHandler;
import dev.slimevr.serial.SerialListener;
import io.eiren.util.logging.LogManager;
import solarxr_protocol.rpc.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


public class RPCSerialHandler implements SerialListener {

	public RPCHandler rpcHandler;
	public ProtocolAPI api;

	public RPCSerialHandler(RPCHandler rpcHandler, ProtocolAPI api) {
		this.rpcHandler = rpcHandler;
		this.api = api;

		rpcHandler
			.registerPacketListener(
				RpcMessage.SerialTrackerRebootRequest,
				this::onSerialTrackerRebootRequest
			);
		rpcHandler
			.registerPacketListener(
				RpcMessage.SerialTrackerGetInfoRequest,
				this::onSerialTrackerGetInfoRequest
			);
		rpcHandler
			.registerPacketListener(
				RpcMessage.SerialTrackerFactoryResetRequest,
				this::onSerialTrackerFactoryResetRequest
			);
		rpcHandler.registerPacketListener(RpcMessage.SetWifiRequest, this::onSetWifiRequest);
		rpcHandler.registerPacketListener(RpcMessage.OpenSerialRequest, this::onOpenSerialRequest);
		rpcHandler
			.registerPacketListener(RpcMessage.CloseSerialRequest, this::onCloseSerialRequest);
		rpcHandler
			.registerPacketListener(RpcMessage.SerialDevicesRequest, this::onRequestSerialDevices);
		this.api.server.getSerialHandler().addListener(this);
	}

	@Override
	public void onSerialDisconnected() {

		FlatBufferBuilder fbb = new FlatBufferBuilder(32);

		SerialUpdateResponse.startSerialUpdateResponse(fbb);
		SerialUpdateResponse.addClosed(fbb, true);
		int update = SerialUpdateResponse.endSerialUpdateResponse(fbb);
		int outbound = rpcHandler.createRPCMessage(fbb, RpcMessage.SerialUpdateResponse, update);
		fbb.finish(outbound);

		this.forAllListeners((conn) -> {
			conn.send(fbb.dataBuffer());
			conn.getContext().setUseSerial(false);
		});
	}

	@Override
	public void onSerialLog(String str) {
		FlatBufferBuilder fbb = new FlatBufferBuilder(32);

		int logOffset = fbb.createString(str);

		SerialUpdateResponse.startSerialUpdateResponse(fbb);
		SerialUpdateResponse.addLog(fbb, logOffset);
		int update = SerialUpdateResponse.endSerialUpdateResponse(fbb);
		int outbound = rpcHandler.createRPCMessage(fbb, RpcMessage.SerialUpdateResponse, update);
		fbb.finish(outbound);

		this.forAllListeners((conn) -> {
			conn.send(fbb.dataBuffer());
		});
	}

	@Override
	public void onNewSerialDevice(SerialPort port) {
		FlatBufferBuilder fbb = new FlatBufferBuilder(32);

		int portOffset = fbb.createString(port.getPortLocation());
		int nameOffset = fbb.createString(port.getDescriptivePortName());
		int deviceOffset = SerialDevice.createSerialDevice(fbb, portOffset, nameOffset);
		int newSerialOffset = NewSerialDeviceResponse
			.createNewSerialDeviceResponse(fbb, deviceOffset);
		int outbound = rpcHandler
			.createRPCMessage(fbb, RpcMessage.NewSerialDeviceResponse, newSerialOffset);
		fbb.finish(outbound);


		this.api
			.getAPIServers()
			.forEach(
				(server) -> server
					.getAPIConnections()
					.forEach((conn) -> {
						conn.send(fbb.dataBuffer());
					})
			);
	}

	@Override
	public void onSerialConnected(SerialPort port) {

		FlatBufferBuilder fbb = new FlatBufferBuilder(32);

		SerialUpdateResponse.startSerialUpdateResponse(fbb);
		SerialUpdateResponse.addClosed(fbb, false);
		int update = SerialUpdateResponse.endSerialUpdateResponse(fbb);
		int outbound = rpcHandler.createRPCMessage(fbb, RpcMessage.SerialUpdateResponse, update);
		fbb.finish(outbound);

		this.forAllListeners((conn) -> {
			conn.send(fbb.dataBuffer());
		});
	}

	public void onSerialTrackerRebootRequest(
		GenericConnection conn,
		RpcMessageHeader messageHeader
	) {
		SerialTrackerRebootRequest req = (SerialTrackerRebootRequest) messageHeader
			.message(new SerialTrackerRebootRequest());
		if (req == null)
			return;

		this.api.server.getSerialHandler().rebootRequest();
	}

	public void onSerialTrackerGetInfoRequest(
		GenericConnection conn,
		RpcMessageHeader messageHeader
	) {
		SerialTrackerGetInfoRequest req = (SerialTrackerGetInfoRequest) messageHeader
			.message(new SerialTrackerGetInfoRequest());
		if (req == null)
			return;

		this.api.server.getSerialHandler().infoRequest();
	}

	public void onSerialTrackerFactoryResetRequest(
		GenericConnection conn,
		RpcMessageHeader messageHeader
	) {
		SerialTrackerFactoryResetRequest req = (SerialTrackerFactoryResetRequest) messageHeader
			.message(new SerialTrackerFactoryResetRequest());
		if (req == null)
			return;

		this.api.server.getSerialHandler().factoryResetRequest();
	}


	private void onRequestSerialDevices(GenericConnection conn, RpcMessageHeader messageHeader) {
		SerialDevicesRequest req = (SerialDevicesRequest) messageHeader
			.message(new SerialDevicesRequest());
		if (req == null)
			return;

		FlatBufferBuilder fbb = new FlatBufferBuilder(32);


		List<Integer> devicesOffsets = new ArrayList<>();

		try {
			this.api.server.getSerialHandler().getKnownPorts().forEach((port) -> {
				int portOffset = fbb.createString(port.getPortLocation());
				int nameOffset = fbb.createString(port.getDescriptivePortName());
				devicesOffsets.add(SerialDevice.createSerialDevice(fbb, portOffset, nameOffset));
			});
		} catch (Throwable e) {
			LogManager.severe("Using serial ports is not supported on this platform", e);
		}

		SerialDevicesResponse.startDevicesVector(fbb, devicesOffsets.size());
		devicesOffsets.forEach(offset -> SerialDevicesResponse.addDevices(fbb, offset));
		int devices = fbb.endVector();
		int serialDeviceOffsets = SerialDevicesResponse.createSerialDevicesResponse(fbb, devices);
		int outbound = rpcHandler
			.createRPCMessage(fbb, RpcMessage.SerialDevicesResponse, serialDeviceOffsets);
		fbb.finish(outbound);
		conn.send(fbb.dataBuffer());
	}

	public void onSetWifiRequest(GenericConnection conn, RpcMessageHeader messageHeader) {
		SetWifiRequest req = (SetWifiRequest) messageHeader.message(new SetWifiRequest());
		if (req == null)
			return;

		if (
			req.password() == null
				|| req.ssid() == null
				|| !this.api.server.getSerialHandler().isConnected()
		)
			return;
		this.api.server.getSerialHandler().setWifi(req.ssid(), req.password());
	}

	public void onOpenSerialRequest(GenericConnection conn, RpcMessageHeader messageHeader) {
		OpenSerialRequest req = (OpenSerialRequest) messageHeader.message(new OpenSerialRequest());
		if (req == null)
			return;

		conn.getContext().setUseSerial(true);

		try {
			this.api.server.getSerialHandler().openSerial(req.port(), req.auto());
		} catch (Exception e) {
			LogManager.severe("Unable to open serial port", e);
		} catch (Throwable e) {
			LogManager.severe("Using serial ports is not supported on this platform", e);
		}

		FlatBufferBuilder fbb = new FlatBufferBuilder(32);
		SerialUpdateResponse.startSerialUpdateResponse(fbb);
		SerialUpdateResponse.addClosed(fbb, !this.api.server.getSerialHandler().isConnected());
		int update = SerialUpdateResponse.endSerialUpdateResponse(fbb);
		int outbound = rpcHandler.createRPCMessage(fbb, RpcMessage.SerialUpdateResponse, update);
		fbb.finish(outbound);
		conn.send(fbb.dataBuffer());
	}

	public void onCloseSerialRequest(GenericConnection conn, RpcMessageHeader messageHeader) {
		CloseSerialRequest req = (CloseSerialRequest) messageHeader
			.message(new CloseSerialRequest());
		if (req == null)
			return;

		conn.getContext().setUseSerial(false);

		this.api.server.getSerialHandler().closeSerial();

		FlatBufferBuilder fbb = new FlatBufferBuilder(32);
		SerialUpdateResponse.startSerialUpdateResponse(fbb);
		SerialUpdateResponse.addClosed(fbb, !this.api.server.getSerialHandler().isConnected());
		int update = SerialUpdateResponse.endSerialUpdateResponse(fbb);
		int outbound = rpcHandler.createRPCMessage(fbb, RpcMessage.SerialUpdateResponse, update);
		fbb.finish(outbound);
		conn.send(fbb.dataBuffer());
	}

	public void forAllListeners(Consumer<? super GenericConnection> action) {
		this.api
			.getAPIServers()
			.forEach(
				(server) -> server
					.getAPIConnections()
					.filter(conn -> conn.getContext().useSerial())
					.forEach(action)
			);
	}

}
