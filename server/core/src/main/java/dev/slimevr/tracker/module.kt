package dev.slimevr.tracker

import dev.slimevr.AppContextProvider
import dev.slimevr.config.Settings
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.context.debug.DiffStyle
import dev.slimevr.context.debug.LoggingMiddleware
import dev.slimevr.device.DeviceOrigin
import dev.slimevr.math.angle.Angle
import dev.slimevr.math.angle.AngleErrors
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.CoroutineScope
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.MagnetometerStatus
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.datatypes.hardware_info.ImuType
import kotlin.time.Duration

data class TrackerSensorIds(val trackerId: Int, val sensorId: Int)

/**
 * Indicates if the tracker is moving, at rest or recently at rest.
 */
enum class Motion {
	MOVING,
	RESTING,
	STARTED_MOVING,
}

data class YawResetSmoothing(
	val from: HeadingCorrection,
	val to: HeadingCorrection,
	val duration: Duration,
)

/**
 * Aggregates the yaw errors from multiple forces.
 */
data class YawErrors(
	val lockedError: AngleErrors,
	val centerError: AngleErrors,
	val neighborError: AngleErrors,
)

data class StayAlignedData(
	// Rotation of the tracker when it was locked
	val lockedRotation: Quaternion?,
	// Yaw correction to apply to tracker rotation
	val yawCorrection: Angle,
	// Alignment error that yaw correction attempts to minimize
	val yawErrors: YawErrors,
)

data class TrackerState(
	val id: Int,
	val name: String,
	val hardwareId: String,
	val imuType: ImuType?,
	val bodyPart: BodyPart?,
	val customName: String?,
	val mountingOrientation: HeadingAlignment,
	val restOrientation: RestOrientation,
	val rawRotation: RawRotation,
	val rotation: CalibratedRotation,
	val rawAcceleration: RawAcceleration,
	val acceleration: CalibratedAcceleration,
	val rawMagnetometer: Vector3, // TODO apply calibration
	val deviceId: Int,
	val origin: DeviceOrigin,
	val tps: UShort,
	val imuTemp: Float?,
	val position: Vector3?,
	val status: TrackerStatus,
	val completedRestCalibration: Boolean?,
	val magStatus: MagnetometerStatus,
	val sessionCalibration: SessionCalibration?,
	val motion: Motion,
	val yawResetSmoothing: YawResetSmoothing?,
	val stayAlignedData: StayAlignedData,
)

sealed interface TrackerActions {
	data class Update(val transform: TrackerState.() -> TrackerState) : TrackerActions
	data class SetMagStatus(val status: MagnetometerStatus) : TrackerActions
	data class SetStatus(val status: TrackerStatus) : TrackerActions
	data class SetRotation(val rotation: Quaternion? = null, val acceleration: Vector3? = null, val magnetometer: Vector3? = null, val position: Vector3? = null) : TrackerActions
	data class SetMountingOrientation(val mountingOrientation: HeadingAlignment) : TrackerActions
	data class SetRestOrientation(val restOrientation: Quaternion) : TrackerActions
	data class FullReset(val referenceRotation: Quaternion) : TrackerActions
	data class YawReset(val referenceRotation: Quaternion, val smoothTime: Duration = Duration.ZERO) : TrackerActions
	data class TickYawResetSmoothing(val heading: HeadingCorrection, val done: Boolean) : TrackerActions
	data class MountingReset(val referenceRotation: Quaternion, val yawOffset: Float) : TrackerActions
	data object ClearMountingReset : TrackerActions
	data class SetRestState(val restState: Motion) : TrackerActions
}

typealias TrackerContext = Context<TrackerState, TrackerActions>
typealias TrackerBehaviour = Behaviour<TrackerState, TrackerActions, Tracker>

class Tracker(
	val context: TrackerContext,
	val appContext: AppContextProvider,
	val settings: Settings,
) {
	fun startObserving() = context.observeAll(this)

	companion object {
		fun create(
			scope: CoroutineScope,
			id: Int,
			name: String = "Tracker #$id",
			bodyPart: BodyPart? = null,
			deviceId: Int,
			sensorType: ImuType? = null,
			hardwareId: String,
			origin: DeviceOrigin,
			appContext: AppContextProvider,
		): Tracker {
			val settings = appContext.config.settings
			val trackerConfigs = settings.context.state.value.data.trackers
			val savedConfig = trackerConfigs[hardwareId]
			val baseState = TrackerState(
				id = id,
				hardwareId = hardwareId,
				name = name,
				restOrientation = Quaternion.IDENTITY,
				rawRotation = Quaternion.IDENTITY,
				rotation = Quaternion.IDENTITY,
				rawAcceleration = Vector3.NULL,
				acceleration = Vector3.NULL,
				rawMagnetometer = Vector3.NULL,
				bodyPart = bodyPart,
				mountingOrientation = Quaternion.IDENTITY,
				origin = origin,
				deviceId = deviceId,
				customName = null,
				imuType = sensorType,
				position = null,
				tps = 0u,
				imuTemp = null,
				status = TrackerStatus.DISCONNECTED,
				completedRestCalibration = false,
				magStatus = MagnetometerStatus.NOT_SUPPORTED,
				sessionCalibration = null,
				motion = Motion.RESTING,
				yawResetSmoothing = null,
				stayAlignedData = StayAlignedData(
					null,
					Angle.ZERO,
					YawErrors(
						AngleErrors(),
						AngleErrors(),
						AngleErrors(),
					),
				),
			)
			val trackerState = if (savedConfig != null) {
				restoreFromConfig(baseState, savedConfig, settings.context.state.value.data.resetsConfig.saveMountingReset)
			} else {
				baseState
			}

			val behaviours = listOf(
				TrackerBasicBehaviour(),
				TrackerYawResetSmoothingBehaviour(),
				TrackerDefaultMountingOrientationBehaviour(),
				TrackerConfigBehaviour(settings, hardwareId),
				TrackerTPSBehaviour(),
				TrackerMotionDetectionBehaviour(),
				TrackerToSkeletonBehaviour(),
				TrackerRestOrientationBehaviour(settings),
				TrackerStayAlignedBehaviour(settings, appContext.stayAlignedManager),
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
			val tracker = Tracker(context = context, appContext, settings)
			tracker.startObserving()
			return tracker
		}
	}
}
