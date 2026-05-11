package dev.slimevr.firmware

import dev.slimevr.VRServer
import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import dev.slimevr.serial.FlashingHandler
import dev.slimevr.serial.MAC_REGEX
import dev.slimevr.serial.SerialConnection
import dev.slimevr.serial.SerialServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.rpc.FirmwarePart
import solarxr_protocol.rpc.FirmwareUpdateStatus

fun interface FirmwareFlasher {
	suspend fun flash(
		portLocation: String,
		handler: FlashingHandler,
		parts: List<DownloadedFirmwarePart>,
		onProgress: (Int) -> Unit,
	)
}

object NoopFirmwareFlasher : FirmwareFlasher {
	override suspend fun flash(
		portLocation: String,
		handler: FlashingHandler,
		parts: List<DownloadedFirmwarePart>,
		onProgress: (Int) -> Unit,
	) = Unit
}

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun doSerialFlash(
	portLocation: String,
	parts: List<FirmwarePart>,
	needManualReboot: Boolean,
	ssid: String?,
	password: String?,
	serialServer: SerialServer,
	settings: Settings,
	server: VRServer,
	flasher: FirmwareFlasher,
	onStatus: suspend (FirmwareUpdateStatus, Int) -> Unit,
	scope: CoroutineScope,
) {
	onStatus(FirmwareUpdateStatus.DOWNLOADING, 0)

	val downloadedParts = try {
		withContext(Dispatchers.IO) {
			parts.map { part ->
				val url = part.url ?: error("missing url")
				val digest = part.digest ?: error("missing digest")
				val offset = part.offset?.toInt() ?: error("missing offset")
				DownloadedFirmwarePart(
					data = downloadFirmware(url, digest),
					offset = offset,
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

	val runFlasher = runCatching {
		flasher.flash(
			portLocation = portLocation,
			handler = handler,
			parts = downloadedParts,
			onProgress = { progress ->
				scope.launch { onStatus(FirmwareUpdateStatus.UPLOADING, progress) }
			},
		)
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
		settings = settings,
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
	settings: Settings,
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

	settings.context.dispatch(SettingsActions.AddAllowedUdpDevice(macAddress))

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
	val connected = waitForConnected(server, macAddress)

	if (connected == null) {
		onStatus(FirmwareUpdateStatus.ERROR_TIMEOUT, 0)
		return
	}

	onStatus(FirmwareUpdateStatus.DONE, 0)
}

suspend fun waitForConnected(server: VRServer, macAddress: String): Boolean? =
	@OptIn(ExperimentalCoroutinesApi::class)
	withTimeoutOrNull(30_000) {
		server.context.state
			.flatMapLatest { state ->
				val device =
					state.devices.values.find { it.context.state.value.macAddress?.uppercase() == macAddress }
				device?.context?.state?.map { it.status != TrackerStatus.DISCONNECTED }
					?: flowOf(false)
			}
			.filter { it }
			.first()
	}
