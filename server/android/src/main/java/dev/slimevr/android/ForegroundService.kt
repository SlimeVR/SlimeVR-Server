package dev.slimevr.android

import android.app.*
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import io.eiren.util.logging.LogManager

/**
 * ForegroundService helps to keep the SlimeVR Server on Android from being killed.
 * Especially when the process is in the background when the user is playing a game
 * or something else.
 *
 * Processes with foreground services on Android are less likely to be kill per
 * https://developer.android.com/guide/components/activities/activity-lifecycle#asem
 *
 * Tested with VRChat on Quest 3 in high population worlds with all avatars on
 * for over an hour with no shut down of the SlimeVR server. Without this service,
 * the server would often be shutdown unexpectedly.
 *
 * Notes:
 * 		Luckily this does not show anything visual over the running app.
 * 		The notification only shows in the alert panel and cannot be dismissed.
 * 		On Quest there's no way to manually kill the Slime Server, so you have to restart the device.
 */
class ForegroundService : Service() {
	private val CHANNEL_ID = "SlimeVrForegroundServiceChannel"
	private val NOTIFICATION_ID = 100

	override fun onCreate() {
		super.onCreate()
		createNotificationChannel()
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		try {
			val notification = createNotification()
			ServiceCompat.startForeground(
				this,
				NOTIFICATION_ID, // Cannot be 0
				notification,
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
					ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE
				} else {
					0
				},
			)
		} catch (e: Exception) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
				e is ForegroundServiceStartNotAllowedException
			) {
				LogManager.severe("Tried to start foreground service when not allowed:", e)
			}
			// ...
		}
		/*
		 * Currently being a foreground process should be enough to keep the server running.
		 * If it turns out to not be enough then the next option would be to return sticky here
		 * which would also require moving server initialization from the MainActivity to here
		 * so the server is restarted when the activity is. You can tell the process is restarted
		 * if the intent variable is null.
		 */
		return START_NOT_STICKY
	}

	override fun onBind(intent: Intent?): IBinder? = null

	private fun createNotificationChannel() {
		val serviceChannel = NotificationChannelCompat.Builder(CHANNEL_ID, NotificationManager.IMPORTANCE_LOW)
			.setName("SlimeVR Foreground Service Channel")
			.build()
		NotificationManagerCompat
			.from(this)
			.createNotificationChannel(serviceChannel)
	}

	private fun createNotification(): Notification = NotificationCompat.Builder(this, CHANNEL_ID)
		.setContentTitle("SlimeVR Server Running")
		.setContentText("This notification helps keep the server alive")
		.setSmallIcon(R.drawable.ic_launcher_foreground)
		.build()
}
