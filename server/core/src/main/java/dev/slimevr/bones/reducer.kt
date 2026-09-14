package dev.slimevr.bones

import solarxr_protocol.connection.BoneDefinition
import solarxr_protocol.connection.BoneRegistry as WireBoneRegistry

/**
 * Assigns each of [incoming] a fresh ID above every ID [registry] already has, preserving order.
 * A `parent` must reference a bone [registry] already defines
 */
private fun assignIds(registry: BoneRegistry, incoming: List<BoneDefinition>): List<BoneDefinition> {
	var nextId = registry.maxId
	return incoming.map { definition ->
		require(definition.parent == 0.toUShort() || registry[BoneId(definition.parent)] != null) {
			"Extension bone \"${definition.key}\" has an unknown parent"
		}
		nextId++
		definition.copy(id = nextId.toUShort())
	}
}

fun reduce(state: BoneRegistryState, action: BoneRegistryActions): BoneRegistryState = when (action) {
	is BoneRegistryActions.RegisterBones -> {
		require(!state.frozen) { "The bone registry is frozen; bones must be registered before the tracking stack starts" }
		val merged = WireBoneRegistry(bones = state.registry.value.bones + assignIds(state.registry, action.bones))
		state.copy(registry = BoneRegistry.from(merged))
	}

	is BoneRegistryActions.Freeze -> state.copy(frozen = true)
}
