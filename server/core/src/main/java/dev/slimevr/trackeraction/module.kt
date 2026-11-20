package dev.slimevr.trackeraction

import dev.slimevr.Phase1ContextProvider
import dev.slimevr.VRServer
import dev.slimevr.config.Settings
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.resets.ResetsManager
import kotlinx.coroutines.CoroutineScope
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.TrackerActionMethod
import solarxr_protocol.rpc.ResetType
import solarxr_protocol.rpc.TrackerActionSetupNotification
import kotlin.collections.listOf

data class TrackerActionState(
	val setupMode: Boolean = false,
	val yawResetBodyPart: BodyPart? = BodyPart.UPPER_CHEST,
	val fullResetBodyPart: BodyPart? = BodyPart.LEFT_UPPER_LEG,
	val mountingResetBodyPart: BodyPart? = BodyPart.RIGHT_UPPER_LEG,
)

sealed interface TrackerActionActions {
	data class SetSetupMode(val setupMode: Boolean) : TrackerActionActions
	data class UpdateBodyParts(val yawResetBodyPart: BodyPart?, val fullResetBodyPart: BodyPart?, val mountingResetBodyPart: BodyPart?) : TrackerActionActions
}

typealias TrackerActionContext = Context<TrackerActionState, TrackerActionActions>
typealias TrackerActionBehaviour = Behaviour<TrackerActionManager>

class TrackerActionManager(val context: TrackerActionContext, val server: VRServer, val resetsManager: ResetsManager, val settings: Settings) {
	fun startObserving() = context.observeAll(this)

	suspend fun actionTriggered(trackerId: Int, sourceName: String, sourceMethod: TrackerActionMethod) {
		val tracker = server.getTracker(trackerId) ?: return
		val state = context.state.value

		// If it's in setup mode, tap/button press to assign
		if (state.setupMode) {
			server.sendSolarxrRpc(
				TrackerActionSetupNotification(
					trackerId.toUShort(),
				),
			)
			return
		}

		val bodyPart = tracker.context.state.value.bodyPart ?: return
		val trackerActionConfig = settings.context.state.value.data.trackerActionConfig
		val (resetType, actionDelay) = when (bodyPart) {
			state.yawResetBodyPart if trackerActionConfig.yawResetAction.enabled -> ResetType.YAW to trackerActionConfig.yawResetAction.delay
			state.fullResetBodyPart if trackerActionConfig.fullResetAction.enabled -> ResetType.FULL to trackerActionConfig.fullResetAction.delay
			state.mountingResetBodyPart if trackerActionConfig.mountingResetAction.enabled -> ResetType.MOUNTING to trackerActionConfig.mountingResetAction.delay
			else -> return
		}

		val method = when (resetType) {
			ResetType.YAW -> trackerActionConfig.yawResetAction.method
			ResetType.FULL -> trackerActionConfig.fullResetAction.method
			ResetType.MOUNTING -> trackerActionConfig.mountingResetAction.method
		}

		if (method == sourceMethod || method == TrackerActionMethod.BOTH) {
			resetsManager.scheduleReset(
				"TrackerAction ($sourceName)",
				resetType,
				actionDelay,
			)
		}
	}
	companion object {
		fun create(ctx: Phase1ContextProvider, resetsManager: ResetsManager, scope: CoroutineScope): TrackerActionManager {
			val context = Context.create(
				initialState = TrackerActionState(),
				scope = scope,
				reducer = ::reduce,
				behaviours = listOf(TapDetectionBehaviour()),
				name = "TrackerActionManager",
			)
			return TrackerActionManager(context, ctx.server, resetsManager, ctx.config.settings)
		}
	}
}
