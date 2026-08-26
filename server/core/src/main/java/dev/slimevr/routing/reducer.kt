package dev.slimevr.routing

fun reduce(state: BoneRoutingState, action: BoneRoutingActions): BoneRoutingState = when (action) {
	is BoneRoutingActions.SetRoutes -> state.copy(routes = action.routes)
}
