package dev.slimevr

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import io.eiren.util.logging.LogManager

class MainActivity : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

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

		// Load the web GUI web page
		LogManager.info("[MainActivity] Initializing GUI WebView...")
		val guiWebView = findViewById<WebView>(R.id.guiWebView)

		// Configure for web GUI
		WebView.setWebContentsDebuggingEnabled(true)
		guiWebView.settings.javaScriptEnabled = true
		guiWebView.settings.domStorageEnabled = true
		guiWebView.settings.setSupportZoom(true)

		// Load GUI page
		guiWebView.loadUrl("http://127.0.0.1:8080/")
		LogManager.info("[MainActivity] GUI WebView has been initialized and loaded.")
	}
}
