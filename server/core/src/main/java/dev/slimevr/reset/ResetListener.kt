package dev.slimevr.reset

import dev.slimevr.protocol.rpc.TransactionInfo

interface ResetListener {
	fun onStarted(resetType: Int, tx: TransactionInfo?, bodyParts: List<Int>? = null, progress: Int, duration: Int)

	fun onFinished(resetType: Int, tx: TransactionInfo?, bodyParts: List<Int>? = null, duration: Int)
}
