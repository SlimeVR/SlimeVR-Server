package dev.slimevr.solarxr

import dev.slimevr.config.VRCOSCConfig
import dev.slimevr.config.VRCOSCManualNetworkConfig
import dev.slimevr.config.VRCOSCTrackers
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
import solarxr_protocol.rpc.OSCTrackersSetting
import solarxr_protocol.rpc.VRCOSCNetworkSettings
import solarxr_protocol.rpc.VRCOSCSettings
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
			receiver.sendRpc(VRCOSCSettingsResponse(settings = buildVrcOscSettings(vrcOscManager.context.state.value.config)))
		}

		receiver.rpcDispatcher.on<VRCOSCStatusRequest> {
			val state = vrcOscManager.context.state.value
			receiver.sendRpc(buildStatusResponse(state.status, state.config.enabled))
		}

		receiver.rpcDispatcher.on<ChangeVRCOSCSettingsRequest> { req ->
			val vrcOsc = req.settings ?: return@on
			val trackers = vrcOsc.trackers
			vrcOscManager.context.dispatch(
				VRCOSCActions.UpdateConfig(
					VRCOSCConfig(
						enabled = vrcOsc.enabled,
						manualNetwork = vrcOsc.manualNetwork?.let { network ->
							VRCOSCManualNetworkConfig(
								portIn = network.portIn.toInt(),
								portOut = network.portOut.toInt(),
								address = network.address.orEmpty(),
							)
						},
						trackers = VRCOSCTrackers(
							head = trackers?.head ?: false,
							chest = trackers?.chest ?: false,
							waist = trackers?.waist ?: true,
							knees = trackers?.knees ?: false,
							feet = trackers?.feet ?: true,
							elbows = trackers?.elbows ?: false,
							hands = trackers?.hands ?: false,
						),
					),
				),
			)
		}
	}
}

internal fun buildStatusResponse(status: VRCOSCStatus, enabled: Boolean) = VRCOSCStatusChangeResponse(
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

internal fun buildVrcOscSettings(config: VRCOSCConfig): VRCOSCSettings = VRCOSCSettings(
	trackers = OSCTrackersSetting(
		head = config.trackers.head,
		chest = config.trackers.chest,
		waist = config.trackers.waist,
		knees = config.trackers.knees,
		feet = config.trackers.feet,
		elbows = config.trackers.elbows,
		hands = config.trackers.hands,
	),
	enabled = config.enabled,
	manualNetwork = config.manualNetwork?.let { manual ->
		VRCOSCNetworkSettings(
			portIn = manual.portIn.toUShort(),
			portOut = manual.portOut.toUShort(),
			address = manual.address,
		)
	},
)
