package dev.slimevr.solarxr.rpc

import dev.slimevr.AppContextProvider
import dev.slimevr.config.SettingsActions
import dev.slimevr.routing.OutputStates
import dev.slimevr.routing.Routes
import dev.slimevr.routing.acceptedBones
import dev.slimevr.routing.applyRoutingChange
import dev.slimevr.routing.conflictingOutputs
import dev.slimevr.routing.intendedRoutesFlow
import dev.slimevr.routing.outputStatesFlow
import dev.slimevr.routing.overridableBones
import dev.slimevr.routing.requiredBones
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.BoneRoute
import solarxr_protocol.rpc.BoneRoutingSettingsRequest
import solarxr_protocol.rpc.BoneRoutingSettingsResponse
import solarxr_protocol.rpc.ChangeBoneRoutingSettingsRequest
import solarxr_protocol.rpc.RoutingOutput
import solarxr_protocol.rpc.RoutingOutputState
import solarxr_protocol.rpc.RoutingOutputStatus

// Every bone any output can take gets a row, so the GUI can draw the whole table
// from the response alone.
private val ROUTABLE_BONES = RoutingOutput.entries.flatMapTo(mutableSetOf()) { acceptedBones(it) }

private fun buildResponse(
	automatic: Boolean,
	routes: Routes,
	outputStates: OutputStates,
) = BoneRoutingSettingsResponse(
	automatic = automatic,
	routes = ROUTABLE_BONES.map { bone ->
		BoneRoute(bone = bone, outputs = routes[bone].orEmpty().toList())
	},
	outputs = RoutingOutput.entries.map { output ->
		RoutingOutputStatus(
			output = output,
			accepts = acceptedBones(output).toList(),
			requires = requiredBones(output).toList(),
			overridable = overridableBones(output).toList(),
			conflicts = conflictingOutputs(output).toList(),
			state = outputStates[output] ?: RoutingOutputState.UNSUPPORTED,
		)
	},
)

class BoneRoutingBehaviour(
	private val appContext: AppContextProvider,
) : SolarXRBridgeBehaviour {
	private val settings = appContext.config.settings

	override fun observe(receiver: SolarXRBridge) {
		val responses = combine(
			settings.context.state.map { it.data.boneRoutingConfig.automatic }.distinctUntilChanged(),
			intendedRoutesFlow(appContext),
			outputStatesFlow(appContext),
			::buildResponse,
		).distinctUntilChanged()

		receiver.rpcDispatcher.on<BoneRoutingSettingsRequest> {
			receiver.sendRpc(responses.first())
		}.launchIn(receiver.context.scope)

		responses
			.drop(1)
			.onEach(receiver::sendRpc)
			.launchIn(receiver.context.scope)

		receiver.rpcDispatcher.on<ChangeBoneRoutingSettingsRequest> { req ->
			val requested = req.routes.orEmpty()
				.mapNotNull { route ->
					val bone = route.bone
					if (bone == BodyPart.NONE) return@mapNotNull null
					val outputs = route.outputs.orEmpty().toSet().ifEmpty { return@mapNotNull null }
					bone to outputs
				}
				.toMap()

			settings.context.dispatch(
				SettingsActions.Update {
					copy(
						boneRoutingConfig = applyRoutingChange(
							config = boneRoutingConfig,
							automatic = req.automatic,
							routes = requested,
						),
					)
				},
			)
		}.launchIn(receiver.context.scope)
	}
}
