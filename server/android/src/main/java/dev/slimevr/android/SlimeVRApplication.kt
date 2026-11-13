package dev.slimevr.android

import android.app.Application
import dev.slimevr.util.installUncaughtExceptionReporting

/** Installs the crash handler once per process, before any activity or service */
class SlimeVRApplication : Application() {
	override fun onCreate() {
		super.onCreate()
		installUncaughtExceptionReporting()
	}
}
