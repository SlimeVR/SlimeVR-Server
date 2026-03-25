package dev.slimevr.firmware

import java.io.ByteArrayOutputStream
import java.net.URL
import java.security.MessageDigest

data class DownloadedFirmwarePart(
	val data: ByteArray,
	val offset: Int,
) {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false
		other as DownloadedFirmwarePart
		if (!data.contentEquals(other.data)) return false
		return offset == other.offset
	}

	override fun hashCode(): Int {
		var result = data.contentHashCode()
		result = 31 * result + offset
		return result
	}
}

fun downloadFirmware(url: String, digest: String): ByteArray {
	val output = ByteArrayOutputStream()
	val chunk = ByteArray(4096)
	URL(url).openStream().use { stream ->
		while (true) {
			val read = stream.read(chunk)
			if (read <= 0) break
			output.write(chunk, 0, read)
		}
	}
	val data = output.toByteArray()
	check(verifyChecksum(data, digest)) { "Checksum verification failed for $url" }
	return data
}

fun verifyChecksum(data: ByteArray, expectedDigest: String): Boolean {
	val parts = expectedDigest.split(":", limit = 2)
	check(parts.size == 2) { "Invalid digest format '$expectedDigest', expected 'algorithm:hash'" }
	val algorithm = parts[0].uppercase().replace("-", "")
	val expectedHash = parts[1].lowercase()
	val actualHash = MessageDigest.getInstance(algorithm).digest(data).joinToString("") { "%02x".format(it) }
	return actualHash == expectedHash
}
