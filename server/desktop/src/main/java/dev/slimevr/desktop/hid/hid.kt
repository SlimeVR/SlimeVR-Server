package dev.slimevr.desktop.hid

import dev.slimevr.AppLogger
import dev.slimevr.VRServer
import dev.slimevr.hid.HIDReceiver
import dev.slimevr.hid.HID_TRACKER_PID
import dev.slimevr.hid.HID_TRACKER_RECEIVER_PID
import dev.slimevr.hid.HID_TRACKER_RECEIVER_VID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.hid4java.HidDevice
import org.hid4java.HidManager
import org.hid4java.HidServicesSpecification
import org.hid4java.jna.HidApi
import org.hid4java.jna.HidDeviceInfoStructure

private const val HID_POLL_INTERVAL_MS = 3000L

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

fun createDesktopHIDManager(serverContext: VRServer, scope: CoroutineScope) {
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

				val dataFlow = channelFlow {
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
								data == null -> return@channelFlow

								// read error, device gone
								data.isNotEmpty() -> send(data)

								else -> delay(1) // no data yet, yield without busy-spinning
							}
						}
					} finally {
						withContext(NonCancellable + Dispatchers.IO) { hidDevice.close() }
					}
				}

				val receiver = HIDReceiver.create(
					serialNumber = serial,
					data = dataFlow,
					serverContext = serverContext,
					scope = deviceScope,
				)
				deviceJob.complete()

				active[path] = ActiveReceiver(deviceJob, receiver)
			}

			delay(HID_POLL_INTERVAL_MS)
		}
	}
}
