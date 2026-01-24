package dev.slimevr.reset

interface ResetListener {
	fun onStarted(resetType: Int, txId: Long?, bodyParts: List<Int>? = null, progress: Int, duration: Int)

	fun onFinished(resetType: Int, txId: Long?, bodyParts: List<Int>? = null, duration: Int)
}
