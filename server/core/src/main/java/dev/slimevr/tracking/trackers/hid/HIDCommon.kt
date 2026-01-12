package dev.slimevr.tracking.trackers.hid

import com.jme3.math.FastMath
import dev.slimevr.VRServer
import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.TrackerStatus
import dev.slimevr.tracking.trackers.udp.BoardType
import dev.slimevr.tracking.trackers.udp.IMUType
import dev.slimevr.tracking.trackers.udp.MCUType
import dev.slimevr.tracking.trackers.udp.MagnetometerStatus
import io.eiren.util.logging.LogManager
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Quaternion.Companion.fromRotationVector
import io.github.axisangles.ktmath.Vector3
import java.nio.ByteBuffer
import java.util.function.Consumer
import kotlin.collections.set
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * A collection of shared HID functions between OS specific HID implementations.
 */
class HIDCommon {
	companion object {
		const val HID_TRACKER_RECEIVER_VID = 0x1209
		const val HID_TRACKER_RECEIVER_PID = 0x7690
		const val HID_TRACKER_PID = 0x7692

		const val PACKET_SIZE = 16

		private val AXES_OFFSET = fromRotationVector(-FastMath.HALF_PI, 0f, 0f)

		fun deviceIdLookup(
			hidDevices: MutableList<HIDDevice>,
			hidSerialNumber: String?,
			deviceId: Int,
			deviceName: String? = null,
			deviceList: MutableList<Int>,
		): HIDDevice? {
			synchronized(hidDevices) {
				deviceList.map { hidDevices[it] }.find { it.hidId == deviceId }?.let { return it }
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
				hidDevices.add(device)
				deviceList.add(hidDevices.size - 1)
				VRServer.instance.deviceManager.addDevice(device) // actually add device to the server
				LogManager
					.info(
						"[TrackerServer] Added device $deviceName for ${hidSerialNumber ?: "Unknown HID Device"}, id $deviceId",
					)
				return device
			}
		}

		private fun setUpSensor(
			device: HIDDevice,
			trackerId: Int,
			sensorType: IMUType,
			sensorStatus: TrackerStatus,
			magStatus: MagnetometerStatus,
			trackersConsumer: Consumer<Tracker>,
		) {
			// LogManager.info("[TrackerServer] Sensor $trackerId for ${device.name}, status $sensorStatus")
			var imuTracker = device.getTracker(trackerId)
			if (imuTracker == null) {
				var formattedHWID = device.hardwareIdentifier.replace(":", "").takeLast(5)
				imuTracker = Tracker(
					device,
					VRServer.getNextLocalTrackerId(),
					device.name + "/" + trackerId,
					"Tracker $formattedHWID",
					null,
					trackerNum = trackerId,
					hasRotation = true,
					hasAcceleration = true,
					userEditable = true,
					imuType = sensorType,
					allowFiltering = true,
					allowReset = true,
					allowMounting = true,
					usesTimeout = false,
					magStatus = magStatus,
				)
				// usesTimeout false because HID trackers aren't "Disconnected" unless receiver is physically removed probably
				// TODO: Could tracker maybe use "Timed out" status without marking as disconnecting?
				// TODO: can be marked as "Disconnected" by timeout if the tracker has enabled activity timeouts
				device.trackers[trackerId] = imuTracker
				trackersConsumer.accept(imuTracker)
				imuTracker.status = sensorStatus
				LogManager
					.info(
						"[TrackerServer] Added sensor $trackerId for ${device.name}, type $sensorType",
					)
			}
		}

		fun processPacket(
			dataReceived: ByteArray,
			i: Int,
			packetType: Int,
			device: HIDDevice,
			q: IntArray,
			a: IntArray,
			m: IntArray,
			trackersConsumer: Consumer<Tracker>,
		) {
			val trackerId = 0 // no concept of extensions

			// Register tracker
			if (packetType == 0) { // Tracker register packet (device info)
				val imu_id = dataReceived[i + 8].toUByte().toInt()
				val mag_id = dataReceived[i + 9].toUByte().toInt()
				val sensorType = IMUType.getById(imu_id.toUInt())
				// only able to register magnetometer status, not magnetometer type
				val magStatus = MagnetometerStatus.getById(mag_id.toUByte())
				if (sensorType != null && magStatus != null) {
					setUpSensor(device, trackerId, sensorType, TrackerStatus.OK, magStatus, trackersConsumer)
				}
			}

			val tracker: Tracker? = device.getTracker(trackerId)
			if (tracker == null) { // not registered yet
				return
			}

			// Packet data
			var runtime: Long? = null
			var batt: Int? = null
			var batt_v: Int? = null
			var temp: Int? = null
			var brd_id: Int? = null
			var mcu_id: Int? = null
			var button: Int? = null
			// var imu_id: Int? = null
			// var mag_id: Int? = null
			var fw_date: Int? = null
			var fw_major: Int? = null
			var fw_minor: Int? = null
			var fw_patch: Int? = null
			var svr_status: Int? = null
			// var status: Int? = null // raw status from tracker
			var rssi: Int? = null
			var packets_received: Int? = null
			var packets_lost: Int? = null
			var windows_hit: Int? = null
			var windows_missed: Int? = null

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
					// ushort little endian
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
						// Q15 as short little endian
						q[j] = dataReceived[i + 2 + j * 2 + 1].toInt() shl 8 or dataReceived[i + 2 + j * 2].toUByte().toInt()
					}
					for (j in 0..2) { // accel received as fixed 7, in m/s^2
						// Q7 as short little endian
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
					for (j in 0..2) { // accel received as fixed 7, in m/s^2
						// Q7 as short little endian
						a[j] = dataReceived[i + 9 + j * 2 + 1].toInt() shl 8 or dataReceived[i + 9 + j * 2].toUByte().toInt()
					}
					rssi = dataReceived[i + 15].toUByte().toInt()
				}

				3 -> { // status
					svr_status = dataReceived[i + 2].toUByte().toInt()
					// status = dataReceived[i + 3].toUByte().toInt()
					packets_received = dataReceived[i + 4].toUByte().toInt()
					packets_lost = dataReceived[i + 5].toUByte().toInt()
					windows_hit = dataReceived[i + 6].toUByte().toInt()
					windows_missed = dataReceived[i + 7].toUByte().toInt()
					rssi = dataReceived[i + 15].toUByte().toInt()
				}

				4 -> { // full precision quat and mag, no extra data
					for (j in 0..3) { // quat received as fixed Q15
						// Q15 as short little endian
						q[j] = dataReceived[i + 2 + j * 2 + 1].toInt() shl 8 or dataReceived[i + 2 + j * 2].toUByte().toInt()
					}
					for (j in 0..2) { // mag received as fixed 10, in gauss
						// Q10 as short little endian
						m[j] = dataReceived[i + 10 + j * 2 + 1].toInt() shl 8 or dataReceived[i + 10 + j * 2].toUByte().toInt()
					}
				}

				5 -> { // runtime
					// ulong as little endian
					runtime = (dataReceived[i + 9].toUByte().toLong() shl 56) or (dataReceived[i + 8].toUByte().toLong() shl 48) or (dataReceived[i + 7].toUByte().toLong() shl 40) or (dataReceived[i + 6].toUByte().toLong() shl 32) or (dataReceived[i + 5].toUByte().toLong() shl 24) or (dataReceived[i + 4].toUByte().toLong() shl 16) or (dataReceived[i + 3].toUByte().toLong() shl 8) or dataReceived[i + 2].toUByte().toLong()
				}

				6 -> { // data
					button = dataReceived[i + 2].toUByte().toInt()
					rssi = dataReceived[i + 15].toUByte().toInt()
				}

				7 -> { // reduced precision quat and accel with data
					button = dataReceived[i + 2].toUByte().toInt()
					// quaternion is quantized as exponential map
					// X = 10 bits, Y/Z = 11 bits
					val buffer = ByteBuffer.wrap(dataReceived, i + 5, 4)
					buffer.order(java.nio.ByteOrder.LITTLE_ENDIAN)
					val q_buf = buffer.getInt().toUInt()
					q[0] = (q_buf and 1023u).toInt()
					q[1] = (q_buf shr 10 and 2047u).toInt()
					q[2] = (q_buf shr 21 and 2047u).toInt()
					for (j in 0..2) { // accel received as fixed 7, in m/s^2
						// Q7 as short little endian
						a[j] = dataReceived[i + 9 + j * 2 + 1].toInt() shl 8 or dataReceived[i + 9 + j * 2].toUByte().toInt()
					}
					rssi = dataReceived[i + 15].toUByte().toInt()
				}

				else -> {
				}
			}

			// Assign data
			if (runtime != null && runtime >= 0) {
				tracker.batteryRemainingRuntime = runtime
			}
			// -1: Not known (e.g. not yet calculated after wake up, reusing known value is okay), 0: N/A (e.g. charging)
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
			if (button != null) {
				if (tracker.button == null) {
					tracker.button = 0
				}
				if (button != tracker.button) {
					button = button and tracker.button!!.inv()
					// Nothing to do now..
				}
			}
			if (fw_date != null) {
				val firmwareYear = 2020 + (fw_date shr 9 and 127)
				val firmwareMonth = fw_date shr 5 and 15
				val firmwareDay = fw_date and 31
				device.firmwareDate = String.format("%04d-%02d-%02d", firmwareYear, firmwareMonth, firmwareDay)
			}
			if (fw_major != null && fw_minor != null && fw_patch != null) {
				device.firmwareVersion = "$fw_major.$fw_minor.$fw_patch"
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
			if (packets_received != null && packets_lost != null) {
				tracker.packetsReceived = packets_received
				tracker.packetsLost = packets_lost
				tracker.packetLoss = if (packets_lost == 0) 0.0f else packets_lost.toFloat() / (packets_received + packets_lost).toFloat()
			}

			// Assign rotation and acceleration
			if (packetType == 1 || packetType == 4) {
				// The data comes in the same order as in the UDP protocol
				// x y z w -> w x y z
				var rot = Quaternion(q[3].toFloat(), q[0].toFloat(), q[1].toFloat(), q[2].toFloat())
				val scaleRot = 1 / (1 shl 15).toFloat() // compile time evaluation
				rot = AXES_OFFSET.times(scaleRot).times(rot) // no division
				tracker.setRotation(rot)
			}
			if (packetType == 2 || packetType == 7) {
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
			if (packetType == 1 || packetType == 2 || packetType == 7) {
				// Acceleration is in local device frame
				// On flat surface / face up:
				// Right side of the device is +X
				// Front side (facing up) is +Z
				// Mounted on body / standing up:
				// Top side of the device is +Y
				// Front side (facing out) is +Z
				val scaleAccel = 1 / (1 shl 7).toFloat() // compile time evaluation
				val acceleration = Vector3(a[0].toFloat(), a[1].toFloat(), a[2].toFloat()).times(scaleAccel) // no division
				tracker.setAcceleration(acceleration)
			}
			if (packetType == 4) {
				// Magnetometer is in local device frame
				// On flat surface / face up:
				// Right side of the device is +X
				// Front side (facing up) is +Z
				// Mounted on body / standing up:
				// Top side of the device is +Y
				// Front side (facing out) is +Z
				val scaleMag = 1000 / (1 shl 10).toFloat() // compile time evaluation, and change gauss to milligauss
				val magnetometer = Vector3(m[0].toFloat(), m[1].toFloat(), m[2].toFloat()).times(scaleMag) // no division
				tracker.setMagVector(magnetometer)
			}
			if (packetType == 1 || packetType == 2 || packetType == 4 || packetType == 7) {
				tracker.dataTick() // only data tick if there is rotation data
			}
		}
	}
}
