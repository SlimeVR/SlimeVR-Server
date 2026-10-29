package dev.slimevr.solarxr.rpc

import dev.slimevr.resets.ResetsManager
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import solarxr_protocol.rpc.CancelResetRequest
import solarxr_protocol.rpc.ClearMountingResetRequest
import solarxr_protocol.rpc.ResetRequest

class SessionCalibrationBehaviour(
	private val resetsManager: ResetsManager,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		// Reset request
		receiver.onRpc<ResetRequest> { req, txId ->
			resetsManager.scheduleReset(receiver.context.name, req.resetType, req.delay ?: 0f, req.bodyParts, receiver.id, txId)
		}.launchIn(receiver.context.scope)

		receiver.rpcDispatcher.on<CancelResetRequest> {
			resetsManager.cancel()
		}.launchIn(receiver.context.scope)

		// Clear mounting reset request
		receiver.rpcDispatcher.on<ClearMountingResetRequest> {
			resetsManager.clearTrackersMountingReset(receiver.context.name)
		}.launchIn(receiver.context.scope)
	}
}
