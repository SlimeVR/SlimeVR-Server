package dev.slimevr.solarxr.rpc

import dev.slimevr.config.AppConfig
import dev.slimevr.config.ProfileNames
import dev.slimevr.config.ProfilesState
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.rpc.CreateProfileRequest
import solarxr_protocol.rpc.DeleteProfileRequest
import solarxr_protocol.rpc.ProfileList
import solarxr_protocol.rpc.ProfileOperationResponse
import solarxr_protocol.rpc.ProfilesRequest
import solarxr_protocol.rpc.ProfilesResponse
import solarxr_protocol.rpc.RenameProfileRequest
import solarxr_protocol.rpc.SwitchProfileRequest

private fun buildProfileList(profiles: ProfileNames) = ProfileList(active = profiles.active, names = profiles.names)

internal fun buildProfilesResponse(state: ProfilesState) = ProfilesResponse(
	settings = buildProfileList(state.settings),
	user = buildProfileList(state.user),
)

class ProfilesBehaviour(
	private val config: AppConfig,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		receiver.rpcDispatcher.on<ProfilesRequest> {
			receiver.sendRpc(buildProfilesResponse(config.profiles.value))
		}.launchIn(receiver.context.scope)

		config.profiles
			.drop(1)
			.map(::buildProfilesResponse)
			.onEach(receiver::sendRpc)
			.launchIn(receiver.context.scope)

		receiver.rpcDispatcher.on<SwitchProfileRequest> { req ->
			receiver.sendRpc(ProfileOperationResponse(config.switchProfile(req.kind, req.name.orEmpty())))
		}.launchIn(receiver.context.scope)

		receiver.rpcDispatcher.on<CreateProfileRequest> { req ->
			val copyFrom = req.copyFrom?.takeIf { it.isNotEmpty() }
			receiver.sendRpc(ProfileOperationResponse(config.createProfile(req.kind, req.name.orEmpty(), copyFrom)))
		}.launchIn(receiver.context.scope)

		receiver.rpcDispatcher.on<RenameProfileRequest> { req ->
			receiver.sendRpc(ProfileOperationResponse(config.renameProfile(req.kind, req.name.orEmpty(), req.newName.orEmpty())))
		}.launchIn(receiver.context.scope)

		receiver.rpcDispatcher.on<DeleteProfileRequest> { req ->
			receiver.sendRpc(ProfileOperationResponse(config.deleteProfile(req.kind, req.name.orEmpty())))
		}.launchIn(receiver.context.scope)
	}
}
