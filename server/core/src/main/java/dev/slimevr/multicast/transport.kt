package dev.slimevr.multicast

interface MulticastSocket {
	/** Calls [onPacket] for every datagram received on the group, until the socket closes. */
	suspend fun listen(onPacket: (ByteArray) -> Unit)

	/** Sends [payload] to the group. */
	suspend fun send(payload: ByteArray)

	suspend fun close()
}

/**
 * Opens an IPv4 socket on [port], shared with other processes bound to it, joined to the [group]
 * multicast address on the interface owning [interfaceAddress]. Provided by each platform.
 */
typealias MulticastBind = suspend (group: ByteArray, port: Int, interfaceAddress: String) -> MulticastSocket
