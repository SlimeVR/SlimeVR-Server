package dev.slimevr.trackingchecklist

import dev.slimevr.VRServer
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.skeleton.Skeleton
import dev.slimevr.vrchat.VRCConfigManager
import kotlinx.coroutines.CoroutineScope
import solarxr_protocol.rpc.TrackingChecklistExtraData
import solarxr_protocol.rpc.TrackingChecklistStepId
import solarxr_protocol.rpc.TrackingChecklistStepVisibility

data class ChecklistStep(
    val valid: Boolean,
    val enabled: Boolean,
    val optional: Boolean = false,
    val ignorable: Boolean = false,
    val visibility: TrackingChecklistStepVisibility = TrackingChecklistStepVisibility.WHEN_INVALID,
    val extraData: TrackingChecklistExtraData? = null,
)

data class TrackingChecklistState(
    val steps: Map<TrackingChecklistStepId, ChecklistStep> = emptyMap(),
    val ignoredSteps: Set<TrackingChecklistStepId> = emptySet(),
)

sealed interface TrackingChecklistActions {
    data class UpdateStep(val id: TrackingChecklistStepId, val step: ChecklistStep) : TrackingChecklistActions
    data class SetIgnored(val id: TrackingChecklistStepId, val ignored: Boolean) : TrackingChecklistActions
}

typealias TrackingChecklistContext = Context<TrackingChecklistState, TrackingChecklistActions>
typealias TrackingChecklistBehaviourType = Behaviour<TrackingChecklistState, TrackingChecklistActions, TrackingChecklist>

object ChecklistBaseBehaviour : TrackingChecklistBehaviourType {
    override fun reduce(state: TrackingChecklistState, action: TrackingChecklistActions): TrackingChecklistState = when (action) {
        is TrackingChecklistActions.UpdateStep -> state.copy(steps = state.steps + (action.id to action.step))
        is TrackingChecklistActions.SetIgnored -> state.copy(
            ignoredSteps = if (action.ignored) state.ignoredSteps + action.id else state.ignoredSteps - action.id,
        )
    }
}

class TrackingChecklist(
    val context: TrackingChecklistContext,
) {
    fun setIgnored(stepId: TrackingChecklistStepId, ignored: Boolean) {
        context.dispatch(TrackingChecklistActions.SetIgnored(stepId, ignored))
    }

    companion object {
        fun create(
            scope: CoroutineScope,
            server: VRServer,
            skeleton: Skeleton,
            vrcConfigManager: VRCConfigManager?,
        ): TrackingChecklist {
            val stepBehaviours: List<TrackingChecklistBehaviourType> = buildList {
                add(SteamVRCheckBehaviour(server))
                add(HMDCheckBehaviour(server))
				add(TrackerRestCheckBehaviour(server))
                add(TrackerErrorCheckBehaviour(server))
                vrcConfigManager?.let { add(VRChatSettingsCheckBehaviour(server, skeleton, it)) }
            }

            val behaviours = listOf(ChecklistBaseBehaviour) + stepBehaviours

            val context = Context.create(
                initialState = TrackingChecklistState(),
                scope = scope,
                behaviours = behaviours,
            )

            val checklist = TrackingChecklist(context)
            behaviours.forEach { it.observe(checklist) }
            return checklist
        }
    }
}