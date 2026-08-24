package dev.slimevr.solarxr

import dev.slimevr.resets.ResetsManager
import kotlinx.coroutines.flow.launchIn
import solarxr_protocol.rpc.ClearMountingResetRequest
import solarxr_protocol.rpc.ResetRequest
import solarxr_protocol.rpc.ResetType

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
