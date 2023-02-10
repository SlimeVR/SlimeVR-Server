package dev.slimevr.tracking.processor.skeleton;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import dev.slimevr.tracking.processor.BoneType;
import dev.slimevr.tracking.processor.TransformNode;


public class UnityHierarchy {
	// @formatter:off
	protected final TransformNode headNode = new TransformNode(BoneType.HEAD, true);
	protected final TransformNode neckNode = new TransformNode(BoneType.NECK, true);
	protected final TransformNode chestNode = new TransformNode(BoneType.CHEST, true);
	protected final TransformNode waistTailNode = new TransformNode(BoneType.WAIST, true);
	protected final TransformNode waistHeadNode = new TransformNode(BoneType.WAIST, true);
	protected final TransformNode hipNode = new TransformNode(BoneType.HIP, true);
	protected final TransformNode leftHipNode = new TransformNode(BoneType.LEFT_HIP, true);
	protected final TransformNode rightHipNode = new TransformNode(BoneType.RIGHT_HIP, true);
	protected final TransformNode leftKneeNode = new TransformNode(BoneType.LEFT_UPPER_LEG, true);
	protected final TransformNode leftAnkleNode = new TransformNode(BoneType.LEFT_LOWER_LEG, true);
	protected final TransformNode leftFootNode = new TransformNode(BoneType.LEFT_FOOT, true);
	protected final TransformNode rightKneeNode = new TransformNode(BoneType.RIGHT_UPPER_LEG, true);
	protected final TransformNode rightAnkleNode = new TransformNode(BoneType.RIGHT_LOWER_LEG, true);
	protected final TransformNode rightFootNode = new TransformNode(BoneType.RIGHT_FOOT, true);
	protected final TransformNode leftShoulderHeadNode = new TransformNode(BoneType.LEFT_SHOULDER, true);
	protected final TransformNode rightShoulderHeadNode = new TransformNode(BoneType.RIGHT_SHOULDER, true);
	protected final TransformNode leftShoulderTailNode = new TransformNode(BoneType.LEFT_UPPER_ARM, true);
	protected final TransformNode rightShoulderTailNode = new TransformNode(BoneType.RIGHT_UPPER_ARM, true);
	protected final TransformNode leftElbowNode = new TransformNode(BoneType.LEFT_LOWER_ARM, true);
	protected final TransformNode rightElbowNode = new TransformNode(BoneType.RIGHT_LOWER_ARM, true);
	protected final TransformNode leftWristNode = new TransformNode(BoneType.LEFT_HAND, true);
	protected final TransformNode rightWristNode = new TransformNode(BoneType.RIGHT_HAND, true);
	protected final TransformNode leftHandNode = new TransformNode(BoneType.LEFT_HAND, true);
	protected final TransformNode rightHandNode = new TransformNode(BoneType.RIGHT_HAND, true);
	// @formatter:on
	private static final Quaternion LEFT_SHOULDER_OFFSET = new Quaternion()
		.fromAngles(0f, 0f, -FastMath.HALF_PI);
	private static final Quaternion RIGHT_SHOULDER_OFFSET = new Quaternion()
		.fromAngles(0f, 0f, FastMath.HALF_PI);
	protected final Quaternion rootRotation = new Quaternion();

	public UnityHierarchy() {
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

	public void updateBone(BoneType boneType, Quaternion localRot) {
		TransformNode node = getHeadNodeForBone(boneType);
		if (node != null) {
			if (node == hipNode) {
				node.worldTransform.setRotation(localRot);
			} else {
				if (boneType == BoneType.LEFT_UPPER_ARM) {
					node.localTransform.setRotation(localRot.mult(LEFT_SHOULDER_OFFSET));
				} else if (boneType == BoneType.RIGHT_UPPER_ARM) {
					node.localTransform.setRotation(localRot.mult(RIGHT_SHOULDER_OFFSET));
				} else {
					node.localTransform.setRotation(localRot);
				}
			}
		}
	}

	public Quaternion getGlobalRotForBone(BoneType boneType) {
		TransformNode node = getHeadNodeForBone(boneType);
		if (node != null)
			return node.worldTransform.getRotation().mult(rootRotation);
		return Quaternion.IDENTITY;
	}

	public void setRootRotation(Quaternion globalRot) {
		rootRotation.set(globalRot);
	}

	private TransformNode getHeadNodeForBone(BoneType boneType) {
		if (boneType == null) {
			return null;
		}

		switch (boneType) {
			case HMD:
			case HEAD:
				return neckNode;
			case NECK:
				return chestNode;
			case CHEST:
				return waistTailNode;
			case WAIST:
				return waistHeadNode;
			case HIP:
				return hipNode;
			case LEFT_UPPER_LEG:
				return leftHipNode;
			case RIGHT_UPPER_LEG:
				return rightHipNode;
			case LEFT_LOWER_LEG:
				return leftKneeNode;
			case RIGHT_LOWER_LEG:
				return rightKneeNode;
			case LEFT_FOOT:
				return leftAnkleNode;
			case RIGHT_FOOT:
				return rightAnkleNode;
			case LEFT_SHOULDER:
				return leftShoulderHeadNode;
			case RIGHT_SHOULDER:
				return rightShoulderHeadNode;
			case LEFT_UPPER_ARM:
				return leftShoulderTailNode;
			case RIGHT_UPPER_ARM:
				return rightShoulderTailNode;
			case LEFT_LOWER_ARM:
				return leftElbowNode;
			case RIGHT_LOWER_ARM:
				return rightElbowNode;
			case LEFT_HAND:
				return leftWristNode;
			case RIGHT_HAND:
				return rightWristNode;
			default:
				return null;
		}
	}
}
