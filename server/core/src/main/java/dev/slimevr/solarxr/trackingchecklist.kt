package dev.slimevr.solarxr

import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import dev.slimevr.trackingchecklist.TrackingChecklist
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.rpc.IgnoreTrackingChecklistStepRequest
import solarxr_protocol.rpc.TrackingChecklistRequest
import solarxr_protocol.rpc.TrackingChecklistResponse
import solarxr_protocol.rpc.TrackingChecklistStepId

class TrackingChecklistBehaviour(
    private val checklist: TrackingChecklist,
    private val settings: Settings,
) : SolarXRBridgeBehaviour {

    private fun parseMutedSteps(): Set<TrackingChecklistStepId> =
        settings.context.state.value.data.mutedChecklistSteps
            .mapNotNull { name -> TrackingChecklistStepId.entries.firstOrNull { stepId -> stepId.name == name } }
            .toSet()

    private fun buildResponse(): TrackingChecklistResponse {
        val steps = checklist.context.state.value.steps.mapValues { it.value.copy(id = it.key) }.values.toList()
        return TrackingChecklistResponse(
            steps = steps,
            ignoredSteps = parseMutedSteps().toList(),
        )
    }

    override fun observe(receiver: SolarXRBridge) {
        combine(
            checklist.context.state,
            settings.context.state.map { state -> state.data.mutedChecklistSteps },
        ) { _, _ -> buildResponse() }
            .onEach { response -> receiver.sendRpc(response) }
            .launchIn(receiver.context.scope)

        receiver.rpcDispatcher.on<TrackingChecklistRequest> {
            receiver.sendRpc(buildResponse())
        }

        receiver.rpcDispatcher.on<IgnoreTrackingChecklistStepRequest> { req ->
            val stepId = req.stepId ?: return@on
            val name = stepId.name
            settings.context.dispatch(SettingsActions.Update {
                copy(mutedChecklistSteps = if (req.ignore) mutedChecklistSteps + name else mutedChecklistSteps - name)
            })
        }
    }
}
