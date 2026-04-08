package dev.slimevr.serial

import dev.slimevr.VRServer
import io.eiren.util.logging.LogManager
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Consumer
import kotlin.concurrent.scheduleAtFixedRate

class ProvisioningHandler(private val server: VRServer) : SerialListener {
	private var provisioningStatus = ProvisioningStatus.NONE

	private var isRunning = false
	private val listeners = CopyOnWriteArrayList<ProvisioningListener>()

	private var ssid: String? = null
	private var password: String? = null

	private var preferredPort: String? = null

	private val provisioningTickTimer = Timer("ProvisioningTickTimer")
	private var lastStatusChange: Long = -1
	private var connectRetries: Byte = 0
	private var hasLogs = false

	companion object {
		const val MAX_CONNECTION_RETRIES: Byte = 1
	}

	init {
		server.serialHandler.addListener(this)
		provisioningTickTimer.scheduleAtFixedRate(0, 1000) {
			if (isRunning && provisioningStatus != ProvisioningStatus.DONE) provisioningTick()
		}
	}

	fun start(ssid: String, password: String, port: String?) {
		this.isRunning = true
		this.hasLogs = false
		this.ssid = ssid
		this.password = password
		this.preferredPort = port
		this.provisioningStatus = ProvisioningStatus.NONE
		this.connectRetries = 0
	}

	fun stop() {
		this.isRunning = false
		this.hasLogs = false
		this.ssid = null
		this.password = null
		this.connectRetries = 0
		changeStatus(ProvisioningStatus.NONE)
		server.serialHandler.closeSerial()
	}

	fun initSerial(port: String?) {
		provisioningStatus = ProvisioningStatus.SERIAL_INIT
		hasLogs = false

		try {
			val openResult = if (port != null) {
				server.serialHandler.openSerial(port, false)
			} else {
				server.serialHandler.openSerial(null, true)
			}
			if (!openResult) {
				LogManager.info("[SerialHandler] Serial port wasn't open...")
			}
		} catch (e: Exception) {
			LogManager.severe("[SerialHandler] Unable to open serial port", e)
		} catch (e: Throwable) {
			LogManager.severe("[SerialHandler] Using serial ports is not supported on this platform", e)
		}
	}

	fun tryObtainMacAddress() {
		changeStatus(ProvisioningStatus.OBTAINING_MAC_ADDRESS)
		server.serialHandler.infoRequest()
	}

	fun tryProvisioning() {
		changeStatus(ProvisioningStatus.PROVISIONING)
		server.serialHandler.setWifi(ssid!!, password!!)
	}

	fun provisioningTick() {
		if (provisioningStatus == ProvisioningStatus.OBTAINING_MAC_ADDRESS) tryObtainMacAddress()

		if (!hasLogs && provisioningStatus == ProvisioningStatus.OBTAINING_MAC_ADDRESS && System.currentTimeMillis() - lastStatusChange > 1000) {
			changeStatus(ProvisioningStatus.NO_SERIAL_LOGS_ERROR)
			return
		}

		if (provisioningStatus == ProvisioningStatus.SERIAL_INIT &&
			server.serialHandler.knownPorts.findAny().isEmpty &&
			System.currentTimeMillis() - lastStatusChange > 15000
		) {
			changeStatus(ProvisioningStatus.NO_SERIAL_DEVICE_FOUND)
			return
		}

		if (System.currentTimeMillis() - lastStatusChange
			> provisioningStatus.timeout
		) {
			if (provisioningStatus == ProvisioningStatus.NONE ||
				provisioningStatus == ProvisioningStatus.SERIAL_INIT
			) {
				initSerial(preferredPort)
			} else if (provisioningStatus == ProvisioningStatus.CONNECTING) {
				changeStatus(ProvisioningStatus.CONNECTION_ERROR)
			} else if (provisioningStatus == ProvisioningStatus.LOOKING_FOR_SERVER) {
				changeStatus(
					ProvisioningStatus.COULD_NOT_FIND_SERVER,
				)
			} else if (!provisioningStatus.isError) {
				changeStatus(ProvisioningStatus.CONNECTION_ERROR) // TIMEOUT
			}
		}
	}

	override fun onSerialConnected(port: SerialPort) {
		if (!isRunning) return
		tryObtainMacAddress()
	}

	override fun onSerialDisconnected() {
		if (!isRunning) return
		changeStatus(ProvisioningStatus.NONE)
		connectRetries = 0
	}

	override fun onSerialLog(str: String, fromServer: Boolean) {
		if (!isRunning) return
		if (!fromServer) {
			hasLogs = true
			if (provisioningStatus == ProvisioningStatus.NO_SERIAL_LOGS_ERROR) {
				// Recover the onboarding process if the user turned on the
				// tracker afterward
				changeStatus(ProvisioningStatus.OBTAINING_MAC_ADDRESS)
			}
		}

		if (provisioningStatus == ProvisioningStatus.OBTAINING_MAC_ADDRESS && str.contains("mac:")) {
			val match = Regex("mac: (?<mac>([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})), ")
				.find(str, str.indexOf("mac:"))

			if (match != null) {
				val b = match.groups[1]
				if (b != null) {
					server.configManager.vrConfig.addKnownDevice(b.value)
					server.configManager.saveConfig()
					tryProvisioning()
				}
			}
		}

		if (provisioningStatus == ProvisioningStatus.PROVISIONING &&
			str.contains("New wifi credentials set")
		) {
			changeStatus(ProvisioningStatus.CONNECTING)
		}

		if (provisioningStatus == ProvisioningStatus.CONNECTING &&
			(
				str.contains("Looking for the server") ||
					str.contains("Searching for the server")
				)
		) {
			changeStatus(ProvisioningStatus.LOOKING_FOR_SERVER)
		}

		if (provisioningStatus == ProvisioningStatus.LOOKING_FOR_SERVER &&
			str.contains("Handshake successful")
		) {
			changeStatus(ProvisioningStatus.DONE)
		}

		if (provisioningStatus == ProvisioningStatus.CONNECTING &&
			str.contains("Can't connect from any credentials")
		) {
			if (++connectRetries >= MAX_CONNECTION_RETRIES) {
				changeStatus(ProvisioningStatus.CONNECTION_ERROR)
			} else {
				server.serialHandler.rebootRequest()
			}
		}
	}

	fun changeStatus(status: ProvisioningStatus) {
		if (provisioningStatus != status) {
			lastStatusChange = System.currentTimeMillis()
			listeners
				.forEach { l ->
					l.onProvisioningStatusChange(status, server.serialHandler.getCurrentPort())
				}
			provisioningStatus = status
		}
	}

	override fun onNewSerialDevice(port: SerialPort) {
		if (!isRunning) return
		initSerial(preferredPort)
	}

	fun addListener(channel: ProvisioningListener) {
		listeners.add(channel)
	}

	fun removeListener(l: ProvisioningListener) {
		listeners.removeIf { listener: ProvisioningListener? -> l === listener }
	}

	override fun onSerialDeviceDeleted(port: SerialPort) {
	}
}
