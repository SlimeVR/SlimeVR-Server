package dev.slimevr.tracking.processor.skeleton;

import dev.slimevr.tracking.processor.BoneType;
import dev.slimevr.tracking.processor.TransformNode;
import dev.slimevr.tracking.processor.config.SkeletonConfigManager;

import java.util.*;


/**
 * WIP Saved as part of skeleton rework. Is not finished.
 */
@Deprecated
public class SkeletonData {

	public final Joint[] joints;
	public final List<Bone> bones = new ArrayList<>();
	private final SkeletonConfigManager config;

	private Map<BoneType, List<Bone>> bonesByOffsetKey = new EnumMap<>(
		BoneType.class
	);

	// #region Upper body nodes (torso)
	protected final Joint hmdJoint = new Joint(BoneType.HMD);
	protected final Joint headJoint = new Joint(BoneType.HEAD);
	protected final Joint neckJoint = new Joint(BoneType.NECK);
	protected final Joint chestJoint = new Joint(BoneType.CHEST);
	protected final Joint trackerChestJoint = new Joint(BoneType.CHEST_TRACKER);
	protected final Joint waistJoint = new Joint(BoneType.WAIST);
	protected final Joint hipJoint = new Joint(BoneType.HIP);
	protected final Joint trackerHipJoint = new Joint(BoneType.HIP_TRACKER);
	// #region Legs
	protected final Joint leftHipJoint = new Joint(BoneType.LEFT_HIP);
	protected final Joint leftKneeJoint = new Joint(BoneType.LEFT_UPPER_LEG);
	protected final Joint trackerLeftKneeJoint = new Joint(BoneType.LEFT_KNEE_TRACKER);
	protected final Joint leftAnkleJoint = new Joint(BoneType.LEFT_LOWER_LEG);
	protected final Joint leftFootJoint = new Joint(BoneType.LEFT_FOOT);
	protected final Joint trackerLeftFootJoint = new Joint(BoneType.LEFT_FOOT_TRACKER);
	protected final Joint rightHipJoint = new Joint(BoneType.RIGHT_HIP);
	protected final Joint rightKneeJoint = new Joint(BoneType.RIGHT_UPPER_LEG);
	protected final Joint trackerRightKneeJoint = new Joint(BoneType.RIGHT_KNEE_TRACKER);
	protected final Joint rightAnkleJoint = new Joint(BoneType.RIGHT_LOWER_LEG);
	protected final Joint rightFootJoint = new Joint(BoneType.RIGHT_FOOT);
	protected final Joint trackerRightFootJoint = new Joint(BoneType.RIGHT_FOOT_TRACKER);
	// #region Arms
	protected final Joint leftShoulderJoint = new Joint(BoneType.LEFT_SHOULDER);
	protected final Joint rightShoulderJoint = new Joint(BoneType.RIGHT_SHOULDER);
	protected final Joint leftElbowJoint = new Joint(BoneType.LEFT_UPPER_LEG);
	protected final Joint rightElbowJoint = new Joint(BoneType.RIGHT_UPPER_ARM);
	protected final Joint trackerLeftElbowJoint = new Joint(BoneType.LEFT_ELBOW_TRACKER);
	protected final Joint trackerRightElbowJoint = new Joint(BoneType.RIGHT_ELBOW_TRACKER);
	protected final Joint leftWristJoint = new Joint(BoneType.LEFT_LOWER_ARM);
	protected final Joint rightWristJoint = new Joint(BoneType.RIGHT_LOWER_ARM);
	protected final Joint leftHandJoint = new Joint(BoneType.LEFT_HAND);
	protected final Joint rightHandJoint = new Joint(BoneType.RIGHT_HAND);
	protected final Joint trackerLeftHandJoint = new Joint(BoneType.LEFT_HAND_TRACKER);
	protected final Joint trackerRightHandJoint = new Joint(BoneType.RIGHT_HAND_TRACKER);
	// #endregion

	public SkeletonData(SkeletonConfigManager config) {
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

		leftHipJoint.attachJoint(leftKneeJoint, BoneType.LEFT_UPPER_LEG);
		rightHipJoint.attachJoint(rightKneeJoint, BoneType.RIGHT_UPPER_LEG);

		leftKneeJoint.attachJoint(leftAnkleJoint, BoneType.LEFT_LOWER_LEG);
		rightKneeJoint.attachJoint(rightAnkleJoint, BoneType.RIGHT_LOWER_LEG);

		leftAnkleJoint.attachJoint(leftFootJoint, BoneType.LEFT_FOOT);
		rightAnkleJoint.attachJoint(rightFootJoint, BoneType.RIGHT_FOOT);
		// #endregion

		// #region Assemble skeleton arms from controllers
		// TODO : Rebuild skeleton depending on if it's from controllers or from
		// shoulders
		// if (fromControllers)
		leftHandJoint.attachJoint(leftWristJoint, BoneType.LEFT_HAND);
		rightHandJoint.attachJoint(rightWristJoint, BoneType.RIGHT_HAND);
		rightWristJoint.attachJoint(leftElbowJoint, BoneType.LEFT_LOWER_ARM);
		leftWristJoint.attachJoint(rightElbowJoint, BoneType.RIGHT_LOWER_ARM);
		// } else {
		// #endregion

		// #region Assemble skeleton arms from chest
		chestJoint.attachJoint(leftShoulderJoint, BoneType.LEFT_SHOULDER);
		chestJoint.attachJoint(rightShoulderJoint, BoneType.RIGHT_SHOULDER);

		leftShoulderJoint.attachJoint(leftElbowJoint, BoneType.LEFT_LOWER_ARM);
		rightShoulderJoint.attachJoint(rightElbowJoint, BoneType.RIGHT_LOWER_ARM);

		leftElbowJoint.attachJoint(leftWristJoint, BoneType.LEFT_HAND);
		rightElbowJoint.attachJoint(rightWristJoint, BoneType.RIGHT_HAND);

		leftWristJoint.attachJoint(leftHandJoint, BoneType.LEFT_HAND);
		rightWristJoint.attachJoint(rightHandJoint, BoneType.RIGHT_HAND);
		// }
		// #endregion

		// #region Attach tracker nodes for offsets
		chestJoint.attachJoint(trackerChestJoint, BoneType.CHEST_TRACKER);
		hipJoint.attachJoint(trackerHipJoint, BoneType.HIP_TRACKER);

		leftKneeJoint.attachJoint(trackerLeftKneeJoint, BoneType.LEFT_KNEE_TRACKER);
		rightKneeJoint.attachJoint(trackerRightKneeJoint, BoneType.RIGHT_KNEE_TRACKER);

		leftFootJoint.attachJoint(trackerLeftFootJoint, BoneType.LEFT_FOOT_TRACKER);
		rightFootJoint.attachJoint(trackerRightFootJoint, BoneType.RIGHT_FOOT_TRACKER);

		leftElbowJoint.attachJoint(trackerLeftElbowJoint, BoneType.LEFT_ELBOW_TRACKER);
		rightElbowJoint.attachJoint(trackerRightElbowJoint, BoneType.RIGHT_ELBOW_TRACKER);

		leftHandJoint.attachJoint(trackerLeftHandJoint, BoneType.LEFT_HAND_TRACKER);
		rightHandJoint.attachJoint(trackerRightHandJoint, BoneType.RIGHT_HAND_TRACKER);
		// #endregion
	}

	public class Joint {
		public final TransformNode node;
		public Map<Joint, Bone> childrenBones = new HashMap<>();
		public boolean isInput = false;

		public Joint(BoneType bone) {
			node = new TransformNode(bone, false);
		}

		public Bone attachJoint(Joint childJoint, BoneType offsetKey) {
			Bone bone = childrenBones.get(childJoint);
			if (bone == null) {
				bone = new Bone(
					this.node.getBoneType().name() + "-" + childJoint.node.getBoneType().name(),
					this,
					childJoint,
					offsetKey
				);
				childrenBones.put(childJoint, bone);
				bones.add(bone);
				List<Bone> bonesByOffset = bonesByOffsetKey
					.computeIfAbsent(offsetKey, k -> new ArrayList<>());
				bonesByOffset.add(bone);
			} else {
				List<Bone> oldBonesList = bonesByOffsetKey.get(bone.offsetKey);
				if (oldBonesList != null) {
					oldBonesList.remove(bone);
				}
				bone.offsetKey = offsetKey;
				List<Bone> bonesByOffset = bonesByOffsetKey
					.computeIfAbsent(offsetKey, k -> new ArrayList<>());
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
