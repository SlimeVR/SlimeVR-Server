package dev.slimevr.vrcosc

import dev.slimevr.config.Settings
import dev.slimevr.config.VRCOSCConfig
import dev.slimevr.logging.AppLogger
import dev.slimevr.osc.OscSender
import dev.slimevr.routing.BoneRoutingManager
import dev.slimevr.skeleton.BoneState
import dev.slimevr.skeleton.ComputedSkeleton
import dev.slimevr.skeleton.Skeleton
import dev.slimevr.util.formatExceptionMessage
import dev.slimevr.util.timeSource
import io.github.axisangles.ktmath.Quaternion
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.RoutingOutput
import solarxr_protocol.rpc.VRCOSCOutputState
import solarxr_protocol.rpc.VRCOSCTargetSource
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

private val FRAME_RETRY_DELAY = 2.seconds

// UDP reports a target with nothing listening through an ICMP refusal that surfaces on a later
// send and is cleared once read, so sends alternate between failing and succeeding. A single
// success is therefore no proof of recovery; require an uninterrupted run of them.
private val RECOVERY_CONFIRM_DELAY = 2.seconds

class VRCOSCOutputBehaviour(
	private val skeleton: Skeleton,
	private val settings: Settings,
	private val boneRouting: BoneRoutingManager,
) : VRCOSCBehaviour {
	private class OutputRuntime {
		var sender: OscSender? = null
		var sendFailing = false
		var nextFrameRetryAt: TimeSource.Monotonic.ValueTimeMark? = null
		var healthySince: TimeSource.Monotonic.ValueTimeMark? = null
	}

	override fun reduce(state: VRCOSCState, action: VRCOSCActions) = when (action) {
		is VRCOSCActions.SetOutput -> state.copy(
			status = state.status.copy(
				outputState = action.state,
				targetAddress = action.targetAddress,
				targetPort = action.targetPort,
				targetSource = action.targetSource,
				outputError = action.error,
			),
		)

		is VRCOSCActions.SetLastFrameSent -> state.copy(
			status = state.status.copy(lastFrameSentMillis = action.millis),
		)

		else -> state
	}

	override fun observe(receiver: VRCOSCManager) {
		val runtime = OutputRuntime()

		observeTargetChanges(receiver, runtime)
		observeFrames(receiver, runtime)
		observeYawAlign(receiver, runtime)
	}

	private fun observeTargetChanges(receiver: VRCOSCManager, runtime: OutputRuntime) {
		val configFlow = settings.context.state.map { it.data.vrcOscConfig }.distinctUntilChanged()
		val discoveredFlow = receiver.context.state.map { state -> state.status.discoveredTargets }.distinctUntilChanged()

		configFlow.combine(discoveredFlow) { config, discoveredTargets ->
			val (target, source) = resolveTarget(config, discoveredTargets)
			Triple(config.enabled, target, source)
		}
			.distinctUntilChanged()
			.onEach { (enabled, target, source) ->
				applyTarget(receiver, runtime, enabled, target, source)
			}
			.launchIn(receiver.context.scope)
	}

	private fun observeFrames(receiver: VRCOSCManager, runtime: OutputRuntime) {
		val routedBones = boneRouting.context.state
			.map { state -> state.routes.filterValues { RoutingOutput.VRC_OSC in it }.keys }
			.distinctUntilChanged()

		combine(skeleton.computed, routedBones, ::Pair)
			.onEach { (computedSkeleton, bones) -> sendFrame(receiver, runtime, computedSkeleton, bones) }
			.launchIn(receiver.context.scope)
	}

	private fun observeYawAlign(receiver: VRCOSCManager, runtime: OutputRuntime) {
		receiver.events.on<VRCOSCEvent.YawAlign> { event ->
			sendYawAlign(receiver, runtime, event.headRotation)
		}.launchIn(receiver.context.scope)
	}

	private suspend fun applyTarget(
		receiver: VRCOSCManager,
		runtime: OutputRuntime,
		enabled: Boolean,
		target: OscSenderTarget?,
		source: VRCOSCTargetSource,
	) {
		runtime.sender?.close()
		runtime.sender = null
		runtime.sendFailing = false
		runtime.nextFrameRetryAt = null
		runtime.healthySince = null

		if (!enabled || target == null) {
			receiver.context.dispatch(
				VRCOSCActions.SetOutput(
					state = VRCOSCOutputState.IDLE,
					targetAddress = target?.address,
					targetPort = target?.port,
					targetSource = source,
					error = null,
				),
			)
			return
		}

		val sender = try {
			OscSender(target.address, target.port).also {
				AppLogger.vrc.info("VRChat OSC output started: ${target.address}:${target.port}")
			}
		} catch (e: Exception) {
			dispatchOutputError(
				receiver = receiver,
				message = "Failed to create VRChat OSC sender",
				throwable = e,
				targetAddress = target.address,
				targetPort = target.port,
				targetSource = source,
			)
			return
		}

		runtime.sender = sender
		receiver.context.dispatch(
			VRCOSCActions.SetOutput(
				state = VRCOSCOutputState.IDLE,
				targetAddress = target.address,
				targetPort = target.port,
				targetSource = source,
				error = null,
			),
		)
	}

	private suspend fun sendFrame(
		receiver: VRCOSCManager,
		runtime: OutputRuntime,
		bones: ComputedSkeleton,
		routedBones: Set<BodyPart>,
	) {
		val sender = runtime.sender ?: return
		val state = receiver.context.state.value
		if (!settings.context.state.value.data.vrcOscConfig.enabled) return

		if (runtime.sendFailing && runtime.nextFrameRetryAt?.hasPassedNow() == false) return

		val bundle = buildOutgoingBundle(bones, routedBones) ?: return

		try {
			sender.send(bundle)
			receiver.context.dispatch(VRCOSCActions.SetLastFrameSent(System.currentTimeMillis()))
			runtime.sendFailing = false
			runtime.nextFrameRetryAt = null

			val healthySince = runtime.healthySince ?: timeSource.markNow().also { runtime.healthySince = it }
			if (state.status.outputState == VRCOSCOutputState.READY) return
			if (healthySince.elapsedNow() < RECOVERY_CONFIRM_DELAY) return

			AppLogger.vrc.info("VRChat OSC frames reaching ${state.status.targetAddress}:${state.status.targetPort}")
			receiver.context.dispatch(
				VRCOSCActions.SetOutput(
					state = VRCOSCOutputState.READY,
					targetAddress = state.status.targetAddress,
					targetPort = state.status.targetPort,
					targetSource = state.status.targetSource,
					error = null,
				),
			)
		} catch (e: Exception) {
			runtime.healthySince = null
			runtime.nextFrameRetryAt = timeSource.markNow() + FRAME_RETRY_DELAY
			runtime.sendFailing = true

			if (state.status.outputState == VRCOSCOutputState.ERROR) return

			dispatchOutputError(
				receiver = receiver,
				message = "Failed to send VRChat OSC frame",
				throwable = e,
				targetAddress = state.status.targetAddress,
				targetPort = state.status.targetPort,
				targetSource = state.status.targetSource,
			)
		}
	}

	private suspend fun sendYawAlign(
		receiver: VRCOSCManager,
		runtime: OutputRuntime,
		headRotation: Quaternion,
	) {
		val sender = runtime.sender ?: return
		try {
			sender.send(buildYawAlignMessage(headRotation))
		} catch (e: Exception) {
			val state = receiver.context.state.value
			dispatchOutputError(
				receiver = receiver,
				message = "Failed to send VRChat yaw align",
				throwable = e,
				targetAddress = state.status.targetAddress,
				targetPort = state.status.targetPort,
				targetSource = state.status.targetSource,
			)
		}
	}

	private suspend fun dispatchOutputError(
		receiver: VRCOSCManager,
		message: String,
		throwable: Throwable,
		targetAddress: String?,
		targetPort: Int?,
		targetSource: VRCOSCTargetSource,
	) {
		AppLogger.vrc.error(message, throwable)
		receiver.context.dispatch(
			VRCOSCActions.SetOutput(
				state = VRCOSCOutputState.ERROR,
				targetAddress = targetAddress,
				targetPort = targetPort,
				targetSource = targetSource,
				error = formatExceptionMessage(message, throwable),
			),
		)
	}

	private fun resolveTarget(
		config: VRCOSCConfig,
		discoveredTargets: List<VRCOSCDiscoveredTargetInfo>,
	): Pair<OscSenderTarget?, VRCOSCTargetSource> {
		if (config.useManualNetwork) {
			return OscSenderTarget(address = config.address, port = config.portOut) to VRCOSCTargetSource.MANUAL
		}

		val discoveredTarget = discoveredTargets.firstOrNull() ?: return null to VRCOSCTargetSource.NONE
		return OscSenderTarget(
			address = discoveredTarget.address,
			port = discoveredTarget.portOut,
		) to VRCOSCTargetSource.DISCOVERED
	}
}
