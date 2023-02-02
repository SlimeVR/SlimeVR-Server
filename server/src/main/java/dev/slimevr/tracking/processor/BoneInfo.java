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
		.fromAngles(0f, 0f, FastMath.HALF_PI);
	private static final Quaternion RIGHT_SHOULDER_OFFSET = new Quaternion()
		.fromAngles(0f, 0f, -FastMath.HALF_PI);
	private final Vector3f vecBuf = new Vector3f();
	private final Quaternion quatBuf = new Quaternion();

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
		length = tailNode.localTransform.getTranslation().length();
	}

	public Vector3f getLocalTranslation() {
		return tailNode.localTransform.getTranslation();
	}

	public Vector3f getGlobalTranslation() {
		return tailNode.worldTransform.getTranslation();
	}

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

	// TODO : There shouldn't be edge cases like multiplying
	// feet by rotation. This is the best solution right now,
	// or we'd need to store this info on the client, which is
	// worse. Need to rework the way the sussy offsets work
	private void adjustRotation(Quaternion rot, boolean unity) {
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
	}

	/**
	 * @param root The new root for offsetting the translation
	 * @param unity Only use bones from Unity's HumanBodyBones
	 * @return The bone's local translation relative to a new root
	 */
	public Vector3f getLocalTranslationFromRoot(BoneInfo root, boolean unity) {
		if (this == root) {
			vecBuf.zero();
		} else {
			vecBuf
				.set(
					getNodeTowards(headNode, root.headNode, unity).worldTransform
						.getTranslation()
						.subtract(getGlobalTranslation())
				);
		}

		return vecBuf;
	}

	/**
	 * @param root The new root for offsetting the rotation
	 * @param unity Only use bones from Unity's HumanBodyBones
	 * @return The bone's local rotation relative to a new root
	 */
	public Quaternion getLocalRotationFromRoot(BoneInfo root, boolean unity) {
		if (this == root) {
			quatBuf.set(getGlobalRotation(unity));
		} else if (boneType == BoneType.LEFT_FOOT || boneType == BoneType.RIGHT_FOOT) {
			// TODO fix feet
			return getLocalRotation(unity);
		} else {
			quatBuf
				.set(
					getNodeTowards(headNode, root.headNode, unity).worldTransform
						.getRotation()
						.inverse()
						.mult(getGlobalRotation(unity))
				);
		}

		return quatBuf;
	}

	/**
	 * @param from The root of the search
	 * @param towards The goal of the search
	 * @param unity Only use bones from Unity's HumanBodyBones
	 * @return the first node from "from" towards "towards"
	 */
	private TransformNode getNodeTowards(
		TransformNode from,
		TransformNode towards,
		boolean unity
	) {
		// TODO cache the result

		// Search in children
		FastList<TransformNode> searching = new FastList<>(from.children);
		int i = 0;
		while (i < searching.size()) {
			// Search for node in children
			if (searching.get(i).getName().equalsIgnoreCase(towards.getName())) {
				// Found in children. Go back to "from"
				TransformNode secondSearching = searching.get(i);
				if (unity) {
					while (secondSearching.getParent() != from) {
						// TODO unity
						secondSearching = secondSearching.getParent();
					}
				} else {
					while (secondSearching.getParent() != from) {
						secondSearching = secondSearching.getParent();
					}
				}

				return secondSearching;
			}

			searching.addAll(searching.get(i).children);
			i++;
		}

		// None are found in children, so must be in parents.
		if (unity) {
			TransformNode thirdSearching = from.getParent();
			while (
				thirdSearching.getParent() != null
					&& UnityBone
						.getByBoneType(
							BoneType
								.valueOf(thirdSearching.getParent().getName())
						)
						== null
			) {

				thirdSearching = thirdSearching.getParent();
			}
			return thirdSearching;
		}

		return from.getParent();
	}

}
