package dev.slimevr.solarxr

import dev.slimevr.config.ResetsConfig
import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import solarxr_protocol.rpc.ArmsResetMode
import solarxr_protocol.rpc.ChangeResetsSettingsRequest
import solarxr_protocol.rpc.ResetsSettingsRequest
import solarxr_protocol.rpc.ResetsSettingsResponse

class ResetsBehaviour(
	private val settings: Settings,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		// Send config
		receiver.rpcDispatcher.on<ResetsSettingsRequest> {
			val config = settings.context.state.value.data.resetsConfig
			receiver.sendRpc(
				ResetsSettingsResponse(
					resetMountingFeet = config.resetMountingFeet,
					armsResetMode = config.armsResetMode,
					yawResetSmoothTime = config.yawResetSmoothTime,
					saveMountingReset = config.saveMountingReset,
					resetPositionalHeadAttitude = config.resetPositionalHeadAttitude,
				),
			)
		}.launchIn(receiver.context.scope)

		// Receive config
		receiver.rpcDispatcher.on<ChangeResetsSettingsRequest> { req ->
			settings.context.dispatch(
				SettingsActions.Update {
					copy(
						resetsConfig = ResetsConfig(
							resetMountingFeet = req.resetMountingFeet == true,
							armsResetMode = req.armsResetMode ?: ArmsResetMode.BACK,
							yawResetSmoothTime = req.yawResetSmoothTime ?: 0f,
							saveMountingReset = req.saveMountingReset == true,
							resetPositionalHeadAttitude = req.resetPositionalHeadAttitude == true,
						),
					)
				},
			)
		}.launchIn(receiver.context.scope)
	}
}
