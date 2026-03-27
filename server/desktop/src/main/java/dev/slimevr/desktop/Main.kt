@file:JvmName("Main")

package dev.slimevr.desktop

import dev.slimevr.VRServer
import dev.slimevr.config.createAppConfig
import dev.slimevr.desktop.hid.createDesktopHIDManager
import dev.slimevr.desktop.ipc.createIpcServers
import dev.slimevr.desktop.serial.createDesktopSerialServer
import dev.slimevr.desktop.vrchat.createDesktopVRCConfigManager
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
	val vrcConfigManager = createDesktopVRCConfigManager(
		config = config,
		scope = this,
		userHeight = { config.userConfig.context.state.value.data.userHeight.toDouble() },
	)
	val server = VRServer.create(this, serialServer, firmwareManager, vrcConfigManager)

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
