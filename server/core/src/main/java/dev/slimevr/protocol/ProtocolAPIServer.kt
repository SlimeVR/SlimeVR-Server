package dev.slimevr.protocol

import java.util.stream.Stream

interface ProtocolAPIServer {
	val apiConnections: Stream<GenericConnection>
}
