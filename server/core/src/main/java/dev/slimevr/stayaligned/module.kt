package dev.slimevr.stayaligned

import dev.slimevr.Phase1ContextProvider
import dev.slimevr.VRServer
import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import dev.slimevr.config.StayAlignedRelaxedPoseConfig
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.logging.AppLogger
import dev.slimevr.skeleton.Skeleton
import dev.slimevr.stayaligned.poses.RelaxedPose
import kotlinx.coroutines.CoroutineScope
import solarxr_protocol.rpc.StayAlignedRelaxedPose
import kotlin.collections.listOf

data class StayAlignedState(
	/**
	 * Temporarily hides the correction from Stay Aligned.
	 *
	 * Used to compare to when Stay Aligned is not enabled.
	 * Useful to verify if Stay Aligned improved the situation or is responsible for bad tracking.
	 */
	val hideCorrection: Boolean,
)

sealed interface StayAlignedActions {
	data class SetHideYawCorrection(val hideYawCorrection: Boolean) : StayAlignedActions
}

typealias StayAlignedContext = Context<StayAlignedState, StayAlignedActions>
typealias StayAlignedBehaviour = Behaviour<StayAlignedState, StayAlignedActions, StayAlignedManager>

class StayAlignedManager(val context: StayAlignedContext, val server: VRServer, val skeleton: Skeleton, val settings: Settings) {
	fun startObserving() = context.observeAll(this)

	/**
	 * Sets and enables a relaxed pose from the user's current pose.
	 */
    suspend fun detectRelaxedPose(pose: StayAlignedRelaxedPose) {
		val trackerStates = server.context.state.value.trackers.values.map { it.context.state.value }
		val relaxedPose = RelaxedPose.fromTrackers(trackerStates)
		updatePoseInConfig(pose, StayAlignedRelaxedPoseConfig(true, relaxedPose.upperLeg.toDeg(), relaxedPose.lowerLeg.toDeg(), relaxedPose.foot.toDeg()))
		AppLogger.stayAligned.info("Set relaxed pose $pose with $relaxedPose")
	}

	/**
	 * Resets and disables a relaxed pose.
	 */
    suspend fun resetRelaxedPose(pose: StayAlignedRelaxedPose) {
		updatePoseInConfig(pose, StayAlignedRelaxedPoseConfig(false, 0f, 0f, 0f))
		AppLogger.stayAligned.info("Reset relaxed pose $pose")
	}

	private fun updatePoseInConfig(pose: StayAlignedRelaxedPose, poseConfig: StayAlignedRelaxedPoseConfig) {
		settings.context.dispatch(
			SettingsActions.Update {
				copy(
					stayAlignedConfig = when (pose) {
						StayAlignedRelaxedPose.STANDING -> stayAlignedConfig.copy(standingRelaxedPose = poseConfig)
						StayAlignedRelaxedPose.SITTING -> stayAlignedConfig.copy(sittingRelaxedPose = poseConfig)
						StayAlignedRelaxedPose.FLAT -> stayAlignedConfig.copy(flatRelaxedPose = poseConfig)
					},
				)
			},
		)
	}

	companion object {
		fun create(ctx: Phase1ContextProvider, skeleton: Skeleton, scope: CoroutineScope): StayAlignedManager {
			val context = Context.create(
				initialState = StayAlignedState(
					hideCorrection = false,
				),
				scope = scope,
				behaviours = listOf(StayAlignedBasicBehaviour()),
				name = "StayAlignedManager",
			)
			return StayAlignedManager(context, ctx.server, skeleton, ctx.config.settings)
		}
	}
}
