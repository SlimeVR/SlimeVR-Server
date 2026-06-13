package dev.slimevr.solarxr

import dev.slimevr.VRServer
import dev.slimevr.tracker.TrackerActions
import io.github.axisangles.ktmath.Quaternion
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.ResetRequest
import solarxr_protocol.rpc.ResetType

class SessionCalibrationBehaviour(
	private val server: VRServer,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		receiver.rpcDispatcher.on<ResetRequest> { req ->
			val bodyParts = req.bodyParts
			val allTrackers = server.context.state.value.trackers

			val trackers = if (!bodyParts.isNullOrEmpty()) {
				allTrackers.filterValues {
					bodyParts.contains(it.context.state.value.bodyPart)
				}
			} else {
				allTrackers
			}
			val reference = allTrackers.firstNotNullOfOrNull {
				val trackerState = it.value.context.state.value
				if (trackerState.bodyPart == BodyPart.HEAD) {
					trackerState.rotation
				} else {
					null
				}
			} ?: Quaternion.IDENTITY

			val action = when (req.resetType) {
				ResetType.Yaw -> TrackerActions.YawReset(reference)
				ResetType.Full -> TrackerActions.FullReset(reference)
				ResetType.Mounting -> TrackerActions.MountingReset(reference)
				else -> return@on
			}
			for ((_, tracker) in trackers) {
				tracker.context.dispatch(action)
			}
		}
	}
}
