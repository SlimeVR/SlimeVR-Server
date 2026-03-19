@file:JvmName("Main")

package dev.slimevr.desktop

import dev.slimevr.VRServer
import dev.slimevr.config.createConfig
import dev.slimevr.tracker.udp.createUDPTrackerServer
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) = runBlocking {
	val config = createConfig(this)
	val server = VRServer.create(this)

	createUDPTrackerServer(server, config)

	Unit
}
