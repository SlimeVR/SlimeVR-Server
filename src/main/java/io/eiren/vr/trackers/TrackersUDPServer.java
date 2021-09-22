package io.eiren.vr.trackers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
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
				}
			}
			if(firmware.length() == 0) {
				firmware.append("owoTrack");
				isOwo = true;
			}
			String trackerName = macString != null ? "upd://" + macString : "udp:/" + handshakePacket.getAddress().toString();
			IMUTracker imu = new IMUTracker(trackerName, this);
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
		sensor.tracker.setStatus(TrackerStatus.OK);
        socket.send(new DatagramPacket(HANDSHAKE_BUFFER, HANDSHAKE_BUFFER.length, handshakePacket.getAddress(), handshakePacket.getPort()));
	}
	
	private void setUpAuxilarySensor(TrackerConnection connection) throws IOException {
		System.out.println("[TrackerServer] Setting up auxilary sensor for " + connection.tracker.getName());
		IMUTracker imu = new IMUTracker(connection.tracker.getName() + "/1", this);
		connection.secondTracker = imu;
		ReferenceAdjustedTracker<IMUTracker> adjustedTracker = new ReferenceAdjustedTracker<>(imu);
		trackersConsumer.accept(adjustedTracker);
		System.out.println("[TrackerServer] Sensor added with address " + imu.getName());
	}
	
	
	@Override
	public void run() {
		byte[] rcvBuffer = new byte[512];
		ByteBuffer bb = ByteBuffer.wrap(rcvBuffer).order(ByteOrder.BIG_ENDIAN);
		StringBuilder serialBuffer2 = new StringBuilder();
		try {
			socket = new DatagramSocket(port);
			socket.setSoTimeout(250);
			while(true) {
				try {
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
							tracker = connection.tracker;
						} else {
							tracker = connection.secondTracker;
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
						if(sensorId == 0) {
							tracker = connection.tracker;
						} else if(sensorId == 1) {
							tracker = connection.secondTracker;
						}
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
						if(sensorId == 0) {
							tracker = connection.tracker;
						} else if(sensorId == 1) {
							tracker = connection.secondTracker;
						}
						if(tracker == null)
							break;
						float accuracyInfo = bb.getFloat();
						tracker.magnetometerAccuracy = accuracyInfo;
						// TODO
						break;
					case 2:
						if(connection == null)
							break;
						bb.getLong();
						connection.tracker.gyroVector.set(bb.getFloat(), bb.getFloat(), bb.getFloat());
						break;
					case 4:
						if(connection == null)
							break;
						bb.getLong();
						float x = bb.getFloat();
						float z = bb.getFloat();
						float y = bb.getFloat();
						connection.tracker.accelVector.set(x, y, z);
						break;
					case 5:
						if(connection == null)
							break;
						if(connection.isOwoTrack)
							break;
						bb.getLong();
						x = bb.getFloat();
						z = bb.getFloat();
						y = bb.getFloat();
						connection.tracker.magVector.set(x, y, z);
						break;
					case 6: // PACKET_RAW_CALIBRATION_DATA
						if(connection == null)
							break;
						if(connection.isOwoTrack)
							break;
						bb.getLong();
						//sensor.rawCalibrationData.add(new double[] {bb.getInt(), bb.getInt(), bb.getInt(), bb.getInt(), bb.getInt(), bb.getInt()});
						break;
					case 7: // PACKET_GYRO_CALIBRATION_DATA
						if(connection == null)
							break;
						if(connection.isOwoTrack)
							break;
						bb.getLong();
						//sensor.gyroCalibrationData = new double[] {bb.getFloat(), bb.getFloat(), bb.getFloat()};
						break;
					case 8: // PACKET_CONFIG
						if(connection == null)
							break;
						if(connection.isOwoTrack)
							break;
						bb.getLong();
						MPUTracker.ConfigurationData data = new MPUTracker.ConfigurationData(bb);
						Consumer<String> dataConsumer = calibrationDataRequests.remove(connection.tracker);
						if(dataConsumer != null) {
							dataConsumer.accept(data.toTextMatrix());
						}
						break;
					case 9: // PACKET_RAW_MAGENTOMETER
						if(connection == null)
							break;
						if(connection.isOwoTrack)
							break;
						bb.getLong();
						float mx = bb.getFloat();
						float my = bb.getFloat();
						float mz = bb.getFloat();
						connection.tracker.confidence = (float) Math.sqrt(mx * mx + my * my + mz * mz);
						break;
					case 10: // PACKET_PING_PONG:
						if(connection == null)
							break;
						if(connection.isOwoTrack)
							break;
						int pingId = bb.getInt();
						if(connection.lastPingPacketId == pingId) {
							tracker = connection.tracker;
							tracker.ping = (int) (System.currentTimeMillis() - connection.lastPingPacketTime) / 2;
						}
						break;
					case 11: // PACKET_SERIAL
						if(connection == null)
							break;
						if(connection.isOwoTrack)
							break;
						tracker = connection.tracker;
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
						tracker = connection.tracker;
						bb.getLong();
						tracker.setBatteryVoltage(bb.getFloat());
						break;
					case 13: // PACKET_TAP
						if(connection == null)
							break;
						if(connection.isOwoTrack)
							break;
						tracker = connection.tracker;
						bb.getLong();
						sensorId = bb.get() & 0xFF;
						if(sensorId == 0) {
							tracker = connection.tracker;
						} else if(sensorId == 1) {
							tracker = connection.secondTracker;
						}
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
						tracker = connection.tracker;
						tracker.setStatus(TrackerStatus.ERROR);
						break;
					case 15: // PACKET_SENSOR_INFO
						if(connection == null)
							break;
						bb.getLong();
						sensorId = bb.get() & 0xFF;
						int sensorStatus = bb.get() & 0xFF;
						if(sensorId == 1 && sensorStatus == 1 && connection.secondTracker == null) {
							setUpAuxilarySensor(connection);
						}
						bb.rewind();
						bb.putInt(15);
						bb.put((byte) sensorId);
						bb.put((byte) sensorStatus);
						socket.send(new DatagramPacket(rcvBuffer, bb.position(), connection.address));
						System.out.println("[TrackerServer] Sensor info for " + connection.tracker.getName() + "/" + sensorId + ": " + sensorStatus);
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
							IMUTracker tracker = conn.tracker;
							socket.send(new DatagramPacket(KEEPUP_BUFFER, KEEPUP_BUFFER.length, conn.address));
							if(conn.lastPacket + 1000 < System.currentTimeMillis()) {
								if(tracker.getStatus() != TrackerStatus.DISCONNECTED) {
									tracker.setStatus(TrackerStatus.DISCONNECTED);
									if(conn.secondTracker != null)
										conn.secondTracker.setStatus(TrackerStatus.DISCONNECTED);
								}
							} else if(tracker.getStatus() != TrackerStatus.ERROR && tracker.getStatus() != TrackerStatus.BUSY) {
								tracker.setStatus(TrackerStatus.OK);
								if(conn.secondTracker != null)
									conn.secondTracker.setStatus(TrackerStatus.OK);
							}
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
		
		IMUTracker tracker;
		IMUTracker secondTracker;
		SocketAddress address;
		public long lastPacket = System.currentTimeMillis();
		public int lastPingPacketId = -1;
		public long lastPingPacketTime = 0;
		public boolean isOwoTrack = false;
		
		public TrackerConnection(IMUTracker tracker, SocketAddress address) {
			this.tracker = tracker;
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
