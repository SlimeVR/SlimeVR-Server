package dev.slimevr.solarxr.rpc

import dev.slimevr.resets.ResetsManager
import dev.slimevr.skeleton.BoneId
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
			val boneIds = req.boneIds?.map { BoneId(it) }
			resetsManager.scheduleReset("SolarXRBridge", req.resetType, req.delay ?: 0f, boneIds)
		}.launchIn(receiver.context.scope)

		// Clear mounting reset request
		receiver.rpcDispatcher.on<ClearMountingResetRequest> {
			resetsManager.clearTrackersMountingReset("SolarXRBridge")
		}.launchIn(receiver.context.scope)
	}
}
