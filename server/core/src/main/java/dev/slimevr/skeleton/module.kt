package dev.slimevr.skeleton

import com.jme3.math.FastMath
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import solarxr_protocol.datatypes.BodyPart
import kotlin.math.cos

data class BoneDefinition(
	val bodyPart: BodyPart,
	val length: Float,
	val localTailOffset: Vector3,
	val parentPart: BodyPart?,
)

val BONE_DEFINITIONS: Map<BodyPart, BoneDefinition> = run {
	val defs = mutableMapOf<BodyPart, BoneDefinition>()
	iterateBodyPartHierarchy().forEach { (parentPart, child) ->
		val length = when (child) {
			BodyPart.HEAD, BodyPart.NECK -> 0.1f

			BodyPart.LEFT_SHOULDER, BodyPart.RIGHT_SHOULDER -> 0.175f

			BodyPart.LEFT_UPPER_ARM, BodyPart.RIGHT_UPPER_ARM,
			BodyPart.LEFT_LOWER_ARM, BodyPart.RIGHT_LOWER_ARM,
			-> 0.26f

			BodyPart.LEFT_HAND, BodyPart.RIGHT_HAND -> 0.13f

			BodyPart.LEFT_THUMB_METACARPAL, BodyPart.LEFT_THUMB_PROXIMAL, BodyPart.LEFT_THUMB_DISTAL,
			BodyPart.LEFT_INDEX_DISTAL, BodyPart.LEFT_INDEX_INTERMEDIATE, BodyPart.LEFT_INDEX_PROXIMAL,
			BodyPart.LEFT_MIDDLE_DISTAL, BodyPart.LEFT_MIDDLE_INTERMEDIATE, BodyPart.LEFT_MIDDLE_PROXIMAL,
			BodyPart.LEFT_RING_DISTAL, BodyPart.LEFT_RING_INTERMEDIATE, BodyPart.LEFT_RING_PROXIMAL,
			BodyPart.LEFT_LITTLE_DISTAL, BodyPart.LEFT_LITTLE_INTERMEDIATE, BodyPart.LEFT_LITTLE_PROXIMAL,

			BodyPart.RIGHT_THUMB_METACARPAL, BodyPart.RIGHT_THUMB_PROXIMAL, BodyPart.RIGHT_THUMB_DISTAL,
			BodyPart.RIGHT_INDEX_PROXIMAL, BodyPart.RIGHT_INDEX_INTERMEDIATE, BodyPart.RIGHT_INDEX_DISTAL,
			BodyPart.RIGHT_MIDDLE_PROXIMAL, BodyPart.RIGHT_MIDDLE_INTERMEDIATE, BodyPart.RIGHT_MIDDLE_DISTAL,
			BodyPart.RIGHT_RING_PROXIMAL, BodyPart.RIGHT_RING_INTERMEDIATE, BodyPart.RIGHT_RING_DISTAL,
			BodyPart.RIGHT_LITTLE_PROXIMAL, BodyPart.RIGHT_LITTLE_INTERMEDIATE, BodyPart.RIGHT_LITTLE_DISTAL,
			-> 0.025f

			BodyPart.UPPER_CHEST, BodyPart.CHEST -> 0.16f

			BodyPart.WAIST -> 0.2f

			BodyPart.HIP -> 0.04f

			BodyPart.LEFT_HIP, BodyPart.RIGHT_HIP -> 0.13f

			BodyPart.LEFT_UPPER_LEG, BodyPart.RIGHT_UPPER_LEG -> 0.42f

			BodyPart.LEFT_LOWER_LEG, BodyPart.RIGHT_LOWER_LEG -> 0.5f

			BodyPart.LEFT_FOOT, BodyPart.RIGHT_FOOT -> 0.05f

			else -> 0.1f
		}
		val restRotation = when (child) {
			BodyPart.LEFT_FOOT, BodyPart.RIGHT_FOOT -> Quaternion.rotationAroundXAxis(FastMath.HALF_PI)
			else -> Quaternion.IDENTITY
		}
		val offset = when (child) {
			BodyPart.HEAD -> Vector3(0f, 0f, length)
			BodyPart.LEFT_HAND, BodyPart.RIGHT_HAND -> Vector3(0f, 0f, -length)
			BodyPart.LEFT_SHOULDER -> Vector3(-length, -0.08f, 0f)
			BodyPart.RIGHT_SHOULDER -> Vector3(length, -0.08f, 0f)
			BodyPart.LEFT_HIP -> Vector3(-length, 0f, 0f)
			BodyPart.RIGHT_HIP -> Vector3(length, 0f, 0f)
			else -> Vector3(0f, -length, 0f)
		}
		defs[child] = BoneDefinition(
			bodyPart = child,
			length = length,
			localTailOffset = restRotation.inv().sandwich(offset),
			parentPart = parentPart,
		)
	}
	defs
}

data class BoneInput(
	val rotation: Quaternion = Quaternion.IDENTITY,
	val headPositionOverride: Vector3? = null,
)

data class BonePose(val head: Vector3, val tail: Vector3)

data class ComputedBone(
	val bodyPart: BodyPart,
	val length: Float,
	val rotation: Quaternion,
	val head: Vector3,
	val tail: Vector3,
	val parentBone: ComputedBone? = null,
) {
	val localRotation: Quaternion
		get() = parentBone?.let { it.rotation.inv() * rotation } ?: rotation
	val localHeadPosition: Vector3
		get() = parentBone?.let { head - it.tail } ?: head
	val localTailPosition: Vector3
		get() = tail - head
}

data class SkeletonState(val inputs: Map<BodyPart, BoneInput>)

val DEFAULT_SKELETON_STATE = SkeletonState(
	inputs = BONE_DEFINITIONS.keys.associateWith { part ->
		BoneInput(
			rotation = when (part) {
				BodyPart.LEFT_FOOT, BodyPart.RIGHT_FOOT -> Quaternion.rotationAroundXAxis(FastMath.HALF_PI)
				else -> Quaternion.IDENTITY
			}
		)
	}
)

fun computePoses(state: SkeletonState, rootHead: Vector3 = Vector3.NULL): Map<BodyPart, BonePose> {
	val poses = mutableMapOf<BodyPart, BonePose>()
	iterateBodyPartHierarchy().forEach { (parentPart, childPart) ->
		val def = BONE_DEFINITIONS[childPart] ?: return@forEach
		val input = state.inputs[childPart] ?: return@forEach
		val fkHead = parentPart?.let { poses[it]?.tail } ?: rootHead
		val head = input.headPositionOverride ?: fkHead
		poses[childPart] = BonePose(head, head + input.rotation.sandwich(def.localTailOffset))
	}
	return poses
}

data class ComputedSkeletonState(val bones: Map<BodyPart, ComputedBone>)

fun buildComputedBones(state: SkeletonState): Map<BodyPart, ComputedBone> {
	val poses = computePoses(state)
	val result = mutableMapOf<BodyPart, ComputedBone>()
	iterateBodyPartHierarchy().forEach { (parentPart, childPart) ->
		val def = BONE_DEFINITIONS[childPart] ?: return@forEach
		val input = state.inputs[childPart] ?: return@forEach
		val pose = poses[childPart] ?: return@forEach
		result[childPart] = ComputedBone(
			bodyPart = def.bodyPart,
			length = def.length,
			rotation = input.rotation,
			head = pose.head,
			tail = pose.tail,
			parentBone = parentPart?.let { result[it] },
		)
	}
	return result
}

sealed interface SkeletonActions {
	data class SetBoneRotation(val bodyPart: BodyPart, val rotation: Quaternion) : SkeletonActions
	data class SetBoneHeadPosition(val bodyPart: BodyPart, val position: Vector3?) : SkeletonActions
}

typealias SkeletonContext = Context<SkeletonState, SkeletonActions>
typealias SkeletonBehaviour = Behaviour<SkeletonState, SkeletonActions, Skeleton>

class YouSpinMeRightRoundBehaviour(val inputHz: Float = 1f) : SkeletonBehaviour {

	override fun reduce(state: SkeletonState, action: SkeletonActions): SkeletonState {
		val inputs = state.inputs.toMutableMap()
		return when (action) {
			is SkeletonActions.SetBoneRotation -> {
				val boneInput = inputs[action.bodyPart] ?: return state
				inputs[action.bodyPart] = boneInput.copy(rotation = action.rotation)
				state.copy(inputs = inputs)
			}
			is SkeletonActions.SetBoneHeadPosition -> {
				val boneInput = inputs[action.bodyPart] ?: return state
				inputs[action.bodyPart] = boneInput.copy(headPositionOverride = action.position)
				state.copy(inputs = inputs)
			}
		}
	}

	override fun observe(receiver: Skeleton) {
		receiver.context.scope.launch {
			val intervalMs = (1000f / inputHz).toLong()
			val startTime = System.currentTimeMillis()
			while (true) {
				delay(intervalMs)
				val elapsed = (System.currentTimeMillis() - startTime) / 1000f
				receiver.context.dispatch(
					SkeletonActions.SetBoneRotation(
						BodyPart.CHEST,
						Quaternion.fromRotationVector(Vector3(cos(elapsed), cos(elapsed), 0f)),
					)
				)
			}
		}
	}
}

interface SkeletonProcessor {
	var enabled: Boolean
	fun process(state: SkeletonState): SkeletonState
}

class SmoothingProcessor(var smoothing: Float) : SkeletonProcessor {
	override var enabled: Boolean = true
	private var smoothedRotations: Map<BodyPart, Quaternion> = emptyMap()

	override fun process(state: SkeletonState): SkeletonState {
		val alpha = 1f - smoothing.coerceAtMost(0.99f)
		smoothedRotations = state.inputs.mapValues { (bodyPart, input) ->
			(smoothedRotations[bodyPart] ?: input.rotation).lerpR(input.rotation, alpha).unit()
		}
		return state.copy(
			inputs = state.inputs.mapValues { (bodyPart, input) ->
				input.copy(rotation = smoothedRotations[bodyPart] ?: input.rotation)
			}
		)
	}
}

class PredictionProcessor(var predictionAmount: Float) : SkeletonProcessor {
	override var enabled: Boolean = true

	private data class BoneVelocity(
		val lastRotation: Quaternion,
		val delta: Quaternion,
	)

	private var velocities: Map<BodyPart, BoneVelocity> = emptyMap()

	override fun process(state: SkeletonState): SkeletonState {
		val newVelocities = mutableMapOf<BodyPart, BoneVelocity>()

		val predicted = state.inputs.mapValues { (bodyPart, input) ->
			val prev = velocities[bodyPart]

			if (prev == null) {
				newVelocities[bodyPart] = BoneVelocity(input.rotation, Quaternion.IDENTITY)
				return@mapValues input
			}

			val velocity = if (input.rotation !== prev.lastRotation) {
				BoneVelocity(input.rotation, input.rotation * prev.lastRotation.inv())
			} else {
				prev
			}

			newVelocities[bodyPart] = velocity

			val scaledDelta = Quaternion.IDENTITY.lerpR(velocity.delta, predictionAmount).unit()
			input.copy(rotation = (scaledDelta * input.rotation).unit())
		}

		velocities = newVelocities
		return state.copy(inputs = predicted)
	}
}

class ComputedSkeletonBehaviour(val processors: List<SkeletonProcessor> = emptyList()) : SkeletonBehaviour {
	override fun observe(receiver: Skeleton) {
		receiver.context.scope.launch {
			while (true) {
				delay(10)
				val targetState = receiver.context.state.value
				val processed = processors
					.filter { it.enabled }
					.fold(targetState) {
						state, processor -> processor.process(state)
					}
				receiver.computed.value = ComputedSkeletonState(buildComputedBones(processed))
			}
		}
	}
}

class Skeleton(
	val context: SkeletonContext,
	val computed: MutableStateFlow<ComputedSkeletonState>,
) {
	companion object {
		fun create(scope: CoroutineScope): Skeleton {
			val behaviours = listOf<SkeletonBehaviour>(
				YouSpinMeRightRoundBehaviour(inputHz = 1f),
				ComputedSkeletonBehaviour(processors = listOf(
//					PredictionProcessor(predictionAmount = 0.3f),
					SmoothingProcessor(smoothing = 1f)
				)),
			)

			val context = Context.create(
				initialState = DEFAULT_SKELETON_STATE,
				scope = scope,
				behaviours = behaviours,
			)

			val skeleton = Skeleton(context, MutableStateFlow(ComputedSkeletonState(buildComputedBones(context.state.value))))
			behaviours.forEach { it.observe(skeleton) }

			return skeleton
		}
	}
}
