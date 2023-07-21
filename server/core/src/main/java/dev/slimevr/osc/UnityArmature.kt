package dev.slimevr.osc

import com.jme3.math.FastMath
import dev.slimevr.tracking.processor.Bone
import dev.slimevr.tracking.processor.BoneType
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3

class UnityArmature(localRot: Boolean) {

	private val hipsBone = Bone(BoneType.HIP)
	private val leftUpperLegBone = Bone(BoneType.LEFT_UPPER_LEG)
	private val rightUpperLegBone = Bone(BoneType.RIGHT_UPPER_LEG)
	private val leftLowerLegBone = Bone(BoneType.LEFT_LOWER_LEG)
	private val rightLowerLegBone = Bone(BoneType.RIGHT_LOWER_LEG)
	private val leftFootBone = Bone(BoneType.LEFT_FOOT)
	private val rightFootBone = Bone(BoneType.RIGHT_FOOT)
	private val spineBone = Bone(BoneType.WAIST)
	private val chestBone = Bone(BoneType.CHEST)
	private val upperChestBone = Bone(BoneType.CHEST)
	private val neckBone = Bone(BoneType.NECK)
	private val headBone = Bone(BoneType.HEAD)
	private val leftShoulderBone = Bone(BoneType.LEFT_SHOULDER)
	private val rightShoulderBone = Bone(BoneType.RIGHT_SHOULDER)
	private val leftUpperArmBone = Bone(BoneType.LEFT_UPPER_ARM)
	private val rightUpperArmBone = Bone(BoneType.RIGHT_UPPER_ARM)
	private val leftLowerArmBone = Bone(BoneType.LEFT_LOWER_ARM)
	private val rightLowerArmBone = Bone(BoneType.RIGHT_LOWER_ARM)
	private val leftHandBone = Bone(BoneType.LEFT_HAND)
	private val rightHandBone = Bone(BoneType.RIGHT_HAND)

	private var rootPosition = Vector3.NULL
	private var rootRotation = Quaternion.IDENTITY

	init {
		// Attach nodes
		// Spine
		hipsBone.attachChild(spineBone)
		spineBone.attachChild(chestBone)
		chestBone.attachChild(upperChestBone)
		upperChestBone.attachChild(neckBone)
		neckBone.attachChild(headBone)

		// Legs
		hipsBone.attachChild(leftUpperLegBone)
		hipsBone.attachChild(rightUpperLegBone)
		leftUpperLegBone.attachChild(leftLowerLegBone)
		rightUpperLegBone.attachChild(rightLowerLegBone)
		leftLowerLegBone.attachChild(leftFootBone)
		rightLowerLegBone.attachChild(rightFootBone)

		// Arms
		upperChestBone.attachChild(leftShoulderBone)
		upperChestBone.attachChild(rightShoulderBone)
		leftShoulderBone.attachChild(leftUpperArmBone)
		rightShoulderBone.attachChild(rightUpperArmBone)
		leftUpperArmBone.attachChild(leftLowerArmBone)
		rightUpperArmBone.attachChild(rightLowerArmBone)
		leftLowerArmBone.attachChild(leftHandBone)
		rightLowerArmBone.attachChild(rightHandBone)
	}

	fun updateNodes() {
		// Update the root node
		hipsBone.update()
	}

	fun setRootPose(globalPos: Vector3, globalRot: Quaternion) {
		rootPosition = globalPos
		rootRotation = globalRot
	}

	fun setGlobalRotationForBone(unityBone: UnityBone, globalRot: Quaternion) {
		val bone = getBone(unityBone)
		if (bone != null) {
			val rot = when (unityBone) {
				UnityBone.LEFT_UPPER_ARM, UnityBone.LEFT_LOWER_ARM, UnityBone.LEFT_HAND -> globalRot * LEFT_SHOULDER_OFFSET
				UnityBone.RIGHT_UPPER_ARM, UnityBone.RIGHT_LOWER_ARM, UnityBone.RIGHT_HAND -> globalRot * RIGHT_SHOULDER_OFFSET
				else -> globalRot
			}
			bone.setRotation(rot)
		}
	}

	fun setLocalRotationForBone(unityBone: UnityBone, localRot: Quaternion) {
		val bone = getBone(unityBone)
		if (bone != null) {
			if (unityBone === UnityBone.HIPS) {
				bone.setRotation(localRot)
			} else {
				val rot = when (unityBone) {
					UnityBone.LEFT_UPPER_ARM -> localRot * RIGHT_SHOULDER_OFFSET
					UnityBone.RIGHT_UPPER_ARM -> localRot * LEFT_SHOULDER_OFFSET
					else -> localRot
				}
				bone.setRotation(rot)
			}
		}
	}

	fun getGlobalTranslationForBone(unityBone: UnityBone): Vector3 {
		val bone = getBone(unityBone)
		return if (bone != null) {
			if (unityBone === UnityBone.HIPS) {
				val hipsAverage = (
					leftUpperLegBone.getPosition() +
						rightUpperLegBone.getPosition()
					) * 0.5f
				bone.getPosition() * 2f - hipsAverage + rootPosition
			} else {
				bone.getPosition() + rootPosition
			}
		} else {
			Vector3.NULL
		}
	}

	fun getLocalTranslationForBone(unityBone: UnityBone): Vector3 {
		val bone = getBone(unityBone)
		return if (bone != null) {
			if (unityBone === UnityBone.HIPS) {
				val hipsAverage = (
					leftUpperLegBone.getPosition() +
						rightUpperLegBone.getPosition()
					) * 0.5f
				bone.getPosition() * 2f - hipsAverage + rootPosition
			} else {
				bone.rotationOffset.toRotationVector() * bone.length
			}
		} else {
			Vector3.NULL
		}
	}

	fun getGlobalRotationForBone(unityBone: UnityBone?): Quaternion {
		val bone = getBone(unityBone)
		return if (bone != null) {
			bone.getGlobalRotation() * rootRotation
		} else {
			Quaternion.IDENTITY
		}
	}

	fun getLocalRotationForBone(unityBone: UnityBone): Quaternion {
		val bone = getBone(unityBone)
		return if (bone != null) {
			if (unityBone === UnityBone.HIPS) {
				bone.getGlobalRotation() * rootRotation
			} else {
				(bone.parent?.getGlobalRotation()?.inv() ?: Quaternion.IDENTITY) * bone.getGlobalRotation()
			}
		} else {
			Quaternion.IDENTITY
		}
	}

	fun getBone(unityBone: UnityBone?): Bone? {
		return if (unityBone == null) {
			null
		} else {
			when (unityBone) {
				UnityBone.HIPS -> hipsBone
				UnityBone.LEFT_UPPER_LEG -> leftUpperLegBone
				UnityBone.RIGHT_UPPER_LEG -> rightUpperLegBone
				UnityBone.LEFT_LOWER_LEG -> leftLowerLegBone
				UnityBone.RIGHT_LOWER_LEG -> rightLowerLegBone
				UnityBone.LEFT_FOOT -> leftFootBone
				UnityBone.RIGHT_FOOT -> rightFootBone
				UnityBone.SPINE -> spineBone
				UnityBone.CHEST -> chestBone
				UnityBone.UPPER_CHEST -> upperChestBone
				UnityBone.NECK -> neckBone
				UnityBone.HEAD -> headBone
				UnityBone.LEFT_SHOULDER -> leftShoulderBone
				UnityBone.RIGHT_SHOULDER -> rightShoulderBone
				UnityBone.LEFT_UPPER_ARM -> leftUpperArmBone
				UnityBone.RIGHT_UPPER_ARM -> rightUpperArmBone
				UnityBone.LEFT_LOWER_ARM -> leftLowerArmBone
				UnityBone.RIGHT_LOWER_ARM -> rightLowerArmBone
				UnityBone.LEFT_HAND -> leftHandBone
				UnityBone.RIGHT_HAND -> rightHandBone
				else -> null
			}
		}
	}

	companion object {
		private val LEFT_SHOULDER_OFFSET = EulerAngles(EulerOrder.YZX, 0f, 0f, FastMath.HALF_PI).toQuaternion()
		private val RIGHT_SHOULDER_OFFSET = EulerAngles(EulerOrder.YZX, 0f, 0f, -FastMath.HALF_PI).toQuaternion()
	}
}
