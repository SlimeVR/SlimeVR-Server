package dev.slimevr.desktop.ipc

import dev.slimevr.AppContextProvider
import dev.slimevr.CURRENT_PLATFORM
import dev.slimevr.Platform
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

const val DRIVER_SOCKET_NAME = "SlimeVRDriver"
const val FEEDER_SOCKET_NAME = "SlimeVRInput"
const val SOLARXR_SOCKET_NAME = "SlimeVRRpc"

const val DRIVER_PIPE = "\\\\.\\pipe\\SlimeVRDriver"
const val FEEDER_PIPE = "\\\\.\\pipe\\SlimeVRInput"
const val SOLARXR_PIPE = "\\\\.\\pipe\\SlimeVRRpc"

suspend fun createIpcServers(appContext: AppContextProvider) = coroutineScope {
	when (CURRENT_PLATFORM) {
		Platform.LINUX, Platform.OSX -> {
			launch { createUnixDriverSocket(appContext) }
			launch { createUnixFeederSocket(appContext) }
			launch { createUnixSolarXRSocket(appContext) }
		}

		Platform.WINDOWS -> {
			launch { createWindowsDriverPipe(appContext) }
			launch { createWindowsFeederPipe(appContext) }
			launch { createWindowsSolarXRPipe(appContext) }
		}

		else -> Unit
	}
}
