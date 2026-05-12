package dev.slimevr.skeleton

import dev.slimevr.config.UserConfig
import dev.slimevr.util.safeLaunch
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.datatypes.BodyPart
import kotlin.math.cos
import kotlin.math.sin

class BoneTransformBehaviour : SkeletonBehaviour {
	override fun reduce(state: SkeletonState, action: SkeletonActions): SkeletonState = when (action) {
		is SkeletonActions.SetBoneRotation -> {
			val bones = state.rawBones.toMutableMap()
			val bone = bones[action.bodyPart] ?: return state
			bones[action.bodyPart] = bone.copy(rawRotation = action.rotation)
			state.copy(rawBones = bones)
		}

		is SkeletonActions.SetBonePosition -> {
			val bones = state.rawBones.toMutableMap()
			val bone = bones[action.bodyPart] ?: return state
			bones[action.bodyPart] = bone.copy(rawPosition = action.position)
			state.copy(rawBones = bones)
		}

		else -> state
	}
}

class ProportionsBehaviour : SkeletonBehaviour {
	override fun reduce(state: SkeletonState, action: SkeletonActions): SkeletonState = when (action) {
		is SkeletonActions.SetProportions -> {
			val bones = action.lengths.toBoneOffsets()
			val newBones = state.rawBones.mapValues { (bodyPart, bone) ->
				bone.copy(offset = bones[bodyPart] ?: bone.offset)
			}
			state.copy(rawBones = newBones, userHeight = action.lengths.height())
		}

		else -> state
	}
}

class ScaledProportionsBehaviour(private val userConfig: UserConfig) : SkeletonBehaviour {
	override fun observe(receiver: Skeleton) {
		userConfig.context.state
			.map { state -> state.data.proportions }
			.distinctUntilChanged()
			.onEach { proportions ->
				if (proportions.isNotEmpty()) {
					receiver.context.dispatch(SkeletonActions.SetProportions(configToBoneValues(proportions)))
				}
			}
			.launchIn(receiver.context.scope)
	}
}

class HeightLogBehaviour : SkeletonBehaviour {
	override fun observe(receiver: Skeleton) {
		receiver.context.scope.safeLaunch {
			receiver.context.state
				.map { state -> state.userHeight }
				.distinctUntilChanged()
				.collect { height -> println("User height changed: ${"%.2f".format(height)}m") }
		}
	}
}

class YouSpinMeRightRoundBehaviour(val inputHz: Float = 1f) : SkeletonBehaviour {
	override fun observe(receiver: Skeleton) {
		receiver.context.scope.safeLaunch {
			val intervalMs = (1000f / inputHz).toLong()
			val startTime = System.currentTimeMillis()
			while (true) {
				delay(intervalMs)
				val elapsed = (System.currentTimeMillis() - startTime) / 1000f
				val state = receiver.context.state.value

				receiver.context.dispatch(
					SkeletonActions.SetBoneRotation(
						BodyPart.CHEST,
						Quaternion.fromRotationVector(Vector3(cos(elapsed), sin(elapsed), 0f)),
					),
				)
				receiver.context.dispatch(
					SkeletonActions.SetBoneRotation(
						BodyPart.LEFT_LOWER_LEG,
						Quaternion.fromRotationVector(Vector3(cos(elapsed + 1000), sin(elapsed + 1000), 0f)),
					),
				)

				val circleRadius = 0.5f
				val circleX = cos(elapsed * 2f) * circleRadius
				val circleZ = sin(elapsed * 2f) * circleRadius
				val jumpHeight = maxOf(0f, sin(elapsed * 3f) * 0.3f)
				receiver.context.dispatch(
					SkeletonActions.SetBonePosition(
						BodyPart.HEAD,
						Vector3(circleX, state.userHeight + jumpHeight, circleZ),
					),
				)
			}
		}
	}
}

class ComputedSkeletonBehaviour(
	val hz: Float = 100f,
	val processors: List<SkeletonProcessor> = emptyList(),
) : SkeletonBehaviour {
	override fun observe(receiver: Skeleton) {
		val intervalMs = (1000f / hz).toLong()
		receiver.context.scope.safeLaunch {
			while (true) {
				try {
					delay(intervalMs)
					val targetState = receiver.context.state.value
					val processed = processors
						.filter { processor -> processor.enabled }
						.fold(targetState) { state, processor -> processor.process(state) }
					val rootHead = Vector3(0f, targetState.userHeight, 0f) // FIXME WRONG
					receiver.computed.value = buildBones(processed, rootHead = rootHead)
				} catch (e: Exception) {
					dev.slimevr.AppLogger.coroutines.error(e, "Error in ComputedSkeletonBehaviour")
				}
			}
		}
	}
}
