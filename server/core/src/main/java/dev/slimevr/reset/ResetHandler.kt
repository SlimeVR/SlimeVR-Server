package dev.slimevr.reset

import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Consumer

class ResetHandler {
	private val listeners: MutableList<ResetListener> = CopyOnWriteArrayList()

	fun sendStarted(resetType: UByte, bodyParts: List<UByte>? = null, progress: Int = 0, duration: Int = 0) {
		this.listeners.forEach { listener: ResetListener -> listener.onStarted(resetType, bodyParts, progress, duration) }
	}

	fun sendFinished(resetType: UByte, bodyParts: List<UByte>? = null, duration: Int) {
		this.listeners.forEach { listener: ResetListener -> listener.onFinished(resetType, bodyParts, duration) }
	}

	fun addListener(listener: ResetListener) {
		this.listeners.add(listener)
	}

	fun removeListener(l: ResetListener) {
		listeners.removeIf { listener: ResetListener -> l === listener }
	}
}
