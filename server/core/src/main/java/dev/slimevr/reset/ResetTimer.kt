package dev.slimevr.reset

import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.schedule
import kotlin.math.min


class ResetTimerManager {
	val timer: Timer = Timer();
	val timers: ArrayList<TimerTask> = arrayListOf();

	fun cancelTimers() {
		timers.forEach { it.cancel() }
	}
}

fun resetTimer(resetTimerManager: ResetTimerManager, delay: Long, onTick: (progress: Int) -> Unit, onComplete: () -> Unit) {
	resetTimerManager.cancelTimers()

	var progress = 0
	val period = min(1000, delay)
	if (period == 0L) {
		onComplete()
		return
	}

	val task = resetTimerManager.timer.schedule(0, period) {
		val current = progress * 1000
		if (current >= delay) {
			cancel()
			onComplete()
		} else {
			if (delay >= 1000L)
				onTick(current)
		}
		progress++
	}
	resetTimerManager.timers.add(task);
}
