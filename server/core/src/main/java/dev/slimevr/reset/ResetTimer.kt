package dev.slimevr.reset

import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.math.floor
import kotlin.math.min

class ResetTimerManager {
	val timer: Timer = Timer()
	val timers: ArrayList<TimerTask> = arrayListOf()

	fun cancelTimers() {
		timers.forEach { it.cancel() }
	}
}

fun resetTimer(
	resetTimerManager: ResetTimerManager,
	stillTime: Long,
	delay: Long,
	isUserStatic: () -> Boolean = { true },
	onTick: (progress: Int) -> Unit,
	onComplete: () -> Unit,
) {
	resetTimerManager.cancelTimers()

	if (delay == 0L) {
		onComplete()
		return
	}

	val tickMs: Long = 100L
	var elapsed: Long = 0L
	var lastGuiTick: Int = -1
	val task: TimerTask = resetTimerManager.timer.scheduleAtFixedRate(tickMs, tickMs) {
		try {
			if (stillTime > 0 && !isUserStatic()) {
				// Trackers are in motion
				elapsed = -stillTime
				lastGuiTick = -1
				onTick(-stillTime.toInt())
				return@scheduleAtFixedRate
			}
			elapsed = min(elapsed + tickMs, delay)
			if (elapsed >= delay) {
				onComplete()
				cancel()
			} else {
				val nextTick = floor(elapsed / 1000f).toInt()
				if (nextTick > lastGuiTick) {
					onTick(nextTick * 1000)
					lastGuiTick = nextTick
				}
			}
		} catch (e: Exception) {
			cancel()
		}
	}
	resetTimerManager.timers.add(task)
}
