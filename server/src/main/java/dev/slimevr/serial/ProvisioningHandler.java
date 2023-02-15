package dev.slimevr.serial;

import com.fazecast.jSerialComm.SerialPort;
import dev.slimevr.VRServer;
import io.eiren.util.logging.LogManager;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;


public class ProvisioningHandler implements SerialListener {

	private ProvisioningStatus provisioningStatus = ProvisioningStatus.NONE;

	private boolean isRunning = false;
	private final List<ProvisioningListener> listeners = new CopyOnWriteArrayList<>();

	private String ssid;
	private String password;

	private String preferredPort;

	private final Timer provisioningTickTimer = new Timer("ProvisioningTickTimer");
	private long lastStatusChange = -1;
	private final VRServer vrServer;

	public ProvisioningHandler(VRServer vrServer) {
		this.vrServer = vrServer;
		vrServer.getSerialHandler().addListener(this);
		this.provisioningTickTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (!isRunning)
					return;
				provisioningTick();
			}
		}, 0, 1000);
	}


	public void start(String ssid, String password, String port) {
		this.isRunning = true;
		this.ssid = ssid;
		this.password = password;
		this.preferredPort = port;
		this.provisioningStatus = ProvisioningStatus.NONE;

	}

	public void stop() {
		this.isRunning = false;
		this.ssid = null;
		this.password = null;
		this.changeStatus(ProvisioningStatus.NONE);
		this.vrServer.getSerialHandler().closeSerial();
	}

	public void initSerial(String port) {
		this.provisioningStatus = ProvisioningStatus.SERIAL_INIT;

		try {
			if (port != null)
				vrServer.getSerialHandler().openSerial(port, false);
			else
				vrServer.getSerialHandler().openSerial(null, true);
		} catch (Exception e) {
			LogManager.severe("Unable to open serial port", e);
		} catch (Throwable e) {
			LogManager.severe("Using serial ports is not supported on this platform", e);
		}

	}


	public void tryProvisioning() {
		this.changeStatus(ProvisioningStatus.PROVISIONING);
		vrServer.getSerialHandler().setWifi(this.ssid, this.password);
	}


	public void provisioningTick() {

		if (
			this.provisioningStatus == ProvisioningStatus.CONNECTION_ERROR
				|| this.provisioningStatus == ProvisioningStatus.DONE
		)
			return;


		if (System.currentTimeMillis() - this.lastStatusChange > 10000) {
			if (this.provisioningStatus == ProvisioningStatus.NONE)
				this.initSerial(this.preferredPort);
			else if (this.provisioningStatus == ProvisioningStatus.SERIAL_INIT)
				initSerial(this.preferredPort);
			else if (this.provisioningStatus == ProvisioningStatus.PROVISIONING)
				this.tryProvisioning();
			else if (this.provisioningStatus == ProvisioningStatus.LOOKING_FOR_SERVER)
				this.changeStatus(ProvisioningStatus.COULD_NOT_FIND_SERVER);
		}
	}


	@Override
	public void onSerialConnected(SerialPort port) {
		if (!isRunning)
			return;
		this.tryProvisioning();
	}

	@Override
	public void onSerialDisconnected() {
		if (!isRunning)
			return;
		this.changeStatus(ProvisioningStatus.NONE);
	}

	@Override
	public void onSerialLog(String str) {
		if (!isRunning)
			return;

		if (
			provisioningStatus == ProvisioningStatus.PROVISIONING
				&& str.contains("New wifi credentials set")
		) {
			this.changeStatus(ProvisioningStatus.CONNECTING);
		}

		if (
			provisioningStatus == ProvisioningStatus.CONNECTING
				&& str.contains("Looking for the server")
		) {
			this.changeStatus(ProvisioningStatus.LOOKING_FOR_SERVER);
		}

		if (
			provisioningStatus == ProvisioningStatus.LOOKING_FOR_SERVER
				&& str.contains("Handshake successful")
		) {
			this.changeStatus(ProvisioningStatus.DONE);
		}

		if (
			provisioningStatus == ProvisioningStatus.CONNECTING
				&& str.contains("Can't connect from any credentials")
		) {
			this.changeStatus(ProvisioningStatus.CONNECTION_ERROR);
		}
	}

	public void changeStatus(ProvisioningStatus status) {
		this.lastStatusChange = System.currentTimeMillis();
		if (this.provisioningStatus != status) {
			this.listeners.forEach((l) -> l.onProvisioningStatusChange(status));
			this.provisioningStatus = status;
		}
	}

	@Override
	public void onNewSerialDevice(SerialPort port) {
		this.initSerial(this.preferredPort);
	}

	public void addListener(ProvisioningListener channel) {
		this.listeners.add(channel);
	}

	public void removeListener(ProvisioningListener l) {
		listeners.removeIf(listener -> l == listener);
	}

}
