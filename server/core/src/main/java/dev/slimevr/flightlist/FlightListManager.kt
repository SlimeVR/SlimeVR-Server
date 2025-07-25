package dev.slimevr.flightlist

import dev.slimevr.VRServer
import dev.slimevr.bridge.ISteamVRBridge
import dev.slimevr.config.MountingMethods
import dev.slimevr.games.vrchat.VRCConfigListener
import dev.slimevr.games.vrchat.VRCConfigRecommendedValues
import dev.slimevr.games.vrchat.VRCConfigValidity
import dev.slimevr.games.vrchat.VRCConfigValues
import dev.slimevr.reset.ResetListener
import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.TrackerStatus
import dev.slimevr.tracking.trackers.udp.TrackerDataType
import io.eiren.util.OperatingSystem
import solarxr_protocol.datatypes.DeviceIdT
import solarxr_protocol.datatypes.TrackerIdT
import solarxr_protocol.rpc.*
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.concurrent.timerTask
import kotlin.system.measureTimeMillis

interface FlightListListener {
	fun onStepsUpdate()
}

class FlightListManager(private val vrServer: VRServer) : VRCConfigListener {

	private val listeners: MutableList<FlightListListener> = CopyOnWriteArrayList()
	val steps: MutableList<FlightListStepT> = mutableListOf()

	private val updateFlightListTimer = Timer("FetchVRCConfigTimer")

	// Simple flag set to true if reset mounting was performed at least once.
	// This value is only runtime and never saved
	var resetMountingCompleted = false

	init {
		vrServer.vrcConfigManager.addListener(this)

		createSteps()
		updateFlightListTimer.scheduleAtFixedRate(
			timerTask {
				updateFlightlist()
			},
			0,
			1000,
		)
	}

	fun addListener(channel: FlightListListener) {
		listeners.add(channel)
	}

	fun removeListener(channel: FlightListListener) {
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
			FlightListStepT().apply {
				id = FlightListStepId.NETWORK_PROFILE_PUBLIC
				enabled = vrServer.networkProfileChecker.isSupported
				optional = false
				ignorable = true
				visibility = FlightListStepVisibility.WHEN_INVALID
			},
		)

		steps.add(
			FlightListStepT().apply {
				id = FlightListStepId.STEAMVR_DISCONNECTED
				enabled = true
				optional = false
				ignorable = true
				visibility = FlightListStepVisibility.WHEN_INVALID
			},
		)

		steps.add(
			FlightListStepT().apply {
				id = FlightListStepId.TRACKER_ERROR
				valid = true // Default to valid
				enabled = true
				optional = false
				ignorable = false
				visibility = FlightListStepVisibility.WHEN_INVALID
			},
		)

		steps.add(
			FlightListStepT().apply {
				id = FlightListStepId.TRACKERS_REST_CALIBRATION
				enabled = true
				optional = false
				ignorable = true
				visibility = FlightListStepVisibility.ALWAYS
			},
		)

		steps.add(
			FlightListStepT().apply {
				id = FlightListStepId.FULL_RESET
				enabled = true
				optional = false
				ignorable = false
				visibility = FlightListStepVisibility.ALWAYS
			},
		)

		steps.add(
			FlightListStepT().apply {
				id = FlightListStepId.MOUNTING_CALIBRATION
				valid = false
				enabled = vrServer.configManager.vrConfig.resetsConfig.preferedMountingMethod == MountingMethods.AUTOMATIC
				optional = false
				ignorable = true
				visibility = FlightListStepVisibility.ALWAYS
			},
		)

		steps.add(
			FlightListStepT().apply {
				id = FlightListStepId.UNASSIGNED_HMD
				enabled = true
				optional = false
				ignorable = false
				visibility = FlightListStepVisibility.WHEN_INVALID
			},
		)

		steps.add(
			FlightListStepT().apply {
				id = FlightListStepId.STAY_ALIGNED_CONFIGURED
				enabled = true
				optional = true
				ignorable = true
				visibility = FlightListStepVisibility.WHEN_INVALID
			},
		)

		steps.add(
			FlightListStepT().apply {
				id = FlightListStepId.VRCHAT_SETTINGS
				enabled = vrServer.vrcConfigManager.isSupported
				optional = true
				ignorable = true
				visibility = FlightListStepVisibility.WHEN_INVALID
			},
		)
	}

	fun updateFlightlist() {
		println(
			measureTimeMillis {
				val assignedTrackers =
					vrServer.allTrackers.filter { it.trackerPosition != null && it.status != TrackerStatus.DISCONNECTED }
				val imuTrackers =
					assignedTrackers.filter { it.isImu() && it.trackerDataType != TrackerDataType.FLEX_ANGLE }

				val trackersWithError =
					imuTrackers.filter { it.status === TrackerStatus.ERROR }
				updateValidity(
					FlightListStepId.TRACKER_ERROR,
					trackersWithError.isEmpty(),
				) {
					if (trackersWithError.isNotEmpty()) {
						it.extraData = FlightListExtraDataUnion().apply {
							type = FlightListExtraData.FlightListTrackerError
							value = FlightListTrackerErrorT().apply {
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
				updateValidity(FlightListStepId.FULL_RESET, trackerRequireReset.isEmpty()) {
					if (trackerRequireReset.isNotEmpty()) {
						it.extraData = FlightListExtraDataUnion().apply {
							type = FlightListExtraData.FlightListTrackerReset
							value = FlightListTrackerResetT().apply {
								trackersId = buildTrackersIds(trackerRequireReset)
							}
						}
					} else {
						it.extraData = null
					}
				}

				val hmd =
					assignedTrackers.firstOrNull { it.isHmd && !it.isInternal && it.status.sendData }
				val assignedHmd = hmd == null || vrServer.humanPoseManager.skeleton.headTracker != null
				updateValidity(FlightListStepId.UNASSIGNED_HMD, assignedHmd) {
					if (!assignedHmd) {
						it.extraData = FlightListExtraDataUnion().apply {
							type = FlightListExtraData.FlightListUnassignedHMD
							value = FlightListUnassignedHMDT().apply {
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
					FlightListStepId.TRACKERS_REST_CALIBRATION,
					trackersNeedCalibration.isEmpty(),
				) {
					// Don't show the step if none of the trackers connected support IMU calibration
					it.enabled = imuTrackers.any { t ->
						t.hasCompletedRestCalibration != null
					}
					if (trackersNeedCalibration.isNotEmpty()) {
						it.extraData = FlightListExtraDataUnion().apply {
							type = FlightListExtraData.FlightListNeedCalibration
							value = FlightListNeedCalibrationT().apply {
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
						FlightListStepId.STEAMVR_DISCONNECTED,
						steamvrConnected,
					) {
						if (!steamvrConnected) {
							it.extraData = FlightListExtraDataUnion().apply {
								type = FlightListExtraData.FlightListSteamVRDisconnected
								value = FlightListSteamVRDisconnectedT().apply {
									bridgeSettingsName = steamVRBridge.getBridgeConfigKey()
								}
							}
						} else {
							it.extraData = null
						}
					}
				}

				if (vrServer.networkProfileChecker.isSupported) {
					updateValidity(FlightListStepId.NETWORK_PROFILE_PUBLIC, vrServer.networkProfileChecker.publicNetworks.isEmpty()) {
						if (vrServer.networkProfileChecker.publicNetworks.isNotEmpty()) {
							it.extraData = FlightListExtraDataUnion().apply {
								type = FlightListExtraData.FlightListPublicNetworks
								value = FlightListPublicNetworksT().apply {
									adapters = vrServer.networkProfileChecker.publicNetworks.map { it.name }.toTypedArray()
								}
							}
						} else {
							it.extraData = null;
						}
					}
				}

				updateValidity(FlightListStepId.MOUNTING_CALIBRATION, resetMountingCompleted) {
					it.enabled = vrServer.configManager.vrConfig.resetsConfig.preferedMountingMethod == MountingMethods.AUTOMATIC
				}

				updateValidity(FlightListStepId.STAY_ALIGNED_CONFIGURED, vrServer.configManager.vrConfig.stayAlignedConfig.enabled)

				listeners.forEach { it.onStepsUpdate() }
			},
		)
	}

	private fun updateValidity(id: Int, valid: Boolean, beforeUpdate: ((step: FlightListStepT) -> Unit)? = null) {
		require(id != FlightListStepId.UNKNOWN) {
			"id is unknown"
		}
		val step = steps.find { it.id == id } ?: return
		step.valid = valid
		if (beforeUpdate != null) {
			beforeUpdate(step)
		}
	}


	override fun onChange(
		validity: VRCConfigValidity,
		values: VRCConfigValues,
		recommended: VRCConfigRecommendedValues,
		muted: List<String>
	) {
		updateValidity(
			FlightListStepId.VRCHAT_SETTINGS,
			VRCConfigValidity::class.java.declaredFields.asSequence().all { p ->
				p.isAccessible = true
				return@all p.get(validity) == true || muted.contains(p.name)
			},
		)
		listeners.forEach { it.onStepsUpdate() }
	}

	fun ignoreStep(step: FlightListStepT, ignore: Boolean) {
		if (!step.ignorable) return;
		val ignoredSteps = vrServer.configManager.vrConfig.flightList.ignoredStepsIds
		if (ignore && !ignoredSteps.contains(step.id)) {
			ignoredSteps.add(step.id)
		} else if (!ignore) {
			ignoredSteps.remove(step.id)
		}
		vrServer.configManager.saveConfig()
	}

}
