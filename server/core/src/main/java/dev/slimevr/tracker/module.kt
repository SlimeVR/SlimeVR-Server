package dev.slimevr.tracker

import dev.slimevr.AppContextProvider
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.context.debug.DiffStyle
import dev.slimevr.context.debug.LoggingMiddleware
import dev.slimevr.device.DeviceOrigin
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.CoroutineScope
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.MagnetometerStatus
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.datatypes.hardware_info.ImuType

data class TrackerIdNum(val id: Int, val trackerNum: Int)

data class TrackerState(
	val id: Int,
	val name: String,
	val hardwareId: String,
	val sensorType: ImuType?,
	val bodyPart: BodyPart?,
	val customName: String?,
	val mountingOrientation: Quaternion?,
	val rawRotation: Quaternion,
	val acceleration: Vector3,
	val deviceId: Int,
	val origin: DeviceOrigin,
	val tps: UShort,
	val imuTemp: Float?,
	val position: Vector3?,
	val status: TrackerStatus,
	val completedRestCalibration: Boolean?,
	val magStatus: MagnetometerStatus,
)

sealed interface TrackerActions {
	data class Update(val transform: TrackerState.() -> TrackerState) : TrackerActions
	data class SetMagStatus(val status: MagnetometerStatus) : TrackerActions
	data class SetStatus(val status: TrackerStatus) : TrackerActions
	data class SetRotation(val rotation: Quaternion? = null, val acceleration: Vector3? = null) : TrackerActions
}

typealias TrackerContext = Context<TrackerState, TrackerActions>
typealias TrackerBehaviour = Behaviour<TrackerState, TrackerActions, Tracker>

class Tracker(
	val context: TrackerContext,
	val appContext: AppContextProvider,
) {
	companion object {
		fun create(
			scope: CoroutineScope,
			id: Int,
			name: String = "Tracker #$id",
			deviceId: Int,
			sensorType: ImuType?,
			hardwareId: String,
			origin: DeviceOrigin,
			appContext: AppContextProvider,
		): Tracker {
			val settings = appContext.config.settings
			val trackerConfigs = appContext.config.settings.context.state.value.data.trackers
			val savedConfig = trackerConfigs[hardwareId]
			val baseState = TrackerState(
				id = id,
				hardwareId = hardwareId,
				name = name,
				rawRotation = Quaternion.IDENTITY,
				acceleration = Vector3.NULL,
				bodyPart = null,
				mountingOrientation = null,
				origin = origin,
				deviceId = deviceId,
				customName = null,
				sensorType = sensorType,
				position = null,
				tps = 0u,
				imuTemp = null,
				status = TrackerStatus.DISCONNECTED,
				completedRestCalibration = false,
				magStatus = MagnetometerStatus.NOT_SUPPORTED,
			)
			val trackerState = if (savedConfig != null) {
				restoreFromConfig(baseState, savedConfig)
			} else {
				baseState
			}

			val behaviours = listOf(
				TrackerBasicBehaviour,
				TrackerConfigBehaviour(settings, hardwareId),
				TrackerTPSBehaviour,
				TrackerTapDetectionBehaviour,
				TrackerToSkeletonBehaviour,
			)
			val context = Context.create(
				initialState = trackerState,
				scope = scope,
				behaviours = behaviours,
				debugMiddleware = LoggingMiddleware(
					block = setOf(TrackerActions.SetRotation::class),
					diffStyle = DiffStyle.MULTILINE,
				),
				name = "Tracker[$hardwareId]",
			)
			val tracker = Tracker(context = context, appContext)
			behaviours.forEach { it.observe(tracker) }
			return tracker
		}
	}
}
