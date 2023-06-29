package dev.slimevr.tracking.trackers.udp

import com.jme3.math.FastMath
import dev.slimevr.NetworkProtocol
import dev.slimevr.VRServer
import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.TrackerStatus
import dev.slimevr.vrServer
import io.eiren.util.Util
import io.eiren.util.collections.FastList
import io.eiren.util.logging.LogManager
import io.github.axisangles.ktmath.Quaternion.Companion.fromRotationVector
import org.apache.commons.lang3.ArrayUtils
import solarxr_protocol.rpc.ResetType
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.SocketTimeoutException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import java.util.function.Consumer

/**
 * Receives trackers data by UDP using extended owoTrack protocol.
 */
class TrackersUDPServer(private val port: Int, name: String, private val trackersConsumer: Consumer<Tracker>) :
	Thread(name) {
	private val random = Random()
	private val connections: MutableList<UDPDevice> = FastList()
	private val connectionsByAddress: MutableMap<InetAddress, UDPDevice> = HashMap()
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
		}.filterNotNull().map { InetSocketAddress(it, this.port) }.toList()
	} catch (e: Exception) {
		LogManager.severe("[TrackerServer] Can't enumerate network interfaces", e)
		emptyList()
	}
	private val parser = UDPProtocolParser()
	private val rcvBuffer = ByteArray(512)
	private val bb = ByteBuffer.wrap(rcvBuffer).order(ByteOrder.BIG_ENDIAN)

	// Gets initialized in this.run()
	private lateinit var socket: DatagramSocket
	private var lastKeepup = System.currentTimeMillis()

	private fun setUpNewConnection(handshakePacket: DatagramPacket, handshake: UDPPacket3Handshake) {
		LogManager.info("[TrackerServer] Handshake received from ${handshakePacket.address}:${handshakePacket.port}")
		val addr = handshakePacket.address

		val connection: UDPDevice = synchronized(connections) { connectionsByAddress[addr] } ?: run {
			val connection = UDPDevice(
				handshakePacket.socketAddress,
				addr,
				handshake.macString ?: addr.hostAddress,
				handshake.boardType,
				handshake.mcuType
			)
			vrServer.deviceManager.addDevice(connection)
			connection.firmwareBuild = handshake.firmwareBuild
			connection.protocol = if (handshake.firmware?.isEmpty() == true) {
				// Only old owoTrack doesn't report firmware and have different packet IDs with SlimeVR
				NetworkProtocol.OWO_LEGACY
			} else {
				NetworkProtocol.SLIMEVR_RAW
			}
			connection.name = handshake.macString?.let { "udp://$it" }
				?: "udp:/${handshakePacket.address}"
			// TODO: The missing slash in udp:// was intended because InetAddress.toString()
			// 		returns "hostname/address" but it wasn't known that if hostname is empty
			// 		string it just looks like "/address" lol.
			// 		Fixing this would break config!
			connection.descriptiveName = "udp:/${handshakePacket.address}"
			synchronized(connections) {
				if (handshake.macString != null && connectionsByMAC.containsKey(handshake.macString)) {
					val previousConnection = connectionsByMAC[handshake.macString]!!
					val i = connections.indexOf(previousConnection)
					connectionsByAddress.remove(previousConnection.ipAddress)
					previousConnection.lastPacketNumber = 0
					previousConnection.ipAddress = addr
					previousConnection.address = handshakePacket.socketAddress
					previousConnection.name = connection.name
					previousConnection.descriptiveName = connection.descriptiveName
					connectionsByAddress[addr] = previousConnection
					LogManager
						.info(
							"""
							[TrackerServer] Tracker $i handed over to address ${handshakePacket.socketAddress}.
							Board type: ${handshake.boardType},
							imu type: ${handshake.imuType},
							firmware: ${handshake.firmware} (${connection.firmwareBuild}),
							mac: ${handshake.macString},
							name: ${previousConnection.name}
							""".trimIndent()
						)
				} else {
					val i = connections.size
					connections.add(connection)
					connectionsByAddress[addr] = connection
					if (handshake.macString != null) {
						connectionsByMAC[handshake.macString!!] = connection
					}
					LogManager
						.info(
							"""
							[TrackerServer] Tracker $i handed over to address ${handshakePacket.socketAddress}.
							Board type: ${handshake.boardType},
							imu type: ${handshake.imuType},
							firmware: ${handshake.firmware} (${connection.firmwareBuild}),
							mac: ${handshake.macString},
							name: ${connection.name}
							""".trimIndent()
						)
				}
			}
			if (connection.protocol == NetworkProtocol.OWO_LEGACY || connection.firmwareBuild < 9) {
				// Set up new sensor for older firmware.
				// Firmware after 7 should send sensor status packet and sensor
				// will be created when it's received
				setUpSensor(connection, 0, handshake.imuType, 1)
			}
			connection
		}
		bb.limit(bb.capacity())
		bb.rewind()
		parser.writeHandshakeResponse(bb, connection)
		socket.send(DatagramPacket(rcvBuffer, bb.position(), connection.address))
	}

	private fun setUpSensor(connection: UDPDevice, trackerId: Int, sensorType: IMUType, sensorStatus: Int) {
		LogManager.info("[TrackerServer] Sensor $trackerId for ${connection.name} status: $sensorStatus")
		var imuTracker = connection.getTracker(trackerId)
		if (imuTracker == null) {
			imuTracker = Tracker(
				connection,
				VRServer.getNextLocalTrackerId(),
				connection.name + "/" + trackerId,
				"IMU Tracker #" + VRServer.getCurrentLocalTrackerId(),
				null,
				trackerNum = trackerId,
				hasRotation = true,
				hasAcceleration = true,
				userEditable = true,
				imuType = sensorType,
				allowFiltering = true,
				needsReset = true,
				needsMounting = true
			)
			connection.trackers[trackerId] = imuTracker
			trackersConsumer.accept(imuTracker)
			LogManager.info("[TrackerServer] Added sensor $trackerId for ${connection.name}, type $sensorType")
		}
		val status = UDPPacket15SensorInfo.getStatus(sensorStatus)
		if (status != null) imuTracker.status = status
	}

	override fun run() {
		val serialBuffer2 = StringBuilder()
		try {
			socket = DatagramSocket(port)
			var prevPacketTime = System.currentTimeMillis()
			socket.soTimeout = 250
			while (true) {
				var received: DatagramPacket? = null
				try {
					val hasActiveTrackers = connections.any { it.trackers.size > 0 }
					if (!hasActiveTrackers) {
						val discoveryPacketTime = System.currentTimeMillis()
						if (discoveryPacketTime - prevPacketTime >= 2000) {
							for (addr in broadcastAddresses) {
								bb.limit(bb.capacity())
								bb.rewind()
								parser.write(bb, null, UDPPacket0Heartbeat)
								socket.send(DatagramPacket(rcvBuffer, bb.position(), addr))
							}
							prevPacketTime = discoveryPacketTime
						}
					}
					received = DatagramPacket(rcvBuffer, rcvBuffer.size)
					socket.receive(received)
					bb.limit(received.length)
					bb.rewind()
					val connection = synchronized(connections) { connectionsByAddress[received.address] }
					val packet = parser.parse(bb, connection)
					packet?.let { processPacket(received, it, connection) }
				} catch (ignored: SocketTimeoutException) {
				} catch (e: Exception) {
					LogManager.warning(
						"[TrackerServer] Error parsing packet ${packetToString(received)}",
						e
					)
				}
				if (lastKeepup + 500 < System.currentTimeMillis()) {
					lastKeepup = System.currentTimeMillis()
					synchronized(connections) {
						for (conn in connections) {
							bb.limit(bb.capacity())
							bb.rewind()
							parser.write(bb, conn, UDPPacket1Heartbeat)
							socket.send(DatagramPacket(rcvBuffer, bb.position(), conn.address))
							if (conn.lastPacket + 1000 < System.currentTimeMillis()) {
								for (value in conn.trackers.values) {
									value.status = TrackerStatus.DISCONNECTED
								}
								if (!conn.timedOut) {
									conn.timedOut = true
									LogManager.info("[TrackerServer] Tracker timed out: $conn")
								}
							} else {
								conn.timedOut = false
								for (value in conn.trackers.values) {
									value.status = TrackerStatus.OK
								}
							}
							if (conn.serialBuffer.isNotEmpty() &&
								conn.lastSerialUpdate + 500L < System.currentTimeMillis()
							) {
								serialBuffer2
									.append('[')
									.append(conn.name)
									.append("] ")
									.append(conn.serialBuffer)
								println(serialBuffer2)
								serialBuffer2.setLength(0)
								conn.serialBuffer.setLength(0)
							}
							if (conn.lastPingPacketTime + 500 < System.currentTimeMillis()) {
								conn.lastPingPacketId = random.nextInt()
								conn.lastPingPacketTime = System.currentTimeMillis()
								bb.limit(bb.capacity())
								bb.rewind()
								bb.putInt(10)
								bb.putLong(0)
								bb.putInt(conn.lastPingPacketId)
								socket.send(DatagramPacket(rcvBuffer, bb.position(), conn.address))
							}
						}
					}
				}
			}
		} catch (e: Exception) {
			e.printStackTrace()
		} finally {
			Util.close(socket)
		}
	}

	private fun processPacket(received: DatagramPacket, packet: UDPPacket, connection: UDPDevice?) {
		val tracker: Tracker?
		when (packet) {
			is UDPPacket0Heartbeat, is UDPPacket1Heartbeat -> {}
			is UDPPacket3Handshake -> setUpNewConnection(received, packet)
			is RotationPacket -> {
				var rot = packet.rotation
				rot = AXES_OFFSET.times(rot)
				tracker = connection?.getTracker(packet.sensorId)
				if (tracker == null) return
				tracker.setRotation(rot)
				tracker.dataTick()
			}
			is UDPPacket17RotationData -> {
				tracker = connection?.getTracker(packet.sensorId)
				if (tracker == null) return
				var rot17 = packet.rotation
				rot17 = AXES_OFFSET.times(rot17)
				when (packet.dataType) {
					UDPPacket17RotationData.DATA_TYPE_NORMAL -> {
						tracker.setRotation(rot17)
						tracker.dataTick()
						// tracker.calibrationStatus = rotationData.calibrationInfo;
						// Not implemented in server
					}

					UDPPacket17RotationData.DATA_TYPE_CORRECTION -> {
// 						tracker.rotMagQuaternion.set(rot17);
// 						tracker.magCalibrationStatus = rotationData.calibrationInfo;
// 						tracker.hasNewCorrectionData = true;
						// Not implemented in server
					}
				}
			}
			is UDPPacket18MagnetometerAccuracy -> {}
			is UDPPacket4Acceleration -> {
				tracker = connection?.getTracker(packet.sensorId)
				if (tracker == null) return
				val acceleration = tracker.getRotation().sandwich(packet.acceleration)
				tracker.acceleration = acceleration
			}
			is UDPPacket10PingPong -> {
				if (connection == null) return
				if (connection.lastPingPacketId == packet.pingId) {
					for (t in connection.trackers.values) {
						t.ping = (System.currentTimeMillis() - connection.lastPingPacketTime).toInt() / 2
						t.dataTick()
					}
				} else {
					LogManager.debug(
						"[TrackerServer] Wrong ping id ${packet.pingId} != ${connection.lastPingPacketId}"
					)
				}
			}

			is UDPPacket11Serial -> {
				if (connection == null) return
				println("[${connection.name}] ${packet.serial}")
			}

			is UDPPacket12BatteryLevel -> connection?.trackers?.values?.forEach {
				it.batteryVoltage = packet.voltage
				it.batteryLevel = packet.level * 100
			}

			is UDPPacket13Tap -> {
				tracker = connection?.getTracker(packet.sensorId)
				if (tracker == null) return
				LogManager.info(
					"[TrackerServer] Tap packet received from ${tracker.name}: ${packet.tap}"
				)
			}

			is UDPPacket14Error -> {
				LogManager.severe(
					"[TrackerServer] Error received from ${received.socketAddress}: ${packet.errorNumber}"
				)
				tracker = connection?.getTracker(packet.sensorId)
				if (tracker == null) return
				tracker.status = TrackerStatus.ERROR
			}

			is UDPPacket15SensorInfo -> {
				if (connection == null) return
				setUpSensor(connection, packet.sensorId, packet.sensorType, packet.sensorStatus)
				// Send ack
				bb.limit(bb.capacity())
				bb.rewind()
				parser.writeSensorInfoResponse(bb, connection, packet)
				socket.send(DatagramPacket(rcvBuffer, bb.position(), connection.address))
				LogManager.info(
					"[TrackerServer] Sensor info for ${connection.descriptiveName}/${packet.sensorId}: ${packet.sensorStatus}"
				)
			}

			is UDPPacket19SignalStrength -> connection?.trackers?.values?.forEach {
				it.signalStrength = packet.signalStrength
			}

			is UDPPacket20Temperature -> {
				tracker = connection?.getTracker(packet.sensorId)
				if (tracker == null) return
				tracker.temperature = packet.temperature
			}

			is UDPPacket21UserAction -> {
				if (connection == null) return
				var name = ""
				when (packet.type) {
					UDPPacket21UserAction.RESET_FULL -> {
						name = "Full"
						vrServer.resetHandler.sendStarted(ResetType.Full)
						vrServer.resetTrackersFull(resetSourceName)
					}

					UDPPacket21UserAction.RESET_YAW -> {
						name = "Yaw"
						vrServer.resetHandler.sendStarted(ResetType.Yaw)
						vrServer.resetTrackersYaw(resetSourceName)
					}

					UDPPacket21UserAction.RESET_MOUNTING -> {
						name = "Mounting"
						vrServer
							.resetHandler
							.sendStarted(ResetType.Mounting)
						vrServer.resetTrackersMounting(resetSourceName)
					}
				}

				LogManager.info(
					"[TrackerServer] User action from ${connection.descriptiveName } received. $name reset performed."
				)
			}
			is UDPPacket200ProtocolChange -> {}
		}
	}

	fun getConnections(): List<UDPDevice?> {
		return connections
	}

	companion object {
		/**
		 * Change between IMU axes and OpenGL/SteamVR axes
		 */
		private val AXES_OFFSET = fromRotationVector(-FastMath.HALF_PI, 0f, 0f)
		private const val resetSourceName = "TrackerServer"
		private fun packetToString(packet: DatagramPacket?): String {
			val sb = StringBuilder()
			sb.append("DatagramPacket{")
			if (packet == null) {
				sb.append("null")
			} else {
				sb.append(packet.address.toString())
				sb.append(packet.port)
				sb.append(',')
				sb.append(packet.length)
				sb.append(',')
				sb.append(ArrayUtils.toString(packet.data))
			}
			sb.append('}')
			return sb.toString()
		}
	}
}
