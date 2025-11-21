package dev.slimevr.guards

import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.schedule

class ServerGuards {

	var canDoMounting: Boolean = false
	var canDoYawReset: Boolean = false
	var canDoUserHeightCalibration: Boolean = false

	private val timer = Timer()
	private var mountingTimeoutTask: TimerTask? = null

	fun onFullReset() {
		canDoMounting = true
		canDoYawReset = true
		mountingTimeoutTask?.cancel()
		mountingTimeoutTask = timer.schedule(MOUNTING_RESET_TIMEOUT) {
			canDoMounting = false
		}
	}

	companion object {
		const val MOUNTING_RESET_TIMEOUT = 2 * 60 * 1000L
	}
}
