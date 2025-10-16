package dev.slimevr.android

import android.content.Intent
import android.os.Bundle
import android.webkit.JavascriptInterface
import androidx.activity.enableEdgeToEdge
import io.eiren.util.logging.LogManager

class AndroidJsObject {
	@JavascriptInterface
	fun isThere(): Boolean = true
}

class MainActivity : TauriActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		enableEdgeToEdge()
		super.onCreate(savedInstanceState)

		// Initialize logger (doesn't re-initialize if already run)
		try {
			LogManager.initialize(filesDir)
		} catch (e1: java.lang.Exception) {
			e1.printStackTrace()
		}

		// Start the server if it isn't already running
		if (!vrServerInitialized) {
			LogManager.info("[MainActivity] VRServer isn't running yet, starting it...")
			main(this)
		} else {
			LogManager.info("[MainActivity] VRServer is already running, skipping initialization.")
		}

		// Start a foreground service to notify the user the SlimeVR Server is running
		// This also helps prevent Android from ejecting the process unexpectedly
		val serviceIntent = Intent(this, ForegroundService::class.java)
		startForegroundService(serviceIntent)
	}
}
