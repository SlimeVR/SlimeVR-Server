package dev.slimevr.protocol;

import com.google.flatbuffers.FlatBufferBuilder;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.slimevr.vr.trackers.IMUTracker;
import dev.slimevr.vr.trackers.Tracker;
import dev.slimevr.vr.trackers.udp.UDPDevice;
import solarxr_protocol.data_feed.DataFeedUpdate;
import solarxr_protocol.data_feed.device_data.DeviceData;
import solarxr_protocol.data_feed.device_data.DeviceDataMaskT;
import solarxr_protocol.data_feed.tracker.TrackerData;
import solarxr_protocol.data_feed.tracker.TrackerDataMaskT;
import solarxr_protocol.data_feed.tracker.TrackerInfo;
import solarxr_protocol.datatypes.DeviceId;
import solarxr_protocol.datatypes.Temperature;
import solarxr_protocol.datatypes.TrackerId;
import solarxr_protocol.datatypes.hardware_info.HardwareInfo;
import solarxr_protocol.datatypes.hardware_info.HardwareStatus;
import solarxr_protocol.datatypes.math.Quat;
import solarxr_protocol.datatypes.math.Vec3f;

import java.util.ArrayList;
import java.util.List;


public class DataFeedBuilder {

	public static int createHardwareInfo(FlatBufferBuilder fbb, UDPDevice device) {
		IMUTracker tracker = device.sensors.get(0);

		HardwareInfo.startHardwareInfo(fbb);
		// BRUH MOMENT
		// TODO need support: HardwareInfo.addFirmwareVersion(fbb,
		// firmwareVersionOffset);
		// TODO need support: HardwareInfo.addHardwareRevision(fbb,
		// hardwareRevisionOffset);
		// TODO need support: HardwareInfo.addManufacturer(fbb, device.m);
		// TODO need support: HardwareInfo.addDisplayName(fbb, de);
		// TODO need support: HardwareInfo.addHardwareAddress(fbb, tracker.);
		// TODO need support: HardwareInfo.addMcuId(device);
		return HardwareInfo.endHardwareInfo(fbb);
	}

	public static int createTrackerId(FlatBufferBuilder fbb, Tracker tracker) {
		TrackerId.startTrackerId(fbb);

		TrackerId.addTrackerNum(fbb, tracker.getTrackerNum());
		if (tracker.getDevice() != null)
			TrackerId.addDeviceId(fbb, DeviceId.createDeviceId(fbb, tracker.getDevice().getId()));

		return TrackerId.endTrackerId(fbb);
	}

	public static int createTrackerInfos(FlatBufferBuilder fbb, boolean infoMask, Tracker tracker) {

		if (!infoMask)
			return 0;

		TrackerInfo.startTrackerInfo(fbb);
		if (tracker.getBodyPosition() != null)
			TrackerInfo.addBodyPart(fbb, tracker.getBodyPosition().bodyPart);
		TrackerInfo.addEditable(fbb, tracker.userEditable());
		TrackerInfo.addComputed(fbb, tracker.isComputed());
		// TODO need support: TrackerInfo.addImuType(fbb, tracker.im);
		// TODO need support: TrackerInfo.addPollRate(fbb, tracker.);

		if (tracker instanceof IMUTracker) {
			IMUTracker imuTracker = (IMUTracker) tracker;
			if (imuTracker.getMountingRotation() != null) {
				Quaternion quaternion = imuTracker.getMountingRotation();
				TrackerInfo
					.addMountingOrientation(
						fbb,
						Quat
							.createQuat(
								fbb,
								quaternion.getX(),
								quaternion.getY(),
								quaternion.getZ(),
								quaternion.getW()
							)
					);
			}
		}
		return TrackerInfo.endTrackerInfo(fbb);
	}

	public static int createTrackerPosition(FlatBufferBuilder fbb, Tracker tracker) {
		Vector3f pos = new Vector3f();
		tracker.getPosition(pos);

		return Vec3f.createVec3f(fbb, pos.x, pos.y, pos.z);
	}

	public static int createTrackerRotation(FlatBufferBuilder fbb, Tracker tracker) {
		Quaternion quaternion = new Quaternion();
		tracker.getRotation(quaternion);

		return Quat
			.createQuat(
				fbb,
				quaternion.getX(),
				quaternion.getY(),
				quaternion.getZ(),
				quaternion.getW()
			);
	}

	public static int createTrackerTemperature(FlatBufferBuilder fbb, Tracker tracker) {
		if (!(tracker instanceof IMUTracker))
			return 0;
		IMUTracker imuTracker = (IMUTracker) tracker;
		return Temperature.createTemperature(fbb, imuTracker.temperature);
	}

	public static int createTrackerData(
		FlatBufferBuilder fbb,
		TrackerDataMaskT mask,
		Tracker tracker
	) {
		int trackerInfosOffset = DataFeedBuilder.createTrackerInfos(fbb, mask.getInfo(), tracker);
		int trackerIdOffset = DataFeedBuilder.createTrackerId(fbb, tracker);

		TrackerData.startTrackerData(fbb);

		TrackerData.addTrackerId(fbb, trackerIdOffset);

		if (trackerInfosOffset != 0)
			TrackerData.addInfo(fbb, trackerInfosOffset);
		if (mask.getStatus())
			TrackerData.addStatus(fbb, tracker.getStatus().id + 1);
		if (mask.getPosition())
			TrackerData.addPosition(fbb, DataFeedBuilder.createTrackerPosition(fbb, tracker));
		if (mask.getRotation())
			TrackerData.addRotation(fbb, DataFeedBuilder.createTrackerRotation(fbb, tracker));
		if (mask.getTemp()) {
			int trackerTemperatureOffset = DataFeedBuilder.createTrackerTemperature(fbb, tracker);
			if (trackerTemperatureOffset != 0)
				TrackerData.addTemp(fbb, trackerTemperatureOffset);
		}

		return TrackerData.endTrackerData(fbb);
	}

	public static int createTrackersData(
		FlatBufferBuilder fbb,
		DeviceDataMaskT mask,
		UDPDevice device
	) {
		if (mask.getTrackerData() == null)
			return 0;

		List<Integer> trackersOffsets = new ArrayList<>();

		device.sensors.forEach((key, value) -> {
			trackersOffsets
				.add(DataFeedBuilder.createTrackerData(fbb, mask.getTrackerData(), value));
		});

		DeviceData.startTrackersVector(fbb, trackersOffsets.size());
		trackersOffsets.forEach(offset -> {
			DeviceData.addTrackers(fbb, offset);
		});
		return fbb.endVector();
	}

	public static int createDeviceData(
		FlatBufferBuilder fbb,
		int id,
		DeviceDataMaskT mask,
		UDPDevice device
	) {
		if (!mask.getDeviceData())
			return 0;

		IMUTracker tracker = device.sensors.get(0);

		if (tracker == null)
			return 0;

		int hardwareDataOffset = HardwareStatus
			.createHardwareStatus(
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
		int trackersOffset = DataFeedBuilder.createTrackersData(fbb, mask, device);

		DeviceData.startDeviceData(fbb);
		// TODO need support: DeviceData.addCustomName(fbb, nameOffset);
		DeviceData.addId(fbb, DeviceId.createDeviceId(fbb, id));
		DeviceData.addHardwareStatus(fbb, hardwareDataOffset);
		DeviceData.addHardwareInfo(fbb, hardwareInfoOffset);
		DeviceData.addTrackers(fbb, trackersOffset);

		return DeviceData.endDeviceData(fbb);
	}

	public static int createSyntheticTrackersData(
		FlatBufferBuilder fbb,
		TrackerDataMaskT trackerDataMaskT,
		List<Tracker> trackers
	) {
		if (trackerDataMaskT == null)
			return 0;

		List<Integer> trackerOffsets = new ArrayList<>();

		trackers.stream().filter(Tracker::isComputed).forEach((tracker) -> {
			trackerOffsets.add(DataFeedBuilder.createTrackerData(fbb, trackerDataMaskT, tracker));
		});

		DataFeedUpdate.startSyntheticTrackersVector(fbb, trackerOffsets.size());
		trackerOffsets.forEach((tracker -> {
			DataFeedUpdate.addSyntheticTrackers(fbb, tracker);
		}));
		return fbb.endVector();
	}

	public static int createDevicesData(
		FlatBufferBuilder fbb,
		DeviceDataMaskT deviceDataMaskT,
		List<UDPDevice> devices
	) {
		if (deviceDataMaskT == null)
			return 0;

		int[] devicesDataOffsets = new int[devices.size()];
		for (int i = 0; i < devices.size(); i++) {
			UDPDevice device = devices.get(i);
			devicesDataOffsets[i] = DataFeedBuilder
				.createDeviceData(fbb, i, deviceDataMaskT, device);
		}

		return DataFeedUpdate.createDevicesVector(fbb, devicesDataOffsets);
	}
}
