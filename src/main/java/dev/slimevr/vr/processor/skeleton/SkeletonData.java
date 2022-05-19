package dev.slimevr.vr.processor.skeleton;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.slimevr.vr.processor.TransformNode;


public class SkeletonData {

	public final Joint[] joints;
	public final List<Bone> bones = new ArrayList<>();
	private final SkeletonConfig config;

	private Map<SkeletonNodeOffset, List<Bone>> bonesByOffsetKey = new EnumMap<>(
		SkeletonNodeOffset.class
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
		hmdJoint.attachJoint(headJoint, SkeletonNodeOffset.HEAD);
		headJoint.attachJoint(neckJoint, SkeletonNodeOffset.NECK);
		neckJoint.attachJoint(chestJoint, SkeletonNodeOffset.CHEST);
		chestJoint.attachJoint(waistJoint, SkeletonNodeOffset.WAIST);
		waistJoint.attachJoint(hipJoint, SkeletonNodeOffset.HIP);
		// #endregion

		// #region Assemble skeleton from hips to feet
		hipJoint.attachJoint(leftHipJoint, SkeletonNodeOffset.LEFT_HIP);
		hipJoint.attachJoint(rightHipJoint, SkeletonNodeOffset.RIGHT_HIP);

		leftHipJoint.attachJoint(leftKneeJoint, SkeletonNodeOffset.KNEE);
		rightHipJoint.attachJoint(rightKneeJoint, SkeletonNodeOffset.KNEE);

		leftKneeJoint.attachJoint(leftAnkleJoint, SkeletonNodeOffset.ANKLE);
		rightKneeJoint.attachJoint(rightAnkleJoint, SkeletonNodeOffset.ANKLE);

		leftAnkleJoint.attachJoint(leftFootJoint, SkeletonNodeOffset.FOOT);
		rightAnkleJoint.attachJoint(rightFootJoint, SkeletonNodeOffset.FOOT);
		// #endregion

		// #region Assemble skeleton arms from controllers
		// TODO : Rebuild skeleton depending on if it's from controllers or from
		// shoulders
		// if (fromControllers)
		leftHandJoint.attachJoint(leftWristJoint, SkeletonNodeOffset.HAND);
		rightHandJoint.attachJoint(rightWristJoint, SkeletonNodeOffset.HAND);
		rightWristJoint.attachJoint(leftElbowJoint, SkeletonNodeOffset.FOREARM);
		leftWristJoint.attachJoint(rightElbowJoint, SkeletonNodeOffset.FOREARM);
		// } else {
		// #endregion

		// #region Assemble skeleton arms from chest
		chestJoint.attachJoint(leftShoulderJoint, SkeletonNodeOffset.LEFT_SHOULDER);
		chestJoint.attachJoint(rightShoulderJoint, SkeletonNodeOffset.RIGHT_SHOULDER);

		leftShoulderJoint.attachJoint(leftElbowJoint, SkeletonNodeOffset.FOREARM);
		rightShoulderJoint.attachJoint(rightElbowJoint, SkeletonNodeOffset.FOREARM);

		leftElbowJoint.attachJoint(leftWristJoint, SkeletonNodeOffset.HAND);
		rightElbowJoint.attachJoint(rightWristJoint, SkeletonNodeOffset.HAND);

		leftWristJoint.attachJoint(leftHandJoint, SkeletonNodeOffset.HAND);
		rightWristJoint.attachJoint(rightHandJoint, SkeletonNodeOffset.HAND);
		// }
		// #endregion

		// #region Attach tracker nodes for offsets
		chestJoint.attachJoint(trackerChestJoint, SkeletonNodeOffset.CHEST_TRACKER);
		hipJoint.attachJoint(trackerWaistJoint, SkeletonNodeOffset.WAIST_TRACKER);

		leftKneeJoint.attachJoint(trackerLeftKneeJoint, SkeletonNodeOffset.KNEE_TRACKER);
		rightKneeJoint.attachJoint(trackerRightKneeJoint, SkeletonNodeOffset.KNEE_TRACKER);

		leftFootJoint.attachJoint(trackerLeftFootJoint, SkeletonNodeOffset.FOOT_TRACKER);
		rightFootJoint.attachJoint(trackerRightFootJoint, SkeletonNodeOffset.FOOT_TRACKER);

		leftElbowJoint.attachJoint(trackerLeftElbowJoint, SkeletonNodeOffset.ELBOW_TRACKER);
		rightElbowJoint.attachJoint(trackerRightElbowJoint, SkeletonNodeOffset.ELBOW_TRACKER);

		leftHandJoint.attachJoint(trackerLeftHandJoint, SkeletonNodeOffset.HAND_TRACKER);
		rightHandJoint.attachJoint(trackerRightHandJoint, SkeletonNodeOffset.HAND_TRACKER);
		// #endregion
	}

	public class Joint {
		public final TransformNode node;
		public Map<Joint, Bone> childrenBones = new HashMap<>();
		public boolean isInput = false;

		public Joint(String name) {
			node = new TransformNode(name, false);
		}

		public Bone attachJoint(Joint childJoint, SkeletonNodeOffset offsetKey) {
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
		public SkeletonNodeOffset offsetKey;

		public Bone(
			String name,
			Joint firstJoint,
			Joint secondJoint,
			SkeletonNodeOffset offsetKey
		) {
			this.parent = firstJoint;
			this.child = secondJoint;
			this.offsetKey = offsetKey;
		}
	}
}
