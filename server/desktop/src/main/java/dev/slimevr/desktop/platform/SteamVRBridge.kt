package dev.slimevr.desktop.platform

import dev.slimevr.VRServer
import dev.slimevr.VRServer.Companion.getNextLocalTrackerId
import dev.slimevr.VRServer.Companion.instance
import dev.slimevr.bridge.BridgeThread
import dev.slimevr.bridge.ISteamVRBridge
import dev.slimevr.config.BridgeConfig
import dev.slimevr.desktop.platform.ProtobufMessages.*
import dev.slimevr.protocol.rpc.settings.RPCSettingsHandler
import dev.slimevr.tracking.trackers.DeviceOrigin
import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.TrackerPosition
import dev.slimevr.tracking.trackers.TrackerPosition.Companion.getByTrackerRole
import dev.slimevr.tracking.trackers.TrackerRole
import dev.slimevr.tracking.trackers.TrackerRole.Companion.getById
import dev.slimevr.tracking.trackers.TrackerUtils.getTrackerForSkeleton
import dev.slimevr.util.ann.VRServerThread
import io.eiren.util.OperatingSystem
import io.eiren.util.collections.FastList
import io.eiren.util.logging.LogManager
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.exists

class BindingsProviderManager : Runnable {
	private var process: Process? = null
	private var watcherThread: Thread? = null

	private fun getBinaryPath(): Path? {
		// First we want to try to find it in the working directory, its location on
		// Steam/Windows/portable.
		val binaryName = if (OperatingSystem.currentPlatform == OperatingSystem.WINDOWS) {
			"SlimeVR-Bindings-Provider.exe"
		} else {
			"slimevr-bindings-provider"
		}
		val workingDir = System.getProperty("user.dir")!!

		val binaryPath = Path(workingDir, binaryName)
		if (binaryPath.exists()) return binaryPath

		// Then look through PATH to find the binary.
		// PATH shouldn't be null, but if it is just gracefully fail
		val path = System.getenv("PATH") ?: return null
		val pathSeparator = System.getProperty("path.separator")!!
		for (path in path.split(pathSeparator)) {
			val binaryPath = Path(path, binaryName)
			if (binaryPath.exists()) return binaryPath
		}

		// :(
		return null
	}

	fun start() {
		check(process == null && watcherThread == null) {
			"BindingsProviderManager already running"
		}

		val binaryPath = getBinaryPath() ?: throw RuntimeException("Unable to find bindings provider binary")
		process = ProcessBuilder(binaryPath.toString())
			.redirectErrorStream(true)
			.start()
		LogManager.info("[BindingsProviderManager] Started process")
		watcherThread = Thread(this, "Bindings provider watcher")
		watcherThread!!.start()
	}
	fun stop() {
		process?.let {
			it.destroy()
		}
		process = null
		watcherThread?.interrupt()
		watcherThread = null
	}

	override fun run() {
		try {
			val interval = 1000L / 30L
			while (process?.isAlive == true) {
				Thread.sleep(interval)
			}

			val exitCode = process?.exitValue()
			if (exitCode != null) {
				LogManager.info("[BindingsProviderManager] Process has exited with exit code $exitCode")
			} else {
				LogManager.info("[BindingsProviderManager] Process has exited")
			}
		} catch (_: InterruptedException) {
			// Ignore it
		}
	}
}

abstract class SteamVRBridge(
	protected val server: VRServer,
	threadName: String,
	bridgeName: String,
	val bridgeSettingsKey: String,
	protected val shareableTrackers: List<Tracker>,
) : ProtobufBridge(bridgeName),
	Runnable {
	protected val runnerThread: Thread = Thread(this, threadName)
	private var bindingsProviderManager: BindingsProviderManager? = null
	protected val config: BridgeConfig = server.configManager.vrConfig.getBridge(bridgeSettingsKey)
	var connected: Boolean = false

	@VRServerThread
	override fun startBridge() {
		for (tr in shareableTrackers) {
			val role = tr.trackerPosition?.trackerRole
			changeShareSettings(
				role,
				config.getBridgeTrackerRole(role, false),
			)
		}
		runnerThread.start()
	}

	@VRServerThread
	override fun stopBridge() {
		bindingsProviderManager?.stop()
		bindingsProviderManager = null
		runnerThread.interrupt()
	}

	@VRServerThread
	override fun getShareSetting(role: TrackerRole): Boolean {
		for (tr in shareableTrackers) {
			if (tr.trackerPosition?.trackerRole == role) {
				return sharedTrackers.contains(tr)
			}
		}
		return false
	}

	override fun updateShareSettingsAutomatically(): Boolean {
		// Return false if automatic trackers is disabled or if tracking is paused
		if (!config.automaticSharedTrackersToggling || server.getPauseTracking()) return false

		val skeleton = instance.humanPoseManager.skeleton
		// Hands aren't touched as they will override the controller's tracking
		val roleToTrackers = mapOf(
			TrackerRole.CHEST to setOf(skeleton.upperChestTracker, skeleton.chestTracker),
			TrackerRole.LEFT_ELBOW to setOf(skeleton.leftUpperArmTracker),
			TrackerRole.RIGHT_ELBOW to setOf(skeleton.rightUpperArmTracker),
			TrackerRole.WAIST to setOf(skeleton.waistTracker, skeleton.hipTracker),
			TrackerRole.LEFT_KNEE to setOf(skeleton.leftUpperLegTracker),
			TrackerRole.RIGHT_KNEE to setOf(skeleton.rightUpperLegTracker),
			TrackerRole.LEFT_FOOT to setOf(skeleton.leftLowerLegTracker, skeleton.leftFootTracker),
			TrackerRole.RIGHT_FOOT to setOf(skeleton.rightLowerLegTracker, skeleton.rightFootTracker),
		)

		for ((role, trackers) in roleToTrackers) {
			val shouldShare = if (role == TrackerRole.WAIST) {
				// Waist is special, it should be enabled if there is any tracker on the spine,
				// but ignored if the waist or hip tracker is from us
				skeleton.hasSpineTracker &&
					!trackers.any {
						it?.device?.origin == DeviceOrigin.STEAMVR
					}
			} else {
				trackers.any {
					it != null && it.device?.origin != DeviceOrigin.STEAMVR
				}
			}
			changeShareSettings(role, shouldShare)
		}
		return true
	}

	override fun getAutomaticSharedTrackers(): Boolean = config.automaticSharedTrackersToggling

	override fun setAutomaticSharedTrackers(value: Boolean) {
		if (value == config.automaticSharedTrackersToggling) return

		config.automaticSharedTrackersToggling = value
		updateShareSettingsAutomatically()
		instance.configManager.saveConfig()
	}

	@VRServerThread
	override fun changeShareSettings(role: TrackerRole?, share: Boolean) {
		if (role == null) return
		for (tr in shareableTrackers) {
			if (tr.trackerPosition?.trackerRole == role) {
				if (share) {
					addSharedTracker(tr)
				} else {
					removeSharedTracker(tr)
				}
				config.setBridgeTrackerRole(role, share)
				instance.configManager.saveConfig()
			}
		}
	}

	@VRServerThread
	override fun createNewTracker(trackerAdded: TrackerAdded): Tracker {
		val device = instance.deviceManager
			.createDevice(
				DeviceOrigin.STEAMVR,
				trackerAdded.trackerName,
				null,
				trackerAdded.manufacturer.ifEmpty { "OpenVR" },
			)

		// Display name, needsReset and isHmd
		val displayName: String = trackerAdded.trackerName

		// trackerPosition
		val role = getById(trackerAdded.trackerRole)
		val isHmd = trackerAdded.trackerId == 0
		val trackerPosition = if (role != null) {
			getByTrackerRole(role)
		} else {
			null
		}

		// Make the tracker
		val tracker = Tracker(
			device,
			getNextLocalTrackerId(),
			trackerAdded.trackerSerial,
			displayName,
			trackerPosition,
			trackerAdded.trackerId,
			hasPosition = true,
			hasRotation = true,
			userEditable = true,
			isComputed = true,
			allowReset = true,
			isHmd = isHmd,
		)

		device.trackers[0] = tracker
		instance.deviceManager.addDevice(device)
		return tracker
	}

	@VRServerThread
	override fun versionReceived(versionMessage: Version) {
		super.versionReceived(versionMessage)
		if (bridgeSettingsKey == "steamvr" && remoteProtocolVersion >= 2) {
			instance.queueTask {
				// Shut off the feeder bridge if the connected driver is recent enough
				val bridge = instance.getVRBridge {
					it is ISteamVRBridge && it.getBridgeConfigKey() == "steamvr_feeder"
				} as? SteamVRBridge
				bridge?.let {
					LogManager.info("[$bridgeName] Driver version is new enough, deactivating feeder bridge")
					instance.removeVRBridge(it)
				}

				// Start the bindings utility when the driver starts up
				if (bindingsProviderManager == null) {
					bindingsProviderManager = BindingsProviderManager()
				}
				try {
					bindingsProviderManager!!.start()
				} catch (e: Exception) {
					LogManager.warning("[$bridgeName] Failed to start bindings provider", e)
				}
			}
		}
	}

	// Battery Status
	@VRServerThread
	override fun writeBatteryUpdate(localTracker: Tracker) {
		var lowestLevel = 200f // Arbitrarily higher than expected battery
		// percentage
		var trackerLevel = 0f // Tracker battery percentage on a scale from 0
		// to 100. SteamVR expects a value from 0 to 1.
		var trackerVoltage = 0f // Tracker voltage. This is used to determine
		// if the tracker is being charged. owoTrack
		// devices do not have a tracker voltage.
		var isCharging = false

		val allTrackers = instance.allTrackers
		val role = localTracker.trackerPosition?.trackerRole ?: return

		val batteryTrackers: MutableList<Tracker?> = FastList()

		// batteryTrackers is filled with trackers that would give battery data
		// for the SteamVR tracker according to its role. Warning: trackers
		// inside batteryTrackers could be null, so there must be a null check
		// when accessing its data.
		batteryTrackers
			.add(
				getTrackerForSkeleton(
					allTrackers,
					getByTrackerRole(role) ?: return,
				),
			)
		when (role) {
			TrackerRole.WAIST -> {
				// Add waist because the first tracker is hip
				batteryTrackers
					.add(
						getTrackerForSkeleton(
							allTrackers,
							TrackerPosition.WAIST,
						),
					)
				// When the chest SteamVR tracking point is disabled, aggregate
				// its battery level alongside waist and hip.
				if (!(config.getBridgeTrackerRole(TrackerRole.CHEST, true))) {
					batteryTrackers
						.add(
							getTrackerForSkeleton(
								allTrackers,
								TrackerPosition.CHEST,
							),
						)
					batteryTrackers
						.add(
							getTrackerForSkeleton(
								allTrackers,
								TrackerPosition.UPPER_CHEST,
							),
						)
				}
			}

			TrackerRole.CHEST -> {
				// Add chest because the first tracker is upperChest
				batteryTrackers
					.add(
						getTrackerForSkeleton(
							allTrackers,
							TrackerPosition.CHEST,
						),
					)
				// When the waist SteamVR tracking point is disabled, aggregate
				// waist and hip battery level with the chest.
				if (!(config.getBridgeTrackerRole(TrackerRole.WAIST, true))) {
					batteryTrackers
						.add(
							getTrackerForSkeleton(
								allTrackers,
								TrackerPosition.WAIST,
							),
						)
					batteryTrackers
						.add(
							getTrackerForSkeleton(
								allTrackers,
								TrackerPosition.HIP,
							),
						)
				}
			}

			TrackerRole.LEFT_FOOT -> {
				batteryTrackers
					.add(
						getTrackerForSkeleton(
							allTrackers,
							TrackerPosition.LEFT_LOWER_LEG,
						),
					)
				// When the left knee SteamVR tracking point is disabled,
				// aggregate its battery level with left ankle and left foot.
				if (!(config.getBridgeTrackerRole(TrackerRole.LEFT_KNEE, true))) {
					batteryTrackers
						.add(
							getTrackerForSkeleton(
								allTrackers,
								TrackerPosition.LEFT_UPPER_LEG,
							),
						)
				}
			}

			TrackerRole.RIGHT_FOOT -> {
				batteryTrackers
					.add(
						getTrackerForSkeleton(
							allTrackers,
							TrackerPosition.RIGHT_LOWER_LEG,
						),
					)
				// When the right knee SteamVR tracking point is disabled,
				// aggregate its battery level with right ankle and right foot.
				if (!(config.getBridgeTrackerRole(TrackerRole.RIGHT_KNEE, true))) {
					batteryTrackers
						.add(
							getTrackerForSkeleton(
								allTrackers,
								TrackerPosition.RIGHT_UPPER_LEG,
							),
						)
				}
			}

			TrackerRole.LEFT_ELBOW -> {
				batteryTrackers
					.add(
						getTrackerForSkeleton(
							allTrackers,
							TrackerPosition.LEFT_LOWER_ARM,
						),
					)
				batteryTrackers
					.add(
						getTrackerForSkeleton(
							allTrackers,
							TrackerPosition.LEFT_SHOULDER,
						),
					)
			}

			TrackerRole.RIGHT_ELBOW -> {
				batteryTrackers
					.add(
						getTrackerForSkeleton(
							allTrackers,
							TrackerPosition.RIGHT_LOWER_ARM,
						),
					)
				batteryTrackers
					.add(
						getTrackerForSkeleton(
							allTrackers,
							TrackerPosition.RIGHT_SHOULDER,
						),
					)
			}

			else -> {}
		}
		// If the battery level of the tracker is lower than lowestLevel, then
		// the battery level of the tracker position becomes lowestLevel.
		// Tracker voltage is set if the tracker position has a battery level
		// lower than the lowest level and has a battery voltage (owoTrack
		// devices do not).
		for (batteryTracker in batteryTrackers) {
			if (batteryTracker?.batteryLevel?.let { it < lowestLevel } == true) {
				lowestLevel = batteryTracker.batteryLevel!!

				trackerVoltage = if (batteryTracker.batteryVoltage != null) {
					batteryTracker.batteryVoltage!!
				} else {
					0f
				}
			}
		}

		// Internal battery reporting 5V max, and <= 3.2V when >=50mV
		// lower than initial reading (e.g. 3.25V down from 3.3V), and ~0V when
		// battery is fine.
		// 3.2V is technically 0%, but the last 5% of battery level is ignored,
		// which makes 3.36V 0% in practice. Refer to batterymonitor.cpp in
		// SlimeVR-Tracker-ESP for exact details.
		// External battery reporting anything > 0V.
		// The following should catch internal battery reporting and erroneous
		// readings.
		if ((lowestLevel >= 200 || lowestLevel < 0) ||
			(trackerVoltage < 3.2 && lowestLevel <= 0) ||
			(trackerVoltage >= 5 && lowestLevel > 150)
		) {
			return
		} else {
			trackerLevel = lowestLevel / 100
			if (trackerVoltage >= 4.3) {
				// TO DO: Add sending whether the tracker is charging from the
				// tracker itself rather than checking voltage.
				isCharging = true
			}
		}

		val builder = Battery.newBuilder().setTrackerId(localTracker.trackerNum)

		builder.setBatteryLevel(trackerLevel)
		builder.setIsCharging(isCharging)

		sendMessage(ProtobufMessage.newBuilder().setBattery(builder).build())
	}

	@VRServerThread
	override fun batteryReceived(batteryMessage: Battery) {
		val tracker = getInternalRemoteTrackerById(batteryMessage.trackerId) ?: return

		tracker.batteryLevel = batteryMessage.batteryLevel

		// Purely for cosmetic purposes, SteamVR does not report device
		// voltage.
		if (batteryMessage.isCharging) {
			tracker.batteryVoltage = 4.3f
			// TO DO: Add "tracker.setIsCharging()"
		} else {
			tracker.batteryVoltage = 3.7f
		}
	}

	@BridgeThread
	protected fun reportDisconnected() {
		bindingsProviderManager?.stop()
		connected = false
	}

	@BridgeThread
	protected fun reportConnected() {
		connected = true
	}
}
