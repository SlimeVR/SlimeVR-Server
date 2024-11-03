package dev.slimevr.config

import dev.slimevr.VRServer
import dev.slimevr.tracking.trackers.udp.MagnetometerStatus
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex

class ServerConfig {
	val trackerPort: Int = 6969

	var useMagnetometerOnAllTrackers: Boolean = false
		private set

	private val magMutex = Mutex()
	suspend fun defineMagOnAllTrackers(state: Boolean) = coroutineScope {
		magMutex.lock()
		try {
			if (useMagnetometerOnAllTrackers == state) return@coroutineScope

			VRServer.instance.deviceManager.devices.filter { it.magSupport }.map {
				async {
					// Not using 255 as it sometimes could make one of the sensors go into
					// error mode (if there is more than one sensor inside the device)
					if (!state) {
						val trackers = it.trackers.filterValues {
							it.magStatus != MagnetometerStatus.NOT_SUPPORTED
						}
// 						if(trackers.size == it.trackers.size) {
// 							it.setMag(false)
// 						} else {
						trackers.map { (_, t) ->
							async { it.setMag(false, t.trackerNum) }
						}.awaitAll()
// 						}
						return@async
					}

// 					val every = it.trackers.all { (_, t) -> t.config.shouldHaveMagEnabled == true
// 						&& t.magStatus != MagnetometerStatus.NOT_SUPPORTED }
// 					if (every) {
// 						it.setMag(true)
// 						return@async
// 					}

					it.trackers.filterValues {
						it.config.shouldHaveMagEnabled == true &&
							it.magStatus != MagnetometerStatus.NOT_SUPPORTED
					}
						.map { (_, t) ->
							async {
								// FIXME: Tracker gets restarted after each setMag, what will happen for devices with 3 trackers?
								it.setMag(true, t.trackerNum)
							}
						}.awaitAll()
				}
			}.awaitAll()

			useMagnetometerOnAllTrackers = state
		} finally {
			magMutex.unlock()
		}
	}
}
