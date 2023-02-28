package dev.slimevr.tracking.processor.skeleton;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.slimevr.osc.UnityBone;
import dev.slimevr.tracking.processor.TransformNode;


public class UnityArmature {
	protected final TransformNode headNode = new TransformNode();
	protected final TransformNode neckTailNode = new TransformNode();
	protected final TransformNode neckHeadNode = new TransformNode();
	protected final TransformNode upperChestNode = new TransformNode();
	protected final TransformNode chestNode = new TransformNode();
	protected final TransformNode waistTailNode = new TransformNode();
	protected final TransformNode waistHeadNode = new TransformNode();
	protected final TransformNode hipNode = new TransformNode();
	protected final TransformNode leftHipNode = new TransformNode();
	protected final TransformNode rightHipNode = new TransformNode();
	protected final TransformNode leftKneeNode = new TransformNode();
	protected final TransformNode leftAnkleNode = new TransformNode();
	protected final TransformNode leftFootNode = new TransformNode();
	protected final TransformNode rightKneeNode = new TransformNode();
	protected final TransformNode rightAnkleNode = new TransformNode();
	protected final TransformNode rightFootNode = new TransformNode();
	protected final TransformNode leftShoulderHeadNode = new TransformNode();
	protected final TransformNode rightShoulderHeadNode = new TransformNode();
	protected final TransformNode leftShoulderTailNode = new TransformNode();
	protected final TransformNode rightShoulderTailNode = new TransformNode();
	protected final TransformNode leftElbowNode = new TransformNode();
	protected final TransformNode rightElbowNode = new TransformNode();
	protected final TransformNode leftWristNode = new TransformNode();
	protected final TransformNode rightWristNode = new TransformNode();
	protected final TransformNode leftHandNode = new TransformNode();
	protected final TransformNode rightHandNode = new TransformNode();
	private static final Quaternion LEFT_SHOULDER_OFFSET = new Quaternion()
		.fromAngles(0f, 0f, FastMath.HALF_PI);
	private static final Quaternion RIGHT_SHOULDER_OFFSET = new Quaternion()
		.fromAngles(0f, 0f, -FastMath.HALF_PI);
	protected final Vector3f rootPosition = new Vector3f();
	protected final Quaternion rootRotation = new Quaternion();

	public UnityArmature(boolean localRotation) {
		for (TransformNode node : getAllNodes()) {
			node.localRotation = localRotation;
		}

		// Attach nodes
		// Spine
		hipNode.attachChild(waistHeadNode);
		waistHeadNode.attachChild(waistTailNode);
		waistTailNode.attachChild(chestNode);
		chestNode.attachChild(upperChestNode);
		upperChestNode.attachChild(neckHeadNode);
		neckHeadNode.attachChild(neckTailNode);
		neckTailNode.attachChild(headNode);

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
		upperChestNode.attachChild(leftShoulderHeadNode);
		upperChestNode.attachChild(rightShoulderHeadNode);
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

	public TransformNode getRootNode() {
		return hipNode;
	}

	public void setRootPose(Vector3f globalPos, Quaternion globalRot) {
		rootPosition.set(globalPos);
		rootRotation.set(globalRot);
	}

	public void setBoneRotationFromGlobal(UnityBone unityBone, Quaternion globalRot) {
		TransformNode node = getHeadNodeOfBone(unityBone);
		if (node != null) {
			if (
				unityBone == UnityBone.LEFT_UPPER_ARM
					|| unityBone == UnityBone.LEFT_LOWER_ARM
					|| unityBone == UnityBone.LEFT_HAND
			) {
				node.localTransform.setRotation(globalRot.mult(LEFT_SHOULDER_OFFSET));
			} else if (
				unityBone == UnityBone.RIGHT_UPPER_ARM
					|| unityBone == UnityBone.RIGHT_LOWER_ARM
					|| unityBone == UnityBone.RIGHT_HAND
			) {
				node.localTransform.setRotation(globalRot.mult(RIGHT_SHOULDER_OFFSET));
			} else {
				node.localTransform.setRotation(globalRot);
			}
		}
	}

	public void setBoneRotationFromLocal(UnityBone unityBone, Quaternion localRot) {
		TransformNode node = getHeadNodeOfBone(unityBone);
		if (node != null) {
			if (unityBone == UnityBone.HIPS) {
				node.worldTransform.setRotation(localRot);
			} else {
				if (unityBone == UnityBone.LEFT_UPPER_ARM) {
					node.localTransform.setRotation(localRot.mult(RIGHT_SHOULDER_OFFSET));
				} else if (unityBone == UnityBone.RIGHT_UPPER_ARM) {
					node.localTransform.setRotation(localRot.mult(LEFT_SHOULDER_OFFSET));
				} else {
					node.localTransform.setRotation(localRot);
				}
			}
		}
	}

	public Vector3f getGlobalTranslationForBone(UnityBone unityBone) {
		TransformNode node = getHeadNodeOfBone(unityBone);
		if (node != null) {
			if (unityBone == UnityBone.HIPS) {
				return node.worldTransform
					.getTranslation()
					.mult(2f)
					.subtractLocal(
						(leftHipNode.worldTransform
							.getTranslation()
							.add(rightHipNode.worldTransform.getTranslation())).multLocal(0.5f)
					)
					.addLocal(rootPosition);
			}
			return node.worldTransform.getTranslation().add(rootPosition);
		}
		return Vector3f.ZERO;
	}

	public Vector3f getLocalTranslationForBone(UnityBone unityBone) {
		TransformNode node = getHeadNodeOfBone(unityBone);
		if (node != null) {
			if (unityBone == UnityBone.HIPS) {
				return node.worldTransform
					.getTranslation()
					.mult(2f)
					.subtractLocal(
						(leftHipNode.worldTransform
							.getTranslation()
							.add(rightHipNode.worldTransform.getTranslation())).multLocal(0.5f)
					)
					.addLocal(rootPosition);
			}
			return node.localTransform.getTranslation();
		}
		return Vector3f.ZERO;
	}

	public Quaternion getGlobalRotationForBone(UnityBone unityBone) {
		TransformNode node = getHeadNodeOfBone(unityBone);
		if (node != null)
			return node.worldTransform.getRotation().mult(rootRotation);
		return Quaternion.IDENTITY;
	}

	public Quaternion getLocalRotationForBone(UnityBone unityBone) {
		TransformNode node = getHeadNodeOfBone(unityBone);
		if (node != null) {
			if (unityBone == UnityBone.HIPS)
				return node.worldTransform.getRotation().mult(rootRotation);
			else
				return node.getParent().worldTransform
					.getRotation()
					.inverse()
					.multLocal(node.worldTransform.getRotation());
		}
		return Quaternion.IDENTITY;
	}

	public TransformNode getHeadNodeOfBone(UnityBone unityBone) {
		if (unityBone == null) {
			return null;
		}

		return switch (unityBone) {
			case HEAD -> neckTailNode;
			case NECK -> neckHeadNode;
			case UPPER_CHEST -> chestNode;
			case CHEST -> waistTailNode;
			case SPINE -> waistHeadNode;
			case HIPS -> hipNode;
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

	public TransformNode[] getAllNodes() {
		return new TransformNode[] {
			headNode,
			neckTailNode,
			neckHeadNode,
			chestNode,
			waistHeadNode,
			waistTailNode,
			hipNode,
			leftHipNode,
			leftKneeNode,
			leftAnkleNode,
			leftFootNode,
			rightHipNode,
			rightKneeNode,
			rightAnkleNode,
			rightFootNode,
			leftShoulderHeadNode,
			rightShoulderHeadNode,
			leftShoulderTailNode,
			rightShoulderTailNode,
			leftElbowNode,
			rightElbowNode,
			leftWristNode,
			rightWristNode,
			leftHandNode,
			rightHandNode,
		};
	}
}
