package dev.slimevr.tapdetection

import dev.slimevr.Phase1ContextProvider
import dev.slimevr.VRServer
import dev.slimevr.config.Settings
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.resets.ResetsManager
import dev.slimevr.tracker.Tracker
import kotlinx.coroutines.CoroutineScope
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.ResetType
import solarxr_protocol.rpc.TapDetectionSetupNotification
import kotlin.collections.listOf

data class TapDetectionState(
	val setupMode: Boolean = false,
	val yawResetBodyPart: BodyPart? = BodyPart.UPPER_CHEST,
	val fullResetBodyPart: BodyPart? = BodyPart.LEFT_UPPER_LEG,
	val mountingResetBodyPart: BodyPart? = BodyPart.RIGHT_UPPER_LEG,
)

sealed interface TapDetectionActions {
	data class SetSetupMode(val setupMode: Boolean) : TapDetectionActions
	data class UpdateBodyParts(val yawResetBodyPart: BodyPart?, val fullResetBodyPart: BodyPart?, val mountingResetBodyPart: BodyPart?) : TapDetectionActions
}

typealias TapDetectionContext = Context<TapDetectionState, TapDetectionActions>
typealias TapDetectionBehaviour = Behaviour<TapDetectionManager>

class TapDetectionManager(val context: TapDetectionContext, val server: VRServer, val resetsManager: ResetsManager, val settings: Settings) {
	fun startObserving() = context.observeAll(this)

	suspend fun actionTriggered(trackerId: Int) {
		val tracker = server.getTracker(trackerId) ?: return
		val state = context.state.value

		// If it's in setup mode, tap/button press to assign
		if (state.setupMode) {
			server.sendSolarxrRpc(
				TapDetectionSetupNotification(
					trackerId.toUShort(),
				),
			)
			return
		}

		val bodyPart = tracker.context.state.value.bodyPart ?: return
		val tapDetectionConfig = settings.context.state.value.data.tapDetectionConfig
		val (resetType, actionDelay) = when (bodyPart) {
			state.yawResetBodyPart if tapDetectionConfig.yawResetEnabled -> ResetType.YAW to tapDetectionConfig.yawResetDelay
			state.fullResetBodyPart if tapDetectionConfig.fullResetEnabled -> ResetType.FULL to tapDetectionConfig.fullResetDelay
			state.mountingResetBodyPart if tapDetectionConfig.mountingResetEnabled -> ResetType.MOUNTING to tapDetectionConfig.mountingResetDelay
			else -> return
		}

		resetsManager.scheduleReset(
			"TapDetection",
			resetType,
			actionDelay,
		)
	}

	companion object {
		fun create(ctx: Phase1ContextProvider, resetsManager: ResetsManager, scope: CoroutineScope): TapDetectionManager {
			val context = Context.create(
				initialState = TapDetectionState(),
				scope = scope,
				reducer = ::reduce,
				behaviours = listOf(TapDetectionBasicBehaviour()),
				name = "TapDetectionManager",
			)
			return TapDetectionManager(context, ctx.server, resetsManager, ctx.config.settings)
		}
	}
}
