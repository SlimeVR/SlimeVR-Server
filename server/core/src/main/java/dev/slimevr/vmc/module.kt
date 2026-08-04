package dev.slimevr.vmc

import dev.slimevr.AppContextProvider
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import kotlinx.coroutines.CoroutineScope
import solarxr_protocol.rpc.VMCOSCInputState
import solarxr_protocol.rpc.VMCOSCOutputState
import solarxr_protocol.rpc.VMCOSCVrmState

data class VMCStatus(
	val inputState: VMCOSCInputState = VMCOSCInputState.IDLE,
	val inputPort: Int? = null,
	val inputError: String? = null,
	val lastReceivedInputMillis: Long? = null,

	val outputState: VMCOSCOutputState = VMCOSCOutputState.IDLE,
	val outputError: String? = null,
	val targetAddress: String? = null,
	val targetPort: Int? = null,
	val lastFrameSentMillis: Long? = null,

	val vrmState: VMCOSCVrmState = VMCOSCVrmState.NONE,
	val vrmError: String? = null,
)

data class VMCState(
	val status: VMCStatus = VMCStatus(),
)

sealed interface VMCActions {
	data class SetInput(
		val state: VMCOSCInputState,
		val port: Int? = null,
		val error: String? = null,
	) : VMCActions
	data class SetLastReceivedInput(val millis: Long) : VMCActions

	data class SetOutput(
		val state: VMCOSCOutputState,
		val targetAddress: String? = null,
		val targetPort: Int? = null,
		val error: String? = null,
	) : VMCActions
	data class SetLastFrameSent(val millis: Long) : VMCActions

	data class SetVrm(
		val state: VMCOSCVrmState,
		val error: String? = null,
	) : VMCActions
}

typealias VMCContext = Context<VMCState, VMCActions>
typealias VMCBehaviour = Behaviour<VMCState, VMCActions, VMCManager>

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
				initialState = VMCState(),
				scope = scope,
				behaviours = emptyList<VMCBehaviour>(),
				name = "VMC",
			)
			return VMCManager(context)
		}
	}
}
