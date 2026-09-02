package dev.slimevr.desktop.hid

import dev.slimevr.AppContextProvider
import dev.slimevr.hid.HidConnection
import dev.slimevr.hid.HidDeviceDescriptor
import dev.slimevr.hid.HidTransport
import dev.slimevr.hid.hidVendorIds
import dev.slimevr.hid.isCompatibleHidReceiver
import dev.slimevr.hid.isCompatibleHidTracker
import dev.slimevr.hid.runHidManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.hid4java.jna.HidApi
import org.hid4java.jna.HidDeviceInfoStructure
import org.hid4java.jna.HidDeviceStructure

private val hidLibLock = Any()
private var hidLibInitialized = false

private fun ensureHidApi() {
	synchronized(hidLibLock) {
		if (hidLibInitialized) return
		HidApi.init()
		hidLibInitialized = true
	}
}

private fun shutdownHidApi() {
	synchronized(hidLibLock) {
		if (!hidLibInitialized) return
		HidApi.exit()
		hidLibInitialized = false
	}
}

private fun enumerateHidDevices(directTrackersEnabled: Boolean): Map<String, HidDeviceDescriptor> {
	ensureHidApi()

	val result = mutableMapOf<String, HidDeviceDescriptor>()
	for (vendorId in hidVendorIds(directTrackersEnabled)) {
		val root = HidApi.enumerateDevices(vendorId, 0) ?: continue
		try {
			var info: HidDeviceInfoStructure? = root
			while (info != null) {
				val vid = info.vendor_id.toInt() and 0xFFFF
				val pid = info.product_id.toInt() and 0xFFFF
				if (isCompatibleHidReceiver(vid, pid) || (directTrackersEnabled && isCompatibleHidTracker(vid, pid))) {
					// Needs to be copied over as freeEnumeration would free the memory. JNA shenanigans
					result[info.path] = HidDeviceDescriptor(
						key = info.path,
						vendorId = vid,
						productId = pid,
						serialNumber = info.serial_number?.toString(),
					)
				}
				info = info.next()
			}
		} finally {
			HidApi.freeEnumeration(root)
		}
	}
	return result
}

private class DesktopHidConnection(private val struct: HidDeviceStructure) : HidConnection {
	private val writeLock = Mutex()

	@Volatile
	private var closed = false

	override suspend fun read(buffer: ByteArray, timeoutMs: Int): Int = withContext(Dispatchers.IO) {
		if (closed) return@withContext -1
		HidApi.read(struct, buffer, timeoutMs)
	}

	override suspend fun write(data: ByteArray): Int = writeLock.withLock {
		if (closed) return@withLock -1
		withContext(Dispatchers.IO) {
			val written = HidApi.write(struct, data, data.size, 0)
			if (written > 0) written - 1 else written
		}
	}

	override suspend fun close() {
		writeLock.withLock {
			if (closed) return@withLock
			closed = true
			withContext(Dispatchers.IO) { HidApi.close(struct) }
		}
	}
}

private class DesktopHidTransport : HidTransport {
	override val wakeSignal: ReceiveChannel<Unit>? = null

	override suspend fun enumerate(directTrackersEnabled: Boolean): Map<String, HidDeviceDescriptor> = withContext(Dispatchers.IO) {
		enumerateHidDevices(directTrackersEnabled)
	}

	override suspend fun open(descriptor: HidDeviceDescriptor): HidConnection? = withContext(Dispatchers.IO) {
		ensureHidApi()
		HidApi.open(descriptor.key)?.let { struct -> DesktopHidConnection(struct) }
	}

	override suspend fun shutdown() = withContext(Dispatchers.IO) { shutdownHidApi() }
}

fun createDesktopHIDManager(appContext: AppContextProvider, scope: CoroutineScope): Job = runHidManager(appContext, DesktopHidTransport(), scope)
