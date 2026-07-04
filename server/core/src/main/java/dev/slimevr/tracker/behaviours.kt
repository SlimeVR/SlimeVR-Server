package dev.slimevr.tracker

import dev.slimevr.skeleton.SkeletonActions
import dev.slimevr.util.safeLaunch
import io.github.axisangles.ktmath.Quaternion
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch
import kotlin.time.TimeSource

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
					cal.headingAlignment,
				)

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

		is TrackerActions.FullReset -> {
			val headingCorrection = estimateHeadingCorrect(
				state.rawRotation,
				action.referenceRotation,
			)
			val attitudeAlignment = estimateAttitudeAlign(
				state.rawRotation,
				headingCorrection,
			)

			val sessionCalibration = state.sessionCalibration?.copy(
				headingCorrection = headingCorrection,
				attitudeAlignment = attitudeAlignment,
			) ?: SessionCalibration(
				headingCorrection = headingCorrection,
				attitudeAlignment = attitudeAlignment,
			)

			// TODO: Immediately apply calibration on `state.rawRotation` so we don't
			//  need to worry about desync of `state.rotation`
			state.copy(sessionCalibration = sessionCalibration)
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

			// TODO: Apply calibration on `state.rawRotation` -> `state.rotation`
			state.copy(sessionCalibration = sessionCalibration)
		}

		is TrackerActions.MountingReset -> {
			val cal = state.sessionCalibration

			val headingAlignment = estimateHeadingAlign(
				state.rawRotation,
				action.referenceRotation,
				cal?.headingCorrection ?: Quaternion.IDENTITY,
				cal?.attitudeAlignment ?: Quaternion.IDENTITY,
				state.mountingOrientation ?: Quaternion.IDENTITY,
			)

			val sessionCalibration = state.sessionCalibration?.copy(
				headingAlignment = headingAlignment,
			) ?: SessionCalibration(
				headingAlignment = headingAlignment,
			)

			// TODO: Apply calibration on `state.rawRotation` -> `state.rotation`
			state.copy(sessionCalibration = sessionCalibration)
		}
	}
}

class TrackerTPSBehaviour : TrackerBehaviour {
	@OptIn(ExperimentalAtomicApi::class)
	override fun observe(receiver: Tracker) {
		val count = AtomicInt(0)

		receiver.context.state.distinctUntilChangedBy { it.rawRotation }.onEach {
			count.incrementAndFetch()
		}.launchIn(receiver.context.scope)

		receiver.context.scope.safeLaunch {
			var mark = TimeSource.Monotonic.markNow()
			while (isActive) {
				try {
					delay(1000)
					val elapsed = mark.elapsedNow()
					val tps = count.exchange(0) * 1000L / elapsed.inWholeMilliseconds
					receiver.context.dispatch(TrackerActions.Update { copy(tps = tps.toUShort()) })
					mark = TimeSource.Monotonic.markNow()
				} catch (e: Exception) {
					dev.slimevr.AppLogger.coroutines.error(e, "Error in TrackerTPSBehaviour")
				}
			}
		}
	}
}

class TrackerToSkeletonBehaviour : TrackerBehaviour {
	override fun observe(receiver: Tracker) {
		receiver.context.state
			.filter { it.bodyPart != null }
			.map { Pair(it.bodyPart, it.rotation) }
			.distinctUntilChanged()
			.onEach { (bodyPart, rotation) ->
				bodyPart?.let {
					receiver.appContext.skeleton.context.dispatch(
						SkeletonActions.SetBoneRotation(it, rotation),
					)
				}
			}.launchIn(receiver.context.scope)
	}
}
