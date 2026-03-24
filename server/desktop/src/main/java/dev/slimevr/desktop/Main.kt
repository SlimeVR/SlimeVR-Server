@file:JvmName("Main")

package dev.slimevr.desktop

import dev.slimevr.VRServer
import dev.slimevr.config.createAppConfig
import dev.slimevr.desktop.ipc.createIpcServers
import dev.slimevr.solarxr.createSolarXRWebsocketServer
import dev.slimevr.tracker.udp.createUDPTrackerServer
import dev.slimevr.resolveConfigDirectory
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) = runBlocking {
	val configFolder = resolveConfigDirectory() ?: error("Unable to resolve config folder")
	val config = createAppConfig(this, configFolder = configFolder.toFile())
	val server = VRServer.create(this)

	launch {
		createUDPTrackerServer(server, config)
	}
	launch {
		createSolarXRWebsocketServer(server)
	}
	launch {
		createIpcServers(server)
	}
	Unit
}
