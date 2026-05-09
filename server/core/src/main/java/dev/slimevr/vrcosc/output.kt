package dev.slimevr.vrcosc

import dev.slimevr.AppLogger
import dev.slimevr.config.VRCOSCConfig
import dev.slimevr.osc.OscArg
import dev.slimevr.osc.OscBundle
import dev.slimevr.osc.OscContent
import dev.slimevr.osc.OscMessage
import dev.slimevr.osc.OscSender
import dev.slimevr.skeleton.BoneState
import dev.slimevr.skeleton.Skeleton
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.VRCOSCOutputState
import solarxr_protocol.rpc.VRCOSCTargetSource
import kotlin.math.PI

private const val FRAME_RETRY_DELAY_MS = 5_000L

class VRCOSCOutputBehaviour(
	private val skeleton: Skeleton,
) : VRCOSCBehaviour {
	private class OutputRuntime {
		var sender: OscSender? = null
		var frameSendFailureActive = false
		var nextFrameRetryAt = 0L
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

		val configFlow = receiver.context.state.map { state -> state.config }.distinctUntilChanged()
		val discoveredFlow = receiver.context.state.map { state -> state.status.discoveredTargets }.distinctUntilChanged()

		configFlow.combine(discoveredFlow) { config, discoveredTargets ->
			Triple(
				config.enabled,
				resolveTarget(config, discoveredTargets),
				resolveTargetSource(config, discoveredTargets),
			)
		}
			.distinctUntilChanged()
			.onEach { (enabled, target, source) ->
				applyTarget(receiver, runtime, enabled, target, source)
			}
			.launchIn(receiver.context.scope)

		skeleton.computed
			.onEach { bones -> sendFrame(receiver, runtime, bones) }
			.launchIn(receiver.context.scope)

		receiver.events.on<VRCOSCEvent.YawAlign> { event ->
			sendYawAlign(receiver, runtime, event.headRotation)
		}
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
		runtime.frameSendFailureActive = false
		runtime.nextFrameRetryAt = 0L

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
				state = VRCOSCOutputState.READY,
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
		bones: Map<BodyPart, BoneState>,
	) {
		val sender = runtime.sender ?: return
		val state = receiver.context.state.value
		if (!state.config.enabled) return

		val now = System.currentTimeMillis()
		if (runtime.frameSendFailureActive && now < runtime.nextFrameRetryAt) return

		val bundle = buildOutgoingBundle(bones, state.config) ?: return

		try {
			sender.send(bundle)
			receiver.context.dispatch(VRCOSCActions.SetLastFrameSent(System.currentTimeMillis()))
			if (runtime.frameSendFailureActive) {
				runtime.frameSendFailureActive = false
				runtime.nextFrameRetryAt = 0L
				AppLogger.vrc.info("VRChat OSC frame send recovered")
				receiver.context.dispatch(
					VRCOSCActions.SetOutput(
						state = VRCOSCOutputState.READY,
						targetAddress = state.status.targetAddress,
						targetPort = state.status.targetPort,
						targetSource = state.status.targetSource,
						error = null,
					),
				)
			}
		} catch (e: Exception) {
			runtime.nextFrameRetryAt = System.currentTimeMillis() + FRAME_RETRY_DELAY_MS
			if (runtime.frameSendFailureActive) return

			runtime.frameSendFailureActive = true
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
	): OscSenderTarget? {
		config.manualNetwork?.let { manual ->
			return OscSenderTarget(address = manual.address, port = manual.portOut)
		}

		val discoveredTarget = discoveredTargets.firstOrNull() ?: return null
		return OscSenderTarget(
			address = discoveredTarget.address,
			port = discoveredTarget.portOut,
		)
	}

	private fun resolveTargetSource(
		config: VRCOSCConfig,
		discoveredTargets: List<VRCOSCDiscoveredTargetInfo>,
	): VRCOSCTargetSource = when {
		config.manualNetwork != null -> VRCOSCTargetSource.MANUAL
		discoveredTargets.isNotEmpty() -> VRCOSCTargetSource.DISCOVERED
		else -> VRCOSCTargetSource.NONE
	}

	private fun buildOutgoingBundle(
		bones: Map<BodyPart, BoneState>,
		config: VRCOSCConfig,
	): OscBundle? {
		val messages = buildList {
			for ((bodyPart, trackerId) in trackerIdsByBodyPart) {
				if (!shouldSendTracker(bodyPart, config)) continue

				val bone = bones[bodyPart] ?: continue
				add(
					OscContent.Message(
						positionMessage("/tracking/trackers/$trackerId/position", bone.headPosition),
					),
				)
				add(
					OscContent.Message(
						rotationMessage("/tracking/trackers/$trackerId/rotation", bone.rotation),
					),
				)
			}

			bones[BodyPart.HEAD]?.let { head ->
				add(
					OscContent.Message(
						positionMessage("/tracking/trackers/head/position", head.headPosition),
					),
				)
			}
		}

		if (messages.isEmpty()) return null
		return OscBundle(1L, messages)
	}

	private fun shouldSendTracker(bodyPart: BodyPart, config: VRCOSCConfig): Boolean = when (bodyPart) {
		BodyPart.HIP -> config.trackers.waist
		BodyPart.LEFT_FOOT, BodyPart.RIGHT_FOOT -> config.trackers.feet
		BodyPart.LEFT_UPPER_LEG, BodyPart.RIGHT_UPPER_LEG -> config.trackers.knees
		BodyPart.UPPER_CHEST -> config.trackers.chest
		BodyPart.LEFT_UPPER_ARM, BodyPart.RIGHT_UPPER_ARM -> config.trackers.elbows
		else -> false
	}

	private fun positionMessage(address: String, position: Vector3) = OscMessage(
		address,
		listOf(
			OscArg.Float(position.x),
			OscArg.Float(position.y),
			OscArg.Float(-position.z),
		),
	)

	private fun rotationMessage(address: String, rotation: Quaternion): OscMessage {
		val (_, x, y, z) = Quaternion(rotation.w, -rotation.x, -rotation.y, rotation.z)
			.toEulerAngles(EulerOrder.YXZ)
		return OscMessage(
			address,
			listOf(
				OscArg.Float(x * 180f / PI.toFloat()),
				OscArg.Float(y * 180f / PI.toFloat()),
				OscArg.Float(z * 180f / PI.toFloat()),
			),
		)
	}

	private fun buildYawAlignMessage(headRotation: Quaternion): OscMessage {
		val (_, _, yaw, _) = headRotation.toEulerAngles(EulerOrder.YXZ)
		return OscMessage(
			"/tracking/trackers/head/rotation",
			listOf(
				OscArg.Float(0f),
				OscArg.Float(-yaw * 180f / PI.toFloat()),
				OscArg.Float(0f),
			),
		)
	}

	private companion object {
		private val trackerIdsByBodyPart = mapOf(
			BodyPart.HIP to 1,
			BodyPart.LEFT_FOOT to 2,
			BodyPart.RIGHT_FOOT to 3,
			BodyPart.LEFT_UPPER_LEG to 4,
			BodyPart.RIGHT_UPPER_LEG to 5,
			BodyPart.UPPER_CHEST to 6,
			BodyPart.LEFT_UPPER_ARM to 7,
			BodyPart.RIGHT_UPPER_ARM to 8,
		)
	}
}
