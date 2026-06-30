package dev.slimevr.firmware

import dev.slimevr.VRServer
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.BoundDatagramSocket
import io.ktor.network.sockets.Datagram
import io.ktor.network.sockets.InetSocketAddress
import io.ktor.network.sockets.ServerSocket
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.network.sockets.port
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.core.buildPacket
import io.ktor.utils.io.core.readText
import io.ktor.utils.io.core.writeFully
import io.ktor.utils.io.discardExact
import io.ktor.utils.io.readRemaining
import io.ktor.utils.io.writeFully
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.io.readByteArray
import solarxr_protocol.datatypes.DeviceId
import solarxr_protocol.rpc.FirmwarePart
import solarxr_protocol.rpc.FirmwareUpdateStatus
import java.security.MessageDigest
import java.util.UUID
import kotlin.math.min

private const val OTA_PORT = 8266
private const val OTA_PASSWORD = "SlimeVR-OTA"
private const val OTA_CHUNK_SIZE = 2048

private fun bytesToMd5(bytes: ByteArray): String = MessageDigest.getInstance("MD5").digest(bytes).joinToString("") { "%02x".format(it) }

private suspend fun sendDatagram(socket: BoundDatagramSocket, message: String, target: InetSocketAddress) = socket.send(Datagram(buildPacket { writeFully(message.toByteArray()) }, target))

/**
 * Sends the OTA invitation over UDP and performs the optional AUTH challenge-response.
 * Returns true if authentication succeeded (or was not required).
 */
private suspend fun otaAuthenticate(
	selectorManager: SelectorManager,
	deviceIp: String,
	localPort: Int,
	firmware: ByteArray,
): Boolean {
	val fileMd5 = bytesToMd5(firmware)
	val target = InetSocketAddress(deviceIp, OTA_PORT)

	aSocket(selectorManager).udp().bind(InetSocketAddress("0.0.0.0", 0)).use { socket ->
		sendDatagram(socket, "0 $localPort ${firmware.size} $fileMd5\n", target)

		val responseData = withTimeout(10_000) { socket.receive() }.packet.readText()
		if (responseData == "OK") return true

		val args = responseData.split(" ")
		if (args.size != 2 || args[0] != "AUTH") return false

		val authToken = args[1]
		val signature = bytesToMd5(UUID.randomUUID().toString().toByteArray())
		val hashedPassword = bytesToMd5(OTA_PASSWORD.toByteArray())
		val payload = bytesToMd5("$hashedPassword:$authToken:$signature".toByteArray())

		sendDatagram(socket, "200 $signature $payload\n", target)

		val authResponseData = withTimeout(10_000) { socket.receive() }.packet.readText()
		return authResponseData == "OK"
	}
}

/**
 * Accepts a TCP connection from the device and streams the firmware in chunks.
 * Returns true if the device confirmed a successful flash with "OK".
 */
private suspend fun otaUpload(
	tcpServer: ServerSocket,
	firmware: ByteArray,
	onProgress: suspend (Int) -> Unit,
): Boolean {
	val socket = withTimeout(30_000) { tcpServer.accept() }
	return socket.use {
		uploadFirmware(
			output = socket.openWriteChannel(autoFlush = true),
			input = socket.openReadChannel(),
			firmware = firmware,
			onProgress = onProgress,
		)
	}
}

internal suspend fun uploadFirmware(
	output: ByteWriteChannel,
	input: ByteReadChannel,
	firmware: ByteArray,
	onProgress: suspend (Int) -> Unit,
): Boolean {
	var offset = 0
	while (offset < firmware.size) {
		onProgress(((offset.toDouble() / firmware.size) * 100).toInt())

		val chunkLen = min(OTA_CHUNK_SIZE, firmware.size - offset)
		output.writeFully(firmware, offset, offset + chunkLen)
		offset += chunkLen

		withTimeout(1_000) { input.discardExact(4) }
	}

	output.flush()
	val response = withTimeout(10_000) { input.readRemaining().readByteArray().decodeToString() }
	return response.contains("OK")
}

suspend fun doOtaFlash(
	deviceIp: String,
	deviceId: DeviceId,
	part: FirmwarePart,
	server: VRServer,
	onStatus: suspend (FirmwareUpdateStatus, Int) -> Unit,
) {
	onStatus(FirmwareUpdateStatus.DOWNLOADING, 0)

	val firmware = try {
		withContext(Dispatchers.IO) {
			val url = part.url ?: error("missing url")
			val digest = part.digest ?: error("missing digest")
			downloadFirmware(url, digest)
		}
	} catch (_: Exception) {
		onStatus(FirmwareUpdateStatus.ERROR_DOWNLOAD_FAILED, 0)
		return
	}

	onStatus(FirmwareUpdateStatus.AUTHENTICATING, 0)

	SelectorManager(Dispatchers.IO).use { selectorManager ->
		// Bind TCP server first so we know which port to advertise in the invitation
		aSocket(selectorManager).tcp().bind(port = 0).use { tcpServer ->
			val localPort = tcpServer.port

			if (!otaAuthenticate(selectorManager, deviceIp, localPort, firmware)) {
				onStatus(FirmwareUpdateStatus.ERROR_AUTHENTICATION_FAILED, 0)
				return
			}

			val uploaded = runCatching {
				otaUpload(tcpServer, firmware) { progress ->
					onStatus(FirmwareUpdateStatus.UPLOADING, progress)
				}
			}

			if (uploaded.isFailure) {
				onStatus(FirmwareUpdateStatus.ERROR_UPLOAD_FAILED, 0)
				return
			}
		}
	}

	onStatus(FirmwareUpdateStatus.REBOOTING, 0)

	if (waitForReconnected(server, deviceId) == null) {
		onStatus(FirmwareUpdateStatus.ERROR_TIMEOUT, 0)
		return
	}

	onStatus(FirmwareUpdateStatus.DONE, 0)
}
