package io.eiren.vr.trackers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
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

import io.eiren.hardware.magentometer.Magneto;
import io.eiren.util.Util;
import io.eiren.util.collections.FastList;
import io.eiren.vr.trackers.IMUTracker.ConfigurationData;

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
	private final Map<SocketAddress, TrackerConnection> trackersMap = new HashMap<>();
	private final Map<Tracker, Consumer<String>> calibrationDataRequests = new HashMap<>();
	private final Map<Tracker, Consumer<String>> newCalibrationDataRequests = new HashMap<>();
	private final Consumer<IMUTracker> trackersConsumer;
	private final int port;
	
	protected DatagramSocket socket = null;
	protected long lastKeepup = System.currentTimeMillis();
	
	public TrackersUDPServer(int port, String name, Consumer<IMUTracker> trackersConsumer) {
		super(name);
		this.port = port;
		this.trackersConsumer = trackersConsumer;
	}
	
	public void sendCalibrationCommand(Tracker tracker, Consumer<String> calibrationDataConsumer) {
		TrackerConnection connection = null;
		synchronized(trackers) {
			for(int i = 0; i < trackers.size(); ++i) {
				if(trackers.get(i).tracker == tracker) {
					connection = trackers.get(i);
					break;
				}
			}
		}
		if(connection == null)
			return;
		synchronized(connection) {
			if(connection.isCalibrating)
				return;
			connection.tracker.setStatus(TrackerStatus.BUSY);
			connection.isCalibrating = true;
			connection.rawCalibrationData.clear();
		}
		if(calibrationDataConsumer != null)
			newCalibrationDataRequests.put(tracker, calibrationDataConsumer);
		try {
			socket.send(new DatagramPacket(CALIBRATION_BUFFER, CALIBRATION_BUFFER.length, connection.address));
			System.out.println("[TrackerServer] Calibrating sensor on " + connection.address);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void requestCalibrationData(Tracker tracker, Consumer<String> consumer) {
		TrackerConnection connection = null;
		synchronized(trackers) {
			for(int i = 0; i < trackers.size(); ++i) {
				if(trackers.get(i).tracker == tracker) {
					connection = trackers.get(i);
					break;
				}
			}
		}
		if(connection == null)
			return;
		calibrationDataRequests.put(tracker, consumer);
		try {
			socket.send(new DatagramPacket(CALIBRATION_REQUEST_BUFFER, CALIBRATION_REQUEST_BUFFER.length, connection.address));
			System.out.println("[TrackerServer] Requesting config from " + connection.address);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void uploadNewCalibrationData(Tracker tracker, ConfigurationData data) {
		TrackerConnection connection = null;
		synchronized(trackers) {
			for(int i = 0; i < trackers.size(); ++i) {
				if(trackers.get(i).tracker == tracker) {
					connection = trackers.get(i);
					break;
				}
			}
		}
		if(connection == null)
			return;
		// TODO
		try {
			socket.send(new DatagramPacket(CALIBRATION_REQUEST_BUFFER, CALIBRATION_REQUEST_BUFFER.length, connection.address));
			System.out.println("[TrackerServer] Requesting config from " + connection.address);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private void stopCalibration(TrackerConnection sensor) {
		synchronized(sensor) {
			if(!sensor.isCalibrating)
				return;
			if(sensor.gyroCalibrationData == null || sensor.rawCalibrationData.size() == 0)
				return; // Calibration not started yet
			sensor.tracker.setStatus(TrackerStatus.OK);
			sensor.isCalibrating = false;
		}
		if(sensor.rawCalibrationData.size() > 50 && sensor.gyroCalibrationData != null) {
			System.out.println("[TrackerServer] Gathered " + sensor.address + " calibrration data, processing...");
		} else {
			System.out.println("[TrackerServer] Can't gather enough calibration data, aboring...");
			return;
		}
		double[] accelBasis = new double[3];
		double[] accelAInv = new double[3 * 3];
		double[] magBasis = new double[3];
		double[] magAInv = new double[3 * 3];
		double[] gyroOffset = sensor.gyroCalibrationData;
		
		double[] accelData = new double[sensor.rawCalibrationData.size() * 3];
		double[] magData = new double[sensor.rawCalibrationData.size() * 3];
		double magMinX = Double.MAX_VALUE;
		double magMinY = Double.MAX_VALUE;
		double magMinZ = Double.MAX_VALUE;
		double magMaxX = Double.MIN_VALUE;
		double magMaxY = Double.MIN_VALUE;
		double magMaxZ = Double.MIN_VALUE;
		for(int i = 0; i < sensor.rawCalibrationData.size(); ++i) {
			double[] line = sensor.rawCalibrationData.get(i);
			accelData[i * 3 + 0] = line[0];
			accelData[i * 3 + 1] = line[1];
			accelData[i * 3 + 2] = line[2];
			magData[i * 3 + 0] = line[3];
			magData[i * 3 + 1] = line[4];
			magData[i * 3 + 2] = line[5];
			magMinX = Math.min(magMinX, line[3]);
			magMinY = Math.min(magMinY, line[4]);
			magMinZ = Math.min(magMinZ, line[5]);
			magMaxX = Math.max(magMaxX, line[3]);
			magMaxY = Math.max(magMaxY, line[4]);
			magMaxZ = Math.max(magMaxZ, line[5]);
		}
		
		double accelHnorm = 10000;
		double magentometerHnorm = 100;
		
		System.out.println("[TrackerServer] Accelerometer Hnorm: " + (accelHnorm = Magneto.INSTANCE.calculateHnorm(accelData, sensor.rawCalibrationData.size())));
		Magneto.INSTANCE.calculate(accelData, sensor.rawCalibrationData.size(), 2, accelHnorm, accelBasis, accelAInv);
		System.out.println("[TrackerServer] Magentometer Hnorm: " + (magentometerHnorm = Magneto.INSTANCE.calculateHnorm(magData, sensor.rawCalibrationData.size())));
		Magneto.INSTANCE.calculate(magData, sensor.rawCalibrationData.size(), 2, magentometerHnorm, magBasis, magAInv);
		
		System.out.println("float A_B[3] =");
		System.out.println(String.format("  {%8.2f,%8.2f,%8.2f},", accelBasis[0], accelBasis[1], accelBasis[2]));
		System.out.println("float A_Ainv[3][3] =");
		System.out.println(String.format("  {{%9.5f,%9.5f,%9.5f},", accelAInv[0], accelAInv[1], accelAInv[2]));
		System.out.println(String.format("  {%9.5f,%9.5f,%9.5f},", accelAInv[3], accelAInv[4], accelAInv[5]));
		System.out.println(String.format("  {%9.5f,%9.5f,%9.5f}},", accelAInv[6], accelAInv[7], accelAInv[8]));
		System.out.println("float M_B[3] =");
		System.out.println(String.format("  {%8.2f,%8.2f,%8.2f},", magBasis[0], magBasis[1], magBasis[2]));
		System.out.println("float M_Ainv[3][3] =");
		System.out.println(String.format("  {{%9.5f,%9.5f,%9.5f},", magAInv[0], magAInv[1], magAInv[2]));
		System.out.println(String.format("  {%9.5f,%9.5f,%9.5f},", magAInv[3], magAInv[4], magAInv[5]));
		System.out.println(String.format("  {%9.5f,%9.5f,%9.5f}},", magAInv[6], magAInv[7], magAInv[8]));
		System.out.println("float G_off[3] =");
		System.out.println(String.format("  {%8.2f, %8.2f, %8.2f}};", gyroOffset[0], gyroOffset[1], gyroOffset[2]));
		System.out.println(String.format("Min/Max {%8.2f, %8.2f, %8.2f} / {%8.2f, %8.2f, %8.2f}", magMinX, magMinY, magMinZ, magMaxX, magMaxY, magMaxZ));
		System.out.println(String.format("Mag agv {%8.2f, %8.2f, %8.2f},", (magMaxX + magMinX) / 2, (magMaxY + magMinY) / 2, (magMaxZ + magMinZ) / 2));
		
		
		IMUTracker.ConfigurationData data = new IMUTracker.ConfigurationData(accelBasis, accelAInv, magBasis, magAInv, gyroOffset);
		sensor.tracker.newCalibrationData = data;
		
		Consumer<String> consumer = newCalibrationDataRequests.remove(sensor.tracker);
		if(consumer != null) {
			consumer.accept(data.toTextMatrix());
		}
	}
	
	private void setUpNewSensor(DatagramPacket handshakePacket, ByteBuffer data) throws IOException {
		System.out.println("[TrackerServer] Handshake recieved from " + handshakePacket.getAddress() + ":" + handshakePacket.getPort());
		SocketAddress addr = handshakePacket.getSocketAddress();
		TrackerConnection sensor;
		synchronized(trackers) {
			sensor = trackersMap.get(addr);
		}
		if(sensor == null) {
			data.getLong(); // Skip packet number
			int boardType = -1;
			int imuType = -1;
			int firmwareBuild = -1;
			StringBuilder sb = new StringBuilder();
			if(data.remaining() > 0) {
				if(data.remaining() > 3)
					boardType = data.getInt();
				if(data.remaining() > 3)
					imuType = data.getInt();
				if(data.remaining() > 11) {
					data.getInt(); // IMU info
					data.getInt();
					data.getInt();
				}
				if(data.remaining() > 3)
					firmwareBuild = data.getInt();
				while(true) {
					if(data.remaining() == 0)
						break;
					char c = (char) data.get();
					if(c == 0)
						break;
					sb.append(c);
				}
			}
			if(sb.length() == 0)
				sb.append("owoTrack");
			IMUTracker imu = new IMUTracker("udp:/" + handshakePacket.getAddress().toString(), this);
			trackersConsumer.accept(imu);
			sensor = new TrackerConnection(imu, addr);
			int i = 0;
			synchronized(trackers) {
				i = trackers.size();
				trackers.add(sensor);
				trackersMap.put(addr, sensor);
			}
			System.out.println("[TrackerServer] Sensor " + i + " added with address " + addr + ". Board type: " + boardType + ", imu type: " + imuType + ", firmware: " + sb + " (" + firmwareBuild + ")");
		}
		sensor.tracker.setStatus(TrackerStatus.OK);
        socket.send(new DatagramPacket(HANDSHAKE_BUFFER, HANDSHAKE_BUFFER.length, handshakePacket.getAddress(), handshakePacket.getPort()));
	}
	
	private void setUpAuxialrySensor(TrackerConnection connection) throws IOException {
		System.out.println("[TrackerServer] Setting up auxilary sensor for " + connection.tracker.getName());
		IMUTracker imu = new IMUTracker(connection.tracker.getName() + "/1", this);
		connection.secondTracker = imu;
		trackersConsumer.accept(imu);
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

					TrackerConnection sensor;
					synchronized(trackers) {
						sensor = trackersMap.get(recieve.getSocketAddress());
					}
					if(sensor != null)
						sensor.lastPacket = System.currentTimeMillis();
					int packetId;
					switch(packetId = bb.getInt()) {
					case 3:
						setUpNewSensor(recieve, bb);
						break;
					case 1: // PACKET_ROTATION
					case 16: // PACKET_ROTATION_2
						if(sensor == null)
							break;
						bb.getLong();
						stopCalibration(sensor);
						buf.set(bb.getFloat(), bb.getFloat(), bb.getFloat(), bb.getFloat());
						offset.mult(buf, buf);
						IMUTracker tracker;
						if(packetId == 1) {
							tracker = sensor.tracker;
						} else {
							tracker = sensor.secondTracker;
						}
						if(tracker == null)
							break;
						tracker.rotQuaternion.set(buf);
						tracker.dataTick();
						break;
					case 2:
						if(sensor == null)
							break;
						bb.getLong();
						stopCalibration(sensor);
						sensor.tracker.gyroVector.set(bb.getFloat(), bb.getFloat(), bb.getFloat());
						break;
					case 4:
						if(sensor == null)
							break;
						bb.getLong();
						stopCalibration(sensor);
						float x = bb.getFloat();
						float z = bb.getFloat();
						float y = bb.getFloat();
						sensor.tracker.accelVector.set(x, y, z);
						break;
					case 5:
						if(sensor == null)
							break;
						bb.getLong();
						stopCalibration(sensor);
						x = bb.getFloat();
						z = bb.getFloat();
						y = bb.getFloat();
						sensor.tracker.magVector.set(x, y, z);
						break;
					case 6: // PACKET_RAW_CALIBRATION_DATA
						if(sensor == null)
							break;
						bb.getLong();
						sensor.rawCalibrationData.add(new double[] {bb.getInt(), bb.getInt(), bb.getInt(), bb.getInt(), bb.getInt(), bb.getInt()});
						break;
					case 7: // PACKET_GYRO_CALIBRATION_DATA
						if(sensor == null)
							break;
						bb.getLong();
						sensor.gyroCalibrationData = new double[] {bb.getFloat(), bb.getFloat(), bb.getFloat()};
						break;
					case 8: // PACKET_CONFIG
						if(sensor == null)
							break;
						bb.getLong();
						IMUTracker.ConfigurationData data = new IMUTracker.ConfigurationData(bb);
						Consumer<String> dataConsumer = calibrationDataRequests.remove(sensor.tracker);
						if(dataConsumer != null) {
							dataConsumer.accept(data.toTextMatrix());
						}
						break;
					case 9: // PACKET_RAW_MAGENTOMETER
						if(sensor == null)
							break;
						bb.getLong();
						float mx = bb.getFloat();
						float my = bb.getFloat();
						float mz = bb.getFloat();
						sensor.tracker.confidence = (float) Math.sqrt(mx * mx + my * my + mz * mz);
						break;
					case 10: // PACKET_PING_PONG:
						if(sensor == null)
							break;
						int pingId = bb.getInt();
						if(sensor.lastPingPacketId == pingId) {
							tracker = sensor.tracker;
							tracker.ping = (int) (System.currentTimeMillis() - sensor.lastPingPacketTime) / 2;
						}
						break;
					case 11: // PACKET_SERIAL
						if(sensor == null)
							break;
						tracker = sensor.tracker;
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
						if(sensor == null)
							break;
						tracker = sensor.tracker;
						bb.getLong();
						tracker.setBatteryVoltage(bb.getFloat());
						break;
					case 13: // PACKET_TAP
						if(sensor == null)
							break;
						tracker = sensor.tracker;
						bb.getLong();
						byte tap = bb.get();
						System.out.println("[TrackerServer] Tap packet received from " + tracker.getName() + ": b" + Integer.toBinaryString(tap));
						break;
					case 14: // PACKET_RESET_REASON
						bb.getLong();
						byte reason = bb.get();
						System.out.println("[TrackerServer] Reset recieved from " + recieve.getSocketAddress() + ": " + reason);
						if(sensor == null)
							break;
						tracker = sensor.tracker;
						tracker.setStatus(TrackerStatus.ERROR);
						break;
					case 15: // PACKET_SENSOR_INFO
						if(sensor == null)
							break;
						bb.getLong();
						int sensorId = bb.get() & 0xFF;
						int sensorStatus = bb.get() & 0xFF;
						if(sensorId == 1 && sensorStatus == 1 && sensor.secondTracker == null) {
							setUpAuxialrySensor(sensor);
						}
						bb.rewind();
						bb.putInt(15);
						bb.put((byte) sensorId);
						bb.put((byte) sensorStatus);
						socket.send(new DatagramPacket(rcvBuffer, bb.position(), sensor.address));
						System.out.println("[TrackerServer] Sensor info for " + sensor.tracker.getName() + "/" + sensorId + ": " + sensorStatus);
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
		boolean isCalibrating;
		private List<double[]> rawCalibrationData = new FastList<>();
		private double[] gyroCalibrationData;
		public long lastPacket = System.currentTimeMillis();
		public int lastPingPacketId = -1;
		public long lastPingPacketTime = 0;
		
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
