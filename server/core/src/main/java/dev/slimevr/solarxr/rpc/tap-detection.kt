package dev.slimevr.solarxr.rpc

import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import dev.slimevr.config.TapDetectionConfig
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import dev.slimevr.tapdetection.TapDetectionActions
import dev.slimevr.tapdetection.TapDetectionManager
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.rpc.ChangeTapDetectionSettingsRequest
import solarxr_protocol.rpc.TapDetectionSettingsRequest
import solarxr_protocol.rpc.TapDetectionSettingsResponse
import solarxr_protocol.rpc.TapDetectionSetupModeRequest

internal fun buildTapDetectionSettingsResponse(config: TapDetectionConfig) = TapDetectionSettingsResponse(
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

class TapDetectionBehaviour(
	private val settings: Settings,
	private val tapDetectionManager: TapDetectionManager,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		// Send config
		receiver.rpcDispatcher.on<TapDetectionSettingsRequest> {
			receiver.sendRpc(buildTapDetectionSettingsResponse(settings.context.state.value.data.tapDetectionConfig))
		}.launchIn(receiver.context.scope)

		// Push config changes, including profile switches
		settings.context.state
			.map { buildTapDetectionSettingsResponse(it.data.tapDetectionConfig) }
			.distinctUntilChanged()
			.drop(1)
			.onEach(receiver::sendRpc)
			.launchIn(receiver.context.scope)

		// Receive config
		receiver.rpcDispatcher.on<ChangeTapDetectionSettingsRequest> { req ->
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
							yawResetBodyPart = req.yawResetTracker,
							fullResetBodyPart = req.fullResetTracker,
							mountingResetBodyPart = req.mountingResetTracker,
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
