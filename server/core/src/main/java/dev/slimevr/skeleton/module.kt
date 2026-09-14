package dev.slimevr.skeleton

import dev.slimevr.Phase1ContextProvider
import dev.slimevr.bones.BoneId
import dev.slimevr.bones.BoneMap
import dev.slimevr.bones.BoneOffsets
import dev.slimevr.bones.BoneRegistry
import dev.slimevr.bones.BoneSet
import dev.slimevr.bones.CompiledSkeleton
import dev.slimevr.bones.mapValues
import dev.slimevr.bones.mutateCopy
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.skeleton.computedprocessors.VelocityComputedProcessor
import dev.slimevr.skeleton.fkprocessors.FootPlantFkProcessor
import dev.slimevr.skeleton.fkprocessors.LocalizerFkProcessor
import dev.slimevr.skeleton.fkprocessors.ToeSnapFkProcessor
import dev.slimevr.skeleton.inputprocessors.BoneDirectLinkInputProcessor
import dev.slimevr.skeleton.inputprocessors.BoneYawFallbackInputProcessor
import dev.slimevr.skeleton.inputprocessors.ConstraintInputProcessor
import dev.slimevr.skeleton.inputprocessors.FingerImputeInputProcessor
import dev.slimevr.skeleton.inputprocessors.HeadPositionFallbackProcessor
import dev.slimevr.skeleton.inputprocessors.HipYawRollAlignInputProcessor
import dev.slimevr.skeleton.inputprocessors.PredictionInputProcessor
import dev.slimevr.skeleton.inputprocessors.SmoothingInputProcessor
import dev.slimevr.skeleton.inputprocessors.SpineInputProcessor
import dev.slimevr.skeleton.inputprocessors.ToeActiveLinkInputProcessor
import dev.slimevr.skeleton.inputprocessors.UpperLegsRollAlignInputProcessor
import dev.slimevr.skeleton.targetprocessors.FloorClipTargetProcessor
import dev.slimevr.skeleton.targetprocessors.SkatingCorrectionTargetProcessor
import dev.slimevr.util.PreciseWaiter
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import solarxr_protocol.rpc.ResetType

data class Velocity(
	/** In meters/s */
	val linear: Vector3,
	/** In radians/s */
	val angular: Vector3,
)

val ZERO_VELOCITY = Velocity(Vector3.ZERO, Vector3.ZERO)

/** Pre-FK */
data class BoneInput(
	val boneId: BoneId,
	val headOffset: Vector3,
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
	val boneId: BoneId,
	val headOffset: Vector3,
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

typealias InputSkeleton = BoneMap<BoneInput>
typealias ComputedSkeleton = BoneMap<BoneState>

data class SkeletonState(
	val boneInputs: InputSkeleton,
	val skeletonHeight: Float,
	val floorLevel: Float,
	val paused: Boolean,
	val pausedProcessedBoneInputs: InputSkeleton?,
)

val DEFAULT_BONE_INPUT = BoneInput(
	boneId = BoneId(0u),
	headOffset = Vector3.ZERO,
	offset = Vector3.ZERO,
	rotation = Quaternion.IDENTITY,
	acceleration = Vector3.ZERO,
	position = null,
	isRotationActive = false,
	isAccelerationActive = false,
	isPositionActive = false,
)

fun defaultSkeletonState(definition: CompiledSkeleton): SkeletonState {
	val defaults = definition.defaultProportionValues()
	val offsets = definition.toBoneOffsets(defaults)
	return SkeletonState(
		boneInputs = offsets.tail.mapValues { boneId, tailOffset ->
			DEFAULT_BONE_INPUT.copy(
				boneId = boneId,
				headOffset = offsets.head[boneId] ?: Vector3.ZERO,
				offset = tailOffset,
			)
		},
		skeletonHeight = definition.height(defaults),
		floorLevel = 0f,
		paused = false,
		pausedProcessedBoneInputs = null,
	)
}

fun buildBone(bone: BoneInput, parentBone: BoneState?, velocity: Velocity = ZERO_VELOCITY): BoneState {
	// Raw position of the bone input is used for a root bone since it has no parent
	val headPosition = parentBone?.let { it.tailPosition + it.rotation.sandwich(bone.headOffset) }
		?: bone.position ?: Vector3.ZERO
	return BoneState(
		parentBone = parentBone,
		boneId = bone.boneId,
		headOffset = bone.headOffset,
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
fun buildBones(
	boneInputs: InputSkeleton,
	changedParts: BoneSet = boneInputs.registry.rootSet,
	lastResult: ComputedSkeleton = BoneMap.of(boneInputs.registry),
): ComputedSkeleton {
	val registry = boneInputs.registry
	return lastResult.mutateCopy { result ->
		for (boneId in registry.highest(changedParts)) {
			val parent = registry.parentOf(boneId)
			registry.hierarchyFrom(parent ?: boneId, onlyChildren = parent != null).forEach { (parentId, childId) ->
				val rawBone = boneInputs[childId] ?: return@forEach
				val parentBone = parentId?.let { result[it] }
				result[childId] = buildBone(rawBone, parentBone, result[childId]?.velocity ?: ZERO_VELOCITY)
			}
		}
	}
}

sealed interface SkeletonActions {
	data class SetBoneRotation(val boneId: BoneId, val rotation: Quaternion, val setActive: Boolean = true) : SkeletonActions
	data class SetBoneAcceleration(val boneId: BoneId, val acceleration: Vector3, val setActive: Boolean = true) : SkeletonActions
	data class SetBonePosition(val boneId: BoneId, val position: Vector3?, val setActive: Boolean = true) : SkeletonActions
	data class DisableBone(val boneId: BoneId) : SkeletonActions
	data class SetProportions(val boneOffsets: BoneOffsets, val skeletonHeight: Float) : SkeletonActions
	data class PauseTracking(val pause: Boolean) : SkeletonActions
	data class SetPausedBoneInputs(val pausedBoneInputs: InputSkeleton) : SkeletonActions
	data object ResetHeadPosition : SkeletonActions
	data object ComputeFloorLevel : SkeletonActions
}

typealias SkeletonContext = Context<SkeletonState, SkeletonActions>
typealias SkeletonBehaviour = Behaviour<Skeleton>

interface ResettableSkeletonProcessor {
	fun reset(resetType: ResetType)
}
interface SkeletonInputProcessor {
	fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float)
}
interface SkeletonFkProcessor {
	fun process(mutableInputSkeleton: InputSkeleton, fk: ComputedSkeleton, floorLevel: Float)
}
interface SkeletonComputedProcessor {
	fun process(mutableComputedSkeleton: ComputedSkeleton)
}
typealias IKTargets = BoneMap<Vector3>
interface SkeletonTargetProcessor {
	fun process(mutableIkTargets: IKTargets, fk: ComputedSkeleton, floorLevel: Float)
}

class Skeleton(
	val context: SkeletonContext,
	val definition: CompiledSkeleton,
	val computed: MutableSharedFlow<ComputedSkeleton>,
	private val resettableSkeletonProcessors: Set<ResettableSkeletonProcessor>,
) {
	val currentComputed: ComputedSkeleton get() = computed.replayCache.first()

	// Reads off boneInputs so registry and state always agree, even if a future registry
	// swap is added later
	val registry: BoneRegistry get() = context.state.value.boneInputs.registry

	fun startObserving() = context.observeAll(this)

	fun resetProcessors(resetType: ResetType) {
		resettableSkeletonProcessors.forEach { it.reset(resetType) }
	}

	companion object {
		const val DEFAULT_HZ = 500

		fun create(scope: CoroutineScope, ctx: Phase1ContextProvider, definition: CompiledSkeleton, waiter: PreciseWaiter, hz: Int = DEFAULT_HZ): Skeleton {
			val settings = ctx.config.settings

			val resettableSkeletonProcessors = mutableSetOf<ResettableSkeletonProcessor>()
			val behaviours = listOf(
				ProportionsBehaviour(ctx.config.userConfig, definition),
				HeightLogBehaviour(),
				LocalizerResetBehaviour(settings),
// 				YouSpinMeRightRoundBehaviour(inputHz = 50f),
				ComputedSkeletonBehaviour(
					hz = hz,
					waiter = waiter,
					inputProcessors = listOf(
						PredictionInputProcessor(settings).also { resettableSkeletonProcessors.add(it) },
						SmoothingInputProcessor(settings).also { resettableSkeletonProcessors.add(it) },
						HeadPositionFallbackProcessor(settings),
						BoneYawFallbackInputProcessor(),
						SpineInputProcessor(settings),
						HipYawRollAlignInputProcessor(settings),
						UpperLegsRollAlignInputProcessor(settings),
						BoneDirectLinkInputProcessor(),
						FingerImputeInputProcessor(),
						ToeActiveLinkInputProcessor(),
						ConstraintInputProcessor(settings),
					),
					fkComputedProcessors = listOf(
						VelocityComputedProcessor().also { resettableSkeletonProcessors.add(it) },
					),
					fkProcessors = listOf(
						LocalizerFkProcessor(settings).also { resettableSkeletonProcessors.add(it) },
						FootPlantFkProcessor(settings),
						ToeSnapFkProcessor(settings),
					),
					targetProcessors = listOf(
						FloorClipTargetProcessor(settings),
						SkatingCorrectionTargetProcessor(settings).also { resettableSkeletonProcessors.add(it) },
					),
					ikComputedProcessors = listOf(
						VelocityComputedProcessor().also { resettableSkeletonProcessors.add(it) },
					),
				),
			)

			val context = Context.create(
				initialState = defaultSkeletonState(definition),
				scope = scope,
				reducer = ::reduce,
				behaviours = behaviours,
				name = "Skeleton",
			)

			// Replay one sample for late subscribers. Non-blocking: a slow driver misses a
			// pose instead of stalling this thread, which has nothing else to do.
			val computed = MutableSharedFlow<ComputedSkeleton>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
			computed.tryEmit(buildBones(context.state.value.boneInputs))

			return Skeleton(context, definition, computed, resettableSkeletonProcessors)
		}
	}
}
