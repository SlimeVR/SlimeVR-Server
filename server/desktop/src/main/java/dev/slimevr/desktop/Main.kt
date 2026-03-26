@file:JvmName("Main")

package dev.slimevr.desktop

import dev.slimevr.VRServer
import dev.slimevr.config.createAppConfig
import dev.slimevr.desktop.hid.createDesktopHIDManager
import dev.slimevr.desktop.ipc.createIpcServers
import dev.slimevr.desktop.serial.createDesktopSerialServer
import dev.slimevr.firmware.createFirmwareManager
import dev.slimevr.resolveConfigDirectory
import dev.slimevr.solarxr.createSolarXRWebsocketServer
import dev.slimevr.udp.createUDPTrackerServer
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) = runBlocking {
	val configFolder = resolveConfigDirectory() ?: error("Unable to resolve config folder")
	val config = createAppConfig(this, configFolder = configFolder.toFile())
	val serialServer = createDesktopSerialServer(this)
	val firmwareManager = createFirmwareManager(serialServer = serialServer, scope = this)
	val server = VRServer.create(this, serialServer, firmwareManager)

	launch {
		createUDPTrackerServer(server, config)
	}
	launch {
		createSolarXRWebsocketServer(server)
	}
	launch {
		createIpcServers(server)
	}
	launch {
		createDesktopHIDManager(server, this)
	}
	Unit
}
