package dev.slimevr.provisioning

import dev.slimevr.VRServer
import dev.slimevr.firmware.waitForConnected
import dev.slimevr.serial.MAC_REGEX
import dev.slimevr.serial.SerialConnection
import dev.slimevr.serial.SerialConnectionActions
import dev.slimevr.serial.SerialServer
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withTimeoutOrNull
import solarxr_protocol.rpc.WifiProvisioningStatus

internal const val MAX_CONNECTION_RETRIES = 3

// Waits for an available port, opens a serial connection, and dispatches PortSelected.
// Returns false if no port found within timeout.
internal suspend fun selectAndOpenPort(
	context: ProvisioningManagerContext,
	serialServer: SerialServer,
): Boolean {
	val portEntry = withTimeoutOrNull(15_000) {
		serialServer.context.state
			.mapNotNull { state -> state.availablePorts.entries.firstOrNull() }
			.first()
	}

	if (portEntry == null) {
		context.dispatch(ProvisioningActions.StatusChanged(WifiProvisioningStatus.NO_SERIAL_DEVICE_FOUND))
		delay(2_000)
		return false
	}

	context.dispatchAll(
		listOf(
			ProvisioningActions.PortSelected(portEntry.key),
			ProvisioningActions.StatusChanged(WifiProvisioningStatus.SERIAL_INIT),
		)
	)
	serialServer.openConnection(portEntry.key)
	return true
}

// Reboots the tracker and waits for a MAC address in the serial logs.
// Handles NO_SERIAL_LOGS_ERROR by blocking until logs appear (not counted as a retry).
// Dispatches MacAddressObtained on success. Returns false on failure.
internal suspend fun obtainMacAddress(
	context: ProvisioningManagerContext,
	serialConn: SerialConnection.Console,
): Boolean {
	// Reboot and clear logs before MAC acquisition
	serialConn.context.dispatch(SerialConnectionActions.ClearLogs)
	serialConn.handle.writeCommand("REBOOT")
	delay(2_000)

	while (currentCoroutineContext().isActive) {
		context.dispatch(ProvisioningActions.StatusChanged(WifiProvisioningStatus.OBTAINING_MAC_ADDRESS))
		serialConn.handle.writeCommand("GET INFO")

		val mac = withTimeoutOrNull(5_000) {
			serialConn.context.state.map { it.logLines }
				.mapNotNull { lines ->
					lines.firstNotNullOfOrNull { MAC_REGEX.find(it)?.groupValues?.get(1)?.uppercase() }
				}
				.first()
		}

		if (mac != null) {
			context.dispatch(ProvisioningActions.MacAddressObtained(mac))
			return true
		}

		// If no logs arrived at all, the tracker is connected but silent.
		// Show the error and block until logs appear, this is not a retry.
		if (serialConn.context.state.value.logLines.isEmpty()) {
			context.dispatch(ProvisioningActions.StatusChanged(WifiProvisioningStatus.NO_SERIAL_LOGS_ERROR))
			serialConn.context.state.filter { it.logLines.isNotEmpty() }.first()

			// The GET INFO response may have arrived while we were in the error state.
			val existingMac = serialConn.context.state.value.logLines
				.firstNotNullOfOrNull { MAC_REGEX.find(it)?.groupValues?.get(1)?.uppercase() }
			if (existingMac != null) {
				context.dispatch(ProvisioningActions.MacAddressObtained(existingMac))
				return true
			}

			// No MAC yet, retry GET INFO without rebooting
			continue
		}

		// Got logs but no MAC after timeout, genuine error
		context.dispatch(ProvisioningActions.StatusChanged(WifiProvisioningStatus.CONNECTION_ERROR))
		delay(3_000)
		return false
	}
	return false
}

// Sends Wi-Fi credentials and waits for acknowledgement.
// Returns false on timeout.
internal suspend fun sendCredentials(
	context: ProvisioningManagerContext,
	serialConn: SerialConnection.Console,
	ssid: String,
	password: String?,
): Boolean {
	serialConn.context.dispatch(SerialConnectionActions.ClearLogs)
	context.dispatch(ProvisioningActions.StatusChanged(WifiProvisioningStatus.PROVISIONING))
	serialConn.handle.writeCommand("SET WIFI \"$ssid\" \"${password ?: ""}\"\n")

	val acked = withTimeoutOrNull(5_000) {
		serialConn.context.state.map { it.logLines }
			.filter { lines -> lines.any { "new wifi credentials set" in it.lowercase() } }
			.first()
	}

	if (acked == null) {
		context.dispatch(ProvisioningActions.StatusChanged(WifiProvisioningStatus.CONNECTION_ERROR))
		delay(3_000)
		return false
	}
	return true
}

// Waits for the tracker to reach "looking for server", retrying on "can't connect" up to MAX_CONNECTION_RETRIES.
// Returns false on timeout or exhausted retries.
internal suspend fun waitForWifiConnect(
	context: ProvisioningManagerContext,
	serialConn: SerialConnection.Console,
): Boolean {

	var connectRetries = 0

	while (currentCoroutineContext().isActive) {
		serialConn.context.dispatch(SerialConnectionActions.ClearLogs)
		context.dispatch(ProvisioningActions.StatusChanged(WifiProvisioningStatus.CONNECTING))
		// null = timeout, true = looking for server, false = can't connect
		val connectResult = withTimeoutOrNull(15_000) {
			serialConn.context.state.map { it.logLines }
				.mapNotNull { lines ->
					when {
						lines.any {
							"looking for the server" in it.lowercase() ||
								"searching for the server" in it.lowercase()
						} -> true

						lines.any { "can't connect from any credentials" in it.lowercase() } -> false
						else -> null
					}
				}
				.first()
		}

		when (connectResult) {
			true -> return true

			false -> if (connectRetries < MAX_CONNECTION_RETRIES) {
				connectRetries++
				context.dispatch(ProvisioningActions.StatusChanged(WifiProvisioningStatus.CONNECTION_ERROR))
				delay(3_000)
				serialConn.handle.writeCommand("REBOOT")
			} else {
				context.dispatch(ProvisioningActions.StatusChanged(WifiProvisioningStatus.CONNECTION_ERROR))
				delay(3_000)
				return false
			}

			else -> {
				context.dispatch(ProvisioningActions.StatusChanged(WifiProvisioningStatus.CONNECTION_ERROR))
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
	serialConn: SerialConnection.Console,
	ssid: String,
	password: String?,
) {
	if (!obtainMacAddress(context, serialConn)) return
	val macAddress = context.state.value.macAddress ?: return

	if (!sendCredentials(context, serialConn, ssid, password)) return
	if (!waitForWifiConnect(context, serialConn)) return
	if (!waitForServerConnect(context, server, macAddress)) return

	context.dispatch(ProvisioningActions.StatusChanged(WifiProvisioningStatus.DONE))
}

// Waits for the tracker to connect to the server via UDP.
// Returns false on timeout.
internal suspend fun waitForServerConnect(
	context: ProvisioningManagerContext,
	server: VRServer,
	macAddress: String,
): Boolean {
	context.dispatch(ProvisioningActions.StatusChanged(WifiProvisioningStatus.LOOKING_FOR_SERVER))
	val connected = waitForConnected(server, macAddress)

	if (connected == null) {
		context.dispatch(ProvisioningActions.StatusChanged(WifiProvisioningStatus.COULD_NOT_FIND_SERVER))
		delay(3_000)
		return false
	}
	return true
}
