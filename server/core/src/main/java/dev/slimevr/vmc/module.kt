package dev.slimevr.vmc

import dev.slimevr.AppContextProvider
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import kotlinx.coroutines.CoroutineScope

object VMCState

sealed interface VMCActions

typealias VMCContext = Context<VMCState, VMCActions>
typealias VMCBehaviourType = Behaviour<VMCState, VMCActions, VMCManager>

class VMCManager(val context: VMCContext) {
	fun startObserving(appContext: AppContextProvider) {
		val settings = appContext.config.settings
		val behaviours = listOf(
			VMCOutputBehaviour(appContext.skeleton, settings, appContext.boneRouting),
			VMCInputBehaviour(settings),
		)

		context.behaviours.addAll(behaviours)
		context.observeAll(this)
	}

	companion object {
		fun create(scope: CoroutineScope): VMCManager {
			val context = Context.create(
				initialState = VMCState,
				scope = scope,
				behaviours = emptyList<VMCBehaviourType>(),
				name = "VMC",
			)
			return VMCManager(context)
		}
	}
}
