package dev.slimevr.tracker.udp

import dev.slimevr.context.Context
import dev.slimevr.context.createContext
import io.ktor.network.sockets.BoundDatagramSocket
import io.ktor.network.sockets.Datagram
import io.ktor.network.sockets.InetSocketAddress
import io.ktor.utils.io.core.buildPacket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class LastPing(
	val id: Int,
	val startTime: Long,
	val duration: Long,
)

data class UDPConnectionState(
	val id: String,
	val lastPacket: Long,
	val lastPacketNum: Int,
	val lastPing: LastPing,
	val didHandshake: Boolean,
	val address: String,
	val port: Int,
)

sealed interface UDPConnectionActions {
	data class StartPing(val startTime: Long): UDPConnectionActions
	data class ReceivedPong(val id: Int, val duration: Long): UDPConnectionActions
}

typealias UDPConnectionContext = Context<UDPConnectionState, UDPConnectionActions>

data class UDPConnection(
	val context: UDPConnectionContext,
	val packetEvents: PacketDispatcher,
	val send: (Packet) -> Unit
)

data class UDPConnectionModule(
	val reducer: (UDPConnectionState, UDPConnectionActions) -> UDPConnectionState,
	val observer: ((UDPConnection) -> Unit)? = null,
)

val PingModule = UDPConnectionModule(
	reducer = { s, a ->
		when (a) {
			is UDPConnectionActions.StartPing -> {
				s.copy(lastPing = s.lastPing.copy(startTime = a.startTime))
			}
			is UDPConnectionActions.ReceivedPong -> {
				s.copy(lastPing = s.lastPing.copy(duration = a.duration, id = a.id))
			}
			else -> s
		}
	},
	observer = {
		// Send the ping every 1s
		it.context.scope.launch {
			while (isActive) {
				val state = it.context.state.value
				if (state.didHandshake) {
					it.context.dispatch(UDPConnectionActions.StartPing(startTime = System.currentTimeMillis()))
					it.send(PingPong(state.lastPacketNum + 1))
				}
				delay(1000)
			}
		}

		// listen for the pong
		it.packetEvents.on<PingPong> { packet ->
			val state = it.context.state.value
			if (packet.pingId != state.lastPing.id + 1) {
				println("Ping ID does not match, ignoring")
				return@on
			}

			val ping = System.currentTimeMillis() - state.lastPing.startTime;

			it.context.scope.launch {
				it.context.dispatch(UDPConnectionActions.ReceivedPong(id = packet.pingId, duration = ping))

				// TODO update the device ping delay
			}
		}
	}
)



fun createUDPConnectionContext(
	id: String,
	socket: BoundDatagramSocket,
	remoteAddress: InetSocketAddress,
	scope: CoroutineScope
): UDPConnection {

	val modules = listOf(PingModule)

	val context = createContext(
		initialState = UDPConnectionState(
			id = id,
			lastPacket = System.currentTimeMillis(),
			lastPacketNum = 0,
			lastPing = LastPing(id = 0, duration = 0, startTime = 0),
			didHandshake = false,
			address = remoteAddress.hostname,
			port = remoteAddress.port,
		),
		reducers = modules.map { it.reducer },
		scope = scope
	)

	val dispatcher = PacketDispatcher()

	val sendFunc = { packet: Packet ->
		scope.launch {
			val bytePacket = buildPacket {
				writePacket(this, packet)
			}
			socket.send(Datagram(bytePacket, remoteAddress))
		}
		Unit
	}

	val conn = UDPConnection(
		context,
		dispatcher,
		send = sendFunc
	)

	modules.map { it.observer }.forEach { it?.invoke(conn) }

	return conn
}
