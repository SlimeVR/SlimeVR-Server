package dev.slimevr.osc

import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.BoundDatagramSocket
import io.ktor.network.sockets.ConnectedDatagramSocket
import io.ktor.network.sockets.Datagram
import io.ktor.network.sockets.InetSocketAddress
import io.ktor.network.sockets.aSocket
import kotlinx.coroutines.Dispatchers
import kotlinx.io.Buffer
import kotlinx.io.readByteArray

private val selectorManager = SelectorManager(Dispatchers.IO)

private fun ByteArray.toBuffer() = Buffer().apply { write(this@toBuffer) }

class OscSender(private val address: String, private val port: Int) {
	private var socket: ConnectedDatagramSocket? = null

	private suspend fun socket(): ConnectedDatagramSocket {
		if (socket == null) socket = aSocket(selectorManager).udp().connect(InetSocketAddress(address, port))
		return socket!!
	}

	suspend fun send(message: OscMessage) = socket().send(Datagram(encodeMessage(message).toBuffer(), InetSocketAddress(address, port)))
	suspend fun send(bundle: OscBundle) = socket().send(Datagram(encodeBundle(bundle).toBuffer(), InetSocketAddress(address, port)))

	fun close() {
		socket?.close()
		socket = null
	}
}

class OscReceiver(private val port: Int) {
	private var socket: BoundDatagramSocket? = null
	private var running = false

	private suspend fun socket(): BoundDatagramSocket {
		if (socket == null) socket = aSocket(selectorManager).udp().bind(InetSocketAddress("0.0.0.0", port))
		return socket!!
	}

	suspend fun listenBundles(onBundle: (OscBundle) -> Unit) {
		val s = socket()
		running = true
		while (running) {
			try {
				val bytes = s.receive().packet.readByteArray()
				val bundle = try {
					decodeBundle(bytes).first
				} catch (_: IllegalArgumentException) {
					OscBundle(1, listOf(OscContent.Message(decodeMessage(bytes).first)))
				}
				onBundle(bundle)
			} catch (_: Exception) {
				if (!running) break
			}
		}
	}

	fun close() {
		running = false
		socket?.close()
		socket = null
	}
}
