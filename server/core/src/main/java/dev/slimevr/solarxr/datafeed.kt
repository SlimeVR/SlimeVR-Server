package dev.slimevr.solarxr

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.VRServer
import dev.slimevr.tracker.DeviceState
import dev.slimevr.tracker.TrackerState
import io.ktor.util.moveToByteArray
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import solarxr_protocol.MessageBundle
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
import solarxr_protocol.datatypes.TrackerId
import solarxr_protocol.datatypes.hardware_info.HardwareStatus
import solarxr_protocol.datatypes.math.Quat

private fun createTracker(device: DeviceState, tracker: TrackerState, trackerMask: TrackerDataMask): TrackerData = TrackerData(
	trackerId = TrackerId(
		trackerNum = tracker.id.toUByte(),
		deviceId = DeviceId(device.id.toUByte()),
	),
	status = if (trackerMask.status == true) tracker.status else null,
	rotation = if (trackerMask.rotation == true) tracker.rawRotation.let { Quat(it.x, it.y, it.z, it.w) } else null,
	info = if (trackerMask.info == true) {
		TrackerInfo(
			imuType = tracker.sensorType,
			bodyPart = tracker.bodyPart,
			displayName = tracker.name,
		)
	} else {
		null
	},
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
			batteryPctEstimate = device.batteryLevel.toUInt()
				.toUByte(),
			ping = device.ping?.toUShort(),
		),
		trackers = if (trackerMask != null) {
			trackers.filter { it.deviceId == device.id }
				.map { tracker -> createTracker(device, tracker, trackerMask) }
		} else {
			null
		},
	)
}

fun createDatafeedFrame(
	serverContext: VRServer,
	datafeedConfig: DataFeedConfig,
	index: Int = 0,
): DataFeedMessageHeader {
	val serverState = serverContext.context.state.value
	val trackers =
		serverState.trackers.values.map { it.context.state.value }
	val devices =
		serverState.devices.values.map { it.context.state.value }
			.map { device -> createDevice(device, trackers, datafeedConfig) }
	return DataFeedMessageHeader(
		message = DataFeedUpdate(
			devices = if (datafeedConfig.dataMask?.deviceData != null) devices else null,
			index = index.toUByte(),
		),
	)
}

val DataFeedInitBehaviour = SolarXRConnectionBehaviour(
	reducer = { s, a ->
		when (a) {
			is SolarXRConnectionActions.SetConfig -> s.copy(
				dataFeedConfigs = a.configs,
				datafeedTimers = a.timers,
			)
		}
	},
	observer = { context ->
		context.dataFeedDispatcher.on<StartDataFeed> { event ->
			val datafeeds = event.dataFeeds ?: return@on

			context.context.state.value.datafeedTimers.forEach { it.cancelAndJoin() }

			val timers = datafeeds.mapIndexed { index, config ->
				context.context.scope.launch {
					val fbb = FlatBufferBuilder(1024)
					val minTime = config.minimumTimeSinceLast.toLong()
					while (isActive) {
						fbb.clear()
						fbb.finish(
							MessageBundle(
								dataFeedMsgs = listOf(
									createDatafeedFrame(context.serverContext, config, index),
								),
							).encode(fbb),
						)
						context.send(fbb.dataBuffer().moveToByteArray())
						delay(minTime)
					}
				}
			}

			context.context.dispatch(
				SolarXRConnectionActions.SetConfig(
					datafeeds,
					timers = timers,
				),
			)
		}

		context.dataFeedDispatcher.on<PollDataFeed> { event ->
			val config = event.config ?: return@on

			val fbb = FlatBufferBuilder(1024)
			fbb.finish(
				MessageBundle(
					dataFeedMsgs = listOf(
						createDatafeedFrame(serverContext = context.serverContext, datafeedConfig = config),
					),
				).encode(fbb),
			)
			context.send(fbb.dataBuffer().moveToByteArray())
		}
	},
)
