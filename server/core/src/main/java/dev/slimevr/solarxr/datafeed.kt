package dev.slimevr.solarxr

import dev.slimevr.VRServer
import dev.slimevr.device.DeviceState
import dev.slimevr.skeleton.BoneState
import dev.slimevr.skeleton.Skeleton
import dev.slimevr.tracker.TrackerState
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import solarxr_protocol.data_feed.DataFeedConfig
import solarxr_protocol.data_feed.DataFeedMessageHeader
import solarxr_protocol.data_feed.DataFeedUpdate
import solarxr_protocol.data_feed.PollDataFeed
import solarxr_protocol.data_feed.StartDataFeed
import solarxr_protocol.data_feed.device_data.DeviceData
import solarxr_protocol.data_feed.tracker.TrackerData
import solarxr_protocol.data_feed.tracker.TrackerDataMask
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

private fun createTracker(device: DeviceState, tracker: TrackerState, trackerMask: TrackerDataMask, datafeedConfig: DataFeedConfig): TrackerData {
	return TrackerData(
		trackerId = TrackerId(
			trackerNum = tracker.id.toUByte(),
			deviceId = DeviceId(device.id.toUByte()),
		),
		status = if (trackerMask.status == true) device.status else null,
		rotation = if (trackerMask.rotation == true) tracker.rawRotation.let { Quat(it.x, it.y, it.z, it.w) } else null,
		position = if (trackerMask.position == true && tracker.position != null) tracker.position.let { Vec3f(it.x, it.y, it.z) } else null,
		info = if (trackerMask.info == true) {
			TrackerInfo(
				imuType = tracker.sensorType,
				bodyPart = tracker.bodyPart,
				displayName = tracker.name,
				isImu = tracker.sensorType != null
			)
		} else {
			null
		},
		tps = if (trackerMask.tps == true) tracker.tps else null,
		temp = if (trackerMask.temp == true && tracker.imuTemp != null) Temperature(temp = tracker.imuTemp) else null,
		rawAcceleration = if (trackerMask.rawAcceleration == true) tracker.acceleration.let { Vec3f(it.x, it.y, it.z) } else null,
		linearAcceleration = if (trackerMask.linearAcceleration == true) tracker.acceleration.let { Vec3f(it.x, it.y, it.z) } else null // FIXME: temp value
	)
}

private fun createDevice(
	device: DeviceState,
	trackers: List<TrackerState>,
	datafeedConfig: DataFeedConfig,
): DeviceData {
	val trackerMask = datafeedConfig.dataMask?.trackerData

	return DeviceData(
		id = DeviceId(device.id.toUByte()),
		hardwareStatus = HardwareStatus(
			batteryVoltage = device.batteryVoltage,
			batteryPctEstimate = (device.batteryLevel * 100).toUInt().toUByte(),
			ping = device.ping?.toUShort(),
		),
		hardwareInfo = HardwareInfo(
			mcuId = device.mcuType,
			manufacturer = "SlimeVR",
			boardType = device.boardType.toString(),
			officialBoardType = device.boardType,
			model = device.mcuType.toString(),
			firmwareVersion = device.firmware,
			ipAddress = Ipv4Address(ByteBuffer.wrap(device.address.toByteArray()).getLong().toUInt()),
		),
		trackers = if (trackerMask != null) {
			trackers.filter { it.deviceId == device.id }
				.map { tracker -> createTracker(device, tracker, trackerMask, datafeedConfig) }
		} else {
			null
		},
	)
}

private fun createBone(bone: BoneState): solarxr_protocol.data_feed.Bone = solarxr_protocol.data_feed.Bone(
	bodyPart = bone.bodyPart,
	rotationG = bone.rotation.let { Quat(it.x, it.y, it.z, it.w) },
	boneLength = bone.length,
	headPositionG = bone.headPosition.let { Vec3f(it.x, it.y, it.z) },
)

fun createDatafeedFrame(
	server: VRServer,
	datafeedConfig: DataFeedConfig,
	skeleton: Skeleton,
	index: Int = 0,
): DataFeedMessageHeader {
	val serverState = server.context.state.value
	val trackers = serverState.trackers.values.map { it.context.state.value }
	val devices = serverState.devices.values.map { it.context.state.value }
		.map { device -> createDevice(device, trackers, datafeedConfig) }
	val bones = if (datafeedConfig.boneMask) {
		skeleton.computed.value.values.map { createBone(it) }
	} else {
		null
	}
	return DataFeedMessageHeader(
		message = DataFeedUpdate(
			devices = if (datafeedConfig.dataMask?.deviceData != null) devices else null,
			bones = bones,
			index = index.toUByte(),
		),
	)
}

class DataFeedInitBehaviour(val server: VRServer, val skeleton: Skeleton) : SolarXRBridgeBehaviour {
	override fun reduce(state: SolarXRBridgeState, action: SolarXRBridgeActions) = when (action) {
		is SolarXRBridgeActions.SetConfig -> state.copy(
			dataFeedConfigs = action.configs,
			datafeedTimers = action.timers,
		)
	}

	override fun observe(receiver: SolarXRBridge) {
		receiver.dataFeedDispatcher.on<StartDataFeed> { event ->
			val datafeeds = event.dataFeeds ?: return@on

			receiver.context.state.value.datafeedTimers.forEach { it.cancelAndJoin() }

			val timers = datafeeds.mapIndexed { index, config ->
				receiver.context.scope.launch {
					val minTime = config.minimumTimeSinceLast.toLong()
					while (isActive) {
						receiver.sendDataFeed(createDatafeedFrame(server = server, skeleton = skeleton, datafeedConfig = config, index = index))
						delay(minTime)
					}
				}
			}

			receiver.context.dispatch(
				SolarXRBridgeActions.SetConfig(datafeeds, timers = timers),
			)
		}

		receiver.dataFeedDispatcher.on<PollDataFeed> { event ->
			val config = event.config ?: return@on
			receiver.sendDataFeed(createDatafeedFrame(server = server, datafeedConfig = config, skeleton = skeleton))
		}
	}
}
