package dev.slimevr.reset

interface ResetListener {
	fun onStarted(resetType: UByte, bodyParts: List<UByte>? = null, progress: Int, duration: Int)

	fun onFinished(resetType: UByte, bodyParts: List<UByte>? = null, duration: Int)
}
