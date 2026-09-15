package dev.slimevr.tracker

import dev.slimevr.degreeToRadian
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import solarxr_protocol.rpc.ResetType
import kotlin.math.abs
import kotlin.math.withSign
import kotlin.test.Test
import kotlin.test.assertEquals

class PolarityTrackingTest {
	@Test
	fun `Reducer polarity tracking mega test`() = ResetType.entries.forEach { resetType ->
		untrackedHeading.forEach { trackerHeading ->
			// rawRotation in state
			trackedHeading.forEach { stateHeading ->
				// rotation in state
				untrackedHeading.forEach { referenceHeading ->
					untrackedAttitude.forEach { referenceAttitude ->
						val debugText = "resetType $resetType, trackerHeading $trackerHeading, stateHeading $stateHeading, referenceHeading $referenceHeading, referenceAttitude $referenceAttitude"
						val referenceRotation = referenceHeading * referenceAttitude

						// Reset
						val initialState = Tracker.DEFAULT_STATE.copy(rawRotation = trackerHeading, rotation = stateHeading)
						val trackerAction = when (resetType) {
							ResetType.FULL -> TrackerActions.FullReset(referenceRotation)
							ResetType.YAW -> TrackerActions.YawReset(referenceRotation)
							ResetType.POSE_MOUNTING -> TrackerActions.PoseMountingReset(referenceRotation, 0f)
						}
						val resetState = reduce(initialState, trackerAction)
						assertEquals(resetState.rotation, resetState.rotation.twinNearest(referenceRotation), message = "Reset didn't reset polarity, $debugText")

						// Set same rotation, polarity should match IDENTITY
						val rotatedState = reduce(resetState, TrackerActions.SetRotation(initialState.rawRotation))
						assertEquals(rotatedState.rotation, rotatedState.rotation.twinNearest(referenceRotation), message = "Polarity didn't reset after SetRotation, $debugText")

						// Rotate
						trackedStep.forEach { degreesChange ->
							var rotatedState = resetState
							for (change in 0..abs(degreesChange) step STEP_INCREMENT) { // So we don't go over 180d in one SetRotation
								val headingChange = Quaternion.rotationAroundYAxis(degreeToRadian(change.toFloat().withSign(degreesChange)))
								val newRotation = trackerHeading * headingChange
								rotatedState = reduce(rotatedState, TrackerActions.SetRotation(newRotation))
							}

							// If within 180 degrees, it's still IDENTITY polarity, if >180 degrees, it's not
							val expectedRotation = if (abs(degreesChange) < 180) {
								rotatedState.rotation.twinNearest(referenceRotation)
							} else {
								rotatedState.rotation.twinFurthest(referenceRotation)
							}
							assertEquals(rotatedState.rotation, expectedRotation, "Rotating after reset yielded wrong polarity, $debugText. degreesChange = $degreesChange")
						}
					}
				}
			}
		}
	}

	companion object {
		const val STEP_INCREMENT = 72
		val untrackedStep = (-180..180 step STEP_INCREMENT)
		val trackedStep = (-360..360 step STEP_INCREMENT)
		val trackedHeading = trackedStep.map { y ->
			Quaternion.rotationAroundYAxis(degreeToRadian(y.toFloat()))
		}
		val untrackedHeading = untrackedStep.map { y ->
			Quaternion.rotationAroundYAxis(degreeToRadian(y.toFloat()))
		}
		val untrackedAttitude = untrackedStep.flatMap { x ->
			untrackedStep.map { z ->
				EulerAngles(
					EulerOrder.YZX,
					degreeToRadian(x.toFloat()),
					0f,
					degreeToRadian(z.toFloat()),
				).toQuaternion()
			}
		}
	}
}
