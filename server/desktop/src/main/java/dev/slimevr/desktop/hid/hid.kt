package dev.slimevr.desktop.hid

import dev.slimevr.AppLogger
import dev.slimevr.VRServer
import dev.slimevr.config.Settings
import dev.slimevr.device.DeviceActions
import dev.slimevr.hid.HIDReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import solarxr_protocol.datatypes.TrackerStatus
import org.hid4java.HidDevice
import org.hid4java.HidManager
import org.hid4java.HidServicesSpecification
import org.hid4java.jna.HidApi
import org.hid4java.jna.HidDeviceInfoStructure

private const val HID_POLL_INTERVAL_MS = 3000L

private const val HID_TRACKER_RECEIVER_VID = 0x1209
private const val HID_TRACKER_RECEIVER_PID = 0x7690
private const val HID_TRACKER_PID = 0x7692

private fun isCompatibleDevice(vid: Int, pid: Int) = vid == HID_TRACKER_RECEIVER_VID && (pid == HID_TRACKER_RECEIVER_PID || pid == HID_TRACKER_PID)

private val hidSpec = HidServicesSpecification().apply { isAutoStart = false }

// Initialize the native HID library. Must be called before enumerateDevices.
private val hidServices by lazy { HidManager.getHidServices(hidSpec) }

private fun enumerateCompatibleDevices(): Map<String, HidDevice> {
	hidServices // ensure native lib is loaded
	val root = HidApi.enumerateDevices(0, 0) ?: return emptyMap()
	val result = mutableMapOf<String, HidDevice>()
	var info: HidDeviceInfoStructure? = root
	while (info != null) {
		if (isCompatibleDevice(info.vendor_id.toInt(), info.product_id.toInt())) {
			val device = HidDevice(info, null, hidSpec)
			// Use path as key, unique per physical device, available without opening
			result[info.path] = device
		}
		info = info.next()
	}
	HidApi.freeEnumeration(root)
	return result
}

private data class ActiveReceiver(val job: Job, val receiver: HIDReceiver)

fun createDesktopHIDManager(serverContext: VRServer, settings: Settings, scope: CoroutineScope) {
	val active = mutableMapOf<String, ActiveReceiver>()

	scope.launch {
		while (isActive) {
			val found = withContext(Dispatchers.IO) {
				try {
					enumerateCompatibleDevices()
				} catch (_: Exception) {
					emptyMap()
				}
			}

			// Devices no longer present + jobs that exited on their own (read error)
			val toRemove = (active.keys - found.keys) +
				active.entries.filter { !it.value.job.isActive }.map { it.key }
			for (path in toRemove) {
				val entry = active.remove(path) ?: continue
				entry.job.cancel()
				entry.job.join()
				AppLogger.hid.info("HID device removed: $path")
			}

			// Open newly detected devices
			for ((path, hidDevice) in found) {
				if (path in active) continue

				if (!hidDevice.open()) {
					AppLogger.hid.warn("Failed to open HID device: $path")
					continue
				}

				val serial = hidDevice.serialNumber ?: path
				AppLogger.hid.info("HID device detected: $serial")

				val deviceJob = Job(scope.coroutineContext[Job])
				val deviceScope = CoroutineScope(scope.coroutineContext + deviceJob)

				val receiver = HIDReceiver.create(
					serialNumber = serial,
					serverContext = serverContext,
					settings = settings,
					scope = deviceScope,
				)

				deviceScope.launch {
					try {
						while (isActive) {
							val data = withContext(Dispatchers.IO) {
								try {
									hidDevice.readAll(0)
								} catch (_: Exception) {
									null
								}
							}
							when {
								data == null -> return@launch

								// read error, device gone
								data.isNotEmpty() -> parseHIDPackets(data).forEach { receiver.packetEvents.emit(it) }

								else -> delay(1) // no data yet, yield without busy-spinning
							}
						}
					} finally {
						withContext(NonCancellable + Dispatchers.IO) { hidDevice.close() }
						withContext(NonCancellable) {
							for (record in receiver.context.state.value.trackers.values) {
								serverContext.getDevice(record.deviceId)?.context?.dispatch(
									DeviceActions.Update { copy(status = TrackerStatus.DISCONNECTED) },
								)
							}
						}
					}
				}
				deviceJob.complete()

				active[path] = ActiveReceiver(deviceJob, receiver)
			}

			delay(HID_POLL_INTERVAL_MS)
		}
	}
}
