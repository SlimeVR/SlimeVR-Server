package dev.slimevr.reset

import dev.slimevr.protocol.rpc.TransactionInfo
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Consumer

class ResetHandler {
	private val listeners: MutableList<ResetListener> = CopyOnWriteArrayList()

	fun sendStarted(resetType: Int, tx: TransactionInfo? = null, bodyParts: List<Int>? = null, progress: Int = 0, duration: Int = 0) {
		this.listeners.forEach { listener: ResetListener -> listener.onStarted(resetType, tx, bodyParts, progress, duration) }
	}

	fun sendFinished(resetType: Int, tx: TransactionInfo? = null, bodyParts: List<Int>? = null, duration: Int) {
		this.listeners.forEach { listener: ResetListener -> listener.onFinished(resetType, tx, bodyParts, duration) }
	}

	fun addListener(listener: ResetListener) {
		this.listeners.add(listener)
	}

	fun removeListener(l: ResetListener) {
		listeners.removeIf { listener: ResetListener -> l === listener }
	}
}
