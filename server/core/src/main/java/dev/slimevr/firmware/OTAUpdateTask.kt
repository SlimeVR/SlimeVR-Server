package dev.slimevr.firmware

import io.eiren.util.logging.LogManager
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.EOFException
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.function.Consumer
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import kotlin.math.min

class OTAUpdateTask(
	private val firmware: ByteArray,
	private val deviceId: UpdateDeviceId<Int>,
	private val deviceIp: InetAddress,
	private val statusCallback: Consumer<UpdateStatusEvent<Int>>,
	private val port: Int = 8266,
) {
	private val receiveBuffer: ByteArray = ByteArray(69)
	var socketServer: ServerSocket? = null
	var uploadSocket: Socket? = null
	var authSocket: DatagramSocket? = null
	var canceled: Boolean = false

	@Throws(NoSuchAlgorithmException::class)
	private fun bytesToMd5(bytes: ByteArray): String {
		val md5 = MessageDigest.getInstance("MD5")
		md5.update(bytes)
		val digest = md5.digest()
		val md5str = StringBuilder()
		for (b in digest) {
			md5str.append(String.format("%02x", b))
		}
		return md5str.toString()
	}

	@Throws(NoSuchAlgorithmException::class)
	private fun bytesToSha256(bytes: ByteArray): String {
		val sha256 = MessageDigest.getInstance("SHA-256")
		sha256.update(bytes)
		val digest = sha256.digest()
		val sha256str = StringBuilder()
		for (b in digest) {
			sha256str.append(String.format("%02x", b))
		}
		return sha256str.toString()
	}

	fun pbkdf2Hmac(password: String, salt: ByteArray, iterations: Int, keyLength: Int): ByteArray {
		val spec = PBEKeySpec(password.toCharArray(), salt, iterations, keyLength * 8)
		val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
		return factory.generateSecret(spec).encoded
	}

	private fun authenticate(localPort: Int): Boolean {
		try {
			DatagramSocket().use { socket ->
				authSocket = socket
				statusCallback.accept(UpdateStatusEvent(deviceId, FirmwareUpdateStatus.AUTHENTICATING))
				LogManager.info("[OTAUpdate] Sending OTA invitation to: $deviceIp:$port")

				val fileMd5 = bytesToMd5(firmware)
				val message = "$FLASH $localPort ${firmware.size} $fileMd5\n"

				socket.send(DatagramPacket(message.toByteArray(), message.length, deviceIp, port))
				socket.soTimeout = 10000

				val authPacket = DatagramPacket(receiveBuffer, receiveBuffer.size)
				socket.receive(authPacket)

				val data = String(authPacket.data, 0, authPacket.length)

				// if we received OK directly from the MCU, we do not need to authenticate
				if (data == "OK") return true

				val args = data.split(" ")

				// The expected auth payload should look like "AUTH AUTH_TOKEN"
				// if we have less than those two args it means that we are in an invalid state
				if (args.size != 2 || args[0] != "AUTH") return false

				LogManager.info("[OTAUpdate] Authenticating...")
				var payload = ""
				var signature = ""

				val authToken = args[1]
				if (authToken.length == 32) {
					signature =
						bytesToMd5(UUID.randomUUID().toString().toByteArray())
					val hashedPassword = bytesToMd5(PASSWORD.toByteArray())
					val resultText = "$hashedPassword:$authToken:$signature"
					payload = bytesToMd5(resultText.toByteArray())
				} else if (authToken.length == 64) {
					signature =
						bytesToSha256(UUID.randomUUID().toString().toByteArray())
					val salt = "$authToken:$signature"
					val hashedPassword = bytesToSha256(PASSWORD.toByteArray())
					val derivedkey = pbkdf2Hmac(hashedPassword, salt.toByteArray(), 10000, 32)
					val derivedkeyHex = derivedkey.joinToString("") { "%02x".format(it) }
					val challenge = "$derivedkeyHex:$authToken:$signature"
					payload = bytesToSha256(challenge.toByteArray())
				}
				val authMessage = "$AUTH $signature $payload\n"

				socket.soTimeout = 10000
				socket.send(
					DatagramPacket(
						authMessage.toByteArray(),
						authMessage.length,
						deviceIp,
						port,
					),
				)

				val authResponsePacket = DatagramPacket(receiveBuffer, receiveBuffer.size)
				socket.receive(authResponsePacket)

				val authResponse = String(authResponsePacket.data, 0, authResponsePacket.length)

				return authResponse == "OK"
			}
		} catch (e: Exception) {
			LogManager.severe("OTA Authentication exception", e)
			return false
		}
	}

	private fun upload(serverSocket: ServerSocket): Boolean {
		var connection: Socket? = null
		try {
			LogManager.info("[OTAUpdate] Starting on: ${serverSocket.localPort}")
			LogManager.info("[OTAUpdate] Waiting for device...")

			connection = serverSocket.accept()
			this.uploadSocket = connection
			connection.setSoTimeout(1000)
			val dos = DataOutputStream(connection.getOutputStream())
			val dis = DataInputStream(connection.getInputStream())

			LogManager.info("[OTAUpdate] Upload size: ${firmware.size} bytes")
			var offset = 0
			val chunkSize = 2048
			while (offset != firmware.size && !canceled) {
				statusCallback.accept(
					UpdateStatusEvent(
						deviceId,
						FirmwareUpdateStatus.UPLOADING,
						((offset.toDouble() / firmware.size) * 100).toInt(),
					),
				)

				val chunkLen = min(chunkSize, (firmware.size - offset))
				dos.write(firmware, offset, chunkLen)
				dos.flush()
				offset += chunkLen

				// Those skipped bytes are the size written to the MCU. We do not really need that information,
				// so we simply skip it.
				// The reason those bytes are skipped here is to not have to skip all of them when checking
				// for the OK response. Saving time
				val bytesSkipped = dis.skipBytes(4)
				// Replicate behaviour of .skipNBytes()
				if (bytesSkipped != 4) {
					throw IOException("Unexpected number of bytes skipped: $bytesSkipped")
				}
			}
			if (canceled) return false

			LogManager.info("[OTAUpdate] Waiting for result...")
			// We set the timeout of the connection bigger as it can take some time for the MCU
			// to confirm that everything is ok
			connection.setSoTimeout(10000)
			val responseBytes = dis.readBytes()
			val response = String(responseBytes)

			return response.contains("OK")
		} catch (e: Exception) {
			LogManager.severe("Unable to upload the firmware using ota", e)
			return false
		} finally {
			connection?.close()
		}
	}

	fun run() {
		ServerSocket(0).use { serverSocket ->
			socketServer = serverSocket
			if (!authenticate(serverSocket.localPort)) {
				statusCallback.accept(
					UpdateStatusEvent(
						deviceId,
						FirmwareUpdateStatus.ERROR_AUTHENTICATION_FAILED,
					),
				)
				return
			}

			if (!upload(serverSocket)) {
				statusCallback.accept(
					UpdateStatusEvent(
						deviceId,
						FirmwareUpdateStatus.ERROR_UPLOAD_FAILED,
					),
				)
				return
			}

			statusCallback.accept(
				UpdateStatusEvent(
					deviceId,
					FirmwareUpdateStatus.REBOOTING,
				),
			)
		}
	}

	fun cancel() {
		canceled = true
		socketServer?.close()
		authSocket?.close()
		uploadSocket?.close()
	}

	companion object {
		private const val FLASH = 0
		private const val PASSWORD = "SlimeVR-OTA"
		private const val AUTH = 200
	}
}
