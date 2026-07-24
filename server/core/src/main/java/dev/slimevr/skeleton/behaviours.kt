package dev.slimevr.skeleton

import dev.slimevr.config.UserConfig
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import solarxr_protocol.datatypes.BodyPart
import kotlin.math.cos
import kotlin.math.sin

class BoneTransformBehaviour : SkeletonBehaviour {
	override fun reduce(state: SkeletonState, action: SkeletonActions): SkeletonState = when (action) {
		is SkeletonActions.SetBoneRotation -> {
			val bones = state.boneInputs.toMutableMap()
			val bone = bones[action.bodyPart] ?: return state
			bones[action.bodyPart] = bone.copy(rawRotation = action.rotation, isActive = true)
			state.copy(boneInputs = bones)
		}

		is SkeletonActions.SetBonePosition -> {
			val bones = state.boneInputs.toMutableMap()
			val bone = bones[action.bodyPart] ?: return state
			bones[action.bodyPart] = bone.copy(rawPosition = action.position, isActive = true)
			state.copy(boneInputs = bones)
		}

		is SkeletonActions.DisableBone -> {
			val bones = state.boneInputs.toMutableMap()
			val bone = bones[action.bodyPart] ?: return state
			bones[action.bodyPart] = bone.copy(isActive = false)
			state.copy(boneInputs = bones)
		}

		else -> state
	}
}

class ProportionsBehaviour(private val userConfig: UserConfig) : SkeletonBehaviour {
	override fun reduce(state: SkeletonState, action: SkeletonActions): SkeletonState = when (action) {
		is SkeletonActions.SetProportions -> {
			val bones = action.lengths.toBoneOffsets()
			val newBones = state.boneInputs.mapValues { (bodyPart, bone) ->
				bone.copy(offset = bones[bodyPart] ?: bone.offset)
			}
			state.copy(boneInputs = newBones, skeletonHeight = action.lengths.height())
		}

		else -> state
	}

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
		receiver.context.scope.launch {
			receiver.context.state
				.map { state -> state.skeletonHeight }
				.distinctUntilChanged()
				.collect { height -> println("User height changed: ${"%.2f".format(height)}m") }
		}
	}
}

class YouSpinMeRightRoundBehaviour(val inputHz: Float = 1f) : SkeletonBehaviour {
	override fun observe(receiver: Skeleton) {
		receiver.context.scope.launch {
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
						Vector3(circleX, state.skeletonHeight + jumpHeight, circleZ),
					),
				)
			}
		}
	}
}

class PauseTrackingBehaviour : SkeletonBehaviour {
	override fun reduce(state: SkeletonState, action: SkeletonActions): SkeletonState = when (action) {
		is SkeletonActions.PauseTracking -> state.copy(paused = action.pause)
		else -> state
	}
}

class ComputedSkeletonBehaviour(
	val hz: Float = 100f, // TODO behaviours like smoothing will behave different based on hz
	val processors: List<SkeletonProcessor> = emptyList(),
) : SkeletonBehaviour {
	override fun observe(receiver: Skeleton) {
		val intervalMs = (1000f / hz).toLong()
		receiver.context.scope.launch {
			while (true) {
				try {
					delay(intervalMs)
					val targetState = receiver.context.state.value
					val processed = processors
						.fold(targetState) { state, processor -> processor.process(state) } // TODO: Add a constrain processor (maybe not needed)

					// Get head position
					val rootHead = processed.boneInputs[BodyPart.HEAD]
						?.let { Vector3(it.rawPosition.x, it.rawPosition.y, it.rawPosition.z) }
						?: Vector3(0f, targetState.skeletonHeight, 0f)

					val fk = buildBones(processed, rootHead = rootHead)

// 					val targetProcessors = [FloorClip, FloorSkating, ToePlant, FootPlant]
//
// 					val targets = targetProcessors
// 						.filter { targetProcessors -> targetProcessors.enabled }
// 						.fold(emptyList<Target>()) { targets, processor -> processor.process(fk, targets) }
//
// 					val ikOutput = solver.solve(fk, targets)

// 					receiver.computed.value = ikOutput

					if (!targetState.paused) {
						receiver.computed.value = fk
					}
				} catch (e: CancellationException) {
					throw e
				} catch (e: Exception) {
					dev.slimevr.AppLogger.coroutines.error(e, "Error in ComputedSkeletonBehaviour")
				}
			}
		}
	}
}
