package dev.slimevr.tracker

import com.jme3.math.FastMath
import dev.slimevr.config.Settings
import dev.slimevr.logging.AppLogger
import dev.slimevr.math.angle.Angle
import dev.slimevr.resets.ResetBodyParts
import dev.slimevr.skeleton.SkeletonActions
import dev.slimevr.stayaligned.StayAlignedDefaults.IMU_TO_YAW_CORRECTION
import dev.slimevr.stayaligned.StayAlignedDefaults.YAW_CORRECTION_DEFAULT
import dev.slimevr.stayaligned.StayAlignedManager
import dev.slimevr.stayaligned.todo.AdjustTrackerYaw
import dev.slimevr.util.inFloatingSeconds
import dev.slimevr.util.timeSource
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.MagnetometerStatus
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.rpc.ArmsResetMode
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class TrackerBasicBehaviour(private val stayAlignedManager: StayAlignedManager) : TrackerBehaviour {
	override fun reduce(state: TrackerState, action: TrackerActions) = when (action) {
		is TrackerActions.Update -> action.transform(state)

		is TrackerActions.SetMagStatus -> state.copy(magStatus = action.status)

		is TrackerActions.SetStatus -> state.copy(status = action.status)

		is TrackerActions.SetRotation -> {
			val rawRotation: RawRotation = action.rotation ?: state.rawRotation
			val rawAcceleration: RawAcceleration = action.acceleration ?: state.rawAcceleration
			val rawMagnetometer = action.magnetometer ?: state.rawMagnetometer
			val position = action.position ?: state.position

			val cal = state.sessionCalibration

			val hideYawCorrection = stayAlignedManager.context.state.value.hideYawCorrection
			val yawCorrectedRawRotation = Quaternion.rotationAroundYAxis(state.stayAlignedData.yawCorrection.toRad()) * rawRotation

			// Rotation calibration
			val (rotation: CalibratedRotation, forceStayAlignedRotation: CalibratedRotation) = when {
				state.imuType == null -> rawRotation to rawRotation

				// TODO non-IMU trackers still want some form of calibration applied

				cal != null && action.rotation != null -> {
					fun calibrate(rot: RawRotation) = applyCalibration(
						rot,
						cal.headingCorrection,
						cal.attitudeAlignment,
						cal.headingAlignment * state.mountingOrientation,
					).twinNearest(state.rotation)
					val yawCorrectCalibratedRotation = calibrate(yawCorrectedRawRotation)
					(if (hideYawCorrection) calibrate(rawRotation) else yawCorrectCalibratedRotation) to yawCorrectCalibratedRotation
				}

				cal != null -> state.rotation to state.stayAlignedData.forceStayAlignedRotation

				else -> rawRotation to rawRotation
			}

			// Accel calibration
			val acceleration: CalibratedAcceleration = when {
				state.imuType == null -> rawAcceleration

				cal != null && action.acceleration != null -> applyCalibration(
					rawAcceleration,
					if (hideYawCorrection) rawRotation else yawCorrectedRawRotation,
					cal.headingCorrection,
					cal.headingAlignment,
				)

				cal != null -> state.acceleration

				else -> rawAcceleration
			}

			state.copy(
				rawRotation = rawRotation,
				rotation = rotation,
				stayAlignedData = state.stayAlignedData.copy(forceStayAlignedRotation = forceStayAlignedRotation),
				rawAcceleration = rawAcceleration,
				acceleration = acceleration,
				rawMagnetometer = rawMagnetometer,
				position = position,
			)
		}

		is TrackerActions.SetMountingOrientation -> {
			state.copy(
				mountingOrientation = action.mountingOrientation,
				sessionCalibration = state.sessionCalibration?.copy(headingAlignment = Quaternion.IDENTITY),
			)
		}

		is TrackerActions.SetRestOrientation -> state.copy(restOrientation = action.restOrientation)

		is TrackerActions.FullReset -> {
			val headingCorrection = estimateHeadingCorrect(
				state.rawRotation,
				action.referenceRotation,
			)
			val attitudeAlignment = estimateAttitudeAlign(
				state.rawRotation,
				headingCorrection,
				action.referenceRotation,
			)

			val sessionCalibration = state.sessionCalibration?.copy(
				headingCorrection = headingCorrection,
				attitudeAlignment = attitudeAlignment,
			) ?: SessionCalibration(
				headingCorrection = headingCorrection,
				attitudeAlignment = attitudeAlignment,
			)

			// Reset polarity tracking
			val defaultPolarityRotation = state.rotation.twinNearest(Quaternion.IDENTITY)

			state.copy(
				sessionCalibration = sessionCalibration,
				rotation = defaultPolarityRotation,
				// Full reset snaps: cancel any in-progress yaw smoothing.
				yawResetSmoothing = null,
			)
		}

		is TrackerActions.YawReset -> {
			val newHeading = estimateHeadingCorrect(
				state.rawRotation,
				action.referenceRotation,
			)
			val cal = state.sessionCalibration

			if (action.smoothTime > Duration.ZERO && cal != null && cal.headingCorrection != newHeading) {
				// Smooth: only set the target. Leave the applied heading where it is
				// TrackerYawResetSmoothingBehaviour eases sessionCalibration.headingCorrection
				// to newHeading over smoothTime. A reset mid-ease just replaces the seed.
				state.copy(
					yawResetSmoothing = YawResetSmoothing(
						from = cal.headingCorrection,
						to = newHeading,
						duration = action.smoothTime,
					),
				)
			} else {
				// Snap: apply the new heading immediately (default, no smoothing configured).
				state.copy(
					sessionCalibration = cal?.copy(headingCorrection = newHeading)
						?: SessionCalibration(headingCorrection = newHeading),
					yawResetSmoothing = null,
				)
			}
		}

		is TrackerActions.MountingReset -> {
			val cal = state.sessionCalibration

			val headingAlignment = estimateHeadingAlign(
				state.rawRotation,
				action.referenceRotation,
				cal?.headingCorrection ?: Quaternion.IDENTITY,
				cal?.attitudeAlignment ?: Quaternion.IDENTITY,
				state.mountingOrientation,
				action.yawOffset,
			)

			val sessionCalibration = state.sessionCalibration?.copy(
				headingAlignment = headingAlignment,
			) ?: SessionCalibration(
				headingAlignment = headingAlignment,
			)

			state.copy(sessionCalibration = sessionCalibration)
		}

		is TrackerActions.ClearMountingReset -> {
			state.copy(sessionCalibration = state.sessionCalibration?.copy(headingAlignment = Quaternion.IDENTITY))
		}

		else -> state
	}

	override fun observe(receiver: Tracker) {
		// Refresh the tracker's rotation whenever calibration gets updated
		receiver.context.state
			.distinctUntilChanged { old, new ->
				old.sessionCalibration == new.sessionCalibration &&
					old.restOrientation == new.restOrientation &&
					old.mountingOrientation == new.mountingOrientation
			}
			.onEach {
				// Make sure to send the raw data to have calibration re-apply
				receiver.context.dispatch(TrackerActions.SetRotation(it.rawRotation, it.rawAcceleration, it.rawMagnetometer))
			}.launchIn(receiver.context.scope)
	}
}

class TrackerYawResetSmoothingBehaviour : TrackerBehaviour {

	private fun animateEase(t: Float) = t * t

	@OptIn(ExperimentalCoroutinesApi::class)
	override fun observe(receiver: Tracker) {
		receiver.context.state
			.map { it.yawResetSmoothing }
			.distinctUntilChanged()
			.flatMapLatest { yawResetSmoothing ->
				yawResetSmoothing ?: return@flatMapLatest emptyFlow()
				val startTime = timeSource.markNow()

				receiver.appContext.skeleton.computed
					.onEach {
						val t = (startTime.elapsedNow() / yawResetSmoothing.duration).toFloat().coerceIn(0f, 1f)
						val done = t >= 1f
						val heading = if (done) yawResetSmoothing.to else yawResetSmoothing.from.interpR(yawResetSmoothing.to, animateEase(t))
						receiver.context.dispatch(TrackerActions.TickYawResetSmoothing(heading, done))
						if (done) return@onEach
					}
			}
			.launchIn(receiver.context.scope)
	}

	override fun reduce(state: TrackerState, action: TrackerActions) = when (action) {
		is TrackerActions.TickYawResetSmoothing -> {
			val cal = state.sessionCalibration
			if (cal == null || state.yawResetSmoothing == null) {
				// Nothing to advance.
				state.copy(yawResetSmoothing = null)
			} else {
				// The behaviour computed the interpolated heading; store it in the session
				// calibration. TrackerBasicBehaviour.observe re-applies it to the rotation
				// (using the last raw rotation), so this progresses even with no new IMU
				// data. On `done` the seed is cleared, leaving the target heading in place.
				state.copy(
					sessionCalibration = cal.copy(headingCorrection = action.heading),
					yawResetSmoothing = if (action.done) null else state.yawResetSmoothing,
				)
			}
		}

		else -> state
	}
}

class TrackerDefaultMountingOrientationBehaviour : TrackerBehaviour {
	/**
	 * Returns the default mounting orientation for the body part
	 */
	private fun defaultMountingForBodyPart(bodyPart: BodyPart?): Quaternion = when (bodyPart) {
		BodyPart.LEFT_LOWER_ARM, BodyPart.LEFT_HAND,
		BodyPart.LEFT_INDEX_PROXIMAL, BodyPart.LEFT_INDEX_INTERMEDIATE,
		BodyPart.LEFT_INDEX_DISTAL, BodyPart.LEFT_MIDDLE_PROXIMAL,
		BodyPart.LEFT_MIDDLE_INTERMEDIATE, BodyPart.LEFT_MIDDLE_DISTAL,
		BodyPart.LEFT_RING_PROXIMAL, BodyPart.LEFT_RING_INTERMEDIATE,
		BodyPart.LEFT_RING_DISTAL, BodyPart.LEFT_LITTLE_PROXIMAL,
		BodyPart.LEFT_LITTLE_INTERMEDIATE, BodyPart.LEFT_LITTLE_DISTAL,
		BodyPart.LEFT_SHOULDER,
		-> Quaternion.SLIMEVR.LEFT

		BodyPart.RIGHT_LOWER_ARM, BodyPart.RIGHT_HAND,
		BodyPart.RIGHT_INDEX_PROXIMAL, BodyPart.RIGHT_INDEX_INTERMEDIATE,
		BodyPart.RIGHT_INDEX_DISTAL, BodyPart.RIGHT_MIDDLE_PROXIMAL,
		BodyPart.RIGHT_MIDDLE_INTERMEDIATE, BodyPart.RIGHT_MIDDLE_DISTAL,
		BodyPart.RIGHT_RING_PROXIMAL, BodyPart.RIGHT_RING_INTERMEDIATE,
		BodyPart.RIGHT_RING_DISTAL, BodyPart.RIGHT_LITTLE_PROXIMAL,
		BodyPart.RIGHT_LITTLE_INTERMEDIATE, BodyPart.RIGHT_LITTLE_DISTAL,
		BodyPart.RIGHT_SHOULDER,
		-> Quaternion.SLIMEVR.RIGHT

		BodyPart.LEFT_UPPER_ARM, BodyPart.LEFT_LOWER_LEG -> Quaternion.SLIMEVR.FRONT_LEFT

		BodyPart.RIGHT_UPPER_ARM, BodyPart.RIGHT_LOWER_LEG -> Quaternion.SLIMEVR.FRONT_RIGHT

		else -> Quaternion.SLIMEVR.FRONT
	}

	override fun observe(receiver: Tracker) {
		receiver.context.state
			.map { it.bodyPart }
			.distinctUntilChanged()
			.drop(1)
			.onEach {
				receiver.context.dispatch(TrackerActions.SetMountingOrientation(defaultMountingForBodyPart(it)))
			}.launchIn(receiver.context.scope)
	}
}

class TrackerTPSBehaviour : TrackerBehaviour {
	@OptIn(ExperimentalAtomicApi::class)
	override fun observe(receiver: Tracker) {
		val count = AtomicInt(0)

		receiver.context.state.distinctUntilChangedBy { it.rawRotation }.onEach {
			count.incrementAndFetch()
		}.launchIn(receiver.context.scope)

		receiver.context.scope.launch {
			var mark = timeSource.markNow()
			while (isActive) {
				try {
					delay(1000)
					val elapsed = mark.elapsedNow()
					val tps = count.exchange(0) * 1000L / elapsed.inWholeMilliseconds

					// Tracker is at rest if it hasn't been updated in the last second
					val updateMotionAction = if (tps == 0L && receiver.context.state.value.motion != Motion.RESTING) TrackerActions.SetMotion(Motion.RESTING) else null
					receiver.context.dispatchAll(
						listOfNotNull(
							TrackerActions.Update { copy(tps = tps.toUShort()) },
							updateMotionAction,
						),
					)

					mark = timeSource.markNow()
				} catch (e: Exception) {
					AppLogger.coroutines.error(e, "Error in TrackerTPSBehaviour")
				}
			}
		}
	}
}

/**
 * Detects whether a tracker is at rest or rotating.
 * Used for StayAligned and TapDetection.
 */
class TrackerMotionDetectionBehaviour : TrackerBehaviour {

	override fun observe(receiver: Tracker) {
		var lastStateChangeTime = timeSource.markNow()
		var lastRotationTime = timeSource.markNow()
		var lastRotation: RawRotation = Quaternion.IDENTITY

		// For update loop
		receiver.context.state
			.map { it.rawRotation }
			.distinctUntilChanged()
			.onEach { rotation ->
				val now = timeSource.markNow()
				val motionState = receiver.context.state.value.motion
				val isRotating = isRotating(lastRotation, rotation)

				when (motionState) {
					// Detect if tracker is at rest
					Motion.ROTATING,
					Motion.STARTED_ROTATING,
					->
						if (!isRotating) {
							if (now > lastRotationTime + ENTER_REST_TIME) {
								// Been not moving for long enough; set as at rest.
								receiver.context.dispatch(TrackerActions.SetMotion(Motion.RESTING))
								lastStateChangeTime = now

								// Update the rotation to continue detecting if the tracker is at rest
								lastRotation = rotation
								lastRotationTime = now
							}
						} else if (motionState == Motion.STARTED_ROTATING && (now > lastStateChangeTime + ENTER_MOVING_TIME)) {
							// Been moving for long enough; set as moving.
							receiver.context.dispatch(TrackerActions.SetMotion(Motion.ROTATING))
							lastStateChangeTime = now
						}

					// Detect if tracker is moving
					Motion.RESTING ->
						if (isRotating) {
							// Started moving; set as recently at rest.
							receiver.context.dispatch(TrackerActions.SetMotion(Motion.STARTED_ROTATING))
							lastStateChangeTime = now
						}
				}

				// Update rotation if the tracker is moving
				if (isRotating) {
					lastRotation = rotation
					lastRotationTime = now
				}
			}
			.launchIn(receiver.context.scope)
	}

	/**
	 * Tracker is moving if rotating or accelerating past certain thresholds.
	 */
	private fun isRotating(lastRotation: RawRotation, rotation: RawRotation) = Angle.absBetween(lastRotation, rotation) > MAX_ROTATION

	override fun reduce(state: TrackerState, action: TrackerActions): TrackerState = when (action) {
		is TrackerActions.SetMotion -> {
			state.copy(
				motion = action.motion,
				stayAlignedData = state.stayAlignedData.copy(lockedRotation = if (action.motion == Motion.RESTING) state.stayAlignedData.forceStayAlignedRotation else null),
			)
		}

		else -> state
	}

	// TODO : these values may need fine-tuning
	companion object {
		val MAX_ROTATION = Angle.ofDeg(2f)
		val ENTER_REST_TIME = 0.8.seconds
		val ENTER_MOVING_TIME = 3.seconds
	}
}

class TrackerToSkeletonBehaviour : TrackerBehaviour {
	var lastBodyPartSent: BodyPart? = null

	@OptIn(ExperimentalCoroutinesApi::class)
	override fun observe(receiver: Tracker) {
		receiver.context.state
			.distinctUntilChanged { old, new -> old.status == new.status && old.bodyPart == new.bodyPart }
			.onEach { _ ->
				// Tell the skeleton the tracker has stopped sending data to the last bone it was sending data to.
				lastBodyPartSent?.let {
					receiver.appContext.skeleton.context.dispatch(
						SkeletonActions.DisableBone(it),
					)
					lastBodyPartSent = null
				}
			}
			.flatMapLatest { _ ->
				// We only want trackers that are assigned to a BodyPart and are OK or SLEEPING.
				val activeState = receiver.context.state
					.filter { it.bodyPart != null && (it.status == TrackerStatus.OK || it.status == TrackerStatus.SLEEPING) }

				activeState
					.distinctUntilChangedBy { it.rotation to it.position }
					.onEach { trackerState ->
						trackerState.bodyPart?.let { bodyPart ->
							receiver.appContext.skeleton.context.dispatchAll(
								listOfNotNull(
									SkeletonActions.SetBoneRotation(bodyPart, trackerState.rotation),
									if (trackerState.position != null) SkeletonActions.SetBonePosition(bodyPart, trackerState.position) else null,
								),
							)
							lastBodyPartSent = trackerState.bodyPart
						}
					}
			}
			.launchIn(receiver.context.scope)
	}
}

/**
 * Sets the orientation the tracker should match after a Full Reset.
 * Used for T-Pose down.
 */
class TrackerRestOrientationBehaviour(
	private val settings: Settings,
) : TrackerBehaviour {
	override fun observe(receiver: Tracker) {
		val armsResetModeFlow = settings.context.state.map { it.data.resetsConfig.armsResetMode }
		val bodyPartFlow = receiver.context.state.map { it.bodyPart }.distinctUntilChanged()

		combine(armsResetModeFlow, bodyPartFlow) { armsResetMode, bodyPart ->
			getRestOrientation(bodyPart, armsResetMode)
		}
			.distinctUntilChanged()
			.onEach { restOrientation ->
				receiver.context.dispatch(TrackerActions.SetRestOrientation(restOrientation))
			}
			.launchIn(receiver.context.scope)
	}

	private val quarterRollLeft = EulerAngles(EulerOrder.YZX, 0f, 0f, -FastMath.HALF_PI).toQuaternion()
	private val quarterRollRight = EulerAngles(EulerOrder.YZX, 0f, 0f, FastMath.HALF_PI).toQuaternion()
	private fun getRestOrientation(bodyPart: BodyPart?, armsResetMode: ArmsResetMode) = if (armsResetMode == ArmsResetMode.T_POSE_DOWN) {
		when (bodyPart) {
			in ResetBodyParts.LEFT_ARM, in ResetBodyParts.LEFT_FINGERS -> quarterRollLeft
			in ResetBodyParts.RIGHT_ARM, in ResetBodyParts.RIGHT_FINGERS -> quarterRollRight
			else -> Quaternion.IDENTITY
		}
	} else {
		Quaternion.IDENTITY
	}
}

class TrackerStayAlignedBehaviour(
	private val settings: Settings,
) : TrackerBehaviour {
	private var lastRotationTime = timeSource.markNow()

	@OptIn(ExperimentalCoroutinesApi::class)
	override fun observe(receiver: Tracker) {
		receiver.context.state
			.distinctUntilChanged { old, new -> old.sessionCalibration == new.sessionCalibration }
			.onEach {
				receiver.context.dispatch(TrackerActions.SetYawCorrection(Angle.ZERO))
			}.launchIn(receiver.context.scope)

		val serverFlow = receiver.appContext.server.context.state

		val stayAlignedConfigFlow = settings.context.state.map { it.data.stayAlignedConfig }.distinctUntilChanged()
		val imuTypeFlow = receiver.context.state.map { it.imuType }.distinctUntilChanged()
		val magStatusFlow = receiver.context.state.map { it.magStatus }.distinctUntilChanged()
		combine(stayAlignedConfigFlow, imuTypeFlow, magStatusFlow, ::Triple)
			.flatMapLatest { (stayAlignedConfig, imuType, magStatus) ->
				if (magStatus == MagnetometerStatus.ENABLED || imuType == null || !stayAlignedConfig.enabled) return@flatMapLatest emptyFlow()

				// Ignore every other emission for performance (50FPS instead of 100FPS at peak)
				var index = 0
				receiver.context.state
					.distinctUntilChangedBy { it.rawRotation }
					.filter { index++ % 2 == 0 }
					.onEach { state ->
						val yawCorrectionPerSec = IMU_TO_YAW_CORRECTION.getOrDefault(state.imuType, YAW_CORRECTION_DEFAULT)
						if (yawCorrectionPerSec == Angle.ZERO) return@onEach

						val lastFrameTimeSeconds = lastRotationTime.elapsedNow().inFloatingSeconds
						lastRotationTime = timeSource.markNow()

						val normalizedYawCorrection = yawCorrectionPerSec * lastFrameTimeSeconds
						val yawCorrectionRot = AdjustTrackerYaw.computeYawCorrection(
							state,
							serverFlow.value.trackers.values.map { it.context.state.value },
							normalizedYawCorrection,
							stayAlignedConfig,
						)

						if (yawCorrectionRot != null) {
							receiver.context.dispatch(TrackerActions.SetYawCorrection(yawCorrectionRot))
						}
					}
			}
			.launchIn(receiver.context.scope)
	}

	override fun reduce(state: TrackerState, action: TrackerActions): TrackerState = when (action) {
		is TrackerActions.SetYawCorrection -> state.copy(stayAlignedData = state.stayAlignedData.copy(yawCorrection = action.yawCorrection))
		else -> state
	}
}
