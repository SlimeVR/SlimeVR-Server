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
import dev.slimevr.desktop.keybind.createDesktopKeybindManager
import dev.slimevr.desktop.keybind.isGnome
import dev.slimevr.desktop.keybind.resolveGnomeAppId
import dev.slimevr.desktop.networkprofile.setupDesktopNetworkProfileChecker
import dev.slimevr.desktop.serial.DesktopFirmwareFlasher
import dev.slimevr.desktop.serial.createDesktopSerialServer
import dev.slimevr.desktop.trackingchecklist.SteamVRCheckBehaviour
import dev.slimevr.desktop.udp.resolveDesktopUdpAddress
import dev.slimevr.desktop.vrchat.createDesktopVRCConfigManager
import dev.slimevr.desktop.vrchat.resolveDesktopOscQueryAddress
import dev.slimevr.firmware.FirmwareManager
import dev.slimevr.heightcalibration.HeightCalibrationManager
import dev.slimevr.keybind.KeybindManager
import dev.slimevr.networkprofile.NetworkProfileManager
import dev.slimevr.outputtrackertoggle.OutputTrackerToggleManager
import dev.slimevr.provisioning.ProvisioningManager
import dev.slimevr.resets.ResetsManager
import dev.slimevr.resolveConfigDirectory
import dev.slimevr.skeleton.Skeleton
import dev.slimevr.tapdetection.TapDetectionManager
import dev.slimevr.trackingchecklist.TrackingChecklist
import dev.slimevr.udp.UdpServer
import dev.slimevr.util.safeLaunch
import dev.slimevr.vmc.VMCManager
import dev.slimevr.vrcosc.VRCOSCManager
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.runBlocking
import solarxr_protocol.rpc.KeybindSupport

fun main(args: Array<String>) = runBlocking<Unit> {
	contextDebugEnabled = System.getProperty("slimevr.debug.context") == "true" ||
		System.getenv("SLIMEVR_DEBUG_CONTEXT") == "true"

	val featureFlags = FeatureFlags()
	for (arg in args) {
		when (arg) {
			"--steam", "-s" -> featureFlags.steam = true

			"--install", "-i" -> {
				runInstaller()
				return@runBlocking
			}

			"--no-udev", "-u" -> featureFlags.skipCheckUdev = true
		}
	}
	if (CURRENT_PLATFORM != Platform.LINUX) featureFlags.skipCheckUdev = true

	// Windows registers hotkeys itself, and on GNOME we can rewrite the triggers it stores in dconf.
	// Other compositors own the bindings, so there the user rebinds from the system settings.
	featureFlags.keybindSupport = when (CURRENT_PLATFORM) {
		Platform.WINDOWS -> KeybindSupport.APP_MANAGED

		Platform.LINUX -> if (isGnome()) {
			KeybindSupport.APP_MANAGED
		} else {
			KeybindSupport.SYSTEM_MANAGED
		}

		else -> KeybindSupport.UNSUPPORTED
	}

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
	val trackingChecklist = TrackingChecklist.create(scope = this, extraBehaviours = { appContext ->
		buildList {
			add(SteamVRCheckBehaviour(appContext.server))
		}
	})
	val udpServer = UdpServer.create(scope = this, addressResolver = ::resolveDesktopUdpAddress)
	val outputTrackerToggle = OutputTrackerToggleManager.create(ctx = phase1, scope = this)
	val bvhManager = BVHManager.create(skeleton = skeleton, storage = storage, scope = this)
	val vmcManager = VMCManager.create(ctx = phase1, skeleton = skeleton, scope = this)
	val vrcOscManager = VRCOSCManager.create(
		ctx = phase1,
		scope = this,
		oscQueryAddress = resolveDesktopOscQueryAddress(),
		outputTrackerToggle = outputTrackerToggle,
	)
	val resetsManager = ResetsManager.create(ctx = phase1, scope = this)
	val tapDetectionManager = TapDetectionManager.create(ctx = phase1, resetsManager = resetsManager, scope = this)
	val keybindManager = KeybindManager.create(scope = this)

	val appContext = AppContext(
		server = server,
		config = config,
		serialServer = serialServer,
		featureFlags = featureFlags,
		keybindManager = keybindManager,
		skeleton = skeleton,
		firmwareManager = firmwareManager,
		vrcConfigManager = vrcConfigManager,
		networkProfileManager = networkProfileManager,
		provisioningManager = provisioningManager,
		heightCalibrationManager = heightCalibrationManager,
		trackingChecklist = trackingChecklist,
		udpServer = udpServer,
		outputTrackerToggle = outputTrackerToggle,
		bvhManager = bvhManager,
		vmcManager = vmcManager,
		vrcOscManager = vrcOscManager,
		resetsManager = resetsManager,
		tapDetectionManager = tapDetectionManager,
	)


	try {
		appContext.startObserving()

		safeLaunch { createDesktopHIDManager(appContext, this) }
		safeLaunch { createDesktopKeybindManager(appContext, this) }
		safeLaunch { createSolarXRWebsocketServer(appContext) }
		safeLaunch { createIpcServers(appContext) }
		safeLaunch { setupDesktopNetworkProfileChecker(this, networkProfileManager) }

		awaitCancellation()
	} finally {
		appContext.dispose()
	}
}
