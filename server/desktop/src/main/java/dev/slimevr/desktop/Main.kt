@file:JvmName("Main")

package dev.slimevr.desktop

import dev.slimevr.AppContext
import dev.slimevr.Phase1Context
import dev.slimevr.VRServer
import dev.slimevr.config.AppConfig
import dev.slimevr.desktop.hid.createDesktopHIDManager
import dev.slimevr.desktop.ipc.createIpcServers
import dev.slimevr.desktop.ipc.createSolarXRWebsocketServer
import dev.slimevr.desktop.serial.createDesktopSerialServer
import dev.slimevr.desktop.vrchat.createDesktopVRCConfigManager
import dev.slimevr.firmware.FirmwareManager
import dev.slimevr.heightcalibration.HeightCalibrationManager
import dev.slimevr.provisioning.ProvisioningManager
import dev.slimevr.resolveConfigDirectory
import dev.slimevr.skeleton.Skeleton
import dev.slimevr.trackingchecklist.TrackingChecklist
import dev.slimevr.udp.createUDPTrackerServer
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) = runBlocking {
	val configFolder = resolveConfigDirectory() ?: error("Unable to resolve config folder")
	val config = AppConfig.create(this, configFolder = configFolder.toFile())
	val server = VRServer.create(this)
	val serialServer = createDesktopSerialServer(this)

	val phase1 = Phase1Context(server = server, config = config, serialServer = serialServer)

	val firmwareManager = FirmwareManager.create(ctx = phase1, scope = this)
	val vrcConfigManager = createDesktopVRCConfigManager(ctx = phase1, scope = this)
	val skeleton = Skeleton.create(scope = this, ctx = phase1)
	val provisioningManager = ProvisioningManager.create(ctx = phase1, scope = this)
	val heightCalibrationManager = HeightCalibrationManager.create(ctx = phase1, scope = this)
	val trackingChecklist = TrackingChecklist.create(scope = this)

	val appContext = AppContext(
		server = server,
		config = config,
		serialServer = serialServer,
		skeleton = skeleton,
		firmwareManager = firmwareManager,
		vrcConfigManager = vrcConfigManager,
		provisioningManager = provisioningManager,
		heightCalibrationManager = heightCalibrationManager,
		trackingChecklist = trackingChecklist,
	)

	appContext.startObserving()

	launch { createUDPTrackerServer(appContext) }
	launch { createDesktopHIDManager(appContext, this) }
	launch { createSolarXRWebsocketServer(appContext) }
	launch { createIpcServers(appContext) }

	Unit
}
