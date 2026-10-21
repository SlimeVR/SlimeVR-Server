package dev.slimevr.solarxr.rpc

import dev.slimevr.config.GlobalConfigState
import dev.slimevr.config.SettingsConfigState
import dev.slimevr.config.UserConfigData
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import solarxr_protocol.rpc.BoneRoutingSettingsResponse
import solarxr_protocol.rpc.ErrorReportingSettingsResponse
import solarxr_protocol.rpc.MagToggleResponse
import solarxr_protocol.rpc.SettingsDefaultsRequest
import solarxr_protocol.rpc.SettingsDefaultsResponse

// Every config data class defaults its own constructor params, so one default tree
// answers every domain at once.
private fun buildSettingsDefaultsResponse(): SettingsDefaultsResponse {
	val settings = SettingsConfigState()
	val user = UserConfigData()
	val global = GlobalConfigState()
	return SettingsDefaultsResponse(
		skeleton = buildSkeletonSettingsResponse(settings.skeletonConfig),
		resets = buildResetsSettingsResponse(settings.resetsConfig),
		stayAligned = buildStayAlignedSettingsResponse(settings.stayAlignedConfig, user),
		tapDetection = buildTapDetectionSettingsResponse(settings.tapDetectionConfig),
		timeout = buildTimeoutSettingsResponse(settings.trackersConfig),
		hid = buildHidSettingsResponse(settings.hidConfig),
		driver = buildDriverSettingsResponse(settings.driverConfig),
		vrcosc = buildVrcOscSettingsResponse(settings.vrcOscConfig),
		vmcosc = buildVmcOscSettingsResponse(settings.vmcConfig),
		vrm = buildVrmSettingsResponse(settings.vmcConfig),
		// Routes and output states come from the connected trackers and the platform
		boneRouting = BoneRoutingSettingsResponse(automatic = settings.boneRoutingConfig.automatic),
		// The user id and session id identify an install and a run
		errorReporting = ErrorReportingSettingsResponse(consent = global.errorReporting.consent),
		mag = MagToggleResponse(trackerId = 0u, enable = settings.trackersConfig.globalMagEnabled),
	)
}

class SettingsDefaultsBehaviour : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		receiver.rpcDispatcher.on<SettingsDefaultsRequest> {
			receiver.sendRpc(buildSettingsDefaultsResponse())
		}.launchIn(receiver.context.scope)
	}
}
