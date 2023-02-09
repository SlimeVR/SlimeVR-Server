package dev.slimevr.tracking.processor.skeleton;

import com.jme3.math.Quaternion;
import dev.slimevr.tracking.processor.BoneType;
import dev.slimevr.tracking.processor.TransformNode;


public class UnityHierarchy {
	// @formatter:off
	protected final TransformNode headNode = new TransformNode(BoneType.HEAD, true);
	protected final TransformNode neckNode = new TransformNode(BoneType.NECK, true);
	protected final TransformNode chestNode = new TransformNode(BoneType.CHEST, true);
	protected final TransformNode waistNode = new TransformNode(BoneType.WAIST, true);
	protected final TransformNode hipNode = new TransformNode(BoneType.HIP, true);
	protected final TransformNode leftKneeNode = new TransformNode(BoneType.LEFT_UPPER_LEG, true);
	protected final TransformNode leftAnkleNode = new TransformNode(BoneType.LEFT_LOWER_LEG, true);
	protected final TransformNode leftFootNode = new TransformNode(BoneType.LEFT_FOOT, true);
	protected final TransformNode rightKneeNode = new TransformNode(BoneType.RIGHT_UPPER_LEG, true);
	protected final TransformNode rightAnkleNode = new TransformNode(BoneType.RIGHT_LOWER_LEG, true);
	protected final TransformNode rightFootNode = new TransformNode(BoneType.RIGHT_FOOT, true);
	protected final TransformNode leftShoulderTailNode = new TransformNode(BoneType.LEFT_UPPER_ARM, true);
	protected final TransformNode rightShoulderTailNode = new TransformNode(BoneType.RIGHT_UPPER_ARM, true);
	protected final TransformNode leftElbowNode = new TransformNode(BoneType.LEFT_LOWER_ARM, true);
	protected final TransformNode rightElbowNode = new TransformNode(BoneType.RIGHT_LOWER_ARM, true);
	protected final TransformNode leftWristNode = new TransformNode(BoneType.LEFT_HAND, true);
	protected final TransformNode rightWristNode = new TransformNode(BoneType.RIGHT_HAND, true);
	protected final TransformNode leftHandNode = new TransformNode(BoneType.LEFT_HAND, true);
	protected final TransformNode rightHandNode = new TransformNode(BoneType.RIGHT_HAND, true);
	// @formatter:on

	public UnityHierarchy() {
		// Spine
		hipNode.attachChild(waistNode);
		waistNode.attachChild(chestNode);
		chestNode.attachChild(neckNode);
		neckNode.attachChild(headNode);

		// Legs
		hipNode.attachChild(leftKneeNode);
		hipNode.attachChild(rightKneeNode);
		leftKneeNode.attachChild(leftAnkleNode);
		rightKneeNode.attachChild(rightAnkleNode);
		leftAnkleNode.attachChild(leftFootNode);
		rightAnkleNode.attachChild(rightFootNode);

		// Arms
		chestNode.attachChild(leftShoulderTailNode);
		chestNode.attachChild(rightShoulderTailNode);
		leftShoulderTailNode.attachChild(leftElbowNode);
		rightShoulderTailNode.attachChild(rightElbowNode);
		leftElbowNode.attachChild(leftWristNode);
		rightElbowNode.attachChild(rightWristNode);
		leftWristNode.attachChild(leftHandNode);
		rightWristNode.attachChild(rightHandNode);
	}

	public void updatePose(BoneType boneType, Quaternion localRot) {
		hipNode.update();
		TransformNode node = getTailNodeForBone(boneType);
		if(node != null){
			if(node == hipNode){
				node.worldTransform.setRotation(localRot);
			}else{
				node.localTransform.setRotation(localRot);
			}
		}

	}

	public Quaternion getGlobalRotForBone(BoneType boneType){
		TransformNode node = getTailNodeForBone(boneType);
		if(node != null)
			return node.worldTransform.getRotation();
		return Quaternion.IDENTITY;
	}

	private TransformNode getTailNodeForBone(BoneType boneType){
		if (boneType == null) {
			return null;
		}

		switch (boneType) {
			case HMD:
			case HEAD:
				return headNode;
			case NECK:
				return neckNode;
			case CHEST:
				return chestNode;
			case WAIST:
				return waistNode;
			case HIP:
				return hipNode;
			case LEFT_UPPER_LEG:
				return leftKneeNode;
			case RIGHT_UPPER_LEG:
				return rightKneeNode;
			case LEFT_LOWER_LEG:
				return leftAnkleNode;
			case RIGHT_LOWER_LEG:
				return rightAnkleNode;
			case LEFT_FOOT:
				return leftFootNode;
			case RIGHT_FOOT:
				return rightFootNode;
			case LEFT_SHOULDER:
				return leftShoulderTailNode;
			case RIGHT_SHOULDER:
				return rightShoulderTailNode;
			case LEFT_UPPER_ARM:
				return leftElbowNode;
			case RIGHT_UPPER_ARM:
				return rightElbowNode;
			case LEFT_LOWER_ARM:
				return leftWristNode;
			case RIGHT_LOWER_ARM:
				return rightWristNode;
			case LEFT_HAND:
				return leftHandNode;
			case RIGHT_HAND:
				return rightHandNode;
			default:
				return null;
		}
	}
}
