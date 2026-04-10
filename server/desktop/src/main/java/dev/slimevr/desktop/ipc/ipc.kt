package dev.slimevr.desktop.ipc

import dev.slimevr.CURRENT_PLATFORM
import dev.slimevr.Platform
import dev.slimevr.VRServer
import dev.slimevr.solarxr.SolarXRConnectionBehaviour
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

const val DRIVER_SOCKET_NAME = "SlimeVRDriver"
const val FEEDER_SOCKET_NAME = "SlimeVRInput"
const val SOLARXR_SOCKET_NAME = "SlimeVRRpc"

const val DRIVER_PIPE = "\\\\.\\pipe\\SlimeVRDriver"
const val FEEDER_PIPE = "\\\\.\\pipe\\SlimeVRInput"
const val SOLARXR_PIPE = "\\\\.\\pipe\\SlimeVRRpc"

suspend fun createIpcServers(server: VRServer, behaviours: List<SolarXRConnectionBehaviour>) = coroutineScope {
	when (CURRENT_PLATFORM) {
		Platform.LINUX, Platform.OSX -> {
			launch { createUnixDriverSocket(server) }
			launch { createUnixFeederSocket(server) }
			launch { createUnixSolarXRSocket(behaviours) }
		}

		Platform.WINDOWS -> {
			launch { createWindowsDriverPipe(server) }
			launch { createWindowsFeederPipe(server) }
			launch { createWindowsSolarXRPipe(behaviours) }
		}

		else -> Unit
	}
}
