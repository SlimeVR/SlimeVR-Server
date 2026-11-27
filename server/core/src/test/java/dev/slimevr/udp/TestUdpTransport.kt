package dev.slimevr.udp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.io.Buffer
import kotlinx.io.Source
import java.io.IOException
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.ClosedChannelException
import java.nio.channels.DatagramChannel

private fun receiveDatagrams(channel: DatagramChannel, onDatagram: (from: UdpPeer, payload: Source) -> Unit) {
	val buffer = ByteBuffer.allocate(65_536)
	while (channel.isOpen) {
		buffer.clear()
		val from = try {
			channel.receive(buffer) as? InetSocketAddress ?: continue
		} catch (_: ClosedChannelException) {
			return
		} catch (_: IOException) {
			continue
		}
		buffer.flip()
		val peer = UdpPeer(address = "${from.hostString}/${from.port}", host = from.hostString, port = from.port)
		onDatagram(peer, Buffer().apply { write(ByteArray(buffer.remaining()).also { buffer.get(it) }) })
	}
}

private class TestUdpSocket(val channel: DatagramChannel) : UdpSocket {
	override suspend fun listen(onDatagram: (from: UdpPeer, payload: Source) -> Unit) = suspendCancellableCoroutine { continuation ->
		Thread({ continuation.resumeWith(runCatching { receiveDatagrams(channel, onDatagram) }) }, "Test UDP receive").apply {
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

suspend fun bindTestUdp(port: Int): UdpSocket = withContext(Dispatchers.IO) { TestUdpSocket(DatagramChannel.open().bind(InetSocketAddress(port))) }
