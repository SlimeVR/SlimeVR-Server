package dev.slimevr.tracking.trackers.udp;

import com.jme3.math.FastMath;
import dev.slimevr.Main;
import dev.slimevr.NetworkProtocol;
import dev.slimevr.VRServer;
import dev.slimevr.tracking.trackers.Tracker;
import dev.slimevr.tracking.trackers.TrackerStatus;
import io.eiren.util.Util;
import io.eiren.util.collections.FastList;
import io.eiren.util.logging.LogManager;
import io.github.axisangles.ktmath.Quaternion;
import io.github.axisangles.ktmath.Vector3;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;
import java.util.function.Consumer;


/**
 * Receives trackers data by UDP using extended owoTrack protocol.
 */
public class TrackersUDPServer extends Thread {
	/**
	 * Change between IMU axes and OpenGL/SteamVR axes
	 */
	private static final Quaternion AXES_OFFSET = new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3.Companion.getPOS_X());


	private static final String resetSourceName = "TrackerServer";
	private final Random random = new Random();
	private final List<UDPDevice> connections = new FastList<>();
	private final Map<InetAddress, UDPDevice> connectionsByAddress = new HashMap<>();
	private final Map<String, UDPDevice> connectionsByMAC = new HashMap<>();
	private final Consumer<Tracker> trackersConsumer;
	private final int port;
	private final ArrayList<SocketAddress> broadcastAddresses = new ArrayList<>();
	private final UDPProtocolParser parser = new UDPProtocolParser();
	private final byte[] rcvBuffer = new byte[512];
	private final ByteBuffer bb = ByteBuffer.wrap(rcvBuffer).order(ByteOrder.BIG_ENDIAN);

	protected DatagramSocket socket = null;
	protected long lastKeepup = System.currentTimeMillis();

	public TrackersUDPServer(int port, String name, Consumer<Tracker> trackersConsumer) {
		super(name);
		this.port = port;
		this.trackersConsumer = trackersConsumer;
		try {
			Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
			while (ifaces.hasMoreElements()) {
				NetworkInterface iface = ifaces.nextElement();
				// Ignore loopback, PPP, virtual and disabled devices
				if (
					iface.isLoopback()
						|| !iface.isUp()
						|| iface.isPointToPoint()
						|| iface.isVirtual()
				) {
					continue;
				}
				Enumeration<InetAddress> iaddrs = iface.getInetAddresses();
				while (iaddrs.hasMoreElements()) {
					InetAddress iaddr = iaddrs.nextElement();
					// Ignore IPv6 addresses
					if (iaddr instanceof Inet6Address) {
						continue;
					}
					String[] iaddrParts = iaddr.getHostAddress().split("\\.");
					broadcastAddresses
						.add(
							new InetSocketAddress(
								String
									.format(
										"%s.%s.%s.255",
										iaddrParts[0],
										iaddrParts[1],
										iaddrParts[2]
									),
								port
							)
						);
				}
			}
		} catch (Exception e) {
			LogManager.severe("[TrackerServer] Can't enumerate network interfaces", e);
		}
	}

	private static String packetToString(DatagramPacket packet) {
		StringBuilder sb = new StringBuilder();
		sb.append("DatagramPacket{");
		if (packet == null) {
			sb.append("null");
		} else {
			sb.append(packet.getAddress().toString());
			sb.append(packet.getPort());
			sb.append(',');
			sb.append(packet.getLength());
			sb.append(',');
			sb.append(ArrayUtils.toString(packet.getData()));
		}
		sb.append('}');
		return sb.toString();
	}

	private void setUpNewConnection(DatagramPacket handshakePacket, UDPPacket3Handshake handshake)
		throws IOException {
		LogManager
			.info(
				"[TrackerServer] Handshake received from "
					+ handshakePacket.getAddress()
					+ ":"
					+ handshakePacket.getPort()
			);
		InetAddress addr = handshakePacket.getAddress();
		UDPDevice connection;
		synchronized (connections) {
			connection = connectionsByAddress.get(addr);
		}
		if (connection == null) {
			connection = new UDPDevice(handshakePacket.getSocketAddress(), addr);
			Main.getVrServer().getDeviceManager().addDevice(connection);
			connection.firmwareBuild = handshake.firmwareBuild;
			if (handshake.firmware == null || handshake.firmware.length() == 0) {
				// Only old owoTrack doesn't report firmware and have different
				// packet IDs with
				// SlimeVR
				connection.protocol = NetworkProtocol.OWO_LEGACY;
			} else {
				connection.protocol = NetworkProtocol.SLIMEVR_RAW;
			}
			connection
				.setName(
					handshake.macString != null
						? "udp://" + handshake.macString
						: "udp:/"
							+ handshakePacket.getAddress().toString()
				);
			connection.descriptiveName = "udp:/" + handshakePacket.getAddress().toString();
			int i = 0;
			synchronized (connections) {
				if (
					handshake.macString != null && connectionsByMAC.containsKey(handshake.macString)
				) {
					UDPDevice previousConnection = connectionsByMAC.get(handshake.macString);
					i = connections.indexOf(previousConnection);
					connectionsByAddress.remove(previousConnection.getIpAddress());
					previousConnection.lastPacketNumber = 0;
					previousConnection.setIpAddress(addr);
					previousConnection.setAddress(handshakePacket.getSocketAddress());
					previousConnection.setName(connection.getName());
					previousConnection.descriptiveName = connection.descriptiveName;
					connectionsByAddress.put(addr, previousConnection);
					LogManager
						.info(
							"[TrackerServer] Tracker "
								+ i
								+ " handed over to address "
								+ handshakePacket.getSocketAddress()
								+ ". Board type: "
								+ handshake.boardType
								+ ", imu type: "
								+ handshake.imuType
								+ ", firmware: "
								+ handshake.firmware
								+ " ("
								+ connection.firmwareBuild
								+ "), mac: "
								+ handshake.macString
								+ ", name: "
								+ previousConnection.getName()
						);
				} else {
					i = connections.size();
					connections.add(connection);
					connectionsByAddress.put(addr, connection);
					if (handshake.macString != null) {
						connectionsByMAC.put(handshake.macString, connection);
					}
					LogManager
						.info(
							"[TrackerServer] Tracker "
								+ i
								+ " added with address "
								+ handshakePacket.getSocketAddress()
								+ ". Board type: "
								+ handshake.boardType
								+ ", imu type: "
								+ handshake.imuType
								+ ", firmware: "
								+ handshake.firmware
								+ " ("
								+ connection.firmwareBuild
								+ "), mac: "
								+ handshake.macString
								+ ", name: "
								+ connection.getName()
						);
				}
			}
			if (connection.protocol == NetworkProtocol.OWO_LEGACY || connection.firmwareBuild < 9) {
				// Set up new sensor for older firmware.
				// Firmware after 7 should send sensor status packet and sensor
				// will be created when it's received
				setUpSensor(connection, 0, handshake.imuType, 1);
			}
		}
		bb.limit(bb.capacity());
		bb.rewind();
		parser.writeHandshakeResponse(bb, connection);
		socket.send(new DatagramPacket(rcvBuffer, bb.position(), connection.getAddress()));
	}

	private void setUpSensor(UDPDevice connection, int trackerId, int sensorType, int sensorStatus)
		throws IOException {
		LogManager
			.info(
				"[TrackerServer] Sensor "
					+ trackerId
					+ " for "
					+ connection.getName()
					+ " status: "
					+ sensorStatus
			);
		Tracker imuTracker = connection.getTracker(trackerId);
		if (imuTracker == null) {
			imuTracker = new Tracker(
				connection,
				VRServer.getNextLocalTrackerId(),
				connection.getName() + "/" + trackerId,
				null,
				false,
				true,
				true,
				true,
				false,
				false,
				true,
				false,
				false,
				true,
				true,
				true,
				true,
				true
			);

			connection.getTrackers().put(trackerId, imuTracker);
			trackersConsumer.accept(imuTracker);
			LogManager
				.info(
					"[TrackerServer] Added sensor "
						+ trackerId
						+ " for "
						+ connection.getName()
						+ ", type "
						+ sensorType
				);
		}
		TrackerStatus status = UDPPacket15SensorInfo.getStatus(sensorStatus);
		if (status != null)
			imuTracker.setStatus(status);
	}

	@Override
	public void run() {
		StringBuilder serialBuffer2 = new StringBuilder();
		try {
			socket = new DatagramSocket(port);

			long prevPacketTime = System.currentTimeMillis();
			socket.setSoTimeout(250);
			while (true) {
				DatagramPacket received = null;
				try {
					boolean hasActiveTrackers = false;
					for (UDPDevice tracker : connections) {
						if (tracker.getTrackers().size() > 0) {
							hasActiveTrackers = true;
							break;
						}
					}
					if (!hasActiveTrackers) {
						long discoveryPacketTime = System.currentTimeMillis();
						if ((discoveryPacketTime - prevPacketTime) >= 2000) {
							for (SocketAddress addr : broadcastAddresses) {
								bb.limit(bb.capacity());
								bb.rewind();
								parser.write(bb, null, new UDPPacket0Heartbeat());
								socket.send(new DatagramPacket(rcvBuffer, bb.position(), addr));
							}
							prevPacketTime = discoveryPacketTime;
						}
					}

					received = new DatagramPacket(rcvBuffer, rcvBuffer.length);
					socket.receive(received);
					bb.limit(received.getLength());
					bb.rewind();

					UDPDevice connection;

					synchronized (connections) {
						connection = connectionsByAddress.get(received.getAddress());
					}
					UDPPacket packet = parser.parse(bb, connection);
					if (packet != null) {
						processPacket(received, packet, connection);
					}
				} catch (SocketTimeoutException ignored) {} catch (Exception e) {
					LogManager
						.warning(
							"[TrackerServer] Error parsing packet " + packetToString(received),
							e
						);
				}
				if (lastKeepup + 500 < System.currentTimeMillis()) {
					lastKeepup = System.currentTimeMillis();
					synchronized (connections) {
						for (UDPDevice conn : connections) {
							bb.limit(bb.capacity());
							bb.rewind();
							parser.write(bb, conn, new UDPPacket1Heartbeat());
							socket
								.send(
									new DatagramPacket(rcvBuffer, bb.position(), conn.getAddress())
								);
							if (conn.lastPacket + 1000 < System.currentTimeMillis()) {
								for (Tracker value : conn.getTrackers().values()) {
									if (value.getStatus() != TrackerStatus.DISCONNECTED)
										value.setStatus(TrackerStatus.DISCONNECTED);
								}
								if (!conn.timedOut) {
									conn.timedOut = true;
									LogManager.info("[TrackerServer] Tracker timed out: " + conn);
								}
							} else {
								conn.timedOut = false;
								for (Tracker value : conn.getTrackers().values()) {
									if (value.getStatus() == TrackerStatus.DISCONNECTED)
										value.setStatus(TrackerStatus.OK);
								}
							}
							if (conn.serialBuffer.length() > 0) {
								if (conn.lastSerialUpdate + 500L < System.currentTimeMillis()) {
									serialBuffer2
										.append('[')
										.append(conn.getName())
										.append("] ")
										.append(conn.serialBuffer);
									System.out.println(serialBuffer2);
									serialBuffer2.setLength(0);
									conn.serialBuffer.setLength(0);
								}
							}
							if (conn.lastPingPacketTime + 500 < System.currentTimeMillis()) {
								conn.lastPingPacketId = random.nextInt();
								conn.lastPingPacketTime = System.currentTimeMillis();
								bb.limit(bb.capacity());
								bb.rewind();
								bb.putInt(10);
								bb.putLong(0);
								bb.putInt(conn.lastPingPacketId);
								socket
									.send(
										new DatagramPacket(
											rcvBuffer,
											bb.position(),
											conn.getAddress()
										)
									);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Util.close(socket);
		}
	}

	protected void processPacket(DatagramPacket received, UDPPacket packet, UDPDevice connection)
		throws IOException {
		Tracker tracker = null;
		switch (packet.getPacketId()) {
			case UDPProtocolParser.PACKET_HEARTBEAT:
				break;
			case UDPProtocolParser.PACKET_HANDSHAKE:
				setUpNewConnection(received, (UDPPacket3Handshake) packet);
				break;
			case UDPProtocolParser.PACKET_ROTATION:
			case UDPProtocolParser.PACKET_ROTATION_2:
				if (connection == null)
					break;
				UDPPacket1Rotation rotationPacket = (UDPPacket1Rotation) packet;
				Quaternion rot = rotationPacket.rotation;
				rot = AXES_OFFSET.times(rot);
				tracker = connection.getTracker(rotationPacket.getSensorId());
				if (tracker == null)
					break;
				tracker.setRotation(rot);
				tracker.dataTick();
				break;
			case UDPProtocolParser.PACKET_ROTATION_DATA:
				if (connection == null)
					break;
				UDPPacket17RotationData rotationData = (UDPPacket17RotationData) packet;
				tracker = connection.getTracker(rotationData.getSensorId());
				if (tracker == null)
					break;
				Quaternion rot17 = rotationData.rotation;
				rot17 = AXES_OFFSET.times(rot17);

				switch (rotationData.dataType) {
					case UDPPacket17RotationData.DATA_TYPE_NORMAL -> {
						tracker.setRotation(rot17);
						tracker.dataTick();
//						tracker.calibrationStatus = rotationData.calibrationInfo;
						// Not implemented in server
					}
					case UDPPacket17RotationData.DATA_TYPE_CORRECTION -> {
//						tracker.rotMagQuaternion.set(rot17);
//						tracker.magCalibrationStatus = rotationData.calibrationInfo;
//						tracker.hasNewCorrectionData = true;
						// Not implemented in server
					}
				}
				break;
			case UDPProtocolParser.PACKET_MAGNETOMETER_ACCURACY:
//				if (connection == null)
//					break;
//				UDPPacket18MagnetometerAccuracy magAccuracy = (UDPPacket18MagnetometerAccuracy) packet;
//				tracker = connection.getTracker(magAccuracy.getSensorId());
//				if (tracker == null)
//					break;
//				tracker.magnetometerAccuracy = magAccuracy.accuracyInfo;
				// Not implemented in server
				break;

			case UDPProtocolParser.PACKET_ACCEL:
				if (connection == null)
					break;

				UDPPacket4Acceleration accelPacket = (UDPPacket4Acceleration) packet;
				tracker = connection.getTracker(accelPacket.getSensorId());

				if (tracker == null)
					break;

				Vector3 acceleration = tracker.getRotation().sandwich(accelPacket.acceleration);
				tracker.setAcceleration(acceleration);
				break;

			case 2: // PACKET_GYRO
			case 5: // PACKET_MAG
			case 9: // PACKET_RAW_MAGNETOMETER
				break; // None of these packets are used by SlimeVR trackers and
						// are deprecated, use more generic PACKET_ROTATION_DATA
			case 8: // PACKET_CONFIG
				if (connection == null)
					break;
				break;
			case UDPProtocolParser.PACKET_PING_PONG: // PACKET_PING_PONG:
				if (connection == null)
					break;
				UDPPacket10PingPong ping = (UDPPacket10PingPong) packet;
				if (connection.lastPingPacketId == ping.pingId) {
					for (Tracker t : connection.getTrackers().values()) {
						t
							.setPing(
								(int) (System.currentTimeMillis() - connection.lastPingPacketTime)
									/ 2
							);
						t.dataTick();
					}
				} else {
					LogManager
						.debug(
							"[TrackerServer] Wrong ping id "
								+ ping.pingId
								+ " != "
								+ connection.lastPingPacketId
						);
				}
				break;
			case UDPProtocolParser.PACKET_SERIAL:
				if (connection == null)
					break;
				UDPPacket11Serial serial = (UDPPacket11Serial) packet;
				System.out.println("[" + connection.getName() + "] " + serial.serial);
				break;
			case UDPProtocolParser.PACKET_BATTERY_LEVEL:
				if (connection == null)
					break;
				UDPPacket12BatteryLevel battery = (UDPPacket12BatteryLevel) packet;

				if (connection.getTrackers().size() > 0) {

					for (Tracker value : connection.getTrackers().values()) {
						value.setBatteryVoltage(battery.voltage);
						value.setBatteryLevel(battery.level * 100);
					}
				}
				break;
			case UDPProtocolParser.PACKET_TAP:
				if (connection == null)
					break;
				UDPPacket13Tap tap = (UDPPacket13Tap) packet;
				tracker = connection.getTracker(tap.getSensorId());
				if (tracker == null)
					break;
				LogManager
					.info(
						"[TrackerServer] Tap packet received from "
							+ tracker.getName()
							+ ": "
							+ tap.tap
					);
				break;
			case UDPProtocolParser.PACKET_ERROR:
				UDPPacket14Error error = (UDPPacket14Error) packet;
				LogManager
					.severe(
						"[TrackerServer] Error received from "
							+ received.getSocketAddress()
							+ ": "
							+ error.errorNumber
					);
				if (connection == null)
					break;
				tracker = connection.getTracker(error.getSensorId());
				if (tracker == null)
					break;
				tracker.setStatus(TrackerStatus.ERROR);
				break;
			case UDPProtocolParser.PACKET_SENSOR_INFO:
				if (connection == null)
					break;
				UDPPacket15SensorInfo info = (UDPPacket15SensorInfo) packet;
				setUpSensor(connection, info.getSensorId(), info.sensorType, info.sensorStatus);
				// Send ack
				bb.limit(bb.capacity());
				bb.rewind();
				parser.writeSensorInfoResponse(bb, connection, info);
				socket.send(new DatagramPacket(rcvBuffer, bb.position(), connection.getAddress()));
				LogManager
					.info(
						"[TrackerServer] Sensor info for "
							+ connection.descriptiveName
							+ "/"
							+ info.getSensorId()
							+ ": "
							+ info.sensorStatus
					);
				break;
			case UDPProtocolParser.PACKET_SIGNAL_STRENGTH:
				if (connection == null)
					break;
				UDPPacket19SignalStrength signalStrength = (UDPPacket19SignalStrength) packet;

				if (connection.getTrackers().size() > 0) {
					for (Tracker value : connection.getTrackers().values()) {
						value.setSignalStrength(signalStrength.signalStrength);
					}
				}
				break;
			case UDPProtocolParser.PACKET_TEMPERATURE:
				if (connection == null)
					break;
				UDPPacket20Temperature temp = (UDPPacket20Temperature) packet;
				tracker = connection.getTracker(temp.getSensorId());
				if (tracker == null)
					break;
				tracker.setTemperature(temp.temperature);
				break;
			case UDPProtocolParser.PACKET_USER_ACTION:
				if (connection == null)
					break;
				UDPPacket21UserAction action = (UDPPacket21UserAction) packet;
				switch (action.type) {
					case UDPPacket21UserAction.RESET_FULL:
					case UDPPacket21UserAction.RESET_YAW:
					case UDPPacket21UserAction.RESET_MOUNTING:
						String name = "";
						switch (action.type) {
							case UDPPacket21UserAction.RESET -> {
								name = "Full";
								Main.getVrServer().resetTrackersFull(resetSourceName);
							}
							case UDPPacket21UserAction.RESET_YAW -> {
								name = "Yaw";
								Main.getVrServer().resetTrackersYaw(resetSourceName);
							}
							case UDPPacket21UserAction.RESET_MOUNTING -> {
								name = "Mounting";
								Main.getVrServer().resetTrackersMounting(resetSourceName);
							}
						}
						LogManager
							.info(
								"[TrackerServer] User action from "
									+ connection.descriptiveName
									+ " received. "
									+ name
									+ " reset performed."
							);
						break;
				}
				break;
			default:
				LogManager.warning("[TrackerServer] Skipped packet " + packet);
				break;
		}
	}

	public List<UDPDevice> getConnections() {
		return connections;
	}
}
