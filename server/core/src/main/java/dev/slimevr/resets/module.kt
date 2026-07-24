package dev.slimevr.resets

import com.jme3.math.FastMath
import dev.slimevr.AppLogger
import dev.slimevr.Phase1ContextProvider
import dev.slimevr.VRServer
import dev.slimevr.config.MountingMethods
import dev.slimevr.config.ResetsConfig
import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.util.safeLaunch
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.rpc.ArmsResetMode
import solarxr_protocol.rpc.ResetResponse
import solarxr_protocol.rpc.ResetStatus
import solarxr_protocol.rpc.ResetType
import kotlin.collections.contains
import kotlin.collections.listOf

data class ResetsState(
	val canDoYawReset: Boolean,
	val canDoMountingReset: Boolean,
	val lastFullResetTime: Long,
)

sealed interface ResetsActions {
	data class ClearResets(val resetType: List<ResetType>) : ResetsActions
	data class EndReset(val resetType: ResetType) : ResetsActions
}

typealias ResetsContext = Context<ResetsState, ResetsActions>
typealias ResetsBehaviour = Behaviour<ResetsState, ResetsActions, ResetsManager>

class ResetsManager(val context: ResetsContext, val server: VRServer, val settings: Settings) {
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
		resetJob = context.scope.safeLaunch {
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
			delay((remainder).toLong())

			executeTrackerResets(resetType, bodyParts, settings.context.state.value.data.resetsConfig)

			// Update state and config
			context.dispatch(ResetsActions.EndReset(resetType))
			settings.context.dispatch(SettingsActions.Update { copy(resetsConfig = resetsConfig.copy(lastMountingMethod = MountingMethods.MANUAL)) })

			AppLogger.resets.info("${resetType.name} Reset from $resetSourceName")

			// Tell the GUI we ended a reset
			server.sendSolarxrRpc(
				ResetResponse(resetType, ResetStatus.FINISHED, bodyParts, delayMs, delayMs),
			)
		}
	}

	private fun executeTrackerResets(resetType: ResetType, bodyParts: List<BodyPart>? = null, config: ResetsConfig) {
		val allTrackers = server.context.state.value.trackers.values

		// Filter out the trackers that we want to reset
		val trackers = if (!bodyParts.isNullOrEmpty()) {
			allTrackers.filter {
				bodyParts.contains(it.context.state.value.bodyPart)
			}
		} else {
			// Exclude feet trackers from mounting reset if resetMountingFeet = false
			allTrackers.filter {
				resetType != ResetType.MOUNTING ||
					config.resetMountingFeet ||
					it.context.state.value.bodyPart !in FOOT_PARTS
			}
		}

		val referenceRotation = allTrackers
			.map { it.context.state.value }
			.firstOrNull { it.status == TrackerStatus.OK && it.bodyPart == BodyPart.HEAD }
			?.rotation ?: Quaternion.IDENTITY

		// Dispatch the reset action to the trackers
		trackers.forEach {
			it.context.dispatch(
				when (resetType) {
					ResetType.YAW -> TrackerActions.YawReset(referenceRotation)
					ResetType.FULL -> TrackerActions.FullReset(referenceRotation)
					ResetType.MOUNTING -> TrackerActions.MountingReset(referenceRotation, getYawOffset(it.context.state.value.bodyPart, config.armsResetMode))
				},
			)
		}
	}

	private fun getYawOffset(bodyPart: BodyPart?, armsResetMode: ArmsResetMode) = when (bodyPart) {
		// Going forward
		in UPPER_LEG_PARTS -> 0f

		in LOWER_ARM_PARTS if armsResetMode == ArmsResetMode.BACK -> 0f

		in ARM_PARTS if armsResetMode == ArmsResetMode.FORWARD -> 0f

		// Going left
		in LEFT_ARM_PARTS if armsResetMode == ArmsResetMode.T_POSE_UP -> -FastMath.HALF_PI

		in RIGHT_ARM_PARTS if armsResetMode == ArmsResetMode.T_POSE_DOWN -> -FastMath.HALF_PI

		in RIGHT_FINGER_PARTS -> -FastMath.HALF_PI

		// Going right
		in LEFT_ARM_PARTS if armsResetMode == ArmsResetMode.T_POSE_DOWN -> FastMath.HALF_PI

		in RIGHT_ARM_PARTS if armsResetMode == ArmsResetMode.T_POSE_UP -> FastMath.HALF_PI

		in LEFT_FINGER_PARTS -> FastMath.HALF_PI

		// Going back
		else -> FastMath.PI
	}

	companion object {
		fun create(ctx: Phase1ContextProvider, scope: CoroutineScope): ResetsManager {
			val context = Context.create(
				initialState = ResetsState(
					canDoYawReset = false,
					canDoMountingReset = false,
					lastFullResetTime = 0,
				),
				scope = scope,
				behaviours = listOf(ResetsBasicBehaviour(), ResetsMountingTimeoutBehaviour()),
				name = "ResetsManager",
			)
			return ResetsManager(context, ctx.server, ctx.config.settings)
		}
	}
}
