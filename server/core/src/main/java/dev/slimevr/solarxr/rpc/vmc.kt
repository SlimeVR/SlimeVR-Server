package dev.slimevr.solarxr.rpc

import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import dev.slimevr.vmc.VMCManager
import dev.slimevr.vmc.VMCStatus
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.sample
import solarxr_protocol.rpc.ChangeVMCOSCSettingsRequest
import solarxr_protocol.rpc.ChangeVRMSettingsRequest
import solarxr_protocol.rpc.VMCOSCSettingsRequest
import solarxr_protocol.rpc.VMCOSCSettingsResponse
import solarxr_protocol.rpc.VMCOSCStatusChangeResponse
import solarxr_protocol.rpc.VMCOSCStatusRequest
import solarxr_protocol.rpc.VRMSettingsRequest
import solarxr_protocol.rpc.VRMSettingsResponse

private const val VMC_STATUS_SAMPLE_MS = 300L

class VmcBehaviour(
	private val settings: Settings,
	private val vmcManager: VMCManager,
) : SolarXRBridgeBehaviour {
	@OptIn(FlowPreview::class)
	override fun observe(receiver: SolarXRBridge) {
		vmcManager.context.state
			.map { state -> state.status }
			.drop(1)
			.sample(VMC_STATUS_SAMPLE_MS)
			.onEach { status -> receiver.sendRpc(buildStatusResponse(status)) }
			.launchIn(receiver.context.scope)

		// Send VMC config
		receiver.rpcDispatcher.on<VMCOSCSettingsRequest> {
			val config = settings.context.state.value.data.vmcConfig
			receiver.sendRpc(
				VMCOSCSettingsResponse(
					enabled = config.enabled,
					portIn = config.portIn.toUShort(),
					portOut = config.portOut.toUShort(),
					address = config.address,
					anchorHip = config.anchorAtHips,
					mirrorTracking = config.mirrorTracking,
				),
			)
		}.launchIn(receiver.context.scope)

		receiver.rpcDispatcher.on<VMCOSCStatusRequest> {
			receiver.sendRpc(buildStatusResponse(vmcManager.context.state.value.status))
		}.launchIn(receiver.context.scope)

		// Send VRM json
		receiver.rpcDispatcher.on<VRMSettingsRequest> {
			val config = settings.context.state.value.data.vmcConfig
			receiver.sendRpc(
				VRMSettingsResponse(
					vrmJson = config.vrmJson,
				),
			)
		}.launchIn(receiver.context.scope)

		// Receive VMC config
		receiver.rpcDispatcher.on<ChangeVMCOSCSettingsRequest> { req ->
			settings.context.dispatch(
				SettingsActions.Update {
					copy(
						vmcConfig = vmcConfig.copy(
							enabled = req.enabled,
							portIn = req.portIn.takeIf { it > 0u }?.toInt() ?: vmcConfig.portIn,
							portOut = req.portOut.takeIf { it > 0u }?.toInt() ?: vmcConfig.portOut,
							address = req.address ?: vmcConfig.address,
							mirrorTracking = req.mirrorTracking,
							anchorAtHips = req.anchorHip,
						),
					)
				},
			)
		}.launchIn(receiver.context.scope)

		// Receive VRM json
		receiver.rpcDispatcher.on<ChangeVRMSettingsRequest> { req ->
			settings.context.dispatch(
				SettingsActions.Update {
					copy(
						vmcConfig = vmcConfig.copy(vrmJson = req.vrmJson),
					)
				},
			)
		}.launchIn(receiver.context.scope)
	}

	private fun buildStatusResponse(status: VMCStatus) = VMCOSCStatusChangeResponse(
		inputState = status.inputState,
		inputPort = status.inputPort?.toUShort(),
		inputError = status.inputError,
		lastReceivedInputMillis = status.lastReceivedInputMillis?.toULong(),
		outputState = status.outputState,
		outputError = status.outputError,
		targetAddress = status.targetAddress,
		targetPort = status.targetPort?.toUShort(),
		lastFrameSentMillis = status.lastFrameSentMillis?.toULong(),
		vrmState = status.vrmState,
		vrmError = status.vrmError,
	)
}
