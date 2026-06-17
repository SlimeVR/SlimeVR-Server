package dev.slimevr.vmc

import dev.slimevr.Phase1ContextProvider
import dev.slimevr.config.VMCConfig
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.skeleton.Skeleton
import kotlinx.coroutines.CoroutineScope

data class VMCState(val config: VMCConfig = VMCConfig())

sealed interface VMCActions {
	data class UpdateConfig(val config: VMCConfig) : VMCActions
}

typealias VMCContext = Context<VMCState, VMCActions>
typealias VMCBehaviourType = Behaviour<VMCState, VMCActions, VMCManager>

class VMCManager(val context: VMCContext) {
	fun startObserving() = context.observeAll(this)

	companion object {
		fun create(skeleton: Skeleton, ctx: Phase1ContextProvider, scope: CoroutineScope): VMCManager {
			val settings = ctx.config.settings
			val context = Context.create(
				initialState = VMCState(config = settings.context.state.value.data.vmcConfig),
				scope = scope,
				behaviours = listOf(
					VMCOutputBehaviour(skeleton, settings),
					VMCInputBehaviour(),
				),
				name = "VMC",
			)
			return VMCManager(context)
		}
	}
}
