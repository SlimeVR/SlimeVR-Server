package dev.slimevr.guards

import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.schedule


class ServerGuards() {

	var canDoMounting: Boolean = true

	private val timer = Timer()
	private var mountingTimeoutTask: TimerTask? = null

	fun scheduleMountingTimeout() {
		canDoMounting = true
		mountingTimeoutTask?.cancel()
		mountingTimeoutTask = timer.schedule(10 * 1000) {
			canDoMounting = false
		}
	}
}
