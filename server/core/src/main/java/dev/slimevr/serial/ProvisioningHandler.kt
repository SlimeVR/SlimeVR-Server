package dev.slimevr.serial

import dev.slimevr.VRServer
import io.eiren.util.logging.LogManager
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Consumer
import kotlin.concurrent.scheduleAtFixedRate

class ProvisioningHandler(private val vrServer: VRServer) : SerialListener {
    private var provisioningStatus = ProvisioningStatus.NONE

    private var isRunning = false
    private val listeners: MutableList<ProvisioningListener?> = CopyOnWriteArrayList<ProvisioningListener?>()

    private var ssid: String? = null
    private var password: String? = null

    private var preferredPort: String? = null

    private val provisioningTickTimer = Timer("ProvisioningTickTimer")
    private var lastStatusChange: Long = -1
    private var connectRetries: Byte = 0
    private var hasLogs = false

    companion object {
		private const val MAX_CONNECTION_RETRIES: Byte = 1
	}

    init {
        vrServer.serialHandler.addListener(this)
        this.provisioningTickTimer.scheduleAtFixedRate(0, 1000) {
			if (!isRunning) return@scheduleAtFixedRate
			provisioningTick()
		}
    }


    fun start(ssid: String?, password: String?, port: String?) {
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
        this.changeStatus(ProvisioningStatus.NONE)
        this.vrServer.serialHandler.closeSerial()
    }

    fun initSerial(port: String?) {
        this.provisioningStatus = ProvisioningStatus.SERIAL_INIT
        this.hasLogs = false

        try {
            val openResult = if (port != null) {
				vrServer.serialHandler.openSerial(port, false)
			} else {
				vrServer.serialHandler.openSerial(null, true)
			}

            if (!openResult) LogManager.info("[SerialHandler] Serial port wasn't open...")
        } catch (e: Exception) {
            LogManager.severe("[SerialHandler] Unable to open serial port", e)
        } catch (e: Throwable) {
            LogManager
                .severe("[SerialHandler] Using serial ports is not supported on this platform", e)
        }
    }

    fun tryObtainMacAddress() {
        this.changeStatus(ProvisioningStatus.OBTAINING_MAC_ADDRESS)
        vrServer.serialHandler.infoRequest()
    }

    fun tryProvisioning() {
        this.changeStatus(ProvisioningStatus.PROVISIONING)
        vrServer.serialHandler.setWifi(this.ssid!!, this.password!!)
    }


    fun provisioningTick() {
        if (this.provisioningStatus == ProvisioningStatus.OBTAINING_MAC_ADDRESS) this.tryObtainMacAddress()

        if (!hasLogs && this.provisioningStatus == ProvisioningStatus.OBTAINING_MAC_ADDRESS && System.currentTimeMillis() - this.lastStatusChange > 1000
        ) {
            this.changeStatus(ProvisioningStatus.NO_SERIAL_LOGS_ERROR)
            return
        }

        if (this.provisioningStatus == ProvisioningStatus.SERIAL_INIT && vrServer.serialHandler.knownPorts.findAny()
                .isEmpty
            && System.currentTimeMillis() - this.lastStatusChange > 15000
        ) {
            this.changeStatus(ProvisioningStatus.NO_SERIAL_DEVICE_FOUND)
            return
        }

        if (System.currentTimeMillis() - this.lastStatusChange
            > this.provisioningStatus.timeout
        ) {
            if (this.provisioningStatus == ProvisioningStatus.NONE
                || this.provisioningStatus == ProvisioningStatus.SERIAL_INIT
            ) this.initSerial(this.preferredPort)
            else if (this.provisioningStatus == ProvisioningStatus.CONNECTING) this.changeStatus(ProvisioningStatus.CONNECTION_ERROR)
            else if (this.provisioningStatus == ProvisioningStatus.LOOKING_FOR_SERVER) this.changeStatus(
                ProvisioningStatus.COULD_NOT_FIND_SERVER
            )
            else if (!this.provisioningStatus.isError) {
                this.changeStatus(ProvisioningStatus.CONNECTION_ERROR) // TIMEOUT
            }
        }
    }


    override fun onSerialConnected(port: SerialPort) {
        if (!isRunning) return
        this.tryObtainMacAddress()
    }

    override fun onSerialDisconnected() {
        if (!isRunning) return
        this.changeStatus(ProvisioningStatus.NONE)
        this.connectRetries = 0
    }

    override fun onSerialLog(str: String, server: Boolean) {
        if (!isRunning) return
        if (!server) {
            this.hasLogs = true
            if (provisioningStatus == ProvisioningStatus.NO_SERIAL_LOGS_ERROR) {
                // Recover the onboarding process if the user turned on the
                // tracker afterward
                this.changeStatus(ProvisioningStatus.OBTAINING_MAC_ADDRESS)
            }
        }

        if (provisioningStatus == ProvisioningStatus.OBTAINING_MAC_ADDRESS && str.contains("mac:")
        ) {
            val match = Regex("mac: (?<mac>([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})), ")
                .find(str, str.indexOf("mac:"))

            if (match != null) {
                val b = match.groups[1]
                if (b != null) {
                    vrServer.configManager.vrConfig.addKnownDevice(b.value)
                    vrServer.configManager.saveConfig()
                    this.tryProvisioning()
                }
            }
        }

        if (provisioningStatus == ProvisioningStatus.PROVISIONING
            && str.contains("New wifi credentials set")
        ) {
            this.changeStatus(ProvisioningStatus.CONNECTING)
        }

        if (provisioningStatus == ProvisioningStatus.CONNECTING
            && (str.contains("Looking for the server")
                    || str.contains("Searching for the server"))
        ) {
            this.changeStatus(ProvisioningStatus.LOOKING_FOR_SERVER)
        }

        if (provisioningStatus == ProvisioningStatus.LOOKING_FOR_SERVER
            && str.contains("Handshake successful")
        ) {
            this.changeStatus(ProvisioningStatus.DONE)
        }

        if (provisioningStatus == ProvisioningStatus.CONNECTING
            && str.contains("Can't connect from any credentials")
        ) {
            if (++connectRetries >= MAX_CONNECTION_RETRIES) {
                this.changeStatus(ProvisioningStatus.CONNECTION_ERROR)
            } else {
                this.vrServer.serialHandler.rebootRequest()
            }
        }
    }

    fun changeStatus(status: ProvisioningStatus) {
        if (this.provisioningStatus != status) {
            this.lastStatusChange = System.currentTimeMillis()
            this.listeners
                .forEach(
                    Consumer { l: ProvisioningListener? ->
                        l!!
                            .onProvisioningStatusChange(status, vrServer.serialHandler.getCurrentPort())
                    }
                )
            this.provisioningStatus = status
        }
    }

    override fun onNewSerialDevice(port: SerialPort) {
        if (!isRunning) return
        this.initSerial(this.preferredPort)
    }

    fun addListener(channel: ProvisioningListener) {
        this.listeners.add(channel)
    }

    fun removeListener(l: ProvisioningListener) {
        listeners.removeIf { listener: ProvisioningListener? -> l === listener }
    }

    override fun onSerialDeviceDeleted(port: SerialPort) {
    }
}
