package dev.slimevr.tracker

import com.jme3.math.FastMath
import dev.slimevr.config.Settings
import dev.slimevr.logging.AppLogger
import dev.slimevr.resets.LEFT_ARM_PARTS
import dev.slimevr.resets.LEFT_FINGER_PARTS
import dev.slimevr.resets.RIGHT_ARM_PARTS
import dev.slimevr.resets.RIGHT_FINGER_PARTS
import dev.slimevr.skeleton.SkeletonActions
import dev.slimevr.timeSource
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.rpc.ArmsResetMode
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch
import kotlin.time.Duration.Companion.seconds

class TrackerBasicBehaviour : TrackerBehaviour {
	override fun reduce(state: TrackerState, action: TrackerActions) = when (action) {
		is TrackerActions.Update -> action.transform(state)

		is TrackerActions.SetMagStatus -> state.copy(magStatus = action.status)

		is TrackerActions.SetStatus -> state.copy(status = action.status)

		is TrackerActions.SetRotation -> {
			val cal = state.sessionCalibration

			val rawRotation: RawRotation = action.rotation ?: state.rawRotation
			val rotation: CalibratedRotation = when {
				cal != null && action.rotation != null -> applyCalibration(
					rawRotation,
					cal.headingCorrection,
					cal.attitudeAlignment,
					cal.headingAlignment * state.mountingOrientation,
				).twinNearest(state.rotation)

				cal != null -> state.rotation

				else -> rawRotation
			}

			val rawAcceleration: RawAcceleration =
				action.acceleration ?: state.rawAcceleration
			val acceleration: CalibratedAcceleration = when {
				cal != null && action.acceleration != null -> applyCalibration(
					rawAcceleration,
					rawRotation,
					cal.headingCorrection,
					cal.headingAlignment,
				)

				cal != null -> state.acceleration

				else -> rawAcceleration
			}

			val rawMagnetometer = action.magnetometer ?: state.rawMagnetometer

			state.copy(
				rawRotation = rawRotation,
				rotation = rotation,
				rawAcceleration = rawAcceleration,
				acceleration = acceleration,
				rawMagnetometer = rawMagnetometer,
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
			)
		}

		is TrackerActions.YawReset -> {
			val headingCorrection = estimateHeadingCorrect(
				state.rawRotation,
				action.referenceRotation,
			)

			val sessionCalibration = state.sessionCalibration?.copy(
				headingCorrection = headingCorrection,
			) ?: SessionCalibration(
				headingCorrection = headingCorrection,
			)

			state.copy(sessionCalibration = sessionCalibration)
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
				receiver.context.dispatch(TrackerActions.SetRotation(it.rawRotation, it.rawAcceleration, it.rawMagnetometer))
			}.launchIn(receiver.context.scope)
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

		else -> Quaternion.SLIMEVR.BACK
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
					receiver.context.dispatch(TrackerActions.Update { copy(tps = tps.toUShort()) })
					mark = timeSource.markNow()
				} catch (e: Exception) {
					AppLogger.coroutines.error(e, "Error in TrackerTPSBehaviour")
				}
			}
		}
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
				// We only want trackers that are assigned to a BodyPart and are OK.
				val activeState = receiver.context.state
					.filter { it.bodyPart != null && it.status == TrackerStatus.OK }

				val rotationFlow = activeState
					.distinctUntilChangedBy { it.rotation }
					.onEach { trackerState ->
						receiver.appContext.skeleton.context.dispatch(
							SkeletonActions.SetBoneRotation(trackerState.bodyPart ?: BodyPart.NONE, trackerState.rotation),
						)
						lastBodyPartSent = trackerState.bodyPart
					}

				val positionFlow = activeState
					.distinctUntilChangedBy { it.position }
					.onEach { trackerState ->
						trackerState.position?.let {
							receiver.appContext.skeleton.context.dispatch(
								SkeletonActions.SetBonePosition(trackerState.bodyPart ?: BodyPart.NONE, it),
							)
							lastBodyPartSent = trackerState.bodyPart
						}
					}

				merge(rotationFlow, positionFlow)
			}
			.launchIn(receiver.context.scope)
	}
}

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
			in LEFT_ARM_PARTS, in LEFT_FINGER_PARTS -> quarterRollLeft
			in RIGHT_ARM_PARTS, in RIGHT_FINGER_PARTS -> quarterRollRight
			else -> Quaternion.IDENTITY
		}
	} else {
		Quaternion.IDENTITY
	}
}
