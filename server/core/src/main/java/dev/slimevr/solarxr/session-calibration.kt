package dev.slimevr.solarxr

import dev.slimevr.resets.ResetsManager
import solarxr_protocol.rpc.ResetRequest
import solarxr_protocol.rpc.ResetType

class SessionCalibrationBehaviour(
	private val resetsManager: ResetsManager,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		receiver.rpcDispatcher.on<ResetRequest> { req ->
			resetsManager.scheduleReset("SolarXRBridge", req.resetType ?: ResetType.YAW, req.delay ?: 0f, req.bodyParts)
		}
	}
}
