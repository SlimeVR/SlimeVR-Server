package dev.slimevr.android

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsetsController
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.IOException
import java.net.URLConnection

class AndroidJsObject(val updateBackground: (String) -> Unit) {
	@JavascriptInterface
	fun isThere(): Boolean = true

	@JavascriptInterface
	fun updateInsetBackground(color: String) = updateBackground(color)
}

class MainActivity : AppCompatActivity() {
	private val requestNotificationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
		startVRServer(this)
	}

	private val finishReceiver = object : BroadcastReceiver() {
		override fun onReceive(context: Context?, intent: Intent?) = finishAndRemoveTask()
	}

	private fun updateBackground(newColor: String) {
		val rgb = newColor.split(",").map { it.trim().toInt() }
		if (rgb.size != 3) {
			Log.e(TAG, "RGB string is malformed: $newColor")
			return
		}

		val colorInt = Color.rgb(rgb[0], rgb[1], rgb[2])

		val parentView = findViewById<ConstraintLayout>(R.id.constraintLayoutView)
		parentView.setBackgroundColor(colorInt)

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			window.isNavigationBarContrastEnforced = false
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			val mask = WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
			val appearance = if (Color.luminance(colorInt) > 0.5) mask else 0

			window.decorView.windowInsetsController?.setSystemBarsAppearance(appearance, mask)
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		ContextCompat.registerReceiver(
			this,
			finishReceiver,
			IntentFilter(ACTION_FINISH_APP),
			ContextCompat.RECEIVER_NOT_EXPORTED,
		)

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
			ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
		) {
			requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
		} else {
			startVRServer(this)
		}

		Log.i(TAG, "Initializing GUI WebView...")
		val guiWebView = findViewById<WebView>(R.id.guiWebView)

		WebView.setWebContentsDebuggingEnabled(true)

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

		guiWebView.settings.javaScriptEnabled = true
		guiWebView.settings.domStorageEnabled = true

		val parentView = findViewById<ConstraintLayout>(R.id.constraintLayoutView)

		ViewCompat.setOnApplyWindowInsetsListener(parentView) { v, windowInsets ->
			val insets = windowInsets.getInsets(
				WindowInsetsCompat.Type.systemBars()
					or WindowInsetsCompat.Type.displayCutout()
					or WindowInsetsCompat.Type.ime(),
			)

			v.setPadding(insets.left, insets.top, insets.right, insets.bottom)
			WindowInsetsCompat.CONSUMED
		}

		guiWebView.addJavascriptInterface(
			AndroidJsObject(updateBackground = { newColor ->
				parentView.post {
					updateBackground(newColor)
				}
			}),
			"__ANDROID__",
		)

		guiWebView.settings.setSupportZoom(true)
		guiWebView.settings.useWideViewPort = true
		guiWebView.settings.loadWithOverviewMode = true
		guiWebView.invokeZoomPicker()

		guiWebView.loadUrl("https://slimevr.gui/")
		Log.i(TAG, "GUI WebView initialized.")
	}

	override fun onDestroy() {
		unregisterReceiver(finishReceiver)
		super.onDestroy()
	}

	companion object {
		private const val TAG = "MainActivity"
	}
}
