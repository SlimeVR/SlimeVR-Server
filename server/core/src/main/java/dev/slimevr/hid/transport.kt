package dev.slimevr.hid

import kotlinx.coroutines.channels.ReceiveChannel

data class HidDeviceDescriptor(
	/** unique per-device id from the platform: a hidraw path, or a UsbDevice name */
	val key: String,
	val vendorId: Int,
	val productId: Int,
	val serialNumber: String?,
)

interface HidConnection {
	/** Bytes read into [buffer], 0 on a timeout with no data, negative if the device is gone */
	suspend fun read(buffer: ByteArray, timeoutMs: Int): Int

	/** Payload bytes written, or negative on failure. */
	suspend fun write(data: ByteArray): Int

	suspend fun close()
}

interface HidTransport {
	/** Compatible devices present and ready to open, keyed by [HidDeviceDescriptor.key] */
	suspend fun enumerate(directTrackersEnabled: Boolean): Map<String, HidDeviceDescriptor>

	suspend fun open(descriptor: HidDeviceDescriptor): HidConnection?

	/** Hotplug hint that cuts the poll wait short. Null where the platform reports no such events */
	val wakeSignal: ReceiveChannel<Unit>?

	suspend fun shutdown()
}
