package dev.slimevr.android.tracking.trackers.hid

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import androidx.core.content.ContextCompat
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
import java.nio.ByteBuffer
import java.util.function.Consumer

const val ACTION_USB_PERMISSION = "dev.slimevr.USB_PERMISSION"

/**
 * Handles Android USB Host HID dongles and receives tracker data from them.
 */
class AndroidHIDManager(
	name: String,
	private val trackersConsumer: Consumer<Tracker>,
	private val context: Context,
) : Thread(name) {
	private val devices: MutableList<HIDDevice> = mutableListOf()
	private val devicesBySerial: MutableMap<String, MutableList<Int>> = HashMap()
	private val devicesByHID: MutableMap<UsbDevice, MutableList<Int>> = HashMap()
	private val connByHID: MutableMap<UsbDevice, AndroidHIDDevice> = HashMap()
	private val lastDataByHID: MutableMap<UsbDevice, Int> = HashMap()
	private val usbManager: UsbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager

	val usbReceiver: BroadcastReceiver = object : BroadcastReceiver() {
		override fun onReceive(context: Context, intent: Intent) {
			when (intent.action) {
				UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
					(intent.getParcelableExtra(UsbManager.EXTRA_DEVICE) as UsbDevice?)?.let {
						checkConfigureDevice(it, requestPermission = true)
					}
				}

				UsbManager.ACTION_USB_DEVICE_DETACHED -> {
					(intent.getParcelableExtra(UsbManager.EXTRA_DEVICE) as UsbDevice?)?.let {
						removeDevice(it)
					}
				}

				ACTION_USB_PERMISSION -> {
					deviceEnumerate(false)
				}
			}
		}
	}

	private fun proceedWithDeviceConfiguration(hidDevice: UsbDevice) {
		// This is the original logic from checkConfigureDevice after permission is confirmed
		LogManager.info("[TrackerServer] USB Permission granted for ${hidDevice.deviceName}. Proceeding with configuration.")

		// Close any existing connection (do we still have one?)
		this.connByHID[hidDevice]?.close()
		// Open new HID connection with USB device
		this.connByHID[hidDevice] = AndroidHIDDevice(hidDevice, usbManager)

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
	}

	fun checkConfigureDevice(usbDevice: UsbDevice, requestPermission: Boolean = false) {
		if (usbDevice.vendorId == HID_TRACKER_RECEIVER_VID && (usbDevice.productId == HID_TRACKER_RECEIVER_PID || usbDevice.productId == HID_TRACKER_PID)) {
			if (usbManager.hasPermission(usbDevice)) {
				LogManager.info("[TrackerServer] Already have permission for ${usbDevice.deviceName}")
				proceedWithDeviceConfiguration(usbDevice)
			} else if (requestPermission) {
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

			val oldConn = this.connByHID.remove(hidDevice)
			val serial = oldConn?.serialNumber ?: "Unknown"
			oldConn?.close()

			LogManager.info("[TrackerServer] Linked HID device removed: $serial")
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
				val conn = connByHID[hidDevice]!!
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

	private fun deviceEnumerate(requestPermission: Boolean = false) {
		val trackersOverHID: Boolean = VRServer.instance.configManager.vrConfig.hidConfig.trackersOverHID
		val hidDeviceList: MutableList<UsbDevice> = usbManager.deviceList.values.filter {
			it.vendorId == HID_TRACKER_RECEIVER_VID && (it.productId == HID_TRACKER_RECEIVER_PID || (trackersOverHID && it.productId == HID_TRACKER_PID))
		}.toMutableList()
		synchronized(devicesByHID) {
			// Work on devicesByHid and add/remove as necessary
			val removeList: MutableList<UsbDevice> = devicesByHID.keys.toMutableList()
			removeList.removeAll(hidDeviceList)
			for (device in removeList) {
				removeDevice(device)
			}

			hidDeviceList.removeAll(devicesByHID.keys) // addList
			for (device in hidDeviceList) {
				// This will handle permission check/request
				checkConfigureDevice(device, requestPermission)
			}
		}
	}

	override fun run() {
		val intentFilter = IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED)
		intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
		intentFilter.addAction(ACTION_USB_PERMISSION)

		// Listen for USB device attach/detach
		ContextCompat.registerReceiver(
			context,
			usbReceiver,
			intentFilter,
			ContextCompat.RECEIVER_NOT_EXPORTED,
		)

		// Enumerate existing devices
		deviceEnumerate(true)

		// Data read loop
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

	fun getDevices(): List<Device> = devices

	companion object {
		private const val resetSourceName = "TrackerServer"
	}
}
