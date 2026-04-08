package dev.slimevr.websocketapi

import dev.slimevr.protocol.ConnectionContext
import dev.slimevr.protocol.GenericConnection
import org.java_websocket.WebSocket
import org.java_websocket.exceptions.WebsocketNotConnectedException
import java.nio.ByteBuffer
import java.util.*

class WebsocketConnection(val conn: WebSocket) : GenericConnection {
	override val context = ConnectionContext()
	override val connectionId: UUID = UUID.randomUUID()

	override fun send(bytes: ByteBuffer) {
		if (this.conn.isOpen) {
			try {
				this.conn.send(bytes.slice())
			} catch (ignored: WebsocketNotConnectedException) {
				// Race condition if it closes between our check and sending
			}
		}
	}
}
