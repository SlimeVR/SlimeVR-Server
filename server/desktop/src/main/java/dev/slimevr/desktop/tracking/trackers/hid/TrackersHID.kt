package dev.slimevr.desktop.tracking.trackers.hid

import com.jme3.math.FastMath
import dev.slimevr.VRServer
import dev.slimevr.tracking.trackers.Device
import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.TrackerStatus
import dev.slimevr.tracking.trackers.udp.BoardType
import dev.slimevr.tracking.trackers.udp.IMUType
import dev.slimevr.tracking.trackers.udp.MCUType
import io.eiren.util.logging.LogManager
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Quaternion.Companion.fromRotationVector
import io.github.axisangles.ktmath.Vector3
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
import kotlin.experimental.and
import kotlin.math.*

private const val HID_TRACKER_RECEIVER_VID = 0x1209
private const val HID_TRACKER_RECEIVER_PID = 0x7690

private const val PACKET_SIZE = 16

/**
 * Receives trackers data by UDP using extended owoTrack protocol.
 */
class TrackersHID(name: String, private val trackersConsumer: Consumer<Tracker>) :
	Thread(name),
	HidServicesListener {
	private val devices: MutableList<HIDDevice> = mutableListOf()
	private val devicesBySerial: MutableMap<String, MutableList<Int>> = HashMap()
	private val devicesByHID: MutableMap<HidDevice, MutableList<Int>> = HashMap()
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
		if (hidDevice.vendorId == HID_TRACKER_RECEIVER_VID && hidDevice.productId == HID_TRACKER_RECEIVER_PID) { // TODO: Use correct ids
			if (hidDevice.isClosed) {
				check(hidDevice.open()) { "Unable to open device" }
			}
			// TODO: Configure the device here
			val serial = hidDevice.serialNumber ?: "Unknown HID Device"
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

	private fun setUpSensor(device: HIDDevice, trackerId: Int, sensorType: IMUType, sensorStatus: TrackerStatus) {
		// LogManager.info("[TrackerServer] Sensor $trackerId for ${device.name}, status $sensorStatus")
		var imuTracker = device.getTracker(trackerId)
		if (imuTracker == null) {
			imuTracker = Tracker(
				device,
				VRServer.getNextLocalTrackerId(),
				device.name + "/" + trackerId,
				"IMU Tracker #" + VRServer.currentLocalTrackerId,
				null,
				trackerNum = trackerId,
				hasRotation = true,
				hasAcceleration = true,
				userEditable = true,
				imuType = sensorType,
				allowFiltering = true,
				needsReset = true,
				needsMounting = true,
				usesTimeout = false,
			)
			// usesTimeout false because HID trackers aren't "Disconnected" unless receiver is physically removed probably
			// TODO: Could tracker maybe use "Timed out" status without marking as disconnecting?
			device.trackers[trackerId] = imuTracker
			trackersConsumer.accept(imuTracker)
			imuTracker.status = sensorStatus
			LogManager
				.info(
					"[TrackerServer] Added sensor $trackerId for ${device.name}, type $sensorType",
				)
		}
	}

	private fun deviceIdLookup(hidDevice: HidDevice, deviceId: Int, deviceName: String? = null, deviceList: MutableList<Int>): HIDDevice? {
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
					val packetCount = dataReceived.size / PACKET_SIZE
					var i = 0
					while (i < packetCount * PACKET_SIZE) {
						// Common packet data
						val packetType = dataReceived[i].toUByte().toInt()
						val id = dataReceived[i + 1].toUByte().toInt()
						val trackerId = 0 // no concept of extensions
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

						// Register tracker
						if (packetType == 0) { // Tracker register packet (device info)
							val imu_id = dataReceived[i + 8].toUByte().toInt()
							val sensorType = IMUType.getById(imu_id.toUInt())
							if (sensorType != null) {
								setUpSensor(device, trackerId, sensorType!!, TrackerStatus.OK)
							}
						}

						var tracker: Tracker? = device.getTracker(trackerId)
						if (tracker == null) { // not registered yet
							i += PACKET_SIZE
							continue
						}

						// Packet data
						var batt: Int? = null
						var batt_v: Int? = null
						var temp: Int? = null
						var brd_id: Int? = null
						var mcu_id: Int? = null
						// var imu_id: Int? = null
						// var mag_id: Int? = null // not used currently
						var fw_date: Int? = null
						var fw_major: Int? = null
						var fw_minor: Int? = null
						var fw_patch: Int? = null
						var svr_status: Int? = null
						// var status: Int? = null // raw status from tracker
						var rssi: Int? = null

						// Tracker packets
						when (packetType) {
							0 -> { // device info
								batt = dataReceived[i + 2].toUByte().toInt()
								batt_v = dataReceived[i + 3].toUByte().toInt()
								temp = dataReceived[i + 4].toUByte().toInt()
								brd_id = dataReceived[i + 5].toUByte().toInt()
								mcu_id = dataReceived[i + 6].toUByte().toInt()
								// imu_id = dataReceived[i + 8].toUByte().toInt()
								// mag_id = dataReceived[i + 9].toUByte().toInt()
								// ushort big endian
								fw_date = dataReceived[i + 11].toUByte().toInt() shl 8 or dataReceived[i + 10].toUByte().toInt()
								fw_major = dataReceived[i + 12].toUByte().toInt()
								fw_minor = dataReceived[i + 13].toUByte().toInt()
								fw_patch = dataReceived[i + 14].toUByte().toInt()
								rssi = dataReceived[i + 15].toUByte().toInt()
							}

							1 -> { // full precision quat and accel, no extra data
								// Q15: 1 is represented as 0x7FFF, -1 as 0x8000
								// The sender can use integer saturation to avoid overflow
								for (j in 0..3) { // quat received as fixed Q15
									// Q15 as short big endian
									q[j] = dataReceived[i + 2 + j * 2 + 1].toInt() shl 8 or dataReceived[i + 2 + j * 2].toUByte().toInt()
								}
								for (j in 0..2) { // accel received as fixed 7, in m/s
									// Q7 as short big endian
									a[j] = dataReceived[i + 10 + j * 2 + 1].toInt() shl 8 or dataReceived[i + 10 + j * 2].toUByte().toInt()
								}
							}

							2 -> { // reduced precision quat and accel with data
								batt = dataReceived[i + 2].toUByte().toInt()
								batt_v = dataReceived[i + 3].toUByte().toInt()
								temp = dataReceived[i + 4].toUByte().toInt()
								// quaternion is quantized as exponential map
								// X = 10 bits, Y/Z = 11 bits
								val buffer = ByteBuffer.wrap(dataReceived, i + 5, 4)
								buffer.order(java.nio.ByteOrder.LITTLE_ENDIAN)
								val q_buf = buffer.getInt().toUInt()
								q[0] = (q_buf and 1023u).toInt()
								q[1] = (q_buf shr 10 and 2047u).toInt()
								q[2] = (q_buf shr 21 and 2047u).toInt()
								for (j in 0..2) { // accel received as fixed 7, in m/s
									// Q7 as short big endian
									a[j] = dataReceived[i + 9 + j * 2 + 1].toInt() shl 8 or dataReceived[i + 9 + j * 2].toUByte().toInt()
								}
								rssi = dataReceived[i + 15].toUByte().toInt()
							}

							3 -> { // status
								svr_status = dataReceived[i + 2].toUByte().toInt()
								// status = dataReceived[i + 3].toUByte().toInt()
								rssi = dataReceived[i + 15].toUByte().toInt()
							}

							else -> {
							}
						}

						// Assign data
						if (batt != null) {
							tracker.batteryLevel = if (batt == 128) 1f else (batt and 127).toFloat()
						}
						// Server still won't display battery at 0% at all
						if (batt_v != null) {
							tracker.batteryVoltage = (batt_v.toFloat() + 245f) / 100f
						}
						if (temp != null) {
							tracker.temperature = if (temp > 0) temp.toFloat() / 2f - 39f else null
						}
						// Range 1 - 255 -> -38.5 - +88.5 C
						if (brd_id != null) {
							val boardType = BoardType.getById(brd_id.toUInt())
							if (boardType != null) {
								device.boardType = boardType!!
							}
						}
						if (mcu_id != null) {
							val mcuType = MCUType.getById(mcu_id.toUInt())
							if (mcuType != null) {
								device.mcuType = mcuType!!
							}
						}
						if (fw_date != null && fw_major != null && fw_minor != null && fw_patch != null) {
							val firmwareYear = 2020 + (fw_date shr 9 and 127)
							val firmwareMonth = fw_date shr 5 and 15
							val firmwareDay = fw_date and 31
							val firmwareDate = String.format("%04d-%02d-%02d", firmwareYear, firmwareMonth, firmwareDay)
							device.firmwareVersion = "$fw_major.$fw_minor.$fw_patch (Build $firmwareDate)"
						}
						if (svr_status != null) {
							val status = TrackerStatus.getById(svr_status)
							if (status != null) {
								tracker.status = status!!
							}
						}
						if (rssi != null) {
							tracker.signalStrength = -rssi
						}

						// Assign rotation and acceleration
						if (packetType == 1) {
							// The data comes in the same order as in the UDP protocol
							// x y z w -> w x y z
							var rot = Quaternion(q[3].toFloat(), q[0].toFloat(), q[1].toFloat(), q[2].toFloat())
							val scaleRot = 1 / (1 shl 15).toFloat() // compile time evaluation
							rot = AXES_OFFSET.times(scaleRot).times(rot) // no division
							tracker.setRotation(rot)
						}
						if (packetType == 2) {
							val v = floatArrayOf(q[0].toFloat(), q[1].toFloat(), q[2].toFloat()) // used q array for quantized data
							v[0] /= (1 shl 10).toFloat()
							v[1] /= (1 shl 11).toFloat()
							v[2] /= (1 shl 11).toFloat()
							for (i in 0..2) {
								v[i] = v[i] * 2 - 1
							}
							// http://marc-b-reynolds.github.io/quaternions/2017/05/02/QuatQuantPart1.html#fnref:pos:3
							// https://github.com/Marc-B-Reynolds/Stand-alone-junk/blob/559bd78893a3a95cdee1845834c632141b945a45/src/Posts/quatquant0.c#L898
							val d = v[0] * v[0] + v[1] * v[1] + v[2] * v[2]
							val invSqrtD = 1 / sqrt(d + 1e-6f)
							val a = (PI.toFloat() / 2) * d * invSqrtD
							val s = sin(a)
							val k = s * invSqrtD
							var rot = Quaternion(cos(a), k * v[0], k * v[1], k * v[2])
							rot = AXES_OFFSET.times(rot) // no division
							tracker.setRotation(rot)
						}
						if (packetType == 1 || packetType == 2) {
							// TODO: Acceleration "was" wrong
							val scaleAccel = 1 / (1 shl 7).toFloat() // compile time evaluation
							var acceleration = Vector3(a[0].toFloat(), a[1].toFloat(), a[2].toFloat()).times(scaleAccel) // no division
							tracker.setAcceleration(acceleration)
							tracker.dataTick() // only data tick if there is rotation data
						}

						i += PACKET_SIZE
					}
					// LogManager.info("[TrackerServer] HID received $packetCount tracker packets")
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
		var root: HidDeviceInfoStructure? = null
		try {
			root = HidApi.enumerateDevices(HID_TRACKER_RECEIVER_VID, HID_TRACKER_RECEIVER_PID) // TODO: change to proper vendorId and productId, need to enum all appropriate productId
		} catch (e: Throwable) {
			LogManager.severe("[TrackerServer] Couldn't enumerate HID devices", e)
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
			hidDeviceList.removeAll(devicesByHID.keys) // addList
			for (device in removeList) {
				removeDevice(device)
			}
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
		/**
		 * Change between IMU axes and OpenGL/SteamVR axes
		 */
		private val AXES_OFFSET = fromRotationVector(-FastMath.HALF_PI, 0f, 0f)
		private const val resetSourceName = "TrackerServer"
	}
}
