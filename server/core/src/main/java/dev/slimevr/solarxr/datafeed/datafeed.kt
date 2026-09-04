package dev.slimevr.solarxr.datafeed

import dev.slimevr.VRServer
import dev.slimevr.device.Device
import dev.slimevr.device.DeviceState
import dev.slimevr.heightcalibration.HeightCalibrationManager
import dev.slimevr.hid.HIDReceiverState
import dev.slimevr.logging.AppLogger
import dev.slimevr.resets.ResetsManager
import dev.slimevr.skeleton.Skeleton
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeActions
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import dev.slimevr.solarxr.createBone
import dev.slimevr.tracker.Motion
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
import solarxr_protocol.data_feed.dongle_data.DongleData
import solarxr_protocol.data_feed.dongle_data.DongleDataMask
import solarxr_protocol.data_feed.dongle_data.DongleStatus
import solarxr_protocol.data_feed.server.ServerGuards
import solarxr_protocol.data_feed.tracker_data.StayAlignedTracker
import solarxr_protocol.data_feed.tracker_data.TrackerData
import solarxr_protocol.data_feed.tracker_data.TrackerDataMask
import solarxr_protocol.data_feed.tracker_data.TrackerInfo
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.MagnetometerStatus
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.datatypes.hardware_info.HardwareInfo
import solarxr_protocol.datatypes.hardware_info.HardwareStatus
import solarxr_protocol.datatypes.hardware_info.ImuType
import solarxr_protocol.datatypes.math.Quat
import solarxr_protocol.datatypes.math.Vec3f
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource

private fun ipv4AddressFromString(address: String): UInt {
	val parts = address.split('.')
	if (parts.size != 4) return 0u
	return parts.fold(0u) { acc, part ->
		val value = part.toUIntOrNull()?.takeIf { it <= 255u } ?: return 0u
		(acc shl 8) or value
	}
}

private fun createTracker(device: DeviceState, tracker: TrackerState, trackerMask: TrackerDataMask): TrackerData = TrackerData(
	deviceId = device.id.toUShort(),
	trackerId = tracker.id.toUShort(),
	status = if (trackerMask.status) tracker.status else TrackerStatus.NONE,
	rotation = if (trackerMask.rotation) tracker.rawRotation.let { Quat(it.x, it.y, it.z, it.w) } else null,
	position = if (trackerMask.position && tracker.position != null) tracker.position.let { Vec3f(it.x, it.y, it.z) } else null,
	info = if (trackerMask.info) {
		TrackerInfo(
			isImu = tracker.imuType != null,
			imuType = tracker.imuType ?: ImuType.UNKNOWN,
			bodyPart = tracker.bodyPart ?: BodyPart.NONE,
			mountingOrientation = tracker.mountingOrientation.let { Quat(it.x, it.y, it.z, it.w) },
			displayName = tracker.name,
			customName = tracker.customName,
			lastMountingMethod = tracker.lastMountingMethod,
			magnetometer = tracker.magStatus,
			dataType = tracker.trackerDataType,
		)
	} else {
		null
	},
	tps = if (trackerMask.tps) tracker.tps else null,
	temp = if (trackerMask.temp && tracker.imuTemp != null) tracker.imuTemp else null,
	rawAcceleration = if (trackerMask.rawAcceleration) tracker.rawAcceleration.let { Vec3f(it.x, it.y, it.z) } else null,
	linearAcceleration = if (trackerMask.linearAcceleration) tracker.acceleration.let { Vec3f(it.x, it.y, it.z) } else null,
	rotationReferenceAdjusted = if (trackerMask.rotationReferenceAdjusted) tracker.rotation.let { Quat(it.x, it.y, it.z, it.w) } else null,
	rotationIdentityAdjusted = if (trackerMask.rotationIdentityAdjusted) tracker.rotation.let { Quat(it.x, it.y, it.z, it.w) } else null, // FIXME: uses reference adjusted
	rawMagneticVector = if (trackerMask.rawMagneticVector && tracker.magStatus == MagnetometerStatus.ENABLED) tracker.rawMagnetometer.let { Vec3f(it.x, it.y, it.z) } else null,
	stayAligned = if (trackerMask.stayAligned) StayAlignedTracker(tracker.stayAlignedData.yawCorrection.toDeg(), tracker.motion == Motion.RESTING) else null,
	origin = tracker.origin,
)

private const val DEVICE_STATS_WINDOW_MS = 5_000L

private fun createDevice(
	device: Device,
	trackers: List<TrackerState>,
	datafeedConfig: DataFeedConfig,
): DeviceData {
	val deviceState = device.context.state.value
	val stats = device.getStatsForWindow(DEVICE_STATS_WINDOW_MS)
	val trackerMask = datafeedConfig.dataMask?.trackerData

	return DeviceData(
		id = deviceState.id.toUShort(),
		hardwareStatus = HardwareStatus(
			batteryVoltage = deviceState.batteryVoltage,
			batteryPctEstimate = deviceState.batteryLevel?.let { (it * 100).toUInt().toUByte() },
			batteryRuntimeEstimate = deviceState.batteryRemainingRuntime,
			ping = deviceState.ping?.toUShort(),
			rssi = stats.rssiAvg?.toShort(),
			rssiMin = stats.rssiMin?.toShort(),
			rssiMax = stats.rssiMax?.toShort(),
			packetsReceived = stats.packetsReceived,
			packetsLost = stats.packetsLost,
			packetLoss = stats.packetLoss,
			// TODO missing fields
		),
		hardwareInfo = HardwareInfo(
			mcuId = deviceState.mcuType,
			manufacturer = deviceState.manufacturer,
			boardType = deviceState.boardType.toString(),
			officialBoardType = deviceState.boardType,
			model = deviceState.mcuType.toString(),
			firmwareVersion = deviceState.firmwareVersion,
			firmwareDate = deviceState.firmwareDate,
			ipAddress = ipv4AddressFromString(deviceState.address),
			hardwareIdentifier = deviceState.macAddress,
			networkProtocolVersion = deviceState.protocolVersion.toUShort(),
			// TODO missing fields
		),
		trackers = if (trackerMask != null) {
			trackers.filter { it.deviceId == deviceState.id }
				.map { tracker -> createTracker(deviceState, tracker, trackerMask) }
		} else {
			null
		},
		origin = deviceState.origin,
	)
}

private fun createServerGuards(resetsManager: ResetsManager, heightCalibrationManager: HeightCalibrationManager): ServerGuards {
	val resetsState = resetsManager.context.state.value
	val heightCalibrationState = heightCalibrationManager.context.state.value
	return ServerGuards(
		canDoMountingReset = resetsState.canDoMountingReset,
		canDoYawReset = resetsState.canDoYawReset,
		canDoUserHeightCalibration = heightCalibrationState.canDoUserHeightCalibration,
	)
}

private fun createDongle(dongle: HIDReceiverState, mask: DongleDataMask): DongleData = DongleData(
	id = dongle.id.toUShort(),
	displayName = dongle.displayName.takeIf { mask.displayName },
	customName = dongle.customName.takeIf { mask.customName },
	hardwareRevision = dongle.hardwareRevision.takeIf { mask.hardwareRevision },
	hardwareAddress = null, // FIXME: send me
	model = dongle.model.takeIf { mask.model },
	manufacturer = dongle.manufacturer.takeIf { mask.manufacturer },
	firmwareVersion = dongle.firmwareVersion.takeIf { mask.firmwareVersion },
	firmwareDate = dongle.firmwareDate.takeIf { mask.firmwareDate },
	boardType = dongle.boardType.takeIf { mask.boardType },
	devicesIds = dongle.trackers.values.map { it.deviceId.toUShort() }.distinct().takeIf { mask.devicesIds },
	status = dongle.status.takeIf { mask.status } ?: DongleStatus.NONE,
	protocolVersion = dongle.protocolVersion?.toUShort().takeIf { mask.protocolVersion },
)

fun createDatafeedFrame(
	server: VRServer,
	datafeedConfig: DataFeedConfig,
	skeleton: Skeleton,
	resetsManager: ResetsManager,
	heightCalibrationManager: HeightCalibrationManager,
	index: Int = 0,
): DataFeedMessageHeader {
	val serverState = server.context.state.value
	val trackers = serverState.trackers.values.map { it.context.state.value }
	val devices = if (datafeedConfig.dataMask?.deviceData != null) {
		serverState.devices.values.map { device -> createDevice(device, trackers, datafeedConfig) }
	} else {
		null
	}
	val bones = datafeedConfig.boneMask?.let { mask ->
		skeleton.currentComputed.values.map { createBone(it, mask) }
	}
	val serverGuards = if (datafeedConfig.serverGuardsMask) {
		createServerGuards(resetsManager, heightCalibrationManager)
	} else {
		null
	}

	val dongles = datafeedConfig.dongleMask?.let { mask ->
		serverState.dongles.values.map { it.context.state.value }.map { createDongle(it, mask) }
	}
	return DataFeedMessageHeader(
		message = DataFeedUpdate(
			devices = devices,
			bones = bones,
			serverGuards = serverGuards,
			dongles = dongles,
			index = index.toUByte(),
		),
	)
}

class DataFeedInitBehaviour(
	val server: VRServer,
	val skeleton: Skeleton,
	private val timeSource: TimeSource.WithComparableMarks,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		receiver.dataFeedDispatcher.on<StartDataFeed> { event ->
			val dataFeeds = event.dataFeeds ?: return@on

			receiver.datafeedTimers.forEach { it.cancelAndJoin() }

			val timers = dataFeeds.mapIndexed { index, config ->
				receiver.context.scope.launch {
					val interval = config.minimumTimeSinceLast.toLong().milliseconds
					var nextSend = timeSource.markNow()

					while (isActive) {
						try {
							receiver.sendDataFeed(
								createDatafeedFrame(
									server = server,
									datafeedConfig = config,
									skeleton = skeleton,
									resetsManager = receiver.appContext.resetsManager,
									heightCalibrationManager = receiver.appContext.heightCalibrationManager,
									index = index,
								),
							)
						} catch (e: Exception) {
							AppLogger.solarxr.error(e, "Error sending data feed")
						}

						// Sleeping to an absolute deadline takes the send's own duration out of the
						// gap instead of adding it on top
						nextSend += interval
						val remaining = -nextSend.elapsedNow()
						if (remaining > Duration.ZERO) {
							delay(remaining)
						} else {
							// A send that outran the interval gives up the slots it missed rather
							// than firing back to back to catch up
							nextSend = timeSource.markNow()
						}
					}
				}
			}

			receiver.datafeedTimers = timers
			receiver.context.dispatch(SolarXRBridgeActions.SetConfig(dataFeeds))
		}.launchIn(receiver.context.scope)

		receiver.dataFeedDispatcher.on<PollDataFeed> { event ->
			val config = event.config ?: return@on
			receiver.sendDataFeed(
				createDatafeedFrame(
					server = server,
					datafeedConfig = config,
					skeleton = skeleton,
					resetsManager = receiver.appContext.resetsManager,
					heightCalibrationManager = receiver.appContext.heightCalibrationManager,
				),
			)
		}.launchIn(receiver.context.scope)
	}
}
