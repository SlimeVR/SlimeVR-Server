package dev.slimevr.hid

import dev.slimevr.AppContextProvider
import dev.slimevr.VRServerActions
import dev.slimevr.logging.AppLogger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import solarxr_protocol.data_feed.dongle_data.DongleStatus
import kotlin.time.Duration.Companion.seconds

val HID_POLL_INTERVAL = 3.seconds
const val HID_READ_BUFFER_SIZE = 64
const val HID_READ_TIMEOUT_MS = 100
const val HID_STALL_READS = 30

private data class ActiveReceiver(val job: Job, val receiver: HIDReceiver)

fun runHidManager(appContext: AppContextProvider, transport: HidTransport, scope: CoroutineScope): Job {
	val active = mutableMapOf<String, ActiveReceiver>()

	return scope.launch {
		try {
			while (isActive) {
				val directTrackersEnabled = appContext.config.settings.context.state.value.data.hidConfig.trackersOverHid
				val found = try {
					transport.enumerate(directTrackersEnabled)
				} catch (e: Exception) {
					AppLogger.hid.error(e, "HID enumeration failed")
					emptyMap()
				}

				val toRemove = (active.keys - found.keys) +
					active.entries.filter { !it.value.job.isActive }.map { it.key }
				for (key in toRemove) {
					val entry = active.remove(key) ?: continue
					entry.job.cancel()
					entry.job.join()
					AppLogger.hid.info("HID device removed: $key")
				}

				for ((key, descriptor) in found) {
					if (key in active) continue

					val connection = try {
						transport.open(descriptor)
					} catch (e: Exception) {
						AppLogger.hid.error(e, "Failed to open HID device: $key")
						null
					}
					if (connection == null) {
						AppLogger.hid.warn("Failed to open HID device: $key")
						continue
					}

					val serial = descriptor.serialNumber ?: key
					val isDirect = isCompatibleHidTracker(descriptor.vendorId, descriptor.productId)
					AppLogger.hid.info("HID device detected: $serial")

					val deviceJob = SupervisorJob(scope.coroutineContext[Job])
					val deviceScope = CoroutineScope(scope.coroutineContext + deviceJob)

					val receiver = appContext.server.context.state.value.dongles.values
						.find { it.context.state.value.serialNumber == serial }
						?: run {
							val id = appContext.server.nextHandle()
							val created = HIDReceiver.create(
								id = id,
								serialNumber = serial,
								isDirect = isDirect,
								appContext = appContext,
								scope = scope,
							)
							appContext.server.context.dispatch(VRServerActions.NewDongle(id, created))
							created
						}
					receiver.context.dispatch(HIDReceiverActions.SetStatus(DongleStatus.CONNECTED))

					// Device is already open, so the body must start even if the scope is cancelled
					// first: readDevice's finally is what closes it
					deviceScope.launch(start = CoroutineStart.UNDISPATCHED) {
						// A direct tracker legitimately goes quiet while it sleeps, so it gets no watchdog
						readDevice(connection, receiver, if (isDirect) null else HID_STALL_READS)
					}
					deviceJob.complete()

					active[key] = ActiveReceiver(deviceJob, receiver)
				}

				val wake = transport.wakeSignal
				if (wake == null) {
					delay(HID_POLL_INTERVAL)
				} else {
					withTimeoutOrNull(HID_POLL_INTERVAL) { wake.receive() }
				}
			}
		} finally {
			// Stop every in-flight native read before the transport tears its backend down, so we
			// never call hid_exit() while another thread is still inside hid_read()/hid_close().
			withContext(NonCancellable) {
				for ((job) in active.values) {
					job.cancel()
					job.join()
				}
				transport.shutdown()
			}
		}
	}
}

private suspend fun readDevice(connection: HidConnection, receiver: HIDReceiver, stallReads: Int?) {
	try {
		val buffer = ByteArray(HID_READ_BUFFER_SIZE)
		var emptyReads = 0

		while (currentCoroutineContext().isActive) {
			val read = try {
				connection.read(buffer, HID_READ_TIMEOUT_MS)
			} catch (e: CancellationException) {
				throw e
			} catch (_: Exception) {
				// A throwing read means the same thing as a negative one: the device is gone
				-1
			}
			when {
				// read error, device gone
				read < 0 -> return

				read > 0 -> {
					emptyReads = 0
					parseHIDPackets(buffer, read).forEach { receiver.packetEvents.emit(it) }
				}

				// A timeout with no data: the read already blocked, so just go again, unless the
				// device has been silent long enough to count as wedged
				else -> {
					emptyReads++
					if (stallReads != null && emptyReads >= stallReads) {
						AppLogger.hid.info("No HID data from ${receiver.context.state.value.serialNumber}, reopening")
						return
					}
				}
			}
		}
	} finally {
		withContext(NonCancellable) {
			connection.close()
			receiver.onDisconnected()
		}
	}
}
