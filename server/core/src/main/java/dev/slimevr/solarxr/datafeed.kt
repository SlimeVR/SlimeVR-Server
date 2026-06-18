package dev.slimevr.solarxr

import dev.slimevr.AppLogger
import dev.slimevr.VRServer
import dev.slimevr.device.DeviceState
import dev.slimevr.resets.ResetsManager
import dev.slimevr.skeleton.BoneState
import dev.slimevr.skeleton.Skeleton
import dev.slimevr.tracker.TrackerState
import dev.slimevr.util.safeLaunch
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import solarxr_protocol.data_feed.DataFeedConfig
import solarxr_protocol.data_feed.DataFeedMessageHeader
import solarxr_protocol.data_feed.DataFeedUpdate
import solarxr_protocol.data_feed.PollDataFeed
import solarxr_protocol.data_feed.StartDataFeed
import solarxr_protocol.data_feed.device_data.DeviceData
import solarxr_protocol.data_feed.server.ServerGuards
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

private fun ipv4AddressFromString(address: String): UInt {
	val parts = address.split('.')
	if (parts.size != 4) return 0u
	return parts.fold(0u) { acc, part ->
		val value = part.toUIntOrNull()?.takeIf { it <= 255u } ?: return 0u
		(acc shl 8) or value
	}
}

private fun createTracker(device: DeviceState, tracker: TrackerState, trackerMask: TrackerDataMask, datafeedConfig: DataFeedConfig): TrackerData = TrackerData(
	trackerId = TrackerId(
		trackerNum = tracker.id.toUByte(),
		deviceId = DeviceId(device.id.toUByte()),
	),
	status = if (trackerMask.status == true) tracker.status else null,
	rotation = if (trackerMask.rotation == true) tracker.rawRotation.let { Quat(it.x, it.y, it.z, it.w) } else null,
	position = if (trackerMask.position == true && tracker.position != null) tracker.position.let { Vec3f(it.x, it.y, it.z) } else null,
	info = if (trackerMask.info == true) {
		TrackerInfo(
			imuType = tracker.sensorType,
			bodyPart = tracker.bodyPart,
			displayName = tracker.name,
			customName = tracker.customName,
			mountingOrientation = tracker.mountingOrientation?.let { Quat(it.x, it.y, it.z, it.w) },
			isImu = tracker.sensorType != null,
			magnetometer = tracker.magStatus,
		)
	} else {
		null
	},
	tps = if (trackerMask.tps == true) tracker.tps else null,
	temp = if (trackerMask.temp == true && tracker.imuTemp != null) Temperature(temp = tracker.imuTemp) else null,
	rawAcceleration = if (trackerMask.rawAcceleration == true) tracker.rawAcceleration.let { Vec3f(it.x, it.y, it.z) } else null,
	linearAcceleration = if (trackerMask.linearAcceleration == true) tracker.acceleration.let { Vec3f(it.x, it.y, it.z) } else null,
	rotationReferenceAdjusted = if (trackerMask.rotationReferenceAdjusted == true) tracker.rotation.let { Quat(it.x, it.y, it.z, it.w) } else null,
	rotationIdentityAdjusted = if (trackerMask.rotationIdentityAdjusted == true) tracker.rotation.let { Quat(it.x, it.y, it.z, it.w) } else null, // FIXME: uses reference adjusted
)

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
			batteryPctEstimate = device.batteryLevel?.let { (it * 100).toUInt().toUByte() },
			batteryRuntimeEstimate = device.batteryRemainingRuntime,
			ping = device.ping?.toUShort(),
			rssi = device.signalStrength?.toShort(),
			packetsReceived = device.packetsReceived.toInt(),
			packetsLost = device.packetsLost.toInt(),
			packetLoss = if (device.packetsReceived > 0) device.packetsLost.toFloat() / device.packetsReceived.toFloat() else null,
		),
		hardwareInfo = HardwareInfo(
			mcuId = device.mcuType,
			manufacturer = device.manufacturer,
			boardType = device.boardType.toString(),
			officialBoardType = device.boardType,
			model = device.mcuType.toString(),
			firmwareVersion = device.firmware,
			ipAddress = Ipv4Address(ipv4AddressFromString(device.address)),
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
	boneLength = bone.offset.len(),
	headPositionG = bone.headPosition.let { Vec3f(it.x, it.y, it.z) },
)

private fun createServerGuards(resetsManager: ResetsManager): ServerGuards {
	val resetsState = resetsManager.context.state.value
	return ServerGuards(
		candomounting = resetsState.canDoMountingReset,
		candoyawreset = resetsState.canDoYawReset,
		//candouserheightcalibration = true, TODO
	)
}

fun createDatafeedFrame(
	server: VRServer,
	datafeedConfig: DataFeedConfig,
	skeleton: Skeleton,
	resetsManager: ResetsManager,
	index: Int = 0,
): DataFeedMessageHeader {
	val serverState = server.context.state.value
	val trackers = serverState.trackers.values.map { it.context.state.value }
	val devices = serverState.devices.values.map { it.context.state.value }
		.map { device -> createDevice(device, trackers, datafeedConfig) }
	val bones = if (datafeedConfig.boneMask == true) {
		skeleton.computed.value.values.map { createBone(it) }
	} else {
		null
	}
	val serverGuards = if (datafeedConfig.serverGuardsMask == true) {
		createServerGuards(resetsManager)
	} else {
		null
	}
	return DataFeedMessageHeader(
		message = DataFeedUpdate(
			devices = if (datafeedConfig.dataMask?.deviceData != null) devices else null,
			bones = bones,
			serverGuards = serverGuards,
			index = index.toUByte(),
		),
	)
}

class DataFeedInitBehaviour(val server: VRServer, val skeleton: Skeleton) : SolarXRBridgeBehaviour {
	override fun reduce(state: SolarXRBridgeState, action: SolarXRBridgeActions) = when (action) {
		is SolarXRBridgeActions.SetConfig -> state.copy(dataFeedConfigs = action.configs)
	}

	override fun observe(receiver: SolarXRBridge) {
		receiver.dataFeedDispatcher.on<StartDataFeed> { event ->
			val datafeeds = event.dataFeeds ?: return@on

			receiver.datafeedTimers.forEach { it.cancelAndJoin() }

			val timers = datafeeds.mapIndexed { index, config ->
				receiver.context.scope.safeLaunch {
					val minTime = config.minimumTimeSinceLast.toLong()
					while (isActive) {
						try {
							receiver.sendDataFeed(createDatafeedFrame(server = server, datafeedConfig = config, skeleton = skeleton, resetsManager = receiver.appContext.resetsManager, index = index))
						} catch (e: Exception) {
							AppLogger.solarxr.error(e, "Error sending data feed")
						}
						delay(minTime)
					}
				}
			}

			receiver.datafeedTimers = timers
			receiver.context.dispatch(SolarXRBridgeActions.SetConfig(datafeeds))
		}

		receiver.dataFeedDispatcher.on<PollDataFeed> { event ->
			val config = event.config ?: return@on
			receiver.sendDataFeed(createDatafeedFrame(server = server, datafeedConfig = config, skeleton = skeleton, resetsManager = receiver.appContext.resetsManager))
		}
	}
}
