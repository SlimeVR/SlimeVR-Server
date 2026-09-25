package dev.slimevr.tracker

import dev.slimevr.degreeToRadian
import dev.slimevr.tracker.behaviours.TrackerRotationRefreshBehaviour
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.hardware_info.ImuType
import solarxr_protocol.rpc.ResetType
import kotlin.test.Test
import kotlin.test.assertEquals

class PolarityTrackingTest {
	private fun getResetAction(resetType: ResetType, referenceRot: Quaternion?) = when (resetType) {
		ResetType.FULL -> TrackerActions.FullReset(referenceRot)
		ResetType.YAW -> TrackerActions.YawReset(referenceRot)
		ResetType.POSE_MOUNTING -> TrackerActions.PoseMountingReset(referenceRot, 0f)
	}

	private fun assertPolarityAlignedAfterResets(
		initialState: TrackerState,
		referenceRot: Quaternion,
	) {
		ResetType.entries.forEach { resetType ->
			val resetState = reduce(initialState, getResetAction(resetType, referenceRot))

			val refreshedState =
				reduce(resetState, TrackerRotationRefreshBehaviour.getRotationRefreshAction(resetState))
			assertEquals(
				refreshedState.rotation,
				refreshedState.rotation.twinNearest(referenceRot),
				message = "resetType $resetType, referenceRot $referenceRot, initialState $initialState",
			)
		}
	}

	@Test
	fun `Resets align IMU Tracker polarity with reference`() = // Tracker rawRotation (untracked polarity)
		untrackedHeading.forEach { rawRot ->
			// Tracker calibrated rotation (tracked polarity)
			trackedHeading.forEach { stateRot ->
				// Reference rotation (tracked polarity)
				trackedHeading.forEach { referenceRot ->
					assertPolarityAlignedAfterResets(
						Tracker.DEFAULT_STATE.copy(
							imuType = ImuType.BNO085,
							position = null,
							rawRotation = rawRot,
							rotation = stateRot,
						),
						referenceRot,
					)
				}
			}
		}

	@Test
	fun `Resets align Positional Tracker polarity with reference`() = // Tracker rawRotation (untracked polarity)
		untrackedHeading.forEach { rawRot ->
			// Tracker calibrated rotation (tracked polarity)
			trackedHeading.forEach { stateRot ->
				// Reference rotation (tracked polarity)
				trackedHeading.forEach { referenceRot ->
					assertPolarityAlignedAfterResets(
						Tracker.DEFAULT_STATE.copy(
							imuType = null,
							position = Vector3.POS_Y,
							rawRotation = rawRot,
							rotation = stateRot,
						),
						referenceRot,
					)
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
