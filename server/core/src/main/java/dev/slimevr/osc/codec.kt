package dev.slimevr.osc

private val BUNDLE_PREFIX = "#bundle".encodeToByteArray() + byteArrayOf(0)

private fun pad4(n: Int) = (n + 3) and 3.inv()

private class Writer(capacity: Int = 65536) {
	private val buf = ByteArray(capacity)
	var pos = 0

	fun byte(v: Byte) {
		buf[pos++] = v
	}
	fun bytes(v: ByteArray) {
		v.copyInto(buf, pos)
		pos += v.size
	}
	fun int(v: Int) {
		byte((v ushr 24).toByte())
		byte((v ushr 16).toByte())
		byte((v ushr 8).toByte())
		byte(v.toByte())
	}
	fun long(v: Long) {
		int((v ushr 32).toInt())
		int(v.toInt())
	}
	fun float(v: Float) = int(v.toBits())
	fun double(v: Double) = long(v.toBits())
	fun string(s: String) {
		val b = s.encodeToByteArray()
		bytes(b)
		byte(0)
		repeat(pad4(b.size + 1) - b.size - 1) { byte(0) }
	}
	fun result() = buf.copyOfRange(0, pos)
}

private class Reader(private val bytes: ByteArray, var pos: Int = 0) {
	private fun byte() = bytes[pos++].toInt()
	fun int() = (byte() and 0xFF shl 24) or (byte() and 0xFF shl 16) or (byte() and 0xFF shl 8) or (byte() and 0xFF)
	fun long() = (int().toLong() and 0xFFFFFFFFL shl 32) or (int().toLong() and 0xFFFFFFFFL)
	fun float() = Float.fromBits(int())
	fun double() = Double.fromBits(long())
	fun string(): String {
		val start = pos
		while (pos < bytes.size && bytes[pos] != 0.toByte()) pos++
		val s = bytes.decodeToString(start, pos)
		pos++ // skip null
		pos = pad4(pos)
		return s
	}
	fun bytes(n: Int) = bytes.copyOfRange(pos, pos + n).also { pos += n }
	fun hasPrefix(prefix: ByteArray) = pos + prefix.size <= bytes.size &&
		bytes.copyOfRange(pos, pos + prefix.size).contentEquals(prefix)
}

fun encodeMessage(msg: OscMessage): ByteArray {
	val w = Writer()
	w.string(msg.address)
	w.string("," + msg.args.joinToString("") { it.typeTag.toString() })
	for (arg in msg.args) {
		when (arg) {
			is OscArg.Int -> w.int(arg.value)

			is OscArg.Long -> w.long(arg.value)

			is OscArg.Float -> w.float(arg.value)

			is OscArg.Double -> w.double(arg.value)

			is OscArg.String -> w.string(arg.value)

			is OscArg.Blob -> {
				w.int(arg.value.size)
				w.bytes(arg.value)
				repeat(pad4(arg.value.size) - arg.value.size) { w.byte(0) }
			}

			else -> {}
		}
	}
	return w.result()
}

fun encodeBundle(bundle: OscBundle): ByteArray {
	val w = Writer()
	w.bytes(BUNDLE_PREFIX)
	w.long(bundle.timetag)
	for (content in bundle.contents) {
		val encoded = when (content) {
			is OscContent.Message -> encodeMessage(content.msg)
			is OscContent.Bundle -> encodeBundle(content.bundle)
		}
		w.int(encoded.size)
		w.bytes(encoded)
	}
	return w.result()
}

fun decodeMessage(bytes: ByteArray, offset: Int = 0): Pair<OscMessage, Int> {
	val r = Reader(bytes, offset)
	val address = r.string()
	val typeTag = r.string()
	val args = mutableListOf<OscArg>()
	if (typeTag.startsWith(",")) {
		for (c in typeTag.drop(1)) {
			when (c) {
				'i' -> args += OscArg.Int(r.int())

				'h' -> args += OscArg.Long(r.long())

				'f' -> args += OscArg.Float(r.float())

				'd' -> args += OscArg.Double(r.double())

				's' -> args += OscArg.String(r.string())

				'b' -> {
					val size = r.int()
					args += OscArg.Blob(r.bytes(size))
					r.pos += pad4(size) - size
				}

				'I' -> args += OscArg.Impulse

				'N' -> args += OscArg.Null

				'T' -> args += OscArg.True

				'F' -> args += OscArg.False
			}
		}
	}
	return OscMessage(address, args) to r.pos
}

fun decodeBundle(bytes: ByteArray, offset: Int = 0): Pair<OscBundle, Int> {
	val r = Reader(bytes, offset)
	if (!r.hasPrefix(BUNDLE_PREFIX)) throw IllegalArgumentException("Invalid bundle prefix")
	r.pos += 8
	val timetag = r.long()
	val contents = mutableListOf<OscContent>()
	while (r.pos + 4 <= bytes.size) {
		val elementSize = r.int()
		if (elementSize <= 0 || r.pos + elementSize > bytes.size) break
		val elemStart = r.pos
		try {
			contents += if (Reader(bytes, elemStart).hasPrefix(BUNDLE_PREFIX)) {
				OscContent.Bundle(decodeBundle(bytes, elemStart).first)
			} else {
				OscContent.Message(decodeMessage(bytes, elemStart).first)
			}
		} catch (_: Exception) {
			break
		}
		r.pos = elemStart + elementSize
	}
	return OscBundle(timetag, contents) to r.pos
}
