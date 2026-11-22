package dev.slimevr.android.udp

import dev.slimevr.logging.AppLogger
import dev.slimevr.udp.UdpPeer
import dev.slimevr.udp.UdpSocket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.io.Buffer
import kotlinx.io.Source
import java.io.IOException
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.nio.ByteBuffer
import java.nio.channels.ClosedChannelException
import java.nio.channels.DatagramChannel

private const val MAX_DATAGRAM_SIZE = 65_536
private const val MAX_CACHED_PEERS = 255

// Least recently heard sender goes first, so active trackers stay cached even past the cap
private fun peerCache(): MutableMap<SocketAddress, UdpPeer> = object : LinkedHashMap<SocketAddress, UdpPeer>(16, 0.75f, true) {
	override fun removeEldestEntry(eldest: MutableMap.MutableEntry<SocketAddress, UdpPeer>) = size > MAX_CACHED_PEERS
}

private fun peerOf(from: InetSocketAddress) = UdpPeer(address = from.hostString, host = from.hostString, port = from.port)

private fun receiveDatagrams(channel: DatagramChannel, onDatagram: (from: UdpPeer, payload: Source) -> Unit) {
	val buffer = ByteBuffer.allocateDirect(MAX_DATAGRAM_SIZE)
	val peers = peerCache()
	while (channel.isOpen) {
		buffer.clear()
		val from = try {
			channel.receive(buffer) as? InetSocketAddress ?: continue
		} catch (_: ClosedChannelException) {
			return
		} catch (e: IOException) {
			AppLogger.udpReceive.debug("Ignored UDP receive error: ${e.message}")
			continue
		}
		buffer.flip()
		val payload = Buffer().apply { write(ByteArray(buffer.remaining()).also { buffer.get(it) }) }

		onDatagram(peers.getOrPut(from) { peerOf(from) }, payload)
	}
}

private class AndroidUdpSocket(private val channel: DatagramChannel, private val port: Int) : UdpSocket {
	override suspend fun listen(onDatagram: (from: UdpPeer, payload: Source) -> Unit) = suspendCancellableCoroutine { continuation ->
		Thread({
			continuation.resumeWith(runCatching { receiveDatagrams(channel, onDatagram) })
		}, "UDP receive :$port").apply {
			isDaemon = true
			start()
		}
		continuation.invokeOnCancellation { channel.close() }
	}

	override suspend fun send(to: UdpPeer, payload: ByteArray) {
		withContext(Dispatchers.IO) { channel.send(ByteBuffer.wrap(payload), InetSocketAddress(to.host, to.port)) }
	}

	override suspend fun close() = withContext(Dispatchers.IO) { channel.close() }
}

suspend fun bindAndroidUdp(port: Int): UdpSocket = withContext(Dispatchers.IO) {
	AndroidUdpSocket(DatagramChannel.open().bind(InetSocketAddress(port)), port)
}
