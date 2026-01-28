package dev.slimevr.protocol.datafeed

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.guards.ServerGuards
import dev.slimevr.tracking.processor.Bone
import dev.slimevr.tracking.processor.skeleton.HumanSkeleton
import dev.slimevr.tracking.processor.stayaligned.poses.RelaxedPose
import dev.slimevr.tracking.processor.stayaligned.trackers.RestDetector
import dev.slimevr.tracking.processor.stayaligned.trackers.StayAlignedTrackerState
import dev.slimevr.tracking.trackers.Device
import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.udp.MagnetometerStatus
import dev.slimevr.tracking.trackers.udp.UDPDevice
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.data_feed.DataFeedUpdate
import solarxr_protocol.data_feed.device_data.DeviceData
import solarxr_protocol.data_feed.device_data.DeviceDataMaskT
import solarxr_protocol.data_feed.stay_aligned.StayAlignedPose
import solarxr_protocol.data_feed.stay_aligned.StayAlignedTracker
import solarxr_protocol.data_feed.tracker.TrackerData
import solarxr_protocol.data_feed.tracker.TrackerDataMaskT
import solarxr_protocol.data_feed.tracker.TrackerInfo
import solarxr_protocol.datatypes.DeviceId
import solarxr_protocol.datatypes.Ipv4Address
import solarxr_protocol.datatypes.Temperature
import solarxr_protocol.datatypes.TrackerId
import solarxr_protocol.datatypes.hardware_info.HardwareInfo
import solarxr_protocol.datatypes.hardware_info.HardwareStatus
import solarxr_protocol.datatypes.math.Quat
import solarxr_protocol.datatypes.math.Vec3f
import java.nio.ByteBuffer
import java.util.function.Consumer

fun createHardwareInfo(fbb: FlatBufferBuilder, device: Device): Int {
	val nameOffset = if (device.firmwareVersion != null) {
		fbb.createString(device.firmwareVersion)
	} else {
		0
	}

	val manufacturerOffset = if (device.manufacturer != null) {
		fbb.createString(device.manufacturer)
	} else {
		0
	}

	val firmwareDateOffset = if (device.firmwareDate != null) {
		fbb.createString(device.firmwareDate)
	} else {
		0
	}

	val hardwareIdentifierOffset = fbb.createString(device.hardwareIdentifier)

	HardwareInfo.startHardwareInfo(fbb)
	HardwareInfo.addFirmwareVersion(fbb, nameOffset)
	HardwareInfo.addFirmwareDate(fbb, firmwareDateOffset)
	HardwareInfo.addManufacturer(fbb, manufacturerOffset)
	HardwareInfo.addHardwareIdentifier(fbb, hardwareIdentifierOffset)

	if (device is UDPDevice) {
		val address = device.ipAddress.address
		HardwareInfo
			.addIpAddress(
				fbb,
				Ipv4Address
					.createIpv4Address(
						fbb,
						ByteBuffer.wrap(address).getInt().toLong(),
					),
			)

		HardwareInfo.addNetworkProtocolVersion(fbb, device.protocolVersion)
	}

	// BRUH MOMENT
	// TODO need support: HardwareInfo.addHardwareRevision(fbb,
	// hardwareRevisionOffset);
	// TODO need support: HardwareInfo.addDisplayName(fbb, de);
	HardwareInfo.addMcuId(fbb, device.mcuType.getSolarType())
	HardwareInfo.addOfficialBoardType(fbb, device.boardType.getSolarType())
	return HardwareInfo.endHardwareInfo(fbb)
}

fun createTrackerId(fbb: FlatBufferBuilder, tracker: Tracker): Int {
	TrackerId.startTrackerId(fbb)

	TrackerId.addTrackerNum(fbb, tracker.trackerNum)
	if (tracker.device != null) {
		TrackerId.addDeviceId(
			fbb,
			DeviceId.createDeviceId(fbb, tracker.device.id),
		)
	}

	return TrackerId.endTrackerId(fbb)
}

fun createVec3(fbb: FlatBufferBuilder, vec: Vector3): Int = Vec3f
	.createVec3f(
		fbb,
		vec.x,
		vec.y,
		vec.z,
	)

fun createQuat(fbb: FlatBufferBuilder, quaternion: Quaternion): Int = Quat
	.createQuat(
		fbb,
		quaternion.x,
		quaternion.y,
		quaternion.z,
		quaternion.w,
	)

fun createTrackerInfos(
	fbb: FlatBufferBuilder,
	infoMask: Boolean,
	tracker: Tracker,
): Int {
	if (!infoMask) return 0

	val displayNameOffset = fbb.createString(tracker.displayName)
	val customNameOffset = if (tracker.customName != null) {
		fbb.createString(tracker.customName)
	} else {
		0
	}

	TrackerInfo.startTrackerInfo(fbb)
	if (tracker.trackerPosition != null) {
		TrackerInfo.addBodyPart(
			fbb,
			tracker.trackerPosition!!.bodyPart,
		)
	}
	TrackerInfo.addEditable(fbb, tracker.userEditable)
	TrackerInfo.addIsComputed(fbb, tracker.isComputed)
	TrackerInfo.addDisplayName(fbb, displayNameOffset)
	TrackerInfo.addCustomName(fbb, customNameOffset)
	if (tracker.imuType != null) {
		TrackerInfo.addImuType(fbb, tracker.imuType.getSolarType())
	}

	// TODO need support: TrackerInfo.addPollRate(fbb, tracker.);
	if (tracker.isImu()) {
		TrackerInfo.addIsImu(fbb, true)
		TrackerInfo
			.addAllowDriftCompensation(
				fbb,
				tracker.resetsHandler.allowDriftCompensation,
			)
	} else {
		TrackerInfo.addIsImu(fbb, false)
		TrackerInfo.addAllowDriftCompensation(fbb, false)
	}

	if (tracker.allowMounting) {
		val quaternion = tracker.resetsHandler.mountingOrientation
		val mountResetFix = tracker.resetsHandler.mountRotFix
		TrackerInfo.addMountingOrientation(fbb, createQuat(fbb, quaternion))
		TrackerInfo.addMountingResetOrientation(fbb, createQuat(fbb, mountResetFix))
	}

	TrackerInfo.addMagnetometer(fbb, tracker.magStatus.getSolarType())
	TrackerInfo.addIsHmd(fbb, tracker.isHmd)

	TrackerInfo.addDataSupport(fbb, tracker.trackerDataType.getSolarType())

	return TrackerInfo.endTrackerInfo(fbb)
}

fun createTrackerPosition(fbb: FlatBufferBuilder, tracker: Tracker): Int = createVec3(fbb, tracker.position)

fun createTrackerRotation(fbb: FlatBufferBuilder, tracker: Tracker): Int = createQuat(fbb, tracker.getRawRotation())

fun createTrackerAcceleration(fbb: FlatBufferBuilder, tracker: Tracker): Int = createVec3(fbb, tracker.getAcceleration())

fun createTrackerMagneticVector(fbb: FlatBufferBuilder, tracker: Tracker): Int = createVec3(fbb, tracker.getMagVector())

fun createTrackerTemperature(fbb: FlatBufferBuilder, tracker: Tracker): Int {
	if (tracker.temperature == null) return 0
	return Temperature.createTemperature(fbb, tracker.temperature!!)
}

fun createTrackerData(
	fbb: FlatBufferBuilder,
	mask: TrackerDataMaskT,
	tracker: Tracker,
): Int {
	val trackerInfosOffset = createTrackerInfos(fbb, mask.info, tracker)
	val trackerIdOffset = createTrackerId(fbb, tracker)

	var stayAlignedOffset = 0
	if (mask.stayAligned) {
		stayAlignedOffset =
			createTrackerStayAlignedTracker(fbb, tracker.stayAligned)
	}

	TrackerData.startTrackerData(fbb)

	TrackerData.addTrackerId(fbb, trackerIdOffset)

	if (trackerInfosOffset != 0) TrackerData.addInfo(fbb, trackerInfosOffset)
	if (mask.status) TrackerData.addStatus(fbb, tracker.status.id + 1)
	if (mask.position && tracker.hasPosition) {
		TrackerData.addPosition(
			fbb,
			createTrackerPosition(fbb, tracker),
		)
	}
	if (mask.rotation && tracker.hasRotation) {
		TrackerData.addRotation(
			fbb,
			createTrackerRotation(fbb, tracker),
		)
	}
	if (mask.linearAcceleration && tracker.hasAcceleration) {
		TrackerData
			.addLinearAcceleration(
				fbb,
				createTrackerAcceleration(fbb, tracker),
			)
	}
	if (mask.temp) {
		val trackerTemperatureOffset = createTrackerTemperature(fbb, tracker)
		if (trackerTemperatureOffset != 0) {
			TrackerData.addTemp(
				fbb,
				trackerTemperatureOffset,
			)
		}
	}
	if (tracker.allowMounting && tracker.hasRotation) {
		if (mask.rotationReferenceAdjusted) {
			TrackerData
				.addRotationReferenceAdjusted(
					fbb,
					createQuat(fbb, tracker.getRotation()),
				)
		}
		if (mask.rotationIdentityAdjusted) {
			TrackerData
				.addRotationIdentityAdjusted(
					fbb,
					createQuat(fbb, tracker.getIdentityAdjustedRotation()),
				)
		}
	} else if (tracker.allowReset && tracker.hasRotation) {
		if (mask.rotationReferenceAdjusted) {
			TrackerData
				.addRotationReferenceAdjusted(
					fbb,
					createQuat(fbb, tracker.getRotation()),
				)
		}
		if (mask.rotationIdentityAdjusted) {
			TrackerData
				.addRotationIdentityAdjusted(
					fbb,
					createQuat(fbb, tracker.getRawRotation()),
				)
		}
	}
	if (mask.tps) {
		TrackerData.addTps(fbb, tracker.tps.toInt())
	}
	if (mask.rawMagneticVector && tracker.magStatus == MagnetometerStatus.ENABLED) {
		TrackerData.addRawMagneticVector(
			fbb,
			createTrackerMagneticVector(fbb, tracker),
		)
	}
	if (mask.stayAligned) {
		TrackerData.addStayAligned(fbb, stayAlignedOffset)
	}

	return TrackerData.endTrackerData(fbb)
}

fun createTrackersData(
	fbb: FlatBufferBuilder,
	mask: DeviceDataMaskT,
	device: Device,
): Int {
	if (mask.trackerData == null) return 0

	val trackersOffsets: MutableList<Int> = ArrayList()

	device
		.trackers
		.forEach { (_: Int, value: Tracker) ->
			trackersOffsets
				.add(createTrackerData(fbb, mask.trackerData, value))
		}

	DeviceData.startTrackersVector(fbb, trackersOffsets.size)
	trackersOffsets.forEach(
		Consumer { offset: Int ->
			DeviceData.addTrackers(
				fbb,
				offset,
			)
		},
	)
	return fbb.endVector()
}

fun createDeviceData(
	fbb: FlatBufferBuilder,
	id: Int,
	mask: DeviceDataMaskT,
	device: Device,
): Int {
	if (!mask.deviceData) return 0

	if (device.trackers.isEmpty()) return 0

	var firstTracker = device.trackers[0]
	if (firstTracker == null) {
		// Not actually the "first" tracker, but do we care?
		firstTracker = device.trackers.entries.iterator().next().value
	}

	val tracker: Tracker = firstTracker

	HardwareStatus.startHardwareStatus(fbb)
	HardwareStatus.addErrorStatus(fbb, tracker.status.id)

	if (tracker.batteryVoltage != null) {
		HardwareStatus.addBatteryVoltage(fbb, tracker.batteryVoltage!!)
	}
	if (tracker.batteryLevel != null) {
		HardwareStatus.addBatteryPctEstimate(fbb, tracker.batteryLevel!!.toInt())
	}
	if (tracker.ping != null) {
		HardwareStatus.addPing(fbb, tracker.ping!!)
	}
	if (tracker.signalStrength != null) {
		HardwareStatus.addRssi(fbb, tracker.signalStrength!!.toShort())
	}
	if (tracker.packetLoss != null) {
		HardwareStatus.addPacketLoss(fbb, tracker.packetLoss!!)
	}
	if (tracker.packetsLost != null) {
		HardwareStatus.addPacketsLost(fbb, tracker.packetsLost!!)
	}
	if (tracker.packetsReceived != null) {
		HardwareStatus.addPacketsReceived(fbb, tracker.packetsReceived!!)
	}
	if (tracker.batteryRemainingRuntime != null) {
		HardwareStatus.addBatteryRuntimeEstimate(fbb, tracker.batteryRemainingRuntime!!)
	}

	val hardwareDataOffset = HardwareStatus.endHardwareStatus(fbb)
	val hardwareInfoOffset = createHardwareInfo(fbb, device)
	val trackersOffset = createTrackersData(fbb, mask, device)

	val nameOffset = if (device.name != null) {
		fbb.createString(device.name)
	} else {
		0
	}

	DeviceData.startDeviceData(fbb)
	DeviceData.addCustomName(fbb, nameOffset)
	DeviceData.addId(fbb, DeviceId.createDeviceId(fbb, id))
	DeviceData.addHardwareStatus(fbb, hardwareDataOffset)
	DeviceData.addHardwareInfo(fbb, hardwareInfoOffset)
	DeviceData.addTrackers(fbb, trackersOffset)

	return DeviceData.endDeviceData(fbb)
}

fun createSyntheticTrackersData(
	fbb: FlatBufferBuilder,
	trackerDataMaskT: TrackerDataMaskT?,
	trackers: MutableList<Tracker>,
): Int {
	if (trackerDataMaskT == null) return 0

	val trackerOffsets: MutableList<Int> = ArrayList()

	trackers
		.forEach(
			Consumer { tracker: Tracker ->
				trackerOffsets
					.add(createTrackerData(fbb, trackerDataMaskT, tracker))
			},
		)

	DataFeedUpdate.startSyntheticTrackersVector(fbb, trackerOffsets.size)
	trackerOffsets.forEach(
		(
			Consumer { tracker: Int ->
				DataFeedUpdate.addSyntheticTrackers(
					fbb,
					tracker,
				)
			}
			),
	)
	return fbb.endVector()
}

fun createDevicesData(
	fbb: FlatBufferBuilder,
	deviceDataMaskT: DeviceDataMaskT?,
	devices: MutableList<Device>,
): Int {
	if (deviceDataMaskT == null) return 0

	val devicesDataOffsets = IntArray(devices.size)
	for (i in devices.indices) {
		val device = devices[i]
		devicesDataOffsets[i] =
			createDeviceData(fbb, device.id, deviceDataMaskT, device)
	}

	return DataFeedUpdate.createDevicesVector(fbb, devicesDataOffsets)
}

fun createBonesData(
	fbb: FlatBufferBuilder,
	shouldSend: Boolean,
	bones: MutableList<Bone>,
): Int {
	if (!shouldSend) {
		return 0
	}

	val boneOffsets = IntArray(bones.size)
	for (i in bones.indices) {
		val bi = bones[i]

		val headPosG =
			bi.getPosition()
		val rotG =
			bi.getGlobalRotation()
		val length = bi.length

		solarxr_protocol.data_feed.Bone.startBone(fbb)

		val rotGOffset = createQuat(fbb, rotG)
		solarxr_protocol.data_feed.Bone.addRotationG(fbb, rotGOffset)
		val headPosGOffset = Vec3f
			.createVec3f(fbb, headPosG.x, headPosG.y, headPosG.z)
		solarxr_protocol.data_feed.Bone.addHeadPositionG(fbb, headPosGOffset)
		solarxr_protocol.data_feed.Bone.addBodyPart(fbb, bi.boneType.bodyPart)
		solarxr_protocol.data_feed.Bone.addBoneLength(fbb, length)

		boneOffsets[i] = solarxr_protocol.data_feed.Bone.endBone(fbb)
	}

	return DataFeedUpdate.createBonesVector(fbb, boneOffsets)
}

fun createStayAlignedPose(
	fbb: FlatBufferBuilder,
	humanSkeleton: HumanSkeleton,
): Int {
	val relaxedPose = RelaxedPose.fromTrackers(humanSkeleton)

	StayAlignedPose.startStayAlignedPose(fbb)

	StayAlignedPose.addUpperLegAngleInDeg(fbb, relaxedPose.upperLeg.toDeg())
	StayAlignedPose.addLowerLegAngleInDeg(fbb, relaxedPose.lowerLeg.toDeg())
	StayAlignedPose.addFootAngleInDeg(fbb, relaxedPose.foot.toDeg())

	return StayAlignedPose.endStayAlignedPose(fbb)
}

fun createTrackerStayAlignedTracker(
	fbb: FlatBufferBuilder,
	state: StayAlignedTrackerState,
): Int {
	StayAlignedTracker.startStayAlignedTracker(fbb)

	StayAlignedTracker.addYawCorrectionInDeg(fbb, state.yawCorrection.toDeg())
	StayAlignedTracker.addLockedErrorInDeg(
		fbb,
		state.yawErrors.lockedError.toL2Norm().toDeg(),
	)
	StayAlignedTracker.addCenterErrorInDeg(
		fbb,
		state.yawErrors.centerError.toL2Norm().toDeg(),
	)
	StayAlignedTracker.addNeighborErrorInDeg(
		fbb,
		state.yawErrors.neighborError.toL2Norm().toDeg(),
	)
	StayAlignedTracker.addLocked(
		fbb,
		state.restDetector.state == RestDetector.State.AT_REST,
	)

	return StayAlignedTracker.endStayAlignedTracker(fbb)
}

fun createServerGuard(fbb: FlatBufferBuilder, serverGuards: ServerGuards): Int = solarxr_protocol.data_feed.server.ServerGuards.createServerGuards(
	fbb,
	serverGuards.canDoMounting,
	serverGuards.canDoYawReset,
	serverGuards.canDoUserHeightCalibration,
)
