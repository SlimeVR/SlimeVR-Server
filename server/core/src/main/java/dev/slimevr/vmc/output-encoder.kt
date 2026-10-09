package dev.slimevr.vmc

import dev.slimevr.config.VMCConfig
import dev.slimevr.osc.OscArg
import dev.slimevr.osc.OscBundle
import dev.slimevr.osc.OscContent
import dev.slimevr.osc.OscMessage
import dev.slimevr.skeleton.ComputedSkeleton
import dev.slimevr.util.inFloatingSeconds
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart
import kotlin.collections.get
import kotlin.time.Duration

private fun averageRotations(rotations: List<Quaternion>) = rotations.reduceIndexedOrNull { index, acc, rotation ->
	acc.interpQ(rotation, 1f / (index + 1))
} ?: Quaternion.IDENTITY

private fun averagePositions(positions: List<Vector3>) = positions.reduceIndexedOrNull { index, acc, position ->
	acc.lerp(position, 1f / (index + 1))
} ?: Vector3.ZERO

internal fun buildOutgoingBundle(
	bones: ComputedSkeleton,
	routedBones: Set<BodyPart>,
	config: VMCConfig,
	vrm: VrmGeometry?,
	skeletonHeight: Float,
	floorLevel: Float,
	elapsed: Duration,
): OscBundle {
	val contents = buildList {
		add(OscContent.Message(OscMessage("/VMC/Ext/T", listOf(OscArg.Float(elapsed.inFloatingSeconds)))))
		add(OscContent.Message(OscMessage("/VMC/Ext/OK", listOf(OscArg.Int(1)))))

		// Send the origin (0, 0, 0) as root
		add(OscContent.Message(transformMessage("/VMC/Ext/Root/Pos", "root", Vector3.ZERO, Quaternion.IDENTITY)))

		// Compute global positions and rotations of every UnityBone
		val unityBoneGlobalRotations: MutableMap<UnityBone, Quaternion> = mutableMapOf()
		val unityBoneGlobalPositions: MutableMap<UnityBone, Vector3> = mutableMapOf()
		for (unityBone in UnityBone.entries) {
			val bodyParts = if ((unityBone == UnityBone.CHEST || unityBone == UnityBone.UPPER_CHEST) && vrm != null && !vrm.hasUpperChest) {
				// If the VRM doesn't have an upper chest, we merge it with the (lower) chest
				UnityBone.CHEST.bodyParts + UnityBone.UPPER_CHEST.bodyParts
			} else {
				unityBone.bodyParts
			}
			val bones = bodyParts.mapNotNull { bones[it] }
			if (bones.isEmpty()) continue
			unityBoneGlobalRotations[unityBone] = averageRotations(bones.map { it.rotation })
			unityBoneGlobalPositions[unityBone] = averagePositions(bones.map { it.headPosition })
		}

		// Send data for each bone
		for (targetUnityBone in UnityBone.entries) {
			if (!targetUnityBone.bodyParts.any { it in routedBones }) continue

			val trackingUnityBone = if (config.mirrorTracking) targetUnityBone.opposite else targetUnityBone
			val targetParentUnityBone = VMC_BONE_PARENTS[targetUnityBone]
			val trackingParentUnityBone = VMC_BONE_PARENTS[trackingUnityBone]

			// Compute local rotation of bone
			val boneRotation = unityBoneGlobalRotations[trackingUnityBone] ?: continue
			val parentRotation = unityBoneGlobalRotations[trackingParentUnityBone]
			val localRotation = vmcLocalRotation(boneRotation, parentRotation, targetUnityBone, targetParentUnityBone, config.mirrorTracking)

			// Compute local position of bone
			val localPosition = if (targetParentUnityBone == null) {
				// Special case for root
				vmcHipPosition(
					unityBoneGlobalRotations,
					unityBoneGlobalPositions,
					bones,
					vrm,
					config,
					skeletonHeight,
					floorLevel,
				)
			} else {
				val bonePosition = unityBoneGlobalPositions[trackingUnityBone] ?: Vector3.ZERO
				val parentPosition = unityBoneGlobalPositions[trackingParentUnityBone] ?: Vector3.ZERO
				emittedLocalPosition(targetUnityBone, targetParentUnityBone, bonePosition, parentRotation ?: Quaternion.IDENTITY, parentPosition, vrm, config.mirrorTracking)
			}

			add(
				OscContent.Message(
					transformMessage(
						"/VMC/Ext/Bone/Pos",
						targetUnityBone.serial,
						localPosition,
						localRotation,
					),
				),
			)
		}
	}

	return OscBundle(1L, contents)
}

internal fun buildInitRequestMessage(): OscMessage = OscMessage("/VMC/Ext/Req", emptyList())

private fun restAdjustedWorld(
	boneRotation: Quaternion,
	restUnityBone: UnityBone?,
	mirror: Boolean,
): Quaternion {
	val world = if (mirror) vmcMirrorRotation(boneRotation) else boneRotation
	val rest = VMC_REST_ROTATIONS[restUnityBone] ?: return world
	return world / rest
}

internal fun vmcLocalRotation(
	boneRotation: Quaternion,
	parentRotation: Quaternion?,
	restUnityBone: UnityBone,
	restParentUnityBone: UnityBone?,
	mirror: Boolean,
): Quaternion {
	val adjusted = restAdjustedWorld(boneRotation, restUnityBone, mirror)
	if (parentRotation == null) return adjusted
	return restAdjustedWorld(parentRotation, restParentUnityBone, mirror).inv() * adjusted
}

internal fun vmcLocalPosition(
	bonePosition: Vector3,
	parentRotation: Quaternion,
	parentPosition: Vector3,
	restParentUnityBone: UnityBone,
	mirror: Boolean,
): Vector3 {
	val parentAdjusted = restAdjustedWorld(parentRotation, restParentUnityBone, mirror)
	val localPosition = bonePosition - parentPosition
	return parentAdjusted.inv().sandwich(if (mirror) vmcMirrorPosition(localPosition) else localPosition)
}

private fun emittedLocalPosition(
	unityBone: UnityBone,
	parentUnityBone: UnityBone,
	bonePosition: Vector3,
	parentRotation: Quaternion,
	parentPosition: Vector3,
	vrm: VrmGeometry?,
	mirror: Boolean,
): Vector3 = if (vrm != null) {
	vrm.bindOffsets[unityBone] ?: Vector3.ZERO
} else {
	vmcLocalPosition(bonePosition, parentRotation, parentPosition, parentUnityBone, mirror)
}

private fun hipToNeckOffset(
	unityBoneGlobalRotations: MutableMap<UnityBone, Quaternion>,
	unityBoneGlobalPositions: MutableMap<UnityBone, Vector3>,
	vrm: VrmGeometry?,
	mirror: Boolean,
): Vector3 {
	var parentUnityBone = UnityBone.HIPS
	var offset = Vector3.ZERO
	for (childUnityBone in VMC_HIP_TO_NECK_CHAIN) {
		val parentRotation = unityBoneGlobalRotations[parentUnityBone] ?: Quaternion.IDENTITY
		val bonePosition = unityBoneGlobalPositions[childUnityBone] ?: Vector3.ZERO
		val parentPosition = unityBoneGlobalPositions[parentUnityBone] ?: Vector3.ZERO

		val parentWorldRotation = restAdjustedWorld(parentRotation, parentUnityBone, mirror)
		val localPosition = emittedLocalPosition(childUnityBone, parentUnityBone, bonePosition, parentRotation, parentPosition, vrm, mirror)
		offset += parentWorldRotation.sandwich(localPosition)

		parentUnityBone = childUnityBone
	}
	return offset
}

// Hip height above the floor at rest, used when anchored with no VRM loaded
private fun restHipHeight(bones: ComputedSkeleton, skeletonHeight: Float): Float = skeletonHeight +
	SPINE_CHAIN_ABOVE_HIP.sumOf { unityBone -> unityBone.bodyParts.sumOf { (bones[it]?.offset?.y ?: 0f).toDouble() } }.toFloat()

private fun vmcHipPosition(
	unityBoneGlobalRotations: MutableMap<UnityBone, Quaternion>,
	unityBoneGlobalPositions: MutableMap<UnityBone, Vector3>,
	bones: ComputedSkeleton,
	vrm: VrmGeometry?,
	config: VMCConfig,
	skeletonHeight: Float,
	floorLevel: Float,
): Vector3 {
	val anchoredHipPosition = vrm?.hipPosition ?: Vector3(0f, restHipHeight(bones, skeletonHeight), 0f)
	if (config.anchorAtHips) return anchoredHipPosition

	val neckPosition = unityBoneGlobalPositions[UnityBone.NECK] ?: return anchoredHipPosition
	val adjustedNeckPosition = if (config.mirrorTracking) vmcMirrorPosition(neckPosition) else neckPosition
	val restHeight = vrm?.outputRestHeight ?: skeletonHeight
	val scale = if (skeletonHeight != 0f) restHeight / skeletonHeight else 1f
	val floorRelativeNeck = (adjustedNeckPosition - Vector3(0f, floorLevel, 0f)) * scale

	val neckOffset = hipToNeckOffset(unityBoneGlobalRotations, unityBoneGlobalPositions, vrm, config.mirrorTracking)

	return floorRelativeNeck - neckOffset
}

private fun transformMessage(address: String, name: String, pos: Vector3, rot: Quaternion): OscMessage = OscMessage(
	address,
	listOf(
		OscArg.String(name),
		OscArg.Float(pos.x),
		OscArg.Float(pos.y),
		OscArg.Float(-pos.z),
		OscArg.Float(rot.x),
		OscArg.Float(rot.y),
		OscArg.Float(-rot.z),
		OscArg.Float(-rot.w),
	),
)
