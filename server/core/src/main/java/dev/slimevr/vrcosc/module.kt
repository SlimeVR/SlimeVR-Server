package dev.slimevr.vrcosc

import dev.slimevr.EventDispatcher
import dev.slimevr.Phase1ContextProvider
import dev.slimevr.config.VRCOSCConfig
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.skeleton.Skeleton
import io.github.axisangles.ktmath.Quaternion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import solarxr_protocol.rpc.VRCOSCInputState
import solarxr_protocol.rpc.VRCOSCOscQueryState
import solarxr_protocol.rpc.VRCOSCOutputState
import solarxr_protocol.rpc.VRCOSCTargetSource

data class OscSenderTarget(
	val address: String,
	val port: Int,
)

data class VRCOSCDiscoveredTargetInfo(
	val name: String,
	val address: String,
	val portOut: Int,
)

/**
 * Internal runtime status for the VRC OSC subsystem.
 * Kept close to the SolarXR status response shape so the bridge mapping stays small,
 * but this remains a server-side state model rather than an RPC transport type.
 */
data class VRCOSCStatus(
	val inputState: VRCOSCInputState = VRCOSCInputState.IDLE,
	val inputPort: Int? = null,
	val inputError: String? = null,
	val lastReceivedInputMillis: Long? = null,

	val outputState: VRCOSCOutputState = VRCOSCOutputState.IDLE,
	val outputError: String? = null,
	val targetAddress: String? = null,
	val targetPort: Int? = null,
	val targetSource: VRCOSCTargetSource = VRCOSCTargetSource.NONE,
	val lastFrameSentMillis: Long? = null,

	val oscQueryState: VRCOSCOscQueryState = VRCOSCOscQueryState.DISABLED,
	val oscQueryAdvertisedPort: Int? = null,
	val oscQueryError: String? = null,
	val discoveredTargets: List<VRCOSCDiscoveredTargetInfo> = emptyList(),
)

data class VRCOSCState(
	val config: VRCOSCConfig = VRCOSCConfig(),
	val status: VRCOSCStatus = VRCOSCStatus(),
)

enum class VRSystemTracker {
	HEAD,
	LEFT_WRIST,
	RIGHT_WRIST,
}

sealed interface VRCOSCActions {
	data class UpdateConfig(val config: VRCOSCConfig) : VRCOSCActions

	data class SetInput(
		val state: VRCOSCInputState,
		val port: Int? = null,
		val error: String? = null,
	) : VRCOSCActions
	data class SetLastReceivedInput(val millis: Long) : VRCOSCActions

	data class SetOutput(
		val state: VRCOSCOutputState,
		val targetAddress: String? = null,
		val targetPort: Int? = null,
		val targetSource: VRCOSCTargetSource = VRCOSCTargetSource.NONE,
		val error: String? = null,
	) : VRCOSCActions
	data class SetLastFrameSent(val millis: Long) : VRCOSCActions

	data class SetOscQuery(
		val state: VRCOSCOscQueryState,
		val advertisedPort: Int? = null,
		val error: String? = null,
	) : VRCOSCActions
	data class SetDiscoveredTargets(val targets: List<VRCOSCDiscoveredTargetInfo>) : VRCOSCActions
}

sealed interface VRCOSCEvent {
	data class YawAlign(val headRotation: Quaternion) : VRCOSCEvent
}

typealias VRCOSCContext = Context<VRCOSCState, VRCOSCActions>
typealias VRCOSCBehaviour = Behaviour<VRCOSCState, VRCOSCActions, VRCOSCManager>

class VRCOSCManager(
	val context: VRCOSCContext,
	val events: EventDispatcher<VRCOSCEvent> = EventDispatcher(),
) {
	fun startObserving() = context.observeAll(this)

	fun yawAlign(headRotation: Quaternion) {
		context.scope.launch {
			events.emit(VRCOSCEvent.YawAlign(headRotation))
		}
	}

	companion object {
		fun create(skeleton: Skeleton, ctx: Phase1ContextProvider, scope: CoroutineScope): VRCOSCManager {
			val settings = ctx.config.settings
			val initialConfig = settings.context.state.value.data.vrcOscConfig
			val context = Context.create(
				initialState = VRCOSCState(
					config = initialConfig,
					status = VRCOSCStatus(),
				),
				scope = scope,
				behaviours = listOf(
					VRCOSCSettingsBehaviour(settings),
					VRCOSCOutputBehaviour(skeleton),
					VRCOSCInputBehaviour(ctx),
					VRCOSCOscQueryBehaviour(),
				),
				name = "VRCOSC",
			)
			return VRCOSCManager(context)
		}
	}
}
