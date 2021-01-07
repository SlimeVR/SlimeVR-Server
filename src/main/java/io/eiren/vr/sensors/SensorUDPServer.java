package io.eiren.vr.sensors;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.commons.math3.util.DoubleArray;

import com.sun.jna.ptr.DoubleByReference;

import essentia.util.collections.FastList;
import io.eiren.hardware.magentometer.Magneto;
import io.eiren.util.Util;

public class SensorUDPServer extends Thread {
	
	private static final byte[] HANDSHAKE_BUFFER = new byte[64];
	private static final byte[] KEEPUP_BUFFER = new byte[64];
	private static final byte[] CALIBRATION_BUFFER = new byte[64];

	DatagramSocket socket = null;
	byte[] sendBuffer = new byte[64];
	long lastKeepup = System.currentTimeMillis();
	private final Supplier<RotationSensor> sensorSupplier;
	private final int port;
	private List<SensorConnection> sensors = new FastList<>();
	private Map<SocketAddress, SensorConnection> sensorsMap = new HashMap<>();
	
	public SensorUDPServer(int port, String name, Supplier<RotationSensor> sensorSupplier) {
		super(name);
		this.port = port;
		this.sensorSupplier = sensorSupplier;
	}
	
	public void sendCalibrationCommand(int sensorId) {
		SensorConnection sensor;
		synchronized(sensors) {
			if(sensors.size() < sensorId + 1)
				return;
			sensor = sensors.get(sensorId);
		}
		synchronized(sensor) {
			if(sensor.isCalibrating)
				return;
			sensor.isCalibrating = true;
			sensor.rawCalibrationData.clear();
		}
		try {
			socket.send(new DatagramPacket(CALIBRATION_BUFFER, CALIBRATION_BUFFER.length, sensor.address));
			System.out.println("Calibrating sensor on " + sensor.address);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private void stopCalibration(SensorConnection sensor) {
		synchronized(sensor) {
			if(!sensor.isCalibrating)
				return;
			if(sensor.gyroCalibrationData == null || sensor.rawCalibrationData.size() == 0)
				return; // Calibration not started yet
			sensor.isCalibrating = false;
		}
		if(sensor.rawCalibrationData.size() > 50 && sensor.gyroCalibrationData != null) {
			System.out.println("Gathered " + sensor.address + " calibrration data, processing...");
		} else {
			System.out.println("Can't gather enough calibration data, aboring...");
			return;
		}
		double[] accelBasis = new double[3];
		double[] accelAInv = new double[3 * 3];
		double[] magBasis = new double[3];
		double[] magAInv = new double[3 * 3];
		double[] gyroOffset = sensor.gyroCalibrationData;
		
		double[] accelData = new double[sensor.rawCalibrationData.size() * 3];
		double[] magData = new double[sensor.rawCalibrationData.size() * 3];
		for(int i = 0; i < sensor.rawCalibrationData.size(); ++i) {
			double[] line = sensor.rawCalibrationData.get(i);
			accelData[i * 3 + 0] = line[0];
			accelData[i * 3 + 1] = line[1];
			accelData[i * 3 + 2] = line[2];
			magData[i * 3 + 0] = line[3];
			magData[i * 3 + 1] = line[4];
			magData[i * 3 + 2] = line[5];
		}
		
		
		System.out.println("Accelerometer Hnorm: " + Magneto.INSTANCE.calculateHnorm(accelData, sensor.rawCalibrationData.size()));
		Magneto.INSTANCE.calculate(accelData, sensor.rawCalibrationData.size(), 2, 10000, accelBasis, accelAInv);
		System.out.println("Magentometer Hnorm: " + Magneto.INSTANCE.calculateHnorm(magData, sensor.rawCalibrationData.size()));
		Magneto.INSTANCE.calculate(magData, sensor.rawCalibrationData.size(), 2, 100, magBasis, magAInv);
		
		System.out.println("float G_off[3] =");
		System.out.println(String.format("  {%8.2f, %8.2f, %8.2f}", gyroOffset[0], gyroOffset[1], gyroOffset[2]));
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
	}
	
	private void setUpNewSensor(DatagramPacket handshakePacket, ByteBuffer data) throws IOException {
		System.out.println("Handshake recieved from " + handshakePacket.getAddress() + ":" + handshakePacket.getPort());
		SocketAddress addr = handshakePacket.getSocketAddress();
		SensorConnection sensor;
		synchronized(sensors) {
			sensor = sensorsMap.get(addr);
		}
		if(sensor == null) {
			sensor = new SensorConnection(sensorSupplier.get(), addr);
			int i = 0;
			synchronized(sensors) {
				i = sensors.size();
				sensors.add(sensor);
				sensorsMap.put(addr, sensor);
			}
			System.out.println("Sensor " + i + " added with address " + addr);
		}
        socket.send(new DatagramPacket(HANDSHAKE_BUFFER, HANDSHAKE_BUFFER.length, handshakePacket.getAddress(), handshakePacket.getPort()));
	}
	
	@Override
	public void run() {
		byte[] rcvBuffer = new byte[64];
		ByteBuffer bb = ByteBuffer.wrap(rcvBuffer).order(ByteOrder.BIG_ENDIAN);
		try {
			socket = new DatagramSocket(port);
			while(true) {
				try {
					DatagramPacket recieve = new DatagramPacket(rcvBuffer, rcvBuffer.length);
					socket.receive(recieve);
					bb.rewind();

					SensorConnection sensor;
					synchronized(sensors) {
						sensor = sensorsMap.get(recieve.getSocketAddress());
					}
					int packetId;
					switch(packetId = bb.getInt()) {
					case 3:
						setUpNewSensor(recieve, bb);
						break;
					case 1:
						if(sensor == null)
							break;
						bb.getLong();
						stopCalibration(sensor);
						sensor.sensor.rotQuaternion.set(bb.getFloat(), bb.getFloat(), bb.getFloat(), bb.getFloat());
						//rotQuaternion.set(-rotQuaternion.getY(), rotQuaternion.getX(), rotQuaternion.getZ(), rotQuaternion.getW());
						//System.out.println("Rot: " + rotQuaternion.getX() + "," + rotQuaternion.getY() + "," + rotQuaternion.getZ() + "," + rotQuaternion.getW());
						break;
					case 2:
						if(sensor == null)
							break;
						bb.getLong();
						stopCalibration(sensor);
						sensor.sensor.gyroVector.set(bb.getFloat(), bb.getFloat(), bb.getFloat());
						//System.out.println("Gyro: " + bb.getFloat() + "," + bb.getFloat() + "," + bb.getFloat());
						break;
					case 4:
						if(sensor == null)
							break;
						bb.getLong();
						stopCalibration(sensor);
						sensor.sensor.accelVector.set(bb.getFloat(), bb.getFloat(), bb.getFloat());
						//System.out.println("Accel: " + bb.getFloat() + "," + bb.getFloat() + "," + bb.getFloat());
						break;
					case 5:
						if(sensor == null)
							break;
						bb.getLong();
						stopCalibration(sensor);
						sensor.sensor.magVector.set(bb.getFloat(), bb.getFloat(), bb.getFloat());
						//System.out.println("Accel: " + bb.getFloat() + "," + bb.getFloat() + "," + bb.getFloat());
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
					default:
						System.out.println("Unknown data recieved: " + packetId);
						break;
					}
					if(lastKeepup + 500 < System.currentTimeMillis()) {
						lastKeepup = System.currentTimeMillis();
						synchronized(sensors) {
							for(int i = 0; i < sensors.size(); ++i)
								socket.send(new DatagramPacket(KEEPUP_BUFFER, KEEPUP_BUFFER.length, sensors.get(i).address));
						}
					}
				} catch(SocketTimeoutException e) {
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			Util.close(socket);
		}
	}
	
	private class SensorConnection {
		
		RotationSensor sensor;
		SocketAddress address;
		boolean isCalibrating;
		private List<double[]> rawCalibrationData = new FastList<>();
		private double[] gyroCalibrationData;
		
		public SensorConnection(RotationSensor sensor, SocketAddress address) {
			this.sensor = sensor;
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
	}
}
