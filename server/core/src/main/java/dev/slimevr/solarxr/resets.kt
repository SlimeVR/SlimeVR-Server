package dev.slimevr.solarxr

import dev.slimevr.config.ResetsConfig
import dev.slimevr.resets.ResetsActions
import dev.slimevr.resets.ResetsManager
import solarxr_protocol.rpc.ChangeResetsSettingsRequest
import solarxr_protocol.rpc.ResetsSettingsRequest
import solarxr_protocol.rpc.ResetsSettingsResponse

class ResetsBehaviour(
	private val resetsManager: ResetsManager,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		receiver.rpcDispatcher.on<ResetsSettingsRequest> {
			receiver.sendRpc(buildResetsSettings(resetsManager.context.state.value.config))
		}

		val oldConfig = resetsManager.context.state.value.config
		receiver.rpcDispatcher.on<ChangeResetsSettingsRequest> { req ->
			resetsManager.context.dispatch(ResetsActions.UpdateConfig(ResetsConfig(
				resetMountingFeet = req.resetMountingFeet == true,
				armsResetMode = req.armsResetMode ?: oldConfig.armsResetMode,
				yawResetSmoothTime = req.yawResetSmoothTime ?: oldConfig.yawResetSmoothTime,
				saveMountingReset = req.saveMountingReset == true,
				resetHmdPitch = req.resetHmdPitch == true,
			)))
		}
	}

	private fun buildResetsSettings(config: ResetsConfig): ResetsSettingsResponse = ResetsSettingsResponse(
		resetMountingFeet = config.resetMountingFeet,
		armsMountingResetMode = config.armsResetMode,
		yawResetSmoothTime = config.yawResetSmoothTime,
		saveMountingReset = config.saveMountingReset,
		resetHmdPitch = config.resetHmdPitch,
	)
}
