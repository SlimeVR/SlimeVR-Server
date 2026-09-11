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

class OscSender(private val address: String, private val port: Int) {
	private var socket: ConnectedDatagramSocket? = null

	private suspend fun socket(): ConnectedDatagramSocket {
		val s = socket ?: aSocket(selectorManager).udp().connect(InetSocketAddress(address, port)).also { socket = it }
		return s
	}

	suspend fun send(message: OscMessage) {
		val buf = Buffer()
		writeMessage(buf, message)
		socket().send(Datagram(buf, InetSocketAddress(address, port)))
	}

	suspend fun send(bundle: OscBundle) {
		val buf = Buffer()
		writeBundle(buf, bundle)
		socket().send(Datagram(buf, InetSocketAddress(address, port)))
	}

	fun close() {
		socket?.close()
		socket = null
	}
}

class OscReceiver(private val port: Int) {
	private var socket: BoundDatagramSocket? = null
	private var running = false

	private suspend fun socket(): BoundDatagramSocket {
		val s = socket ?: aSocket(selectorManager).udp().bind(InetSocketAddress("0.0.0.0", port)).also { socket = it }
		return s
	}

	suspend fun listenBundles(onBundle: (OscBundle) -> Unit) {
		val s = socket()
		running = true
		while (running) {
			val packet = try {
				s.receive().packet
			} catch (e: Exception) {
				if (!running) break
				throw e
			}
			val bundle = try {
				readBundle(packet)
			} catch (_: IllegalArgumentException) {
				try {
					OscBundle(1, listOf(OscContent.Message(readMessage(packet))))
				} catch (_: Exception) {
					continue
				}
			}
			onBundle(bundle)
		}
	}

	fun close() {
		running = false
		socket?.close()
		socket = null
	}
}
