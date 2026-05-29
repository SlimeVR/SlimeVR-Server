package dev.slimevr.android.hid

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface
import android.hardware.usb.UsbManager
import androidx.core.content.ContextCompat
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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import solarxr_protocol.datatypes.TrackerStatus

private const val ACTION_USB_HID_PERMISSION = "dev.slimevr.android.USB_HID_PERMISSION"
private const val HID_POLL_INTERVAL_MS = 3000L

private data class HidProductRule(val vendorId: Int, val productId: Int, val productMask: Int = 0xFFFF)

private val HID_PRODUCT_RULES = listOf(
	HidProductRule(0x1209, 0x7690), // SlimeVR receiver
	HidProductRule(0x1209, 0x7692), // SlimeVR tracker direct
	HidProductRule(0x4E76, 0xD200, 0xFF00), // Gestures Inc. D2XX
)

private fun isCompatibleDevice(vid: Int, pid: Int) =
	HID_PRODUCT_RULES.any { rule -> vid == rule.vendorId && (pid and rule.productMask) == rule.productId }

private fun findHidInputEndpoint(device: UsbDevice): Pair<UsbInterface, UsbEndpoint>? {
	for (ifaceIdx in 0 until device.interfaceCount) {
		val iface = device.getInterface(ifaceIdx)
		if (iface.interfaceClass != UsbConstants.USB_CLASS_HID) continue
		for (epIdx in 0 until iface.endpointCount) {
			val endpoint = iface.getEndpoint(epIdx)
			if (endpoint.type == UsbConstants.USB_ENDPOINT_XFER_INT &&
				endpoint.direction == UsbConstants.USB_DIR_IN
			) return iface to endpoint
		}
	}
	return null
}

private data class ActiveReceiver(val job: Job, val receiver: HIDReceiver)

fun createAndroidHIDManager(context: Context, appContext: AppContextProvider, scope: CoroutineScope) {
	val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
	val wakeSignal = Channel<Unit>(Channel.CONFLATED)
	val permissionRequested = mutableSetOf<String>()
	val active = mutableMapOf<String, ActiveReceiver>()

	val permissionIntent = PendingIntent.getBroadcast(
		context,
		0,
		Intent(ACTION_USB_HID_PERMISSION).apply { setPackage(context.packageName) },
		PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
	)

	val usbReceiver = object : BroadcastReceiver() {
		override fun onReceive(ctx: Context, intent: Intent) {
			wakeSignal.trySend(Unit)
		}
	}

	val intentFilter = IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED).apply {
		addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
		addAction(ACTION_USB_HID_PERMISSION)
	}
	ContextCompat.registerReceiver(context, usbReceiver, intentFilter, ContextCompat.RECEIVER_NOT_EXPORTED)

	scope.safeLaunch {
		while (isActive) {
			val found = withContext(Dispatchers.IO) {
				try {
					usbManager.deviceList.values
						.filter { device -> isCompatibleDevice(device.vendorId, device.productId) }
						.associate { device -> device.deviceName to device }
				} catch (e: Exception) {
					AppLogger.hid.error(e, "HID enumeration failed")
					emptyMap()
				}
			}

			for ((deviceName, device) in found) {
				if (!usbManager.hasPermission(device) && deviceName !in permissionRequested) {
					AppLogger.hid.info("Requesting USB HID permission for $deviceName")
					usbManager.requestPermission(device, permissionIntent)
					permissionRequested.add(deviceName)
				}
			}

			val authorized = found.filter { (_, device) -> usbManager.hasPermission(device) }

			val toRemove = (active.keys - authorized.keys) +
				active.entries.filter { (_, entry) -> !entry.job.isActive }.map { (key, _) -> key }
			for (deviceName in toRemove) {
				val entry = active.remove(deviceName) ?: continue
				entry.job.cancel()
				entry.job.join()
				permissionRequested.remove(deviceName)
				AppLogger.hid.info("HID device removed: $deviceName")
			}

			for ((deviceName, device) in authorized) {
				if (deviceName in active) continue
				permissionRequested.remove(deviceName)

				val hidEndpoint = findHidInputEndpoint(device)
				if (hidEndpoint == null) {
					AppLogger.hid.warn("No HID input endpoint found for $deviceName")
					continue
				}
				val (iface, endpoint) = hidEndpoint

				val connection = withContext(Dispatchers.IO) { usbManager.openDevice(device) }
				if (connection == null) {
					AppLogger.hid.warn("Failed to open HID device $deviceName")
					continue
				}

				connection.claimInterface(iface, true)

				val serial = device.serialNumber ?: deviceName
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
						val buffer = ByteArray(64)
						while (isActive) {
							val read = withContext(Dispatchers.IO) {
								try {
									connection.bulkTransfer(endpoint, buffer, buffer.size, 0)
								} catch (_: Exception) {
									-1
								}
							}
							when {
								read < 0 -> return@safeLaunch
								read > 0 -> parseHIDPackets(buffer.copyOf(read)).forEach { packet -> receiver.packetEvents.emit(packet) }
								else -> delay(1)
							}
						}
					} finally {
						withContext(NonCancellable + Dispatchers.IO) {
							connection.releaseInterface(iface)
							connection.close()
						}
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

				active[deviceName] = ActiveReceiver(deviceJob, receiver)
			}

			permissionRequested.retainAll(found.keys)
			withTimeoutOrNull(HID_POLL_INTERVAL_MS) { wakeSignal.receive() }
		}
	}
}