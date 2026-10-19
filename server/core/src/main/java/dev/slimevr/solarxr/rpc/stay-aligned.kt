package dev.slimevr.solarxr.rpc

import dev.slimevr.VRServer
import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import dev.slimevr.config.StayAlignedConfig
import dev.slimevr.config.UserConfig
import dev.slimevr.config.UserConfigActions
import dev.slimevr.config.UserConfigData
import dev.slimevr.logging.AppLogger
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import dev.slimevr.tracker.TrackerState
import dev.slimevr.tracker.stayaligned.PoseRecordingCheck
import dev.slimevr.tracker.stayaligned.StayAlignedDefaults
import dev.slimevr.tracker.stayaligned.checkPoseRecording
import dev.slimevr.tracker.stayaligned.missingRequiredParts
import dev.slimevr.tracker.stayaligned.poses.PlayerPose
import dev.slimevr.tracker.stayaligned.poses.STAY_ALIGNED_POSES
import dev.slimevr.tracker.stayaligned.poses.hasStayAlignedPoseDefault
import dev.slimevr.tracker.stayaligned.poses.stayAlignedPoseConfig
import dev.slimevr.tracker.stayaligned.recordedPoseConfig
import dev.slimevr.trackingchecklist.TrackingChecklist
import dev.slimevr.util.allContextStates
import dev.slimevr.util.isActive
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch
import solarxr_protocol.rpc.CancelStayAlignedPoseRecordingRequest
import solarxr_protocol.rpc.ChangeStayAlignedSettingsRequest
import solarxr_protocol.rpc.ResetStayAlignedRelaxedPoseRequest
import solarxr_protocol.rpc.StartStayAlignedPoseRecordingRequest
import solarxr_protocol.rpc.StartStayAlignedStatusRequest
import solarxr_protocol.rpc.StayAlignedDetectedPoseState
import solarxr_protocol.rpc.StayAlignedPose
import solarxr_protocol.rpc.StayAlignedPoseCountdown
import solarxr_protocol.rpc.StayAlignedPoseRecordingDetail
import solarxr_protocol.rpc.StayAlignedPoseRecordingFailure
import solarxr_protocol.rpc.StayAlignedPoseRecordingLifecycle
import solarxr_protocol.rpc.StayAlignedPoseRecordingStatusResponse
import solarxr_protocol.rpc.StayAlignedPoseState
import solarxr_protocol.rpc.StayAlignedPoseWaiting
import solarxr_protocol.rpc.StayAlignedRecordingTracker
import solarxr_protocol.rpc.StayAlignedRelaxedPose
import solarxr_protocol.rpc.StayAlignedSettingsRequest
import solarxr_protocol.rpc.StayAlignedSettingsResponse
import solarxr_protocol.rpc.StayAlignedStatusResponse
import solarxr_protocol.rpc.StayAlignedTrackerCorrection
import solarxr_protocol.rpc.StopStayAlignedStatusRequest
import solarxr_protocol.rpc.TrackingChecklistStepId
import kotlin.math.abs
import kotlin.math.round
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

internal fun buildStayAlignedSettingsResponse(config: StayAlignedConfig, userConfig: UserConfigData) = StayAlignedSettingsResponse(
	enabled = config.enabled,
	poses = STAY_ALIGNED_POSES.map { pose ->
		val poseConfig = stayAlignedPoseConfig(userConfig, pose)
		StayAlignedPose(
			pose = pose,
			recorded = poseConfig.recorded,
			hasDefault = hasStayAlignedPoseDefault(pose),
			enabled = poseConfig.enabled,
			upperLegAngle = poseConfig.upperLegAngleInDeg,
			lowerLegAngle = poseConfig.lowerLegAngleInDeg,
			footAngle = poseConfig.footAngleInDeg,
		)
	},
)

internal fun buildStayAlignedStatusResponse(enabled: Boolean, trackerStates: List<TrackerState>, userConfig: UserConfigData) = StayAlignedStatusResponse(
	enabled = enabled,
	detectedPose = detectedPose(PlayerPose.of(trackerStates).relaxedPose, userConfig),
	missingParts = missingRequiredParts(trackerStates),
	largestCorrection = if (enabled) largestCorrection(trackerStates) else null,
)

private fun largestCorrection(trackerStates: List<TrackerState>): StayAlignedTrackerCorrection? {
	val corrections = trackerStates.mapNotNull { state ->
		// A tracker that is not connected keeps its correction but nothing uses it
		val bodyPart = state.bodyPart?.takeIf { state.status.isActive() } ?: return@mapNotNull null
		StayAlignedTrackerCorrection(bodyPart = bodyPart, degrees = round(abs(state.stayAlignedData.yawCorrection.toDeg()) * 10f) / 10f)
	}
	return corrections.maxByOrNull { it.degrees }?.takeIf { it.degrees > 0f }
}

private fun detectedPose(pose: StayAlignedRelaxedPose, userConfig: UserConfigData): StayAlignedDetectedPoseState? {
	if (pose == StayAlignedRelaxedPose.UNKNOWN) return null
	val config = stayAlignedPoseConfig(userConfig, pose)
	val state = when {
		config.enabled -> StayAlignedPoseState.ENABLED
		!config.recorded && !hasStayAlignedPoseDefault(pose) -> StayAlignedPoseState.NOT_RECORDED
		else -> StayAlignedPoseState.DISABLED
	}
	return StayAlignedDetectedPoseState(pose = pose, state = state)
}

class StayAlignedBehaviour(
	private val settings: Settings,
	private val userConfig: UserConfig,
	private val server: VRServer,
	private val trackingChecklist: TrackingChecklist,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		observeSettings(receiver)
		observeStatus(receiver)
		observeRecording(receiver)
	}

	private suspend fun sendConfig(receiver: SolarXRBridge) {
		receiver.sendRpc(buildStayAlignedSettingsResponse(settings.context.state.value.data.stayAlignedConfig, userConfig.context.state.value.data))
	}

	private fun observeSettings(receiver: SolarXRBridge) {
		receiver.rpcDispatcher.on<StayAlignedSettingsRequest> {
			sendConfig(receiver)
		}.launchIn(receiver.context.scope)

		// Push config changes, including profile switches
		combine(
			settings.context.state.map { it.data.stayAlignedConfig }.distinctUntilChanged(),
			userConfig.context.state.map { it.data }.distinctUntilChanged(),
			::buildStayAlignedSettingsResponse,
		)
			.distinctUntilChanged()
			.drop(1)
			.onEach(receiver::sendRpc)
			.launchIn(receiver.context.scope)

		receiver.rpcDispatcher.on<ChangeStayAlignedSettingsRequest> { req ->
			settings.context.dispatch(SettingsActions.Update { copy(stayAlignedConfig = StayAlignedConfig(enabled = req.enabled)) })
			userConfig.context.dispatch(
				UserConfigActions.Update {
					// Only the poses that change are stored, so the others keep following their default.
					// A pose can only be turned on once it is recorded, or when it has a default
					val toggled = req.poseToggles.orEmpty()
						.filter { stayAlignedPoseConfig(this, it.pose).enabled != it.enabled }
						.filter { !it.enabled || stayAlignedPoseConfig(this, it.pose).recorded || hasStayAlignedPoseDefault(it.pose) }
						.associate { it.pose to stayAlignedPoseConfig(this, it.pose).copy(enabled = it.enabled) }
					copy(stayAlignedPoses = stayAlignedPoses + toggled)
				},
			)
			sendConfig(receiver)
		}.launchIn(receiver.context.scope)

		receiver.rpcDispatcher.on<ResetStayAlignedRelaxedPoseRequest> { req ->
			if (req.pose == StayAlignedRelaxedPose.UNKNOWN) return@on
			userConfig.context.dispatch(UserConfigActions.Update { copy(stayAlignedPoses = stayAlignedPoses - req.pose) })
			AppLogger.stayAligned.info("Reset relaxed pose ${req.pose}")
			sendConfig(receiver)
		}.launchIn(receiver.context.scope)
	}

	@OptIn(FlowPreview::class)
	private fun observeStatus(receiver: SolarXRBridge) {
		var statusJob: Job? = null

		receiver.rpcDispatcher.on<StartStayAlignedStatusRequest> {
			statusJob?.cancel()
			statusJob = combine(
				settings.context.state.map { it.data.stayAlignedConfig.enabled }.distinctUntilChanged(),
				userConfig.context.state.map { it.data }.distinctUntilChanged(),
				allContextStates(server.context.state, { state -> state.trackers.values }) { tracker ->
					tracker.context.state.sample(STATUS_SAMPLE_INTERVAL)
				},
			) { enabled, userConfigData, trackers -> buildStayAlignedStatusResponse(enabled, trackers, userConfigData) }
				.distinctUntilChanged()
				.onEach(receiver::sendRpc)
				.launchIn(receiver.context.scope)
		}.launchIn(receiver.context.scope)

		receiver.rpcDispatcher.on<StopStayAlignedStatusRequest> {
			statusJob?.cancel()
			statusJob = null
		}.launchIn(receiver.context.scope)
	}

	private fun currentTrackerStates(): List<TrackerState> = server.context.state.value.trackers.values.map { it.context.state.value }

	private fun observeRecording(receiver: SolarXRBridge) {
		var recordingJob: Job? = null
		var recordingPose: StayAlignedRelaxedPose? = null

		receiver.rpcDispatcher.on<StartStayAlignedPoseRecordingRequest> { req ->
			if (req.pose == StayAlignedRelaxedPose.UNKNOWN) return@on
			recordingJob?.cancel()
			recordingPose = req.pose
			recordingJob = receiver.context.scope.launch { recordPose(receiver, req.pose) }
		}.launchIn(receiver.context.scope)

		receiver.rpcDispatcher.on<CancelStayAlignedPoseRecordingRequest> {
			val job = recordingJob ?: return@on
			val pose = recordingPose ?: return@on
			if (!job.isActive) return@on
			job.cancel()
			receiver.sendRpc(recordingStatus(pose, StayAlignedPoseRecordingLifecycle.CANCELED, null))
		}.launchIn(receiver.context.scope)
	}

	/**
	 * Gives the user time to get into the pose, then waits until every tracker that is read is still and
	 * the position matches the pose, and saves the pose. Gives up when that does not happen in time.
	 */
	private suspend fun recordPose(receiver: SolarXRBridge, pose: StayAlignedRelaxedPose) {
		val countdown = StayAlignedDefaults.POSE_RECORDING_COUNTDOWN_SECONDS
		val timeout = StayAlignedDefaults.POSE_RECORDING_TIMEOUT_SECONDS

		if (missingRequiredParts(currentTrackerStates()).isNotEmpty()) {
			AppLogger.stayAligned.info("Not recording relaxed pose $pose: trackers are missing")
			receiver.sendRpc(recordingStatus(pose, StayAlignedPoseRecordingLifecycle.FAILED, null, StayAlignedPoseRecordingFailure.MISSING_TRACKERS))
			return
		}

		// A pose measured on trackers that are not reset or mounted would be saved wrong
		val steps = trackingChecklist.context.state.value.steps
		if (CALIBRATION_STEPS.any { id -> steps[id]?.let { it.enabled && !it.valid } == true }) {
			AppLogger.stayAligned.info("Not recording relaxed pose $pose: trackers are not calibrated")
			receiver.sendRpc(recordingStatus(pose, StayAlignedPoseRecordingLifecycle.FAILED, null, StayAlignedPoseRecordingFailure.NOT_CALIBRATED))
			return
		}

		receiver.sendRpc(recordingStatus(pose, StayAlignedPoseRecordingLifecycle.RUNNING, StayAlignedPoseCountdown(0, countdown)))
		repeat(countdown) { index ->
			delay(1.seconds)
			if (index != countdown - 1) {
				receiver.sendRpc(recordingStatus(pose, StayAlignedPoseRecordingLifecycle.RUNNING, StayAlignedPoseCountdown(index + 1, countdown)))
			}
		}

		// The GUI shows the checks and the seconds waited, so both are sent whenever one changes
		val checks = allContextStates(server.context.state, { state -> state.trackers.values }) { tracker -> tracker.context.state }
			.map { trackers -> checkPoseRecording(trackers, pose) }
			.distinctUntilChanged()
		val waited = flow {
			repeat(timeout + 1) { second ->
				emit(second)
				delay(1.seconds)
			}
		}
		val (check, seconds) = combine(checks, waited, ::Pair)
			.onEach { (check, seconds) -> receiver.sendRpc(recordingStatus(pose, StayAlignedPoseRecordingLifecycle.RUNNING, waitingDetail(seconds, timeout, check))) }
			.first { (check, seconds) -> check.failure == StayAlignedPoseRecordingFailure.NONE || seconds == timeout }

		if (check.failure != StayAlignedPoseRecordingFailure.NONE) {
			AppLogger.stayAligned.info("Relaxed pose $pose not recorded: ${check.failure}")
			receiver.sendRpc(recordingStatus(pose, StayAlignedPoseRecordingLifecycle.FAILED, waitingDetail(seconds, timeout, check), check.failure))
			return
		}

		// The trackers may have changed since the check that passed, so what is saved is measured now
		val trackers = currentTrackerStates()
		if (missingRequiredParts(trackers).isNotEmpty()) {
			receiver.sendRpc(recordingStatus(pose, StayAlignedPoseRecordingLifecycle.FAILED, waitingDetail(seconds, timeout, check), StayAlignedPoseRecordingFailure.MISSING_TRACKERS))
			return
		}
		val recorded = recordedPoseConfig(trackers)
		userConfig.context.dispatch(UserConfigActions.Update { copy(stayAlignedPoses = stayAlignedPoses + (pose to recorded)) })
		AppLogger.stayAligned.info("Recorded relaxed pose $pose: upperLeg=${recorded.upperLegAngleInDeg} lowerLeg=${recorded.lowerLegAngleInDeg} foot=${recorded.footAngleInDeg}")
		receiver.sendRpc(recordingStatus(pose, StayAlignedPoseRecordingLifecycle.DONE, waitingDetail(seconds, timeout, check)))
	}

	private fun waitingDetail(progressSeconds: Int, durationSeconds: Int, check: PoseRecordingCheck) = StayAlignedPoseWaiting(
		progress = progressSeconds,
		duration = durationSeconds,
		detectedPose = check.detectedPose,
		trackers = check.parts.map { (part, state) -> StayAlignedRecordingTracker(part = part, state = state) },
	)

	private fun recordingStatus(
		pose: StayAlignedRelaxedPose,
		lifecycle: StayAlignedPoseRecordingLifecycle,
		detail: StayAlignedPoseRecordingDetail?,
		failure: StayAlignedPoseRecordingFailure = StayAlignedPoseRecordingFailure.NONE,
	) = StayAlignedPoseRecordingStatusResponse(pose = pose, lifecycle = lifecycle, detail = detail, failure = failure)

	private companion object {
		val STATUS_SAMPLE_INTERVAL = 250.milliseconds
		val CALIBRATION_STEPS = listOf(
			TrackingChecklistStepId.FULL_RESET,
			TrackingChecklistStepId.MOUNTING_CALIBRATION,
			TrackingChecklistStepId.FEET_MOUNTING_CALIBRATION,
		)
	}
}
