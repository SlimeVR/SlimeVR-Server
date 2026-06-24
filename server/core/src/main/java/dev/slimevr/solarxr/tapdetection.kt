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
			println(req.setupMode) // TODO
		}

		receiver.rpcDispatcher.on<ChangeTapDetectionSettingsRequest> { req ->
			settings.context.dispatch(SettingsActions.Update { copy(tapDetectionConfig = TapDetectionConfig(
				yawResetDelay = req.yawResetDelay ?: error("yawResetDelay should be set"),
				fullResetDelay = req.fullResetDelay ?: error("fullResetDelay should be set"),
				mountingResetDelay = req.mountingResetDelay ?: error("mountingResetDelay should be set"),
				yawResetEnabled = req.yawResetEnabled == true,
				fullResetEnabled = req.fullResetEnabled == true,
				mountingResetEnabled = req.mountingResetEnabled == true,
				yawResetTaps = req.yawResetTaps?.toInt() ?: error("yawResetTaps should be set"),
				fullResetTaps = req.fullResetTaps?.toInt() ?: error("fullResetTaps should be set"),
				mountingResetTaps = req.mountingResetTaps?.toInt() ?: error("mountingResetTaps should be set"),
				yawResetBodyPart = req.yawResetTracker ?: error("yawResetTracker should be set"),
				fullResetBodyPart = req.fullResetTracker ?: error("fullResetTracker should be set"),
				mountingResetBodyPart = req.mountingResetTracker ?: error("mountingResetTracker should be set"),
				numberTrackersOverThreshold = req.numberTrackersOverThreshold?.toInt() ?: error("numberTrackersOverThreshold should be set"),
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
