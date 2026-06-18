package dev.slimevr.tracker

import dev.slimevr.VRServerState
import dev.slimevr.config.SettingsState
import dev.slimevr.skeleton.SkeletonActions
import dev.slimevr.util.safeLaunch
import io.github.axisangles.ktmath.Quaternion
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.DeviceId
import solarxr_protocol.datatypes.TrackerId
import solarxr_protocol.rpc.ResetType
import solarxr_protocol.rpc.TapDetectionSetupNotification
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch
import kotlin.time.TimeSource

private const val NS_CONVERTER = 1.0e9f
private const val CLUMP_TIME_NS = 0.06f * NS_CONVERTER
private const val NEEDED_ACCEL_DELTA = 6.0f
private const val ALLOWED_BODY_ACCEL = 2.25f
private const val ALLOWED_BODY_ACCEL_SQUARED = ALLOWED_BODY_ACCEL * ALLOWED_BODY_ACCEL
private const val TAP_WINDOW_PER_TAP_NS = 0.3f * NS_CONVERTER

class TrackerTapDetectionBehaviour : TrackerBehaviour {
	private val accelList = ArrayDeque<Pair<Float, Long>>()
	private val tapTimestamps = ArrayDeque<Long>()
	private var waitForLowAccel = false
	private var resetType: ResetType? = null
	private var setupModeAssign = false
	private var tapsNeeded = 0
	private var actionDelay = 0f
	private var numberTrackersOverThreshold = 0

	@OptIn(ExperimentalCoroutinesApi::class)
	override fun observe(receiver: Tracker) {
		// Outer flow (loading) is refreshed when TapDetection config or a tracker's bodyPart or status changes
		val tapConfigChangingFlow = receiver.appContext.config.settings.context.state
			.distinctUntilChangedBy { configState ->
				configState.data.tapDetectionConfig }
		val trackerChangingFlow = receiver.appContext.server.context.state
			.flatMapLatest { serverState ->
				combine(serverState.trackers.values.map { tracker -> tracker.context.state })
				{ trackerStates ->
					trackerStates.map { it.bodyPart to it.status }
				}
			}
			.distinctUntilChanged()

		combine(
			tapConfigChangingFlow,
			trackerChangingFlow
		) { configState, _ ->
			loadTapDetection(receiver, configState, receiver.appContext.server.context.state.value)
		}
		// Inner flow (process) is refreshed everytime this tracker's acceleration is updated
		.flatMapLatest {
			receiver.context.state
				.filter { resetType != null || setupModeAssign }
				.distinctUntilChangedBy { it.rawAcceleration }
		}
		.onEach { currentTracker ->
			processTapDetection(receiver, currentTracker)
		}.launchIn(receiver.context.scope)
	}

	// Loads TapDetection config
	private fun loadTapDetection(
		receiver: Tracker,
		configState: SettingsState,
		serverState: VRServerState
	) {
		val tapDetectionConfig = configState.data.tapDetectionConfig
		numberTrackersOverThreshold = tapDetectionConfig.numberTrackersOverThreshold
		resetTapDetection()

		// If setupMode is true, double tap to assign
		if (tapDetectionConfig.setupMode) {
			setupModeAssign = true
			resetType = null
			tapsNeeded = 2
			actionDelay = 0f
			return
		}

		val trackersBodyParts = serverState.trackers.values
			.map { it.context.state.value.bodyPart }
			.toSet()
		val yawResetBodyPart = listOf(tapDetectionConfig.yawResetBodyPart, BodyPart.UPPER_CHEST, BodyPart.CHEST, BodyPart.HIP, BodyPart.WAIST)
			.firstOrNull { it in trackersBodyParts }
		val fullResetBodyPart = listOf(tapDetectionConfig.fullResetBodyPart, BodyPart.LEFT_UPPER_LEG, BodyPart.LEFT_LOWER_LEG)
			.firstOrNull { it in trackersBodyParts }
		val mountingResetBodyPart = listOf(tapDetectionConfig.mountingResetBodyPart, BodyPart.RIGHT_UPPER_LEG, BodyPart.RIGHT_LOWER_LEG)
			.firstOrNull { it in trackersBodyParts }

		// Switch case for each possible action
		when (receiver.context.state.value.bodyPart) {
			null -> resetType = null // BodyParts above could be null
			yawResetBodyPart if tapDetectionConfig.yawResetEnabled -> {
				resetType = ResetType.Yaw
				tapsNeeded = tapDetectionConfig.yawResetTaps
				actionDelay = tapDetectionConfig.yawResetDelay
			}
			fullResetBodyPart if tapDetectionConfig.fullResetEnabled -> {
				resetType = ResetType.Full
				tapsNeeded = tapDetectionConfig.fullResetTaps
				actionDelay = tapDetectionConfig.fullResetDelay
			}
			mountingResetBodyPart if tapDetectionConfig.mountingResetEnabled -> {
				resetType = ResetType.Mounting
				tapsNeeded = tapDetectionConfig.mountingResetTaps
				actionDelay = tapDetectionConfig.mountingResetDelay
			}
			else -> resetType = null
		}
	}

	// Logic loop for tap detection
	private fun processTapDetection(
		receiver: Tracker,
		currentTracker: TrackerState,
	) {
		val now = System.nanoTime()

		// Get the acceleration of the tracker and store it
		accelList.add(currentTracker.rawAcceleration.len() to now)

		// Remove old stored accelerations (if they are too old)
		while (accelList.isNotEmpty() && now - accelList.first().second > CLUMP_TIME_NS) {
			accelList.removeFirst()
		}

		val max = accelList.maxOfOrNull { it.first } ?: 0f
		val min = accelList.minOfOrNull { it.first } ?: 0f
		val accelDelta = max - min

		// Check for a single tap
		if (accelDelta > NEEDED_ACCEL_DELTA && !waitForLowAccel) {
			val othersOverThreshold = receiver.appContext.server.context.state.value.trackers.values
				.count { it.context.state.value.id != currentTracker.id && it.context.state.value.rawAcceleration.lenSq() > ALLOWED_BODY_ACCEL_SQUARED }
			if (othersOverThreshold < numberTrackersOverThreshold) {
				tapTimestamps.add(now)
				// After a tap, a lower acceleration is needed before another one
				waitForLowAccel = true
			}
		}

		// Achieved low accel?
		if (max < ALLOWED_BODY_ACCEL) waitForLowAccel = false

		if (tapTimestamps.isNotEmpty()) {
			// Remove old stored taps (if they are too old)
			val totalWindowNs = (TAP_WINDOW_PER_TAP_NS * tapTimestamps.size).toLong()
			while (tapTimestamps.isNotEmpty() && now - tapTimestamps.first() > totalWindowNs) {
				tapTimestamps.removeFirst()
			}

			if (tapTimestamps.size >= tapsNeeded) {
				// Taps completed!
				receiver.context.scope.safeLaunch {
					// If it's in setup mode, tap to assign
					if (setupModeAssign) {
						receiver.appContext.server.sendSolarxrRpc(
							TapDetectionSetupNotification(
								TrackerId(DeviceId(currentTracker.deviceId.toUByte()), currentTracker.id.toUByte())
							)
						)
					}

					// If it has a reset to execute
					resetType?.let { reset ->
						receiver.appContext.resetsManager.scheduleReset("TapDetection", reset, actionDelay)
					}
				}

				resetTapDetection()
			}
		}
	}

	private fun resetTapDetection(){
		accelList.clear()
		tapTimestamps.clear()
		waitForLowAccel = false
	}
}

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

			state.copy(
				rawRotation = rawRotation,
				rotation = rotation,
				rawAcceleration = rawAcceleration,
				acceleration = acceleration,
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
