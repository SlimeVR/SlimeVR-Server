package dev.slimevr.customosc

import dev.slimevr.config.CustomOscConfig
import dev.slimevr.config.CustomOscProfile
import dev.slimevr.config.OscAxisSource
import dev.slimevr.config.Settings
import dev.slimevr.osc.OscArg
import dev.slimevr.osc.OscMessage
import dev.slimevr.osc.OscSender
import dev.slimevr.skeleton.BoneState
import dev.slimevr.skeleton.ComputedSkeleton
import dev.slimevr.skeleton.Skeleton
import io.github.axisangles.ktmath.EulerOrder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.ConcurrentHashMap

class CustomOscOutputManager(
	private val skeleton: Skeleton,
	private val settings: Settings,
	private val scope: CoroutineScope,
) {
	private val senders = ConcurrentHashMap<String, OscSender>()

	fun startObserving() {
		val configFlow = settings.context.state
			.map { it.data.customOscConfig }
			.distinctUntilChanged()

		combine(skeleton.computed, configFlow, ::Pair)
			.onEach { (computedSkeleton, customOscConfig) ->
				sendCustomOscFrames(computedSkeleton, customOscConfig)
			}
			.launchIn(scope)
	}

	companion object {
		fun create(skeleton: Skeleton, settings: Settings, scope: CoroutineScope): CustomOscOutputManager {
			return CustomOscOutputManager(skeleton = skeleton, settings = settings, scope = scope)
		}
	}

	private suspend fun sendCustomOscFrames(
		computedSkeleton: ComputedSkeleton,
		config: CustomOscConfig,
	) {
		if (!config.enabled) {
			closeAllSenders()
			return
		}

		val activeProfiles = config.profiles.filter { it.enabled }
		val activeProfileIds = activeProfiles.map { it.id }.toSet()

		// Close senders for removed/disabled profiles
		senders.keys.removeIf { profileId ->
			if (!activeProfileIds.contains(profileId)) {
				senders[profileId]?.close()
				true
			} else {
				false
			}
		}

		for (profile in activeProfiles) {
			sendProfileFrame(computedSkeleton, profile)
		}
	}

	private suspend fun sendProfileFrame(
		computedSkeleton: ComputedSkeleton,
		profile: CustomOscProfile,
	) {
		val senderKey = profile.id
		val sender = senders.getOrPut(senderKey) {
			OscSender(profile.address, profile.port)
		}

		for (trackerMapping in profile.trackers) {
			val bodyPart = trackerMapping.bodyPart ?: continue
			val boneState = computedSkeleton[bodyPart] ?: continue

			for (paramMapping in trackerMapping.params) {
				if (paramMapping.address.isBlank()) continue
				val value = extractAxisValue(boneState, paramMapping.axis) ?: continue
				try {
					sender.send(OscMessage(paramMapping.address, listOf(OscArg.Float(value))))
				} catch (_: Exception) {
					// UDP transmit exception ignored or handled gracefully
				}
			}
		}
	}

	private fun extractAxisValue(boneState: BoneState, axis: OscAxisSource): Float? {
		val pos = boneState.headPosition
		val rot = boneState.rotation
		val euler = rot.toEulerAngles(EulerOrder.YXZ)

		return when (axis) {
			OscAxisSource.POSITION_X -> pos.x
			OscAxisSource.POSITION_Y -> pos.y
			OscAxisSource.POSITION_Z -> pos.z
			OscAxisSource.ROTATION_PITCH -> Math.toDegrees(euler.x.toDouble()).toFloat()
			OscAxisSource.ROTATION_YAW -> Math.toDegrees(euler.y.toDouble()).toFloat()
			OscAxisSource.ROTATION_ROLL -> Math.toDegrees(euler.z.toDouble()).toFloat()
			OscAxisSource.QUAT_X -> rot.x
			OscAxisSource.QUAT_Y -> rot.y
			OscAxisSource.QUAT_Z -> rot.z
			OscAxisSource.QUAT_W -> rot.w
		}
	}

	fun closeAllSenders() {
		senders.values.forEach { it.close() }
		senders.clear()
	}
}
