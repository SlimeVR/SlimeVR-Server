package dev.slimevr.solarxr

import dev.slimevr.config.VRCOSCConfig
import dev.slimevr.config.VRCOSCManualNetworkConfig
import dev.slimevr.vrcosc.VRCOSCActions
import dev.slimevr.vrcosc.VRCOSCManager
import dev.slimevr.vrcosc.VRCOSCStatus
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.sample
import solarxr_protocol.rpc.ChangeVRCOSCSettingsRequest
import solarxr_protocol.rpc.VRCOSCNetworkSettings
import solarxr_protocol.rpc.VRCOSCSettingsRequest
import solarxr_protocol.rpc.VRCOSCSettingsResponse
import solarxr_protocol.rpc.VRCOSCStatusChangeResponse
import solarxr_protocol.rpc.VRCOSCStatusRequest
import solarxr_protocol.rpc.VRCOSCDiscoveredTarget as RpcVRCOSCDiscoveredTarget

private const val STATUS_SAMPLE_MS = 300L

internal class VrcOscBehaviour(
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
			.sample(STATUS_SAMPLE_MS)
			.onEach { status -> receiver.sendRpc(buildStatusResponse(status, vrcOscManager.context.state.value.config.enabled)) }
			.launchIn(receiver.context.scope)

		receiver.rpcDispatcher.on<VRCOSCSettingsRequest> {
			receiver.sendRpc(buildVrcOscSettings(vrcOscManager.context.state.value.config))
		}

		receiver.rpcDispatcher.on<VRCOSCStatusRequest> {
			val state = vrcOscManager.context.state.value
			receiver.sendRpc(buildStatusResponse(state.status, state.config.enabled))
		}

		receiver.rpcDispatcher.on<ChangeVRCOSCSettingsRequest> { req ->
			vrcOscManager.context.dispatch(
				VRCOSCActions.UpdateConfig(
					VRCOSCConfig(
						enabled = req.enabled == true,
						manualNetwork = req.manualNetwork?.takeIf { it.portIn !== null && it.portOut !== null && it.address !== null }?.let { network ->
							VRCOSCManualNetworkConfig(
								portIn = network.portIn?.toInt() ?: error("portIn should be set"),
								portOut = network.portOut?.toInt() ?: error("portOut should be set"),
								address = network.address ?: error("address should be set"),
							)
						},
					),
				),
			)
		}
	}

	private fun buildStatusResponse(status: VRCOSCStatus, enabled: Boolean) = VRCOSCStatusChangeResponse(
		enabled = enabled,
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

	private fun buildVrcOscSettings(config: VRCOSCConfig): VRCOSCSettingsResponse = VRCOSCSettingsResponse(
		enabled = config.enabled,
		manualNetwork = config.manualNetwork?.let { manual ->
			VRCOSCNetworkSettings(
				portIn = manual.portIn.toUShort(),
				portOut = manual.portOut.toUShort(),
				address = manual.address,
			)
		},
	)
}
