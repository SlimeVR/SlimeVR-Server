package dev.slimevr.websocketapi;

import java.util.Map;

public interface ProtocolAPIServer {

	Map<Integer, GenericConnection> getAPIConnections();

}
