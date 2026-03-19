@file:JvmName("Main")

package dev.slimevr.desktop

import dev.slimevr.VRServer
import dev.slimevr.config.createConfig
import dev.slimevr.solarxr.createSolarXRWebsocketServer
import dev.slimevr.tracker.udp.createUDPTrackerServer
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) = runBlocking {
	val config = createConfig(this)
	val server = VRServer.create(this)

	launch {
		createUDPTrackerServer(server, config)
	}
	launch {
		createSolarXRWebsocketServer()
	}
	Unit
}
