package dev.slimevr.websocketapi;

import dev.slimevr.protocol.ConnectionContext;
import dev.slimevr.protocol.GenericConnection;
import org.java_websocket.WebSocket;
import org.java_websocket.exceptions.WebsocketNotConnectedException;

import java.nio.ByteBuffer;
import java.util.UUID;


public class WebsocketConnection implements GenericConnection {

	public final ConnectionContext context;
	public final WebSocket conn;
	public UUID id;

	public WebsocketConnection(WebSocket conn) {
		this.context = new ConnectionContext();
		this.conn = conn;
		this.id = UUID.randomUUID();
	}

	@Override
	public ConnectionContext getContext() {
		return this.context;
	}

	@Override
	public void send(ByteBuffer bytes) {
		if (this.conn.isOpen()) {
			try {
				this.conn.send(bytes.slice());
			} catch (WebsocketNotConnectedException ignored) {
				// Race condition if it closes between our check and sending
			}
		}
	}

	@Override
	public UUID getConnectionId() {
		return id;
	}
}
