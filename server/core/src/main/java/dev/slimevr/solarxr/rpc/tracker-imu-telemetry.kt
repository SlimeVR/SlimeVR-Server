package dev.slimevr.solarxr.rpc

import dev.slimevr.VRServer
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import dev.slimevr.solarxr.toQuat
import dev.slimevr.solarxr.toVec3f
import dev.slimevr.tracker.CalibratedAcceleration
import dev.slimevr.tracker.CalibratedRotation
import dev.slimevr.tracker.RawAcceleration
import dev.slimevr.tracker.RawRotation
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import solarxr_protocol.rpc.StartTrackerImuTelemetryRequest
import solarxr_protocol.rpc.StopTrackerImuTelemetryRequest
import solarxr_protocol.rpc.TrackerImuSample
import solarxr_protocol.rpc.TrackerImuTelemetryResponse

private const val FLUSH_INTERVAL_MS = 50L
private const val MAX_PENDING_SAMPLES = 1024

private data class ImuValues(
	val rawRotation: RawRotation,
	val rotation: CalibratedRotation,
	val rawAcceleration: RawAcceleration?,
	val acceleration: CalibratedAcceleration?,
)

class TrackerImuTelemetryBehaviour(private val server: VRServer) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		var activeJob: Job? = null

		receiver.rpcDispatcher.on<StartTrackerImuTelemetryRequest> { req ->
			activeJob?.cancelAndJoin()
			activeJob = null

			val trackerId = req.trackerId
			val tracker = server.getTracker(trackerId.toInt()) ?: return@on
			val pending = Channel<TrackerImuSample>(MAX_PENDING_SAMPLES, BufferOverflow.DROP_OLDEST)

			activeJob = receiver.context.scope.launch {
				launch {
					tracker.context.state
						.map { ImuValues(it.rawRotation, it.rotation, it.rawAcceleration, it.acceleration) }
						.distinctUntilChanged()
						.collect { values ->
							pending.send(
								TrackerImuSample(
									time = System.currentTimeMillis().toULong(),
									rawRotation = values.rawRotation.toQuat(),
									rotation = values.rotation.toQuat(),
									rawAcceleration = values.rawAcceleration?.toVec3f(),
									linearAcceleration = values.acceleration?.toVec3f(),
								),
							)
						}
				}

				while (isActive) {
					delay(FLUSH_INTERVAL_MS)
					val samples = mutableListOf<TrackerImuSample>()
					while (true) {
						samples.add(pending.tryReceive().getOrNull() ?: break)
					}
					if (samples.isNotEmpty()) {
						receiver.sendRpc(TrackerImuTelemetryResponse(trackerId = trackerId, samples = samples))
					}
				}
			}
		}.launchIn(receiver.context.scope)

		receiver.rpcDispatcher.on<StopTrackerImuTelemetryRequest> {
			activeJob?.cancelAndJoin()
			activeJob = null
		}.launchIn(receiver.context.scope)
	}
}
