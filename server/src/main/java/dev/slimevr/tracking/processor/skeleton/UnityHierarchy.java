package dev.slimevr.tracking.processor.skeleton;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.slimevr.tracking.processor.BoneType;
import dev.slimevr.tracking.processor.TransformNode;


public class UnityHierarchy {
	// @formatter:off
	protected final TransformNode headNode;
	protected final TransformNode neckNode;
	protected final TransformNode chestNode;
	protected final TransformNode waistTailNode;
	protected final TransformNode waistHeadNode;
	protected final TransformNode hipNode;
	protected final TransformNode leftHipNode;
	protected final TransformNode rightHipNode ;
	protected final TransformNode leftKneeNode ;
	protected final TransformNode leftAnkleNode;
	protected final TransformNode leftFootNode;
	protected final TransformNode rightKneeNode ;
	protected final TransformNode rightAnkleNode;
	protected final TransformNode rightFootNode ;
	protected final TransformNode leftShoulderHeadNode ;
	protected final TransformNode rightShoulderHeadNode;
	protected final TransformNode leftShoulderTailNode ;
	protected final TransformNode rightShoulderTailNode;
	protected final TransformNode leftElbowNode;
	protected final TransformNode rightElbowNode;
	protected final TransformNode leftWristNode ;
	protected final TransformNode rightWristNode;
	protected final TransformNode leftHandNode;
	protected final TransformNode rightHandNode ;
	private static final Quaternion LEFT_SHOULDER_OFFSET = new Quaternion()
		.fromAngles(0f, 0f, FastMath.HALF_PI);
	private static final Quaternion RIGHT_SHOULDER_OFFSET = new Quaternion()
		.fromAngles(0f, 0f, -FastMath.HALF_PI);
	protected final Vector3f rootPosition = new Vector3f();
	protected final Quaternion rootRotation = new Quaternion();

	public UnityHierarchy(boolean local) {
		// Create nodes
		headNode = new TransformNode(BoneType.HEAD, local);
		neckNode = new TransformNode(BoneType.NECK, local);
		chestNode = new TransformNode(BoneType.CHEST, local);
		waistTailNode = new TransformNode(BoneType.WAIST, local);
		waistHeadNode = new TransformNode(BoneType.WAIST, local);
		hipNode = new TransformNode(BoneType.HIP, local);
		leftHipNode = new TransformNode(BoneType.LEFT_HIP, local);
		rightHipNode = new TransformNode(BoneType.RIGHT_HIP, local);
		leftKneeNode = new TransformNode(BoneType.LEFT_UPPER_LEG, local);
		leftAnkleNode = new TransformNode(BoneType.LEFT_LOWER_LEG, local);
		leftFootNode = new TransformNode(BoneType.LEFT_FOOT, local);
		rightKneeNode = new TransformNode(BoneType.RIGHT_UPPER_LEG, local);
		rightAnkleNode = new TransformNode(BoneType.RIGHT_LOWER_LEG, local);
		rightFootNode = new TransformNode(BoneType.RIGHT_FOOT, local);
		leftShoulderHeadNode = new TransformNode(BoneType.LEFT_SHOULDER, local);
		rightShoulderHeadNode = new TransformNode(BoneType.RIGHT_SHOULDER, local);
		leftShoulderTailNode = new TransformNode(BoneType.LEFT_UPPER_ARM, local);
		rightShoulderTailNode = new TransformNode(BoneType.RIGHT_UPPER_ARM, local);
		leftElbowNode = new TransformNode(BoneType.LEFT_LOWER_ARM, local);
		rightElbowNode = new TransformNode(BoneType.RIGHT_LOWER_ARM, local);
		leftWristNode = new TransformNode(BoneType.LEFT_HAND, local);
		rightWristNode = new TransformNode(BoneType.RIGHT_HAND, local);
		leftHandNode = new TransformNode(BoneType.LEFT_HAND, local);
		rightHandNode = new TransformNode(BoneType.RIGHT_HAND, local);

		// Attach nodes
		// Spine
		hipNode.attachChild(waistHeadNode);
		waistHeadNode.attachChild(waistTailNode);
		waistTailNode.attachChild(chestNode);
		chestNode.attachChild(neckNode);
		neckNode.attachChild(headNode);

		// Legs
		hipNode.attachChild(leftHipNode);
		hipNode.attachChild(rightHipNode);
		leftHipNode.attachChild(leftKneeNode);
		rightHipNode.attachChild(rightKneeNode);
		leftKneeNode.attachChild(leftAnkleNode);
		rightKneeNode.attachChild(rightAnkleNode);
		leftAnkleNode.attachChild(leftFootNode);
		rightAnkleNode.attachChild(rightFootNode);

		// Arms
		chestNode.attachChild(leftShoulderHeadNode);
		chestNode.attachChild(rightShoulderHeadNode);
		leftShoulderHeadNode.attachChild(leftShoulderTailNode);
		rightShoulderHeadNode.attachChild(rightShoulderTailNode);
		leftShoulderTailNode.attachChild(leftElbowNode);
		rightShoulderTailNode.attachChild(rightElbowNode);
		leftElbowNode.attachChild(leftWristNode);
		rightElbowNode.attachChild(rightWristNode);
		leftWristNode.attachChild(leftHandNode);
		rightWristNode.attachChild(rightHandNode);
	}

	public void updateNodes() {
		hipNode.update();
	}

	public void setRootPose(Vector3f globalPos, Quaternion globalRot) {
		rootPosition.set(globalPos);
		rootRotation.set(globalRot);
	}

	public void setBoneGlobalRotation(BoneType boneType, Quaternion globalRot) {
		TransformNode node = getHeadNodeForBone(boneType);
		if (node != null)
			node.localTransform.setRotation(globalRot);
	}

	public void setBoneLocalRotation(BoneType boneType, Quaternion localRot) {
		TransformNode node = getHeadNodeForBone(boneType);
		if (node != null) {
			if (node == hipNode) {
				node.worldTransform.setRotation(localRot);
			} else {
				if (boneType == BoneType.LEFT_UPPER_ARM) {
					localRot.mult(RIGHT_SHOULDER_OFFSET, localRot);
				} else if (boneType == BoneType.RIGHT_UPPER_ARM) {
					localRot.mult(LEFT_SHOULDER_OFFSET, localRot);
				}

				node.localTransform.setRotation(localRot);
			}
		}
	}

	public Vector3f getGlobalTranslationForBone(BoneType boneType) {
		TransformNode node = getHeadNodeForBone(boneType);
		if (node != null){
			if(node.getParent() != null)
				node = node.getParent();
			return node.worldTransform.getTranslation().add(rootPosition);
		}
		return Vector3f.ZERO;
	}

	public Vector3f getLocalTranslationForBone(BoneType boneType) {
		TransformNode node = getHeadNodeForBone(boneType);
		if (node != null){
			if(node == hipNode)
				return node.worldTransform.getTranslation();
			if(node.getParent() != null)
				node = node.getParent();
			return node.localTransform.getTranslation();
		}
		return Vector3f.ZERO;
	}

	public Quaternion getGlobalRotationForBone(BoneType boneType) {
		TransformNode node = getHeadNodeForBone(boneType);
		if (node != null)
			return node.worldTransform.getRotation().mult(rootRotation);
		return Quaternion.IDENTITY;
	}

	public Quaternion getLocalRotationForBone(BoneType boneType) {
		TransformNode node = getHeadNodeForBone(boneType);
		Quaternion rotBuf = new Quaternion();
		if (node != null){
			if(node == hipNode){
				// Use global rotation for hip (root)
				rotBuf.set(node.worldTransform.getRotation());
			}else{
				rotBuf.set(node.worldTransform.getRotation());
				// Adjust from I-Pose to T-Pose
				if (boneType == BoneType.LEFT_UPPER_ARM) {
					rotBuf.multLocal(LEFT_SHOULDER_OFFSET);
				} else if (boneType == BoneType.RIGHT_UPPER_ARM){
					rotBuf.multLocal(RIGHT_SHOULDER_OFFSET);
				}
				// Compute local rotation from parent
				rotBuf.set(node.getParent().worldTransform.getRotation().inverse().multLocal(rotBuf));
			}
		}
		return rotBuf;
	}

	private TransformNode getHeadNodeForBone(BoneType boneType) {
		if (boneType == null) {
			return null;
		}

		return switch (boneType) {
			case HMD, HEAD -> neckNode;
			case NECK -> chestNode;
			case CHEST -> waistTailNode;
			case WAIST -> waistHeadNode;
			case HIP -> hipNode;
			case LEFT_UPPER_LEG -> leftHipNode;
			case RIGHT_UPPER_LEG -> rightHipNode;
			case LEFT_LOWER_LEG -> leftKneeNode;
			case RIGHT_LOWER_LEG -> rightKneeNode;
			case LEFT_FOOT -> leftAnkleNode;
			case RIGHT_FOOT -> rightAnkleNode;
			case LEFT_SHOULDER -> leftShoulderHeadNode;
			case RIGHT_SHOULDER -> rightShoulderHeadNode;
			case LEFT_UPPER_ARM -> leftShoulderTailNode;
			case RIGHT_UPPER_ARM -> rightShoulderTailNode;
			case LEFT_LOWER_ARM -> leftElbowNode;
			case RIGHT_LOWER_ARM -> rightElbowNode;
			case LEFT_HAND -> leftWristNode;
			case RIGHT_HAND -> rightWristNode;
			default -> null;
		};
	}
}
