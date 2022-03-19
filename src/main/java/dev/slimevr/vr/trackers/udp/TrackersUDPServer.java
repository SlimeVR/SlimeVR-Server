package dev.slimevr.vr.trackers.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

import org.apache.commons.lang3.ArrayUtils;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import dev.slimevr.Main;
import dev.slimevr.NetworkProtocol;
import dev.slimevr.vr.trackers.IMUTracker;
import dev.slimevr.vr.trackers.ReferenceAdjustedTracker;
import dev.slimevr.vr.trackers.Tracker;
import dev.slimevr.vr.trackers.TrackerStatus;
import io.eiren.util.Util;
import io.eiren.util.collections.FastList;
import io.eiren.util.logging.LogManager;

/**
 * Receives trackers data by UDP using extended owoTrack protocol.
 */
public class TrackersUDPServer extends Thread {
	
	/**
	 * Change between IMU axes and OpenGL/SteamVR axes
	 */
	private static final Quaternion offset = new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X);
	
	private final Quaternion buf = new Quaternion();
	private final Random random = new Random();
	private final List<TrackerUDPConnection> connections = new FastList<>();
	private final Map<InetAddress, TrackerUDPConnection> connectionsByAddress = new HashMap<>();
	private final Map<String, TrackerUDPConnection> connectionsByMAC = new HashMap<>();
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
		while(ifaces.hasMoreElements()) {
			NetworkInterface iface = ifaces.nextElement();
			// Ignore loopback, PPP, virtual and disabled devices
			if(iface.isLoopback() || !iface.isUp() || iface.isPointToPoint() || iface.isVirtual()) {
				continue;
			}
			Enumeration<InetAddress> iaddrs = iface.getInetAddresses();
			while(iaddrs.hasMoreElements()) {
				InetAddress iaddr = iaddrs.nextElement();
				// Ignore IPv6 addresses
				if(iaddr instanceof Inet6Address) {
					continue;
				}
				String[] iaddrParts = iaddr.getHostAddress().split("\\.");
				broadcastAddresses.add(new InetSocketAddress(String.format("%s.%s.%s.255", iaddrParts[0], iaddrParts[1], iaddrParts[2]), port));
			}
		}
		} catch(Exception e) {
			LogManager.log.severe("[TrackerServer] Can't enumerate network interfaces", e);
		}
	}
	
	private void setUpNewConnection(DatagramPacket handshakePacket, UDPPacket3Handshake handshake) throws IOException {
		LogManager.log.info("[TrackerServer] Handshake received from " + handshakePacket.getAddress() + ":" + handshakePacket.getPort());
		InetAddress addr = handshakePacket.getAddress();
		TrackerUDPConnection connection;
		synchronized(connections) {
			connection = connectionsByAddress.get(addr);
		}
		if(connection == null) {
			connection = new TrackerUDPConnection(handshakePacket.getSocketAddress(), addr);
			connection.firmwareBuild = handshake.firmwareBuild;
			if(handshake.firmware == null || handshake.firmware.length() == 0) {
				// Only old owoTrack doesn't report firmware and have different packet IDs with SlimeVR
				connection.protocol = NetworkProtocol.OWO_LEGACY;
			} else {
				connection.protocol = NetworkProtocol.SLIMEVR_RAW;
			}
			connection.name = handshake.macString != null ? "udp://" + handshake.macString : "udp:/" + handshakePacket.getAddress().toString();
			connection.descriptiveName = "udp:/" + handshakePacket.getAddress().toString();
			int i = 0;
			synchronized(connections) {
				if(handshake.macString != null && connectionsByMAC.containsKey(handshake.macString)) {
					TrackerUDPConnection previousConnection = connectionsByMAC.get(handshake.macString);
					i = connections.indexOf(previousConnection);
					connectionsByAddress.remove(previousConnection.ipAddress);
					previousConnection.lastPacketNumber = 0;
					previousConnection.ipAddress = addr;
					previousConnection.address = handshakePacket.getSocketAddress();
					previousConnection.name = connection.name;
					previousConnection.descriptiveName = connection.descriptiveName;
					connectionsByAddress.put(addr, previousConnection);
					LogManager.log.info("[TrackerServer] Tracker " + i + " handed over to address " + handshakePacket.getSocketAddress() + ". Board type: " + handshake.boardType + ", imu type: " + handshake.imuType + ", firmware: " + handshake.firmware + " (" + connection.firmwareBuild + "), mac: " + handshake.macString + ", name: " + previousConnection.name);
				} else {
					i = connections.size();
					connections.add(connection);
					connectionsByAddress.put(addr, connection);
					if(handshake.macString != null) {
						connectionsByMAC.put(handshake.macString, connection);
					}
					LogManager.log.info("[TrackerServer] Tracker " + i + " added with address " + handshakePacket.getSocketAddress() + ". Board type: " + handshake.boardType + ", imu type: " + handshake.imuType + ", firmware: " + handshake.firmware + " (" + connection.firmwareBuild + "), mac: " + handshake.macString + ", name: " + connection.name);
				}
			}
			if(connection.protocol == NetworkProtocol.OWO_LEGACY || connection.firmwareBuild < 9) {
				// Set up new sensor for older firmware
				// Firmware after 7 should send sensor status packet and sensor will be created when it's received
				setUpSensor(connection, 0, handshake.imuType, 1);
			}
		}
		bb.limit(bb.capacity());
		bb.rewind();
		parser.writeHandshakeResponse(bb, connection);
		socket.send(new DatagramPacket(rcvBuffer, bb.position(), connection.address));
	}
	
	private void setUpSensor(TrackerUDPConnection connection, int trackerId, int sensorType, int sensorStatus) throws IOException {
		LogManager.log.info("[TrackerServer] Sensor " + trackerId + " for " + connection.name + " status: " + sensorStatus);
		IMUTracker imu = connection.sensors.get(trackerId);
		if(imu == null) {
			imu = new IMUTracker(Tracker.getNextLocalTrackerId(), connection.name + "/" + trackerId, connection.descriptiveName + "/" + trackerId, this, Main.vrServer);
			connection.sensors.put(trackerId, imu);
			ReferenceAdjustedTracker<IMUTracker> adjustedTracker = new ReferenceAdjustedTracker<>(imu);
			trackersConsumer.accept(adjustedTracker);
			LogManager.log.info("[TrackerServer] Added sensor " + trackerId + " for " + connection.name + ", type " + sensorType);
		}
		TrackerStatus status = UDPPacket15SensorInfo.getStatus(sensorStatus);
		if(status != null)
			imu.setStatus(status);
	}
	
	@Override
	public void run() {
		StringBuilder serialBuffer2 = new StringBuilder();
		try {
			socket = new DatagramSocket(port);
			
			long prevPacketTime = System.currentTimeMillis();
			socket.setSoTimeout(250);
			while(true) {
				DatagramPacket received = null;
				try {
					boolean hasActiveTrackers = false;
					for(TrackerUDPConnection tracker : connections) {
						if(tracker.sensors.size() > 0) {
							hasActiveTrackers = true;
							break;
						}
					}
					if(!hasActiveTrackers) {
						long discoveryPacketTime = System.currentTimeMillis();
						if((discoveryPacketTime - prevPacketTime) >= 2000) {
							for(SocketAddress addr : broadcastAddresses) {
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
					
					TrackerUDPConnection connection;
					
					synchronized(connections) {
						connection = connectionsByAddress.get(received.getAddress());
					}
					UDPPacket packet = parser.parse(bb, connection);
					if(packet != null) {
						processPacket(received, packet, connection);
					}
				} catch(SocketTimeoutException e) {
				} catch(Exception e) {
					LogManager.log.warning("[TrackerServer] Error parsing packet " + packetToString(received), e);
				}
				if(lastKeepup + 500 < System.currentTimeMillis()) {
					lastKeepup = System.currentTimeMillis();
					synchronized(connections) {
						for(int i = 0; i < connections.size(); ++i) {
							TrackerUDPConnection conn = connections.get(i);
							bb.limit(bb.capacity());
							bb.rewind();
							parser.write(bb, conn, new UDPPacket1Heartbeat());
							socket.send(new DatagramPacket(rcvBuffer, bb.position(), conn.address));
							if(conn.lastPacket + 1000 < System.currentTimeMillis()) {
								Iterator<IMUTracker> iterator = conn.sensors.values().iterator();
								while(iterator.hasNext()) {
									IMUTracker tracker = iterator.next();
									if(tracker.getStatus() == TrackerStatus.OK)
										tracker.setStatus(TrackerStatus.DISCONNECTED);
								}
								if(!conn.timedOut) {
									conn.timedOut = true;
									LogManager.log.info("[TrackerServer] Tracker timed out: " + conn);
								}
							} else {
								conn.timedOut = false;
								Iterator<IMUTracker> iterator = conn.sensors.values().iterator();
								while(iterator.hasNext()) {
									IMUTracker tracker = iterator.next();
									if(tracker.getStatus() == TrackerStatus.DISCONNECTED)
										tracker.setStatus(TrackerStatus.OK);
								}
							}
							if(conn.serialBuffer.length() > 0) {
								if(conn.lastSerialUpdate + 500L < System.currentTimeMillis()) {
									serialBuffer2.append('[').append(conn.name).append("] ").append(conn.serialBuffer);
									System.out.println(serialBuffer2.toString());
									serialBuffer2.setLength(0);
									conn.serialBuffer.setLength(0);
								}
							}
							if(conn.lastPingPacketTime + 500 < System.currentTimeMillis()) {
								conn.lastPingPacketId = random.nextInt();
								conn.lastPingPacketTime = System.currentTimeMillis();
								bb.limit(bb.capacity());
								bb.rewind();
								bb.putInt(10);
								bb.putLong(0);
								bb.putInt(conn.lastPingPacketId);
								socket.send(new DatagramPacket(rcvBuffer, bb.position(), conn.address));
							}
						}
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			Util.close(socket);
		}
	}
	
	protected void processPacket(DatagramPacket received, UDPPacket packet, TrackerUDPConnection connection) throws IOException {
		IMUTracker tracker = null;
		switch(packet.getPacketId()) {
		case UDPProtocolParser.PACKET_HEARTBEAT:
			break;
		case UDPProtocolParser.PACKET_HANDSHAKE:
			setUpNewConnection(received, (UDPPacket3Handshake) packet);
			break;
		case UDPProtocolParser.PACKET_ROTATION:
		case UDPProtocolParser.PACKET_ROTATION_2:
			if(connection == null)
				break;
			UDPPacket1Rotation rotationPacket = (UDPPacket1Rotation) packet;
			buf.set(rotationPacket.rotation);
			offset.mult(buf, buf);
			tracker = connection.sensors.get(rotationPacket.getSensorId());
			if(tracker == null)
				break;
			tracker.rotQuaternion.set(buf);
			tracker.dataTick();
			break;
		case UDPProtocolParser.PACKET_ROTATION_DATA:
			if(connection == null)
				break;
			UDPPacket17RotationData rotationData = (UDPPacket17RotationData) packet;
			tracker = connection.sensors.get(rotationData.getSensorId());
			if(tracker == null)
				break;
			buf.set(rotationData.rotation);
			offset.mult(buf, buf);
			
			switch(rotationData.dataType) {
			case UDPPacket17RotationData.DATA_TYPE_NORMAL:
				tracker.rotQuaternion.set(buf);
				tracker.calibrationStatus = rotationData.calibrationInfo;
				tracker.dataTick();
				break;
			case UDPPacket17RotationData.DATA_TYPE_CORRECTION:
				tracker.rotMagQuaternion.set(buf);
				tracker.magCalibrationStatus = rotationData.calibrationInfo;
				tracker.hasNewCorrectionData = true;
				break;
			}
			break;
		case UDPProtocolParser.PACKET_MAGNETOMETER_ACCURACY:
			if(connection == null)
				break;
			UDPPacket18MagnetometerAccuracy magAccuracy = (UDPPacket18MagnetometerAccuracy) packet;
			tracker = connection.sensors.get(magAccuracy.getSensorId());
			if(tracker == null)
				break;
			tracker.magnetometerAccuracy = magAccuracy.accuracyInfo;
			break;
		case 2: // PACKET_GYRO
		case 4: // PACKET_ACCEL
		case 5: // PACKET_MAG
		case 9: // PACKET_RAW_MAGENTOMETER
			break; // None of these packets are used by SlimeVR trackers and are deprecated, use more generic PACKET_ROTATION_DATA
		case 8: // PACKET_CONFIG
			if(connection == null)
				break;
			break;
		case UDPProtocolParser.PACKET_PING_PONG: // PACKET_PING_PONG:
			if(connection == null)
				break;
			UDPPacket10PingPong ping = (UDPPacket10PingPong) packet;
			if(connection.lastPingPacketId == ping.pingId) {
				for(int i = 0; i < connection.sensors.size(); ++i) {
					tracker = connection.sensors.get(i);
					tracker.ping = (int) (System.currentTimeMillis() - connection.lastPingPacketTime) / 2;
					tracker.dataTick();
				}
			} else {
				LogManager.log.debug("[TrackerServer] Wrong ping id " + ping.pingId + " != " + connection.lastPingPacketId);
			}
			break;
		case UDPProtocolParser.PACKET_SERIAL:
			if(connection == null)
				break;
			UDPPacket11Serial serial = (UDPPacket11Serial) packet;
			System.out.println("[" + connection.name + "] " + serial.serial);
			break;
		case UDPProtocolParser.PACKET_BATTERY_LEVEL:
			if(connection == null)
				break;
			UDPPacket12BatteryLevel battery = (UDPPacket12BatteryLevel) packet;
			if(connection.sensors.size() > 0) {
				Collection<IMUTracker> trackers = connection.sensors.values();
				Iterator<IMUTracker> iterator = trackers.iterator();
				while(iterator.hasNext()) {
					IMUTracker tr = iterator.next();
					tr.setBatteryVoltage(battery.voltage);
					tr.setBatteryLevel(battery.level * 100);
				}
			}
			break;
		case UDPProtocolParser.PACKET_TAP:
			if(connection == null)
				break;
			UDPPacket13Tap tap = (UDPPacket13Tap) packet;
			tracker = connection.sensors.get(tap.getSensorId());
			if(tracker == null)
				break;
			LogManager.log.info("[TrackerServer] Tap packet received from " + tracker.getName() + ": " + tap.tap);
			break;
		case UDPProtocolParser.PACKET_ERROR:
			UDPPacket14Error error = (UDPPacket14Error) packet;
			LogManager.log.severe("[TrackerServer] Error received from " + received.getSocketAddress() + ": " + error.errorNumber);
			if(connection == null)
				break;
			tracker = connection.sensors.get(error.getSensorId());
			if(tracker == null)
				break;
			tracker.setStatus(TrackerStatus.ERROR);
			break;
		case UDPProtocolParser.PACKET_SENSOR_INFO:
			if(connection == null)
				break;
			UDPPacket15SensorInfo info = (UDPPacket15SensorInfo) packet;
			setUpSensor(connection, info.getSensorId(), info.sensorType, info.sensorStatus);
			// Send ack
			bb.limit(bb.capacity());
			bb.rewind();
			parser.writeSensorInfoResponse(bb, connection, info);
			socket.send(new DatagramPacket(rcvBuffer, bb.position(), connection.address));
			LogManager.log.info("[TrackerServer] Sensor info for " + connection.descriptiveName + "/" + info.getSensorId() + ": " + info.sensorStatus);
			break;
		case UDPProtocolParser.PACKET_SIGNAL_STRENGTH:
			if(connection == null)
				break;
			UDPPacket19SignalStrength signalStrength = (UDPPacket19SignalStrength) packet;
			if(connection.sensors.size() > 0) {
				Collection<IMUTracker> trackers = connection.sensors.values();
				Iterator<IMUTracker> iterator = trackers.iterator();
				while(iterator.hasNext()) {
					IMUTracker tr = iterator.next();
					tr.signalStrength = signalStrength.signalStrength;
				}
			}
			break;
		case UDPProtocolParser.PACKET_TEMPERATURE:
			if(connection == null)
				break;
			UDPPacket20Temperature temp = (UDPPacket20Temperature) packet;
			tracker = connection.sensors.get(temp.getSensorId());
			if(tracker == null)
				break;
			tracker.temperature = temp.temperature;
			break;
		default:
			LogManager.log.warning("[TrackerServer] Skipped packet " + packet);
			break;
		}
	}
	
	private static String packetToString(DatagramPacket packet) {
		StringBuilder sb = new StringBuilder();
		sb.append("DatagramPacket{");
		sb.append(packet.getAddress().toString());
		sb.append(packet.getPort());
		sb.append(',');
		sb.append(packet.getLength());
		sb.append(',');
		sb.append(ArrayUtils.toString(packet.getData()));
		sb.append('}');
		return sb.toString();
	}
}
