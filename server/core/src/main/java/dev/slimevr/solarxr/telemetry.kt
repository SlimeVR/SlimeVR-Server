package dev.slimevr.solarxr

import dev.slimevr.VRServer
import dev.slimevr.device.Device
import dev.slimevr.device.DevicePacketSample
import dev.slimevr.device.computeWindowedStats
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.rpc.StartTelemetryRequest
import solarxr_protocol.rpc.StopTelemetryRequest
import solarxr_protocol.rpc.TelemetryGapEvent
import solarxr_protocol.rpc.TelemetryGapResponse
import solarxr_protocol.rpc.TelemetrySample
import solarxr_protocol.rpc.TelemetryUpdateResponse
import kotlin.math.max

private fun Device.isDeviceOk(server: VRServer): Boolean {
	val deviceState = context.state.value
	if (deviceState.status == TrackerStatus.OK) return true
	val deviceTrackers = server.context.state.value.trackers.values
		.filter { it.context.state.value.deviceId == deviceState.id }
	return deviceTrackers.any { it.context.state.value.status == TrackerStatus.OK }
}

private fun Device.getMeasuredTps(server: VRServer): Double {
	val deviceState = context.state.value
	val deviceTrackers = server.context.state.value.trackers.values
		.filter { it.context.state.value.deviceId == deviceState.id }
	return deviceTrackers
		.mapNotNull { it.context.state.value.tps.toDouble().takeIf { t -> t > 0.0 } }
		.maxOrNull() ?: 100.0
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
							val isOk = device.isDeviceOk(server)

							val deviceSamples = device.context.state.value.samples
							val stats = computeWindowedStats(deviceSamples, windowMs = 1000L, now = now)
							val lastSample = deviceSamples.lastOrNull()

							val isTotalLoss = !isOk || (stats.packetLoss != null && stats.packetLoss >= 1.0f)

							samples.add(
								TelemetrySample(
									deviceId = id,
									time = now.toULong(),
									rssi = if (isTotalLoss) null else stats.rssiAvg?.toShort(),
									rssiMin = if (isTotalLoss) null else stats.rssiMin?.toShort(),
									rssiMax = if (isTotalLoss) null else stats.rssiMax?.toShort(),
									packetLossPct = if (!isOk) null else stats.packetLoss,
									packetsLost = lastSample?.packetsLost?.toInt(),
									packetsReceived = lastSample?.packetsReceived?.toInt(),
								)
							)
						}

						if (samples.isNotEmpty()) {
							receiver.sendRpc(TelemetryUpdateResponse(samples = samples))
						}

						delay(100L)
					}
				}
			)

			deviceIds.forEach { id ->
				val device = server.getDevice(id.toInt()) ?: return@forEach
				jobs.add(
					receiver.context.scope.launch {
						var prevSample: DevicePacketSample? = null
						device.context.state
							.map { it.samples.lastOrNull() }
							.distinctUntilChanged()
							.filterNotNull()
							.collect { cur ->
								val p = prevSample
								if (p != null && device.isDeviceOk(server)) {
									val durationMs = (cur.time - p.time).coerceAtLeast(0L)
									val hasPacketLoss = cur.packetsLost != null && p.packetsLost != null && cur.packetsLost > p.packetsLost

									val deviceTps = device.getMeasuredTps(server)
									val expectedIntervalMs = 1000.0 / deviceTps.coerceIn(5.0, 200.0)
									val thresholdMs = max(3.0 * expectedIntervalMs, 30.0)

									if (hasPacketLoss && durationMs.toDouble() >= thresholdMs) {
										val lostDiff = (cur.packetsLost!! - p.packetsLost!!).toUInt()
										receiver.sendRpc(
											TelemetryGapResponse(
												events = listOf(
													TelemetryGapEvent(
														deviceId = id,
														time = cur.time.toULong(),
														durationMs = durationMs.toUInt(),
														packetsLost = lostDiff,
													)
												)
											)
										)
									}
								}
								prevSample = cur
							}
					}
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
