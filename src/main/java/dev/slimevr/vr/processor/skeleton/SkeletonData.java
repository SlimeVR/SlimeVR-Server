package dev.slimevr.vr.processor.skeleton;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.slimevr.vr.processor.TransformNode;


/**
 * WIP Saved as part of skeleton rework. Is not finished.
 */
@Deprecated
public class SkeletonData {

	public final Joint[] joints;
	public final List<Bone> bones = new ArrayList<>();
	private final SkeletonConfig config;

	private Map<BoneType, List<Bone>> bonesByOffsetKey = new EnumMap<>(
		BoneType.class
	);

	// #region Upper body nodes (torso)
	protected final Joint hmdJoint = new Joint("HMD");
	protected final Joint headJoint = new Joint("Head");
	protected final Joint neckJoint = new Joint("Neck");
	protected final Joint chestJoint = new Joint("Chest");
	protected final Joint trackerChestJoint = new Joint("Chest-Tracker");
	protected final Joint waistJoint = new Joint("Waist");
	protected final Joint hipJoint = new Joint("Hip");
	protected final Joint trackerWaistJoint = new Joint("Waist-Tracker");
	// #region Legs
	protected final Joint leftHipJoint = new Joint("Left-Hip");
	protected final Joint leftKneeJoint = new Joint("Left-Knee");
	protected final Joint trackerLeftKneeJoint = new Joint("Left-Knee-Tracker");
	protected final Joint leftAnkleJoint = new Joint("Left-Ankle");
	protected final Joint leftFootJoint = new Joint("Left-Foot");
	protected final Joint trackerLeftFootJoint = new Joint("Left-Foot-Tracker");
	protected final Joint rightHipJoint = new Joint("Right-Hip");
	protected final Joint rightKneeJoint = new Joint("Right-Knee");
	protected final Joint trackerRightKneeJoint = new Joint("Right-Knee-Tracker");
	protected final Joint rightAnkleJoint = new Joint("Right-Ankle");
	protected final Joint rightFootJoint = new Joint("Right-Foot");
	protected final Joint trackerRightFootJoint = new Joint("Right-Foot-Tracker");
	// #region Arms
	protected final Joint leftShoulderJoint = new Joint("Left-Shoulder");
	protected final Joint rightShoulderJoint = new Joint("Right-Shoulder");
	protected final Joint leftElbowJoint = new Joint("Left-Elbow");
	protected final Joint rightElbowJoint = new Joint("Right-Elbow");
	protected final Joint trackerLeftElbowJoint = new Joint("Left-Elbow-Tracker");
	protected final Joint trackerRightElbowJoint = new Joint("Right-Elbow-Tracker");
	protected final Joint leftWristJoint = new Joint("Left-Wrist");
	protected final Joint rightWristJoint = new Joint("Right-Wrist");
	protected final Joint leftHandJoint = new Joint("Left-Hand-Hm");
	protected final Joint rightHandJoint = new Joint("Right-Hand");
	protected final Joint trackerLeftHandJoint = new Joint("Left-Hand-Tracker");
	protected final Joint trackerRightHandJoint = new Joint("Right-Hand-Tracker");
	// #endregion

	public SkeletonData(SkeletonConfig config) {
		this.config = config;
		List<Joint> jointsList = new ArrayList<>();

		joints = jointsList.toArray(new Joint[jointsList.size()]);
	}

	public void assembleNodeHierarchy() {
		// Notes on directions:
		// Skeleton is built from HMD down, so all nodes are offset in the
		// negative Y direction generally.
		// Forward is Negative Z, left iz Negative X. See head joint and hip
		// joints as examples.
		// #region Assemble skeleton from hmd to hip
		hmdJoint.attachJoint(headJoint, BoneType.HEAD);
		headJoint.attachJoint(neckJoint, BoneType.NECK);
		neckJoint.attachJoint(chestJoint, BoneType.CHEST);
		chestJoint.attachJoint(waistJoint, BoneType.WAIST);
		waistJoint.attachJoint(hipJoint, BoneType.HIP);
		// #endregion

		// #region Assemble skeleton from hips to feet
		hipJoint.attachJoint(leftHipJoint, BoneType.LEFT_HIP);
		hipJoint.attachJoint(rightHipJoint, BoneType.RIGHT_HIP);

		leftHipJoint.attachJoint(leftKneeJoint, BoneType.UPPER_LEG);
		rightHipJoint.attachJoint(rightKneeJoint, BoneType.UPPER_LEG);

		leftKneeJoint.attachJoint(leftAnkleJoint, BoneType.LOWER_LEG);
		rightKneeJoint.attachJoint(rightAnkleJoint, BoneType.LOWER_LEG);

		leftAnkleJoint.attachJoint(leftFootJoint, BoneType.FOOT);
		rightAnkleJoint.attachJoint(rightFootJoint, BoneType.FOOT);
		// #endregion

		// #region Assemble skeleton arms from controllers
		// TODO : Rebuild skeleton depending on if it's from controllers or from
		// shoulders
		// if (fromControllers)
		leftHandJoint.attachJoint(leftWristJoint, BoneType.HAND);
		rightHandJoint.attachJoint(rightWristJoint, BoneType.HAND);
		rightWristJoint.attachJoint(leftElbowJoint, BoneType.LOWER_ARM);
		leftWristJoint.attachJoint(rightElbowJoint, BoneType.LOWER_ARM);
		// } else {
		// #endregion

		// #region Assemble skeleton arms from chest
		chestJoint.attachJoint(leftShoulderJoint, BoneType.LEFT_SHOULDER);
		chestJoint.attachJoint(rightShoulderJoint, BoneType.RIGHT_SHOULDER);

		leftShoulderJoint.attachJoint(leftElbowJoint, BoneType.LOWER_ARM);
		rightShoulderJoint.attachJoint(rightElbowJoint, BoneType.LOWER_ARM);

		leftElbowJoint.attachJoint(leftWristJoint, BoneType.HAND);
		rightElbowJoint.attachJoint(rightWristJoint, BoneType.HAND);

		leftWristJoint.attachJoint(leftHandJoint, BoneType.HAND);
		rightWristJoint.attachJoint(rightHandJoint, BoneType.HAND);
		// }
		// #endregion

		// #region Attach tracker nodes for offsets
		chestJoint.attachJoint(trackerChestJoint, BoneType.CHEST_TRACKER);
		hipJoint.attachJoint(trackerWaistJoint, BoneType.WAIST_TRACKER);

		leftKneeJoint.attachJoint(trackerLeftKneeJoint, BoneType.KNEE_TRACKER);
		rightKneeJoint.attachJoint(trackerRightKneeJoint, BoneType.KNEE_TRACKER);

		leftFootJoint.attachJoint(trackerLeftFootJoint, BoneType.FOOT_TRACKER);
		rightFootJoint.attachJoint(trackerRightFootJoint, BoneType.FOOT_TRACKER);

		leftElbowJoint.attachJoint(trackerLeftElbowJoint, BoneType.ELBOW_TRACKER);
		rightElbowJoint.attachJoint(trackerRightElbowJoint, BoneType.ELBOW_TRACKER);

		leftHandJoint.attachJoint(trackerLeftHandJoint, BoneType.HAND_TRACKER);
		rightHandJoint.attachJoint(trackerRightHandJoint, BoneType.HAND_TRACKER);
		// #endregion
	}

	public class Joint {
		public final TransformNode node;
		public Map<Joint, Bone> childrenBones = new HashMap<>();
		public boolean isInput = false;

		public Joint(String name) {
			node = new TransformNode(name, false);
		}

		public Bone attachJoint(Joint childJoint, BoneType offsetKey) {
			Bone bone = childrenBones.get(childJoint);
			if (bone == null) {
				bone = new Bone(
					this.node.getName() + "-" + childJoint.node.getName(),
					this,
					childJoint,
					offsetKey
				);
				childrenBones.put(childJoint, bone);
				bones.add(bone);
				List<Bone> bonesByOffset = bonesByOffsetKey.get(offsetKey);
				if (bonesByOffset == null) {
					bonesByOffset = new ArrayList<>();
					bonesByOffsetKey.put(offsetKey, bonesByOffset);
				}
				bonesByOffset.add(bone);
			} else {
				List<Bone> oldBonesList = bonesByOffsetKey.get(bone.offsetKey);
				if (oldBonesList != null) {
					oldBonesList.remove(bone);
				}
				bone.offsetKey = offsetKey;
				List<Bone> bonesByOffset = bonesByOffsetKey.get(offsetKey);
				if (bonesByOffset == null) {
					bonesByOffset = new ArrayList<>();
					bonesByOffsetKey.put(offsetKey, bonesByOffset);
				}
				bonesByOffset.add(bone);
			}
			return bone;
		}

		public Bone detachJoint(Joint childJoint) {
			Bone bone = childrenBones.remove(childJoint);
			if (bone != null) {
				bones.remove(bone);
				List<Bone> oldBonesList = bonesByOffsetKey.get(bone.offsetKey);
				if (oldBonesList != null) {
					oldBonesList.remove(bone);
				}
			}
			return bone;
		}
	}

	public class Bone {
		public final Joint parent;
		public final Joint child;
		public BoneType offsetKey;

		public Bone(
			String name,
			Joint firstJoint,
			Joint secondJoint,
			BoneType offsetKey
		) {
			this.parent = firstJoint;
			this.child = secondJoint;
			this.offsetKey = offsetKey;
		}
	}
}
