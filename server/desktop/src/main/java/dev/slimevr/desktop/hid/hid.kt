package dev.slimevr.desktop.hid

import dev.slimevr.AppContextProvider
import dev.slimevr.AppLogger
import dev.slimevr.VRServerActions
import dev.slimevr.device.DeviceActions
import dev.slimevr.hid.HIDReceiver
import dev.slimevr.hid.isCompatibleHidDevice
import dev.slimevr.hid.parseHIDPackets
import dev.slimevr.util.safeLaunch
import kotlinx.coroutines.*
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import org.hid4java.HidDevice
import org.hid4java.HidManager
import org.hid4java.HidServicesSpecification
import org.hid4java.jna.HidApi
import org.hid4java.jna.HidDeviceInfoStructure
import solarxr_protocol.datatypes.TrackerStatus

private const val HID_POLL_INTERVAL_MS = 3000L

private val hidSpec = HidServicesSpecification().apply { isAutoStart = false }

// Initialize the native HID library. Must be called before enumerateDevices.
private val hidServices by lazy { HidManager.getHidServices(hidSpec) }

private fun enumerateCompatibleDevices(): Map<String, HidDevice> {
	hidServices // ensure native lib is loaded
	hidServices.start()
	val result = mutableMapOf<String, HidDevice>()

	for (device in hidServices.attachedHidDevices) {
		if (isCompatibleHidDevice(device.vendorId, device.productId)) {
			result[device.path] = device
		}
	}

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

				val receiver = HIDReceiver.create(
					serialNumber = serial,
					appContext = appContext,
					scope = deviceScope,
				)

				val dongleId = appContext.server.nextHandle()
				appContext.server.context.dispatch(VRServerActions.NewDongle(dongleId, receiver))

				deviceScope.safeLaunch {
					receiver.outboundPackets.onAny{ packet ->
						deviceScope.safeLaunch {
							val buf = Buffer()
							AppLogger.hid.info("Received packet: $packet")
							packet.write(buf)
							hidDevice.write(buf.readByteArray(), buf.size.toInt(), 0)
						}
					}
				}

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
								data.isNotEmpty() -> parseHIDPackets(data).forEach { receiver.inboundPackets.emit(it) }

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
