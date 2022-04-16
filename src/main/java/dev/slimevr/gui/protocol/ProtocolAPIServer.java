package dev.slimevr.gui.protocol;

import java.util.Map;

public interface ProtocolAPIServer {

	Map<Integer, GenericConnection> getAPIConnections();

}
