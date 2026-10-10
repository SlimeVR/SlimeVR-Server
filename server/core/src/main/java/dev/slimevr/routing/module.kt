package dev.slimevr.routing

import dev.slimevr.AppContextProvider
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import kotlinx.coroutines.CoroutineScope

data class BoneRoutingState(
	/**
	 * Which outputs each bone is sent to. A bone that is absent, or maps to an empty
	 * set, is not sent anywhere.
	 *
	 * Automatic mode builds this from the connected trackers and the output priority.
	 * Manual mode resolves it from BoneRoutingConfig.manualRoutes, dropping outputs
	 * that are off or cannot take the bone.
	 */
	val routes: Routes,
)

sealed interface BoneRoutingActions {
	data class SetRoutes(val routes: Routes) : BoneRoutingActions
}

typealias BoneRoutingContext = Context<BoneRoutingState, BoneRoutingActions>
typealias BoneRoutingBehaviour = Behaviour<BoneRoutingManager>

class BoneRoutingManager(val context: BoneRoutingContext) {
	fun startObserving(appContext: AppContextProvider) {
		context.behaviours.addAll(listOf(BoneRoutingBasicBehaviour(appContext)))
		context.observeAll(this)
	}

	companion object {
		fun create(scope: CoroutineScope): BoneRoutingManager {
			val context = Context.create(
				initialState = BoneRoutingState(routes = emptyMap()),
				scope = scope,
				reducer = ::reduce,
				name = "BoneRoutingManager",
			)
			return BoneRoutingManager(context)
		}
	}
}
