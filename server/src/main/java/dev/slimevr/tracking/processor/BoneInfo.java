package dev.slimevr.tracking.processor;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.slimevr.tracking.trackers.UnityBone;
import io.eiren.util.collections.FastList;


/**
 * Provides an easy way to access pose information of a particular skeletal bone
 * (as opposed to trackers).
 */
public class BoneInfo {
	public final BoneType boneType;
	public final TransformNode headNode;
	public final TransformNode tailNode;
	public float length;
	private static final Quaternion FOOT_OFFSET = Quaternion.X_90_DEG;
	private static final Quaternion LEFT_SHOULDER_OFFSET = new Quaternion()
		.fromAngles(0f, 0f, -FastMath.HALF_PI);
	private static final Quaternion RIGHT_SHOULDER_OFFSET = new Quaternion()
		.fromAngles(0f, 0f, FastMath.HALF_PI);

	/**
	 * Creates a `BoneInfo`.
	 *
	 * We use `tailNode` because the length of the bone comes from the tail
	 * node's local transform (offset from head), but the rotation of a bone
	 * comes from the head node's rotation.
	 */
	public BoneInfo(BoneType boneType, TransformNode tailNode) {
		this.boneType = boneType;
		this.headNode = tailNode.getParent();
		this.tailNode = tailNode;
		updateLength();
	}

	/**
	 * Recomputes `BoneInfo.length`
	 */
	public void updateLength() {
		this.length = this.tailNode.localTransform.getTranslation().length();
	}

	public Vector3f getLocalTranslation(boolean unity) {
		return tailNode.localTransform.getTranslation();
	}

	public Vector3f getGlobalTranslation(boolean unity) {
		return tailNode.worldTransform.getTranslation();
	}

	// TODO : There shouldn't be edge cases like multiplying
	// feet by rotation. This is the best solution right now,
	// or we'd need to store this info on the client, which is
	// worse. Need to rework the skeleton using new @SkeletonData
	// system
	public Quaternion getLocalRotation(boolean unity) {
		Quaternion rot = headNode.localTransform.getRotation();
		adjustRotation(rot, unity);
		return rot;
	}

	public Quaternion getGlobalRotation(boolean unity) {
		Quaternion rot = headNode.worldTransform.getRotation();
		adjustRotation(rot, unity);
		return rot;
	}

	private void adjustRotation(Quaternion rot, boolean unity) {
		if (!unity) {
			if (boneType == BoneType.LEFT_FOOT || boneType == BoneType.RIGHT_FOOT)
				rot.multLocal(FOOT_OFFSET);
		}
		// TODO
		if (boneType == BoneType.LEFT_UPPER_ARM)
			rot.multLocal(LEFT_SHOULDER_OFFSET);
		if (boneType == BoneType.RIGHT_UPPER_ARM)
			rot.multLocal(RIGHT_SHOULDER_OFFSET);
	}

	/**
	 * @param root The new root for offsetting the translation
	 * @param unity Only use bones from Unity's HumanBodyBones
	 * @return The bone's local translation relative to a new root
	 */
	public Vector3f getLocalBoneTranslationFromRoot(BoneInfo root, boolean unity) {
		if (this == root) {
			return Vector3f.ZERO;
		} else {
			return getGlobalTranslation(unity)
				.subtract(
					getNodeTowards(headNode, root.headNode, unity).worldTransform
						.getTranslation()
				);
		}
	}

	/**
	 * @param root The new root for offsetting the rotation
	 * @param unity Only use bones from Unity's HumanBodyBones
	 * @return The bone's local rotation relative to a new root
	 */
	public Quaternion getLocalBoneRotationFromRoot(BoneInfo root, boolean unity) {
		if (this == root) {
			return headNode.worldTransform.getRotation();
		} else {
			return getGlobalRotation(unity)
				.mult(
					getNodeTowards(headNode, root.headNode, unity).worldTransform
						.getRotation()
						.inverse()
				);
		}
	}

	/**
	 * @param from The root of the search
	 * @param towards The goal of the search
	 * @param unity Only use bones from Unity's HumanBodyBones
	 * @return the first node from "from" towards "towards" or "from" if none
	 * are found.
	 */
	private TransformNode getNodeTowards(
		TransformNode from,
		TransformNode towards,
		boolean unity
	) {
		// Search in parents
		TransformNode searchingNode = from;
		while (searchingNode.getParent() != null) {
			searchingNode = searchingNode.getParent();
			if (searchingNode.getParent() == towards)
				return from.getParent();
		}

		// Search in children
		FastList<TransformNode> searching = new FastList<>(from.children);
		int i = 0;
		while (i < searching.size()) {
			if (searching.get(i).getName().equalsIgnoreCase(towards.getName())) {
				if (unity) {
					TransformNode secondSearching = searching.get(i);
					while (secondSearching.getParent() != from) {
						if (
							secondSearching.getParent().getParent() != null
								&& secondSearching.getParent().getParent() == from
						) {
							if (
								UnityBone
									.getByBoneType(
										BoneType
											.valueOf(secondSearching.getParent().getName())
									)
									!= null
							) {
								secondSearching = secondSearching.getParent();
							}
						} else
							secondSearching = secondSearching.getParent();
					}
					return secondSearching;
				} else {
					TransformNode secondSearching = searching.get(i).getParent();
					while (secondSearching.getParent() != from)
						secondSearching = secondSearching.getParent();
					return secondSearching;
				}
			}
			searching.addAll(searching.get(i).children);
			i++;
		}

		return from;
	}

}
