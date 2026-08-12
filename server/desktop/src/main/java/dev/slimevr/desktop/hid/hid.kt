package dev.slimevr.desktop.hid

import dev.slimevr.AppContextProvider
import dev.slimevr.VRServerActions
import dev.slimevr.device.DeviceActions
import dev.slimevr.hid.HIDReceiver
import dev.slimevr.hid.isCompatibleHidReceiver
import dev.slimevr.hid.isCompatibleHidTracker
import dev.slimevr.hid.parseHIDPackets
import dev.slimevr.logging.AppLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.hid4java.HidDevice
import org.hid4java.HidManager
import org.hid4java.HidServicesSpecification
import org.hid4java.jna.HidApi
import org.hid4java.jna.HidDeviceInfoStructure
import solarxr_protocol.datatypes.TrackerStatus

private const val HID_POLL_INTERVAL_MS = 3000L

// A HID report is 64 bytes, which parseHIDPackets splits into four 16-byte packets
private const val HID_READ_BUFFER_SIZE = 64

// Bounds how long cancellation waits on the uninterruptible native read when a device is idle
private const val HID_READ_TIMEOUT_MS = 100

private val hidSpec = HidServicesSpecification().apply { isAutoStart = false }

// Initialize the native HID library. Must be called before enumerateDevices.
private val hidServices by lazy { HidManager.getHidServices(hidSpec) }

private fun enumerateCompatibleDevices(directTrackersEnabled: Boolean): Map<String, HidDevice> {
	hidServices // ensure native lib is loaded
	val root = HidApi.enumerateDevices(0, 0) ?: return emptyMap()
	val result = mutableMapOf<String, HidDevice>()
	var info: HidDeviceInfoStructure? = root
	while (info != null) {
		val vid = info.vendor_id.toInt()
		val pid = info.product_id.toInt()
		if (isCompatibleHidReceiver(vid, pid) || (directTrackersEnabled && isCompatibleHidTracker(vid, pid))) {
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

	scope.launch {
		while (isActive) {
			val directTrackersEnabled = appContext.config.settings.context.state.value.data.hidConfig.trackersOverHid
			val found = withContext(Dispatchers.IO) {
				try {
					enumerateCompatibleDevices(directTrackersEnabled)
				} catch (e: Exception) {
					AppLogger.hid.error(e, "HID enumeration failed")
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

				val deviceJob = SupervisorJob(scope.coroutineContext[Job])
				val deviceScope = CoroutineScope(scope.coroutineContext + deviceJob)

				val id = appContext.server.nextHandle()
				val receiver = HIDReceiver.create(
					id = id,
					serialNumber = serial,
					isDirect = isCompatibleHidTracker(hidDevice.vendorId, hidDevice.productId),
					appContext = appContext,
					scope = deviceScope,
				)
				appContext.server.context.dispatch(VRServerActions.NewDongle(id, receiver))

				deviceScope.launch(Dispatchers.IO) {
					try {
						// Reused across reads: parseHIDPackets is told how much of it is live
						val buffer = ByteArray(HID_READ_BUFFER_SIZE)
						while (isActive) {
							val read = try {
								hidDevice.read(buffer, HID_READ_TIMEOUT_MS)
							} catch (_: Exception) {
								-1
							}
							when {
								// read error, device gone
								read < 0 -> return@launch

								read > 0 -> parseHIDPackets(buffer, read).forEach { receiver.packetEvents.emit(it) }

								// 0 is a timeout with no data: the read already blocked, so just go again
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
							appContext.server.context.dispatch(VRServerActions.RemoveDongle(receiver.context.state.value.id))
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
