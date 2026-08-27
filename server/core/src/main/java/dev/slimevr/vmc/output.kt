package dev.slimevr.vmc

import dev.slimevr.config.Settings
import dev.slimevr.config.VMCConfig
import dev.slimevr.logging.AppLogger
import dev.slimevr.osc.OscSender
import dev.slimevr.routing.BoneRoutingManager
import dev.slimevr.skeleton.ComputedSkeleton
import dev.slimevr.skeleton.Skeleton
import dev.slimevr.util.formatExceptionMessage
import dev.slimevr.util.timeSource
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.RoutingOutput
import solarxr_protocol.rpc.VMCOSCOutputState
import solarxr_protocol.rpc.VMCOSCVrmState
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

private val FRAME_RETRY_DELAY = 2.seconds

private val RECOVERY_CONFIRM_DELAY = 2.seconds

class VMCOutputBehaviour(
	private val skeleton: Skeleton,
	private val settings: Settings,
	private val boneRouting: BoneRoutingManager,
) : VMCBehaviour {
	private class OutputRuntime {
		var sender: OscSender? = null
		var vrm: VrmGeometry? = null
		var sendFailing = false
		var nextFrameRetryAt: TimeSource.Monotonic.ValueTimeMark? = null

		/** Start of the current unbroken run of successful sends, null while sends are failing. */
		var healthySince: TimeSource.Monotonic.ValueTimeMark? = null
	}

	override fun observe(receiver: VMCManager) {
		val runtime = OutputRuntime()

		observeVrm(receiver, runtime)
		observeTargetChanges(receiver, runtime)
		observeFrames(receiver, runtime)
	}

	private fun observeVrm(receiver: VMCManager, runtime: OutputRuntime) {
		settings.context.state
			.map { it.data.vmcConfig.vrmJson }
			.distinctUntilChanged()
			.onEach { vrmJson ->
				val json = vrmJson?.takeIf { it.isNotEmpty() }
				if (json == null) {
					runtime.vrm = null
					receiver.context.dispatch(VMCActions.SetVrm(state = VMCOSCVrmState.NONE))
					return@onEach
				}

				try {
					runtime.vrm = buildVrmGeometry(VrmReader(json))
					receiver.context.dispatch(VMCActions.SetVrm(state = VMCOSCVrmState.LOADED))
				} catch (e: Exception) {
					runtime.vrm = null
					val message = "Failed to parse VRM JSON"
					AppLogger.vmc.error(message, e)
					receiver.context.dispatch(
						VMCActions.SetVrm(state = VMCOSCVrmState.ERROR, error = formatExceptionMessage(message, e)),
					)
				}
			}
			.launchIn(receiver.context.scope)
	}

	private fun observeTargetChanges(receiver: VMCManager, runtime: OutputRuntime) {
		settings.context.state
			.map { Triple(it.data.vmcConfig.enabled, it.data.vmcConfig.address, it.data.vmcConfig.portOut) }
			.distinctUntilChanged()
			.onEach { (enabled, address, port) -> applyTarget(receiver, runtime, enabled, address, port) }
			.launchIn(receiver.context.scope)
	}

	private fun observeFrames(receiver: VMCManager, runtime: OutputRuntime) {
		val startedAt = timeSource.markNow()

		val routedBones = boneRouting.context.state
			.map { state -> state.routes.filterValues { RoutingOutput.VMC in it }.keys }
			.distinctUntilChanged()

		val config = settings.context.state
			.map { it.data.vmcConfig }
			.distinctUntilChanged()

		combine(skeleton.computed, routedBones, config, ::Triple)
			.onEach { (computedSkeleton, bones, vmcConfig) ->
				sendFrame(receiver, runtime, computedSkeleton, bones, vmcConfig, startedAt)
			}
			.launchIn(receiver.context.scope)
	}

	private suspend fun applyTarget(
		receiver: VMCManager,
		runtime: OutputRuntime,
		enabled: Boolean,
		address: String,
		port: Int,
	) {
		runtime.sender?.close()
		runtime.sender = null
		runtime.sendFailing = false
		runtime.nextFrameRetryAt = null
		runtime.healthySince = null

		if (!enabled) {
			receiver.context.dispatch(VMCActions.SetOutput(state = VMCOSCOutputState.IDLE))
			return
		}

		val sender = try {
			OscSender(address, port).also {
				AppLogger.vmc.info("VMC output started: $address:$port")
			}
		} catch (e: Exception) {
			dispatchOutputError(
				receiver = receiver,
				message = "Failed to create VMC sender",
				throwable = e,
				targetAddress = address,
				targetPort = port,
			)
			return
		}

		runtime.sender = sender
		// Opening a UDP socket proves nothing about the target, so the output stays idle until a
		// frame has actually gone out without error.
		receiver.context.dispatch(
			VMCActions.SetOutput(state = VMCOSCOutputState.IDLE, targetAddress = address, targetPort = port),
		)

		receiver.context.scope.launch {
			try {
				sender.send(buildInitRequestMessage())
			} catch (e: Exception) {
				dispatchOutputError(
					receiver = receiver,
					message = "Failed to send VMC init request",
					throwable = e,
					targetAddress = address,
					targetPort = port,
				)
			}
		}
	}

	private suspend fun sendFrame(
		receiver: VMCManager,
		runtime: OutputRuntime,
		bones: ComputedSkeleton,
		routedBones: Set<BodyPart>,
		config: VMCConfig,
		startedAt: TimeSource.Monotonic.ValueTimeMark,
	) {
		val sender = runtime.sender ?: return
		val status = receiver.context.state.value.status
		if (runtime.sendFailing && runtime.nextFrameRetryAt?.hasPassedNow() == false) return

		val bundle = buildOutgoingBundle(bones, routedBones, config, runtime.vrm, startedAt.elapsedNow())

		try {
			sender.send(bundle)
			receiver.context.dispatch(VMCActions.SetLastFrameSent(System.currentTimeMillis()))
			runtime.sendFailing = false
			runtime.nextFrameRetryAt = null

			val healthySince = runtime.healthySince ?: timeSource.markNow().also { runtime.healthySince = it }
			if (status.outputState == VMCOSCOutputState.READY) return
			if (healthySince.elapsedNow() < RECOVERY_CONFIRM_DELAY) return

			AppLogger.vmc.info("VMC frames reaching ${status.targetAddress}:${status.targetPort}")
			receiver.context.dispatch(
				VMCActions.SetOutput(
					state = VMCOSCOutputState.READY,
					targetAddress = status.targetAddress,
					targetPort = status.targetPort,
				),
			)
		} catch (e: Exception) {
			runtime.healthySince = null
			runtime.nextFrameRetryAt = timeSource.markNow() + FRAME_RETRY_DELAY
			runtime.sendFailing = true
			sender.close()
			// Report once per transition. The refusal is only raised every other send, so gating
			// on the previous attempt instead would log again on each one that fails.
			if (status.outputState == VMCOSCOutputState.ERROR) return

			dispatchOutputError(
				receiver = receiver,
				message = "Failed to send VMC frame",
				throwable = e,
				targetAddress = status.targetAddress,
				targetPort = status.targetPort,
			)
		}
	}

	private suspend fun dispatchOutputError(
		receiver: VMCManager,
		message: String,
		throwable: Throwable,
		targetAddress: String?,
		targetPort: Int?,
	) {
		AppLogger.vmc.error(message, throwable)
		receiver.context.dispatch(
			VMCActions.SetOutput(
				state = VMCOSCOutputState.ERROR,
				targetAddress = targetAddress,
				targetPort = targetPort,
				error = formatExceptionMessage(message, throwable),
			),
		)
	}
}
