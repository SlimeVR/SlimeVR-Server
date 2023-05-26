package dev.slimevr.setup

import dev.slimevr.tracking.trackers.Tracker
import java.util.concurrent.CopyOnWriteArrayList

class TapSetupHandler {
	private val listeners: MutableList<TapSetupListener> = CopyOnWriteArrayList()

	fun addListener(listener: TapSetupListener) {
		listeners.add(listener)
	}

	fun removeListener(listener: TapSetupListener) {
		listeners.remove(listener)
	}

	fun sendTap(tracker: Tracker) {
		listeners.forEach { it.onStarted(tracker) }
	}
}

interface TapSetupListener {
	fun onStarted(tracker: Tracker)
}
