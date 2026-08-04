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

class ChecklistBaseBehaviour : TrackingChecklistBehaviourType {
	override fun reduce(state: TrackingChecklistState, action: TrackingChecklistActions): TrackingChecklistState = when (action) {
		is TrackingChecklistActions.UpdateStep -> state.copy(steps = state.steps + (action.id to action.step))
	}
}

class TrackingChecklist(
	val context: TrackingChecklistContext,
	val extraBehaviours: (AppContextProvider) -> List<TrackingChecklistBehaviourType>,
) {
	fun startObserving(appContext: AppContextProvider) {
		val stepBehaviours: List<TrackingChecklistBehaviourType> = buildList {
			add(HMDCheckBehaviour(appContext.server))
			add(TrackerRestCheckBehaviour(appContext.server))
			add(FullResetCheckBehaviour(appContext.server, appContext.resetsManager))
			add(MountingCalibrationCheckBehaviour(appContext.server, appContext.resetsManager, appContext.config.settings))
			add(TrackerErrorCheckBehaviour(appContext.server))
			add(SteamVRHandsCheckBehaviour(appContext.server, appContext.config.settings, appContext.boneRouting))
			add(FeetMountingCalibrationCheckBehaviour(appContext.server, appContext.resetsManager, appContext.config.settings))
			// TODO: STAY_ALIGNED_CONFIGURED

			appContext.vrcConfigManager?.let { add(VRChatSettingsCheckBehaviour(appContext.server, appContext.skeleton, it)) }
			appContext.networkProfileManager?.let { add(NetworkProfileCheckBehaviour(it)) }
		} +
			extraBehaviours(appContext)
		context.behaviours.addAll(stepBehaviours)
		context.observeAll(this)
	}

	companion object {
		fun create(scope: CoroutineScope, extraBehaviours: (AppContextProvider) -> List<TrackingChecklistBehaviourType> = { emptyList() }): TrackingChecklist {
			val initialBehaviours = listOf(ChecklistBaseBehaviour())
			val context = Context.create(
				initialState = TrackingChecklistState(),
				scope = scope,
				behaviours = initialBehaviours,
				name = "TrackingChecklist",
			)
			val checklist = TrackingChecklist(context, extraBehaviours)
			return checklist
		}
	}
}
