package dev.slimevr.solarxr.rpc

import dev.slimevr.config.ResetsConfig
import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.rpc.ChangeResetsSettingsRequest
import solarxr_protocol.rpc.ResetsSettingsRequest
import solarxr_protocol.rpc.ResetsSettingsResponse

private fun buildResponse(config: ResetsConfig) = ResetsSettingsResponse(
	resetMountingFeet = config.resetMountingFeet,
	armsMountingResetMode = config.armsMountingResetMode,
	yawResetSmoothTime = config.yawResetSmoothTime,
	saveMountingReset = config.saveMountingReset,
	resetReliableReferenceAttitude = config.resetReliableReferenceAttitude,
	mountingMethod = config.mountingMethod,
)

class ResetsBehaviour(
	private val settings: Settings,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		settings.context.state
			.map { state -> state.data.resetsConfig }
			.distinctUntilChanged()
			.onEach { config -> receiver.sendRpc(buildResponse(config)) }
			.launchIn(receiver.context.scope)

		// Send config
		receiver.rpcDispatcher.on<ResetsSettingsRequest> {
			receiver.sendRpc(buildResponse(settings.context.state.value.data.resetsConfig))
		}.launchIn(receiver.context.scope)

		// Receive config
		receiver.rpcDispatcher.on<ChangeResetsSettingsRequest> { req ->
			settings.context.dispatch(
				SettingsActions.Update {
					copy(
						resetsConfig = resetsConfig.copy(
							resetMountingFeet = req.resetMountingFeet ?: resetsConfig.resetMountingFeet,
							armsMountingResetMode = req.armsMountingResetMode ?: resetsConfig.armsMountingResetMode,
							yawResetSmoothTime = req.yawResetSmoothTime ?: resetsConfig.yawResetSmoothTime,
							saveMountingReset = req.saveMountingReset ?: resetsConfig.saveMountingReset,
							resetReliableReferenceAttitude = req.resetReliableReferenceAttitude ?: resetsConfig.resetReliableReferenceAttitude,
							mountingMethod = req.mountingMethod ?: resetsConfig.mountingMethod,
						),
					)
				},
			)
		}.launchIn(receiver.context.scope)
	}
}
