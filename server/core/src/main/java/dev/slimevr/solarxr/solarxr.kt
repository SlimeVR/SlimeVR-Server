package dev.slimevr.solarxr

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.VRServer
import dev.slimevr.context.Context
import dev.slimevr.context.createContext
import io.ktor.util.moveToByteArray
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import solarxr_protocol.MessageBundle
import solarxr_protocol.data_feed.DataFeedConfig
import solarxr_protocol.data_feed.DataFeedMessage
import solarxr_protocol.data_feed.DataFeedMessageHeader
import solarxr_protocol.data_feed.DataFeedUpdate
import solarxr_protocol.data_feed.StartDataFeed
import solarxr_protocol.data_feed.device_data.DeviceData
import solarxr_protocol.data_feed.tracker.TrackerData
import solarxr_protocol.datatypes.DeviceId
import solarxr_protocol.datatypes.TrackerId
import solarxr_protocol.datatypes.hardware_info.HardwareStatus
import solarxr_protocol.rpc.RpcMessage
import kotlin.reflect.KClass
import kotlin.time.measureTime

data class SolarXRConnectionState(
	val dataFeedConfigs: List<DataFeedConfig>,
	val datafeedTimers: List<Job>,
)

sealed interface SolarXRConnectionActions {
	data class SetConfig(val configs: List<DataFeedConfig>, val timers: List<Job>) :
		SolarXRConnectionActions
}

typealias SolarXRConnectionContext = Context<SolarXRConnectionState, SolarXRConnectionActions>

class PacketDispatcher<T : Any> {
	val listeners = mutableMapOf<KClass<out T>, MutableList<suspend (T) -> Unit>>()
	val globalListeners = mutableListOf<suspend (T) -> Unit>()
	val mutex = Mutex()

	@Suppress("UNCHECKED_CAST")
	inline fun <reified P : T> on(crossinline callback: suspend (P) -> Unit) {
		runBlocking {
			mutex.withLock {
				val list =
					listeners.getOrPut(P::class as KClass<out T>) { mutableListOf() }
				list.add { callback(it as P) }
			}
		}
	}

	fun onAny(callback: suspend (T) -> Unit) {
		runBlocking {
			mutex.withLock { globalListeners.add(callback) }
		}
	}

	suspend fun emit(event: T) {
		val targets = mutex.withLock {
			val specific = listeners[event::class]?.toList() ?: emptyList()
			val global = globalListeners.toList()
			global + specific
		}
		targets.forEach { it(event) }
	}
}

data class SolarXRConnection(
	val context: SolarXRConnectionContext,
	val serverContext: VRServer,
	val dataFeedDispatcher: PacketDispatcher<DataFeedMessage>,
	val rpcDispatcher: PacketDispatcher<RpcMessage>,
	val send: suspend (ByteArray) -> Unit
)

data class SolarXRConnectionModule(
	val reducer: ((SolarXRConnectionState, SolarXRConnectionActions) -> SolarXRConnectionState)? = null,
	val observer: ((SolarXRConnection) -> Unit)? = null,
)


val DataFeedInitModule = SolarXRConnectionModule(
	observer = { context ->
		context.dataFeedDispatcher.on<StartDataFeed> { event ->
			val datafeeds = event.dataFeeds ?: return@on
			val state = context.context.state.value

			state.datafeedTimers.forEach {
				it.cancelAndJoin()
			}

			val timers = datafeeds.map { config ->
				val minTime = config.minimumTimeSinceLast ?: return@map null

				return@map context.context.scope.launch {
					val fbb = FlatBufferBuilder(1024)
					while (isActive) {
						val timeTaken = measureTime {
							fbb.clear()

							val serverState = context.serverContext.context.state.value
							val trackers =
								serverState.trackers.values.map { it.context.state.value }
							val devices =
								serverState.devices.values.map { it.context.state.value }
									.map { device ->
										DeviceData(
											id = DeviceId(device.id.toUByte()),
											hardwareStatus = HardwareStatus(
												batteryVoltage = device.batteryVoltage,
												batteryPctEstimate = device.batteryLevel.toUInt()
													.toUByte(),
												ping = device.ping?.toUShort()
											),
											trackers = trackers.filter { it.deviceId == device.id }
												.map { tracker ->
													TrackerData(
														trackerId = TrackerId(
															trackerNum = tracker.id.toUByte(),
															deviceId = DeviceId(device.id.toUByte())
														),
														status = tracker.status
													)
												}
										)
									}


							fbb.finish(
								MessageBundle(
									dataFeedMsgs = listOf(
										DataFeedMessageHeader(
											message = DataFeedUpdate(
												devices = devices
											)
										)
									)
								).encode(fbb)
							)

							context.send(fbb.dataBuffer().moveToByteArray())
						}
						val remainingDelay =
							(minTime.toLong() - timeTaken.inWholeMilliseconds).coerceAtLeast(
								0
							)
						delay(remainingDelay)
					}
				}
			}.filterNotNull()

			context.context.dispatch(
				SolarXRConnectionActions.SetConfig(
					datafeeds,
					timers = timers
				)
			)

			timers.forEach { it.start() }
		}
	}
)

fun createSolarXRConnection(
	serverContext: VRServer,
	onSend: suspend (ByteArray) -> Unit,
	scope: CoroutineScope
): SolarXRConnection {

	val state = SolarXRConnectionState(
		dataFeedConfigs = listOf(),
		datafeedTimers = listOf()
	)

	val modules = listOf(DataFeedInitModule)

	val context = createContext(
		initialState = state,
		reducers = modules.map { it.reducer },
		scope = scope,
	)

	val conn = SolarXRConnection(
		context = context,
		serverContext = serverContext,
		dataFeedDispatcher = PacketDispatcher(),
		rpcDispatcher = PacketDispatcher(),
		onSend,
	)

	modules.map { it.observer }.forEach { it?.invoke(conn) }

	return conn
}
