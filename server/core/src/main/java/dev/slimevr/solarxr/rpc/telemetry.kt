package dev.slimevr.solarxr.rpc

import dev.slimevr.VRServer
import dev.slimevr.device.Device
import dev.slimevr.device.DevicePacketSample
import dev.slimevr.device.isOutage
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.rpc.StartTelemetryRequest
import solarxr_protocol.rpc.StopTelemetryRequest
import solarxr_protocol.rpc.TelemetryGapEvent
import solarxr_protocol.rpc.TelemetryGapResponse
import solarxr_protocol.rpc.TelemetrySample
import solarxr_protocol.rpc.TelemetryUpdateResponse

private fun isDeviceOk(device: Device, server: VRServer): Boolean {
	val deviceState = device.context.state.value
	if (deviceState.status == TrackerStatus.OK) return true
	val deviceTrackers = server.context.state.value.trackers.values
		.filter { it.context.state.value.deviceId == deviceState.id }
	return deviceTrackers.any { it.context.state.value.status == TrackerStatus.OK }
}

class TelemetryBehaviour(private val server: VRServer) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		var activeJobs: List<Job> = emptyList()

		receiver.rpcDispatcher.on<StartTelemetryRequest> { req ->
			activeJobs.forEach { it.cancelAndJoin() }
			val deviceIds = req.deviceIds ?: emptyList()
			if (deviceIds.isEmpty()) {
				activeJobs = emptyList()
				return@on
			}

			val jobs = mutableListOf<Job>()

			jobs.add(
				receiver.context.scope.launch {
					while (isActive) {
						val now = System.currentTimeMillis()
						val samples = mutableListOf<TelemetrySample>()

						for (id in deviceIds) {
							val device = server.getDevice(id.toInt()) ?: continue
							val isOk = isDeviceOk(device, server)

							val stats = device.getStatsForWindow(windowMs = 1000L, now = now)

							samples.add(
								TelemetrySample(
									deviceId = id,
									time = now.toULong(),
									rssi = if (isOk) stats.rssiAvg?.toShort() else null,
									rssiMin = if (isOk) stats.rssiMin?.toShort() else null,
									rssiMax = if (isOk) stats.rssiMax?.toShort() else null,
									packetLossPct = if (isOk) stats.packetLoss else null,
									packetsLost = if (isOk) stats.packetsLost else null,
									packetsReceived = if (isOk) stats.packetsReceived else null,
								),
							)
						}

						if (samples.isNotEmpty()) {
							receiver.sendRpc(TelemetryUpdateResponse(samples = samples))
						}

						delay(100L)
					}
				},
			)

			deviceIds.forEach { id ->
				val device = server.getDevice(id.toInt()) ?: return@forEach
				jobs.add(
					receiver.context.scope.launch {
						var prevSample: DevicePacketSample? = null
						device.packetHistory.latest
							.collect { cur ->
								val p = prevSample
								prevSample = cur
								if (p == null || !isDeviceOk(device, server)) return@collect

								if (!isOutage(cur)) return@collect

								val durationMs = (cur.time - p.time).coerceAtLeast(0L)

								receiver.sendRpc(
									TelemetryGapResponse(
										events = listOf(
											TelemetryGapEvent(
												deviceId = id,
												time = cur.time.toULong(),
												durationMs = durationMs.toUInt(),
												packetsLost = cur.lost.toUInt(),
											),
										),
									),
								)
							}
					},
				)
			}

			activeJobs = jobs
		}.launchIn(receiver.context.scope)

		receiver.rpcDispatcher.on<StopTelemetryRequest> {
			activeJobs.forEach { it.cancelAndJoin() }
			activeJobs = emptyList()
		}.launchIn(receiver.context.scope)
	}
}
