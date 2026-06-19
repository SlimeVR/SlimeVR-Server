package dev.slimevr.resets

import dev.slimevr.AppLogger
import dev.slimevr.Phase1ContextProvider
import dev.slimevr.VRServer
import dev.slimevr.config.ResetsConfig
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.util.safeLaunch
import io.github.axisangles.ktmath.Quaternion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.rpc.ResetResponse
import solarxr_protocol.rpc.ResetStatus
import solarxr_protocol.rpc.ResetType
import kotlin.collections.listOf

data class ResetsState(
	val config: ResetsConfig = ResetsConfig(),
	val canDoYawReset: Boolean,
	val canDoMountingReset: Boolean,
	val lastFullResetTime: Long,
)

sealed interface ResetsActions {
	data class UpdateConfig(val config: ResetsConfig) : ResetsActions
	data class ClearResets(val resetType: List<ResetType>) : ResetsActions
	data class EndReset(val resetType: ResetType) : ResetsActions
}

typealias ResetsContext = Context<ResetsState, ResetsActions>
typealias ResetsBehaviour = Behaviour<ResetsState, ResetsActions, ResetsManager>

class ResetsManager(val context: ResetsContext, val server: VRServer) {
	fun startObserving() = context.observeAll(this)

	private var resetJob: Job = Job()

	/**
	 * Schedules a reset according to the resetType.
	 * resetSourceName is used for logging
	 * If delay is null, the default delay from config will be used.
	 * If bodyParts is null, resets all trackers.
	 */
	suspend fun scheduleReset(resetSourceName: String, resetType: ResetType, delay: Float? = null, bodyParts: List<BodyPart>? = null) {
		resetJob.cancelAndJoin()
		resetJob = context.scope.safeLaunch {
			val config = context.state.value.config
			val delayMs = if (delay != null) {
				(delay * 1000).toInt() // Use provided delay
			} else {
				when (resetType) { // Fall back to config
					ResetType.Yaw -> (config.yawResetDelay * 1000).toInt()

					ResetType.Full -> (config.fullResetDelay * 1000).toInt()

					ResetType.Mounting -> (config.mountingResetDelay * 1000).toInt()
				}
			}
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

			executeTrackerResets(resetType, bodyParts, config)

			context.dispatch(ResetsActions.EndReset(resetType))

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
				resetType != ResetType.Mounting ||
					config.resetMountingFeet ||
					it.context.state.value.bodyPart !in setOf(BodyPart.LEFT_FOOT, BodyPart.RIGHT_FOOT)
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
					ResetType.Yaw -> TrackerActions.YawReset(referenceRotation)
					ResetType.Full -> TrackerActions.FullReset(referenceRotation)
					ResetType.Mounting -> TrackerActions.MountingReset(referenceRotation)
				},
			)
		}
	}

	companion object {
		fun create(server: VRServer, scope: CoroutineScope): ResetsManager {
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
			return ResetsManager(context, server)
		}
	}
}
