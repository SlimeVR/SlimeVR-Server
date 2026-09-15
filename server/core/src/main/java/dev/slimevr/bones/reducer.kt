package dev.slimevr.bones

fun reduce(state: BoneRegistryState, action: BoneRegistryActions): BoneRegistryState = when (action) {
	is BoneRegistryActions.Freeze -> state.copy(frozen = true)
}
