package dev.slimevr.desktop.ipc

import dev.slimevr.VRServer
import dev.slimevr.getSocketDirectory
import dev.slimevr.solarxr.SolarXRConnectionBehaviour
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.StandardProtocolFamily
import java.net.UnixDomainSocketAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import kotlin.io.path.Path

suspend fun createUnixDriverSocket(server: VRServer) = acceptUnixClients(DRIVER_SOCKET_NAME) { channel ->
	handleDriverConnection(
		server = server,
		messages = readFramedMessages(channel),
		send = { bytes -> withContext(Dispatchers.IO) { writeFramed(channel, bytes) } },
	)
}

suspend fun createUnixFeederSocket(server: VRServer) = acceptUnixClients(FEEDER_SOCKET_NAME) { channel ->
	handleFeederConnection(
		server = server,
		messages = readFramedMessages(channel),
		send = { bytes -> withContext(Dispatchers.IO) { writeFramed(channel, bytes) } },
	)
}

suspend fun createUnixSolarXRSocket(server: VRServer, behaviours: List<SolarXRConnectionBehaviour>) = acceptUnixClients(SOLARXR_SOCKET_NAME) { channel ->
	handleSolarXRConnection(
		server = server,
		messages = readFramedMessages(channel),
		send = { bytes -> withContext(Dispatchers.IO) { writeFramed(channel, bytes) } },
		behaviours = behaviours
	)
}

private fun isSocketInUse(socketPath: String): Boolean = try {
	SocketChannel.open(StandardProtocolFamily.UNIX).use {
		it.connect(UnixDomainSocketAddress.of(socketPath))
		true
	}
} catch (_: Exception) {
	false
}

// Length field is LE u32 and includes the 4-byte header itself
private fun readFramedMessages(channel: SocketChannel) = flow {
	val lenBuf = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
	while (true) {
		lenBuf.clear()
		if (channel.read(lenBuf) == -1) break
		lenBuf.flip()

		val dataBuf = ByteBuffer.allocate(lenBuf.int - 4)
		while (dataBuf.hasRemaining()) {
			if (channel.read(dataBuf) == -1) break
		}
		emit(dataBuf.array())
	}
}.flowOn(Dispatchers.IO)

private fun writeFramed(channel: SocketChannel, bytes: ByteArray) {
	val header = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(bytes.size + 4).flip()
	channel.write(arrayOf(header, ByteBuffer.wrap(bytes)))
}

private suspend fun acceptUnixClients(
	name: String,
	handle: suspend (SocketChannel) -> Unit,
) = withContext(Dispatchers.IO) {
	val path = Path(getSocketDirectory(), name)
	val file = path.toFile()
	if (file.exists()) {
		check(!isSocketInUse(path.toString())) {
			"$name socket is already in use by another process"
		}
		file.delete()
	}
	file.deleteOnExit()

	ServerSocketChannel.open(StandardProtocolFamily.UNIX).use { server ->
		server.bind(UnixDomainSocketAddress.of(path))
		while (isActive) {
			val client = server.accept()
			launch { handle(client) }
		}
	}
}
