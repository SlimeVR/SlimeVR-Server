package dev.slimevr.reset

import java.util.Timer
import kotlin.concurrent.schedule
import kotlin.math.min

fun resetTimer(timer: Timer, delay: Long, onTick: (progress: Int) -> Unit, onComplete: () -> Unit) {
	var progress = 0
	val period = min(1000, delay)
	if (period == 0L) {
		onComplete()
		return
	}
	timer.schedule(0, period) {
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
}
