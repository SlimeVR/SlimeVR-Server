package dev.slimevr.serial;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.concurrent.*;
import static java.util.concurrent.TimeUnit.*;


public class SerialHandler implements SerialPortMessageListener {

	private final List<SerialListener> listeners = new CopyOnWriteArrayList<>();
	private SerialPort trackerPort = null;
	private boolean rts;
	private boolean dtr;
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	ScheduledFuture<?> nextstephandler;

	public void addListener(SerialListener channel) {
		this.listeners.add(channel);
	}

	public void removeListener(SerialListener l) {
		listeners.removeIf(listener -> l == listener);
	}

	public boolean openSerial() {
		if (this.isConnected()) {
			return true;
		}

		SerialPort[] ports = SerialPort.getCommPorts();
		for (SerialPort port : ports) {
			if (
				port.getDescriptivePortName().toLowerCase().contains("ch340")
					|| port.getDescriptivePortName().toLowerCase().contains("cp21")
					|| port.getDescriptivePortName().toLowerCase().contains("ch910")
			) {
				trackerPort = port;
				break;
			}
		}
		if (trackerPort == null) {
			return false;
		}
		trackerPort.setBaudRate(115200);
		trackerPort.clearRTS();
		trackerPort.clearDTR();
		if (!trackerPort.openPort()) {
			return false;
		}
		rts = trackerPort.getRTS();
		dtr = trackerPort.getDTR();


		trackerPort.addDataListener(this);
		this.listeners.forEach((listener) -> listener.onSerialConnected(trackerPort));
		return true;
	}

	public void setRts(boolean value) {
		if (trackerPort == null) {
			return;
		}
		if (value) {
			trackerPort.setRTS();
		} else {
			trackerPort.clearRTS();
		}
		rts = trackerPort.getRTS();
	}

	public boolean getRts() {
		if (trackerPort != null) {
			rts = trackerPort.getRTS();
		}
		return rts;
	}

	public void setDtr(boolean value) {

		if (value) {
			trackerPort.setDTR();
		} else {
			trackerPort.clearDTR();
		}
		dtr = trackerPort.getDTR();
	}

	public boolean getDtr() {
		if (trackerPort != null) {
			dtr = trackerPort.getDTR();
		}
		return dtr;
	}

	private void toggleRts() {
		if (trackerPort == null) {
			return;
		}
		if (trackerPort.getRTS()) {
			trackerPort.clearRTS();
		} else {
			trackerPort.setRTS();
		}
	}

	private void toggleDtr() {
		if (trackerPort == null) {
			return;
		}
		if (trackerPort.getDTR()) {
			trackerPort.clearDTR();
		} else {
			trackerPort.setDTR();
		}
	}

	public void resetRequest() {
		this.writeSerial("REBOOT");
	}

	public void factoryResetRequest() {
		this.writeSerial("FRST");
	}

	public void infoRequest() {
		this.writeSerial("GET INFO");
	}

	public void restartRequest() {
		if (trackerPort == null) {
			return;
		}
		final Runnable nextstep = new Runnable() {
			private int step = 0;

			public void run() {
				switch (step) {
					case 0:
						toggleRts();
						break;
					case 1:
						toggleRts();
						break;
					case 2:
						toggleDtr();
						break;
					case 3:
						toggleDtr();
						break;
					default:
				}
				step++;
				if (step >= 4) {
					nextstephandler.cancel(true);
				}
			};
		};
		if (nextstephandler != null) {
			if ((!nextstephandler.isCancelled()) || (!nextstephandler.isDone())) {
				return;
			}
		}
		nextstephandler = scheduler.scheduleAtFixedRate(nextstep, 0, 100, MILLISECONDS);
	}

	public void closeSerial() {
		try {
			if (trackerPort != null)
				trackerPort.closePort();
			this.listeners.forEach(SerialListener::onSerialDisconnected);
			System.out.println("Port closed okay");
			trackerPort = null;
		} catch (Exception e) {
			System.out.println("Error closing port: " + e.getMessage());
		}
	}

	private void writeSerial(String serialText) {
		if (trackerPort == null)
			return;
		OutputStream os = trackerPort.getOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(os);
		try {
			writer.append(serialText + "\n");
			writer.flush();
			this.addLog("-> " + serialText + "\n");
		} catch (IOException e) {
			addLog(e + "\n");
			e.printStackTrace();
		}
	}

	public void setWifi(String ssid, String passwd) {
		this.writeSerial("SET WIFI \"" + ssid + "\" \"" + passwd + "\"");
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
		return this.trackerPort != null && this.trackerPort.isOpen();
	}

	@Override
	public byte[] getMessageDelimiter() {
		return new byte[] { (byte) 0x0A };
	}

	@Override
	public boolean delimiterIndicatesEndOfMessage() {
		return true;
	}
}
