package dev.slimevr.trackingpause

import java.util.concurrent.CopyOnWriteArrayList

class TrackingPauseHandler {
	private val listeners: MutableList<TrackingPauseListener> = CopyOnWriteArrayList()

	fun sendTrackingPauseState(trackingPaused: Boolean) {
		listeners.forEach { it.onTrackingPause(trackingPaused) }
	}

	fun addListener(listener: TrackingPauseListener) {
		listeners.add(listener)
	}

	fun removeListener(listener: TrackingPauseListener) {
		listeners.removeIf { it == listener }
	}
}
