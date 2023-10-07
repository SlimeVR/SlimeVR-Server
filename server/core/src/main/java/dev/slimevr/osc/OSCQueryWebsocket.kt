import io.eiren.util.logging.LogManager
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress
import java.nio.ByteBuffer

class OSCQueryWebsocket(address: InetSocketAddress, val message: String) : WebSocketServer(address) {

	override fun onStart() {
		LogManager.debug("server started successfully")
	}

	override fun onOpen(conn: WebSocket, handshake: ClientHandshake) {
		LogManager.debug("new connection to " + conn.remoteSocketAddress)
		conn.send(message) // This method sends a message to the new client
	}

	override fun onClose(conn: WebSocket, code: Int, reason: String, remote: Boolean) {
		LogManager.debug("closed " + conn.remoteSocketAddress + " with exit code " + code + " additional info: " + reason)
	}

	override fun onError(conn: WebSocket, ex: Exception) {
		LogManager.warning("an error occurred on connection " + conn.remoteSocketAddress + ":" + ex)
	}

	override fun onMessage(conn: WebSocket, message: String) { }

	override fun onMessage(conn: WebSocket, message: ByteBuffer) { }
}
