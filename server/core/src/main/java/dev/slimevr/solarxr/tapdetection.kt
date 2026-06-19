package dev.slimevr.solarxr

import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import dev.slimevr.config.TapDetectionConfig
import solarxr_protocol.rpc.ChangeSettingsRequest
import solarxr_protocol.rpc.TapDetectionSettings

class TapDetectionBehaviour(
	private val settings: Settings,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		receiver.rpcDispatcher.on<ChangeSettingsRequest> { req ->
			val tapDetectionSettings = req.tapDetectionSettings ?: return@on

			val oldConfig = settings.context.state.value.data.tapDetectionConfig
			val newConfig = TapDetectionConfig(
				yawResetDelay = tapDetectionSettings.yawResetDelay ?: oldConfig.yawResetDelay,
				fullResetDelay = tapDetectionSettings.fullResetDelay ?: oldConfig.fullResetDelay,
				mountingResetDelay = tapDetectionSettings.mountingResetDelay ?: oldConfig.mountingResetDelay,
				yawResetEnabled = tapDetectionSettings.yawResetEnabled == true,
				fullResetEnabled = tapDetectionSettings.fullResetEnabled == true,
				mountingResetEnabled = tapDetectionSettings.mountingResetEnabled == true,
				yawResetTaps = tapDetectionSettings.yawResetTaps?.toInt() ?: oldConfig.yawResetTaps,
				fullResetTaps = tapDetectionSettings.fullResetTaps?.toInt() ?: oldConfig.fullResetTaps,
				mountingResetTaps = tapDetectionSettings.mountingResetTaps?.toInt() ?: oldConfig.mountingResetTaps,
				yawResetBodyPart = tapDetectionSettings.yawResetTracker ?: oldConfig.yawResetBodyPart,
				fullResetBodyPart = tapDetectionSettings.fullResetTracker ?: oldConfig.fullResetBodyPart,
				mountingResetBodyPart = tapDetectionSettings.mountingResetTracker ?: oldConfig.mountingResetBodyPart,
				numberTrackersOverThreshold = tapDetectionSettings.numberTrackersOverThreshold?.toInt() ?: oldConfig.numberTrackersOverThreshold,
				setupMode = tapDetectionSettings.setupMode == true,
			)

			settings.context.dispatch(SettingsActions.Update { copy(tapDetectionConfig = newConfig) })
		}
	}
}

fun buildTapDetectionSettings(config: TapDetectionConfig): TapDetectionSettings = TapDetectionSettings(
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
