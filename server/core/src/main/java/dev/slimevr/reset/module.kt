package dev.slimevr.reset

import dev.slimevr.Phase1ContextProvider
import dev.slimevr.VRServer
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.util.safeLaunch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.ResetType
import solarxr_protocol.rpc.UnknownDeviceHandshakeNotification
import kotlin.collections.listOf

data class ResetState(
	val canDoYawReset: Boolean,
	val canDoMountingReset: Boolean
)

sealed interface ResetActions {
	data class ClearResets(val resetType: List<ResetType>) : ResetActions // sets to false
	data class EndReset(val resetType: ResetType) : ResetActions // sets to true if full reset
}

typealias ResetContext = Context<ResetState, ResetActions>
typealias ResetBehaviour = Behaviour<ResetState, ResetActions, ResetManager>

class ResetManager(val context: ResetContext, val server: VRServer) {
	fun startObserving() = context.observeAll(this)

	private var resetJob: Job = Job()

	suspend fun scheduleFullReset(resetType: ResetType, status: Int, bodyParts: List<BodyPart>? = null, progress: Int = 0, duration: Int = 0) {
		resetJob.cancelAndJoin()
		resetJob = context.scope.safeLaunch {
			server.context.state.value.solarxr.values.forEach { bridge ->
				//bridge.sendRpc((macAddress = mac))
			}
			//server.context.dispatch()
		}
	}

	companion object {
		fun create(ctx: Phase1ContextProvider, scope: CoroutineScope): ResetManager {
			val context = Context.create(
				initialState = ResetState(
					canDoYawReset = false,
					canDoMountingReset = false
				),
				scope = scope,
				behaviours = listOf<ResetBehaviour>(),
				name = "ResetManager",
			)
			return ResetManager(context, ctx.server)
		}
	}
}
