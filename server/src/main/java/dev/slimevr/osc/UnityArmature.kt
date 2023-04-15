package dev.slimevr.osc

import com.jme3.math.FastMath
import dev.slimevr.tracking.processor.TransformNode
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3

private val LEFT_SHOULDER_OFFSET = EulerAngles(EulerOrder.YZX, 0f, 0f, FastMath.HALF_PI).toQuaternion()
private val RIGHT_SHOULDER_OFFSET = EulerAngles(EulerOrder.YZX, 0f, 0f, -FastMath.HALF_PI).toQuaternion()
class UnityArmature(localRot: Boolean) {

	private val headNode: TransformNode
	private val neckTailNode: TransformNode
	private val neckHeadNode: TransformNode
	private val upperChestNode: TransformNode
	private val chestNode: TransformNode
	private val waistTailNode: TransformNode
	private val waistHeadNode: TransformNode
	private val hipNode: TransformNode
	private val leftHipNode: TransformNode
	private val rightHipNode: TransformNode
	private val leftKneeNode: TransformNode
	private val leftAnkleNode: TransformNode
	private val leftFootNode: TransformNode
	private val rightKneeNode: TransformNode
	private val rightAnkleNode: TransformNode
	private val rightFootNode: TransformNode
	private val leftShoulderHeadNode: TransformNode
	private val rightShoulderHeadNode: TransformNode
	private val leftShoulderTailNode: TransformNode
	private val rightShoulderTailNode: TransformNode
	private val leftElbowNode: TransformNode
	private val rightElbowNode: TransformNode
	private val leftWristNode: TransformNode
	private val rightWristNode: TransformNode
	private val leftHandNode: TransformNode
	private val rightHandNode: TransformNode

	private var rootPosition = Vector3.NULL
	private var rootRotation = Quaternion.IDENTITY

	init {
		headNode = TransformNode(localRotation = localRot)
		neckTailNode = TransformNode(localRotation = localRot)
		neckHeadNode = TransformNode(localRotation = localRot)
		upperChestNode = TransformNode(localRotation = localRot)
		chestNode = TransformNode(localRotation = localRot)
		waistTailNode = TransformNode(localRotation = localRot)
		waistHeadNode = TransformNode(localRotation = localRot)
		hipNode = TransformNode(localRotation = localRot)
		leftHipNode = TransformNode(localRotation = localRot)
		rightHipNode = TransformNode(localRotation = localRot)
		leftKneeNode = TransformNode(localRotation = localRot)
		leftAnkleNode = TransformNode(localRotation = localRot)
		leftFootNode = TransformNode(localRotation = localRot)
		rightKneeNode = TransformNode(localRotation = localRot)
		rightAnkleNode = TransformNode(localRotation = localRot)
		rightFootNode = TransformNode(localRotation = localRot)
		leftShoulderHeadNode = TransformNode(localRotation = localRot)
		rightShoulderHeadNode = TransformNode(localRotation = localRot)
		leftShoulderTailNode = TransformNode(localRotation = localRot)
		rightShoulderTailNode = TransformNode(localRotation = localRot)
		leftElbowNode = TransformNode(localRotation = localRot)
		rightElbowNode = TransformNode(localRotation = localRot)
		leftWristNode = TransformNode(localRotation = localRot)
		rightWristNode = TransformNode(localRotation = localRot)
		leftHandNode = TransformNode(localRotation = localRot)
		rightHandNode = TransformNode(localRotation = localRot)

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
		hipNode.update()
	}

	fun setRootPose(globalPos: Vector3, globalRot: Quaternion) {
		rootPosition = globalPos
		rootRotation = globalRot
	}

	fun setBoneRotationFromGlobal(unityBone: UnityBone, globalRot: Quaternion) {
		val node = getHeadNodeOfBone(unityBone)
		if (node != null) {
			node.localTransform.rotation = when (unityBone) {
				UnityBone.LEFT_UPPER_ARM, UnityBone.LEFT_LOWER_ARM, UnityBone.LEFT_HAND -> globalRot * LEFT_SHOULDER_OFFSET
				UnityBone.RIGHT_UPPER_ARM, UnityBone.RIGHT_LOWER_ARM, UnityBone.RIGHT_HAND -> globalRot * RIGHT_SHOULDER_OFFSET
				else -> globalRot
			}
		}
	}

	fun setBoneRotationFromLocal(unityBone: UnityBone, localRot: Quaternion) {
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
				val hipsAverage = (leftHipNode.worldTransform.translation +
					rightHipNode.worldTransform.translation) * 0.5f
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
				val hipsAverage = (leftHipNode.worldTransform.translation +
					rightHipNode.worldTransform.translation) * 0.5f
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
}
