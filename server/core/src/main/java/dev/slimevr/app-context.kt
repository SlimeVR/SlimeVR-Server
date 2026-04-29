package dev.slimevr

import dev.slimevr.bvh.BVHManager
import dev.slimevr.config.AppConfig
import dev.slimevr.firmware.FirmwareManager
import dev.slimevr.heightcalibration.HeightCalibrationManager
import dev.slimevr.provisioning.ProvisioningManager
import dev.slimevr.serial.SerialServer
import dev.slimevr.skeleton.Skeleton
import dev.slimevr.trackingchecklist.TrackingChecklist
import dev.slimevr.udp.UdpServer
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
	val provisioningManager: ProvisioningManager
	val heightCalibrationManager: HeightCalibrationManager
	val trackingChecklist: TrackingChecklist
	val udpServer: UdpServer
	val bvhManager: BVHManager
	fun startObserving()
}

class AppContext(
	override val server: VRServer,
	override val config: AppConfig,
	override val serialServer: SerialServer,
	override val skeleton: Skeleton,
	override val firmwareManager: FirmwareManager,
	override val vrcConfigManager: VRCConfigManager?,
	override val provisioningManager: ProvisioningManager,
	override val heightCalibrationManager: HeightCalibrationManager,
	override val trackingChecklist: TrackingChecklist,
	override val udpServer: UdpServer,
	override val bvhManager: BVHManager,
) : AppContextProvider {
	override fun startObserving() {
		skeleton.startObserving()
		firmwareManager.startObserving()
		provisioningManager.startObserving()
		heightCalibrationManager.startObserving()
		vrcConfigManager?.startObserving()
		trackingChecklist.startObserving(this)
		udpServer.startReceiving(this, server.context.scope)
	}
}
