package dev.slimevr.solarxr

import dev.slimevr.config.ResetsConfig
import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import dev.slimevr.config.TapDetectionConfig
import solarxr_protocol.rpc.ChangeSettingsRequest
import solarxr_protocol.rpc.ResetsSettings
import solarxr_protocol.rpc.TapDetectionSettings

class ResetsBehaviour(
	private val settings: Settings,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		receiver.rpcDispatcher.on<ChangeSettingsRequest> { req ->
			val resetsSettings = req.resetsSettings ?: return@on

			val oldConfig = settings.context.state.value.data.resetsConfig
			val newConfig = oldConfig.copy(
				resetMountingFeet = resetsSettings.resetMountingFeet == true,
				armsResetMode = resetsSettings.armsMountingResetMode ?: oldConfig.armsResetMode,
				yawResetSmoothTime = resetsSettings.yawResetSmoothTime ?: oldConfig.yawResetSmoothTime,
				saveMountingReset = resetsSettings.saveMountingReset == true,
				resetHmdPitch = resetsSettings.resetHmdPitch == true,
			)

			settings.context.dispatch(SettingsActions.Update { copy(resetsConfig = newConfig) })
		}
	}
}

fun buildResetsSettings(config: ResetsConfig): ResetsSettings = ResetsSettings(
	resetMountingFeet = config.resetMountingFeet,
	armsMountingResetMode = config.armsResetMode,
	yawResetSmoothTime = config.yawResetSmoothTime,
	saveMountingReset = config.saveMountingReset,
	resetHmdPitch = config.resetHmdPitch,
)
