package dev.slimevr.websocketapi;

import com.google.flatbuffers.FlatBufferBuilder;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.slimevr.vr.trackers.IMUTracker;
import dev.slimevr.vr.trackers.udp.TrackerUDPConnection;
import slimevr_protocol.data_feed.DataFeedConfigT;
import slimevr_protocol.data_feed.DataFeedUpdate;
import slimevr_protocol.data_feed.device_data.DeviceData;
import slimevr_protocol.data_feed.device_data.DeviceDataMaskT;
import slimevr_protocol.data_feed.tracker.*;
import slimevr_protocol.datatypes.DeviceId;
import slimevr_protocol.datatypes.Temperature;
import slimevr_protocol.datatypes.TrackerId;
import slimevr_protocol.datatypes.hardware_info.HardwareInfo;
import slimevr_protocol.datatypes.hardware_info.HardwareStatus;
import slimevr_protocol.datatypes.math.Quat;
import slimevr_protocol.datatypes.math.Vec3f;

import java.util.ArrayList;
import java.util.List;

public class DataFeedBuilder {

	public static int createHardwareInfo(FlatBufferBuilder fbb, TrackerUDPConnection device) {
		IMUTracker tracker = device.sensors.get(0);

		HardwareInfo.startHardwareInfo(fbb);
		// BRUH MOMENT
		//TODO need support:  HardwareInfo.addFirmwareVersion(fbb, firmwareVersionOffset);
		//TODO need support:  HardwareInfo.addHardwareRevision(fbb, hardwareRevisionOffset);
		//TODO need support:  HardwareInfo.addManufacturer(fbb, device.m);
		//TODO need support:  HardwareInfo.addDisplayName(fbb, de);
		//TODO need support:  HardwareInfo.addHardwareAddress(fbb, tracker.);
		//TODO need support:  HardwareInfo.addMcuId(device);
		return HardwareInfo.endHardwareInfo(fbb);
	}

	public static int createTrackerId(FlatBufferBuilder fbb, int trackerNum, int deviceId) {
		TrackerId.startTrackerId(fbb);

		TrackerId.addTrackerNum(fbb, trackerNum);
		TrackerId.addDeviceId(fbb, DeviceId.createDeviceId(fbb, deviceId));

		return TrackerId.endTrackerId(fbb);
	}

	public static int createTrackerDataComponents(FlatBufferBuilder fbb, TrackerDataMaskT mask, IMUTracker tracker) {

		List<Integer> dataOffsets = new ArrayList<>(TrackerDataComponent.names.length);

		for (int index = 0; index < TrackerDataComponent.names.length; index++) {
			if (index == TrackerDataComponent.info && mask.getInfo()) {
//				Vector3f pos = new Vector3f();
//				tracker.getPosition(pos);
//				dataOffsets[index] = TrackerInfo.;
			}

			if (index == TrackerDataComponent.position && mask.getPosition()) {
				Vector3f pos = new Vector3f();
				tracker.getPosition(pos);
				dataOffsets.add(
						TrackerDataComponentW.createTrackerDataComponentW(
								fbb,
								TrackerDataComponent.position,
								Vec3f.createVec3f(fbb, pos.x, pos.y, pos.z)
						)
				);
			}

			if (index == TrackerDataComponent.rotation && mask.getRotation()) {
				Quaternion quaternion = new Quaternion();
				tracker.getRotation(quaternion);
				dataOffsets.add(
						TrackerDataComponentW.createTrackerDataComponentW(
								fbb,
								TrackerDataComponent.rotation,
								Quat.createQuat(fbb,
										quaternion.getX(),
										quaternion.getY(),
										quaternion.getZ(),
										quaternion.getW()
								)
						)
				);
			}

			if (index == TrackerDataComponent.temp && mask.getTemp()) {
				dataOffsets.add(TrackerDataComponentW.createTrackerDataComponentW(fbb, TrackerDataComponent.temp, Temperature.createTemperature(fbb, tracker.temperature)));
			}
		}

		TrackerData.startDataVector(fbb, dataOffsets.size());
		dataOffsets.forEach((offset) -> {
			TrackerData.addData(fbb, offset);
		});
		return fbb.endVector();
	}

	public static int createTrackersData(FlatBufferBuilder fbb, DeviceDataMaskT mask, TrackerUDPConnection device, int deviceId) {
		if (mask.getTrackerData() == null) return -1;

		int[] sensorsOffsets = new int[device.sensors.size()];

		device.sensors.forEach((key, value) -> {
			int trackerIdOffset = DataFeedBuilder.createTrackerId(fbb, key, deviceId);
			int dataOffset = DataFeedBuilder.createTrackerDataComponents(fbb, mask.getTrackerData(), value);
			sensorsOffsets[key] = TrackerData.createTrackerData(fbb, trackerIdOffset, dataOffset);
		});

		return TrackerData.createDataVector(fbb, sensorsOffsets);
	}

	public static int createDeviceData(FlatBufferBuilder fbb, int id, DeviceDataMaskT mask, TrackerUDPConnection device) {
		if (!mask.getDeviceData()) return -1;

		IMUTracker tracker = device.sensors.get(0);

		if (tracker == null) return -1;

		int nameOffset = fbb.createString(device.name);
		int hardwareDataOffset = HardwareStatus.createHardwareStatus(
				fbb,
				tracker.getStatus().id,
				(int) tracker.getTPS(),
				tracker.ping,
				(short) tracker.signalStrength,
				tracker.temperature,
				tracker.getBatteryVoltage(),
				(int) tracker.getBatteryLevel(),
				-1
		);
		int hardwareInfoOffset = DataFeedBuilder.createHardwareInfo(fbb, device);
		int trackersOffset = DataFeedBuilder.createTrackersData(fbb, mask, device, id);

		DeviceData.startDeviceData(fbb);
		DeviceData.addCustomName(fbb, nameOffset);
		DeviceData.addId(fbb, DeviceId.createDeviceId(fbb, id));
		DeviceData.addHardwareStatus(fbb, hardwareDataOffset);
		DeviceData.addHardwareInfo(fbb, hardwareInfoOffset);
		DeviceData.addTrackers(fbb, trackersOffset);

		return DeviceData.endDeviceData(fbb);
	}

	public static int createDevicesData(FlatBufferBuilder fbb, DataFeedConfigT config, List<TrackerUDPConnection> devices) {
		if (config.getDataMask() == null) return -1;

		DeviceDataMaskT deviceDataMaskT = config.getDataMask();
		int[] devicesDataOffsets = new int[devices.size()];
		for (int i = 0; i < devices.size(); i++) {
			TrackerUDPConnection device = devices.get(i);
			devicesDataOffsets[i] = DataFeedBuilder.createDeviceData(fbb, i, deviceDataMaskT, device);
		}

		return DataFeedUpdate.createDevicesVector(fbb, devicesDataOffsets);
	}
}
