@file:JvmName("Main")

package dev.slimevr.desktop

import dev.slimevr.AppContext
import dev.slimevr.CURRENT_PLATFORM
import dev.slimevr.FeatureFlags
import dev.slimevr.Phase1Context
import dev.slimevr.Platform
import dev.slimevr.VRServer
import dev.slimevr.bvh.BVHManager
import dev.slimevr.config.AppConfig
import dev.slimevr.context.debug.contextDebugEnabled
import dev.slimevr.desktop.config.DesktopConfigStorage
import dev.slimevr.desktop.hid.createDesktopHIDManager
import dev.slimevr.desktop.install.executeShellCommand
import dev.slimevr.desktop.install.runInstaller
import dev.slimevr.desktop.ipc.createIpcServers
import dev.slimevr.desktop.ipc.createSolarXRWebsocketServer
import dev.slimevr.desktop.networkprofile.setupDesktopNetworkProfileChecker
import dev.slimevr.desktop.serial.DesktopFirmwareFlasher
import dev.slimevr.desktop.serial.createDesktopSerialServer
import dev.slimevr.desktop.vrchat.createDesktopVRCConfigManager
import dev.slimevr.desktop.vrchat.resolveDesktopOscQueryAddress
import dev.slimevr.firmware.FirmwareManager
import dev.slimevr.heightcalibration.HeightCalibrationManager
import dev.slimevr.networkprofile.NetworkProfileManager
import dev.slimevr.provisioning.ProvisioningManager
import dev.slimevr.resolveConfigDirectory
import dev.slimevr.skeleton.Skeleton
import dev.slimevr.trackingchecklist.TrackingChecklist
import dev.slimevr.desktop.udp.resolveDesktopUdpAddress
import dev.slimevr.resets.ResetsManager
import dev.slimevr.udp.UdpServer
import dev.slimevr.util.safeLaunch
import dev.slimevr.vmc.VMCManager
import dev.slimevr.vrcosc.VRCOSCManager
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) = runBlocking<Unit> {
	contextDebugEnabled = System.getProperty("slimevr.debug.context") == "true" ||
		System.getenv("SLIMEVR_DEBUG_CONTEXT") == "true"

	val featureFlags = FeatureFlags()
	for (arg in args) {
		when (arg) {
			"--steam", "-s" -> featureFlags.steam = true
			"--install", "-i" -> { runInstaller(); return@runBlocking }
			"--no-udev", "-u" -> featureFlags.skipCheckUdev = true
		}
	}
	if (CURRENT_PLATFORM != Platform.LINUX) featureFlags.skipCheckUdev = true

	val isInstallDisabled = System.getenv("SLIME_SERVER_DISABLE_INSTALLER")?.toIntOrNull()
	if (featureFlags.steam && isInstallDisabled != 1) runInstaller()

	if (!featureFlags.skipCheckUdev) {
		val command = if (featureFlags.steam) {
			arrayOf("steam-runtime-launch-client", "--alongside-steam", "--", "udevadm", "cat")
		} else {
			arrayOf("udevadm", "cat")
		}
		featureFlags.udevRulesInstalled = executeShellCommand(*command)?.second?.contains("slime")
	}

	val configFolder = resolveConfigDirectory() ?: error("Unable to resolve config folder")
	val storage = DesktopConfigStorage(configFolder.toFile())
	val config = AppConfig.create(this, storage = storage)
	val server = VRServer.create(this)
	val serialServer = createDesktopSerialServer(this)

	val phase1 = Phase1Context(server = server, config = config, serialServer = serialServer)

	val firmwareManager = FirmwareManager.create(ctx = phase1, scope = this, flasher = DesktopFirmwareFlasher)
	val vrcConfigManager = createDesktopVRCConfigManager(ctx = phase1, scope = this)
	val networkProfileManager = NetworkProfileManager.create(scope = this, isSupported = CURRENT_PLATFORM == Platform.WINDOWS)
	val skeleton = Skeleton.create(scope = this, ctx = phase1)
	val provisioningManager = ProvisioningManager.create(ctx = phase1, scope = this)
	val heightCalibrationManager = HeightCalibrationManager.create(ctx = phase1, scope = this)
	val trackingChecklist = TrackingChecklist.create(scope = this)
	val udpServer = UdpServer.create(scope = this, addressResolver = ::resolveDesktopUdpAddress)
	val bvhManager = BVHManager.create(skeleton = skeleton, storage = storage, scope = this)
	val vmcManager = VMCManager.create(skeleton = skeleton, ctx = phase1, scope = this)
	val vrcOscManager = VRCOSCManager.create(
		ctx = phase1,
		scope = this,
		oscQueryAddress = resolveDesktopOscQueryAddress(),
	)
	val resetsManager = ResetsManager.create(server = server, scope = this)

	val appContext = AppContext(
		server = server,
		config = config,
		serialServer = serialServer,
		featureFlags = featureFlags,
		skeleton = skeleton,
		firmwareManager = firmwareManager,
		vrcConfigManager = vrcConfigManager,
		networkProfileManager = networkProfileManager,
		provisioningManager = provisioningManager,
		heightCalibrationManager = heightCalibrationManager,
		trackingChecklist = trackingChecklist,
		udpServer = udpServer,
		bvhManager = bvhManager,
		vmcManager = vmcManager,
		vrcOscManager = vrcOscManager,
		resetsManager = resetsManager,
	)

	try {
		appContext.startObserving()

		safeLaunch { createDesktopHIDManager(appContext, this) }
		safeLaunch { createSolarXRWebsocketServer(appContext) }
		safeLaunch { createIpcServers(appContext) }
		safeLaunch { setupDesktopNetworkProfileChecker(this, networkProfileManager) }

		awaitCancellation()
	} finally {
		appContext.dispose()
	}
}
