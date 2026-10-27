package dev.slimevr.android.multicast

import dev.slimevr.multicast.MulticastSocket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.StandardProtocolFamily
import java.net.StandardSocketOptions
import java.nio.ByteBuffer
import java.nio.channels.ClosedChannelException
import java.nio.channels.DatagramChannel

private const val MAX_DATAGRAM_SIZE = 65_536

private class AndroidMulticastSocket(private val channel: DatagramChannel, private val group: InetSocketAddress) : MulticastSocket {
	override suspend fun listen(onPacket: (ByteArray) -> Unit) = withContext(Dispatchers.IO) {
		val buffer = ByteBuffer.allocate(MAX_DATAGRAM_SIZE)
		while (true) {
			buffer.clear()
			try {
				channel.receive(buffer)
			} catch (_: ClosedChannelException) {
				return@withContext
			}
			onPacket(buffer.array().copyOf(buffer.position()))
		}
	}

	override suspend fun send(payload: ByteArray) {
		withContext(Dispatchers.IO) { channel.send(ByteBuffer.wrap(payload), group) }
	}

	override suspend fun close() = withContext(Dispatchers.IO) { channel.close() }
}

suspend fun bindAndroidMulticast(group: ByteArray, port: Int, interfaceAddress: String): MulticastSocket = withContext(Dispatchers.IO) {
	val groupAddress = InetAddress.getByAddress(group)
	val iface = requireNotNull(NetworkInterface.getByInetAddress(InetAddress.getByName(interfaceAddress))) { "No network interface for $interfaceAddress" }
	val channel = DatagramChannel.open(StandardProtocolFamily.INET)
		.setOption(StandardSocketOptions.SO_REUSEADDR, true)
		.setOption(StandardSocketOptions.IP_MULTICAST_IF, iface)
		.setOption(StandardSocketOptions.IP_MULTICAST_LOOP, true)
		.bind(InetSocketAddress(port))
	channel.join(groupAddress, iface)
	AndroidMulticastSocket(channel, InetSocketAddress(groupAddress, port))
}
