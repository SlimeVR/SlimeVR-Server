package dev.slimevr.osc

import com.jme3.math.FastMath
import dev.slimevr.tracking.processor.TransformNode
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3

/**
 * TODO make this class use Bone.kt
 */
class UnityArmature(localRot: Boolean) {

	// Head
	private val headNode = TransformNode(localRotation = localRot)
	private val neckTailNode = TransformNode(localRotation = localRot)
	private val neckHeadNode = TransformNode(localRotation = localRot)

	// Spine
	private val upperChestNode = TransformNode(localRotation = localRot)
	private val chestNode = TransformNode(localRotation = localRot)
	private val spineTailNode = TransformNode(localRotation = localRot)
	private val spineHeadNode = TransformNode(localRotation = localRot)
	private val hipsNode = TransformNode(localRotation = localRot)
	private val leftHipNode = TransformNode(localRotation = localRot)
	private val rightHipNode = TransformNode(localRotation = localRot)

	// Legs
	private val leftKneeNode = TransformNode(localRotation = localRot)
	private val leftAnkleNode = TransformNode(localRotation = localRot)
	private val leftFootNode = TransformNode(localRotation = localRot)
	private val rightKneeNode = TransformNode(localRotation = localRot)
	private val rightAnkleNode = TransformNode(localRotation = localRot)
	private val rightFootNode = TransformNode(localRotation = localRot)

	// Arms
	private val leftShoulderHeadNode = TransformNode(localRotation = localRot)
	private val rightShoulderHeadNode = TransformNode(localRotation = localRot)
	private val leftShoulderTailNode = TransformNode(localRotation = localRot)
	private val rightShoulderTailNode = TransformNode(localRotation = localRot)
	private val leftElbowNode = TransformNode(localRotation = localRot)
	private val rightElbowNode = TransformNode(localRotation = localRot)
	private val leftWristNode = TransformNode(localRotation = localRot)
	private val rightWristNode = TransformNode(localRotation = localRot)
	private val leftHandNode = TransformNode(localRotation = localRot)
	private val rightHandNode = TransformNode(localRotation = localRot)

	// Fingers
	val leftThumbProximalHeadNode = TransformNode(localRotation = localRot)
	val leftThumbProximalTailNode = TransformNode(localRotation = localRot)
	val leftThumbIntermediateNode = TransformNode(localRotation = localRot)
	val leftThumbDistalNode = TransformNode(localRotation = localRot)
	val leftIndexProximalHeadNode = TransformNode(localRotation = localRot)
	val leftIndexProximalTailNode = TransformNode(localRotation = localRot)
	val leftIndexIntermediateNode = TransformNode(localRotation = localRot)
	val leftIndexDistalNode = TransformNode(localRotation = localRot)
	val leftMiddleProximalHeadNode = TransformNode(localRotation = localRot)
	val leftMiddleProximalTailNode = TransformNode(localRotation = localRot)
	val leftMiddleIntermediateNode = TransformNode(localRotation = localRot)
	val leftMiddleDistalNode = TransformNode(localRotation = localRot)
	val leftRingProximalHeadNode = TransformNode(localRotation = localRot)
	val leftRingProximalTailNode = TransformNode(localRotation = localRot)
	val leftRingIntermediateNode = TransformNode(localRotation = localRot)
	val leftRingDistalNode = TransformNode(localRotation = localRot)
	val leftLittleProximalHeadNode = TransformNode(localRotation = localRot)
	val leftLittleProximalTailNode = TransformNode(localRotation = localRot)
	val leftLittleIntermediateNode = TransformNode(localRotation = localRot)
	val leftLittleDistalNode = TransformNode(localRotation = localRot)
	val rightThumbProximalHeadNode = TransformNode(localRotation = localRot)
	val rightThumbProximalTailNode = TransformNode(localRotation = localRot)
	val rightThumbIntermediateNode = TransformNode(localRotation = localRot)
	val rightThumbDistalNode = TransformNode(localRotation = localRot)
	val rightIndexProximalHeadNode = TransformNode(localRotation = localRot)
	val rightIndexProximalTailNode = TransformNode(localRotation = localRot)
	val rightIndexIntermediateNode = TransformNode(localRotation = localRot)
	val rightIndexDistalNode = TransformNode(localRotation = localRot)
	val rightMiddleProximalHeadNode = TransformNode(localRotation = localRot)
	val rightMiddleProximalTailNode = TransformNode(localRotation = localRot)
	val rightMiddleIntermediateNode = TransformNode(localRotation = localRot)
	val rightMiddleDistalNode = TransformNode(localRotation = localRot)
	val rightRingProximalHeadNode = TransformNode(localRotation = localRot)
	val rightRingProximalTailNode = TransformNode(localRotation = localRot)
	val rightRingIntermediateNode = TransformNode(localRotation = localRot)
	val rightRingDistalNode = TransformNode(localRotation = localRot)
	val rightLittleProximalHeadNode = TransformNode(localRotation = localRot)
	val rightLittleProximalTailNode = TransformNode(localRotation = localRot)
	val rightLittleIntermediateNode = TransformNode(localRotation = localRot)
	val rightLittleDistalNode = TransformNode(localRotation = localRot)

	private var rootPosition = Vector3.NULL
	private var rootRotation = Quaternion.IDENTITY

	init {
		// Attach nodes
		// Spine
		hipsNode.attachChild(spineHeadNode)
		spineHeadNode.attachChild(spineTailNode)
		spineTailNode.attachChild(chestNode)
		chestNode.attachChild(upperChestNode)
		upperChestNode.attachChild(neckHeadNode)
		neckHeadNode.attachChild(neckTailNode)
		neckTailNode.attachChild(headNode)

		// Legs
		hipsNode.attachChild(leftHipNode)
		hipsNode.attachChild(rightHipNode)
		leftHipNode.attachChild(leftKneeNode)
		rightHipNode.attachChild(rightKneeNode)
		leftKneeNode.attachChild(leftAnkleNode)
		rightKneeNode.attachChild(rightAnkleNode)
		leftAnkleNode.attachChild(leftFootNode)
		rightAnkleNode.attachChild(rightFootNode)

		// Arms
		upperChestNode.attachChild(leftShoulderHeadNode)
		upperChestNode.attachChild(rightShoulderHeadNode)
		leftShoulderHeadNode.attachChild(leftShoulderTailNode)
		rightShoulderHeadNode.attachChild(rightShoulderTailNode)
		leftShoulderTailNode.attachChild(leftElbowNode)
		rightShoulderTailNode.attachChild(rightElbowNode)
		leftElbowNode.attachChild(leftWristNode)
		rightElbowNode.attachChild(rightWristNode)
		leftWristNode.attachChild(leftHandNode)
		rightWristNode.attachChild(rightHandNode)

		// Fingers
		leftHandNode.attachChild(leftThumbProximalHeadNode)
		leftThumbProximalHeadNode.attachChild(leftThumbProximalTailNode)
		leftThumbProximalTailNode.attachChild(leftThumbIntermediateNode)
		leftThumbIntermediateNode.attachChild(leftThumbDistalNode)
		leftHandNode.attachChild(leftIndexProximalHeadNode)
		leftIndexProximalHeadNode.attachChild(leftIndexProximalTailNode)
		leftIndexProximalTailNode.attachChild(leftIndexIntermediateNode)
		leftIndexIntermediateNode.attachChild(leftIndexDistalNode)
		leftHandNode.attachChild(leftMiddleProximalHeadNode)
		leftMiddleProximalHeadNode.attachChild(leftMiddleProximalTailNode)
		leftMiddleProximalTailNode.attachChild(leftMiddleIntermediateNode)
		leftMiddleIntermediateNode.attachChild(leftMiddleDistalNode)
		leftHandNode.attachChild(leftRingProximalHeadNode)
		leftRingProximalHeadNode.attachChild(leftRingProximalTailNode)
		leftRingProximalTailNode.attachChild(leftRingIntermediateNode)
		leftRingIntermediateNode.attachChild(leftRingDistalNode)
		leftHandNode.attachChild(leftLittleProximalHeadNode)
		leftLittleProximalHeadNode.attachChild(leftLittleProximalTailNode)
		leftLittleProximalTailNode.attachChild(leftLittleIntermediateNode)
		leftLittleIntermediateNode.attachChild(leftLittleDistalNode)
		rightHandNode.attachChild(rightThumbProximalHeadNode)
		rightThumbProximalHeadNode.attachChild(rightThumbProximalTailNode)
		rightThumbProximalTailNode.attachChild(rightThumbIntermediateNode)
		rightThumbIntermediateNode.attachChild(rightThumbDistalNode)
		rightHandNode.attachChild(rightIndexProximalHeadNode)
		rightIndexProximalHeadNode.attachChild(rightIndexProximalTailNode)
		rightIndexProximalTailNode.attachChild(rightIndexIntermediateNode)
		rightIndexIntermediateNode.attachChild(rightIndexDistalNode)
		rightHandNode.attachChild(rightMiddleProximalHeadNode)
		rightMiddleProximalHeadNode.attachChild(rightMiddleProximalTailNode)
		rightMiddleProximalTailNode.attachChild(rightMiddleIntermediateNode)
		rightMiddleIntermediateNode.attachChild(rightMiddleDistalNode)
		rightHandNode.attachChild(rightRingProximalHeadNode)
		rightRingProximalHeadNode.attachChild(rightRingProximalTailNode)
		rightRingProximalTailNode.attachChild(rightRingIntermediateNode)
		rightRingIntermediateNode.attachChild(rightRingDistalNode)
		rightHandNode.attachChild(rightLittleProximalHeadNode)
		rightLittleProximalHeadNode.attachChild(rightLittleProximalTailNode)
		rightLittleProximalTailNode.attachChild(rightLittleIntermediateNode)
		rightLittleIntermediateNode.attachChild(rightLittleDistalNode)
	}

	fun update() {
		// Set the upper chest node's rotation to the chest's
		upperChestNode.localTransform.rotation = chestNode.localTransform.rotation
		// Update the root node
		hipsNode.update()
	}

	fun setRootPose(globalPos: Vector3, globalRot: Quaternion) {
		rootPosition = globalPos
		rootRotation = globalRot
	}

	fun setGlobalRotationForBone(unityBone: UnityBone, globalRot: Quaternion) {
		val node = getHeadNodeOfBone(unityBone)
		if (node != null) {
			node.localTransform.rotation = when (unityBone) {
				UnityBone.LEFT_UPPER_ARM, UnityBone.LEFT_LOWER_ARM, UnityBone.LEFT_HAND -> globalRot * LEFT_SHOULDER_OFFSET
				UnityBone.RIGHT_UPPER_ARM, UnityBone.RIGHT_LOWER_ARM, UnityBone.RIGHT_HAND -> globalRot * RIGHT_SHOULDER_OFFSET
				else -> globalRot
			}
		}
	}

	fun setLocalRotationForBone(unityBone: UnityBone, localRot: Quaternion) {
		val node = getHeadNodeOfBone(unityBone)
		if (node != null) {
			if (unityBone === UnityBone.HIPS) {
				node.worldTransform.rotation = localRot
			} else {
				node.localTransform.rotation = when (unityBone) {
					UnityBone.LEFT_UPPER_ARM -> localRot * RIGHT_SHOULDER_OFFSET
					UnityBone.RIGHT_UPPER_ARM -> localRot * LEFT_SHOULDER_OFFSET
					else -> localRot
				}
			}
		}
	}

	fun getGlobalTranslationForBone(unityBone: UnityBone): Vector3 {
		val node = getHeadNodeOfBone(unityBone)
		return if (node != null) {
			if (unityBone === UnityBone.HIPS) {
				val hipsAverage = (
					leftHipNode.worldTransform.translation +
						rightHipNode.worldTransform.translation
					) * 0.5f
				node.worldTransform.translation * 2f - hipsAverage + rootPosition
			} else {
				node.worldTransform.translation + rootPosition
			}
		} else {
			Vector3.NULL
		}
	}

	fun getLocalTranslationForBone(unityBone: UnityBone): Vector3 {
		val node = getHeadNodeOfBone(unityBone)
		return if (node != null) {
			if (unityBone === UnityBone.HIPS) {
				val hipsAverage = (
					leftHipNode.worldTransform.translation +
						rightHipNode.worldTransform.translation
					) * 0.5f
				node.worldTransform.translation * 2f - hipsAverage + rootPosition
			} else {
				node.localTransform.translation
			}
		} else {
			Vector3.NULL
		}
	}

	fun getGlobalRotationForBone(unityBone: UnityBone?): Quaternion {
		val node = getHeadNodeOfBone(unityBone)
		return if (node != null) {
			node.worldTransform.rotation * rootRotation
		} else {
			Quaternion.IDENTITY
		}
	}

	fun getLocalRotationForBone(unityBone: UnityBone): Quaternion {
		val node = getHeadNodeOfBone(unityBone)
		return if (node != null) {
			if (unityBone === UnityBone.HIPS) {
				node.worldTransform.rotation * rootRotation
			} else {
				node.parent!!.worldTransform.rotation.inv() * node.worldTransform.rotation
			}
		} else {
			Quaternion.IDENTITY
		}
	}

	fun getHeadNodeOfBone(unityBone: UnityBone?): TransformNode? = if (unityBone == null) {
		null
	} else {
		when (unityBone) {
			UnityBone.HEAD -> neckTailNode
			UnityBone.NECK -> neckHeadNode
			UnityBone.UPPER_CHEST -> chestNode
			UnityBone.CHEST -> spineTailNode
			UnityBone.SPINE -> spineHeadNode
			UnityBone.HIPS -> hipsNode
			UnityBone.LEFT_UPPER_LEG -> leftHipNode
			UnityBone.RIGHT_UPPER_LEG -> rightHipNode
			UnityBone.LEFT_LOWER_LEG -> leftKneeNode
			UnityBone.RIGHT_LOWER_LEG -> rightKneeNode
			UnityBone.LEFT_FOOT -> leftAnkleNode
			UnityBone.RIGHT_FOOT -> rightAnkleNode
			UnityBone.LEFT_SHOULDER -> leftShoulderHeadNode
			UnityBone.RIGHT_SHOULDER -> rightShoulderHeadNode
			UnityBone.LEFT_UPPER_ARM -> leftShoulderTailNode
			UnityBone.RIGHT_UPPER_ARM -> rightShoulderTailNode
			UnityBone.LEFT_LOWER_ARM -> leftElbowNode
			UnityBone.RIGHT_LOWER_ARM -> rightElbowNode
			UnityBone.LEFT_HAND -> leftWristNode
			UnityBone.RIGHT_HAND -> rightWristNode
			UnityBone.LEFT_THUMB_PROXIMAL -> leftThumbProximalHeadNode
			UnityBone.LEFT_THUMB_INTERMEDIATE -> leftThumbProximalTailNode
			UnityBone.LEFT_THUMB_DISTAL -> leftThumbIntermediateNode
			UnityBone.LEFT_INDEX_PROXIMAL -> leftIndexProximalHeadNode
			UnityBone.LEFT_INDEX_INTERMEDIATE -> leftIndexProximalTailNode
			UnityBone.LEFT_INDEX_DISTAL -> leftIndexIntermediateNode
			UnityBone.LEFT_MIDDLE_PROXIMAL -> leftMiddleProximalHeadNode
			UnityBone.LEFT_MIDDLE_INTERMEDIATE -> leftMiddleProximalTailNode
			UnityBone.LEFT_MIDDLE_DISTAL -> leftMiddleIntermediateNode
			UnityBone.LEFT_RING_PROXIMAL -> leftRingProximalHeadNode
			UnityBone.LEFT_RING_INTERMEDIATE -> leftRingProximalTailNode
			UnityBone.LEFT_RING_DISTAL -> leftRingIntermediateNode
			UnityBone.LEFT_LITTLE_PROXIMAL -> leftLittleProximalHeadNode
			UnityBone.LEFT_LITTLE_INTERMEDIATE -> leftLittleProximalTailNode
			UnityBone.LEFT_LITTLE_DISTAL -> leftLittleIntermediateNode
			UnityBone.RIGHT_THUMB_PROXIMAL -> rightThumbProximalHeadNode
			UnityBone.RIGHT_THUMB_INTERMEDIATE -> rightThumbProximalTailNode
			UnityBone.RIGHT_THUMB_DISTAL -> rightThumbIntermediateNode
			UnityBone.RIGHT_INDEX_PROXIMAL -> rightIndexProximalHeadNode
			UnityBone.RIGHT_INDEX_INTERMEDIATE -> rightIndexProximalTailNode
			UnityBone.RIGHT_INDEX_DISTAL -> rightIndexIntermediateNode
			UnityBone.RIGHT_MIDDLE_PROXIMAL -> rightMiddleProximalHeadNode
			UnityBone.RIGHT_MIDDLE_INTERMEDIATE -> rightMiddleProximalTailNode
			UnityBone.RIGHT_MIDDLE_DISTAL -> rightMiddleIntermediateNode
			UnityBone.RIGHT_RING_PROXIMAL -> rightRingProximalHeadNode
			UnityBone.RIGHT_RING_INTERMEDIATE -> rightRingProximalTailNode
			UnityBone.RIGHT_RING_DISTAL -> rightRingIntermediateNode
			UnityBone.RIGHT_LITTLE_PROXIMAL -> rightLittleProximalHeadNode
			UnityBone.RIGHT_LITTLE_INTERMEDIATE -> rightLittleProximalTailNode
			UnityBone.RIGHT_LITTLE_DISTAL -> rightLittleIntermediateNode
			else -> null
		}
	}

	companion object {
		private val LEFT_SHOULDER_OFFSET = EulerAngles(EulerOrder.YZX, 0f, 0f, FastMath.HALF_PI).toQuaternion()
		private val RIGHT_SHOULDER_OFFSET = EulerAngles(EulerOrder.YZX, 0f, 0f, -FastMath.HALF_PI).toQuaternion()
	}
}
