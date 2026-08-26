package dev.slimevr.tracker

import dev.slimevr.AppContextProvider
import dev.slimevr.config.Settings
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.context.debug.DiffStyle
import dev.slimevr.context.debug.LoggingMiddleware
import dev.slimevr.math.angle.Angle
import dev.slimevr.tracker.behaviours.TrackerBasicBehaviour
import dev.slimevr.tracker.behaviours.TrackerConfigBehaviour
import dev.slimevr.tracker.behaviours.TrackerDefaultMountingOrientationBehaviour
import dev.slimevr.tracker.behaviours.TrackerMotionDetectionBehaviour
import dev.slimevr.tracker.behaviours.TrackerRestOrientationBehaviour
import dev.slimevr.tracker.behaviours.TrackerStayAlignedBehaviour
import dev.slimevr.tracker.behaviours.TrackerToSkeletonBehaviour
import dev.slimevr.tracker.behaviours.TrackerYawResetSmoothingBehaviour
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.CoroutineScope
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.DeviceOrigin
import solarxr_protocol.datatypes.MagnetometerStatus
import solarxr_protocol.datatypes.MountingMethod
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.datatypes.hardware_info.ImuType
import solarxr_protocol.datatypes.hardware_info.TrackerDataType
import kotlin.time.Duration

// A tracker is initialized as ROTATING to prevent TapDetection and Stay Aligned from running immediately.
enum class Motion {
	ROTATING,
	RESTING,
	STARTED_ROTATING,
}

data class YawResetSmoothing(
	val from: HeadingCorrection,
	val to: HeadingCorrection,
	val duration: Duration,
)

data class StayAlignedData(
	// Whether stay aligned is enabled
	val enabled: Boolean = false,
	// Rotation of the tracker when it started resting
	val lockedRotation: Quaternion? = null,
	// Yaw correction to apply to the tracker's rotation
	val yawCorrection: Angle = Angle.ZERO,
)

data class TrackerState(
	val id: Int,
	val deviceId: Int,
	val origin: DeviceOrigin,
	val driverName: String?,
	val hardwareId: String,
	val name: String,
	val imuType: ImuType?,
	val bodyPart: BodyPart?,
	val customName: String?,
	val trackerDataType: TrackerDataType, // TODO
	val lastMountingMethod: MountingMethod,
	val mountingOrientation: HeadingAlignment,
	val restOrientation: RestOrientation,
	val sessionCalibration: SessionCalibration,
	val rawRotation: RawRotation,
	val rotation: CalibratedRotation,
	val rawAcceleration: RawAcceleration,
	val acceleration: CalibratedAcceleration,
	val rawMagnetometer: Vector3,
	val position: Vector3?,
	val imuTemp: Float?,
	val accumulatedTicks: UShort,
	val tps: UShort,
	val status: TrackerStatus,
	val completedRestCalibration: Boolean?,
	val magStatus: MagnetometerStatus,
	val motion: Motion,
	val yawResetSmoothing: YawResetSmoothing?,
	val stayAlignedData: StayAlignedData,
)

/**
 * Returns the first OK or SLEEPING tracker state that matches the body part, or null
 */
fun List<TrackerState>.getFirstFineFor(bodyPart: BodyPart): TrackerState? = this.firstOrNull {
	it.bodyPart == bodyPart &&
		(it.status == TrackerStatus.OK || it.status == TrackerStatus.SLEEPING)
}

/**
 * Returns all the OK or SLEEPING tracker states that matches the body parts
 */
fun List<TrackerState>.getAllFineFor(bodyParts: List<BodyPart>): List<TrackerState> = this.filter {
	it.bodyPart in bodyParts &&
		(it.status == TrackerStatus.OK || it.status == TrackerStatus.SLEEPING)
}

sealed interface TrackerActions {
	data class Update(val transform: TrackerState.() -> TrackerState) : TrackerActions
	data class SetMagStatus(val status: MagnetometerStatus) : TrackerActions
	data class SetStatus(val status: TrackerStatus) : TrackerActions
	data class SetDriverName(val driverName: String?) : TrackerActions

	/**
	 * Do not instantiate [SetRotation] directly. Use [Tracker.setRotation] instead so `headTrackerRotation` is automatically included.
	 */
	data class SetRotation
	@Deprecated(
		message = "Do not instantiate SetRotation directly. Use tracker.setRotation(...) instead so headTrackerRotation is automatically included.",
		level = DeprecationLevel.ERROR,
	)
	constructor(
		val rotation: Quaternion? = null,
		val acceleration: Vector3? = null,
		val magnetometer: Vector3? = null,
		val position: Vector3? = null,
		val newData: Boolean = true,
		val headTrackerRotation: Quaternion? = null,
	) : TrackerActions
	data class SetMountingOrientation(val mountingOrientation: HeadingAlignment) : TrackerActions
	data class SetRestOrientation(val restOrientation: Quaternion) : TrackerActions
	data class FullReset(val referenceRotation: Quaternion, val resetPositionalHeadAttitude: Boolean = false) : TrackerActions
	data class YawReset(val referenceRotation: Quaternion, val smoothTime: Duration = Duration.ZERO) : TrackerActions
	data class TickYawResetSmoothing(val heading: HeadingCorrection, val done: Boolean) : TrackerActions
	data class PoseMountingReset(val referenceRotation: Quaternion, val yawOffset: Float) : TrackerActions
	data object ClearMountingReset : TrackerActions
	data class SetMotion(val motion: Motion) : TrackerActions
	data class SetYawCorrection(val yawCorrection: Angle) : TrackerActions
	data class SetStayAlignedEnabled(val enabled: Boolean) : TrackerActions
}

typealias TrackerContext = Context<TrackerState, TrackerActions>
typealias TrackerBehaviour = Behaviour<Tracker>

class Tracker(
	val context: TrackerContext,
	val appContext: AppContextProvider,
	val settings: Settings,
) {
	fun startObserving() = context.observeAll(this)

	fun setRotation(
		rotation: Quaternion? = null,
		acceleration: Vector3? = null,
		magnetometer: Vector3? = null,
		position: Vector3? = null,
		newData: Boolean = true,
	) {
		val headTrackerRotation = appContext.server.context.state.value.trackers.values
			.map { it.context.state.value }
			.getFirstFineFor(BodyPart.HEAD)?.rotation
		context.dispatch(
			@Suppress("DEPRECATION_ERROR")
			TrackerActions.SetRotation(
				rotation = rotation,
				acceleration = acceleration,
				magnetometer = magnetometer,
				position = position,
				newData = newData,
				headTrackerRotation = headTrackerRotation,
			),
		)
	}

	companion object {
		fun create(
			scope: CoroutineScope,
			id: Int,
			name: String = "Tracker #$id",
			bodyPart: BodyPart? = null,
			deviceId: Int,
			imuType: ImuType? = null,
			hardwareId: String,
			origin: DeviceOrigin,
			driverName: String? = null,
			appContext: AppContextProvider,
		): Tracker {
			val settings = appContext.config.settings
			val trackerConfigs = settings.context.state.value.data.trackers
			val savedConfig = trackerConfigs[hardwareId]
			val baseState = DEFAULT_STATE.copy(
				id = id,
				deviceId = deviceId,
				origin = origin,
				driverName = driverName,
				hardwareId = hardwareId,
				name = name,
				imuType = imuType,
				bodyPart = bodyPart,
				stayAlignedData = DEFAULT_STATE.stayAlignedData.copy(enabled = settings.context.state.value.data.stayAlignedConfig.enabled),
			)
			val trackerState = if (savedConfig != null) {
				TrackerConfigBehaviour.restoreFromConfig(baseState, savedConfig, settings.context.state.value.data.resetsConfig.saveMountingReset)
			} else {
				baseState
			}

			val behaviours = listOf(
				TrackerBasicBehaviour(),
				TrackerYawResetSmoothingBehaviour(),
				TrackerDefaultMountingOrientationBehaviour(),
				TrackerConfigBehaviour(settings, hardwareId),
				TrackerMotionDetectionBehaviour(),
				TrackerToSkeletonBehaviour(),
				TrackerRestOrientationBehaviour(settings),
				TrackerStayAlignedBehaviour(settings),
			)
			val context = Context.create(
				initialState = trackerState,
				scope = scope,
				reducer = ::reduce,
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

		val DEFAULT_STATE = TrackerState(
			id = 0,
			deviceId = 0,
			origin = DeviceOrigin.UDP,
			driverName = null,
			hardwareId = "defaultHardwareId",
			name = "defaultTracker",
			imuType = ImuType.BNO085,
			bodyPart = null,
			customName = null,
			trackerDataType = TrackerDataType.ROTATION,
			lastMountingMethod = MountingMethod.MANUAL,
			mountingOrientation = Quaternion.IDENTITY,
			restOrientation = Quaternion.IDENTITY,
			sessionCalibration = SessionCalibration(),
			rawRotation = Quaternion.IDENTITY,
			rotation = Quaternion.IDENTITY,
			rawAcceleration = Vector3.NULL,
			acceleration = Vector3.NULL,
			rawMagnetometer = Vector3.NULL,
			position = null,
			imuTemp = null,
			accumulatedTicks = 0u,
			tps = 0u,
			status = TrackerStatus.DISCONNECTED,
			completedRestCalibration = false,
			magStatus = MagnetometerStatus.NOT_SUPPORTED,
			motion = Motion.ROTATING,
			yawResetSmoothing = null,
			stayAlignedData = StayAlignedData(),
		)
	}
}
