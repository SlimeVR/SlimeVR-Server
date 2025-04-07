package dev.slimevr.flightlist

import dev.slimevr.VRServer
import dev.slimevr.games.vrchat.VRCConfigListener
import dev.slimevr.games.vrchat.VRCConfigRecommendedValues
import dev.slimevr.games.vrchat.VRCConfigValidity
import dev.slimevr.games.vrchat.VRCConfigValues
import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.TrackerStatus
import dev.slimevr.tracking.trackers.TrackerStatusListener
import dev.slimevr.tracking.trackers.udp.TrackerDataType
import solarxr_protocol.datatypes.DeviceIdT
import solarxr_protocol.datatypes.TrackerIdT
import solarxr_protocol.rpc.FlightListExtraData
import solarxr_protocol.rpc.FlightListExtraDataUnion
import solarxr_protocol.rpc.FlightListNeedCalibrationT
import solarxr_protocol.rpc.FlightListStepId
import solarxr_protocol.rpc.FlightListStepT
import solarxr_protocol.rpc.FlightListStepVisibility
import solarxr_protocol.rpc.StatusTrackerErrorT
import solarxr_protocol.rpc.StatusTrackerResetT
import solarxr_protocol.rpc.StatusUnassignedHMDT
import java.util.concurrent.CopyOnWriteArrayList


interface FlightListListener {
	fun onStepUpdate(step: FlightListStepT)
}

class FlightListManager(private val vrServer: VRServer) : VRCConfigListener, TrackerStatusListener {

	private val listeners: MutableList<FlightListListener> = CopyOnWriteArrayList()
	private val steps: HashMap<Int, FlightListStepT> = hashMapOf()

	init {
		vrServer.vrcConfigManager.addListener(this)
		vrServer.addTrackerStatusListener(this)

		createSteps()
	}

	fun addListener(channel: FlightListListener) {
		listeners.add(channel)
	}

	fun removeListener(channel: FlightListListener) {
		listeners.removeIf { channel == it }
	}

	fun buildTrackersIds(trackers: List<Tracker>): Array<TrackerIdT> {
		return trackers.map { tracker ->
			TrackerIdT().apply {
				if (tracker.device != null) {
					deviceId = DeviceIdT().apply { id = tracker.device.id }
				}
				trackerNum = tracker.trackerNum
			}
		}.toTypedArray()
	}

	fun createSteps() {
		steps[FlightListStepId.TRACKERS_CALIBRATION] = FlightListStepT().apply {
			id = FlightListStepId.TRACKERS_CALIBRATION
			optional = true
			ignorable = false
			visibility = FlightListStepVisibility.ALWAYS
		}

		steps[FlightListStepId.FULL_RESET] = FlightListStepT().apply {
			id = FlightListStepId.FULL_RESET
			optional = false
			ignorable = false
			visibility = FlightListStepVisibility.ALWAYS
		}

		steps[FlightListStepId.STEAMVR_DISCONNECTED] = FlightListStepT().apply {
			id = FlightListStepId.STEAMVR_DISCONNECTED
			optional = true
			ignorable = true
			visibility = FlightListStepVisibility.WHEN_INVALID
		}

		steps[FlightListStepId.UNASSIGNED_HMD] = FlightListStepT().apply {
			id = FlightListStepId.UNASSIGNED_HMD
			optional = false
			ignorable = false
			visibility = FlightListStepVisibility.WHEN_INVALID
		}

		steps[FlightListStepId.TRACKER_ERROR] = FlightListStepT().apply {
			id = FlightListStepId.TRACKER_ERROR
			optional = false
			ignorable = false
			visibility = FlightListStepVisibility.WHEN_INVALID
		}

		if (vrServer.vrcConfigManager.isSupported) {
			steps[FlightListStepId.VRCHAT_SETTINGS] = FlightListStepT().apply {
				id = FlightListStepId.VRCHAT_SETTINGS
				optional = false
				ignorable = true
				visibility = FlightListStepVisibility.WHEN_INVALID
			}
		}

		require(steps.all { it.key == it.value.id }) {
			"Mismatching id/key pair"
		}
	}

	fun updateValidity(id: Int, valid: Boolean, beforeUpdate: ((step: FlightListStepT) -> Unit)? = null) {
		val step = steps[id] ?: return;
		if (step.valid != valid) {
			step.valid = valid
			if (beforeUpdate != null) {
				beforeUpdate(step)
			}
			listeners.forEach { it.onStepUpdate(step) }
		}
	}

	fun updateUnassignedHMD(valid: Boolean, trackerId: TrackerIdT?) {
		updateValidity(FlightListStepId.UNASSIGNED_HMD, valid) {
			if (trackerId == null) {
				it.extraData = null
			} else {
				it.extraData = FlightListExtraDataUnion().apply {
					type = FlightListExtraData.StatusUnassignedHMD
					value = StatusUnassignedHMDT().apply {
						this.trackerId = trackerId
					}
				}
			}
		}
	}

	fun updateRequireReset() {
		val trackerRequireReset = vrServer.allTrackers.filter {
			it.needsReset && it.trackerPosition != null && it.status.reset && (
				it.isImu()
					|| !it.statusResetRecently && it.trackerDataType != TrackerDataType.FLEX_ANGLE
				)
		}
		updateValidity(FlightListStepId.FULL_RESET, trackerRequireReset.isNotEmpty()) {
			if (trackerRequireReset.isNotEmpty()) {
				it.extraData = FlightListExtraDataUnion().apply {
					type = FlightListExtraData.StatusTrackerReset
					value = StatusTrackerResetT().apply {
						trackersId = buildTrackersIds(trackerRequireReset)
					}
				}
			} else {
				it.extraData = null;
			}
		}
	}


	fun updateTrackerCalibrationStep() {
		val trackersNeedCalibration = vrServer.allTrackers.filter { it.isImu() && it.hasCompletedRestCalibration == false }
		updateValidity(FlightListStepId.TRACKERS_CALIBRATION, trackersNeedCalibration.isNotEmpty()) {
			if (trackersNeedCalibration.isNotEmpty()) {
				it.extraData = FlightListExtraDataUnion().apply {
					type = FlightListExtraData.FlightListNeedCalibration
					value = FlightListNeedCalibrationT().apply {
						trackersId = buildTrackersIds(trackersNeedCalibration)
					}
				}
			}
		}
	}

	override fun onChange(
		validity: VRCConfigValidity,
		values: VRCConfigValues,
		recommended: VRCConfigRecommendedValues
	) {
		updateValidity(
			FlightListStepId.VRCHAT_SETTINGS,
			validity.javaClass.declaredFields.asSequence().all { p -> p.get(validity) == true }
		)
	}

	override fun onTrackerStatusChanged(tracker: Tracker, oldStatus: TrackerStatus, newStatus: TrackerStatus) {
		// Prevent useless computation if we are not dealing with error statuses
		if (oldStatus == TrackerStatus.ERROR || newStatus == TrackerStatus.ERROR) {
			val trackersWithError = vrServer.allTrackers.filter { it.status === TrackerStatus.ERROR }
			updateValidity(FlightListStepId.TRACKER_ERROR, trackersWithError.isNotEmpty()) {
				if (trackersWithError.isNotEmpty()) {
					it.extraData = FlightListExtraDataUnion().apply {
						type = FlightListExtraData.StatusTrackerError
						value = StatusTrackerErrorT().apply {
							trackersId = buildTrackersIds(trackersWithError)
						}
					}
				} else {
					it.extraData = null;
				}
			}
		}

		if (oldStatus.reset || newStatus.reset) {
			updateRequireReset()
		}
	}
}
