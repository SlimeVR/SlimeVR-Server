package dev.slimevr.desktop.ipc

import dev.slimevr.AppContextProvider
import dev.slimevr.driver.DriverBridgeSource
import dev.slimevr.getSocketDirectory
import dev.slimevr.logging.AppLogger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import okio.Buffer
import java.io.IOException
import java.net.SocketException
import java.net.StandardProtocolFamily
import java.net.UnixDomainSocketAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.ClosedByInterruptException
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import kotlin.io.path.Path

suspend fun createUnixDriverSocket(appContext: AppContextProvider) = acceptUnixClients(DRIVER_SOCKET_NAME) { channel ->
	val writer = FramedWriter(channel)
	handleDriverConnection(
		appContext = appContext,
		source = DriverBridgeSource.DRIVER,
		messages = readFramedMessages(channel),
		send = { frame -> writer.write(frame) },
	)
}

suspend fun createUnixFeederSocket(appContext: AppContextProvider) = acceptUnixClients(FEEDER_SOCKET_NAME) { channel ->
	val writer = FramedWriter(channel)
	handleDriverConnection(
		appContext = appContext,
		source = DriverBridgeSource.FEEDER,
		messages = readFramedMessages(channel),
		send = { frame -> writer.write(frame) },
	)
}

suspend fun createUnixSolarXRSocket(appContext: AppContextProvider) = acceptUnixClients(SOLARXR_SOCKET_NAME) { channel ->
	val writer = FramedWriter(channel)
	handleSolarXRBridge(
		appContext = appContext,
		messages = readFramedMessages(channel),
		send = { frame -> writer.write(frame) },
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

// Length field is LE u32 and includes the 4-byte header itself.
// The payload lands in one Buffer that every frame reuses
private fun readFramedMessages(channel: SocketChannel) = flow {
	val lenBuf = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
	val dataBuf = ByteBuffer.allocate(MAX_FRAME_SIZE)
	val frame = Buffer()
	try {
		while (true) {
			val len = runInterruptible(Dispatchers.IO) { readFrame(channel, lenBuf, dataBuf) }
			if (len < 0) break
			frame.clear()
			frame.write(dataBuf.array(), 0, len)
			emit(frame)
		}
	} catch (e: SocketException) {
		AppLogger.ipc.warn("Exception on socket: ${e.message}")
	} catch (e: ClosedByInterruptException) {
		AppLogger.ipc.info("Socket read interrupted, dropping connection")
	}
}

private fun readFrame(channel: SocketChannel, lenBuf: ByteBuffer, dataBuf: ByteBuffer): Int {
	lenBuf.clear()
	if (!readFully(channel, lenBuf)) return -1
	lenBuf.flip()

	val len = lenBuf.int - 4
	if (len !in 0..MAX_FRAME_SIZE) throw IOException("Frame length out of range: ${len + 4}")
	dataBuf.clear().limit(len)
	return if (readFully(channel, dataBuf)) len else -1
}

private fun readFully(channel: SocketChannel, buf: ByteBuffer): Boolean {
	while (buf.hasRemaining()) {
		if (channel.read(buf) == -1) return false
	}
	return true
}

private class FramedWriter(private val channel: SocketChannel) {
	private val header = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
	private val payload = ByteBuffer.allocate(MAX_FRAME_SIZE)
	private val gather = arrayOf(header, payload)

	fun write(frame: Buffer) {
		val len = frame.size.toInt()
		if (len > MAX_FRAME_SIZE) throw IOException("Frame too large to send: ${len + 4} bytes")
		payload.clear().limit(len)
		frame.read(payload.array(), 0, len)

		header.clear()
		header.putInt(len + 4)
		header.flip()
		while (header.hasRemaining() || payload.hasRemaining()) channel.write(gather)
	}
}

private suspend fun acceptUnixClients(
	name: String,
	handle: suspend (SocketChannel) -> Unit,
) = withContext(Dispatchers.IO) {
	val path = Path(getSocketDirectory(), name)
	AppLogger.ipc.info("Creating $name socket at $path")
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
		supervisorScope {
			while (isActive) {
				val client = try {
					runInterruptible { server.accept() }
				} catch (e: ClosedByInterruptException) {
					break
				}
				AppLogger.ipc.info("$name client connected")
				launch(Dispatchers.Default) {
					try {
						handle(client)
					} catch (e: CancellationException) {
						throw e
					} catch (e: Exception) {
						AppLogger.ipc.error(e, "Error while handling $name client, dropping connection")
					} finally {
						AppLogger.ipc.info("$name client disconnected")
						try {
							client.close()
						} catch (ignored: IOException) {
						}
					}
				}
			}
		}
	}
}
