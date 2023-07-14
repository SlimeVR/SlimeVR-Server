package dev.slimevr.protocol;

import java.nio.ByteBuffer;
import java.util.UUID;


public interface GenericConnection {

	UUID getConnectionId();

	ConnectionContext getContext();

	void send(ByteBuffer bytes);
}
