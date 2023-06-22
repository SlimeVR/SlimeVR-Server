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
import java.util.stream.Collectors;
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
				try {
					detectNewPorts();
				} catch (Throwable t) {
					LogManager
						.severe(
							"[SerialHandler] Error while watching for new devices, cancelling the \"getDevicesTimer\".",
							t
						);
					getDevicesTimer.cancel();
				}
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

	public synchronized boolean openSerial(String portLocation, boolean auto) {
		LogManager.info("[SerialHandler] Trying to open: " + portLocation + ", auto: " + auto);

		SerialPort[] ports = SerialPort.getCommPorts();
		lastKnownPorts = ports;
		SerialPort newPort = null;
		for (SerialPort port : ports) {
			if (!auto && port.getPortLocation().equals(portLocation)) {
				newPort = port;
				break;
			}

			if (auto && isKnownBoard(port.getDescriptivePortName())) {
				newPort = port;
				break;
			}
		}
		if (newPort == null) {
			LogManager
				.info(
					"[SerialHandler] No serial ports found to connect to ("
						+ ports.length
						+ ") total ports"
				);
			return false;
		}
		if (this.isConnected()) {
			if (
				!newPort.getPortLocation().equals(currentPort.getPortLocation())
					|| !newPort
						.getDescriptivePortName()
						.equals(currentPort.getDescriptivePortName())
			) {
				LogManager
					.info(
						"[SerialHandler] Closing current serial port "
							+ currentPort.getDescriptivePortName()
					);
				currentPort.removeDataListener();
				currentPort.closePort();
			} else {
				LogManager.info("[SerialHandler] Reusing already open port");
				this.listeners.forEach((listener) -> listener.onSerialConnected(currentPort));
				return true;
			}
		}
		currentPort = newPort;
		LogManager
			.info(
				"[SerialHandler] Trying to connect to new serial port "
					+ currentPort.getDescriptivePortName()
			);

		currentPort.setBaudRate(115200);
		currentPort.clearRTS();
		currentPort.clearDTR();
		if (!currentPort.openPort(1000)) {
			LogManager
				.warning(
					"[SerialHandler] Can't open serial port "
						+ currentPort.getDescriptivePortName()
						+ ", last error: "
						+ currentPort.getLastErrorCode()
				);
			currentPort = null;
			return false;
		}

		currentPort.addDataListener(this);
		this.listeners.forEach((listener) -> listener.onSerialConnected(currentPort));
		LogManager
			.info("[SerialHandler] Serial port " + newPort.getDescriptivePortName() + " is open");
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

	public synchronized void closeSerial() {
		try {
			if (currentPort != null)
				currentPort.closePort();
			this.listeners.forEach(SerialListener::onSerialDisconnected);
			LogManager
				.info(
					"[SerialHandler] Port "
						+ (currentPort != null ? currentPort.getDescriptivePortName() : "null")
						+ " closed okay"
				);
			currentPort = null;
		} catch (Exception e) {
			LogManager
				.warning(
					"[SerialHandler] Error closing port "
						+ (currentPort != null ? currentPort.getDescriptivePortName() : "null"),
					e
				);
		}
	}

	private synchronized void writeSerial(String serialText) {
		if (currentPort == null)
			return;
		OutputStream os = currentPort.getOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(os);
		try {
			writer.append(serialText).append("\n");
			writer.flush();
			this.addLog("-> " + serialText + "\n");
		} catch (IOException e) {
			addLog("[!] Serial error: " + e.getMessage() + "\n");
			LogManager.warning("[SerialHandler] Serial port write error", e);
		}
	}

	public synchronized void setWifi(String ssid, String passwd) {
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
			LogManager.warning("[SerialHandler] Serial port write error", e);
		}
	}

	public void addLog(String str) {
		LogManager.info("[Serial] " + str);
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

	public synchronized boolean isConnected() {
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
						this.getKnownPorts().collect(Collectors.toList()),
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
			LogManager
				.severe("[SerialHandler] Using serial ports is not supported on this platform", e);
			throw new RuntimeException("Serial unsupported");
		}
	}
}
