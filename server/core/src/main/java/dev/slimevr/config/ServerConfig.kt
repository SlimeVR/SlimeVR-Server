package dev.slimevr.config

import dev.slimevr.VRServer
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
			useMagnetometerOnAllTrackers = state

			VRServer.instance.deviceManager.devices.filter { it.magSupport }.map {
				async {
					if (!state) {
						it.setMag(false)
						return@async
					}

					val every = it.trackers.all { (_, t) -> t.config.shouldHaveMagEnabled == true }
					if (every) {
						it.setMag(true)
						return@async
					}

					it.trackers.filterValues { it.config.shouldHaveMagEnabled == true }
						.map { (_, t) ->
							async {
								// FIXME: Tracker gets restarted after each setMag, what will happen for devices with 3 trackers?
								it.setMag(true, t.trackerNum)
							}
						}.awaitAll()
				}
			}.awaitAll()
		} finally {
			magMutex.unlock()
		}
	}
}
