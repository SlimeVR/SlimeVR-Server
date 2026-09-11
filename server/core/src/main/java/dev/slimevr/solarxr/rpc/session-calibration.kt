package dev.slimevr.solarxr.rpc

import dev.slimevr.resets.ResetsManager
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import solarxr_protocol.rpc.ClearMountingResetRequest
import solarxr_protocol.rpc.ResetRequest

class SessionCalibrationBehaviour(
	private val resetsManager: ResetsManager,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		// Reset request
		receiver.rpcDispatcher.on<ResetRequest> { req ->
			resetsManager.scheduleReset("SolarXRBridge", req.resetType, req.delay ?: 0f, req.bodyParts)
		}.launchIn(receiver.context.scope)

		// Clear mounting reset request
		receiver.rpcDispatcher.on<ClearMountingResetRequest> {
			resetsManager.clearTrackersMountingReset("SolarXRBridge")
		}.launchIn(receiver.context.scope)
	}
}
