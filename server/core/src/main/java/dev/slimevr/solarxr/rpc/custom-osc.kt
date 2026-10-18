package dev.slimevr.solarxr.rpc

import dev.slimevr.config.CustomOscConfig
import dev.slimevr.config.CustomOscParamMapping
import dev.slimevr.config.CustomOscProfile
import dev.slimevr.config.CustomOscTrackerMapping
import dev.slimevr.config.OscAxisSource
import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import kotlinx.coroutines.flow.launchIn
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.ChangeCustomOSCSettingsRequest
import solarxr_protocol.rpc.CustomOSCSettingsRequest
import solarxr_protocol.rpc.CustomOSCSettingsResponse

class CustomOscBehaviour(
	private val settings: Settings,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		// Handle settings read request
		receiver.rpcDispatcher.on<CustomOSCSettingsRequest> {
			val config = settings.context.state.value.data.customOscConfig
			receiver.sendRpc(buildResponse(config))
		}.launchIn(receiver.context.scope)

		// Handle settings write request
		receiver.rpcDispatcher.on<ChangeCustomOSCSettingsRequest> { req ->
			settings.context.dispatch(
				SettingsActions.Update {
					copy(
						customOscConfig = CustomOscConfig(
							enabled = req.enabled,
							profiles = req.profiles?.map { fbProfile ->
								CustomOscProfile(
									id = fbProfile.id ?: "",
									name = fbProfile.name ?: "",
									enabled = fbProfile.enabled,
									address = fbProfile.address ?: "127.0.0.1",
									port = fbProfile.port.takeIf { it > 0u }?.toInt() ?: 9000,
									trackers = fbProfile.trackers?.map { fbTracker ->
										CustomOscTrackerMapping(
											bodyPart = BodyPart.entries.firstOrNull { it.value == fbTracker.bodyPart },
											params = fbTracker.params?.map { fbParam ->
												CustomOscParamMapping(
													axis = OscAxisSource.entries.getOrNull(fbParam.axis.ordinal)
														?: OscAxisSource.ROTATION_PITCH,
													address = fbParam.address ?: "",
												)
											} ?: emptyList(),
										)
									} ?: emptyList(),
								)
							} ?: customOscConfig.profiles,
						),
					)
				},
			)
		}.launchIn(receiver.context.scope)
	}

	private fun buildResponse(config: CustomOscConfig) = CustomOSCSettingsResponse(
		enabled = config.enabled,
		profiles = config.profiles.map { profile ->
			solarxr_protocol.rpc.CustomOSCProfile(
				id = profile.id,
				name = profile.name,
				enabled = profile.enabled,
				address = profile.address,
				port = profile.port.toUShort(),
				trackers = profile.trackers.map { tracker ->
					solarxr_protocol.rpc.CustomOSCTrackerMapping(
						bodyPart = tracker.bodyPart?.value ?: BodyPart.NONE.value,
						params = tracker.params.map { param ->
							solarxr_protocol.rpc.CustomOSCParamMapping(
								axis = solarxr_protocol.rpc.CustomOSCAxisSource.entries.getOrNull(param.axis.ordinal)
									?: solarxr_protocol.rpc.CustomOSCAxisSource.POSITION_X,
								address = param.address,
							)
						},
					)
				},
			)
		},
	)
}
