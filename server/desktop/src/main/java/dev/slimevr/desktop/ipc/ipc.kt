package dev.slimevr.desktop.ipc

import dev.slimevr.AppContextProvider
import dev.slimevr.CURRENT_PLATFORM
import dev.slimevr.Platform
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

const val DRIVER_SOCKET_NAME = "SlimeVRDriver"
const val FEEDER_SOCKET_NAME = "SlimeVRInput"
const val SOLARXR_SOCKET_NAME = "SlimeVRRpc"

const val DRIVER_PIPE = "\\\\.\\pipe\\SlimeVRDriver"
const val FEEDER_PIPE = "\\\\.\\pipe\\SlimeVRInput"
const val SOLARXR_PIPE = "\\\\.\\pipe\\SlimeVRRpc"

// Frames larger than this are refused: nothing in either protocol comes close
internal const val MAX_FRAME_SIZE = 256 * 1024

suspend fun createIpcServers(appContext: AppContextProvider) = coroutineScope {
	if (appContext.featureFlags.supportsDriver) {
		launch { createDriverServers(appContext) }
	}

	when (CURRENT_PLATFORM) {
		Platform.LINUX, Platform.OSX -> launch { createUnixSolarXRSocket(appContext) }
		Platform.WINDOWS -> launch { createWindowsSolarXRPipe(appContext) }
		else -> Unit
	}
}

private suspend fun createDriverServers(appContext: AppContextProvider) {
	appContext.config.settings.context.state
		.map { it.data.driverConfig.enabled }
		.distinctUntilChanged()
		.collectLatest { enabled ->
			if (!enabled) return@collectLatest

			coroutineScope {
				when (CURRENT_PLATFORM) {
					Platform.LINUX, Platform.OSX -> {
						launch { createUnixDriverSocket(appContext) }
						launch { createUnixFeederSocket(appContext) }
					}

					Platform.WINDOWS -> {
						launch { createWindowsDriverPipe(appContext) }
						launch { createWindowsFeederPipe(appContext) }
					}

					else -> Unit
				}
			}
		}
}
