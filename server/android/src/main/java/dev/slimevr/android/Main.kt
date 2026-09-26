@file:JvmName("Main")

package dev.slimevr.android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

fun startVRServer(activity: AppCompatActivity) {
	activity.startForegroundService(Intent(activity, ForegroundService::class.java))
}
