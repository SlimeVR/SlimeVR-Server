package dev.slimevr.desktop.tracking.trackers.hid

import dev.slimevr.VRServer
import dev.slimevr.config.config
import dev.slimevr.tracking.trackers.Device
import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.TrackerStatus
import dev.slimevr.tracking.trackers.hid.HIDCommon
import dev.slimevr.tracking.trackers.hid.HIDCommon.Companion.HID_TRACKER_PID
import dev.slimevr.tracking.trackers.hid.HIDCommon.Companion.HID_TRACKER_RECEIVER_PID
import dev.slimevr.tracking.trackers.hid.HIDCommon.Companion.HID_TRACKER_RECEIVER_VID
import dev.slimevr.tracking.trackers.hid.HIDCommon.Companion.PACKET_SIZE
import dev.slimevr.tracking.trackers.hid.HIDDevice
import io.eiren.util.logging.LogManager
import org.hid4java.HidDevice
import org.hid4java.HidException
import org.hid4java.HidManager
import org.hid4java.HidServices
import org.hid4java.HidServicesListener
import org.hid4java.HidServicesSpecification
import org.hid4java.event.HidServicesEvent
import org.hid4java.jna.HidApi
import org.hid4java.jna.HidDeviceInfoStructure
import java.nio.ByteBuffer
import java.util.function.Consumer

/**
 * Handles desktop USB HID dongles and receives tracker data from them.
 */
class DesktopHIDManager(name: String, private val trackersConsumer: Consumer<Tracker>) :
	Thread(name),
	HidServicesListener {
	private val devices: MutableList<HIDDevice> = mutableListOf()
	private val devicesBySerial: MutableMap<String, MutableList<Int>> = HashMap()
	private val devicesByHID: MutableMap<HidDevice, MutableList<Int>> = HashMap()
	private val lastDataByHID: MutableMap<HidDevice, Int> = HashMap()
	private val hidServicesSpecification = HidServicesSpecification()
	private var hidServices: HidServices? = null

	init {
		hidServicesSpecification.setAutoStart(false)
		try {
			hidServices = HidManager.getHidServices(hidServicesSpecification)
			hidServices?.addHidServicesListener(this)
			val dataReadThread = Thread(dataReadRunnable)
			dataReadThread.isDaemon = true
			dataReadThread.name = "hid4java data reader"
			dataReadThread.start()
			// We use hid4java but actually do not start the service ever, because it will just enumerate everything and cause problems
			// Do enumeration ourself
			val deviceEnumerateThread = Thread(deviceEnumerateRunnable)
			deviceEnumerateThread.isDaemon = true
			deviceEnumerateThread.name = "hid4java device enumerator"
			deviceEnumerateThread.start()
		} catch (e: HidException) {
			LogManager.severe("Error initializing HID services: ${e.message}", e)
		}
	}

	private fun checkConfigureDevice(hidDevice: HidDevice) {
		if (hidDevice.vendorId == HID_TRACKER_RECEIVER_VID && (hidDevice.productId == HID_TRACKER_RECEIVER_PID || hidDevice.productId == HID_TRACKER_PID)) { // TODO: Use list of valid ids
			val serial = hidDevice.serialNumber ?: "Unknown HID Device"
			if (hidDevice.isClosed) {
				if (!hidDevice.open()) {
					LogManager.warning("[TrackerServer] Unable to open device: $serial")
					return
				}
			}
			// TODO: Configure the device here
			// val product = hidDevice.product
			// val manufacturer = hidDevice.manufacturer
			this.devicesBySerial[serial]?.let {
				this.devicesByHID[hidDevice] = it
				synchronized(this.devices) {
					for (id in it) {
						val device = this.devices[id]
						for (value in device.trackers.values) {
							if (value.status == TrackerStatus.DISCONNECTED) value.status = TrackerStatus.OK
						}
					}
				}
				LogManager.info("[TrackerServer] Linked HID device reattached: $serial")
				return
			}

			// TODO: Need to check that the hidDevice is the same or different?
			// TODO: Get firmware and manufacturer from the device?
			val list: MutableList<Int> = mutableListOf()
			this.devicesBySerial[serial] = list
			this.devicesByHID[hidDevice] = list
			this.lastDataByHID[hidDevice] = 0 // initialize last data received
			LogManager.info("[TrackerServer] (Probably) Compatible HID device detected: $serial")
		}
	}

	private fun removeDevice(hidDevice: HidDevice) {
		this.devicesByHID[hidDevice]?.let {
			synchronized(this.devices) {
				for (id in it) {
					val device = this.devices[id]
					for (value in device.trackers.values) {
						if (value.status == TrackerStatus.OK) {
							value.status =
								TrackerStatus.DISCONNECTED
						}
					}
				}
			}
			this.devicesByHID.remove(hidDevice)
			LogManager.info("[TrackerServer] Linked HID device removed: ${hidDevice.serialNumber}")
		}
	}

	@get:Synchronized
	private val dataReadRunnable: Runnable
		get() = Runnable {
			while (true) {
				try {
					sleep(0) // Possible performance impact
				} catch (e: InterruptedException) {
					currentThread().interrupt()
					break
				}
				dataRead() // not in try catch?
			}
		}

	@get:Synchronized
	private val deviceEnumerateRunnable: Runnable
		get() = Runnable {
			try {
				sleep(100) // Delayed start
			} catch (e: InterruptedException) {
				currentThread().interrupt()
				return@Runnable
			}
			while (true) {
				try {
					sleep(1000)
				} catch (e: InterruptedException) {
					currentThread().interrupt()
					break
				}
				deviceEnumerate() // not in try catch?
			}
		}

	private fun dataRead() {
		synchronized(devicesByHID) {
			var devicesPresent = false
			var devicesDataReceived = false
			val q = intArrayOf(0, 0, 0, 0)
			val a = intArrayOf(0, 0, 0)
			val m = intArrayOf(0, 0, 0)
			for ((hidDevice, deviceList) in devicesByHID) {
				val dataReceived: ByteArray = try {
					hidDevice.readAll(0) // multiples 64 bytes
				} catch (e: NegativeArraySizeException) {
					continue // Skip devices with read error (Maybe disconnected)
				}
				devicesPresent = true // Even if the device has no data
				if (dataReceived.isNotEmpty()) {
					// Process data
					// The data is always received as 64 bytes, this check no longer works
					if (dataReceived.size % PACKET_SIZE != 0) {
						LogManager.info("[TrackerServer] Malformed HID packet, ignoring")
						continue // Don't continue with this data
					}
					devicesDataReceived = true // Data is received and is valid (not malformed)
					lastDataByHID[hidDevice] = 0 // reset last data received
					val packetCount = dataReceived.size / PACKET_SIZE
					var i = 0
					while (i < packetCount * PACKET_SIZE) {
						// Common packet data
						val packetType = dataReceived[i].toUByte().toInt()
						val id = dataReceived[i + 1].toUByte().toInt()
						val deviceId = id

						// Register device
						if (packetType == 255) { // device register packet from receiver
							val buffer = ByteBuffer.wrap(dataReceived, i + 2, 8)
							buffer.order(java.nio.ByteOrder.LITTLE_ENDIAN)
							val addr = buffer.getLong() and 0xFFFFFFFFFFFF
							val deviceName = String.format("%012X", addr)
							HIDCommon.deviceIdLookup(devices, hidDevice.serialNumber, deviceId, deviceName, deviceList) // register device
							// server wants tracker to be unique, so use combination of hid serial and full id
							i += PACKET_SIZE
							continue
						}

						val device: HIDDevice? = HIDCommon.deviceIdLookup(devices, hidDevice.serialNumber, deviceId, null, deviceList)
						if (device == null) { // not registered yet
							i += PACKET_SIZE
							continue
						}

						HIDCommon.processPacket(dataReceived, i, packetType, device, q, a, m, trackersConsumer)
						i += PACKET_SIZE
					}
					// LogManager.info("[TrackerServer] HID received $packetCount tracker packets")
				} else {
					lastDataByHID[hidDevice] = lastDataByHID[hidDevice]!! + 1 // increment last data received
				}
			}
			if (!devicesPresent) {
				sleep(10) // No hid device, "empty loop" so sleep to save the poor cpu
			} else if (!devicesDataReceived) {
				sleep(1) // read has no timeout, no data also causes an "empty loop"
			}
		}
	}

	private fun deviceEnumerate() {
		var rootReceivers: HidDeviceInfoStructure? = null
		var rootTrackers: HidDeviceInfoStructure? = null
		val trackersOverHID: Boolean = VRServer.instance.configManager.vrConfig.hidConfig.trackersOverHID
		try {
			rootReceivers = HidApi.enumerateDevices(HID_TRACKER_RECEIVER_VID, HID_TRACKER_RECEIVER_PID) // TODO: Use list of ids
			rootTrackers = if (trackersOverHID) {
				HidApi.enumerateDevices(HID_TRACKER_RECEIVER_VID, HID_TRACKER_PID)
			} else {
				null
			} // TODO: Use list of ids
		} catch (e: Throwable) {
			LogManager.severe("[TrackerServer] Couldn't enumerate HID devices", e)
		}
		var root: HidDeviceInfoStructure? = rootReceivers
		if (root == null) {
			root = rootTrackers
		} else {
			var last: HidDeviceInfoStructure = root
			while (last.hasNext()) {
				last = last.next()
			}
			last.next = rootTrackers
		}
		val hidDeviceList: MutableList<HidDevice> = mutableListOf()
		if (root != null) {
			var hidDeviceInfoStructure: HidDeviceInfoStructure? = root
			do {
				hidDeviceList.add(HidDevice(hidDeviceInfoStructure, null, hidServicesSpecification))
				hidDeviceInfoStructure = hidDeviceInfoStructure?.next()
			} while (hidDeviceInfoStructure != null)
			HidApi.freeEnumeration(root)
		}
		synchronized(devicesByHID) {
			// Work on devicesByHid and add/remove as necessary
			val removeList: MutableList<HidDevice> = devicesByHID.keys.toMutableList()
			removeList.removeAll(hidDeviceList)
			for (device in removeList) {
				removeDevice(device)
			}
			// Quickly reattaching a device may not be detected, so always try to open existing devices
			for (device in devicesByHID.keys) {
				// a receiver sends keep-alive data at 10 packets/s
				if (lastDataByHID[device]!! > 100) { // try to reopen device if no data was received recently (about >100ms)
					LogManager.info("[TrackerServer] Reopening device ${device.serialNumber} after no data received")
					device.open()
				}
			}
			hidDeviceList.removeAll(devicesByHID.keys) // addList
			for (device in hidDeviceList) {
				checkConfigureDevice(device)
			}
		}
	}

	override fun run() { // Doesn't seem to run
	}

	fun getDevices(): List<Device> = devices

	// We don't use these
	override fun hidDeviceAttached(event: HidServicesEvent) {
	}

	override fun hidDeviceDetached(event: HidServicesEvent) {
	}

	override fun hidFailure(event: HidServicesEvent) {
	}

	override fun hidDataReceived(p0: HidServicesEvent?) {
	}

	companion object {
		private const val resetSourceName = "TrackerServer"
	}
}
