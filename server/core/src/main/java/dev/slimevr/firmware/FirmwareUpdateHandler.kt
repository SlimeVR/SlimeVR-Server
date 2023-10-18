package dev.slimevr.firmware

import com.mayakapps.kache.InMemoryKache
import com.mayakapps.kache.KacheStrategy
import dev.llelievr.espflashkotlin.Flasher
import dev.llelievr.espflashkotlin.FlashingProgressListener
import dev.slimevr.VRServer
import dev.slimevr.serial.ProvisioningListener
import dev.slimevr.serial.ProvisioningStatus
import dev.slimevr.serial.SerialPort
import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.TrackerStatus
import dev.slimevr.tracking.trackers.TrackerStatusListener
import dev.slimevr.tracking.trackers.udp.UDPDevice
import io.eiren.util.logging.LogManager
import kotlinx.coroutines.*
import solarxr_protocol.rpc.FirmwarePartT
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.Timer
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.concurrent.scheduleAtFixedRate

data class DownloadedFirmwarePart(
	val firmware: ByteArray,
	val offset: Long?,
) {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as DownloadedFirmwarePart

		if (!firmware.contentEquals(other.firmware)) return false
		if (offset != other.offset) return false

		return true
	}

	override fun hashCode(): Int {
		var result = firmware.contentHashCode()
		result = 31 * result + (offset?.hashCode() ?: 0)
		return result
	}
}

class FirmwareUpdateHandler(private val server: VRServer) :
	TrackerStatusListener,
	ProvisioningListener {

	private val provisioningTickTimer = Timer("StatusUpdateTimer")
	private val runningJobs: MutableList<Job> = CopyOnWriteArrayList()
	private val watchRestartQueue: MutableList<Pair<UpdateDeviceId<*>, () -> Unit>> =
		CopyOnWriteArrayList()
	private val updatingDevicesStatus: MutableMap<UpdateDeviceId<*>, UpdateStatusEvent<*>> =
		ConcurrentHashMap()
	private val listeners: MutableList<FirmwareUpdateListener> = CopyOnWriteArrayList()
	private val firmwareCache =
		InMemoryKache<String, Array<DownloadedFirmwarePart>>(maxSize = 5 * 1024 * 1024) {
			strategy = KacheStrategy.LRU
			sizeCalculator = { _, parts -> parts.sumOf { it.firmware.size }.toLong() }
		}
	private val mainScope: CoroutineScope = CoroutineScope(SupervisorJob())
	private var clearJob: Deferred<Unit>? = null

	fun addListener(channel: FirmwareUpdateListener) {
		listeners.add(channel)
	}

	fun removeListener(channel: FirmwareUpdateListener) {
		listeners.removeIf { channel == it }
	}

	fun destroy() {
		mainScope.cancel()
	}

	init {
		server.addTrackerStatusListener(this)
		server.provisioningHandler.addListener(this)

		this.provisioningTickTimer.scheduleAtFixedRate(0, 1000) {
			checkUpdateTimeout()
		}
	}

	private fun startOtaUpdate(
		part: DownloadedFirmwarePart,
		deviceId: UpdateDeviceId<Int>,
	) {
		val udpDevice: UDPDevice? =
			(this.server.deviceManager.devices.find { device -> device is UDPDevice && device.id == deviceId.id }) as UDPDevice?

		if (udpDevice == null) {
			onStatusChange(
				UpdateStatusEvent(
					deviceId,
					FirmwareUpdateStatus.ERROR_DEVICE_NOT_FOUND
				)
			)
			return
		}
		OTAUpdateTask(
			part.firmware,
			deviceId,
			udpDevice.ipAddress,
			this::onStatusChange
		).run()
	}

	private fun startSerialUpdate(
		firmwares: Array<DownloadedFirmwarePart>,
		deviceId: UpdateDeviceId<String>,
		ssid: String,
		password: String,
	) {
		val serialPort = this.server.serialHandler.knownPorts.toList()
			.find { port -> deviceId.id == port.portLocation }

		if (serialPort == null) {
			onStatusChange(
				UpdateStatusEvent(
					deviceId,
					FirmwareUpdateStatus.ERROR_DEVICE_NOT_FOUND
				)
			)
			return
		}

		val flashingHandler = this.server.serialFlashingHandler

		if (flashingHandler == null) {
			onStatusChange(
				UpdateStatusEvent(
					deviceId,
					FirmwareUpdateStatus.ERROR_UNSUPPORTED_METHOD
				)
			)
			return
		}

		try {
			val flasher = Flasher(flashingHandler)

			for (part in firmwares) {
				if (part.offset == null) {
					error("Offset is empty")
				}
				flasher.addBin(part.firmware, part.offset.toInt())
			}

			flasher.addProgressListener(object : FlashingProgressListener {
				override fun progress(progress: Float) {
					onStatusChange(
						UpdateStatusEvent(
							deviceId,
							FirmwareUpdateStatus.UPLOADING,
							(progress * 100).toInt()
						)
					)
				}
			})

			onStatusChange(
				UpdateStatusEvent(
					deviceId,
					FirmwareUpdateStatus.SYNCING_WITH_MCU
				)
			)
			flasher.flash(serialPort)
			onStatusChange(UpdateStatusEvent(deviceId, FirmwareUpdateStatus.REBOOTING))
			server.provisioningHandler.start(ssid, password, serialPort.portLocation)
		} catch (e: Exception) {
			e.printStackTrace()
			onStatusChange(
				UpdateStatusEvent(
					deviceId,
					FirmwareUpdateStatus.ERROR_UPLOAD_FAILED
				)
			)
			server.provisioningHandler.stop()
		}
	}

	fun queueFirmwareUpdate(
		toDownloadParts: Array<FirmwarePartT>,
		method: FirmwareUpdateMethod,
		deviceId: UpdateDeviceId<*>,
		ssid: String?,
		password: String?,
	) = mainScope.launch {
		clearJob?.await()
		if (method == FirmwareUpdateMethod.OTA) {
			if (watchRestartQueue.find { it.first == deviceId } != null) {
				LogManager.info("[FirmwareUpdateHandler] Device is already updating, Skipping")
			}

			onStatusChange(
				UpdateStatusEvent(
					deviceId,
					FirmwareUpdateStatus.WAITING_FOR_REBOOT
				)
			)
			watchRestartQueue.add(
				Pair(deviceId) {
					mainScope.launch {
						startFirmwareUpdateJob(
							toDownloadParts,
							method,
							deviceId,
							ssid,
							password
						)
					}
				}
			)
		} else {
			if (updatingDevicesStatus[deviceId] != null) {
				LogManager.info("[FirmwareUpdateHandler] Device is already updating, Skipping")
				return@launch
			}

			startFirmwareUpdateJob(toDownloadParts, method, deviceId, ssid, password)
		}
	}

	fun cancelUpdates() {
		val oldClearJob = clearJob
		clearJob = mainScope.async {
			oldClearJob?.await()
			watchRestartQueue.clear()
			runningJobs.forEach { it.cancelAndJoin() }
			runningJobs.clear()
		}
	}

	private suspend fun startFirmwareUpdateJob(
		toDownloadParts: Array<FirmwarePartT>,
		method: FirmwareUpdateMethod,
		deviceId: UpdateDeviceId<*>,
		ssid: String?,
		password: String?,
	) = coroutineScope {
		onStatusChange(
			UpdateStatusEvent(
				deviceId,
				FirmwareUpdateStatus.DOWNLOADING
			)
		)

		try {
			// We add the firmware to an LRU cache

			val firmwareParts =
				firmwareCache.getOrPut(toDownloadParts.joinToString("|") { "${it.url}#${it.offset}" }) {
					withTimeoutOrNull(30_000) {
						toDownloadParts.map {
							val firmware = downloadFirmware(it.url)
								?: error("unable to download firmware part")
							DownloadedFirmwarePart(
								firmware,
								it.offset
							)
						}.toTypedArray()
					}
				}

			val job = launch {
				withTimeout(2 * 60 * 1000) {
					if (firmwareParts.isNullOrEmpty()) {
						onStatusChange(
							UpdateStatusEvent(
								deviceId,
								FirmwareUpdateStatus.ERROR_DOWNLOAD_FAILED
							)
						)
						return@withTimeout
					}

					when (method) {
						FirmwareUpdateMethod.NONE -> error("unsupported method")
						FirmwareUpdateMethod.OTA -> {
							if (deviceId.id !is Int) {
								error("invalid state, the device id is not an int")
							}
							if (firmwareParts.size > 1) {
								error("Invalid state, there should never be more than one firmware file doing OTA updates")
							}
							startOtaUpdate(
								firmwareParts.first(),
								UpdateDeviceId(
									FirmwareUpdateMethod.OTA,
									deviceId.id
								)
							)
						}

						FirmwareUpdateMethod.SERIAL -> {
							if (deviceId.id !is String) {
								error("invalid state, the device id is not an string")
							}
							if (ssid == null || password == null) {
								error("invalid state, wifi credentials not set")
							}
							startSerialUpdate(
								firmwareParts,
								UpdateDeviceId(
									FirmwareUpdateMethod.SERIAL,
									deviceId.id
								),
								ssid,
								password
							)
						}
					}
				}
			}
			runningJobs.add(job)
		} catch (e: Exception) {
			onStatusChange(
				UpdateStatusEvent(
					deviceId,
					if (e is TimeoutCancellationException) FirmwareUpdateStatus.ERROR_TIMEOUT else FirmwareUpdateStatus.ERROR_UNKNOWN
				)
			)
			if (e !is TimeoutCancellationException) {
				e.printStackTrace()
			}
			return@coroutineScope
		}
	}

	private fun <T> onStatusChange(event: UpdateStatusEvent<T>) {
		this.updatingDevicesStatus[event.deviceId] = event

		if (event.status == FirmwareUpdateStatus.DONE || event.status.isError()) {
			this.updatingDevicesStatus.remove(event.deviceId)
			if (event.deviceId.type === FirmwareUpdateMethod.SERIAL) {
				this.server.provisioningHandler.stop()
			}
		}
		listeners.forEach { l -> l.onUpdateStatusChange(event) }
	}

	fun checkUpdateTimeout() {
		updatingDevicesStatus.forEach { (id, device) ->
			// if more than 30s between two events, consider the update as stuck
			if (!device.status.isError() && device.status != FirmwareUpdateStatus.DONE && System.currentTimeMillis() - device.time > 30 * 1000) {
				onStatusChange(
					UpdateStatusEvent(
						id,
						FirmwareUpdateStatus.ERROR_TIMEOUT
					)
				)
			}
		}
	}

	// this only works for OTA trackers as the device id will
	override fun onTrackerStatusChanged(
		tracker: Tracker,
		oldStatus: TrackerStatus,
		newStatus: TrackerStatus,
	) {
		val device = tracker.device
		if (device !is UDPDevice) return

		if (oldStatus == TrackerStatus.DISCONNECTED && newStatus == TrackerStatus.OK) {
			val queuedDevice = watchRestartQueue.find { it.first.id == device.id }

			if (queuedDevice != null) {
				queuedDevice.second() // we start the queued update task
				watchRestartQueue.remove(queuedDevice) // then we remove it from the queue
				return
			}

			// We can only filter OTA method here as the device id is only provided when using Wi-Fi
			val deviceStatusKey =
				updatingDevicesStatus.keys.find { it.type == FirmwareUpdateMethod.OTA && it.id == device.id }
					?: return
			val updateStatus = updatingDevicesStatus[deviceStatusKey] ?: return
			// We check for the reconnection of the tracker, once the tracker reconnected we notify the user that the update is completed
			if (updateStatus.status == FirmwareUpdateStatus.REBOOTING) {
				onStatusChange(
					UpdateStatusEvent(
						updateStatus.deviceId,
						FirmwareUpdateStatus.DONE
					)
				)
			}
		}
	}

	override fun onProvisioningStatusChange(
		status: ProvisioningStatus,
		port: SerialPort?,
	) {
		fun update(s: FirmwareUpdateStatus) {
			val deviceStatusKey =
				updatingDevicesStatus.keys.find { it.type == FirmwareUpdateMethod.SERIAL && it.id == port?.portLocation }
					?: return
			val updateStatus = updatingDevicesStatus[deviceStatusKey] ?: return
			onStatusChange(UpdateStatusEvent(updateStatus.deviceId, s))
		}

		if (status == ProvisioningStatus.PROVISIONING) {
			update(FirmwareUpdateStatus.PROVISIONING)
		}

		if (status == ProvisioningStatus.DONE) {
			this.server.provisioningHandler.stop()
			update(FirmwareUpdateStatus.DONE)
		}

		if (status == ProvisioningStatus.CONNECTION_ERROR || status == ProvisioningStatus.COULD_NOT_FIND_SERVER) {
			this.server.provisioningHandler.stop()
			update(FirmwareUpdateStatus.ERROR_PROVISIONING_FAILED)
		}
	}
}

fun downloadFirmware(url: String): ByteArray? {
	val outputStream = ByteArrayOutputStream()

	try {
		val chunk = ByteArray(4096)
		var bytesRead: Int
		val stream: InputStream = URL(url).openStream()
		while (stream.read(chunk).also { bytesRead = it } > 0) {
			outputStream.write(chunk, 0, bytesRead)
		}
	} catch (e: IOException) {
		error("Cant download firmware $url")
	}

	return outputStream.toByteArray()
}
