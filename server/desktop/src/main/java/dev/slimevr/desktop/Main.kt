@file:JvmName("Main")

package dev.slimevr.desktop

import dev.slimevr.VRServer
import dev.slimevr.config.AppConfig
import dev.slimevr.desktop.hid.createDesktopHIDManager
import dev.slimevr.desktop.ipc.createIpcServers
import dev.slimevr.desktop.serial.createDesktopSerialServer
import dev.slimevr.desktop.vrchat.createDesktopVRCConfigManager
import dev.slimevr.firmware.FirmwareManager
import dev.slimevr.heightcalibration.HeightCalibrationManager
import dev.slimevr.provisioning.ProvisioningManager
import dev.slimevr.resolveConfigDirectory
import dev.slimevr.skeleton.Skeleton
import dev.slimevr.solarxr.DataFeedInitBehaviour
import dev.slimevr.solarxr.FirmwareBehaviour
import dev.slimevr.solarxr.HeightCalibrationBehaviour
import dev.slimevr.solarxr.ProvisioningBehaviour
import dev.slimevr.solarxr.SerialBehaviour
import dev.slimevr.solarxr.VrcBehaviour
import dev.slimevr.solarxr.createSolarXRWebsocketServer
import dev.slimevr.udp.createUDPTrackerServer
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) = runBlocking {
	val configFolder = resolveConfigDirectory() ?: error("Unable to resolve config folder")
	val config = AppConfig.create(this, configFolder = configFolder.toFile())
	val server = VRServer.create(this)

	val serialServer = createDesktopSerialServer(this)
	val firmwareManager = FirmwareManager.create(serialServer = serialServer, scope = this)

	val vrcConfigManager = createDesktopVRCConfigManager(config = config, scope = this)
	val heightCalibrationManager = HeightCalibrationManager.create(serverContext = server, scope = this)
	val provisioningManager = ProvisioningManager.create(serialServer, this)

	val skeleton = Skeleton.create(this)

	launch {
		createUDPTrackerServer(server, config)
	}
	launch {
		createDesktopHIDManager(server, this)
	}

	val solarXRBehaviours = listOf(
		DataFeedInitBehaviour(server, skeleton),
		SerialBehaviour(serialServer),
		FirmwareBehaviour(server, firmwareManager),
		VrcBehaviour(vrcConfigManager, server, userHeight = { config.userConfig.context.state.value.data.userHeight.toDouble() }),
		HeightCalibrationBehaviour(heightCalibrationManager),
		ProvisioningBehaviour(server, provisioningManager)
	)
	launch { createSolarXRWebsocketServer(solarXRBehaviours) }
	launch { createIpcServers(server, solarXRBehaviours) }

	Unit
}
