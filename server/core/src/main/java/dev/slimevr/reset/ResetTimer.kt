package dev.slimevr.reset

import java.util.Timer
import kotlin.concurrent.schedule

fun resetTimer(timer: Timer, delay: Long, onTick: (progress: Int) -> Unit, onComplete: () -> Unit) {
	var progress = 0
	timer.schedule(0, 1000) {
		val current = progress * 1000
		if (current >= delay) {
			cancel()
			onComplete()
		} else {
			onTick(current)
		}
		progress++
	}
}
