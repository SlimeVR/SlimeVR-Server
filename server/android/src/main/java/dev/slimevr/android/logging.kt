package dev.slimevr.android

import io.klogging.Level
import io.klogging.config.loggingConfiguration
import io.klogging.sending.ANDROID

fun setupAndroidLogging() {
	loggingConfiguration {
		sink("android", ANDROID)
		logging {
			fromMinLevel(Level.DEBUG) {
				toSink("android")
			}
		}
	}
}
