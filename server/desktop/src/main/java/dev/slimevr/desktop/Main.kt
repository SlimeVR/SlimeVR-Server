@file:JvmName("Main")

package dev.slimevr.desktop

import dev.slimevr.config.createConfig
import dev.slimevr.createVRServer
import dev.slimevr.tracker.udp.createUDPTrackerServer
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) = runBlocking {
	val config = createConfig(this)
	val server = createVRServer(this)

	val udpServer = createUDPTrackerServer(server, config)
}
