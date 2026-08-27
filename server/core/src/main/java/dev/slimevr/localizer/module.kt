package dev.slimevr.localizer

import dev.slimevr.config.Settings
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.skeleton.Skeleton
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.CoroutineScope
import kotlin.collections.listOf

data class LocalizerState(
    val headPosition: Vector3,
)

sealed interface LocalizerActions {
    data object Reset : LocalizerActions
    data class SetHeadPosition(val position: Vector3) : LocalizerActions
}

typealias LocalizerContext = Context<LocalizerState, LocalizerActions>
typealias LocalizerBehaviour = Behaviour<LocalizerManager>

class LocalizerManager(val context: LocalizerContext, val settings: Settings, val skeleton: Skeleton) {
    fun startObserving() = context.observeAll(this)

    companion object {
        fun create(settings: Settings, skeleton: Skeleton, scope: CoroutineScope): LocalizerManager {
            val context = Context.create(
                initialState = LocalizerState(
                    headPosition = Vector3.NULL
                ),
                scope = scope,
                reducer = ::reduce,
                behaviours = listOf(LocalizerBasicBehaviour(settings, skeleton)),
                name = "LocalizerManager",
            )
            return LocalizerManager(context, settings, skeleton)
        }
    }
}
