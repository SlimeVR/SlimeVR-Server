package dev.slimevr.desktop.platform.linux

import com.google.protobuf.InvalidProtocolBufferException
import dev.slimevr.VRServer
import dev.slimevr.bridge.BridgeThread
import dev.slimevr.desktop.platform.ProtobufMessages.ProtobufMessage
import dev.slimevr.desktop.platform.SteamVRBridge
import dev.slimevr.tracking.trackers.Tracker
import io.eiren.util.ann.ThreadSafe
import io.eiren.util.logging.LogManager
import java.io.File
import java.io.IOException
import java.lang.AutoCloseable
import java.lang.Thread.currentThread
import java.lang.Thread.sleep
import java.net.StandardProtocolFamily
import java.net.UnixDomainSocketAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel

class UnixSocketBridge(
	server: VRServer,
	bridgeSettingsKey: String,
	bridgeName: String,
	val socketPath: String,
	shareableTrackers: List<Tracker>,
) : SteamVRBridge(server, "Named socket thread", bridgeName, bridgeSettingsKey, shareableTrackers),
	AutoCloseable {
	val socketAddress: UnixDomainSocketAddress = UnixDomainSocketAddress.of(socketPath)
	private val dst: ByteBuffer = ByteBuffer.allocate(2048).order(ByteOrder.LITTLE_ENDIAN)
	private val src: ByteBuffer = ByteBuffer.allocate(2048).order(ByteOrder.LITTLE_ENDIAN)

	lateinit var serverSocket: ServerSocketChannel
	private var channel: SocketChannel? = null
	private var selector: Selector? = null
	private var socketError = false

	init {
		val socketFile = File(socketPath)
		if (socketFile.exists()) {
			if (SocketUtils.isSocketInUse(socketPath)) {
				throw RuntimeException("$socketPath socket is already in use by another process")
			}

			LogManager.warning("[$bridgeName] Cleaning up stale socket $socketPath")
			if (!socketFile.delete()) {
				throw RuntimeException("Failed to delete stale socket $socketPath")
			}
		}
		socketFile.deleteOnExit()
	}

	@BridgeThread
	override fun run() {
		try {
			serverSocket = createSocket()
			while (true) {
				if (channel == null) {
					reportDisconnected()
					selector = Selector.open()
					channel = serverSocket.accept() ?: continue
					channel!!.configureBlocking(false)
					channel!!.register(selector, SelectionKey.OP_READ)
					server.queueTask { reconnected() }
					LogManager.info("[$bridgeName] Connected to ${channel!!.remoteAddress}")
					continue
				}

				if (socketError || !channel!!.isConnected) {
					resetChannel()
					continue
				}

				try {
					val updated = updateSocket()
					updateMessageQueue()
					if (updated) {
						reportConnected()
					} else {
						waitForData(10)
					}
				} catch (e: IOException) {
					resetChannel()
					LogManager.severe("[$bridgeName] Exception when processing packets from socket", e)
					try {
						sleep(10)
					} catch (_: InterruptedException) {
						currentThread().interrupt()
						break
					}
				}
			}
		} catch (e: IOException) {
			LogManager.severe("[$bridgeName] Exception in listen loop", e)
		}
	}

	@ThreadSafe
	override fun signalSend() {
		val selector = selector ?: return
		selector.wakeup()
	}

	@BridgeThread
	@Throws(IOException::class)
	private fun waitForData(timeoutMs: Long) {
		selector!!.select(timeoutMs)
	}

	@BridgeThread
	override fun sendMessageReal(message: ProtobufMessage?): Boolean {
		channel?.let { channel ->
			try {
				val size = message!!.getSerializedSize() + 4
				src.putInt(size)
				val serialized = message.toByteArray()
				src.put(serialized)
				src.flip()

				while (src.hasRemaining()) {
					channel.write(src)
				}

				src.clear()
				return true
			} catch (e: IOException) {
				LogManager.severe("[$bridgeName] Exception when sending message", e)
			}
		}
		return false
	}

	@Throws(IOException::class)
	private fun updateSocket(): Boolean {
		val read = channel!!.read(dst)
		if (read == -1) {
			LogManager.info("[$bridgeName] Reached end-of-stream on connection of ${channel!!.remoteAddress}")
			socketError = true
			return false
		} else if (read == 0) {
			return false
		}

		var readAnything = false
		// if buffer has 4 bytes at least, we got the message size!
		// process all messages
		while (dst.position() >= 4) {
			val messageLength = dst.getInt(0)
			if (messageLength > 1024) { // Overflow
				LogManager.severe("[$bridgeName] Buffer overflow on socket. Message length: $messageLength")
				socketError = true
				break
			} else if (dst.position() >= messageLength) {
				// Parse the message (this reads the array directly from the
				// dst, so we need to move position ourselves)
				try {
					val message: ProtobufMessage = parseMessage(dst.array(), 4, messageLength - 4)
					messageReceived(message)
				} catch (e: InvalidProtocolBufferException) {
					LogManager.severe("[$bridgeName] Failed to read protocol message", e)
				}
				val originalPos = dst.position()
				dst.position(messageLength)
				dst.compact()
				// move position after compacting
				dst.position(originalPos - messageLength)
				readAnything = true
			} else {
				break
			}
		}
		return readAnything
	}

	@Throws(IOException::class)
	private fun resetChannel() {
		LogManager.info("[$bridgeName] Disconnected from ${channel!!.remoteAddress}")
		selector?.close()
		selector = null
		channel?.close()
		channel = null
		socketError = false
		dst.clear()
		server.queueTask { disconnected() }
	}

	@Throws(IOException::class)
	private fun createSocket(): ServerSocketChannel {
		val server = ServerSocketChannel.open(StandardProtocolFamily.UNIX)
		server.bind(socketAddress)
		LogManager.info("[$bridgeName] Socket $socketPath created")
		return server
	}

	@Throws(Exception::class)
	override fun close() {
		serverSocket.close()
	}

	override fun isConnected() = channel != null && channel!!.isConnected

	override fun getBridgeConfigKey(): String = bridgeSettingsKey

	companion object {
		@Throws(InvalidProtocolBufferException::class)
		private fun parseMessage(
			data: ByteArray?,
			offset: Int,
			length: Int,
		) = ProtobufMessage.parser().parseFrom(data, offset, length)
	}
}
