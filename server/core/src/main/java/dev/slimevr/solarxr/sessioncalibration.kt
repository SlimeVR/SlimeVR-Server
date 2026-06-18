package dev.slimevr.solarxr

import dev.slimevr.VRServer
import dev.slimevr.resets.ResetsManager
import dev.slimevr.tracker.TrackerActions
import io.github.axisangles.ktmath.Quaternion
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.ResetRequest
import solarxr_protocol.rpc.ResetType

class SessionCalibrationBehaviour(
	private val resetsManager: ResetsManager,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		receiver.rpcDispatcher.on<ResetRequest> { req ->
			resetsManager.scheduleReset("SolarXRBridge", req.resetType ?: ResetType.Yaw, req.delay, req.bodyParts)
		}
	}
}
