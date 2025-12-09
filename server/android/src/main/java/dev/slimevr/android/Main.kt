@file:JvmName("Main")

package dev.slimevr.android

import android.content.Context
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import dev.slimevr.Keybinding
import dev.slimevr.VRServer
import dev.slimevr.android.serial.AndroidSerialHandler
import dev.slimevr.android.tracking.trackers.hid.AndroidHIDManager
import dev.slimevr.config.ConfigManager
import dev.slimevr.tracking.trackers.Tracker
import io.eiren.util.logging.LogManager
import java.io.File
import kotlin.concurrent.thread
import kotlin.system.exitProcess

lateinit var vrServer: VRServer
	private set
val vrServerInitialized: Boolean
	get() = ::vrServer.isInitialized

fun startVRServer(activity: AppCompatActivity) {
	thread(start = true, name = "Main VRServer Thread") {
		try {
			LogManager.initialize(activity.filesDir)
		} catch (e1: java.lang.Exception) {
			e1.printStackTrace()
		}

		try {
			val configPath = File(activity.filesDir, "vrconfig.yml").absolutePath
			val configManager = ConfigManager(configPath)
			configManager.loadConfig()

			vrServer = VRServer(
				configManager = configManager,
				serialHandlerProvider = { _ -> AndroidSerialHandler(activity) },
				acquireMulticastLock = {
					val wifi = activity.getSystemService(Context.WIFI_SERVICE) as WifiManager
					val lock = wifi.createMulticastLock("slimevr-jmdns-multicast-lock")
					lock.setReferenceCounted(true)
					lock.acquire()
				},
			)
			vrServer.start()

			// Start service for USB HID trackers
			val androidHidManager = AndroidHIDManager(
				"Sensors HID service",
				{ tracker: Tracker -> vrServer.registerTracker(tracker) },
				activity,
			)
			androidHidManager.start()

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
