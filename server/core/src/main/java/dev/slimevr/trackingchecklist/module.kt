package dev.slimevr.trackingchecklist

import dev.slimevr.AppContextProvider
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import kotlinx.coroutines.CoroutineScope
import solarxr_protocol.rpc.TrackingChecklistStep
import solarxr_protocol.rpc.TrackingChecklistStepId

data class TrackingChecklistState(
	val steps: Map<TrackingChecklistStepId, TrackingChecklistStep> = emptyMap(),
)

sealed interface TrackingChecklistActions {
	data class UpdateStep(val id: TrackingChecklistStepId, val step: TrackingChecklistStep) : TrackingChecklistActions
}

typealias TrackingChecklistContext = Context<TrackingChecklistState, TrackingChecklistActions>
typealias TrackingChecklistBehaviourType = Behaviour<TrackingChecklistState, TrackingChecklistActions, TrackingChecklist>

object ChecklistBaseBehaviour : TrackingChecklistBehaviourType {
	override fun reduce(state: TrackingChecklistState, action: TrackingChecklistActions): TrackingChecklistState = when (action) {
		is TrackingChecklistActions.UpdateStep -> state.copy(steps = state.steps + (action.id to action.step))
	}
}

class TrackingChecklist(
	val context: TrackingChecklistContext,
) {
	fun startObserving(appContext: AppContextProvider) {
		val stepBehaviours: List<TrackingChecklistBehaviourType> = buildList {
			add(SteamVRCheckBehaviour(appContext.server))
			add(HMDCheckBehaviour(appContext.server))
			add(TrackerRestCheckBehaviour(appContext.server))
			add(TrackerErrorCheckBehaviour(appContext.server))
			appContext.vrcConfigManager?.let { add(VRChatSettingsCheckBehaviour(appContext.server, appContext.skeleton, it)) }
		}
		context.behaviours.addAll(stepBehaviours)
		context.observeAll(this)
	}

	companion object {
		fun create(scope: CoroutineScope): TrackingChecklist {
			val initialBehaviours = listOf(ChecklistBaseBehaviour)
			val context = Context.create(
				initialState = TrackingChecklistState(),
				scope = scope,
				behaviours = initialBehaviours,
			)
			val checklist = TrackingChecklist(context)
			return checklist
		}
	}
}
