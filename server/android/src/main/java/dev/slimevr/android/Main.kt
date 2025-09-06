@file:JvmName("Main")

package dev.slimevr.android

import android.content.Context
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import dev.slimevr.Keybinding
import dev.slimevr.VRServer
import dev.slimevr.android.serial.AndroidSerialHandler
import io.eiren.util.logging.LogManager
import java.io.File
import kotlin.concurrent.thread
import kotlin.system.exitProcess

lateinit var vrServer: VRServer
	private set
val vrServerInitialized: Boolean
	get() = ::vrServer.isInitialized

fun main(activity: AppCompatActivity) {
	thread(start = true, name = "Main VRServer Thread") {
		try {
			LogManager.initialize(activity.filesDir)
		} catch (e1: java.lang.Exception) {
			e1.printStackTrace()
		}

		try {
			vrServer = VRServer(
				configPath = File(activity.filesDir, "vrconfig.yml").absolutePath,
				serialHandlerProvider = { _ -> AndroidSerialHandler(activity) },
				acquireMulticastLock = {
					val wifi = activity.getSystemService(Context.WIFI_SERVICE) as WifiManager
					val lock = wifi.createMulticastLock("slimevr-jmdns-multicast-lock")
					lock.setReferenceCounted(true)
					lock.acquire()
				},
			)
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
