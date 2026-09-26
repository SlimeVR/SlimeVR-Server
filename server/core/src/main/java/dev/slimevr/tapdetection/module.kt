package dev.slimevr.tapdetection

import dev.slimevr.Phase1ContextProvider
import dev.slimevr.VRServer
import dev.slimevr.config.Settings
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.resets.ResetsManager
import kotlinx.coroutines.CoroutineScope
import kotlin.collections.listOf

data class TapDetectionState(
	val setupMode: Boolean,
)

sealed interface TapDetectionActions {
	data class SetSetupMode(val setupMode: Boolean) : TapDetectionActions
}

typealias TapDetectionContext = Context<TapDetectionState, TapDetectionActions>
typealias TapDetectionBehaviour = Behaviour<TapDetectionManager>

class TapDetectionManager(val context: TapDetectionContext, val server: VRServer, val resetsManager: ResetsManager, val settings: Settings) {
	fun startObserving() = context.observeAll(this)

	companion object {
		fun create(ctx: Phase1ContextProvider, resetsManager: ResetsManager, scope: CoroutineScope): TapDetectionManager {
			val context = Context.create(
				initialState = TapDetectionState(
					setupMode = false,
				),
				scope = scope,
				reducer = ::reduce,
				behaviours = listOf(TapDetectionBasicBehaviour()),
				name = "TapDetectionManager",
			)
			return TapDetectionManager(context, ctx.server, resetsManager, ctx.config.settings)
		}
	}
}
