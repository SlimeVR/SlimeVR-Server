package dev.slimevr.websocketapi;

import com.google.flatbuffers.FlatBufferBuilder;
import dev.slimevr.vr.trackers.udp.TrackerUDPConnection;
import slimevr_protocol.InboundPacket;
import slimevr_protocol.InboundUnion;
import slimevr_protocol.data_feed.*;
import slimevr_protocol.datatypes.hardware_info.FirmwareInfo;
import slimevr_protocol.datatypes.hardware_info.FirmwareInfoMaskT;

import java.nio.ByteBuffer;
import java.util.List;

public class DataFeedHandler {

	private final ProtocolAPI api;

	public DataFeedHandler(ProtocolAPI api) {
		this.api = api;

		api.registerPacketListener(InboundUnion.slimevr_protocol_data_feed_DataFeedRequest, this::onDataFeedRequest);
		api.registerPacketListener(InboundUnion.slimevr_protocol_data_feed_PollDataFeed, this::onPollDataFeedRequest);
	}

	private void onDataFeedRequest(GenericConnection conn, InboundPacket inboundPacket) {
		DataFeedRequest req = (DataFeedRequest) inboundPacket.packet(new DataFeedRequest());
		if (req == null) return;
		int dataFeeds = req.dataFeedsLength();

		conn.getContext().getDataFeedConfigList().clear();
		for (int i = 0; i < dataFeeds; i++) {
			DataFeedConfigT config = req.dataFeeds(i).unpack();
			conn.getContext().getDataFeedConfigList().add(config);
		}
	}

	private void onPollDataFeedRequest(GenericConnection conn, InboundPacket inboundPacket) {

	}

	public ByteBuffer dataFeedBuffer(DataFeedConfigT config) {

		FlatBufferBuilder fbb = new FlatBufferBuilder(300);

		DataFeedUpdate.startDataFeedUpdate(fbb);
		if (config.getDataMask() != null) {
			DeviceStatusMaskT maskT = config.getDataMask();
			List<TrackerUDPConnection> devicesList = this.api.server.getTrackersServer().getConnections();
			int[] devicesStatusOffsets = new int[devicesList.size()];
			for (int i = 0; i < devicesList.size(); i++) {

				TrackerUDPConnection device = devicesList.get(i);
				FirmwareInfoMaskT firmwareInfoMaskT = maskT.getFirmwareInfo();

				int nameOffset = -1;
				int versionOffset = -1;

				if (maskT.getCustomName())
					nameOffset = fbb.createString(device.name);
				if (firmwareInfoMaskT != null && firmwareInfoMaskT.getFirmwareVersion())
					versionOffset = fbb.createString(device.firmwareBuild + "");


				int firmwareInfoOffset = -1;
				if (firmwareInfoMaskT != null) {
					{
						FirmwareInfo.startFirmwareInfo(fbb);
						if (firmwareInfoMaskT.getFirmwareVersion())
							FirmwareInfo.addFirmwareVersion(fbb, versionOffset);
						if (firmwareInfoMaskT.getDisplayName())

							firmwareInfoOffset = FirmwareInfo.endFirmwareInfo(fbb);
						DeviceStatus.addFirmwareInfo(fbb, firmwareInfoOffset);
					}
				}

				{
					DeviceStatus.startDeviceStatus(fbb);
					if (maskT.getCustomName())
						DeviceStatus.addCustomName(fbb, nameOffset);
					if (firmwareInfoMaskT != null)
						DeviceStatus.addFirmwareInfo(fbb, firmwareInfoOffset);
					devicesStatusOffsets[i] = DeviceStatus.endDeviceStatus(fbb);
				}
			}

			int devices = DataFeedUpdate.createDevicesVector(fbb, devicesStatusOffsets);
			DataFeedUpdate.addDevices(fbb, devices);
		}
		return null;
	}

	public void sendDataFeedUpdate() {
		this.api.getAPIServers().forEach((server) -> {
			server.getAPIConnections().values().forEach((conn) -> {


//				conn.send(this.dataFeedBuffer(conn.getContext().));
			});
		});
	}

}
