package dev.slimevr

import dev.slimevr.bvh.BVHManager
import dev.slimevr.config.AppConfig
import dev.slimevr.firmware.FirmwareManager
import dev.slimevr.heightcalibration.HeightCalibrationManager
import dev.slimevr.networkprofile.NetworkProfileManager
import dev.slimevr.provisioning.ProvisioningManager
import dev.slimevr.serial.SerialServer
import dev.slimevr.skeleton.Skeleton
import dev.slimevr.trackingchecklist.TrackingChecklist
import dev.slimevr.udp.UdpServer
import dev.slimevr.vmc.VMCManager
import dev.slimevr.vrchat.VRCConfigManager

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
	val skeleton: Skeleton
	val firmwareManager: FirmwareManager
	val vrcConfigManager: VRCConfigManager?
	val networkProfileManager: NetworkProfileManager?
	val provisioningManager: ProvisioningManager
	val heightCalibrationManager: HeightCalibrationManager
	val trackingChecklist: TrackingChecklist
	val udpServer: UdpServer
	val bvhManager: BVHManager
	val vmcManager: VMCManager
	fun startObserving()
}

class AppContext(
	override val server: VRServer,
	override val config: AppConfig,
	override val serialServer: SerialServer,
	override val skeleton: Skeleton,
	override val firmwareManager: FirmwareManager,
	override val vrcConfigManager: VRCConfigManager?,
	override val networkProfileManager: NetworkProfileManager?,
	override val provisioningManager: ProvisioningManager,
	override val heightCalibrationManager: HeightCalibrationManager,
	override val trackingChecklist: TrackingChecklist,
	override val udpServer: UdpServer,
	override val bvhManager: BVHManager,
	override val vmcManager: VMCManager,
) : AppContextProvider {
	override fun startObserving() {
		skeleton.startObserving()
		firmwareManager.startObserving()
		provisioningManager.startObserving()
		heightCalibrationManager.startObserving()
		vrcConfigManager?.startObserving()
		networkProfileManager?.startObserving()
		trackingChecklist.startObserving(this)
		udpServer.startReceiving(this, server.context.scope)
		vmcManager.startObserving()
	}
}
