package dev.slimevr.trackingchecklist

import dev.slimevr.VRServer
import dev.slimevr.bridge.ISteamVRBridge
import dev.slimevr.config.MountingMethods
import dev.slimevr.games.vrchat.VRCConfigListener
import dev.slimevr.games.vrchat.VRCConfigRecommendedValues
import dev.slimevr.games.vrchat.VRCConfigValidity
import dev.slimevr.games.vrchat.VRCConfigValues
import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.TrackerStatus
import dev.slimevr.tracking.trackers.TrackerUtils
import dev.slimevr.tracking.trackers.udp.TrackerDataType
import solarxr_protocol.datatypes.DeviceIdT
import solarxr_protocol.datatypes.TrackerIdT
import solarxr_protocol.rpc.*
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.concurrent.timerTask

interface TrackingChecklistListener {
	fun onStepsUpdate()
}

class TrackingChecklistManager(private val vrServer: VRServer) : VRCConfigListener {

	private val listeners: MutableList<TrackingChecklistListener> = CopyOnWriteArrayList()
	val steps: MutableList<TrackingChecklistStepT> = mutableListOf()

	private val updateTrackingChecklistTimer = Timer("TrackingChecklistTimer")

	// Simple flag set to true if reset mounting was performed at least once.
	// This value is only runtime and never saved
	var resetMountingCompleted = false
	var feetResetMountingCompleted = false

	init {
		createSteps()
		vrServer.vrcConfigManager.addListener(this)

		updateTrackingChecklistTimer.scheduleAtFixedRate(
			timerTask {
				updateChecklist()
			},
			0,
			1000,
		)
	}

	fun addListener(channel: TrackingChecklistListener) {
		listeners.add(channel)
	}

	fun removeListener(channel: TrackingChecklistListener) {
		listeners.removeIf { channel == it }
	}

	fun buildTrackersIds(trackers: List<Tracker>): Array<TrackerIdT> = trackers.map { tracker ->
		TrackerIdT().apply {
			if (tracker.device != null) {
				deviceId = DeviceIdT().apply { id = tracker.device.id }
			}
			trackerNum = tracker.trackerNum
		}
	}.toTypedArray()

	private fun createSteps() {
		steps.add(
			TrackingChecklistStepT().apply {
				id = TrackingChecklistStepId.NETWORK_PROFILE_PUBLIC
				enabled = vrServer.networkProfileChecker.isSupported
				optional = false
				ignorable = true
				visibility = TrackingChecklistStepVisibility.WHEN_INVALID
			},
		)

		steps.add(
			TrackingChecklistStepT().apply {
				id = TrackingChecklistStepId.STEAMVR_DISCONNECTED
				enabled = false
				optional = false
				ignorable = true
				visibility = TrackingChecklistStepVisibility.WHEN_INVALID
			},
		)

		steps.add(
			TrackingChecklistStepT().apply {
				id = TrackingChecklistStepId.TRACKER_ERROR
				valid = true // Default to valid
				enabled = true
				optional = false
				ignorable = false
				visibility = TrackingChecklistStepVisibility.WHEN_INVALID
			},
		)

		steps.add(
			TrackingChecklistStepT().apply {
				id = TrackingChecklistStepId.TRACKERS_REST_CALIBRATION
				enabled = true
				optional = false
				ignorable = true
				visibility = TrackingChecklistStepVisibility.ALWAYS
			},
		)

		steps.add(
			TrackingChecklistStepT().apply {
				id = TrackingChecklistStepId.FULL_RESET
				enabled = true
				optional = false
				ignorable = false
				visibility = TrackingChecklistStepVisibility.ALWAYS
			},
		)

		steps.add(
			TrackingChecklistStepT().apply {
				id = TrackingChecklistStepId.MOUNTING_CALIBRATION
				valid = false
				enabled = vrServer.configManager.vrConfig.resetsConfig.lastMountingMethod == MountingMethods.AUTOMATIC
				optional = false
				ignorable = true
				visibility = TrackingChecklistStepVisibility.ALWAYS
			},
		)

		steps.add(
			TrackingChecklistStepT().apply {
				id = TrackingChecklistStepId.FEET_MOUNTING_CALIBRATION
				valid = false
				enabled = false
				optional = false
				ignorable = true
				visibility = TrackingChecklistStepVisibility.ALWAYS
			},
		)

		steps.add(
			TrackingChecklistStepT().apply {
				id = TrackingChecklistStepId.UNASSIGNED_HMD
				enabled = true
				optional = false
				ignorable = false
				visibility = TrackingChecklistStepVisibility.WHEN_INVALID
			},
		)

		steps.add(
			TrackingChecklistStepT().apply {
				id = TrackingChecklistStepId.STAY_ALIGNED_CONFIGURED
				enabled = true
				optional = true
				ignorable = true
				visibility = TrackingChecklistStepVisibility.WHEN_INVALID
			},
		)

		steps.add(
			TrackingChecklistStepT().apply {
				id = TrackingChecklistStepId.VRCHAT_SETTINGS
				enabled = vrServer.vrcConfigManager.isSupported
				valid = true
				optional = true
				ignorable = true
				visibility = TrackingChecklistStepVisibility.WHEN_INVALID
			},
		)
	}

	fun updateChecklist() {
		val assignedTrackers =
			vrServer.allTrackers.filter { it.trackerPosition != null && it.status != TrackerStatus.DISCONNECTED }
		val imuTrackers =
			assignedTrackers.filter { it.isImu() && it.trackerDataType != TrackerDataType.FLEX_ANGLE }

		val trackersWithError =
			imuTrackers.filter { it.status == TrackerStatus.ERROR }
		updateValidity(
			TrackingChecklistStepId.TRACKER_ERROR,
			trackersWithError.isEmpty(),
		) {
			if (trackersWithError.isNotEmpty()) {
				it.extraData = TrackingChecklistExtraDataUnion().apply {
					type = TrackingChecklistExtraData.TrackingChecklistTrackerError
					value = TrackingChecklistTrackerErrorT().apply {
						trackersId = buildTrackersIds(trackersWithError)
					}
				}
			} else {
				it.extraData = null
			}
		}

		val trackerRequireReset = imuTrackers.filter {
			it.status !== TrackerStatus.ERROR && !it.isInternal && it.allowReset && it.needReset
		}
		// We ask for a full reset if you need to do mounting calibration but cant because you haven't done full reset in a while
		// or if you have trackers that need reset after re-assigning
		val needFullReset = (!resetMountingCompleted && !vrServer.serverGuards.canDoMounting) || trackerRequireReset.isNotEmpty()
		updateValidity(TrackingChecklistStepId.FULL_RESET, !needFullReset) {
			it.enabled = imuTrackers.isNotEmpty()
			if (trackerRequireReset.isNotEmpty()) {
				it.extraData = TrackingChecklistExtraDataUnion().apply {
					type = TrackingChecklistExtraData.TrackingChecklistTrackerReset
					value = TrackingChecklistTrackerResetT().apply {
						trackersId = buildTrackersIds(trackerRequireReset)
					}
				}
				resetMountingCompleted = false
				feetResetMountingCompleted = false
			} else {
				it.extraData = null
			}
		}
		val hmd =
			vrServer.allTrackers.firstOrNull { it.status != TrackerStatus.DISCONNECTED && it.isHmd && !it.isInternal && it.status.sendData }
		val assignedHmd = hmd == null || vrServer.humanPoseManager.skeleton.headTracker != null
		updateValidity(TrackingChecklistStepId.UNASSIGNED_HMD, assignedHmd) {
			if (!assignedHmd) {
				it.extraData = TrackingChecklistExtraDataUnion().apply {
					type = TrackingChecklistExtraData.TrackingChecklistUnassignedHMD
					value = TrackingChecklistUnassignedHMDT().apply {
						trackerId = TrackerIdT().apply {
							if (hmd.device != null) {
								deviceId = DeviceIdT().apply { id = hmd.device.id }
							}
							trackerNum = hmd.trackerNum
						}
					}
				}
			} else {
				it.extraData = null
			}
		}

		val trackersNeedCalibration = imuTrackers.filter {
			it.hasCompletedRestCalibration == false
		}
		updateValidity(
			TrackingChecklistStepId.TRACKERS_REST_CALIBRATION,
			trackersNeedCalibration.isEmpty(),
		) {
			// Don't show the step if none of the trackers connected support IMU calibration
			it.enabled = imuTrackers.any { t ->
				t.hasCompletedRestCalibration != null
			}
			if (trackersNeedCalibration.isNotEmpty()) {
				it.extraData = TrackingChecklistExtraDataUnion().apply {
					type = TrackingChecklistExtraData.TrackingChecklistNeedCalibration
					value = TrackingChecklistNeedCalibrationT().apply {
						trackersId = buildTrackersIds(trackersNeedCalibration)
					}
				}
			} else {
				it.extraData = null
			}
		}

		val steamVRBridge = vrServer.getVRBridge(ISteamVRBridge::class.java)
		if (steamVRBridge != null) {
			val steamvrConnected = steamVRBridge.isConnected()
			updateValidity(
				TrackingChecklistStepId.STEAMVR_DISCONNECTED,
				steamvrConnected,
			) {
				it.enabled = true
				if (!steamvrConnected) {
					it.extraData = TrackingChecklistExtraDataUnion().apply {
						type = TrackingChecklistExtraData.TrackingChecklistSteamVRDisconnected
						value = TrackingChecklistSteamVRDisconnectedT().apply {
							bridgeSettingsName = steamVRBridge.getBridgeConfigKey()
						}
					}
				} else {
					it.extraData = null
				}
			}
		}

		if (vrServer.networkProfileChecker.isSupported) {
			updateValidity(TrackingChecklistStepId.NETWORK_PROFILE_PUBLIC, vrServer.networkProfileChecker.publicNetworks.isEmpty()) {
				if (vrServer.networkProfileChecker.publicNetworks.isNotEmpty()) {
					it.extraData = TrackingChecklistExtraDataUnion().apply {
						type = TrackingChecklistExtraData.TrackingChecklistPublicNetworks
						value = TrackingChecklistPublicNetworksT().apply {
							adapters = vrServer.networkProfileChecker.publicNetworks.map { it.name }.toTypedArray()
						}
					}
				} else {
					it.extraData = null
				}
			}
		}

		updateValidity(TrackingChecklistStepId.MOUNTING_CALIBRATION, resetMountingCompleted) {
			it.enabled = vrServer.configManager.vrConfig.resetsConfig.lastMountingMethod == MountingMethods.AUTOMATIC && imuTrackers.isNotEmpty()
		}

		updateValidity(TrackingChecklistStepId.FEET_MOUNTING_CALIBRATION, feetResetMountingCompleted) {
			it.enabled =
				vrServer.configManager.vrConfig.resetsConfig.lastMountingMethod == MountingMethods.AUTOMATIC &&
				!vrServer.configManager.vrConfig.resetsConfig.resetMountingFeet &&
				imuTrackers.any { t -> TrackerUtils.feetsBodyParts.contains(t.trackerPosition?.bodyPart) }
		}

		updateValidity(TrackingChecklistStepId.STAY_ALIGNED_CONFIGURED, vrServer.configManager.vrConfig.stayAlignedConfig.enabled)

		listeners.forEach { it.onStepsUpdate() }
	}

	private fun updateValidity(id: Int, valid: Boolean, beforeUpdate: ((step: TrackingChecklistStepT) -> Unit)? = null) {
		require(id != TrackingChecklistStepId.UNKNOWN) {
			"id is unknown"
		}
		val step = steps.find { it.id == id } ?: error("step does not exists")
		step.valid = valid
		if (beforeUpdate != null) {
			beforeUpdate(step)
		}
	}

	override fun onChange(
		validity: VRCConfigValidity,
		values: VRCConfigValues,
		recommended: VRCConfigRecommendedValues,
		muted: List<String>,
	) {
		updateValidity(
			TrackingChecklistStepId.VRCHAT_SETTINGS,
			VRCConfigValidity::class.java.declaredFields.asSequence().all { p ->
				p.isAccessible = true
				return@all p.get(validity) == true || muted.contains(p.name)
			},
		)
		listeners.forEach { it.onStepsUpdate() }
	}

	fun ignoreStep(step: TrackingChecklistStepT, ignore: Boolean) {
		if (!step.ignorable) return
		val ignoredSteps = vrServer.configManager.vrConfig.trackingChecklist.ignoredStepsIds
		if (ignore && !ignoredSteps.contains(step.id)) {
			ignoredSteps.add(step.id)
		} else if (!ignore) {
			ignoredSteps.remove(step.id)
		}
		vrServer.configManager.saveConfig()
	}
}
