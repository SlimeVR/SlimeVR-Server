package dev.slimevr.skeleton

import dev.slimevr.Phase1ContextProvider
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.skeleton.processors.BoneDirectLinkProcessor
import dev.slimevr.skeleton.processors.BoneFallbackProcessor
import dev.slimevr.skeleton.processors.BonePredictionProcessor
import dev.slimevr.skeleton.processors.BoneActiveLinkProcessor
import dev.slimevr.skeleton.processors.BoneSmoothingProcessor
import dev.slimevr.skeleton.processors.BoneYawRollAlignProcessor
import dev.slimevr.skeleton.processors.FingerImputeProcessor
import dev.slimevr.skeleton.processors.SpineImputeProcessor
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.SkeletonBone

data class BoneInput(
	val bodyPart: BodyPart,
	val offset: Vector3,
	val rawRotation: Quaternion,
	val rawPosition: Vector3,
	val isActive: Boolean,
)

data class BoneState(
	val bodyPart: BodyPart,
	val offset: Vector3,
	val rotation: Quaternion = Quaternion.IDENTITY,
	val headPosition: Vector3 = Vector3.NULL,
	val tailPosition: Vector3 = Vector3.NULL,
	val parentBone: BoneState? = null,
) {
	private val orientationOffset = if (offset.len() > 0f) {
		if (offset.unit().y == 1f) {
			Quaternion.I
		} else {
			Quaternion.fromTo(Vector3.NEG_Y, offset)
		}
	} else {
		Quaternion.IDENTITY
	}
	val orientation: Quaternion = rotation * orientationOffset
	val localRotation: Quaternion
		get() = parentBone?.let { it.rotation.inv() * rotation } ?: rotation
	val localHeadPosition: Vector3
		get() = parentBone?.let { headPosition - it.tailPosition } ?: headPosition
	val localTailPosition = tailPosition - headPosition
}

/**
 * Returns the average rotation of the BodyParts.
 */
fun Map<BodyPart, BoneInput>.resolveRotationFor(bodyParts: Set<BodyPart>): Quaternion {
	val rotations = bodyParts.mapNotNull { this[it]?.rawRotation }
	return rotations.reduceIndexedOrNull { index, acc, rotation ->
		acc.lerpQ(rotation, 1f / (index + 1))
	} ?: Quaternion.IDENTITY
}

data class SkeletonState(val boneInputs: Map<BodyPart, BoneInput>, val skeletonHeight: Float, val paused: Boolean)

val DEFAULT_SKELETON_STATE: SkeletonState = run {
	val bones = DEFAULT_BONE_OFFSETS.entries.associate { (bodyPart, tailOffset) ->
		bodyPart to BoneState(bodyPart = bodyPart, offset = tailOffset)
	}
	SkeletonState(
		boneInputs = bones.mapValues { (_, bone) -> BoneInput(rawRotation = bone.rotation, bodyPart = bone.bodyPart, offset = bone.offset, rawPosition = Vector3.NULL, isActive = false) },
		skeletonHeight = DEFAULT_HEIGHT,
		paused = false,
	)
}

fun buildBone(bone: BoneInput, parentBone: BoneState?, originPosition: Vector3 = Vector3.NULL): BoneState {
	val head = parentBone?.tailPosition ?: originPosition
	return BoneState(
		bodyPart = bone.bodyPart,
		offset = bone.offset,
		headPosition = head,
		rotation = bone.rawRotation,
		tailPosition = head + bone.rawRotation.sandwich(bone.offset),
		parentBone = parentBone,
	)
}

fun buildBones(
	state: Map<BodyPart, BoneInput>,
	rootHead: Vector3 = Vector3.NULL,
	hierarchy: Sequence<Pair<BodyPart?, BodyPart>> = iterateBodyPartHierarchy(),
): Map<BodyPart, BoneState> {
	val result = mutableMapOf<BodyPart, BoneState>()
	hierarchy.forEach { (parentPart, childPart) ->
		val rawBone = state[childPart] ?: return@forEach
		val parentBone = parentPart?.let { result[it] }
		result[childPart] = buildBone(rawBone, parentBone, rootHead)
	}
	return result
}

fun buildBones(
	state: SkeletonState,
	rootHead: Vector3 = Vector3.NULL,
	hierarchy: Sequence<Pair<BodyPart?, BodyPart>> = iterateBodyPartHierarchy(),
): Map<BodyPart, BoneState> = buildBones(state.boneInputs, rootHead, hierarchy)

sealed interface SkeletonActions {
	data class SetBoneRotation(val bodyPart: BodyPart, val rotation: Quaternion) : SkeletonActions
	data class SetBonePosition(val bodyPart: BodyPart, val position: Vector3) : SkeletonActions
	data class DisableBone(val bodyPart: BodyPart) : SkeletonActions
	data class SetProportions(val lengths: Map<SkeletonBone, Float>) : SkeletonActions
	data class PauseTracking(val pause: Boolean) : SkeletonActions
}

typealias SkeletonContext = Context<SkeletonState, SkeletonActions>
typealias SkeletonBehaviour = Behaviour<SkeletonState, SkeletonActions, Skeleton>
interface SkeletonProcessor {
	fun process(state: SkeletonState): SkeletonState
}

class Skeleton(
	val context: SkeletonContext,
	val computed: MutableStateFlow<Map<BodyPart, BoneState>>,
) {
	fun startObserving() = context.observeAll(this)

	companion object {
		fun create(scope: CoroutineScope, ctx: Phase1ContextProvider): Skeleton {
			val behaviours = listOf(
				PauseTrackingBehaviour(),
				BoneTransformBehaviour(),
				ProportionsBehaviour(ctx.config.userConfig),
				HeightLogBehaviour(),
				// YouSpinMeRightRoundBehaviour(inputHz = 50f),
				ComputedSkeletonBehaviour(
					processors = listOf(
						BoneFallbackProcessor(),
						BoneActiveLinkProcessor(),
						SpineImputeProcessor(ctx.config.settings),
						BoneYawRollAlignProcessor(ctx.config.settings),
						BoneDirectLinkProcessor(),
						FingerImputeProcessor(),
						BonePredictionProcessor(ctx.config.settings),
						BoneSmoothingProcessor(ctx.config.settings),
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
