package dev.slimevr.solarxr

import dev.slimevr.VRServer
import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import dev.slimevr.config.StayAlignedConfig
import dev.slimevr.config.StayAlignedRelaxedPoseConfig
import dev.slimevr.logging.AppLogger
import dev.slimevr.tracker.stayaligned.poses.RelaxedPose
import solarxr_protocol.rpc.ChangeStayAlignedEnabledRequest
import solarxr_protocol.rpc.ChangeStayAlignedSettingsRequest
import solarxr_protocol.rpc.DetectStayAlignedRelaxedPoseRequest
import solarxr_protocol.rpc.ResetStayAlignedRelaxedPoseRequest
import solarxr_protocol.rpc.StayAlignedRelaxedPose
import solarxr_protocol.rpc.StayAlignedSettingsRequest
import solarxr_protocol.rpc.StayAlignedSettingsResponse

class StayAlignedBehaviour(
	private val settings: Settings,
	private val server: VRServer,
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

		// Detect pose
		receiver.rpcDispatcher.on<DetectStayAlignedRelaxedPoseRequest> { req ->
			val pose = req.pose
			val trackerStates = server.context.state.value.trackers.values.map { it.context.state.value }
			val relaxedPose = RelaxedPose.fromTrackers(trackerStates)
			updatePoseInConfig(pose, StayAlignedRelaxedPoseConfig(true, relaxedPose.upperLeg.toDeg(), relaxedPose.lowerLeg.toDeg(), relaxedPose.foot.toDeg()))
			AppLogger.stayAligned.info("Set relaxed pose $pose with $relaxedPose")
			sendConfig(receiver, settings)
		}.launchIn(receiver.context.scope)

		// Reset pose
		receiver.rpcDispatcher.on<ResetStayAlignedRelaxedPoseRequest> { req ->
			val pose = req.pose
			updatePoseInConfig(pose, StayAlignedRelaxedPoseConfig(false, 0f, 0f, 0f))
			AppLogger.stayAligned.info("Reset relaxed pose $pose")
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

	private fun updatePoseInConfig(pose: StayAlignedRelaxedPose, poseConfig: StayAlignedRelaxedPoseConfig) {
		settings.context.dispatch(
			SettingsActions.Update {
				copy(
					stayAlignedConfig = when (pose) {
						StayAlignedRelaxedPose.STANDING -> stayAlignedConfig.copy(standingRelaxedPose = poseConfig)
						StayAlignedRelaxedPose.SITTING -> stayAlignedConfig.copy(sittingRelaxedPose = poseConfig)
						StayAlignedRelaxedPose.FLAT -> stayAlignedConfig.copy(flatRelaxedPose = poseConfig)
					},
				)
			},
		)
	}
}
