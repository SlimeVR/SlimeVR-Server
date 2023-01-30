package dev.slimevr.serial;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;
import io.eiren.util.logging.LogManager;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Equator;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;


public class SerialHandler implements SerialPortMessageListener {

	private final List<SerialListener> listeners = new CopyOnWriteArrayList<>();
	private final Timer getDevicesTimer = new Timer("GetDevicesTimer");

	private SerialPort currentPort = null;

	private boolean watchingNewDevices = false;
	private SerialPort[] lastKnownPorts = new SerialPort[] {};

	public SerialHandler() {
		startWatchingNewDevices();
	}

	public void startWatchingNewDevices() {
		if (this.watchingNewDevices)
			return;
		this.watchingNewDevices = true;
		this.getDevicesTimer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				detectNewPorts();
			}
		}, 0, 3000);
	}

	public void stopWatchingNewDevices() {
		if (!this.watchingNewDevices)
			return;
		this.watchingNewDevices = false;
		this.getDevicesTimer.cancel();
		this.getDevicesTimer.purge();
	}

	public void onNewDevice(SerialPort port) {
		this.listeners.forEach((listener) -> listener.onNewSerialDevice(port));
	}


	public void addListener(SerialListener channel) {
		this.listeners.add(channel);
	}

	public void removeListener(SerialListener l) {
		listeners.removeIf(listener -> l == listener);
	}

	public boolean openSerial(String portLocation, boolean auto) {
		if (this.isConnected()) {
			if (currentPort != null)
				currentPort.closePort();
		}

		System.out.println("Trying to open:" + portLocation + "  auto: " + auto);

		SerialPort[] ports = SerialPort.getCommPorts();
		lastKnownPorts = ports;
		for (SerialPort port : ports) {
			if (!auto && port.getPortLocation().equals(portLocation)) {
				currentPort = port;
				break;
			}

			if (auto && isKnownBoard(port.getDescriptivePortName())) {
				currentPort = port;
				break;
			}
		}
		if (currentPort == null) {
			return false;
		}

		currentPort.setBaudRate(115200);
		currentPort.clearRTS();
		currentPort.clearDTR();
		if (!currentPort.openPort()) {
			return false;
		}

		currentPort.addDataListener(this);
		this.listeners.forEach((listener) -> listener.onSerialConnected(currentPort));
		return true;
	}

	public void rebootRequest() {
		this.writeSerial("REBOOT");
	}

	public void factoryResetRequest() {
		this.writeSerial("FRST");
	}

	public void infoRequest() {
		this.writeSerial("GET INFO");
	}

	public void closeSerial() {
		try {
			if (currentPort != null)
				currentPort.closePort();
			this.listeners.forEach(SerialListener::onSerialDisconnected);
			System.out.println("Port closed okay");
			currentPort = null;
		} catch (Exception e) {
			System.out.println("Error closing port: " + e.getMessage());
		}
	}

	private void writeSerial(String serialText) {
		if (currentPort == null)
			return;
		OutputStream os = currentPort.getOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(os);
		try {
			writer.append(serialText).append("\n");
			writer.flush();
			this.addLog("-> " + serialText + "\n");
		} catch (IOException e) {
			addLog(e + "\n");
			e.printStackTrace();
		}
	}

	public void setWifi(String ssid, String passwd) {
		if (currentPort == null)
			return;
		OutputStream os = currentPort.getOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(os);
		try {
			writer.append("SET WIFI \"").append(ssid).append("\" \"").append(passwd).append("\"\n");
			writer.flush();
			this.addLog("-> SET WIFI \"" + ssid + "\" \"" + passwd.replaceAll(".", "*") + "\"\n");
		} catch (IOException e) {
			addLog(e + "\n");
			e.printStackTrace();
		}
	}

	public void addLog(String str) {
		this.listeners.forEach(listener -> listener.onSerialLog(str));
	}

	@Override
	public int getListeningEvents() {
		return SerialPort.LISTENING_EVENT_PORT_DISCONNECTED
			| SerialPort.LISTENING_EVENT_DATA_RECEIVED;
	}

	@Override
	public void serialEvent(SerialPortEvent event) {
		if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_RECEIVED) {
			byte[] newData = event.getReceivedData();
			String s = StandardCharsets.UTF_8.decode(ByteBuffer.wrap(newData)).toString();
			this.addLog(s);
		} else if (event.getEventType() == SerialPort.LISTENING_EVENT_PORT_DISCONNECTED) {
			this.closeSerial();
		}
	}

	public boolean isConnected() {
		return this.currentPort != null && this.currentPort.isOpen();
	}

	@Override
	public byte[] getMessageDelimiter() {
		return new byte[] { (byte) 0x0A };
	}

	@Override
	public boolean delimiterIndicatesEndOfMessage() {
		return true;
	}

	public Stream<SerialPort> getKnownPorts() {
		return Arrays
			.stream(SerialPort.getCommPorts())
			.filter((port) -> isKnownBoard(port.getDescriptivePortName()));

	}

	private boolean isKnownBoard(String com) {
		String lowerCom = com.toLowerCase();

		return lowerCom.contains("ch340")
			|| lowerCom.contains("cp21")
			|| lowerCom.contains("ch910")
			|| (lowerCom.contains("usb")
				&& lowerCom.contains("seri"));
	}

	private void detectNewPorts() {
		try {
			List<SerialPort> differences = new ArrayList<>(
				CollectionUtils
					.removeAll(
						this.getKnownPorts().toList(),
						Arrays.asList(lastKnownPorts),
						new Equator<>() {
							@Override
							public boolean equate(SerialPort o1, SerialPort o2) {
								return o1.getPortLocation().equals(o2.getPortLocation())
									&& o1
										.getDescriptivePortName()
										.equals(o1.getDescriptivePortName());
							}

							@Override
							public int hash(SerialPort o) {
								return 0;
							}
						}
					)
			);
			lastKnownPorts = SerialPort.getCommPorts();
			differences.forEach(this::onNewDevice);
		} catch (Throwable e) {
			LogManager.severe("Using serial ports is not supported on this platform", e);
		}
	}
}
