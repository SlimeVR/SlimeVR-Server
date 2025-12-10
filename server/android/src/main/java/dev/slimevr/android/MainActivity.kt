package dev.slimevr.android

import android.content.Intent
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import io.eiren.util.logging.LogManager
import java.io.IOException
import java.net.URLConnection
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class AndroidJsObject {
	@JavascriptInterface
	fun isThere(): Boolean = true
}

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

		initLock.withLock {
			// Start the server if it isn't already running
			if (!vrServerInitialized) {
				LogManager.info("[MainActivity] VRServer isn't running yet, starting it...")
				startVRServer(this)
			} else {
				LogManager.info("[MainActivity] VRServer is already running, skipping initialization.")
			}
		}

		// Load the web GUI web page
		LogManager.info("[MainActivity] Initializing GUI WebView...")
		val guiWebView = findViewById<WebView>(R.id.guiWebView)

		// ## Configure for web GUI ##
		// Enable debug mode
		WebView.setWebContentsDebuggingEnabled(true)

		// Handle path resolution
		guiWebView.webViewClient = object : WebViewClient() {
			override fun shouldInterceptRequest(
				view: WebView,
				request: WebResourceRequest,
			): WebResourceResponse? {
				if ((request.url.scheme != "http" && request.url.scheme != "https") ||
					request.url.host != "slimevr.gui"
				) {
					return null
				}

				val path = when (request.url.path) {
					null, "", "/" -> "/index.html"
					else -> request.url.path
				}

				return try {
					WebResourceResponse(
						URLConnection.guessContentTypeFromName(path) ?: "text/plain",
						null,
						assets.open("web-gui$path"),
					)
				} catch (_: IOException) {
					WebResourceResponse(null, null, null)
				}
			}
		}

		// Set required features
		guiWebView.settings.javaScriptEnabled = true
		guiWebView.settings.domStorageEnabled = true

		// TODO: Let code know it is in android, should be gone when we start using tauri
		guiWebView.addJavascriptInterface(AndroidJsObject(), "__ANDROID__")

		// Try fixing zoom usability
		guiWebView.settings.setSupportZoom(true)
		guiWebView.settings.useWideViewPort = true
		guiWebView.settings.loadWithOverviewMode = true
		guiWebView.invokeZoomPicker()

		// Load GUI page
		guiWebView.loadUrl("https://slimevr.gui/")
		LogManager.info("[MainActivity] GUI WebView has been initialized and loaded.")

		// Start a foreground service to notify the user the SlimeVR Server is running
		// This also helps prevent Android from ejecting the process unexpectedly
		val serviceIntent = Intent(this, ForegroundService::class.java)
		startForegroundService(serviceIntent)
	}

	companion object {
		val initLock = ReentrantLock()
	}
}
