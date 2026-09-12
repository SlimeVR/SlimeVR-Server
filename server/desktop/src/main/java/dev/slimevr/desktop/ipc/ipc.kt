package dev.slimevr.desktop.ipc

import dev.slimevr.AppContextProvider
import dev.slimevr.CURRENT_PLATFORM
import dev.slimevr.Platform
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

const val SOLARXR_SOCKET_NAME = "SlimeVRRpc"

// Frames larger than this are refused: nothing in either protocol comes close
internal const val MAX_FRAME_SIZE = 256 * 1024

suspend fun createIpcServers(appContext: AppContextProvider) = coroutineScope {
	when (CURRENT_PLATFORM) {
		Platform.LINUX, Platform.WINDOWS, Platform.OSX -> launch { createUnixSolarXRSocket(appContext) }
		else -> Unit
	}
}
