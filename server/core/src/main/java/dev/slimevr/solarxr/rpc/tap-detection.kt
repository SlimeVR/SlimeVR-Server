package dev.slimevr.solarxr.rpc

import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import dev.slimevr.config.TrackerActionConfig
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import dev.slimevr.trackeraction.TrackerActionActions
import dev.slimevr.trackeraction.TrackerActionManager
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.TrackerActionMethod
import solarxr_protocol.rpc.ChangeTrackerActionSettingsRequest
import solarxr_protocol.rpc.TrackerActionSettings
import solarxr_protocol.rpc.TrackerActionSettingsRequest
import solarxr_protocol.rpc.TrackerActionSettingsResponse
import solarxr_protocol.rpc.TrackerActionSetupModeRequest

internal fun buildTrackerActionSettingsResponse(config: TrackerActionConfig) = TrackerActionSettingsResponse(
	yawResetAction = TrackerActionSettings(
		enabled = config.yawResetAction.enabled,
		delay = config.yawResetAction.delay,
		bodyPart = config.yawResetAction.bodyPart ?: BodyPart.NONE,
		method = config.yawResetAction.method,
		taps = config.yawResetAction.taps.toUByte(),
	),
	fullResetAction = TrackerActionSettings(
		enabled = config.fullResetAction.enabled,
		delay = config.fullResetAction.delay,
		bodyPart = config.fullResetAction.bodyPart ?: BodyPart.NONE,
		method = config.fullResetAction.method,
		taps = config.fullResetAction.taps.toUByte(),
	),
	mountingResetAction = TrackerActionSettings(
		enabled = config.mountingResetAction.enabled,
		delay = config.mountingResetAction.delay,
		bodyPart = config.mountingResetAction.bodyPart ?: BodyPart.NONE,
		method = config.mountingResetAction.method,
		taps = config.mountingResetAction.taps.toUByte(),
	),
	tapsTrackersOverThreshold = config.tapsTrackersOverThreshold.toUByte(),
)

class TrackerActionBehaviour(
	private val settings: Settings,
	private val trackerActionManager: TrackerActionManager,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		// Send config
		receiver.onRpc<TrackerActionSettingsRequest> { _, txId ->
			receiver.sendRpc(buildTrackerActionSettingsResponse(settings.context.state.value.data.trackerActionConfig), txId)
		}.launchIn(receiver.context.scope)

		// Push config changes, including profile switches
		settings.context.state
			.map { buildTrackerActionSettingsResponse(it.data.trackerActionConfig) }
			.distinctUntilChanged()
			.drop(1)
			.onEach(receiver::sendRpc)
			.launchIn(receiver.context.scope)

		// Receive config
		receiver.rpcDispatcher.on<ChangeTrackerActionSettingsRequest> { req ->
			settings.context.dispatch(
				SettingsActions.Update {
					copy(
						trackerActionConfig = TrackerActionConfig(
							yawResetAction = dev.slimevr.config.TrackerActionSettings(
								enabled = req.yawResetAction?.enabled == true,
								delay = req.yawResetAction?.delay ?: 0f,
								bodyPart = req.yawResetAction?.bodyPart,
								method = req.yawResetAction?.method ?: TrackerActionMethod.TAP,
								taps = req.yawResetAction?.taps?.toInt() ?: 2,
							),
							fullResetAction = dev.slimevr.config.TrackerActionSettings(
								enabled = req.fullResetAction?.enabled == true,
								delay = req.fullResetAction?.delay ?: 0f,
								bodyPart = req.fullResetAction?.bodyPart,
								method = req.fullResetAction?.method ?: TrackerActionMethod.TAP,
								taps = req.fullResetAction?.taps?.toInt() ?: 2,
							),
							mountingResetAction = dev.slimevr.config.TrackerActionSettings(
								enabled = req.mountingResetAction?.enabled == true,
								delay = req.mountingResetAction?.delay ?: 0f,
								bodyPart = req.mountingResetAction?.bodyPart,
								method = req.mountingResetAction?.method ?: TrackerActionMethod.TAP,
								taps = req.mountingResetAction?.taps?.toInt() ?: 2,
							),
							tapsTrackersOverThreshold = req.tapsTrackersOverThreshold.toInt(),
						),
					)
				},
			)
		}.launchIn(receiver.context.scope)

		// Setup Mode (tap/button press to assign)
		receiver.rpcDispatcher.on<TrackerActionSetupModeRequest> { req ->
			trackerActionManager.context.dispatch(TrackerActionActions.SetSetupMode(req.setupMode))
		}.launchIn(receiver.context.scope)
	}
}
