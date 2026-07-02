package dev.slimevr.solarxr

import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import dev.slimevr.config.TapDetectionConfig
import solarxr_protocol.rpc.ChangeTapDetectionSettingsRequest
import solarxr_protocol.rpc.TapDetectionSettingsRequest
import solarxr_protocol.rpc.TapDetectionSettingsResponse
import solarxr_protocol.rpc.TapDetectionSetupModeRequest
import solarxr_protocol.rpc.VMCOSCSettingsRequest

class TapDetectionBehaviour(
	private val settings: Settings,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		receiver.rpcDispatcher.on<TapDetectionSettingsRequest> {
			receiver.sendRpc(buildTapDetectionSettings(settings.context.state.value.data.tapDetectionConfig))
		}

		receiver.rpcDispatcher.on<TapDetectionSetupModeRequest> { req ->
			println("req.setupMode " + req.setupMode) // TODO
		}

		val oldConfig = settings.context.state.value.data.tapDetectionConfig
		receiver.rpcDispatcher.on<ChangeTapDetectionSettingsRequest> { req ->
			settings.context.dispatch(SettingsActions.Update { copy(tapDetectionConfig = TapDetectionConfig(
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
			))})
		}
	}

	private fun buildTapDetectionSettings(config: TapDetectionConfig): TapDetectionSettingsResponse = TapDetectionSettingsResponse(
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
	)
}
