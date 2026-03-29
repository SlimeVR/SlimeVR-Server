@file:OptIn(kotlinx.coroutines.FlowPreview::class)

package dev.slimevr.heightcalibration

import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.withTimeoutOrNull
import solarxr_protocol.rpc.UserHeightCalibrationStatus
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sqrt

internal const val SAMPLE_INTERVAL_MS = 16L

private const val FLOOR_ALPHA = 0.1f
private const val HMD_ALPHA = 0.1f

private const val CONTROLLER_STABILITY_THRESHOLD = 0.005f
internal const val CONTROLLER_STABILITY_DURATION = 300_000_000L

private const val HMD_STABILITY_THRESHOLD = 0.003f
internal const val HEAD_STABILITY_DURATION = 600_000_000L

internal const val MAX_FLOOR_Y = 0.10f
internal const val HMD_RISE_THRESHOLD = 1.2f
internal const val HEIGHT_MIN = 1.4f
internal const val HEIGHT_MAX = 1.936f

private val HEAD_ANGLE_THRESHOLD = cos((PI / 180.0) * 15.0)
private val CONTROLLER_ANGLE_THRESHOLD = cos((PI / 180.0) * 45.0)

internal const val TIMEOUT_MS = 30_000L

private fun UserHeightCalibrationStatus.isTerminal() = when (this) {
	UserHeightCalibrationStatus.DONE,
	UserHeightCalibrationStatus.ERROR_TOO_HIGH,
	UserHeightCalibrationStatus.ERROR_TOO_SMALL,
	-> true
	else -> false
}

private fun isControllerPointingDown(snapshot: TrackerSnapshot): Boolean {
	val forward = snapshot.rotation.sandwich(Vector3.NEG_Z)
	return (forward dot Vector3.NEG_Y) >= CONTROLLER_ANGLE_THRESHOLD
}

private fun isHmdLeveled(snapshot: TrackerSnapshot): Boolean {
	val up = snapshot.rotation.sandwich(Vector3.POS_Y)
	return (up dot Vector3.POS_Y) >= HEAD_ANGLE_THRESHOLD
}

object CalibrationBehaviour : HeightCalibrationBehaviourType {
	override fun reduce(state: HeightCalibrationState, action: HeightCalibrationActions) =
		when (action) {
			is HeightCalibrationActions.Update -> state.copy(
				status = action.status,
				currentHeight = action.currentHeight,
			)
		}
}

internal suspend fun runCalibrationSession(
	context: HeightCalibrationContext,
	hmdUpdates: kotlinx.coroutines.flow.Flow<TrackerSnapshot>,
	controllerUpdates: kotlinx.coroutines.flow.Flow<TrackerSnapshot>,
	clock: () -> Long = System::nanoTime,
) {
	var currentFloorLevel = Float.MAX_VALUE
	var currentHeight = 0f
	var floorStableStart: Long? = null
	var heightStableStart: Long? = null

	var floorFiltered: Vector3? = null
	var floorEnergyEma = 0f
	var hmdFiltered: Vector3? = null
	var hmdEnergyEma = 0f

	fun dispatch(status: UserHeightCalibrationStatus, height: Float = currentHeight) {
		currentHeight = height
		context.dispatch(HeightCalibrationActions.Update(status, height))
	}

	dispatch(UserHeightCalibrationStatus.RECORDING_FLOOR)

	withTimeoutOrNull(TIMEOUT_MS) {

		// Floor phase: collect controller updates until the floor level is locked in
		controllerUpdates
			.sample(SAMPLE_INTERVAL_MS)
			.takeWhile { context.state.value.status != UserHeightCalibrationStatus.WAITING_FOR_RISE }
			.collect { snapshot ->
				val now = clock()

				if (snapshot.position.y > MAX_FLOOR_Y) {
					floorStableStart = null
					floorFiltered = null
					floorEnergyEma = 0f
					return@collect
				}

				if (!isControllerPointingDown(snapshot)) {
					dispatch(UserHeightCalibrationStatus.WAITING_FOR_CONTROLLER_PITCH)
					floorStableStart = null
					floorFiltered = null
					floorEnergyEma = 0f
					return@collect
				}

				val pos = snapshot.position
				val prev = floorFiltered ?: pos
				val newFiltered = prev * (1f - FLOOR_ALPHA) + pos * FLOOR_ALPHA
				floorFiltered = newFiltered
				currentFloorLevel = minOf(currentFloorLevel, pos.y)

				val dev = pos - newFiltered
				floorEnergyEma = floorEnergyEma * (1f - FLOOR_ALPHA) + (dev dot dev) * FLOOR_ALPHA

				if (sqrt(floorEnergyEma) > CONTROLLER_STABILITY_THRESHOLD) {
					floorStableStart = null
					floorFiltered = null
					floorEnergyEma = 0f
					return@collect
				}

				val stableStart = floorStableStart ?: now.also { floorStableStart = it }
				if (now - stableStart >= CONTROLLER_STABILITY_DURATION) {
					dispatch(UserHeightCalibrationStatus.WAITING_FOR_RISE)
				}
			}

		// Height phase: collect HMD updates until a terminal status is reached
		hmdUpdates
			.sample(SAMPLE_INTERVAL_MS)
			.takeWhile { !context.state.value.status.isTerminal() }
			.collect { snapshot ->
				val now = clock()
				val relativeY = snapshot.position.y - currentFloorLevel

				if (relativeY <= HMD_RISE_THRESHOLD) {
					dispatch(UserHeightCalibrationStatus.WAITING_FOR_RISE, relativeY)
					heightStableStart = null
					hmdFiltered = null
					hmdEnergyEma = 0f
					return@collect
				}

				if (!isHmdLeveled(snapshot)) {
					dispatch(UserHeightCalibrationStatus.WAITING_FOR_FW_LOOK, relativeY)
					heightStableStart = null
					hmdFiltered = null
					hmdEnergyEma = 0f
					return@collect
				}

				dispatch(UserHeightCalibrationStatus.RECORDING_HEIGHT, relativeY)

				val pos = snapshot.position
				val prev = hmdFiltered ?: pos
				val newFiltered = prev * (1f - HMD_ALPHA) + pos * HMD_ALPHA
				hmdFiltered = newFiltered

				val dev = pos - newFiltered
				hmdEnergyEma = hmdEnergyEma * (1f - HMD_ALPHA) + (dev dot dev) * HMD_ALPHA

				if (sqrt(hmdEnergyEma) > HMD_STABILITY_THRESHOLD) {
					heightStableStart = null
					hmdFiltered = null
					hmdEnergyEma = 0f
					return@collect
				}

				val stableStart = heightStableStart ?: now.also { heightStableStart = it }
				if (now - stableStart >= HEAD_STABILITY_DURATION) {
					val finalStatus = when {
						relativeY < HEIGHT_MIN -> UserHeightCalibrationStatus.ERROR_TOO_SMALL
						relativeY > HEIGHT_MAX -> UserHeightCalibrationStatus.ERROR_TOO_HIGH
						else -> UserHeightCalibrationStatus.DONE
					}
					dispatch(finalStatus, relativeY)

					// TODO (when DONE): persist height to config, update user proportions, clear mounting reset flags
				}
			}
	} ?: dispatch(UserHeightCalibrationStatus.ERROR_TIMEOUT)
}
