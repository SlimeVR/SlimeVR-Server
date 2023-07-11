package dev.slimevr.osc

import com.jme3.math.FastMath
import dev.slimevr.tracking.processor.TransformNode
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3

class UnityArmature(localRot: Boolean) {

	private val headNode = TransformNode(localRotation = localRot)
	private val neckTailNode = TransformNode(localRotation = localRot)
	private val neckHeadNode = TransformNode(localRotation = localRot)
	private val upperChestNode = TransformNode(localRotation = localRot)
	private val chestNode = TransformNode(localRotation = localRot)
	private val waistTailNode = TransformNode(localRotation = localRot)
	private val waistHeadNode = TransformNode(localRotation = localRot)
	private val hipNode = TransformNode(localRotation = localRot)
	private val leftHipNode = TransformNode(localRotation = localRot)
	private val rightHipNode = TransformNode(localRotation = localRot)
	private val leftKneeNode = TransformNode(localRotation = localRot)
	private val leftAnkleNode = TransformNode(localRotation = localRot)
	private val leftFootNode = TransformNode(localRotation = localRot)
	private val rightKneeNode = TransformNode(localRotation = localRot)
	private val rightAnkleNode = TransformNode(localRotation = localRot)
	private val rightFootNode = TransformNode(localRotation = localRot)
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

	private var rootPosition = Vector3.NULL
	private var rootRotation = Quaternion.IDENTITY

	init {
		// Attach nodes
		// Spine
		hipNode.attachChild(waistHeadNode)
		waistHeadNode.attachChild(waistTailNode)
		waistTailNode.attachChild(chestNode)
		chestNode.attachChild(upperChestNode)
		upperChestNode.attachChild(neckHeadNode)
		neckHeadNode.attachChild(neckTailNode)
		neckTailNode.attachChild(headNode)

		// Legs
		hipNode.attachChild(leftHipNode)
		hipNode.attachChild(rightHipNode)
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
	}

	fun updateNodes() {
		// Set the upper chest node's rotation to the chest's
		upperChestNode.localTransform.rotation = chestNode.localTransform.rotation
		// Update the root node
		hipNode.update()
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

	fun getHeadNodeOfBone(unityBone: UnityBone?): TransformNode? {
		return if (unityBone == null) {
			null
		} else {
			when (unityBone) {
				UnityBone.HEAD -> neckTailNode
				UnityBone.NECK -> neckHeadNode
				UnityBone.UPPER_CHEST -> chestNode
				UnityBone.CHEST -> waistTailNode
				UnityBone.SPINE -> waistHeadNode
				UnityBone.HIPS -> hipNode
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
				else -> null
			}
		}
	}

	companion object {
		private val LEFT_SHOULDER_OFFSET = EulerAngles(EulerOrder.YZX, 0f, 0f, FastMath.HALF_PI).toQuaternion()
		private val RIGHT_SHOULDER_OFFSET = EulerAngles(EulerOrder.YZX, 0f, 0f, -FastMath.HALF_PI).toQuaternion()
	}
}
