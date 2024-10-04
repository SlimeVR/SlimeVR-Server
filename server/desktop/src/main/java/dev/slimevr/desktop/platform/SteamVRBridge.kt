package dev.slimevr.desktop.platform

import dev.slimevr.VRServer
import dev.slimevr.VRServer.Companion.getNextLocalTrackerId
import dev.slimevr.VRServer.Companion.instance
import dev.slimevr.bridge.BridgeThread
import dev.slimevr.config.BridgeConfig
import dev.slimevr.desktop.platform.ProtobufMessages.*
import dev.slimevr.protocol.rpc.settings.RPCSettingsHandler
import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.TrackerPosition
import dev.slimevr.tracking.trackers.TrackerPosition.Companion.getByTrackerRole
import dev.slimevr.tracking.trackers.TrackerRole
import dev.slimevr.tracking.trackers.TrackerRole.Companion.getById
import dev.slimevr.tracking.trackers.TrackerUtils.getTrackerForSkeleton
import dev.slimevr.util.ann.VRServerThread
import io.eiren.util.collections.FastList
import solarxr_protocol.rpc.StatusData
import solarxr_protocol.rpc.StatusDataUnion
import solarxr_protocol.rpc.StatusSteamVRDisconnectedT

abstract class SteamVRBridge(
	protected val server: VRServer,
	threadName: String,
	bridgeName: String,
	protected val bridgeSettingsKey: String,
	protected val shareableTrackers: List<Tracker>,
) : ProtobufBridge(bridgeName),
	Runnable {
	protected val runnerThread: Thread = Thread(this, threadName)
	protected val config: BridgeConfig = server.configManager.vrConfig.getBridge(bridgeSettingsKey)

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
		val isWaistSteamVr = skeleton.hipTracker?.device?.isOpenVrDevice == true ||
			skeleton.waistTracker?.device?.isOpenVrDevice == true
		// Enable waist if skeleton has an spine tracker
		changeShareSettings(TrackerRole.WAIST, skeleton.hasSpineTracker && !isWaistSteamVr)

		// hasChest if waist and/or hip is on, and chest and/or upper chest is also on
		val hasChest = skeleton.upperChestTracker != null || skeleton.chestTracker != null
		val isChestSteamVr = skeleton.upperChestTracker?.device?.isOpenVrDevice == true ||
			skeleton.chestTracker?.device?.isOpenVrDevice == true
		changeShareSettings(
			TrackerRole.CHEST,
			hasChest && !isChestSteamVr,
		)

		// hasFeet if lower and/or upper leg tracker is on
		val hasLeftFoot =
			(skeleton.leftUpperLegTracker != null || skeleton.leftLowerLegTracker != null)
		val isLeftFootSteamVr =
			skeleton.leftLowerLegTracker?.device?.isOpenVrDevice == true ||
				skeleton.leftFootTracker?.device?.isOpenVrDevice == true

		val hasRightFoot =
			(skeleton.rightUpperLegTracker != null || skeleton.rightLowerLegTracker != null)
		val isRightFootSteamVr =
			skeleton.rightLowerLegTracker?.device?.isOpenVrDevice == true ||
				skeleton.rightFootTracker?.device?.isOpenVrDevice == true
		changeShareSettings(
			TrackerRole.LEFT_FOOT,
			hasLeftFoot && !isLeftFootSteamVr,
		)
		changeShareSettings(
			TrackerRole.RIGHT_FOOT,
			hasRightFoot && !isRightFootSteamVr,
		)

		// hasKnees is just hasFeet
		val isLeftKneeSteamVr = skeleton.leftUpperLegTracker?.device?.isOpenVrDevice == true

		val isRightKneeSteamVr = skeleton.rightUpperLegTracker?.device?.isOpenVrDevice == true
		changeShareSettings(TrackerRole.LEFT_KNEE, hasLeftFoot && !isLeftKneeSteamVr)
		changeShareSettings(TrackerRole.RIGHT_KNEE, hasRightFoot && !isRightKneeSteamVr)

		// hasElbows if an upper arm or a lower arm tracker is on
		val hasLeftElbow = skeleton.hasLeftArmTracker
		val isLeftElbowSteamVr = skeleton.leftUpperArmTracker?.device?.isOpenVrDevice == true ||
			skeleton.leftLowerArmTracker?.device?.isOpenVrDevice == true

		val hasRightElbow = skeleton.hasRightArmTracker
		val isRightElbowSteamVr = skeleton.rightUpperArmTracker?.device?.isOpenVrDevice == true ||
			skeleton.rightLowerArmTracker?.device?.isOpenVrDevice == true
		changeShareSettings(TrackerRole.LEFT_ELBOW, hasLeftElbow && !isLeftElbowSteamVr)
		changeShareSettings(TrackerRole.RIGHT_ELBOW, hasRightElbow && !isRightElbowSteamVr)

		// Hands aren't touched as they will override the controller's tracking
		// Return true to say that trackers were successfully toggled automatically
		return true
	}

	override fun getAutomaticSharedTrackers(): Boolean = config.automaticSharedTrackersToggling

	override fun setAutomaticSharedTrackers(value: Boolean) {
		if (value == config.automaticSharedTrackersToggling) return

		config.automaticSharedTrackersToggling = value
		if (value) {
			updateShareSettingsAutomatically()
			RPCSettingsHandler.sendSteamVRUpdatedSettings(instance.protocolAPI, instance.protocolAPI.rpcHandler)
		}
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
				trackerAdded.trackerName,
				trackerAdded.trackerSerial,
				"OpenVR", // TODO : We need the manufacturer
			)

		// Display name, needsReset and isHmd
		val displayName: String
		val isHmd = if (trackerAdded.trackerId == 0) {
			displayName = if (trackerAdded.trackerName == "HMD") {
				"SteamVR Driver HMD"
			} else {
				"Feeder App HMD"
			}
			true
		} else {
			displayName = trackerAdded.trackerName
			false
		}

		// trackerPosition
		val role = getById(trackerAdded.trackerRole)
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
			needsReset = true,
			isHmd = isHmd,
		)

		device.trackers[0] = tracker
		instance.deviceManager.addDevice(device)
		return tracker
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
		if (((lowestLevel >= 200) || (lowestLevel < 0)) ||
			(
				(trackerVoltage < 3.2) && (lowestLevel <= 0) ||
					((trackerVoltage >= 5) && (lowestLevel > 150))
				)
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

	/**
	 * When 0, then it means null
	 */
	protected var lastSteamVRStatus: Int = 0

	@BridgeThread
	protected fun reportDisconnected() {
		if (lastSteamVRStatus != 0) {
			return
		}
		val statusData = StatusSteamVRDisconnectedT()
		statusData.bridgeSettingsName = bridgeSettingsKey

		val status = StatusDataUnion()
		status.type = StatusData.StatusSteamVRDisconnected
		status.value = statusData
		lastSteamVRStatus = instance.statusSystem
			.addStatus(status, false).toInt()
	}

	@BridgeThread
	protected fun reportConnected() {
		if (lastSteamVRStatus == 0) {
			return
		}
		instance.statusSystem
			.removeStatus(lastSteamVRStatus.toUInt())
		lastSteamVRStatus = 0
	}
}
