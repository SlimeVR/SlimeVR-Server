package dev.slimevr.protocol;

import java.util.stream.Stream;


public interface ProtocolAPIServer {

	Stream<GenericConnection> getAPIConnections();
}
