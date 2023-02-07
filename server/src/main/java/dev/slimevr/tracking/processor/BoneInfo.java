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
	protected final TransformNode unityNodeTowardsHip;
	private static final Quaternion FOOT_OFFSET = Quaternion.X_90_DEG;
	private static final Quaternion LEFT_SHOULDER_OFFSET = new Quaternion()
		.fromAngles(0f, 0f, FastMath.HALF_PI);
	private static final Quaternion RIGHT_SHOULDER_OFFSET = new Quaternion()
		.fromAngles(0f, 0f, -FastMath.HALF_PI);

	/**
	 * Creates a `BoneInfo`. We use `tailNode` because the length of the bone
	 * comes from the tail node's local transform (offset from head), but the
	 * rotation of a bone comes from the head node's rotation.
	 */
	public BoneInfo(BoneType boneType, TransformNode tailNode) {
		this.boneType = boneType;
		this.tailNode = tailNode;
		if (tailNode.getParent() != null)
			this.headNode = tailNode.getParent();
		else
			this.headNode = tailNode;
		unityNodeTowardsHip = getNodeTowards(BoneType.HIP, true);
		updateLength();
	}

	/**
	 * Recomputes `BoneInfo.length`
	 */
	public void updateLength() {
		length = tailNode.localTransform.getTranslation().length();
	}

	public Vector3f getGlobalTranslation(boolean unity) {
		return getAdjustedTranslation(tailNode.worldTransform.getTranslation(), unity);
	}

	public Vector3f getLocalTranslation(boolean unity) {
		return getAdjustedTranslation(tailNode.localTransform.getTranslation(), unity);
	}

	public Quaternion getGlobalRotation(boolean unity) {
		return getAdjustedRotation(headNode.worldTransform.getRotation(), unity);
	}

	public Quaternion getLocalRotation(boolean unity) {
		return getAdjustedRotation(headNode.localTransform.getRotation(), unity);
	}

	// TODO : There shouldn't be edge cases like multiplying
	// feet by rotation. This is the best solution right now,
	// or we'd need to store this info on the client, which is
	// worse. Need to rework the way the sussy offsets work
	private Vector3f getAdjustedTranslation(Vector3f pos, boolean unity) {
		// TODO
		if (!unity) {
			// Offset feet 90 degrees to satisfy the SteamVR bone overlay
			if (boneType == BoneType.LEFT_FOOT || boneType == BoneType.RIGHT_FOOT) {
//				pos.multLocal(FOOT_OFFSET);
			}
		} else { // Adapt to Unity/VMC standards
			// Offset to satisfy apps that expect T-Pose, but we have I-Pose
			if (
				boneType == BoneType.LEFT_UPPER_ARM
					|| boneType == BoneType.LEFT_LOWER_ARM
					|| boneType == BoneType.LEFT_HAND
			) {
//				pos.multLocal(LEFT_SHOULDER_OFFSET);
			} else if (
				boneType == BoneType.RIGHT_UPPER_ARM
					|| boneType == BoneType.RIGHT_LOWER_ARM
					|| boneType == BoneType.RIGHT_HAND
			) {
//				pos.multLocal(RIGHT_SHOULDER_OFFSET);
			}
		}

		return pos;
	}

	private Quaternion getAdjustedRotation(Quaternion rot, boolean unity) {
		if (!unity) {
			// Offset feet 90 degrees to satisfy the SteamVR bone overlay
			if (boneType == BoneType.LEFT_FOOT || boneType == BoneType.RIGHT_FOOT) {
				rot.multLocal(FOOT_OFFSET);
			}
		} else { // Adapt to Unity/VMC standards
			// Offset to satisfy apps that expect T-Pose, but we have I-Pose
			if (
				boneType == BoneType.LEFT_UPPER_ARM
					|| boneType == BoneType.LEFT_LOWER_ARM
					|| boneType == BoneType.LEFT_HAND
			) {
				rot.multLocal(LEFT_SHOULDER_OFFSET);
			} else if (
				boneType == BoneType.RIGHT_UPPER_ARM
					|| boneType == BoneType.RIGHT_LOWER_ARM
					|| boneType == BoneType.RIGHT_HAND
			) {
				rot.multLocal(RIGHT_SHOULDER_OFFSET);
			}
		}

		return rot;
	}

	/**
	 * @param root The new root for offsetting the translation
	 * @param unity Only use bones from Unity's HumanBodyBones
	 * @return The bone's local translation relative to a new root
	 */
	public Vector3f getLocalTranslationFromRoot(BoneType root, boolean unity) {
		return getGlobalTranslation(unity)
			.subtract(
				getNodeTowards(root, unity).worldTransform
					.getTranslation()
			);
	}

	/**
	 * @param root The new root for offsetting the rotation
	 * @param unity Only use bones from Unity's HumanBodyBones
	 * @return The bone's local rotation relative to a new root
	 */
	public Quaternion getLocalRotationFromRoot(BoneType root, boolean unity) {
		if (this.boneType == root) {
			return getGlobalRotation(unity);
		} else {
			return getNodeTowards(root, unity).worldTransform
				.getRotation()
				.inverse()
				.mult(getGlobalRotation(unity));
		}
	}

	private TransformNode getNodeTowards(
		BoneType towards,
		boolean unity
	) {
		if (towards == BoneType.HIP && unity && unityNodeTowardsHip != null) {
			// Use cached nodeTowards
			return unityNodeTowardsHip;
		}
		if (boneType == towards) {
			// Return itself
			return headNode;
		}

		// Search in children
		FastList<TransformNode> searching = new FastList<>(headNode.children);
		int i = 0;
		while (i < searching.size()) {
			// Search for node in children
			if (searching.get(i).getBoneType() == towards) {
				// Found in children. Go back to "headNode"
				TransformNode fromChildSearching = searching.get(i);

				if (unity) {
					// Search in parents
					while (fromChildSearching.getParent() != headNode) {
						// Check if the parent of the parent == headNode and
						// if parent isn't a unity bone
						if (
							fromChildSearching.getParent().getParent() != null
								&& fromChildSearching.getParent().getParent() == headNode
								&& UnityBone
									.getByBoneType(
										fromChildSearching.getParent().getBoneType()
									)
									== null
						) {
							return fromChildSearching;
						} else {
							fromChildSearching = fromChildSearching.getParent();
						}
					}
				} else {
					// Search in parents
					while (fromChildSearching.getParent() != headNode) {
						fromChildSearching = fromChildSearching.getParent();
					}
				}
				return fromChildSearching;
			}

			searching.addAll(searching.get(i).children);
			i++;
		}

		// None are found in children, so must be in parents.
		TransformNode parentSearching = headNode;
		if (unity) {
			// Check if current bone isn't a unity bone
			while (
				parentSearching.getParent() != null
					&& UnityBone.getByBoneType(parentSearching.getBoneType()) == null
			) {
				parentSearching = parentSearching.getParent();
			}
		}
		if (parentSearching.getParent() == null)
			return parentSearching;
		return parentSearching.getParent();
	}

}
