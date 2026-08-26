package dev.slimevr.tracker.behaviours

import com.jme3.math.FastMath
import dev.slimevr.VRServer
import dev.slimevr.config.Settings
import dev.slimevr.logging.AppLogger
import dev.slimevr.tracker.CalibratedAcceleration
import dev.slimevr.tracker.CalibratedRotation
import dev.slimevr.tracker.Motion
import dev.slimevr.tracker.RawAcceleration
import dev.slimevr.tracker.RawRotation
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.tracker.TrackerBehaviour
import dev.slimevr.tracker.TrackerState
import dev.slimevr.tracker.YawResetSmoothing
import dev.slimevr.tracker.applyCalibration
import dev.slimevr.tracker.estimateAttitudeAlign
import dev.slimevr.tracker.estimateHeadingAlign
import dev.slimevr.tracker.estimateHeadingCorrect
import dev.slimevr.tracker.getFirstFineFor
import dev.slimevr.util.timeSource
import io.github.axisangles.ktmath.Quaternion
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.MountingMethod
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.time.Duration

private const val UNRELIABLE_ANGLE_CHANGE_THRESHOLD = 135 * FastMath.DEG_TO_RAD // 135 to 215 degrees

@OptIn(ExperimentalAtomicApi::class)
class TrackerBasicBehaviour(private val settings: Settings, private val server: VRServer) : TrackerBehaviour {

	private fun trackPolarity(newRotation: Quaternion, oldRotation: Quaternion, bodyPart: BodyPart?): Quaternion {
		val absAngleChange = newRotation.angleToR(oldRotation)
		if (absAngleChange > UNRELIABLE_ANGLE_CHANGE_THRESHOLD && bodyPart != BodyPart.HEAD) {
			// Rotation rotated too much since last SetRotation; we're not sure which direction the tracker rotated.
			// Solution: align polarity with the head tracker since the user is likely to be spinning their whole body.
			val headTracker = server.context.state.value.trackers.values.map { it.context.state.value }.getFirstFineFor(BodyPart.HEAD)
			if (headTracker != null) return newRotation.twinNearest(headTracker.rotation)
		}

		// Track polarity compared to the last rotation
		return newRotation.twinNearest(oldRotation)
	}

	override fun reduce(state: TrackerState, action: TrackerActions) = when (action) {
		is TrackerActions.Update -> action.transform(state)

		is TrackerActions.SetMagStatus -> state.copy(magStatus = action.status)

		is TrackerActions.SetStatus -> state.copy(status = action.status)

		is TrackerActions.SetDriverName -> state.copy(driverName = action.driverName)

		is TrackerActions.SetRotation -> {
			// This action counts as a tick towards TPS if it has new rotation data.
			val accumulatedTicks = if (action.newData && action.rotation != null) (state.accumulatedTicks + 1u).toUShort() else state.accumulatedTicks

			// Rotation
			val rawRotation: RawRotation = action.rotation ?: state.rawRotation
			val yawCorrectedRawRotation = Quaternion.rotationAroundYAxis(state.stayAlignedData.yawCorrection.toRad()) * rawRotation
			val stayAlignedEnabled = settings.context.state.value.data.stayAlignedConfig.enabled
			val correctedRawRotation = if (stayAlignedEnabled) yawCorrectedRawRotation else rawRotation

			// Other inputs
			val rawAcceleration: RawAcceleration = action.acceleration ?: state.rawAcceleration
			val rawMagnetometer = action.magnetometer ?: state.rawMagnetometer
			val position = action.position ?: state.position

			val cal = state.sessionCalibration

			// Rotation calibration
			val rotation: CalibratedRotation =
				if (action.rotation != null) {
					trackPolarity(applyCalibration(correctedRawRotation, cal.headingCorrection, cal.attitudeAlignment, cal.headingAlignment, state.restOrientation), state.rotation, state.bodyPart)
				} else {
					state.rotation
				}

			// Accel calibration
			val acceleration: CalibratedAcceleration =
				if (action.acceleration != null) {
					applyCalibration(rawAcceleration, correctedRawRotation, cal.headingCorrection, cal.headingAlignment)
				} else {
					state.acceleration
				}

			state.copy(
				rawRotation = rawRotation,
				rotation = rotation,
				rawAcceleration = rawAcceleration,
				acceleration = acceleration,
				rawMagnetometer = rawMagnetometer,
				position = position,
				accumulatedTicks = accumulatedTicks,
			)
		}

		is TrackerActions.SetMountingOrientation -> {
			if (state.position != null) {
				// Don't set mounting orientation for positional trackers
				state
			} else {
				state.copy(
					mountingOrientation = action.mountingOrientation,
					sessionCalibration = state.sessionCalibration.copy(headingAlignment = action.mountingOrientation),
					lastMountingMethod = MountingMethod.MANUAL,
				)
			}
		}

		is TrackerActions.SetRestOrientation -> state.copy(restOrientation = action.restOrientation)

		is TrackerActions.FullReset -> {
			val isPositional = state.position != null
			val isHead = state.bodyPart == BodyPart.HEAD
			val shouldAlignAttitude = !isHead || !isPositional || settings.context.state.value.data.resetsConfig.resetPositionalHeadAttitude
			val shouldAlignHeadingWithReference = !isHead && isPositional

			val headingCorrection =
				if (shouldAlignAttitude) {
					estimateHeadingCorrect(
						state.rawRotation,
						action.referenceRotation,
					)
				} else {
					state.sessionCalibration.attitudeAlignment
				}
			val attitudeAlignment =
				if (shouldAlignAttitude) {
					estimateAttitudeAlign(
						state.rawRotation,
						headingCorrection,
						action.referenceRotation,
					)
				} else {
					state.sessionCalibration.attitudeAlignment
				}
			val headingAlignment =
				if (shouldAlignHeadingWithReference) {
					headingCorrection
				} else {
					state.sessionCalibration.headingAlignment
				}

			state.copy(
				sessionCalibration = state.sessionCalibration.copy(
					headingCorrection = headingCorrection,
					attitudeAlignment = attitudeAlignment,
					headingAlignment = headingAlignment,
				),
				// Reset polarity tracking
				rotation = state.rotation.twinNearest(Quaternion.IDENTITY),
				// Full reset snaps: cancel any in-progress yaw smoothing.
				yawResetSmoothing = null,
			)
		}

		is TrackerActions.YawReset -> {
			val cal = state.sessionCalibration

			val newHeading = estimateHeadingCorrect(
				applyCalibration(state.rawRotation, attitudeAlign = cal.attitudeAlignment, headingAlign = cal.headingAlignment),
				action.referenceRotation,
			)

			if (action.smoothTime > Duration.ZERO && cal.headingCorrection != newHeading) {
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
					sessionCalibration = cal.copy(headingCorrection = newHeading),
					yawResetSmoothing = null,
				)
			}
		}

		is TrackerActions.PoseMountingReset -> {
			val cal = state.sessionCalibration

			val headingAlignment = estimateHeadingAlign(
				state.rawRotation,
				action.referenceRotation,
				cal.headingCorrection,
				cal.attitudeAlignment,
				state.mountingOrientation,
				action.yawOffset,
			) *
				state.mountingOrientation

			state.copy(
				sessionCalibration = state.sessionCalibration.copy(headingAlignment = headingAlignment),
				lastMountingMethod = MountingMethod.POSE,
			)
		}

		is TrackerActions.ClearMountingReset -> {
			state.copy(
				sessionCalibration = state.sessionCalibration.copy(headingAlignment = state.mountingOrientation),
				lastMountingMethod = MountingMethod.MANUAL,
			)
		}

		else -> state
	}

	override fun observe(receiver: Tracker) {
		observeCalibration(receiver)
		observeTps(receiver)
	}

	/**
	 * Refreshes the tracker's rotation whenever calibration gets updated
	 */
	private fun observeCalibration(receiver: Tracker) {
		receiver.context.state
			.distinctUntilChanged { old, new ->
				old.sessionCalibration == new.sessionCalibration &&
					old.restOrientation == new.restOrientation &&
					old.mountingOrientation == new.mountingOrientation
			}
			.onEach {
				// Make sure to send the raw data to have calibration re-apply
				receiver.context.dispatch(TrackerActions.SetRotation(it.rawRotation, it.rawAcceleration, it.rawMagnetometer, newData = false))
			}.launchIn(receiver.context.scope)
	}

	/**
	 * Sets the tracker's Ticks Per Second (TPS) every second.
	 *
	 * One tick = one new rotation data
	 */
	private fun observeTps(receiver: Tracker) {
		receiver.context.scope.launch {
			var mark = timeSource.markNow()
			while (isActive) {
				try {
					delay(1000)
					val elapsed = mark.elapsedNow()
					val tps = receiver.context.state.value.accumulatedTicks * 1000u / elapsed.inWholeMilliseconds.toUInt()

					// Tracker is at rest if it hasn't been updated in the last second
					val updateMotionAction = if (tps == 0u && receiver.context.state.value.motion != Motion.RESTING) TrackerActions.SetMotion(Motion.RESTING) else null
					receiver.context.dispatchAll(
						listOfNotNull(
							TrackerActions.Update { copy(tps = tps.toUShort(), accumulatedTicks = 0u) },
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
