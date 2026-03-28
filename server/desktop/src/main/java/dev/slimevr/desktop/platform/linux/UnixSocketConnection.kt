package dev.slimevr.desktop.platform.linux

import dev.slimevr.protocol.ConnectionContext
import dev.slimevr.protocol.GenericConnection
import io.eiren.util.logging.LogManager
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.SocketChannel
import java.util.*

class UnixSocketConnection(private val channel: SocketChannel) : GenericConnection {
	override val connectionId: UUID = UUID.randomUUID()
	override val context = ConnectionContext()
	private val dst: ByteBuffer = ByteBuffer.allocate(2048).order(ByteOrder.LITTLE_ENDIAN)
	private var remainingBytes = 0

	private fun resetChannel() {
		try {
			channel.close()
		} catch (e: IOException) {
			LogManager.severe("[SolarXR Bridge] Failed to close socket", e)
		}
	}

	override fun send(bytes: ByteBuffer) {
		if (!this.channel.isConnected) return
		try {
			val src = arrayOf(
				ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN),
				bytes.slice(),
			)
			src[0].putInt(src[1].remaining() + 4)
			src[0].flip()
			synchronized(this) {
				while (src[1].hasRemaining()) {
					this.channel.write(src)
				}
			}
		} catch (e: IOException) {
			LogManager.severe("[SolarXR Bridge] Failed to send message", e)
		}
	}

	fun read(): ByteBuffer? {
		if (dst.position() < 4 || dst.position() < dst.getInt(0)) {
			if (!channel.isConnected) return null
			try {
				val result = channel.read(dst)
				if (result == -1) {
					LogManager.info("[SolarXR Bridge] Reached end-of-stream on connection")
					resetChannel()
					return null
				}
				if (dst.position() < 4) {
					return null
				}
			} catch (e: IOException) {
				LogManager.severe("[SolarXR Bridge] Exception when reading from connection", e)
				resetChannel()
				return null
			}
		}
		val messageLength = dst.getInt(0)
		if (messageLength > 1024) {
			LogManager.severe("[SolarXR Bridge] Buffer overflow on socket. Message length: $messageLength")
			resetChannel()
			return null
		}
		if (dst.position() < messageLength) {
			return null
		}
		remainingBytes = dst.position() - messageLength
		dst.position(4)
		dst.limit(messageLength)
		return dst
	}

	fun next() {
		dst.position(dst.limit())
		dst.limit(dst.limit() + remainingBytes)
		dst.compact()
		dst.limit(dst.capacity())
	}
}
