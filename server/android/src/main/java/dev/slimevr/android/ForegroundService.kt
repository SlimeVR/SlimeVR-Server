package dev.slimevr.android

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat

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
		val notification = createNotification()
		startForeground(NOTIFICATION_ID, notification)

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
		val serviceChannel = NotificationChannel(
			CHANNEL_ID,
			"SlimeVR Foreground Service Channel",
			NotificationManager.IMPORTANCE_LOW,
		)
		val manager = getSystemService(NotificationManager::class.java)
		manager.createNotificationChannel(serviceChannel)
	}

	private fun createNotification(): Notification = NotificationCompat.Builder(this, CHANNEL_ID)
		.setContentTitle("SlimeVR Server Running")
		.setContentText("This notification helps keep the server alive")
		.setSmallIcon(R.drawable.ic_launcher_foreground)
		.build()
}
