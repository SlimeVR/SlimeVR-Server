package dev.slimevr.solarxr.rpc

import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import dev.slimevr.config.TapDetectionConfig
import dev.slimevr.skeleton.BoneId
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import dev.slimevr.tapdetection.TapDetectionActions
import dev.slimevr.tapdetection.TapDetectionManager
import solarxr_protocol.rpc.ChangeTapDetectionSettingsRequest
import solarxr_protocol.rpc.TapDetectionSettingsRequest
import solarxr_protocol.rpc.TapDetectionSettingsResponse
import solarxr_protocol.rpc.TapDetectionSetupModeRequest

class TapDetectionBehaviour(
	private val settings: Settings,
	private val tapDetectionManager: TapDetectionManager,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		// Send config
		receiver.rpcDispatcher.on<TapDetectionSettingsRequest> {
			val config = settings.context.state.value.data.tapDetectionConfig
			val registry = receiver.registry
			receiver.sendRpc(
				TapDetectionSettingsResponse(
					yawResetDelay = config.yawResetDelay,
					fullResetDelay = config.fullResetDelay,
					mountingResetDelay = config.mountingResetDelay,
					yawResetEnabled = config.yawResetEnabled,
					fullResetEnabled = config.fullResetEnabled,
					mountingResetEnabled = config.mountingResetEnabled,
					yawResetTaps = config.yawResetTaps.toUByte(),
					fullResetTaps = config.fullResetTaps.toUByte(),
					mountingResetTaps = config.mountingResetTaps.toUByte(),
					yawResetBoneId = config.yawResetBone?.let { registry.byKey(it)?.id },
					fullResetBoneId = config.fullResetBone?.let { registry.byKey(it)?.id },
					mountingResetBoneId = config.mountingResetBone?.let { registry.byKey(it)?.id },
					numberTrackersOverThreshold = config.numberTrackersOverThreshold.toUByte(),
				),
			)
		}.launchIn(receiver.context.scope)

		// Receive config
		receiver.rpcDispatcher.on<ChangeTapDetectionSettingsRequest> { req ->
			val registry = receiver.registry
			settings.context.dispatch(
				SettingsActions.Update {
					copy(
						tapDetectionConfig = TapDetectionConfig(
							yawResetDelay = req.yawResetDelay ?: 0f,
							fullResetDelay = req.fullResetDelay ?: 0f,
							mountingResetDelay = req.mountingResetDelay ?: 0f,
							yawResetEnabled = req.yawResetEnabled == true,
							fullResetEnabled = req.fullResetEnabled == true,
							mountingResetEnabled = req.mountingResetEnabled == true,
							yawResetTaps = req.yawResetTaps?.toInt() ?: 2,
							fullResetTaps = req.fullResetTaps?.toInt() ?: 2,
							mountingResetTaps = req.mountingResetTaps?.toInt() ?: 2,
							yawResetBone = req.yawResetBoneId?.let { registry[BoneId(it)]?.key },
							fullResetBone = req.fullResetBoneId?.let { registry[BoneId(it)]?.key },
							mountingResetBone = req.mountingResetBoneId?.let { registry[BoneId(it)]?.key },
							numberTrackersOverThreshold = req.numberTrackersOverThreshold?.toInt() ?: 1,
						),
					)
				},
			)
		}.launchIn(receiver.context.scope)

		// Setup Mode (tap to assign)
		receiver.rpcDispatcher.on<TapDetectionSetupModeRequest> { req ->
			tapDetectionManager.context.dispatch(TapDetectionActions.SetSetupMode(req.setupMode == true))
		}.launchIn(receiver.context.scope)
	}
}
