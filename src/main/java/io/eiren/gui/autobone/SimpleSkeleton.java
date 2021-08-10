package io.eiren.gui.autobone;

import java.util.HashMap;
import java.util.Map.Entry;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import io.eiren.vr.VRServer;
import io.eiren.vr.processor.HumanSkeletonWithLegs;
import io.eiren.vr.processor.HumanSkeletonWithWaist;
import io.eiren.vr.processor.TransformNode;

public class SimpleSkeleton {

	protected final VRServer server;

	// Waist
	protected final TransformNode hmdNode = new TransformNode("HMD", false);
	protected final TransformNode headNode = new TransformNode("Head", false);
	protected final TransformNode neckNode = new TransformNode("Neck", false);
	protected final TransformNode waistNode = new TransformNode("Waist", false);
	protected final TransformNode chestNode = new TransformNode("Chest", false);

	protected float chestDistance = 0.42f;
	/**
	 * Distance from eyes to waist
	 */
	protected float waistDistance = 0.85f;
	/**
	 * Distance from eyes to the base of the neck
	 */
	protected float neckLength = HumanSkeletonWithWaist.NECK_LENGTH_DEFAULT;
	/**
	 * Distance from eyes to ear
	 */
	protected float headShift = HumanSkeletonWithWaist.HEAD_SHIFT_DEFAULT;

	// Legs
	protected final TransformNode leftHipNode = new TransformNode("Left-Hip", false);
	protected final TransformNode leftKneeNode = new TransformNode("Left-Knee", false);
	protected final TransformNode leftAnkleNode = new TransformNode("Left-Ankle", false);
	protected final TransformNode leftFootNode = new TransformNode("Left-Foot", false);
	protected final TransformNode rightHipNode = new TransformNode("Right-Hip", false);
	protected final TransformNode rightKneeNode = new TransformNode("Right-Knee", false);
	protected final TransformNode rightAnkleNode = new TransformNode("Right-Ankle", false);
	protected final TransformNode rightFootNode = new TransformNode("Right-Foot", false);

	/**
	 * Distance between centers of both hips
	 */
	protected float hipsWidth = HumanSkeletonWithLegs.HIPS_WIDTH_DEFAULT;
	/**
	 * Length from waist to knees
	 */
	protected float kneeHeight = 0.42f;
	/**
	 * Distance from waist to ankle
	 */
	protected float legsLength = 0.84f;
	protected float footLength = HumanSkeletonWithLegs.FOOT_LENGTH_DEFAULT;

	protected final HashMap<String, TransformNode> nodes = new HashMap<String,TransformNode>();

	public SimpleSkeleton(VRServer server) {
		this.server = server;

		// Load waist configs
		headShift = server.config.getFloat("body.headShift", headShift);
		neckLength = server.config.getFloat("body.neckLength", neckLength);
		chestDistance = server.config.getFloat("body.chestDistance", chestDistance);
		waistDistance = server.config.getFloat("body.waistDistance", waistDistance);

		// Load leg configs
		hipsWidth = server.config.getFloat("body.hipsWidth", hipsWidth);
		kneeHeight = server.config.getFloat("body.kneeHeight", kneeHeight);
		legsLength = server.config.getFloat("body.legsLength", legsLength);
		footLength = server.config.getFloat("body.footLength", footLength);

		// Assemble skeleton to waist
		hmdNode.attachChild(headNode);
		headNode.localTransform.setTranslation(0, 0, headShift);

		headNode.attachChild(neckNode);
		neckNode.localTransform.setTranslation(0, -neckLength, 0);

		neckNode.attachChild(chestNode);
		chestNode.localTransform.setTranslation(0, -chestDistance, 0);

		chestNode.attachChild(waistNode);
		waistNode.localTransform.setTranslation(0, -(waistDistance - chestDistance), 0);

		// Assemble skeleton to feet
		waistNode.attachChild(leftHipNode);
		leftHipNode.localTransform.setTranslation(-hipsWidth / 2, 0, 0);

		waistNode.attachChild(rightHipNode);
		rightHipNode.localTransform.setTranslation(hipsWidth / 2, 0, 0);

		leftHipNode.attachChild(leftKneeNode);
		leftKneeNode.localTransform.setTranslation(0, -(legsLength - kneeHeight), 0);

		rightHipNode.attachChild(rightKneeNode);
		rightKneeNode.localTransform.setTranslation(0, -(legsLength - kneeHeight), 0);

		leftKneeNode.attachChild(leftAnkleNode);
		leftAnkleNode.localTransform.setTranslation(0, -kneeHeight, 0);

		rightKneeNode.attachChild(rightAnkleNode);
		rightAnkleNode.localTransform.setTranslation(0, -kneeHeight, 0);

		leftAnkleNode.attachChild(leftFootNode);
		leftFootNode.localTransform.setTranslation(0, 0, -footLength);

		rightAnkleNode.attachChild(rightFootNode);
		rightFootNode.localTransform.setTranslation(0, 0, -footLength);

		// Set up a HashMap to get nodes by name easily
		hmdNode.depthFirstTraversal(visitor -> {
			nodes.put(visitor.getName(), visitor);
		});
	}

	public void setPoseFromSkeleton(HumanSkeletonWithLegs humanSkeleton) {
		TransformNode rootNode = humanSkeleton.getRootNode();

		// Copy headset position
		hmdNode.localTransform.setTranslation(rootNode.localTransform.getTranslation());

		// Copy all rotations
		rootNode.depthFirstTraversal(visitor -> {
			TransformNode targetNode = nodes.get(visitor.getName());

			// Handle unexpected nodes gracefully
			if (targetNode != null) {
				targetNode.localTransform.setRotation(visitor.localTransform.getRotation());
			}
		});
	}

	public void setPoseFromFrame(PoseFrame frame) {
		// Copy headset position
		hmdNode.localTransform.setTranslation(frame.rootPos);

		// Copy all rotations
		for (Entry<String, Quaternion> rotation : frame.rotations.entrySet()) {
			TransformNode targetNode = nodes.get(rotation.getKey());

			// Handle unexpected nodes gracefully
			if (targetNode != null) {
				targetNode.localTransform.setRotation(rotation.getValue());
			}
		}
	}

	public void setSkeletonConfig(String joint, float newLength) {
		switch(joint) {
		case "Head":
			headShift = newLength;
			headNode.localTransform.setTranslation(0, 0, headShift);
			break;
		case "Neck":
			neckLength = newLength;
			neckNode.localTransform.setTranslation(0, -neckLength, 0);
			break;
		case "Waist":
			waistDistance = newLength;
			waistNode.localTransform.setTranslation(0, -(waistDistance - chestDistance), 0);
			break;
		case "Chest":
			chestDistance = newLength;
			chestNode.localTransform.setTranslation(0, -chestDistance, 0);
			waistNode.localTransform.setTranslation(0, -(waistDistance - chestDistance), 0);
			break;
		case "Hips width":
			hipsWidth = newLength;
			leftHipNode.localTransform.setTranslation(-hipsWidth / 2, 0, 0);
			rightHipNode.localTransform.setTranslation(hipsWidth / 2, 0, 0);
			break;
		case "Knee height":
			kneeHeight = newLength;
			leftAnkleNode.localTransform.setTranslation(0, -kneeHeight, 0);
			rightAnkleNode.localTransform.setTranslation(0, -kneeHeight, 0);
			leftKneeNode.localTransform.setTranslation(0, -(legsLength - kneeHeight), 0);
			rightKneeNode.localTransform.setTranslation(0, -(legsLength - kneeHeight), 0);
			break;
		case "Legs length":
			legsLength = newLength;
			leftKneeNode.localTransform.setTranslation(0, -(legsLength - kneeHeight), 0);
			rightKneeNode.localTransform.setTranslation(0, -(legsLength - kneeHeight), 0);
			break;
		case "Foot length":
			footLength = newLength;
			leftFootNode.localTransform.setTranslation(0, 0, -footLength);
			rightFootNode.localTransform.setTranslation(0, 0, -footLength);
			break;
		}
	}

	public void updatePose() {
		hmdNode.update();
	}

	public Vector3f getLeftFootPos() {
		return leftAnkleNode.worldTransform.getTranslation();
	}

	public Vector3f getRightFootPos() {
		return rightAnkleNode.worldTransform.getTranslation();
	}

	public void saveConfigs() {
		// Save waist configs
		server.config.setProperty("body.headShift", headShift);
		server.config.setProperty("body.neckLength", neckLength);
		server.config.setProperty("body.waistDistance", waistDistance);
		server.config.setProperty("body.chestDistance", chestDistance);

		// Save leg configs
		server.config.setProperty("body.hipsWidth", hipsWidth);
		server.config.setProperty("body.kneeHeight", kneeHeight);
		server.config.setProperty("body.legsLength", legsLength);
		server.config.setProperty("body.footLength", footLength);
	}
}
