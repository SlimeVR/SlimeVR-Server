package dev.slimevr.android.hid

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface
import android.hardware.usb.UsbManager
import androidx.core.content.ContextCompat
import dev.slimevr.AppContextProvider
import dev.slimevr.hid.HidConnection
import dev.slimevr.hid.HidDeviceDescriptor
import dev.slimevr.hid.HidTransport
import dev.slimevr.hid.isCompatibleHidReceiver
import dev.slimevr.hid.isCompatibleHidTracker
import dev.slimevr.hid.runHidManager
import dev.slimevr.logging.AppLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

private const val ACTION_USB_HID_PERMISSION = "dev.slimevr.android.USB_HID_PERMISSION"
private const val HID_WRITE_TIMEOUT_MS = 1000

private data class HidEndpoints(val iface: UsbInterface, val input: UsbEndpoint, val output: UsbEndpoint?)

private fun findHidEndpoints(device: UsbDevice): HidEndpoints? {
	for (ifaceIdx in 0 until device.interfaceCount) {
		val iface = device.getInterface(ifaceIdx)
		if (iface.interfaceClass != UsbConstants.USB_CLASS_HID) continue

		var input: UsbEndpoint? = null
		var output: UsbEndpoint? = null
		for (epIdx in 0 until iface.endpointCount) {
			val endpoint = iface.getEndpoint(epIdx)
			if (endpoint.type != UsbConstants.USB_ENDPOINT_XFER_INT) continue
			when (endpoint.direction) {
				UsbConstants.USB_DIR_IN -> if (input == null) input = endpoint
				UsbConstants.USB_DIR_OUT -> if (output == null) output = endpoint
			}
		}

		if (input != null) return HidEndpoints(iface, input, output)
	}
	return null
}

private class AndroidHidConnection(
	private val connection: UsbDeviceConnection,
	private val endpoints: HidEndpoints,
) : HidConnection {
	private val writeLock = Mutex()

	@Volatile
	private var closed = false

	override suspend fun read(buffer: ByteArray, timeoutMs: Int): Int = withContext(Dispatchers.IO) {
		if (closed) return@withContext -1
		connection.bulkTransfer(endpoints.input, buffer, buffer.size, timeoutMs).coerceAtLeast(0)
	}

	override suspend fun write(data: ByteArray): Int = writeLock.withLock {
		val output = endpoints.output ?: return@withLock -1
		if (closed) return@withLock -1
		withContext(Dispatchers.IO) {
			connection.bulkTransfer(output, data, data.size, HID_WRITE_TIMEOUT_MS)
		}
	}

	override suspend fun close() {
		writeLock.withLock {
			if (closed) return@withLock
			closed = true
			withContext(Dispatchers.IO) {
				connection.releaseInterface(endpoints.iface)
				connection.close()
			}
		}
	}
}

private class AndroidHidTransport(
	private val context: Context,
	private val usbManager: UsbManager,
	private val permissionIntent: PendingIntent,
	private val usbReceiver: BroadcastReceiver,
	private val wakeChannel: Channel<Unit>,
) : HidTransport {
	private val permissionRequested = mutableSetOf<String>()

	override val wakeSignal: ReceiveChannel<Unit> = wakeChannel

	override suspend fun enumerate(directTrackersEnabled: Boolean): Map<String, HidDeviceDescriptor> = withContext(Dispatchers.IO) {
		val compatible = usbManager.deviceList.values.filter { device ->
			isCompatibleHidReceiver(device.vendorId, device.productId) ||
				(directTrackersEnabled && isCompatibleHidTracker(device.vendorId, device.productId))
		}

		for (device in compatible) {
			if (usbManager.hasPermission(device) || device.deviceName in permissionRequested) continue
			AppLogger.hid.info("Requesting USB HID permission for ${device.deviceName}")
			usbManager.requestPermission(device, permissionIntent)
			permissionRequested.add(device.deviceName)
		}
		permissionRequested.retainAll(compatible.mapTo(mutableSetOf()) { device -> device.deviceName })

		// Only permitted devices are handed up, so the manager never tries to open one it cannot
		compatible
			.filter { device -> usbManager.hasPermission(device) }
			.associate { device ->
				device.deviceName to HidDeviceDescriptor(
					key = device.deviceName,
					vendorId = device.vendorId,
					productId = device.productId,
					serialNumber = device.serialNumber,
				)
			}
	}

	override suspend fun open(descriptor: HidDeviceDescriptor): HidConnection? = withContext(Dispatchers.IO) {
		val device = usbManager.deviceList[descriptor.key] ?: return@withContext null

		val endpoints = findHidEndpoints(device)
		if (endpoints == null) {
			AppLogger.hid.warn("No HID input endpoint found for ${descriptor.key}")
			return@withContext null
		}

		val connection = usbManager.openDevice(device) ?: return@withContext null
		if (!connection.claimInterface(endpoints.iface, true)) {
			connection.close()
			AppLogger.hid.warn("Failed to claim HID interface for ${descriptor.key}")
			return@withContext null
		}

		AndroidHidConnection(connection, endpoints)
	}

	override suspend fun shutdown() {
		context.unregisterReceiver(usbReceiver)
		wakeChannel.close()
	}
}

fun createAndroidHIDManager(context: Context, appContext: AppContextProvider, scope: CoroutineScope): Job {
	val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
	val wakeChannel = Channel<Unit>(Channel.CONFLATED)

	val permissionIntent = PendingIntent.getBroadcast(
		context,
		0,
		Intent(ACTION_USB_HID_PERMISSION).apply { setPackage(context.packageName) },
		PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
	)

	val usbReceiver = object : BroadcastReceiver() {
		override fun onReceive(ctx: Context, intent: Intent) {
			wakeChannel.trySend(Unit)
		}
	}

	val intentFilter = IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED).apply {
		addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
		addAction(ACTION_USB_HID_PERMISSION)
	}
	ContextCompat.registerReceiver(context, usbReceiver, intentFilter, ContextCompat.RECEIVER_NOT_EXPORTED)

	val transport = AndroidHidTransport(context, usbManager, permissionIntent, usbReceiver, wakeChannel)
	return runHidManager(appContext, transport, scope)
}
