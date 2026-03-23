package dev.slimevr.tracking.trackers.udp

import com.jme3.math.FastMath
import dev.slimevr.NetworkProtocol
import dev.slimevr.VRServer
import dev.slimevr.config.config
import dev.slimevr.protocol.rpc.MAG_TIMEOUT
import dev.slimevr.tracking.trackers.*
import io.eiren.util.collections.FastList
import io.eiren.util.logging.LogManager
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Quaternion.Companion.fromRotationVector
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.net.InetAddress
import java.net.NetworkInterface
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.function.Consumer
import kotlin.coroutines.resume

/**
 * Receives trackers data by UDP using extended owoTrack protocol.
 */
class TrackersUDPServer(private val port: Int, name: String, private val trackersConsumer: Consumer<Tracker>) : Thread(name) {
	private val random = Random()
	private val connections: MutableList<UDPDevice> = FastList()
	private val connectionsByAddress: MutableMap<SocketAddress, UDPDevice> = HashMap()
	private val connectionsByMAC: MutableMap<String, UDPDevice> = HashMap()
	private val broadcastAddresses: List<InetSocketAddress> = try {
		NetworkInterface.getNetworkInterfaces().asSequence().filter {
			// Ignore loopback, PPP, virtual and disabled interfaces
			!it.isLoopback && it.isUp && !it.isPointToPoint && !it.isVirtual
		}.flatMap {
			it.interfaceAddresses.asSequence()
		}.map {
			// This ignores IPv6 addresses
			it.broadcast
		}.filter { it != null && it.isSiteLocalAddress }.map { InetSocketAddress(it!!.hostAddress, this.port) }.toList()
	} catch (e: Exception) {
		LogManager.severe("[TrackerServer] Can't enumerate network interfaces", e)
		emptyList()
	}
	private val parser = UDPProtocolParser()

	// 1500 is a common network MTU. 1472 is the maximum size of a UDP packet (1500 - 20 for IPv4 header - 8 for UDP header)
	private val rcvBuffer = ByteArray(1500 - 20 - 8)
	private val bb = ByteBuffer.wrap(rcvBuffer).order(ByteOrder.BIG_ENDIAN)
	private val sendMutex = Mutex()

	// Gets initialized in this.run()
	private lateinit var socket: BoundDatagramSocket
	private val mainScope = CoroutineScope(SupervisorJob())
	private var discoveryJob: Job? = null

	// Per-connection processing: one Channel<ByteArray> per device, drained on Dispatchers.Default
	private val deviceChannels = ConcurrentHashMap<Int, Channel<Pair<Long, ByteArray>>>()
	// Single channel for packets from unrecognised addresses (handshakes and initial traffic)
	private val handshakeChannel = Channel<Pair<SocketAddress, ByteArray>>(Channel.BUFFERED)

	// region Send helpers

	private suspend fun sendPacket(packet: UDPPacket, address: SocketAddress) {
		val datagram = sendMutex.withLock {
			bb.limit(bb.capacity())
			bb.rewind()
			parser.write(bb, packet)
			Datagram(ByteReadPacket(rcvBuffer.copyOf(bb.position())), address)
		}
		socket.send(datagram)
	}

	private suspend fun sendHandshakeResponse(connection: UDPDevice) {
		val datagram = sendMutex.withLock {
			bb.limit(bb.capacity())
			bb.rewind()
			parser.writeHandshakeResponse(bb)
			Datagram(ByteReadPacket(rcvBuffer.copyOf(bb.position())), connection.address)
		}
		socket.send(datagram)
	}

	private suspend fun sendSensorInfoResponse(connection: UDPDevice, packet: UDPPacket15SensorInfo) {
		val datagram = sendMutex.withLock {
			bb.limit(bb.capacity())
			bb.rewind()
			parser.writeSensorInfoResponse(bb, packet)
			Datagram(ByteReadPacket(rcvBuffer.copyOf(bb.position())), connection.address)
		}
		socket.send(datagram)
	}

	// endregion

	// region Connection setup

	private suspend fun setUpNewConnection(remoteAddress: SocketAddress, handshake: UDPPacket3Handshake) {
		val addr = withContext(Dispatchers.IO) {
			InetAddress.getByName((remoteAddress as InetSocketAddress).hostname)
		}
		LogManager.info("[TrackerServer] Handshake received from $remoteAddress")

		// Check if it's a known device
		VRServer.instance.configManager.vrConfig.let { vrConfig ->
			if (vrConfig.isKnownDevice(handshake.macString)) return@let
			val mac = handshake.macString ?: return@let

			VRServer.instance.handshakeHandler.sendUnknownHandshake(mac)
			return
		}

		// Get a connection either by an existing one, or by creating a new one
		val reconnection: UDPDevice? = synchronized(connections) {
			reconnectByMAC(remoteAddress, addr, handshake)
				?: reconnectByAddress(remoteAddress, addr, handshake)
		}
		val connection = reconnection ?: registerNewConnection(remoteAddress, addr, handshake)

		// On reconnect, restart the device channel so its lastPacketNumber resets to -1.
		// The old coroutine drains its remaining buffered items then exits naturally.
		if (reconnection != null) {
			restartDeviceChannel(connection)
		}

		connection.firmwareFeatures = FirmwareFeatures()
		sendHandshakeResponse(connection)
	}

	/** Must be called under synchronized(connections). Returns the updated device if found by MAC, null otherwise. */
	private fun reconnectByMAC(socketAddr: SocketAddress, addr: InetAddress, handshake: UDPPacket3Handshake): UDPDevice? {
		val connection = connectionsByMAC[handshake.macString] ?: return null
		connectionsByAddress.remove(connection.address)
		connection.address = socketAddr
		connection.ipAddress = addr
		connection.name = handshake.macString?.let { "udp://$it" }
		connection.descriptiveName = "udp:/$addr"
		connection.protocolVersion = handshake.protocolVersion
		connection.firmwareVersion = handshake.firmware
		connectionsByAddress[connection.address] = connection

		val i = connections.indexOf(connection)
		LogManager.info(
			"""
			[TrackerServer] Tracker $i handed over to address $socketAddr.
			Board type: ${handshake.boardType},
			firmware name: ${handshake.firmware},
			protocol version: ${connection.protocolVersion},
			mac: ${handshake.macString},
			name: ${connection.name}
			""".trimIndent(),
		)
		return connection
	}

	/** Must be called under synchronized(connections). Returns the updated device if found by address, null otherwise. */
	private fun reconnectByAddress(socketAddr: SocketAddress, addr: InetAddress, handshake: UDPPacket3Handshake): UDPDevice? {
		val connection = connectionsByAddress[socketAddr] ?: return null
		connection.ipAddress = addr
		connection.name = handshake.macString?.let { "udp://$it" } ?: "udp:/$addr"
		connection.descriptiveName = "udp:/$addr"
		connection.protocolVersion = handshake.protocolVersion
		connection.firmwareVersion = handshake.firmware

		val i = connections.indexOf(connection)
		LogManager.info(
			"""
			[TrackerServer] Tracker $i reconnected from address $socketAddr.
			Board type: ${handshake.boardType},
			firmware name: ${handshake.firmware},
			protocol version: ${connection.protocolVersion},
			mac: ${handshake.macString},
			name: ${connection.name}
			""".trimIndent(),
		)
		return connection
	}

	/** Creates, configures, and registers a brand-new connection. */
	private fun registerNewConnection(socketAddr: SocketAddress, addr: InetAddress, handshake: UDPPacket3Handshake): UDPDevice {
		val connection = UDPDevice(
			socketAddr,
			addr,
			handshake.macString ?: addr.hostAddress,
			handshake.boardType,
			handshake.mcuType,
		)
		VRServer.instance.deviceManager.addDevice(connection)
		connection.protocolVersion = handshake.protocolVersion
		connection.protocol = if (handshake.firmware?.isEmpty() == true) {
			// Only old owoTrack doesn't report firmware and have different packet IDs with SlimeVR
			NetworkProtocol.OWO_LEGACY
		} else {
			NetworkProtocol.SLIMEVR_RAW
		}
		connection.name = handshake.macString?.let { "udp://$it" }
			?: "udp:/$addr"
		// TODO: The missing slash in udp:// was intended because InetAddress.toString()
		// 		returns "hostname/address" but it wasn't known that if hostname is empty
		// 		string it just looks like "/address" lol.
		// 		Fixing this would break config!
		connection.descriptiveName = "udp:/$addr"
		connection.firmwareVersion = handshake.firmware
		// Create the channel before publishing to maps so the dispatch loop never
		// sees a connection without a ready channel.
		launchDeviceChannel(connection)
		synchronized(connections) {
			val i = connections.size
			connections.add(connection)
			connectionsByAddress[socketAddr] = connection
			if (handshake.macString != null) {
				connectionsByMAC[handshake.macString!!] = connection
			}
			LogManager.info(
				"""
				[TrackerServer] Tracker $i connected from address $socketAddr.
				Board type: ${handshake.boardType},
				firmware name: ${handshake.firmware},
				protocol version: ${connection.protocolVersion},
				mac: ${handshake.macString},
				name: ${connection.name}
				""".trimIndent(),
			)
		}
		if (connection.protocol == NetworkProtocol.OWO_LEGACY || connection.protocolVersion < 9) {
			// Set up new sensor for older firmware.
			// Firmware after 7 should send sensor status packet and sensor
			// will be created when it's received
			setUpSensor(
				connection,
				0,
				handshake.imuType,
				1,
				MagnetometerStatus.NOT_SUPPORTED,
				null,
				TrackerDataType.ROTATION,
				null,
			)
		}
		return connection
	}


	private fun setUpSensor(
		connection: UDPDevice,
		trackerId: Int,
		sensorType: IMUType,
		sensorStatus: Int,
		magStatus: MagnetometerStatus,
		trackerPosition: TrackerPosition?,
		trackerDataType: TrackerDataType,
		hasCompletedRestCalibration: Boolean?,
	) {
		LogManager.info("[TrackerServer] Sensor $trackerId for ${connection.name} status: $sensorStatus")
		val imuTracker = createTrackerIfAbsent(connection, trackerId, sensorType, trackerPosition, trackerDataType, magStatus)

		val status = UDPPacket15SensorInfo.getStatus(sensorStatus)
		if (status != null) imuTracker.status = status
		imuTracker.hasCompletedRestCalibration = hasCompletedRestCalibration

		if (magStatus != MagnetometerStatus.NOT_SUPPORTED) {
			syncMagState(connection, trackerId, magStatus, imuTracker)
		}
	}

	private fun createTrackerIfAbsent(
		connection: UDPDevice,
		trackerId: Int,
		sensorType: IMUType,
		trackerPosition: TrackerPosition?,
		trackerDataType: TrackerDataType,
		magStatus: MagnetometerStatus,
	): Tracker {
		connection.getTracker(trackerId)?.let { return it }

		var formattedHWID = connection.hardwareIdentifier.replace(":", "").takeLast(5)
		if (trackerId != 0) {
			formattedHWID += " Extension"
			if (trackerId > 1) {
				formattedHWID += " $trackerId"
			}
		}

		val imuTracker = Tracker(
			connection,
			VRServer.getNextLocalTrackerId(),
			connection.name + "/" + trackerId,
			"Tracker $formattedHWID",
			trackerPosition,
			trackerNum = trackerId,
			hasRotation = true,
			hasAcceleration = true,
			userEditable = true,
			imuType = if (trackerDataType == TrackerDataType.ROTATION) sensorType else null,
			allowFiltering = true,
			allowReset = true,
			allowMounting = true,
			usesTimeout = true,
			magStatus = magStatus,
			trackerDataType = trackerDataType,
		)
		connection.trackers[trackerId] = imuTracker
		trackersConsumer.accept(imuTracker)
		LogManager.info("[TrackerServer] Added sensor $trackerId for ${connection.name}, ImuType $sensorType, DataType $trackerDataType, default TrackerPosition $trackerPosition")
		return imuTracker
	}

	private fun syncMagState(connection: UDPDevice, trackerId: Int, magStatus: MagnetometerStatus, tracker: Tracker) {
		if (magStatus == MagnetometerStatus.ENABLED &&
			(!VRServer.instance.configManager.vrConfig.server.useMagnetometerOnAllTrackers || tracker.config.shouldHaveMagEnabled == false)
		) {
			mainScope.launch {
				withTimeoutOrNull(MAG_TIMEOUT) {
					connection.setMag(false, trackerId)
				}
			}
		} else if (magStatus == MagnetometerStatus.DISABLED &&
			VRServer.instance.configManager.vrConfig.server.useMagnetometerOnAllTrackers &&
			tracker.config.shouldHaveMagEnabled == true
		) {
			mainScope.launch {
				withTimeoutOrNull(MAG_TIMEOUT) {
					connection.setMag(true, trackerId)
				}
			}
		}
	}

	// endregion

	// region Config flag queue

	private data class ConfigStateWaiter(
		val expectedState: Boolean,
		val channel: CancellableContinuation<Boolean>,
		var ran: Boolean = false,
	)

	private val queues: MutableMap<Triple<SocketAddress, ConfigTypeId, Int>, Deque<ConfigStateWaiter>> = ConcurrentHashMap()

	suspend fun setConfigFlag(device: UDPDevice, configTypeId: ConfigTypeId, state: Boolean, sensorId: Int = 255) {
		if (device.timedOut) return
		val triple = Triple(device.address, configTypeId, sensorId)
		val queue = queues.computeIfAbsent(triple) { _ -> ConcurrentLinkedDeque() }

		suspendCancellableCoroutine {
			val waiter = ConfigStateWaiter(state, it)
			queue.add(waiter)
			it.invokeOnCancellation {
				queue.remove(waiter)
			}
		}
	}

	private suspend fun actualSetConfigFlag(device: UDPDevice, configTypeId: ConfigTypeId, state: Boolean, sensorId: Int) {
		sendPacket(UDPPacket25SetConfigFlag(sensorId, configTypeId, state), device.address)
	}

	private suspend fun processConfigQueuesFor(device: UDPDevice) {
		queues.forEach { (t, p) ->
			if (t.first != device.address) return@forEach
			val q = p.firstOrNull() ?: return@forEach
			if (q.ran) return@forEach
			actualSetConfigFlag(device, t.second, q.expectedState, t.third)
			if (!device.timedOut) q.ran = true
		}
	}

	// endregion

	// region Main loop

	override fun run() {
		runBlocking {
			val selectorManager = SelectorManager(Dispatchers.IO)
			try {
				socket = aSocket(selectorManager).udp().bind(InetSocketAddress("0.0.0.0", port)) {
					broadcast = true
				}
				startDiscovery()
				// Keepalive / heartbeat timer
				launch {
					while (true) {
						delay(500)
						tickConnections()
					}
				}
				// Handshake coroutine — processes packets from unrecognised addresses
				launch {
					for ((remoteAddress, bytes) in handshakeChannel) {
						try {
							val connection = synchronized(connections) { connectionsByAddress[remoteAddress] }
							// If the connection is already set up, redirect to its channel to avoid
							// racing on lastPacketNumber with the per-device coroutine.
							val deviceCh = connection?.let { deviceChannels[it.id] }
							if (deviceCh != null) {
								deviceCh.trySend(Pair(System.currentTimeMillis(), bytes))
								continue
							}
							val bb = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)
							parser.parse(bb).packets
								.forEach { processPacket(remoteAddress, it, connection) }
						} catch (e: CancellationException) {
							throw e
						} catch (e: Exception) {
							LogManager.warning("[TrackerServer] Error handling handshake from $remoteAddress", e)
						}
					}
				}
				// Dispatch loop — receives and routes to the right channel
				for (datagram in socket.incoming) {
					val bytes = datagram.packet.readBytes()
					val connection = synchronized(connections) { connectionsByAddress[datagram.address] }
					if (connection != null) {
						deviceChannels[connection.id]?.trySend(Pair(System.currentTimeMillis(), bytes))
					} else {
						handshakeChannel.trySend(Pair(datagram.address, bytes))
					}
				}
			} catch (e: Exception) {
				e.printStackTrace()
			} finally {
				handshakeChannel.close()
				deviceChannels.values.forEach { it.close() }
				if (::socket.isInitialized) runCatching { socket.close() }
				selectorManager.close()
				mainScope.cancel()
			}
		}
	}

	/** Creates the per-device processing channel and launches its coroutine on [mainScope]. */
	/** Closes the existing device channel (if any) and launches a fresh one with a reset sequence counter. */
	private fun restartDeviceChannel(connection: UDPDevice) {
		deviceChannels.remove(connection.id)?.close()
		launchDeviceChannel(connection)
	}

	private fun launchDeviceChannel(connection: UDPDevice) {
		val ch = Channel<Pair<Long, ByteArray>>(Channel.BUFFERED)
		deviceChannels[connection.id] = ch
		mainScope.launch {
			var lastPacketNumber = -1L
			for ((arrivalTime, bytes) in ch) {
				try {
					val bb = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)
					val (packetNumber, packets) = parser.parse(bb)
					if (packetNumber != 0L && packetNumber <= lastPacketNumber) {
						// Skip packet with exact same packet number -> usually happens
						// when an ap or interface does retransmission
						// happen often on linux hotspots
						if (packetNumber == lastPacketNumber) continue

						println("Out of order packet received: id ${packets.joinToString { it.packetId.toString() }} number $packetNumber, last $lastPacketNumber, from $connection")
						continue
					}
					lastPacketNumber = packetNumber
					connection.lastPacket = System.currentTimeMillis()
					connection.trackers.values.forEach { it.heartbeat() }
					packets.forEach { processPacket(connection.address, it, connection, arrivalTime) }
					processConfigQueuesFor(connection)
				} catch (e: CancellationException) {
					throw e
				} catch (e: Exception) {
					LogManager.warning("[TrackerServer] Error handling packet from ${connection.name}", e)
				}
			}
		}
	}

	private fun startDiscovery() {
		discoveryJob?.cancel()
		discoveryJob = mainScope.launch {
			while (synchronized(connections) { connections.none { it.trackers.isNotEmpty() } }) {
				for (addr in broadcastAddresses) {
					sendPacket(UDPPacket0Heartbeat, addr)
				}
				delay(2000)
			}
		}
	}

	private suspend fun tickConnections() {
		data class PendingSend(val conn: UDPDevice, val packet: UDPPacket)
		val toSend = mutableListOf<PendingSend>()

		synchronized(connections) {
			val now = System.currentTimeMillis()
			for (conn in connections) {
				toSend.add(PendingSend(conn, UDPPacket1Heartbeat))
				if (conn.lastPacket + 1000 < now) {
					if (!conn.timedOut) {
						conn.timedOut = true
						LogManager.info("[TrackerServer] Tracker timed out: $conn")
					}
				} else {
					for (value in conn.trackers.values) {
						if (value.status == TrackerStatus.DISCONNECTED ||
							value.status == TrackerStatus.TIMED_OUT
						) {
							value.status = TrackerStatus.OK
						}
					}
					conn.timedOut = false
				}

				if (conn.serialBuffer.isNotEmpty() &&
					conn.lastSerialUpdate + 500L < now
				) {
					LogManager.info("[${conn.name}] ${conn.serialBuffer}")
					conn.serialBuffer.setLength(0)
				}

				if (conn.lastPingPacketTime + 1000 < now) {
					conn.lastPingPacketId = random.nextInt()
					toSend.add(PendingSend(conn, UDPPacket10PingPong(conn.lastPingPacketId)))
					println("SENDING PING ----")
				}
			}
		}

		for ((conn, packet) in toSend) {
			if (packet is UDPPacket10PingPong) {
				conn.lastPingPacketTime = System.currentTimeMillis()
			}
			sendPacket(packet, conn.address)
		}
	}

	// endregion

	// region Packet processing

	private suspend fun processPacket(remoteAddress: SocketAddress, packet: UDPPacket, connection: UDPDevice?, arrivalTime: Long = System.currentTimeMillis()) {
		val tracker: Tracker?
		when (packet) {
			is UDPPacket0Heartbeat, is UDPPacket1Heartbeat, is UDPPacket25SetConfigFlag -> {}

			is UDPPacket3Handshake -> setUpNewConnection(remoteAddress, packet)

			is RotationPacket -> handleRotationPacket(packet, connection)

			is UDPPacket17RotationData -> {
				tracker = connection?.getTracker(packet.sensorId)
				if (tracker == null) return
				var rot17 = packet.rotation
				rot17 = AXES_OFFSET * rot17
				when (packet.dataType) {
					UDPPacket17RotationData.DATA_TYPE_NORMAL -> {
						tracker.setRotation(rot17)
						tracker.dataTick()
					}

					UDPPacket17RotationData.DATA_TYPE_CORRECTION -> {}
				}
			}

			is UDPPacket18MagnetometerAccuracy -> {}

			is UDPPacket4Acceleration -> {
				tracker = connection?.getTracker(packet.sensorId)
				if (tracker == null) return
				// sensorOffset is applied correctly since protocol 22
				// See: https://github.com/SlimeVR/SlimeVR-Tracker-ESP/pull/480
				if (connection.protocolVersion >= 22) {
					tracker.setAcceleration(packet.acceleration)
				} else {
					tracker.setAcceleration(SENSOR_OFFSET_CORRECTION.sandwich(packet.acceleration))
				}
			}

			is UDPPacket10PingPong -> {
				if (connection == null) return
				if (connection.lastPingPacketId == packet.pingId) {
					val delay = System.currentTimeMillis() - connection.lastPingPacketTime
					val ping = delay.toInt() / 2
					LogManager.debug("[TrackerServer] Pong from ${connection.name}: ${ping}ms")
					for (t in connection.trackers.values) {
						t.ping = ping
						t.dataTick()
					}
				} else {
					LogManager.debug(
						"[TrackerServer] Wrong ping id ${packet.pingId} != ${connection.lastPingPacketId}",
					)
				}
			}

			is UDPPacket11Serial -> {
				if (connection == null) return
				LogManager.info("[${connection.name}] ${packet.serial}")
			}

			is UDPPacket12BatteryLevel -> connection?.trackers?.values?.forEach {
				it.batteryVoltage = packet.voltage
				it.batteryLevel = packet.level * 100
			}

			is UDPPacket13Tap -> {
				tracker = connection?.getTracker(packet.sensorId)
				if (tracker == null) return
				LogManager.info(
					"[TrackerServer] Tap packet received from ${tracker.name}: ${packet.tap}",
				)
			}

			is UDPPacket14Error -> {
				LogManager.severe(
					"[TrackerServer] Error received from $remoteAddress: ${packet.errorNumber}",
				)
				tracker = connection?.getTracker(packet.sensorId)
				if (tracker == null) return
				tracker.status = TrackerStatus.ERROR
			}

			is UDPPacket15SensorInfo -> handleSensorInfo(packet, connection)

			is UDPPacket19SignalStrength -> connection?.trackers?.values?.forEach {
				it.signalStrength = packet.signalStrength
			}

			is UDPPacket20Temperature -> {
				tracker = connection?.getTracker(packet.sensorId) ?: return
				tracker.temperature = packet.temperature
			}

			is UDPPacket21UserAction -> handleUserAction(packet, connection)

			is UDPPacket22FeatureFlags -> {
				if (connection == null) return
				// Respond with server flags
				sendPacket(packet, connection.address)
				connection.firmwareFeatures = packet.firmwareFeatures
			}

			is UDPPacket24AckConfigChange -> handleAckConfigChange(packet, connection)

			is UDPPacket26FlexData -> {
				tracker = connection?.getTracker(packet.sensorId)
				if (tracker == null) return
				if (tracker.trackerDataType == TrackerDataType.FLEX_RESISTANCE) {
					tracker.trackerFlexHandler.setFlexResistance(packet.flexData)
				} else if (tracker.trackerDataType == TrackerDataType.FLEX_ANGLE) {
					tracker.trackerFlexHandler.setFlexAngle(packet.flexData)
				}
				tracker.dataTick()
			}

			is UDPPacket27Position -> {
				tracker = connection?.getTracker(packet.sensorId)
				if (tracker == null) return
				tracker.position = packet.position
				// dont call dataTick here as this is just position update
			}

			is UDPPacket200ProtocolChange -> {}
		}
	}

	private fun handleRotationPacket(packet: RotationPacket, connection: UDPDevice?) {
		val rot = AXES_OFFSET.times(packet.rotation)
		val tracker = connection?.getTracker(packet.sensorId) ?: return
		tracker.setRotation(rot)
		if (packet is UDPPacket23RotationAndAcceleration) {
			// sensorOffset is applied correctly since protocol 22
			// See: https://github.com/SlimeVR/SlimeVR-Tracker-ESP/pull/480
			if (connection.protocolVersion >= 22) {
				tracker.setAcceleration(packet.acceleration)
			} else {
				tracker.setAcceleration(SENSOR_OFFSET_CORRECTION.sandwich(packet.acceleration))
			}
		}
		tracker.dataTick()
	}

	private suspend fun handleSensorInfo(packet: UDPPacket15SensorInfo, connection: UDPDevice?) {
		if (connection == null) return
		val magStatus = packet.sensorConfig?.magStatus ?: MagnetometerStatus.NOT_SUPPORTED
		setUpSensor(
			connection,
			packet.sensorId,
			packet.sensorType,
			packet.sensorStatus,
			magStatus,
			packet.trackerPosition,
			packet.trackerDataType,
			packet.hasCompletedRestCalibration,
		)
		// Send ack
		sendSensorInfoResponse(connection, packet)
		LogManager.info(
			"[TrackerServer] Sensor info for ${connection.descriptiveName}/${packet.sensorId}: ${packet.sensorStatus}, mag $magStatus",
		)
	}

	private fun handleUserAction(packet: UDPPacket21UserAction, connection: UDPDevice?) {
		if (connection == null) return
		var name = ""
		when (packet.type) {
			UDPPacket21UserAction.RESET_FULL -> {
				name = "Full reset"
				VRServer.instance.scheduleResetTrackersFull(
					RESET_SOURCE_NAME,
					(VRServer.instance.configManager.vrConfig.resetsConfig.fullResetDelay * 1000).toLong(),
				)
			}

			UDPPacket21UserAction.RESET_YAW -> {
				name = "Yaw reset"
				VRServer.instance.scheduleResetTrackersYaw(
					RESET_SOURCE_NAME,
					(VRServer.instance.configManager.vrConfig.resetsConfig.yawResetDelay * 1000).toLong(),
				)
			}

			UDPPacket21UserAction.RESET_MOUNTING -> {
				name = "Mounting reset"
				VRServer.instance.scheduleResetTrackersMounting(
					RESET_SOURCE_NAME,
					(VRServer.instance.configManager.vrConfig.resetsConfig.mountingResetDelay * 1000).toLong(),
				)
			}

			UDPPacket21UserAction.PAUSE_TRACKING -> {
				name = "Pause tracking toggle"
				VRServer.instance.togglePauseTracking(RESET_SOURCE_NAME)
			}
		}
		LogManager.info(
			"[TrackerServer] User action from ${connection.descriptiveName} received. $name performed.",
		)
	}

	private fun handleAckConfigChange(packet: UDPPacket24AckConfigChange, connection: UDPDevice?) {
		if (connection == null) return
		val queue = queues[Triple(connection.address, packet.configType, packet.sensorId)] ?: run {
			LogManager.severe("[TrackerServer] Error, acknowledgment of config change that we don't have in our queue.")
			return
		}
		val changed = queue.removeFirst()
		changed.channel.resume(true)
		val trackers = if (SensorSpecificPacket.isGlobal(packet.sensorId)) {
			connection.trackers.values.toList()
		} else {
			listOf(connection.getTracker(packet.sensorId) ?: return)
		}
		LogManager.info(
			"[TrackerServer] Acknowledged config change on ${connection.descriptiveName} "
			+ "(${trackers.map { it.trackerNum }.joinToString()}). Config changed on ${packet.configType}",
		)
	}

	// endregion

	fun getConnections(): List<UDPDevice> = connections

	// FIXME: for some reason it ends up disconnecting after 30 seconds have passed instead of immediately
	fun disconnectDevice(device: UDPDevice) {
		synchronized(connections) {
			connections.remove(device)
			connectionsByAddress.keys.removeIf { connectionsByAddress[it]?.id == device.id }
			connectionsByMAC.keys.removeIf { connectionsByMAC[it]?.id == device.id }
		}
		deviceChannels.remove(device.id)?.close()
		device.trackers.forEach { (_, tracker) ->
			tracker.status = TrackerStatus.DISCONNECTED
		}
		if (connections.none { it.trackers.isNotEmpty() }) {
			startDiscovery()
		}

		LogManager.info(
			"[TrackerServer] Forcefully disconnected ${device.hardwareIdentifier} device.",
		)
	}

	companion object {
		/**
		 * Change between IMU axes and OpenGL/SteamVR axes
		 */
		private val AXES_OFFSET = fromRotationVector(-FastMath.HALF_PI, 0f, 0f)

		// TODO: Set this offset to Quaternion.IDENTITY when the firmware is corrected!
		// 270 deg (-90 deg) default for officials
		private val SENSOR_OFFSET_CORRECTION = Quaternion.rotationAroundZAxis(-FastMath.HALF_PI)
		private const val RESET_SOURCE_NAME = "TrackerServer"
	}
}
