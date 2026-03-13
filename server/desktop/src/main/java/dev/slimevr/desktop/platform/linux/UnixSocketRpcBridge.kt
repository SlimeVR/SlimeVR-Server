package dev.slimevr.desktop.platform.linux

import dev.slimevr.VRServer
import dev.slimevr.bridge.Bridge
import dev.slimevr.bridge.BridgeThread
import dev.slimevr.protocol.GenericConnection
import dev.slimevr.protocol.ProtocolAPI
import dev.slimevr.protocol.ProtocolAPIServer
import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.util.ann.VRServerThread
import io.eiren.util.logging.LogManager
import java.io.File
import java.io.IOException
import java.lang.AutoCloseable
import java.net.StandardProtocolFamily
import java.net.UnixDomainSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.util.*
import java.util.stream.Stream

class UnixSocketRpcBridge(
	private val server: VRServer,
	socketPath: String,
) : Bridge,
	ProtocolAPIServer,
	Runnable,
	AutoCloseable {
	private val runnerThread = Thread(this, "Named socket thread")
	private val socketAddress = UnixDomainSocketAddress.of(socketPath)
	private val socket: ServerSocketChannel
	private val selector: Selector

	init {
		val socketFile = File(socketPath)
		if (socketFile.exists()) {
			if (SocketUtils.isSocketInUse(socketPath)) {
				throw RuntimeException("$socketPath socket is already in use by another process")
			}

			LogManager.warning("[SolarXR Bridge] Cleaning up stale socket $socketPath")
			if (!socketFile.delete()) {
				throw RuntimeException("Failed to delete stale socket $socketPath")
			}
		}
		socketFile.deleteOnExit()

		try {
			socket = ServerSocketChannel.open(StandardProtocolFamily.UNIX)
			selector = Selector.open()
		} catch (e: IOException) {
			LogManager.severe("[SolarXR Bridge] Exception when opening socket $socketPath", e)
			throw RuntimeException("Socket open failed.")
		}

		server.protocolAPI.registerAPIServer(this)
	}

	@VRServerThread
	override fun dataRead() {
	}

	@VRServerThread
	override fun dataWrite() {
	}

	@VRServerThread
	override fun addSharedTracker(tracker: Tracker?) {
	}

	@VRServerThread
	override fun removeSharedTracker(tracker: Tracker?) {
	}

	@VRServerThread
	override fun startBridge() {
		runnerThread.start()
	}

	@BridgeThread
	override fun run() {
		try {
			socket.bind(socketAddress)
			socket.configureBlocking(false)
			socket.register(selector, SelectionKey.OP_ACCEPT)
			LogManager.info("[SolarXR Bridge] Socket $socketAddress created")
			while (socket.isOpen) {
				selector.select(0)
				for (key in selector.selectedKeys()) {
					val conn = key.attachment() as UnixSocketConnection?
					if (conn != null) {
						var message: ByteBuffer?
						while ((conn.read().also { message = it }) != null) {
							server.protocolAPI.onMessage(conn, message!!)
							conn.next()
						}
					} else {
						var channel: SocketChannel?
						while ((socket.accept().also { channel = it }) != null) {
							channel!!.configureBlocking(false)
							channel
								.register(
									selector,
									SelectionKey.OP_READ,
									UnixSocketConnection(channel),
								)
							LogManager.info("[SolarXR Bridge] Connected to ${channel.remoteAddress}")
						}
					}
				}
			}
		} catch (e: IOException) {
			LogManager.severe("[SolarXR Bridge] Exception when running bridge", e)
		}
	}

	@Throws(Exception::class)
	override fun close() {
		socket.close()
		selector.close()
	}

	override fun isConnected() = selector.keys().stream().anyMatch { key: SelectionKey? -> key!!.attachment() != null }

	override val apiConnections: Stream<GenericConnection>
		get() = selector
			.keys()
			.stream()
			.map<GenericConnection?> { key: SelectionKey? -> key!!.attachment() as GenericConnection? }
			.filter { obj: GenericConnection? -> Objects.nonNull(obj) }
			.map { it!! }
}
