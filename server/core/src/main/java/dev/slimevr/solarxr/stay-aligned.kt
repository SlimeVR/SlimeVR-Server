package dev.slimevr.solarxr

import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import dev.slimevr.config.StayAlignedConfig
import dev.slimevr.stayaligned.StayAlignedActions
import dev.slimevr.stayaligned.StayAlignedManager
import kotlinx.coroutines.delay
import solarxr_protocol.rpc.ChangeStayAlignedEnabledRequest
import solarxr_protocol.rpc.ChangeStayAlignedHideCorrectionRequest
import solarxr_protocol.rpc.ChangeStayAlignedSettingsRequest
import solarxr_protocol.rpc.DetectStayAlignedRelaxedPoseRequest
import solarxr_protocol.rpc.ResetStayAlignedRelaxedPoseRequest
import solarxr_protocol.rpc.StayAlignedHideCorrectionRequest
import solarxr_protocol.rpc.StayAlignedHideCorrectionResponse
import solarxr_protocol.rpc.StayAlignedRelaxedPose
import solarxr_protocol.rpc.StayAlignedSettingsRequest
import solarxr_protocol.rpc.StayAlignedSettingsResponse

class StayAlignedBehaviour(
	private val settings: Settings,
	private val stayAlignedManager: StayAlignedManager,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		// Send config
		receiver.rpcDispatcher.on<StayAlignedSettingsRequest> {
			sendConfig(receiver, settings)
		}.launchIn(receiver.context.scope)

		// Receive config
		receiver.rpcDispatcher.on<ChangeStayAlignedSettingsRequest> { req ->
			settings.context.dispatch(
				SettingsActions.Update {
					copy(
						stayAlignedConfig = StayAlignedConfig(
							setupComplete = if (req.enabled == true) true else stayAlignedConfig.setupComplete,
							enabled = req.enabled == true,
							standingRelaxedPose = stayAlignedConfig.standingRelaxedPose.copy(
								enabled = req.standingEnabled == true,
							),
							sittingRelaxedPose = stayAlignedConfig.sittingRelaxedPose.copy(
								enabled = req.sittingEnabled == true,
							),
							flatRelaxedPose = stayAlignedConfig.flatRelaxedPose.copy(
								enabled = req.flatEnabled == true,
							),
						),
					)
				},
			)
			sendConfig(receiver, settings)
		}.launchIn(receiver.context.scope)

		// Receive enabled
		receiver.rpcDispatcher.on<ChangeStayAlignedEnabledRequest> { req ->
			settings.context.dispatch(
				SettingsActions.Update {
					copy(
						stayAlignedConfig = stayAlignedConfig.copy(
							setupComplete = if (req.enabled == true) true else stayAlignedConfig.setupComplete,
							enabled = req.enabled == true,
						),
					)
				},
			)
		}.launchIn(receiver.context.scope)

		// Send hideYawCorrection
		receiver.rpcDispatcher.on<StayAlignedHideCorrectionRequest> {
			receiver.sendRpc(
				StayAlignedHideCorrectionResponse(
					hideCorrection = stayAlignedManager.context.state.value.hideCorrection,
				),
			)
		}.launchIn(receiver.context.scope)

		// Receive hideYawCorrection
		receiver.rpcDispatcher.on<ChangeStayAlignedHideCorrectionRequest> { req ->
			stayAlignedManager.context.dispatch(StayAlignedActions.SetHideYawCorrection(req.hideCorrection == true))
		}.launchIn(receiver.context.scope)

		// Detect pose
		receiver.rpcDispatcher.on<DetectStayAlignedRelaxedPoseRequest> { req ->
			stayAlignedManager.detectRelaxedPose(req.pose ?: StayAlignedRelaxedPose.STANDING)
			sendConfig(receiver, settings)
		}.launchIn(receiver.context.scope)

		// Reset pose
		receiver.rpcDispatcher.on<ResetStayAlignedRelaxedPoseRequest> { req ->
			stayAlignedManager.resetRelaxedPose(req.pose ?: StayAlignedRelaxedPose.STANDING)
			sendConfig(receiver, settings)
		}.launchIn(receiver.context.scope)
	}

	private suspend fun sendConfig(receiver: SolarXRBridge, settings: Settings) {
		val config = settings.context.state.value.data.stayAlignedConfig
		receiver.sendRpc(
			StayAlignedSettingsResponse(
				enabled = config.enabled,
				standingEnabled = config.standingRelaxedPose.enabled,
				standingUpperLegAngle = config.standingRelaxedPose.upperLegAngleInDeg,
				standingLowerLegAngle = config.standingRelaxedPose.lowerLegAngleInDeg,
				standingFootAngle = config.standingRelaxedPose.footAngleInDeg,
				sittingEnabled = config.sittingRelaxedPose.enabled,
				sittingUpperLegAngle = config.sittingRelaxedPose.upperLegAngleInDeg,
				sittingLowerLegAngle = config.sittingRelaxedPose.lowerLegAngleInDeg,
				sittingFootAngle = config.sittingRelaxedPose.footAngleInDeg,
				flatEnabled = config.flatRelaxedPose.enabled,
				flatUpperLegAngle = config.flatRelaxedPose.upperLegAngleInDeg,
				flatLowerLegAngle = config.flatRelaxedPose.lowerLegAngleInDeg,
				flatFootAngle = config.flatRelaxedPose.footAngleInDeg,
				setupComplete = config.setupComplete,
			),
		)
	}
}
