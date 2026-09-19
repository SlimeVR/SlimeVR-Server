package dev.slimevr.tracker

import dev.slimevr.degreeToRadian
import io.github.axisangles.ktmath.Quaternion
import solarxr_protocol.rpc.ResetType
import kotlin.test.Test
import kotlin.test.assertEquals

class PolarityTrackingTest {
	private fun getResetAction(resetType: ResetType, referenceRot: Quaternion) = when (resetType) {
		ResetType.FULL -> TrackerActions.FullReset(referenceRot)
		ResetType.YAW -> TrackerActions.YawReset(referenceRot)
		ResetType.POSE_MOUNTING -> TrackerActions.PoseMountingReset(referenceRot, 0f)
	}

	// TODO test for when positional tracker
	@Test
	fun `Reducer Resets align polarity with reference rotation`() = ResetType.entries.forEach { resetType ->
		// Tracker rawRotation (untracked polarity)
		untrackedHeading.forEach { rawRot ->
			// Tracker calibrated rotation (tracked polarity)
			trackedHeading.forEach { stateRot ->
				// Reference rotation (assumes it is using the shortest rotation)
				untrackedHeading.forEach { referenceRot ->
					// Reset
					val initialState = Tracker.DEFAULT_STATE.copy(rawRotation = rawRot, rotation = stateRot)
					val resetState = reduce(initialState, getResetAction(resetType, referenceRot))

					// Calibration refresh, polarity should match reference
					val refreshedState = reduce(resetState, TrackerActions.SetRotation(initialState.rawRotation, refresh = true))
					assertEquals(
						refreshedState.rotation,
						refreshedState.rotation.twinNearest(referenceRot),
						message = "resetType $resetType, rawRot $rawRot, stateRot $stateRot, referenceRot $referenceRot",
					)
				}
			}
		}
	}

	companion object {
		val untrackedStep = (-180..180 step 72)
		val trackedStep = (-360..360 step 72)
		val trackedHeading = trackedStep.map { y ->
			Quaternion.rotationAroundYAxis(degreeToRadian(y.toFloat()))
		}
		val untrackedHeading = untrackedStep.map { y ->
			Quaternion.rotationAroundYAxis(degreeToRadian(y.toFloat()))
		}
	}
}
