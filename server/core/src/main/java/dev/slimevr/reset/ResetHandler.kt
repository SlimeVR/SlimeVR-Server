package dev.slimevr.reset

import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Consumer

class ResetHandler {
	private val listeners: MutableList<ResetListener> = CopyOnWriteArrayList()

	fun sendStarted(resetType: Int, txId: Long? = null, bodyParts: List<Int>? = null, progress: Int = 0, duration: Int = 0) {
		this.listeners.forEach { listener: ResetListener -> listener.onStarted(resetType, txId, bodyParts, progress, duration) }
	}

	fun sendFinished(resetType: Int, txId: Long? = null, bodyParts: List<Int>? = null, duration: Int) {
		this.listeners.forEach { listener: ResetListener -> listener.onFinished(resetType, txId, bodyParts, duration) }
	}

	fun addListener(listener: ResetListener) {
		this.listeners.add(listener)
	}

	fun removeListener(l: ResetListener) {
		listeners.removeIf { listener: ResetListener -> l === listener }
	}
}
