package dev.slimevr.solarxr

import dev.slimevr.config.OutputTrackersConfig
import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import dev.slimevr.outputtrackertoggle.OutputTrackerToggleManager
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.ChangeOutputTrackersSettingsRequest
import solarxr_protocol.rpc.OutputTrackersSettingsRequest
import solarxr_protocol.rpc.OutputTrackersSettingsResponse

class OutputTrackersBehaviour(
	private val settings: Settings,
	private val outputTrackerToggleManager: OutputTrackerToggleManager,
) : SolarXRBridgeBehaviour {
	companion object {
		private fun getSettingsResponse(config: OutputTrackersConfig, autoTrackers: List<BodyPart>) = OutputTrackersSettingsResponse(
			automaticTrackerToggle = config.automaticTrackerToggle,
			trackers = if (config.automaticTrackerToggle) autoTrackers else config.trackers,
			sendDerivedVelocity = config.sendDerivedVelocity,
		)
	}

	override fun observe(receiver: SolarXRBridge) {
		// Send config
		receiver.rpcDispatcher.on<OutputTrackersSettingsRequest> {
			receiver.sendRpc(getSettingsResponse(settings.context.state.value.data.outputTrackersConfig, outputTrackerToggleManager.context.state.value.trackers))
		}.launchIn(receiver.context.scope)

		combine(settings.context.state.map { it.data.outputTrackersConfig }, outputTrackerToggleManager.context.state.map { it.trackers }, ::Pair)
			.map { (config, trackers) -> getSettingsResponse(config, trackers) }
			.distinctUntilChanged()
			.onEach(receiver::sendRpc)
			.launchIn(receiver.context.scope)

		// Receive config
		receiver.rpcDispatcher.on<ChangeOutputTrackersSettingsRequest> { req ->
			settings.context.dispatch(
				SettingsActions.Update {
					copy(
						outputTrackersConfig = OutputTrackersConfig(
							automaticTrackerToggle = req.automaticTrackerToggle == true,
							trackers = req.trackers ?: listOf(),
							sendDerivedVelocity = req.sendDerivedVelocity == true,
						),
					)
				},
			)
		}.launchIn(receiver.context.scope)
	}
}
