package dev.slimevr.solarxr

import dev.slimevr.VRServer
import dev.slimevr.context.Context
import dev.slimevr.context.CustomModule
import dev.slimevr.context.createContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import solarxr_protocol.data_feed.DataFeedConfig
import solarxr_protocol.data_feed.DataFeedMessage
import solarxr_protocol.rpc.RpcMessage
import kotlin.reflect.KClass

data class SolarXRConnectionState(
	val dataFeedConfigs: List<DataFeedConfig>,
	val datafeedTimers: List<Job>,
)

sealed interface SolarXRConnectionActions {
	data class SetConfig(val configs: List<DataFeedConfig>, val timers: List<Job>) :
		SolarXRConnectionActions
}

typealias SolarXRConnectionContext = Context<SolarXRConnectionState, SolarXRConnectionActions>
typealias SolarXRConnectionModule = CustomModule<SolarXRConnectionState, SolarXRConnectionActions, SolarXRConnection>


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
