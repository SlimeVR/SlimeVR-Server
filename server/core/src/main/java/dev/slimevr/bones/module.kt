package dev.slimevr.bones

import dev.slimevr.context.Context
import kotlinx.coroutines.CoroutineScope

data class BoneRegistryState(val registry: BoneRegistry, val frozen: Boolean)

sealed interface BoneRegistryActions {
	data object Freeze : BoneRegistryActions
}

typealias BoneRegistryContext = Context<BoneRegistryState, BoneRegistryActions>

/**
 * Owns the server's [BoneRegistry], seeded from the compiled resource-pack skeleton at bootstrap.
 * [freeze] closes the window before the tracking stack starts: no tracker or skeleton state is
 * ever built against a registry that can still change under it.
 */
class BoneRegistryManager(val context: BoneRegistryContext) {
	val current: BoneRegistry get() = context.state.value.registry

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
