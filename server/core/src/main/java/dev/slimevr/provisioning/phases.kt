package dev.slimevr.provisioning

import dev.slimevr.VRServer
import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import dev.slimevr.firmware.waitForConnected
import dev.slimevr.serial.MAC_REGEX
import dev.slimevr.serial.SerialConsole
import dev.slimevr.serial.SerialServer
import dev.slimevr.serial.buildSetWifiCommand
import dev.slimevr.serial.sortPorts
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withTimeoutOrNull
import solarxr_protocol.rpc.SerialDeviceType
import solarxr_protocol.rpc.TrackerProvisioningStatus
import solarxr_protocol.rpc.WifiAuthMode
import solarxr_protocol.rpc.WifiNetwork
import solarxr_protocol.rpc.WifiScanStatus
import kotlin.time.Duration.Companion.seconds

internal const val MAX_CONNECTION_RETRIES = 3

private const val WSCAN_LINE_MARKER = "[WSCAN]"
private const val WSCAN_FAILED_MARKER = "$WSCAN_LINE_MARKER Scan failed!"
private const val WSCAN_ACK_MARKER = "$WSCAN_LINE_MARKER Scanning for WiFi networks..."

private val WSCAN_HEADER_REGEX = Regex("""\[WSCAN] Found (\d+) networks:""")
private val WSCAN_ENTRY_PREFIX_REGEX = Regex("""\[WSCAN] (\d+):\s+(\d+)\s+(.*)$""")
private val WSCAN_ENTRY_SUFFIX_REGEX = Regex("""\s*\((-?\d+)(?:\s*dBm)?\)\s*(\S+)\s*$""")

private sealed interface WifiScanOutcome {
	data class Results(val networks: List<WifiNetwork>) : WifiScanOutcome
	data object Failed : WifiScanOutcome
}

private fun parseWifiScanEntry(line: String): Pair<Int, WifiNetwork>? {
	val prefixMatch = WSCAN_ENTRY_PREFIX_REGEX.find(line) ?: return null
	val index = prefixMatch.groupValues[1].toIntOrNull() ?: return null
	val ssidLength = prefixMatch.groupValues[2].toIntOrNull() ?: return null
	val rest = prefixMatch.groupValues[3]

	val quoted = rest.startsWith("'")
	val ssidStart = if (quoted) 1 else 0
	val ssidEnd = ssidStart + ssidLength
	val tailStart = if (quoted) ssidEnd + 1 else ssidEnd
	if (rest.length < tailStart) return null

	val ssid = rest.substring(ssidStart, ssidEnd)
	val suffixMatch = WSCAN_ENTRY_SUFFIX_REGEX.find(rest.substring(tailStart)) ?: return null
	val rssi = suffixMatch.groupValues[1].toIntOrNull()?.toByte() ?: return null

	return index to WifiNetwork(ssid = ssid, rssi = rssi, authMode = parseAuthMode(suffixMatch.groupValues[2]))
}

private fun parseWifiScanOutcome(lines: List<String>): WifiScanOutcome? {
	if (lines.any { WSCAN_FAILED_MARKER in it }) return WifiScanOutcome.Failed

	val expectedCount = lines.firstNotNullOfOrNull { WSCAN_HEADER_REGEX.find(it) }
		?.groupValues?.get(1)?.toIntOrNull() ?: return null

	val entriesByIndex = lines.mapNotNull { parseWifiScanEntry(it) }.toMap()

	if (entriesByIndex.size < expectedCount) return null

	val networks = (0 until expectedCount).mapNotNull { i -> entriesByIndex[i] }
		.groupBy { it.ssid }
		.map { (_, duplicates) -> duplicates.maxBy { it.rssi } }
		.sortedByDescending { it.rssi }
	return WifiScanOutcome.Results(networks)
}

private suspend fun awaitWifiScanOutcome(console: SerialConsole): WifiScanOutcome {
	val scanLines = mutableListOf<String>()
	return console.lines
		.mapNotNull { line ->
			if (WSCAN_LINE_MARKER !in line) return@mapNotNull null
			scanLines += line
			parseWifiScanOutcome(scanLines)
		}
		.first()
}

private fun parseMacAddress(line: String) = MAC_REGEX.find(line)?.groupValues?.get(1)?.uppercase()

private fun parseAuthMode(raw: String): WifiAuthMode = when (raw) {
	"OPEN" -> WifiAuthMode.OPEN
	"WEP" -> WifiAuthMode.WEP
	"WPA_PSK" -> WifiAuthMode.WPA_PSK
	"WPA2_PSK" -> WifiAuthMode.WPA2_PSK
	"WPA_WPA2_PSK" -> WifiAuthMode.WPA_WPA2_PSK
	"WPA2_ENTERPRISE" -> WifiAuthMode.WPA2_ENTERPRISE
	"WPA3_PSK" -> WifiAuthMode.WPA3_PSK
	"WPA2_WPA3_PSK" -> WifiAuthMode.WPA2_WPA3_PSK
	"WAPI_PSK" -> WifiAuthMode.WAPI_PSK
	"WPA3_ENT_192" -> WifiAuthMode.WPA3_ENT_192
	else -> WifiAuthMode.UNKNOWN
}

internal const val MAX_SCAN_RETRIES = 3
private const val WSCAN_ACK_TIMEOUT_MS = 3_000L
private const val WSCAN_RESULT_TIMEOUT_MS = 15_000L

internal suspend fun scanWifiNetworks(
	context: ProvisioningManagerContext,
	serialConn: SerialConsole,
) {
	context.dispatch(ProvisioningActions.ScanStatusChanged(WifiScanStatus.SCANNING))

	var everAcked = false
	var attempt = 0

	while (attempt < MAX_SCAN_RETRIES) {
		serialConn.clearLog()
		serialConn.write("GET WIFISCAN")

		val acked = withTimeoutOrNull(WSCAN_ACK_TIMEOUT_MS) {
			serialConn.lines.first { WSCAN_ACK_MARKER in it }
		}

		if (acked == null) {
			if (serialConn.recent.isEmpty()) {
				context.dispatch(ProvisioningActions.ScanStatusChanged(WifiScanStatus.NO_SERIAL_LOGS_ERROR))
				// Wait until we get a log line from the device
				serialConn.lines.first()
				context.dispatch(ProvisioningActions.ScanStatusChanged(WifiScanStatus.SCANNING))
				continue
			}
			attempt++
			if (attempt < MAX_SCAN_RETRIES) delay(1_000)
			continue
		}
		everAcked = true

		val outcome = withTimeoutOrNull(WSCAN_RESULT_TIMEOUT_MS) {
			awaitWifiScanOutcome(serialConn)
		}

		if (outcome is WifiScanOutcome.Results) {
			context.dispatchAll(
				listOf(
					ProvisioningActions.ScanResults(outcome.networks),
					ProvisioningActions.ScanStatusChanged(WifiScanStatus.RESULTS),
				),
			)
			return
		}

		// Acked but got "Scan failed!" or no parseable result in time, retry
		attempt++
		if (attempt < MAX_SCAN_RETRIES) delay(1_000)
	}

	context.dispatch(
		ProvisioningActions.ScanStatusChanged(
			if (everAcked) WifiScanStatus.CONNECTION_ERROR else WifiScanStatus.UNSUPPORTED,
		),
	)
}

internal suspend fun selectScanPort(
	context: ProvisioningManagerContext,
	serialServer: SerialServer,
): Boolean {
	val portLocation = withTimeoutOrNull(15_000) {
		serialServer.context.state
			.mapNotNull { state -> sortPorts(state.ports.values).firstOrNull { it.type == SerialDeviceType.ESP_TRACKER }?.portLocation }
			.first()
	}

	if (portLocation == null) {
		context.dispatch(ProvisioningActions.ScanStatusChanged(WifiScanStatus.NO_SERIAL_DEVICE_FOUND))
		delay(2_000)
		return false
	}

	val actions = mutableListOf<ProvisioningActions>(ProvisioningActions.ScanPortSelected(portLocation))
	if (context.state.value.scan.networks.isEmpty()) {
		actions += ProvisioningActions.ScanStatusChanged(WifiScanStatus.SERIAL_INIT)
	}
	context.dispatchAll(actions)
	return true
}

internal suspend fun runProvisioningForPort(
	context: ProvisioningManagerContext,
	server: VRServer,
	settings: Settings,
	serialServer: SerialServer,
	portLocation: String,
	ssid: String,
	password: String?,
) {
	context.dispatch(ProvisioningActions.TrackerStatusChanged(portLocation, TrackerProvisioningStatus.SERIAL_INIT))

	val serialConn = serialServer.awaitConsole(portLocation, 3.seconds)

	if (serialConn == null) {
		context.dispatch(ProvisioningActions.TrackerStatusChanged(portLocation, TrackerProvisioningStatus.CONNECTION_ERROR))
		return
	}

	provisionPort(context, server, settings, serialConn, ssid, password)
}

// Reboots the tracker and waits for a MAC address in the serial logs.
// Handles NO_SERIAL_LOGS_ERROR by blocking until logs appear (not counted as a retry).
internal suspend fun obtainMacAddress(
	context: ProvisioningManagerContext,
	serialConn: SerialConsole,
): Boolean {
	val portLocation = serialConn.portLocation

	// Reboot and clear logs before MAC acquisition
	serialConn.clearLog()
	serialConn.write("REBOOT")
	delay(2_000)

	var connectRetries = 0

	while (currentCoroutineContext().isActive) {
		context.dispatch(ProvisioningActions.TrackerStatusChanged(portLocation, TrackerProvisioningStatus.OBTAINING_MAC_ADDRESS))
		serialConn.write("GET INFO")

		val mac = withTimeoutOrNull(5_000) {
			serialConn.lines.mapNotNull { parseMacAddress(it) }.first()
		}

		if (mac != null) {
			context.dispatch(ProvisioningActions.TrackerMacAddressObtained(portLocation, mac))
			return true
		}

		// If no logs arrived at all, the tracker is connected but silent.
		// Show the error and block until logs appear, this is not a retry.
		if (serialConn.recent.isEmpty()) {
			context.dispatch(ProvisioningActions.TrackerStatusChanged(portLocation, TrackerProvisioningStatus.NO_SERIAL_LOGS_ERROR))
			serialConn.lines.first()
			context.dispatch(ProvisioningActions.TrackerStatusChanged(portLocation, TrackerProvisioningStatus.OBTAINING_MAC_ADDRESS))

			// The GET INFO response may have arrived while we were in the error state.
			val existingMac = serialConn.recent.firstNotNullOfOrNull { parseMacAddress(it) }
			if (existingMac != null) {
				context.dispatch(ProvisioningActions.TrackerMacAddressObtained(portLocation, existingMac))
				return true
			}

			// No MAC yet, retry GET INFO without rebooting
			continue
		}

		// Got logs but no MAC after timeout. The tracker may still be mid-boot, retry GET INFO without false error status.
		connectRetries++
		if (connectRetries < MAX_CONNECTION_RETRIES) {
			delay(1_000)
			continue
		}

		context.dispatch(ProvisioningActions.TrackerStatusChanged(portLocation, TrackerProvisioningStatus.CONNECTION_ERROR))
		return false
	}
	return false
}

// Sends Wi-Fi credentials and waits for acknowledgement.
// Returns false on timeout.
internal suspend fun sendCredentials(
	context: ProvisioningManagerContext,
	serialConn: SerialConsole,
	ssid: String,
	password: String?,
): Boolean {
	val portLocation = serialConn.portLocation

	serialConn.clearLog()
	context.dispatch(ProvisioningActions.TrackerStatusChanged(portLocation, TrackerProvisioningStatus.PROVISIONING))
	serialConn.write(buildSetWifiCommand(ssid, password))

	val acked = withTimeoutOrNull(5_000) {
		serialConn.lines.first { "new wifi credentials set" in it.lowercase() }
	}

	if (acked == null) {
		context.dispatch(ProvisioningActions.TrackerStatusChanged(portLocation, TrackerProvisioningStatus.CONNECTION_ERROR))
		delay(3_000)
		return false
	}
	return true
}

// Waits for the tracker to reach "looking for server", retrying on "can't connect" up to MAX_CONNECTION_RETRIES.
// Returns false on timeout or exhausted retries.
internal suspend fun waitForWifiConnect(
	context: ProvisioningManagerContext,
	serialConn: SerialConsole,
): Boolean {
	val portLocation = serialConn.portLocation
	var connectRetries = 0

	while (currentCoroutineContext().isActive) {
		serialConn.clearLog()
		context.dispatch(ProvisioningActions.TrackerStatusChanged(portLocation, TrackerProvisioningStatus.CONNECTING))
		// null = timeout, true = looking for server, false = can't connect
		val connectResult = withTimeoutOrNull(15_000) {
			serialConn.lines
				.mapNotNull { line ->
					val lower = line.lowercase()
					when {
						"looking for the server" in lower || "searching for the server" in lower -> true
						"can't connect from any credentials" in lower -> false
						else -> null
					}
				}
				.first()
		}

		when {
			connectResult == true -> return true

			connectResult == false && connectRetries < MAX_CONNECTION_RETRIES -> {
				connectRetries++
				context.dispatch(ProvisioningActions.TrackerStatusChanged(portLocation, TrackerProvisioningStatus.CONNECTION_ERROR))
				delay(3_000)
				serialConn.write("REBOOT")
			}

			// connectResult == false with retries exhausted, or connectResult == null (timeout)
			else -> {
				context.dispatch(ProvisioningActions.TrackerStatusChanged(portLocation, TrackerProvisioningStatus.CONNECTION_ERROR))
				delay(3_000)
				return false
			}
		}
	}
	return false
}

// Runs all provisioning phases for an already-opened serial connection.
// Dispatches DONE on success. Each phase dispatches its own error status on failure.
internal suspend fun provisionPort(
	context: ProvisioningManagerContext,
	server: VRServer,
	settings: Settings,
	serialConn: SerialConsole,
	ssid: String,
	password: String?,
) {
	val portLocation = serialConn.portLocation

	if (!obtainMacAddress(context, serialConn)) return
	val macAddress = context.state.value.trackers[portLocation]?.macAddress ?: return

	settings.context.dispatch(SettingsActions.AddAllowedUdpDevice(macAddress))

	// Remove old UDP connection for this MAC before rebooting to prevent false inactivity timeouts
	val existingDevice = server.context.state.value.devices.values.find { d ->
		d.context.state.value.macAddress?.uppercase() == macAddress.uppercase()
	}
	if (existingDevice != null) {
		val oldConn = existingDevice.appContext.udpServer.context.state.value.connections.values.find { c ->
			c.context.state.value.deviceId == existingDevice.context.state.value.id
		}
		if (oldConn != null) {
			existingDevice.appContext.udpServer.removeConnection(oldConn.context.state.value.address)
		}
	}

	val provisionStartTime = System.currentTimeMillis()
	if (!sendCredentials(context, serialConn, ssid, password)) return
	if (!waitForWifiConnect(context, serialConn)) return
	if (!waitForServerConnect(context, server, portLocation, macAddress, provisionStartTime)) return

	context.dispatch(ProvisioningActions.TrackerStatusChanged(portLocation, TrackerProvisioningStatus.DONE))
}

// Waits for the tracker to connect to the server via UDP.
// Returns false on timeout.
internal suspend fun waitForServerConnect(
	context: ProvisioningManagerContext,
	server: VRServer,
	portLocation: String,
	macAddress: String,
	minPacketTime: Long = 0L,
): Boolean {
	context.dispatch(ProvisioningActions.TrackerStatusChanged(portLocation, TrackerProvisioningStatus.LOOKING_FOR_SERVER))
	val connected = waitForConnected(server, macAddress, minPacketTime)

	if (connected == null) {
		context.dispatch(ProvisioningActions.TrackerStatusChanged(portLocation, TrackerProvisioningStatus.COULD_NOT_FIND_SERVER))
		delay(3_000)
		return false
	}
	return true
}
