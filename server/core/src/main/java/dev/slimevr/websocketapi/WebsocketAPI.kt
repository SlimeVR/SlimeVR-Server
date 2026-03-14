package dev.slimevr.websocketapi

import dev.slimevr.VRServer
import dev.slimevr.protocol.GenericConnection
import dev.slimevr.protocol.ProtocolAPI
import dev.slimevr.protocol.ProtocolAPIServer
import io.eiren.util.logging.LogManager
import org.java_websocket.WebSocket
import org.java_websocket.drafts.Draft_6455
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.util.stream.Stream

open class WebsocketAPI(val server: VRServer, val protocolAPI: ProtocolAPI) :
	WebSocketServer(InetSocketAddress(21110), mutableListOf(Draft_6455())),
	ProtocolAPIServer {
	init {
		protocolAPI.registerAPIServer(this)
		isReuseAddr = true
	}

	override fun onOpen(conn: WebSocket, handshake: ClientHandshake) {
		LogManager.info("[WebSocketAPI] New connection from ${conn.remoteSocketAddress.address.hostAddress}")
		conn.setAttachment(WebsocketConnection(conn))
	}

	override fun onClose(conn: WebSocket, code: Int, reason: String, remote: Boolean) {
		LogManager.info("[WebSocketAPI] Disconnected: ${connAddr(conn)}, ($code) $reason. Remote: $remote")
	}

	override fun onMessage(conn: WebSocket, message: String) {
	}

	override fun onMessage(conn: WebSocket, message: ByteBuffer) {
		val connection = conn.getAttachment<WebsocketConnection?>() ?: return
		protocolAPI.onMessage(connection, message)
	}

	override fun onError(conn: WebSocket, ex: Exception) {
		LogManager
			.severe("[WebSocket] Exception on connection ${connAddr(conn)}", ex)
	}

	override fun onStart() {
		LogManager.info("[WebSocketAPI] Started on port ${getPort()}")
		connectionLostTimeout = 0
	}

	override val apiConnections: Stream<GenericConnection>
		get() = connections
			.stream()
			.map { conn: WebSocket ->
				conn.getAttachment<WebsocketConnection?>() as GenericConnection?
			}
			.filter { it != null }
			.map { it!! }

	companion object {
		/**
		 * Helper function to get the string of the `conn` while handling `null`
		 */
		protected fun connAddr(conn: WebSocket?): String {
			if (conn == null) return "null"

			val remote = conn.remoteSocketAddress ?: return conn.toString()
			return remote.address?.hostAddress ?: return remote.toString()
		}
	}
}
