package dev.slimevr.solarxr

import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import dev.slimevr.config.TapDetectionConfig
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
					yawResetTracker = config.yawResetBodyPart,
					fullResetTracker = config.fullResetBodyPart,
					mountingResetTracker = config.mountingResetBodyPart,
					numberTrackersOverThreshold = config.numberTrackersOverThreshold.toUByte(),
				),
			)
		}

		// Receive config
		receiver.rpcDispatcher.on<ChangeTapDetectionSettingsRequest> { req ->
			val oldConfig = settings.context.state.value.data.tapDetectionConfig
			settings.context.dispatch(
				SettingsActions.Update {
					copy(
						tapDetectionConfig = TapDetectionConfig(
							yawResetDelay = req.yawResetDelay ?: oldConfig.yawResetDelay,
							fullResetDelay = req.fullResetDelay ?: oldConfig.fullResetDelay,
							mountingResetDelay = req.mountingResetDelay ?: oldConfig.mountingResetDelay,
							yawResetEnabled = req.yawResetEnabled == true,
							fullResetEnabled = req.fullResetEnabled == true,
							mountingResetEnabled = req.mountingResetEnabled == true,
							yawResetTaps = req.yawResetTaps?.toInt() ?: oldConfig.yawResetTaps,
							fullResetTaps = req.fullResetTaps?.toInt() ?: oldConfig.fullResetTaps,
							mountingResetTaps = req.mountingResetTaps?.toInt() ?: oldConfig.mountingResetTaps,
							yawResetBodyPart = req.yawResetTracker ?: oldConfig.yawResetBodyPart,
							fullResetBodyPart = req.fullResetTracker ?: oldConfig.fullResetBodyPart,
							mountingResetBodyPart = req.mountingResetTracker ?: oldConfig.mountingResetBodyPart,
							numberTrackersOverThreshold = req.numberTrackersOverThreshold?.toInt() ?: oldConfig.numberTrackersOverThreshold,
						),
					)
				},
			)
		}

		// Setup Mode (tap to assign)
		receiver.rpcDispatcher.on<TapDetectionSetupModeRequest> { req ->
			tapDetectionManager.context.dispatch(TapDetectionActions.SetSetupMode(req.setupMode == true))
		}
	}
}
