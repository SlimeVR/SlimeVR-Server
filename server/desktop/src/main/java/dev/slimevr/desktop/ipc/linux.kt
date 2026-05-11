package dev.slimevr.desktop.ipc

import dev.slimevr.AppContextProvider
import dev.slimevr.AppLogger
import dev.slimevr.getSocketDirectory
import dev.slimevr.solarxr.handleSolarXRBridge
import dev.slimevr.util.safeLaunch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.net.SocketException
import java.net.StandardProtocolFamily
import java.net.UnixDomainSocketAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import kotlin.io.path.Path

suspend fun createUnixDriverSocket(appContext: AppContextProvider) = acceptUnixClients(DRIVER_SOCKET_NAME) { channel ->
	handleDriverConnection(
		appContext = appContext,
		messages = readFramedMessages(channel),
		send = { bytes -> withContext(Dispatchers.IO) { writeFramed(channel, bytes) } },
	)
}

suspend fun createUnixFeederSocket(appContext: AppContextProvider) = acceptUnixClients(FEEDER_SOCKET_NAME) { channel ->
	handleDriverConnection(
		appContext = appContext,
		messages = readFramedMessages(channel),
		send = { bytes -> withContext(Dispatchers.IO) { writeFramed(channel, bytes) } },
	)
}

suspend fun createUnixSolarXRSocket(appContext: AppContextProvider) = acceptUnixClients(SOLARXR_SOCKET_NAME) { channel ->
	handleSolarXRBridge(
		appContext = appContext,
		messages = readFramedMessages(channel),
		send = { bytes -> withContext(Dispatchers.IO) { writeFramed(channel, bytes) } },
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
	try {
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
	} catch (e: SocketException) {
		AppLogger.ipc.warn("Exception on socket: ${e.message}")
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
			safeLaunch { handle(client) }
		}
	}
}
