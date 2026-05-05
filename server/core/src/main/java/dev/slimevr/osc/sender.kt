package dev.slimevr.osc

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class OscSender(private val address: String, private val port: Int) {
	private var socket: DatagramSocket? = null

	private suspend fun ensureSocket() {
		if (socket == null) {
			socket = withContext(Dispatchers.IO) {
				DatagramSocket()
			}
		}
	}

	suspend fun send(message: OscMessage) {
		val bytes = encodeMessage(message)
		ensureSocket()
		sendBytes(bytes)
	}

	suspend fun send(bundle: OscBundle) {
		val bytes = encodeBundle(bundle)
		ensureSocket()
		sendBytes(bytes)
	}

	private suspend fun sendBytes(bytes: ByteArray) {
		val s = socket ?: return
		withContext(Dispatchers.IO) {
			val packet = DatagramPacket(bytes, bytes.size, InetAddress.getByName(address), port)
			s.send(packet)
		}
	}

	fun close() {
		socket?.close()
		socket = null
	}
}
