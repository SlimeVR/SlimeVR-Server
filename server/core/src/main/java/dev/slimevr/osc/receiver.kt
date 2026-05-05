package dev.slimevr.osc

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket

class OscReceiver(private val port: Int) {
	private var socket: DatagramSocket? = null
	private var listenerScope: CoroutineScope? = null

	suspend fun listen(onMessage: (OscMessage) -> Unit) {
		if (socket == null) {
			socket = withContext(Dispatchers.IO) {
				DatagramSocket(port)
			}
		}
		val s = socket ?: return

		val scope = CoroutineScope(Dispatchers.IO)
		listenerScope = scope

		scope.launch {
			while (isActive) {
				try {
					val buffer = ByteArray(65536)
					val packet = DatagramPacket(buffer, buffer.size)
					s.receive(packet)
					val bytes = packet.data.sliceArray(0 until packet.length)

					try {
						val (msg, _) = decodeMessage(bytes)
						onMessage(msg)
					} catch (e: Exception) {
						// Ignore malformed packets
					}
				} catch (e: Exception) {
					if (isActive) {
						// Socket closed or error
						break
					}
				}
			}
		}
	}

	suspend fun listenBundles(onBundle: (OscBundle) -> Unit) {
		if (socket == null) {
			socket = withContext(Dispatchers.IO) {
				DatagramSocket(port)
			}
		}
		val s = socket ?: return

		val scope = CoroutineScope(Dispatchers.IO)
		listenerScope = scope

		scope.launch {
			while (isActive) {
				try {
					val buffer = ByteArray(65536)
					val packet = DatagramPacket(buffer, buffer.size)
					s.receive(packet)
					val bytes = packet.data.sliceArray(0 until packet.length)

					try {
						// Try bundle first, fall back to message
						val bundle = if (bytes.size >= 8 && bytes.sliceArray(0..7).contentEquals("#bundle ".toByteArray())) {
							val (b, _) = decodeBundle(bytes)
							b
						} else {
							// Wrap message in a bundle
							val (msg, _) = decodeMessage(bytes)
							OscBundle(1, listOf(OscContent.Message(msg)))
						}
						onBundle(bundle)
					} catch (e: Exception) {
						// Ignore malformed packets
					}
				} catch (e: Exception) {
					if (isActive) {
						break
					}
				}
			}
		}
	}

	fun close() {
		listenerScope?.cancel()
		listenerScope = null
		socket?.close()
		socket = null
	}
}
