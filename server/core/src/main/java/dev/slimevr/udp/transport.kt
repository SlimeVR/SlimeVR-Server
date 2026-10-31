package dev.slimevr.udp

import kotlinx.io.Source

data class UdpPeer(
	val address: String,
	val host: String,
	val port: Int,
)

interface UdpSocket {
	/**
	 * Calls [onDatagram] for every datagram, on the socket's own receive thread, until cancelled or
	 * the socket closes. [onDatagram] must not block, as the next datagram waits for it.
	 */
	suspend fun listen(onDatagram: (from: UdpPeer, payload: Source) -> Unit)

	suspend fun send(to: UdpPeer, payload: ByteArray)

	suspend fun close()
}

/** Binds the tracker port, provided by each platform. Throws when the port can't be bound */
typealias UdpBind = suspend (port: Int) -> UdpSocket
