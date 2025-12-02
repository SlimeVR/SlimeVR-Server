package dev.slimevr.reset

import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.schedule
import kotlin.math.floor
import kotlin.math.min

class ResetTimerManager {
	val timer: Timer = Timer()
	val timers: ArrayList<TimerTask> = arrayListOf()

	fun cancelTimers() {
		timers.forEach { it.cancel() }
	}
}

fun resetTimer(resetTimerManager: ResetTimerManager, delay: Long, onTick: (progress: Int) -> Unit, onComplete: () -> Unit) {
	resetTimerManager.cancelTimers()

	if (delay == 0L) {
		onComplete()
		return
	}

	val ticks: Int = floor(delay / 1000f).toInt()
	for (tick in 0..ticks) {
		if (tick * 1000L == delay) continue
		resetTimerManager.timers.add(
			resetTimerManager.timer.schedule(tick * 1000L) {
				onTick(tick * 1000)
			},
		)
	}
	resetTimerManager.timers.add(
		resetTimerManager.timer.schedule(delay) {
			onComplete()
		},
	)
}
