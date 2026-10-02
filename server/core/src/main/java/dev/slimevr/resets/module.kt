package dev.slimevr.resets

import com.jme3.math.FastMath
import dev.slimevr.Phase1ContextProvider
import dev.slimevr.VRServer
import dev.slimevr.config.ResetsConfig
import dev.slimevr.config.Settings
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.logging.AppLogger
import dev.slimevr.sentry.ErrorReportingManager
import dev.slimevr.skeleton.Skeleton
import dev.slimevr.skeleton.SkeletonActions
import dev.slimevr.stepmounting.StepMountingManager
import dev.slimevr.stepmounting.StepMountingStatus
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.tracker.behaviours.TrackerRotationRefreshBehaviour
import dev.slimevr.util.isActive
import io.github.axisangles.ktmath.Quaternion
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import solarxr_protocol.data_feed.server.ResetAvailability
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.MountingMethod
import solarxr_protocol.rpc.ArmsMountingResetMode
import solarxr_protocol.rpc.CountdownDetail
import solarxr_protocol.rpc.ResetDetail
import solarxr_protocol.rpc.ResetLifecycle
import solarxr_protocol.rpc.ResetStatusResponse
import solarxr_protocol.rpc.ResetType
import solarxr_protocol.rpc.StepMountingDetail
import kotlin.collections.contains
import kotlin.collections.listOf
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeMark
import solarxr_protocol.rpc.StepMountingStatus as RpcStepMountingStatus

data class ResetsState(
	val canDoYawReset: Boolean,
	val canDoMountingReset: Boolean,
	val lastFullResetTime: TimeMark?,
	val status: ResetStatusResponse? = null,
)

sealed interface ResetsActions {
	data class ClearResets(val resetTypes: List<ResetType>) : ResetsActions
	data class EndReset(val resetType: ResetType) : ResetsActions
	data class SetStatus(val status: ResetStatusResponse) : ResetsActions
}

typealias ResetsContext = Context<ResetsState, ResetsActions>
typealias ResetsBehaviour = Behaviour<ResetsManager>

class ResetsManager(
	val context: ResetsContext,
	val server: VRServer,
	val settings: Settings,
	val skeleton: Skeleton,
	private val stepMountingManager: StepMountingManager,
	private val errorReporting: ErrorReportingManager,
) {
	fun startObserving() = context.observeAll(this)

	private var resetJob: Job = Job()

	fun availability(resetType: ResetType): ResetAvailability = when (resetType) {
		ResetType.FULL -> ResetAvailability.AVAILABLE
		ResetType.YAW -> yawAvailability()
		ResetType.MOUNTING -> mountingAvailability()
	}

	private fun yawAvailability(): ResetAvailability = when {
		server.context.state.value.trackers.isEmpty() -> ResetAvailability.NO_TRACKERS
		!context.state.value.canDoYawReset -> ResetAvailability.NEEDS_FULL_RESET
		else -> ResetAvailability.AVAILABLE
	}

	private fun mountingAvailability(): ResetAvailability {
		val usesStepMounting = settings.context.state.value.data.resetsConfig.mountingMethod == MountingMethod.STEP
		return when {
			server.context.state.value.trackers.isEmpty() -> ResetAvailability.NO_TRACKERS
			!context.state.value.canDoMountingReset -> ResetAvailability.NEEDS_FULL_RESET
			usesStepMounting && !stepMountingManager.context.state.value.canDoStepMounting -> ResetAvailability.NEEDS_POSITIONAL_HEAD
			else -> ResetAvailability.AVAILABLE
		}
	}

	/**
	 * Schedules a reset according to the resetType.
	 * A MOUNTING reset runs the configured mounting method.
	 * resetSourceName is used for logging
	 * If bodyParts is null, resets all trackers.
	 */
	suspend fun scheduleReset(resetSourceName: String, resetType: ResetType, delay: Float = 0f, bodyParts: List<BodyPart>? = null) {
		val method = settings.context.state.value.data.resetsConfig.mountingMethod.takeIf { resetType == ResetType.MOUNTING }
		if (method == MountingMethod.UNKNOWN || method == MountingMethod.MANUAL) {
			AppLogger.resets.info("Ignoring mounting reset from $resetSourceName: the mounting method is $method")
			return
		}
		val availability = availability(resetType)
		if (availability != ResetAvailability.AVAILABLE) {
			AppLogger.resets.info("Ignoring ${resetType.name} reset from $resetSourceName: $availability")
			return
		}

		errorReporting.reportUsage(
			"reset",
			mapOf(
				"resetType" to resetType.name,
				// SolarXR sources are named per connection "SolarXR[3]"
				"source" to resetSourceName.substringBefore('['),
				"bodyParts" to (bodyParts?.map { it.name }?.sorted()?.joinToString(",") ?: "all"),
			),
		)
		if (method != null) {
			errorReporting.reportUsageOncePerSession("mounting_method_used", mapOf("method" to method.name.lowercase()))
		}

		resetJob.cancelAndJoin()
		resetJob = context.scope.launch { runReset(resetSourceName, resetType, method, delay, bodyParts) }
	}

	suspend fun cancel() {
		if (context.state.value.status?.lifecycle != ResetLifecycle.RUNNING) return
		resetJob.cancelAndJoin()
	}

	private suspend fun runReset(resetSourceName: String, resetType: ResetType, method: MountingMethod?, delaySeconds: Float, bodyParts: List<BodyPart>?) {
		val delayMs = (delaySeconds * 1000).toInt()
		var lastDetail: ResetDetail = CountdownDetail(0, delayMs)

		suspend fun publish(lifecycle: ResetLifecycle, detail: ResetDetail) {
			lastDetail = detail
			val status = ResetStatusResponse(resetType, lifecycle, bodyParts, detail)
			context.dispatch(ResetsActions.SetStatus(status))
			server.sendSolarxrRpc(status)
		}

		try {
			publish(ResetLifecycle.RUNNING, CountdownDetail(0, delayMs))

			// Wait for the reset delay while updating the GUI every second
			val fullSeconds = delayMs / 1000
			val remainder = delayMs % 1000
			repeat(fullSeconds) { index ->
				delay(1.seconds)
				// Skip final tick if at the same time as finish
				if (index != fullSeconds - 1 || remainder != 0) {
					publish(ResetLifecycle.RUNNING, CountdownDetail((index + 1) * 1000, delayMs))
				}
			}
			delay(remainder.toLong())

			val config = settings.context.state.value.data.resetsConfig
			if (method == MountingMethod.STEP) {
				val failure = runStepMounting { publish(ResetLifecycle.RUNNING, StepMountingDetail(it)) }
				if (failure != null) {
					AppLogger.resets.info("Step mounting from $resetSourceName failed: $failure")
					publish(ResetLifecycle.FAILED, StepMountingDetail(failure))
					return
				}
			} else {
				executeTrackerResets(resetType, method, bodyParts, config)
			}

			if (resetType == ResetType.FULL) {
				// Tell the skeleton to set the floor level and try resetting the head position (for mocap mode)
				skeleton.context.dispatchAll(listOf(SkeletonActions.ResetHeadPosition, SkeletonActions.ResetFloorLevel))
			}

			context.dispatch(ResetsActions.EndReset(resetType))

			AppLogger.resets.info("${resetType.name} Reset from $resetSourceName")

			publish(ResetLifecycle.DONE, if (method == MountingMethod.STEP) lastDetail else CountdownDetail(delayMs, delayMs))
		} catch (e: CancellationException) {
			withContext(NonCancellable) { publish(ResetLifecycle.CANCELED, lastDetail) }
			throw e
		}
	}

	private suspend fun runStepMounting(onPhase: suspend (RpcStepMountingStatus) -> Unit): RpcStepMountingStatus? {
		stepMountingManager.cancel()
		stepMountingManager.start()
		try {
			val lastStatus = stepMountingManager.context.state
				.map { it.status }
				.distinctUntilChanged()
				.onEach { status -> stepMountingPhase(status)?.let { onPhase(it) } }
				.first { it == StepMountingStatus.DONE || stepMountingFailure(it) != null }
			return stepMountingFailure(lastStatus)
		} finally {
			stepMountingManager.cancel()
		}
	}

	private fun stepMountingPhase(status: StepMountingStatus) = when (status) {
		StepMountingStatus.WAITING_FOR_MOVEMENT -> RpcStepMountingStatus.WAITING_FOR_MOVEMENT
		StepMountingStatus.RECORDING -> RpcStepMountingStatus.RECORDING
		StepMountingStatus.PROCESSING -> RpcStepMountingStatus.PROCESSING
		else -> null
	}

	private fun stepMountingFailure(status: StepMountingStatus) = when (status) {
		StepMountingStatus.ERROR_NO_DATA -> RpcStepMountingStatus.ERROR_NO_DATA
		StepMountingStatus.ERROR_TIMEOUT -> RpcStepMountingStatus.ERROR_TIMEOUT
		else -> null
	}

	suspend fun clearTrackersMountingReset(resetSourceName: String) {
		val trackers = server.context.state.value.trackers.values
		trackers.forEach { it.context.dispatch(TrackerActions.ClearMountingReset) }

		AppLogger.resets.info("Clear Mounting Reset from $resetSourceName")
	}

	private fun getYawOffset(bodyPart: BodyPart?, armsResetMode: ArmsMountingResetMode) = when (bodyPart) {
		// Going forward
		in ResetBodyParts.UPPER_LEGS -> 0f

		in ResetBodyParts.LOWER_ARMS if armsResetMode == ArmsMountingResetMode.BACK -> 0f

		in ResetBodyParts.ARMS if armsResetMode == ArmsMountingResetMode.FORWARD -> 0f

		// Going left/
		in ResetBodyParts.LEFT_ARM if armsResetMode == ArmsMountingResetMode.SIDE -> -FastMath.HALF_PI

		// Going right
		in ResetBodyParts.RIGHT_ARM if armsResetMode == ArmsMountingResetMode.SIDE -> FastMath.HALF_PI

		// Going back
		else -> FastMath.PI
	}

	private fun getResetAction(referenceRotation: Quaternion?, resetType: ResetType, bodyPart: BodyPart?, resetsConfig: ResetsConfig) = when (resetType) {
		ResetType.YAW -> TrackerActions.YawReset(referenceRotation, resetsConfig.yawResetSmoothTime.toDouble().seconds)
		ResetType.FULL -> TrackerActions.FullReset(referenceRotation, resetsConfig.resetReliableReferenceAttitude)
		ResetType.MOUNTING -> TrackerActions.PoseMountingReset(referenceRotation, getYawOffset(bodyPart, resetsConfig.armsMountingResetMode))
	}

	// By priority, higher value = higher priority. 0 for others.
	private val referenceBodyParts = mapOf(
		BodyPart.HEAD to 7,
		BodyPart.NECK to 6,
		BodyPart.UPPER_CHEST to 5,
		BodyPart.LOWER_CHEST to 4,
		BodyPart.HIP to 3,
		BodyPart.LOWER_WAIST to 2,
		BodyPart.UPPER_WAIST to 1,
		BodyPart.LEFT_HAND to -1,
		BodyPart.RIGHT_HAND to -1,
	)

	private fun getSortedReferenceTrackers(allTrackers: Collection<Tracker>) = allTrackers.sortedBy {
		referenceBodyParts[it.context.state.value.bodyPart]
	}.filter {
		val state = it.context.state.value
		state.position != null && state.bodyPart != null && state.status.isActive()
	}

	private fun resetReferenceTracker(referenceTracker: Tracker, resetAction: TrackerActions, isReliable: Boolean, allTrackers: Collection<Tracker>) {
		// Reset the reference tracker
		val preDispatchState = referenceTracker.context.state.value
		referenceTracker.context.dispatchAll(
			listOf(
				resetAction,
				TrackerRotationRefreshBehaviour.getRotationRefreshAction(preDispatchState),
			),
		)

		// Mounting reset on a non-reliable reference tracker offsets the yaw
		if (!isReliable && resetAction is TrackerActions.PoseMountingReset) {
			val postDispatchState = referenceTracker.context.state.value

			// Get the new reference rotation
			val referenceRotation = postDispatchState.rotation
			// Get the headingCorrection (yaw reset correction) delta
			val headingCorrectionChange = postDispatchState.sessionCalibration.headingCorrection / preDispatchState.sessionCalibration.headingCorrection

			// Apply the delta to all other trackers
			(allTrackers - referenceTracker).forEach { otherTracker ->
				val otherTrackerState = otherTracker.context.state.value
				otherTracker.context.dispatchAll(
					listOf(
						TrackerActions.Update {
							otherTrackerState.copy(
								sessionCalibration = sessionCalibration.copy(
									headingCorrection = otherTrackerState.sessionCalibration.headingCorrection * headingCorrectionChange,
									headingAlignment = otherTrackerState.sessionCalibration.headingAlignment * headingCorrectionChange,
								),
								lastReference = referenceRotation,
								rotationDirty = true,
							)
						},
						TrackerRotationRefreshBehaviour.getRotationRefreshAction(otherTrackerState),
					),
				)
			}
		}
	}

	private fun filterTrackers(allTrackers: Collection<Tracker>, bodyParts: List<BodyPart>?, method: MountingMethod?, config: ResetsConfig): List<Tracker> = if (!bodyParts.isNullOrEmpty()) {
		allTrackers.filter { bodyParts.contains(it.context.state.value.bodyPart) }
	} else {
		// Exclude feet, fingers and toes from the pose mounting except if forced
		allTrackers.filter {
			val bodyPart = it.context.state.value.bodyPart
			method != MountingMethod.POSE ||
				(
					(config.resetMountingFeet || bodyPart !in ResetBodyParts.FEET) &&
						bodyPart !in ResetBodyParts.FINGERS &&
						bodyPart !in ResetBodyParts.TOES
					)
		}
	}

	private fun executeTrackerResets(resetType: ResetType, method: MountingMethod?, bodyParts: List<BodyPart>?, config: ResetsConfig) {
		val allTrackers = server.context.state.value.trackers.values

		// Get the reference. The reference is used to align rotation/spaces.
		val sortedReferenceTrackers = getSortedReferenceTrackers(allTrackers)
		val reliableReferenceTracker = sortedReferenceTrackers.lastOrNull { it.context.state.value.isAssignedReliableReference }
		val referenceTracker = reliableReferenceTracker ?: sortedReferenceTrackers.lastOrNull()

		// Reset the reference before the other trackers
		if (referenceTracker != null) {
			resetReferenceTracker(
				referenceTracker,
				getResetAction(null, resetType, referenceTracker.context.state.value.bodyPart, config),
				referenceTracker == reliableReferenceTracker,
				allTrackers,
			)
		}

		// The reference rotation is never null for other trackers
		val referenceRotation = referenceTracker?.context?.state?.value?.rotation ?: Quaternion.IDENTITY

		// Filter out the trackers that we want to reset. Reference has already been reset.
		val trackersToReset = filterTrackers(allTrackers, bodyParts, method, config).filter { it != referenceTracker }

		// Dispatch the reset action to the trackers
		trackersToReset.forEach {
			// Do the actual reset
			it.context.dispatch(getResetAction(referenceRotation, resetType, it.context.state.value.bodyPart, config))
		}
	}

	companion object {
		fun create(ctx: Phase1ContextProvider, skeleton: Skeleton, stepMountingManager: StepMountingManager, scope: CoroutineScope): ResetsManager {
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
			return ResetsManager(context, ctx.server, ctx.config.settings, skeleton, stepMountingManager, ctx.errorReporting)
		}
	}
}
