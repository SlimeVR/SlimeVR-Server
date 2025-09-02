package dev.slimevr.android.tracking.trackers.hid

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import dev.slimevr.VRServer
import dev.slimevr.tracking.trackers.Device
import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.TrackerStatus
import dev.slimevr.tracking.trackers.hid.HIDCommon
import dev.slimevr.tracking.trackers.hid.HIDCommon.Companion.HID_TRACKER_RECEIVER_PID
import dev.slimevr.tracking.trackers.hid.HIDCommon.Companion.HID_TRACKER_RECEIVER_VID
import dev.slimevr.tracking.trackers.hid.HIDCommon.Companion.PACKET_SIZE
import dev.slimevr.tracking.trackers.hid.HIDDevice
import io.eiren.util.logging.LogManager
import java.nio.ByteBuffer
import java.util.function.Consumer

const val ACTION_USB_PERMISSION = "dev.slimevr.USB_PERMISSION"

/**
 * Receives trackers data by UDP using extended owoTrack protocol.
 */
class TrackersHID(
	name: String,
	private val trackersConsumer: Consumer<Tracker>,
	private val context: Context,
) : Thread(name) {
	private val devices: MutableList<HIDDevice> = mutableListOf()
	private val devicesBySerial: MutableMap<String, MutableList<Int>> = HashMap()
	private val devicesByHID: MutableMap<UsbDevice, MutableList<Int>> = HashMap()
	private val devicesByConn: MutableMap<UsbDevice, UsbDeviceHID> = HashMap()
	private val lastDataByHID: MutableMap<UsbDevice, Int> = HashMap()
	private val usbManager: UsbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager

	init {
		// TODO: Implement device enumeration and data reading using Android USB Host API
		//  For now, starting the threads as in the original hid4java implementation
		val dataReadThread = Thread(dataReadRunnable)
		dataReadThread.isDaemon = true
		dataReadThread.name = "Android USB HID data reader"
		dataReadThread.start()

		val deviceEnumerateThread = Thread(deviceEnumerateRunnable)
		deviceEnumerateThread.isDaemon = true
		deviceEnumerateThread.name = "Android USB HID device enumerator"
		deviceEnumerateThread.start()
	}

	private fun proceedWithDeviceConfiguration(hidDevice: UsbDevice) {
		// This is the original logic from checkConfigureDevice after permission is confirmed
		// TODO: Open UsbDeviceConnection here
		LogManager.info("[TrackerServer] USB Permission granted for ${hidDevice.deviceName}. Proceeding with configuration.")

		val serial = hidDevice.serialNumber ?: "Unknown USB Device ${hidDevice.deviceId}"
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

		val list: MutableList<Int> = mutableListOf()
		this.devicesBySerial[serial] = list
		this.devicesByHID[hidDevice] = list
		this.lastDataByHID[hidDevice] = 0 // initialize last data received
		LogManager.info("[TrackerServer] (Probably) Compatible HID device detected: $serial")

		// Close any existing connection (do we still have one?)
		this.devicesByConn[hidDevice]?.close()
		// Open new HID connection with USB device
		this.devicesByConn[hidDevice] = UsbDeviceHID(hidDevice, usbManager)
	}

	fun checkConfigureDevice(usbDevice: UsbDevice) {
		if (usbDevice.vendorId == HID_TRACKER_RECEIVER_VID && usbDevice.productId == HID_TRACKER_RECEIVER_PID) {
			if (usbManager.hasPermission(usbDevice)) {
				LogManager.info("[TrackerServer] Already have permission for ${usbDevice.deviceName}")
				proceedWithDeviceConfiguration(usbDevice)
			} else {
				LogManager.info("[TrackerServer] Requesting permission for ${usbDevice.deviceName}")
				val permissionIntent = PendingIntent.getBroadcast(
					context,
					0,
					Intent(ACTION_USB_PERMISSION).apply { setPackage(context.packageName) }, // Explicitly set package
					PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
				)
				usbManager.requestPermission(usbDevice, permissionIntent)
			}
		}
	}

	private fun removeDevice(hidDevice: UsbDevice) {
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

	private fun deviceIdLookup(hidDevice: UsbDevice, deviceId: Int, deviceName: String? = null, deviceList: MutableList<Int>): HIDDevice? {
		synchronized(this.devices) {
			deviceList.map { this.devices[it] }.find { it.hidId == deviceId }?.let { return it }
			if (deviceName == null) { // not registered yet
				return null
			}
			val device = HIDDevice(deviceId)
			// server wants tracker to be unique, so use combination of hid serial and full id // TODO: use the tracker "address" instead
			// TODO: the server should not setup any device, only when the receiver associates the id with the tracker "address" and sends this packet (0xff?) which it will do occasionally
			// device.name = hidDevice.serialNumber ?: "Unknown HID Device"
			// device.name += "-$deviceId"
			device.name = deviceName
			device.manufacturer = "HID Device" // TODO:
			// device.manufacturer = hidDevice.manufacturer ?: "HID Device"
// 			device.hardwareIdentifier = hidDevice.serialNumber // hardwareIdentifier is not used to identify the tracker, so also display the receiver serial
// 			device.hardwareIdentifier += "-$deviceId/$deviceName" // receiver serial + assigned id in receiver + device address
			device.hardwareIdentifier = deviceName // the rest of identifier wont fit in gui
			this.devices.add(device)
			deviceList.add(this.devices.size - 1)
			VRServer.instance.deviceManager.addDevice(device) // actually add device to the server
			LogManager
				.info(
					"[TrackerServer] Added device $deviceName for ${hidDevice.serialNumber}, id $deviceId",
				)
			return device
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
				val dataReceived = ByteArray(64)
				val conn = devicesByConn[hidDevice]!!
				val dataRead = conn.deviceConnection.bulkTransfer(conn.endpointIn, dataReceived, dataReceived.size, 0)

				// LogManager.info("[TrackerServer] HID data read ($dataRead bytes): ${dataReceived.contentToString()}")

				devicesPresent = true // Even if the device has no data
				if (dataRead > 0) {
					// Process data
					// The data is always received as 64 bytes, this check no longer works
					if (dataRead % PACKET_SIZE != 0) {
						LogManager.info("[TrackerServer] Malformed HID packet, ignoring")
						continue // Don't continue with this data
					}
					devicesDataReceived = true // Data is received and is valid (not malformed)
					lastDataByHID[hidDevice] = 0 // reset last data received
					val packetCount = dataRead / PACKET_SIZE
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
							deviceIdLookup(hidDevice, deviceId, deviceName, deviceList) // register device
							// server wants tracker to be unique, so use combination of hid serial and full id
							i += PACKET_SIZE
							continue
						}

						val device: HIDDevice? = deviceIdLookup(hidDevice, deviceId, null, deviceList)
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
		val hidDeviceList: MutableList<UsbDevice> = usbManager.deviceList.values.filter {
			it.vendorId == HID_TRACKER_RECEIVER_VID && it.productId == HID_TRACKER_RECEIVER_PID
		}.toMutableList()
		synchronized(devicesByHID) {
			// Work on devicesByHid and add/remove as necessary
			val removeList: MutableList<UsbDevice> = devicesByHID.keys.toMutableList()
			removeList.removeAll(hidDeviceList)
			for (device in removeList) {
				removeDevice(device)
			}
			// Quickly reattaching a device may not be detected, so always try to open existing devices
			for (device in devicesByHID.keys) {
				// a receiver sends keep-alive data at 10 packets/s
				if (lastDataByHID[device]!! > 100) { // try to reopen device if no data was received recently (about >100ms)
					LogManager.info("[TrackerServer] Reopening device ${device.serialNumber} after no data received")
					// TODO Try to re-check permission or re-open. For now, just logging.
					//  Re-calling checkConfigureDevice might be an option if it's safe
					//  to do so (e.g., doesn't create duplicate entries).
				}
			}
			hidDeviceList.removeAll(devicesByHID.keys) // addList
			for (device in hidDeviceList) {
				checkConfigureDevice(device) // This will handle permission check/request
			}
		}
	}

	override fun run() { // Doesn't seem to run
	}

	fun getDevices(): List<Device> = devices

	companion object {
		private const val resetSourceName = "TrackerServer"
	}
}
