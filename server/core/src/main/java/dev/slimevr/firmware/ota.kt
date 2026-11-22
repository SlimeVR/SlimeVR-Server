package dev.slimevr.firmware

import dev.slimevr.VRServer
import dev.slimevr.logging.AppLogger
import dev.slimevr.util.stripIpAddressPort
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.BoundDatagramSocket
import io.ktor.network.sockets.Datagram
import io.ktor.network.sockets.InetSocketAddress
import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.core.buildPacket
import io.ktor.utils.io.core.readText
import io.ktor.utils.io.core.writeFully
import io.ktor.utils.io.discardExact
import io.ktor.utils.io.readRemaining
import io.ktor.utils.io.writeFully
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.io.readByteArray
import solarxr_protocol.rpc.FirmwareDeviceIdTable
import solarxr_protocol.rpc.FirmwarePart
import solarxr_protocol.rpc.FirmwareUpdateStatus
import java.security.MessageDigest
import java.util.UUID
import kotlin.math.min

private const val OTA_PORT = 8266
const val OTA_CALLBACK_PORT = 8266
private const val OTA_PASSWORD = "SlimeVR-OTA"
private const val OTA_CHUNK_SIZE = 2048

private fun bytesToMd5(bytes: ByteArray): String = MessageDigest.getInstance("MD5").digest(bytes).joinToString("") { "%02x".format(it) }

private suspend fun sendDatagram(socket: BoundDatagramSocket, message: String, target: InetSocketAddress) = socket.send(Datagram(buildPacket { writeFully(message.toByteArray()) }, target))

data class OtaConnection(val ip: String, val socket: Socket)

/** An OTA job whose device has been invited and has not connected back yet. */
fun isAwaitingOtaCallback(job: FirmwareJobStatus) = job.firmwareDeviceId is FirmwareDeviceIdTable && job.status == FirmwareUpdateStatus.AUTHENTICATING

/**
 * Sends the OTA invitation over UDP and performs the optional AUTH challenge-response.
 * Returns true if authentication succeeded (or was not required).
 */
private suspend fun otaAuthenticate(
	selectorManager: SelectorManager,
	deviceIp: String,
	devicePort: Int,
	localPort: Int,
	firmware: ByteArray,
): Boolean {
	val fileMd5 = bytesToMd5(firmware)
	val target = InetSocketAddress(stripIpAddressPort(deviceIp), devicePort)

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
 * Invites the device to fetch [firmware] and streams it over the connection it opens back
 * to the shared listener. The job must already be in AUTHENTICATING so the connection is routed to it.
 */
internal suspend fun otaTransfer(
	deviceIp: String,
	firmware: ByteArray,
	otaConnections: SharedFlow<OtaConnection>,
	onStatus: suspend (FirmwareUpdateStatus, Int) -> Unit,
	callbackPort: Int = OTA_CALLBACK_PORT,
	devicePort: Int = OTA_PORT,
): Boolean = coroutineScope {
	val ip = stripIpAddressPort(deviceIp)

	val connection = async(start = CoroutineStart.UNDISPATCHED) {
		otaConnections.first { it.ip == ip }.socket
	}

	val authenticated = SelectorManager(Dispatchers.IO).use { selectorManager ->
		try {
			otaAuthenticate(selectorManager, deviceIp, devicePort, callbackPort, firmware)
		} catch (_: TimeoutCancellationException) {
			false
		}
	}
	if (!authenticated) {
		connection.cancel()
		onStatus(FirmwareUpdateStatus.ERROR_AUTHENTICATION_FAILED, 0)
		return@coroutineScope false
	}

	val uploaded = runCatching {
		withTimeout(30_000) { connection.await() }.use { socket ->
			uploadFirmware(
				output = socket.openWriteChannel(autoFlush = true),
				input = socket.openReadChannel(),
				firmware = firmware,
				onProgress = { progress -> onStatus(FirmwareUpdateStatus.UPLOADING, progress) },
			)
		}
	}
	if (uploaded.isFailure) {
		connection.cancel()
		uploaded.exceptionOrNull()?.let { e ->
			AppLogger.firmware.error(e, "OTA firmware upload failed")
		}
		onStatus(FirmwareUpdateStatus.ERROR_UPLOAD_FAILED, 0)
		return@coroutineScope false
	}
	true
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

		withTimeout(5_000) { input.discardExact(4) }
	}

	output.flush()
	val response = withTimeout(10_000) { input.readRemaining().readByteArray().decodeToString() }
	return response.contains("OK")
}

suspend fun doOtaFlash(
	deviceIp: String,
	deviceId: UShort,
	part: FirmwarePart,
	server: VRServer,
	otaConnections: SharedFlow<OtaConnection>,
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

	if (!otaTransfer(deviceIp, firmware, otaConnections, onStatus)) return

	onStatus(FirmwareUpdateStatus.REBOOTING, 0)

	if (waitForReconnected(server, deviceId) == null) {
		onStatus(FirmwareUpdateStatus.ERROR_TIMEOUT, 0)
		return
	}

	onStatus(FirmwareUpdateStatus.DONE, 0)
}
