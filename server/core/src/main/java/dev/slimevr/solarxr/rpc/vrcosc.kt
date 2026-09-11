package dev.slimevr.solarxr.rpc

import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import dev.slimevr.config.VRCOSCConfig
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import dev.slimevr.vrcosc.VRCOSCManager
import dev.slimevr.vrcosc.VRCOSCStatus
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.sample
import solarxr_protocol.rpc.ChangeVRCOSCSettingsRequest
import solarxr_protocol.rpc.VRCOSCSettingsRequest
import solarxr_protocol.rpc.VRCOSCSettingsResponse
import solarxr_protocol.rpc.VRCOSCStatusChangeResponse
import solarxr_protocol.rpc.VRCOSCStatusRequest
import solarxr_protocol.rpc.VRCOSCDiscoveredTarget as RpcVRCOSCDiscoveredTarget

private const val VRC_STATUS_SAMPLE_MS = 300L

internal class VrcOscBehaviour(
	private val settings: Settings,
	private val vrcOscManager: VRCOSCManager,
) : SolarXRBridgeBehaviour {
	@OptIn(FlowPreview::class)
	override fun observe(receiver: SolarXRBridge) {
		// Rate-limit status pushes so brief output-state flips (e.g. a single
		// successful retry packet between two failures) do not reach the GUI.
		// `sample` emits at most one value per window without starving when
		// updates keep coming faster than the window.
		vrcOscManager.context.state
			.map { state -> state.status }
			.drop(1)
			.sample(VRC_STATUS_SAMPLE_MS)
			.onEach { status -> receiver.sendRpc(buildStatusResponse(status)) }
			.launchIn(receiver.context.scope)

		receiver.rpcDispatcher.on<VRCOSCSettingsRequest> {
			val config = settings.context.state.value.data.vrcOscConfig
			receiver.sendRpc(
				VRCOSCSettingsResponse(
					enabled = config.enabled,
					useManualNetwork = config.useManualNetwork,
					portIn = config.portIn.toUShort(),
					portOut = config.portOut.toUShort(),
					address = config.address,
				),
			)
		}.launchIn(receiver.context.scope)

		receiver.rpcDispatcher.on<VRCOSCStatusRequest> {
			receiver.sendRpc(buildStatusResponse(vrcOscManager.context.state.value.status))
		}.launchIn(receiver.context.scope)

		receiver.rpcDispatcher.on<ChangeVRCOSCSettingsRequest> { req ->
			settings.context.dispatch(
				SettingsActions.Update {
					copy(
						vrcOscConfig = VRCOSCConfig(
							enabled = req.enabled,
							useManualNetwork = req.useManualNetwork,
							portIn = req.portIn.takeIf { it > 0u }?.toInt() ?: vrcOscConfig.portIn,
							portOut = req.portOut.takeIf { it > 0u }?.toInt() ?: vrcOscConfig.portOut,
							address = req.address ?: vrcOscConfig.address,
						),
					)
				},
			)
		}.launchIn(receiver.context.scope)
	}

	private fun buildStatusResponse(status: VRCOSCStatus) = VRCOSCStatusChangeResponse(
		inputState = status.inputState,
		inputPort = status.inputPort?.toUShort(),
		inputError = status.inputError,
		lastReceivedInputMillis = status.lastReceivedInputMillis?.toULong(),
		outputState = status.outputState,
		outputError = status.outputError,
		targetAddress = status.targetAddress,
		targetPort = status.targetPort?.toUShort(),
		targetSource = status.targetSource,
		lastFrameSentMillis = status.lastFrameSentMillis?.toULong(),
		oscqueryState = status.oscQueryState,
		oscqueryAdvertisedPort = status.oscQueryAdvertisedPort?.toUShort(),
		oscqueryError = status.oscQueryError,
		discoveredTargets = status.discoveredTargets.map { target ->
			RpcVRCOSCDiscoveredTarget(name = target.name, address = target.address, portOut = target.portOut.toUShort())
		},
	)
}
