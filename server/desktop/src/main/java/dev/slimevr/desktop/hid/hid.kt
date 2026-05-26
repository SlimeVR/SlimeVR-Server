package dev.slimevr.desktop.hid

import dev.slimevr.AppContextProvider
import dev.slimevr.AppLogger
import dev.slimevr.device.DeviceActions
import dev.slimevr.hid.HIDReceiver
import dev.slimevr.hid.parseHIDPackets
import dev.slimevr.util.safeLaunch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import org.hid4java.HidDevice
import org.hid4java.HidManager
import org.hid4java.HidServicesSpecification
import org.hid4java.jna.HidApi
import org.hid4java.jna.HidDeviceInfoStructure
import solarxr_protocol.datatypes.TrackerStatus

private const val HID_POLL_INTERVAL_MS = 3000L

private data class HidProductRule(val vendorId: Int, val productId: Int, val productMask: Int = 0xFFFF)

private val HID_PRODUCT_RULES = listOf(
	HidProductRule(0x1209, 0x7690), // SlimeVR receiver
	HidProductRule(0x1209, 0x7692), // SlimeVR tracker direct
	HidProductRule(0x4E76, 0xD200, 0xFF00), // Gestures Inc. D2XX
)

private fun isCompatibleDevice(vid: Int, pid: Int) = HID_PRODUCT_RULES.any { rule ->
	vid == rule.vendorId && (pid and rule.productMask) == rule.productId
}

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

fun createDesktopHIDManager(appContext: AppContextProvider, scope: CoroutineScope) {
	val active = mutableMapOf<String, ActiveReceiver>()

	scope.safeLaunch {
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
					appContext = appContext,
					scope = deviceScope,
				)

				deviceScope.safeLaunch {
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
								data == null -> return@safeLaunch

								// read error, device gone
								data.isNotEmpty() -> parseHIDPackets(data).forEach { receiver.packetEvents.emit(it) }

								else -> delay(1) // no data yet, yield without busy-spinning
							}
						}
					} finally {
						withContext(NonCancellable + Dispatchers.IO) { hidDevice.close() }
						withContext(NonCancellable) {
							for (record in receiver.context.state.value.trackers.values) {
								appContext.server.getDevice(record.deviceId)?.context?.dispatch(
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
