package dev.slimevr.networkprofile

fun reduce(state: NetworkProfileState, action: NetworkProfileActions): NetworkProfileState = when (action) {
	is NetworkProfileActions.UpdateNetworks -> state.copy(publicNetworks = action.networks)
}
