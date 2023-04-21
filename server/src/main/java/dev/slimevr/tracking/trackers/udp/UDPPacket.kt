package dev.slimevr.tracking.trackers.udp

import io.github.axisangles.ktmath.Quaternion
import java.io.IOException
import java.nio.ByteBuffer

sealed class UDPPacket(val packetId: Int) {
	@Throws(IOException::class)
	open fun readData(buf: ByteBuffer) {}

	@Throws(IOException::class)
	open fun writeData(buf: ByteBuffer) {}

	companion object {
		/**
		 * Naively read null-terminated ASCII string from the byte buffer
		 *
		 * @param buf
		 * @return
		 * @throws IOException
		 */
		@Throws(IOException::class)
		fun readASCIIString(buf: ByteBuffer): String {
			val sb = StringBuilder()
			while (true) {
				val c = (buf.get().toInt() and 0xFF).toChar()
				if (c.code == 0) break
				sb.append(c)
			}
			return sb.toString()
		}

		@JvmStatic
		@Throws(IOException::class)
		fun readASCIIString(buf: ByteBuffer, length: Int): String {
			var length = length
			val sb = StringBuilder()
			while (length-- > 0) {
				val c = (buf.get().toInt() and 0xFF).toChar()
				if (c.code == 0) break
				sb.append(c)
			}
			return sb.toString()
		}

		/**
		 * Naively write null-terminated ASCII string to byte buffer
		 *
		 * @param str
		 * @param buf
		 * @throws IOException
		 */
		@Throws(IOException::class)
		fun writeASCIIString(str: String, buf: ByteBuffer) {
			for (element in str) {
				buf.put((element.code and 0xFF).toByte())
			}
			buf.put(0.toByte())
		}
	}
}

data object UDPPacket0Heartbeat : UDPPacket(0)
data object UDPPacket1Heartbeat : UDPPacket(1)
data class UDPPacket1Rotation(var rotation: Quaternion = Quaternion.IDENTITY) : UDPPacket(1), SensorSpecificPacket {
	override val sensorId = 0
	override fun readData(buf: ByteBuffer) {
		val x = buf.float
		val y = buf.float
		val z = buf.float
		val w = buf.float
		rotation = Quaternion(w, x, y, z)
	}
}

data class UDPPacket3Handshake(val version: Int = 0, val name: String = "") : UDPPacket(3) {
	override fun readData(buf: ByteBuffer) {
		version = buf.int
		name = readASCIIString(buf)
	}

	override fun writeData(buf: ByteBuffer) {
		// Never sent back in current protocol
		// Handshake for RAW SlimeVR and legacy owoTrack has different packet id
		// byte
		// order from normal packets
		// So it's handled by raw protocol call
	}
}
