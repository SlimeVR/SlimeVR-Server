package dev.slimevr.skeleton

import com.jme3.math.FastMath
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import solarxr_protocol.datatypes.BodyPart
import kotlin.math.cos
import kotlin.math.sin

data class BoneState(
	val bodyPart: BodyPart,
	val length: Float,
	val rotation: Quaternion = Quaternion.IDENTITY,
	val headPosition: Vector3 = Vector3.NULL,
	val tailPosition: Vector3 = Vector3.NULL,
	val parentBone: BodyPart? = null,
) {
	val localTailPosition: Vector3 get() = tailPosition - headPosition

	fun localRotation(bones: Map<BodyPart, BoneState>): Quaternion {
		val parent = parentBone?.let { bones[it] } ?: return rotation
		return parent.rotation.inv() * rotation
	}

	fun localHeadPosition(bones: Map<BodyPart, BoneState>): Vector3 {
		val parent = parentBone?.let { bones[it] } ?: return headPosition
		return headPosition - parent.tailPosition
	}
}

private val BONE_TAIL_OFFSETS: Map<BodyPart, Vector3> = run {
	val offsets = mutableMapOf<BodyPart, Vector3>()
	iterateBodyPartHierarchy().forEach { (_, child) ->
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
		offsets[child] = restRotation.inv().sandwich(offset)
	}
	offsets
}

data class SkeletonState(val bones: Map<BodyPart, BoneState>)

val DEFAULT_SKELETON_STATE: SkeletonState = SkeletonState(
	bones = BONE_TAIL_OFFSETS.entries.associate { (bodyPart, tailOffset) ->
		val restRotation = when (bodyPart) {
			BodyPart.LEFT_FOOT, BodyPart.RIGHT_FOOT -> Quaternion.rotationAroundXAxis(FastMath.HALF_PI)
			else -> Quaternion.IDENTITY
		}
		bodyPart to BoneState(bodyPart = bodyPart, length = tailOffset.len(), rotation = restRotation)
	}
)

fun buildBones(state: SkeletonState, rootHead: Vector3 = Vector3.NULL): Map<BodyPart, BoneState> {
	val result = mutableMapOf<BodyPart, BoneState>()
	iterateBodyPartHierarchy().forEach { (parentPart, childPart) ->
		val bone = state.bones[childPart] ?: return@forEach
		val tailOffset = BONE_TAIL_OFFSETS[childPart] ?: return@forEach
		val parentBone = parentPart?.let { result[it] }
		val head = parentBone?.tailPosition ?: rootHead
		result[childPart] = bone.copy(
			headPosition = head,
			tailPosition = head + bone.rotation.sandwich(tailOffset),
			parentBone = parentPart,
		)
	}
	return result
}

sealed interface SkeletonActions {
	data class SetBoneRotation(val bodyPart: BodyPart, val rotation: Quaternion) : SkeletonActions
}

typealias SkeletonContext = Context<SkeletonState, SkeletonActions>
typealias SkeletonBehaviour = Behaviour<SkeletonState, SkeletonActions, Skeleton>

class YouSpinMeRightRoundBehaviour(val inputHz: Float = 1f) : SkeletonBehaviour {

	override fun reduce(state: SkeletonState, action: SkeletonActions): SkeletonState {
		val bones = state.bones.toMutableMap()
		return when (action) {
			is SkeletonActions.SetBoneRotation -> {
				val bone = bones[action.bodyPart] ?: return state
				bones[action.bodyPart] = bone.copy(rotation = action.rotation)
				state.copy(bones = bones)
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
						Quaternion.fromRotationVector(Vector3(cos(elapsed), sin(elapsed), 0f)),
					)
				)
				receiver.context.dispatch(
					SkeletonActions.SetBoneRotation(
						BodyPart.LEFT_LOWER_LEG,
						Quaternion.fromRotationVector(Vector3(cos(elapsed + 1000), sin(elapsed + 1000), 0f)),
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
		smoothedRotations = state.bones.mapValues { (bodyPart, bone) ->
			(smoothedRotations[bodyPart] ?: bone.rotation).lerpR(bone.rotation, alpha).unit()
		}
		return state.copy(
			bones = state.bones.mapValues { (bodyPart, bone) ->
				bone.copy(rotation = smoothedRotations[bodyPart] ?: bone.rotation)
			}
		)
	}
}

class PredictionProcessor(var predictionAmount: Float) : SkeletonProcessor {
	override var enabled: Boolean = true

	private data class BoneVelocity(val lastRotation: Quaternion, val delta: Quaternion)

	private var velocities: Map<BodyPart, BoneVelocity> = emptyMap()

	override fun process(state: SkeletonState): SkeletonState {
		val newVelocities = mutableMapOf<BodyPart, BoneVelocity>()
		val newBones = state.bones.mapValues { (bodyPart, bone) ->
			val prev = velocities[bodyPart]
			if (prev == null) {
				newVelocities[bodyPart] = BoneVelocity(bone.rotation, Quaternion.IDENTITY)
				return@mapValues bone
			}
			val velocity = if (bone.rotation !== prev.lastRotation) {
				BoneVelocity(bone.rotation, bone.rotation * prev.lastRotation.inv())
			} else {
				prev
			}
			newVelocities[bodyPart] = velocity
			val scaledDelta = Quaternion.IDENTITY.lerpR(velocity.delta, predictionAmount).unit()
			bone.copy(rotation = (scaledDelta * bone.rotation).unit())
		}
		velocities = newVelocities
		return state.copy(bones = newBones)
	}
}

class ComputedSkeletonBehaviour(
	val hz: Float = 100f,
	val processors: List<SkeletonProcessor> = emptyList()
) : SkeletonBehaviour {
	override fun observe(receiver: Skeleton) {
		val intervalMs = (1000f / hz).toLong()
		receiver.context.scope.launch {
			while (true) {
				delay(intervalMs)
				val targetState = receiver.context.state.value
				val processed = processors
					.filter { processor -> processor.enabled }
					.fold(targetState) { state, processor -> processor.process(state) }
				receiver.computed.value = buildBones(processed)
			}
		}
	}
}

class Skeleton(
	val context: SkeletonContext,
	val computed: MutableStateFlow<Map<BodyPart, BoneState>>,
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

			val skeleton = Skeleton(context, MutableStateFlow(buildBones(context.state.value)))
			behaviours.forEach { it.observe(skeleton) }

			return skeleton
		}
	}
}
