package dev.slimevr.tapdetection

import dev.slimevr.VRServer
import dev.slimevr.config.Settings
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import kotlinx.coroutines.CoroutineScope
import kotlin.collections.listOf

data class TapDetectionState(
    val setupMode: Boolean,
)

sealed interface TapDetectionActions {
    data class SetSetupMode(val setupMode: Boolean) : TapDetectionActions
}

typealias TapDetectionContext = Context<TapDetectionState, TapDetectionActions>
typealias TapDetectionBehaviour = Behaviour<TapDetectionState, TapDetectionActions, TapDetectionManager>

class TapDetectionManager(val context: TapDetectionContext, val server: VRServer) {
    fun startObserving() = context.observeAll(this)

    companion object {
        fun create(server: VRServer, scope: CoroutineScope, settings: Settings): TapDetectionManager {
            val context = Context.create(
                initialState = TapDetectionState(
                    setupMode = false,
                ),
                scope = scope,
                behaviours = listOf(TapDetectionBasicBehaviour()),
                name = "TapDetectionManager",
            )
            return TapDetectionManager(context, server)
        }
    }
}
