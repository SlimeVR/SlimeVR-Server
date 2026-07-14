package dev.slimevr.solarxr

import dev.slimevr.outputtrackertoggle.OutputTrackerToggleManager
import dev.slimevr.config.OutputTrackersConfig
import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import solarxr_protocol.rpc.ChangeOutputTrackersSettingsRequest
import solarxr_protocol.rpc.OutputTrackersSettingsRequest
import solarxr_protocol.rpc.OutputTrackersSettingsResponse

class OutputTrackersBehaviour(
	private val settings: Settings,
	private val outputTrackerToggleManager: OutputTrackerToggleManager,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		// Send config
		receiver.rpcDispatcher.on<OutputTrackersSettingsRequest> {
			val config = settings.context.state.value.data.outputTrackersConfig
			receiver.sendRpc(
				OutputTrackersSettingsResponse(
					automaticTrackerToggle = config.automaticTrackerToggle,
					trackers = outputTrackerToggleManager.context.state.value.trackers,
					sendDerivedVelocity = config.sendDerivedVelocity,
				),
			)
		}

		// Receive config
		receiver.rpcDispatcher.on<ChangeOutputTrackersSettingsRequest> { req ->
			val oldConfig = settings.context.state.value.data.outputTrackersConfig
			settings.context.dispatch(
				SettingsActions.Update {
					copy(
						outputTrackersConfig = OutputTrackersConfig(
							automaticTrackerToggle = req.automaticTrackerToggle == true,
							trackers = req.trackers ?: oldConfig.trackers,
							sendDerivedVelocity = req.sendDerivedVelocity == true,
						),
					)
				},
			)
		}
	}
}
