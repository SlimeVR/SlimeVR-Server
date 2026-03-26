package dev.slimevr.firmware

import dev.llelievr.espflashkotlin.Flasher
import dev.llelievr.espflashkotlin.FlashingProgressListener
import dev.slimevr.VRServer
import dev.slimevr.serial.SerialConnection
import dev.slimevr.serial.SerialServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.rpc.FirmwarePart
import solarxr_protocol.rpc.FirmwareUpdateStatus

private val MAC_REGEX = Regex("mac: (([0-9A-Fa-f]{2}[:-]){5}[0-9A-Fa-f]{2})", RegexOption.IGNORE_CASE)

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun doSerialFlash(
	portLocation: String,
	parts: List<FirmwarePart>,
	needManualReboot: Boolean,
	ssid: String?,
	password: String?,
	serialServer: SerialServer,
	server: VRServer,
	onStatus: suspend (FirmwareUpdateStatus, Int) -> Unit,
	scope: CoroutineScope,
) {
	onStatus(FirmwareUpdateStatus.DOWNLOADING, 0)

	val downloadedParts = try {
		withContext(Dispatchers.IO) {
			parts.map { part ->
				val url = part.url ?: error("missing url")
				val digest = part.digest ?: error("missing digest")
				DownloadedFirmwarePart(
					data = downloadFirmware(url, digest),
					offset = part.offset.toInt(),
				)
			}
		}
	} catch (_: Exception) {
		onStatus(FirmwareUpdateStatus.ERROR_DOWNLOAD_FAILED, 0)
		return
	}

	onStatus(FirmwareUpdateStatus.SYNCING_WITH_MCU, 0)

	val handler = serialServer.openForFlashing(portLocation) ?: run {
		onStatus(FirmwareUpdateStatus.ERROR_DEVICE_NOT_FOUND, 0)
		return
	}

	val flasher = Flasher(handler)
	for (part in downloadedParts) {
		flasher.addBin(part.data, part.offset)
	}
	flasher.addProgressListener(
		object : FlashingProgressListener {
			override fun progress(progress: Float) {
				scope.launch { onStatus(FirmwareUpdateStatus.UPLOADING, (progress * 100).toInt()) }
			}
		},
	)

	val runFlasher = runCatching {
		withContext(Dispatchers.IO) { flasher.flash(portLocation) }
	}

	if (runFlasher.isFailure) {
		onStatus(FirmwareUpdateStatus.ERROR_UPLOAD_FAILED, 0)
		return
	}

	doSerialFlashPostFlash(
		portLocation = portLocation,
		needManualReboot = needManualReboot,
		ssid = ssid,
		password = password,
		serialServer = serialServer,
		server = server,
		onStatus = onStatus,
	)
}

/**
 * Handles the post-flash provisioning phase: reconnects the serial console,
 * reads the device MAC address, sends Wi-Fi credentials, and waits for the
 * tracker to appear on the network.
 *
 * Separated from [doSerialFlash] so it can also be exercised independently for
 * unit tests
 */
internal suspend fun doSerialFlashPostFlash(
	portLocation: String,
	needManualReboot: Boolean,
	ssid: String?,
	password: String?,
	serialServer: SerialServer,
	server: VRServer,
	onStatus: suspend (FirmwareUpdateStatus, Int) -> Unit,
) {
	onStatus(
		if (needManualReboot) {
			FirmwareUpdateStatus.NEED_MANUAL_REBOOT
		} else {
			FirmwareUpdateStatus.REBOOTING
		},
		0,
	)

	serialServer.openConnection(portLocation)
	val serialConn = serialServer.context.state.value.connections[portLocation]
	if (serialConn == null) {
		onStatus(FirmwareUpdateStatus.ERROR_DEVICE_NOT_FOUND, 0)
		return
	}
	if (serialConn !is SerialConnection.Console) {
		onStatus(FirmwareUpdateStatus.ERROR_UNKNOWN, 0)
		return
	}

	if (needManualReboot) {
		// wait for the device to reboot
		val rebooted = withTimeoutOrNull(60_000) {
			serialConn.context.state.map { it.logLines }
				.filter { logLines -> logLines.any { "starting up" in it.lowercase() } }
				.first()
		}

		if (rebooted == null) {
			onStatus(FirmwareUpdateStatus.ERROR_TIMEOUT, 0)
			return
		}
	}

	// get MAC address by sending GET INFO and parsing the response
	serialConn.handle.writeCommand("GET INFO")

	val macAddress = withTimeoutOrNull(10_000) {
		serialConn.context.state.map { it.logLines }.mapNotNull { logLines ->
			logLines.firstNotNullOfOrNull { line ->
				MAC_REGEX.find(line)?.groupValues?.get(1)?.uppercase()
			}
		}.first()
	}

	if (macAddress == null) {
		onStatus(FirmwareUpdateStatus.ERROR_PROVISIONING_FAILED, 0)
		return
	}

	// provision with Wi-Fi credentials
	if (ssid == null || password == null) {
		onStatus(FirmwareUpdateStatus.ERROR_PROVISIONING_FAILED, 0)
		return
	}

	onStatus(FirmwareUpdateStatus.PROVISIONING, 0)
	serialConn.handle.writeCommand("SET WIFI \"$ssid\" \"$password\"\n")

	// Wait for Wi-Fi to connect ("looking for the server")
	val provisioned = withTimeoutOrNull(30_000) {
		serialConn.context.state.map { it.logLines }.filter { logLines ->
			logLines.any {
				"looking for the server" in it.lowercase() || "searching for the server" in it.lowercase()
			}
		}.first()
	}

	if (provisioned == null) {
		onStatus(FirmwareUpdateStatus.ERROR_PROVISIONING_FAILED, 0)
		return
	}

	// wait for the tracker with that MAC to connect to the server via UDP
	val connected = withTimeoutOrNull(60_000) {
		server.context.state
			.map { state -> state.devices.values.any { it.context.state.value.macAddress?.uppercase() == macAddress
				&& it.context.state.value.status != TrackerStatus.DISCONNECTED } }
			.filter { it }
			.first()
	}

	if (connected == null) {
		onStatus(FirmwareUpdateStatus.ERROR_TIMEOUT, 0)
		return
	}

	onStatus(FirmwareUpdateStatus.DONE, 0)
}
