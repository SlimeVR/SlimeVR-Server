package dev.slimevr.serial

private const val MAX_LINE_BYTES = 4096

class LineAssembler {
	private var buffer = ByteArray(256)
	private var size = 0

	fun feed(data: ByteArray, length: Int, onLine: (String) -> Unit) {
		for (i in 0 until length) {
			val byte = data[i]
			if (byte == NEWLINE) {
				emit(onLine)
				continue
			}
			if (size == buffer.size) buffer = buffer.copyOf(size * 2)
			buffer[size++] = byte
			if (size >= MAX_LINE_BYTES) emit(onLine)
		}
	}

	private fun emit(onLine: (String) -> Unit) {
		val line = String(buffer, 0, size, Charsets.UTF_8).trimEnd()
		size = 0
		onLine(line)
	}

	private companion object {
		const val NEWLINE = '\n'.code.toByte()
	}
}
