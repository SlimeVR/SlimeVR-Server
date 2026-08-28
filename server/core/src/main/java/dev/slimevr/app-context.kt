package dev.slimevr

import dev.slimevr.bvh.BVHManager
import dev.slimevr.config.AppConfig
import dev.slimevr.firmware.FirmwareManager
import dev.slimevr.heightcalibration.HeightCalibrationManager
import dev.slimevr.keybind.KeybindManager
import dev.slimevr.networkprofile.NetworkProfileManager
import dev.slimevr.provisioning.ProvisioningManager
import dev.slimevr.resets.ResetsManager
import dev.slimevr.routing.BoneRoutingManager
import dev.slimevr.serial.SerialServer
import dev.slimevr.skeleton.Skeleton
import dev.slimevr.solarxr.rpc.ServerInfos
import dev.slimevr.tapdetection.TapDetectionManager
import dev.slimevr.trackingchecklist.TrackingChecklist
import dev.slimevr.udp.UdpServer
import dev.slimevr.vmc.VMCManager
import dev.slimevr.vrchat.VRCConfigManager
import dev.slimevr.vrcosc.VRCOSCManager

interface Phase1ContextProvider {
	val server: VRServer
	val config: AppConfig
	val serialServer: SerialServer
}

data class Phase1Context(
	override val server: VRServer,
	override val config: AppConfig,
	override val serialServer: SerialServer,
) : Phase1ContextProvider

interface AppContextProvider : Phase1ContextProvider {
	val serverInfos: ServerInfos
	val featureFlags: FeatureFlags
	val keybindManager: KeybindManager
	val skeleton: Skeleton
	val firmwareManager: FirmwareManager
	val vrcConfigManager: VRCConfigManager?
	val networkProfileManager: NetworkProfileManager?
	val provisioningManager: ProvisioningManager
	val heightCalibrationManager: HeightCalibrationManager
	val trackingChecklist: TrackingChecklist
	val udpServer: UdpServer
	val boneRouting: BoneRoutingManager
	val bvhManager: BVHManager
	val vmcManager: VMCManager
	val vrcOscManager: VRCOSCManager
	val resetsManager: ResetsManager
	val tapDetectionManager: TapDetectionManager
	fun startObserving()
	suspend fun dispose()
}

class AppContext(
	override val server: VRServer,
	override val config: AppConfig,
	override val serialServer: SerialServer,
	override val serverInfos: ServerInfos,
	override val featureFlags: FeatureFlags,
	override val keybindManager: KeybindManager,
	override val skeleton: Skeleton,
	override val firmwareManager: FirmwareManager,
	override val vrcConfigManager: VRCConfigManager?,
	override val networkProfileManager: NetworkProfileManager?,
	override val provisioningManager: ProvisioningManager,
	override val heightCalibrationManager: HeightCalibrationManager,
	override val trackingChecklist: TrackingChecklist,
	override val udpServer: UdpServer,
	override val boneRouting: BoneRoutingManager,
	override val bvhManager: BVHManager,
	override val vmcManager: VMCManager,
	override val vrcOscManager: VRCOSCManager,
	override val resetsManager: ResetsManager,
	override val tapDetectionManager: TapDetectionManager,
) : AppContextProvider {
	override fun startObserving() {
		keybindManager.startObserving(this)
		skeleton.startObserving()
		firmwareManager.startObserving()
		provisioningManager.startObserving()
		heightCalibrationManager.startObserving()
		vrcConfigManager?.startObserving()
		networkProfileManager?.startObserving()
		trackingChecklist.startObserving(this)
		udpServer.startReceiving(this, server.context.scope)
		boneRouting.startObserving(this)
		vmcManager.startObserving(this)
		vrcOscManager.startObserving(this)
		resetsManager.startObserving()
		tapDetectionManager.startObserving()
	}

	override suspend fun dispose() {
		udpServer.dispose()
	}
}
