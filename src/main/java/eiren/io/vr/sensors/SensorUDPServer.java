package eiren.io.vr.sensors;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import io.eiren.util.Util;

public class SensorUDPServer extends Thread {

	DatagramSocket socket = null;
	byte[] sendBuffer = new byte[64];
	long lastKeepup = System.currentTimeMillis();
	boolean connected = false;
	private final RotationSensor sensor;
	private final int port;
	
	public SensorUDPServer(int port, String name, RotationSensor sensor) {
		super(name);
		this.sensor = sensor;
		this.port = port;
	}
	
	@Override
	public void run() {
		try {
			socket = new DatagramSocket(port);
			socket.setSoTimeout(250);
			while(true) {
				try {
					byte[] rcvBuffer = new byte[64];
					ByteBuffer bb = ByteBuffer.wrap(rcvBuffer).order(ByteOrder.BIG_ENDIAN);
					//ByteBuffer bb2 = bb.asReadOnlyBuffer().order(ByteOrder.BIG_ENDIAN);
					DatagramPacket recieve = new DatagramPacket(rcvBuffer, 64);
					socket.receive(recieve);
					bb.rewind();
					//System.out.println(StringUtils.toHexString(rcvBuffer));
					switch(bb.getInt()) {
					case 3:
						System.out.println("Handshake");
						sendBuffer[0] = 3;
						byte[] str = "Hey OVR =D 5".getBytes("ASCII");
				        System.arraycopy(str, 0, sendBuffer, 1, str.length);
				        socket.send(new DatagramPacket(sendBuffer, 64, recieve.getAddress(), recieve.getPort()));
				        connected = true;
						break;
					case 1:
						bb.getLong();
						sensor.rotQuaternion.set(bb.getFloat(), bb.getFloat(), bb.getFloat(), bb.getFloat());
						//rotQuaternion.set(-rotQuaternion.getY(), rotQuaternion.getX(), rotQuaternion.getZ(), rotQuaternion.getW());
						//System.out.println("Rot: " + rotQuaternion.getX() + "," + rotQuaternion.getY() + "," + rotQuaternion.getZ() + "," + rotQuaternion.getW());
						break;
					case 2:
						bb.getLong();
						sensor.gyroVector.set(bb.getFloat(), bb.getFloat(), bb.getFloat());
						//System.out.println("Gyro: " + bb.getFloat() + "," + bb.getFloat() + "," + bb.getFloat());
						break;
					case 4:
						bb.getLong();
						sensor.accelVector.set(bb.get(), bb.getFloat(), bb.getFloat());
						//System.out.println("Accel: " + bb.getFloat() + "," + bb.getFloat() + "," + bb.getFloat());
						break;
					case 5:
						bb.getLong();
						sensor.magVector.set(bb.get(), bb.getFloat(), bb.getFloat());
						//System.out.println("Accel: " + bb.getFloat() + "," + bb.getFloat() + "," + bb.getFloat());
						break;
					}
					if(lastKeepup + 500 < System.currentTimeMillis()) {
						lastKeepup = System.currentTimeMillis();
						if(connected) {
							sendBuffer[0] = 1;
							socket.send(new DatagramPacket(sendBuffer, 64, recieve.getAddress(), recieve.getPort()));
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
}
