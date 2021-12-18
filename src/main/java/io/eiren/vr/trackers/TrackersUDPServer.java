package io.eiren.vr.trackers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import io.eiren.util.Util;
import io.eiren.util.collections.FastList;

/**
 * Recieves trackers data by UDP using extended owoTrack protocol.
 */
public class TrackersUDPServer extends Thread {

	/**
	 * Change between IMU axises and OpenGL/SteamVR axises
	 */
	private static final Quaternion offset = new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X);
	
	private static final byte[] HANDSHAKE_BUFFER = new byte[64];
	private static final byte[] KEEPUP_BUFFER = new byte[64];
	private static final byte[] CALIBRATION_BUFFER = new byte[64];
	private static final byte[] CALIBRATION_REQUEST_BUFFER = new byte[64];

	private final Quaternion buf = new Quaternion();
	private final Random random = new Random();
	private final List<TrackerConnection> trackers = new FastList<>();
	private final Map<InetAddress, TrackerConnection> trackersMap = new HashMap<>();
	private final Map<Tracker, Consumer<String>> calibrationDataRequests = new HashMap<>();
	private final Consumer<Tracker> trackersConsumer;
	private final int port;
	
	protected DatagramSocket socket = null;
	protected long lastKeepup = System.currentTimeMillis();
	
	public TrackersUDPServer(int port, String name, Consumer<Tracker> trackersConsumer) {
		super(name);
		this.port = port;
		this.trackersConsumer = trackersConsumer;
	}
	
	private void setUpNewSensor(DatagramPacket handshakePacket, ByteBuffer data) throws IOException {
		System.out.println("[TrackerServer] Handshake recieved from " + handshakePacket.getAddress() + ":" + handshakePacket.getPort());
		InetAddress addr = handshakePacket.getAddress();
		TrackerConnection sensor;
		synchronized(trackers) {
			sensor = trackersMap.get(addr);
		}
		if(sensor == null) {
			boolean isOwo = false;
			data.getLong(); // Skip packet number
			int boardType = -1;
			int imuType = -1;
			int firmwareBuild = -1;
			StringBuilder firmware = new StringBuilder();
			byte[] mac = new byte[6];
			String macString = null;
			if(data.remaining() > 0) {
				if(data.remaining() > 3)
					boardType = data.getInt();
				if(data.remaining() > 3)
					imuType = data.getInt();
				if(data.remaining() > 3)
					data.getInt(); // MCU TYPE
				if(data.remaining() > 11) {
					data.getInt(); // IMU info
					data.getInt();
					data.getInt();
				}
				if(data.remaining() > 3)
					firmwareBuild = data.getInt();
				int length = 0;
				if(data.remaining() > 0)
					length = data.get() & 0xFF; // firmware version length is 1 longer than that because it's nul-terminated
				while(length > 0 && data.remaining() != 0) {
					char c = (char) data.get();
					if(c == 0)
						break;
					firmware.append(c);
					length--;
				}
				if(data.remaining() > mac.length) {
					data.get(mac);
					macString = String.format("%02X:%02X:%02X:%02X:%02X:%02X", mac[0], mac[1], mac[2], mac[3], mac[4], mac[5]);
					if(macString.equals("00:00:00:00:00:00"))
						macString = null;
				}
			}
			if(firmware.length() == 0) {
				firmware.append("owoTrack");
				isOwo = true;
			}
			String trackerName = macString != null ? "udp://" + macString : "udp:/" + handshakePacket.getAddress().toString();
			String descriptiveName = "udp:/" + handshakePacket.getAddress().toString();
			IMUTracker imu = new IMUTracker(Tracker.getNextLocalTrackerId(), trackerName, descriptiveName, this);
			ReferenceAdjustedTracker<IMUTracker> adjustedTracker = new ReferenceAdjustedTracker<>(imu);
			trackersConsumer.accept(adjustedTracker);
			sensor = new TrackerConnection(imu, handshakePacket.getSocketAddress());
			sensor.isOwoTrack = isOwo;
			int i = 0;
			synchronized(trackers) {
				i = trackers.size();
				trackers.add(sensor);
				trackersMap.put(addr, sensor);
			}
			System.out.println("[TrackerServer] Sensor " + i + " added with address " + handshakePacket.getSocketAddress() + ". Board type: " + boardType + ", imu type: " + imuType + ", firmware: " + firmware + " (" + firmwareBuild + "), mac: " + macString + ", name: " + trackerName);
		}
		sensor.sensors.get(0).setStatus(TrackerStatus.OK);
        socket.send(new DatagramPacket(HANDSHAKE_BUFFER, HANDSHAKE_BUFFER.length, handshakePacket.getAddress(), handshakePacket.getPort()));
	}
	
	private void setUpAuxilarySensor(TrackerConnection connection, int trackerId) throws IOException {
		System.out.println("[TrackerServer] Setting up auxilary sensor for " + connection.sensors.get(0).getName());
		IMUTracker imu = connection.sensors.get(trackerId);
		if(imu == null) {
			imu = new IMUTracker(Tracker.getNextLocalTrackerId(), connection.sensors.get(0).getName() + "/" + trackerId, connection.sensors.get(0).getDescriptiveName() + "/" + trackerId, this);
			connection.sensors.put(trackerId, imu);
			ReferenceAdjustedTracker<IMUTracker> adjustedTracker = new ReferenceAdjustedTracker<>(imu);
			trackersConsumer.accept(adjustedTracker);
			System.out.println("[TrackerServer] Sensor added with address " + imu.getName());
		}
		imu.setStatus(TrackerStatus.OK);
	}
	
	
	@Override
	public void run() {
		byte[] rcvBuffer = new byte[512];
		ByteBuffer bb = ByteBuffer.wrap(rcvBuffer).order(ByteOrder.BIG_ENDIAN);
		StringBuilder serialBuffer2 = new StringBuilder();
		try {
			socket = new DatagramSocket(port);

			// Why not just 255.255.255.255? Because Windows.
			// https://social.technet.microsoft.com/Forums/windows/en-US/72e7387a-9f2c-4bf4-a004-c89ddde1c8aa/how-to-fix-the-global-broadcast-address-255255255255-behavior-on-windows
			ArrayList<SocketAddress> addresses = new ArrayList<SocketAddress>();
			Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
			while (ifaces.hasMoreElements()) {
				NetworkInterface iface = ifaces.nextElement();
				// Ignore loopback, PPP, virtual and disabled devices
				if (iface.isLoopback() || !iface.isUp() || iface.isPointToPoint() || iface.isVirtual()) {
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
					addresses.add(new InetSocketAddress(String.format("%s.%s.%s.255", iaddrParts[0], iaddrParts[1], iaddrParts[2]), port));
				}
			}
			byte[] dummyPacket = new byte[] {0x0};

			long prevPacketTime = System.currentTimeMillis();
			socket.setSoTimeout(250);
			while(true) {
				try {
					boolean hasActiveTrackers = false;
					for (TrackerConnection tracker: trackers) {
						if (tracker.sensors.get(0).getStatus() == TrackerStatus.OK) {
							hasActiveTrackers = true;
							break;
						}
					}
					if (!hasActiveTrackers) {
						long discoveryPacketTime = System.currentTimeMillis();
						if ((discoveryPacketTime - prevPacketTime) >= 2000) {
							for (SocketAddress addr: addresses) {
								socket.send(new DatagramPacket(dummyPacket, dummyPacket.length, addr));
							}
							prevPacketTime = discoveryPacketTime;
						}
					}

					DatagramPacket recieve = new DatagramPacket(rcvBuffer, rcvBuffer.length);
					socket.receive(recieve);
					bb.rewind();

					TrackerConnection connection;
					IMUTracker tracker = null;
					synchronized(trackers) {
						connection = trackersMap.get(recieve.getAddress());
					}
					if(connection != null)
						connection.lastPacket = System.currentTimeMillis();
					int packetId;
					switch(packetId = bb.getInt()) {
					case 0:
						break;
					case 3:
						setUpNewSensor(recieve, bb);
						break;
					case 1: // PACKET_ROTATION
					case 16: // PACKET_ROTATION_2
						if(connection == null)
							break;
						bb.getLong();
						buf.set(bb.getFloat(), bb.getFloat(), bb.getFloat(), bb.getFloat());
						offset.mult(buf, buf);
						if(packetId == 1) {
							tracker = connection.sensors.get(0);
						} else {
							tracker = connection.sensors.get(1);
						}
						if(tracker == null)
							break;
						tracker.rotQuaternion.set(buf);
						tracker.dataTick();
						break;
					case 17: // PACKET_ROTATION_DATA
						if(connection == null)
							break;
						if(connection.isOwoTrack)
							break;
						bb.getLong();
						int sensorId = bb.get() & 0xFF;
						tracker = connection.sensors.get(sensorId);
						if(tracker == null)
							break;
						
						int dataType = bb.get() & 0xFF;
						buf.set(bb.getFloat(), bb.getFloat(), bb.getFloat(), bb.getFloat());
						offset.mult(buf, buf);
						int calibrationInfo = bb.get() & 0xFF;
						
						switch(dataType) {
						case 1: // DATA_TYPE_NORMAL
							tracker.rotQuaternion.set(buf);
							tracker.calibrationStatus = calibrationInfo;
							tracker.dataTick();
							break;
						case 2: // DATA_TYPE_CORRECTION
							tracker.rotMagQuaternion.set(buf);
							tracker.magCalibrationStatus = calibrationInfo;
							tracker.hasNewCorrectionData = true;
							break;
						}
						break;
					case 18: // PACKET_MAGENTOMETER_ACCURACY
						if(connection == null)
							break;
						if(connection.isOwoTrack)
							break;
						bb.getLong();
						sensorId = bb.get() & 0xFF;
						tracker = connection.sensors.get(sensorId);
						if(tracker == null)
							break;
						float accuracyInfo = bb.getFloat();
						tracker.magnetometerAccuracy = accuracyInfo;
						break;
					case 2: // PACKET_GYRO
					case 4: // PACKET_ACCEL
					case 5: // PACKET_MAG
					case 9: // PACKET_RAW_MAGENTOMETER
						break; // None of these packets are used by SlimeVR trackers and are deprecated, use more generic PACKET_ROTATION_DATA
					case 8: // PACKET_CONFIG
						if(connection == null)
							break;
						if(connection.isOwoTrack)
							break;
						bb.getLong();
						MPUTracker.ConfigurationData data = new MPUTracker.ConfigurationData(bb);
						Consumer<String> dataConsumer = calibrationDataRequests.remove(connection.sensors.get(0));
						if(dataConsumer != null) {
							dataConsumer.accept(data.toTextMatrix());
						}
						break;
					case 10: // PACKET_PING_PONG:
						if(connection == null)
							break;
						if(connection.isOwoTrack)
							break;
						int pingId = bb.getInt();
						if(connection.lastPingPacketId == pingId) {
							for(int i = 0; i < connection.sensors.size(); ++i) {
								tracker = connection.sensors.get(i);
								tracker.ping = (int) (System.currentTimeMillis() - connection.lastPingPacketTime) / 2;
								tracker.dataTick();
							}
						}
						break;
					case 11: // PACKET_SERIAL
						if(connection == null)
							break;
						if(connection.isOwoTrack)
							break;
						tracker = connection.sensors.get(0);
						bb.getLong();
						int length = bb.getInt();
						for(int i = 0; i < length; ++i) {
							char ch = (char) bb.get();
							if(ch == '\n') {
								serialBuffer2.append('[').append(tracker.getName()).append("] ").append(tracker.serialBuffer);
								System.out.println(serialBuffer2.toString());
								serialBuffer2.setLength(0);
								tracker.serialBuffer.setLength(0);
							} else {
								tracker.serialBuffer.append(ch);
							}
						}
						break;
					case 12: // PACKET_BATTERY_VOLTAGE
						if(connection == null)
							break;
						tracker = connection.sensors.get(0);
						bb.getLong();
						tracker.setBatteryVoltage(bb.getFloat());
						break;
					case 13: // PACKET_TAP
						if(connection == null)
							break;
						if(connection.isOwoTrack)
							break;
						bb.getLong();
						sensorId = bb.get() & 0xFF;
						tracker = connection.sensors.get(sensorId);
						if(tracker == null)
							break;
						int tap = bb.get() & 0xFF;
						BnoTap tapObj = new BnoTap(tap);
						System.out.println("[TrackerServer] Tap packet received from " + tracker.getName() + "/" + sensorId + ": " + tapObj  + " (b" + Integer.toBinaryString(tap) + ")");
						break;
					case 14: // PACKET_RESET_REASON
						bb.getLong();
						byte reason = bb.get();
						System.out.println("[TrackerServer] Reset recieved from " + recieve.getSocketAddress() + ": " + reason);
						if(connection == null)
							break;
						sensorId = bb.get() & 0xFF;
						tracker = connection.sensors.get(sensorId);
						if(tracker == null)
							break;
						tracker.setStatus(TrackerStatus.ERROR);
						break;
					case 15: // PACKET_SENSOR_INFO
						if(connection == null)
							break;
						bb.getLong();
						sensorId = bb.get() & 0xFF;
						int sensorStatus = bb.get() & 0xFF;
						if(sensorId > 0 && sensorStatus == 1) {
							setUpAuxilarySensor(connection, sensorId);
						}
						bb.rewind();
						bb.putInt(15);
						bb.put((byte) sensorId);
						bb.put((byte) sensorStatus);
						socket.send(new DatagramPacket(rcvBuffer, bb.position(), connection.address));
						System.out.println("[TrackerServer] Sensor info for " + connection.sensors.get(0).getName() + "/" + sensorId + ": " + sensorStatus);
						break;
					default:
						System.out.println("[TrackerServer] Unknown data received: " + packetId + " from " + recieve.getSocketAddress());
						break;
					}
				} catch(SocketTimeoutException e) {
				} catch(Exception e) {
					e.printStackTrace();
				}
				if(lastKeepup + 500 < System.currentTimeMillis()) {
					lastKeepup = System.currentTimeMillis();
					synchronized(trackers) {
						for(int i = 0; i < trackers.size(); ++i) {
							TrackerConnection conn = trackers.get(i);
							socket.send(new DatagramPacket(KEEPUP_BUFFER, KEEPUP_BUFFER.length, conn.address));
							if(conn.lastPacket + 1000 < System.currentTimeMillis()) {
								Iterator<IMUTracker> iterator = conn.sensors.values().iterator();
								while(iterator.hasNext()) {
									IMUTracker tracker = iterator.next();
									if(tracker.getStatus() == TrackerStatus.OK)
										tracker.setStatus(TrackerStatus.DISCONNECTED);
								}
							} else {
								Iterator<IMUTracker> iterator = conn.sensors.values().iterator();
								while(iterator.hasNext()) {
									IMUTracker tracker = iterator.next();
									if(tracker.getStatus() == TrackerStatus.DISCONNECTED)
										tracker.setStatus(TrackerStatus.OK);
								}
							}
							IMUTracker tracker = conn.sensors.get(0);
							if(tracker == null)
								continue;
							if(tracker.serialBuffer.length() > 0) {
								if(tracker.lastSerialUpdate + 500L < System.currentTimeMillis()) {
									serialBuffer2.append('[').append(tracker.getName()).append("] ").append(tracker.serialBuffer);
									System.out.println(serialBuffer2.toString());
									serialBuffer2.setLength(0);
									tracker.serialBuffer.setLength(0);
								}
							}
							if(conn.lastPingPacketTime + 500 < System.currentTimeMillis()) {
								conn.lastPingPacketId = random.nextInt();
								conn.lastPingPacketTime = System.currentTimeMillis();
								bb.rewind();
								bb.putInt(10);
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
	
	private class TrackerConnection {
		
		Map<Integer, IMUTracker> sensors = new HashMap<>();
		SocketAddress address;
		public long lastPacket = System.currentTimeMillis();
		public int lastPingPacketId = -1;
		public long lastPingPacketTime = 0;
		public boolean isOwoTrack = false;
		
		public TrackerConnection(IMUTracker tracker, SocketAddress address) {
			this.sensors.put(0, tracker);
			this.address = address;
		}
	}
	
	static {
		try {
			HANDSHAKE_BUFFER[0] = 3;
			byte[] str = "Hey OVR =D 5".getBytes("ASCII");
	        System.arraycopy(str, 0, HANDSHAKE_BUFFER, 1, str.length);
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError(e);
		}
		KEEPUP_BUFFER[3] = 1;
		CALIBRATION_BUFFER[3] = 4;
		CALIBRATION_BUFFER[4] = 1;
		CALIBRATION_REQUEST_BUFFER[3] = 4;
		CALIBRATION_REQUEST_BUFFER[4] = 2;
	}
}
