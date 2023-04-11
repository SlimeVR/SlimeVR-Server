@file:JvmName("Main")

package dev.slimevr

import androidx.appcompat.app.AppCompatActivity
import io.eiren.util.logging.LogManager
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.defaultResource
import io.ktor.server.http.content.resources
import io.ktor.server.http.content.static
import io.ktor.server.http.content.staticBasePackage
import io.ktor.server.netty.Netty
import io.ktor.server.routing.routing
import java.io.File
import kotlin.concurrent.thread
import kotlin.system.exitProcess

val VERSION = "v0.7.0/android"
lateinit var vrServer: VRServer
	private set
val vrServerInitialized: Boolean
	get() = ::vrServer.isInitialized

fun main(activity: AppCompatActivity) {
	// Host the web GUI server
	embeddedServer(Netty, port = 8080) {
		routing {
			static("/") {
				staticBasePackage = "web-gui"
				resources()
				defaultResource("index.html")
			}
		}
	}.start(wait = false)

	thread(start = true, name = "Main VRServer Thread") {
		try {
			LogManager.initialize(activity.filesDir)
		} catch (e1: java.lang.Exception) {
			e1.printStackTrace()
		}
		LogManager.info("Running version $VERSION")
		try {
			vrServer = VRServer(File(activity.filesDir, "vrconfig.yml").absolutePath)
			vrServer.start()
			Keybinding(vrServer)
			vrServer.join()
			LogManager.closeLogger()
			exitProcess(0)
		} catch (e: Throwable) {
			e.printStackTrace()
			exitProcess(1)
		}
	}
}
