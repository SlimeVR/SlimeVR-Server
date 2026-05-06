package dev.slimevr.skeleton

import com.jme3.math.FastMath
import dev.slimevr.Phase1ContextProvider
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.SkeletonBone

data class RawBone(
	val bodyPart: BodyPart,
	val length: Float,
	val rawRotation: Quaternion,
	val rawPosition: Vector3,
)

data class BoneState(
	val bodyPart: BodyPart,
	val length: Float,
	val rotation: Quaternion = Quaternion.IDENTITY,
	val headPosition: Vector3 = Vector3.NULL,
	val tailPosition: Vector3 = Vector3.NULL,
	val parentBone: BoneState? = null,
) {
	val localRotation: Quaternion
		get() = parentBone?.let { it.rotation.inv() * rotation } ?: rotation
	val localHeadPosition: Vector3
		get() = parentBone?.let { headPosition - it.tailPosition } ?: headPosition
// 	val localTailPosition: Vector3
// 		get() = tailPosition - headPosition
}

data class SkeletonState(val rawBones: Map<BodyPart, RawBone>, val userHeight: Float)

val DEFAULT_SKELETON_STATE: SkeletonState = run {
	val bones = BONE_TAIL_OFFSETS.entries.associate { (bodyPart, tailOffset) ->
		val restRotation = when (bodyPart) {
			BodyPart.LEFT_FOOT, BodyPart.RIGHT_FOOT -> Quaternion.rotationAroundXAxis(FastMath.HALF_PI)
			else -> Quaternion.IDENTITY
		}
		bodyPart to BoneState(bodyPart = bodyPart, length = tailOffset.len(), rotation = restRotation)
	}
	SkeletonState(rawBones = bones.mapValues { (_, bone) -> RawBone(rawRotation = bone.rotation, bodyPart = bone.bodyPart, length = bone.length, rawPosition = Vector3.NULL) }, userHeight = DEFAULT_HEIGHT)
}

fun buildBones(
	state: SkeletonState,
	rootHead: Vector3 = Vector3.NULL,
	hierarchy: Sequence<Pair<BodyPart?, BodyPart>> = iterateBodyPartHierarchy(),
	tailDirections: Map<BodyPart, Vector3> = BONE_TAIL_DIRECTIONS,
): Map<BodyPart, BoneState> {
	val result = mutableMapOf<BodyPart, BoneState>()
	hierarchy.forEach { (parentPart, childPart) ->
		val rawBone = state.rawBones[childPart] ?: return@forEach
		val tailDirection = tailDirections[childPart] ?: return@forEach
		val parentBone = parentPart?.let { result[it] }
		val head = parentBone?.tailPosition ?: rootHead
		result[childPart] = BoneState(
			bodyPart = rawBone.bodyPart,
			length = rawBone.length,
			headPosition = head,
			rotation = rawBone.rawRotation,
			tailPosition = head + rawBone.rawRotation.sandwich(tailDirection * rawBone.length),
			parentBone = parentBone,
		)
	}
	return result
}

sealed interface SkeletonActions {
	data class SetBoneRotation(val bodyPart: BodyPart, val rotation: Quaternion) : SkeletonActions
	data class SetBonePosition(val bodyPart: BodyPart, val position: Vector3) : SkeletonActions
	data class SetProportions(val lengths: Map<SkeletonBone, Float>) : SkeletonActions
}

typealias SkeletonContext = Context<SkeletonState, SkeletonActions>
typealias SkeletonBehaviour = Behaviour<SkeletonState, SkeletonActions, Skeleton>

class Skeleton(
	val context: SkeletonContext,
	val computed: MutableStateFlow<Map<BodyPart, BoneState>>,
) {
	fun startObserving() = context.observeAll(this)

	companion object {
		fun create(scope: CoroutineScope, ctx: Phase1ContextProvider): Skeleton {
			val behaviours = listOf(
				BoneTransformBehaviour(),
				ProportionsBehaviour(),
				ScaledProportionsBehaviour(ctx.config.userConfig),
				HeightLogBehaviour(),
				YouSpinMeRightRoundBehaviour(inputHz = 10f),
				ComputedSkeletonBehaviour(
					processors = listOf(
// 					PredictionProcessor(predictionAmount = 0.3f),
						SmoothingProcessor(smoothing = 0.3f),
					),
				),
			)

			val context = Context.create(
				initialState = DEFAULT_SKELETON_STATE,
				scope = scope,
				behaviours = behaviours,
				name = "Skeleton",
			)

			return Skeleton(context, MutableStateFlow(buildBones(context.state.value)))
		}
	}
}
