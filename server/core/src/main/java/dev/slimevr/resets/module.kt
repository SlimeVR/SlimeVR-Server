package dev.slimevr.resets

import com.jme3.math.FastMath
import dev.slimevr.Phase1ContextProvider
import dev.slimevr.VRServer
import dev.slimevr.config.ResetsConfig
import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.logging.AppLogger
import dev.slimevr.skeleton.Skeleton
import dev.slimevr.skeleton.SkeletonActions
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.util.isActive
import io.github.axisangles.ktmath.Quaternion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.MountingMethod
import solarxr_protocol.rpc.ArmsResetMode
import solarxr_protocol.rpc.ResetResponse
import solarxr_protocol.rpc.ResetStatus
import solarxr_protocol.rpc.ResetType
import kotlin.collections.contains
import kotlin.collections.listOf
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeMark

data class ResetsState(
	val canDoYawReset: Boolean,
	val canDoMountingReset: Boolean,
	val lastFullResetTime: TimeMark?,

	// Session-only flags: whether a mounting reset (and a feet mounting reset) has been done at
	// least once since the last full reset
	val mountingResetCompleted: Boolean = false,
	val feetMountingResetCompleted: Boolean = false,
)

sealed interface ResetsActions {
	data class ClearResets(val resetTypes: List<ResetType>) : ResetsActions
	data class EndReset(val resetType: ResetType, val bodyParts: List<BodyPart>? = null, val resetMountingFeet: Boolean = false) : ResetsActions
	data object ClearMountingCompleted : ResetsActions
}

typealias ResetsContext = Context<ResetsState, ResetsActions>
typealias ResetsBehaviour = Behaviour<ResetsManager>

class ResetsManager(val context: ResetsContext, val server: VRServer, val settings: Settings, val skeleton: Skeleton) {
	fun startObserving() = context.observeAll(this)

	private var resetJob: Job = Job()

	/**
	 * Schedules a reset according to the resetType.
	 * resetSourceName is used for logging
	 * If delay is null, the default delay from config will be used.
	 * If bodyParts is null, resets all trackers.
	 */
	suspend fun scheduleReset(resetSourceName: String, resetType: ResetType, delay: Float = 0f, bodyParts: List<BodyPart>? = null) {
		resetJob.cancelAndJoin()
		resetJob = context.scope.launch {
			val delayMs = (delay * 1000).toInt()
			val fullSeconds = delayMs / 1000
			val remainder = delayMs % 1000

			// Tell the GUI we started a reset
			server.sendSolarxrRpc(
				ResetResponse(resetType, ResetStatus.STARTED, bodyParts, 0, delayMs),
			)

			// Wait for the reset delay while updating the GUI every second
			repeat(fullSeconds) { index ->
				delay(1000)
				// Skip final tick if at the same time as finish
				if (index != fullSeconds - 1 || remainder != 0) {
					server.sendSolarxrRpc(
						ResetResponse(resetType, ResetStatus.STARTED, bodyParts, (index + 1) * 1000, delayMs),
					)
				}
			}
			delay(remainder.toLong())

			// Reset trackers
			executeTrackerResets(resetType, bodyParts, settings.context.state.value.data.resetsConfig)

			if (resetType == ResetType.FULL) {
				// Tell the skeleton to set the floor level and try resetting the head position (for mocap mode)
				skeleton.context.dispatchAll(listOf(SkeletonActions.ResetHeadPosition, SkeletonActions.ResetFloorLevel))
			}

			// Update state and config
			context.dispatch(ResetsActions.EndReset(resetType, bodyParts, settings.context.state.value.data.resetsConfig.resetMountingFeet))
			settings.context.dispatch(
				SettingsActions.Update {
					copy(
						resetsConfig = resetsConfig.copy(
							lastMountingMethod = if (resetType == ResetType.POSE_MOUNTING) {
								MountingMethod.POSE
							} else {
								resetsConfig.lastMountingMethod
							},
						),
					)
				},
			)

			AppLogger.resets.info("${resetType.name} Reset from $resetSourceName")

			// Tell the GUI we finished a reset
			server.sendSolarxrRpc(
				ResetResponse(resetType, ResetStatus.FINISHED, bodyParts, delayMs, delayMs),
			)
		}
	}

	suspend fun clearTrackersMountingReset(resetSourceName: String) {
		val trackers = server.context.state.value.trackers.values
		trackers.forEach { it.context.dispatch(TrackerActions.ClearMountingReset) }
		context.dispatch(ResetsActions.ClearMountingCompleted)

		AppLogger.resets.info("Clear Mounting Reset from $resetSourceName")
	}

	private fun getResetAction(referenceRotation: Quaternion?, resetType: ResetType, bodyPart: BodyPart?, resetsConfig: ResetsConfig) = when (resetType) {
		ResetType.YAW -> TrackerActions.YawReset(referenceRotation, resetsConfig.yawResetSmoothTime.toDouble().seconds)
		ResetType.FULL -> TrackerActions.FullReset(referenceRotation, resetsConfig.resetHmdAttitude)
		ResetType.POSE_MOUNTING -> TrackerActions.PoseMountingReset(referenceRotation, getYawOffset(bodyPart, resetsConfig.armsResetMode))
	}

	// By priority, 0 = highest priority
	private val referenceBodyParts = mapOf(
		BodyPart.HEAD to 0,
		BodyPart.UPPER_CHEST to 1,
		BodyPart.LOWER_CHEST to 2,
		BodyPart.HIP to 3,
		BodyPart.LOWER_WAIST to 4,
		BodyPart.UPPER_WAIST to 5,
	)

	private fun executeTrackerResets(resetType: ResetType, bodyParts: List<BodyPart>? = null, config: ResetsConfig) {
		val allTrackers = server.context.state.value.trackers.values

		val reliableReferenceTracker = allTrackers.firstOrNull {
			val state = it.context.state.value
			state.isHmd && state.bodyPart == BodyPart.HEAD && state.status.isActive()
		}
		val referenceTracker = reliableReferenceTracker ?: allTrackers.sortedBy {
			referenceBodyParts[it.context.state.value.bodyPart]
		}.firstOrNull {
			val state = it.context.state.value
			state.bodyPart in referenceBodyParts.keys && state.position != null && state.status.isActive()
		}

		// Never null
		val referenceRotation = referenceTracker?.let { reference ->
			// Reset the reference before the other trackers
			val preDispatchState = reference.context.state.value
			reference.context.dispatchAll(
				listOf(
					getResetAction(null, resetType, preDispatchState.bodyPart, config),
					TrackerActions.SetRotation(preDispatchState.rawRotation, preDispatchState.rawAcceleration, preDispatchState.rawMagnetometer, refresh = true),
				),
			)

			// Mounting reset on a non-reliable reference tracker offsets the yaw
			if (reference != reliableReferenceTracker && resetType == ResetType.POSE_MOUNTING) {
				// Get the headingCorrection (yaw reset correction) delta
				val headingCorrectionChange = reference.context.state.value.sessionCalibration.headingCorrection / preDispatchState.sessionCalibration.headingCorrection

				// Apply the delta to all other trackers
				(allTrackers - reference).forEach { otherTracker ->
					val otherTrackerState = otherTracker.context.state.value
					otherTracker.context.dispatchAll(
						listOf(
							TrackerActions.Update {
								otherTrackerState.copy(
									sessionCalibration = sessionCalibration.copy(
										headingCorrection = otherTrackerState.sessionCalibration.headingCorrection * headingCorrectionChange,
										headingAlignment = otherTrackerState.sessionCalibration.headingAlignment * headingCorrectionChange,
									),
									rotationDirty = true,
								)
							},
							TrackerActions.SetRotation(otherTrackerState.rawRotation, otherTrackerState.rawAcceleration, otherTrackerState.rawMagnetometer, refresh = true),
						),
					)
				}
			}

			// TODO should this be twinNearest against centaur
			//  reducer may need tweaks too against centaur. adjust unit tests.
			reference.context.state.value.rotation
		} ?: Quaternion.IDENTITY

		// Filter out the trackers that we want to reset. Reference has already been reset.
		val trackersToReset = if (!bodyParts.isNullOrEmpty()) {
			allTrackers.filter {
				bodyParts.contains(it.context.state.value.bodyPart)
			}
		} else {
			// Exclude feet, fingers and toes from mounting reset except if forced
			allTrackers.filter {
				val bodyPart = it.context.state.value.bodyPart
				resetType != ResetType.POSE_MOUNTING ||
					(
						(config.resetMountingFeet || bodyPart !in ResetBodyParts.FEET) &&
							bodyPart !in ResetBodyParts.FINGERS &&
							bodyPart !in ResetBodyParts.TOES
						)
			}
		}.filter { it.context.state.value.id != referenceTracker?.context?.state?.value?.id }

		// Dispatch the reset action to the trackers
		trackersToReset.forEach {
			// Do the actual reset
			it.context.dispatch(getResetAction(referenceRotation, resetType, it.context.state.value.bodyPart, config))
		}
	}

	private fun getYawOffset(bodyPart: BodyPart?, armsResetMode: ArmsResetMode) = when (bodyPart) {
		// Going forward
		in ResetBodyParts.UPPER_LEGS -> 0f

		in ResetBodyParts.LOWER_ARMS if armsResetMode == ArmsResetMode.BACK -> 0f

		in ResetBodyParts.ARMS if armsResetMode == ArmsResetMode.FORWARD -> 0f

		// Going left/
		in ResetBodyParts.LEFT_ARM if armsResetMode == ArmsResetMode.T_POSE_UP -> -FastMath.HALF_PI

		in ResetBodyParts.RIGHT_ARM if armsResetMode == ArmsResetMode.T_POSE_DOWN -> -FastMath.HALF_PI

		// Going right
		in ResetBodyParts.LEFT_ARM if armsResetMode == ArmsResetMode.T_POSE_DOWN -> FastMath.HALF_PI

		in ResetBodyParts.RIGHT_ARM if armsResetMode == ArmsResetMode.T_POSE_UP -> FastMath.HALF_PI

		// Going back
		else -> FastMath.PI
	}

	companion object {
		fun create(ctx: Phase1ContextProvider, skeleton: Skeleton, scope: CoroutineScope): ResetsManager {
			val context = Context.create(
				initialState = ResetsState(
					canDoYawReset = false,
					canDoMountingReset = false,
					lastFullResetTime = null,
				),
				scope = scope,
				reducer = ::reduce,
				behaviours = listOf(ResetsMountingTimeoutBehaviour()),
				name = "ResetsManager",
			)
			return ResetsManager(context, ctx.server, ctx.config.settings, skeleton)
		}
	}
}
