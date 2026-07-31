package dev.slimevr.solarxr

import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import dev.slimevr.stayaligned.StayAlignedManager
import solarxr_protocol.rpc.DetectStayAlignedRelaxedPoseRequest
import solarxr_protocol.rpc.EnableStayAlignedRequest
import solarxr_protocol.rpc.ResetStayAlignedRelaxedPoseRequest

class StayAlignedBehaviour(
    private val settings: Settings,
    private val stayAlignedManager: StayAlignedManager,
) : SolarXRBridgeBehaviour {
    override fun observe(receiver: SolarXRBridge) {
        // Enable request
        receiver.rpcDispatcher.on<EnableStayAlignedRequest> { req ->
            settings.context.dispatch(
                SettingsActions.Update {
                    copy(stayAlignedConfig = stayAlignedConfig.copy(enabled = req.enable == true))
                }
            )
        }.launchIn(receiver.context.scope)

        // Detect pose
        receiver.rpcDispatcher.on<DetectStayAlignedRelaxedPoseRequest> { req ->
            req.pose?.let { stayAlignedManager.detectRelaxedPose(it) }
        }.launchIn(receiver.context.scope)

        // Reset pose
        receiver.rpcDispatcher.on<ResetStayAlignedRelaxedPoseRequest> { req ->
            req.pose?.let { stayAlignedManager.resetRelaxedPose(it) }
        }.launchIn(receiver.context.scope)
    }
}
