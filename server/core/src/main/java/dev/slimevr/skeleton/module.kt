package dev.slimevr.skeleton

import dev.slimevr.Phase1ContextProvider
import dev.slimevr.config.Settings
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.skeleton.computedprocessors.VelocityComputedProcessor
import dev.slimevr.skeleton.fkprocessors.FootPlantFkProcessor
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
import dev.slimevr.util.PreciseWaiter
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.SkeletonBone

data class Velocity(
	/** In meters/s */
	val linear: Vector3,
	/** In radians/s */
	val angular: Vector3,
)

val ZERO_VELOCITY = Velocity(Vector3.NULL, Vector3.NULL)

/** Pre-FK */
data class BoneInput(
	val bodyPart: BodyPart,
	val offset: Vector3,
	val rotation: Quaternion,
	val acceleration: Vector3,
	val position: Vector3?,
	val isRotationActive: Boolean,
	val isAccelerationActive: Boolean,
	val isPositionActive: Boolean,
)

/** Post-FK */
data class BoneState(
	val parentBone: BoneState?,
	val bodyPart: BodyPart,
	val offset: Vector3,
	val rotation: Quaternion,
	val acceleration: Vector3,
	val headPosition: Vector3,
	val tailPosition: Vector3,
	val velocity: Velocity,
) {
	// FK rebuilds most of the skeleton every frame, so anything read less often than that is
	// computed on demand rather than per bone
	private val orientationOffset: Quaternion
		get() = when {
			offset.len() == 0f -> Quaternion.IDENTITY
			offset.unit().y == 1f -> Quaternion.I
			else -> Quaternion.fromTo(Vector3.NEG_Y, offset)
		}
	val orientation: Quaternion
		get() = rotation * orientationOffset

	val localRotation: Quaternion
		get() = parentBone?.let { it.rotation.inv() * rotation } ?: rotation
	val localHeadPosition: Vector3
		get() = parentBone?.let { headPosition - it.tailPosition } ?: headPosition
	val localTailPosition: Vector3
		get() = tailPosition - headPosition
}

typealias InputSkeleton = BodyPartMap<BoneInput>
typealias ComputedSkeleton = BodyPartMap<BoneState>

data class SkeletonState(
	val boneInputs: InputSkeleton,
	val skeletonHeight: Float,
	val floorLevel: Float,
	val paused: Boolean,
	val pausedProcessedBoneInputs: InputSkeleton?,
)

val DEFAULT_BONE_INPUT = BoneInput(
	bodyPart = BodyPart.NONE,
	offset = Vector3.NULL,
	rotation = Quaternion.IDENTITY,
	acceleration = Vector3.NULL,
	position = null,
	isRotationActive = false,
	isAccelerationActive = false,
	isPositionActive = false,
)

val DEFAULT_SKELETON_STATE = SkeletonState(
	boneInputs = DEFAULT_PROPORTIONS.toBoneOffsets().mapValues { bodyPart, tailOffset ->
		DEFAULT_BONE_INPUT.copy(
			bodyPart = bodyPart,
			offset = tailOffset,
		)
	},
	skeletonHeight = DEFAULT_HEIGHT,
	floorLevel = 0f,
	paused = false,
	pausedProcessedBoneInputs = null,
)

fun buildBone(bone: BoneInput, parentBone: BoneState?, velocity: Velocity = ZERO_VELOCITY): BoneState {
	// Raw position of the bone input is used for BodyPart.HEAD since it has no parent
	val headPosition = parentBone?.tailPosition ?: bone.position ?: Vector3.NULL
	return BoneState(
		parentBone = parentBone,
		bodyPart = bone.bodyPart,
		offset = bone.offset,
		rotation = bone.rotation,
		acceleration = bone.acceleration,
		headPosition = headPosition,
		tailPosition = headPosition + bone.rotation.sandwich(bone.offset),
		velocity = velocity,
	)
}

/**
 * Runs FK from boneInputs.
 *
 * If changedParts is used, pass lastResult to fill in the gaps that won't be re-computed.
 */
fun buildBones(boneInputs: InputSkeleton, changedParts: Set<BodyPart> = headPartSet, lastResult: BodyPartMap<BoneState> = bodyPartMap()): ComputedSkeleton {
	return lastResult.mutateCopy { result ->
		for (bodyPart in highestBodyParts(changedParts)) {
			iterateBodyPartHierarchy(parentOf(bodyPart) ?: bodyPart, bodyPart != BodyPart.HEAD).forEach { (parentPart, childPart) ->
				val rawBone = boneInputs[childPart] ?: return@forEach
				val parentBone = parentPart?.let { result[it] }
				// Velocity is written onto the computed bones, not the inputs, so a rebuilt bone
				// keeps what the last pass measured
				result[childPart] = buildBone(rawBone, parentBone, result[childPart]?.velocity ?: ZERO_VELOCITY)
			}
		}
	}
}

sealed interface SkeletonActions {
	data class SetBoneRotation(val bodyPart: BodyPart, val rotation: Quaternion, val setActive: Boolean = true) : SkeletonActions
	data class SetBoneAcceleration(val bodyPart: BodyPart, val acceleration: Vector3, val setActive: Boolean = true) : SkeletonActions
	data class SetBonePosition(val bodyPart: BodyPart, val position: Vector3?, val setActive: Boolean = true) : SkeletonActions
	data class DisableBone(val bodyPart: BodyPart) : SkeletonActions
	data class SetProportions(val lengths: Map<SkeletonBone, Float>) : SkeletonActions
	data class PauseTracking(val pause: Boolean) : SkeletonActions
	data class SetPausedBoneInputs(val pausedBoneInputs: InputSkeleton) : SkeletonActions
	data object ResetHeadPosition : SkeletonActions
	data object ComputeFloorLevel : SkeletonActions
}

typealias SkeletonContext = Context<SkeletonState, SkeletonActions>
typealias SkeletonBehaviour = Behaviour<Skeleton>
interface SkeletonInputProcessor {
	fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float)
}
interface SkeletonFkProcessor {
	fun process(mutableInputSkeleton: InputSkeleton, fk: ComputedSkeleton, floorLevel: Float)
}
interface SkeletonComputedProcessor {
	fun process(mutableComputedSkeleton: ComputedSkeleton, floorLevel: Float)
}
typealias IKTargets = BodyPartMap<Vector3>
interface SkeletonTargetProcessor {
	fun process(mutableIkTargets: IKTargets, fk: ComputedSkeleton, floorLevel: Float)
}

class Skeleton(
	val context: SkeletonContext,
	val settings: Settings,
	val computed: MutableSharedFlow<ComputedSkeleton>,
) {
	val currentComputed: ComputedSkeleton get() = computed.replayCache.first()

	fun startObserving() = context.observeAll(this)

	companion object {
		const val DEFAULT_HZ = 500

		fun create(scope: CoroutineScope, ctx: Phase1ContextProvider, waiter: PreciseWaiter, hz: Int = DEFAULT_HZ): Skeleton {
			val settings = ctx.config.settings
			val behaviours = listOf(
				ProportionsBehaviour(ctx.config.userConfig),
				HeightLogBehaviour(),
				LocalizerResetBehaviour(),
// 				YouSpinMeRightRoundBehaviour(inputHz = 50f),
				ComputedSkeletonBehaviour(
					hz = hz,
					waiter = waiter,
					inputProcessors = listOf(
						BonePredictionInputProcessor(settings),
						BoneSmoothingInputProcessor(settings),
						HeadPositionFallbackProcessor(settings),
						BoneYawFallbackInputProcessor(),
						BoneActiveLinkInputProcessor(),
						SpineImputeInputProcessor(settings),
						HipYawRollAlignInputProcessor(settings),
						UpperLegsRollAlignInputProcessor(settings),
						BoneDirectLinkInputProcessor(),
						FingerImputeInputProcessor(),
					),
					computedProcessors = listOf(
						VelocityComputedProcessor(),
					),
					fkProcessors = listOf(
// 						LocalizerFkProcessor(settings),
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
