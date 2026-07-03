package dev.slimevr.vmc

import dev.slimevr.Phase1ContextProvider
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.skeleton.Skeleton
import kotlinx.coroutines.CoroutineScope

object VMCState

sealed interface VMCActions {
}

typealias VMCContext = Context<VMCState, VMCActions>
typealias VMCBehaviourType = Behaviour<VMCState, VMCActions, VMCManager>

class VMCManager(val context: VMCContext) {
	fun startObserving() = context.observeAll(this)

	companion object {
		fun create(ctx: Phase1ContextProvider, skeleton: Skeleton, scope: CoroutineScope): VMCManager {
			val settings = ctx.config.settings
			val context = Context.create(
				initialState = VMCState,
				scope = scope,
				behaviours = listOf(
					VMCOutputBehaviour(skeleton, settings),
					VMCInputBehaviour(settings),
				),
				name = "VMC",
			)
			return VMCManager(context)
		}
	}
}
