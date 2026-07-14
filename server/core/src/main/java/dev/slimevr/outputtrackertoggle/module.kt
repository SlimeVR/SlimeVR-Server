package dev.slimevr.outputtrackertoggle

import dev.slimevr.Phase1ContextProvider
import dev.slimevr.VRServer
import dev.slimevr.config.Settings
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import kotlinx.coroutines.CoroutineScope
import solarxr_protocol.datatypes.BodyPart
import kotlin.collections.listOf

data class OutputTrackerToggleState(
	/**
	 * The output trackers should be on the tail of the present BodyParts
	 * Example: for the SteamVR waist tracker, since it uses the hip's rotation and tail position,
	 * we check for BodyPart.HIP.
	 */
	val trackers: List<BodyPart>,
)

sealed interface OutputTrackerToggleActions {
	data class SetOutputTrackers(val trackers: List<BodyPart>) : OutputTrackerToggleActions
}

typealias OutputTrackerToggleContext = Context<OutputTrackerToggleState, OutputTrackerToggleActions>
typealias OutputTrackerToggleBehaviour = Behaviour<OutputTrackerToggleState, OutputTrackerToggleActions, OutputTrackerToggleManager>

class OutputTrackerToggleManager(val context: OutputTrackerToggleContext, val server: VRServer, val settings: Settings) {
	fun startObserving() = context.observeAll(this)

	companion object {
		fun create(ctx: Phase1ContextProvider, scope: CoroutineScope): OutputTrackerToggleManager {
			val context = Context.create(
				initialState = OutputTrackerToggleState(
					trackers = listOf(),
				),
				scope = scope,
				behaviours = listOf(OutputTrackerToggleBasicBehaviour()),
				name = "OutputTrackerToggleManager",
			)
			return OutputTrackerToggleManager(context, ctx.server, ctx.config.settings)
		}
	}
}
