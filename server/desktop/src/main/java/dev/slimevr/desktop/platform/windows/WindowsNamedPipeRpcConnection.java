package dev.slimevr.desktop.platform.windows;

import dev.slimevr.protocol.ConnectionContext;
import dev.slimevr.protocol.GenericConnection;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;


public class WindowsNamedPipeRpcConnection implements GenericConnection {
	private final UUID id;
	private final ConnectionContext context;
	private final WindowsNamedPipe.PipeConnection connection;

	WindowsNamedPipeRpcConnection(WindowsNamedPipe.PipeConnection connection) {
		this.id = UUID.randomUUID();
		this.context = new ConnectionContext();
		this.connection = connection;
	}

	@Override
	public UUID getConnectionId() {
		return id;
	}

	@Override
	public ConnectionContext getContext() {
		return this.context;
	}

	public boolean update(WindowsNamedPipe.PipeMessageReader reader) {
		return this.connection.update(reader);
	}

	public void disconnect() {
		this.connection.disconnect();
	}

	public PipeState getState() {
		return this.connection.getState();
	}

	public WindowsNamedPipe.PipeConnection getConnection() {
		return this.connection;
	}

	@Override
	public void send(ByteBuffer bytes) {
		if (this.connection.getState() != PipeState.OPEN)
			return;

		ByteBuffer src = ByteBuffer.allocate(4 + bytes.remaining());
		src.order(ByteOrder.LITTLE_ENDIAN);
		src.putInt(4 + bytes.remaining());
		src.put(bytes);
		src.flip();
		synchronized (this) {
			this.connection.sendBuffer(bytes.array(), bytes.remaining());
		}
	}
}
