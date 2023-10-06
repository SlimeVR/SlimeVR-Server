import io.eiren.util.logging.LogManager
import org.java_websocket.WebSocket
import org.java_websocket.drafts.Draft
import org.java_websocket.drafts.Draft_6455
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress
import java.nio.ByteBuffer

class OSCQueryWebsocket(port: Int, draft: Draft_6455) : WebSocketServer(InetSocketAddress(port), listOf<Draft>(draft)) {

	override fun onOpen(conn: WebSocket?, handshake: ClientHandshake) {
		val jsonTest = "{\"COMMAND\": \"LISTEN\",\"DATA\": \"/tracking/vrsystem\" }"

		LogManager.debug("test")
		conn?.send(jsonTest) // This method sends a message to the new client
		broadcast(jsonTest) // This method sends a message to all clients connected
		LogManager.debug(
			conn?.remoteSocketAddress?.address?.hostAddress + " entered the room!"
		)
	}

	override fun onClose(conn: WebSocket?, code: Int, reason: String, remote: Boolean) {
		broadcast("$conn has left the room!")
		LogManager.debug("$conn has left the room!")
	}

	override fun onMessage(conn: WebSocket?, message: String) {
		broadcast(message)
		LogManager.debug("$conn: $message")
	}

	override fun onMessage(conn: WebSocket?, message: ByteBuffer) {
		broadcast(message.array())
		LogManager.debug("$conn: $message")
	}

	override fun onError(conn: WebSocket?, ex: Exception) {
		ex.printStackTrace()
		if (conn != null) {
			// some errors like port binding failed may not be assignable to a specific websocket
		}
	}

	override fun onStart() {
		LogManager.debug("Server started!")
		connectionLostTimeout = 0
		connectionLostTimeout = 100
	}
}
