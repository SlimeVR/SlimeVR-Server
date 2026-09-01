package dev.slimevr.skeleton

import dev.slimevr.Phase1ContextProvider
import dev.slimevr.config.Settings
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.skeleton.fkprocessors.FootPlantFkProcessor
import dev.slimevr.skeleton.fkprocessors.LocalizerFkProcessor
import dev.slimevr.skeleton.fkprocessors.ToeSnapFkProcessor
import dev.slimevr.skeleton.inputprocessors.BoneActiveLinkInputProcessor
import dev.slimevr.skeleton.inputprocessors.BoneDirectLinkInputProcessor
import dev.slimevr.skeleton.inputprocessors.BonePredictionInputProcessor
import dev.slimevr.skeleton.inputprocessors.BoneSmoothingInputProcessor
import dev.slimevr.skeleton.inputprocessors.BoneYawFallbackInputProcessor
import dev.slimevr.skeleton.inputprocessors.FingerImputeInputProcessor
import dev.slimevr.skeleton.inputprocessors.HeadPositionFallbackProcessor
import dev.slimevr.skeleton.inputprocessors.HipYawRollAlignInputProcessor
import dev.slimevr.skeleton.inputprocessors.SpineImputeInputProcessor
import dev.slimevr.skeleton.inputprocessors.UpperLegsRollAlignInputProcessor
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.SkeletonBone

data class BoneInput(
	val bodyPart: BodyPart,
	val offset: Vector3,
	val rawRotation: Quaternion,
	val rawPosition: Vector3?,
	val isRotationActive: Boolean,
	val isPositionActive: Boolean,
)

data class BoneState(
	val bodyPart: BodyPart,
	val offset: Vector3,
	val rotation: Quaternion = Quaternion.IDENTITY,
	val headPosition: Vector3 = Vector3.NULL,
	val tailPosition: Vector3 = Vector3.NULL,
	val parentBone: BoneState? = null,
) {
	private val orientationOffset = when {
		offset.len() == 0f -> Quaternion.IDENTITY
		offset.unit().y == 1f -> Quaternion.I
		else -> Quaternion.fromTo(Vector3.NEG_Y, offset)
	}
	val orientation: Quaternion = rotation * orientationOffset
	val localRotation: Quaternion
		get() = parentBone?.let { it.rotation.inv() * rotation } ?: rotation
	val localHeadPosition: Vector3
		get() = parentBone?.let { headPosition - it.tailPosition } ?: headPosition
	val localTailPosition = tailPosition - headPosition
}

typealias InputSkeleton = BodyPartMap<BoneInput>
typealias ComputedSkeleton = BodyPartMap<BoneState>

@JvmName("resolveAverageRotationForInput")
fun InputSkeleton.resolveAverageRotationFor(bodyParts: Array<BodyPart>): Quaternion {
	val rotations = bodyParts.mapNotNull { this[it]?.rawRotation }
	return rotations.reduceIndexedOrNull { index, acc, rotation ->
		acc.lerpQ(rotation, 1f / (index + 1))
	} ?: Quaternion.IDENTITY
}

@JvmName("resolveAverageRotationForComputed")
fun ComputedSkeleton.resolveAverageRotationFor(bodyParts: Array<BodyPart>): Quaternion {
	val rotations = bodyParts.mapNotNull { this[it]?.rotation }
	return rotations.reduceIndexedOrNull { index, acc, rotation ->
		acc.lerpQ(rotation, 1f / (index + 1))
	} ?: Quaternion.IDENTITY
}

data class SkeletonState(
	val boneInputs: InputSkeleton,
	val skeletonHeight: Float,
	val floorLevel: Float,
	val paused: Boolean,
	val pausedBoneInputs: InputSkeleton?,
)

val DEFAULT_SKELETON_STATE: SkeletonState = SkeletonState(
	boneInputs = DEFAULT_PROPORTIONS.toBoneOffsets().mapValues { bodyPart, tailOffset ->
		BoneInput(
			rawRotation = Quaternion.IDENTITY,
			bodyPart = bodyPart,
			offset = tailOffset,
			rawPosition = null,
			isRotationActive = false,
			isPositionActive = false,
		)
	},
	skeletonHeight = DEFAULT_HEIGHT,
	floorLevel = 0f,
	paused = false,
	pausedBoneInputs = null,
)

fun buildBone(bone: BoneInput, parentBone: BoneState?): BoneState {
	// Raw position of the bone input is used for the head bone
	val headPosition = parentBone?.tailPosition ?: bone.rawPosition ?: Vector3.NULL
	return BoneState(
		bodyPart = bone.bodyPart,
		offset = bone.offset,
		headPosition = headPosition,
		rotation = bone.rawRotation,
		tailPosition = headPosition + bone.rawRotation.sandwich(bone.offset),
		parentBone = parentBone,
	)
}

fun buildBones(
	boneInputs: InputSkeleton,
	lastResult: BodyPartMap<BoneState> = bodyPartMap(),
): ComputedSkeleton {
	val highestBone = if (lastResult.isEmpty() || BodyPart.HEAD in boneInputs.keys) {
		BodyPart.HEAD
	} else {
		// TODO this gets the first common parent and builds bones down from it
		//  but it may be better to separate chains and iterate each one separately.
		//  Example: Instead of building from HIP when passing LEFT_FOOT and RIGHT_UPPER_LEG,
		//  build LEFT_FOOT and from RIGHT_UPPER_LEG down.
		boneInputs.keys.findFirstCommonParent()
	}
	val result = BodyPartMap(lastResult)
	iterateBodyPartHierarchy(highestBone, highestBone != BodyPart.HEAD).forEach { (parentPart, childPart) ->
		val rawBone = boneInputs[childPart] ?: return@forEach
		val parentBone = parentPart?.let { result[it] }
		result[childPart] = buildBone(rawBone, parentBone)
	}
	return result
}

sealed interface SkeletonActions {
	data class SetBoneRotation(val bodyPart: BodyPart, val rotation: Quaternion, val setActive: Boolean = true) : SkeletonActions
	data class SetBonePosition(val bodyPart: BodyPart, val position: Vector3?, val setActive: Boolean = true) : SkeletonActions
	data class DisableBone(val bodyPart: BodyPart) : SkeletonActions
	data class SetProportions(val lengths: Map<SkeletonBone, Float>) : SkeletonActions
	data class PauseTracking(val pause: Boolean) : SkeletonActions
	data class SetPausedBoneInputs(val pausedBoneInputs: InputSkeleton) : SkeletonActions
	data object ComputeFloorLevel : SkeletonActions
	data object ResetHeadPosition : SkeletonActions
}

typealias SkeletonContext = Context<SkeletonState, SkeletonActions>
typealias SkeletonBehaviour = Behaviour<Skeleton>
interface SkeletonInputProcessor {
	fun process(inputSkeleton: InputSkeleton, skeletonHeight: Float): InputSkeleton
}
interface SkeletonFkProcessor {
	fun process(inputSkeleton: InputSkeleton, fk: ComputedSkeleton, floorLevel: Float): InputSkeleton
}
typealias IKTargets = BodyPartMap<Vector3>
interface SkeletonTargetProcessor {
	fun process(fk: ComputedSkeleton, ikTargets: IKTargets, floorLevel: Float): IKTargets
}

class Skeleton(
	val context: SkeletonContext,
	val settings: Settings,
	val computed: MutableSharedFlow<ComputedSkeleton>,
) {
	val currentComputed: ComputedSkeleton get() = computed.replayCache.first()

	fun startObserving() = context.observeAll(this)

	companion object {
		const val DEFAULT_HZ = 300

		fun create(scope: CoroutineScope, ctx: Phase1ContextProvider, hz: Int = DEFAULT_HZ): Skeleton {
			val settings = ctx.config.settings
			val behaviours = listOf(
				ProportionsBehaviour(ctx.config.userConfig),
				HeightLogBehaviour(),
				LocalizerResetBehaviour(),
// 				YouSpinMeRightRoundBehaviour(inputHz = 50f),
				ComputedSkeletonBehaviour(
					hz = hz,
					inputProcessors = listOf(
						HeadPositionFallbackProcessor(settings),
						BoneYawFallbackInputProcessor(),
						BoneActiveLinkInputProcessor(),
						SpineImputeInputProcessor(settings),
						HipYawRollAlignInputProcessor(settings),
						UpperLegsRollAlignInputProcessor(settings),
						BoneDirectLinkInputProcessor(),
						FingerImputeInputProcessor(),
						BonePredictionInputProcessor(settings),
						BoneSmoothingInputProcessor(settings),
					),
					fkProcessors = listOf(
						LocalizerFkProcessor(settings),
						FootPlantFkProcessor(settings),
						ToeSnapFkProcessor(settings),
					),
					targetProcessors = listOf(
// 						FloorClipTargetProcessor(settings),
// 						SkatingCorrectionTargetProcessor(settings),
					),
				),
			)

			val context = Context.create(
				initialState = DEFAULT_SKELETON_STATE,
				scope = scope,
				reducer = ::reduce,
				behaviours = behaviours,
				name = "Skeleton",
			)

			val computed = MutableSharedFlow<ComputedSkeleton>(
				replay = 1,
				onBufferOverflow = BufferOverflow.DROP_OLDEST,
			)
			computed.tryEmit(buildBones(context.state.value.boneInputs))

			return Skeleton(context, settings, computed)
		}
	}
}
