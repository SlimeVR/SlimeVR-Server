package dev.slimevr.bones

import dev.slimevr.context.Context
import kotlinx.coroutines.CoroutineScope
import solarxr_protocol.connection.BoneDefinition

data class BoneRegistryState(val registry: BoneRegistry, val frozen: Boolean)

sealed interface BoneRegistryActions {
	data class RegisterBones(val bones: List<BoneDefinition>) : BoneRegistryActions
	data object Freeze : BoneRegistryActions
}

typealias BoneRegistryContext = Context<BoneRegistryState, BoneRegistryActions>

/**
 * Owns the server's [BoneRegistry]. Bones are contributed via [register] during bootstrap, then
 * [freeze] closes the window before the tracking stack starts: no tracker or skeleton state is
 * ever built against a registry that can still change under it.
 */
class BoneRegistryManager(val context: BoneRegistryContext) {
	val current: BoneRegistry get() = context.state.value.registry

	fun register(bones: List<BoneDefinition>) = context.dispatch(BoneRegistryActions.RegisterBones(bones))
	fun freeze() = context.dispatch(BoneRegistryActions.Freeze)

	companion object {
		fun create(scope: CoroutineScope, initial: BoneRegistry = BoneRegistry.standard()): BoneRegistryManager {
			val context = Context.create(
				initialState = BoneRegistryState(registry = initial, frozen = false),
				scope = scope,
				reducer = ::reduce,
				name = "BoneRegistry",
			)
			return BoneRegistryManager(context)
		}
	}
}
